package de.jClipCorn.gui.guiComponents.onlinescore;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.util.Str;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;

public class OnlineScoreControl extends JPanel {
	private CCOnlineScore value = CCOnlineScore.ZERO_OF_TEN;

	private JSpinner spnNumerator;
	private JSpinner spnDenominator;

	private int _suppressChange = 0;

	public OnlineScoreControl() {
		super();
		init();
		update();
	}

	public OnlineScoreControl(CCOnlineScore v) {
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
				ColumnSpec.decode("default:grow")
			},
			new RowSpec[]
			{
				FormSpecs.DEFAULT_ROWSPEC
			}
		));

		add(spnNumerator   = new JSpinner(), "1, 1, fill, fill");
		add(new JLabel("/"),                 "3, 1, fill, fill");
		add(spnDenominator = new JSpinner(), "5, 1, fill, fill");

		spnNumerator.setModel(new SpinnerNumberModel(0, 0, Short.MAX_VALUE, 1));
		spnDenominator.setModel(new SpinnerNumberModel(10, 0, Short.MAX_VALUE, 1));

		spnNumerator.addChangeListener(this::onChange);
		spnDenominator.addChangeListener(this::onChange);
	}

	private void onChange(ChangeEvent changeEvent) {
		if (_suppressChange > 0) return;

		var n = (short)(int)spnNumerator.getValue();
		var d = (short)(int)spnDenominator.getValue();
		var newval = CCOnlineScore.create(n, d);

		if (!CCOnlineScore.isEqual(value, newval))
		{
			value = newval;
			for (var l: listenerList.getListeners(OnlineScoreChangedListener.class)) l.onlineScoreChanged(new ActionEvent(this, 0, Str.Empty));
		}
	}

	private void update() {
		try {
			_suppressChange++;
			spnNumerator.setValue((int)value.Numerator);
			spnDenominator.setValue((int)value.Denominator);
		} finally {
			_suppressChange--;
		}
	}

	public CCOnlineScore getValue() {
		return value;
	}

	public void setValue(CCOnlineScore v) {
		value = v;
		update();
		for (var l: listenerList.getListeners(OnlineScoreChangedListener.class)) l.onlineScoreChanged(new ActionEvent(this, 0, Str.Empty));
	}

	public void setReadOnly(boolean ro) {
		spnNumerator.setEnabled(!ro);
		spnDenominator.setEnabled(!ro);
	}

	public boolean getReadOnly() {
		return !spnNumerator.isEnabled();
	}

	public void addOnlineScoreChangedListener(final OnlineScoreChangedListener l) {
		listenerList.add(OnlineScoreChangedListener.class, l);
	}
	public void removeOnlineScoreChangedListener(final OnlineScoreChangedListener l) {
		listenerList.add(OnlineScoreChangedListener.class, l);
	}
}
