package de.jClipCorn.gui.frames.mainFrame.filterTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.gui.frames.customFilterEditDialog.CustomFilterEditDialog;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTable;
import de.jClipCorn.gui.frames.mainFrame.clipTable.RowFilterSource;
import de.jClipCorn.gui.frames.organizeFilterFrame.OrganizeFilterDialog;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeObject;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeObject.SimpleTreeEvent;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpletreeActionMode;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.IconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.TableCustomFilter;
import de.jClipCorn.table.filter.customFilter.CustomExtendedViewedFilter;
import de.jClipCorn.table.filter.customFilter.CustomFSKFilter;
import de.jClipCorn.table.filter.customFilter.CustomFormatFilter;
import de.jClipCorn.table.filter.customFilter.CustomGenreFilter;
import de.jClipCorn.table.filter.customFilter.CustomGroupFilter;
import de.jClipCorn.table.filter.customFilter.CustomLanguageFilter;
import de.jClipCorn.table.filter.customFilter.CustomOnlinescoreFilter;
import de.jClipCorn.table.filter.customFilter.CustomQualityFilter;
import de.jClipCorn.table.filter.customFilter.CustomTagFilter;
import de.jClipCorn.table.filter.customFilter.CustomTypFilter;
import de.jClipCorn.table.filter.customFilter.CustomUserScoreFilter;
import de.jClipCorn.table.filter.customFilter.CustomYearFilter;
import de.jClipCorn.table.filter.customFilter.CustomZyklusFilter;
import de.jClipCorn.table.filter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.stream.CCStreams;

public class FilterTree extends AbstractFilterTree {
	private static final long serialVersionUID = 592519777667038909L;

	private CustomFilterList customFilterList = new CustomFilterList();
	
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
		DefaultMutableTreeNode node_all = addNodeI(null, null, null, null);
		initAll(node_all);
		
		DefaultMutableTreeNode node_zyklus = addNode(null, Resources.ICN_SIDEBAR_ZYKLUS, LocaleBundle.getString("FilterTree.Zyklus"), this::expand); //$NON-NLS-1$
		initZyklus(node_zyklus);
		
		if (movielist.getGroupList().size() > 0) {
			DefaultMutableTreeNode node_groups = addNode(null, Resources.ICN_SIDEBAR_GROUPS, LocaleBundle.getString("FilterTree.Groups"), this::expand); //$NON-NLS-1$
			initGroups(node_groups);
		}
		
		DefaultMutableTreeNode node_genre = addNode(null, Resources.ICN_SIDEBAR_GENRE, LocaleBundle.getString("FilterTree.Genre"), this::expand); //$NON-NLS-1$
		initGenre(node_genre);
		
		DefaultMutableTreeNode node_onlinescore = addNode(null, Resources.ICN_SIDEBAR_ONLINESCORE, LocaleBundle.getString("FilterTree.IMDB"), this::expand); //$NON-NLS-1$
		initOnlineScore(node_onlinescore);
		
		DefaultMutableTreeNode node_score = addNode(null, Resources.ICN_SIDEBAR_SCORE, LocaleBundle.getString("FilterTree.Score"), this::expand); //$NON-NLS-1$
		initScore(node_score);
		
		DefaultMutableTreeNode node_fsk = addNode(null, Resources.ICN_TABLE_FSK_2, LocaleBundle.getString("FilterTree.FSK"), this::expand); //$NON-NLS-1$
		initFSK(node_fsk);
		
		DefaultMutableTreeNode node_year = addNode(null, Resources.ICN_SIDEBAR_YEAR, LocaleBundle.getString("FilterTree.Year"), this::expand); //$NON-NLS-1$
		initYear(node_year);
		
		DefaultMutableTreeNode node_format = addNode(null, Resources.ICN_SIDEBAR_FORMAT, LocaleBundle.getString("FilterTree.Format"), this::expand); //$NON-NLS-1$
		initFormat(node_format);
		
		DefaultMutableTreeNode node_quality = addNode(null, Resources.ICN_SIDEBAR_QUALITY, LocaleBundle.getString("FilterTree.Quality"), this::expand); //$NON-NLS-1$
		initQuality(node_quality);
		
		DefaultMutableTreeNode node_tags = addNode(null, Resources.ICN_SIDEBAR_TAGS, LocaleBundle.getString("FilterTree.Tags"), this::expand); //$NON-NLS-1$
		initTags(node_tags);
		
		DefaultMutableTreeNode node_language = addNode(null, Resources.ICN_SIDEBAR_LANGUAGE, LocaleBundle.getString("FilterTree.Language"), this::expand); //$NON-NLS-1$
		initLanguage(node_language);
		
		DefaultMutableTreeNode node_typ = addNode(null, Resources.ICN_SIDEBAR_TYP, LocaleBundle.getString("FilterTree.Type"), this::expand); //$NON-NLS-1$
		initTyp(node_typ);
		
		DefaultMutableTreeNode node_viewed = addNode(null, Resources.ICN_SIDEBAR_VIEWED, LocaleBundle.getString("FilterTree.Viewed"), this::expand); //$NON-NLS-1$
		initViewed(node_viewed);

		DefaultMutableTreeNode node_custom = addNode(null,  Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom"), this::expand); //$NON-NLS-1$
		initCustom(node_custom);
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
		parent.setUserObject(new SimpleTreeObject(CachedResourceLoader.getIcon(Resources.ICN_SIDEBAR_ALL), LocaleBundle.getString("FilterTree.All"), e ->  //$NON-NLS-1$
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
		for (final CCFSK fsk : CCFSK.values()) {
			addNodeF(parent, fsk.getIcon(), fsk.asString(), () -> CustomFSKFilter.create(fsk));
		}
	}
	
	private void initScore(DefaultMutableTreeNode parent) {
		for (final CCUserScore score : CCUserScore.values()) {
			addNodeF(parent, score.getIcon(), score.asString(), () -> CustomUserScoreFilter.create(score));
		}
	}
	
	private void initOnlineScore(DefaultMutableTreeNode parent) {
		for (final CCOnlineScore oscore : CCOnlineScore.values()) {
			addNodeF(parent, oscore.getIcon(), oscore.asInt()/2.0+"", () -> CustomOnlinescoreFilter.create(oscore)); //$NON-NLS-1$
		}
	}
	
	private void initFormat(DefaultMutableTreeNode parent) {
		for (final CCFileFormat format : CCFileFormat.values()) {
			addNodeF(parent, format.getIcon(), format.asString(), () -> CustomFormatFilter.create(format));
		}
	}
	
	private void initQuality(DefaultMutableTreeNode parent) {
		for (final CCQuality quality : CCQuality.values()) {
			addNodeF(parent, quality.getIcon(), quality.asString(), () -> CustomQualityFilter.create(quality));
		}
	}
	
	private void initTags(DefaultMutableTreeNode parent) {
		for (int i = 0; i < CCTagList.ACTIVETAGS; i++) {
			final int curr = i;
			addNodeF(parent, CCTagList.getOnIcon(i), CCTagList.getName(i), () -> CustomTagFilter.create(curr));
		}
	}
	
	private void initLanguage(DefaultMutableTreeNode parent) {
		for (final CCDBLanguage language : CCDBLanguage.values()) {
			addNodeF(parent, language.getIcon(), language.asString(), () -> CustomLanguageFilter.create(language));
		}
	}
	
	private void initTyp(DefaultMutableTreeNode parent) {
		for (final CCDBElementTyp typ : CCDBElementTyp.values()) {
			addNodeF(parent, typ.getIcon(), typ.asString(), () -> CustomTypFilter.create(typ));
		}
	}
	
	private void initZyklus(DefaultMutableTreeNode parent) {
		for (final String zyklus : movielist.getZyklusList()) {
			addNodeF(parent, (Icon)null, zyklus, () -> CustomZyklusFilter.create(zyklus));
		}
	}
	
	private void initGroups(DefaultMutableTreeNode parent) {
		List<CCGroup> groups_list = movielist.getGroupList();
		Map<String, DefaultMutableTreeNode> groups_done = new HashMap<>();
		groups_done.put("", parent); //$NON-NLS-1$
		
		for(int i = 0; i < 12; i++) {
			
			for (final CCGroup group : new ArrayList<>(groups_list)) {
				DefaultMutableTreeNode pp = groups_done.get(group.Parent);
				if (pp == null) continue;
				
				groups_list.remove(group);
				DefaultMutableTreeNode n = addNodeF(pp, (Icon)null, group.Name, () -> CustomGroupFilter.create(group));
				groups_done.put(group.Name, n);
			}
			
		}
		
		for (final CCGroup group : movielist.getGroupList()) {
			addNodeF(parent, (Icon)null, group.Name, () -> CustomGroupFilter.create(group));
		}
	}
	
	private void initGenre(DefaultMutableTreeNode parent) {
		if (CCMovieList.isBlocked()) {
			return;
		}
		
		List<CCGenre> genres = movielist.getGenreList();
		
		Collections.sort(genres, CCGenre.getTextComparator());
		
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
	
	protected DefaultMutableTreeNode addNodeF(DefaultMutableTreeNode aroot, IconRef icon, String txt, Supplier<AbstractCustomFilter> filter) {
		return addNodeF(aroot, CachedResourceLoader.getIcon(icon), txt, filter);
	}
	
	protected DefaultMutableTreeNode addNodeF(DefaultMutableTreeNode aroot, Icon icon, String txt, Supplier<AbstractCustomFilter> filter) {
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
				
				op.combineWith(filter.get());

				table.setRowFilter(op, RowFilterSource.SIDEBAR);
			} else {
				table.setRowFilter(filter.get(), RowFilterSource.SIDEBAR);
			}
			
		}));
		
		aroot.add(node);
		return node;
	}
}
