package de.jClipCorn.gui.frames.settingsFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.Main;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.gui.frames.extendedSettingsFrame.ExtendedSettingsFrame;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.ExtendedFocusTraversalOnArray;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AutomaticSettingsFrame extends JCCFrame {
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
		super(owner.getMovielist());
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

		setSize(new Dimension(1200, 650));
		
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
			if (owner.getMovielist().isReadonly()) return;

			if (! Main.DEBUG) {
				DialogHelper.showDispatchInformation(AutomaticSettingsFrame.this, LocaleBundle.getString("Settingsframe.informationDlg.caption"), LocaleBundle.getString("Settingsframe.informationDlg.text")); //$NON-NLS-1$ //$NON-NLS-2$
			}

			var changes = applyValues();

			SwingUtils.invokeLater(() -> MainFrame.getInstance().onSettingsChanged(changes));

			dispose();
		});
		pnlBottom.add(btnOk, "4, 2, fill, top"); //$NON-NLS-1$
		btnOk.setEnabled(!owner.getMovielist().isReadonly());
		tabOrder.add(btnOk);
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(arg0 -> dispose());
		pnlBottom.add(btnCancel, "6, 2, fill, top"); //$NON-NLS-1$
		tabOrder.add(btnCancel);
		
		btnExtended = new JButton(LocaleBundle.getString("Settingsframe.btnExtended.title")); //$NON-NLS-1$
		btnExtended.addActionListener(e ->
		{
			(new ExtendedSettingsFrame(AutomaticSettingsFrame.this, movielist, properties)).setVisible(true);
			dispose();
		});
		pnlBottom.add(btnExtended, "10, 2"); //$NON-NLS-1$
		tabOrder.add(btnExtended);
		btnExtended.setVisible(ccprops().PROP_SHOW_EXTENDED_FEATURES.getValue());
		
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

		var colspec = new ColumnSpec[]
		{
			FormSpecs.RELATED_GAP_COLSPEC,
			FormSpecs.DEFAULT_COLSPEC,
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("default:grow"), //$NON-NLS-1$
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("50dlu"), //$NON-NLS-1$
			FormSpecs.RELATED_GAP_COLSPEC,
			FormSpecs.DEFAULT_COLSPEC,
			FormSpecs.RELATED_GAP_COLSPEC,
		};

		var rowspec = CCStreams
				.iterate(properties.getPropertyList())
				.filter(p -> p.getCategory().equals(category))
				.map(p -> new RowSpec[]{FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, p.getComponentBottomMargin() ? FormSpecs.UNRELATED_GAP_ROWSPEC : null})
				.flatten(CCStreams::iterate)
				.filter(Objects::nonNull)
				.append(FormSpecs.RELATED_GAP_ROWSPEC)
				.toArray(new RowSpec[0]);

		pnlTab.setLayout(new FormLayout(colspec, rowspec));

		int c = 2;

		for (final CCProperty<Object> p : properties.getPropertyList()) {
			if (p.getCategory().equals(category)) {
				JLabel info = new JLabel(p.getDescription());
				pnlTab.add(info, "2, " + c + ", right, " + p.getLabelRowAlign()); //$NON-NLS-1$ //$NON-NLS-2$

				final Component comp1 = p.getComponent();
				final Component comp2 = p.getSecondaryComponent(comp1);

				if (comp2 == null && p.getComponent1ColStretch()) {

					pnlTab.add(comp1, "4, " + c + ",3, 1, fill, " + p.getComponent1RowAlign()); //$NON-NLS-1$ //$NON-NLS-2$
					tabOrder.add(comp1);

				} else if (comp2 == null) {

					pnlTab.add(comp1, "4, " + c + ", fill, " + p.getComponent1RowAlign()); //$NON-NLS-1$ //$NON-NLS-2$
					tabOrder.add(comp1);

				} else {

					pnlTab.add(comp1, "4, " + c + ", fill, " + p.getComponent1RowAlign()); //$NON-NLS-1$ //$NON-NLS-2$
					tabOrder.add(comp1);

					pnlTab.add(comp2, "6, " + c + ", left, " + p.getComponent2RowAlign()); //$NON-NLS-1$ //$NON-NLS-2$
					tabOrder.add(comp2);
				}

				JButton btnReset = new JButton(LocaleBundle.getString("Settingsframe.btnReset.title")); //$NON-NLS-1$
				btnReset.addActionListener(e -> p.setComponentValueToValue(comp1, p.getDefault()));
				pnlTab.add(btnReset, "8, " + c); //$NON-NLS-1$
				tabOrder.add(btnReset);
				
				elements.add(new PropertyElement(p, comp1));

				c+=2;

				if (p.getComponentBottomMargin()) c++;
			}
		}
		
		return tabOrder;
	}

	private void setValues() {
		for (PropertyElement pe : elements) {
			pe.setComponentValue();
		}
	}
	
	private List<String> applyValues() {
		List<String> r = new ArrayList<>();
		for (PropertyElement pe : elements) {
			var v = pe.setPropertyValue();
			if (v.isPresent()) r.add(v.get());
		}
		return r;
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
