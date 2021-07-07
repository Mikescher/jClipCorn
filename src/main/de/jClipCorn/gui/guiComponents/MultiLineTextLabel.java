package de.jClipCorn.gui.guiComponents;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.util.Str;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class MultiLineTextLabel extends JPanel {

	private String _text       = Str.Empty;
	private Color  _foreground = new JLabel().getForeground();
	private Color  _background = new JLabel().getBackground();
	private Font   _font       = new JLabel().getFont();

	public MultiLineTextLabel() {
		super(new FormLayout());
	}

	public void setText(String txt) {
		_text = txt;
		refresh();
	}

	public String getText() {
		return _text;
	}

	public void setForeground(Color fg) {
		_foreground = fg;
		refresh();
	}

	public Color getLabelForeground() {
		return _foreground;
	}

	public void setLabelBackground(Color bg) {
		_background = bg;
		refresh();
	}

	public Color getLabelBackground() {
		return _background;
	}

	public void setLabelFont(Font font) {
		_font = font;
		refresh();
	}

	public Font getLabelFont() {
		return _font;
	}

	@SuppressWarnings("nls")
	private void refresh()
	{
		removeAll();

		var lines = (_text == null) ? new String[0] : _text.split("\\r?\\n");

		var spec = new RowSpec[lines.length];
		Arrays.fill(spec, FormSpecs.PREF_ROWSPEC);

		setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("default") }, spec));

		for (int i=0; i < lines.length; i++) add(createLabel(lines[i]), CC.xy(1, i+1));
	}

	private JLabel createLabel(String txt) {
		var lbl = new JLabel(txt);

		lbl.setForeground(getLabelForeground());
		lbl.setBackground(getLabelBackground());
		lbl.setFont(getLabelFont());

		return lbl;
	}
}
