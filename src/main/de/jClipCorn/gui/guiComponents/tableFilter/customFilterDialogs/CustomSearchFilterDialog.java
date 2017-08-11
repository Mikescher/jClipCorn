package de.jClipCorn.gui.guiComponents.tableFilter.customFilterDialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.jClipCorn.gui.guiComponents.tableFilter.CustomFilterDialog;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomSearchFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.listener.FinishListener;

public class CustomSearchFilterDialog extends CustomFilterDialog {
	private static final long serialVersionUID = -6822558028101935911L;
	
	private JPanel pnlMiddle;
	private JPanel pnlBottom;
	private JButton btnOk;
	private JTextField edSearchString;

	public CustomSearchFilterDialog(CustomSearchFilter ft, FinishListener fl, Component parent) {
		super(ft, fl);
		initGUI();
		
		setLocationRelativeTo(parent);
	}
	
	@Override
	protected CustomSearchFilter getFilter() {
		return (CustomSearchFilter) super.getFilter();
	}
	
	private void initGUI() {
		setSize(new Dimension(340, 100));
		
		pnlMiddle = new JPanel();
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		pnlMiddle.setLayout(null);
		
		edSearchString = new JTextField();
		edSearchString.setText(getFilter().getSearchTerm());
		edSearchString.setBounds(12, 12, 310, 20);
		pnlMiddle.add(edSearchString);
		edSearchString.setColumns(10);
		
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
		getFilter().setSearchTerm(edSearchString.getText());
	}
}