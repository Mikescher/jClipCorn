package de.jClipCorn.gui.frames.addSeasonFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.findCoverFrame.FindCoverDialog;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.ExtendedFocusTraversalOnArray;
import de.jClipCorn.util.FileChooserHelper;
import de.jClipCorn.util.ImageUtilities;
import de.jClipCorn.util.PathFormatter;
import de.jClipCorn.util.UpdateCallbackListener;
import de.jClipCorn.util.parser.ParseResultHandler;
import de.jClipCorn.util.userdataProblem.UserDataProblem;
import de.jClipCorn.util.userdataProblem.UserDataProblemHandler;

public class AddSeasonFrame extends JFrame implements UserDataProblemHandler, ParseResultHandler {
	private static final long serialVersionUID = -5479523926638394942L;
	
	private final CCSeries parent;
	private final JFileChooser coverFileChooser;
	
	private BufferedImage currentCoverImage = null;
	
	private final UpdateCallbackListener listener;
	
	private JLabel label;
	private JTextField edTitle;
	private JButton btnOpen;
	private CoverLabel lblCover;
	private JButton btnCancel;
	private JButton btnOK;
	private JLabel label_2;
	private JSpinner spnYear;
	private JButton btnFind;

	public AddSeasonFrame(Component owner, CCSeries ser, UpdateCallbackListener ucl){
		super();
		setSize(new Dimension(500, 400));
		this.parent = ser;
		this.coverFileChooser = new JFileChooser(PathFormatter.getAbsoluteSelfDirectory());
		this.listener = ucl;
		
		initGUI();
		initFileChooser();
		
		setLocationRelativeTo(owner);
		setFocusTraversalPolicy(new ExtendedFocusTraversalOnArray(new Component[]{edTitle, spnYear, btnFind, btnOpen, btnOK, btnCancel}));
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getFormattedString("AddSeasonFrame.this.title", parent.getTitle())); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		getContentPane().setLayout(null);
		
		label = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		label.setBounds(12, 15, 52, 16);
		getContentPane().add(label);
		
		edTitle = new JTextField();
		edTitle.setColumns(10);
		edTitle.setBounds(76, 13, 212, 20);
		getContentPane().add(edTitle);
		
		btnOpen = new JButton(LocaleBundle.getString("AddMovieFrame.btnOpenCover.text")); //$NON-NLS-1$
		btnOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openChooseCoverDialog();
			}
		});
		btnOpen.setBounds(435, 15, 47, 25);
		getContentPane().add(btnOpen);
		
		lblCover = new CoverLabel(false);
		lblCover.setHorizontalAlignment(SwingConstants.CENTER);
		lblCover.setBounds(300, 53, 182, 254);
		getContentPane().add(lblCover);
		
		btnCancel = new JButton(LocaleBundle.getString("AddMovieFrame.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		btnCancel.setBounds(244, 334, 95, 25);
		getContentPane().add(btnCancel);
		
		btnOK = new JButton(LocaleBundle.getString("AddMovieFrame.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnOK(true);
			}
		});
		btnOK.setBounds(137, 334, 95, 25);
		getContentPane().add(btnOK);
		
		label_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
		label_2.setBounds(12, 48, 52, 16);
		getContentPane().add(label_2);
		
		spnYear = new JSpinner();
		spnYear.setModel(new SpinnerNumberModel(new Integer(1900), new Integer(1900), null, new Integer(1)));
		spnYear.setEditor(new JSpinner.NumberEditor(spnYear, "0")); //$NON-NLS-1$
		spnYear.setBounds(76, 44, 212, 20);
		getContentPane().add(spnYear);
		
		btnFind = new JButton(LocaleBundle.getString("AddMovieFrame.btnFindCover.text")); //$NON-NLS-1$
		btnFind.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				findCover();
			}
		});
		btnFind.setBounds(365, 16, 60, 23);
		getContentPane().add(btnFind);
	}
	
	private void initFileChooser() {
		coverFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("AddMovieFrame.coverFileChooser.filterDescription", "png", "bmp", "gif", "jpg", "jpeg")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		
		coverFileChooser.setDialogTitle(LocaleBundle.getString("AddMovieFrame.coverFileChooser.title")); //$NON-NLS-1$
	}
	
	@Override
	public void setCover(BufferedImage nci) {
		nci = ImageUtilities.resizeCoverImage(nci);
		
		this.currentCoverImage = nci;
		if (nci != null) {
			lblCover.setIcon(new ImageIcon(currentCoverImage));
		} else {
			lblCover.setIcon(null);
		}
	}
	
	private void cancel() {
		this.dispose();
	}
	
	private void openChooseCoverDialog() {
		int returnval = coverFileChooser.showOpenDialog(this);
		
		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}
		try {
			BufferedImage read = ImageIO.read(coverFileChooser.getSelectedFile());
			setCover(read);
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}

	private void onBtnOK(boolean check) {
		ArrayList<UserDataProblem> problems = new ArrayList<>();
		
		boolean probvalue = (! check) || checkUserData(problems);
		
		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(problems, this, this);
			amied.setVisible(true);
			return;
		}
		
		CCSeason newS = parent.createNewEmptySeason();
		
		newS.beginUpdating();
		
		//#####################################################################################

		newS.setTitle(edTitle.getText());
		newS.setYear((int) spnYear.getValue());
		newS.setCover(currentCoverImage);
		
		//#####################################################################################
		
		newS.endUpdating();
		
		if (listener != null) {
			listener.onUpdate(null);
		}
		
		dispose();
	}
	
	public boolean checkUserData(ArrayList<UserDataProblem> ret) {
		String title = edTitle.getText();
		int year = (int) spnYear.getValue();

		UserDataProblem.testSeasonData(ret, currentCoverImage, title, year);
		
		return ret.isEmpty();
	}

	@Override
	public void onAMIEDIgnoreClicked() {
		onBtnOK(false);
	}
	
	private void findCover() {
		(new FindCoverDialog(this, this, CCMovieTyp.SERIES)).setVisible(true);
	}

	@Override
	public String getFullTitle() {
		return parent.getTitle() + ": " + edTitle.getText(); //$NON-NLS-1$
	}

	@Override
	public void setMovieFormat(CCMovieFormat cmf) {
		// NO SUCH ELEMENT
	}

	@Override
	public void setFilepath(int p, String t) {
		// NO SUCH ELEMENT
	}

	@Override
	public void setMovieName(String name) {
		// NO SUCH ELEMENT
	}

	@Override
	public void setZyklus(String mZyklusTitle) {
		// NO SUCH ELEMENT
	}

	@Override
	public void setZyklusNumber(int iRoman) {
		// NO SUCH ELEMENT
	}

	@Override
	public void setFilesize(long size) {
		// NO SUCH ELEMENT
	}

	@Override
	public void setMovieLanguage(CCMovieLanguage lang) {
		// NO SUCH ELEMENT
	}

	@Override
	public void setQuality(CCMovieQuality q) {
		// NO SUCH ELEMENT
	}

	@Override
	public void setYear(int y) {
		// NO SUCH ELEMENT
	}

	@Override
	public void setGenre(int gid, int movGenre) {
		// NO SUCH ELEMENT
	}

	@Override
	public void setFSK(int fsk) {
		// NO SUCH ELEMENT
	}

	@Override
	public void setLength(int l) {
		// NO SUCH ELEMENT
	}

	@Override
	public void setScore(int s) {
		// NO SUCH ELEMENT
	}
}
