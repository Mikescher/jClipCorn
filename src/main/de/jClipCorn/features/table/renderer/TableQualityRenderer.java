package de.jClipCorn.features.table.renderer;

public class TableQualityRenderer extends TableRenderer {
	private static final long serialVersionUID = 5357005350195701739L;

	public TableQualityRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText(((CCQuality)value).asString());
		setIcon(((CCQuality)value).getIcon());
    }
}
