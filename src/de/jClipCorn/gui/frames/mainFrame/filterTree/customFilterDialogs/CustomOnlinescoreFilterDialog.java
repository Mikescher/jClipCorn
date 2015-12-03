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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomOnlinescoreFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.listener.FinishListener;

public class CustomOnlinescoreFilterDialog extends CustomFilterDialog {
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
	private JSpinner spnLesser;
	private JSpinner spnGreater;
	private JSpinner spnBetween1;
	private JSpinner spnBetween2;
	private JSpinner spnExactly;

	public CustomOnlinescoreFilterDialog(CustomOnlinescoreFilter ft, FinishListener fl, Component parent) {
		super(ft, fl);
		initGUI();
		
		initValues();
		
		setLocationRelativeTo(parent);
	}
	
	@Override
	protected CustomOnlinescoreFilter getFilter() {
		return (CustomOnlinescoreFilter) super.getFilter();
	}
	
	private void initGUI() {
		setSize(new Dimension(300, 220));
		
		pnlMiddle = new JPanel();
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		pnlMiddle.setLayout(null);
		
		lblType = new JLabel(LocaleBundle.getString("FilterTree.Custom.FilterFrames.Onlinescore")); //$NON-NLS-1$
		lblType.setBounds(10, 11, 62, 14);
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
		
		spnLesser = new JSpinner();
		spnLesser.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		spnLesser.setBounds(130, 33, 154, 20);
		pnlMiddle.add(spnLesser);
		
		spnGreater = new JSpinner();
		spnGreater.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		spnGreater.setBounds(130, 59, 154, 20);
		pnlMiddle.add(spnGreater);
		
		spnBetween1 = new JSpinner();
		spnBetween1.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		spnBetween1.setBounds(130, 85, 70, 20);
		pnlMiddle.add(spnBetween1);
		
		spnBetween2 = new JSpinner();
		spnBetween2.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		spnBetween2.setBounds(214, 85, 70, 20);
		pnlMiddle.add(spnBetween2);
		
		spnExactly = new JSpinner();
		spnExactly.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		spnExactly.setBounds(130, 111, 154, 20);
		pnlMiddle.add(spnExactly);
		
		pnlBottom = new JPanel();
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		
		btnOk = new JButton(LocaleBundle.getString("AddMovieFrame.btnOK.text")); //$NON-NLS-1$
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
			spnLesser.setValue(getFilter().getHigh().asInt());
			rdbtnLesser.setSelected(true);
			break;
		case GREATER:
			spnGreater.setValue(getFilter().getLow().asInt());
			rdbtnGreater.setSelected(true);
			break;
		case IN_RANGE:
			spnBetween1.setValue(getFilter().getLow().asInt());
			spnBetween2.setValue(getFilter().getHigh().asInt());
			rdbtnBetween.setSelected(true);
			break;
		case EXACT:
			spnExactly.setValue(getFilter().getLow().asInt());
			rdbtnExactly.setSelected(true);
			break;
		}
	}

	@Override
	protected void onAfterOK() {
		if (rdbtnLesser.isSelected()) {
			getFilter().setHigh(CCMovieOnlineScore.find((int) spnLesser.getValue()));
			
			getFilter().setSearchType(DecimalSearchType.LESSER);
		} else if (rdbtnGreater.isSelected()) {
			getFilter().setLow(CCMovieOnlineScore.find((int) spnGreater.getValue()));
			
			getFilter().setSearchType(DecimalSearchType.GREATER);
		} else if (rdbtnBetween.isSelected()) {
			getFilter().setLow(CCMovieOnlineScore.find((int) spnBetween1.getValue()));
			getFilter().setHigh(CCMovieOnlineScore.find((int) spnBetween2.getValue()));
			
			getFilter().setSearchType(DecimalSearchType.IN_RANGE);
		} else if (rdbtnExactly.isSelected()) {
			getFilter().setLow(CCMovieOnlineScore.find((int) spnExactly.getValue()));
			
			getFilter().setSearchType(DecimalSearchType.EXACT);
		}
	}
}