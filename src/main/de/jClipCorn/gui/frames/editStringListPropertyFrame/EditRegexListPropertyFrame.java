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

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.property.CCRegexListProperty;

public class EditRegexListPropertyFrame extends JDialog {
	private static final long serialVersionUID = -7538672244655631538L;
	
	private JTextArea edMain;
	private JPanel pnlBottom;
	private JButton btnCancel;
	private JButton btnOk;
	private JScrollPane scrollPane;
	private JPanel pnlContent;
	private RegexValidationRowHeader rexRowHeader;

	private final Component component;
	private final CCRegexListProperty target;
	private final ActionListener okListener;
	
	/**
	 * Create the frame.
	 */
	public EditRegexListPropertyFrame(Component owner, ActionListener onFinish, Component comp, CCRegexListProperty prop) {
		super();
		
		component = comp;
		target = prop;
		okListener = onFinish;
		
		initGUI();
		setLocationRelativeTo(owner);
		initData();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("EditRegexListPropertyFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
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
				String[] data = edMain.getText().split("\r?\n"); //$NON-NLS-1$
				
				for (String datum : data) {
					if (! (datum.trim().isEmpty() || target.testRegex(datum))) return;
				}
				
				setVisible(false);
				dispose();
				
				okListener.actionPerformed(new ActionEvent(this, 0, String.join("\n", data))); //$NON-NLS-1$
			}
		});
		pnlBottom.add(btnOk);
		
		scrollPane = new JScrollPane();
		pnlContent.add(scrollPane);
		
		edMain = new JTextArea();
		scrollPane.setViewportView(edMain);
		
		rexRowHeader = new RegexValidationRowHeader(edMain, target);
		scrollPane.setRowHeaderView(rexRowHeader);
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
