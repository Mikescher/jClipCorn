package de.jClipCorn.gui.frames.logFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.features.log.CCChangeLogElement;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public class LogChangesTable extends JCCSimpleTable<CCChangeLogElement> {

	private final LogFrame _frame;
	private final CCMovieList _movielist;

	@DesignCreate
	private static LogChangesTable designCreate() { return new LogChangesTable(null, null); }

	public LogChangesTable(LogFrame lf, CCMovieList ml) {
		super(ml);
		_frame     = lf;
		_movielist = ml;
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<CCChangeLogElement> configureColumns() {
		JCCSimpleColumnList<CCChangeLogElement> r = new JCCSimpleColumnList<>(this);

		r.add("@Time")
				.withSize("auto")
				.withText(p -> p.Time.toStringUINormal());
		r.add("@Root.Type")
				.withSize("auto")
				.withText(p -> p.RootType);
		r.add("@Root.ID")
				.withSize("auto")
				.withText(p -> String.valueOf(p.RootID));
		r.add("@Element.Type")
				.withSize("auto")
				.withText(p -> p.ActualType);
		r.add("@Element.ID")
				.withSize("auto")
				.withText(p -> String.valueOf(p.ActualID));
		r.add("@Element")
				.withSize("auto")
				.withText(p -> getElem(p.RootID).mapOrElse(ICCDatabaseStructureElement::getQualifiedTitle, Str.Empty));
		r.add("@Properties")
				.withSize("star,min=auto")
				.withText(p -> "["+CCStreams.iterate(p.Properties).stringjoin(q->q, ", ")+"]");

		return r;
	}

	private Opt<ICCDatabaseStructureElement> getElem(int id) {
		return _movielist.getAny(id);
	}

	@Override
	protected void OnDoubleClickElement(CCChangeLogElement element) {
		var oelem = getElem(element.ActualID);

		if (!oelem.isPresent()) return;

		var elem = oelem.get();

		if (elem instanceof CCMovie)   PreviewMovieFrame.show( _frame, (CCMovie)elem,   true);
		if (elem instanceof CCSeries)  PreviewSeriesFrame.show(_frame, (CCSeries)elem,  true);
		if (elem instanceof CCSeason)  PreviewSeriesFrame.show(_frame, (CCSeason)elem,  true);
		if (elem instanceof CCEpisode) PreviewSeriesFrame.show(_frame, (CCEpisode)elem, true);
	}

	@Override
	protected void OnSelectElement(CCChangeLogElement element) {

	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
