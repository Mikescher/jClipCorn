package de.jClipCorn.database.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.frames.importElementsFrame.ImportElementsFrame;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.DialogHelper;
import de.jClipCorn.util.PathFormatter;
import de.jClipCorn.util.TextFileUtils;

public class ExportHelper {
	private final static String DB_XML_FILENAME = "database.xml"; //$NON-NLS-1$
	
	public final static String EXTENSION_BACKUP = "jccbkp"; // = jClipCornBackup   						//$NON-NLS-1$ 
	public final static String EXTENSION_FULLEXPORT = "jxmlbkp"; // = jXMLBackup   						//$NON-NLS-1$ 
	public final static String EXTENSION_SINGLEEXPORT = "jsccexport"; // = jSingleClipCornExport 		//$NON-NLS-1$
	public final static String EXTENSION_MULTIPLEEXPORT = "jmccexport"; // = jMultipleClipCornExport 	//$NON-NLS-1$
	public final static String EXTENSION_CCBACKUP = "xml"; // OLD Clipcorn Backup   					//$NON-NLS-1$
	public final static String EXTENSION_COMPAREFILE = "jcccf"; 										//$NON-NLS-1$
	public final static String EXTENSION_EPISODEGUIDE = "txt"; 											//$NON-NLS-1$
	
	public static void zipDir(File owner, File zipDir, ZipOutputStream zos, boolean recursively) {
		try {
			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			for (int i = 0; i < dirList.length; i++) {
				File file = new File(zipDir, dirList[i]);
				if (file.isDirectory()) {
					if (recursively) {
						zipDir(owner, file, zos, recursively);
					}
					continue;
				}
				
				FileInputStream fis = new FileInputStream(file);
				ZipEntry anEntry = new ZipEntry(file.getAbsolutePath().replace(owner.getAbsolutePath() + '\\', "")); //$NON-NLS-1$
				zos.putNextEntry(anEntry);
				
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				fis.close();
			}
		} catch (Exception e) {
			CCLog.addError(e);
		}
	}
	
	public static void exportDatabase(File file, CCMovieList movielist) {
		Document xml = movielist.getAsXML();
		
		try {
			FileOutputStream ostream = new FileOutputStream(file);
			ZipOutputStream zos = new ZipOutputStream(ostream);
			
			ZipEntry xmlentry = new ZipEntry(DB_XML_FILENAME);
			zos.putNextEntry(xmlentry);
			XMLOutputter xout = new XMLOutputter();
			xout.setFormat(Format.getPrettyFormat());
			xout.output(xml, zos);
			
			zipDir(movielist.getCoverCache().getCoverDirectory().getParentFile(), movielist.getCoverCache().getCoverDirectory(), zos, false);
			
			zos.close();
		} catch (IOException e) {
			CCLog.addError(e);
		}
		

	}
	
	private static void copyAllCoverFromBackup(File backup, CCMovieList movielist) throws Exception {
		byte[] buffer = new byte[2048];

		InputStream theFile = new FileInputStream(backup);
		ZipInputStream stream = new ZipInputStream(theFile);

		try {
			ZipEntry entry;
			while ((entry = stream.getNextEntry()) != null) {
				if (! PathFormatter.getExtension(entry.getName()).equals(CCProperties.getInstance().PROP_COVER_TYPE.getValue())) {
					continue;
				}
				
				String outpath = movielist.getCoverCache().getCoverPath() + PathFormatter.getFilenameWithExt(entry.getName());
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(outpath);
					int len = 0;
					while ((len = stream.read(buffer)) > 0) {
						output.write(buffer, 0, len);
					}
				} finally {
					if (output != null) {
						output.close();
					}
				}
			}
		} finally {
			stream.close();
		}
	}
	
	private static String getXMLContentFromBackup(File backup) throws Exception{
		String content = null;

		InputStream theFile = new FileInputStream(backup);
		ZipInputStream stream = new ZipInputStream(theFile);

		try {
			ZipEntry entry;
			while ((entry = stream.getNextEntry()) != null) {
				if (! entry.getName().equals(DB_XML_FILENAME)) {
					continue;
				}
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8")); //$NON-NLS-1$
				content = TextFileUtils.readTextFile(reader);
				reader.close();
				
				return content;
			}
		} finally {
			stream.close();
			theFile.close();
		}
		
		return content;
	}
	
	public static void restoreDatabaseFromBackup(File backup, CCMovieList movielist) {
		try {
			movielist.clear();

			copyAllCoverFromBackup(backup, movielist);
			String content = getXMLContentFromBackup(backup);

			if (content == null) {
				throw new Exception("File not found: " + DB_XML_FILENAME); //$NON-NLS-1$
			}

			Document doc = new SAXBuilder().build(new StringReader(content));

			Element root = doc.getRootElement();

			for (Element e : root.getChildren()) {
				if (e.getName().equals("movie")) { //$NON-NLS-1$
					CCMovie mov = movielist.createNewEmptyMovie();
					mov.parseFromXML(e, false, false);
				} else if (e.getName().equals("series")) { //$NON-NLS-1$
					CCSeries ser = movielist.createNewEmptySeries();
					ser.parseFromXML(e, false, false);
				}
			}
		} catch (Exception e) {
			CCLog.addError(e);
		}
	}
	
	public static void exportMovie(File file, CCMovieList movielist, CCMovie mov, boolean includeCover) {
		List<CCDatabaseElement> list = new ArrayList<>(1);
		list.add(mov);
		exportDBElements(file, movielist, list, includeCover);
	}
	
	public static void exportSeries(File file, CCMovieList movielist, CCSeries ser, boolean includeCover) {
		List<CCDatabaseElement> list = new ArrayList<>(1);
		list.add(ser);
		exportDBElements(file, movielist, list, includeCover);
	}
	
	public static void exportDBElements(File file, CCMovieList movielist, List<CCDatabaseElement> elements, boolean includeCover) {
		Document xml = movielist.getEmptyXML();
		
		Element root = xml.getRootElement();
		
		root.setAttribute("elementcount", elements.size() + ""); //$NON-NLS-1$ //$NON-NLS-2$
		
		for (CCDatabaseElement el : elements) {
			el.generateXML(root, false, false, includeCover);
		}
		
		XMLOutputter xout = new XMLOutputter();
		xout.setFormat(Format.getPrettyFormat());
		
		FileOutputStream ostream = null;
		try {
			ostream = new FileOutputStream(file);
			xout.output(xml, ostream);
		} catch (IOException e) {
			CCLog.addError(e);
		} finally {
			if (ostream != null) {
				try {
					ostream.close();
				} catch (IOException e) {
					CCLog.addError(e);
				}
			}
		}
	}
	
	public static Element getFirstElementOfExport(String xmlcontent) {
		Document doc = null;
		try {
			doc = new SAXBuilder().build(new StringReader(xmlcontent));
		} catch (JDOMException | IOException e) {
			CCLog.addError(e);
			return null;
		}

		Element root = doc.getRootElement();
		
		List<Element> elements = root.getChildren();
		
		if (elements.size() > 0) {
			return elements.get(0);
		}
		
		return null;
	}
	
	public static void importSingleElement(CCMovieList movielist, String xmlcontent, boolean resetAddDate, boolean resetViewed) {
		Element value = getFirstElementOfExport(xmlcontent);
		
		if (value != null) {
			if (value.getName().equalsIgnoreCase("movie")) {  //$NON-NLS-1$
				CCMovie mov = movielist.createNewEmptyMovie();
				mov.parseFromXML(value, resetAddDate, resetViewed);
			} else if (value.getName().equalsIgnoreCase("series")) { //$NON-NLS-1$
				CCSeries ser = movielist.createNewEmptySeries();
				ser.parseFromXML(value, resetAddDate, resetViewed);
			}
		}
	}
	
	public static CCMovieTyp getTypOfFirstElementOfExport(String xmlcontent) {
		Element value = getFirstElementOfExport(xmlcontent);
		
		if (value.getName().equalsIgnoreCase("movie")) {  //$NON-NLS-1$
			return CCMovieTyp.MOVIE;
		} else if (value.getName().equalsIgnoreCase("series")) { //$NON-NLS-1$
			return CCMovieTyp.SERIES;
		}
		
		return null;
	}
	
	public static void openSingleElementFile(File f, MainFrame owner, CCMovieList movielist, CCMovieTyp forceTyp) {
		try {
			String xml = TextFileUtils.readTextFile(f);
			CCMovieTyp type = null;
			if (forceTyp != null && (type = ExportHelper.getTypOfFirstElementOfExport(xml)) != forceTyp) {
				CCLog.addError(LocaleBundle.getString("LogMessage.FormatErrorInExport")); //$NON-NLS-1$
				return;
			}
			
			int methodval = 0;
			if (type == CCMovieTyp.MOVIE) {
				methodval = DialogHelper.showLocaleOptions(owner, "ExportHelper.dialogs.importDirect", 1); //$NON-NLS-1$
			}
			
			boolean resetDate = DialogHelper.showLocaleYesNo(owner, "ExportHelper.dialogs.resetDate"); //$NON-NLS-1$
			boolean resetViewed = DialogHelper.showLocaleYesNo(owner, "ExportHelper.dialogs.resetViewed"); //$NON-NLS-1$
			
			if (methodval == 0) { // Direct
				ExportHelper.importSingleElement(movielist, xml, resetDate, resetViewed);
			} else if (methodval == 1) { // Edit & Add
				Element value = ExportHelper.getFirstElementOfExport(xml);
				AddMovieFrame amf = new AddMovieFrame(owner, movielist);
				amf.parseFromXML(value, resetDate, resetViewed);
				amf.setVisible(true);
			}
		} catch (IOException e) {
			CCLog.addError(LocaleBundle.getString("LogMessage.FormatErrorInExport"), e); //$NON-NLS-1$
		}
	}
	
	public static void openFullBackupFile(final File f, final MainFrame owner, final CCMovieList movielist) {
		if (DialogHelper.showLocaleYesNo(owner, "Dialogs.LoadBackup")) { //$NON-NLS-1$
			new Thread(new Runnable() {
				@Override
				public void run() {
					owner.beginBlockingIntermediate();
					
					ExportHelper.restoreDatabaseFromBackup(f, movielist);
					owner.getClipTable().autoResize();
					
					owner.endBlockingIntermediate();
				}
			}, "THREAD_IMPORT_JXMLBKP").start(); //$NON-NLS-1$
		}
	}
	
	public static void openMultipleElementFile(File f, MainFrame owner, CCMovieList movielist) {
		try {
			String xml = TextFileUtils.readTextFile(f);
			
			ImportElementsFrame ief = new ImportElementsFrame(owner, xml, movielist);
			ief.setVisible(true);
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}
}
