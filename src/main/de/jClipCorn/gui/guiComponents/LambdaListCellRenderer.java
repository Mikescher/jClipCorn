package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.util.lambda.Func1to1;

import javax.swing.*;
import java.awt.*;

public class LambdaListCellRenderer<T> extends DefaultListCellRenderer
{
	private final Func1to1<T, String> _lambda;

	public LambdaListCellRenderer(Func1to1<T, String> lambda)
	{
		_lambda = lambda;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		label.setText(_lambda.invoke((T)value));

		return label;
	}
}
