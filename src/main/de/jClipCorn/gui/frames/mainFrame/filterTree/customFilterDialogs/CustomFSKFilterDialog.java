package de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomFSKFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.listener.FinishListener;

public class CustomFSKFilterDialog extends CustomFilterDialog {
	private static final long serialVersionUID = -6822558028101935911L;
	
	private JPanel pnlMiddle;
	private JPanel pnlBottom;
	private JButton btnOk;
	private JComboBox<String> cbxMiddle;

	public CustomFSKFilterDialog(CustomFSKFilter ft, FinishListener fl, Component parent) {
		super(ft, fl);
		initGUI();
		
		cbxMiddle.setModel(new DefaultComboBoxModel<>(CCMovieFSK.getWrapper().getList()));
		cbxMiddle.setSelectedIndex(ft.getFSK().asInt());
		
		setLocationRelativeTo(parent);
	}
	
	@Override
	protected CustomFSKFilter getFilter() {
		return (CustomFSKFilter) super.getFilter();
	}
	
	private void initGUI() {
		setSize(new Dimension(300, 105));
		
		pnlMiddle = new JPanel();
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		pnlMiddle.setLayout(null);
		
		cbxMiddle = new JComboBox<>();
		cbxMiddle.setBounds(10, 11, 274, 20);
		pnlMiddle.add(cbxMiddle);
		
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
		getFilter().setFSK(CCMovieFSK.getWrapper().find(cbxMiddle.getSelectedIndex()));
	}
}