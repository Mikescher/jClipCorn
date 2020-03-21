package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseComparator {
	@SuppressWarnings("nls")
	public static List<CompareElement> compare(File db1f, File db2f, final ProgressCallbackListener pcl) {
		List<CompareElement> resultlist = new ArrayList<>();
		
		SAXBuilder builder = new SAXBuilder();
		
		Document db1 = null;
		Document db2 = null;
		
		try {
			db1 = builder.build(db1f);
			db2 = builder.build(db2f);
		} catch (JDOMException | IOException e) {
			CCLog.addError(e);
			return null;
		}
		
		Element root1 = db1.getRootElement();
		Element root2 = db2.getRootElement();

		final List<Element> childs1 = root1.getChildren();
		final List<Element> childs2 = root2.getChildren();
		
		pcl.reset();
		pcl.setMax(childs1.size() + childs2.size());
		
		for (int i = 0; i < childs1.size(); i++) {
			CompareElement ce = findInList(childs1.get(i), resultlist);
			
			if (ce == null) {
				ce = new CompareElement(childs1.get(i).getAttributeValue("title"), childs1.get(i).getAttributeValue("zyklus"), Integer.parseInt(childs1.get(i).getAttributeValue("zyklusnumber")), Integer.parseInt(childs1.get(i).getAttributeValue("language")));
				ce.setDB1(childs1.get(i).getAttributeValue("coverhash"), childs1.get(i).getAttributeValue("filehash"), childs1.get(i).getAttributeValue("part_0"), Integer.parseInt(childs1.get(i).getAttributeValue("localid")));
				
				resultlist.add(ce);
			} else {
				ce.setDB1(childs1.get(i).getAttributeValue("coverhash"), childs1.get(i).getAttributeValue("filehash"), childs1.get(i).getAttributeValue("part_0"), Integer.parseInt(childs1.get(i).getAttributeValue("localid")));
			}
			
			pcl.step();
		}
		
		for (int i = 0; i < childs2.size(); i++) {
			CompareElement ce = findInList(childs2.get(i), resultlist);
			
			if (ce == null) {
				ce = new CompareElement(childs2.get(i).getAttributeValue("title"), childs2.get(i).getAttributeValue("zyklus"), Integer.parseInt(childs2.get(i).getAttributeValue("zyklusnumber")), Integer.parseInt(childs2.get(i).getAttributeValue("language")));
				ce.setDB2(childs2.get(i).getAttributeValue("coverhash"), childs2.get(i).getAttributeValue("filehash"), childs2.get(i).getAttributeValue("part_0"), Integer.parseInt(childs2.get(i).getAttributeValue("localid")));
				
				resultlist.add(ce);
			} else {
				ce.setDB2(childs2.get(i).getAttributeValue("coverhash"), childs2.get(i).getAttributeValue("filehash"), childs2.get(i).getAttributeValue("part_0"), Integer.parseInt(childs2.get(i).getAttributeValue("localid")));
			}
			
			pcl.step();
		}
		
		SwingUtils.invokeLater(pcl::reset);
		
		return resultlist;
	}
	
	@SuppressWarnings("nls")
	private static CompareElement findInList(Element e, List<CompareElement> list) {
		for (int i = 0; i < list.size(); i++) {
			CompareElement ce = list.get(i);
			
			String title = e.getAttributeValue("title");
			String zyklus = e.getAttributeValue("zyklus");
			String sZyklusID = e.getAttributeValue("zyklusnumber");
			int zyklusID = Integer.MIN_VALUE;
			if (sZyklusID != null) {
				zyklusID = Integer.parseInt(sZyklusID);
			}
			String sLanguage = e.getAttributeValue("language");
			int language = Integer.MIN_VALUE;
			if (sLanguage != null) {
				language = Integer.parseInt(sLanguage);
			}
			
			
			if (ce.getTitle().equals(title) && ce.getZyklus().getTitle().equals(zyklus) && ce.getZyklus().getNumber() == zyklusID && ce.getLanguage() == language) {
				return list.get(i);
			}
		}
		
		return null;
	}
	
	public static void openFile(File f, Component owner, CCMovieList mlist) {
		new CompareDatabaseFrame(owner, mlist, f).setVisible(true);
	}
}
