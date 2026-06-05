package de.jClipCorn.features.actionTree.menus;

import de.jClipCorn.database.CCMovieList;
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
import de.jClipCorn.util.lambda.Func0to0;
import de.jClipCorn.util.listener.ActionCallbackListener;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ClipMenuBar extends JMenuBar {
	private static final long serialVersionUID = -6537611193514097201L;
	
	private JMenu lastMaster;
	private JMenu lastSubMaster;
	private JMenu lastSubSubMaster;

	private final List<CCActionElement> actions = new ArrayList<>();

	private final Func0to0 _postAction;

	protected final CCMovieList movielist;

	public ClipMenuBar(CCMovieList ml, Func0to0 postAction) {
		super();

		_postAction = (postAction==null) ? Func0to0.NOOP : postAction;
		movielist = ml;
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	protected abstract void init();

	protected void addMaster(final String textID)
	{
		lastMaster = new JMenu(LocaleBundle.getString(textID));

		this.add(lastMaster);
		lastSubMaster = null;
	}

	protected void addSubMaster(final String textID, final MultiSizeIconRef icon)
	{
		lastSubMaster = new JMenu(LocaleBundle.getString(textID));
		if (icon != null) lastSubMaster.setIcon(icon.get16x16());

		lastMaster.add(lastSubMaster);
	}

	protected void addSubSubMaster(final String textID, final MultiSizeIconRef icon)
	{
		lastSubSubMaster = new JMenu(LocaleBundle.getString(textID));
		if (icon != null) lastSubSubMaster.setIcon(icon.get16x16());

		lastSubMaster.add(lastSubSubMaster);
	}

	protected void addNode(final String textID, final Func0to0 action, final MultiSizeIconRef icon, final boolean readOnlyRestriction, final boolean highlight)
	{
		_addNode(lastMaster, textID, action, icon, readOnlyRestriction, highlight);
	}

	protected void addSubNode(final String textID, final Func0to0 action, final MultiSizeIconRef icon, final boolean readOnlyRestriction, final boolean highlight)
	{
		_addNode(lastSubMaster, textID, action, icon, readOnlyRestriction, highlight);
	}

	protected void addSubSubNode(final String textID, final Func0to0 action, final MultiSizeIconRef icon, final boolean readOnlyRestriction, final boolean highlight)
	{
		_addNode(lastSubSubMaster, textID, action, icon, readOnlyRestriction, highlight);
	}

	private void _addNode(final JMenu parent, final String textID, final Func0to0 action, final MultiSizeIconRef icon, final boolean readOnlyRestriction, final boolean highlight)
	{
		JMenuItem node;
		if (icon != null)
			node = new JMenuItem(LocaleBundle.getString(textID), icon.get16x16());
		else
			node = new JMenuItem(LocaleBundle.getString(textID));

		node.addActionListener(e -> {
			if (action == null) return;
			if (readOnlyRestriction && movielist.isReadonly()) return;
			action.invoke();
			_postAction.invoke();
		});

		if (highlight)
		{
			var f1 = node.getFont();
			var f2 = new Font(f1.getName(), f1.getStyle() | Font.BOLD, f1.getSize());
			node.setFont(f2);
		}

		if (readOnlyRestriction && movielist.isReadonly()) node.setEnabled(false);

		parent.add(node);
	}

	protected void addSeparator() {
		lastMaster.add(new JSeparator());
	}

	protected void addSubSeparator() {
		lastSubMaster.add(new JSeparator());
	}

	protected void addSubSubSeparator() {
		lastSubSubMaster.add(new JSeparator());
	}

	protected void addActionNode(String actionIdent)
	{
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);

		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return;
		}

		actions.add(el);

		addNode(el.getCaptionIdent(),
				() -> el.execute(getSourceComponent(), getActionSource(), getActionSourceObject(), getSourceListener()),
				el.getIconRef(),
				el.isReadOnlyRestricted(),
				CCStreams.iterate(getActionSourceObject()).any(aso -> aso.shouldHighlightAction(el)));
	}

	protected void addActionSubNode(String actionIdent)
	{
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);

		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return;
		}

		actions.add(el);

		addSubNode(el.getCaptionIdent(),
				() -> el.execute(getSourceComponent(), getActionSource(), getActionSourceObject(), getSourceListener()),
				el.getIconRef(),
				el.isReadOnlyRestricted(),
				CCStreams.iterate(getActionSourceObject()).any(aso -> aso.shouldHighlightAction(el)));
	}

	protected void addActionTreeNode(String actionIdent) {
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);

		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return;
		}

		actions.add(el);

		addSubMaster(el.getCaptionIdent(), el.getIconRef());

		for (CCActionElement child : el.getAllChildren())
		{
			actions.add(child);
			addActionSubNode(child.getName());
		}
	}

	@SuppressWarnings("nls")
	protected void addOpenInBrowserActionNodes(List<Tuple<Opt<String>, CCOnlineReferenceList>> _refs)
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
			addNode("ClipMenuBar.Other.MovieExtra.ShowInBrowser", () -> openRef(refs.get(0).Item2.Main), Resources.ICN_MENUBAR_ONLINEREFERENCE, false, false);
			return;
		}

		addSubMaster("ClipMenuBar.Other.MovieExtra.ShowInBrowser", Resources.ICN_MENUBAR_ONLINEREFERENCE);

		if (own.isMainSet())
		{
			addSubNode("@" + (own.Main.hasDescription() ? own.Main.description : own.Main.type.asString()), () -> openRef(own.Main), own.Main.getIconRef(), false, false);

			addSubSeparator();
		}

		for (var soref : own.Additional)
		{
			addSubNode("@" + soref.type.asString(), () -> openRef(soref), soref.getIconRef(), false, false);
		}

		if (!nonOwn.isEmpty()) addSubSeparator();

		for (var group : nonOwn)
		{
			addSubSubMaster("@" + group.Item1.get(), null);

			for (var soref : group.Item2)
			{
				addSubSubNode("@" + soref.type.asString(), () -> openRef(soref), soref.getIconRef(), false, false);
			}

			if (group.Item2.totalCount() > 1)
			{
				addSubSubSeparator();
				addSubSubNode("ClipMenuBar.Other.ShowAllInBrowser", () -> openAllInBrowser(group.Item2.ccstream().toList()), Resources.ICN_MENUBAR_ONLINEREFERENCE, false, false);
			}
		}

		if (all.size() > 2)
		{
			addSubSeparator();
			addSubNode("ClipMenuBar.Other.ShowAllInBrowser", () -> openAllInBrowser(all), Resources.ICN_MENUBAR_ONLINEREFERENCE, false, false);
		}
	}

	private void openRef(CCSingleOnlineReference r) {
		if (r.isSet() && r.isValid()) r.openInBrowser(null, ccprops());
	}

	private void openAllInBrowser(List<CCSingleOnlineReference> references) {
		for (var r : references) if (r.isSet() && r.isValid()) r.openInBrowser(null, ccprops());
	}

	public void implementDirectKeyListener(PreviewSeriesFrame frame, JPanel contentPane)
	{
		for (var act : actions) act.implementDirectKeyListener(frame, frame, contentPane);
	}

	protected abstract List<IActionSourceObject> getActionSourceObject();
	protected abstract ActionSource getActionSource();
	protected abstract ActionCallbackListener getSourceListener();
	protected abstract Component getSourceComponent();

}
