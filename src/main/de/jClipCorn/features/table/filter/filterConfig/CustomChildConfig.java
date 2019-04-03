package de.jClipCorn.features.table.filter.filterConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.gui.frames.customFilterEditDialog.CustomFilterEditFilterComboboxRenderer;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

public class CustomChildConfig extends CustomFilterConfig {

	private final Func0to1<AbstractCustomFilter> valueGetter;
	private final Func1to0<AbstractCustomFilter> valueSetter;
	private final String btnText;

	public CustomChildConfig(Func0to1<AbstractCustomFilter> get, Func1to0<AbstractCustomFilter> set, String buttonText) {
		valueGetter = get;
		valueSetter = set;
		btnText     = buttonText;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		JPanel pnl = new JPanel(new FormLayout(
			new ColumnSpec[] 
			{
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
			},
			new RowSpec[] 
			{
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
			}));

		List<AbstractCustomFilter> acffilter = new ArrayList<>();
		acffilter.addAll(Arrays.asList(AbstractCustomFilter.getAllOperatorFilter()));
		acffilter.addAll(Arrays.asList(AbstractCustomFilter.getAllSimpleFilter()));
		
		JComboBox<AbstractCustomFilter> cbxFilter = new JComboBox<>();
		AbstractCustomFilter[] arr = acffilter.toArray(new AbstractCustomFilter[acffilter.size()]);
		cbxFilter.setModel(new DefaultComboBoxModel<>(arr));
		cbxFilter.setRenderer(new CustomFilterEditFilterComboboxRenderer());
		cbxFilter.setSelectedIndex(getIndex(arr, valueGetter.invoke(), 0));
		cbxFilter.setMaximumRowCount(32);
		pnl.add(cbxFilter, "1, 2, fill, default"); //$NON-NLS-1$
		
		JButton btnAddFilter = new JButton(btnText);
		btnAddFilter.addActionListener(e -> { onChanged((AbstractCustomFilter)cbxFilter.getSelectedItem(), onChange); onChange.run(); });
		pnl.add(btnAddFilter, "1, 4"); //$NON-NLS-1$
		
		return pnl;
	}
	
	private int getIndex(AbstractCustomFilter[] arr, AbstractCustomFilter f, int def) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].getClass().equals(f.getClass())) return i;
		}
		return def;
	}

	private void onChanged(AbstractCustomFilter f, Runnable onChange) {
		valueSetter.invoke(f);
		onChange.run();
	}

	@Override
	public void setValueRandom(Random r) {
		// does not work
	}
}
