package de.jClipCorn.properties.property;

import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.properties.enumerations.AppTheme;

import javax.swing.*;
import java.awt.*;

public class CCLookAndFeelProperty extends CCEnumProperty<AppTheme> {

	public CCLookAndFeelProperty(CCPropertyCategory cat, CCProperties prop, String ident, AppTheme standard) {
		super(cat, prop, ident, standard, AppTheme.getWrapper());
	}

	@Override
	public Component getSecondaryComponent(final Component firstComponent) {
		JButton b = new JButton("->"); //$NON-NLS-1$
		b.addActionListener(evt->
		{
			var at = getComponentValue(firstComponent);
			LookAndFeelManager.setLookAndFeel(at, true);
		});
		return b;
	}
}
