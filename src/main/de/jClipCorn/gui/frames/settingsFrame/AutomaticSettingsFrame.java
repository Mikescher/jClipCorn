package de.jClipCorn.gui.frames.settingsFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.Main;
import de.jClipCorn.gui.frames.extendedSettingsFrame.ExtendedSettingsFrame;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.ExtendedFocusTraversalOnArray;
import de.jClipCorn.util.helper.LookAndFeelManager;

public abstract class AutomaticSettingsFrame extends JFrame {
	private static final long serialVersionUID = 4681197289662529891L;

	private final CCProperties properties;
	private final MainFrame owner;
	
	private JTabbedPane tpnlSettings;
	private JPanel pnlBottom;
	private JButton btnOk;
	private JButton btnCancel;
	private JButton btnExtended;

	private List<PropertyElement> elements = new ArrayList<>();
	
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
		List<Component> tabOrder = new ArrayList<>();
		
		setIconImage(Resources.IMG_FRAME_ICON.get());
		
		if (LookAndFeelManager.isMetal())
			setSize(new Dimension(825, 525));
		else
			setSize(new Dimension(800, 475));
		
		setMinimumSize(new Dimension(650, 400));
		setTitle(LocaleBundle.getString("Settingsframe.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		tpnlSettings = new JTabbedPane(JTabbedPane.TOP);
		tpnlSettings.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		getContentPane().add(tpnlSettings, BorderLayout.CENTER);
		
		int pnlCount = properties.getCategoryCount();
		
		for (int i = 0; i < pnlCount; i++) {
			tabOrder.addAll(initPanel(CCProperties.CATEGORIES[i]));
		}

		pnlBottom = new JPanel();
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		pnlBottom.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.BUTTON_COLSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				FormSpecs.BUTTON_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.BUTTON_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("23px"), //$NON-NLS-1$
				FormSpecs.LINE_GAP_ROWSPEC,}));
		
		btnOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOk.addActionListener(arg0 ->
		{
			if (! validateValues()) return;
			if (CCProperties.getInstance().ARG_READONLY) return;

			if (! Main.DEBUG) {
				DialogHelper.showDispatchInformation(AutomaticSettingsFrame.this, LocaleBundle.getString("Settingsframe.informationDlg.caption"), LocaleBundle.getString("Settingsframe.informationDlg.text")); //$NON-NLS-1$ //$NON-NLS-2$
			}

			okValues();

			SwingUtilities.invokeLater(() -> MainFrame.getInstance().onSettingsChanged());

			dispose();
		});
		pnlBottom.add(btnOk, "4, 2, fill, top"); //$NON-NLS-1$
		btnOk.setEnabled(!CCProperties.getInstance().ARG_READONLY);
		tabOrder.add(btnOk);
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(arg0 -> dispose());
		pnlBottom.add(btnCancel, "6, 2, fill, top"); //$NON-NLS-1$
		tabOrder.add(btnCancel);
		
		btnExtended = new JButton(LocaleBundle.getString("Settingsframe.btnExtended.title")); //$NON-NLS-1$
		btnExtended.addActionListener(e ->
		{
			(new ExtendedSettingsFrame(AutomaticSettingsFrame.this, properties)).setVisible(true);
			dispose();
		});
		pnlBottom.add(btnExtended, "10, 2"); //$NON-NLS-1$
		tabOrder.add(btnExtended);
		btnExtended.setVisible(CCProperties.getInstance().PROP_SHOW_EXTENDED_FEATURES.getValue());
		
		setFocusTraversalPolicy(new ExtendedFocusTraversalOnArray(tabOrder));
	}

	private List<Component> initPanel(CCPropertyCategory category) {
		List<Component> tabOrder = new ArrayList<>();
		
		JPanel pnlRoot = new JPanel();
		JScrollPane scrlPane = new JScrollPane();
		
		JPanel pnlTab = new JPanel();
		tpnlSettings.addTab(category.getCaption(), null, pnlRoot, null);
		
		pnlRoot.setLayout(new BorderLayout());
		pnlRoot.add(scrlPane, BorderLayout.CENTER);
		
		scrlPane.setViewportView(pnlTab);
		scrlPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		pnlTab.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("50dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC},
			getRowSpec(properties.getCountForCategory(category))));

		int c = 1;
		for (final CCProperty<Object> p : properties.getPropertyList()) {
			if (p.getCategory().equals(category)) {
				JLabel info = new JLabel(p.getDescription());
				pnlTab.add(info, "2, " + c*2 + ", right, default"); //$NON-NLS-1$ //$NON-NLS-2$

				final Component comp1 = p.getComponent();
				pnlTab.add(comp1, "4, " + c*2 + ", fill, default"); //$NON-NLS-1$ //$NON-NLS-2$
				tabOrder.add(comp1);
				
				Component comp2 = p.getSecondaryComponent(comp1);
				
				if (comp2 != null) {
					pnlTab.add(comp2, "6, " + c*2 + ", left, default"); //$NON-NLS-1$ //$NON-NLS-2$
					tabOrder.add(comp2);
				}

				JButton btnReset = new JButton(LocaleBundle.getString("Settingsframe.btnReset.title")); //$NON-NLS-1$
				btnReset.addActionListener(e -> p.setComponentValueToValue(comp1, p.getDefault()));
				pnlTab.add(btnReset, "8, " + c*2); //$NON-NLS-1$
				tabOrder.add(btnReset);
				
				elements.add(new PropertyElement(p, comp1));
				
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
	
	protected Object getCurrentInputValue(CCProperty<?> prop) {
		for (PropertyElement pe : elements) {
			if (pe.getProperty().getIdentifier().equals(prop.getIdentifier())) return pe.getComponentValue();
		}
		
		return null;
	}
	
	private RowSpec[] getRowSpec(int rowCount) {
		int c = rowCount * 2 + 1;
		RowSpec[] spec = new RowSpec[c];
		
		for (int i = 0; i < c; i++) {
			spec[i] = (i % 2 == 0) ? (FormSpecs.RELATED_GAP_ROWSPEC) : (FormSpecs.DEFAULT_ROWSPEC);
		}
		
		return spec;
	}
	
	protected abstract boolean validateValues();
}
