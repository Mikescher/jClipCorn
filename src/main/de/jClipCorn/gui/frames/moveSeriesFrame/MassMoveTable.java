package de.jClipCorn.gui.frames.moveSeriesFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class MassMoveTable extends JCCSimpleTable<MassMoveEntry> {

	@DesignCreate
	private static MassMoveTable designCreate() { return new MassMoveTable(new MoveSeriesDialog(new JFrame(), new CCSeries(CCMovieList.createStub(), 0)), false, false); }

	public MassMoveTable(@NotNull ICCWindow frame, boolean showMovCol, boolean showSerCol) {
		super(frame, Tuple.Create(showMovCol, showSerCol));
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<MassMoveEntry> configureColumns() {
		JCCSimpleColumnList<MassMoveEntry> r = new JCCSimpleColumnList<>(this);

		if (this.<Tuple<Boolean, Boolean>>initOptions().Item1) {
			r.add("MoveSeriesFrame.Table.HeaderMovie")
					.withSize("auto")
					.withText(e -> Opt.cast(e.entry, CCMovie.class).map(CCMovie::getCompleteTitle).orElse("???"))
					.withBackground(e -> Color.WHITE)
					.withForeground(e -> Color.BLACK);
		}

		if (this.<Tuple<Boolean, Boolean>>initOptions().Item2) {
			r.add("MoveSeriesFrame.Table.HeaderSeries")
					.withSize("auto")
					.withText(e -> Opt.cast(e.entry, CCEpisode.class).map(CCEpisode::getSeries).map(p -> p.Title.get()).orElse("???"))
					.withBackground(e -> Color.WHITE)
					.withForeground(e -> Color.BLACK);

			r.add("MoveSeriesFrame.Table.HeaderEpisode")
					.withSize("auto")
					.withText(e -> Opt.cast(e.entry, CCEpisode.class).map(CCEpisode::getStringIdentifier).orElse("???"))
					.withBackground(e -> Color.WHITE)
					.withForeground(e -> Color.BLACK);
		}

		r.add("MoveSeriesFrame.Table.HeaderOld")
				.withSize("*")
				.withText(e -> e.PathOld.toString())
				.withBackground(e -> e.OldIsValid ? Color.GREEN : Color.RED)
				.withForeground(e -> Color.BLACK);

		r.add("MoveSeriesFrame.Table.HeaderNew")
				.withSize("*")
				.withText(e -> e.PathNew.toString())
				.withBackground(e -> e.NewIsValid ? Color.GREEN : Color.RED)
				.withForeground(e -> Color.BLACK);

		return r;
	}

	@Override
	protected void OnDoubleClickElement(MassMoveEntry element) {
		// nothing
	}

	@Override
	protected void OnSelectElement(MassMoveEntry element) {
		//
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}

}
