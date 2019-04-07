package de.jClipCorn.gui.guiComponents.language;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class LanguageChooserDialog extends JDialog {
	private static final long serialVersionUID = 672690138720968482L;

	private HashSet<CCDBLanguage> _value;

	private final Func1to0<CCDBLanguageList> _okListener;

	public LanguageChooserDialog(LanguageChooser owner, Func1to0<CCDBLanguageList> onFinish, CCDBLanguageList value) {
		super();

		_okListener = onFinish;

		initGUI(value);
		setLocationRelativeTo(owner);

		_value = new HashSet<>(value.iterate().enumerate());
	}

	private void initGUI(CCDBLanguageList value) {
		setTitle(LocaleBundle.getString("EditStringListPropertyFrame.this.title")); //$NON-NLS-1$
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
		ArrayList<RowSpec> rspec = new ArrayList<>();
		for (int i = 0; i < CCDBLanguage.values().length; i++) {
			rspec.add(FormSpecs.RELATED_GAP_ROWSPEC);
			rspec.add(FormSpecs.DEFAULT_ROWSPEC);
		}
		rspec.add(FormSpecs.RELATED_GAP_ROWSPEC);

		JPanel pnlContent = new JPanel();
		pnlContent.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnlBase.add(pnlContent, BorderLayout.CENTER);
		pnlContent.setLayout(new FormLayout(new ColumnSpec[]{ ColumnSpec.decode("default"), FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow") }, rspec.toArray(new RowSpec[0]))); //$NON-NLS-1$ //$NON-NLS-2$

		int i = 1;
		for (CCDBLanguage itlang : CCDBLanguage.values())
		{
			final CCDBLanguage lang = itlang;
			final JCheckBox cb = new JCheckBox(lang.asString());
			//cb.setIcon(lang.getIcon());

			pnlContent.add(cb, "3, "+(i*2)+", fill, fill"); //$NON-NLS-1$ //$NON-NLS-2$
			cb.setSelected(value.contains(lang));
			cb.addActionListener(evt -> {
				if (cb.isSelected()) _value.add(lang);
				else _value.remove(lang);
			});

			pnlContent.add(new JLabel(lang.getIcon()), "1, "+(i*2)+", fill, fill"); //$NON-NLS-1$ //$NON-NLS-2$

			i++;
		}

		JPanel pnlBottom = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnlBottom.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		pnlBase.add(pnlBottom, BorderLayout.SOUTH);

		JButton btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(e -> { setVisible(false); dispose(); });
		pnlBottom.add(btnCancel);

		JButton btnOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOk.addActionListener(e -> { setVisible(false); dispose(); _okListener.invoke(CCDBLanguageList.createDirect(_value)); });
		pnlBottom.add(btnOk);

		pack();
	}
}
