package de.jClipCorn.database;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.jdom2.Document;
import org.jdom2.Element;

import de.jClipCorn.Globals;
import de.jClipCorn.Main;
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
import de.jClipCorn.database.util.backupManager.BackupManager;
import de.jClipCorn.database.util.covercache.CCCoverCache;
import de.jClipCorn.database.util.iterators.DatedElementsIterator;
import de.jClipCorn.database.util.iterators.EpisodesIterator;
import de.jClipCorn.database.util.iterators.MoviesIterator;
import de.jClipCorn.database.util.iterators.PlayablesIterator;
import de.jClipCorn.database.util.iterators.SeasonsIterator;
import de.jClipCorn.database.util.iterators.SeriesIterator;
import de.jClipCorn.gui.frames.initialConfigFrame.InitialConfigFrame;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.comparator.CCDatabaseElementComparator;
import de.jClipCorn.util.comparator.CCMovieComparator;
import de.jClipCorn.util.comparator.CCSeriesComparator;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.IterableStream;

public class CCMovieList {
	private static CCMovieList instance = null;
	
	private List<CCDatabaseElement> list;
	
	private Map<CCGroup, List<CCDatabaseElement>> globalGroupList = new HashMap<>();
	private List<CCDBUpdateListener> listener;
	
	private CCDatabase database;
	
	private boolean blocked = false;

	private CCMovieList(CCDatabase db) {
		this.database = null;
		this.list = new Vector<>();
		this.listener = new Vector<>();

		database = db;
		
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
				ApplicationHelper.exitApplication(0);
			}

			database = CCDatabase.create(CCProperties.getInstance().PROP_DATABASE_NAME.getValue()); // in case db type has changed
		}
	}
	
	public void connect(final MainFrame mf) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Globals.TIMINGS.start(Globals.TIMING_LOAD_TOTAL);
				{
					BackupManager bm = new BackupManager(CCMovieList.this);
					bm.doActions(mf);
	
					mf.beginBlockingIntermediate();
	
					Globals.TIMINGS.start(Globals.TIMING_DATABASE_CONNECT);
					{
						DatabaseConnectResult dbcr = database.tryconnect();
						
						if (dbcr == DatabaseConnectResult.ERROR_CANTCONNECT) {
							CCLog.addFatalError(LocaleBundle.getString("LogMessage.ErrorConnectDB"), database.getLastError()); //$NON-NLS-1$
						} else if (dbcr == DatabaseConnectResult.ERROR_CANTCREATE) {
							CCLog.addFatalError(LocaleBundle.getString("LogMessage.ErrorCreateDB"), database.getLastError()); //$NON-NLS-1$
						}
					}
					Globals.TIMINGS.stop(Globals.TIMING_DATABASE_CONNECT);
					
					testDatabaseVersion();
	
					Globals.TIMINGS.start(Globals.TIMING_MOVIELIST_FILL);
					{
						database.fillMovieList(CCMovieList.this);
					}
					Globals.TIMINGS.stop(Globals.TIMING_MOVIELIST_FILL);
	
					fireOnAfterLoad();
				}
				Globals.TIMINGS.stop(Globals.TIMING_LOAD_TOTAL);

				mf.endBlockingIntermediate();
			}
		}, "THREAD_LOAD_DATABASE").start(); //$NON-NLS-1$
	}
	
	public void connectForTests() {
		database.tryconnect();
		database.fillMovieList(CCMovieList.this);
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
		
		if (e != null && e instanceof CCMovie) return (CCMovie)e;
		
		return null;
	}

	public CCSeries findDatabaseSeries(int id) {
		CCDatabaseElement e = findDatabaseElement(id);
		
		if (e != null && e instanceof CCSeries) return (CCSeries)e;
		
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
	
	private void fireOnRefresh() {
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

	public CCCoverCache getCoverCache() {
		return database.getCoverCache();
	}
	
	public void removeEpisodeFromDatabase(CCEpisode ep) {
		database.removeFromEpisodes(ep.getLocalID());
	}
	
	public void removeSeasonDatabase(CCSeason s) {
		database.removeFromSeasons(s.getSeasonID());
	}

	public int getTotalLength(boolean includeSeries) {
		int v = 0;
		for (CCDatabaseElement m : list) {
			if (m.isMovie()) {
				v += ((CCMovie) m).getLength();
			} else if (includeSeries) {
				v += ((CCSeries) m).getLength();
			}
		}
		return v;
	}

	public CCFileSize getTotalSize(boolean includeSeries) {
		long bytes = 0;
		for (CCDatabaseElement m : list) {
			bytes += m.getFilesize().getBytes();
		}
		return new CCFileSize(bytes);
	}

	public boolean contains(CCDatabaseElement m) {
		return list.contains(m);
	}

	public void remove(final CCDatabaseElement el) {
		if (!EventQueue.isDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						remove(el);
					}
				});
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
		unlinkElementFromGroups(m, m.getGroups());
		
		if (! m.getCoverName().isEmpty()) {
			getCoverCache().deleteCover(m);
		}
	}

	private void removeSeries(CCSeries s) {
		list.remove(s);
		for (int i = s.getSeasonCount() - 1; i >= 0; i--) {
			s.deleteSeason(s.getSeasonByArrayIndex(i));
		}
		database.removeFromMain(s.getLocalID());
		unlinkElementFromGroups(s, s.getGroups());
		
		if (! s.getCoverName().isEmpty()) {
			getCoverCache().deleteCover(s);
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

	@SuppressWarnings("nls")
	public Document getEmptyXML() {
		Document xml = new Document(new Element("database"));
		
		Element root = xml.getRootElement();
		
		root.setAttribute("version", Main.VERSION);
		root.setAttribute("dbversion", Main.DBVERSION);
		root.setAttribute("date", CCDate.getCurrentDate().toStringSerialize());
		
		return xml;
	}
	
	@SuppressWarnings("nls")
	public Document getElementsAsXML() {
		Document xml = getEmptyXML();
		
		Element root = xml.getRootElement();
		
		root.setAttribute("elementcount", getElementCount() + "");
		
		for (CCDatabaseElement el : list) {
			el.generateXML(root, false, false, false);
		}
		
		return xml;
	}
	
	@SuppressWarnings("nls")
	public Document getGroupsAsXML() {
		Document xml = getEmptyXML();
		
		Element root = xml.getRootElement();
		Element relg = new Element("groups");
		root.addContent(relg);
		
		relg.setAttribute("count", Integer.toString(globalGroupList.size()));
		
		for (Entry<CCGroup, List<CCDatabaseElement>> el : globalGroupList.entrySet()) {
			Element g = new Element("group");
			g.setAttribute("name", el.getKey().Name);
			g.setAttribute("ordering", Integer.toString(el.getKey().Order));
			g.setAttribute("color", el.getKey().getHexColor());
			g.setAttribute("serialize", el.getKey().DoSerialize ? "true" : "false");
			g.setAttribute("parent", el.getKey().Parent);
			relg.addContent(g);
		}
		
		return xml;
	}
	
	@SuppressWarnings("nls")
	public Document getDBInfoAsXML() {
		Document xml = getEmptyXML();
		
		Element root = xml.getRootElement();
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
		String type = database.GetDBTypeName();
		
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
		for (Entry<CCGroup, List<CCDatabaseElement>> entry : globalGroupList.entrySet()) {
			if (entry.getKey().Name.equals(name)) return true;
		}
		return false;
	}
	
	public List<CCGroup> getGroupList() {
		return new ArrayList<>(globalGroupList.keySet());
	}
	
	public List<CCGroup> getSortedGroupList() {
		ArrayList<CCGroup> gl =  new ArrayList<>(globalGroupList.keySet());
		
		Collections.sort(gl);
		
		return gl;
	}
	
	public CCGroup getOrCreateGroup(String name) {
		for (Entry<CCGroup, List<CCDatabaseElement>> entry : globalGroupList.entrySet()) {
			if (entry.getKey().Name.equals(name)) return entry.getKey();
		}
		
		return CCGroup.create(name);
	}
	
	public CCGroup getGroupOrNull(String name) {
		for (Entry<CCGroup, List<CCDatabaseElement>> entry : globalGroupList.entrySet()) {
			if (entry.getKey().Name.equals(name)) return entry.getKey();
		}
		
		return null;
	}

	public int getGroupIndex(CCGroup value) {
		int idx = 0;
		for (CCGroup key : globalGroupList.keySet()) {
			if (key.equals(value)) return idx;
			idx++;
		}
		return -1;
	}
	
	public void unlinkElementFromGroups(CCDatabaseElement source, CCGroupList grouplist) {
		for (CCGroup g : grouplist) {
			List<CCDatabaseElement> lst = globalGroupList.get(g);
			lst.remove(source);
			if (lst.isEmpty()) {
				globalGroupList.remove(g);
				database.removeGroup(g.Name);
			}
		}
	}
	
	public void linkElementToGroups(CCDatabaseElement source, CCGroupList grouplist) {
		for (CCGroup g : grouplist) {
			List<CCDatabaseElement> lst = globalGroupList.get(g);
			if (lst == null) {
				lst = new ArrayList<>();
				globalGroupList.put(g, lst);
				database.addGroup(g.Name, g.Order, g.Color, g.DoSerialize, g.Parent, g.Visible);
			}
			lst.add(source);
		}
	}

	public void recalculateGroupCache(boolean validateExisting) {
		
		// Create new Map
		
		Map<CCGroup, List<CCDatabaseElement>> gm = new HashMap<>();
		for (Iterator<CCDatabaseElement> it = iteratorElements(); it.hasNext();) {
			CCDatabaseElement el = it.next();
			
			for (CCGroup group : el.getGroups()) {
				List<CCDatabaseElement> list = gm.get(group);
				if (list == null) {
					list = new ArrayList<>();
					gm.put(group, list);
				}
				list.add(el);
			}
		}
		
		// compare with existing
		
		if (validateExisting) {
			compareGroupMaps(globalGroupList, gm);
		}
		
		// set new one
		
		globalGroupList = gm;
		
		database.clearGroups();
		for (CCGroup g : globalGroupList.keySet()) {
			database.addGroup(g.Name, g.Order, g.Color, g.DoSerialize, g.Parent, g.Visible);
		}
	}
	
	@SuppressWarnings("nls")
	private void compareGroupMaps(Map<CCGroup, List<CCDatabaseElement>> current, Map<CCGroup, List<CCDatabaseElement>> created) {
		for (CCGroup g : current.keySet()) {
			if (!created.containsKey(g)) {
				CCLog.addError(LocaleBundle.getString("LogMessage.GroupCacheInvalid") + "\r\nCache has one key too many: " + g.Name);
			} else {
				List<CCDatabaseElement> lsCreatedMising = new ArrayList<>(current.get(g));
				lsCreatedMising.removeAll(created.get(g));
				
				for (CCDatabaseElement miss : lsCreatedMising) {
					CCLog.addError(LocaleBundle.getString("LogMessage.GroupCacheInvalid") + "\r\nSuperfluous mapping in current: " + miss.getFullDisplayTitle());
				}
				
				List<CCDatabaseElement> lsCurrentMising = new ArrayList<>(created.get(g));
				lsCurrentMising.removeAll(current.get(g));
				
				for (CCDatabaseElement miss : lsCurrentMising) {
					CCLog.addError(LocaleBundle.getString("LogMessage.GroupCacheInvalid") + "\r\nMissing mapping in current: " + miss.getFullDisplayTitle());
				}
			}
		}
		
		for (CCGroup g : created.keySet()) {
			if (!created.containsKey(g)) {
				CCLog.addError(LocaleBundle.getString("LogMessage.GroupCacheInvalid") + "\r\nCache is missing one key: " + g.Name);
			} else {
				List<CCDatabaseElement> lsCreatedMising = new ArrayList<>(current.get(g));
				lsCreatedMising.removeAll(created.get(g));
				
				for (CCDatabaseElement miss : lsCreatedMising) {
					CCLog.addError(LocaleBundle.getString("LogMessage.GroupCacheInvalid") + "\r\nSuperfluous mapping in current: " + miss.getFullDisplayTitle());
				}
				
				List<CCDatabaseElement> lsCurrentMising = new ArrayList<>(created.get(g));
				lsCurrentMising.removeAll(current.get(g));
				
				for (CCDatabaseElement miss : lsCurrentMising) {
					CCLog.addError(LocaleBundle.getString("LogMessage.GroupCacheInvalid") + "\r\nMissing mapping in current: " + miss.getFullDisplayTitle());
				}
			}
		}
	}
	
	public List<CCDatabaseElement> getInternalListCopy() {
		return new ArrayList<>(list);
	}

	public List<CCDatabaseElement> getDatabaseElementsbyGroup(CCGroup group) {
		List<CCDatabaseElement> result = globalGroupList.get(group);
		if (result == null) result = new ArrayList<>();
		
		return result;
	}

	public void directlyAddGroup(CCGroup g) {
		globalGroupList.put(g, new ArrayList<>());
	}

	public void updateGroup(CCGroup gOld, CCGroup gNew) {
		for (CCDatabaseElement el : new ArrayList<>(getDatabaseElementsbyGroup(gOld))) {
			el.setGroupsInternal(el.getGroups().getRemove(gOld).getAdd(gNew));
		}
		
		List<CCDatabaseElement> garb = globalGroupList.remove(gOld);
		if (garb != null) globalGroupList.put(gNew, garb);
		
		database.updateGroup(gNew.Name, gNew.Order, gNew.Color, gNew.DoSerialize, gNew.Parent, gNew.Visible);
	}
	
	public boolean isInMemory() {
		return database.IsInMemory();
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
}
