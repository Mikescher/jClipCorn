package de.jClipCorn.gui.frames.mainFrame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCDBUpdateListener;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.gui.frames.mainFrame.clipCharSelector.ClipCharSortSelector;
import de.jClipCorn.gui.frames.mainFrame.clipMenuBar.ClipMenuBar;
import de.jClipCorn.gui.frames.mainFrame.clipStatusbar.ClipStatusBar;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTable;
import de.jClipCorn.gui.frames.mainFrame.clipTable.RowFilterSource;
import de.jClipCorn.gui.frames.mainFrame.clipToolbar.ClipToolbar;
import de.jClipCorn.gui.frames.mainFrame.filterTree.FilterTree;
import de.jClipCorn.gui.frames.mainFrame.popupMenus.ClipMoviePopup;
import de.jClipCorn.gui.frames.mainFrame.popupMenus.ClipSeriesPopup;
import de.jClipCorn.gui.frames.mainFrame.searchField.SearchField;
import de.jClipCorn.gui.frames.showUpdateFrame.ShowUpdateFrame;
import de.jClipCorn.gui.guiComponents.tableFilter.TableSearchFilter;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.UpdateConnector;

public class MainFrame extends JFrame implements CCDBUpdateListener {
	private static final long serialVersionUID = 1002435986107998058L;

	private final static int FRAME_WIDTH = 875;
	private final static int FRAME_HEIGHT = 625;

	private Container content;
	private ClipMenuBar mainMenu;
	private ClipToolbar toolbar;
	private JPanel leftPanel;
	private FilterTree filterTree;
	private JLabel coverImage;
	private JPanel middlePanel;
	private ClipTable clipTable;
	private ClipCharSortSelector charSelector;
	private CCMovieList movielist;
	private JPanel coverPanel;
	private JPanel gapPanel;
	private ClipStatusBar statusbar;
	private JPanel topPanel;
	private JPanel toprightPanel;
	private SearchField edSearch;
	private JButton btnSearch;

	public MainFrame(CCMovieList movielist) {
		this.movielist = movielist;
		
		movielist.addChangeListener(this);
		
		new CCActionTree(this);

		initGUI();

		createWindowListener();
	}

	private void initGUI() {
		setTitle(Main.TITLE + " v" + Main.VERSION + (Main.DEBUG ? " [DEBUG]" : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setLocationRelativeTo(null);

		content = getContentPane();

		getContentPane().setLayout(new BorderLayout(3, 5));

		createLayout();
		
		edSearch = new SearchField(this);
		toprightPanel.add(edSearch);
		edSearch.setColumns(16);
		
		btnSearch = new JButton();
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startSearch();
			}
		});
		btnSearch.setIcon(CachedResourceLoader.getSmallImageIcon(Resources.ICN_FRAMES_SEARCH));
		toprightPanel.add(btnSearch);
		content.add(leftPanel, BorderLayout.WEST);
		content.add(middlePanel, BorderLayout.CENTER);

		clipTable = new ClipTable(movielist, this);
		filterTree = new FilterTree(movielist, clipTable);
		charSelector = new ClipCharSortSelector(this);

		middlePanel.add(clipTable, BorderLayout.CENTER);
		middlePanel.add(charSelector, BorderLayout.SOUTH);

		leftPanel.add(filterTree, BorderLayout.CENTER);

		coverPanel = new JPanel();
		leftPanel.add(coverPanel, BorderLayout.SOUTH);
		coverPanel.setLayout(new BorderLayout(0, 5));

		gapPanel = new JPanel();
		coverPanel.add(gapPanel, BorderLayout.NORTH);
		gapPanel.setLayout(null);

		coverImage = new JLabel(CachedResourceLoader.getImageIcon(Resources.IMG_COVER_STANDARD));
		coverPanel.add(coverImage);
		
		statusbar = new ClipStatusBar(this, movielist);
		getContentPane().add(statusbar, BorderLayout.SOUTH);

		pack();
		
		setSize(875, 640);
	}
	
	public void startBlockingIntermediate() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					getProgressBar().setValue(0);
					getProgressBar().setIndeterminate(true);
					setEnabled(false);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace(); //BAD
		}
		
	}
	
	public void endBlockingIntermediate() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					getProgressBar().setIndeterminate(false);
					setEnabled(true);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace(); //BAD
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
		mainMenu = new ClipMenuBar();
		leftPanel = new JPanel(new BorderLayout());

		middlePanel = new JPanel(new BorderLayout());
		setJMenuBar(mainMenu); // $hide$
		
		topPanel = new JPanel();
		getContentPane().add(topPanel, BorderLayout.NORTH);
		topPanel.setLayout(new BorderLayout(0, 0));
		toolbar = new ClipToolbar();
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
			clipTable.setRowFilter(new TableSearchFilter(search), RowFilterSource.TEXTFIELD);
		}
	}

	public void start() {
		setVisible(true);
	}
	
	public JProgressBar getProgressBar() {
		return statusbar.getProgressbar();
	}
	
	public void onClipTableSelectionChanged(CCDatabaseElement ccDatabaseElement) {
		coverImage.setIcon(ccDatabaseElement.getCoverIcon());
	}
	
	public void onClipTableExecute(CCDatabaseElement ccDatabaseElement) {
		if (ccDatabaseElement == null) {
			return;
		}
		
		if (ccDatabaseElement.isMovie()) {
			switch (CCProperties.getInstance().PROP_ON_DBLCLICK_MOVE.getValue()) {
			case 0:
				CCActionTree.getInstance().find(CCActionTree.EVENT_ON_MOVIE_EXECUTED_0).execute();
				break;
			case 1:
				CCActionTree.getInstance().find(CCActionTree.EVENT_ON_MOVIE_EXECUTED_1).execute();
				break;
			}
		} else {
			CCActionTree.getInstance().find(CCActionTree.EVENT_ON_SERIES_EXECUTED).execute();
		}
	}
	
	public void onClipTableSecondaryExecute(CCDatabaseElement element, MouseEvent e) {
		if (element.isMovie()) {
			(new ClipMoviePopup()).show(e.getComponent(), e.getX(), e.getY());
		} else {
			(new ClipSeriesPopup()).show(e.getComponent(), e.getX(), e.getY());
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
	
	public void resetSearchField() {
		edSearch.reset();
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
		if (CCProperties.getInstance().PROP_COMMON_CHECKFORUPDATES.getValue()) {
			new UpdateConnector(Main.TITLE, Main.VERSION, new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							if (Main.DEBUG) {
								System.out.println("!! [DBG] Update found !!"); //$NON-NLS-1$
							} else {
								ShowUpdateFrame suf = new ShowUpdateFrame(MainFrame.this, (UpdateConnector) e.getSource());
								suf.setVisible(true);
							};
						}
					});
				}
			});
		}
	}
}
