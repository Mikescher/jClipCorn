package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;

public class CreateSeriesFolderStructureFrame extends JFrame {
	private static final long serialVersionUID = 8494757660196292481L;
	
	private CCSeries series;
	
	private JPanel pnlTop;
	private CoverLabel lblCover;
	private JPanel pnlLeft;
	private JLabel lblTitel;
	private ReadableTextField edPath;
	private JButton btnChoose;
	private JScrollPane scrlPnlBottom;
	private JList<String> lsTest;
	private JButton btnOk;
	private JButton btnTest;
	private JLabel lblCommonPath;

	public CreateSeriesFolderStructureFrame(Component owner, CCSeries ser) {
		super();
		this.series = ser;
		
		initGUI();
		
		setLocationRelativeTo(owner);
		
		init();
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setTitle(LocaleBundle.getString("CreateSeriesFolderStructureFrame.this.title")); //$NON-NLS-1$
		
		pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BorderLayout(0, 0));
		
		lblCover = new CoverLabel(false);
		pnlTop.add(lblCover, BorderLayout.WEST);
		
		pnlLeft = new JPanel();
		pnlTop.add(pnlLeft, BorderLayout.CENTER);
		pnlLeft.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		lblTitel = new JLabel();
		lblTitel.setFont(new Font("Tahoma", Font.PLAIN, 14)); //$NON-NLS-1$
		pnlLeft.add(lblTitel, "2, 2, 5, 1"); //$NON-NLS-1$
		
		lblCommonPath = new JLabel();
		pnlLeft.add(lblCommonPath, "2, 4, 3, 1"); //$NON-NLS-1$
		
		edPath = new ReadableTextField();
		pnlLeft.add(edPath, "2, 6, 3, 1, fill, default"); //$NON-NLS-1$
		edPath.setColumns(10);
		
		btnChoose = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onBtnChoose();
			}
		});
		pnlLeft.add(btnChoose, "6, 6"); //$NON-NLS-1$
		
		btnOk = new JButton(LocaleBundle.getString("AddMovieFrame.btnOK.text")); //$NON-NLS-1$
		btnOk.setEnabled(false);
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (startMoving()) {
					dispose();
				}
			}
		});
		btnOk.setEnabled(false);
		pnlLeft.add(btnOk, "2, 10, center, default"); //$NON-NLS-1$
		
		btnTest = new JButton(LocaleBundle.getString("MoveSeriesFrame.btnTest.text")); //$NON-NLS-1$
		btnTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startTest();
			}
		});
		btnTest.setEnabled(false);
		pnlLeft.add(btnTest, "4, 10, center, default"); //$NON-NLS-1$
		
		scrlPnlBottom = new JScrollPane();
		scrlPnlBottom.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrlPnlBottom, BorderLayout.CENTER);
		
		lsTest = new JList<>();
		lsTest.setCellRenderer(new CreateSeriesFolderStructureTestListCellRenderer());
		scrlPnlBottom.setViewportView(lsTest);
		
		setSize(550, 600);
	}
	
	private void init() {
		lblCover.setIcon(series.getCoverIcon());
		lblTitel.setText(series.getTitle());
		lblCommonPath.setText(PathFormatter.getAbsolute(series.getCommonPathStart()));
	}
	
	private void onBtnChoose() {
		JFileChooser folderchooser = new JFileChooser(PathFormatter.getAbsolute(series.getCommonPathStart()));
		folderchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		if (folderchooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			if (new File(folderchooser.getSelectedFile().getAbsolutePath()).isDirectory()) {
				edPath.setText(folderchooser.getSelectedFile().getAbsolutePath());
			}
		}
		
		btnOk.setEnabled(false);
		btnTest.setEnabled(true);
	}
	
	private void startTest() {
		DefaultListModel<String> dlm = new DefaultListModel<>();
		
		File parentfolder = new File(edPath.getText());
		
		boolean hasErrors = false;
		
		for (int sea = 0; sea < series.getSeasonCount(); sea++) {
			CCSeason season = series.getSeason(sea);
		
			for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
				CCEpisode episode = season.getEpisode(epi);
				
				File p = episode.getFileForCreatedFolderstructure(parentfolder);
				String pt = p.getAbsolutePath();
				int id = 0; // GREEN
				if (p.exists()) {
					id = 1; // YELLOW;
				}
				for (int i = 0; i < dlm.size(); i++) {
					if (dlm.get(i).substring(1).equals(pt)) {
						id = 2; // RED
						hasErrors = true;
					}
				}
				
				dlm.addElement(id + pt);
			}
		}
		
		lsTest.setModel(dlm);
		
		btnOk.setEnabled(! hasErrors);
	}
	
	private boolean startMoving() {
		lsTest.setModel(new DefaultListModel<String>());
		
		if (! testMoving()) {
			DialogHelper.showLocalInformation(this, "CreateSeriesFolderStructureFrame.dialogs.couldnotmove"); //$NON-NLS-1$
			return false;
		}
		
		if (DialogHelper.showLocaleYesNo(this, "CreateSeriesFolderStructureFrame.dialogs.sure")) { //$NON-NLS-1$
			File parentfolder = new File(edPath.getText());
			
			for (int sea = 0; sea < series.getSeasonCount(); sea++) {
				CCSeason season = series.getSeason(sea);
			
				for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
					CCEpisode episode = season.getEpisode(epi);
					
					File file = new File(episode.getAbsolutePart());
					File newfile = episode.getFileForCreatedFolderstructure(parentfolder);
					
					File mkdirfolder = newfile.getParentFile();
					
					if (newfile.exists()) {
						if (file.equals(newfile)) {
							continue; // Skip already existing and correct Files
						} else {
							DialogHelper.showError(this, LocaleBundle.getString("CreateSeriesFolderStructureFrame.dialogs.error_caption"), LocaleBundle.getFormattedString("CreateSeriesFolderStructureFrame.dialogs.error", episode.getTitle())); //$NON-NLS-1$ //$NON-NLS-2$
							
							return false;
						}
					}
					
					boolean succ = true;
					if (! mkdirfolder.isDirectory()) {
						succ = mkdirfolder.mkdirs();
					}
					if (succ) {
						succ = file.renameTo(newfile);
					}
					
					if (! succ) {
						DialogHelper.showError(this, LocaleBundle.getString("CreateSeriesFolderStructureFrame.dialogs.error_caption"), LocaleBundle.getFormattedString("CreateSeriesFolderStructureFrame.dialogs.error", episode.getTitle())); //$NON-NLS-1$ //$NON-NLS-2$
						
						return false;
					}
					
					episode.setPart(PathFormatter.getRelative(newfile.getAbsolutePath()));
				}
			}
		}
		
		return true;
	}
	
	private boolean testMoving() {
		File parentfolder = new File(edPath.getText());
		
		if (! parentfolder.isDirectory()) {
			return false;
		}
		
		List<File> files = new ArrayList<>();
		
		for (int sea = 0; sea < series.getSeasonCount(); sea++) {
			CCSeason season = series.getSeason(sea);
		
			for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
				CCEpisode episode = season.getEpisode(epi);
				
				files.add(episode.getFileForCreatedFolderstructure(parentfolder));
			}
		}
		
		Collections.sort(files);
		for (int i = 1; i < files.size(); i++) {
			if (files.get(i-1).equals(files.get(i))) {
				return false;
			}
		}
		
		return true;
	}
}
