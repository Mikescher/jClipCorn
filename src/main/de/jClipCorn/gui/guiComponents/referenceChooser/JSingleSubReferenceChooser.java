package de.jClipCorn.gui.guiComponents.referenceChooser;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.gui.guiComponents.WideComboBox;
import de.jClipCorn.util.adapter.DocumentAdapter;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.http.HTTPUtilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;

public class JSingleSubReferenceChooser extends JPanel {
	private static final long serialVersionUID = 2696192041815168280L;

	private DefaultComboBoxModel<CCOnlineRefType> cbxModel;

	private WideComboBox<CCOnlineRefType> cbxType;
	private JTextField edID;
	private JPanel panel;
	private JPanel pnlState;
	private JPanel panel_1;
	private JTextField edDesc;

	public JSingleSubReferenceChooser() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("20px"), //$NON-NLS-1$
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				ColumnSpec.decode("46px"), //$NON-NLS-1$
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("fill:15dlu"),})); //$NON-NLS-1$

		cbxType = new WideComboBox<>();
		cbxType.setPreferredSize(new Dimension(46, 20));
		add(cbxType, "3, 1, left, fill"); //$NON-NLS-1$
		cbxModel = new DefaultComboBoxModel<>(CCOnlineRefType.values());
		cbxType.setModel(cbxModel);
		cbxType.setRenderer(new RefChooserComboBoxRenderer());
		cbxType.setEditor(new RefChooserComboBoxEditor(cbxType));
		cbxType.setEditable(true);
		cbxType.setMaximumRowCount(16);

		edID = new JTextField();
		add(edID, "2, 1, fill, fill"); //$NON-NLS-1$
		edID.setColumns(10);
		edID.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_V) && ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0)) {
                    CCOnlineRefType guess = CCOnlineRefType.guessType(edID.getText());
                    if (guess != null && guess != cbxType.getSelectedItem()) {
                    	cbxType.setSelectedItem(guess);
                    	return;
                    }
                    
                    Tuple<CCOnlineRefType, String> extraction = CCOnlineRefType.extractType(edID.getText());
                    if (extraction != null) {
                    	if (extraction.Item1 != cbxType.getSelectedItem()) cbxType.setSelectedItem(extraction.Item1);
                    	if (! edID.getText().equals(extraction.Item2)) edID.setText(extraction.Item2);
                    	return;
                    }
                }
            }
        });

		cbxType.addActionListener(e -> updateUIControls());

		edID.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateUIControls();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateUIControls();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateUIControls();
			}
		});
		
		edID.addActionListener(e -> updateUIControls());

		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBorder(new EmptyBorder(2, 2, 2, 2));
		add(panel, "1, 1, left, fill"); //$NON-NLS-1$
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
		pnlState.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getClickCount() == 2) {
					CCSingleOnlineReference v = getValue();
					if (v.isSet() && v.isValid()) HTTPUtilities.openInBrowser(v.getURL());
				}
			}
		});
		
		edDesc = new JTextField();
		add(edDesc, "4, 1, fill, fill"); //$NON-NLS-1$
		edDesc.setColumns(10);
	}

	public CCSingleOnlineReference getValue() {
		return new CCSingleOnlineReference((CCOnlineRefType) cbxType.getSelectedItem(), edID.getText(), edDesc.getText());
	}

	public void setValue(CCSingleOnlineReference ref) {
		edID.setText(ref.id);
		cbxType.setSelectedItem(ref.type);
		edDesc.setText(ref.description);
	}
	
	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);

		edID.setEnabled(flag);
		cbxType.setEnabled(flag);
		edDesc.setEnabled(flag);
	}

	public void updateUIControls() {
		CCSingleOnlineReference value = getValue();

		if (!value.isValid()) {
			pnlState.setBackground(Color.RED);
		} else if (value.isUnset()) {
			pnlState.setBackground(Color.YELLOW);
		} else {
			pnlState.setBackground(Color.GREEN);
		}
	}
}
