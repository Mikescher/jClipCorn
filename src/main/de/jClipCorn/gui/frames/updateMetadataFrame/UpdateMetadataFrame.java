package de.jClipCorn.gui.frames.updateMetadataFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
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
			var data = movielist.iteratorElements().map(UpdateMetadataTableElement::new).enumerate();

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

		CCDatabaseElement el = d.Element;
		OnlineMetadata md = d.OnlineMeta;

		if (el == null || md == null) return false;

		var os1 = el.getOnlinescore();
		var os2 = md.OnlineScore;

		if (os1 != null && os2 != null) {
			if (!CCOnlineScore.isEqual(os1, os2)) return true;
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
				btnInputFilter.setEnabled(false);
			});
			ThreadUtils.setProgressbarAndWait(progressBar, 0, 0, data.size()+1);

			int i = 1;
			for (UpdateMetadataTableElement elem : data) {
				ThreadUtils.setProgressbarAndWait(progressBar, i);
				i++;

				CCSingleOnlineReference ref = elem.Element.getOnlineReference().Main;
				if (! ref.isSet()) continue;

				if (elem.OnlineMeta != null && elem.OnlineMeta.OnlineScore != null) continue;

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

	private void onChangeAllowGenreDelete() {
		tableMain.DeleteLocalGenres = cbAllowDeleteGenres.isSelected();
		tableMain.forceDataChangedRedraw();
	}

	private void onChangeAllowRefDelete() {
		tableMain.DeleteLocalReferences = cbAllowDeleteReferences.isSelected();
		tableMain.forceDataChangedRedraw();
	}

	private void UpdateScoreInDatabase(boolean onlySelected) {
		if (movielist.isReadonly()) return;

		List<UpdateMetadataTableElement> data = onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy();

		int count = 0;

		for (UpdateMetadataTableElement elem : data) {
			if (elem.OnlineMeta != null && elem.OnlineMeta.OnlineScore != null) {
				if (!CCOnlineScore.isEqual(elem.Element.getOnlinescore(), elem.OnlineMeta.OnlineScore)) {
					elem.Element.onlineScore().set(elem.OnlineMeta.OnlineScore);
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
			if (elem.OnlineMeta != null && elem.OnlineMeta.Genres != null) {
				if (tableMain.DeleteLocalGenres) {
					if (!elem.Element.getGenres().equals(elem.OnlineMeta.Genres)) {
						elem.Element.Genres.set(elem.OnlineMeta.Genres);
						count++;
					}
				} else {
					boolean diff = false;
					for (CCGenre genre : elem.OnlineMeta.Genres.iterate()) {
						if (!elem.Element.getGenres().includes(genre) && !elem.Element.getGenres().isFull()) {
							elem.Element.Genres.tryAddGenre(genre);
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
				CCOnlineReferenceList localref = elem.Element.getOnlineReference();

				if (onlineref != null && onlineref.isMainSet()) {

					if (tableMain.DeleteLocalReferences) {
						if (!localref.equalsIgnoreAdditionalOrder(onlineref)) {
							elem.Element.OnlineReference.set(onlineref);
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
						if (diff) { count++; elem.Element.OnlineReference.set(localref); }
					}
				}
			}
		}

		DialogHelper.showDispatchInformation(this, LocaleBundle.getString("Dialogs.MetadataUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.MetadataUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$

		tableMain.forceDataChangedRedraw();
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

		//======== this ========
		setTitle(LocaleBundle.getString("UpdateMetadataFrame.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(700, 400));
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $lcgap, default:grow, 2*($lcgap, [75dlu,default]), $ugap", //$NON-NLS-1$
			"$ugap, 2*(default, $lgap), default:grow, 3*($lgap, default), $ugap")); //$NON-NLS-1$

		//======== statusInputFilter ========
		{
			statusInputFilter.setLayout(new FormLayout(
				"14dlu, $lcgap, default:grow", //$NON-NLS-1$
				"default")); //$NON-NLS-1$

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
			edInputFilter.setText("[0]"); //$NON-NLS-1$
			statusInputFilter.add(edInputFilter, CC.xy(3, 1));
		}
		contentPane.add(statusInputFilter, CC.xywh(2, 2, 3, 1, CC.FILL, CC.FILL));

		//---- btnInputFilter ----
		btnInputFilter.setText(LocaleBundle.getString("UpdateMetadataFrame.filterBtn")); //$NON-NLS-1$
		btnInputFilter.addActionListener(e -> filterInput());
		contentPane.add(btnInputFilter, CC.xywh(6, 2, 3, 1));

		//---- btnStartCollectingData ----
		btnStartCollectingData.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect1")); //$NON-NLS-1$
		btnStartCollectingData.addActionListener(e -> queryOnline());
		contentPane.add(btnStartCollectingData, CC.xy(2, 4));
		contentPane.add(progressBar, CC.xy(4, 4, CC.FILL, CC.FILL));

		//---- btnShowAll ----
		btnShowAll.setText(LocaleBundle.getString("UpdateMetadataFrame.SwitchFilter1")); //$NON-NLS-1$
		btnShowAll.setSelected(true);
		btnShowAll.addActionListener(e -> showAll());
		contentPane.add(btnShowAll, CC.xy(6, 4));

		//---- btnShowFiltered ----
		btnShowFiltered.setText(LocaleBundle.getString("UpdateMetadataFrame.SwitchFilter2")); //$NON-NLS-1$
		btnShowFiltered.addActionListener(e -> showFiltered());
		contentPane.add(btnShowFiltered, CC.xy(8, 4));
		contentPane.add(tableMain, CC.xywh(2, 6, 7, 1, CC.FILL, CC.FILL));

		//---- btnUpdateSelectedOnlineScore ----
		btnUpdateSelectedOnlineScore.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate3")); //$NON-NLS-1$
		btnUpdateSelectedOnlineScore.setEnabled(false);
		btnUpdateSelectedOnlineScore.addActionListener(e -> updateSelectedOnlineScore());
		contentPane.add(btnUpdateSelectedOnlineScore, CC.xy(2, 8));

		//---- btnUpdateAllOnlinescore ----
		btnUpdateAllOnlinescore.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate1")); //$NON-NLS-1$
		btnUpdateAllOnlinescore.setEnabled(false);
		btnUpdateAllOnlinescore.addActionListener(e -> updateAllOnlineScore());
		contentPane.add(btnUpdateAllOnlinescore, CC.xy(8, 8));

		//---- btnUpdateSelectedGenres ----
		btnUpdateSelectedGenres.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate4")); //$NON-NLS-1$
		btnUpdateSelectedGenres.setEnabled(false);
		btnUpdateSelectedGenres.addActionListener(e -> updateSelectedGenres());
		contentPane.add(btnUpdateSelectedGenres, CC.xy(2, 10));

		//---- cbAllowDeleteGenres ----
		cbAllowDeleteGenres.setText(LocaleBundle.getString("UpdateMetadataFrame.CBDelGenres")); //$NON-NLS-1$
		cbAllowDeleteGenres.addActionListener(e -> onChangeAllowGenreDelete());
		contentPane.add(cbAllowDeleteGenres, CC.xy(4, 10));

		//---- btnUpdateAllGenres ----
		btnUpdateAllGenres.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate2")); //$NON-NLS-1$
		btnUpdateAllGenres.setEnabled(false);
		btnUpdateAllGenres.addActionListener(e -> updateAllGenres());
		contentPane.add(btnUpdateAllGenres, CC.xy(8, 10));

		//---- btnUpdateSelectedRefs ----
		btnUpdateSelectedRefs.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate5")); //$NON-NLS-1$
		btnUpdateSelectedRefs.setEnabled(false);
		btnUpdateSelectedRefs.addActionListener(e -> updatSelectedOnlineReferences());
		contentPane.add(btnUpdateSelectedRefs, CC.xy(2, 12));

		//---- cbAllowDeleteReferences ----
		cbAllowDeleteReferences.setText(LocaleBundle.getString("UpdateMetadataFrame.CBDelReferences")); //$NON-NLS-1$
		cbAllowDeleteReferences.addActionListener(e -> onChangeAllowRefDelete());
		contentPane.add(cbAllowDeleteReferences, CC.xy(4, 12));

		//---- btnUpdateAllReferences ----
		btnUpdateAllReferences.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnUpdate6")); //$NON-NLS-1$
		btnUpdateAllReferences.setEnabled(false);
		btnUpdateAllReferences.addActionListener(e -> updateAllOnlineReferences());
		contentPane.add(btnUpdateAllReferences, CC.xy(8, 12));
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
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
