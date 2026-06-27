package de.jClipCorn.gui.guiComponents.iconComponents;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.KeyStrokeUtil;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OnlineRefButton extends JPanel {
	private static final long serialVersionUID = -4894887510061074387L;
	
	private JButton btnDropDown;
	private JButton btnMain;

	private CCOnlineReferenceList value = null;
	private List<Tuple<String, CCOnlineReferenceList>> seasonValues = new ArrayList<>();

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
			public void mouseClicked(MouseEvent e)
			{
				JPopupMenu menu = new JPopupMenu();
				buildMenu(menu);
				menu.show(OnlineRefButton.this, e.getX(), e.getY());
			}
		});
	}
	
	public void setValue(CCOnlineReferenceList ref) {
		setValue(ref, new ArrayList<>());
	}

	/**
	 * @param ref        the online-references directly on the element (series / movie)
	 * @param seasonRefs additional per-season online-references (title + references), shown as submenus in the dropdown
	 */
	public void setValue(CCOnlineReferenceList ref, List<Tuple<String, CCOnlineReferenceList>> seasonRefs) {
		btnMain.setIcon(ref.Main.getIconButton());
		btnMain.setEnabled(ref.Main.isSet());

		value = ref;
		seasonValues = seasonRefs;

		btnDropDown.setVisible(ref.hasAdditional() || !seasonValues.isEmpty());
	}
	
	private void buildMenu(JPopupMenu menu)
	{
		boolean ownPart = value.hasAdditional() && value.isMainSet();

		if (!ownPart && seasonValues.isEmpty()) return;

		if (ownPart)
		{
			var ungrouped = CCStreams.iterate(value.Additional).filter(r -> !r.hasDescription()).toList();
			var grouped = CCStreams.iterate(value.Additional).filter(CCSingleOnlineReference::hasDescription).groupBy(r -> r.description).autosortByProperty(Map.Entry::getKey).toList();

			for (var sovalue : ungrouped)
			{
				menu.add(mitem(
						sovalue.hasDescription() ? sovalue.description : sovalue.type.asString(),
						sovalue.type.getIcon16x16(),
						null,
						e -> sovalue.openInBrowser(null, movielist.ccprops())));
			}

			if (!grouped.isEmpty() && !ungrouped.isEmpty()) menu.addSeparator();

			for (var sovaluelist : CCStreams.iterate(grouped))
			{

				JMenu submenu = new JMenu(sovaluelist.getKey());

				for (var sovalue : sovaluelist.getValue())
				{
					submenu.add(mitem(
							sovalue.type.asString(),
							sovalue.type.getIcon16x16(),
							null,
							e -> sovalue.openInBrowser(null, movielist.ccprops())));
				}

				if (sovaluelist.getValue().size() > 1)
				{
					submenu.addSeparator();

					submenu.add(mitem(
							LocaleBundle.getString("ClipMenuBar.Other.ShowAllInBrowser"),
							Resources.ICN_MENUBAR_ONLINEREFERENCE.get16x16(),
							null,
							e -> openAllInBrowser(sovaluelist.getValue())));
				}

				menu.add(submenu);
			}
		}

		if (!seasonValues.isEmpty())
		{
			if (menu.getComponentCount() > 0) menu.addSeparator();

			for (var season : seasonValues)
			{
				JMenu submenu = new JMenu(season.Item1);

				for (var soref : season.Item2)
				{
					submenu.add(mitem(
							soref.type.asString(),
							soref.type.getIcon16x16(),
							null,
							e -> soref.openInBrowser(null, movielist.ccprops())));
				}

				if (season.Item2.totalCount() > 1)
				{
					submenu.addSeparator();

					submenu.add(mitem(
							LocaleBundle.getString("ClipMenuBar.Other.ShowAllInBrowser"),
							Resources.ICN_MENUBAR_ONLINEREFERENCE.get16x16(),
							null,
							e -> openAllInBrowser(season.Item2.ccstream().toList())));
				}

				menu.add(submenu);
			}
		}

		List<CCSingleOnlineReference> allRefs = new ArrayList<>(value.ccstream().toList());
		for (var season : seasonValues) allRefs.addAll(season.Item2.ccstream().toList());

		if (allRefs.size() > 2)
		{
			menu.addSeparator();

			menu.add(mitem(
					LocaleBundle.getString("ClipMenuBar.Other.ShowAllInBrowser"),
					Resources.ICN_MENUBAR_ONLINEREFERENCE.get16x16(),
					null,
					e -> openAllInBrowser(allRefs)));
		}
	}

	private void openAllInBrowser(List<CCSingleOnlineReference> references) {
		for (var r : references) if (r.isSet() && r.isValid()) r.openInBrowser(null, movielist.ccprops());
	}

	private JMenuItem mitem(String txt, Icon icn, KeyStroke ks, ActionListener act) {
		var r = new JMenuItem(txt);

		r.setIcon(icn);
		if (!KeyStrokeUtil.isEmpty(ks)) r.setAccelerator(ks);
		r.addActionListener(act);

		return r;
	}
}
