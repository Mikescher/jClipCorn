package de.jClipCorn.database;

import de.jClipCorn.Globals;
import de.jClipCorn.Main;
import de.jClipCorn.database.covertab.ICoverCache;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.database.databaseElement.caches.MovieListCache;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.driver.DatabaseConnectResult;
import de.jClipCorn.database.driver.DatabaseStructure;
import de.jClipCorn.database.driver.PublicDatabaseInterface;
import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.database.util.iterators.*;
import de.jClipCorn.features.backupManager.BackupManager;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.xmlexport.DatabaseXMLExporter;
import de.jClipCorn.features.serialization.xmlexport.ExportOptions;
import de.jClipCorn.gui.frames.initialConfigFrame.InitialConfigFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.util.comparator.CCDatabaseElementComparator;
import de.jClipCorn.util.comparator.CCMovieComparator;
import de.jClipCorn.util.comparator.CCSeriesComparator;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple1;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.lambda.Func0to0;
import de.jClipCorn.util.sqlwrapper.CCSQLKVKey;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;
import de.jClipCorn.util.stream.IterableStream;
import org.jdom2.Document;
import org.jdom2.Element;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.*;

public class CCMovieList {
	private final List<CCDatabaseElement> list;
	
	private final List<CCDBUpdateListener> listener;
	
	private CCDatabase database;
	private List<CCGroup> databaseGroups;

	private final MovieListCache _cache;

	private boolean blocked   = false;
	private boolean isLoading = false;
	private boolean isLoaded  = false;

	private CCMovieList(CCDatabase db) {
		this.list     = new Vector<>();
		this.listener = new Vector<>();

		this.databaseGroups = new ArrayList<>();
		this.database = db;

		_cache = new MovieListCache(this);
	}
	
	public static CCMovieList createInstanceMovieList() {
		var db = CCDatabase.create(
				CCProperties.getInstance().PROP_DATABASE_DRIVER.getValue(),
				FilesystemUtils.getRealSelfDirectory(),
				CCProperties.getInstance().PROP_DATABASE_NAME.getValue(),
				CCProperties.getInstance().ARG_READONLY);

		return new CCMovieList(db);
	}
	
	public static CCMovieList createInMemory() {
		return new CCMovieList(CCDatabase.createInMemory());
	}
	
	public static CCMovieList createStub() {
		return new CCMovieList(CCDatabase.createStub());
	}

	public static CCMovieList loadExtern(CCDatabaseDriver drv, FSPath directory, String dbName, boolean readonly) {
		return new CCMovieList(CCDatabase.create(drv, directory, dbName, readonly));
	}

	public void showInitialWizard() {
		if (!database.exists()) {
			boolean cont = InitialConfigFrame.ShowWizard();
			
			if (! cont) {
				ApplicationHelper.exitApplication(0, true);
			}

			// in case db type has changed
			database = CCDatabase.create(
					CCProperties.getInstance().PROP_DATABASE_DRIVER.getValue(),
					FilesystemUtils.getRealSelfDirectory(),
					CCProperties.getInstance().PROP_DATABASE_NAME.getValue(),
					CCProperties.getInstance().ARG_READONLY);
		}
	}
	
	public void connect(final MainFrame mf, Func0to0 postInit) {
		new Thread(() ->
		{
			Globals.TIMINGS.start(Globals.TIMING_LOAD_TOTAL);
			{
				isLoading = true;
				isLoaded  = false;

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
						database.fillCoverCache(CCProperties.getInstance().PROP_DATABASE_LOAD_ALL_COVERDATA.getValue());
					}
					Globals.TIMINGS.stop(Globals.TIMING_LOAD_MOVIELIST_FILL_COVERS);
				}
				Globals.TIMINGS.stop(Globals.TIMING_LOAD_DATABASE);

				fireOnAfterLoad();

				if (postInit != null) SwingUtils.invokeLater(postInit::invoke);

				isLoading = false;
				isLoaded  = true;
			}
			Globals.TIMINGS.stop(Globals.TIMING_LOAD_TOTAL);

			Globals.TIMINGS.stop(Globals.TIMING_STARTUP_TOTAL);

			mf.endBlockingIntermediate();
		}, "THREAD_LOAD_DATABASE").start(); //$NON-NLS-1$
	}
	
	public void connectForTests()
	{
		isLoading = true;
		isLoaded  = false;
		{
			var r = database.tryconnect();
			if (r != DatabaseConnectResult.SUCESS_CONNECTED && r != DatabaseConnectResult.SUCCESS_CREATED) throw new Error(String.valueOf(r));

			database.fillGroups(CCMovieList.this);
			database.fillMovieList(CCMovieList.this);
			database.fillCoverCache(true);
		}
		isLoading = false;
		isLoaded  = true;
	}

	public void connectExternal(boolean allowCreate) throws Exception
	{
		isLoading = true;
		isLoaded  = false;
		{
			var r = database.tryconnect();
			if (r != DatabaseConnectResult.SUCESS_CONNECTED && r != DatabaseConnectResult.SUCCESS_CREATED) throw new Exception(String.valueOf(r));
			if (!allowCreate && r == DatabaseConnectResult.SUCCESS_CREATED) throw new Exception(String.valueOf(r));

			database.fillGroups(CCMovieList.this);
			database.fillMovieList(CCMovieList.this);
			database.fillCoverCache(true);
		}
		isLoading = false;
		isLoaded  = true;
	}

	public void forceReconnectAndReloadForTests() {
		// do no call clear, we only want to remove RAM values

		databaseGroups.clear();
		list.clear();
		database.resetForTestReload();

		// refill
		database.fillGroups(CCMovieList.this);
		database.fillMovieList(CCMovieList.this);
		database.fillCoverCache(true);
	}

	public boolean isInitializingOrIsLoading() {
		return isLoading || !isLoaded;
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
		return _cache.getInt(MovieListCache.TOTAL_DATABASE_COUNT, null, ml->
		{
			int c = 0;
			for (CCDatabaseElement dbe : list) {
				if (dbe.isMovie()) {
					c++;
				} else {
					c += ((CCSeries) dbe).getEpisodeCount();
				}
			}
			return c;
		});
	}

	public int getTotalDatabaseElementCount() {
		return _cache.getInt(MovieListCache.TOTAL_DATABASE_ELEMENT_COUNT, null, ml->
		{
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
		});
	}

	public int getViewedCount() {
		return _cache.get(MovieListCache.VIEWED_COUNT, Tuple1.Create(CCProperties.getInstance().PROP_INCLUDE_SERIES_IN_VIEWEDCOUNT.getValue()), ml->
		{
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
		});
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

	public CCMovie createNewEmptyMovie() {
		CCMovie mov = database.createNewEmptyMovie(this);
		list.add(mov);
		_cache.bust();

		fireOnAddDatabaseElement(mov);

		return mov;
	}

	public CCSeries createNewEmptySeries() {
		CCSeries s = database.createNewEmptySeries(this);
		list.add(s);
		_cache.bust();

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
			SwingUtils.invokeAndWaitConditionalThrow(() -> {
				list.add(m);
				_cache.bust();
				fireOnAddDatabaseElement(m);
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
		}
	}

	// Only used by CCDatabase.fillMovieList
	public void directlyInsert(final List<CCDatabaseElement> m) {
		try {
			SwingUtils.invokeAndWaitConditionalThrow(() ->
			{
				list.addAll(m);
				_cache.bust();
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
		}
	}

	public void clear() { // Serious method is doing serious business
		while (!list.isEmpty()) {
			remove(list.get(0));
		}
		_cache.bust();
	}

	public void addChangeListener(CCDBUpdateListener l) {
		listener.add(l);
	}

	public void fireOnAddDatabaseElement(final CCDatabaseElement el) {
		if (!EventQueue.isDispatchThread()) {
			SwingUtils.invokeLater(() -> fireOnAddDatabaseElement(el));
			return;
		}

		for (CCDBUpdateListener l : listener) {
			l.onAddDatabaseElement(el);
		}
	}

	public void fireOnChangeDatabaseElement(final CCDatabaseElement el) {
		if (!EventQueue.isDispatchThread()) {
			SwingUtils.invokeLater(() -> fireOnChangeDatabaseElement(el));
			return;
		}

		for (CCDBUpdateListener l : listener) {
			l.onChangeDatabaseElement(el);
		}
	}

	public void fireOnRemDatabaseElement(final CCDatabaseElement el) {
		if (!EventQueue.isDispatchThread()) {
			SwingUtils.invokeLater(() -> fireOnRemDatabaseElement(el));
			return;
		}

		for (CCDBUpdateListener l : listener) {
			l.onRemMovie(el);
		}
	}

	private void fireOnAfterLoad() {
		if (!EventQueue.isDispatchThread()) {
			SwingUtils.invokeLater(this::fireOnAfterLoad);
			return;
		}
		
		database.upgrader.onAfterConnect(this, database);

		for (CCDBUpdateListener l : listener) {
			l.onAfterLoad();
		}
	}
	
	public void fireOnRefresh() {
		if (!EventQueue.isDispatchThread()) {
			SwingUtils.invokeLater(this::fireOnRefresh);
			return;
		}

		for (CCDBUpdateListener l : listener) {
			l.onRefresh();
		}
	}

	public boolean update(CCMovie el) {
		if (database.isReadonly()) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			
			el.beginUpdating();
			database.updateMovieFromDatabase(el);
			el.abortUpdating();
			
			return true;
		}

		var ok = database.updateMovieInDatabase(el);

		fireOnChangeDatabaseElement(el);

		return ok;
	}

	public boolean update(CCEpisode ep) {
		if (database.isReadonly()) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
						
			ep.beginUpdating();
			database.updateEpisodeFromDatabase(ep);
			ep.abortUpdating();
			
			return true;
		}

		var ok = database.updateEpisodeInDatabase(ep);

		fireOnChangeDatabaseElement(ep.getSeries());

		return ok;
	}

	public boolean update(CCSeries se) {
		if (database.isReadonly()) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			
			se.beginUpdating();
			database.updateSeriesFromDatabase(se);
			se.abortUpdating();
			
			return true;
		}
		
		var ok = database.updateSeriesInDatabase(se);

		fireOnChangeDatabaseElement(se);

		return ok;
	}

	public boolean update(CCSeason sa) {
		if (database.isReadonly()) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			
			sa.beginUpdating();
			database.updateSeasonFromDatabase(sa);
			sa.abortUpdating();
			
			return true;
		}

		var ok = database.updateSeasonInDatabase(sa);

		fireOnChangeDatabaseElement(sa.getSeries());

		return ok;
	}

	public ICoverCache getCoverCache() {
		return database.getCoverCache();
	}
	
	public void removeEpisodeFromDatabase(CCEpisode ep) {
		database.removeFromEpisodes(ep.getLocalID());
	}
	
	public void removeSeasonDatabase(CCSeason s) {
		database.removeFromSeasons(s.getLocalID());
	}

	public int getTotalLength(boolean includeMovies, boolean includeSeries) {
		return _cache.getInt(MovieListCache.TOTAL_LENGTH, Tuple.Create(includeMovies, includeSeries), ml->
		{
			int v = 0;
			for (CCDatabaseElement m : list) {
				if (includeMovies && m.isMovie())  v += ((CCMovie) m).Length.get();
				if (includeSeries && m.isSeries()) v += ((CCSeries) m).getLength();
			}
			return v;
		});
	}

	public CCFileSize getTotalSize(boolean includeMovies, boolean includeSeries) {
		return _cache.get(MovieListCache.TOTAL_SIZE, Tuple.Create(includeMovies, includeSeries), ml->
		{
			long bytes = 0;
			for (CCDatabaseElement m : list) {
				if (includeMovies && m.isMovie())  bytes += ((CCMovie) m).FileSize.get().getBytes();
				if (includeSeries && m.isSeries()) bytes += ((CCSeries) m).getFilesize().getBytes();
			}
			return new CCFileSize(bytes);
		});
	}

	public boolean contains(CCDatabaseElement m) {
		return list.contains(m);
	}

	public void remove(final CCDatabaseElement el) {
		if (!EventQueue.isDispatchThread()) {
			try {
				SwingUtils.invokeAndWaitConditionalThrow(() -> remove(el));
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

			_cache.bust();

			fireOnRemDatabaseElement(el);
		}
	}

	private void removeMovie(CCMovie m) {
		list.remove(m);
		database.removeFromMovies(m.getLocalID());

		if (m.getCoverID() != -1) {
			getCoverCache().deleteCover(m.getCoverID());
		}

		_cache.bust();
	}

	private void removeSeries(CCSeries s) {
		list.remove(s);
		for (int i = s.getSeasonCount() - 1; i >= 0; i--) {
			s.deleteSeason(s.getSeasonByArrayIndex(i));
		}
		database.removeFromSeries(s.getLocalID());
		
		if (s.getCoverID() != -1) {
			getCoverCache().deleteCover(s.getCoverID());
		}

		_cache.bust();
	}

	public List<CCDatabaseElement> getRawList() {
		return list;
	}

	public int getMovieCount() {
		return _cache.getInt(MovieListCache.MOVIE_COUNT, null, ml -> iteratorMovies().count());
	}
	
	public int getSeriesCount() {
		return _cache.getInt(MovieListCache.SERIES_COUNT, null, ml -> iteratorSeries().count());
	}

	public int getEpisodeCount() {
		return _cache.getInt(MovieListCache.EPISODE_COUNT, null, ml -> iteratorEpisodes().count());
	}

	public int getSeasonCount() {
		return _cache.getInt(MovieListCache.SEASON_COUNT, null, ml -> iteratorSeasons().count());
	}

	public List<String> getZyklusList() {
		return _cache.get(MovieListCache.ZYKLUS_LIST, null, ml->
		{
			List<String> result = new ArrayList<>();

			for (CCMovie mov : iteratorMovies()) {
				String zyklus = mov.Zyklus.get().getTitle();
				if (!result.contains(zyklus) && !zyklus.isEmpty()) {
					result.add(zyklus);
				}
			}

			Collections.sort(result);

			return result;
		});
	}

	public List<Integer> getYearList() {
		return _cache.get(MovieListCache.YEAR_LIST, null, ml->
		{
			List<Integer> result = new ArrayList<>();

			for (CCMovie mov : iteratorMovies()) {
				Integer year = mov.Year.get();
				if (!result.contains(year)) {
					result.add(year);
				}
			}

			Collections.sort(result);

			return result;
		});
	}

	public List<CCGenre> getGenreList() {
		return _cache.get(MovieListCache.GENRE_LIST, null, ml->
		{
			List<CCGenre> result = new ArrayList<>();

			for (CCDatabaseElement el : list) {
				for (int j = 0; j < el.Genres.get().getGenreCount(); j++) {
					if (!result.contains(el.Genres.get().getGenre(j))) {
						result.add(el.Genres.get().getGenre(j));
					}
				}
			}

			Collections.sort(result);

			return result;
		});
	}

	public void shutdown() {
		if (database != null) { // Close even after Intialize AV's
			database.disconnect(CCProperties.getInstance().PROP_DATABASE_CLEANSHUTDOWN.getValue());
		}
	}

	public FSPath getDatabaseDirectory() {
		return database.getDBPath();
	}
	
	public List<FSPath> getAbsolutePathList(boolean includeSeries) {
		List<FSPath> result = new ArrayList<>();
		
		for (CCDatabaseElement el : list) {
			if (el.isMovie()) {
				for (var p: ((CCMovie) el).getParts()) result.add(p.toFSPath());
			} else if (includeSeries){
				for (var e: ((CCSeries) el).getEpisodeList()) if (!e.Part.get().isEmpty()) result.add(e.Part.get().toFSPath());
			}
		}
		
		return result;
	}

	public void resetAllMovieViewed() {
		for (CCMovie mov : iteratorMovies()) mov.ViewedHistory.set(CCDateTimeList.createEmpty());
	}
	
	public CCMovie findfirst(CCMovieZyklus zyklus) {
		for (CCMovie mov : iteratorMovies()) {
			if (mov.Zyklus.get().equals(zyklus)) {
				return mov;
			}
		}
		
		return null;
	}

	public FSPath getCommonPathForMovieFileChooser() {
		var p = getCommonMoviesPath().toFSPath();
		if (p.isEmpty()) p = getCommonSeriesPath().toFSPath();
		if (p.isEmpty()) p = FilesystemUtils.getRealSelfDirectory();
		if (p.isEmpty()) p = FilesystemUtils.getAbsoluteSelfDirectory();
		return p;
	}

	public FSPath getCommonPathForSeriesFileChooser() {
		var p = getCommonSeriesPath().toFSPath();
		if (p.isEmpty()) p = getCommonMoviesPath().toFSPath();
		if (p.isEmpty()) p = FilesystemUtils.getRealSelfDirectory();
		if (p.isEmpty()) p = FilesystemUtils.getAbsoluteSelfDirectory();
		return p;
	}

	public CCPath getCommonSeriesPath() {
		return _cache.get(MovieListCache.COMMON_SERIES_PATH, null, ml->
		{
			List<CCPath> all = new ArrayList<>();

			for (CCSeries ser : iteratorSeries()) {
				var p = ser.getCommonPathStart(false);
				if (!p.isEmpty()) all.add(p);
			}

			return CCPath.getCommonPath(all);
		});
	}

	public CCPath getCommonMoviesPath() {
		return _cache.get(MovieListCache.COMMON_MOVIES_PATH, null, ml->
		{
			List<CCPath> all = new ArrayList<>();

			for (CCMovie curr : iteratorMovies()) {
				for (int i = 0; i < curr.getPartcount(); i++) {
					var p = curr.Parts.get(i);
					if (!p.isEmpty()) all.add(p);
				}
			}

			return CCPath.getCommonPath(all);
		});
	}

	public FSPath guessSeriesRootPath() {
		return _cache.get(MovieListCache.GUESS_SERIES_ROOT_PATH, null, ml->
		{
			return iteratorSeries()
					.map(CCSeries::guessSeriesRootPath)
					.filter(p -> !p.isEmpty())
					.groupBy(p -> p)
					.autosortByProperty(p -> p.getValue().size())
					.map(Map.Entry::getKey)
					.lastOr(FSPath.Empty);
		});
	}
	
	public Map<String, List<CCMovie>> listAllZyklus() {
		Map<String, List<CCMovie>> map = new TreeMap<>(); // TreeMap is ordered by default
		
		for (CCMovie curr : iteratorMovies()) {
			if (! curr.hasZyklus()) continue;
			
			String zyklus = curr.Zyklus.get().getTitle();
			if (! map.containsKey(curr.Zyklus.get().getTitle())) {
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
		
		for (CCSQLKVKey key : DatabaseStructure.INFOKEYS) {
			String v = database.readInformationFromDB(key, null);
			if (v != null) {
				Element e = new Element(key.Key);
				e.setText(v);
				rdbi.addContent(e);
			}
		}
		
		return xml;
	}
	
	public boolean isBlocked() {
		return blocked;
	}
	
	public void beginBlocking() {
		blocked = true;
	}
	
	public void endBlocking() {
		blocked = false;
		fireOnRefresh();
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
		return _cache.getBool(MovieListCache.HAS_MOVIES, null, ml->
		{
			return iteratorMovies().any();
		});
	}

	public boolean hasSeries() {
		return _cache.getBool(MovieListCache.HAS_SERIES, null, ml->
		{
			return iteratorSeries().any();
		});
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

	public String getDatabaseVersion() {
		return database.getInformation_DBVersion();
	}

	public void resetLocalDUUID() {
		database.resetInformation_DUUID();
		_cache.bust();
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

		_cache.bust();
		addGroup(g);
		
		return g;
	}
	
	public CCGroup getOrCreateAndAddGroup(CCGroup template) {
		for (CCGroup g : databaseGroups) {
			if (g.Name.equals(template.Name)) return g;
		}

		_cache.bust();
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
		_cache.bust();
		
		database.addGroup(g.Name, g.Order, g.Color, g.DoSerialize, g.Parent, g.Visible);
	}

	public void addGroupInternal(CCGroup g) {
		databaseGroups.add(g);
		_cache.bust();
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
		
		if (gOld.Name.equals(gNew.Name)) {

			for (CCDatabaseElement el : new ArrayList<>(getDatabaseElementsbyGroup(gOld))) {
				// Only setGroupsInternal, no actual DB Update, we only want to update the CCGroup objects
				el.Groups.setFromMovieListWithoutDBUpdateOrCallback(el.getGroups().getRemove(gOld).getAdd(gNew));
			}

			database.updateGroup(gNew.Name, gNew.Order, gNew.Color, gNew.DoSerialize, gNew.Parent, gNew.Visible);
		} else {

			for (CCDatabaseElement el : new ArrayList<>(getDatabaseElementsbyGroup(gOld))) {
				el.Groups.setFromMovieListWithoutCallback(el.getGroups().getRemove(gOld).getAdd(gNew));
			}

			database.removeGroup(gOld.Name);
			database.addGroup(gNew.Name, gNew.Order, gNew.Color, gNew.DoSerialize, gNew.Parent, gNew.Visible);
		}

		_cache.bust();
	}

	public void removeGroup(CCGroup gOld) {
		
		databaseGroups.remove(gOld);

		for (CCDatabaseElement el : new ArrayList<>(getDatabaseElementsbyGroup(gOld))) {
			el.Groups.set(el.getGroups().getRemove(gOld));
		}
		
		database.removeGroup(gOld.Name);

		_cache.bust();
	}
	
	public boolean isInMemory() {
		return database.isInMemory();
	}

	public CCSeries getSeries(String title) {
		for (CCSeries ser : iteratorSeries()) {
			if (ser.Title.get().equals(title)) return ser;
		}
		
		return null;
	}

	public CCMovie getMovie(String title) {
		for (CCMovie mov : iteratorMovies()) {
			if (mov.Title.get().equals(title)) return mov;
		}
		
		return null;
	}

	public List<CCGroup> getSubGroups(CCGroup group) {
		return CCStreams.iterate(databaseGroups).filter(g -> g.Parent.equals(group.Name)).enumerate();
	}

	public PublicDatabaseInterface getInternalDatabaseDirectly() {
		return database.getInternalDatabaseAccess();
	}

	public CCDatabase getDatabaseForUnitTests() {
		return database;
	}

	public CCDatabaseHistory getHistory() {
		return database.getHistory();
	}

	public ICalculationCache getCache() {
		return _cache;
	}

	public boolean isReadonly() {
		return database.isReadonly();
	}

	public boolean databaseExists() {
		return database.exists();
	}

	public void sortByIDAfterInitialLoad() {
		list.sort(Comparator.comparingInt(e -> -1 * e.LocalID.get()));
	}
}
