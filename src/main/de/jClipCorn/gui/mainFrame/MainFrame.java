package de.jClipCorn.gui.mainFrame;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.features.actionTree.IActionRootFrame;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.actionTree.menus.impl.ClipMoviePopup;
import de.jClipCorn.features.actionTree.menus.impl.ClipSeriesPopup;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.features.table.filter.customFilter.CustomSearchFilter;
import de.jClipCorn.gui.frames.compareDatabaseFrame.DatabaseComparator;
import de.jClipCorn.gui.frames.quickAddMoviesDialog.QuickAddMoviesDialog;
import de.jClipCorn.gui.frames.showUpdateFrame.ShowUpdateFrame;
import de.jClipCorn.gui.guiComponents.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.guiComponents.FileDrop;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.clipCharSelector.AbstractClipCharSortSelector;
import de.jClipCorn.gui.mainFrame.clipCharSelector.FullClipCharSortSelector;
import de.jClipCorn.gui.mainFrame.clipCharSelector.SmallClipCharSortSelector;
import de.jClipCorn.gui.mainFrame.clipMenuBar.ClipMenuBar;
import de.jClipCorn.gui.mainFrame.clipStatusbar.ClipStatusBar;
import de.jClipCorn.gui.mainFrame.clipTable.ClipTable;
import de.jClipCorn.gui.mainFrame.clipTable.RowFilterSource;
import de.jClipCorn.gui.mainFrame.clipToolbar.ClipToolbar;
import de.jClipCorn.gui.mainFrame.filterTree.FilterTree;
import de.jClipCorn.gui.mainFrame.searchField.SearchField;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.UpdateConnector;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class MainFrame extends JFrame implements CCDBUpdateListener, FileDrop.Listener, IActionRootFrame {
	private static final long serialVersionUID = 1002435986107998058L;

	private final static int FRAME_WIDTH = 875;
	private final static int FRAME_HEIGHT = 625;
	
	private static MainFrame instance = null;

	private JPanel leftPanel;
	private FilterTree filterTree;
	private DatabaseElementPreviewLabel coverImage;
	private JPanel middlePanel;
	private ClipTable clipTable;
	private CCMovieList movielist;
	private ClipStatusBar statusbar;
	private JPanel toprightPanel;
	private SearchField edSearch;

	public MainFrame(CCMovieList movielist) {
		super();
		this.movielist = movielist;
		
		movielist.addChangeListener(this);
		CCActionTree actionTree = new CCActionTree(this);

		initGUI();

		createWindowListener();
		actionTree.implementKeyListener(this, (JPanel) getContentPane());
		
		instance = this;
		
		if (CCProperties.getInstance().firstLaunch) {
			JOptionPane.showMessageDialog(this, 
					LocaleBundle.getString("MainFrame.disclaimer.text"),  //$NON-NLS-1$
					LocaleBundle.getString("MainFrame.disclaimer.caption"),  //$NON-NLS-1$
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void initGUI() {
		setTitle(createTitle());
		setIconImage(Resources.IMG_FRAME_ICON.get());

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setLocationRelativeTo(null);

		Container content = getContentPane();

		getContentPane().setLayout(new BorderLayout(3, 5));

		createLayout();
		
		edSearch = new SearchField(this);
		toprightPanel.add(edSearch);
		edSearch.setColumns(16);

		JButton btnSearch = new JButton();
		btnSearch.setMinimumSize(new Dimension(20, 20));
		btnSearch.setMaximumSize(new Dimension(20, 20));
		btnSearch.setPreferredSize(new Dimension(20, 20));
		btnSearch.setContentAreaFilled(false);
		btnSearch.setBorderPainted(false);
		btnSearch.setFocusPainted(false);
		btnSearch.addActionListener(e -> startSearch());
		btnSearch.setIcon(Resources.ICN_FRAMES_SEARCH.get16x16());
		toprightPanel.add(btnSearch);
		content.add(leftPanel, BorderLayout.WEST);
		content.add(middlePanel, BorderLayout.CENTER);

		clipTable = new ClipTable(movielist, this);
		filterTree = new FilterTree(movielist, clipTable);
		AbstractClipCharSortSelector charSelector;
		if (LookAndFeelManager.isExternal())
			charSelector = new SmallClipCharSortSelector(this);
		else
			charSelector = new FullClipCharSortSelector(this);

		middlePanel.add(clipTable, BorderLayout.CENTER);
		middlePanel.add(charSelector, BorderLayout.SOUTH);

		leftPanel.add(filterTree, BorderLayout.CENTER);

		JPanel coverPanel = new JPanel();
		leftPanel.add(coverPanel, BorderLayout.SOUTH);
		coverPanel.setLayout(new BorderLayout(0, 5));

		JPanel gapPanel = new JPanel();
		coverPanel.add(gapPanel, BorderLayout.NORTH);
		gapPanel.setLayout(null);

		coverImage = new DatabaseElementPreviewLabel(false);
		coverPanel.add(coverImage);
		if (CCLog.hasErrors()) coverImage.setModeError();

		statusbar = new ClipStatusBar(this, movielist);
		getContentPane().add(statusbar, BorderLayout.SOUTH);

		pack();

		setSize(CCProperties.getInstance().PROP_MAINFRAME_WIDTH.getValue(), CCProperties.getInstance().PROP_MAINFRAME_HEIGHT.getValue());

		clipTable.configureColumnVisibility(CCProperties.getInstance().PROP_MAINFRAME_VISIBLE_COLUMNS.getValue(), true);

		new FileDrop(clipTable, true, this);
	}
	
	public void beginBlockingIntermediate() {
		movielist.beginBlocking();

		Runnable runnner = () ->
		{
			getProgressBar().setValue(0);
			getProgressBar().setIndeterminate(true);
			setEnabled(false);
		};

		if (SwingUtilities.isEventDispatchThread()) {
			runnner.run();
		} else {
			try { SwingUtils.invokeAndWait(runnner); } catch (InvocationTargetException | InterruptedException e) { CCLog.addError(e); }
		}
	}
	
	public void endBlockingIntermediate() {
		movielist.endBlocking();

		Runnable runnner = () ->
		{
			getProgressBar().setIndeterminate(false);
			setEnabled(true);
		};
		
		if (SwingUtilities.isEventDispatchThread()) {
			runnner.run();
		} else {
			SwingUtils.invokeLater(runnner);
		}
	}

	public boolean tryTerminate() {
		if (CCLog.hasUnwatchedErrorsOrUndef()) {
			return DialogHelper.showLocaleYesNo(this, "Dialogs.QuitWithLog"); //$NON-NLS-1$
		}
		return true;
	}

	/**
	 * does not terminate - is called onClose
	 */
	public void terminate() {
		CCLog.save();
		movielist.shutdown();
	}

	private void createLayout() {
		ClipMenuBar mainMenu = new ClipMenuBar();
		leftPanel = new JPanel(new BorderLayout());

		middlePanel = new JPanel(new BorderLayout());
		setJMenuBar(mainMenu); // $hide$

		JPanel topPanel = new JPanel();
		getContentPane().add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BorderLayout(0, 0));
		ClipToolbar toolbar = new ClipToolbar();
		topPanel.add(toolbar);
		
		toprightPanel = new JPanel();
		FlowLayout fly = new FlowLayout();
		fly.setAlignment(FlowLayout.RIGHT);
		toprightPanel.setLayout(fly);
		toprightPanel.setOpaque(false);
		toolbar.add(toprightPanel, BorderLayout.EAST);
	}
	
	public void startSearch() {
		String search = edSearch.getRealText().trim();
		if (search.isEmpty()) {
			clipTable.setRowFilter(null, RowFilterSource.TEXTFIELD);
		} else {
			clipTable.setRowFilter(CustomSearchFilter.create(search), RowFilterSource.TEXTFIELD);
		}
	}

	public void start() {
		setVisible(true);
	}
	
	private JProgressBar getProgressBar() {
		return statusbar.getProgressbar();
	}
	
	public void onClipTableSelectionChanged(CCDatabaseElement ccDatabaseElement) {
		coverImage.setModeCover(ccDatabaseElement);
	}
	
	public void onClipTableExecute(CCDatabaseElement cde) {
		if (cde == null) {
			return;
		}
		
		if (cde.isMovie()) {
			switch (CCProperties.getInstance().PROP_ON_DBLCLICK_MOVE.getValue()) {
			case PLAY:
				CCActionTree.getInstance().find(CCActionTree.EVENT_ON_MOVIE_EXECUTED_0).execute(this, ActionSource.DIRECT_CLICK, cde, null);
				break;
			case PREVIEW:
				CCActionTree.getInstance().find(CCActionTree.EVENT_ON_MOVIE_EXECUTED_1).execute(this, ActionSource.DIRECT_CLICK, cde, null);
				break;
			case EDIT:
				CCActionTree.getInstance().find(CCActionTree.EVENT_ON_MOVIE_EXECUTED_2).execute(this, ActionSource.DIRECT_CLICK, cde, null);
				break;
			}
		} else {
			CCActionTree.getInstance().find(CCActionTree.EVENT_ON_SERIES_EXECUTED).execute(this, ActionSource.DIRECT_CLICK, cde, null);
		}
	}
	
	public void onClipTableSecondaryExecute(CCDatabaseElement element, MouseEvent e) {
		if (element.isMovie()) {
			(new ClipMoviePopup(this, (CCMovie)element)).show(e.getComponent(), e.getX(), e.getY());
		} else if (element.isSeries()) {
			(new ClipSeriesPopup(this, (CCSeries)element)).show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public CCMovieList getMovielist() {
		return movielist;
	}
	
	private void createWindowListener() {
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
				//unused
			}

			@Override
			public void windowIconified(WindowEvent e) {
				//unused
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				//unused
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				//unused
			}

			@Override
			public void windowClosing(WindowEvent e) {
				if (tryTerminate()) {
					dispose();
					terminate();
					System.exit(0);
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
				//unused
			}

			@Override
			public void windowActivated(WindowEvent e) {
				//unused
			}
		});
	}

	public CCDatabaseElement getSelectedElement() {
		return clipTable.getSelectedDatabaseElement();
	}

	public IActionSourceObject getSelectedActionSource() {
		return clipTable.getSelectedDatabaseElement();
	}

	public ClipStatusBar getStatusBar() {
		return statusbar;
	}
	
	public ClipTable getClipTable() {
		return clipTable;
	}
	
	public void resetCharSelector() {
		//--
	}
	
	public void resetSidebar() {
		filterTree.reset();
	}
	
	public void resetSearchField(boolean force) {
		edSearch.reset(force);
	}

	@Override
	public void onAddDatabaseElement(CCDatabaseElement mov) {
		// -
	}

	@Override
	public void onRemMovie(CCDatabaseElement el) {
		// -
	}

	@Override
	public void onChangeDatabaseElement(CCDatabaseElement el) {
		// -
	}

	@Override
	public void onAfterLoad() {
		if (CCProperties.getInstance().PROP_COMMON_CHECKFORUPDATES.getValue() && ! Main.BETA) {
			new UpdateConnector(Main.TITLE, Main.VERSION, (src, available, version) -> SwingUtils.invokeLater(() ->
			{
				if (available) {
					if (Main.DEBUG) {
						CCLog.addDebug("Update found"); //$NON-NLS-1$
					} else {
						ShowUpdateFrame suf = new ShowUpdateFrame(MainFrame.this, src, true);
						suf.setVisible(true);
					}
				}
			}), true);
		}
	}

	@Override
	public void onRefresh() {
		// -
	}
	
	@SuppressWarnings("nls")
	public String createTitle() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(Main.TITLE);
		builder.append(" v");
		builder.append(Main.VERSION);
		
		if (Main.BETA) {
			builder.append(" [BETA]");
		}
		
		if (Main.DEBUG) {
			builder.append(" [DEBUG]");
		}
		
		if (CCProperties.getInstance().ARG_READONLY) {
			builder.append(" [READONLY]");
		}
		
		return builder.toString();
	}
	
	public static MainFrame getInstance() {
		return instance;
	}

	public DatabaseElementPreviewLabel getCoverLabel() {
		return coverImage;
	}

	@Override
	public void filesDropped(final File[] files) {
		if (files.length>0) {

			if (CCStreams.iterate(files).all(CCFileFormat::isValidMovieFormat)) {
				SwingUtils.invokeLater(() -> new QuickAddMoviesDialog(this, getMovielist(), files).setVisible(true));
				return;
			}

			if (files.length == 1) {
				String extension = PathFormatter.getExtension(files[0]);

				if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_FULLEXPORT)) {
					ExportHelper.openFullBackupFile(files[0], MainFrame.getInstance(), movielist);
					return;
				}
				if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_SINGLEEXPORT)) {
					ExportHelper.openSingleElementFile(files[0], MainFrame.getInstance(), movielist, null);
					return;
				}
				if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_MULTIPLEEXPORT)) {
					ExportHelper.openMultipleElementFile(files[0], MainFrame.getInstance(), movielist);
					return;
				}
				if (extension.equalsIgnoreCase(ExportHelper.EXTENSION_COMPAREFILE)) {
					DatabaseComparator.openFile(files[0], MainFrame.getInstance(), movielist);
					return;
				}
			}

			// unknown file dropped
		}

		// nothing dropped
	}

	public void onSettingsChanged()
	{
		clipTable.configureColumnVisibility(CCProperties.getInstance().PROP_MAINFRAME_VISIBLE_COLUMNS.getValue(), false);
		clipTable.autoResize();

		//if (CCProperties.getInstance().PROP_UI_APPTHEME.getValue() != LookAndFeelManager.getCurrentTheme())
		//{
		//	LookAndFeelManager.setLookAndFeel(CCProperties.getInstance().PROP_UI_APPTHEME.getValue(), true);
		//}
	}
}
