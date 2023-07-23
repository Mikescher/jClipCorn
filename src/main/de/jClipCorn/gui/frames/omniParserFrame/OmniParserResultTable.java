package de.jClipCorn.gui.frames.omniParserFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.datatypes.Tuple3;

import javax.swing.*;
import java.awt.*;

public class OmniParserResultTable extends JCCSimpleTable<Tuple3<Boolean, String, String>> {

	@DesignCreate
	private static OmniParserResultTable designCreate() { return new OmniParserResultTable(null); }

	public OmniParserResultTable(ICCWindow w) {
		super((w==null) ? null : w.getMovieList());
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<Tuple3<Boolean, String, String>> configureColumns() {
		JCCSimpleColumnList<Tuple3<Boolean, String, String>> r = new JCCSimpleColumnList<>(this);

		r.add("OmniParserFrame.Header.title_A")
				.withSize("*,min=auto")
				.withText(p -> p.Item2)
				.withForeground(p -> p.Item1 ? UIManager.getColor("TextField.foreground") : Color.RED);

		r.add("OmniParserFrame.Header.title_B")
				.withSize("*,min=auto")
				.withText(p -> p.Item3)
				.withForeground(p -> p.Item1 ? UIManager.getColor("TextField.foreground") : Color.RED);

		return r;
	}

	@Override
	protected void OnDoubleClickElement(Tuple3<Boolean, String, String> element) {
		//
	}

	@Override
	protected void OnSelectElement(Tuple3<Boolean, String, String> element) {
		//
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
