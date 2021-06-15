package de.jClipCorn.features.table.filter.filterConfig;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.CCIntArea;
import de.jClipCorn.util.datatypes.DecimalSearchType;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import java.util.Random;

public class CustomFilterIntAreaConfig extends CustomFilterConfig {

	private class CFIAFContainer {
		public Runnable onChange;
		
		public JSpinner spnLesser;
		public JSpinner spnGreater;
		public JSpinner spnBetween1;
		public JSpinner spnBetween2;
		public JSpinner spnExactly;

		public JRadioButton rdbtnLesser;
		public JRadioButton rdbtnGreater;
		public JRadioButton rdbtnBetween;
		public JRadioButton rdbtnExactly;
	}
	
	private final Func0to1<CCIntArea> valueGetter;
	private final Func1to0<CCIntArea> valueSetter;

	private final Comparable<?> minimum;
	private final Comparable<?> maximum;
	
	public CustomFilterIntAreaConfig(CCMovieList ml, Func0to1<CCIntArea> get, Func1to0<CCIntArea> set, Comparable<?> min, Comparable<?> max) {
		super(ml);

		valueGetter = get;
		valueSetter = set;

		minimum = min;
		maximum = max;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		CCIntArea initial = valueGetter.invoke();
		CFIAFContainer container = new CFIAFContainer();
		container.onChange = onChange;
		
		JPanel pnl = new JPanel();
		ButtonGroup rdioBtnGroup = new ButtonGroup();
		
		pnl.setLayout(new FormLayout(
				new ColumnSpec[] 
				{
					FormSpecs.UNRELATED_GAP_COLSPEC,
					FormSpecs.PREF_COLSPEC,
					FormSpecs.UNRELATED_GAP_COLSPEC,
					ColumnSpec.decode("default:grow"), //$NON-NLS-1$
					FormSpecs.UNRELATED_GAP_COLSPEC,
					ColumnSpec.decode("default:grow"), //$NON-NLS-1$
					FormSpecs.UNRELATED_GAP_COLSPEC,
				},
				new RowSpec[] 
				{
					FormSpecs.RELATED_GAP_ROWSPEC,
					FormSpecs.PREF_ROWSPEC,
					FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
					FormSpecs.PREF_ROWSPEC,
					FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
					FormSpecs.PREF_ROWSPEC,
					FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
					FormSpecs.PREF_ROWSPEC,
				}));
		
		{
			container.rdbtnLesser = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.DecimalSearchType.Less")); //$NON-NLS-1$
			container.rdbtnLesser.setSelected(initial.type == DecimalSearchType.LESSER);
			rdioBtnGroup.add(container.rdbtnLesser);
			container.rdbtnLesser.addActionListener(e -> onChanged(container));
			pnl.add(container.rdbtnLesser, "2, 2, fill, default"); //$NON-NLS-1$

			container.spnLesser = createSpinner(initial.high);
			container.spnLesser.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnLesser, "6, 2, fill, default"); //$NON-NLS-1$
		}
		
		{
			container.rdbtnGreater = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.DecimalSearchType.Greater")); //$NON-NLS-1$
			container.rdbtnGreater.setSelected(initial.type == DecimalSearchType.GREATER);
			rdioBtnGroup.add(container.rdbtnGreater);
			container.rdbtnGreater.addActionListener(e -> onChanged(container));
			pnl.add(container.rdbtnGreater, "2, 4, fill, default"); //$NON-NLS-1$
			
			container.spnGreater = createSpinner(initial.low);
			container.spnGreater.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnGreater, "4, 4, fill, default"); //$NON-NLS-1$
		}
		
		{
			container.rdbtnBetween = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.DecimalSearchType.InRange")); //$NON-NLS-1$
			container.rdbtnBetween.setSelected(initial.type == DecimalSearchType.IN_RANGE);
			rdioBtnGroup.add(container.rdbtnBetween);
			container.rdbtnBetween.addActionListener(e -> onChanged(container));
			pnl.add(container.rdbtnBetween, "2, 6, fill, default"); //$NON-NLS-1$

			container.spnBetween1 = createSpinner(initial.low);
			container.spnBetween1.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnBetween1, "4, 6, fill, default"); //$NON-NLS-1$
			
			container.spnBetween2 = createSpinner(initial.high);
			container.spnBetween2.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnBetween2, "6, 6, fill, default"); //$NON-NLS-1$
		}
		
		{
			container.rdbtnExactly = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.DecimalSearchType.Exactly")); //$NON-NLS-1$
			container.rdbtnExactly.setSelected(initial.type == DecimalSearchType.EXACT);
			rdioBtnGroup.add(container.rdbtnExactly);
			container.rdbtnExactly.addActionListener(e -> onChanged(container));
			pnl.add(container.rdbtnExactly, "2, 8, fill, default"); //$NON-NLS-1$

			container.spnExactly = createSpinner(initial.low);
			container.spnExactly.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnExactly, "4, 8, fill, default"); //$NON-NLS-1$
		}
		
		return pnl;
	}
	
	private JSpinner createSpinner(int value) {
		JSpinner s = new JSpinner();
		
		if (maximum != null && maximum instanceof Integer && value > (Integer)maximum) value = (Integer)maximum;
		if (minimum != null && minimum instanceof Integer && value < (Integer)minimum) value = (Integer)minimum;
		
		s.setModel(new SpinnerNumberModel(value, minimum, maximum, 1));
		s.setEditor(new JSpinner.NumberEditor(s, "0")); //$NON-NLS-1$
		s.setValue(value);
		return s;
	}

	private void onChanged(CFIAFContainer container) {

		CCIntArea old = valueGetter.invoke();
		
		if (container.rdbtnLesser.isSelected()) {
			CCIntArea a = new CCIntArea(old.low, (int) container.spnLesser.getValue(), DecimalSearchType.LESSER);

			valueSetter.invoke(a);
			container.onChange.run();
		} else if (container.rdbtnGreater.isSelected()) {
			CCIntArea a = new CCIntArea((int) container.spnGreater.getValue(), old.high, DecimalSearchType.GREATER);

			valueSetter.invoke(a);
			container.onChange.run();
		} else if (container.rdbtnBetween.isSelected()) {
			CCIntArea a = new CCIntArea((int) container.spnBetween1.getValue(), (int) container.spnBetween2.getValue(), DecimalSearchType.IN_RANGE);

			valueSetter.invoke(a);
			container.onChange.run();
		} else if (container.rdbtnExactly.isSelected()) {
			CCIntArea a = new CCIntArea((int) container.spnExactly.getValue(), old.high, DecimalSearchType.EXACT);

			valueSetter.invoke(a);
			container.onChange.run();
		}

	}

	@Override
	public void setValueRandom(Random r) {
		int a = rand(r);
		int b = rand(r);
		if (a>b) { int t=b; b=a; a=t; }
		valueSetter.invoke(new CCIntArea(a, b, DecimalSearchType.getWrapper().randomValue(r)));
	}
	
	private int rand(Random r) {
		if (maximum != null && maximum instanceof Integer && minimum != null && minimum instanceof Integer) return (Integer)minimum + r.nextInt((Integer)maximum - (Integer)minimum);
		if (minimum != null && minimum instanceof Integer) return (Integer)minimum + r.nextInt(Integer.MAX_VALUE - (Integer)minimum);
		if (maximum != null && maximum instanceof Integer) return r.nextInt((Integer)maximum);
		return r.nextInt();
	}
}
