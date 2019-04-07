package de.jClipCorn.gui.guiComponents.referenceChooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.gui.localization.LocaleBundle;

public class ReferenceChooserPopup extends JDialog implements WindowFocusListener {
	private static final long serialVersionUID = 1286034649164678546L;

	private static ReferenceChooserPopup instance = null;

	private JReferenceChooser parent;

	private List<JSingleSubReferenceChooser> uiList = new ArrayList<>();
	
	private JScrollPane pnlScroll;
	private JPanel pnlContent;
	private JPanel pnllBottom;
	private JButton btnAdd;
	private JButton btnOK;
	private JPanel pnlBase;
	
	public ReferenceChooserPopup(List<CCSingleOnlineReference> data, JReferenceChooser parent) {
		super();
		
		if (instance != null) {
			instance.dispose();
		}
		instance = this;
		
		this.parent = parent;
				
		setAlwaysOnTop(true);
		initGUI(parent);
		initData(data);
		
		setLocation((int) parent.getLocationOnScreen().getX(), (int) (parent.getLocationOnScreen().getY() + parent.getSize().getHeight() + 2));
		
		addWindowFocusListener(this);
	}

	private void initGUI(JReferenceChooser parent) { 
		setSize(450, 25);
		
		setUndecorated(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setType(Type.POPUP);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		
		pnlBase = new JPanel();
		pnlBase.setBorder(null);
		getContentPane().add(pnlBase, BorderLayout.CENTER);
		pnlBase.setLayout(new BorderLayout(0, 0));
		
		pnllBottom = new JPanel();
		pnlBase.add(pnllBottom, BorderLayout.SOUTH);
		pnllBottom.setLayout(new BorderLayout(0, 0));

		btnAdd = new JButton("+"); //$NON-NLS-1$
		btnAdd.addActionListener(e -> onAdd());
		btnAdd.setMargin(new java.awt.Insets(1, 2, 1, 2));
		pnllBottom.add(btnAdd, BorderLayout.EAST);

		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(e -> onOK());
		btnOK.setFont(new Font(btnOK.getFont().getFontName(), Font.BOLD, btnOK.getFont().getSize()));
		pnllBottom.add(btnOK, BorderLayout.WEST);
		
		pnlScroll = new JScrollPane();
		pnlScroll.getVerticalScrollBar().setUnitIncrement(16);
		pnlBase.add(pnlScroll, BorderLayout.CENTER);
		
		pnlContent = new JPanel();
		pnlContent.setBackground(Color.WHITE);
		pnlScroll.setViewportView(pnlContent);
		pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
	}

	private void initData(List<CCSingleOnlineReference> data) {
		for (CCSingleOnlineReference soref : data) {
			addSingleControl(soref);
		}
		addSingleControl(CCSingleOnlineReference.createNone());

		updateHeight();
	}

	private void addSingleControl(CCSingleOnlineReference soref) {
		JSingleSubReferenceChooser chsr = new JSingleSubReferenceChooser();
		chsr.setValue(soref);
		chsr.setSize(pnlContent.getWidth(), (int) chsr.getPreferredSize().getHeight());
		chsr.setBorder(new EmptyBorder(2, 2, 2, 2));
		
		pnlContent.add(chsr);
		pnlContent.validate();

		uiList.add(chsr);
	}

	public void updateHeight() {
		pack();
		
		if (uiList.isEmpty()) {
			pnlScroll.setVisible(false);
		} else {
			pnlScroll.setVisible(true);
		}
				
		int contentHeight = pnlContent.getHeight();
		
		int width = 450;
		int height = pnllBottom.getHeight() + contentHeight;
		
		//if (height > 250) height = 250;
		if (height < 24) height = 24;
		
		int scrollHeight = 2 + height - pnllBottom.getHeight();
		
		if (contentHeight > scrollHeight) {
			pnlScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		} else {
			pnlScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		}

		pnlScroll.setPreferredSize(new Dimension(width, scrollHeight));
		
		setPreferredSize(new Dimension(width, height));
		pack();
	}

	private void onAdd() {
		addSingleControl(CCSingleOnlineReference.createNone());

		updateHeight();
	}

	private void onOK() {
		List<CCSingleOnlineReference> value = new ArrayList<>();
		for	(JSingleSubReferenceChooser c : uiList) {
			CCSingleOnlineReference soref = c.getValue();
			if (soref.isUnset()) continue;
			if (soref.isInvalid()) continue;
			value.add(soref);
		}
		
		parent.setAdditional(value);

		dispose();
		disposeInstance();
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
		// 
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		dispose();
		disposeInstance();
	}

	public static void disposeInstance() {
		if (instance != null) {
			instance.dispose();
			instance = null;
		}
	}
}
