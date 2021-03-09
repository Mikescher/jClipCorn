package de.jClipCorn.gui.mainFrame.filterTree;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.util.CCQualityCategoryType;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.TableCustomFilter;
import de.jClipCorn.features.table.filter.customFilter.*;
import de.jClipCorn.features.table.filter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.gui.frames.customFilterEditDialog.CustomFilterEditDialog;
import de.jClipCorn.gui.frames.organizeFilterFrame.OrganizeFilterDialog;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeObject;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeObject.SimpleTreeEvent;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpletreeActionMode;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.clipTable.ClipTable;
import de.jClipCorn.gui.mainFrame.clipTable.RowFilterSource;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterTree extends AbstractFilterTree {
	private static final long serialVersionUID = 592519777667038909L;

	private final CustomFilterList customFilterList = new CustomFilterList();
	
	private final ClipTable table;
	private final CCMovieList movielist;
	
	public FilterTree(CCMovieList list, ClipTable table) {
		super(list);
		this.table = table;
		this.movielist = list;
		
		this.tree.ActionMode = SimpletreeActionMode.OnClick;
		
		customFilterList.load();
	}

	@Override
	protected void addFields() {
		initAll(addNodeI(null, null, null, null));
		
		initZyklus(addNode(null, Resources.ICN_SIDEBAR_ZYKLUS, LocaleBundle.getString("FilterTree.Zyklus"), this::parentClicked)); //$NON-NLS-1$
		
		if (movielist.getGroupList().size() > 0) {
			initGroups(addNode(null, Resources.ICN_SIDEBAR_GROUPS, LocaleBundle.getString("FilterTree.Groups"), this::parentClicked)); //$NON-NLS-1$
		}
		
		initGenre(addNode(null, Resources.ICN_SIDEBAR_GENRE, LocaleBundle.getString("FilterTree.Genre"), this::parentClicked)); //$NON-NLS-1$
		
		initOnlineScore(addNode(null, Resources.ICN_SIDEBAR_ONLINESCORE, LocaleBundle.getString("FilterTree.IMDB"), this::parentClicked)); //$NON-NLS-1$
		
		initScore(addNode(null, Resources.ICN_SIDEBAR_SCORE, LocaleBundle.getString("FilterTree.Score"), this::parentClicked)); //$NON-NLS-1$
		
		initFSK(addNode(null, Resources.ICN_TABLE_FSK_2, LocaleBundle.getString("FilterTree.FSK"), this::parentClicked)); //$NON-NLS-1$
		
		initYear(addNode(null, Resources.ICN_SIDEBAR_YEAR, LocaleBundle.getString("FilterTree.Year"), this::parentClicked)); //$NON-NLS-1$
		
		initFormat(addNode(null, Resources.ICN_SIDEBAR_FORMAT, LocaleBundle.getString("FilterTree.Format"), this::parentClicked)); //$NON-NLS-1$

		initQuality(addNode(null, Resources.ICN_SIDEBAR_QUALITY, LocaleBundle.getString("FilterTree.Quality"), this::parentClicked)); //$NON-NLS-1$
		
		initTags(addNode(null, Resources.ICN_SIDEBAR_TAGS, LocaleBundle.getString("FilterTree.Tags"), this::parentClicked)); //$NON-NLS-1$
		
		initLanguage(addNode(null, Resources.ICN_SIDEBAR_LANGUAGE, LocaleBundle.getString("FilterTree.Language"), this::parentClicked)); //$NON-NLS-1$
		
		initTyp(addNode(null, Resources.ICN_SIDEBAR_TYP, LocaleBundle.getString("FilterTree.Type"), this::parentClicked)); //$NON-NLS-1$
		
		initViewed(addNode(null, Resources.ICN_SIDEBAR_VIEWED, LocaleBundle.getString("FilterTree.Viewed"), this::parentClicked)); //$NON-NLS-1$

		initCustom(addNode(null, Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom"), this::parentClicked)); //$NON-NLS-1$
	}

	private void parentClicked(SimpleTreeEvent evt) {

		if (CCProperties.getInstance().PROP_MAINFRAME_FILTERTREE_RECOLLAPSE.getValue())
		{
			if (tree.isExpanded(evt.path)) tree.collapsePath(evt.path);
			else expand(evt);
		}
		else
		{
			expand(evt);
		}
	}

	@SuppressWarnings("unchecked")
	private void expand(SimpleTreeEvent evt) {
		tree.expandPath(evt.path);
		
		if (evt.path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)evt.path.getLastPathComponent();
			
			for (Object o : CCStreams.iterate(node.depthFirstEnumeration())) {
				
				if (o == node) continue;
				
				if (o instanceof DefaultMutableTreeNode) {
					
					DefaultMutableTreeNode comp = ((DefaultMutableTreeNode)o);
					
					TreePath p = new TreePath(comp.getPath());
					
					tree.collapsePath(p);
					
				}
			}
		}
	}
	
	private void initAll(DefaultMutableTreeNode parent) {
		parent.setUserObject(new SimpleTreeObject(Resources.ICN_SIDEBAR_ALL.get(), LocaleBundle.getString("FilterTree.All"), e ->  //$NON-NLS-1$
		{
			table.setRowFilter(null, RowFilterSource.SIDEBAR);
			collapseAll();
		}));
	}
	
	private void initViewed(DefaultMutableTreeNode parent) {
		addNodeF(parent, Resources.ICN_SIDEBAR_VIEWED, LocaleBundle.getString("FilterTree.Viewed.Viewed"), () -> CustomExtendedViewedFilter.create(ExtendedViewedStateType.VIEWED)); //$NON-NLS-1$

		if (CCProperties.getInstance().PROP_SHOW_PARTIAL_VIEWED_STATE.getValue()) 
			addNodeF(parent, Resources.ICN_SIDEBAR_PARTIALLY, LocaleBundle.getString("FilterTree.Viewed.Partial"), () -> CustomExtendedViewedFilter.create(ExtendedViewedStateType.PARTIAL_VIEWED)); //$NON-NLS-1$
		
		addNodeF(parent, Resources.ICN_SIDEBAR_UNVIEWED, LocaleBundle.getString("FilterTree.Viewed.Unviewed"), () -> CustomExtendedViewedFilter.create(ExtendedViewedStateType.NOT_VIEWED)); //$NON-NLS-1$

		addNodeF(parent, Resources.ICN_SIDEBAR_LATER, LocaleBundle.getString("FilterTree.Viewed.Later"), () -> CustomExtendedViewedFilter.create(ExtendedViewedStateType.MARKED_FOR_LATER)); //$NON-NLS-1$

		addNodeF(parent, Resources.ICN_SIDEBAR_AGAIN, LocaleBundle.getString("FilterTree.Viewed.Again"), () -> CustomExtendedViewedFilter.create(ExtendedViewedStateType.MARKED_FOR_AGAIN)); //$NON-NLS-1$

		addNodeF(parent, Resources.ICN_SIDEBAR_NEVER, LocaleBundle.getString("FilterTree.Viewed.Never"), () -> CustomExtendedViewedFilter.create(ExtendedViewedStateType.MARKED_FOR_NEVER)); //$NON-NLS-1$
	}
	
	private void initFSK(DefaultMutableTreeNode parent) {
		for (final CCFSK fsk : CCFSK.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, fsk.getIcon(), fsk.asString(), () -> CustomFSKFilter.create(fsk));
		}
	}

	private void initQuality(DefaultMutableTreeNode parent) {
		for (final CCQualityCategoryType qual : CCQualityCategoryType.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, qual.getIcon(), qual.asString(), () -> CustomQualityCategoryTypeFilter.create(qual));
		}
	}

	private void initScore(DefaultMutableTreeNode parent) {
		for (final CCUserScore score : CCUserScore.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, score.getIcon(), score.asString(), () -> CustomUserScoreFilter.create(score));
		}
	}
	
	private void initOnlineScore(DefaultMutableTreeNode parent) {
		for (final CCOnlineScore oscore : CCOnlineScore.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, oscore.getIcon(), oscore.asInt()/2.0+"", () -> CustomOnlinescoreFilter.create(oscore)); //$NON-NLS-1$
		}
	}
	
	private void initFormat(DefaultMutableTreeNode parent) {
		for (final CCFileFormat format : CCFileFormat.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, format.getIcon(), format.asString(), () -> CustomFormatFilter.create(format));
		}
	}
	
	private void initTags(DefaultMutableTreeNode parent) {
		for (int i = 0; i < CCTagList.ACTIVETAGS; i++) {
			final int curr = i;
			addNodeF(parent, CCTagList.getOnIcon(i), CCTagList.getName(i), () -> CustomTagFilter.create(curr));
		}
	}
	
	private void initLanguage(DefaultMutableTreeNode parent) {
		for (final CCDBLanguage language : CCDBLanguage.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, language.getIcon(), language.asString(), () -> CustomLanguageFilter.create(language));
		}
	}
	
	private void initTyp(DefaultMutableTreeNode parent) {
		for (final CCDBElementTyp typ : CCDBElementTyp.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, typ.getIcon(), typ.asString(), () -> CustomTypFilter.create(typ));
		}
	}
	
	private void initZyklus(DefaultMutableTreeNode parent) {
		for (final String zyklus : movielist.getZyklusList()) {
			addNodeF(parent, (Icon)null, zyklus, () -> CustomZyklusFilter.create(zyklus));
		}
	}
	
	private void initGroups(DefaultMutableTreeNode parent) {
		List<CCGroup> groups_list = CCStreams.iterate(movielist.getGroupList()).autosortByProperty(p -> p.Order).enumerate();
		Map<String, DefaultMutableTreeNode> groups_done = new HashMap<>();
		groups_done.put("", parent); //$NON-NLS-1$
		
		for(int i = 0; i < 12; i++) {
			
			for (final CCGroup group : new ArrayList<>(groups_list)) {
				DefaultMutableTreeNode pp = groups_done.get(group.Parent);
				if (pp == null) continue;
				
				groups_list.remove(group);
				DefaultMutableTreeNode n = addNodeF(pp, (Icon)null, group.Name, () -> CustomGroupFilter.create(group, true));
				groups_done.put(group.Name, n);
			}
			
		}
	}
	
	private void initGenre(DefaultMutableTreeNode parent) {
		if (movielist.isBlocked()) {
			return;
		}
		
		List<CCGenre> genres = movielist.getGenreList();
		
		genres.sort(CCGenre.getTextComparator());
		
		for (final CCGenre genre : genres) {
			addNodeF(parent, (Icon)null, genre.asString(), () -> CustomGenreFilter.create(genre));
		}
	}
	
	private void initYear(DefaultMutableTreeNode parent) {
		for (final Integer year : movielist.getYearList()) {
			addNodeF(parent, (Icon)null, year+"", () -> CustomYearFilter.create(year)); //$NON-NLS-1$
		}
	}
	
	private void initCustom(DefaultMutableTreeNode parent) {
		for (int i = 0; i < customFilterList.size(); i++) {
			final CustomFilterObject fo = customFilterList.get(i);
			addNodeF(parent, Resources.ICN_SIDEBAR_CUSTOM, fo.getName(), () -> fo.getFilter());
		}
		
		addNode(parent, Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom.OrganizeFilter"), e -> onOrganizeCustomFilterClicked()); //$NON-NLS-1$
		
		addNode(parent, Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom.NewFilter"), e -> onNewCustomFilterClicked()); //$NON-NLS-1$
	}
	
	private void onOrganizeCustomFilterClicked() {
		new OrganizeFilterDialog(movielist, table.getMainFrame(), customFilterList, new FinishListener() {
			@Override
			public void finish() {
				updateTree();
				
				customFilterList.save();
			}
		}).setVisible(true);
	}
	
	private void onNewCustomFilterClicked() {
		CustomFilterObject cfo = new CustomFilterObject(CustomFilterList.NAME_TEMPORARY, new CustomAndOperator());
		
		TableCustomFilter tcf = table.getRowFilter();
		if (tcf != null) {
			
			AbstractCustomFilter acf = tcf.getFilter();
			
			if (acf instanceof CustomAndOperator) {
				cfo = new CustomFilterObject(CustomFilterList.NAME_TEMPORARY, ((CustomAndOperator)tcf.getFilter().createCopy()));
			} else {
				cfo = new CustomFilterObject(CustomFilterList.NAME_TEMPORARY, new CustomAndOperator(tcf.getFilter().createCopy()));
			}
			
		}
		
		final CustomFilterObject fcfilter = cfo;
		
		new CustomFilterEditDialog(table.getMainFrame(), movielist, fcfilter, new FinishListener() {
			@Override
			public void finish() {
				table.setRowFilter(fcfilter.getFilter(), RowFilterSource.SIDEBAR);
				
				if (! fcfilter.getName().equals(CustomFilterList.NAME_TEMPORARY) && !customFilterList.contains(fcfilter)) {
					customFilterList.add(fcfilter);
					updateTree();
					customFilterList.save();
				}
			}
		}).setVisible(true);
	}
	
	protected DefaultMutableTreeNode addNodeF(DefaultMutableTreeNode aroot, IconRef icon, String txt, Func0to1<AbstractCustomFilter> filter) {
		return addNodeF(aroot, icon.get(), txt, filter);
	}
	
	protected DefaultMutableTreeNode addNodeF(DefaultMutableTreeNode aroot, Icon icon, String txt, Func0to1<AbstractCustomFilter> filter) {
		if (aroot == null) aroot = root;
		
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new SimpleTreeObject(icon, txt, e -> 
		{
			if (e.ctrlDown) {
				
				CustomAndOperator op = new CustomAndOperator();
				
				TableCustomFilter tcf = table.getRowFilter();
				if (tcf != null) {
					
					AbstractCustomFilter acf = tcf.getFilter();
					
					if (acf instanceof CustomAndOperator) {
						op = ((CustomAndOperator)tcf.getFilter().createCopy());
					} else {
						op = new CustomAndOperator(tcf.getFilter().createCopy());
					}
					
				}
				
				op.combineWith(filter.invoke());

				table.setRowFilter(op, RowFilterSource.SIDEBAR);
			} else {
				table.setRowFilter(filter.invoke(), RowFilterSource.SIDEBAR);
			}
			
		}));
		
		aroot.add(node);
		return node;
	}
}
