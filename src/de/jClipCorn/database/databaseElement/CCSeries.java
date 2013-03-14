package de.jClipCorn.database.databaseElement;

import java.util.ArrayList;
import java.util.Vector;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.YearRange;

public class CCSeries extends CCDatabaseElement {
	private Vector<CCSeason> seasons = new Vector<>();
	
	public CCSeries(CCMovieList ml, int id, int seriesID) {
		super(ml, CCMovieTyp.SERIES, id, seriesID);
	}
	
	@Override
	protected void updateDB() {
		if (! isUpdating) {
			movielist.update(this);
		}
	}

	public void directlyInsertSeason(CCSeason s) { // !! ONLY CALLED FROM CCDatabase
		seasons.add(s);
	}

	public CCSeason createNewEmptySeason() {
		return movielist.createNewEmptySeason(this);
	}

	public CCMovieList getMovieList() {
		return movielist;
	}
	
	public boolean isViewed() { // All parts viewed
		boolean v = true;
		for (CCSeason se: seasons) {
			v &= se.isViewed();
		}
		return v;
	}
	
	public boolean isUnviewed() { // All parts not viewed
		boolean v = true;
		for (CCSeason se: seasons) {
			v &= se.isUnviewed();
		}
		return v;
	}
	
	public CCMovieQuality getQuality() {
		int qs = 0;
		int qc = 0;
		for (CCSeason se: seasons) {
			qc++;
			qs += se.getQuality().asInt();
		}
		
		if (qc > 0) {
			int q = (int) Math.round((qs*1d) / qc);
			
			q = Math.max(0, q);
			q = Math.min(q, CCMovieQuality.values().length - 1);
			
			return CCMovieQuality.find(q);
		} else {
			return CCMovieQuality.STREAM;
		}
	}
	
	public int getSeasonCount() {
		return seasons.size();
	}
	
	public int getEpisodeCount() {
		int c = 0;
		for (CCSeason se : seasons) {
			c += se.getEpisodeCount();
		}
		return c;
	}
	
	public int getLength() {
		int l = 0;
		for (CCSeason se: seasons) {
			l += se.getLength();
		}
		return l;
	}
	
	public CCDate getAddDate() {
		CCDate cd = CCDate.getNewMinimumDate();
		for (CCSeason se: seasons) {
			if (se.getAddDate().isGreaterThan(cd)) {
				cd = se.getAddDate();
			}
		}
		return cd;
	}
	
	public CCMovieFormat getFormat() {
		int l = CCMovieFormat.values().length;
		int[] ls = new int[l];
		for (int i = 0; i < l; i++) {
			ls[i] = 0;
		}
		
		for (CCSeason se: seasons) {
			ls[se.getFormat().asInt()]++;
		}
		
		int max = 0;
		int maxid = 0;
		for (int i = 0; i < l; i++) {
			if (ls[i] > max) {
				max = ls[i];
				maxid = i;
			}
		}
		
		return CCMovieFormat.find(maxid);
	}
	
	public CCMovieSize getFilesize() {
		CCMovieSize sz = new CCMovieSize();
		
		for (CCSeason se: seasons) {
			sz.add(se.getFilesize());
		}
		
		return sz;
	}
	
	public YearRange getYearRange() {
		int miny = Integer.MAX_VALUE;
		int maxy = Integer.MIN_VALUE;
		
		for (CCSeason se: seasons) {
			miny = Math.min(miny, se.getYear());
			maxy = Math.max(maxy, se.getYear());
		}
		return new YearRange(miny, maxy);
	}

	public CCSeason getSeason(int ss) {
		return seasons.get(ss);
	}

	public void deleteSeason(CCSeason season) {
		seasons.remove(season);
		
		for (int i = season.getEpisodeCount()-1; i >= 0; i--) {
			season.deleteEpisode(season.getEpisode(i));
		}
		
		getMovieList().removeSeasonDatabase(season);
		
		getMovieList().getCoverCache().deleteCover(getCoverName());
		
		getMovieList().fireOnChangeDatabaseElement(this);
	}

	public int getViewedCount() {
		int v = 0;
		for (CCSeason se : seasons) {
			v += se.getViewedCount();
		}
		return v;
	}

	public void delete() {
		getMovieList().remove(this);
	}
	
	public CCEpisode getFirstEpisode() {
		for (int i = 0; i < seasons.size(); i++) {
			CCEpisode ep = seasons.get(i).getFirstEpisode();
			if (ep != null) return ep;
		}
		return null;
	}
	
	public ArrayList<String> getAbsolutePathList() {
		ArrayList<String> result = new ArrayList<>();
		
		for (int i = 0; i < seasons.size(); i++) {
			result.addAll(getSeason(i).getAbsolutePathList());
		}
		
		return result;
	}
	
	public boolean isFileInList(String path) {
		for (int i = 0; i < seasons.size(); i++) {
			CCSeason s = getSeason(i);
			if(s.isFileInList(path)) {
				return true;
			}
		}
		
		return false;
	}
}
