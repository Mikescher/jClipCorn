package de.jClipCorn.gui.frames.mainFrame.filterTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

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
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTable;
import de.jClipCorn.gui.frames.mainFrame.clipTable.RowFilterSource;
import de.jClipCorn.gui.frames.organizeFilterFrame.OrganizeFilterDialog;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeObject;
import de.jClipCorn.gui.guiComponents.tableFilter.AbstractCustomFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomFSKFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomFormatFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomGenreFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomGroupFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomLanguageFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomOnlinescoreFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomQualityFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomUserScoreFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomTagFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomTypFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomViewedFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomYearFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomZyklusFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilterDialogs.CustomOperatorFilterDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.IconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.listener.FinishListener;

public class FilterTree extends AbstractFilterTree {
	private static final long serialVersionUID = 592519777667038909L;

	private CustomFilterList customFilterList = new CustomFilterList();
	
	private final ClipTable table;
	private final CCMovieList movielist;
	
	public FilterTree(CCMovieList list, ClipTable table) {
		super(list);
		this.table = table;
		this.movielist = list;
		
		customFilterList.load();
	}

	@Override
	protected void addFields() {
		DefaultMutableTreeNode node_all = addNodeI(null, null, null, null);
		initAll(node_all);
		
		DefaultMutableTreeNode node_zyklus = addNode(null, Resources.ICN_SIDEBAR_ZYKLUS, LocaleBundle.getString("FilterTree.Zyklus"), null); //$NON-NLS-1$
		initZyklus(node_zyklus);
		
		if (movielist.getGroupList().size() > 0) {
			DefaultMutableTreeNode node_groups = addNode(null, Resources.ICN_SIDEBAR_GROUPS, LocaleBundle.getString("FilterTree.Groups"), null); //$NON-NLS-1$
			initGroups(node_groups);
		}
		
		DefaultMutableTreeNode node_genre = addNode(null, Resources.ICN_SIDEBAR_GENRE, LocaleBundle.getString("FilterTree.Genre"), null); //$NON-NLS-1$
		initGenre(node_genre);
		
		DefaultMutableTreeNode node_onlinescore = addNode(null, Resources.ICN_SIDEBAR_ONLINESCORE, LocaleBundle.getString("FilterTree.IMDB"), null); //$NON-NLS-1$
		initOnlineScore(node_onlinescore);
		
		DefaultMutableTreeNode node_score = addNode(null, Resources.ICN_SIDEBAR_SCORE, LocaleBundle.getString("FilterTree.Score"), null); //$NON-NLS-1$
		initScore(node_score);
		
		DefaultMutableTreeNode node_fsk = addNode(null, Resources.ICN_TABLE_FSK_2, LocaleBundle.getString("FilterTree.FSK"), null); //$NON-NLS-1$
		initFSK(node_fsk);
		
		DefaultMutableTreeNode node_year = addNode(null, Resources.ICN_SIDEBAR_YEAR, LocaleBundle.getString("FilterTree.Year"), null); //$NON-NLS-1$
		initYear(node_year);
		
		DefaultMutableTreeNode node_format = addNode(null, Resources.ICN_SIDEBAR_FORMAT, LocaleBundle.getString("FilterTree.Format"), null); //$NON-NLS-1$
		initFormat(node_format);
		
		DefaultMutableTreeNode node_quality = addNode(null, Resources.ICN_SIDEBAR_QUALITY, LocaleBundle.getString("FilterTree.Quality"), null); //$NON-NLS-1$
		initQuality(node_quality);
		
		DefaultMutableTreeNode node_tags = addNode(null, Resources.ICN_SIDEBAR_TAGS, LocaleBundle.getString("FilterTree.Tags"), null); //$NON-NLS-1$
		initTags(node_tags);
		
		DefaultMutableTreeNode node_language = addNode(null, Resources.ICN_SIDEBAR_LANGUAGE, LocaleBundle.getString("FilterTree.Language"), null); //$NON-NLS-1$
		initLanguage(node_language);
		
		DefaultMutableTreeNode node_typ = addNode(null, Resources.ICN_SIDEBAR_TYP, LocaleBundle.getString("FilterTree.Type"), null); //$NON-NLS-1$
		initTyp(node_typ);
		
		DefaultMutableTreeNode node_viewed = addNode(null, Resources.ICN_SIDEBAR_VIEWED, LocaleBundle.getString("FilterTree.Viewed"), null); //$NON-NLS-1$
		initViewed(node_viewed);

		DefaultMutableTreeNode node_custom = addNode(null,  Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom"), null); //$NON-NLS-1$
		initCustom(node_custom);
	}
	
	private void initAll(DefaultMutableTreeNode parent) {
		parent.setUserObject(new SimpleTreeObject(CachedResourceLoader.getIcon(Resources.ICN_SIDEBAR_ALL), LocaleBundle.getString("FilterTree.All"), new ActionListener() { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent arg0) {
				table.setRowFilter(null, RowFilterSource.SIDEBAR);
				collapseAll();
			}
		}));
	}
	
	private void initViewed(DefaultMutableTreeNode parent) {
		addNodeF(parent, Resources.ICN_SIDEBAR_VIEWED, LocaleBundle.getString("FilterTree.Viewed.Viewed"), () -> CustomViewedFilter.create(ExtendedViewedStateType.VIEWED)); //$NON-NLS-1$

		if (CCProperties.getInstance().PROP_SHOW_PARTIAL_VIEWED_STATE.getValue())
			addNodeF(parent, Resources.ICN_SIDEBAR_PARTIALLY, LocaleBundle.getString("FilterTree.Viewed.Partial"), () -> CustomViewedFilter.create(ExtendedViewedStateType.PARTIAL_VIEWED)); //$NON-NLS-1$
		
		addNodeF(parent, Resources.ICN_SIDEBAR_UNVIEWED, LocaleBundle.getString("FilterTree.Viewed.Unviewed"), () -> CustomViewedFilter.create(ExtendedViewedStateType.NOT_VIEWED)); //$NON-NLS-1$

		addNodeF(parent, Resources.ICN_SIDEBAR_LATER, LocaleBundle.getString("FilterTree.Viewed.Later"), () -> CustomViewedFilter.create(ExtendedViewedStateType.MARKED_FOR_LATER)); //$NON-NLS-1$

		addNodeF(parent, Resources.ICN_SIDEBAR_NEVER, LocaleBundle.getString("FilterTree.Viewed.Never"), () -> CustomViewedFilter.create(ExtendedViewedStateType.MARKED_FOR_NEVER)); //$NON-NLS-1$
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
		
		addNode(parent, Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom.OrganizeFilter"), new ActionListener() { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent e) {
				onOrganizeCustomFilterClicked();
			}
		});
		
		addNode(parent, Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom.NewFilter"), new ActionListener() { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent e) {
				onNewCustomFilterClicked();
			}
		});
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
		final CustomAndOperator cfilter = new CustomAndOperator();
		
		new CustomOperatorFilterDialog(movielist, cfilter, new FinishListener() {
			@Override
			public void finish() {
				table.setRowFilter(cfilter, RowFilterSource.SIDEBAR);
			}
		}, table.getMainFrame(), true).setVisible(true);
	}
	
	protected DefaultMutableTreeNode addNodeF(DefaultMutableTreeNode aroot, IconRef icon, String txt, Supplier<AbstractCustomFilter> filter) {
		return addNodeF(aroot, CachedResourceLoader.getIcon(icon), txt, filter);
	}
	
	protected DefaultMutableTreeNode addNodeF(DefaultMutableTreeNode aroot, Icon icon, String txt, Supplier<AbstractCustomFilter> filter) {
		if (aroot == null) {
			aroot = root;
		}
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(new SimpleTreeObject(icon, txt, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.setRowFilter(filter.get(), RowFilterSource.SIDEBAR);
			}
		}));
		aroot.add(node);
		return node;
	}
}
