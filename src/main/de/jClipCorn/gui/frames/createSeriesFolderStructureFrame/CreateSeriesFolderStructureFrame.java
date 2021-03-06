package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.JReadableFSPathTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateSeriesFolderStructureFrame extends JFrame {
	private static final long serialVersionUID = 8494757660196292481L;
	
	private CCSeries series;
	
	private JPanel pnlTop;
	private CoverLabel lblCover;
	private JPanel pnlLeft;
	private JLabel lblTitel;
	private JReadableFSPathTextField edPath;
	private JButton btnChoose;
	private CSFSTable lsTest;
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
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle(LocaleBundle.getString("CreateSeriesFolderStructureFrame.this.title")); //$NON-NLS-1$
		
		pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BorderLayout(0, 0));
		
		lblCover = new CoverLabel(false);
		pnlTop.add(lblCover, BorderLayout.WEST);
		
		pnlLeft = new JPanel();
		pnlTop.add(pnlLeft, BorderLayout.CENTER);
		pnlLeft.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		lblTitel = new JLabel();
		lblTitel.setFont(new Font("Tahoma", Font.PLAIN, 14)); //$NON-NLS-1$
		pnlLeft.add(lblTitel, "2, 2, 5, 1"); //$NON-NLS-1$
		
		lblCommonPath = new JLabel();
		pnlLeft.add(lblCommonPath, "2, 4, 3, 1"); //$NON-NLS-1$
		
		edPath = new JReadableFSPathTextField();
		pnlLeft.add(edPath, "2, 6, 3, 1, fill, default"); //$NON-NLS-1$
		edPath.setColumns(10);
		
		btnChoose = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose.addActionListener(arg0 -> onBtnChoose());
		pnlLeft.add(btnChoose, "6, 6"); //$NON-NLS-1$
		
		btnOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOk.setEnabled(false);
		btnOk.addActionListener(arg0 -> {
			if (startMoving()) {
				dispose();
			}
		});
		btnOk.setEnabled(false);
		pnlLeft.add(btnOk, "2, 10, center, default"); //$NON-NLS-1$
		
		btnTest = new JButton(LocaleBundle.getString("MoveSeriesFrame.btnTest.text")); //$NON-NLS-1$
		btnTest.addActionListener(arg0 -> startTest());
		btnTest.setEnabled(false);
		pnlLeft.add(btnTest, "4, 10, center, default"); //$NON-NLS-1$

		lsTest = new CSFSTable();
		getContentPane().add(lsTest, BorderLayout.CENTER);
		
		setSize(1050, 800);
	}
	
	private void init() {
		lblCover.setAndResizeCover(series.getCover());
		lblTitel.setText(series.getTitle());
		lblCommonPath.setText(series.getCommonPathStart(false).toFSPath().toString());
		edPath.setPath(series.guessSeriesRootPath());
		btnTest.setEnabled(! edPath.getPath().isEmpty());
	}
	
	private void onBtnChoose() {
		var pStart = series.guessSeriesRootPath();
		if (pStart.isEmpty()) pStart = series.getCommonPathStart(false).toFSPath();
				
		JFileChooser folderchooser = new JFileChooser(pStart.toFile());
		folderchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		if (folderchooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			var p = FSPath.create(folderchooser.getSelectedFile());
			if (p.isDirectory()) edPath.setPath(p);
		}
		
		btnOk.setEnabled(false);
		btnTest.setEnabled(true);
	}
	
	private void startTest() {
		List<CSFSElement> elements = new ArrayList<>();
		
		FSPath parentfolder = edPath.getPath();

		for (int sea = 0; sea < series.getSeasonCount(); sea++) {
			CCSeason season = series.getSeasonByArrayIndex(sea);
		
			for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
				CCEpisode episode = season.getEpisodeByArrayIndex(epi);

				CSFSElement elem = new CSFSElement();

				var fileNew = episode.getPathForCreatedFolderstructure(parentfolder);

				elem.CCPathOld = episode.getPart();
				elem.FSPathOld = episode.getPart().toFSPath();

				elem.FSPathNew = (fileNew==null)? FSPath.Empty : fileNew;
				elem.CCPathNew = CCPath.createFromFSPath(elem.FSPathNew);

				if (fileNew==null)
				{
					elem.State = CSFSElement.CSFSState.Error;
				}
				else if (elem.FSPathNew.equals(elem.FSPathOld))
				{
					elem.State = CSFSElement.CSFSState.Nothing;
				}
				else
				{
					if (fileNew.exists())
						elem.State = CSFSElement.CSFSState.Warning;
					else
						elem.State = CSFSElement.CSFSState.Move;

				}

				for (CSFSElement e : elements) {
				    if (e.FSPathNew.equals(elem.FSPathNew) || e.CCPathNew.equals(elem.CCPathNew)) {
				    	e.State = CSFSElement.CSFSState.Error;
					}
				}

				elements.add(elem);
			}
		}

		lsTest.setData(elements);

		lsTest.autoResize();

		btnOk.setEnabled(!CCStreams.iterate(elements).any(e -> e.State== CSFSElement.CSFSState.Error) && ! series.getMovieList().isReadonly());
	}
	
	private boolean startMoving() {
		lsTest.setData(new ArrayList<>());

		if (! testMoving()) {
			DialogHelper.showDispatchLocalInformation(this, "CreateSeriesFolderStructureFrame.dialogs.couldnotmove"); //$NON-NLS-1$
			return false;
		}
		
		if (DialogHelper.showLocaleYesNo(this, "CreateSeriesFolderStructureFrame.dialogs.sure")) { //$NON-NLS-1$
			var parentfolder = edPath.getPath();
			
			for (int sea = 0; sea < series.getSeasonCount(); sea++) {
				CCSeason season = series.getSeasonByArrayIndex(sea);
			
				for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
					CCEpisode episode = season.getEpisodeByArrayIndex(epi);
					
					var file = episode.getPart().toFSPath();
					var newfile = episode.getPathForCreatedFolderstructure(parentfolder);
					
					var mkdirfolder = newfile.getParent();
					
					if (newfile.exists()) {
						if (file.equals(newfile)) {
							continue; // Skip already existing and correct Files
						} else {
							DialogHelper.showDispatchError(this, LocaleBundle.getString("CreateSeriesFolderStructureFrame.dialogs.error_caption"), LocaleBundle.getFormattedString("CreateSeriesFolderStructureFrame.dialogs.error", episode.getTitle())); //$NON-NLS-1$ //$NON-NLS-2$
							
							return false;
						}
					}
					
					boolean succ = true;
					if (! mkdirfolder.isDirectory()) {
						succ = mkdirfolder.mkdirsSafe();
					}
					if (succ) {
						succ = file.renameToSafe(newfile);
					}
					
					if (! succ) {
						DialogHelper.showDispatchError(this, LocaleBundle.getString("CreateSeriesFolderStructureFrame.dialogs.error_caption"), LocaleBundle.getFormattedString("CreateSeriesFolderStructureFrame.dialogs.error", episode.getTitle())); //$NON-NLS-1$ //$NON-NLS-2$
						
						return false;
					}
					
					episode.Part.set(CCPath.createFromFSPath(newfile));
				}
			}
		}
		
		return true;
	}
	
	private boolean testMoving() {
		var parentfolder = edPath.getPath();
		
		if (! parentfolder.isDirectory()) {
			return false;
		}
		
		List<FSPath> files = new ArrayList<>();
		
		for (int sea = 0; sea < series.getSeasonCount(); sea++) {
			CCSeason season = series.getSeasonByArrayIndex(sea);
		
			for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
				CCEpisode episode = season.getEpisodeByArrayIndex(epi);
				
				files.add(episode.getPathForCreatedFolderstructure(parentfolder));
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
