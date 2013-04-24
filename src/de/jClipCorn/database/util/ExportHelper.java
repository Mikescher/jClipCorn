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
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.PathFormatter;
import de.jClipCorn.util.TextFileUtils;

public class ExportHelper {
	private final static String DB_XML_FILENAME = "database.xml"; //$NON-NLS-1$
	
	public final static String EXTENSION_BACKUP = "jccbkp"; // = jClipCornBackup   						//$NON-NLS-1$ 
	public final static String EXTENSION_FULLEXPORT = "jxmlbkp"; // = jXMLBackup   						//$NON-NLS-1$ 
	public final static String EXTENSION_SINGLEEXPORT = "jsccexport"; // = jSingleClipCornExport 		//$NON-NLS-1$
	public final static String EXTENSION_MULTIPLEEXPORT = "jmccexport"; // = jMultipleClipCornExport 	//$NON-NLS-1$
	public final static String EXTENSION_CCBACKUP = "xml"; // OLD Clipcorn Backup   					//$NON-NLS-1$ 
	
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
			if (stream != null) {
				stream.close();
			}
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
}
