package de.jClipCorn.gui.guiComponents.iconComponents;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;

public class CCIcon32CenterButton extends JButton
{
	private Icon32RefLink _value = Icon32RefLink.ICN_GENERIC_ORB_GRAY;

	private final JLabel iconLabel;
	private final JLabel textLabel;

	@SuppressWarnings("nls")
	public CCIcon32CenterButton()
	{
		super();

		iconLabel = new JLabel();
		textLabel = new JLabel();

		iconLabel.setIcon(_value.get());
		textLabel.setText(getText());

		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		textLabel.setHorizontalAlignment(SwingConstants.CENTER);

		this.setLayout(new FormLayout("default:grow", "0dlu:grow, 32px, 0dlu:grow"));

		this.add(iconLabel, CC.xy(1, 2, CC.FILL, CC.FILL));
		this.add(textLabel, CC.xy(1, 3, CC.FILL, CC.FILL));
	}

	public Icon32RefLink getIconRef() {
		return _value;
	}

	public void setIconRef(Icon32RefLink t) {
		_value = t;
		iconLabel.setIcon(t.get());
	}

	@Override
	@Deprecated
	public void setIcon(Icon icon) { /**/ }

	@Override
	@Deprecated
	public void setText(String v) {
		super.setText(v);
	}

	public void setRealText(String v) {
		textLabel.setText(v);
	}

	public String getRealText() {
		return textLabel.getText();
	}

	@Override
	@Deprecated
	public void setFont(java.awt.Font v) {
		super.setFont(v);
	}

	public void setRealFont(java.awt.Font v) {
		textLabel.setFont(v);
	}

	public java.awt.Font getRealFont() {
		return textLabel.getFont();
	}
}
