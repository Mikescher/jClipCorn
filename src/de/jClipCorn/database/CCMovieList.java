package de.jClipCorn.database;

import java.awt.EventQueue;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.SwingUtilities;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.PathFormatter;

public class CCMovieList {
	private Vector<CCDatabaseElement> list;

	private CCCoverCache coverCache;
	private CCDatabase database;

	private Vector<CCDBUpdateListener> listener;;

	private long loadTime = -1;

	public CCMovieList() {
		this.database = null;
		this.list = new Vector<>();
		this.listener = new Vector<>();
	}

	public void connect(final MainFrame mf) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				BackupManager bm = new BackupManager(CCMovieList.this);
				bm.start();
				
				long starttime = System.currentTimeMillis();

				mf.startBlockingIntermediate();

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
		};

		(new Thread(r)).start();
	}

	public CCDatabaseElement getDatabaseElementBySort(int row) { // WARNIG SORT <> MOVIEID || SORT IN DATABASE (SORTED BY MOVIEID)
		return list.get(row);
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
		CCMovie m = database.createNewEmptyMovie(this);
		list.add(m);

		fireOnAddDatabaseElement(m);

		return m;
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
			e.printStackTrace();
			return;
		}
	}

	// Only used by CCDatabase.fillMovieList
	public void directlyInsert(final ArrayList<CCDatabaseElement> m) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					list.addAll(m);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}

	public void clear() {
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

	public void update(CCMovie el) {
		database.updateMovie(el);

		fireOnChangeDatabaseElement(el);
	}

	public void update(CCEpisode ep) {
		database.updateEpisode(ep);

		fireOnAddDatabaseElement(ep.getSeries());
	}

	public void update(CCSeries se) {
		database.updateSeries(se);

		fireOnChangeDatabaseElement(se);
	}

	public void update(CCSeason sa) {
		database.updateSeason(sa);

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
				e.printStackTrace();
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
		coverCache.deleteCover(m);
	}

	private void removeSeries(CCSeries s) {
		list.remove(s);
		for (int i = s.getSeasonCount() - 1; i >= 0; i--) {
			s.deleteSeason(s.getSeason(i));
		}
		database.removeFromMain(s.getLocalID());
		coverCache.deleteCover(s);
	}

	public Vector<CCDatabaseElement> getRawList() {
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
		for (CCDatabaseElement dbe : list) {
			if (dbe.isSeries()) {
				c += ((CCSeries) dbe).getEpisodeCount();
			}
		}
		return c;
	}

	public ArrayList<String> getZyklusList() {
		ArrayList<String> result = new ArrayList<>();

		for (int i = 0; i < getElementCount(); i++) {
			CCDatabaseElement el = getDatabaseElementBySort(i);
			if (el.isMovie()) {
				String zyklus = ((CCMovie) el).getZyklus().getTitle();
				if (!result.contains(zyklus) && !zyklus.isEmpty()) {
					result.add(zyklus);
				}
			}
		}

		Collections.sort(result);

		return result;
	}

	public ArrayList<Integer> getYearList() {
		ArrayList<Integer> result = new ArrayList<>();

		for (int i = 0; i < getElementCount(); i++) {
			CCDatabaseElement el = getDatabaseElementBySort(i);
			if (el.isMovie()) {
				Integer year = ((CCMovie) el).getYear();
				if (!result.contains(year)) {
					result.add(year);
				}
			}
		}

		Collections.sort(result);

		return result;
	}

	public ArrayList<CCMovieGenre> getGenreList() {
		ArrayList<CCMovieGenre> result = new ArrayList<>();

		for (int i = 0; i < getElementCount(); i++) {
			CCDatabaseElement el = getDatabaseElementBySort(i);
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
		database.shutdown();
	}
	
	public String getDatabasePath() {
		return database.getDBPath();
	}
	
	public File getDatabaseDirectory() {
		return new File(PathFormatter.getRealSelfDirectory() + CCProperties.getInstance().PROP_DATABASE_NAME.getValue() + '\\');
	}
	
	public ArrayList<File> getAbsolutePathList(boolean includeSeries) {
		ArrayList<File> result = new ArrayList<>();
		
		for (int i = 0; i < list.size(); i++) {
			CCDatabaseElement dbe = getDatabaseElementBySort(i);
			if (dbe.isMovie()) {
				for (int j = 0; j < ((CCMovie)dbe).getPartcount(); j++) {
					result.add(new File(((CCMovie)dbe).getAbsolutePart(j)));
				}
			} else if (includeSeries){
				result.addAll(((CCSeries)dbe).getAbsolutePathList());
			}
		}
		
		return result;
	}
	
	public boolean isFileInList(String path, boolean includeSeries) {
		for (int i = 0; i < list.size(); i++) {
			CCDatabaseElement dbe = getDatabaseElementBySort(i);
			if (dbe.isMovie()) {
				for (int j = 0; j < ((CCMovie)dbe).getPartcount(); j++) {
					if (((CCMovie)dbe).getAbsolutePart(j).equals(path)) {
						return true;
					}
				}
			} else if (includeSeries){
				if (((CCSeries)dbe).isFileInList(path)) {
					return true;
				}
			}
		}
		
		return false;
	}

	public void resetAllMovieViewed(boolean to) {
		for (int i = 0; i < getElementCount(); i++) {
			CCDatabaseElement d = getDatabaseElementBySort(i);
			if (d.isMovie()) {
				((CCMovie)d).setViewed(to);
			}
		}
	}
}
