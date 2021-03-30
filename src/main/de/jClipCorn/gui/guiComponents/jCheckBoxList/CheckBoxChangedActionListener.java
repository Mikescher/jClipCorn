package de.jClipCorn.gui.guiComponents.jCheckBoxList;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.EventListener;

public interface CheckBoxChangedActionListener<T> extends EventListener {

	class CheckBoxChangedEvent<T> extends ActionEvent {

		public final JCheckBox Checkbox;
		public final T Data;
		public final boolean NewValue;

		public CheckBoxChangedEvent(Object source, int id, String command, T d, JCheckBox c, boolean v) {
			super(source, id, command);
			Data = d;
			Checkbox = c;
			NewValue = v;
		}
	}

	void actionPerformed(CheckBoxChangedEvent<T> e);
}
