package de.jClipCorn.gui.guiComponents;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.util.http.HTTPUtilities;
import java.awt.Insets;

public class OnlineRefButton extends JPanel {
	private static final long serialVersionUID = -4894887510061074387L;
	
	private JButton btnDropDown;
	private JButton btnMain;

	private CCOnlineReferenceList value = null;
	
	public OnlineRefButton() {
		super();
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));

		btnMain = new JButton();
		add(btnMain, BorderLayout.CENTER);
		btnMain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (value == null || value.Main.isUnset()) return;
				
				HTTPUtilities.openInBrowser(value.Main.getURL());
			}
		});
		
		btnDropDown = new JButton("V"); //$NON-NLS-1$
		btnDropDown.setMargin(new Insets(2, 2, 2, 2));
		add(btnDropDown, BorderLayout.EAST);
		btnDropDown.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JPopupMenu menu = new JPopupMenu();
				for (final CCSingleOnlineReference soref : value.Additional) {
					JMenuItem mi = new JMenuItem(soref.hasDescription() ? soref.description : soref.type.asString());
					mi.setIcon(soref.type.getIcon16x16());
					mi.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							HTTPUtilities.openInBrowser(soref.getURL());
						}
					});
					menu.add(mi);
				}
				menu.show(OnlineRefButton.this, e.getX(), e.getY());
				
			}
		});
	}
	
	public void setValue(CCOnlineReferenceList ref) {
		btnMain.setIcon(ref.Main.getIconButton());
		btnMain.setEnabled(ref.Main.isSet());
		
		btnDropDown.setVisible(ref.hasAdditional());
		
		value = ref;
	}
}
