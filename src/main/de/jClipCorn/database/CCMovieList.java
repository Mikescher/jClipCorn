package de.jClipCorn.database;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.SwingUtilities;

import de.jClipCorn.database.driver.PublicDatabaseInterface;
import de.jClipCorn.features.serialization.xmlexport.DatabaseXMLExporter;
import de.jClipCorn.features.serialization.xmlexport.ExportOptions;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.lambda.Func0to0;
import org.jdom2.Document;
import org.jdom2.Element;

import de.jClipCorn.Globals;
import de.jClipCorn.Main;
import de.jClipCorn.database.covertab.ICoverCache;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCDatedElement;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.driver.DatabaseConnectResult;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.features.backupManager.BackupManager;
import de.jClipCorn.database.util.iterators.DatedElementsIterator;
import de.jClipCorn.database.util.iterators.EpisodesIterator;
import de.jClipCorn.database.util.iterators.MoviesIterator;
import de.jClipCorn.database.util.iterators.PlayablesIterator;
import de.jClipCorn.database.util.iterators.SeasonsIterator;
import de.jClipCorn.database.util.iterators.SeriesIterator;
import de.jClipCorn.gui.frames.initialConfigFrame.InitialConfigFrame;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.comparator.CCDatabaseElementComparator;
import de.jClipCorn.util.comparator.CCMovieComparator;
import de.jClipCorn.util.comparator.CCSeriesComparator;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;
import de.jClipCorn.util.stream.IterableStream;

public class CCMovieList {
	private static CCMovieList instance = null;
	
	private List<CCDatabaseElement> list;
	
	private List<CCDBUpdateListener> listener;
	
	private CCDatabase database;
	private List<CCGroup> databaseGroups;

	private boolean blocked = false;

	private CCMovieList(CCDatabase db) {
		this.list     = new Vector<>();
		this.listener = new Vector<>();

		this.databaseGroups = new ArrayList<>();
		this.database = db;

		instance = this;
	}
	
	public static CCMovieList create() {
		return new CCMovieList(CCDatabase.create(CCProperties.getInstance().PROP_DATABASE_NAME.getValue()));
	}
	
	public static CCMovieList createInMemory() {
		return new CCMovieList(CCDatabase.createInMemory());
	}
	
	public static CCMovieList createStub() {
		return new CCMovieList(CCDatabase.createStub());
	}

	public void showInitialWizard() {
		if (!database.exists(CCProperties.getInstance().PROP_DATABASE_NAME.getValue())) {
			boolean cont = InitialConfigFrame.ShowWizard();
			
			if (! cont) {
				ApplicationHelper.exitApplication(0, true);
			}

			database = CCDatabase.create(CCProperties.getInstance().PROP_DATABASE_NAME.getValue()); // in case db type has changed
		}
	}
	
	public void connect(final MainFrame mf, Func0to0 postInit) {
		new Thread(() ->
		{
			Globals.TIMINGS.start(Globals.TIMING_LOAD_TOTAL);
			{
				BackupManager bm = new BackupManager(CCMovieList.this);
				bm.init();
				bm.doActions(mf);

				mf.beginBlockingIntermediate();

				Globals.TIMINGS.start(Globals.TIMING_LOAD_DATABASE_CONNECT);
				{
					DatabaseConnectResult dbcr = database.tryconnect();

					if (dbcr == DatabaseConnectResult.ERROR_CANTCONNECT) {
						CCLog.addFatalError(LocaleBundle.getString("LogMessage.ErrorConnectDB"), database.getLastError()); //$NON-NLS-1$
					} else if (dbcr == DatabaseConnectResult.ERROR_CANTCREATE) {
						CCLog.addFatalError(LocaleBundle.getString("LogMessage.ErrorCreateDB"), database.getLastError()); //$NON-NLS-1$
					}
				}
				Globals.TIMINGS.stop(Globals.TIMING_LOAD_DATABASE_CONNECT);

				testDatabaseVersion();

				Globals.TIMINGS.start(Globals.TIMING_LOAD_DATABASE);
				{
					Globals.TIMINGS.start(Globals.TIMING_LOAD_MOVIELIST_FILL_GROUPS);
					{
						database.fillGroups(CCMovieList.this);
					}
					Globals.TIMINGS.stop(Globals.TIMING_LOAD_MOVIELIST_FILL_GROUPS);
					Globals.TIMINGS.start(Globals.TIMING_LOAD_MOVIELIST_FILL_ELEMENTS);
					{
						database.fillMovieList(CCMovieList.this);
					}
					Globals.TIMINGS.stop(Globals.TIMING_LOAD_MOVIELIST_FILL_ELEMENTS);
					Globals.TIMINGS.start(Globals.TIMING_LOAD_MOVIELIST_FILL_COVERS);
					{
						database.fillCoverCache();
					}
					Globals.TIMINGS.stop(Globals.TIMING_LOAD_MOVIELIST_FILL_COVERS);
				}
				Globals.TIMINGS.stop(Globals.TIMING_LOAD_DATABASE);

				fireOnAfterLoad();

				if (postInit != null) SwingUtilities.invokeLater(postInit::invoke);
			}
			Globals.TIMINGS.stop(Globals.TIMING_LOAD_TOTAL);

			Globals.TIMINGS.stop(Globals.TIMING_STARTUP_TOTAL);

			mf.endBlockingIntermediate();
		}, "THREAD_LOAD_DATABASE").start(); //$NON-NLS-1$
	}
	
	public void connectForTests() {
		database.tryconnect();

		database.fillGroups(CCMovieList.this);
		database.fillMovieList(CCMovieList.this);
		database.fillCoverCache();
	}
	public void forceReconnectAndReloadForTests() {
		// do no call clear, we only want to remove RAM values

		databaseGroups.clear();
		list.clear();
		database.resetForTestReload();

		// refill
		database.fillGroups(CCMovieList.this);
		database.fillMovieList(CCMovieList.this);
		database.fillCoverCache();
	}

	public CCDatabaseElement getDatabaseElementBySort(int row) { // WARNIG SORT <> MOVIEID || SORT IN DATABASE (SORTED BY MOVIEID)
		return list.get(row);
	}
	
	public int getSortByDatabaseElement(CCDatabaseElement el) {
		return list.indexOf(el);
	}

	public int getElementCount() {
		return list.size();
	}

	public int getTotalDatabaseCount() {
		int c = 0;
		for (CCDatabaseElement dbe : list) {
			if (dbe.isMovie()) {
				c++;
			} else {
				c += ((CCSeries) dbe).getEpisodeCount();
			}
		}
		return c;
	}

	public int getTotalDatabaseElementCount() {
		int c = 0;
		for (CCDatabaseElement dbe : list) {
			if (dbe.isMovie()) {
				c++;
			} else {
				c++;
				c += ((CCSeries) dbe).getSeasonCount();
				c += ((CCSeries) dbe).getEpisodeCount();
			}
		}
		return c;
	}

	public int getViewedCount() {
		int v = 0;
		for (CCDatabaseElement m : list) {
			if (m.isMovie()) {
				if (((CCMovie) m).isViewed()) {
					v++;
				}
			} else if (CCProperties.getInstance().PROP_INCLUDE_SERIES_IN_VIEWEDCOUNT.getValue()) {
				if (((CCSeries) m).isViewed()) {
					v++;
				}
			}
		}
		return v;
	}

	public CCDatabaseElement findDatabaseElement(int id) {
		for (CCDatabaseElement m : list) {
			if (m.getLocalID() == id) {
				return m;
			}
		}
		return null;
	}

	public CCMovie findDatabaseMovie(int id) {
		CCDatabaseElement e = findDatabaseElement(id);
		
		if (e instanceof CCMovie) return (CCMovie)e;
		
		return null;
	}

	public CCSeries findDatabaseSeries(int id) {
		CCDatabaseElement e = findDatabaseElement(id);
		
		if (e instanceof CCSeries) return (CCSeries)e;
		
		return null;
	}

	public CCMovie createNewEmptyMovie() { // Does this make getMovieBySort fail (id changes etc ??)
		CCMovie mov = database.createNewEmptyMovie(this);
		list.add(mov);

		fireOnAddDatabaseElement(mov);

		return mov;
	}

	public CCSeries createNewEmptySeries() { // Does this make getMovieBySort fail (id changes etc ??)
		CCSeries s = database.createNewEmptySeries(this);
		list.add(s);

		fireOnAddDatabaseElement(s);

		return s;
	}

	public CCSeason createNewEmptySeason(CCSeries s) {
		CCSeason seas = database.createNewEmptySeason(s);
		s.directlyInsertSeason(seas);

		fireOnChangeDatabaseElement(s);

		return seas;
	}

	public CCEpisode createNewEmptyEpisode(CCSeason s) {
		CCEpisode e = database.createNewEmptyEpisode(s);
		s.directlyInsertEpisode(e);

		fireOnChangeDatabaseElement(s.getSeries());

		return e;
	}

	// Only used by CCDatabase.fillMovieList
	public void directlyInsert(final CCDatabaseElement m) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					list.add(m);
					fireOnAddDatabaseElement(m);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
			return;
		}
	}

	// Only used by CCDatabase.fillMovieList
	public void directlyInsert(final List<CCDatabaseElement> m) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					list.addAll(m);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
			return;
		}
	}

	public void clear() { // Serious method is doing serious business
		while (!list.isEmpty()) {
			remove(list.get(0));
		}
	}

	public void addChangeListener(CCDBUpdateListener l) {
		listener.add(l);
	}

	public void fireOnAddDatabaseElement(final CCDatabaseElement el) {
		if (!EventQueue.isDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireOnAddDatabaseElement(el);
				}
			});
			return;
		}

		for (CCDBUpdateListener l : listener) {
			l.onAddDatabaseElement(el);
		}
	}

	public void fireOnChangeDatabaseElement(final CCDatabaseElement el) {
		if (!EventQueue.isDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireOnChangeDatabaseElement(el);
				}
			});
			return;
		}

		for (CCDBUpdateListener l : listener) {
			l.onChangeDatabaseElement(el);
		}
	}

	public void fireOnRemDatabaseElement(final CCDatabaseElement el) {
		if (!EventQueue.isDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireOnRemDatabaseElement(el);
				}
			});
			return;
		}

		for (CCDBUpdateListener l : listener) {
			l.onRemMovie(el);
		}
	}

	private void fireOnAfterLoad() {
		if (!EventQueue.isDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireOnAfterLoad();
				}
			});
			return;
		}
		
		database.upgrader.onAfterConnect(this, database);

		for (CCDBUpdateListener l : listener) {
			l.onAfterLoad();
		}
	}
	
	public void fireOnRefresh() {
		if (!EventQueue.isDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireOnRefresh();
				}
			});
			return;
		}

		for (CCDBUpdateListener l : listener) {
			l.onRefresh();
		}
	}

	public void update(CCMovie el) {
		if (CCProperties.getInstance().ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			
			el.beginUpdating();
			database.updateMovieFromDatabase(el);
			el.abortUpdating();
			
			return;
		}
		
		database.updateMovieInDatabase(el);

		fireOnChangeDatabaseElement(el);
	}

	public void update(CCEpisode ep) {
		if (CCProperties.getInstance().ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
						
			ep.beginUpdating();
			database.updateEpisodeFromDatabase(ep);
			ep.abortUpdating();
			
			return;
		}
		
		database.updateEpisodeInDatabase(ep);

		fireOnChangeDatabaseElement(ep.getSeries()); // Why on onAdd
	}

	public void update(CCSeries se) {
		if (CCProperties.getInstance().ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			
			se.beginUpdating();
			database.updateSeriesFromDatabase(se);
			se.abortUpdating();
			
			return;
		}
		
		database.updateSeriesInDatabase(se);

		fireOnChangeDatabaseElement(se);
	}

	public void update(CCSeason sa) {
		if (CCProperties.getInstance().ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			
			sa.beginUpdating();
			database.updateSeasonFromDatabase(sa);
			sa.abortUpdating();
			
			return;
		}
		
		database.updateSeasonInDatabase(sa);

		fireOnChangeDatabaseElement(sa.getSeries());
	}

	public ICoverCache getCoverCache() {
		return database.getCoverCache();
	}
	
	public void removeEpisodeFromDatabase(CCEpisode ep) {
		database.removeFromEpisodes(ep.getLocalID());
	}
	
	public void removeSeasonDatabase(CCSeason s) {
		database.removeFromSeasons(s.getSeasonID());
	}

	public int getTotalLength(boolean includeMovies, boolean includeSeries) {
		int v = 0;
		for (CCDatabaseElement m : list) {
			if (includeMovies && m.isMovie())  v += ((CCMovie) m).getLength();
			if (includeSeries && m.isSeries()) v += ((CCSeries) m).getLength();
		}
		return v;
	}

	public CCFileSize getTotalSize(boolean includeMovies, boolean includeSeries) {
		long bytes = 0;
		for (CCDatabaseElement m : list) {
			if (includeMovies && m.isMovie())  bytes += m.getFilesize().getBytes();
			if (includeSeries && m.isSeries()) bytes += m.getFilesize().getBytes();
		}
		return new CCFileSize(bytes);
	}

	public boolean contains(CCDatabaseElement m) {
		return list.contains(m);
	}

	public void remove(final CCDatabaseElement el) {
		if (!EventQueue.isDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(() -> remove(el));
			} catch (InvocationTargetException | InterruptedException e) {
				CCLog.addError(e);
			}
			return;
		}

		if (this.contains(el)) {
			if (el.isMovie()) {
				removeMovie((CCMovie) el);
			} else {
				removeSeries((CCSeries) el);
			}

			fireOnRemDatabaseElement(el);
		}
	}

	private void removeMovie(CCMovie m) {
		list.remove(m);
		database.removeFromMain(m.getLocalID());
		
		if (m.getCoverID() != -1) {
			getCoverCache().deleteCover(m.getCoverID());
		}
	}

	private void removeSeries(CCSeries s) {
		list.remove(s);
		for (int i = s.getSeasonCount() - 1; i >= 0; i--) {
			s.deleteSeason(s.getSeasonByArrayIndex(i));
		}
		database.removeFromMain(s.getLocalID());
		
		if (s.getCoverID() != -1) {
			getCoverCache().deleteCover(s.getCoverID());
		}
		
	}

	public List<CCDatabaseElement> getRawList() {
		return list;
	}

	public int getMovieCount() {
		return iteratorMovies().count();
	}
	
	public int getSeriesCount() {
		return iteratorSeries().count();
	}

	public boolean containsMovies() {
		return iteratorMovies().any();
	}
	
	public boolean containsSeries() {
		return iteratorSeries().any();
	}

	public int getEpisodeCount() {
		return iteratorEpisodes().count();
	}

	public int getSeasonCount() {
		return iteratorSeasons().count();
	}

	public List<String> getZyklusList() {
		List<String> result = new ArrayList<>();

		for (CCMovie mov : iteratorMovies()) {
			String zyklus = mov.getZyklus().getTitle();
			if (!result.contains(zyklus) && !zyklus.isEmpty()) {
				result.add(zyklus);
			}
		}
		
		Collections.sort(result);

		return result;
	}

	public List<Integer> getYearList() {
		List<Integer> result = new ArrayList<>();

		for (CCMovie mov : iteratorMovies()) {
			Integer year = mov.getYear();
			if (!result.contains(year)) {
				result.add(year);
			}
		}

		Collections.sort(result);

		return result;
	}

	public List<CCGenre> getGenreList() {
		List<CCGenre> result = new ArrayList<>();
		
		for (CCDatabaseElement el : list) {
			for (int j = 0; j < el.getGenreCount(); j++) {
				if (!result.contains(el.getGenre(j))) {
					result.add(el.getGenre(j));
				}
			}
		}

		Collections.sort(result);

		return result;
	}

	public void shutdown() {
		if (database != null) { // Close even after Intialize AV's
			database.disconnect(CCProperties.getInstance().PROP_DATABASE_CLEANSHUTDOWN.getValue());
		}
	}
	
	public String getDatabasePath() {
		return database.getDBPath();
	}

	public File getDatabaseDirectory() {
		return new File(PathFormatter.combineAndAppend(PathFormatter.getRealSelfDirectory(), CCProperties.getInstance().PROP_DATABASE_NAME.getValue()));
	}
	
	public List<File> getAbsolutePathList(boolean includeSeries) {
		List<File> result = new ArrayList<>();
		
		for (CCDatabaseElement el : list) {
			if (el.isMovie()) {
				for (int j = 0; j < ((CCMovie) el).getPartcount(); j++) {
					result.add(new File(((CCMovie) el).getAbsolutePart(j)));
				}
			} else if (includeSeries){
				result.addAll(((CCSeries)el).getAbsolutePathList());
			}
		}
		
		return result;
	}
	
	public boolean isFileInList(String path, boolean includeSeries) {
		for (CCDatabaseElement el : list) {
			if (el.isMovie()) {
				for (int j = 0; j < ((CCMovie) el).getPartcount(); j++) {
					if (((CCMovie) el).getAbsolutePart(j).equals(path)) {
						return true;
					}
				}
			} else if (includeSeries){
				if (((CCSeries) el).isFileInList(path)) {
					return true;
				}
			}
		}
		
		return false;
	}

	public void resetAllMovieViewed(boolean to) {
		for (CCMovie mov : iteratorMovies()) {
			mov.setViewed(to);
		}
	}
	
	public CCMovie findfirst(CCMovieZyklus zyklus) {
		for (CCMovie mov : iteratorMovies()) {
			if (mov.getZyklus().equals(zyklus)) {
				return mov;
			}
		}
		
		return null;
	}

	public String getCommonSeriesPath() {
		List<String> all = new ArrayList<>();

		for (CCSeries ser : iteratorSeries()) {
			all.add(ser.getCommonPathStart(false));
		}

		while (all.contains("")) all.remove(""); //$NON-NLS-1$ //$NON-NLS-2$

		String common = PathFormatter.getCommonFolderPath(all);

		return common;
	}

	public String guessSeriesRootPath() { // kinda slow :(

		return iteratorSeries()
			.map(CCSeries::guessSeriesRootPath)
			.filter(p -> !Str.isNullOrWhitespace(p))
			.groupBy(p -> p)
			.autosortByProperty(p -> p.getValue().size())
			.map(Map.Entry::getKey)
			.lastOr(Str.Empty);

	}

	public String getCommonMoviesPath() {
		List<String> all = new ArrayList<>();
		
		for (CCMovie curr : iteratorMovies()) {
			for (int i = 0; i < curr.getPartcount(); i++) {
				all.add(curr.getPart(i));
			}
		}
		
		while (all.contains("")) all.remove(""); //$NON-NLS-1$ //$NON-NLS-2$
		
		String common = PathFormatter.getCommonFolderPath(all);
		
		return common;
	}
	
	public Map<String, List<CCMovie>> listAllZyklus() {
		Map<String, List<CCMovie>> map = new TreeMap<>(); // TreeMap is ordered by default
		
		for (CCMovie curr : iteratorMovies()) {
			if (! curr.hasZyklus()) continue;
			
			String zyklus = curr.getZyklus().getTitle();
			if (! map.containsKey(curr.getZyklus().getTitle())) {
				map.put(zyklus, new ArrayList<>());
			}
			map.get(zyklus).add(curr);
		}

		return map;
	}
	
	public CCStream<CCDatabaseElement> iteratorElements() {
		return new IterableStream<>(list);
	}
	
	public CCStream<CCDatabaseElement> iteratorElementsSorted() {
		return new IterableStream<>(list).sort(new CCDatabaseElementComparator());
	}
	
	public CCStream<CCMovie> iteratorMovies() {
		return new MoviesIterator(list);
	}
	
	public CCStream<CCMovie> iteratorMoviesSorted() {
		return iteratorMovies().sort(new CCMovieComparator());
	}
	
	public CCStream<CCSeries> iteratorSeries() {
		return new SeriesIterator(list);
	}

	public CCStream<CCSeries> iteratorSeriesSorted() {
		return iteratorSeries().sort(new CCSeriesComparator());
	}
	
	public CCStream<CCEpisode> iteratorEpisodes() {
		return new EpisodesIterator(list);
	}
	
	public CCStream<ICCPlayableElement> iteratorPlayables() {
		return new PlayablesIterator(list);
	}
	
	public CCStream<CCSeason> iteratorSeasons() {
		return new SeasonsIterator(list);
	}
	
	public CCStream<ICCDatedElement> iteratorDatedElements() {
		return new DatedElementsIterator(list);
	}

	public Document getElementsAsXML() {
		return DatabaseXMLExporter.export(list, new ExportOptions(false, false, false, true));
	}
	
	@SuppressWarnings("nls")
	public Document getGroupsAsXML() {
		Document xml = new Document(new Element("database"));
		Element root = xml.getRootElement();
		root.setAttribute("version", Main.VERSION);
		root.setAttribute("dbversion", Main.DBVERSION);
		root.setAttribute("date", CCDate.getCurrentDate().toStringSerialize());
		
		Element relg = new Element("groups");
		root.addContent(relg);
		
		relg.setAttribute("count", Integer.toString(databaseGroups.size()));
		
		for (CCGroup el : databaseGroups) {
			Element g = new Element("group");
			g.setAttribute("name", el.Name);
			g.setAttribute("ordering", Integer.toString(el.Order));
			g.setAttribute("color", el.getHexColor());
			g.setAttribute("serialize", el.DoSerialize ? "true" : "false");
			g.setAttribute("parent", el.Parent);
			relg.addContent(g);
		}
		
		return xml;
	}
	
	@SuppressWarnings("nls")
	public Document getDBInfoAsXML() {
		Document xml = new Document(new Element("database"));
		Element root = xml.getRootElement();
		root.setAttribute("version", Main.VERSION);
		root.setAttribute("dbversion", Main.DBVERSION);
		root.setAttribute("date", CCDate.getCurrentDate().toStringSerialize());
		
		Element rdbi = new Element("info");
		root.addContent(rdbi);
		
		for (String key : CCDatabase.INFOKEYS) {
			Element e = new Element(key);
			e.setText(database.getInformationFromDB(key));
			rdbi.addContent(e);
		}
		
		return xml;
	}
	
	public static boolean isBlocked() {
		return getInstance().blocked;
	}
	
	public static void beginBlocking() {
		getInstance().blocked = true;
	}
	
	public static void endBlocking() {
		getInstance().blocked = false;
		
		getInstance().fireOnRefresh();
	}
	
	public void forceUpdateAll() {
		for (Iterator<CCDatabaseElement> it = iteratorElements(); it.hasNext();) {
			CCDatabaseElement el = it.next();
			el.forceUpdate();
		}
	}
	
	public void disconnectDatabase(boolean cleanShutdown) {
		database.disconnect(cleanShutdown);
	}

	public void reconnectDatabase() {
		database.reconnect();
	}
	
	public static CCMovieList getInstance() {
		return instance;
	}

	public CCEpisode getLastPlayedEpisode() {
		CCEpisode max = null;
		
		for (CCSeries ser : iteratorSeries()) {
			for (int i = 0; i < ser.getSeasonCount(); i++) {
				for (int j = 0; j < ser.getSeasonByArrayIndex(i).getEpisodeCount(); j++) {
					CCEpisode ep = ser.getSeasonByArrayIndex(i).getEpisodeByArrayIndex(j);
					
					if (max == null || (ep.isViewed() && ep.getViewedHistoryLastDateTime().isGreaterEqualsThan(max.getViewedHistoryLastDateTime()))) {
						max = ep;
					}
				}
			}
		}

		return max;
	}

	public boolean hasMovies() {
		return iteratorMovies().any();
	}

	public boolean hasSeries() {
		return iteratorSeries().any();
	}

	public boolean hasElements() {
		return list.size() > 0;
	}

	public void testDatabaseVersion() {
		String real = database.getInformation_DBVersion();
		String expected = Main.DBVERSION;
		String name = CCProperties.getInstance().PROP_DATABASE_NAME.getValue();
		String type = database.getDBTypeName();
		
		if (! real.equals(Main.DBVERSION)) {
			CCLog.addFatalError(LocaleBundle.getFormattedString("LogMessage.WrongDatabaseVersion", real, expected)); //$NON-NLS-1$
		} else {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.CorrectDatabaseVersion", name, type, expected, database.getInformation_DUUID())); //$NON-NLS-1$
		}
	}

	public void resetLocalDUUID() {
		database.resetInformation_DUUID();
	}
	
	public boolean groupExists(String name) {
		for (CCGroup entry : databaseGroups) {
			if (entry.Name.equals(name)) return true;
		}
		return false;
	}

	public List<CCGroup> getGroupList() {
		return new ArrayList<>(databaseGroups);
	}

	public int getGroupCount() {
		return databaseGroups.size();
	}
	
	public List<CCGroup> getSortedGroupList() {
		ArrayList<CCGroup> gl =  new ArrayList<>(databaseGroups);
		
		Collections.sort(gl);
		
		return gl;
	}
	
	public CCGroup getOrCreateAndAddGroup(String name) {
		for (CCGroup g : databaseGroups) {
			if (g.Name.equals(name)) return g;
		}
		
		CCGroup g = CCGroup.create(name);
		
		addGroup(g);
		
		return g;
	}
	
	public CCGroup getOrCreateAndAddGroup(CCGroup template) {
		for (CCGroup g : databaseGroups) {
			if (g.Name.equals(template.Name)) return g;
		}
		
		addGroup(template);
		return template;
	}
	
	public CCGroup getGroupOrNull(String name) {
		if (name == null) return null;

		for (CCGroup entry : databaseGroups) {
			if (entry.Name.equals(name)) return entry;
		}
		
		return null;
	}

	public int getGroupIndex(CCGroup value) {
		int idx = 0;
		for (CCGroup key : databaseGroups) {
			if (key.equals(value)) return idx;
			idx++;
		}
		return -1;
	}

	public List<CCDatabaseElement> getInternalListCopy() {
		return new ArrayList<>(list);
	}

	public List<CCDatabaseElement> getDatabaseElementsbyGroup(CCGroup group) {
		return iteratorElements().filter(elem -> elem.getGroups().contains(group)).enumerate();
	}

	public void addGroup(CCGroup g) {
		databaseGroups.add(g);
		
		database.addGroup(g.Name, g.Order, g.Color, g.DoSerialize, g.Parent, g.Visible);
	}

	public void addGroupInternal(CCGroup g) {
		databaseGroups.add(g);
	}

	public CCGroupList addMissingGroups(CCGroupList grouplist) {
		
		List<CCGroup> r = new ArrayList<>();
		
		for (CCGroup g : grouplist) {
			r.add(getOrCreateAndAddGroup(g));
		}
		
		return CCGroupList.create(r);
	}

	public void updateGroup(CCGroup gOld, CCGroup gNew) {
		
		databaseGroups.remove(gOld);
		databaseGroups.add(gNew);
		
		for (CCDatabaseElement el : new ArrayList<>(getDatabaseElementsbyGroup(gOld))) {
			el.setGroupsInternal(el.getGroups().getRemove(gOld).getAdd(gNew));
		}
		
		if (gOld.Name.equals(gNew.Name)) {
			database.updateGroup(gNew.Name, gNew.Order, gNew.Color, gNew.DoSerialize, gNew.Parent, gNew.Visible);
		} else {
			database.removeGroup(gOld.Name);
			database.addGroup(gNew.Name, gNew.Order, gNew.Color, gNew.DoSerialize, gNew.Parent, gNew.Visible);
		}
		
	}

	public void removeGroup(CCGroup gOld) {
		
		databaseGroups.remove(gOld);

		for (CCDatabaseElement el : new ArrayList<>(getDatabaseElementsbyGroup(gOld))) {
			el.setGroups(el.getGroups().getRemove(gOld));
		}
		
		database.removeGroup(gOld.Name);
		
	}
	
	public boolean isInMemory() {
		return database.isInMemory();
	}

	public CCSeries getSeries(String title) {
		for (CCSeries ser : iteratorSeries()) {
			if (ser.getTitle().equals(title)) return ser;
		}
		
		return null;
	}

	public CCMovie getMovie(String title) {
		for (CCMovie mov : iteratorMovies()) {
			if (mov.getTitle().equals(title)) return mov;
		}
		
		return null;
	}

	public List<CCGroup> getSubGroups(CCGroup group) {
		return CCStreams.iterate(databaseGroups).filter(g -> g.Parent.equals(group.Name)).enumerate();
	}

	public PublicDatabaseInterface getInternalDatabaseDirectly() {
		return database.getInternalDatabaseAccess();
	}
}
