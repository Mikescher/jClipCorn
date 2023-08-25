package de.jClipCorn.gui.guiComponents.iconComponents;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.util.http.HTTPUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OnlineRefButton extends JPanel {
	private static final long serialVersionUID = -4894887510061074387L;
	
	private JButton btnDropDown;
	private JButton btnMain;

	private CCOnlineReferenceList value = null;

	private final CCMovieList movielist;

	@DesignCreate
	private static OnlineRefButton designCreate() { return new OnlineRefButton(null); }

	public OnlineRefButton(CCMovieList ml) {
		super();
		movielist = ml;
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));

		btnMain = new JButton();
		add(btnMain, BorderLayout.CENTER);
		btnMain.addActionListener(arg0 -> {
			if (value == null || value.Main.isUnset()) return;

			HTTPUtilities.openInBrowser(value.Main.getURL(movielist.ccprops()));
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
					mi.addActionListener(e1 -> HTTPUtilities.openInBrowser(soref.getURL(movielist.ccprops())));
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
