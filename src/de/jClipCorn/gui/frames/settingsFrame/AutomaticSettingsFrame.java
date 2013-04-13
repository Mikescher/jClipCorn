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
import javax.swing.JScrollPane;
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
import de.jClipCorn.util.ExtendedFocusTraversalOnArray;
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
	private JButton btnExtended;

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
	}
	
	private void initGUI() {
		ArrayList<Component> tabOrder = new ArrayList<>();
		
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		
		setSize(new Dimension(675, 440));
		setMinimumSize(new Dimension(650, 400));
		setTitle(LocaleBundle.getString("Settingsframe.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		tpnlSettings = new JTabbedPane(JTabbedPane.TOP);
		tpnlSettings.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		getContentPane().add(tpnlSettings, BorderLayout.CENTER);
		
		int pnlCount = properties.getHighestCategory() + 1;
		
		for (int i = 0; i < pnlCount; i++) {
			tabOrder.addAll(initPanel(i));
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
		tabOrder.add(btnOk);
		
		btnCancel = new JButton(LocaleBundle.getString("Settingsframe.btnCancel.title")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		pnlBottom.add(btnCancel, "6, 2, fill, top"); //$NON-NLS-1$
		tabOrder.add(btnCancel);
		
		btnExtended = new JButton(LocaleBundle.getString("Settingsframe.btnExtended.title")); //$NON-NLS-1$
		btnExtended.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				(new ExtendedSettingsFrame(AutomaticSettingsFrame.this, properties)).setVisible(true);
				dispose();
			}
		});
		pnlBottom.add(btnExtended, "10, 2"); //$NON-NLS-1$
		tabOrder.add(btnExtended);
		
		setFocusTraversalPolicy(new ExtendedFocusTraversalOnArray(tabOrder));
	}

	private ArrayList<Component> initPanel(int pnlNumber) {
		ArrayList<Component> tabOrder = new ArrayList<>();
		
		JPanel pnlRoot = new JPanel();
		JScrollPane scrlPane = new JScrollPane();
		
		JPanel pnlTab = new JPanel();
		tpnlSettings.addTab(LocaleBundle.getString("Settingsframe.tabbedCpt.Caption_" + pnlNumber), null, pnlRoot, null); //$NON-NLS-1$
		
		pnlRoot.setLayout(new BorderLayout());
		pnlRoot.add(scrlPane, BorderLayout.CENTER);
		
		scrlPane.setViewportView(pnlTab);
		scrlPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
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
			getRowSpec(properties.getCategoryCount(pnlNumber))));

		int c = 1;
		for (final CCProperty<Object> p : properties.getPropertyList()) {
			if (p.getCategory() == pnlNumber) {
				JLabel info = new JLabel(p.getDescription());
				pnlTab.add(info, "2, " + c*2 + ", right, default"); //$NON-NLS-1$ //$NON-NLS-2$

				final Component comp = p.getComponent();
				pnlTab.add(comp, "4, " + c*2 + ", fill, default"); //$NON-NLS-1$ //$NON-NLS-2$
				tabOrder.add(comp);
				
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
					tabOrder.add(btnChoose);
				}

				JButton btnReset = new JButton(LocaleBundle.getString("Settingsframe.btnReset.title")); //$NON-NLS-1$
				btnReset.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						p.setComponentValueToValue(comp, p.getDefault());
					}
				});
				pnlTab.add(btnReset, "9, " + c*2); //$NON-NLS-1$
				tabOrder.add(btnReset);
				
				elements.add(new PropertyElement(p, comp));
				
				c++;
			}
		}
		
		return tabOrder;
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
	
	private RowSpec[] getRowSpec(int rowCount) {
		int c = rowCount * 2 + 1;
		RowSpec[] spec = new RowSpec[c];
		
		for (int i = 0; i < c; i++) {
			spec[i] = (i % 2 == 0) ? (FormFactory.RELATED_GAP_ROWSPEC) : (FormFactory.DEFAULT_ROWSPEC);
		}
		
		return spec;
	}
}
