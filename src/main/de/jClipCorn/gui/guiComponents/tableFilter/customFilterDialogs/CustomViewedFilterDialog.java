package de.jClipCorn.gui.guiComponents.tableFilter.customFilterDialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.gui.guiComponents.tableFilter.CustomFilterDialog;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomViewedFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.listener.FinishListener;

public class CustomViewedFilterDialog extends CustomFilterDialog {
	private static final long serialVersionUID = -6822558028101935911L;
	
	private JPanel pnlMiddle;
	private JPanel pnlBottom;
	private JButton btnOk;
	private JComboBox<String> cbxState;

	public CustomViewedFilterDialog(CustomViewedFilter ft, FinishListener fl, Component parent) {
		super(ft, fl);
		initGUI();
		
		cbxState.setModel(new DefaultComboBoxModel<>(ExtendedViewedStateType.getWrapper().getList()));
		cbxState.setSelectedIndex(ft.getState().asInt());
		
		setLocationRelativeTo(parent);
	}
	
	@Override
	protected CustomViewedFilter getFilter() {
		return (CustomViewedFilter) super.getFilter();
	}
	
	private void initGUI() {
		setSize(new Dimension(200, 95));
		
		pnlMiddle = new JPanel();
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		pnlMiddle.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("31px:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("25px:grow"),})); //$NON-NLS-1$
		
		cbxState = new JComboBox<>();
		pnlMiddle.add(cbxState, "1, 1, default, center"); //$NON-NLS-1$
		
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
		getFilter().setState(ExtendedViewedStateType.getWrapper().find(cbxState.getSelectedIndex()));
	}
}