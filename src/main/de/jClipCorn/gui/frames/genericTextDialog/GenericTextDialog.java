package de.jClipCorn.gui.frames.genericTextDialog;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GenericTextDialog extends JDialog {
	private static final long serialVersionUID = 315192508785168630L;

	private final boolean _editable;
	private final Func1to0<String> _callback;

	/**
	 * Create the frame.
	 */
	private GenericTextDialog(Component owner, String title, String content, boolean modal, boolean editable, Func1to0<String> callback) {
		super();

		_editable = editable;
		_callback = callback;

		initGUI(title, content, modal);
		setLocationRelativeTo(owner);
	}

	public static void showText(Component owner, String title, String content, boolean modal) {
		if (owner instanceof JDialog && ((JDialog)owner).isModal()) modal = true;
		new GenericTextDialog(owner, title, content, modal, false, null).setVisible(true);
	}

	public static void showEditableText(Component owner, String title, String content, boolean modal, Func1to0<String> callback) {
		if (owner instanceof JDialog && ((JDialog)owner).isModal()) modal = true;
		new GenericTextDialog(owner, title, content, modal, true, callback).setVisible(true);
	}

	private void initGUI(String title, String content, boolean modal) {
		setTitle(title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setSize(new Dimension(1000, 600));
		setModal(modal);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, "2, 2, fill, fill"); //$NON-NLS-1$

		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(_editable);
		textArea.setText(content);
		scrollPane.getVerticalScrollBar().setValue(0);
		SwingUtils.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		contentPane.add(panel, "2, 4, fill, fill"); //$NON-NLS-1$

		JButton btnNewButton = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnNewButton.addActionListener(e ->
		{
			dispose();
			if (_editable && _callback != null) _callback.invoke(textArea.getText());
		});
		panel.add(btnNewButton);
	}
	
}
