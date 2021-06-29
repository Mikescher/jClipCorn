package de.jClipCorn.gui.frames._template;

import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.gui.resources.Resources;

import javax.swing.*;
import java.awt.*;

public class Template extends JFrame
{
	public Template(Component owner)
	{
		super();

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle("TODO"); //$NON-NLS-1$
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

		//======== this ========
		setTitle("<dynamic>"); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default:grow, $ugap")); //$NON-NLS-1$
		setSize(650, 500);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
