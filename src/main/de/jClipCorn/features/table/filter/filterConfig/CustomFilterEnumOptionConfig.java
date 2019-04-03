package de.jClipCorn.features.table.filter.filterConfig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

public class CustomFilterEnumOptionConfig<T extends ContinoousEnum<T>> extends CustomFilterConfig {

	private final Func0to1<T> valueGetter;
	private final Func1to0<T> valueSetter;
	private final EnumWrapper<T> enumWrapper;
	
	public CustomFilterEnumOptionConfig(Func0to1<T> get, Func1to0<T> set, EnumWrapper<T> wrap) {
		valueGetter = get;
		valueSetter = set;
		enumWrapper = wrap;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		T initial = valueGetter.invoke();
		
		JPanel pnl = new JPanel();
		ButtonGroup rdioBtnGroup = new ButtonGroup();
		
		String[] options = enumWrapper.getList();

		ColumnSpec[] cs = new ColumnSpec[] { ColumnSpec.decode("default:grow") }; //$NON-NLS-1$
		RowSpec[] rs = new RowSpec[1 + options.length*2];
		rs[0] = FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC;
		for (int i = 0; i < options.length; i++) {
			rs[i*2+1] = FormSpecs.DEFAULT_ROWSPEC;
			rs[i*2+2] = FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC;
		}
		
		pnl.setLayout(new FormLayout(cs, rs));

		for (int i = 0; i < options.length; i++) {
			
			final int fi = i;

			JRadioButton btn = new JRadioButton(options[i]);
			btn.setSelected(initial.asInt() == i);
			rdioBtnGroup.add(btn);
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					valueSetter.invoke(enumWrapper.find(fi));
					onChange.run();
				}
			});
			pnl.add(btn, "1, "+(i*2+2)+", fill, default"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return pnl;
	}

	@Override
	public void setValueRandom(Random r) {
		valueSetter.invoke(enumWrapper.randomValue(r));
	}
}
