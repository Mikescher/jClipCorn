package de.jClipCorn.gui.actionTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieStatus;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.database.xml.CCBXMLReader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.aboutFrame.AboutFrame;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.frames.addSeasonFrame.AddSeasonFrame;
import de.jClipCorn.gui.frames.addSeriesFrame.AddSeriesFrame;
import de.jClipCorn.gui.frames.changeScoreFrame.ChangeScoreFrame;
import de.jClipCorn.gui.frames.changeViewedFrame.ChangeViewedFrame;
import de.jClipCorn.gui.frames.checkDatabaseFrame.CheckDatabaseFrame;
import de.jClipCorn.gui.frames.compareDatabaseFrame.CompareDatabaseFrame;
import de.jClipCorn.gui.frames.editMovieFrame.EditMovieFrame;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.frames.exportJxmlBKPFrame.ExportJxmlBKPDialog;
import de.jClipCorn.gui.frames.filenameRulesFrame.FilenameRuleFrame;
import de.jClipCorn.gui.frames.logFrame.LogFrame;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.frames.moveSeriesFrame.MoveSeriesDialog;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.frames.scanFolderFrame.ScanFolderFrame;
import de.jClipCorn.gui.frames.searchFrame.SearchFrame;
import de.jClipCorn.gui.frames.settingsFrame.SettingsFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.DialogHelper;
import de.jClipCorn.util.FileChooserHelper;
import de.jClipCorn.util.HTTPUtilities;
import de.jClipCorn.util.PathFormatter;
import de.jClipCorn.util.parser.ImDBParser;

public class CCActionTree {
	public final static String EVENT_ON_MOVIE_EXECUTED_0 = "PlayMovie"; //$NON-NLS-1$
	public final static String EVENT_ON_MOVIE_EXECUTED_1 = "PrevMovie"; //$NON-NLS-1$
	public final static String EVENT_ON_MOVIE_EXECUTED_2 = "EditMovie"; //$NON-NLS-1$
	public final static String EVENT_ON_SERIES_EXECUTED  = "PrevSeries"; //$NON-NLS-1$

	private static CCActionTree instance = null;

	private MainFrame owner;
	private CCMovieList movielist;

	private CCActionElement root;

	public CCActionTree(MainFrame mf) {
		this.owner = mf;
		this.movielist = mf.getMovielist();

		createStructure();

		instance = this;
	}

	@SuppressWarnings({ "nls"})
	private void createStructure() {
		CCActionElement temp;

		root = new CCActionElement("ROOT", null, "", "");

		// #######################################################################################################
		CCActionElement file = root.addChild(new CCActionElement("File", null, "ClipMenuBar.File", ""));
		// #######################################################################################################

		temp = file.addChild(new CCActionElement("Exit", null, "ClipMenuBar.File.Exit", Resources.ICN_MENUBAR_CLOSE));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickFileExit();
			}
		});

		// #######################################################################################################
		CCActionElement database = root.addChild(new CCActionElement("Database", null, "ClipMenuBar.Database", ""));
		// #######################################################################################################

		temp = database.addChild(new CCActionElement("CheckDatabase", null, "ClipMenuBar.Database.CheckDB", Resources.ICN_MENUBAR_DBCHECK));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickDatabaseCheck();
			}
		});

		temp = database.addChild(new CCActionElement("ClearDatabase", null, "ClipMenuBar.Database.ClearDB", Resources.ICN_MENUBAR_CLEARDB));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickDatabaseClear();
			}
		});
		
		temp = database.addChild(new CCActionElement("ExportDatabase", null, "ClipMenuBar.Database.ExportDB", Resources.ICN_MENUBAR_CREATE_JXMLBKP));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickDatabaseExportAsJxmBKP();
			}
		});
		
		temp = database.addChild(new CCActionElement("ImportDatabase", null, "ClipMenuBar.Database.ImportDB", null)); //TODO ICON !!!!!!1111einseinself
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickDatabaseImportAsJxmBKP();
			}
		});
		
		temp = database.addChild(new CCActionElement("SearchDatabase", KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "ClipMenuBar.Database.SearchDB", Resources.ICN_MENUBAR_SEARCH));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickDatabaseSearchDatabase();
			}
		});

		// #######################################################################################################
		CCActionElement movies = root.addChild(new CCActionElement("Movies", null, "ClipMenuBar.Movies", ""));
		// #######################################################################################################

		temp = movies.addChild(new CCActionElement("PlayMovie", null, "ClipMenuBar.Movies.Play", Resources.ICN_MENUBAR_PLAY));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickMoviesPlay();
			}
		});

		temp = movies.addChild(new CCActionElement("PrevMovie", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ClipMenuBar.Movies.Preview", Resources.ICN_MENUBAR_PREVIEW_MOV));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickMoviesPrev();
			}
		});

		temp = movies.addChild(new CCActionElement("AddMovie", KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK), "ClipMenuBar.Movies.Add", Resources.ICN_MENUBAR_ADD_MOV));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickMoviesAdd();
			}
		});

		temp = movies.addChild(new CCActionElement("EditMovie", KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "ClipMenuBar.Movies.Edit", Resources.ICN_MENUBAR_EDIT_MOV));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickMoviesEdit();
			}
		});

		temp = movies.addChild(new CCActionElement("RemMovie", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "ClipMenuBar.Movies.Remove", Resources.ICN_MENUBAR_REMOVE));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickMoviesRem();
			}
		});

		// #######################################################################################################
		CCActionElement series = root.addChild(new CCActionElement("Serien", null, "ClipMenuBar.Series", ""));
		// #######################################################################################################

		temp = series.addChild(new CCActionElement("PrevSeries", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ClipMenuBar.Series.Preview", Resources.ICN_MENUBAR_PREVIEW_SER));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickSeriesPreview();
			}
		});
		
		temp = series.addChild(new CCActionElement("MoveSeries", null, "ClipMenuBar.Series.MoveSeries", Resources.ICN_MENUBAR_MOVESERIES));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickSeriesMove();
			}
		});

		temp = series.addChild(new CCActionElement("AddSeries", KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "ClipMenuBar.Series.Add", Resources.ICN_MENUBAR_ADD_SER));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickSeriesAdd();
			}
		});

		temp = series.addChild(new CCActionElement("EditSeries", KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "ClipMenuBar.Series.Edit", Resources.ICN_MENUBAR_EDIT_SER));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickSeriesEdit();
			}
		});

		temp = series.addChild(new CCActionElement("AddSeason", null, "ClipMenuBar.Series.AddSeason", Resources.ICN_MENUBAR_ADD_SEA));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickSeasonAdd();
			}
		});

		temp = series.addChild(new CCActionElement("RemSeries", KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "ClipMenuBar.Series.Remove", Resources.ICN_MENUBAR_REMOVE));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickSeriesRem();
			}
		});

		// #######################################################################################################
		CCActionElement extras = root.addChild(new CCActionElement("Extras", null, "ClipMenuBar.Extras", ""));
		// #######################################################################################################

		temp = extras.addChild(new CCActionElement("XML", null, "ClipMenuBar.Extras.XML", Resources.ICN_MENUBAR_PARSEXML));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickExtrasXML();
			}
		});
		
		temp = extras.addChild(new CCActionElement("ScanFolder", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "ClipMenuBar.Extras.ScanFolder", Resources.ICN_MENUBAR_SCANFOLDER));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickExtrasScanFolder();
			}
		});
		
		temp = extras.addChild(new CCActionElement("CompareDBs", null, "ClipMenuBar.Extras.CompareDBs", Resources.ICN_MENUBAR_COMPARE));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickExtrasCompareDBs();
			}
		});
		
		temp = extras.addChild(new CCActionElement("MassChangeViewed", null, "ClipMenuBar.Extras.MassChangeViewed", Resources.ICN_MENUBAR_MCHANGE_VIEWED));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickExtrasMassChangeViewed();
			}
		});
		
		temp = extras.addChild(new CCActionElement("MassChangeScore", null, "ClipMenuBar.Extras.MassChangeScore", Resources.ICN_MENUBAR_MCHANGE_SCORE));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickExtrasMassChangeScore();
			}
		});
		
		temp = extras.addChild(new CCActionElement("ResetViewed", null, "ClipMenuBar.Extras.ResetViewed", Resources.ICN_MENUBAR_RESETVIEWED));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickExtrasResetViewed();
			}
		});

		temp = extras.addChild(new CCActionElement("ShowSettings", null, "ClipMenuBar.Extras.Settings", Resources.ICN_MENUBAR_SETTINGS));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickExtrasSettings();
			}
		});
		
		// #######################################################################################################
		CCActionElement help = root.addChild(new CCActionElement("Help", null, "ClipMenuBar.Help", ""));
		// #######################################################################################################
		
		temp = help.addChild(new CCActionElement("ShowLog", null, "ClipMenuBar.Help.Log", Resources.ICN_MENUBAR_LOG));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickHelpShowLog();
			}
		});
		
		temp = help.addChild(new CCActionElement("ShowRules", null, "ClipMenuBar.Help.Filenamerules", Resources.ICN_MENUBAR_FILENAMERULES));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickHelpShowRules();
			}
		});
		
		temp = help.addChild(new CCActionElement("ShowAbout", KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "ClipMenuBar.Help.About", Resources.ICN_MENUBAR_ABOUT));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickHelpShowAbout();
			}
		});
		
		// #######################################################################################################
		CCActionElement other = root.addChild(new CCActionElement("Other", null, "", "", false));
		// #######################################################################################################
		
		temp = other.addChild(new CCActionElement("SetMovieRating", null, "ClipMenuBar.Other.SetMovieRating", Resources.ICN_SIDEBAR_SCORE));
		
		temp = other.addChild(new CCActionElement("SetSeriesRating", null, "ClipMenuBar.Other.SetSeriesRating", Resources.ICN_SIDEBAR_SCORE));
		
		temp = other.addChild(new CCActionElement("SetMovieStatus", null, "ClipMenuBar.Other.SetMovieStatus", Resources.ICN_SIDEBAR_STATUS));
		
		temp = other.addChild(new CCActionElement("SetRating0", null, "CCMovieScore.R0", CCMovieScore.RATING_0.getIconName()));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherSetRating(CCMovieScore.RATING_0);
			}
		});
		
		temp = other.addChild(new CCActionElement("SetRating1", null, "CCMovieScore.R1", CCMovieScore.RATING_I.getIconName()));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherSetRating(CCMovieScore.RATING_I);
			}
		});
		
		temp = other.addChild(new CCActionElement("SetRating2", null, "CCMovieScore.R2", CCMovieScore.RATING_II.getIconName()));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherSetRating(CCMovieScore.RATING_II);
			}
		});
		
		temp = other.addChild(new CCActionElement("SetRating3", null, "CCMovieScore.R3", CCMovieScore.RATING_III.getIconName()));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherSetRating(CCMovieScore.RATING_III);
			}
		});
		
		temp = other.addChild(new CCActionElement("SetRating4", null, "CCMovieScore.R4", CCMovieScore.RATING_IV.getIconName()));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherSetRating(CCMovieScore.RATING_IV);
			}
		});
		
		temp = other.addChild(new CCActionElement("SetRating5", null, "CCMovieScore.R5", CCMovieScore.RATING_V.getIconName()));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherSetRating(CCMovieScore.RATING_V);
			}
		});
		
		temp = other.addChild(new CCActionElement("SetRatingNO", null, "CCMovieScore.RNO", CCMovieScore.RATING_NO.getIconName()));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherSetRating(CCMovieScore.RATING_NO);
			}
		});
		
		temp = other.addChild(new CCActionElement("SetMovieViewed", null, "ClipMenuBar.Other.SetViewed", Resources.ICN_MENUBAR_VIEWED));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherSetViewed();
			}
		});
		
		temp = other.addChild(new CCActionElement("SetMovieUnviewed", null, "ClipMenuBar.Other.SetUnviewed", Resources.ICN_MENUBAR_UNVIEWED));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherSetUnviewed();
			}
		});
		
		temp = other.addChild(new CCActionElement("SetStatus0", null, "CCMovieStatus.Status0", CCMovieStatus.STATUS_OK.getIconName()));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherSetStatus(CCMovieStatus.STATUS_OK);
			}
		});
		
		temp = other.addChild(new CCActionElement("SetStatus1", null, "CCMovieStatus.Status1", CCMovieStatus.STATUS_LOWQUALITY.getIconName()));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherSetStatus(CCMovieStatus.STATUS_LOWQUALITY);
			}
		});
		
		temp = other.addChild(new CCActionElement("SetStatus2", null, "CCMovieStatus.Status2", CCMovieStatus.STATUS_MISSINGVIDEOTIME.getIconName()));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherSetStatus(CCMovieStatus.STATUS_MISSINGVIDEOTIME);
			}
		});
		
		temp = other.addChild(new CCActionElement("OpenFolder", null, "ClipMenuBar.Other.OpenFolder", Resources.ICN_MENUBAR_FOLDER));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherOpenFolder();
			}
		});
		
		temp = other.addChild(new CCActionElement("ShowInIMDB", null, "ClipMenuBar.Other.ShowInIMDB", Resources.ICN_MENUBAR_IMDB));
		temp.addListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onClickOtherShowInIMDB();
			}
		});
		
		// #######################################################################################################
		
		createProperties();
		
		// #######################################################################################################
		
		if (Main.DEBUG) {
			root.testTree();
			System.out.println(String.format("[DBG] %d Elements in ActionTree intialized", root.getAllChildren().size())); //$NON-NLS-1$
		}
	}

	public CCActionElement getRoot() {
		return root;
	}

	public static CCActionTree getInstance() {
		return instance;
	}

	public CCActionElement find(String name) {
		return root.find(name);
	}
	
	public void implementKeyListener(JComponent comp) {
		getRoot().implementAllKeyListener(comp);
	}
	
	private void createProperties() {
		getRoot().createAllProperties(CCProperties.getInstance());
	}

	// #######################################################################################################
	// ############################################### EVENTS ################################################
	// #######################################################################################################

	private void onClickExtrasSettings() {
		SettingsFrame sf = new SettingsFrame(owner, CCProperties.getInstance());
		sf.setVisible(true);
	}
	
	private void onClickFileExit() {
		owner.terminate();
		owner.dispose();
	}

	private void onClickExtrasXML() {
		final JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(FileChooserHelper.createFileFilter("CCBackup-XML-File", "xml"));  //$NON-NLS-1$//$NON-NLS-2$

		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showOpenDialog(owner);

		if (returnval == JFileChooser.APPROVE_OPTION && DialogHelper.showLocaleYesNo(owner, "Dialogs.LoadXML")) { //$NON-NLS-1$
			new Thread(new Runnable() {
				@Override
				public void run() {
					owner.startBlockingIntermediate();

					CCBXMLReader xmlreader = new CCBXMLReader(chooser.getSelectedFile().getAbsolutePath(), owner.getMovielist());
					if (!xmlreader.parse()) {
						CCLog.addError(LocaleBundle.getString("LogMessage.CouldNotParseCCBXML")); //$NON-NLS-1$
					}

					owner.getClipTable().autoResize();

					owner.endBlockingIntermediate();
				}
			}).start();
		}
	}
	
	private void onClickExtrasScanFolder() {
		ScanFolderFrame sff = new ScanFolderFrame(owner);
		sff.setVisible(true);
	}
	
	private void onClickExtrasCompareDBs() {
		CompareDatabaseFrame cdf = new CompareDatabaseFrame(owner, movielist);
		cdf.setVisible(true);
	}
	
	private void onClickExtrasResetViewed() {
		if (DialogHelper.showLocaleYesNo(owner, "Dialogs.ResetViewed")) { //$NON-NLS-1$
			owner.getMovielist().resetAllMovieViewed(false);
		}
	}
	
	private void onClickExtrasMassChangeViewed() {
		ChangeViewedFrame cvf = new ChangeViewedFrame(owner, movielist);
		cvf.setVisible(true);
	}
	
	private void onClickExtrasMassChangeScore() {
		ChangeScoreFrame csf = new ChangeScoreFrame(owner, movielist);
		csf.setVisible(true);
	}
	
	private void onClickMoviesPlay() {
		CCDatabaseElement element = owner.getSelectedElement();

		if (element.isMovie()) {
			((CCMovie) element).play();
		}
	}

	private void onClickMoviesPrev() {
		if (owner.getSelectedElement().isMovie()) {
			PreviewMovieFrame frame = new PreviewMovieFrame(owner, (CCMovie) owner.getSelectedElement());

			frame.setVisible(true);
		}
	}

	private void onClickMoviesAdd() {
		AddMovieFrame nFrame = new AddMovieFrame(owner, movielist);

		nFrame.setVisible(true);
	}

	private void onClickMoviesRem() {
		if (owner.getSelectedElement() != null && owner.getSelectedElement().isMovie()) {
			if (DialogHelper.showLocaleYesNo(owner, "Dialogs.DeleteMovie")) { //$NON-NLS-1$
				movielist.remove(owner.getSelectedElement());
			}
		}
	}

	private void onClickDatabaseCheck() {
		CheckDatabaseFrame cdd = new CheckDatabaseFrame(movielist, owner);

		cdd.setVisible(true);
	}

	private void onClickDatabaseClear() {
		if (DialogHelper.showLocaleYesNo(owner, "Dialogs.ClearDatabase")) { //$NON-NLS-1$
			new Thread(new Runnable() {
				@Override
				public void run() {
					movielist.clear();
				}
			}).start();
		}
	}
	
	private void onClickDatabaseExportAsJxmBKP() {
		JDialog ejf = new ExportJxmlBKPDialog(owner, owner.getMovielist()); //TODO Why do we need an extra Frame for this ??? (seejxmbkp import)
		ejf.setVisible(true);
	}
	
	private void onClickDatabaseImportAsJxmBKP() {
		final JFileChooser chooser = new JFileChooser();

		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportJxmlBKPDialog.filechooser.description", "jxmlbkp"));  //$NON-NLS-1$//$NON-NLS-2$

		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showOpenDialog(owner);

		if (returnval == JFileChooser.APPROVE_OPTION && DialogHelper.showLocaleYesNo(owner, "Dialogs.LoadBackup")) { //$NON-NLS-1$
			new Thread(new Runnable() {
				@Override
				public void run() {
					owner.startBlockingIntermediate();

					try {
						ExportHelper.restoreFromBackup(chooser.getSelectedFile(), movielist);
					} catch (Exception e) {
						CCLog.addError(e);
					}
					
					owner.getClipTable().autoResize();

					owner.endBlockingIntermediate();
				}
			}).start();
		}
	}
	
	private void onClickDatabaseSearchDatabase() {
		SearchFrame sf = new SearchFrame(movielist, owner);
		sf.setVisible(true);
	}
	
	private void onClickSeriesPreview() {
		CCDatabaseElement el = owner.getSelectedElement();

		if (el != null && el.isSeries()) {
			PreviewSeriesFrame psf = new PreviewSeriesFrame(owner, (CCSeries) el);
			psf.setVisible(true);
		}
	}
	
	private void onClickSeriesMove() {
		CCDatabaseElement el = owner.getSelectedElement();

		if (el != null && el.isSeries()) {
			MoveSeriesDialog msf = new MoveSeriesDialog(owner, (CCSeries) el);
			msf.setVisible(true);
		}
	}

	private void onClickSeriesAdd() {
		(new AddSeriesFrame(owner, movielist)).setVisible(true);
	}

	private void onClickSeriesEdit() {
		CCDatabaseElement el = owner.getSelectedElement();

		if (el != null && el.isSeries()) {
			EditSeriesFrame esf = new EditSeriesFrame(owner, (CCSeries) el, null);
			esf.setVisible(true);
		}
	}

	private void onClickSeasonAdd() {
		CCDatabaseElement el = owner.getSelectedElement();

		if (el != null && el.isSeries()) {
			AddSeasonFrame asf = new AddSeasonFrame(owner, (CCSeries) el, null);
			asf.setVisible(true);
		}
	}

	private void onClickMoviesEdit() {
		CCDatabaseElement el = owner.getSelectedElement();

		if (el != null && el.isMovie()) {
			EditMovieFrame emf = new EditMovieFrame(owner, (CCMovie) el, null);
			emf.setVisible(true);
		}
	}

	private void onClickSeriesRem() {
		if (owner.getSelectedElement() != null && owner.getSelectedElement().isSeries()) {
			if (DialogHelper.showLocaleYesNo(owner, "Dialogs.DeleteSeries")) {//$NON-NLS-1$
				movielist.remove(owner.getSelectedElement());
			}
		}
	}
	
	private void onClickHelpShowLog() {
		LogFrame lf = new LogFrame(owner);
		lf.setVisible(true);
	}
	
	private void onClickHelpShowRules() {
		FilenameRuleFrame frf = new FilenameRuleFrame(owner);
		frf.setVisible(true);
	}
	
	private void onClickHelpShowAbout() {
		AboutFrame af = new AboutFrame(owner);
		af.setVisible(true);
	}
	
	private void onClickOtherOpenFolder() {
		CCDatabaseElement el = owner.getSelectedElement();
		
		if (el == null) {
			return;
		}
		
		if (el.isMovie()) {
			PathFormatter.showInExplorer(((CCMovie)el).getAbsolutePart(0));
		} else {
			CCEpisode e = ((CCSeries)el).getFirstEpisode();
			if (e != null) {
				PathFormatter.showInExplorer(e.getAbsolutePart());
			}
		}
	}

	private void onClickOtherSetUnviewed() {
		CCDatabaseElement el = owner.getSelectedElement();
		if (el != null && el.isMovie()) {
			((CCMovie)el).setViewed(false);
		}
	}

	private void onClickOtherSetViewed() {
		CCDatabaseElement el = owner.getSelectedElement();
		if (el != null && el.isMovie()) {
			((CCMovie)el).setViewed(true);
		}
	}
	
	private void onClickOtherSetRating(CCMovieScore rating) {
		CCDatabaseElement el = owner.getSelectedElement();
		if (el != null) {
			el.setScore(rating);
		}
	}
	
	private void onClickOtherSetStatus(CCMovieStatus status) {
		CCDatabaseElement el = owner.getSelectedElement();
		if (el != null && el.isMovie()) {
			((CCMovie)el).setStatus(status);
		}
	}
	
	private void onClickOtherShowInIMDB() {
		CCDatabaseElement el = owner.getSelectedElement();
		if (el != null && el.isMovie()) {
			HTTPUtilities.openInBrowser(ImDBParser.getSearchURL(((CCMovie)el).getCompleteTitle(), CCMovieTyp.MOVIE));
		} else if (el != null && el.isSeries()) {
			HTTPUtilities.openInBrowser(ImDBParser.getSearchURL(((CCSeries)el).getTitle(), CCMovieTyp.SERIES));
		}
	}
}
