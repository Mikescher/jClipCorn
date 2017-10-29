package de.jClipCorn.table.filter.customFilterDialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.CustomFilterDialog;
import de.jClipCorn.table.filter.customFilter.aggregators.CustomAggregator;
import de.jClipCorn.util.listener.FinishListener;

public class CustomAggregatorFilterDialog extends CustomFilterDialog implements FinishListener {
	private static final long serialVersionUID = -6822558028101935911L;
	
	private JPanel pnlMiddle;
	private JPanel pnlBottom;
	private JButton btnOk;
	private JButton btnEdit;
	private JPanel pnlBottomLeft;

	private final CCMovieList movielist;
	private JComboBox<AbstractCustomFilter> cbxProcessFilter;
	private JLabel lblInfo;
	private JLabel lblHeader;
	
	private AbstractCustomFilter _processorFilter;
	
	public CustomAggregatorFilterDialog(CCMovieList ml, CustomAggregator ag, FinishListener fl, Component parent) {
		super(ag, fl);
		movielist = ml;
		_processorFilter = ag.getProcessingFilter();
		initGUI();
		update();
		
		setLocationRelativeTo(parent);
	}
	
	@Override
	protected CustomAggregator getFilter() {
		return (CustomAggregator) super.getFilter();
	}
	
	private void initGUI() {
		setResizable(true);
		setSize(new Dimension(448, 166));
		
		pnlMiddle = new JPanel();
		getContentPane().add(pnlMiddle, BorderLayout.CENTER);
		pnlMiddle.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("min:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("90dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		btnEdit = new JButton(LocaleBundle.getString("FilterTree.Custom.CustomOperatorFilterDialog.btnEdit.text")); //$NON-NLS-1$
		btnEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onEdit();
			}
		});
		
		lblHeader = new JLabel(""); //$NON-NLS-1$
		pnlMiddle.add(lblHeader, "2, 2, 3, 1"); //$NON-NLS-1$
		
		AbstractCustomFilter[] acffilter = AbstractCustomFilter.getAllEpisodesAndOperatorsFilter();
		
		cbxProcessFilter = new JComboBox<>();
		cbxProcessFilter.setModel(new DefaultComboBoxModel<>(acffilter));
		cbxProcessFilter.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1710057818185541683L;

		    @Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		    	JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		    	comp.setText(((AbstractCustomFilter)value).getPrecreateName());
		        return comp;
		    }});
		
		for (int i = 0; i < acffilter.length; i++) 
			if (acffilter[i].getClass().equals(getFilter().getProcessingFilter().getClass())) 
				cbxProcessFilter.setSelectedIndex(i);
		
		cbxProcessFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});
		pnlMiddle.add(cbxProcessFilter, "2, 4, fill, default"); //$NON-NLS-1$
		pnlMiddle.add(btnEdit, "4, 4"); //$NON-NLS-1$
		
		lblInfo = new JLabel(""); //$NON-NLS-1$
		pnlMiddle.add(lblInfo, "2, 6"); //$NON-NLS-1$
		
		pnlBottom = new JPanel();
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		pnlBottom.setLayout(new BorderLayout(0, 0));
		
		pnlBottomLeft = new JPanel();
		pnlBottom.add(pnlBottomLeft, BorderLayout.CENTER);
		
		btnOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		pnlBottomLeft.add(btnOk);
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
	}
	
	@Override
	protected void onAfterOK() {
		if (! _processorFilter.getClass().equals(cbxProcessFilter.getSelectedItem().getClass())) {
			_processorFilter = ((AbstractCustomFilter)cbxProcessFilter.getSelectedItem()).createNew();

			if (_processorFilter.getClass().equals(getFilter().getProcessingFilter().getClass()))
				_processorFilter = getFilter().getProcessingFilter();
		}
		
		getFilter().setProcessorFilter(_processorFilter);
	}
	
	private void onEdit() {
		if (! _processorFilter.getClass().equals(cbxProcessFilter.getSelectedItem().getClass())) 
			_processorFilter = ((AbstractCustomFilter)cbxProcessFilter.getSelectedItem()).createNew();
		
		CustomFilterDialog dlg = _processorFilter.CreateDialog(this, this, movielist);
		dlg.setVisible(true);
	}
	
	private void update() {
		if (! _processorFilter.getClass().equals(cbxProcessFilter.getSelectedItem().getClass())) {
			_processorFilter = ((AbstractCustomFilter)cbxProcessFilter.getSelectedItem()).createNew();

			if (_processorFilter.getClass().equals(getFilter().getProcessingFilter().getClass()))
				_processorFilter = getFilter().getProcessingFilter();
		}
		
		lblHeader.setText(getFilter().getName());
		lblInfo.setText(_processorFilter.getName());
		
		updateTitle();
	}

	@Override
	public void finish() {
		update();
	}
}
