package de.jClipCorn.features.table.renderer;

import de.jClipCorn.util.datatypes.Opt;

import java.awt.Color;

public interface TableModelRowColorInterface {
	Opt<Color> getRowColor(int row);
}
