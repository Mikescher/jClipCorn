package de.jClipCorn.gui.guiComponents.tableRenderer;

public class TableEpisodeRenderer extends TableRenderer {
	private static final long serialVersionUID = 1L;

	public TableEpisodeRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(value.toString());
    }
}
