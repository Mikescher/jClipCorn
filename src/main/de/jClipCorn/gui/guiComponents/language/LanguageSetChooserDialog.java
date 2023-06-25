package de.jClipCorn.gui.guiComponents.language;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LanguageSetChooserDialog extends JDialog {
	private static final long serialVersionUID = 672690138720968482L;

	private final HashSet<CCDBLanguage> _value;

	private final Func1to0<CCDBLanguageSet> _okListener;

	private final HashMap<CCDBLanguage, Tuple<JLabel, JCheckBox>> _components = new HashMap<>();

	public LanguageSetChooserDialog(Component owner, Func1to0<CCDBLanguageSet> onFinish, CCDBLanguageSet value) {
		super();

		_okListener = onFinish;

		initGUI(value);
		setLocationRelativeTo(owner);

		_value = new HashSet<>(value.ccstream().enumerate());
	}

	@SuppressWarnings("nls")
	private void initGUI(CCDBLanguageSet value) {
		setTitle(LocaleBundle.getString("LanguageChooserDialog.title"));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setType(Type.UTILITY);
		setMinimumSize(new Dimension(250, 200));
		setSize(new Dimension(550, 300));
		setModal(true);

		JPanel pnlBase = new JPanel();
		pnlBase.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnlBase.setLayout(new BorderLayout());
		getContentPane().add(pnlBase, BorderLayout.CENTER);

		int columnCount = 4;

		int rowcount = (int)Math.ceil(CCDBLanguage.values().length / (double)(columnCount));

		ArrayList<RowSpec> rspec = new ArrayList<>();
		ColumnSpec[] cspec = new ColumnSpec[]
		{
			ColumnSpec.decode("default"),
			FormSpecs.RELATED_GAP_COLSPEC,

			ColumnSpec.decode("default"),                 // icon
			FormSpecs.RELATED_GAP_COLSPEC,                // (spacer)
			ColumnSpec.decode("64dlu:grow"),              // checkbox

			FormSpecs.UNRELATED_GAP_COLSPEC,              //

			ColumnSpec.decode("default"),                 // icon
			FormSpecs.RELATED_GAP_COLSPEC,                // (spacer)
			ColumnSpec.decode("64dlu:grow"),              // checkbox

			FormSpecs.UNRELATED_GAP_COLSPEC,              //

			ColumnSpec.decode("default"),                 // icon
			FormSpecs.RELATED_GAP_COLSPEC,                // (spacer)
			ColumnSpec.decode("64dlu:grow"),              // checkbox

			FormSpecs.UNRELATED_GAP_COLSPEC,              //

			ColumnSpec.decode("default"),                 // icon
			FormSpecs.RELATED_GAP_COLSPEC,                // (spacer)
			ColumnSpec.decode("64dlu:grow"),              // checkbox

			FormSpecs.RELATED_GAP_COLSPEC,
			ColumnSpec.decode("default"),
		};

		rspec.add(FormSpecs.RELATED_GAP_ROWSPEC);
		rspec.add(FormSpecs.DEFAULT_ROWSPEC);
		rspec.add(FormSpecs.RELATED_GAP_ROWSPEC);
		for (int i = 0; i < rowcount; i++) {
			rspec.add(RowSpec.decode("11dlu"));
		}
		rspec.add(FormSpecs.RELATED_GAP_ROWSPEC);

		JPanel pnlContent = new JPanel();
		pnlContent.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnlBase.add(pnlContent, BorderLayout.CENTER);
		pnlContent.setLayout(new FormLayout(cspec, rspec.toArray(new RowSpec[0])));

		var searchField = new JTextField();
		searchField.getDocument().addDocumentListener(new DocumentLambdaAdapter(() -> onFilter(searchField.getText())));

		pnlContent.add(searchField, CC.xywh(3, 2, 4*columnCount-1, 1, CC.FILL, CC.FILL));

		int i = 0;
		for (CCDBLanguage itlang : CCDBLanguage.values())
		{
			final CCDBLanguage lang = itlang;
			final JLabel lbl = new JLabel(lang.getIcon());
			final JCheckBox cb = new JCheckBox(lang.asString());

			pnlContent.add(lbl, CC.xy(3 + (i/rowcount)*4, 4 + i%rowcount, CC.FILL, CC.FILL));

			pnlContent.add(cb, CC.xy(5 + (i/rowcount)*4, 4 + i%rowcount, CC.FILL, CC.FILL));
			cb.setSelected(value.contains(lang));
			cb.addActionListener(evt -> {
				if (cb.isSelected()) _value.add(lang);
				else _value.remove(lang);
			});

			_components.put(lang, Tuple.Create(lbl, cb));

			i++;
		}

		JPanel pnlBottom = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnlBottom.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		pnlBase.add(pnlBottom, BorderLayout.SOUTH);

		JButton btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text"));
		btnCancel.addActionListener(e -> { setVisible(false); dispose(); });
		pnlBottom.add(btnCancel);

		JButton btnOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text"));
		btnOk.setFont(new Font(btnOk.getFont().getFontName(), Font.BOLD, btnOk.getFont().getSize()));
		btnOk.addActionListener(e -> { setVisible(false); dispose(); _okListener.invoke(CCDBLanguageSet.createDirect(_value)); });
		pnlBottom.add(btnOk);

		JRootPane root = getRootPane();
		root.registerKeyboardAction(e -> { setVisible(false); dispose(); }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		pack();
	}

	private void onFilter(String search) {
		for (var entry : _components.entrySet()) {
			entry.getValue().Item2.setVisible(entry.getValue().Item2.isSelected() || LanguageSearch.isSearchMatch(entry.getKey(), search));
			entry.getValue().Item1.setVisible(entry.getValue().Item2.isSelected() || LanguageSearch.isSearchMatch(entry.getKey(), search));
		}
	}
}
