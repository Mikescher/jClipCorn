package de.jClipCorn.gui.frames.settingsFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.Main;
import de.jClipCorn.gui.frames.extendedSettingsFrame.ExtendedSettingsFrame;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.properties.property.CCFrameSizeProperty;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.ExtendedFocusTraversalOnArray;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

	private FrameSizesTablePanel frameSizesPanel;

	public AutomaticSettingsFrame(MainFrame owner, CCProperties properties){
		super(owner.getMovielist());
		this.properties = properties;
		this.owner = owner;
	}

	protected void init() {
		initGUI();
		
		setLocationRelativeTo(owner);

		ccprops().PROP_FSIZE_SETTINGSFRAME.applyOrSkip(this);
		
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

	@SuppressWarnings("nls")
	private List<Component> initPanel(CCPropertyCategory category) {
		JPanel pnlRoot = new JPanel();
		JScrollPane scrlPane = new JScrollPane();

		JPanel pnlTabOuter = new JPanel();
		tpnlSettings.addTab(category.getCaption(), null, pnlRoot, null);

		pnlRoot.setLayout(new BorderLayout());
		pnlRoot.add(scrlPane, BorderLayout.CENTER);

		scrlPane.setViewportView(pnlTabOuter);
		scrlPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		var pnlTabInner = new JPanel();

		if (category.equals(CCProperties.CAT_FRAMESIZES))
		{
			// horizontally scrollable so the (Frame | Default | hosts...) table can overflow the window width
			scrlPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

			pnlTabOuter.setLayout(new FormLayout(
				new ColumnSpec[]{FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default"), FormSpecs.RELATED_GAP_COLSPEC}, //$NON-NLS-1$
				new RowSpec[]{FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.UNRELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC}));

			// the generic rows for this category only contain PROP_FSIZE_DEBUG (the frame-size props are rendered as a table)
			var tabOrder = reinitPanel(pnlTabInner, category, Opt.empty());
			pnlTabOuter.add(pnlTabInner, CC.xy(2, 2));

			frameSizesPanel = new FrameSizesTablePanel(properties);
			pnlTabOuter.add(frameSizesPanel, CC.xy(2, 4, CC.LEFT, CC.TOP));

			return tabOrder;
		}

		if (category.showFilter())
		{
			var cspec = new ColumnSpec[]{FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC};
			var rspec = new RowSpec[]{FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, RowSpec.decode("14dlu"), RowSpec.decode("default:grow")};
			pnlTabOuter.setLayout(new FormLayout(cspec, rspec));

			var tf = new JTextField();
			tf.getDocument().addDocumentListener(new DocumentLambdaAdapter(() ->
			{
				if (tf.getText().isEmpty())
					reinitPanel(pnlTabInner, category, Opt.empty());
				else
					reinitPanel(pnlTabInner, category, Opt.of(tf.getText()));

				pnlRoot.invalidate();
				pnlRoot.revalidate();
				pnlRoot.repaint();

				scrlPane.invalidate();
				scrlPane.revalidate();
				scrlPane.repaint();

				pnlTabInner.invalidate();
				pnlTabInner.revalidate();
				pnlTabInner.repaint();
			}));
			pnlTabOuter.add(tf, CC.xy(2, 2, CC.FILL, CC.FILL));

			pnlTabOuter.add(pnlTabInner, CC.xyw(1, 4, 3, CC.FILL, CC.FILL));
		}
		else
		{
			var cspec = new ColumnSpec[]{FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC};
			var rspec = new RowSpec[]{FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("default:grow")};
			pnlTabOuter.setLayout(new FormLayout(cspec, rspec));

			pnlTabOuter.add(pnlTabInner, CC.xyw(1, 2, 3, CC.FILL, CC.FILL));
		}

		return reinitPanel(pnlTabInner, category, Opt.empty());
	}

	@SuppressWarnings("nls")
	private List<Component> reinitPanel(JPanel pnlTab, CCPropertyCategory category, Opt<String> filter) {
		List<Component> tabOrder = new ArrayList<>();

		pnlTab.removeAll();

		var colspec = new ColumnSpec[]
		{
			FormSpecs.RELATED_GAP_COLSPEC,
			FormSpecs.DEFAULT_COLSPEC,
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("default:grow"),
			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("50dlu"),
			FormSpecs.RELATED_GAP_COLSPEC,
			FormSpecs.DEFAULT_COLSPEC,
			FormSpecs.RELATED_GAP_COLSPEC,
		};

		var props = CCStreams
				.iterate(properties.getPropertyList())
				.filter(p -> p.getCategory().equals(category))
				.filter(p -> !CCFrameSizeProperty.class.isInstance(p)) // rendered via FrameSizesTablePanel instead
				.filter(p -> filterBySearch(p, filter))
				.enumerate();

		// in the PATHSYNTAX tab we additionally render the (readonly) CCPath syntax variables that were overridden via commandline (--ccpath key=value)
		boolean showCmdlineOverrides = category.equals(CCProperties.CAT_PATHSYNTAX) && filter.isEmpty() && !Main.ARG_CCPATH_OVERRIDES.isEmpty();

		List<RowSpec> rowspec = new ArrayList<>();
		for (final CCProperty<Object> p : props) {
			rowspec.add(FormSpecs.RELATED_GAP_ROWSPEC);
			rowspec.add(FormSpecs.DEFAULT_ROWSPEC);
			if (p.getComponentBottomMargin()) rowspec.add(FormSpecs.UNRELATED_GAP_ROWSPEC);
		}
		if (showCmdlineOverrides) {
			for (int i = 0; i < Main.ARG_CCPATH_OVERRIDES.size(); i++) {
				rowspec.add(FormSpecs.RELATED_GAP_ROWSPEC);
				rowspec.add(FormSpecs.DEFAULT_ROWSPEC);
			}
		}
		rowspec.add(FormSpecs.RELATED_GAP_ROWSPEC);

		pnlTab.setLayout(new FormLayout(colspec, rowspec.toArray(new RowSpec[0])));

		int c = 2;

		for (final CCProperty<Object> p : props) {
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

		if (showCmdlineOverrides) {
			for (var e : Main.ARG_CCPATH_OVERRIDES.entrySet()) {
				JLabel info = new JLabel(LocaleBundle.getString("Settingsframe.cmdlinePathVar")); //$NON-NLS-1$
				pnlTab.add(info, "2, " + c + ", right, center"); //$NON-NLS-1$ //$NON-NLS-2$

				pnlTab.add(buildReadonlyOverrideComponent(e.getKey(), e.getValue()), "4, " + c + ", fill, center"); //$NON-NLS-1$ //$NON-NLS-2$

				c += 2;
			}
		}

		return tabOrder;
	}

	@SuppressWarnings("nls")
	private JPanel buildReadonlyOverrideComponent(String key, String value) {
		JPanel pnl = new JPanel();

		// pin the host-label column to the exact width the "Host" label occupies in the normal rows,
		// so the Key/Value columns line up with the editable variables above
		int hostLabelWidth = new JLabel(LocaleBundle.getString("CCPathVarProperty.Host")).getPreferredSize().width;

		pnl.setLayout(new FormLayout(
			new ColumnSpec[]
			{
				FormSpecs.UNRELATED_GAP_COLSPEC,              //
				ColumnSpec.decode(hostLabelWidth + "px"),    // (host label slot)
				FormSpecs.UNRELATED_GAP_COLSPEC,              //
				ColumnSpec.decode("110px"),                  // (host field slot)
				FormSpecs.UNRELATED_GAP_COLSPEC,              //
				ColumnSpec.decode("default"),                // "Key" label
				FormSpecs.UNRELATED_GAP_COLSPEC,              //
				ColumnSpec.decode("80px"),                   // key field
				FormSpecs.UNRELATED_GAP_COLSPEC,             //
				ColumnSpec.decode("default"),                // "Value" label
				FormSpecs.UNRELATED_GAP_COLSPEC,             //
				ColumnSpec.decode("default:grow"),           // value field
				FormSpecs.UNRELATED_GAP_COLSPEC,             //
			},
			new RowSpec[]
			{
				FormSpecs.PREF_ROWSPEC,
			}));

		// where the "Host" label + host input field normally are, show the source marker instead (spans both columns)
		pnl.add(new JLabel(LocaleBundle.getString("Settingsframe.cmdlinePathVarSource")), "2, 1, 3, 1, left, default");

		pnl.add(new JLabel(LocaleBundle.getString("CCPathVarProperty.Key")), "6, 1, fill, default");
		var fldKey = new JTextField(key);
		fldKey.setEditable(false);
		pnl.add(fldKey, "8, 1, fill, default");

		pnl.add(new JLabel(LocaleBundle.getString("CCPathVarProperty.Value")), "10, 1, fill, default");
		var fldValue = new JTextField(value);
		fldValue.setEditable(false);
		pnl.add(fldValue, "12, 1, fill, default");

		return pnl;
	}

	private boolean filterBySearch(CCProperty<Object> prop, Opt<String> filter) {
		if (filter.isEmpty()) return true;

		var lcf = filter.get().toLowerCase();

		if (prop.getIdentifier().toLowerCase().contains(lcf)) return true;

		for (var loc : LocaleBundle.listLocales()) {
			if (prop.getDescriptionOrEmpty(loc).toLowerCase().contains(lcf)) return true;
		}

		if (prop.getDescription().toLowerCase().contains(lcf)) return true; // because some props override getDescription()

		return false;
	}

	private void setValues() {
		for (PropertyElement pe : elements) {
			pe.setComponentValue();
		}
		if (frameSizesPanel != null) frameSizesPanel.loadValues();
	}

	private List<String> applyValues() {
		List<String> r = new ArrayList<>();
		for (PropertyElement pe : elements) {
			var v = pe.setPropertyValue();
			if (v.isPresent()) r.add(v.get());
		}
		if (frameSizesPanel != null) r.addAll(frameSizesPanel.applyValues());
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
