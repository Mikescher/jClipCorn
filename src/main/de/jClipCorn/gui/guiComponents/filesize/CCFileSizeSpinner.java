package de.jClipCorn.gui.guiComponents.filesize;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.FileSizeFormatter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;

public class CCFileSizeSpinner extends JPanel {
	private CCFileSize value = CCFileSize.ZERO;

	private JSpinner spinner;
	private JLabel   display;

	public CCFileSizeSpinner() {
		super();
		init();
		update();
	}

	public CCFileSizeSpinner(CCFileSize v) {
		super();
		value = v;
		init();
		update();
	}

	@SuppressWarnings("nls")
	private void init() {
		setLayout(new FormLayout
		(
			new ColumnSpec[]
			{
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("[20dlu,default]")
			},
			new RowSpec[]
			{
				FormSpecs.DEFAULT_ROWSPEC
			}
		));

		add(spinner = new JSpinner(),        "1, 1, fill, fill");
		add(new JLabel("Bytes"),             "3, 1, fill, fill");
		add(new JLabel("="),                 "5, 1, fill, fill");
		add(display = new JLabel(Str.Empty), "7, 1, fill, fill");

		spinner.setModel(new SpinnerNumberModel(0L, 0L, null, 1L));

		spinner.addChangeListener(this::onChange);
	}

	private void onChange(ChangeEvent changeEvent) {
		var newval = new CCFileSize((long)spinner.getValue());

		if (!CCFileSize.isEqual(value, newval))
		{
			value = newval;
			for (var l: listenerList.getListeners(FileSizeChangedListener.class)) l.fileSizeChanged(new ActionEvent(this, 0, Str.Empty));
		}

		display.setText(FileSizeFormatter.format(value));
	}

	private void update() {
		spinner.setValue(value.getBytes());

		display.setText(FileSizeFormatter.format(value));
	}

	public CCFileSize getValue() {
		return value;
	}

	public void setValue(CCFileSize v) {
		value = v;
		update();
		for (var l: listenerList.getListeners(FileSizeChangedListener.class)) l.fileSizeChanged(new ActionEvent(this, 0, Str.Empty));
	}

	public void setReadOnly(boolean ro) {
		spinner.setEnabled(!ro);
	}

	public boolean getReadOnly() {
		return !spinner.isEnabled();
	}

	public void addFileSizeChangedListener(final FileSizeChangedListener l) {
		listenerList.add(FileSizeChangedListener.class, l);
	}
	public void removeFileSizeChangedListener(final FileSizeChangedListener l) {
		listenerList.add(FileSizeChangedListener.class, l);
	}
}
