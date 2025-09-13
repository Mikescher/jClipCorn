package de.jClipCorn.gui.mainFrame.filterTree;

import com.jformdesigner.annotations.DesignCreate;
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
import de.jClipCorn.gui.mainFrame.table.ClipTable;
import de.jClipCorn.gui.mainFrame.table.RowFilterSource;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.*;

public class FilterTree extends AbstractFilterTree {
	private static final long serialVersionUID = 592519777667038909L;

	private final CustomFilterList customFilterList;
	
	private ClipTable table;
	private final CCMovieList movielist;

	@DesignCreate
	private static FilterTree designCreate() { return new FilterTree(); }

	private FilterTree() { super(null); this.customFilterList = null; this.movielist = null; }

	public FilterTree(CCMovieList list) {
		super(list);
		this.customFilterList = new CustomFilterList(list);
		this.movielist = list;
		
		this.tree.ActionMode = SimpletreeActionMode.OnClick;
		
		customFilterList.load();
	}

	public void init(ClipTable table) {
		this.table = table;
	}

	@Override
	protected void addFields() {
		initAll(addNodeI(null, null, null, null));
		
		initZyklus(addNode(null, Resources.ICN_SIDEBAR_ZYKLUS, LocaleBundle.getString("FilterTree.Zyklus"), this::parentClicked)); //$NON-NLS-1$
		
		if (!movielist.getGroupList().isEmpty()) {
			initGroups(addNode(null, Resources.ICN_SIDEBAR_GROUPS, LocaleBundle.getString("FilterTree.Groups"), this::parentClicked)); //$NON-NLS-1$
		}

		if (!movielist.getSpecialVersionList().isEmpty()) {
			initSpecialVersion(addNode(null, Resources.ICN_SIDEBAR_SPECIALVERSION, LocaleBundle.getString("FilterTree.SpecialVersion"), this::parentClicked)); //$NON-NLS-1$
		}

		if (!movielist.getAnimeSeasonList().isEmpty()) {
			initAnimeSeason(addNode(null, Resources.ICN_SIDEBAR_ANIMESEASON, LocaleBundle.getString("FilterTree.AnimeSeason"), this::parentClicked)); //$NON-NLS-1$
		}

		if (!movielist.getAnimeStudioList().isEmpty()) {
			initAnimeStudio(addNode(null, Resources.ICN_SIDEBAR_ANIMESTUDIO, LocaleBundle.getString("FilterTree.AnimeStudio"), this::parentClicked)); //$NON-NLS-1$
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

		initSubtitles(addNode(null, Resources.ICN_SIDEBAR_SUBTITLES, LocaleBundle.getString("FilterTree.Subtitles"), this::parentClicked)); //$NON-NLS-1$

		initTyp(addNode(null, Resources.ICN_SIDEBAR_TYP, LocaleBundle.getString("FilterTree.Type"), this::parentClicked)); //$NON-NLS-1$
		
		initViewed(addNode(null, Resources.ICN_SIDEBAR_VIEWED, LocaleBundle.getString("FilterTree.Viewed"), this::parentClicked)); //$NON-NLS-1$

		initCustom(addNode(null, Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom"), this::parentClicked)); //$NON-NLS-1$
	}

	private void parentClicked(SimpleTreeEvent evt) {

		if (movielist.ccprops().PROP_MAINFRAME_FILTERTREE_RECOLLAPSE.getValue())
		{
			if (tree.isExpanded(evt.path)) tree.collapsePath(evt.path);
			else expand(evt);
		}
		else
		{
			expand(evt);
		}
	}

	private void expand(SimpleTreeEvent evt) {
		tree.expandPath(evt.path);
		
		if (evt.path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)evt.path.getLastPathComponent();
			
			for (TreeNode o : CCStreams.iterate(node.depthFirstEnumeration())) {
				
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
		parent.setUserObject(new SimpleTreeObject(Resources.ICN_SIDEBAR_ALL.get16x16(), LocaleBundle.getString("FilterTree.All"), e ->  //$NON-NLS-1$
		{
			table.setRowFilter(null, RowFilterSource.SIDEBAR, true);
			collapseAll();
		}));
	}
	
	private void initViewed(DefaultMutableTreeNode parent) {
		addNodeF(parent, Resources.ICN_SIDEBAR_VIEWED.get16x16(), LocaleBundle.getString("FilterTree.Viewed.Viewed"), () -> CustomExtendedViewedFilter.create(movielist, ExtendedViewedStateType.VIEWED)); //$NON-NLS-1$

		if (movielist.ccprops().PROP_SHOW_PARTIAL_VIEWED_STATE.getValue()) 
			addNodeF(parent, Resources.ICN_SIDEBAR_PARTIALLY.get16x16(), LocaleBundle.getString("FilterTree.Viewed.Partial"), () -> CustomExtendedViewedFilter.create(movielist, ExtendedViewedStateType.PARTIAL_VIEWED)); //$NON-NLS-1$
		
		addNodeF(parent, Resources.ICN_SIDEBAR_UNVIEWED.get16x16(), LocaleBundle.getString("FilterTree.Viewed.Unviewed"), () -> CustomExtendedViewedFilter.create(movielist, ExtendedViewedStateType.NOT_VIEWED)); //$NON-NLS-1$

		addNodeF(parent, Resources.ICN_SIDEBAR_LATER.get16x16(), LocaleBundle.getString("FilterTree.Viewed.Later"), () -> CustomExtendedViewedFilter.create(movielist, ExtendedViewedStateType.MARKED_FOR_LATER)); //$NON-NLS-1$

		addNodeF(parent, Resources.ICN_SIDEBAR_AGAIN.get16x16(), LocaleBundle.getString("FilterTree.Viewed.Again"), () -> CustomExtendedViewedFilter.create(movielist, ExtendedViewedStateType.MARKED_FOR_AGAIN)); //$NON-NLS-1$

		addNodeF(parent, Resources.ICN_SIDEBAR_CONTINUE.get16x16(), LocaleBundle.getString("FilterTree.Viewed.Continue"), () -> CustomExtendedViewedFilter.create(movielist, ExtendedViewedStateType.MARKED_FOR_CONTINUE)); //$NON-NLS-1$

		addNodeF(parent, Resources.ICN_SIDEBAR_ABORTED.get16x16(), LocaleBundle.getString("FilterTree.Viewed.Aborted"), () -> CustomExtendedViewedFilter.create(movielist, ExtendedViewedStateType.MARKED_ABORTED)); //$NON-NLS-1$

		addNodeF(parent, Resources.ICN_SIDEBAR_NEVER.get16x16(), LocaleBundle.getString("FilterTree.Viewed.Never"), () -> CustomExtendedViewedFilter.create(movielist, ExtendedViewedStateType.MARKED_FOR_NEVER)); //$NON-NLS-1$
	}
	
	private void initFSK(DefaultMutableTreeNode parent) {
		for (final CCFSK fsk : CCFSK.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, fsk.getIcon(), fsk.asString(), () -> CustomFSKFilter.create(movielist, fsk));
		}
	}

	private void initQuality(DefaultMutableTreeNode parent) {
		for (final CCQualityCategoryType qual : CCQualityCategoryType.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, qual.getIcon(), qual.asString(), () -> CustomQualityCategoryTypeFilter.create(movielist, qual));
		}
	}

	private void initScore(DefaultMutableTreeNode parent) {
		for (final CCUserScore score : CCUserScore.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, score.getIcon(false), score.asString(), () -> CustomUserScoreFilter.create(movielist, score));
		}
	}
	
	private void initOnlineScore(DefaultMutableTreeNode parent) {
		for (final CCOnlineStars oscore : CCOnlineStars.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, oscore.getIcon(), oscore.asInt()/2.0+"", () -> CustomOnlinescoreFilter.create(movielist, oscore)); //$NON-NLS-1$
		}
	}
	
	private void initFormat(DefaultMutableTreeNode parent) {
		for (final CCFileFormat format : CCFileFormat.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, format.getIcon(), format.asString(), () -> CustomFormatFilter.create(movielist, format));
		}
	}
	
	private void initTags(DefaultMutableTreeNode parent) {
		for (int i = 0; i < CCSingleTag.count(); i++) {
			final int curr = i;
			addNodeF(parent, CCTagList.getOnIcon(i), CCTagList.getName(i), () -> CustomTagFilter.create(movielist, curr));
		}
	}

	private void initLanguage(DefaultMutableTreeNode parent) {
		var set = new HashSet<CCDBLanguage>();
		for (var e : movielist.iteratorPlayables()) for (var l : e.language().get()) set.add(l);

		for (final CCDBLanguage language : CCDBLanguage.getWrapper().sort(set)) {
			addNodeF(parent, language.getIcon(), language.asString(), () -> CustomLanguageFilter.create(movielist, language));
		}
	}

	private void initSubtitles(DefaultMutableTreeNode parent) {
		var set = new HashSet<CCDBLanguage>();
		for (var e : movielist.iteratorPlayables()) for (var l : e.subtitles().get()) set.add(l);

		for (final CCDBLanguage language : CCDBLanguage.getWrapper().sort(set)) {
			addNodeF(parent, language.getIcon(), language.asString(), () -> CustomSubtitleFilter.create(movielist, language));
		}
	}
	
	private void initTyp(DefaultMutableTreeNode parent) {
		for (final CCDBElementTyp typ : CCDBElementTyp.getWrapper().allDisplayValuesSorted()) {
			addNodeF(parent, typ.getIcon(), typ.asString(), () -> CustomTypFilter.create(movielist, typ));
		}
	}
	
	private void initZyklus(DefaultMutableTreeNode parent) {
		for (final String zyklus : movielist.getZyklusList()) {
			addNodeF(parent, (Icon)null, zyklus, () -> CustomZyklusFilter.create(movielist, zyklus));
		}
	}
	
	private void initGroups(DefaultMutableTreeNode parent) {
		List<CCGroup> groupsList = CCStreams.iterate(movielist.getGroupList()).autosortByProperty(p -> p.Order).enumerate();
		Map<String, DefaultMutableTreeNode> groupsDone = new HashMap<>();
		groupsDone.put("", parent); //$NON-NLS-1$
		
		for(int i = 0; i < 12; i++) {
			
			for (final CCGroup group : new ArrayList<>(groupsList)) {
				DefaultMutableTreeNode pp = groupsDone.get(group.Parent);
				if (pp == null) continue;
				
				groupsList.remove(group);
				DefaultMutableTreeNode n = addNodeF(pp, (Icon)null, group.Name, () -> CustomGroupFilter.create(movielist, group, true));
				groupsDone.put(group.Name, n);
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
			addNodeF(parent, (Icon)null, genre.asString(), () -> CustomGenreFilter.create(movielist, genre));
		}
	}
	
	private void initYear(DefaultMutableTreeNode parent) {
		for (final Integer year : movielist.getYearList()) {
			addNodeF(parent, (Icon)null, year+"", () -> CustomYearFilter.create(movielist, year)); //$NON-NLS-1$
		}
	}
	
	private void initSpecialVersion(DefaultMutableTreeNode parent) {
		for (final String version : movielist.getSpecialVersionList()) {
			addNodeF(parent, (Icon)null, version, () -> CustomSpecialVersionFilter.create(movielist, version));
		}
	}
	
	private void initAnimeSeason(DefaultMutableTreeNode parent) {
		for (final String season : movielist.getAnimeSeasonList()) {
			addNodeF(parent, (Icon)null, season, () -> CustomAnimeSeasonFilter.create(movielist, season));
		}
	}
	
	private void initAnimeStudio(DefaultMutableTreeNode parent) {
		for (final String studio : movielist.getAnimeStudioList()) {
			addNodeF(parent, (Icon)null, studio, () -> CustomAnimeStudioFilter.create(movielist, studio));
		}
	}

	private void initCustom(DefaultMutableTreeNode parent) {
		for (final CustomFilterObject fo : customFilterList) {
			addNodeF(parent, Resources.ICN_SIDEBAR_CUSTOM.get16x16(), fo.getName(), fo::getFilter);
		}
		
		addNode(parent, Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom.OrganizeFilter"), e -> onOrganizeCustomFilterClicked()); //$NON-NLS-1$
		
		addNode(parent, Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom.NewFilter"), e -> onNewCustomFilterClicked()); //$NON-NLS-1$
	}
	
	private void onOrganizeCustomFilterClicked() {
		new OrganizeFilterDialog(movielist, table.getMainFrame(), customFilterList, () -> {
			updateTree();

			customFilterList.save();
		}).setVisible(true);
	}
	
	private void onNewCustomFilterClicked() {
		CustomFilterObject cfo = new CustomFilterObject(CustomFilterList.NAME_TEMPORARY, new CustomAndOperator(movielist));
		
		TableCustomFilter tcf = table.getRowFilter();
		if (tcf != null) {
			
			AbstractCustomFilter acf = tcf.getFilter();
			
			if (acf instanceof CustomAndOperator) {
				cfo = new CustomFilterObject(CustomFilterList.NAME_TEMPORARY, ((CustomAndOperator)tcf.getFilter().createCopy(movielist)));
			} else {
				cfo = new CustomFilterObject(CustomFilterList.NAME_TEMPORARY, new CustomAndOperator(movielist, tcf.getFilter().createCopy(movielist)));
			}
			
		}
		
		final CustomFilterObject fcfilter = cfo;
		
		new CustomFilterEditDialog(table.getMainFrame(), movielist, fcfilter, () ->
		{
			table.setRowFilter(fcfilter.getFilter(), RowFilterSource.SIDEBAR, false);

			if (! fcfilter.getName().equals(CustomFilterList.NAME_TEMPORARY) && !customFilterList.contains(fcfilter)) {
				customFilterList.add(fcfilter);
				updateTree();
				customFilterList.save();
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
				
				CustomAndOperator op = new CustomAndOperator(movielist);
				
				TableCustomFilter tcf = table.getRowFilter();
				if (tcf != null) {
					
					AbstractCustomFilter acf = tcf.getFilter();
					
					if (acf instanceof CustomAndOperator) {
						op = ((CustomAndOperator)tcf.getFilter().createCopy(movielist));
					} else {
						op = new CustomAndOperator(movielist, tcf.getFilter().createCopy(movielist));
					}
					
				}
				
				op.combineWith(filter.invoke());

				table.setRowFilter(op, RowFilterSource.SIDEBAR, false);
			} else {
				table.setRowFilter(filter.invoke(), RowFilterSource.SIDEBAR, false);
			}
			
		}));
		
		aroot.add(node);
		return node;
	}
}
