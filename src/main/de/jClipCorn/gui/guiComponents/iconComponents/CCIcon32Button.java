package de.jClipCorn.gui.guiComponents.iconComponents;

import javax.swing.*;

public class CCIcon32Button extends JButton
{
	private Icon32RefLink _value = Icon32RefLink.ICN_GENERIC_ORB_GRAY;

	public CCIcon32Button()
	{
		super(Icon32RefLink.ICN_GENERIC_ORB_GRAY.get());
	}

	public Icon32RefLink getIconRef() {
		return _value;
	}

	public void setIconRef(Icon32RefLink t) {
		_value = t;
		super.setIcon(t.get());
	}

	@Override
	@Deprecated
	public void setIcon(Icon icon) { /**/ }
}
