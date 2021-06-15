package de.jClipCorn.features.table.filter.filterConfig;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateSearchParameter;
import de.jClipCorn.util.datetime.CCDateSearchParameter.DateSearchType;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import java.util.Random;

public class CustomFilterDateSearchConfig extends CustomFilterConfig {

	private class CFDAFContainer {
		public Runnable onChange;
		
		public JCCDateSpinner spnContains;
		public JCCDateSpinner spnContainsNot;
		public JCCDateSpinner spnContainsBetween1;
		public JCCDateSpinner spnContainsBetween2;
		public JCCDateSpinner spnContainsNotBetween1;
		public JCCDateSpinner spnContainsNotBetween2;
		public JCCDateSpinner spnContainsOnlyBetween1;
		public JCCDateSpinner spnContainsOnlyBetween2;

		public JRadioButton rdbtnContains;
		public JRadioButton rdbtnContainsNot;
		public JRadioButton rdbtnContainsBetween;
		public JRadioButton rdbtnContainsNotBetween;
		public JRadioButton rdbtnContainsOnlyBetween;
	}
	
	private final Func0to1<CCDateSearchParameter> valueGetter;
	private final Func1to0<CCDateSearchParameter> valueSetter;
	
	public CustomFilterDateSearchConfig(CCMovieList ml, Func0to1<CCDateSearchParameter> get, Func1to0<CCDateSearchParameter> set) {
		super(ml);

		valueGetter = get;
		valueSetter = set;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {

		CCDateSearchParameter initial = valueGetter.invoke();
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
					FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
					FormSpecs.PREF_ROWSPEC,
					FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
					FormSpecs.PREF_ROWSPEC,
				}));
		
		{
			container.rdbtnContains = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.Range.Contains")); //$NON-NLS-1$
			container.rdbtnContains.setSelected(initial.Type == DateSearchType.CONTAINS);
			rdioBtnGroup.add(container.rdbtnContains);
			container.rdbtnContains.addActionListener(e -> onChanged(container));
			pnl.add(container.rdbtnContains, "2, 2, fill, default"); //$NON-NLS-1$

			container.spnContains = new JCCDateSpinner();
			container.spnContains.setValue(initial.First);
			container.spnContains.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnContains, "4, 2, 3, 1, fill, default"); //$NON-NLS-1$
		}
		
		{
			container.rdbtnContainsNot = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.Range.ContainsNot")); //$NON-NLS-1$
			container.rdbtnContainsNot.setSelected(initial.Type == DateSearchType.CONTAINS_NOT);
			rdioBtnGroup.add(container.rdbtnContainsNot);
			container.rdbtnContainsNot.addActionListener(e -> onChanged(container));
			pnl.add(container.rdbtnContainsNot, "2, 4, fill, default"); //$NON-NLS-1$
			
			container.spnContainsNot = new JCCDateSpinner();
			container.spnContainsNot.setValue(initial.First);
			container.spnContainsNot.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnContainsNot, "4, 4, 3, 1, fill, default"); //$NON-NLS-1$
		}
		
		{
			container.rdbtnContainsBetween = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.Range.ContainsValueBetween")); //$NON-NLS-1$
			container.rdbtnContainsBetween.setSelected(initial.Type == DateSearchType.CONTAINS_BETWEEN);
			rdioBtnGroup.add(container.rdbtnContainsBetween);
			container.rdbtnContainsBetween.addActionListener(e -> onChanged(container));
			pnl.add(container.rdbtnContainsBetween, "2, 6, fill, default"); //$NON-NLS-1$

			container.spnContainsBetween1 = new JCCDateSpinner();
			container.spnContainsBetween1.setValue(initial.First);
			container.spnContainsBetween1.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnContainsBetween1, "4, 6, fill, default"); //$NON-NLS-1$
			
			container.spnContainsBetween2 = new JCCDateSpinner();
			container.spnContainsBetween2.setValue(initial.Second);
			container.spnContainsBetween2.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnContainsBetween2, "6, 6, fill, default"); //$NON-NLS-1$
		}
		
		{
			container.rdbtnContainsNotBetween = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.Range.ContainsNoValueBetween")); //$NON-NLS-1$
			container.rdbtnContainsNotBetween.setSelected(initial.Type == DateSearchType.CONTAINS_NOT_BETWEEEN);
			rdioBtnGroup.add(container.rdbtnContainsNotBetween);
			container.rdbtnContainsNotBetween.addActionListener(e -> onChanged(container));
			pnl.add(container.rdbtnContainsNotBetween, "2, 8, fill, default"); //$NON-NLS-1$

			container.spnContainsNotBetween1 = new JCCDateSpinner();
			container.spnContainsNotBetween1.setValue(initial.First);
			container.spnContainsNotBetween1.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnContainsNotBetween1, "4, 8, fill, default"); //$NON-NLS-1$
			
			container.spnContainsNotBetween2 = new JCCDateSpinner();
			container.spnContainsNotBetween2.setValue(initial.Second);
			container.spnContainsNotBetween2.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnContainsNotBetween2, "6, 8, fill, default"); //$NON-NLS-1$
		}
		
		{
			container.rdbtnContainsOnlyBetween = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.Range.ContainsOnlyValuesBetween")); //$NON-NLS-1$
			container.rdbtnContainsOnlyBetween.setSelected(initial.Type == DateSearchType.CONTAINS_ONLY_BETWEEN);
			rdioBtnGroup.add(container.rdbtnContainsOnlyBetween);
			container.rdbtnContainsOnlyBetween.addActionListener(e -> onChanged(container));
			pnl.add(container.rdbtnContainsOnlyBetween, "2, 10, fill, default"); //$NON-NLS-1$

			container.spnContainsOnlyBetween1 = new JCCDateSpinner();
			container.spnContainsOnlyBetween1.setValue(initial.First);
			container.spnContainsOnlyBetween1.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnContainsOnlyBetween1, "4, 10, fill, default"); //$NON-NLS-1$
			
			container.spnContainsOnlyBetween2 = new JCCDateSpinner();
			container.spnContainsOnlyBetween2.setValue(initial.Second);
			container.spnContainsOnlyBetween2.addChangeListener((e) -> onChanged(container));
			pnl.add(container.spnContainsOnlyBetween2, "6, 10, fill, default"); //$NON-NLS-1$
		}
		
		return pnl;
	}

	private void onChanged(CFDAFContainer container) {

		CCDateSearchParameter old = valueGetter.invoke();
		
		if (container.rdbtnContains.isSelected()) {
			
			CCDateSearchParameter a = new CCDateSearchParameter(container.spnContains.getValue(), old.Second, DateSearchType.CONTAINS);

			valueSetter.invoke(a);
			container.onChange.run();
			
		} else if (container.rdbtnContainsNot.isSelected()) {

			CCDateSearchParameter a = new CCDateSearchParameter(container.spnContainsNot.getValue(), old.Second, DateSearchType.CONTAINS_NOT);

			valueSetter.invoke(a);
			container.onChange.run();
			
		} else if (container.rdbtnContainsBetween.isSelected()) {

			CCDateSearchParameter a = new CCDateSearchParameter(container.spnContainsBetween1.getValue(), container.spnContainsBetween2.getValue(), DateSearchType.CONTAINS_BETWEEN);

			valueSetter.invoke(a);
			container.onChange.run();
			
		} else if (container.rdbtnContainsNotBetween.isSelected()) {

			CCDateSearchParameter a = new CCDateSearchParameter(container.spnContainsNotBetween1.getValue(), container.spnContainsNotBetween2.getValue(), DateSearchType.CONTAINS_NOT_BETWEEEN);

			valueSetter.invoke(a);
			container.onChange.run();
			
		} else if (container.rdbtnContainsOnlyBetween.isSelected()) {

			CCDateSearchParameter a = new CCDateSearchParameter(container.spnContainsOnlyBetween1.getValue(), container.spnContainsOnlyBetween2.getValue(), DateSearchType.CONTAINS_ONLY_BETWEEN);

			valueSetter.invoke(a);
			container.onChange.run();
			
		}

	}

	@Override
	public void setValueRandom(Random r) {
		valueSetter.invoke(new CCDateSearchParameter(CCDate.createRandom(r), CCDate.createRandom(r), DateSearchType.getWrapper().randomValue(r)));
	}
}
