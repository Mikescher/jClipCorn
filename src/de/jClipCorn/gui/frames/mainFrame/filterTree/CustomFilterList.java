package de.jClipCorn.gui.frames.mainFrame.filterTree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.jClipCorn.Main;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomOperator;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.TextFileUtils;

public class CustomFilterList extends ArrayList<CustomFilterObject> {
	private static final long serialVersionUID = -1658394481177986689L;

	public void save() {
		if (CCProperties.getInstance().ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return;
		}
		
		StringBuilder b = new StringBuilder();
		
		for (int i = 0; i < size(); i++) {
			if (i != 0) {
				b.append("\r\n"); //$NON-NLS-1$
			}
			
			b.append(get(i).exportToString());
		}
		
		File f = new File(PathFormatter.getAbsolute(CCProperties.getInstance().PROP_MAINFRAME_FILTERLISTPATH.getValue()));
		
		try {
			TextFileUtils.writeTextFile(f, b.toString());
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}

	public void load() {
		File f = new File(PathFormatter.getAbsolute(CCProperties.getInstance().PROP_MAINFRAME_FILTERLISTPATH.getValue()));
		
		if (f.exists()) {
			String txt;
			
			try {
				txt = TextFileUtils.readTextFile(f);
			} catch (IOException e) {
				CCLog.addError(e);
				return;
			}
			
			String[] lines = txt.split("\r\n"); //$NON-NLS-1$
			
			for (int i = 0; i < lines.length; i++) {
				lines[i] = lines[i].trim();
				if (lines[i].isEmpty()) continue;
				
				String[] line = lines[i].split("\t"); //$NON-NLS-1$
				
				if (line.length != 2) {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotParseFilterList", i)); //$NON-NLS-1$
					return;
				}
				
				CustomOperator op;
				if ((op = new CustomAndOperator()).importFromString(line[1])) {
					add(new CustomFilterObject(line[0], op));
				} else {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotParseFilterList", i)); //$NON-NLS-1$
					return;
				}
			}
		}
		
		if (Main.DEBUG) System.out.println("[DBG] " + size() + " Filters loaded from File"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
