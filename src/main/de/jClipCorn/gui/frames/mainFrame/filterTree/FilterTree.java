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
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.CustomScoreFilter;
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
import de.jClipCorn.util.listener.FinishListener;

public class FilterTree extends AbstractFilterTree {
	private static final long serialVersionUID = 592519777667038909L;

	private CustomFilterList customFilterList = new CustomFilterList();
	
	private DefaultMutableTreeNode node_all;
	private DefaultMutableTreeNode node_zyklus;
	private DefaultMutableTreeNode node_groups;
	private DefaultMutableTreeNode node_genre;
	private DefaultMutableTreeNode node_onlinescore;
	private DefaultMutableTreeNode node_score;
	private DefaultMutableTreeNode node_fsk;
	private DefaultMutableTreeNode node_year;
	private DefaultMutableTreeNode node_format;
	private DefaultMutableTreeNode node_quality;
	private DefaultMutableTreeNode node_tags;
	private DefaultMutableTreeNode node_language;
	private DefaultMutableTreeNode node_typ;
	private DefaultMutableTreeNode node_viewed;
	private DefaultMutableTreeNode node_custom;

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
		node_all = addNodeI(null, null, null, null);
		initAll();
		
		node_zyklus = addNode(null, Resources.ICN_SIDEBAR_ZYKLUS, LocaleBundle.getString("FilterTree.Zyklus"), null); //$NON-NLS-1$
		initZyklus();
		
		node_groups = addNode(null, Resources.ICN_SIDEBAR_GROUPS, LocaleBundle.getString("FilterTree.Groups"), null); //$NON-NLS-1$
		initGroups(); //TODO HIDE IF NO ELEMENTS
		
		node_genre = addNode(null, Resources.ICN_SIDEBAR_GENRE, LocaleBundle.getString("FilterTree.Genre"), null); //$NON-NLS-1$
		initGenre();
		
		node_onlinescore = addNode(null, Resources.ICN_SIDEBAR_ONLINESCORE, LocaleBundle.getString("FilterTree.IMDB"), null); //$NON-NLS-1$
		initOnlineScore();
		
		node_score = addNode(null, Resources.ICN_SIDEBAR_SCORE, LocaleBundle.getString("FilterTree.Score"), null); //$NON-NLS-1$
		initScore(); //TODO HIDE IF NO ELEMENTS
		
		node_fsk = addNode(null, Resources.ICN_TABLE_FSK_2, LocaleBundle.getString("FilterTree.FSK"), null); //$NON-NLS-1$
		initFSK();
		
		node_year = addNode(null, Resources.ICN_SIDEBAR_YEAR, LocaleBundle.getString("FilterTree.Year"), null); //$NON-NLS-1$
		initYear();
		
		node_format = addNode(null, Resources.ICN_SIDEBAR_FORMAT, LocaleBundle.getString("FilterTree.Format"), null); //$NON-NLS-1$
		initFormat();
		
		node_quality = addNode(null, Resources.ICN_SIDEBAR_QUALITY, LocaleBundle.getString("FilterTree.Quality"), null); //$NON-NLS-1$
		initQuality();
		
		node_tags = addNode(null, Resources.ICN_SIDEBAR_TAGS, LocaleBundle.getString("FilterTree.Tags"), null); //$NON-NLS-1$
		initTags();
		
		node_language = addNode(null, Resources.ICN_SIDEBAR_LANGUAGE, LocaleBundle.getString("FilterTree.Language"), null); //$NON-NLS-1$
		initLanguage();
		
		node_typ = addNode(null, Resources.ICN_SIDEBAR_TYP, LocaleBundle.getString("FilterTree.Type"), null); //$NON-NLS-1$
		initTyp();
		
		node_viewed = addNode(null, Resources.ICN_SIDEBAR_VIEWED, LocaleBundle.getString("FilterTree.Viewed"), null); //$NON-NLS-1$
		initViewed();

		node_custom = addNode(null,  Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom"), null); //$NON-NLS-1$
		initCustom();
	}
	
	private void initAll() {
		node_all.setUserObject(new SimpleTreeObject(CachedResourceLoader.getIcon(Resources.ICN_SIDEBAR_ALL), LocaleBundle.getString("FilterTree.All"), new ActionListener() { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent arg0) {
				table.setRowFilter(null, RowFilterSource.SIDEBAR);
				collapseAll();
			}
		}));
	}
	
	private void initViewed() {
		addNodeF(node_viewed, Resources.ICN_SIDEBAR_VIEWED, LocaleBundle.getString("FilterTree.Viewed.Viewed"), () -> CustomViewedFilter.create(true)); //$NON-NLS-1$
		
		addNodeF(node_viewed, Resources.ICN_SIDEBAR_UNVIEWED, LocaleBundle.getString("FilterTree.Viewed.Unviewed"), () -> CustomViewedFilter.create(false)); //$NON-NLS-1$
	}
	
	private void initFSK() {
		for (final CCFSK fsk : CCFSK.values()) {
			addNodeF(node_fsk, fsk.getIcon(), fsk.asString(), () -> CustomFSKFilter.create(fsk));
		}
	}
	
	private void initScore() {
		for (final CCUserScore score : CCUserScore.values()) {
			addNodeF(node_score, score.getIcon(), score.asString(), () -> CustomScoreFilter.create(score));
		}
	}
	
	private void initOnlineScore() {
		for (final CCOnlineScore oscore : CCOnlineScore.values()) {
			addNodeF(node_onlinescore, oscore.getIcon(), oscore.asInt()/2.0+"", () -> CustomOnlinescoreFilter.create(oscore)); //$NON-NLS-1$
		}
	}
	
	private void initFormat() {
		for (final CCFileFormat format : CCFileFormat.values()) {
			addNodeF(node_format, format.getIcon(), format.asString(), () -> CustomFormatFilter.create(format));
		}
	}
	
	private void initQuality() {
		for (final CCQuality quality : CCQuality.values()) {
			addNodeF(node_quality, quality.getIcon(), quality.asString(), () -> CustomQualityFilter.create(quality));
		}
	}
	
	private void initTags() {
		for (int i = 0; i < CCTagList.ACTIVETAGS; i++) {
			final int curr = i;
			addNodeF(node_tags, CCTagList.getOnIcon(i), CCTagList.getName(i), () -> CustomTagFilter.create(curr));
		}
	}
	
	private void initLanguage() {
		for (final CCDBLanguage language : CCDBLanguage.values()) {
			addNodeF(node_language, language.getIcon(), language.asString(), () -> CustomLanguageFilter.create(language));
		}
	}
	
	private void initTyp() {
		for (final CCDBElementTyp typ : CCDBElementTyp.values()) {
			addNodeF(node_typ, typ.getIcon(), typ.asString(), () -> CustomTypFilter.create(typ));
		}
	}
	
	private void initZyklus() {
		for (final String zyklus : movielist.getZyklusList()) {
			addNodeF(node_zyklus, (Icon)null, zyklus, () -> CustomZyklusFilter.create(zyklus));
		}
	}
	
	private void initGroups() {
		for (final CCGroup group : movielist.getGroupList()) {
			addNodeF(node_groups, (Icon)null, group.Name, () -> CustomGroupFilter.create(group));
		}
	}
	
	private void initGenre() {
		if (CCMovieList.isBlocked()) {
			return;
		}
		
		List<CCGenre> genres = movielist.getGenreList();
		
		Collections.sort(genres, CCGenre.getTextComparator());
		
		for (final CCGenre genre : genres) {
			addNodeF(node_genre, (Icon)null, genre.asString(), () -> CustomGenreFilter.create(genre));
		}
	}
	
	private void initYear() {
		for (final Integer year : movielist.getYearList()) {
			addNodeF(node_year, (Icon)null, year+"", () -> CustomYearFilter.create(year)); //$NON-NLS-1$
		}
	}
	
	private void initCustom() {
		for (int i = 0; i < customFilterList.size(); i++) {
			final CustomFilterObject fo = customFilterList.get(i);
			addNodeF(node_custom, Resources.ICN_SIDEBAR_CUSTOM, fo.getName(), () -> fo.getFilter());
		}
		
		addNode(node_custom, Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom.OrganizeFilter"), new ActionListener() { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent e) {
				onOrganizeCustomFilterClicked();
			}
		});
		
		addNode(node_custom, Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom.NewFilter"), new ActionListener() { //$NON-NLS-1$
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
