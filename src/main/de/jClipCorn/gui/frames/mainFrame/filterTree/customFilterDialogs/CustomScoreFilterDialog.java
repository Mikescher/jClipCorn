package de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomScoreFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.listener.FinishListener;

public class CustomScoreFilterDialog extends CustomFilterDialog {
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
	private JComboBox<String> cbxLesser;
	private JComboBox<String> cbxBetween1;
	private JComboBox<String> cbxGreater;
	private JComboBox<String> cbxBetween2;
	private JComboBox<String> cbxExactly;

	public CustomScoreFilterDialog(CustomScoreFilter ft, FinishListener fl, Component parent) {
		super(ft, fl);
		initGUI();
		
		initValues();
		
		setLocationRelativeTo(parent);
	}
	
	@Override
	protected CustomScoreFilter getFilter() {
		return (CustomScoreFilter) super.getFilter();
	}
	
	private void initGUI() {
		setSize(new Dimension(400, 220));
		
		pnlMiddle = new JPanel();
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		pnlMiddle.setLayout(null);
		
		lblType = new JLabel(LocaleBundle.getString("FilterTree.Custom.FilterFrames.Score")); //$NON-NLS-1$
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
		
		cbxLesser = new JComboBox<>();
		cbxLesser.setBounds(125, 33, 259, 20);
		pnlMiddle.add(cbxLesser);
		
		cbxBetween1 = new JComboBox<>();
		cbxBetween1.setBounds(125, 85, 125, 20);
		pnlMiddle.add(cbxBetween1);
		
		cbxGreater = new JComboBox<>();
		cbxGreater.setBounds(125, 59, 259, 20);
		pnlMiddle.add(cbxGreater);
		
		cbxBetween2 = new JComboBox<>();
		cbxBetween2.setBounds(259, 85, 125, 20);
		pnlMiddle.add(cbxBetween2);
		
		cbxExactly = new JComboBox<>();
		cbxExactly.setBounds(125, 111, 259, 20);
		pnlMiddle.add(cbxExactly);
		
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
		cbxLesser.setModel(new DefaultComboBoxModel<>(CCMovieScore.getWrapper().getList()));
		cbxGreater.setModel(new DefaultComboBoxModel<>(CCMovieScore.getWrapper().getList()));
		cbxBetween1.setModel(new DefaultComboBoxModel<>(CCMovieScore.getWrapper().getList()));
		cbxBetween2.setModel(new DefaultComboBoxModel<>(CCMovieScore.getWrapper().getList()));
		cbxExactly.setModel(new DefaultComboBoxModel<>(CCMovieScore.getWrapper().getList()));
		
		switch (getFilter().getSearchType()) {
		case LESSER:
			cbxLesser.setSelectedIndex(getFilter().getHigh().asInt());
			rdbtnLesser.setSelected(true);
			break;
		case GREATER:
			cbxGreater.setSelectedIndex(getFilter().getLow().asInt());
			rdbtnGreater.setSelected(true);
			break;
		case IN_RANGE:
			cbxBetween1.setSelectedIndex(getFilter().getLow().asInt());
			cbxBetween2.setSelectedIndex(getFilter().getHigh().asInt());
			rdbtnBetween.setSelected(true);
			break;
		case EXACT:
			cbxExactly.setSelectedIndex(getFilter().getLow().asInt());
			rdbtnExactly.setSelected(true);
			break;
		}
	}

	@Override
	protected void onAfterOK() {
		if (rdbtnLesser.isSelected()) {
			getFilter().setHigh(CCMovieScore.getWrapper().find( cbxLesser.getSelectedIndex()));
			
			getFilter().setSearchType(DecimalSearchType.LESSER);
		} else if (rdbtnGreater.isSelected()) {
			getFilter().setLow(CCMovieScore.getWrapper().find(cbxGreater.getSelectedIndex()));
			
			getFilter().setSearchType(DecimalSearchType.GREATER);
		} else if (rdbtnBetween.isSelected()) {
			getFilter().setLow(CCMovieScore.getWrapper().find(cbxBetween1.getSelectedIndex()));
			getFilter().setHigh(CCMovieScore.getWrapper().find(cbxBetween2.getSelectedIndex()));
			
			getFilter().setSearchType(DecimalSearchType.IN_RANGE);
		} else if (rdbtnExactly.isSelected()) {
			getFilter().setLow(CCMovieScore.getWrapper().find(cbxExactly.getSelectedIndex()));
			
			getFilter().setSearchType(DecimalSearchType.EXACT);
		}
	}
}