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

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomGroupFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.listener.FinishListener;

public class CustomGroupFilterDialog extends CustomFilterDialog {
	private static final long serialVersionUID = -6822558028101935911L;
	
	private JPanel pnlMiddle;
	private JPanel pnlBottom;
	private JButton btnOk;
	private JComboBox<String> cbxMiddle;

	public CustomGroupFilterDialog(CustomGroupFilter ft, FinishListener fl, Component parent, CCMovieList ml) {
		super(ft, fl);
		initGUI();
		
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

		model.addElement(""); //$NON-NLS-1$
		
		boolean contained = false;
		for (CCGroup g : ml.getGroupList()) {
			model.addElement(g.Name);
			if (g.Name.equals(ft.getGroup())) contained = true;
		}
		if (! ft.getGroup().trim().isEmpty() && !contained) model.insertElementAt(ft.getGroup(), 1);
		
		cbxMiddle.setModel(model);
		cbxMiddle.setSelectedItem(ft.getGroup());
		
		setLocationRelativeTo(parent);
	}
	
	@Override
	protected CustomGroupFilter getFilter() {
		return (CustomGroupFilter) super.getFilter();
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
		getFilter().setGroup((String) cbxMiddle.getSelectedItem());
	}
}