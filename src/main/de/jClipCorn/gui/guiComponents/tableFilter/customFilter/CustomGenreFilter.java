package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.awt.Component;
import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.guiComponents.tableFilter.AbstractCustomFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.CustomFilterDialog;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilterDialogs.CustomGenreFilterDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.listener.FinishListener;

public class CustomGenreFilter extends AbstractCustomFilter {
	private CCGenre genre = CCGenre.GENRE_000;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return ((CCGenreList) e.getValue(ClipTableModel.COLUMN_GENRE)).includes(genre);
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Genre", genre.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Genre").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public CCGenre getGenre() {
		return genre;
	}

	public void setGenre(CCGenre genre) {
		this.genre = genre;
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_GENRE;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(genre.asInt()+"");
		b.append("]");
		
		return b.toString();
	}
	
	@SuppressWarnings("nls")
	@Override
	public boolean importFromString(String txt) {
		String params = AbstractCustomFilter.getParameterFromExport(txt);
		if (params == null) return false;
		
		String[] paramsplit = params.split(Pattern.quote(","));
		if (paramsplit.length != 1) return false;
		
		int intval;
		try {
			intval = Integer.parseInt(paramsplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		
		CCGenre f = CCGenre.getWrapper().find(intval);
		if (f == null) return false;
		setGenre(f);
		
		return true;
	}

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomGenreFilterDialog(this, fl, parent);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomGenreFilter();
	}

	public static AbstractCustomFilter create(CCGenre data) {
		CustomGenreFilter f = new CustomGenreFilter();
		f.setGenre(data);
		return f;
	}
}
