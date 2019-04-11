package de.jClipCorn.features.serialization;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import de.jClipCorn.features.serialization.xmlexport.DatabaseXMLExporter;
import de.jClipCorn.features.serialization.xmlexport.ExportOptions;
import de.jClipCorn.features.serialization.xmlimport.DatabaseXMLImporter;
import de.jClipCorn.features.serialization.xmlimport.ImportOptions;
import de.jClipCorn.features.serialization.xmlimport.ImportState;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.exceptions.SerializationException;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;
import de.jClipCorn.util.xml.CCXMLParser;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.util.covercache.CCCoverCache;
import de.jClipCorn.database.util.covercache.CCFolderCoverCache;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.frames.importElementsFrame.ImportElementsFrame;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.TimeKeeper;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.lambda.Func0to1WithIOException;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.listener.ProgressCallbackListener;

public class ExportHelper {
	private final static String DB_XML_FILENAME_MAIN   = "database.xml"; 														//$NON-NLS-1$
	private final static String DB_XML_FILENAME_GROUPS = "groups.xml";   														//$NON-NLS-1$
	private final static String DB_XML_FILENAME_INFO   = "info.xml";     														//$NON-NLS-1$
	
	public final static String EXTENSION_BACKUP = "jccbkp"; 				// = jClipCornBackup   								//$NON-NLS-1$ 
	public final static String EXTENSION_BACKUPPROPERTIES = "jccbkpinfo"; 	// = jClipCornBackupInfo		[DEPRECATED] 		//$NON-NLS-1$ 
	public final static String EXTENSION_FULLEXPORT = "jxmlbkp"; 			// = jXMLBackup   									//$NON-NLS-1$ 
	public final static String EXTENSION_SINGLEEXPORT = "jsccexport"; 		// = jSingleClipCornExport 							//$NON-NLS-1$
	public final static String EXTENSION_MULTIPLEEXPORT = "jmccexport"; 	// = jMultipleClipCornExport 						//$NON-NLS-1$
	public final static String EXTENSION_CCBACKUP = "xml"; 					// = OLD Clipcorn Backup   		[DEPRECATED]		//$NON-NLS-1$
	public final static String EXTENSION_COMPAREFILE = "jcccf"; 			// = jClipCornCompareFile							//$NON-NLS-1$
	public final static String EXTENSION_EPISODEGUIDE = "txt"; 				// = TextFile										//$NON-NLS-1$
	public final static String EXTENSION_TEXTEXPORT_PLAIN = "txt"; 			// = TextFile										//$NON-NLS-1$
	public final static String EXTENSION_TEXTEXPORT_XML = "xml"; 			// = XML-TextFile									//$NON-NLS-1$
	public final static String EXTENSION_TEXTEXPORT_JSON = "json"; 			// = JSON-TextFile									//$NON-NLS-1$
	public final static String EXTENSION_FILTERLIST = "flst"; 				// = FilterList										//$NON-NLS-1$

	public final static String FILENAME_BACKUPINFO = "info.ini"; 			// = jClipCornBackupInfo 							//$NON-NLS-1$ 
	
	public static void zipDir(File owner, File zipDir, ZipOutputStream zos, boolean recursively) {
		doZipDir(owner, zipDir, zos, recursively, f->false, null);
	}
	
	public static void zipDir(File owner, File zipDir, ZipOutputStream zos, boolean recursively, Func1to1<File, Boolean> excluded, ProgressCallbackListener pcl) {
		pcl.reset();
		pcl.setMax(PathFormatter.countAllFiles(zipDir));
		
		doZipDir(owner, zipDir, zos, recursively, excluded, pcl);
	}
	
	private static void doZipDir(File owner, File zipDir, ZipOutputStream zos, boolean recursively, Func1to1<File, Boolean> excluded, ProgressCallbackListener pcl) {
		try {
			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			
			for (int i = 0; i < dirList.length; i++) {
				if (dirList[i].toLowerCase().endsWith("thumbs.db")) continue; //$NON-NLS-1$
				
				File file = new File(zipDir, dirList[i]);

				if (excluded.invoke(file)) continue;

				if (file.isDirectory()) {
					if (recursively) {
						doZipDir(owner, file, zos, recursively, excluded, pcl);
					}
					continue;
				}
				
				FileInputStream fis = new FileInputStream(file);
				ZipEntry anEntry = new ZipEntry(file.getAbsolutePath().replace(PathFormatter.appendSeparator(owner.getAbsolutePath()), "")); //$NON-NLS-1$
				zos.putNextEntry(anEntry);
				
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				fis.close();
				
				if (pcl != null) pcl.step();
			}
		} catch (Exception e) {
			CCLog.addError(e);
		}
	}
	
	public static boolean unzipBackupDir(File archive, File targetFolder, ProgressCallbackListener pcl) {
		byte[] buffer = new byte[2048];

		String outdir = targetFolder.getAbsolutePath() + "/"; //$NON-NLS-1$

		InputStream filestream = null;
		ZipInputStream zipstream = null;
		
		try {
			filestream = new FileInputStream(archive);
			zipstream = new ZipInputStream(filestream);
			
			if (pcl != null) {
				int entryCount = 0;
				while (zipstream.getNextEntry() != null) entryCount++;
				pcl.setMax(entryCount);
				pcl.reset();
				
				zipstream.close();
				filestream = new FileInputStream(archive);
				zipstream = new ZipInputStream(filestream);
			}
			
			ZipEntry entry;
			while ((entry = zipstream.getNextEntry()) != null) {
				if (entry.getName().equalsIgnoreCase(ExportHelper.FILENAME_BACKUPINFO)) continue;
				
				File outfile = new File(outdir + entry.getName());
				FileOutputStream output = null;
				
				try {
					new File(outfile.getParent()).mkdirs();
					output = new FileOutputStream(outfile);
					int len = 0;
					while ((len = zipstream.read(buffer)) > 0) {
						output.write(buffer, 0, len);
					}
				} finally {
					if (output != null)
						output.close();
				}
				
				if (pcl != null) pcl.step();
			}
		} catch (IOException e) {
			CCLog.addError(e);
			return false;
		} finally {
			try {
				if (zipstream != null)
					zipstream.close();
				if (filestream != null)
					filestream.close();
			} catch (IOException e) {
				CCLog.addError(e);
				return false;
			}
		}
		
		return true;
	}

	public static void exportDatabase(File file, CCMovieList movielist) {
		try {
			TimeKeeper.start();
			
			FileOutputStream ostream = new FileOutputStream(file);
			ZipOutputStream zos = new ZipOutputStream(ostream);
			
			outputXML(zos, movielist.getElementsAsXML(), DB_XML_FILENAME_MAIN);
			outputXML(zos, movielist.getGroupsAsXML(), DB_XML_FILENAME_GROUPS);
			outputXML(zos, movielist.getDBInfoAsXML(), DB_XML_FILENAME_INFO);
			
			CCCoverCache cc = movielist.getCoverCache();
			
			if (cc instanceof CCFolderCoverCache) {
				// fast track for already serialized images (~ 6 times faster)
				zipDir(((CCFolderCoverCache)cc).getCoverDirectory().getParentFile(), ((CCFolderCoverCache)cc).getCoverDirectory(), zos, false);
			} else {
				for (Tuple<String, Func0to1WithIOException<BufferedImage>> cover : cc.listCoversNonCached()) {
					ZipEntry coverEntry = new ZipEntry(PathFormatter.combine("cover", cover.Item1)); //$NON-NLS-1$
					zos.putNextEntry(coverEntry);
					ImageIO.write(cover.Item2.invoke(), "PNG", zos); //$NON-NLS-1$
				}
			}
			
			zos.close();
			
			TimeKeeper.stopAndPrint();
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}
	
	private static void outputXML(ZipOutputStream zos, Document xml, String filename) throws IOException {
		ZipEntry xmlentry = new ZipEntry(filename);
		zos.putNextEntry(xmlentry);
		XMLOutputter xout = new XMLOutputter();
		xout.setFormat(Format.getPrettyFormat());
		xout.output(xml, zos);
	}
	
	private static void copyAllCoverFromBackup(File backup, CCMovieList movielist) throws Exception {
		InputStream theFile = new FileInputStream(backup);
		ZipInputStream stream = new ZipInputStream(theFile);

		try {
			ZipEntry entry;
			while ((entry = stream.getNextEntry()) != null) {
				if (! PathFormatter.getExtension(entry.getName()).equals(CCProperties.getInstance().PROP_COVER_TYPE.getValue())) {
					continue;
				}
				
				movielist.getCoverCache().addCover(PathFormatter.getFilenameWithExt(entry.getName()), stream);
			}
		} finally {
			stream.close();
		}
	}
	
	private static String getXMLContentFromBackup(File backup, String filename) throws Exception{
		String content = null;

		InputStream theFile = new FileInputStream(backup);
		ZipInputStream stream = new ZipInputStream(theFile);

		try {
			ZipEntry entry;
			while ((entry = stream.getNextEntry()) != null) {
				if (! entry.getName().equals(filename)) {
					continue;
				}
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8")); //$NON-NLS-1$
				content = SimpleFileUtils.readTextFile(reader);
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
			
			{
				String contentMain = getXMLContentFromBackup(backup, DB_XML_FILENAME_MAIN);
	
				if (contentMain == null) {
					throw new Exception("File not found: " + DB_XML_FILENAME_MAIN); //$NON-NLS-1$
				}

				DatabaseXMLImporter.run(contentMain, movielist::createNewEmptyMovie, movielist::createNewEmptySeries, new ImportOptions(false, false, false, false, false));
			}
						
			{
				String contentGroups = getXMLContentFromBackup(backup, DB_XML_FILENAME_GROUPS);
	
				if (contentGroups != null) {
					CCXMLParser doc = CCXMLParser.parse(contentGroups);
					
					CCXMLElement groups = doc.getRoot("database").getFirstChildOrThrow("groups"); //$NON-NLS-1$
		
					for (CCXMLElement e : groups.getAllChildren("group")) {
						
						String name = e.getAttributeValueOrThrow("name"); //$NON-NLS-1$
						int order = e.getAttributeIntValueOrThrow("ordering"); //$NON-NLS-1$
						String colorStr = e.getAttributeValueOrThrow("color"); //$NON-NLS-1$
						boolean doser = e.getAttributeBoolValueOrThrow("serialize"); //$NON-NLS-1$ //$NON-NLS-2$
						String parent = e.getAttributeValueOrDefault("parent", ""); //$NON-NLS-1$ //$NON-NLS-2$
						boolean visible = e.getAttributeBoolValueOrDefault("visible", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						
						Color color = new Color(
					            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
					            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
					            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
						
						CCGroup group = movielist.getGroupOrNull(name);
						if (group != null) {
							movielist.updateGroup(group, CCGroup.create(group.Name, order, color, doser, parent, visible));
						}
					}
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
		Document xml = DatabaseXMLExporter.export(elements, new ExportOptions(false, false, includeCover));
		
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
	
	private static Tuple3<Integer, CCXMLElement, CCXMLParser> getFirstElementOfExport(String xmlcontent) throws CCXMLException {
		CCXMLParser doc = CCXMLParser.parse(xmlcontent);
		CCXMLElement root = doc.getRoot("database"); //$NON-NLS-1$

		int xmlver = root.getAttributeIntValueOrDefault("xmlversion", 1); //$NON-NLS-1$
		
		List<CCXMLElement> elements = root.getAllChildren(new String[]{"movie", "series"}).enumerate(); //$NON-NLS-1$  //$NON-NLS-2$
		
		if (elements.size() > 0) {
			return Tuple3.Create(xmlver, elements.get(0), doc);
		}
		
		return null;
	}
	
	public static void importSingleElement(CCMovieList movielist, String xmlcontent, ImportOptions opt) throws CCFormatException, CCXMLException, SerializationException {
		Tuple3<Integer, CCXMLElement, CCXMLParser> value = getFirstElementOfExport(xmlcontent);
		
		if (value != null) {
			if (value.Item2.getName().equalsIgnoreCase("movie")) {  //$NON-NLS-1$
				CCMovie mov = movielist.createNewEmptyMovie();
				DatabaseXMLImporter.parseSingleMovie(mov, value.Item2, new ImportState(value.Item3, value.Item1, opt));
			} else if (value.Item2.getName().equalsIgnoreCase("series")) { //$NON-NLS-1$
				CCSeries ser = movielist.createNewEmptySeries();
				DatabaseXMLImporter.parseSingleSeries(ser, value.Item2, new ImportState(value.Item3, value.Item1, opt));
			}
		}
	}
	
	private static CCDBElementTyp getTypOfFirstElementOfExport(String xmlcontent) throws CCXMLException {
		Tuple3<Integer, CCXMLElement, CCXMLParser> value = getFirstElementOfExport(xmlcontent);
		if (value == null) return null;

		if (value.Item2.getName().equalsIgnoreCase("movie")) {  //$NON-NLS-1$
			return CCDBElementTyp.MOVIE;
		} else if (value.Item2.getName().equalsIgnoreCase("series")) { //$NON-NLS-1$
			return CCDBElementTyp.SERIES;
		}
		
		return null;
	}
	
	public static void openSingleElementFile(File f, MainFrame owner, CCMovieList movielist, CCDBElementTyp forceTyp) {
		try {
			String xml = SimpleFileUtils.readUTF8TextFile(f);
			CCDBElementTyp type = ExportHelper.getTypOfFirstElementOfExport(xml);
			if (forceTyp != null && type != forceTyp) {
				CCLog.addError(LocaleBundle.getString("LogMessage.FormatErrorInExport")); //$NON-NLS-1$
				return;
			}
			
			int methodval = 0;
			if (type == CCDBElementTyp.MOVIE)
			{
				methodval = DialogHelper.showLocaleOptions(owner, "ExportHelper.dialogs.importDirect", 1); //$NON-NLS-1$
			}
			
			boolean resetDate = DialogHelper.showLocaleYesNo(owner, "ExportHelper.dialogs.resetDate"); //$NON-NLS-1$
			boolean resetViewed = DialogHelper.showLocaleYesNo(owner, "ExportHelper.dialogs.resetViewed"); //$NON-NLS-1$
			boolean resetScore = DialogHelper.showLocaleYesNo(owner, "ExportHelper.dialogs.resetScore"); //$NON-NLS-1$
			boolean resetTags = DialogHelper.showLocaleYesNo(owner, "ExportHelper.dialogs.resetTags"); //$NON-NLS-1$
			
			if (methodval == 0)  // Direct
			{
				ExportHelper.importSingleElement(movielist, xml, new ImportOptions(resetDate, resetViewed, resetScore, resetTags, false));
			}
			else if (methodval == 1) // Edit & Add
			{
				Tuple3<Integer, CCXMLElement, CCXMLParser> value = ExportHelper.getFirstElementOfExport(xml);
				if (value != null)
				{
					CCMovie tmpMov = new CCMovie(CCMovieList.createStub(), -1);
					tmpMov.setDefaultValues(false);
					DatabaseXMLImporter.parseSingleMovie(tmpMov, value.Item2, new ImportState(value.Item3, value.Item1, new ImportOptions(resetDate, resetViewed, resetScore, false, true)));

					AddMovieFrame amf = new AddMovieFrame(owner, movielist);
					amf.parseFromTemp(tmpMov, resetDate, resetScore);
					amf.setVisible(true);
				}
			}
		} catch (IOException | CCFormatException | SerializationException | CCXMLException e) {
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
			String xml = SimpleFileUtils.readUTF8TextFile(f);
			
			ImportElementsFrame ief = new ImportElementsFrame(owner, xml, movielist);
			ief.setVisible(true);
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}
}