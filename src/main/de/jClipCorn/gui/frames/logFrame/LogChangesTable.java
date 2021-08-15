package de.jClipCorn.gui.frames.logFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.features.log.CCChangeLogElement;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
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
	protected List<JCCSimpleColumnPrototype<CCChangeLogElement>> configureColumns() {
		List<JCCSimpleColumnPrototype<CCChangeLogElement>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(this, "auto",          "@Time",         p -> p.Time.toStringUINormal(),                                          null, null));
		r.add(new JCCSimpleColumnPrototype<>(this, "auto",          "@Root.Type",    p -> p.RootType,                                                         null, null));
		r.add(new JCCSimpleColumnPrototype<>(this, "auto",          "@Root.ID",      p -> String.valueOf(p.RootID),                                           null, null));
		r.add(new JCCSimpleColumnPrototype<>(this, "auto",          "@Element.Type", p -> p.ActualType,                                                       null, null));
		r.add(new JCCSimpleColumnPrototype<>(this, "auto",          "@Element.ID",   p -> String.valueOf(p.ActualID),                                         null, null));
		r.add(new JCCSimpleColumnPrototype<>(this, "auto",          "@Element",      p -> getElem(p.RootID).mapOrElse(q -> q.getQualifiedTitle(), Str.Empty), null, null));
		r.add(new JCCSimpleColumnPrototype<>(this, "star,min=auto", "@Properties",   p -> "["+CCStreams.iterate(p.Properties).stringjoin(q->q, ", ")+"]",     null, null));

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
