package de.jClipCorn.gui.mainFrame.filterTree;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.table.filter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.features.table.filter.customFilter.operators.CustomOperator;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.SimpleFileUtils;

import java.io.IOException;
import java.util.ArrayList;

public class CustomFilterList extends ArrayList<CustomFilterObject> {
	private static final long serialVersionUID = -1658394481177986689L;

	public static String NAME_TEMPORARY = "{{temporary}}"; //$NON-NLS-1$

	private final CCMovieList movielist;

	public CustomFilterList(CCMovieList ml) {
		movielist = ml;
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	public void save() {
		if (movielist.isReadonly()) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return;
		}
		
		StringBuilder b = new StringBuilder();
		
		for (int i = 0; i < size(); i++) {
			if (i != 0) {
				b.append(SimpleFileUtils.LINE_END);
			}
			
			b.append(get(i).exportToString());
		}
		
		var f = ccprops().PROP_MAINFRAME_FILTERLISTPATH.getValue().toFSPath(movielist);
		
		try {
			f.writeAsUTF8TextFile(b.toString());
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}

	public void load() {
		var f = ccprops().PROP_MAINFRAME_FILTERLISTPATH.getValue().toFSPath(movielist);
		
		if (f.exists()) {
			String txt;
			
			try {
				txt = f.readAsUTF8TextFile();
			} catch (IOException e) {
				CCLog.addError(e);
				return;
			}
			
			String[] lines = SimpleFileUtils.splitLines(txt);
			
			for (int i = 0; i < lines.length; i++) {
				lines[i] = lines[i].trim();
				if (lines[i].isEmpty()) continue;
				
				String[] line = lines[i].split("\t"); //$NON-NLS-1$
				
				if (line.length != 2) {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotParseFilterList", i+1)); //$NON-NLS-1$
					return;
				}
				
				CustomOperator op;
				if ((op = new CustomAndOperator(movielist)).importFromString(line[1])) {
					add(new CustomFilterObject(line[0], op));
				} else {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotParseFilterList", i+1)); //$NON-NLS-1$
				}
			}
		}
		
		CCLog.addDebug(size() + " Filters loaded from File"); //$NON-NLS-1$
	}
}
