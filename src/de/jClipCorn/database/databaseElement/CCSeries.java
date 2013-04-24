package de.jClipCorn.database.databaseElement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jdom2.Element;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieStatus;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CombinedMovieQuality;
import de.jClipCorn.util.ByteUtilies;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.ImageUtilities;
import de.jClipCorn.util.LargeMD5Calculator;
import de.jClipCorn.util.YearRange;

public class CCSeries extends CCDatabaseElement {
	private List<CCSeason> seasons = new Vector<>();
	
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
			int qual = (int) Math.round((qs*1d) / qc);
			
			qual = Math.max(0, qual);
			qual = Math.min(qual, CCMovieQuality.values().length - 1);
			
			return CCMovieQuality.find(qual);
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
		if (getMovieList().isBlocked()) {
			return new CCMovieSize();
		}
		
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
	
	public CCMovieStatus getStatus() {
		CCMovieStatus i = CCMovieStatus.STATUS_OK;
		
		for (int j = 0; j < getSeasonCount(); j++) {
			CCMovieStatus i2 = getSeason(j).getStatus();
			if (i2.asInt() > i.asInt()) {
				i = i2;
			}
		}
		return i;
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
		
		if (! season.getCoverName().isEmpty()) {
			getMovieList().getCoverCache().deleteCover(season.getCoverName());
		}
		
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
	
	public List<File> getAbsolutePathList() {
		List<File> result = new ArrayList<>();
		
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

	public CombinedMovieQuality getCombinedQuality() {
		return new CombinedMovieQuality(getQuality(), getStatus());
	}
	
	public List<CCEpisode> getEpisodeList() {
		List<CCEpisode> result = new ArrayList<>();
		
		for (int i = 0; i < getSeasonCount(); i++) {
			result.addAll(getSeason(i).getEpisodeList());
		}
		
		return result;
	}
	
	public CCEpisode getRandomEpisode() {
		List<CCEpisode> eplist = getEpisodeList();
		if (eplist.size() > 0) {
			return eplist.get((int) (Math.random()*eplist.size()));
		}
		return null;
	}
	
	public CCEpisode getRandomEpisodeWithViewState(boolean viewed) {
		List<CCEpisode> eplist = getEpisodeList();
		
		for (int i = eplist.size() - 1; i >= 0; i--) {
			if (eplist.get(i).isViewed() ^ viewed) {
				eplist.remove(i);
			}
		}
		
		if (eplist.size() > 0) {
			return eplist.get((int) (Math.random()*eplist.size()));
		}
		return null;
	}
	
	public CCEpisode getNextEpisode() {
		List<CCEpisode> el = getEpisodeList();
		
		if (el.size() == 0) {
			return null;
		}
		
		if (isViewed()) {
			CCDate d = CCDate.getNewMinimumDate();
			
			for (int i = 0; i < el.size(); i++) {
				if (d.isGreaterThan(el.get(i).getLastViewed())) {
					return el.get(i);
				}
				d = el.get(i).getLastViewed();
			}
			
			return el.get(0);
		} else {
			for (int i = 0; i < el.size(); i++) {
				if (! el.get(i).isViewed()) {
					return el.get(i);
				}
			}
		}
		
		return null;
	}

	public int findSeason(CCSeason ccSeason) {
		return seasons.indexOf(ccSeason);
	}
	
	@SuppressWarnings("nls")
	@Override
	protected void setXMLAttributes(Element e, boolean fileHash, boolean coverHash, boolean coverData) {
		super.setXMLAttributes(e, fileHash, coverHash, coverData);
		
		if (coverHash) {
			e.setAttribute("coverhash", getCoverMD5());
		}
		
		if (coverData) {
			e.setAttribute("coverdata", ByteUtilies.byteArrayToHexString(ImageUtilities.imageToByteArray(getCover())));
		}
	}
	
	
	@Override
	@SuppressWarnings("nls")
	public void parseFromXML(Element e, boolean resetAddDate, boolean resetViewed) {
		beginUpdating();
		
		super.parseFromXML(e, resetAddDate, resetViewed);
		
		for (Element e2 : e.getChildren("season")) {
			createNewEmptySeason().parseFromXML(e2, resetAddDate, resetViewed);
		}
		
		endUpdating();
	}
	
	public String getCoverMD5() {
		return LargeMD5Calculator.calcMD5(getCover());
	}
	
	@Override
	public Element generateXML(Element el, boolean fileHash, boolean coverHash, boolean coverData) {
		Element ser = super.generateXML(el, fileHash, coverHash, coverData);
		
		for (int i = 0; i < seasons.size(); i++) {
			seasons.get(i).generateXML(ser, fileHash, coverHash, coverData);
		}
		
		return ser;
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
}
