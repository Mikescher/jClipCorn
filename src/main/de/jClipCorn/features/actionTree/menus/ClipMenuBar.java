package de.jClipCorn.features.actionTree.menus;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.CCActionElement;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.lambda.Func0to0;
import de.jClipCorn.util.listener.ActionCallbackListener;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class ClipMenuBar extends JMenuBar {
	private static final long serialVersionUID = -6537611193514097201L;
	
	private JMenu lastMaster;
	private JMenu lastSubMaster;

	protected final Func0to0 _postAction;

	public ClipMenuBar(Func0to0 postAction) {
		super();

		_postAction = (postAction==null) ? Func0to0.NOOP : postAction;
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

	protected void addNode(final String textID, final Func0to0 action, final MultiSizeIconRef icon, final boolean readOnlyRestriction)
	{
		JMenuItem node;
		if (icon != null)
			node = new JMenuItem(LocaleBundle.getString(textID), icon.get16x16());
		else
			node = new JMenuItem(LocaleBundle.getString(textID));

		node.addActionListener(e -> {
			if (action == null) return;
			if (readOnlyRestriction && CCProperties.getInstance().ARG_READONLY) return;
			action.invoke();
			_postAction.invoke();
		});

		if (readOnlyRestriction && CCProperties.getInstance().ARG_READONLY) node.setEnabled(false);

		lastMaster.add(node);
	}

	protected void addSubNode(final String textID, final Func0to0 action, final MultiSizeIconRef icon, final boolean readOnlyRestriction)
	{
		JMenuItem node;
		if (icon != null)
			node = new JMenuItem(LocaleBundle.getString(textID), icon.get16x16());
		else
			node = new JMenuItem(LocaleBundle.getString(textID));

		node.addActionListener(e -> {
			if (action == null) return;
			if (readOnlyRestriction && CCProperties.getInstance().ARG_READONLY) return;
			action.invoke();
			_postAction.invoke();
		});

		if (readOnlyRestriction && CCProperties.getInstance().ARG_READONLY) node.setEnabled(false);

		lastSubMaster.add(node);
	}

	protected void addSeparator() {
		lastMaster.add(new JSeparator());
	}

	protected void addSubSeparator() {
		lastSubMaster.add(new JSeparator());
	}

	protected void addActionNode(String actionIdent)
	{
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);

		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return;
		}

		addNode(el.getCaptionIdent(), () -> el.execute(getSourceComponent(), getActionSource(), getActionSourceObject(), getSourceListener()), el.getIconRef(), el.isReadOnlyRestricted());
	}

	protected void addActionSubNode(String actionIdent)
	{
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);

		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return;
		}

		addSubNode(el.getCaptionIdent(), () -> el.execute(getSourceComponent(), getActionSource(), getActionSourceObject(), getSourceListener()), el.getIconRef(), el.isReadOnlyRestricted());
	}

	protected void addActionTreeNode(String actionIdent) {
		final CCActionElement el = CCActionTree.getInstance().find(actionIdent);

		if (el == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorActionNotFound", actionIdent)); //$NON-NLS-1$
			return;
		}

		addSubMaster(el.getCaptionIdent(), el.getIconRef());

		for (CCActionElement child : el.getAllChildren()) addActionSubNode(child.getName());
	}

	protected void addOpenInBrowserActionNodes(CCOnlineReferenceList ref) {
		if (ref.hasAdditional())
		{
			addSubMaster("ClipMenuBar.Other.MovieExtra.ShowInBrowser", Resources.ICN_MENUBAR_ONLINEREFERENCE); //$NON-NLS-1$
			for	(final CCSingleOnlineReference soref : ref) {
				addSubNode("@" + (soref.hasDescription() ? soref.description : soref.type.asString()), () -> openRef(soref), soref.getIconRef(), false); //$NON-NLS-1$
				if (soref == ref.Main && ref.hasAdditional()) addSubSeparator();
			}
		}
		else
		{
			addNode("ClipMenuBar.Other.MovieExtra.ShowInBrowser", () -> openRef(ref.Main), Resources.ICN_MENUBAR_ONLINEREFERENCE, false); //$NON-NLS-1$
		}
	}

	private void openRef(CCSingleOnlineReference r) {
		if (r.isSet() && r.isValid()) HTTPUtilities.openInBrowser(r.getURL());
	}

	protected abstract List<IActionSourceObject> getActionSourceObject();
	protected abstract ActionSource getActionSource();
	protected abstract ActionCallbackListener getSourceListener();
	protected abstract Component getSourceComponent();

}
