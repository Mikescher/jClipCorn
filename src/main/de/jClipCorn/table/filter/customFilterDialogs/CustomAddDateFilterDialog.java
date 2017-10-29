package de.jClipCorn.table.filter.customFilterDialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.CustomFilterDialog;
import de.jClipCorn.table.filter.customFilter.CustomAddDateFilter;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.listener.FinishListener;

public class CustomAddDateFilterDialog extends CustomFilterDialog {
	private static final long serialVersionUID = -6822558028101935911L;
	
	private JPanel pnlMiddle;
	private JPanel pnlBottom;
	private JButton btnOk;
	private final ButtonGroup rdioBtnGroup = new ButtonGroup();
	private JLabel lblType;
	private JRadioButton rdbtnLesser;
	private JRadioButton rdbtnGreater;
	private JRadioButton rdbtnBetween;
	private JRadioButton rdbtnExactly;
	private JCCDateSpinner spnLesser;
	private JCCDateSpinner spnGreater;
	private JCCDateSpinner spnBetween1;
	private JCCDateSpinner spnBetween2;
	private JCCDateSpinner spnExactly;

	public CustomAddDateFilterDialog(CustomAddDateFilter ft, FinishListener fl, Component parent) {
		super(ft, fl);
		initGUI();
		
		initValues();
		
		setLocationRelativeTo(parent);
	}
	
	@Override
	protected CustomAddDateFilter getFilter() {
		return (CustomAddDateFilter) super.getFilter();
	}
	
	private void initGUI() {
		setSize(new Dimension(400, 220));
		
		pnlMiddle = new JPanel();
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		pnlMiddle.setLayout(null);
		
		lblType = new JLabel(LocaleBundle.getString("FilterTree.Custom.FilterFrames.AddDate")); //$NON-NLS-1$
		lblType.setBounds(10, 11, 372, 14);
		pnlMiddle.add(lblType);
		
		rdbtnLesser = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.DecimalSearchType.Less")); //$NON-NLS-1$
		rdbtnLesser.setSelected(true);
		rdioBtnGroup.add(rdbtnLesser);
		rdbtnLesser.setBounds(10, 32, 109, 23);
		pnlMiddle.add(rdbtnLesser);
		
		rdbtnGreater = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.DecimalSearchType.Greater")); //$NON-NLS-1$
		rdioBtnGroup.add(rdbtnGreater);
		rdbtnGreater.setBounds(10, 58, 109, 23);
		pnlMiddle.add(rdbtnGreater);
		
		rdbtnBetween = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.DecimalSearchType.InRange")); //$NON-NLS-1$
		rdioBtnGroup.add(rdbtnBetween);
		rdbtnBetween.setBounds(10, 84, 109, 23);
		pnlMiddle.add(rdbtnBetween);
		
		rdbtnExactly = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.DecimalSearchType.Exactly")); //$NON-NLS-1$
		rdioBtnGroup.add(rdbtnExactly);
		rdbtnExactly.setBounds(10, 110, 109, 23);
		pnlMiddle.add(rdbtnExactly);
		
		spnLesser = new JCCDateSpinner();
		spnLesser.setBounds(130, 33, 252, 20);
		pnlMiddle.add(spnLesser);
		
		spnGreater = new JCCDateSpinner();
		spnGreater.setBounds(130, 59, 252, 20);
		pnlMiddle.add(spnGreater);
		
		spnBetween1 = new JCCDateSpinner();
		spnBetween1.setBounds(130, 85, 123, 20);
		pnlMiddle.add(spnBetween1);
		
		spnBetween2 = new JCCDateSpinner();
		spnBetween2.setBounds(265, 84, 117, 20);
		pnlMiddle.add(spnBetween2);
		
		spnExactly = new JCCDateSpinner();
		spnExactly.setBounds(130, 111, 252, 20);
		pnlMiddle.add(spnExactly);
		
		pnlBottom = new JPanel();
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		
		btnOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		pnlBottom.add(btnOk);
	}
	
	private void initValues() {
		switch (getFilter().getSearchType()) {
		case LESSER:
			spnLesser.setValue(getFilter().getHigh());
			rdbtnLesser.setSelected(true);
			break;
		case GREATER:
			spnGreater.setValue(getFilter().getLow());
			rdbtnGreater.setSelected(true);
			break;
		case IN_RANGE:
			spnBetween1.setValue(getFilter().getLow());
			spnBetween2.setValue(getFilter().getHigh());
			rdbtnBetween.setSelected(true);
			break;
		case EXACT:
			spnExactly.setValue(getFilter().getLow());
			rdbtnExactly.setSelected(true);
			break;
		}
	}

	@Override
	protected void onAfterOK() {
		if (rdbtnLesser.isSelected()) {
			getFilter().setHigh(spnLesser.getValue());
			
			getFilter().setSearchType(DecimalSearchType.LESSER);
		} else if (rdbtnGreater.isSelected()) {
			getFilter().setLow(spnGreater.getValue());
			
			getFilter().setSearchType(DecimalSearchType.GREATER);
		} else if (rdbtnBetween.isSelected()) {
			getFilter().setLow(spnBetween1.getValue());
			getFilter().setHigh(spnBetween2.getValue());
			
			getFilter().setSearchType(DecimalSearchType.IN_RANGE);
		} else if (rdbtnExactly.isSelected()) {
			getFilter().setLow(spnExactly.getValue());
			
			getFilter().setSearchType(DecimalSearchType.EXACT);
		}
	}
}