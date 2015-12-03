package de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomViewedFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.listener.FinishListener;

public class CustomViewedFilterDialog extends CustomFilterDialog {
	private static final long serialVersionUID = -6822558028101935911L;
	
	private JPanel pnlMiddle;
	private JPanel pnlBottom;
	private JButton btnOk;
	private JCheckBox chkbxMiddle;

	public CustomViewedFilterDialog(CustomViewedFilter ft, FinishListener fl, Component parent) {
		super(ft, fl);
		initGUI();
		
		chkbxMiddle.setSelected(ft.getViewed());
		
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
		pnlMiddle.setLayout(null);
		
		chkbxMiddle = new JCheckBox();
		chkbxMiddle.setHorizontalAlignment(SwingConstants.CENTER);
		chkbxMiddle.setBounds(6, 7, 182, 21);
		pnlMiddle.add(chkbxMiddle);
		
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

	@Override
	protected void onAfterOK() {
		getFilter().setViewed(chkbxMiddle.isSelected());
	}
}