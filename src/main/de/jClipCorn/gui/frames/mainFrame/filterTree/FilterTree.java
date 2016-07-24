package de.jClipCorn.gui.frames.mainFrame.filterTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTable;
import de.jClipCorn.gui.frames.mainFrame.clipTable.RowFilterSource;
import de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs.CustomOperatorFilterDialog;
import de.jClipCorn.gui.frames.organizeFilterFrame.OrganizeFilterDialog;
import de.jClipCorn.gui.guiComponents.tableFilter.TableCustomFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableFSKFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableFormatFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableGenreFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableGroupFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableLanguageFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableOnlinescoreFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableQualityFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableScoreFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableTagFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableTypFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableViewedFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableYearFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableZyklusFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.gui.localization.LocaleBundle;
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
		ActionListener reset = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				table.setRowFilter(null, RowFilterSource.SIDEBAR);
			}
		};
		
		node_all = addNodeI(null, null, null, null);
		initAll();
		
		node_zyklus = addNode(null, Resources.ICN_SIDEBAR_ZYKLUS, LocaleBundle.getString("FilterTree.Zyklus"), reset); //$NON-NLS-1$
		initZyklus();
		
		node_groups = addNode(null, Resources.ICN_SIDEBAR_GROUPS, LocaleBundle.getString("FilterTree.Groups"), reset); //$NON-NLS-1$
		initGroups(); //TODO HIDE IF NO ELEMENTS
		
		node_genre = addNode(null, Resources.ICN_SIDEBAR_GENRE, LocaleBundle.getString("FilterTree.Genre"), reset); //$NON-NLS-1$
		initGenre();
		
		node_onlinescore = addNode(null, Resources.ICN_SIDEBAR_ONLINESCORE, LocaleBundle.getString("FilterTree.IMDB"), reset); //$NON-NLS-1$
		initOnlineScore();
		
		node_score = addNode(null, Resources.ICN_SIDEBAR_SCORE, LocaleBundle.getString("FilterTree.Score"), reset); //$NON-NLS-1$
		initScore(); //TODO HIDE IF NO ELEMENTS
		
		node_fsk = addNode(null, Resources.ICN_TABLE_FSK_2, LocaleBundle.getString("FilterTree.FSK"), reset); //$NON-NLS-1$
		initFSK();
		
		node_year = addNode(null, Resources.ICN_SIDEBAR_YEAR, LocaleBundle.getString("FilterTree.Year"), reset); //$NON-NLS-1$
		initYear();
		
		node_format = addNode(null, Resources.ICN_SIDEBAR_FORMAT, LocaleBundle.getString("FilterTree.Format"), reset); //$NON-NLS-1$
		initFormat();
		
		node_quality = addNode(null, Resources.ICN_SIDEBAR_QUALITY, LocaleBundle.getString("FilterTree.Quality"), reset); //$NON-NLS-1$
		initQuality();
		
		node_tags = addNode(null, Resources.ICN_SIDEBAR_TAGS, LocaleBundle.getString("FilterTree.Tags"), reset); //$NON-NLS-1$
		initTags();
		
		node_language = addNode(null, Resources.ICN_SIDEBAR_LANGUAGE, LocaleBundle.getString("FilterTree.Language"), reset); //$NON-NLS-1$
		initLanguage();
		
		node_typ = addNode(null, Resources.ICN_SIDEBAR_TYP, LocaleBundle.getString("FilterTree.Type"), reset); //$NON-NLS-1$
		initTyp();
		
		node_viewed = addNode(null, Resources.ICN_SIDEBAR_VIEWED, LocaleBundle.getString("FilterTree.Viewed"), reset); //$NON-NLS-1$
		initViewed();

		node_custom = addNode(null,  Resources.ICN_SIDEBAR_CUSTOM, LocaleBundle.getString("FilterTree.Custom"), reset); //$NON-NLS-1$
		initCustom();
	}
	
	private void initAll() {
		node_all.setUserObject(new FilterTreeNode(CachedResourceLoader.getImageIcon(Resources.ICN_SIDEBAR_ALL), LocaleBundle.getString("FilterTree.All"), new ActionListener() { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent arg0) {
				table.setRowFilter(null, RowFilterSource.SIDEBAR);
				collapseAll();
			}
		}));
	}
	
	private void initViewed() {
		addNode(node_viewed, Resources.ICN_SIDEBAR_VIEWED, LocaleBundle.getString("FilterTree.Viewed.Viewed"), new ActionListener() { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent arg0) {
				table.setRowFilter(new TableViewedFilter(true), RowFilterSource.SIDEBAR);
			}
		});
		
		addNode(node_viewed, Resources.ICN_SIDEBAR_UNVIEWED, LocaleBundle.getString("FilterTree.Viewed.Unviewed"), new ActionListener() { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent arg0) {
				table.setRowFilter(new TableViewedFilter(false), RowFilterSource.SIDEBAR);
			}
		});
	}
	
	private void initFSK() {
		for (final CCMovieFSK fsk : CCMovieFSK.values()) {
			addNodeI(node_fsk, fsk.getIcon(), fsk.asString(), new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					table.setRowFilter(new TableFSKFilter(fsk), RowFilterSource.SIDEBAR);
				}
			});
		}
	}
	
	private void initScore() {
		for (final CCMovieScore score : CCMovieScore.values()) {
			addNodeI(node_score, score.getIcon(), score.asString(), new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					table.setRowFilter(new TableScoreFilter(score), RowFilterSource.SIDEBAR);
				}
			});
		}
	}
	
	private void initOnlineScore() {
		for (final CCMovieOnlineScore oscore : CCMovieOnlineScore.values()) {
			addNodeI(node_onlinescore, oscore.getIcon(), oscore.asInt()/2.0+"", new ActionListener() { //$NON-NLS-1$
				@Override
				public void actionPerformed(ActionEvent e) {
					table.setRowFilter(new TableOnlinescoreFilter(oscore), RowFilterSource.SIDEBAR);
				}
			});
		}
	}
	
	private void initFormat() {
		for (final CCMovieFormat format : CCMovieFormat.values()) {
			addNodeI(node_format, format.getIcon(), format.asString(), new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					table.setRowFilter(new TableFormatFilter(format), RowFilterSource.SIDEBAR);
				}
			});
		}
	}
	
	private void initQuality() {
		for (final CCMovieQuality quality : CCMovieQuality.values()) {
			addNodeI(node_quality, quality.getIcon(), quality.asString(), new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					table.setRowFilter(new TableQualityFilter(quality), RowFilterSource.SIDEBAR);
				}
			});
		}
	}
	
	private void initTags() {
		for (int i = 0; i < CCMovieTags.ACTIVETAGS; i++) {
			final int curr = i;
			addNodeI(node_tags, CCMovieTags.getOnIcon(i), CCMovieTags.getName(i), new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					table.setRowFilter(new TableTagFilter(curr), RowFilterSource.SIDEBAR);
				}
			});
		}
	}
	
	private void initLanguage() {
		for (final CCMovieLanguage language : CCMovieLanguage.values()) {
			addNodeI(node_language, language.getIcon(), language.asString(), new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					table.setRowFilter(new TableLanguageFilter(language), RowFilterSource.SIDEBAR);
				}
			});
		}
	}
	
	private void initTyp() {
		for (final CCMovieTyp typ : CCMovieTyp.values()) {
			addNodeI(node_typ, typ.getIcon(), typ.asString(), new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					table.setRowFilter(new TableTypFilter(typ), RowFilterSource.SIDEBAR);
				}
			});
		}
	}
	
	private void initZyklus() {
		for (final String zyklus : movielist.getZyklusList()) {
			addNodeI(node_zyklus, null, zyklus, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					table.setRowFilter(new TableZyklusFilter(zyklus), RowFilterSource.SIDEBAR);
				}
			});
		}
	}
	
	private void initGroups() {
		for (final CCGroup group : movielist.getGroupList()) {
			addNodeI(node_groups, null, group.Name, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					table.setRowFilter(new TableGroupFilter(group), RowFilterSource.SIDEBAR);
				}
			});
		}
	}
	
	private void initGenre() {
		if (CCMovieList.isBlocked()) {
			return;
		}
		
		List<CCMovieGenre> genres = movielist.getGenreList();
		
		Collections.sort(genres, CCMovieGenre.getTextComparator());
		
		for (final CCMovieGenre genre : genres) {
			addNodeI(node_genre, null, genre.asString(), new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					table.setRowFilter(new TableGenreFilter(genre), RowFilterSource.SIDEBAR);
				}
			});
		}
	}
	
	private void initYear() {
		for (final Integer year : movielist.getYearList()) {
			addNodeI(node_year, null, year+"", new ActionListener() { //$NON-NLS-1$
				@Override
				public void actionPerformed(ActionEvent e) {
					table.setRowFilter(new TableYearFilter(year), RowFilterSource.SIDEBAR);
				}
			});
		}
	}
	
	private void initCustom() {
		for (int i = 0; i < customFilterList.size(); i++) {
			final CustomFilterObject fo = customFilterList.get(i);
			addNode(node_custom, Resources.ICN_SIDEBAR_CUSTOM, fo.getName(), new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					table.setRowFilter(new TableCustomFilter(fo.getFilter()), RowFilterSource.SIDEBAR);
				}
			});
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
				table.setRowFilter(new TableCustomFilter(cfilter), RowFilterSource.SIDEBAR);
			}
		}, table.getMainFrame(), true).setVisible(true);
	}
}
