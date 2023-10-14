package de.jClipCorn.gui.guiComponents.iconComponents;

import javax.swing.*;

public class CCIcon16Button extends JButton
{
	private Icon16RefLink _value = Icon16RefLink.ICN_GENERIC_ORB_GRAY;

	public CCIcon16Button()
	{
		super(Icon16RefLink.ICN_GENERIC_ORB_GRAY.get());
	}

	public Icon16RefLink getIconRef() {
		return _value;
	}

	public void setIconRef(Icon16RefLink t) {
		_value = t;
		super.setIcon(t.get());
	}

	@Override
	@Deprecated
	public void setIcon(Icon icon) { /**/ }
}
