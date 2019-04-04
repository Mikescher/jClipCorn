package de.jClipCorn.gui.mainFrame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.features.actionTree.ActionSource;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.gui.mainFrame.clipCharSelector.AbstractClipCharSortSelector;
import de.jClipCorn.gui.mainFrame.clipCharSelector.FullClipCharSortSelector;
import de.jClipCorn.gui.mainFrame.clipCharSelector.SmallClipCharSortSelector;
import de.jClipCorn.gui.mainFrame.clipMenuBar.ClipMenuBar;
import de.jClipCorn.gui.mainFrame.clipStatusbar.ClipStatusBar;
import de.jClipCorn.gui.mainFrame.clipTable.ClipTable;
import de.jClipCorn.gui.mainFrame.clipTable.RowFilterSource;
import de.jClipCorn.gui.mainFrame.clipToolbar.ClipToolbar;
import de.jClipCorn.gui.mainFrame.filterTree.FilterTree;
import de.jClipCorn.gui.mainFrame.popupMenus.ClipMoviePopup;
import de.jClipCorn.gui.mainFrame.popupMenus.ClipSeriesPopup;
import de.jClipCorn.gui.mainFrame.searchField.SearchField;
import de.jClipCorn.gui.frames.quickAddMoviesDialog.QuickAddMoviesDialog;
import de.jClipCorn.gui.frames.showUpdateFrame.ShowUpdateFrame;
import de.jClipCorn.gui.guiComponents.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.guiComponents.FileDrop;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.features.table.filter.customFilter.CustomSearchFilter;
import de.jClipCorn.util.UpdateConnector;
import de.jClipCorn.util.helper.LookAndFeelManager;

public class MainFrame extends JFrame implements CCDBUpdateListener, FileDrop.Listener {
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
		actionTree.implementKeyListener((JPanel) getContentPane());
		
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
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		btnSearch.setIcon(CachedResourceLoader.getIcon(Resources.ICN_FRAMES_SEARCH.icon16x16));
		toprightPanel.add(btnSearch);
		content.add(leftPanel, BorderLayout.WEST);
		content.add(middlePanel, BorderLayout.CENTER);

		clipTable = new ClipTable(movielist, this);
		filterTree = new FilterTree(movielist, clipTable);
		AbstractClipCharSortSelector charSelector;
		if (LookAndFeelManager.isSubstance())
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
		
		new FileDrop(clipTable, true, this);
	}
	
	public void beginBlockingIntermediate() {
		CCMovieList.beginBlocking();
		
		try {
			SwingUtilities.invokeAndWait(() ->
			{
				getProgressBar().setValue(0);
				getProgressBar().setIndeterminate(true);
				setEnabled(false);
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
		}
	}
	
	public void endBlockingIntermediate() {
		CCMovieList.endBlocking();

		Runnable runnner = () ->
		{
			getProgressBar().setIndeterminate(false);
			setEnabled(true);
		};
		
		if (SwingUtilities.isEventDispatchThread()) {
			runnner.run();
		} else {
			SwingUtilities.invokeLater(runnner);
		}
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
	
	public void onClipTableExecute(CCDatabaseElement ccDatabaseElement) {
		if (ccDatabaseElement == null) {
			return;
		}
		
		if (ccDatabaseElement.isMovie()) {
			switch (CCProperties.getInstance().PROP_ON_DBLCLICK_MOVE.getValue()) {
			case PLAY:
				CCActionTree.getInstance().find(CCActionTree.EVENT_ON_MOVIE_EXECUTED_0).execute(ActionSource.DIRECT_CLICK);
				break;
			case PREVIEW:
				CCActionTree.getInstance().find(CCActionTree.EVENT_ON_MOVIE_EXECUTED_1).execute(ActionSource.DIRECT_CLICK);
				break;
			case EDIT:
				CCActionTree.getInstance().find(CCActionTree.EVENT_ON_MOVIE_EXECUTED_2).execute(ActionSource.DIRECT_CLICK);
				break;
			}
		} else {
			CCActionTree.getInstance().find(CCActionTree.EVENT_ON_SERIES_EXECUTED).execute(ActionSource.DIRECT_CLICK);
		}
	}
	
	public void onClipTableSecondaryExecute(CCDatabaseElement element, MouseEvent e) {
		if (element.isMovie()) {
			(new ClipMoviePopup((CCMovie)element)).show(e.getComponent(), e.getX(), e.getY());
		} else if (element.isSeries()) {
			(new ClipSeriesPopup((CCSeries)element)).show(e.getComponent(), e.getX(), e.getY());
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
				terminate();
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
			new UpdateConnector(Main.TITLE, Main.VERSION, (src, available, version) -> SwingUtilities.invokeLater(() -> 
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
			SwingUtilities.invokeLater(() -> new QuickAddMoviesDialog(this, getMovielist(), files).setVisible(true));
		}
	}
}