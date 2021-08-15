package de.jClipCorn.features.table.filter.filterConfig;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.DecimalSearchType;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateArea;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import java.util.Random;

public class CustomFilterDateAreaConfig extends CustomFilterConfig {

	private static class CFDAFContainer {
		public Runnable onChange;
		
		public JCCDateSpinner spnLesser;
		public JCCDateSpinner spnGreater;
		public JCCDateSpinner spnBetween1;
		public JCCDateSpinner spnBetween2;
		public JCCDateSpinner spnExactly;

		public JRadioButton rdbtnLesser;
		public JRadioButton rdbtnGreater;
		public JRadioButton rdbtnBetween;
		public JRadioButton rdbtnExactly;
	}
	
	private final Func0to1<CCDateArea> valueGetter;
	private final Func1to0<CCDateArea> valueSetter;
	
	public CustomFilterDateAreaConfig(CCMovieList ml, Func0to1<CCDateArea> get, Func1to0<CCDateArea> set) {
		super(ml);

		valueGetter = get;
		valueSetter = set;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		CCDateArea initial = valueGetter.invoke();
		CFDAFContainer container = new CFDAFContainer();
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

			container.spnLesser = new JCCDateSpinner();
			container.spnLesser.setValue(initial.high);
			container.spnLesser.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnLesser, "6, 2, fill, default"); //$NON-NLS-1$
		}
		
		{
			container.rdbtnGreater = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.DecimalSearchType.Greater")); //$NON-NLS-1$
			container.rdbtnGreater.setSelected(initial.type == DecimalSearchType.GREATER);
			rdioBtnGroup.add(container.rdbtnGreater);
			container.rdbtnGreater.addActionListener(e -> onChanged(container));
			pnl.add(container.rdbtnGreater, "2, 4, fill, default"); //$NON-NLS-1$
			
			container.spnGreater = new JCCDateSpinner();
			container.spnGreater.setValue(initial.low);
			container.spnGreater.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnGreater, "4, 4, fill, default"); //$NON-NLS-1$
		}
		
		{
			container.rdbtnBetween = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.DecimalSearchType.InRange")); //$NON-NLS-1$
			container.rdbtnBetween.setSelected(initial.type == DecimalSearchType.IN_RANGE);
			rdioBtnGroup.add(container.rdbtnBetween);
			container.rdbtnBetween.addActionListener(e -> onChanged(container));
			pnl.add(container.rdbtnBetween, "2, 6, fill, default"); //$NON-NLS-1$

			container.spnBetween1 = new JCCDateSpinner();
			container.spnBetween1.setValue(initial.low);
			container.spnBetween1.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnBetween1, "4, 6, fill, default"); //$NON-NLS-1$
			
			container.spnBetween2 = new JCCDateSpinner();
			container.spnBetween2.setValue(initial.high);
			container.spnBetween2.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnBetween2, "6, 6, fill, default"); //$NON-NLS-1$
		}
		
		{
			container.rdbtnExactly = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.DecimalSearchType.Exactly")); //$NON-NLS-1$
			container.rdbtnExactly.setSelected(initial.type == DecimalSearchType.EXACT);
			rdioBtnGroup.add(container.rdbtnExactly);
			container.rdbtnExactly.addActionListener(e -> onChanged(container));
			pnl.add(container.rdbtnExactly, "2, 8, fill, default"); //$NON-NLS-1$

			container.spnExactly = new JCCDateSpinner();
			container.spnExactly.setValue(initial.low);
			container.spnExactly.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnExactly, "4, 8, fill, default"); //$NON-NLS-1$
		}
		
		return pnl;
	}

	private void onChanged(CFDAFContainer container) {

		CCDateArea old = valueGetter.invoke();
		
		if (container.rdbtnLesser.isSelected()) {
			CCDateArea a = new CCDateArea(old.low, container.spnLesser.getValue(), DecimalSearchType.LESSER);

			valueSetter.invoke(a);
			container.onChange.run();
		} else if (container.rdbtnGreater.isSelected()) {
			CCDateArea a = new CCDateArea(container.spnGreater.getValue(), old.high, DecimalSearchType.GREATER);

			valueSetter.invoke(a);
			container.onChange.run();
		} else if (container.rdbtnBetween.isSelected()) {
			CCDateArea a = new CCDateArea(container.spnBetween1.getValue(), container.spnBetween2.getValue(), DecimalSearchType.IN_RANGE);

			valueSetter.invoke(a);
			container.onChange.run();
		} else if (container.rdbtnExactly.isSelected()) {
			CCDateArea a = new CCDateArea(container.spnExactly.getValue(), old.high, DecimalSearchType.EXACT);

			valueSetter.invoke(a);
			container.onChange.run();
		}

	}

	@Override
	public void setValueRandom(Random r) {
		valueSetter.invoke(new CCDateArea(CCDate.createRandom(r), CCDate.createRandom(r), DecimalSearchType.getWrapper().randomValue(r)));
	}
}
