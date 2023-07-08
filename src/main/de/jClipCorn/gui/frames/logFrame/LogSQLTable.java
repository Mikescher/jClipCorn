package de.jClipCorn.gui.frames.logFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.features.log.CCChangeLogElement;
import de.jClipCorn.features.log.CCSQLLogElement;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.stream.CCStreams;

public class LogSQLTable extends JCCSimpleTable<CCSQLLogElement> {

	private final LogFrame _frame;

	@DesignCreate
	private static LogSQLTable designCreate() { return new LogSQLTable(null, null); }

	public LogSQLTable(LogFrame lf, CCMovieList ml) {
		super(ml);
		_frame = lf;
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<CCSQLLogElement> configureColumns() {
		JCCSimpleColumnList<CCSQLLogElement> r = new JCCSimpleColumnList<>(this);

		r.add("LogFrame.lblQuerySuccess")
				.withSize("auto,min=60")
				.withText(p -> p.error != null ? "ERR" : "OK");

		r.add("LogFrame.lblQueryMethod")
				.withSize("auto,min=80")
				.withText(p -> p.method);

		r.add("LogFrame.lblQueryType")
				.withSize("*,min=80")
				.withText(p -> p.statementType.toString());

		return r;
	}

	@Override
	protected void OnDoubleClickElement(CCSQLLogElement element) {}

	@Override
	protected void OnSelectElement(CCSQLLogElement element) {
		_frame.selectSQLLog(element);
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
