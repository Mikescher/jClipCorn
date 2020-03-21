package de.jClipCorn.gui.frames.genericTextDialog;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.helper.SwingUtils;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;

public class GenericTextDialog extends JDialog {
	private static final long serialVersionUID = 315192508785168630L;
	
	private JPanel contentPane;
	private JTextArea textArea;
	private JPanel panel;
	private JButton btnNewButton;
	private JScrollPane scrollPane;

	/**
	 * Create the frame.
	 */
	private GenericTextDialog(Component owner, String title, String content, boolean modal) {
		super();
		initGUI(title, content, modal);
		setLocationRelativeTo(owner);
	}
	
	public static void showText(Component owner, String title, String content, boolean modal) {
		if (owner instanceof JDialog && ((JDialog)owner).isModal()) modal = true;
		new GenericTextDialog(owner, title, content, modal).setVisible(true);
	}

	private void initGUI(String title, String content, boolean modal) {
		setTitle(title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setSize(new Dimension(1000, 600));
		setModal(modal);
		contentPane = new JPanel();
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
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, "2, 2, fill, fill"); //$NON-NLS-1$
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		textArea.setText(content);
		scrollPane.getVerticalScrollBar().setValue(0);
		SwingUtils.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
		
		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		contentPane.add(panel, "2, 4, fill, fill"); //$NON-NLS-1$
		
		btnNewButton = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnNewButton.addActionListener(e -> dispose());
		panel.add(btnNewButton);
	}
	
}
