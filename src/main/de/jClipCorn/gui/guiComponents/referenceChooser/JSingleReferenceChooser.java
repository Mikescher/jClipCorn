package de.jClipCorn.gui.guiComponents.referenceChooser;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.gui.guiComponents.WideComboBox;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.DocumentAdapter;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.http.HTTPUtilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.*;

public class JSingleReferenceChooser extends JPanel {
	private static final long serialVersionUID = 2696192041815168280L;

	private DefaultComboBoxModel<CCOnlineRefType> cbxModel;

	private WideComboBox<CCOnlineRefType> cbxType;
	private JTextField edID;
	private JPanel panel;
	private JPanel pnlState;
	private JPanel panel_1;

	private final CCMovieList movielist;

	@DesignCreate
	private static JSingleReferenceChooser designCreate() { return new JSingleReferenceChooser(null); }

	public JSingleReferenceChooser(CCMovieList ml) {
		super();
		movielist = ml;
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
		cbxType.setMaximumRowCount(16);

		edID = new JTextField();
		add(edID, BorderLayout.CENTER);
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
		panel.setBackground(new Color(0, 0, 0, 0));
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
		pnlState.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if (e.getClickCount() == 2) {
					CCSingleOnlineReference v = getValue();
					if (v.isSet() && v.isValid()) HTTPUtilities.openInBrowser(v.getURL(movielist.ccprops()));
				}
			}
		});
	}

	public CCSingleOnlineReference getValue() {
		return new CCSingleOnlineReference((CCOnlineRefType) cbxType.getSelectedItem(), edID.getText(), Str.Empty);
	}

	public void setValue(CCSingleOnlineReference ref) {
		edID.setText(ref.id);
		cbxType.setSelectedItem(ref.type);
	}
	
	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);

		edID.setEnabled(flag);
		cbxType.setEnabled(flag);
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
