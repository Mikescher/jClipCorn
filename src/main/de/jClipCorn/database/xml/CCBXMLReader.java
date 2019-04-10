package de.jClipCorn.database.xml;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.formatter.RomanNumberFormatter;
import de.jClipCorn.util.helper.DialogHelper;

@SuppressWarnings("nls")
/*
 * Old (pascal) ClipCorn database export format
 */
public class CCBXMLReader {
	private String filename;
	private CCMovieList movielist;
	
	private Document document;
	
	public CCBXMLReader(String filename, CCMovieList ml) {
		this.movielist = ml;
		this.filename = filename;
	}
	
	public boolean parse() {
		if (movielist == null) {
			return false;
		}
		
		if (! loadXML()) {
			return false;
		}
		
		movielist.clear();
		
		try {
			if (! createDatabaseElements()) {
				return false;
			}
		} catch (DataConversionException | CCFormatException e) {
			return false;
		}
		
		return true;
	}
	
	private boolean loadXML() {
		try {
			document = new SAXBuilder().build(filename);
		} catch (JDOMException e) {
			CCLog.addError(e);
			return false;
		} catch (IOException e) {
			CCLog.addError(e);
			return false;
		}
		
		return true;
	}
	
	private boolean createDatabaseElements() throws DataConversionException, CCFormatException {
		Element root = document.getRootElement().getChild("database");
		if (root == null) return false;
		
		
		for (Iterator<Element> it = root.getChildren().iterator(); it.hasNext();) {
			Element e = it.next();
			if (e.getName().equals("movie")) {
				createMovie(e);
			} else {
				createSeries(e);
			}
		}
		
		return true;
	}
	
	private void createMovie(Element e) throws DataConversionException, CCFormatException {
		CCMovie newMov = movielist.createNewEmptyMovie();
		newMov.beginUpdating();
		
		newMov.setTitle(e.getChildText("filmtitel"));
		newMov.setZyklusTitle(getZyklusName(e.getChildText("zyklus")));
		newMov.setZyklusID(getZyklusNumber(e.getChildText("zyklus")));
		newMov.setViewed(! e.getChildText("gesehen").equals("0"));
		newMov.setQuality(e.getChild("qualität").getAttribute("dec").getIntValue() + 1);
		newMov.setLanguage(e.getChild("sprache").getAttribute("dec").getIntValue());
		newMov.setGenre(translateGenre(e.getChild("genre").getChild("genre00").getAttribute("dec").getIntValue()), 0);
		newMov.setGenre(translateGenre(e.getChild("genre").getChild("genre01").getAttribute("dec").getIntValue()), 1);
		newMov.setGenre(translateGenre(e.getChild("genre").getChild("genre02").getAttribute("dec").getIntValue()), 2);
		newMov.setGenre(translateGenre(e.getChild("genre").getChild("genre03").getAttribute("dec").getIntValue()), 3);
		newMov.setGenre(translateGenre(e.getChild("genre").getChild("genre04").getAttribute("dec").getIntValue()), 4);
		newMov.setGenre(translateGenre(e.getChild("genre").getChild("genre05").getAttribute("dec").getIntValue()), 5);
		newMov.setGenre(translateGenre(e.getChild("genre").getChild("genre06").getAttribute("dec").getIntValue()), 6);
		newMov.setLength(Integer.parseInt(e.getChildText("länge")));
		newMov.setAddDate(CCDate.deserialize(e.getChildText("adddate")));
		newMov.setOnlinescore(Integer.parseInt(e.getChildText("imdbscore")));
		newMov.setFsk(e.getChild("usk").getAttribute("dec").getIntValue());
		newMov.setFormat(e.getChild("format").getAttribute("dec").getIntValue());
		newMov.setYear(Integer.parseInt(e.getChildText("jahr")));
		newMov.setFilesize(e.getChild("größe").getAttribute("dec").getLongValue() * 1024);
		newMov.setPart(0, e.getChildText("pathpart1"));
		newMov.setPart(1, e.getChildText("pathpart2"));
		String cvrval = e.getChildText("cover");
		newMov.setCover(cvrval.substring(0, cvrval.length() - 3) + "png");
		
		final CCMovie finmov = newMov;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					finmov.endUpdating();
				}
			});
		} catch (InvocationTargetException | InterruptedException e1) {
			CCLog.addError(e1);
		}
	}
	
	private void createSeries(Element e) throws DataConversionException, CCFormatException {
		CCSeries newSer = movielist.createNewEmptySeries();
		newSer.beginUpdating();

		CCDBLanguage lang = CCDBLanguage.getWrapper().findOrException(e.getChild("info").getChild("sprache").getAttribute("dec").getIntValue());

		newSer.setTitle(e.getChild("info").getChildText("serientitel"));
		newSer.setGenre(translateGenre(e.getChild("info").getChild("genre").getChild("genre00").getAttribute("dec").getIntValue()), 0);
		newSer.setGenre(translateGenre(e.getChild("info").getChild("genre").getChild("genre01").getAttribute("dec").getIntValue()), 1);
		newSer.setGenre(translateGenre(e.getChild("info").getChild("genre").getChild("genre02").getAttribute("dec").getIntValue()), 2);
		newSer.setGenre(translateGenre(e.getChild("info").getChild("genre").getChild("genre03").getAttribute("dec").getIntValue()), 3);
		newSer.setGenre(translateGenre(e.getChild("info").getChild("genre").getChild("genre04").getAttribute("dec").getIntValue()), 4);
		newSer.setGenre(translateGenre(e.getChild("info").getChild("genre").getChild("genre05").getAttribute("dec").getIntValue()), 5);
		newSer.setGenre(translateGenre(e.getChild("info").getChild("genre").getChild("genre06").getAttribute("dec").getIntValue()), 6);
		newSer.setOnlinescore(Integer.parseInt(e.getChild("info").getChildText("imdbscore")));
		newSer.setFsk(e.getChild("info").getChild("usk").getAttribute("dec").getIntValue());
		String cvrval = e.getChild("info").getChildText("cover");
		newSer.setCover(cvrval.substring(0, cvrval.length() - 3) + "png");
		
		for (Iterator<Element> itseries = e.getChildren().iterator(); itseries.hasNext();) {
			Element eseries = itseries.next();
			if (eseries.getName().equals("season")) {
				createSeason(newSer, eseries, lang);
			}
		}
		
		final CCSeries finser = newSer;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					finser.endUpdating();
				}
			});
		} catch (InvocationTargetException | InterruptedException e1) {
			CCLog.addError(e1);
		}
	}
	
	private void createSeason(CCSeries series, Element owner, CCDBLanguage lang) throws DataConversionException, CCFormatException {
		CCSeason newSeas = series.createNewEmptySeason();
		
		newSeas.beginUpdating();
		
		newSeas.setTitle(owner.getChild("info").getChildText("staffeltitel"));
		newSeas.setYear(Integer.parseInt(owner.getChild("info").getChildText("jahr")));
		String cvrval = owner.getChild("info").getChildText("cover");
		newSeas.setCover(cvrval.substring(0, cvrval.length() - 3) + "png");
		
		for (Iterator<Element> itseason = owner.getChildren().iterator(); itseason.hasNext();) {
			Element eseason = itseason.next();
			if (eseason.getName().equals("episode")) {
				createEpisode(newSeas, eseason, lang);
			}
		}
		
		final CCSeason finseas = newSeas;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					finseas.endUpdating();
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
		}
	}
	
	private void createEpisode(CCSeason season, Element owner, CCDBLanguage lang) throws DataConversionException, CCFormatException {
		CCEpisode newEp = season.createNewEmptyEpisode();
		
		newEp.beginUpdating();
		
		newEp.setEpisodeNumber(owner.getAttribute("number").getIntValue());
		newEp.setTitle(owner.getChildText("filmtitel"));
		newEp.setViewed(! owner.getChildText("gesehen").equals("0"));
		newEp.setQuality(owner.getChild("qualität").getAttribute("dec").getIntValue() + 1);
		newEp.setLength(Integer.parseInt(owner.getChildText("länge")));
		newEp.setFormat(owner.getChild("format").getAttribute("dec").getIntValue());
		newEp.setFilesize(owner.getChild("größe").getAttribute("dec").getLongValue() * 1024);
		newEp.setPart(owner.getChildText("pathpart1"));
		newEp.setAddDate(CCDate.deserialize(owner.getChildText("adddate")));
		newEp.setLanguage(new CCDBLanguageList(lang));
		
		final CCEpisode finep = newEp;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					try {
						finep.endUpdating();
					} catch (Exception e) {
						CCLog.addError(e);
						throw e;
					}
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
		}
	}
	
	private CCGenre translateGenre(int genre) throws EnumFormatException {
		if (genre == -1) return CCGenre.getWrapper().findOrException(CCGenre.NO_GENRE);
		return CCGenre.getWrapper().findOrException(genre);
	}
	
	private String getZyklusName(String fullz) {
		if (fullz.isEmpty()) return "";
		
		for (int i = fullz.length()-1; i > 0; i--) {
			String s = fullz.substring(i);
			if (s.charAt(0) == ' ') {
				s = s.substring(1);
				if (RomanNumberFormatter.isRoman(s)) {
					return fullz.substring(0, i);
				}
			}
		}
		return fullz;
	}
	
	private int getZyklusNumber(String fullz) {
		if (fullz.isEmpty()) return -1;
		
		for (int i = fullz.length()-1; i > 0; i--) {
			String s = fullz.substring(i);
			if (s.charAt(0) == ' ') {
				s = s.substring(1);
				if (RomanNumberFormatter.isRoman(s)) {
					return RomanNumberFormatter.romToDec(s);
				}
			}
		}
		return -1;
	}
	
	public static void openFile(final File f, final MainFrame owner) {
		if (DialogHelper.showLocaleYesNo(owner, "Dialogs.LoadXML")) { //$NON-NLS-1$
			new Thread(new Runnable() {
				@Override
				public void run() {
					owner.beginBlockingIntermediate();

					CCBXMLReader xmlreader = new CCBXMLReader(f.getAbsolutePath(), owner.getMovielist());
					if (!xmlreader.parse()) {
						CCLog.addError(LocaleBundle.getString("LogMessage.CouldNotParseCCBXML")); //$NON-NLS-1$
					}
					owner.getClipTable().autoResize();

					owner.endBlockingIntermediate();
				}
			}, "THREAD_PARSE_XML").start(); //$NON-NLS-1$
		}
	}
}
