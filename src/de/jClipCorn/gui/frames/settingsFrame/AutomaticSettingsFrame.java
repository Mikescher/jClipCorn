package de.jClipCorn.gui.frames.settingsFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.Main;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.extendedSettingsFrame.ExtendedSettingsFrame;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCPathProperty;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.util.DialogHelper;
import de.jClipCorn.util.FileChooserHelper;
import de.jClipCorn.util.Validator;

public abstract class AutomaticSettingsFrame extends JFrame {
	private static final long serialVersionUID = 4681197289662529891L;

	private final CCProperties properties;
	private final MainFrame owner;
	
	private JTabbedPane tpnlSettings;
	private JPanel pnlBottom;
	private JButton btnOk;
	private JButton btnCancel;
	private JButton btnErweitert;
	
	private int pnlCount = 0;
	private int rowCount = 10;

	private ArrayList<PropertyElement> elements = new ArrayList<>();
	
	public AutomaticSettingsFrame(MainFrame owner, CCProperties properties){
		super();
		this.properties = properties;
		this.owner = owner;
	}

	protected void init() {
		initGUI();
		
		setLocationRelativeTo(owner);
		
		setValues();
		
		pack();
	}
	
	private void initGUI() {
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		
		setSize(new Dimension(525, 440));
		setMinimumSize(new Dimension(525, 440));
		setTitle(LocaleBundle.getString("Settingsframe.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		tpnlSettings = new JTabbedPane(JTabbedPane.TOP);
		tpnlSettings.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		getContentPane().add(tpnlSettings, BorderLayout.CENTER);
		
		for (int i = 0; i < pnlCount; i++) {
			initPanel(i);
		}

		pnlBottom = new JPanel();
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		pnlBottom.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.BUTTON_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("23px"), //$NON-NLS-1$
				FormFactory.LINE_GAP_ROWSPEC,}));
		
		btnOk = new JButton(LocaleBundle.getString("Settingsframe.btnOK.title")); //$NON-NLS-1$
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (! Main.DEBUG) {
					DialogHelper.showInformation(AutomaticSettingsFrame.this, LocaleBundle.getString("Settingsframe.informationDlg.caption"), LocaleBundle.getString("Settingsframe.informationDlg.text")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				okValues();
				
				dispose();
			}
		});
		pnlBottom.add(btnOk, "4, 2, fill, top"); //$NON-NLS-1$
		
		btnCancel = new JButton(LocaleBundle.getString("Settingsframe.btnCancel.title")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		pnlBottom.add(btnCancel, "6, 2, fill, top"); //$NON-NLS-1$
		
		btnErweitert = new JButton(LocaleBundle.getString("Settingsframe.btnExtended.title")); //$NON-NLS-1$
		btnErweitert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				(new ExtendedSettingsFrame(AutomaticSettingsFrame.this, properties)).setVisible(true);
				dispose();
			}
		});
		pnlBottom.add(btnErweitert, "10, 2"); //$NON-NLS-1$
	}

	private void initPanel(int pnlNumber) {
		JPanel pnlTab = new JPanel();
		tpnlSettings.addTab(LocaleBundle.getString("Settingsframe.tabbedCpt.Caption_" + pnlNumber), null, pnlTab, null); //$NON-NLS-1$
		
		pnlTab.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("25dlu"), //$NON-NLS-1$
				ColumnSpec.decode("25dlu"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC},
			getRowSpec()));

		int c = 1;
		for (final CCProperty<Object> p : properties.getPropertyList()) {
			if (p.getCategory() == pnlNumber) {
				JLabel info = new JLabel(LocaleBundle.getString("Settingsframe.tabbedPnl." + p.getIdentifier())); //$NON-NLS-1$
				pnlTab.add(info, "2, " + c*2 + ", right, default"); //$NON-NLS-1$ //$NON-NLS-2$

				final Component comp = p.getComponent();
				pnlTab.add(comp, "4, " + c*2 + ", fill, default"); //$NON-NLS-1$ //$NON-NLS-2$
				
				if (((CCProperty<?>)p) instanceof CCPathProperty) {
					JButton btnChoose = new JButton("..."); //$NON-NLS-1$
					pnlTab.add(btnChoose, "6, " + c*2 + ", right, default"); //$NON-NLS-1$ //$NON-NLS-2$
					btnChoose.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							final String end = ((CCPathProperty) ((CCProperty<?>) p)).getFilterEnding();
							JFileChooser vc = new JFileChooser();
							vc.setFileFilter(FileChooserHelper.createFullValidateFileFilter("Filter: " + end, new Validator<String>() { //$NON-NLS-1$
										@Override
										public boolean validate(String val) {
											return StringUtils.endsWithIgnoreCase(val, end);
										}
									}));
							vc.setDialogTitle(LocaleBundle.getString("Settingsframe.dlg.title")); //$NON-NLS-1$

							if (vc.showOpenDialog(AutomaticSettingsFrame.this) == JFileChooser.APPROVE_OPTION) {
								p.setComponentValueToValue(comp, vc.getSelectedFile().getAbsolutePath());
							}
						}
					});
				}

				JButton btnReset = new JButton(LocaleBundle.getString("Settingsframe.btnReset.title")); //$NON-NLS-1$
				btnReset.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						p.setComponentValueToValue(comp, p.getDefault());
					}
				});
				pnlTab.add(btnReset, "9, " + c*2); //$NON-NLS-1$
				
				elements.add(new PropertyElement(p, comp));
				
				c++;
			}
		}
	}

	private void setValues() {
		for (PropertyElement pe : elements) {
			pe.setComponentValue();
		}
	}
	
	private void okValues() {
		for (PropertyElement pe : elements) {
			pe.setPropertyValue();
		}		
	}

	protected void setPanelCount(int i) {
		this.pnlCount = i;
	}
	
	protected void setRowCount(int i) {
		this.rowCount = i;
	}
	
	private RowSpec[] getRowSpec() {
		int c = rowCount*2 + 1;
		RowSpec[] spec = new RowSpec[c];
		
		for (int i = 0; i < c; i++) {
			spec[i] = (i % 2 == 0) ? (FormFactory.RELATED_GAP_ROWSPEC) : (FormFactory.DEFAULT_ROWSPEC);
		}
		
		return spec;
	}
}
