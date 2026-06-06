package de.jClipCorn.gui.frames.updateMetadataFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.metadata.Metadataparser;
import de.jClipCorn.features.online.metadata.OnlineMetadata;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.helper.ThreadUtils;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateMetadataFrame extends JCCFrame
{
	private enum FilterState { ALL, CHANGED }

	private FilterState selectedFilter;

	private Thread collThread = null;
	private boolean cancelBackground;

	public UpdateMetadataFrame(Component owner, CCMovieList ml)
	{
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		initTable();

		tableMain.DeleteLocalAnimeSeason = cbReplaceAnimeSeason.isSelected();
		tableMain.DeleteLocalAnimeStudio = cbReplaceAnimeStudio.isSelected();

		var defBackground = statusInputFilter.getBackground();
		edInputFilter.getDocument().addDocumentListener(new DocumentLambdaAdapter(() -> {
			if (edInputFilter.getText().equals("[0]"))
				statusInputFilter.setBackground(defBackground);
			else if (AbstractCustomFilter.createFilterFromExport(movielist, edInputFilter.getText()) == null)
				statusInputFilter.setBackground(Color.RED);
			else
				statusInputFilter.setBackground(Color.GREEN);
		}));
	}

	private void initTable()
	{
		tableMain.clearData();

		try
		{
			List<UpdateMetadataTableElement> data = new ArrayList<>();
			for (CCDatabaseElement el : movielist.iteratorElements()) data.add(new UpdateMetadataTableElement(el));
			for (CCSeason se : movielist.iteratorSeasons())           data.add(new UpdateMetadataTableElement(se));

			var filter = AbstractCustomFilter.createFilterFromExport(movielist, edInputFilter.getText());
			if (filter == null) throw new Exception("failed to parse filter");

			data = CCStreams.iterate(data).filter(elem -> filter.includes(elem.Element)).enumerate();

			tableMain.setData(data);
			tableMain.autoResize();
		}
		catch(Exception e)
		{
			CCLog.addWarning(e);
			GenericTextDialog.showText(this, getTitle(), e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), false); //$NON-NLS-1$ //$NON-NLS-2$
		}
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

		if (!d.Processed) return false;

		OnlineMetadata md = d.OnlineMeta;

		if (md == null) return false;

		if (d.supportsScore()) {
			var os1 = d.getLocalScore();
			var os2 = md.OnlineScore;

			if (os1 != null && os2 != null) {
				if (!CCOnlineScore.isEqual(os1, os2)) return true;
			}
		}

		if (d.supportsGenres()) {
			CCGenreList og1 = d.getLocalGenres();
			CCGenreList og2 = md.Genres;

			if (og1 != null && og2 != null) {
				if (tableMain.DeleteLocalGenres) {
					for (CCGenre online : og2.iterate()) {
						if (!og1.includes(online)) return true;
					}
					for (CCGenre local : og1.iterate()) {
						if (!og2.includes(local)) return true;
					}
				} else {
					for (CCGenre online : og2.iterate()) {
						if (!og1.includes(online) && !og1.isFull()) return true;
					}
				}
			}
		}

		if (d.supportsAnime()) {
			if (hasStringListChange(d.getLocalAnimeSeason(), md.AnimeSeason, tableMain.DeleteLocalAnimeSeason)) return true;
			if (hasStringListChange(d.getLocalAnimeStudio(), md.AnimeStudio, tableMain.DeleteLocalAnimeStudio)) return true;
		}

		return false;
	}

	private static boolean hasStringListChange(CCStringList local, CCStringList online, boolean replace) {
		if (online == null || online.isEmpty()) return false;
		if (local == null) local = CCStringList.EMPTY;

		for (String o : online) if (!local.contains(o)) return true;
		if (replace) {
			for (String l : local) if (!online.contains(l)) return true;
		}

		return false;
	}

	private void queryOnline() {
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

			SwingUtils.invokeAndWaitSafe(() ->
			{
				btnStartCollectingData.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect2"));  //$NON-NLS-1$
				btnUpdateAllGenres.setEnabled(false);
				btnUpdateAllOnlinescore.setEnabled(false);
				btnUpdateSelectedGenres.setEnabled(false);
				btnUpdateSelectedOnlineScore.setEnabled(false);
				btnUpdateAllReferences.setEnabled(false);
				btnUpdateSelectedRefs.setEnabled(false);
				btnUpdateAllAnimeSeason.setEnabled(false);
				btnUpdateSelectedAnimeSeason.setEnabled(false);
				btnUpdateAllAnimeStudio.setEnabled(false);
				btnUpdateSelectedAnimeStudio.setEnabled(false);
				btnInputFilter.setEnabled(false);
			});
			ThreadUtils.setProgressbarAndWait(progressBar, 0, 0, data.size()+1);

			int i = 1;
			for (UpdateMetadataTableElement elem : data) {
				ThreadUtils.setProgressbarAndWait(progressBar, i);
				i++;

				CCSingleOnlineReference ref = elem.getOnlineReference().Main;
				if (! ref.isSet()) continue;

				if (elem.OnlineMeta != null) continue;

				Metadataparser mp = ref.getMetadataParser(movielist);
				if (mp == null) continue;

				OnlineMetadata md = null;
				try {
					md = mp.getMetadata(ref, false);

				} catch (Exception e) {
					CCLog.addDebug(e.toString());
				}
				if (md != null) {
					elem.OnlineMeta = md;
				}
				elem.Processed = true;
				SwingUtils.invokeAndWaitSafe(() -> tableMain.changeData(elem, elem));


				if (cancelBackground) return;
			}
		} finally {
			ThreadUtils.setProgressbarAndWait(progressBar, 0, 0, 1);
			SwingUtils.invokeAndWaitSafe(() ->
			{
				btnStartCollectingData.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect3"));  //$NON-NLS-1$
				btnUpdateAllGenres.setEnabled(!movielist.isReadonly());
				btnUpdateAllOnlinescore.setEnabled(!movielist.isReadonly());
				btnUpdateSelectedGenres.setEnabled(!movielist.isReadonly());
				btnUpdateSelectedOnlineScore.setEnabled(!movielist.isReadonly());
				btnUpdateAllReferences.setEnabled(!movielist.isReadonly());
				btnUpdateSelectedRefs.setEnabled(!movielist.isReadonly());
				btnUpdateAllAnimeSeason.setEnabled(!movielist.isReadonly());
				btnUpdateSelectedAnimeSeason.setEnabled(!movielist.isReadonly());
				btnUpdateAllAnimeStudio.setEnabled(!movielist.isReadonly());
				btnUpdateSelectedAnimeStudio.setEnabled(!movielist.isReadonly());
				btnInputFilter.setEnabled(false);
			});
			collThread = null;
		}
	}

	private void showAll() {
		setFiltered(FilterState.ALL, true);
	}

	private void showFiltered() {
		setFiltered(FilterState.CHANGED, true);
	}

	private void updateSelectedGenres() {
		UpdateGenresInDatabase(true);
	}

	private void updateAllGenres() {
		UpdateGenresInDatabase(false);
	}

	private void updatSelectedOnlineReferences() {
		UpdateReferencesInDatabase(true);
	}

	private void updateSelectedOnlineScore() {
		UpdateScoreInDatabase(true);
	}

	private void updateAllOnlineScore() {
		UpdateScoreInDatabase(false);
	}

	private void updateAllOnlineReferences() {
		UpdateReferencesInDatabase(false);
	}

	private void updateSelectedAnimeSeason() {
		UpdateAnimeSeasonInDatabase(true);
	}

	private void updateAllAnimeSeason() {
		UpdateAnimeSeasonInDatabase(false);
	}

	private void updateSelectedAnimeStudio() {
		UpdateAnimeStudioInDatabase(true);
	}

	private void updateAllAnimeStudio() {
		UpdateAnimeStudioInDatabase(false);
	}

	private void onChangeAllowGenreDelete() {
		tableMain.DeleteLocalGenres = cbAllowDeleteGenres.isSelected();
		tableMain.forceDataChangedRedraw();
	}

	private void onChangeAllowRefDelete() {
		tableMain.DeleteLocalReferences = cbAllowDeleteReferences.isSelected();
		tableMain.forceDataChangedRedraw();
	}

	private void onChangeAllowAnimeSeasonReplace() {
		tableMain.DeleteLocalAnimeSeason = cbReplaceAnimeSeason.isSelected();
		tableMain.forceDataChangedRedraw();
	}

	private void onChangeAllowAnimeStudioReplace() {
		tableMain.DeleteLocalAnimeStudio = cbReplaceAnimeStudio.isSelected();
		tableMain.forceDataChangedRedraw();
	}

	private void UpdateScoreInDatabase(boolean onlySelected) {
		if (movielist.isReadonly()) return;

		List<UpdateMetadataTableElement> data = onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy();

		int count = 0;

		for (UpdateMetadataTableElement elem : data) {
			if (!elem.supportsScore()) continue;
			if (elem.OnlineMeta != null && elem.OnlineMeta.OnlineScore != null) {
				if (!CCOnlineScore.isEqual(elem.getLocalScore(), elem.OnlineMeta.OnlineScore)) {
					elem.setScore(elem.OnlineMeta.OnlineScore);
					count++;
				}
			}
		}

		DialogHelper.showDispatchInformation(this, LocaleBundle.getString("Dialogs.MetadataUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.MetadataUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$

		tableMain.forceDataChangedRedraw();
	}

	private void UpdateGenresInDatabase(boolean onlySelected) {
		if (movielist.isReadonly()) return;

		List<UpdateMetadataTableElement> data = onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy();

		int count = 0;

		for (UpdateMetadataTableElement elem : data) {
			if (!elem.supportsGenres()) continue;
			if (elem.OnlineMeta != null && elem.OnlineMeta.Genres != null) {
				if (tableMain.DeleteLocalGenres) {
					if (!elem.getLocalGenres().equals(elem.OnlineMeta.Genres)) {
						elem.setGenres(elem.OnlineMeta.Genres);
						count++;
					}
				} else {
					boolean diff = false;
					for (CCGenre genre : elem.OnlineMeta.Genres.iterate()) {
						if (!elem.getLocalGenres().includes(genre) && !elem.getLocalGenres().isFull()) {
							elem.tryAddGenre(genre);
							diff = true;
						}
					}
					if (diff) count++;
				}
			}
		}

		DialogHelper.showDispatchInformation(this, LocaleBundle.getString("Dialogs.MetadataUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.MetadataUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$

		tableMain.forceDataChangedRedraw();
	}

	private void UpdateReferencesInDatabase(boolean onlySelected) {
		if (movielist.isReadonly()) return;

		List<UpdateMetadataTableElement> data = onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy();

		int count = 0;

		for (UpdateMetadataTableElement elem : data) {
			if (elem.OnlineMeta != null) {
				CCOnlineReferenceList onlineref = elem.OnlineMeta.getFullReference();
				CCOnlineReferenceList localref = elem.getOnlineReference();

				if (onlineref != null && onlineref.isMainSet()) {

					if (tableMain.DeleteLocalReferences) {
						if (!localref.equalsIgnoreAdditionalOrder(onlineref)) {
							elem.setOnlineReference(onlineref);
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
						if (diff) { count++; elem.setOnlineReference(localref); }
					}
				}
			}
		}

		DialogHelper.showDispatchInformation(this, LocaleBundle.getString("Dialogs.MetadataUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.MetadataUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$

		tableMain.forceDataChangedRedraw();
	}

	private void UpdateAnimeSeasonInDatabase(boolean onlySelected) {
		if (movielist.isReadonly()) return;

		List<UpdateMetadataTableElement> data = onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy();

		int count = 0;

		for (UpdateMetadataTableElement elem : data) {
			if (!elem.supportsAnime()) continue;
			if (elem.OnlineMeta == null || elem.OnlineMeta.AnimeSeason == null || elem.OnlineMeta.AnimeSeason.isEmpty()) continue;

			CCStringList merged = mergeStringList(elem.getLocalAnimeSeason(), elem.OnlineMeta.AnimeSeason, tableMain.DeleteLocalAnimeSeason);
			if (merged != null) { elem.setAnimeSeason(merged); count++; }
		}

		DialogHelper.showDispatchInformation(this, LocaleBundle.getString("Dialogs.MetadataUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.MetadataUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$

		tableMain.forceDataChangedRedraw();
	}

	private void UpdateAnimeStudioInDatabase(boolean onlySelected) {
		if (movielist.isReadonly()) return;

		List<UpdateMetadataTableElement> data = onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy();

		int count = 0;

		for (UpdateMetadataTableElement elem : data) {
			if (!elem.supportsAnime()) continue;
			if (elem.OnlineMeta == null || elem.OnlineMeta.AnimeStudio == null || elem.OnlineMeta.AnimeStudio.isEmpty()) continue;

			CCStringList merged = mergeStringList(elem.getLocalAnimeStudio(), elem.OnlineMeta.AnimeStudio, tableMain.DeleteLocalAnimeStudio);
			if (merged != null) { elem.setAnimeStudio(merged); count++; }
		}

		DialogHelper.showDispatchInformation(this, LocaleBundle.getString("Dialogs.MetadataUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.MetadataUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$

		tableMain.forceDataChangedRedraw();
	}

	/**
	 * Merges the online string-list into the local one and returns the new value, or {@code null} if nothing changed.
	 * With {@code replace == true} the online list fully replaces the local one (if different).
	 */
	private static CCStringList mergeStringList(CCStringList local, CCStringList online, boolean replace) {
		if (local == null) local = CCStringList.EMPTY;

		if (replace) {
			if (local.isEqual(online)) return null;
			return online;
		} else {
			CCStringList result = local;
			boolean diff = false;
			for (String o : online) {
				if (!result.contains(o)) {
					result = result.getAdd(o);
					diff = true;
				}
			}
			return diff ? result : null;
		}
	}

	private void filterInput() {
		initTable();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		statusInputFilter = new JPanel();
		panel2 = new JPanel();
		edInputFilter = new JTextField();
		btnInputFilter = new JButton();
		btnStartCollectingData = new JButton();
		progressBar = new JProgressBar();
		btnShowAll = new JToggleButton();
		btnShowFiltered = new JToggleButton();
		tableMain = new UpdateMetadataTable(this);
		btnUpdateSelectedOnlineScore = new JButton();
		btnUpdateAllOnlinescore = new JButton();
		btnUpdateSelectedGenres = new JButton();
		cbAllowDeleteGenres = new JCheckBox();
		btnUpdateAllGenres = new JButton();
		btnUpdateSelectedRefs = new JButton();
		cbAllowDeleteReferences = new JCheckBox();
		btnUpdateAllReferences = new JButton();
		btnUpdateSelectedAnimeSeason = new JButton();
		cbReplaceAnimeSeason = new JCheckBox();
		btnUpdateAllAnimeSeason = new JButton();
		btnUpdateSelectedAnimeStudio = new JButton();
		cbReplaceAnimeStudio = new JCheckBox();
		btnUpdateAllAnimeStudio = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("UpdateMetadataFrame.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(700, 400));
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $lcgap, default:grow, 2*($lcgap, [75dlu,default]), $ugap",
			"$ugap, 2*(default, $lgap), default:grow, 5*($lgap, default), $ugap"));

		//======== statusInputFilter ========
		{
			statusInputFilter.setLayout(new FormLayout(
				"14dlu, $lcgap, default:grow",
				"default"));

			//======== panel2 ========
			{
				panel2.setLayout(null);

				{
					// compute preferred size
					Dimension preferredSize = new Dimension();
					for(int i = 0; i < panel2.getComponentCount(); i++) {
						Rectangle bounds = panel2.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
					}
					Insets insets = panel2.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					panel2.setMinimumSize(preferredSize);
					panel2.setPreferredSize(preferredSize);
				}
			}
			statusInputFilter.add(panel2, CC.xy(1, 1));

			//---- edInputFilter ----
			edInputFilter.setText("[0]");
			statusInputFilter.add(edInputFilter, CC.xy(3, 1));
		}
		contentPane.add(statusInputFilter, CC.xywh(2, 2, 3, 1, CC.FILL, CC.FILL));

		//---- btnInputFilter ----
		btnInputFilter.setText(LocaleBundle.getString("UpdateMetadataFrame.filterBtn"));
		btnInputFilter.addActionListener(e -> filterInput());
		contentPane.add(btnInputFilter, CC.xywh(6, 2, 3, 1));

		//---- btnStartCollectingData ----
		btnStartCollectingData.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect1"));
		btnStartCollectingData.addActionListener(e -> queryOnline());
		contentPane.add(btnStartCollectingData, CC.xy(2, 4));
		contentPane.add(progressBar, CC.xy(4, 4, CC.FILL, CC.FILL));

		//---- btnShowAll ----
		btnShowAll.setText(LocaleBundle.getString("UpdateMetadataFrame.SwitchFilter1"));
		btnShowAll.setSelected(true);
		btnShowAll.addActionListener(e -> showAll());
		contentPane.add(btnShowAll, CC.xy(6, 4));

		//---- btnShowFiltered ----
		btnShowFiltered.setText(LocaleBundle.getString("UpdateMetadataFrame.SwitchFilter2"));
		btnShowFiltered.addActionListener(e -> showFiltered());
		contentPane.add(btnShowFiltered, CC.xy(8, 4));
		contentPane.add(tableMain, CC.xywh(2, 6, 7, 1, CC.FILL, CC.FILL));

		//---- btnUpdateSelectedOnlineScore ----
		btnUpdateSelectedOnlineScore.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate3"));
		btnUpdateSelectedOnlineScore.setEnabled(false);
		btnUpdateSelectedOnlineScore.addActionListener(e -> updateSelectedOnlineScore());
		contentPane.add(btnUpdateSelectedOnlineScore, CC.xy(2, 8));

		//---- btnUpdateAllOnlinescore ----
		btnUpdateAllOnlinescore.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate1"));
		btnUpdateAllOnlinescore.setEnabled(false);
		btnUpdateAllOnlinescore.addActionListener(e -> updateAllOnlineScore());
		contentPane.add(btnUpdateAllOnlinescore, CC.xy(8, 8));

		//---- btnUpdateSelectedGenres ----
		btnUpdateSelectedGenres.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate4"));
		btnUpdateSelectedGenres.setEnabled(false);
		btnUpdateSelectedGenres.addActionListener(e -> updateSelectedGenres());
		contentPane.add(btnUpdateSelectedGenres, CC.xy(2, 10));

		//---- cbAllowDeleteGenres ----
		cbAllowDeleteGenres.setText(LocaleBundle.getString("UpdateMetadataFrame.CBDelGenres"));
		cbAllowDeleteGenres.addActionListener(e -> onChangeAllowGenreDelete());
		contentPane.add(cbAllowDeleteGenres, CC.xy(4, 10));

		//---- btnUpdateAllGenres ----
		btnUpdateAllGenres.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate2"));
		btnUpdateAllGenres.setEnabled(false);
		btnUpdateAllGenres.addActionListener(e -> updateAllGenres());
		contentPane.add(btnUpdateAllGenres, CC.xy(8, 10));

		//---- btnUpdateSelectedRefs ----
		btnUpdateSelectedRefs.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate5"));
		btnUpdateSelectedRefs.setEnabled(false);
		btnUpdateSelectedRefs.addActionListener(e -> updatSelectedOnlineReferences());
		contentPane.add(btnUpdateSelectedRefs, CC.xy(2, 12));

		//---- cbAllowDeleteReferences ----
		cbAllowDeleteReferences.setText(LocaleBundle.getString("UpdateMetadataFrame.CBDelReferences"));
		cbAllowDeleteReferences.addActionListener(e -> onChangeAllowRefDelete());
		contentPane.add(cbAllowDeleteReferences, CC.xy(4, 12));

		//---- btnUpdateAllReferences ----
		btnUpdateAllReferences.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate6"));
		btnUpdateAllReferences.setEnabled(false);
		btnUpdateAllReferences.addActionListener(e -> updateAllOnlineReferences());
		contentPane.add(btnUpdateAllReferences, CC.xy(8, 12));

		//---- btnUpdateSelectedAnimeSeason ----
		btnUpdateSelectedAnimeSeason.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate7"));
		btnUpdateSelectedAnimeSeason.setEnabled(false);
		btnUpdateSelectedAnimeSeason.addActionListener(e -> updateSelectedAnimeSeason());
		contentPane.add(btnUpdateSelectedAnimeSeason, CC.xy(2, 14));

		//---- cbReplaceAnimeSeason ----
		cbReplaceAnimeSeason.setText(LocaleBundle.getString("UpdateMetadataFrame.CBReplaceAnimeSeason"));
		cbReplaceAnimeSeason.setSelected(true);
		cbReplaceAnimeSeason.addActionListener(e -> onChangeAllowAnimeSeasonReplace());
		contentPane.add(cbReplaceAnimeSeason, CC.xy(4, 14));

		//---- btnUpdateAllAnimeSeason ----
		btnUpdateAllAnimeSeason.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate8"));
		btnUpdateAllAnimeSeason.setEnabled(false);
		btnUpdateAllAnimeSeason.addActionListener(e -> updateAllAnimeSeason());
		contentPane.add(btnUpdateAllAnimeSeason, CC.xy(8, 14));

		//---- btnUpdateSelectedAnimeStudio ----
		btnUpdateSelectedAnimeStudio.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate9"));
		btnUpdateSelectedAnimeStudio.setEnabled(false);
		btnUpdateSelectedAnimeStudio.addActionListener(e -> updateSelectedAnimeStudio());
		contentPane.add(btnUpdateSelectedAnimeStudio, CC.xy(2, 16));

		//---- cbReplaceAnimeStudio ----
		cbReplaceAnimeStudio.setText(LocaleBundle.getString("UpdateMetadataFrame.CBReplaceAnimeStudio"));
		cbReplaceAnimeStudio.setSelected(true);
		cbReplaceAnimeStudio.addActionListener(e -> onChangeAllowAnimeStudioReplace());
		contentPane.add(cbReplaceAnimeStudio, CC.xy(4, 16));

		//---- btnUpdateAllAnimeStudio ----
		btnUpdateAllAnimeStudio.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate10"));
		btnUpdateAllAnimeStudio.setEnabled(false);
		btnUpdateAllAnimeStudio.addActionListener(e -> updateAllAnimeStudio());
		contentPane.add(btnUpdateAllAnimeStudio, CC.xy(8, 16));
		setSize(1200, 650);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel statusInputFilter;
	private JPanel panel2;
	private JTextField edInputFilter;
	private JButton btnInputFilter;
	private JButton btnStartCollectingData;
	private JProgressBar progressBar;
	private JToggleButton btnShowAll;
	private JToggleButton btnShowFiltered;
	private UpdateMetadataTable tableMain;
	private JButton btnUpdateSelectedOnlineScore;
	private JButton btnUpdateAllOnlinescore;
	private JButton btnUpdateSelectedGenres;
	private JCheckBox cbAllowDeleteGenres;
	private JButton btnUpdateAllGenres;
	private JButton btnUpdateSelectedRefs;
	private JCheckBox cbAllowDeleteReferences;
	private JButton btnUpdateAllReferences;
	private JButton btnUpdateSelectedAnimeSeason;
	private JCheckBox cbReplaceAnimeSeason;
	private JButton btnUpdateAllAnimeSeason;
	private JButton btnUpdateSelectedAnimeStudio;
	private JCheckBox cbReplaceAnimeStudio;
	private JButton btnUpdateAllAnimeStudio;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
