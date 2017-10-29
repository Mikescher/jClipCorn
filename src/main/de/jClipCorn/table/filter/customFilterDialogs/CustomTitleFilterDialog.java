package de.jClipCorn.table.filter.customFilterDialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.CustomFilterDialog;
import de.jClipCorn.table.filter.customFilter.CustomTitleFilter;
import de.jClipCorn.util.datatypes.StringMatchType;
import de.jClipCorn.util.listener.FinishListener;

public class CustomTitleFilterDialog extends CustomFilterDialog {
	private static final long serialVersionUID = -6822558028101935911L;
	
	private JPanel pnlMiddle;
	private JPanel pnlBottom;
	private JButton btnOk;
	private JLabel lblMovietitle;
	private JRadioButton rdbtnStartswith;
	private JRadioButton rdbtnIncludes;
	private JRadioButton rdbtnEndsWith;
	private JTextField edSearchString;
	private JCheckBox chckbxCaseSensitive;
	private final ButtonGroup rdioBtnGroup = new ButtonGroup();

	public CustomTitleFilterDialog(CustomTitleFilter ft, FinishListener fl, Component parent) {
		super(ft, fl);
		initGUI();
		
		setLocationRelativeTo(parent);
	}
	
	@Override
	protected CustomTitleFilter getFilter() {
		return (CustomTitleFilter) super.getFilter();
	}
	
	private void initGUI() {
		setSize(new Dimension(340, 220));
		
		pnlMiddle = new JPanel();
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		pnlMiddle.setLayout(null);
		
		lblMovietitle = new JLabel(LocaleBundle.getString("FilterTree.Custom.FilterFrames.Title")); //$NON-NLS-1$
		lblMovietitle.setBounds(10, 11, 46, 14);
		pnlMiddle.add(lblMovietitle);
		
		rdbtnStartswith = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.StringSearchType.StartsWith")); //$NON-NLS-1$
		rdioBtnGroup.add(rdbtnStartswith);
		rdbtnStartswith.setSelected(getFilter().getStringMatch() == StringMatchType.SM_STARTSWITH);
		rdbtnStartswith.setBounds(10, 32, 109, 23);
		pnlMiddle.add(rdbtnStartswith);
		
		rdbtnIncludes = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.StringSearchType.Includes")); //$NON-NLS-1$
		rdioBtnGroup.add(rdbtnIncludes);
		rdbtnIncludes.setSelected(getFilter().getStringMatch() == StringMatchType.SM_INCLUDES);
		rdbtnIncludes.setBounds(10, 58, 109, 23);
		pnlMiddle.add(rdbtnIncludes);
		
		rdbtnEndsWith = new JRadioButton(LocaleBundle.getString("FilterTree.Custom.StringSearchType.EndsWith")); //$NON-NLS-1$
		rdioBtnGroup.add(rdbtnEndsWith);
		rdbtnEndsWith.setSelected(getFilter().getStringMatch() == StringMatchType.SM_ENDSWITH);
		rdbtnEndsWith.setBounds(10, 84, 109, 23);
		pnlMiddle.add(rdbtnEndsWith);
		
		edSearchString = new JTextField();
		edSearchString.setText(getFilter().getSearchString());
		edSearchString.setBounds(10, 114, 173, 20);
		pnlMiddle.add(edSearchString);
		edSearchString.setColumns(10);
		
		chckbxCaseSensitive = new JCheckBox(LocaleBundle.getString("FilterTree.Custom.FilterFrames.CaseSensitive")); //$NON-NLS-1$
		chckbxCaseSensitive.setSelected(getFilter().isCaseSensitive());
		chckbxCaseSensitive.setBounds(189, 113, 139, 23);
		pnlMiddle.add(chckbxCaseSensitive);
		
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
	
	@Override
	protected void onAfterOK() {
		if (rdbtnStartswith.isSelected()) getFilter().setStringMatch(StringMatchType.SM_STARTSWITH);
		else if (rdbtnIncludes.isSelected()) getFilter().setStringMatch(StringMatchType.SM_INCLUDES);
		else if (rdbtnEndsWith.isSelected()) getFilter().setStringMatch(StringMatchType.SM_ENDSWITH);
		
		getFilter().setCaseSensitive(chckbxCaseSensitive.isSelected());
		
		getFilter().setSearchString(edSearchString.getText());
	}
}