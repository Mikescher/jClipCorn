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
import de.jClipCorn.features.online.metadata.tmdb.TMDBParser;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.helper.ThreadUtils;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class UpdateMetadataFrame extends JCCFrame
{
	private enum FilterState { ALL, CHANGED }

	private FilterState selectedFilter;

	private volatile Thread collThread = null;
	private volatile boolean cancelBackground;
	private boolean collectedOnce = false;

	private volatile Thread updateThread = null;
	private volatile boolean cancelUpdate;

	public UpdateMetadataFrame(Component owner, CCMovieList ml)
	{
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		ccprops().PROP_FSIZE_UPDATEMETADATAFRAME.applyOrSkip(this);

		initTable();

		tableMain.DeleteLocalAnimeSeason = cbReplaceAnimeSeason.isSelected();
		tableMain.DeleteLocalAnimeStudio = cbReplaceAnimeStudio.isSelected();
		tableMain.OverwriteLocalShowStatus = cbOverwriteShowStatus.isSelected();
		tableMain.setGroupVisible(UpdateMetadataTable.GROUP_DETAILS, cbAllColumns.isSelected());

		var defBackground = statusInputFilter.getBackground();
		edInputFilter.getDocument().addDocumentListener(new DocumentLambdaAdapter(() -> {
			if (edInputFilter.getText().equals("[0]"))
				statusInputFilter.setBackground(defBackground);
			else if (AbstractCustomFilter.createFilterFromExport(movielist, edInputFilter.getText()) == null)
				statusInputFilter.setBackground(Color.RED);
			else
				statusInputFilter.setBackground(Color.GREEN);
		}));

		tableMain.addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) updateUIState(); });

		// a running update writes to the database - the frame must stay open until it is finished or cancelled
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (updateThread != null) return;
				cancelBackground = true;
				dispose();
			}
		});

		updateUIState();
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

	/** Must be called on the EDT. */
	private void updateUIState() {
		final boolean collecting = (collThread != null);
		final boolean updating   = (updateThread != null);
		final boolean idle       = !collecting && !updating;
		final boolean canWrite   = idle && !movielist.isReadonly();

		btnStartCollectingData.setEnabled(!updating);
		btnInputFilter.setEnabled(idle && !collectedOnce);

		btnShowAll.setEnabled(!updating);
		btnShowFiltered.setEnabled(!updating);
		cbAllColumns.setEnabled(!updating);

		cbAllowDeleteGenres.setEnabled(!updating);
		cbAllowDeleteReferences.setEnabled(!updating);
		cbReplaceAnimeSeason.setEnabled(!updating);
		cbReplaceAnimeStudio.setEnabled(!updating);
		cbOverwriteShowStatus.setEnabled(!updating);

		List<UpdateMetadataTableElement> all = canWrite ? tableMain.getDataDirect()       : List.of();
		List<UpdateMetadataTableElement> sel = canWrite ? tableMain.getSelectedElements() : List.of();

		btnUpdateAllOnlinescore.setEnabled(anyChange(all, tableMain::isScoreChange));
		btnUpdateSelectedOnlineScore.setEnabled(anyChange(sel, tableMain::isScoreChange));
		btnUpdateAllGenres.setEnabled(anyChange(all, tableMain::isGenreChange));
		btnUpdateSelectedGenres.setEnabled(anyChange(sel, tableMain::isGenreChange));
		btnUpdateAllReferences.setEnabled(anyChange(all, tableMain::isReferenceChange));
		btnUpdateSelectedRefs.setEnabled(anyChange(sel, tableMain::isReferenceChange));
		btnUpdateAllAnimeSeason.setEnabled(anyChange(all, tableMain::isAnimeSeasonChange));
		btnUpdateSelectedAnimeSeason.setEnabled(anyChange(sel, tableMain::isAnimeSeasonChange));
		btnUpdateAllAnimeStudio.setEnabled(anyChange(all, tableMain::isAnimeStudioChange));
		btnUpdateSelectedAnimeStudio.setEnabled(anyChange(sel, tableMain::isAnimeStudioChange));
		btnUpdateAllShowStatus.setEnabled(anyChange(all, tableMain::isShowStatusChange));
		btnUpdateSelectedShowStatus.setEnabled(anyChange(sel, tableMain::isShowStatusChange));

		btnCancelUpdate.setEnabled(updating && !cancelUpdate);
	}

	private static boolean anyChange(List<UpdateMetadataTableElement> data, Func1to1<UpdateMetadataTableElement, Boolean> isChange) {
		for (var e : data) if (isChange.invoke(e)) return true;
		return false;
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

		updateUIState();
	}

	private boolean FilterHandlerChanged(UpdateMetadataTableElement d) {
		if (d == null) return false;
		if (!d.Processed) return false;

		return tableMain.isShowStatusChange(d)
			|| tableMain.isScoreChange(d)
			|| tableMain.isGenreChange(d)
			|| tableMain.isAnimeSeasonChange(d)
			|| tableMain.isAnimeStudioChange(d);
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
				updateUIState();
			});
			ThreadUtils.setProgressbarAndWait(progressBar, 0, 0, data.size()+1);

			TMDBParser tmdb = new TMDBParser(movielist);

			int i = 1;
			for (UpdateMetadataTableElement elem : data) {
				ThreadUtils.setProgressbarAndWait(progressBar, i);
				i++;

				if (elem.OnlineMeta != null) continue;

				CCSingleOnlineReference ref = elem.getOnlineReference().Main;
				Metadataparser mp = ref.isSet() ? ref.getMetadataParser(movielist) : null;

				CCSingleOnlineReference tvref = elem.supportsShowStatus() ? elem.getTMDbTVReference() : null;

				if (mp == null && tvref == null) continue;

				OnlineMetadata md = null;
				if (mp != null) {
					try {
						md = mp.getMetadata(ref, false);
					} catch (Exception e) {
						CCLog.addDebug(e.toString());
					}
				}
				if (md != null) {
					elem.OnlineMeta = md;
				}

				if (tvref != null && elem.OnlineShowStatus == null) {
					if (md != null && md.ShowStatus != null) {
						elem.OnlineShowStatus = md.ShowStatus;
					} else if (!tvref.equals(ref)) {
						try {
							elem.OnlineShowStatus = tmdb.getTVShowStatus(tvref);
						} catch (Exception e) {
							CCLog.addDebug(e.toString());
						}
					}
				}

				elem.Processed = true;
				SwingUtils.invokeAndWaitSafe(() -> tableMain.changeData(elem, elem));


				if (cancelBackground) return;
			}
		} finally {
			ThreadUtils.setProgressbarAndWait(progressBar, 0, 0, 1);
			SwingUtils.invokeAndWaitSafe(() ->
			{
				collThread = null;
				collectedOnce = true;
				btnStartCollectingData.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect3"));  //$NON-NLS-1$
				updateUIState();
			});
		}
	}

	/**
	 * Applies {@code apply} to all (or the selected) elements with a pending change in a background thread.
	 * The main window is blocked and this frame cannot be closed until it is finished or cancelled.
	 */
	private void runUpdate(boolean onlySelected, Func1to1<UpdateMetadataTableElement, Boolean> isChange, Func1to1<UpdateMetadataTableElement, Boolean> apply) {
		if (movielist.isReadonly() || updateThread != null || collThread != null) return;

		final List<UpdateMetadataTableElement> data = CCStreams
				.iterate(onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy())
				.filter(isChange)
				.enumerate();

		if (data.isEmpty()) { updateUIState(); return; }

		cancelUpdate = false;
		pbUpdate.setMinimum(0);
		pbUpdate.setMaximum(data.size());
		pbUpdate.setValue(0);
		pbUpdate.setString("0 / " + data.size()); //$NON-NLS-1$

		updateThread = new Thread(() ->
		{
			int count = 0;
			MainFrame mf = MainFrame.getInstance();
			try {
				if (mf != null) mf.beginBlockingIntermediate();

				for (int i = 0; i < data.size(); i++) {
					if (cancelUpdate) break;

					try {
						if (apply.invoke(data.get(i))) count++;
					} catch (Exception e) {
						CCLog.addError(e);
					}

					// invokeAndWait keeps the worker in step with the EDT: every change queues a fireOnChangeDatabaseElement there,
					// running ahead would leave the progress (and the cancel click) behind hundreds of queued events
					final int progress = i + 1;
					SwingUtils.invokeAndWaitSafe(() -> {
						pbUpdate.setValue(progress);
						pbUpdate.setString(progress + " / " + data.size()); //$NON-NLS-1$
					});
				}
			} finally {
				if (mf != null) mf.endBlockingIntermediate();

				final int fCount = count;
				SwingUtils.invokeLater(() ->
				{
					updateThread = null;
					pbUpdate.setValue(0);
					pbUpdate.setString(Str.Empty);
					tableMain.forceDataChangedRedraw();
					updateUIState();
					DialogHelper.showDispatchInformation(this, LocaleBundle.getString("Dialogs.MetadataUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.MetadataUpdateSuccess", fCount)); //$NON-NLS-1$ //$NON-NLS-2$
				});
			}
		}, "THREAD_UPDATE_METADATA_APPLY"); //$NON-NLS-1$
		updateThread.start();

		updateUIState();
	}

	private void cancelUpdate() {
		cancelUpdate = true;
		updateUIState();
	}

	private void showAll() {
		setFiltered(FilterState.ALL, true);
	}

	private void showFiltered() {
		setFiltered(FilterState.CHANGED, true);
	}

	private void onChangeAllColumns() {
		tableMain.setGroupVisible(UpdateMetadataTable.GROUP_DETAILS, cbAllColumns.isSelected());
	}

	private void updateSelectedOnlineScore() {
		runUpdate(true, tableMain::isScoreChange, tableMain::applyScore);
	}

	private void updateAllOnlineScore() {
		runUpdate(false, tableMain::isScoreChange, tableMain::applyScore);
	}

	private void updateSelectedGenres() {
		runUpdate(true, tableMain::isGenreChange, tableMain::applyGenres);
	}

	private void updateAllGenres() {
		runUpdate(false, tableMain::isGenreChange, tableMain::applyGenres);
	}

	private void updatSelectedOnlineReferences() {
		runUpdate(true, tableMain::isReferenceChange, tableMain::applyReferences);
	}

	private void updateAllOnlineReferences() {
		runUpdate(false, tableMain::isReferenceChange, tableMain::applyReferences);
	}

	private void updateSelectedAnimeSeason() {
		runUpdate(true, tableMain::isAnimeSeasonChange, tableMain::applyAnimeSeason);
	}

	private void updateAllAnimeSeason() {
		runUpdate(false, tableMain::isAnimeSeasonChange, tableMain::applyAnimeSeason);
	}

	private void updateSelectedAnimeStudio() {
		runUpdate(true, tableMain::isAnimeStudioChange, tableMain::applyAnimeStudio);
	}

	private void updateAllAnimeStudio() {
		runUpdate(false, tableMain::isAnimeStudioChange, tableMain::applyAnimeStudio);
	}

	private void updateSelectedShowStatus() {
		runUpdate(true, tableMain::isShowStatusChange, tableMain::applyShowStatus);
	}

	private void updateAllShowStatus() {
		runUpdate(false, tableMain::isShowStatusChange, tableMain::applyShowStatus);
	}

	private void onChangeAllowShowStatusOverwrite() {
		tableMain.OverwriteLocalShowStatus = cbOverwriteShowStatus.isSelected();
		tableMain.forceDataChangedRedraw();
		updateUIState();
	}

	private void onChangeAllowGenreDelete() {
		tableMain.DeleteLocalGenres = cbAllowDeleteGenres.isSelected();
		tableMain.forceDataChangedRedraw();
		updateUIState();
	}

	private void onChangeAllowRefDelete() {
		tableMain.DeleteLocalReferences = cbAllowDeleteReferences.isSelected();
		tableMain.forceDataChangedRedraw();
		updateUIState();
	}

	private void onChangeAllowAnimeSeasonReplace() {
		tableMain.DeleteLocalAnimeSeason = cbReplaceAnimeSeason.isSelected();
		tableMain.forceDataChangedRedraw();
		updateUIState();
	}

	private void onChangeAllowAnimeStudioReplace() {
		tableMain.DeleteLocalAnimeStudio = cbReplaceAnimeStudio.isSelected();
		tableMain.forceDataChangedRedraw();
		updateUIState();
	}

	private void filterInput() {
		initTable();
		updateUIState();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		statusInputFilter = new JPanel();
		panel2 = new JPanel();
		edInputFilter = new JTextField();
		btnInputFilter = new JButton();
		btnStartCollectingData = new JButton();
		progressBar = new JProgressBar();
		cbAllColumns = new JCheckBox();
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
		btnUpdateSelectedShowStatus = new JButton();
		cbOverwriteShowStatus = new JCheckBox();
		btnUpdateAllShowStatus = new JButton();
		pbUpdate = new JProgressBar();
		btnCancelUpdate = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("UpdateMetadataFrame.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(700, 400));
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $lcgap, default:grow, $lcgap, default, 2*($lcgap, [75dlu,default]), $ugap",
			"$ugap, 2*(default, $lgap), default:grow, 7*($lgap, default), $ugap"));

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
		contentPane.add(statusInputFilter, CC.xywh(2, 2, 5, 1, CC.FILL, CC.FILL));

		//---- btnInputFilter ----
		btnInputFilter.setText(LocaleBundle.getString("UpdateMetadataFrame.filterBtn"));
		btnInputFilter.addActionListener(e -> filterInput());
		contentPane.add(btnInputFilter, CC.xywh(8, 2, 3, 1));

		//---- btnStartCollectingData ----
		btnStartCollectingData.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect1"));
		btnStartCollectingData.addActionListener(e -> queryOnline());
		contentPane.add(btnStartCollectingData, CC.xy(2, 4));
		contentPane.add(progressBar, CC.xy(4, 4, CC.FILL, CC.FILL));

		//---- cbAllColumns ----
		cbAllColumns.setText(LocaleBundle.getString("UpdateMetadataFrame.cbAllColumns.text"));
		cbAllColumns.addActionListener(e -> onChangeAllColumns());
		contentPane.add(cbAllColumns, CC.xy(6, 4));

		//---- btnShowAll ----
		btnShowAll.setText(LocaleBundle.getString("UpdateMetadataFrame.SwitchFilter1"));
		btnShowAll.setSelected(true);
		btnShowAll.addActionListener(e -> showAll());
		contentPane.add(btnShowAll, CC.xy(8, 4));

		//---- btnShowFiltered ----
		btnShowFiltered.setText(LocaleBundle.getString("UpdateMetadataFrame.SwitchFilter2"));
		btnShowFiltered.addActionListener(e -> showFiltered());
		contentPane.add(btnShowFiltered, CC.xy(10, 4));
		contentPane.add(tableMain, CC.xywh(2, 6, 9, 1, CC.FILL, CC.FILL));

		//---- btnUpdateSelectedOnlineScore ----
		btnUpdateSelectedOnlineScore.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate3"));
		btnUpdateSelectedOnlineScore.setEnabled(false);
		btnUpdateSelectedOnlineScore.addActionListener(e -> updateSelectedOnlineScore());
		contentPane.add(btnUpdateSelectedOnlineScore, CC.xy(2, 8));

		//---- btnUpdateAllOnlinescore ----
		btnUpdateAllOnlinescore.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate1"));
		btnUpdateAllOnlinescore.setEnabled(false);
		btnUpdateAllOnlinescore.addActionListener(e -> updateAllOnlineScore());
		contentPane.add(btnUpdateAllOnlinescore, CC.xy(10, 8));

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
		contentPane.add(btnUpdateAllGenres, CC.xy(10, 10));

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
		contentPane.add(btnUpdateAllReferences, CC.xy(10, 12));

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
		contentPane.add(btnUpdateAllAnimeSeason, CC.xy(10, 14));

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
		contentPane.add(btnUpdateAllAnimeStudio, CC.xy(10, 16));

		//---- btnUpdateSelectedShowStatus ----
		btnUpdateSelectedShowStatus.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate11"));
		btnUpdateSelectedShowStatus.setEnabled(false);
		btnUpdateSelectedShowStatus.addActionListener(e -> updateSelectedShowStatus());
		contentPane.add(btnUpdateSelectedShowStatus, CC.xy(2, 18));

		//---- cbOverwriteShowStatus ----
		cbOverwriteShowStatus.setText(LocaleBundle.getString("UpdateMetadataFrame.CBOverwriteShowStatus"));
		cbOverwriteShowStatus.addActionListener(e -> onChangeAllowShowStatusOverwrite());
		contentPane.add(cbOverwriteShowStatus, CC.xy(4, 18));

		//---- btnUpdateAllShowStatus ----
		btnUpdateAllShowStatus.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate12"));
		btnUpdateAllShowStatus.setEnabled(false);
		btnUpdateAllShowStatus.addActionListener(e -> updateAllShowStatus());
		contentPane.add(btnUpdateAllShowStatus, CC.xy(10, 18));

		//---- pbUpdate ----
		pbUpdate.setStringPainted(true);
		pbUpdate.setString("");
		contentPane.add(pbUpdate, CC.xywh(2, 20, 7, 1, CC.FILL, CC.FILL));

		//---- btnCancelUpdate ----
		btnCancelUpdate.setText(LocaleBundle.getString("UpdateMetadataFrame.btnCancelUpdate.text"));
		btnCancelUpdate.setEnabled(false);
		btnCancelUpdate.addActionListener(e -> cancelUpdate());
		contentPane.add(btnCancelUpdate, CC.xy(10, 20, CC.FILL, CC.DEFAULT));
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
	private JCheckBox cbAllColumns;
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
	private JButton btnUpdateSelectedShowStatus;
	private JCheckBox cbOverwriteShowStatus;
	private JButton btnUpdateAllShowStatus;
	private JProgressBar pbUpdate;
	private JButton btnCancelUpdate;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
