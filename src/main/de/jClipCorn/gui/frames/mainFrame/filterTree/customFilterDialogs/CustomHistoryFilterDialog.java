package de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs;

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
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomHistoryFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomHistoryFilter.CustomHistoryFilterType;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.listener.FinishListener;

public class CustomHistoryFilterDialog extends CustomFilterDialog {
	private static final long serialVersionUID = -6822558028101935911L;
	
	private JPanel pnlMiddle;
	private JPanel pnlBottom;
	private JButton btnOk;
	private final ButtonGroup rdioBtnGroup = new ButtonGroup();
	private JLabel lblType;
	private JRadioButton edbtnContains;
	private JRadioButton rdbtnContainsNot;
	private JRadioButton rdbtnBetween;
	private JRadioButton rdbtnNotBetween;
	private JCCDateSpinner edContains;
	private JCCDateSpinner edBetween1;
	private JCCDateSpinner edContainsNot;
	private JCCDateSpinner edBetween2;
	private JCCDateSpinner edNotBetween1;
	private JCCDateSpinner edNotBetween2;

	public CustomHistoryFilterDialog(CustomHistoryFilter ft, FinishListener fl, Component parent) {
		super(ft, fl);
		initGUI();
		
		initValues();
		
		setLocationRelativeTo(parent);
	}
	
	@Override
	protected CustomHistoryFilter getFilter() {
		return (CustomHistoryFilter) super.getFilter();
	}
	
	private void initGUI() {
		setSize(new Dimension(500, 220));
		
		pnlMiddle = new JPanel();
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		pnlMiddle.setLayout(null);
		
		lblType = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblHistory.text")); //$NON-NLS-1$
		lblType.setBounds(10, 11, 62, 14);
		pnlMiddle.add(lblType);
		
		edbtnContains = new JRadioButton("enthält"); //$NON-NLS-1$
		edbtnContains.setSelected(true);
		rdioBtnGroup.add(edbtnContains);
		edbtnContains.setBounds(10, 32, 205, 23);
		pnlMiddle.add(edbtnContains);
		
		rdbtnContainsNot = new JRadioButton("enthält nicht"); //$NON-NLS-1$
		rdioBtnGroup.add(rdbtnContainsNot);
		rdbtnContainsNot.setBounds(10, 58, 205, 23);
		pnlMiddle.add(rdbtnContainsNot);
		
		rdbtnBetween = new JRadioButton("enthält Wert zwischen"); //$NON-NLS-1$
		rdioBtnGroup.add(rdbtnBetween);
		rdbtnBetween.setBounds(10, 84, 205, 23);
		pnlMiddle.add(rdbtnBetween);
		
		rdbtnNotBetween = new JRadioButton("enthält kein Wert zwischen"); //$NON-NLS-1$
		rdioBtnGroup.add(rdbtnNotBetween);
		rdbtnNotBetween.setBounds(10, 110, 205, 23);
		pnlMiddle.add(rdbtnNotBetween);
		
		edContains = new JCCDateSpinner();
		edContains.setBounds(223, 32, 259, 20);
		pnlMiddle.add(edContains);
		
		edBetween1 = new JCCDateSpinner();
		edBetween1.setBounds(223, 84, 125, 20);
		pnlMiddle.add(edBetween1);
		
		edContainsNot = new JCCDateSpinner();
		edContainsNot.setBounds(223, 58, 259, 20);
		pnlMiddle.add(edContainsNot);
		
		edBetween2 = new JCCDateSpinner();
		edBetween2.setBounds(357, 84, 125, 20);
		pnlMiddle.add(edBetween2);
		
		edNotBetween1 = new JCCDateSpinner();
		edNotBetween1.setBounds(223, 110, 125, 20);
		pnlMiddle.add(edNotBetween1);
		
		edNotBetween2 = new JCCDateSpinner();
		edNotBetween2.setBounds(357, 110, 125, 20);
		pnlMiddle.add(edNotBetween2);
		
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
		edContains.setValue(getFilter().First);
		edContainsNot.setValue(getFilter().First);

		edBetween1.setValue(getFilter().First);
		edBetween2.setValue(getFilter().Second);

		edNotBetween1.setValue(getFilter().First);
		edNotBetween2.setValue(getFilter().Second);
	}

	@Override
	protected void onAfterOK() {
		if (edbtnContains.isSelected()) {
			getFilter().Type = CustomHistoryFilterType.CONTAINS;
			getFilter().First = edContains.getValue();
			getFilter().Second = CCDate.getMinimumDate();
		} else if (rdbtnContainsNot.isSelected()) {
			getFilter().Type = CustomHistoryFilterType.CONTAINS_NOT;
			getFilter().First = edContainsNot.getValue();
			getFilter().Second = CCDate.getMinimumDate();
		} else if (rdbtnBetween.isSelected()) {
			getFilter().Type = CustomHistoryFilterType.CONTAINS_BETWEEN;
			getFilter().First = edBetween1.getValue();
			getFilter().Second = edBetween2.getValue();
		} else if (rdbtnNotBetween.isSelected()) {
			getFilter().Type = CustomHistoryFilterType.CONTAINS_NOT_BETWEEEN;
			getFilter().First = edNotBetween1.getValue();
			getFilter().Second = edNotBetween2.getValue();
		}
	}
}