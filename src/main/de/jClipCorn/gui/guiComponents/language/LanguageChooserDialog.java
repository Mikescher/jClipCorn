package de.jClipCorn.gui.guiComponents.language;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class LanguageChooserDialog extends JDialog {
	private static final long serialVersionUID = 672690138720968482L;

	private final Func1to0<CCDBLanguage> _okListener;

	public LanguageChooserDialog(Component owner, Func1to0<CCDBLanguage> onFinish) {
		super();

		_okListener = onFinish;

		initGUI();
		setLocationRelativeTo(owner);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("LanguageChooserDialog.title")); //$NON-NLS-1$
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

		int rowcount = (int)Math.ceil(CCDBLanguage.values().length / 2.0);

		ArrayList<RowSpec> rspec = new ArrayList<>();
		ColumnSpec[] cspec = new ColumnSpec[]
		{
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$ //$NON-NLS-2$
				FormSpecs.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
		};
		for (int i = 0; i < rowcount; i++) {
			rspec.add(FormSpecs.DEFAULT_ROWSPEC);
		}
		rspec.add(FormSpecs.RELATED_GAP_ROWSPEC);

		JPanel pnlContent = new JPanel();
		pnlContent.setBorder(new EmptyBorder(5, 5, 5, 5));
		pnlBase.add(pnlContent, BorderLayout.CENTER);
		pnlContent.setLayout(new FormLayout(cspec, rspec.toArray(new RowSpec[0])));

		int i = 0;
		for (CCDBLanguage itlang : CCDBLanguage.values())
		{
			final CCDBLanguage lang = itlang;
			final JButton cb = new JButton(lang.asString());
			cb.setIcon(lang.getIcon());
			cb.setHorizontalAlignment(SwingConstants.LEFT);

			pnlContent.add(cb, (2 + (i/rowcount)*2)+", "+(1 + i%rowcount)+", fill, fill"); //$NON-NLS-1$ //$NON-NLS-2$
			cb.addActionListener(evt -> {
				_okListener.invoke(lang);
				dispose();
			});

			i++;
		}

		JPanel pnlBottom = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnlBottom.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		pnlBase.add(pnlBottom, BorderLayout.SOUTH);

		JButton btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(e -> { setVisible(false); dispose(); });
		pnlBottom.add(btnCancel);

		JRootPane root = getRootPane();
		root.registerKeyboardAction(e -> { setVisible(false); dispose(); }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		pack();
	}
}
