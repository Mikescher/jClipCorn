package de.jClipCorn.features.actionTree.menus;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.CCActionElement;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.KeyStrokeUtil;
import de.jClipCorn.util.lambda.Func0to0;
import de.jClipCorn.util.listener.ActionCallbackListener;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public abstract class ClipPopupMenu extends JPopupMenu {
	private static final long serialVersionUID = -3924972933691119441L;

	private final List<CCActionElement> actions = new ArrayList<>();

	protected final CCMovieList movielist;

	public ClipPopupMenu(CCMovieList ml) {
		super();
		movielist = ml;
	}
	
	protected abstract void init();

	protected abstract IActionSourceObject getSourceObject();
	protected abstract Component getSourceFrame();
	protected abstract ActionCallbackListener getSourceListener();

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	protected JMenuItem addAction(String actionIdent) {
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);
		
		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return null;
		}

		actions.add(el);

		JMenuItem item = add(el.getCaption());
		
		item.setIcon(el.getSmallIcon());

		if (getSourceObject().shouldHighlightAction(el))
		{
			var f1 = item.getFont();
			var f2 = new Font(f1.getName(), f1.getStyle() | Font.BOLD, f1.getSize());
			item.setFont(f2);
		}
		
		if (! KeyStrokeUtil.isEmpty(el.getKeyStroke())) {
			item.setAccelerator(el.getKeyStroke());
		}
		
		item.addActionListener(arg0 -> el.execute(getSourceFrame(), ActionSource.POPUP_MENU, getSourceObject(), getSourceListener()));
		
		return item;
	}
	
	protected ActionMenuWrapper addActionMenuTree(String actionIdent) {
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);
		
		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return null;
		}

		actions.add(el);
		
		JMenu item = new JMenu(el.getCaption());
		add(item);
		
		item.setIcon(el.getSmallIcon());

		if (! KeyStrokeUtil.isEmpty(el.getKeyStroke())) {
			item.setAccelerator(el.getKeyStroke());
		}

		if (getSourceObject().shouldHighlightAction(el))
		{
			var f1 = item.getFont();
			var f2 = new Font(f1.getName(), f1.getStyle() | Font.BOLD, f1.getSize());
			item.setFont(f2);
		}
		
		item.addActionListener(arg0 -> el.execute(getSourceFrame(), ActionSource.POPUP_MENU, getSourceObject(), getSourceListener()));
		
		ActionMenuWrapper amw = new ActionMenuWrapper(item);
		
		for (CCActionElement child : el.getAllChildren()) {
			actions.add(child);
			amw.add(getSourceFrame(), child, getSourceObject(), getSourceListener());
		}
		
		return amw;
	}

	protected JMenuItem addCustomAction(String caption, MultiSizeIconRef icnref, Func0to0 action)
	{
		JMenuItem item = add(caption);
		if (icnref != null) item.setIcon(icnref.get16x16());

		item.addActionListener(arg0 -> action.invoke());

		return item;
	}

	@SuppressWarnings("nls")
	protected void addOpenInBrowserAction(CCDatabaseElement src, List<Tuple<Opt<String>, CCOnlineReferenceList>> _refs)
	{
		var refs = CCStreams.iterate(_refs).filter(p -> !p.Item2.isEmpty()).toList();

		var own = CCStreams.
				iterate(refs).
				filter(p -> p.Item1.isEmpty()).
				map(p -> p.Item2).
				firstOr(CCOnlineReferenceList.EMPTY);

		var nonOwn = CCStreams.
				iterate(refs).
				filter(p -> !p.Item1.isEmpty()).
				toList();

		var all = CCStreams.iterate(refs).flatten(p -> p.Item2.ccstream()).toList();

		if (refs.size() == 1 && refs.get(0).Item1.isEmpty() && refs.get(0).Item2.isOnlyMainSet())
		{
			addAction("ShowInBrowser");
			return;
		}

		final CCActionElement el0 = CCActionTree.getInstance().find("ShowInBrowser");
		if (el0 == null)
		{
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", "ShowInBrowser")); //$NON-NLS-1$
			return;
		}

		JMenu menu = new JMenu(el0.getCaption());
		add(menu);
		menu.setIcon(el0.getSmallIcon());

		if (own.isMainSet())
		{
			menu.add(mitem(
					own.Main.hasDescription() ? own.Main.description : own.Main.type.asString(),
					own.Main.type.getIcon16x16(),
					el0.getKeyStroke(),
					e -> own.Main.openInBrowser(src, ccprops())));

			menu.addSeparator();
		}

		var ungrouped = CCStreams.iterate(own.Additional).filter(r -> !r.hasDescription()).toList();

		for (var soref : own.Additional)
		{
			menu.add(mitem(
					soref.type.asString(),
					soref.type.getIcon16x16(),
					null,
					e -> soref.openInBrowser(src, ccprops())));
		}

		if (!nonOwn.isEmpty()) menu.addSeparator();

		for (var group : nonOwn)
		{
			JMenu submenu = new JMenu(group.Item1.get());

			for (var soref : group.Item2)
			{
				submenu.add(mitem(
						soref.type.asString(),
						soref.type.getIcon16x16(),
						null,
						e -> soref.openInBrowser(src, ccprops())));
			}

			if (group.Item2.totalCount() > 1)
			{
				submenu.addSeparator();

				submenu.add(mitem(
						LocaleBundle.getString("ClipMenuBar.Other.ShowAllInBrowser"),
						Resources.ICN_MENUBAR_ONLINEREFERENCE.get16x16(),
						null,
						e -> openAllInBrowser(src, group.Item2.ccstream().toList())));
			}

			menu.add(submenu);
		}

		if (all.size() > 2)
		{
			menu.addSeparator();

			menu.add(mitem(
					LocaleBundle.getString("ClipMenuBar.Other.ShowAllInBrowser"),
					Resources.ICN_MENUBAR_ONLINEREFERENCE.get16x16(),
					null,
					e -> openAllInBrowser(src, all)));
		}
	}

	private void openAllInBrowser(CCDatabaseElement src, List<CCSingleOnlineReference> references) {
		for (var r : references) if (r.isSet() && r.isValid()) r.openInBrowser(src, ccprops());
	}

	@SuppressWarnings("nls")
	protected void addPlayAction(ICCPlayableElement src, boolean anonymous)
	{
		var alts = ccprops().getAlternativeMediaPlayers();

		var actname = (src instanceof CCMovie)
				? (anonymous ? "PlayMovieAnonymous"   : "PlayMovie"  )
				: (anonymous ? "PlayEpisodeAnonymous" : "PlayEpisode");

		if (alts.size() == 0)
		{
			addAction(actname);
		}
		else
		{
			var act = CCActionTree.getInstance().find(actname);

			JMenu menu = new JMenu(act.getCaption());
			add(menu);
			menu.setIcon(act.getSmallIcon());
			{
				JMenuItem subitem = menu.add(LocaleBundle.getString("ClipMenuBar.Movies.Play"));
				subitem.addActionListener(e -> act.execute(getSourceFrame(), ActionSource.POPUP_MENU, getSourceObject(), getSourceListener()));
				subitem.setIcon(act.getSmallIcon());
				menu.add(subitem);
			}

			menu.addSeparator();

			for (var ref : alts)
			{
				JMenuItem subitem = menu.add(ref.getCaption());
				subitem.addActionListener(arg0 -> src.play(getSourceFrame(), !anonymous, ref));
				menu.add(subitem);
			}
		}
	}

	public void implementDirectKeyListener(PreviewSeriesFrame frame, JPanel contentPane)
	{
		for (var act : actions) act.implementDirectKeyListener(frame, frame, contentPane);
	}

	private JMenuItem mitem(String txt, Icon icn, KeyStroke ks, ActionListener act) {
		var r = new JMenuItem(txt);

		r.setIcon(icn);
		if (!KeyStrokeUtil.isEmpty(ks)) r.setAccelerator(ks);
		r.addActionListener(act);

		return r;
	}
}
