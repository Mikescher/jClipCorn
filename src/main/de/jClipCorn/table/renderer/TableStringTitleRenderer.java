package de.jClipCorn.table.renderer;

public class TableStringTitleRenderer extends TableRenderer {
	private static final long serialVersionUID = -2857849315740108323L;

	public TableStringTitleRenderer() {
		super();
	}

	@Override
    public void setValue(Object value) {
		setText((String)value);
    }

}
