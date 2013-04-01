package de.jClipCorn.gui.frames.compareDatabaseFrame;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.jClipCorn.gui.log.CCLog;

public class DatabaseComparator {
	@SuppressWarnings("nls")
	public static ArrayList<CompareElement> compare(File db1f, File db2f, final JProgressBar prog) {
		ArrayList<CompareElement> resultlist = new ArrayList<>();
		
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
		Element root2 = db1.getRootElement();

		final List<Element> childs1 = root1.getChildren();
		final List<Element> childs2 = root2.getChildren();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				prog.setValue(0);
				prog.setMaximum(childs1.size() + childs2.size());
			}
		});
		
		for (int i = 0; i < childs1.size(); i++) {
			CompareElement ce = findInList(childs1.get(i), resultlist);
			
			if (ce == null) {
				ce = new CompareElement(childs1.get(i).getAttributeValue("title"), childs1.get(i).getAttributeValue("zyklus"), Integer.parseInt(childs1.get(i).getAttributeValue("zyklusnumber")));
				ce.setDB1(childs1.get(i).getAttributeValue("coverhash"), childs1.get(i).getAttributeValue("filehash"));
				
				resultlist.add(ce);
			} else {
				ce.setDB1(childs1.get(i).getAttributeValue("coverhash"), childs1.get(i).getAttributeValue("filehash"));
			}
			
			stepBar(prog);
		}
		
		for (int i = 0; i < childs2.size(); i++) {
			CompareElement ce = findInList(childs2.get(i), resultlist);
			
			if (ce == null) {
				ce = new CompareElement(childs2.get(i).getAttributeValue("title"), childs2.get(i).getAttributeValue("zyklus"), Integer.parseInt(childs2.get(i).getAttributeValue("zyklusnumber")));
				ce.setDB2(childs2.get(i).getAttributeValue("coverhash"), childs2.get(i).getAttributeValue("filehash"));
				
				resultlist.add(ce);
			} else {
				ce.setDB2(childs2.get(i).getAttributeValue("coverhash"), childs2.get(i).getAttributeValue("filehash"));
			}
			
			stepBar(prog);
		}
		
		return resultlist;
	}
	
	@SuppressWarnings("nls")
	private static CompareElement findInList(Element e, ArrayList<CompareElement> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getTitle().equals(e.getAttributeValue("title")) && list.get(i).getZyklus().getTitle().equals(e.getAttributeValue("zyklus")) && list.get(i).getZyklus().getNumber() == Integer.parseInt(e.getAttributeValue("zyklusnumber"))) {
				return list.get(i);
			}
		}
		
		return null;
	}
	
	private static void stepBar(final JProgressBar p) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				p.setValue(p.getValue() + 1);
			}
		});
	}
}
