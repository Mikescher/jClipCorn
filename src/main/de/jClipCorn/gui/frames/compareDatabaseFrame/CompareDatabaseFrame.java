package de.jClipCorn.gui.frames.compareDatabaseFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.DoubleProgressCallbackListener;
import de.jClipCorn.util.listener.DoubleProgressCallbackProgressBarHelper;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CompareDatabaseFrame extends JFrame
{
	private final CCMovieList movielist;

	private CompareState currState = null;
	private Thread activeThread = null;

	public CompareDatabaseFrame(Component owner, CCMovieList ml)
	{
		super();

		movielist = ml;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle(LocaleBundle.getString("CompareDatabaseFrame.this.title")); //$NON-NLS-1$

		edDatabaseName.setText(CCProperties.getInstance().PROP_DATABASE_NAME.getDefault());

		try {
			edRules.setText(SimpleFileUtils.readTextResource("/compare_rules_example.txt", this.getClass())); //$NON-NLS-1$
		} catch (IOException e) {
			CCLog.addError(e);
		}

		updateUI();
	}

	private void updateUI()
	{
		var running = (activeThread != null);

		btnCompare.setEnabled(!running && !Str.isNullOrWhitespace(edDatabasePath.getText()));
		btnCreatePatch.setEnabled(!running && currState != null);
		pnlTabs.setEnabled(!running);

		edDatabaseName.setEnabled(!running);
		edRules.setEnabled(!running);
		edDatabasePath.setEnabled(!running);

		if (currState != null)
		{
			var it = CCStreams
					.<ComparisonMatch>empty()
					.append(CCStreams.iterate(currState.Movies).cast())
					.append(CCStreams.iterate(currState.Series).cast())
					.append(CCStreams.iterate(currState.AllSeasons).cast())
					.append(CCStreams.iterate(currState.AllEpisodes).cast());

			tableDeletedEntries.setData(it.filter(ComparisonMatch::getNeedsDelete).toList());
			tableUpdateCover   .setData(it.filter(ComparisonMatch::getNeedsUpdateCover).toList());
			tableUpdateFile    .setData(it.filter(ComparisonMatch::getNeedsUpdateFile).toList());
			tableUpdateMetadata.setData(it.filter(ComparisonMatch::getNeedsUpdateMetadata).toList());
			tableAddedEntry    .setData(it.filter(ComparisonMatch::getNeedsCreateNew).toList());
			tableUnchangedEntry.setData(it.filter(p -> !p.getNeedsAnything()).toList());

			tableDeletedEntries.autoResize();
			tableUpdateCover   .autoResize();
			tableUpdateFile    .autoResize();
			tableUpdateMetadata.autoResize();
			tableAddedEntry    .autoResize();
			tableUnchangedEntry.autoResize();

			pnlTabs.setTitleAt(0, LocaleBundle.getString("BatchEditFrame.tabDelete")           + " (" + tableDeletedEntries.getDataDirect().size() + ")");
			pnlTabs.setTitleAt(1, LocaleBundle.getString("BatchEditFrame.tabUpdateMeta")       + " (" + tableUpdateCover   .getDataDirect().size() + ")");
			pnlTabs.setTitleAt(2, LocaleBundle.getString("BatchEditFrame.tabUpdateCover")      + " (" + tableUpdateFile    .getDataDirect().size() + ")");
			pnlTabs.setTitleAt(3, LocaleBundle.getString("BatchEditFrame.tabUpdateFile")       + " (" + tableUpdateMetadata.getDataDirect().size() + ")");
			pnlTabs.setTitleAt(4, LocaleBundle.getString("BatchEditFrame.tabAddedEntries")     + " (" + tableAddedEntry    .getDataDirect().size() + ")");
			pnlTabs.setTitleAt(5, LocaleBundle.getString("BatchEditFrame.tabUnchangedEntries") + " (" + tableUnchangedEntry.getDataDirect().size() + ")");
		}
		else
		{
			tableDeletedEntries.clearData();
			tableUpdateCover   .clearData();
			tableUpdateFile    .clearData();
			tableUpdateMetadata.clearData();
			tableAddedEntry    .clearData();
			tableUnchangedEntry.clearData();

			pnlTabs.setTitleAt(0, LocaleBundle.getString("BatchEditFrame.tabDelete"));
			pnlTabs.setTitleAt(1, LocaleBundle.getString("BatchEditFrame.tabUpdateMeta"));
			pnlTabs.setTitleAt(2, LocaleBundle.getString("BatchEditFrame.tabUpdateCover"));
			pnlTabs.setTitleAt(3, LocaleBundle.getString("BatchEditFrame.tabUpdateFile"));
			pnlTabs.setTitleAt(4, LocaleBundle.getString("BatchEditFrame.tabAddedEntries"));
			pnlTabs.setTitleAt(5, LocaleBundle.getString("BatchEditFrame.tabUnchangedEntries"));
		}


		edEntryDiff.setText(Str.Empty);

		progressBar1.setMaximum(1);progressBar1.setValue(0);lblProgress1.setText(Str.Empty);
		progressBar2.setMaximum(1);progressBar2.setValue(0);lblProgress2.setText(Str.Empty);
	}

	private void openDatabase(ActionEvent e)
	{
		currState = null;

		final JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) { updateUI(); return; }

		edDatabasePath.setText(chooser.getSelectedFile().getAbsolutePath());
		updateUI();
	}

	private void startComparison(ActionEvent ae)
	{
		currState = null;

		var cb = new DoubleProgressCallbackProgressBarHelper(progressBar1, lblProgress1, progressBar2, lblProgress2);

		var rulestr = edRules.getText();

		activeThread = new Thread(() ->
		{
			try
			{
				var ruleset = CompareDatabaseRuleset.parse(rulestr);

				compare(cb, ruleset);
			}
			catch (Throwable e)
			{
				DialogHelper.showDispatchError(this, "Error", e.toString()); //$NON-NLS-1$
			}
			finally
			{
				activeThread = null;
				SwingUtils.invokeLater(this::updateUI);
				cb.reset();
			}
		});
		activeThread.start();

		updateUI();
	}

	private void compare(DoubleProgressCallbackListener cb, CompareDatabaseRuleset ruleset) throws Exception
	{
		cb.setMaxAndResetValueBoth(3, 1);
		cb.setValueBoth(0, 0, "Connecting", "");

		var mlExt = CCMovieList.loadExtern(null, edDatabasePath.getText(), edDatabaseName.getText(), true);

		if (!mlExt.databaseExists()) throw new Exception("Database " + edDatabasePath.getText() + " | " + edDatabaseName.getText() + " not found");

		cb.setValueBoth(1, 0, "Reading", "");

		mlExt.connectExternal(false);
		try
		{
			cb.setValueBoth(2, 0, "Comparing", "");
			cb.setSubMax(mlExt.getTotalDatabaseElementCount() + movielist.getTotalDatabaseElementCount() + 1);

			var state = new CompareState(cb, ruleset);

			compareAndMatchMovies(mlExt, movielist, state);
			compareAndMatchSeries(mlExt, movielist, state);

			currState = state;
		}
		finally
		{
			mlExt.disconnectDatabase(true);
		}

		SwingUtils.invokeLater(this::updateUI);
	}

	private void compareAndMatchMovies(CCMovieList mlExt, CCMovieList mlLoc, CompareState state)
	{
		var movsLoc = mlLoc.iteratorMovies().filter(e -> !state.Ruleset.ShouldSkipLoc(e.LocalID.get())).toList();
		var movsExt = mlExt.iteratorMovies().filter(e -> !state.Ruleset.ShouldSkipExt(e.LocalID.get())).toList();

		// Force matched by Ruleset
		for (var mloc : new ArrayList<>(movsLoc))
		{
			var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
			{
				return state.Ruleset.IsMatch(mloc.getLocalID(), m.getLocalID());
			}, null, null);
			if (mext == null) continue;

			state.addMovieMatch(mloc, mext);
			movsLoc.remove(mloc);
			movsExt.remove(mext);
		}

		// Movies with the same checksum are matches
		for (var mloc : new ArrayList<>(movsLoc))
		{
			if (mloc.MediaInfo.get().isUnset() || Str.isNullOrWhitespace(mloc.MediaInfo.get().getChecksum())) continue;

			var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
			{
				return Str.equals(m.MediaInfo.get().getChecksum(), mloc.MediaInfo.get().getChecksum());
			}, null, null);
			if (mext == null) continue;

			state.addMovieMatch(mloc, mext);
			movsLoc.remove(mloc);
			movsExt.remove(mext);
		}

		// Movies with (exactly) the same online-refs + same language
		for (var mloc : new ArrayList<>(movsLoc))
		{
			if (mloc.OnlineReference.get().totalCount() == 0) continue;

			var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
			{
				return m.OnlineReference.get().totalCount() > 0 &&
						mloc.OnlineReference.get().equalsAnyOrder(m.OnlineReference.get()) &&
						mloc.Language.get().isEqual(m.Language.get());
			}, null, null);
			if (mext == null) continue;

			state.addMovieMatch(mloc, mext);
			movsLoc.remove(mloc);
			movsExt.remove(mext);
		}

		// Movies with (exactly) the same online-refs (but evtl different language)
		for (var mloc : new ArrayList<>(movsLoc))
		{
			if (mloc.OnlineReference.get().totalCount() == 0) continue;

			var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
			{
				return m.OnlineReference.get().totalCount() > 0 &&
						mloc.OnlineReference.get().equalsAnyOrder(m.OnlineReference.get());
			}, null, null);
			if (mext == null) continue;

			state.addMovieMatch(mloc, mext);
			movsLoc.remove(mloc);
			movsExt.remove(mext);
		}

		// Movies with which contains at least 1 online ref with the same value
		for (var mloc : new ArrayList<>(movsLoc))
		{
			if (mloc.OnlineReference.get().totalCount() == 0) continue;

			var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
			{
				return m.OnlineReference.get().totalCount() > 0 &&
						mloc.OnlineReference.get().equalsAnyNonEmptySubset(m.OnlineReference.get());
			}, null, null);
			if (mext == null) continue;

			state.addMovieMatch(mloc, mext);
			movsLoc.remove(mloc);
			movsExt.remove(mext);
		}

		// Movies with the same name + zyklus + lang
		for (var mloc : new ArrayList<>(movsLoc))
		{
			var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
			{
				if (!Str.equals(mloc.Title.get(), m.Title.get())) return false;
				if (!Str.equals(mloc.Zyklus.get().getTitle(), m.Zyklus.get().getTitle())) return false;
				if (mloc.Zyklus.get().getNumber() != m.Zyklus.get().getNumber()) return false;
				if (!mloc.Language.get().isEqual(m.Language.get())) return false;
				return true;
			}, null, null);
			if (mext == null) continue;

			state.addMovieMatch(mloc, mext);
			movsLoc.remove(mloc);
			movsExt.remove(mext);
		}

		for (var mloc : movsLoc)
		{
			state.addMovieLocalOnly(mloc);
		}
		movsLoc.clear();

		for (var mext : movsExt)
		{
			state.addMovieExternOnly(mext);
		}
		movsExt.clear();
	}

	private void compareAndMatchSeries(CCMovieList mlExt, CCMovieList mlLoc, CompareState state)
	{
		var serLoc = mlLoc.iteratorSeries().filter(e -> !state.Ruleset.ShouldSkipLoc(e.LocalID.get())).toList();
		var serExt = mlExt.iteratorSeries().filter(e -> !state.Ruleset.ShouldSkipExt(e.LocalID.get())).toList();

		// Force matched by Ruleset
		for (var sloc : new ArrayList<>(serLoc))
		{
			var sext = CCStreams.iterate(serExt).singleOrDefault(s ->
			{
				return state.Ruleset.IsMatch(sloc.getLocalID(), s.getLocalID());
			}, null, null);
			if (sext == null) continue;

			var match = state.addSeriesMatch(sloc, sext);
			serLoc.remove(sloc);
			serExt.remove(sext);
			compareAndMatchSeasons(match);
		}

		// Series with (exactly) the same online-refs + same language
		for (var sloc : new ArrayList<>(serLoc))
		{
			if (sloc.OnlineReference.get().totalCount() == 0) continue;

			var sext = CCStreams.iterate(serExt).singleOrDefault(s ->
			{
				return s.OnlineReference.get().totalCount() > 0 &&
						sloc.OnlineReference.get().equalsAnyOrder(s.OnlineReference.get()) &&
						sloc.getAllLanguages().isEqual(s.getAllLanguages()) &&
						sloc.getCommonLanguages().isEqual(s.getCommonLanguages());
			}, null, null);
			if (sext == null) continue;

			var match = state.addSeriesMatch(sloc, sext);
			serLoc.remove(sloc);
			serExt.remove(sext);
			compareAndMatchSeasons(match);
		}

		// Series with (exactly) the same online-refs (but evtl different language)
		for (var sloc : new ArrayList<>(serLoc))
		{
			if (sloc.OnlineReference.get().totalCount() == 0) continue;

			var sext = CCStreams.iterate(serExt).singleOrDefault(s ->
			{
				return s.OnlineReference.get().totalCount() > 0 && sloc.OnlineReference.get().equalsAnyOrder(s.OnlineReference.get());
			}, null, null);
			if (sext == null) continue;

			var match = state.addSeriesMatch(sloc, sext);
			serLoc.remove(sloc);
			serExt.remove(sext);
			compareAndMatchSeasons(match);
		}

		// Series with which contains at least 1 online ref with the same value
		for (var sloc : new ArrayList<>(serLoc))
		{
			if (sloc.OnlineReference.get().totalCount() == 0) continue;

			var sext = CCStreams.iterate(serExt).singleOrDefault(s ->
			{
				return s.OnlineReference.get().totalCount() > 0 &&
						sloc.OnlineReference.get().equalsAnyNonEmptySubset(s.OnlineReference.get());
			}, null, null);
			if (sext == null) continue;

			var match = state.addSeriesMatch(sloc, sext);
			serLoc.remove(sloc);
			serExt.remove(sext);
			compareAndMatchSeasons(match);
		}

		// Series with the same title + lang
		for (var sloc : new ArrayList<>(serLoc))
		{
			var sext = CCStreams.iterate(serExt).singleOrDefault(s ->
			{
				if (!Str.equals(sloc.Title.get(), s.Title.get())) return false;
				if (!sloc.getAllLanguages().isEqual(s.getAllLanguages())) return false;
				if (!sloc.getCommonLanguages().isEqual(s.getCommonLanguages())) return false;
				return true;
			}, null, null);
			if (sext == null) continue;

			var match = state.addSeriesMatch(sloc, sext);
			serLoc.remove(sloc);
			serExt.remove(sext);
			compareAndMatchSeasons(match);
		}

		for (var sloc : serLoc)
		{
			var match = state.addSeriesLocalOnly(sloc);
			for (var nloc: sloc.iteratorSeasons())
			{
				var match2 = match.addSeasonLocalOnly(nloc);
				for (var eloc: nloc.iteratorEpisodes())
				{
					match2.addEpisodeLocalOnly(eloc);
				}
			}
		}
		serLoc.clear();

		for (var sext : serExt)
		{
			var match = state.addSeriesExternOnly(sext);
			for (var next: sext.iteratorSeasons())
			{
				var match2 = match.addSeasonExternOnly(next);
				for (var eext: next.iteratorEpisodes())
				{
					match2.addEpisodeExternOnly(eext);
				}
			}
		}
		serExt.clear();
	}

	private void compareAndMatchSeasons(SeriesMatch match)
	{
		var seaLoc = match.SeriesLocal .iteratorSeasons().filter(e -> !match.State.Ruleset.ShouldSkipLoc(e.LocalID.get())).toList();
		var seaExt = match.SeriesExtern.iteratorSeasons().filter(e -> !match.State.Ruleset.ShouldSkipExt(e.LocalID.get())).toList();

		// Force matched by Ruleset
		for (var sloc : new ArrayList<>(seaLoc))
		{
			var sext = CCStreams.iterate(seaExt).singleOrDefault(s ->
			{
				return match.State.Ruleset.IsMatch(sloc.getLocalID(), s.getLocalID());
			}, null, null);
			if (sext == null) continue;

			var submatch = match.addSeasonMatch(sloc, sext);
			seaLoc.remove(sloc);
			seaExt.remove(sext);
			compareAndMatchEpisodes(submatch);
		}

		// Seasons with same Title+Year
		for (var sloc : new ArrayList<>(seaLoc))
		{
			var sext = CCStreams.iterate(seaExt).singleOrDefault(s ->
			{
				return Str.equals(s.Title.get(), sloc.Title.get()) &&
						s.Year.get().equals(sloc.Year.get());
			}, null, null);
			if (sext == null) continue;

			var submatch = match.addSeasonMatch(sloc, sext);
			seaLoc.remove(sloc);
			seaExt.remove(sext);
			compareAndMatchEpisodes(submatch);
		}

		// Seasons with same Title (evtl diff year)
		for (var sloc : new ArrayList<>(seaLoc))
		{
			var sext = CCStreams.iterate(seaExt).singleOrDefault(s ->
			{
				return Str.equals(s.Title.get(), sloc.Title.get());
			}, null, null);
			if (sext == null) continue;

			var submatch = match.addSeasonMatch(sloc, sext);
			seaLoc.remove(sloc);
			seaExt.remove(sext);
			compareAndMatchEpisodes(submatch);
		}

		// Seasons with same Year (must be unique) and same index+episodecount
		for (var sloc : new ArrayList<>(seaLoc))
		{
			var sext = CCStreams.iterate(seaExt).singleOrDefault(s ->
			{
				return s.Year.get().equals(sloc.Year.get());
			}, null, null);
			if (sext == null) continue;
			if (sext.getSortedSeasonNumber() != sloc.getSortedSeasonNumber()) continue;
			if (sext.getEpisodeCount()       != sloc.getEpisodeCount())       continue;

			var submatch = match.addSeasonMatch(sloc, sext);
			seaLoc.remove(sloc);
			seaExt.remove(sext);
			compareAndMatchEpisodes(submatch);
		}

		for (var sloc : seaLoc)
		{
			var submatch = match.addSeasonLocalOnly(sloc);
			for (var eloc: sloc.iteratorEpisodes())
			{
				submatch.addEpisodeLocalOnly(eloc);
			}
		}
		seaLoc.clear();

		for (var sext : seaExt)
		{
			var submatch = match.addSeasonExternOnly(sext);
			for (var eext: sext.iteratorEpisodes())
			{
				submatch.addEpisodeExternOnly(eext);
			}
		}
		seaExt.clear();
	}

	private void compareAndMatchEpisodes(SeasonMatch match)
	{
		var episLoc = match.SeasonLocal .iteratorEpisodes().filter(e -> !match.State.Ruleset.ShouldSkipLoc(e.LocalID.get())).toList();
		var episExt = match.SeasonExtern.iteratorEpisodes().filter(e -> !match.State.Ruleset.ShouldSkipExt(e.LocalID.get())).toList();

		// Force matched by Ruleset
		for (var eloc : new ArrayList<>(episLoc))
		{
			var eext = CCStreams.iterate(episExt).singleOrDefault(e ->
			{
				return match.State.Ruleset.IsMatch(eloc.getLocalID(), e.getLocalID());
			}, null, null);
			if (eext == null) continue;

			match.addEpisodeMatch(eloc, eext);
			episLoc.remove(eloc);
			episExt.remove(eext);
		}

		// Episodes with the same checksum are matches
		for (var eloc : new ArrayList<>(episLoc))
		{
			if (eloc.MediaInfo.get().isUnset() || Str.isNullOrWhitespace(eloc.MediaInfo.get().getChecksum())) continue;

			var eext = CCStreams.iterate(episExt).singleOrDefault(e ->
			{
				return Str.equals(e.MediaInfo.get().getChecksum(), eloc.MediaInfo.get().getChecksum());
			}, null, null);
			if (eext == null) continue;

			match.addEpisodeMatch(eloc, eext);
			episLoc.remove(eloc);
			episExt.remove(eext);
		}

		// Episodes with the same number
		for (var eloc : new ArrayList<>(episLoc))
		{
			var eext = CCStreams.iterate(episExt).singleOrDefault(e ->
			{
				return e.EpisodeNumber.get().equals(eloc.EpisodeNumber.get());
			}, null, null);
			if (eext == null) continue;

			match.addEpisodeMatch(eloc, eext);
			episLoc.remove(eloc);
			episExt.remove(eext);
		}

		for (var eloc : episLoc)
		{
			match.addEpisodeLocalOnly(eloc);
		}
		episLoc.clear();

		for (var eext : episExt)
		{
			match.addEpisodeExternOnly(eext);
		}
		episExt.clear();
	}

	public void showDiffStr(String diffStr) {
		edEntryDiff.setText(diffStr);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		edDatabasePath = new ReadableTextField();
		btnOpenDatabase = new JButton();
		label2 = new JLabel();
		edDatabaseName = new JTextField();
		label1 = new JLabel();
		scrollPane1 = new JScrollPane();
		edRules = new JTextArea();
		btnCompare = new JButton();
		pnlTabs = new JTabbedPane();
		tabDeletedEntries = new JPanel();
		tableDeletedEntries = new ShowMatchesTable(this, false, true);
		tabUpdateMetadata = new JPanel();
		tableUpdateMetadata = new ShowMatchesTable(this, true, true);
		tabUpdateCover = new JPanel();
		tableUpdateCover = new ShowMatchesTable(this, true, true);
		tabUpdateFile = new JPanel();
		tableUpdateFile = new ShowMatchesTable(this, true, true);
		tabAddedEntry = new JPanel();
		tableAddedEntry = new ShowMatchesTable(this, true, false);
		tabUnchangedEntry = new JPanel();
		tableUnchangedEntry = new ShowMatchesTable(this, true, true);
		scrollPane2 = new JScrollPane();
		edEntryDiff = new JTextArea();
		btnCreatePatch = new JButton();
		progressBar1 = new JProgressBar();
		lblProgress1 = new JLabel();
		progressBar2 = new JProgressBar();
		lblProgress2 = new JLabel();

		//======== this ========
		setTitle(LocaleBundle.getString("CompareDatabaseFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $lcgap, default:grow, $lcgap, 70dlu, $ugap", //$NON-NLS-1$
			"$ugap, 3*(default, $lgap), 80dlu, $lgap, 20dlu, $lgap, default:grow, $lgap, 80dlu, 3*($lgap, default), $ugap")); //$NON-NLS-1$
		contentPane.add(edDatabasePath, CC.xywh(2, 2, 3, 1, CC.FILL, CC.FILL));

		//---- btnOpenDatabase ----
		btnOpenDatabase.setText("..."); //$NON-NLS-1$
		btnOpenDatabase.addActionListener(e -> openDatabase(e));
		contentPane.add(btnOpenDatabase, CC.xy(6, 2));

		//---- label2 ----
		label2.setText(LocaleBundle.getString("BatchEditFrame.lblDBName")); //$NON-NLS-1$
		contentPane.add(label2, CC.xy(2, 4));
		contentPane.add(edDatabaseName, CC.xy(4, 4));

		//---- label1 ----
		label1.setText("Rules:"); //$NON-NLS-1$
		contentPane.add(label1, CC.xywh(2, 6, 3, 1));

		//======== scrollPane1 ========
		{

			//---- edRules ----
			edRules.setText("[from_ressources]"); //$NON-NLS-1$
			scrollPane1.setViewportView(edRules);
		}
		contentPane.add(scrollPane1, CC.xywh(2, 8, 5, 1, CC.FILL, CC.FILL));

		//---- btnCompare ----
		btnCompare.setText(LocaleBundle.getString("BatchEditFrame.btnCompare")); //$NON-NLS-1$
		btnCompare.addActionListener(e -> startComparison(e));
		contentPane.add(btnCompare, CC.xywh(2, 10, 5, 1));

		//======== pnlTabs ========
		{

			//======== tabDeletedEntries ========
			{
				tabDeletedEntries.setLayout(new BorderLayout());
				tabDeletedEntries.add(tableDeletedEntries, BorderLayout.CENTER);
			}
			pnlTabs.addTab(LocaleBundle.getString("BatchEditFrame.tabDelete"), tabDeletedEntries); //$NON-NLS-1$

			//======== tabUpdateMetadata ========
			{
				tabUpdateMetadata.setLayout(new BorderLayout());
				tabUpdateMetadata.add(tableUpdateMetadata, BorderLayout.CENTER);
			}
			pnlTabs.addTab(LocaleBundle.getString("BatchEditFrame.tabUpdateMeta"), tabUpdateMetadata); //$NON-NLS-1$

			//======== tabUpdateCover ========
			{
				tabUpdateCover.setLayout(new BorderLayout());
				tabUpdateCover.add(tableUpdateCover, BorderLayout.CENTER);
			}
			pnlTabs.addTab(LocaleBundle.getString("BatchEditFrame.tabUpdateCover"), tabUpdateCover); //$NON-NLS-1$

			//======== tabUpdateFile ========
			{
				tabUpdateFile.setLayout(new BorderLayout());
				tabUpdateFile.add(tableUpdateFile, BorderLayout.CENTER);
			}
			pnlTabs.addTab(LocaleBundle.getString("BatchEditFrame.tabUpdateFile"), tabUpdateFile); //$NON-NLS-1$

			//======== tabAddedEntry ========
			{
				tabAddedEntry.setLayout(new BorderLayout());
				tabAddedEntry.add(tableAddedEntry, BorderLayout.CENTER);
			}
			pnlTabs.addTab(LocaleBundle.getString("BatchEditFrame.tabAddedEntries"), tabAddedEntry); //$NON-NLS-1$

			//======== tabUnchangedEntry ========
			{
				tabUnchangedEntry.setLayout(new BorderLayout());
				tabUnchangedEntry.add(tableUnchangedEntry, BorderLayout.CENTER);
			}
			pnlTabs.addTab(LocaleBundle.getString("BatchEditFrame.tabUnchangedEntries"), tabUnchangedEntry); //$NON-NLS-1$
		}
		contentPane.add(pnlTabs, CC.xywh(2, 12, 5, 1, CC.FILL, CC.FILL));

		//======== scrollPane2 ========
		{

			//---- edEntryDiff ----
			edEntryDiff.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			edEntryDiff.setEditable(false);
			scrollPane2.setViewportView(edEntryDiff);
		}
		contentPane.add(scrollPane2, CC.xywh(2, 14, 5, 1, CC.DEFAULT, CC.FILL));

		//---- btnCreatePatch ----
		btnCreatePatch.setText(LocaleBundle.getString("BatchEditFrame.btnCreatePatch")); //$NON-NLS-1$
		contentPane.add(btnCreatePatch, CC.xywh(2, 16, 5, 1));
		contentPane.add(progressBar1, CC.xywh(2, 18, 3, 1));
		contentPane.add(lblProgress1, CC.xy(6, 18));
		contentPane.add(progressBar2, CC.xywh(2, 20, 3, 1, CC.DEFAULT, CC.FILL));
		contentPane.add(lblProgress2, CC.xy(6, 20));
		setSize(800, 725);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private ReadableTextField edDatabasePath;
	private JButton btnOpenDatabase;
	private JLabel label2;
	private JTextField edDatabaseName;
	private JLabel label1;
	private JScrollPane scrollPane1;
	private JTextArea edRules;
	private JButton btnCompare;
	private JTabbedPane pnlTabs;
	private JPanel tabDeletedEntries;
	private ShowMatchesTable tableDeletedEntries;
	private JPanel tabUpdateMetadata;
	private ShowMatchesTable tableUpdateMetadata;
	private JPanel tabUpdateCover;
	private ShowMatchesTable tableUpdateCover;
	private JPanel tabUpdateFile;
	private ShowMatchesTable tableUpdateFile;
	private JPanel tabAddedEntry;
	private ShowMatchesTable tableAddedEntry;
	private JPanel tabUnchangedEntry;
	private ShowMatchesTable tableUnchangedEntry;
	private JScrollPane scrollPane2;
	private JTextArea edEntryDiff;
	private JButton btnCreatePatch;
	private JProgressBar progressBar1;
	private JLabel lblProgress1;
	private JProgressBar progressBar2;
	private JLabel lblProgress2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
