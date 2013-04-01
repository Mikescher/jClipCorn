package de.jClipCorn.database.databaseElement;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.jdom2.Element;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieStatus;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.LargeMD5Calculator;

public class CCSeason {
	private final CCSeries owner;
	private final int seasonID;
	
	private Vector<CCEpisode> episodes = new Vector<>();
	private String title;
	private int year;
	private String covername;
	
	private boolean isUpdating = false;
	
	public CCSeason(CCSeries owner, int seasonID) {
		this.owner = owner;
		this.seasonID = seasonID;
	}
	
	public void setDefaultValues(boolean updateDB) {
		episodes.clear();
		title = ""; //$NON-NLS-1$
		year = 0;
		
		if (updateDB) {
			updateDB();
		}
	}

	public void beginUpdating() {
		isUpdating = true;
	}
	
	public void endUpdating() {
		isUpdating = false;
		
		updateDB();
	}
	
	public void abortUpdating() {
		isUpdating = false;
	}
	
	private void updateDB() {
		if (! isUpdating) {
			owner.getMovieList().update(this);
		}
	}
	
	public void directlyInsertEpisode(CCEpisode ep) { // ONLY CALLED FROM CCDATABASE
		episodes.add(ep);
	}

	public int getSeasonID() {
		return seasonID;
	}

	public void setTitle(String t) {
		this.title = t;
		updateDB();
	}

	public void setYear(int y) {
		this.year = y;
		updateDB();
	}

	public void setCover(String cname) {
		this.covername = cname;
		
		updateDB();
	}
	
	public void setCover(BufferedImage name) {
		if (! covername.isEmpty() && name.equals(getCover())) {
			return;
		}
		
		if (! covername.isEmpty()) {
			getSeries().getMovieList().getCoverCache().deleteCover(this.covername);
		}
		
		this.covername = getSeries().getMovieList().getCoverCache().addCover(name);
		
		updateDB();
	}

	public CCSeries getSeries() {
		return owner;
	}

	public String getTitle() {
		return title;
	}

	public int getYear() {
		return year;
	}

	public String getCoverName() {
		return covername;
	}
	
	public BufferedImage getCover() {
		return owner.getMovieList().getCoverCache().getCover(covername);
	}
	
	public ImageIcon getCoverIcon() {
		return owner.getMovieList().getCoverCache().getCoverIcon(covername);
	}
	
	public BufferedImage getHalfsizeCover() {
		return owner.getMovieList().getCoverCache().getHalfsizeCover(covername);
	}
	
	public boolean isViewed() { // All parts viewed
		boolean v = true;
		for (CCEpisode ep : episodes) {
			v &= ep.isViewed();
		}
		return v;
	}
	
	public boolean isUnviewed() { // All parts not viewed
		boolean v = true;
		for (CCEpisode ep : episodes) {
			v &= ! ep.isViewed();
		}
		return v;
	}
	
	public CCMovieQuality getQuality() {
		int qs = 0;
		int qc = 0;
		for (CCEpisode ep : episodes) {
			qc++;
			qs += ep.getQuality().asInt();
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
	
	public CCMovieStatus getStatus() {
		CCMovieStatus i = CCMovieStatus.STATUS_OK;
		
		for (int j = 0; j < getEpisodeCount(); j++) {
			CCMovieStatus i2 = getEpisode(j).getStatus();
			if (i2.asInt() > i.asInt()) {
				i = i2;
			}
		}
		return i;
	}
	
	public int getEpisodeCount() {
		return episodes.size();
	}
	
	public int getLength() {
		int l = 0;
		for (CCEpisode ep : episodes) {
			l += ep.getLength();
		}
		return l;
	}
	
	public CCDate getAddDate() {
		CCDate cd = CCDate.getNewMinimumDate();
		for (CCEpisode ep : episodes) {
			if (ep.getAddDate().isGreaterThan(cd)) {
				cd = ep.getAddDate();
			}
		}
		return cd.copy();
	}
	
	public CCMovieFormat getFormat() {
		int l = CCMovieFormat.values().length;
		int[] ls = new int[l];
		for (int i = 0; i < l; i++) {
			ls[i] = 0;
		}
		
		for (CCEpisode ep : episodes) {
			ls[ep.getFormat().asInt()]++;
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
		
		for (CCEpisode ep : episodes) {
			sz.add(ep.getFilesize());
		}
		
		return sz;
	}

	/**
	 * ATTENTION: gets the n-te Episode | NOT THE Episode with the number n
	 */
	public CCEpisode getEpisode(int ee) {
		return episodes.get(ee);
	}
	
	public CCEpisode getEpisodebyNumber(int en) {
		for (int i = 0; i < episodes.size(); i++) {
			if (episodes.get(i).getEpisode() == en) {
				return episodes.get(i);
			}
		}
		return null;
	}

	public void deleteEpisode(CCEpisode episode) {
		episodes.remove(episode);
		
		getSeries().getMovieList().removeEpisodeFromDatabase(episode);
		
		getMovieList().fireOnChangeDatabaseElement(getSeries());
	}
	
	public CCMovieList getMovieList() {
		return getSeries().getMovieList();
	}

	public int getViewedCount() {
		int v = 0;
		for (CCEpisode ep : episodes) {
			v += ep.isViewed() ? 1 : 0;
		}
		return v;
	}
	
	public CCEpisode createNewEmptyEpisode() {
		return getMovieList().createNewEmptyEpisode(this);
	}
	
	public int getNewUnusedEpisodeNumber() {
		for (int i = 1;; i++) {
			if (getEpisodebyNumber(i) == null) {
				return i;
			}
		}
	}

	public void delete() {
		getSeries().deleteSeason(this);
	}
	
	public CCEpisode getFirstEpisode() {
		if (episodes.size() > 0) {
			return episodes.get(0);
		} else {
			return null;
		}
	}
	
	public ArrayList<File> getAbsolutePathList() {
		ArrayList<File> result = new ArrayList<>();
		
		for (int i = 0; i < episodes.size(); i++) {
			result.add(new File(getEpisode(i).getAbsolutePart()));
		}
		
		return result;
	}
	
	public boolean isFileInList(String path) {
		for (int i = 0; i < episodes.size(); i++) {
			if (getEpisode(i).getAbsolutePart().equals(path)) {
				return true;
			}
		}
		
		return false;
	}

	public Vector<CCEpisode> getEpisodeList() {
		return episodes;
	}
	
	/**
	 * @return the Number of the Season (as it is in the Series-List) (NOT THE ID)
	 */
	public int getSeasonNumber() {
		return getSeries().findSeason(this);
	}

	public int findEpisode(CCEpisode ccEpisode) {
		return episodes.indexOf(ccEpisode);
	}

	@SuppressWarnings("nls")
	protected void setXMLAttributes(Element e, boolean coverHash) {
		e.setAttribute("seasonid", seasonID + "");
		e.setAttribute("title", title);
		e.setAttribute("year", year + "");
		e.setAttribute("covername", covername);
		
		if (coverHash) {
			e.setAttribute("coverhash", getCoverMD5());
		}
	}
	
	public String getCoverMD5() {
		return LargeMD5Calculator.calcMD5(getCover());
	}

	@SuppressWarnings("nls")
	public Element generateXML(Element el, boolean fileHash, boolean coverHash) {
		Element sea = new Element("season");
		
		setXMLAttributes(sea, coverHash);
		
		el.addContent(sea);
		
		for (int i = 0; i < episodes.size(); i++) {
			episodes.get(i).generateXML(sea, fileHash);
		}
		
		return sea;
	}
}
