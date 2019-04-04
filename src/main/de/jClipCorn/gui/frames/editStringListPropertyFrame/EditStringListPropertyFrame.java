package de.jClipCorn.gui.frames.editStringListPropertyFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.property.CCStringListProperty;

public class EditStringListPropertyFrame extends JDialog {
	private static final long serialVersionUID = -7538672244655631538L;
	
	private JTextArea edMain;
	private JPanel pnlBottom;
	private JButton btnCancel;
	private JButton btnOk;
	private JScrollPane scrollPane;
	private JPanel pnlContent;

	private final Component component;
	private final CCStringListProperty target;
	private final ActionListener okListener;
	
	/**
	 * Create the frame.
	 */
	public EditStringListPropertyFrame(Component owner, ActionListener onFinish, Component comp, CCStringListProperty prop) {
		super();

		component = comp;
		target = prop;
		okListener = onFinish;
		
		initGUI();
		setLocationRelativeTo(owner);
		initData();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("EditStringListPropertyFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setType(Type.UTILITY);
		setMinimumSize(new Dimension(250, 200));
		setSize(new Dimension(550, 300));
		setModal(true);

		pnlContent = new JPanel();
		pnlContent.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(pnlContent, BorderLayout.CENTER);
		pnlContent.setLayout(new BorderLayout(0, 0));
		
		pnlBottom = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnlBottom.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		pnlContent.add(pnlBottom, BorderLayout.SOUTH);
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		pnlBottom.add(btnCancel);
		
		btnOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				target.setComponentValueToValue(component, edMain.getText().split("\r?\n")); //$NON-NLS-1$
				
				setVisible(false);
				dispose();
				
				okListener.actionPerformed(new ActionEvent(this, 0, target.getValueAsString()));
			}
		});
		pnlBottom.add(btnOk);
		
		scrollPane = new JScrollPane();
		pnlContent.add(scrollPane);
		
		edMain = new JTextArea();
		scrollPane.setViewportView(edMain);
	}

	private void initData() {
		StringBuilder buildr = new StringBuilder();
		
		boolean first = true;
		for (String v : target.getComponentValue(component)) {
			if (! first) buildr.append("\n"); //$NON-NLS-1$
			buildr.append(v);
			
			first = false;
		}
		
		edMain.setText(buildr.toString());
	}
}
