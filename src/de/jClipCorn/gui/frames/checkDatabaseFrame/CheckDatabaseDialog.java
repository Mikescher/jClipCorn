package de.jClipCorn.gui.frames.checkDatabaseFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.FileSizeFormatter;
import de.jClipCorn.util.PathFormatter;

public class CheckDatabaseDialog extends JDialog {
	private static final long serialVersionUID = 8481907373850170115L;
	
	private final static double MAX_SIZEDRIFT = 0.05; // 5%

	private final CCMovieList movielist;
	
	private final JPanel contentPanel = new JPanel();
	private JPanel pnlTop;
	private JScrollPane scrollPane;
	private JTextArea memoMain;
	private JButton btnValidate;
	private JLabel lblInfo;
	private JProgressBar pBar;
	
	public CheckDatabaseDialog(CCMovieList ml, MainFrame owner) {
		super();
		this.movielist = ml;
		
		initGUI(owner);
		
		lblInfo.setText(LocaleBundle.getFormattedString("CheckDatabaseDialog.lblInfo.text", ml.getElementCount())); //$NON-NLS-1$
		
		pBar = new JProgressBar();
		contentPanel.add(pBar, BorderLayout.SOUTH);
	}
	
	private void initGUI(Component owner) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setTitle(LocaleBundle.getString("CheckDatabaseDialog.this.title")); //$NON-NLS-1$
		setModal(true);
		setBounds(100, 100, 750, 400);
		setLocationRelativeTo(owner);
		
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		pnlTop = new JPanel();
		FlowLayout fl_pnlTop = (FlowLayout) pnlTop.getLayout();
		fl_pnlTop.setAlignment(FlowLayout.LEFT);
		contentPanel.add(pnlTop, BorderLayout.NORTH);
		
		btnValidate = new JButton(LocaleBundle.getString("CheckDatabaseDialog.btnValidate.text")); //$NON-NLS-1$
		btnValidate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startValidate();
			}
		});
		pnlTop.add(btnValidate);
		
		lblInfo = new JLabel();
		pnlTop.add(lblInfo);
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		
		memoMain = new JTextArea();
		memoMain.setEditable(false);
		scrollPane.setViewportView(memoMain);
	}
	
	private void addError(final String error, final CCMovie m) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@SuppressWarnings("nls")
				@Override
				public void run() {
					memoMain.append("[" + m.getLocalID() + "] (" + m.getCompleteTitle() + ") " + error + "\n");
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void stepProgress() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					pBar.setValue(pBar.getValue() + 1);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void endThread() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					pBar.setValue(0);
					btnValidate.setEnabled(true);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void startValidate() { //TODO FAILT: ZEigt an FileNotFound und WrongFilesize bei ALLEM
		memoMain.setText(""); //$NON-NLS-1$
		
		btnValidate.setEnabled(false);
		pBar.setMaximum(movielist.getElementCount());
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (CCDatabaseElement el : movielist.getRawList()) {
					if (! el.isMovie()) continue; //TODO Check Series
					
					CCMovie mov = (CCMovie)el;
					
					//###############################################
					// Hole in Genres
					//###############################################
					
					if (mov.hasHoleInGenres()) {
						addError(LocaleBundle.getString("CheckDatabaseDialog.Error.ERR_01"), mov); //$NON-NLS-1$
					}
					
					//###############################################
					// Genre with wrong ID
					//###############################################
					
					for (int i = 0; i < mov.getGenreCount(); i++) {
						if (! mov.getGenre(i).isValid()) {
							addError(LocaleBundle.getFormattedString("CheckDatabaseDialog.Error.ERR_02", mov.getGenre(i).asInt()), mov); //$NON-NLS-1$
						}
					}
					
					//###############################################
					// Moviesize <> Real size
					//###############################################
					
					CCMovieSize size = new CCMovieSize();
					for (int i = 0; i < mov.getPartcount(); i++) {
						size.add(FileSizeFormatter.getFileSize(mov.getPart(i)));
					}
					if (Math.abs((size.getBytes() - mov.getFilesize().getBytes()) / mov.getFilesize().getBytes()) > MAX_SIZEDRIFT) {
						addError(LocaleBundle.getString("CheckDatabaseDialog.Error.ERR_03"), mov); //$NON-NLS-1$
					}
					
					//###############################################
					// Genrecount <= 0
					//###############################################
					
					if (mov.getGenreCount() <= 0) {
						addError(LocaleBundle.getString("CheckDatabaseDialog.Error.ERR_04"), mov); //$NON-NLS-1$
					}
					
					//###############################################
					// cover not set
					//###############################################
					
					if (mov.getCoverName().isEmpty()) {
						addError(LocaleBundle.getString("CheckDatabaseDialog.Error.ERR_05"), mov); //$NON-NLS-1$
					}
					
					//###############################################
					// cover not found
					//###############################################
					
					if (! movielist.getCoverCache().coverExists(mov.getCoverName())) {
						addError(LocaleBundle.getString("CheckDatabaseDialog.Error.ERR_06"), mov); //$NON-NLS-1$
					}
					
					//###############################################
					// no title set
					//###############################################
					
					if (mov.getTitle().isEmpty()) {
						addError(LocaleBundle.getString("CheckDatabaseDialog.Error.ERR_07"), mov); //$NON-NLS-1$
					}
					
					//###############################################
					// zyklusID == 0
					//###############################################
					
					if (! mov.getZyklus().getTitle().isEmpty() && mov.getZyklus().getNumber() == 0) {
						addError(LocaleBundle.getString("CheckDatabaseDialog.Error.ERR_08"), mov); //$NON-NLS-1$
					}
					
					//###############################################
					// ZyklusID <> -1
					//###############################################
					
					if (mov.getZyklus().getTitle().isEmpty() && mov.getZyklus().getNumber() != -1) {
						addError(LocaleBundle.getString("CheckDatabaseDialog.Error.ERR_09"), mov); //$NON-NLS-1$
					}
					
					//###############################################
					//	Hole in Parts
					//###############################################
					
					if (mov.hasHoleInParts()) {
						addError(LocaleBundle.getString("CheckDatabaseDialog.Error.ERR_10"), mov); //$NON-NLS-1$
					}
					
					//###############################################
					// Wrong Format
					//###############################################
					
					boolean rform = false;
					for (int i = 0; i < mov.getPartcount(); i++) {
						if (CCMovieFormat.getMovieFormat(PathFormatter.getExtension(mov.getPart(i))).equals(mov.getFormat())) {
							rform = true;
						}
					}
					if (! rform) {
						addError(LocaleBundle.getString("CheckDatabaseDialog.Error.ERR_11"), mov); //$NON-NLS-1$
					}
					
					//###############################################
					// Inexistent Paths
					//###############################################
					
					for (int i = 0; i < mov.getPartcount(); i++) {
						if (new File(mov.getAbsolutePart(i)).exists()) {
							addError(LocaleBundle.getString("CheckDatabaseDialog.Error.ERR_12"), mov); //$NON-NLS-1$
						}
					}
					
					//TODO: More Checks (!!!)
					//TODO: Double Movies
					//TODO: Double Files
					//TODO: Double Cover
					//TODO: Check Series
					//TODO: Check internal Database (Double SeriesID, Doubel SeasonID, SeriesID without seasons, Season with SeriesID but without Series etc etc etc)
					
				}	
				
				stepProgress();
				endThread();
			}
		}).start();
	}
}
