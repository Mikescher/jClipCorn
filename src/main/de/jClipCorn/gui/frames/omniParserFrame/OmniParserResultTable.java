package de.jClipCorn.gui.frames.omniParserFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.datatypes.Tuple;

public class OmniParserResultTable extends JCCSimpleTable<Tuple<String, String>> {

	@DesignCreate
	private static OmniParserResultTable designCreate() { return new OmniParserResultTable(null); }

	public OmniParserResultTable(ICCWindow w) {
		super((w==null) ? null : w.getMovieList());
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<Tuple<String, String>> configureColumns() {
		JCCSimpleColumnList<Tuple<String, String>> r = new JCCSimpleColumnList<>(this);

		r.add("OmniParserFrame.Header.title_A")
				.withSize("*,min=auto")
				.withText(p -> p.Item1);
		r.add("OmniParserFrame.Header.title_B")
				.withSize("*,min=auto")
				.withText(p -> p.Item2);

		return r;
	}

	@Override
	protected void OnDoubleClickElement(Tuple<String, String> element) {
		//
	}

	@Override
	protected void OnSelectElement(Tuple<String, String> element) {
		//
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
