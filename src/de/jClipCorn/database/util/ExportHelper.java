package de.jClipCorn.database.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.PathFormatter;
import de.jClipCorn.util.TextFileUtils;

public class ExportHelper {
	
	public static void zipDir(File owner, File zipDir, ZipOutputStream zos, boolean recursively) {
		try {
			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			for (int i = 0; i < dirList.length; i++) {
				File f = new File(zipDir, dirList[i]);
				if (f.isDirectory()) {
					if (recursively) {
						zipDir(owner, f, zos, recursively);
					}
					continue;
				}
				
				FileInputStream fis = new FileInputStream(f);
				ZipEntry anEntry = new ZipEntry(f.getAbsolutePath().replace(owner.getAbsolutePath() + '\\', "")); //$NON-NLS-1$
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
	
	public static void export(File file, CCMovieList movielist) {
		Document xml = movielist.getAsXML();
		
		try {
			FileOutputStream ostream = new FileOutputStream(file);
			ZipOutputStream zos = new ZipOutputStream(ostream);
			
			ZipEntry xmlentry = new ZipEntry("database.xml"); //$NON-NLS-1$
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
				if (! PathFormatter.getExtension(entry.getName()).equals("png")) { //$NON-NLS-1$
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
				if (! entry.getName().equals("database.xml")) { //$NON-NLS-1$
					continue;
				}
				
				BufferedReader b = new BufferedReader(new InputStreamReader(stream, "UTF-8")); //$NON-NLS-1$
				content = TextFileUtils.readTextFile(b);
				b.close();
				
				return content;
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		
		return content;
	}
	
	public static void restoreFromBackup(File backup, CCMovieList movielist) throws Exception {
		movielist.clear();
		
		copyAllCoverFromBackup(backup, movielist);
		String content = getXMLContentFromBackup(backup);
		
		if (content == null) {
			throw new Exception("File not found: database.xml"); //$NON-NLS-1$
		}
		
		Document doc = new SAXBuilder().build(new StringReader(content));
		
		Element root = doc.getRootElement();
		
		for (Element e : root.getChildren()) {
			if (e.getName().equals("movie")) { //$NON-NLS-1$
				CCMovie m = movielist.createNewEmptyMovie();
				m.parseFromXML(e);
			} else if (e.getName().equals("series")) { //$NON-NLS-1$
				CCSeries s = movielist.createNewEmptySeries();
				s.parseFromXML(e);
			}
		}
	}
}
