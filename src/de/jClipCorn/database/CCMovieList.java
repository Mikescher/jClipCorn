package de.jClipCorn.database;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.jdom2.Document;
import org.jdom2.Element;

import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.database.util.BackupManager;
import de.jClipCorn.database.util.CCCoverCache;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.database.util.MovieIterator;
import de.jClipCorn.database.util.SeriesIterator;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.PathFormatter;

public class CCMovieList {
	private static CCMovieList instance = null;
	
	private List<CCDatabaseElement> list;

	private CCCoverCache coverCache;
	private CCDatabase database;

	private List<CCDBUpdateListener> listener;

	private long loadTime = -1;
	
	private boolean blocked = false;

	public CCMovieList() {
		this.database = null;
		this.list = new Vector<>();
		this.listener = new Vector<>();
		
		instance = this;
	}

	public void connect(final MainFrame mf) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				BackupManager bm = new BackupManager(CCMovieList.this);
				bm.start();
				
				long starttime = System.currentTimeMillis();

				mf.beginBlockingIntermediate();

				database = new CCDatabase();

				if (!database.tryconnect(CCProperties.getInstance().PROP_DATABASE_NAME.getValue())) {
					CCLog.addFatalError(LocaleBundle.getString("LogMessage.ErrorCreateDB"), database.getLastError()); //$NON-NLS-1$
				}

				database.fillMovieList(CCMovieList.this);

				coverCache = new CCCoverCache(CCMovieList.this);

				setLoadTime(System.currentTimeMillis() - starttime);

				fireOnAfterLoad();

				mf.endBlockingIntermediate();
			}
		}, "THREAD_LOAD_DATABASE").start(); //$NON-NLS-1$
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

		fireOnAddDatabaseElement(ep.getSeries());
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
		return coverCache;
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

	public CCMovieSize getTotalSize(boolean includeSeries) {
		CCMovieSize v = new CCMovieSize(0);
		for (CCDatabaseElement m : list) {
			if (m.isMovie()) {
				v.add(((CCMovie) m).getFilesize());
			} else if (includeSeries) {
				v.add(((CCSeries) m).getFilesize());
			}
		}
		return v;
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
		
		if (! m.getCoverName().isEmpty()) {
			coverCache.deleteCover(m);
		}
	}

	private void removeSeries(CCSeries s) {
		list.remove(s);
		for (int i = s.getSeasonCount() - 1; i >= 0; i--) {
			s.deleteSeason(s.getSeason(i));
		}
		database.removeFromMain(s.getLocalID());
		
		if (! s.getCoverName().isEmpty()) {
			coverCache.deleteCover(s);
		}
		
	}

	public List<CCDatabaseElement> getRawList() {
		return list;
	}

	public long getLoadTime() {
		return loadTime;
	}

	public void setLoadTime(long loadTime) {
		this.loadTime = loadTime;
	}

	public int getEpisodeCount() {
		int c = 0;
		
		for (Iterator<CCSeries> it = iteratorSeries(); it.hasNext();) {
			CCSeries ser = it.next();
			
			c += ser.getEpisodeCount();
		}
		
		return c;
	}
	
	public int getMovieCount() {
		int c = 0;
		
		for (Iterator<CCMovie> it = iteratorMovies(); it.hasNext();it.next()) {
			c++;
		}
		
		return c;
	}
	
	public int getSeriesCount() {
		int c = 0;
		
		for (Iterator<CCSeries> it = iteratorSeries(); it.hasNext();it.next()) {
			c++;
		}
		
		return c;
	}

	public List<String> getZyklusList() {
		List<String> result = new ArrayList<>();

		for (Iterator<CCMovie> it = iteratorMovies(); it.hasNext();) {
			CCMovie mov = it.next();
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

		for (Iterator<CCMovie> it = iteratorMovies(); it.hasNext();) {
			CCMovie mov = it.next();
			Integer year = mov.getYear();
			if (!result.contains(year)) {
				result.add(year);
			}
		}

		Collections.sort(result);

		return result;
	}

	public List<CCMovieGenre> getGenreList() {
		List<CCMovieGenre> result = new ArrayList<>();
		
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
			database.shutdown();
		}
	}
	
	public String getDatabasePath() {
		return database.getDBPath();
	}
	
	public File getDatabaseDirectory() {
		return new File(PathFormatter.getRealSelfDirectory() + CCProperties.getInstance().PROP_DATABASE_NAME.getValue() + '\\');
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
		for (Iterator<CCMovie> it = iteratorMovies(); it.hasNext();) {
			it.next().setViewed(to);
		}
	}
	
	public CCMovie findfirst(CCMovieZyklus zyklus) {
		for (Iterator<CCMovie> it = iteratorMovies(); it.hasNext();) {
			CCMovie mov = it.next();
			if (mov.getZyklus().equals(zyklus)) {
				return mov;
			}
		}
		
		return null;
	}
	
	public Iterator<CCDatabaseElement> iterator() {
		return list.iterator();
	}
	
	public Iterator<CCMovie> iteratorMovies() {
		return new MovieIterator(list);
	}
	
	public Iterator<CCSeries> iteratorSeries() {
		return new SeriesIterator(list);
	}
	
	@SuppressWarnings("nls")
	public Document getEmptyXML() {
		Document xml = new Document(new Element("database"));
		
		Element root = xml.getRootElement();
		
		root.setAttribute("version", Main.VERSION);
		root.setAttribute("dbversion", Main.DBVERSION);
		root.setAttribute("date", new CCDate().getSimpleStringRepresentation());
		
		return xml;
	}
	
	@SuppressWarnings("nls")
	public Document getAsXML() {
		Document xml = getEmptyXML();
		
		Element root = xml.getRootElement();
		
		root.setAttribute("elementcount", getElementCount() + "");
		
		for (CCDatabaseElement el : list) {
			el.generateXML(root, false, false, false);
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
		for (Iterator<CCDatabaseElement> it = iterator(); it.hasNext();) {
			CCDatabaseElement el = it.next();
			el.forceUpdate();
		}
	}
	
	public static CCMovieList getInstance() {
		return instance;
	}
}
