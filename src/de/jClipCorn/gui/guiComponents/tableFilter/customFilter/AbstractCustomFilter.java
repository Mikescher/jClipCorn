package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomNandOperator;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomNorOperator;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomOrOperator;

public abstract class AbstractCustomFilter {
	public final static int CUSTOMFILTERID_AND = 0;
	public final static int CUSTOMFILTERID_NAND = 1;
	public final static int CUSTOMFILTERID_NOR = 2;
	public final static int CUSTOMFILTERID_OR = 3;
	public final static int CUSTOMFILTERID_FORMAT = 4;
	public final static int CUSTOMFILTERID_FSK = 5;
	public final static int CUSTOMFILTERID_GENRE = 6;
	public final static int CUSTOMFILTERID_LANGUAGE = 7;
	public final static int CUSTOMFILTERID_ONLINESCORE = 8;
	public final static int CUSTOMFILTERID_QUALITY = 9;
	public final static int CUSTOMFILTERID_SCORE = 10;
	public final static int CUSTOMFILTERID_TAG = 11;
	public final static int CUSTOMFILTERID_TITLE = 12;
	public final static int CUSTOMFILTERID_TYP = 13;
	public final static int CUSTOMFILTERID_VIEWED = 14;
	public final static int CUSTOMFILTERID_YEAR = 15;
	public final static int CUSTOMFILTERID_ZYKLUS = 16;
	
	public abstract boolean include(Entry<? extends ClipTableModel, ? extends Object> e);
	
	public abstract String getName();
	
	public abstract int getID();
	public abstract String exportToString();
	public abstract boolean importFromString(String txt);
	
	public static String escape(String txt) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < txt.length(); i++) {
			char c = txt.charAt(i);
			
			if (c == '|' || c == '[' || c == ']' || c == ',' || c == '&') {
				builder.append('&');
			}
			builder.append(c);
		}
		
		return builder.toString();
	}
	
	public static String descape(String txt) {
		StringBuilder builder = new StringBuilder();
		
		boolean escape = false;
		for (int i = 0; i < txt.length(); i++) {
			boolean skip = false;
			char c = txt.charAt(i);
			
			if (escape) {
				escape = false;
			} else if (c == '&') {
				escape = true;
				skip = true;
			}
			
			if (! skip) {
				builder.append(c);
			}
		}
		
		return builder.toString();
	}
	
	@SuppressWarnings("nls")
	public static int getIDFromExport(String txt) {
		if (txt.length() < 4) return -1;
		txt = txt.substring(1, txt.length() - 1);
		String[] split = txt.split(Pattern.quote("|"));
		if (split.length < 2) return -1;
		try {
			return Integer.parseInt(split[0]);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public static String getParameterFromExport(String txt) {
		if (txt.length() < 4) return null;
		txt = txt.substring(1, txt.length() - 1);
		int pos = txt.indexOf('|');
		if (pos > 3 || pos < 1) return null;
		return txt.substring(pos + 1);
	}
	
	public static String[] splitParameterFromExport(String txt) {
		List<String> resultlist = new ArrayList<>();
		
		StringBuilder builder = new StringBuilder();
		
		int depth = 0;
		boolean escape = false;
		for (int i = 0; i < txt.length(); i++) {
			boolean skip = false;
			char c = txt.charAt(i);
			
			if (escape) {
				escape = false;
			} else {
				if (c == '&') {
					escape = true;
				} else if (c == '[') {
					depth++;
				} else if (c == ']') {
					depth--;
				} else if (c == ',') {
					if (depth == 0) {
						skip = true;
						resultlist.add(builder.toString());
						builder = new StringBuilder(); // clear
					}
				}
			}
			
			if (! skip) {
				builder.append(c);
			}
		}
		if (builder.length() > 0) {
			resultlist.add(builder.toString());
		}
		
		return resultlist.toArray(new String[resultlist.size()]);
	}
	
	public static AbstractCustomFilter getFilterByID(int id) {
		switch (id) {
		case CUSTOMFILTERID_AND:
			return new CustomAndOperator();
		case CUSTOMFILTERID_NAND:
			return new CustomNandOperator();
		case CUSTOMFILTERID_NOR:
			return new CustomNorOperator();
		case CUSTOMFILTERID_OR:
			return new CustomOrOperator();
		case CUSTOMFILTERID_FORMAT:
			return new CustomFormatFilter();
		case CUSTOMFILTERID_FSK:
			return new CustomFSKFilter();
		case CUSTOMFILTERID_GENRE:
			return new CustomGenreFilter();
		case CUSTOMFILTERID_LANGUAGE:
			return new CustomLanguageFilter();
		case CUSTOMFILTERID_ONLINESCORE:
			return new CustomOnlinescoreFilter();
		case CUSTOMFILTERID_QUALITY:
			return new CustomQualityFilter();
		case CUSTOMFILTERID_SCORE:
			return new CustomScoreFilter();
		case CUSTOMFILTERID_TAG:
			return new CustomTagFilter();
		case CUSTOMFILTERID_TITLE:
			return new CustomTitleFilter();
		case CUSTOMFILTERID_TYP:
			return new CustomTypFilter();
		case CUSTOMFILTERID_VIEWED:
			return new CustomViewedFilter();
		case CUSTOMFILTERID_YEAR:
			return new CustomYearFilter();
		case CUSTOMFILTERID_ZYKLUS:
			return new CustomZyklusFilter();
		default:
			return null;
		}
	}
	
	public static AbstractCustomFilter createFilterFromExport(String txt) {
		int id = getIDFromExport(txt);
		if (id < 0) return null;
		
		AbstractCustomFilter f = getFilterByID(id);
		if (f == null) return null;
		
		if (f.importFromString(txt)) {
			return f;
		} else {
			return null;
		}
	}
}