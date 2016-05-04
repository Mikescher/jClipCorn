package de.jClipCorn.database.databaseElement;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import org.jdom2.Element;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.LargeMD5Calculator;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ByteUtilies;
import de.jClipCorn.util.helper.ImageUtilities;

public class CCSeason {
	private final CCSeries owner;
	private final int seasonID;
	
	private List<CCEpisode> episodes = new Vector<>();
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
		covername = ""; //$NON-NLS-1$
		
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
			v &= ep.isViewed(); // Shortcut Evaluation ftw
		}
		return v;
	}
	
	public boolean isUnviewed() { // All parts not viewed
		boolean v = true;
		for (CCEpisode ep : episodes) {
			v &= ! ep.isViewed(); // Shortcut Evaluation ftw
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
			int qual = (int) Math.round((qs*1d) / qc);
			
			qual = Math.max(0, qual);
			qual = Math.min(qual, CCMovieQuality.values().length - 1);
			
			return CCMovieQuality.find(qual);
		} else {
			return CCMovieQuality.STREAM;
		}
	}
	
	public CCMovieTags getTags() {
		CCMovieTags i = new CCMovieTags();
		
		for (int j = 0; j < getEpisodeCount(); j++) {
			i.doUnion(getEpisode(j).getTags());
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
		switch (CCProperties.getInstance().PROP_SERIES_ADDDATECALCULATION.getValue()) {
		case 0:
			return calcMinimumAddDate();
		case 1:
			return calcMaximumAddDate();
		case 2:
			return calcAverageAddDate();
		default:
			return null;
		}
	}
	
	public CCDate calcMaximumAddDate() {
		CCDate cd = CCDate.getMinimumDate();
		for (CCEpisode ep : episodes) {
			if (ep.getAddDate().isGreaterThan(cd)) {
				cd = ep.getAddDate();
			}
		}
		return cd;
	}
	
	public CCDate calcMinimumAddDate() {
		CCDate cd = CCDate.getMaximumDate();
		for (CCEpisode ep : episodes) {
			if (ep.getAddDate().isLessThan(cd)) {
				cd = ep.getAddDate();
			}
		}
		return cd;
	}
	
	public CCDate calcAverageAddDate() {
		List<CCDate> dlist = new ArrayList<>();
		for (CCEpisode ep : episodes) {
			dlist.add(ep.getAddDate());
		}
		return CCDate.getAverageDate(dlist);
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
	
	public Integer getCommonEpisodeLength() {
		if (getEpisodeCount() == 0) return null;
		
		int len = -1;
		for (int i = 0; i < getEpisodeCount(); i++) {
			CCEpisode ep = getEpisode(i);

			if (ep.getLength() > 0)
				if (len > 0 && ep.getLength() != len) {
					return null;
				} else {
					len = ep.getLength();
				}
		}
		
		return len;
	}
	
	public int getNextEpisodeNumber() {
		int ep = -1;
		
		for (CCEpisode ccEpisode : episodes) {
			ep = Math.max(ep, ccEpisode.getEpisode());
		}
		
		return ep + 1;
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
	
	public List<File> getAbsolutePathList() {
		List<File> result = new ArrayList<>();
		
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

	public List<CCEpisode> getEpisodeList() {
		return episodes;
	}
	
	/**
	 * @return the Number of the Season (as it is in the Series-List) (NOT THE ID)
	 */
	public int getSeasonNumber() {
		return getSeries().findSeason(this);
	}
	
	/**
	 * @return the Number of the Season (as it should be) (NOT THE ID)
	 */
	public int getSortedSeasonNumber() {
		return getSeries().findSeasoninSorted(this);
	}

	public int findEpisode(CCEpisode ccEpisode) {
		return episodes.indexOf(ccEpisode);
	}

	@SuppressWarnings("nls")
	protected void setXMLAttributes(Element e, boolean coverHash, boolean coverData) {
		e.setAttribute("seasonid", seasonID + "");
		e.setAttribute("title", title);
		e.setAttribute("year", year + "");
		e.setAttribute("covername", covername);
		
		if (coverHash) {
			e.setAttribute("coverhash", getCoverMD5());
		}
		
		if (coverData) {
			e.setAttribute("coverdata", ByteUtilies.byteArrayToHexString(ImageUtilities.imageToByteArray(getCover())));
		}
	}
	
	@SuppressWarnings("nls")
	public void parseFromXML(Element e, boolean resetAddDate, boolean resetViewed, boolean resetTags) {
		beginUpdating();
		
		if (e.getAttributeValue("title") != null)
			setTitle(e.getAttributeValue("title"));
		
		if (e.getAttributeValue("year") != null)
			setYear(Integer.parseInt(e.getAttributeValue("year")));
		
		if (e.getAttributeValue("covername") != null)
			setCover(e.getAttributeValue("covername"));
		
		for (Element e2 : e.getChildren("episode")) {
			createNewEmptyEpisode().parseFromXML(e2, resetAddDate, resetViewed, resetTags);
		}
		
		if (e.getAttributeValue("coverdata") != null) {
			setCover(""); //Damit er nicht probiert was zu löschen
			setCover(ImageUtilities.byteArrayToImage(ByteUtilies.hexStringToByteArray(e.getAttributeValue("coverdata"))));
		}
		
		endUpdating();
	}
	
	public String getCoverMD5() {
		return LargeMD5Calculator.calcMD5(getCover());
	}

	@SuppressWarnings("nls")
	public Element generateXML(Element el, boolean fileHash, boolean coverHash, boolean coverData) {
		Element sea = new Element("season");
		
		setXMLAttributes(sea, coverHash, coverData);
		
		el.addContent(sea);
		
		for (int i = 0; i < episodes.size(); i++) {
			episodes.get(i).generateXML(sea, fileHash);
		}
		
		return sea;
	}
	
	@Override
	public String toString() {
		return getTitle();
	}

	public String getCommonPathStart() {
		List<String> all = new ArrayList<>();
		
		for (int seasi = 0; seasi < getEpisodeCount(); seasi++) {
			CCEpisode episode = getEpisode(seasi);
			all.add(episode.getPart());
		}
		
		while (all.contains("")) all.remove(""); //$NON-NLS-1$ //$NON-NLS-2$
		
		return PathFormatter.getCommonFolderPath(all);
	}
	
	public String getFolderNameForCreatedFolderStructure() {
		return PathFormatter.fixStringToFilesystemname(getTitle());
	}

	public int getIndexForCreatedFolderStructure() {
		Pattern regex = owner.getValidSeasonIndexRegex();
		
		if (regex != null) {
			Matcher m = regex.matcher(getTitle());
			m.matches();
			return Integer.parseInt(m.group("index")); //$NON-NLS-1$
		}
		
		return getSortedSeasonNumber() + 1;
	}
}
