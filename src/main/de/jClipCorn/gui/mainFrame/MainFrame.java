package de.jClipCorn.gui.mainFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
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
import de.jClipCorn.features.table.filter.customFilter.CustomSearchFilter;
import de.jClipCorn.gui.frames.quickAddMoviesDialog.QuickAddMoviesDialog;
import de.jClipCorn.gui.frames.showUpdateFrame.ShowUpdateFrame;
import de.jClipCorn.gui.guiComponents.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.guiComponents.FileDrop;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.charSelector.ClipCharSortSelector;
import de.jClipCorn.gui.mainFrame.filterTree.FilterTree;
import de.jClipCorn.gui.mainFrame.menuBar.MainFrameMenuBar;
import de.jClipCorn.gui.mainFrame.searchField.SearchField;
import de.jClipCorn.gui.mainFrame.statusbar.ClipStatusBar;
import de.jClipCorn.gui.mainFrame.table.ClipTable;
import de.jClipCorn.gui.mainFrame.table.RowFilterSource;
import de.jClipCorn.gui.mainFrame.toolbar.ClipToolbar;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.UpdateConnector;
import de.jClipCorn.util.adapter.CCDBUpdateAdapter;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class MainFrame extends JCCFrame implements FileDrop.Listener, IActionRootFrame
{
	private static MainFrame instance = null;

	private final CCDBUpdateListener _mlListener;

	public MainFrame(CCMovieList movielist) {
		super(movielist);

		movielist.addChangeListener(_mlListener = new CCDBUpdateAdapter() { @Override public void onAfterLoad() { onMovieListAfterLoad(); } });
		CCActionTree actionTree = new CCActionTree(this);

		initComponents();
		postInit();

		actionTree.implementKeyListener(this, (JPanel) getContentPane());

		instance = this;

		if (ccprops().firstLaunch) {
			JOptionPane.showMessageDialog(this,
					LocaleBundle.getString("MainFrame.disclaimer.text"),  //$NON-NLS-1$
					LocaleBundle.getString("MainFrame.disclaimer.caption"),  //$NON-NLS-1$
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void postInit() {
		setTitle(createTitle());

		setJMenuBar(new MainFrameMenuBar());

		btnSearch.setIcon(Resources.ICN_FRAMES_SEARCH.get16x16());
		filterTree.init(clipTable);

		if (CCLog.hasErrors()) coverImage.setModeError();

		setSize(ccprops().PROP_MAINFRAME_WIDTH.getValue(), ccprops().PROP_MAINFRAME_HEIGHT.getValue());

		clipTable.configureColumnVisibility(ccprops().PROP_MAINFRAME_VISIBLE_COLUMNS.getValue(), true);

		new FileDrop(clipTable, true, this);

		setLocationRelativeTo(null);
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

		if (movielist.isReadonly()) {
			builder.append(" [READONLY]");
		}

		return builder.toString();
	}

	public static MainFrame getInstance() {
		return instance;
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

	public void startSearch() {
		String search = edSearch.getRealText().trim();
		if (search.isEmpty()) {
			clipTable.setRowFilter(null, RowFilterSource.TEXTFIELD);
		} else {
			clipTable.setRowFilter(CustomSearchFilter.create(movielist, search), RowFilterSource.TEXTFIELD);
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
			switch (ccprops().PROP_ON_DBLCLICK_MOVE.getValue()) {
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

	public void onMovieListAfterLoad() {
		if (ccprops().PROP_COMMON_CHECKFORUPDATES.getValue() && ! Main.BETA) {
			new UpdateConnector(movielist, Main.TITLE, Main.VERSION, (src, available, version) -> SwingUtils.invokeLater(() ->
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

	public DatabaseElementPreviewLabel getCoverLabel() {
		return coverImage;
	}

	@Override
	public void filesDropped(final File[] files) {
		if (files.length > 0) {

			var f = CCStreams.iterate(files).map(FSPath::create).toArray(new FSPath[0]);

			if (CCStreams.iterate(f).all(CCFileFormat::isValidMovieFormat)) {
				SwingUtils.invokeLater(() -> new QuickAddMoviesDialog(this, getMovielist(), f).setVisible(true));
				return;
			}

			if (files.length == 1) ((CCActionTree)CCActionTree.getInstance()).openFile(f[0]);
		}

		// nothing dropped
	}

	public void onSettingsChanged(java.util.List<String> changes)
	{
		if (CCStreams.iterate(changes).any(c -> Str.equals(c, ccprops().PROP_MAINFRAME_VISIBLE_COLUMNS.getIdentifier())))
		{
			clipTable.configureColumnVisibility(ccprops().PROP_MAINFRAME_VISIBLE_COLUMNS.getValue(), false);
			clipTable.autoResize();
		}

		if (CCStreams.iterate(changes).any(c -> Str.equals(c, ccprops().PROP_TOOLBAR_ELEMENTS.getIdentifier())))
		{
			toolbar.recreate();
		}

		//if (CCStreams.iterate(changes).any(c -> Str.equals(c, ccprops().PROP_UI_APPTHEME.getIdentifier())))
		//{
		//	LookAndFeelManager.setLookAndFeel(ccprops().PROP_UI_APPTHEME.getValue(), true);
		//}
	}

	private void onWindowClosing() {
		if (tryTerminate()) {
			dispose();
			terminate();
			System.exit(0);
		}
	}

	private void onWindowClosed() {
		movielist.removeChangeListener(_mlListener);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panelTop = new JPanel();
		toolbar = new ClipToolbar(movielist);
		edSearch = new SearchField(this);
		btnSearch = new JButton();
		panelLeft = new JPanel();
		filterTree = new FilterTree(movielist);
		coverImage = new DatabaseElementPreviewLabel(movielist, false);
		clipTable = new ClipTable(movielist, this);
		clipCharSelector = new ClipCharSortSelector(this);
		statusbar = new ClipStatusBar(this, movielist);

		//======== this ========
		setMinimumSize(new Dimension(875, 625));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				onWindowClosed();
			}
			@Override
			public void windowClosing(WindowEvent e) {
				onWindowClosing();
			}
		});
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$lcgap, 182px, $lcgap, 0dlu:grow, $lcgap", //$NON-NLS-1$
			"default, $lgap, default:grow, 2*($lgap, default)")); //$NON-NLS-1$

		//======== panelTop ========
		{
			panelTop.setLayout(new FormLayout(
				"default:grow, 2*($lcgap, default), $lcgap", //$NON-NLS-1$
				"default")); //$NON-NLS-1$
			panelTop.add(toolbar, CC.xy(1, 1));

			//---- edSearch ----
			edSearch.setColumns(16);
			panelTop.add(edSearch, CC.xy(3, 1));

			//---- btnSearch ----
			btnSearch.setMinimumSize(new Dimension(20, 20));
			btnSearch.setMaximumSize(new Dimension(20, 20));
			btnSearch.setPreferredSize(new Dimension(20, 20));
			btnSearch.setContentAreaFilled(false);
			btnSearch.setBorderPainted(false);
			btnSearch.setFocusPainted(false);
			btnSearch.setIcon(UIManager.getIcon("FileView.computerIcon")); //$NON-NLS-1$
			btnSearch.addActionListener(e -> startSearch());
			panelTop.add(btnSearch, CC.xy(5, 1));
		}
		contentPane.add(panelTop, CC.xywh(1, 1, 5, 1, CC.FILL, CC.FILL));

		//======== panelLeft ========
		{
			panelLeft.setLayout(new FormLayout(
				"default", //$NON-NLS-1$
				"default:grow, $lgap, default")); //$NON-NLS-1$
			panelLeft.add(filterTree, CC.xy(1, 1, CC.FILL, CC.FILL));
			panelLeft.add(coverImage, CC.xy(1, 3));
		}
		contentPane.add(panelLeft, CC.xywh(2, 3, 1, 3, CC.FILL, CC.FILL));
		contentPane.add(clipTable, CC.xy(4, 3, CC.FILL, CC.FILL));
		contentPane.add(clipCharSelector, CC.xywh(4, 5, 2, 1, CC.FILL, CC.FILL));
		contentPane.add(statusbar, CC.xywh(1, 7, 5, 1));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panelTop;
	private ClipToolbar toolbar;
	private SearchField edSearch;
	private JButton btnSearch;
	private JPanel panelLeft;
	private FilterTree filterTree;
	private DatabaseElementPreviewLabel coverImage;
	private ClipTable clipTable;
	private ClipCharSortSelector clipCharSelector;
	private ClipStatusBar statusbar;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
