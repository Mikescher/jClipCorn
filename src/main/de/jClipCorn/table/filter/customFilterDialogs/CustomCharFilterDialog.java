package de.jClipCorn.table.filter.customFilterDialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.gui.guiComponents.JTextFieldLimit;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.CustomFilterDialog;
import de.jClipCorn.table.filter.customFilter.CustomCharFilter;
import de.jClipCorn.util.listener.FinishListener;

public class CustomCharFilterDialog extends CustomFilterDialog {
	private static final long serialVersionUID = -6822558028101935911L;
	
	private JPanel pnlMiddle;
	private JPanel pnlBottom;
	private JButton btnOk;
	private JTextFieldLimit edSearchString;

	public CustomCharFilterDialog(CustomCharFilter ft, FinishListener fl, Component parent) {
		super(ft, fl);
		initGUI();
		
		setLocationRelativeTo(parent);
	}
	
	@Override
	protected CustomCharFilter getFilter() {
		return (CustomCharFilter) super.getFilter();
	}
	
	private void initGUI() {
		setSize(new Dimension(340, 110));
		
		pnlMiddle = new JPanel();
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		pnlMiddle.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("148px:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("20px:grow"),})); //$NON-NLS-1$
		
		edSearchString = new JTextFieldLimit();
		edSearchString.setMaxLength(1);
		edSearchString.setText(getFilter().getCharset());
		pnlMiddle.add(edSearchString, "1, 1, center, center"); //$NON-NLS-1$
		edSearchString.setColumns(3);
		
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
		if (edSearchString.getText().length() == 0)
			getFilter().setCharset(" "); //$NON-NLS-1$
		else
			getFilter().setCharset(edSearchString.getText().substring(0, 1));
	}
}