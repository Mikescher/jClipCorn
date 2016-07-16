package de.jClipCorn.gui.guiComponents.referenceChooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.guiComponents.WideComboBox;
import de.jClipCorn.util.adapter.DocumentAdapter;

public class JReferenceChooser extends JPanel {
	private static final long serialVersionUID = 2696192041815168280L;

	private DefaultComboBoxModel<CCOnlineRefType> cbxModel;

	private WideComboBox<CCOnlineRefType> cbxType;
	private JTextField edID;
	private JPanel panel;
	private JPanel pnlState;
	private JPanel panel_1;

	public JReferenceChooser() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));

		cbxType = new WideComboBox<>();
		cbxType.setPreferredSize(new Dimension(46, 20));
		add(cbxType, BorderLayout.EAST);
		cbxModel = new DefaultComboBoxModel<>(CCOnlineRefType.values());
		cbxType.setModel(cbxModel);
		cbxType.setRenderer(new RefChooserComboBoxRenderer());
		cbxType.setEditor(new RefChooserComboBoxEditor(cbxType));
		cbxType.setEditable(true);

		edID = new JTextField();
		add(edID, BorderLayout.CENTER);
		edID.setColumns(10);

		cbxType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateColor();
			}
		});

		edID.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateColor();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateColor();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateColor();
			}
		});
		
		edID.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateColor();
			}
		});

		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));
		
		panel_1 = new JPanel();
		panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_1.setPreferredSize(new Dimension(16, 16));
		panel_1.setMinimumSize(new Dimension(16, 16));
		panel.add(panel_1, BorderLayout.NORTH);
				panel_1.setLayout(new BorderLayout(0, 0));
		
				pnlState = new JPanel();
				panel_1.add(pnlState);
				pnlState.setBackground(Color.YELLOW);
				pnlState.setBorder(null);
				pnlState.setLayout(new BorderLayout(0, 0));
	}

	public CCOnlineReference getValue() {
		return new CCOnlineReference((CCOnlineRefType) cbxType.getSelectedItem(), edID.getText());
	}

	public void setValue(CCOnlineReference ref) {
		edID.setText(ref.id);
		cbxType.setSelectedItem(ref.type);
	}
	
	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);

		edID.setEnabled(flag);
		cbxType.setEnabled(flag);
	}

	private void updateColor() {
		CCOnlineReference value = getValue();

		if (!value.isValid()) {
			pnlState.setBackground(Color.RED);
		} else if (value.isUnset()) {
			pnlState.setBackground(Color.YELLOW);
		} else {
			pnlState.setBackground(Color.GREEN);
		}
	}
}
