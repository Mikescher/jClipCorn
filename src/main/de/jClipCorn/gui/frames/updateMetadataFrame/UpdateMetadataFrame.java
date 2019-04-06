package de.jClipCorn.gui.frames.updateMetadataFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.features.online.metadata.Metadataparser;
import de.jClipCorn.features.online.metadata.OnlineMetadata;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.ThreadUtils;
import de.jClipCorn.util.stream.CCStreams;

public class UpdateMetadataFrame extends JFrame {
	private static final long serialVersionUID = -2163175118745491143L;
	
	private enum FilterState { ALL, CHANGED }
	
	private final CCMovieList movielist;
	private FilterState selectedFilter;
	
	private UpdateMetadataTable tableMain;
	private JToggleButton btnShowFiltered;
	private JToggleButton btnShowAll;
	private JButton btnStartCollectingData;
	private JProgressBar progressBar;
	private JPanel panel;
	private JButton btnUpdateAllOnlinescore;

	private Thread collThread = null;
	private boolean cancelBackground;
	private JButton btnUpdateAllGenres;
	private JButton btnUpdateSelectedOnlineScore;
	private JButton btnUpdateSelectedGenres;
	private JCheckBox cbAllowDeleteGenres;
	private JButton btnUpdateSelectedRefs;
	private JCheckBox cbAllowDeleteReferences;
	private JButton btnUpdateAllReferences;
	
	public UpdateMetadataFrame(Component owner, CCMovieList mlist) {
		super();
		setSize(new Dimension(1200, 475));
		movielist = mlist;

		initGUI();
		setLocationRelativeTo(owner);
		
		initTable();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("UpdateMetadataFrame.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				ColumnSpec.decode("21dlu"), //$NON-NLS-1$
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		btnStartCollectingData = new JButton(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect1")); //$NON-NLS-1$
		btnStartCollectingData.addActionListener(this::QueryOnline);
		getContentPane().add(btnStartCollectingData, "2, 2"); //$NON-NLS-1$
		
		progressBar = new JProgressBar();
		getContentPane().add(progressBar, "4, 2, default, fill"); //$NON-NLS-1$
		
		btnShowAll = new JToggleButton(LocaleBundle.getString("UpdateMetadataFrame.SwitchFilter1")); //$NON-NLS-1$
		btnShowAll.addActionListener(e -> setFiltered(FilterState.ALL, true));
		btnShowAll.setSelected(true);
		getContentPane().add(btnShowAll, "6, 2"); //$NON-NLS-1$
		
		btnShowFiltered = new JToggleButton(LocaleBundle.getString("UpdateMetadataFrame.SwitchFilter2")); //$NON-NLS-1$
		btnShowFiltered.addActionListener(e -> setFiltered(FilterState.CHANGED, true));
		getContentPane().add(btnShowFiltered, "7, 2"); //$NON-NLS-1$
		
		tableMain = new UpdateMetadataTable(this);
		getContentPane().add(tableMain, "2, 4, 6, 1, fill, fill"); //$NON-NLS-1$
		
		panel = new JPanel();
		getContentPane().add(panel, "2, 6, 6, 1, fill, fill"); //$NON-NLS-1$
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.PREF_COLSPEC,
				ColumnSpec.decode("5dlu"), //$NON-NLS-1$
				FormSpecs.PREF_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.PREF_COLSPEC,
				ColumnSpec.decode("5dlu"), //$NON-NLS-1$
				FormSpecs.PREF_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		btnUpdateSelectedOnlineScore = new JButton(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate3")); //$NON-NLS-1$
		btnUpdateSelectedOnlineScore.setEnabled(false);
		panel.add(btnUpdateSelectedOnlineScore, "1, 1, fill, fill"); //$NON-NLS-1$
		btnUpdateSelectedOnlineScore.addActionListener(e -> UpdateScoreInDatabase(e, true));
		
		btnUpdateAllOnlinescore = new JButton(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate1")); //$NON-NLS-1$
		btnUpdateAllOnlinescore.setEnabled(false);
		panel.add(btnUpdateAllOnlinescore, "7, 1, fill, fill"); //$NON-NLS-1$
		btnUpdateAllOnlinescore.addActionListener(e -> UpdateScoreInDatabase(e, false));
		
		btnUpdateSelectedGenres = new JButton(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate4")); //$NON-NLS-1$
		btnUpdateSelectedGenres.setEnabled(false);
		panel.add(btnUpdateSelectedGenres, "1, 3, fill, fill"); //$NON-NLS-1$
		btnUpdateSelectedGenres.addActionListener(e -> UpdateGenresInDatabase(e, true));
		
		cbAllowDeleteGenres = new JCheckBox(LocaleBundle.getString("UpdateMetadataFrame.CBDelGenres")); //$NON-NLS-1$
		panel.add(cbAllowDeleteGenres, "4, 3, fill, fill"); //$NON-NLS-1$
		cbAllowDeleteGenres.addActionListener(f -> { tableMain.DeleteLocalGenres = cbAllowDeleteGenres.isSelected(); tableMain.forceDataChangedRedraw(); });
		
		btnUpdateAllGenres = new JButton(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate2")); //$NON-NLS-1$
		btnUpdateAllGenres.setEnabled(false);
		panel.add(btnUpdateAllGenres, "7, 3, fill, fill"); //$NON-NLS-1$
		btnUpdateAllGenres.addActionListener(e -> UpdateGenresInDatabase(e, false));
		
		btnUpdateSelectedRefs = new JButton(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate5")); //$NON-NLS-1$
		btnUpdateSelectedRefs.setEnabled(false);
		panel.add(btnUpdateSelectedRefs, "1, 5"); //$NON-NLS-1$
		btnUpdateSelectedRefs.addActionListener(e -> UpdateReferencesInDatabase(e, true));
		
		cbAllowDeleteReferences = new JCheckBox(LocaleBundle.getString("UpdateMetadataFrame.CBDelReferences")); //$NON-NLS-1$
		panel.add(cbAllowDeleteReferences, "4, 5"); //$NON-NLS-1$
		cbAllowDeleteGenres.addActionListener(f -> { tableMain.DeleteLocalReferences = cbAllowDeleteReferences.isSelected(); tableMain.forceDataChangedRedraw(); });
		
		btnUpdateAllReferences = new JButton(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate6")); //$NON-NLS-1$
		btnUpdateAllReferences.setEnabled(false);
		panel.add(btnUpdateAllReferences, "7, 5"); //$NON-NLS-1$
		btnUpdateAllReferences.addActionListener(e -> UpdateReferencesInDatabase(e, false));
	}

	private void setFiltered(FilterState state, boolean triggerUpdate) {
		boolean changed = (selectedFilter != state);
		
		selectedFilter = state;

		btnShowAll.setSelected(state == FilterState.ALL);
		btnShowFiltered.setSelected(state == FilterState.CHANGED);
		
		if (triggerUpdate && changed) {
			switch (state) {
			case ALL:
				tableMain.resetFilter();
				break;
			case CHANGED:
				tableMain.setFilter(this::FilterHandlerChanged);
				break;
			}
		}
	}
	
	private boolean FilterHandlerChanged(UpdateMetadataTableElement d) {
		if (d == null) return false;

		if (!d.Processed) return true;
		
		CCDatabaseElement el = d.Element;
		OnlineMetadata md = d.OnlineMeta;
		
		if (el == null || md == null) return false;

		CCOnlineScore os1 = el.getOnlinescore();
		Integer os2 = md.OnlineScore;

		if (os1 != null && os2 != null) {
			if (os1.asInt() != os2.intValue()) return true;
		}

		if (tableMain.DeleteLocalGenres) {
			CCGenreList og1 = el.getGenres();
			CCGenreList og2 = md.Genres;

			if (og1 != null && og2 != null) {
				for (CCGenre online : og2.iterate()) {
					if (!og1.includes(online)) return true;
				}
				for (CCGenre local : og1.iterate()) {
					if (!og2.includes(local)) return true;
				}
			}
		} else {
			CCGenreList og1 = el.getGenres();
			CCGenreList og2 = md.Genres;

			if (og1 != null && og2 != null) {
				for (CCGenre online : og2.iterate()) {
					if (!og1.includes(online) && !og1.isFull()) return true;
				}
			}
		}

		
		return false;
	}

	private void initTable() {
		tableMain.setData(movielist.iteratorElements().map(p -> new UpdateMetadataTableElement(p)).enumerate());
		tableMain.autoResize();
	}
	
	private void QueryOnline(ActionEvent e) {
		if (collThread != null && collThread.isAlive()) {
			cancelBackground = true;
		} else {
			cancelBackground = false;
			collThread = new Thread(this::Run, "THREAD_UPDATE_METADATA_COLLECT"); //$NON-NLS-1$
			collThread.start();
		}
	}
	
	private void Run() {
		try {
			List<UpdateMetadataTableElement> data = tableMain.getDataCopy();
			
			ThreadUtils.invokeAndWaitSafe(() -> 
			{ 
				btnStartCollectingData.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect2"));  //$NON-NLS-1$
				btnUpdateAllGenres.setEnabled(false);
				btnUpdateAllOnlinescore.setEnabled(false);
				btnUpdateSelectedGenres.setEnabled(false);
				btnUpdateSelectedOnlineScore.setEnabled(false);
				btnUpdateAllReferences.setEnabled(false);
				btnUpdateSelectedRefs.setEnabled(false);
			});
			ThreadUtils.setProgressbarAndWait(progressBar, 0, 0, data.size()+1);
			
			int i = 1;
			for (UpdateMetadataTableElement elem : data) {
				ThreadUtils.setProgressbarAndWait(progressBar, i);
				i++;
				
				CCSingleOnlineReference ref = elem.Element.getOnlineReference().Main;
				if (! ref.isSet()) continue;
				
				if (elem.OnlineMeta != null && elem.OnlineMeta.OnlineScore != null) continue;
				
				Metadataparser mp = ref.getMetadataParser();
				if (mp == null) continue;
				
				try {
					OnlineMetadata md = mp.getMetadata(ref, false);
					if (md != null) {
						elem.OnlineMeta = md;
						ThreadUtils.invokeAndWaitSafe(() -> { tableMain.changeData(elem, elem); });
					}
					
				} catch (Exception e) {
					CCLog.addDebug(e.toString());
				}
				
				elem.Processed = true;
				
				if (cancelBackground) return;
			}
		} finally {
			ThreadUtils.setProgressbarAndWait(progressBar, 0, 0, 1);
			ThreadUtils.invokeAndWaitSafe(() -> 
			{ 
				btnStartCollectingData.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect3"));  //$NON-NLS-1$
				btnUpdateAllGenres.setEnabled(true);
				btnUpdateAllOnlinescore.setEnabled(true);
				btnUpdateSelectedGenres.setEnabled(true);
				btnUpdateSelectedOnlineScore.setEnabled(true);
				btnUpdateAllReferences.setEnabled(true);
				btnUpdateSelectedRefs.setEnabled(true);
			});
			collThread = null;
		}
	}
	
	private void UpdateScoreInDatabase(ActionEvent e, boolean onlySelected) {
		List<UpdateMetadataTableElement> data = onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy();

		int count = 0;		
		
		for (UpdateMetadataTableElement elem : data) {
			if (elem.OnlineMeta != null && elem.OnlineMeta.OnlineScore != null) {
				if (elem.Element.getOnlinescore().asInt() != elem.OnlineMeta.OnlineScore.intValue()) {
					elem.Element.setOnlinescore(elem.OnlineMeta.OnlineScore);
					count++;
				}
			}
		}
		
		DialogHelper.showInformation(this, LocaleBundle.getString("Dialogs.MetadataUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.MetadataUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$

		tableMain.forceDataChangedRedraw();
	}
	
	private void UpdateGenresInDatabase(ActionEvent e, boolean onlySelected) {
		List<UpdateMetadataTableElement> data = onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy();

		int count = 0;		
		
		for (UpdateMetadataTableElement elem : data) {
			if (elem.OnlineMeta != null && elem.OnlineMeta.Genres != null) {
				if (tableMain.DeleteLocalGenres) {
					if (!elem.Element.getGenres().equals(elem.OnlineMeta.Genres)) {
						elem.Element.setGenres(elem.OnlineMeta.Genres);
						count++;
					}
				} else {
					boolean diff = false;
					for (CCGenre genre : elem.OnlineMeta.Genres.iterate()) {
						if (!elem.Element.getGenres().includes(genre) && !elem.Element.getGenres().isFull()) {
							elem.Element.tryAddGenre(genre);
							diff = true;
						}
					}
					if (diff) count++;
				}
			}
		}
		
		DialogHelper.showInformation(this, LocaleBundle.getString("Dialogs.MetadataUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.MetadataUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$
		
		tableMain.forceDataChangedRedraw();
	}
	
	private void UpdateReferencesInDatabase(ActionEvent e, boolean onlySelected) {
		List<UpdateMetadataTableElement> data = onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy();

		int count = 0;		
		
		for (UpdateMetadataTableElement elem : data) {
			if (elem.OnlineMeta != null) {
				CCOnlineReferenceList onlineref = elem.OnlineMeta.getFullReference();
				CCOnlineReferenceList localref = elem.Element.getOnlineReference();
				
				if (onlineref != null && onlineref.isMainSet()) {

					if (tableMain.DeleteLocalReferences) {
						if (!localref.equalsIgnoreAdditionalOrder(onlineref)) {
							elem.Element.setOnlineReference(onlineref);
							count++;
						}
					} else {
						boolean diff = false;
						for (CCSingleOnlineReference ref : onlineref) {
							if (!CCStreams.iterate(localref).contains(ref)) {
								localref = localref.addAdditional(ref);
								diff = true;
							}
						}
						if (diff) { count++; elem.Element.setOnlineReference(localref); }
					}
				}
			}
		}
		
		DialogHelper.showInformation(this, LocaleBundle.getString("Dialogs.MetadataUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.MetadataUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$
		
		tableMain.forceDataChangedRedraw();
	}
}
