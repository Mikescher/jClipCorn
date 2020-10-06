package de.jClipCorn.gui.frames.watchHistoryFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryElement;
import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryEpisodeElement;
import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryMovieElement;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.jSimpleTree.JSimpleTree;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeNode;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeRenderer;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.adapter.TreeExpansionAdapter;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.util.stream.CCStreams;

public class WatchHistoryFrame extends JFrame {
	private static final long serialVersionUID = 7210078286644540662L;
	
	private final CCMovieList movielist;
	private List<WatchHistoryElement> data = new ArrayList<>();
	
	private JPanel contentPane;
	private JScrollPane scrollPaneTree;
	private JTree treeTimespan;
	private DefaultMutableTreeNode treeTimespanRoot;
	private CoverLabel lblCover;
	private JPanel pnlRight;
	private WatchHistoryTable tableMain;
	private JPanel pnlInfo;
	private JLabel lblName;
	private JLabel lblHistory;
	private ReadableTextField edName1;
	private JScrollPane scrollPaneHistory;
	private JList<CCDateTime> listHistory;
	private ReadableTextField edName2;
	private ReadableTextField edName3;

	public WatchHistoryFrame(Component owner, CCMovieList mlist) {
		super();
		
		movielist = mlist;
		
		initGUI();
		
		setLocationRelativeTo(owner);
		
		loadDataFromMovieList();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("WatchHistoryFrame.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setMinimumSize(new Dimension(600, 415));
		setBounds(100, 100, 875, 660);
		if (LookAndFeelManager.isMetal()) setBounds(100, 100, 875, 670);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		scrollPaneTree = new JScrollPane();
		contentPane.add(scrollPaneTree, "1, 1, fill, fill"); //$NON-NLS-1$
		
		treeTimespan = createTimespanRoot();
		treeTimespan.addTreeExpansionListener(new TreeExpansionAdapter() {
			@Override
			public void treeExpanded(TreeExpansionEvent e) {
				for (int i = 0; i < treeTimespanRoot.getChildCount(); i++) {
					TreePath tp = new TreePath(((DefaultMutableTreeNode)treeTimespanRoot.getChildAt(i)).getPath());
					if (! tp.equals(e.getPath())) {
						treeTimespan.collapsePath(tp);
					}
				}
			}
		});
		scrollPaneTree.setViewportView(treeTimespan);
		
		pnlRight = new JPanel();
		contentPane.add(pnlRight, "3, 1, 1, 3, fill, fill"); //$NON-NLS-1$
		pnlRight.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		tableMain = new WatchHistoryTable(this);
		pnlRight.add(tableMain, "1, 1, fill, fill"); //$NON-NLS-1$
		
		pnlInfo = new JPanel();
		pnlRight.add(pnlInfo, "1, 3, fill, fill"); //$NON-NLS-1$
		pnlInfo.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),})); //$NON-NLS-1$
		
		lblName = new JLabel(LocaleBundle.getString("WatchHistoryFrame.lblName")); //$NON-NLS-1$
		pnlInfo.add(lblName, "2, 1, right, top"); //$NON-NLS-1$
		
		edName1 = new ReadableTextField();
		pnlInfo.add(edName1, "4, 1, fill, default"); //$NON-NLS-1$
		edName1.setColumns(10);
		
		edName2 = new ReadableTextField();
		edName2.setColumns(10);
		pnlInfo.add(edName2, "6, 1, fill, default"); //$NON-NLS-1$
		
		edName3 = new ReadableTextField();
		edName3.setColumns(10);
		pnlInfo.add(edName3, "8, 1, fill, default"); //$NON-NLS-1$
		
		lblHistory = new JLabel(LocaleBundle.getString("WatchHistoryFrame.lblHistory")); //$NON-NLS-1$
		pnlInfo.add(lblHistory, "2, 3, right, top"); //$NON-NLS-1$
		
		scrollPaneHistory = new JScrollPane();
		pnlInfo.add(scrollPaneHistory, "4, 3, 5, 1, fill, fill"); //$NON-NLS-1$
		
		listHistory = new JList<>();
		listHistory.setVisibleRowCount(4);
		listHistory.setCellRenderer(new WatchHistoryListCellRenderer());
		scrollPaneHistory.setViewportView(listHistory);
		
		lblCover = new CoverLabel(false);
		contentPane.add(lblCover, "1, 3"); //$NON-NLS-1$
	}

	private JTree createTimespanRoot() {
		treeTimespanRoot = new SimpleTreeNode();
		
		SimpleTreeNode node_all = new SimpleTreeNode(Resources.ICN_HISTORY_CHRONIK.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Timeline")); //$NON-NLS-1$
		{
			node_all.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Today"),     () -> filterTable(WatchHistoryFilterType.ALL, WatchHistoryTimespanType.TODAY))); //$NON-NLS-1$
			node_all.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Yesterday"), () -> filterTable(WatchHistoryFilterType.ALL, WatchHistoryTimespanType.YESTERDAY))); //$NON-NLS-1$
			node_all.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.SevenDays"), () -> filterTable(WatchHistoryFilterType.ALL, WatchHistoryTimespanType.SEVEN_DAYS))); //$NON-NLS-1$
			node_all.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Month"),     () -> filterTable(WatchHistoryFilterType.ALL, WatchHistoryTimespanType.THIS_MONTH))); //$NON-NLS-1$
			node_all.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Year"),      () -> filterTable(WatchHistoryFilterType.ALL, WatchHistoryTimespanType.THIS_YEAR))); //$NON-NLS-1$
			node_all.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.All"),       () -> filterTable(WatchHistoryFilterType.ALL, WatchHistoryTimespanType.EVERYTHING))); //$NON-NLS-1$
		}
		treeTimespanRoot.add(node_all);

		SimpleTreeNode node_mov = new SimpleTreeNode(Resources.ICN_HISTORY_MOVIES.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Movies")); //$NON-NLS-1$
		{
			node_mov.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Today"),     () -> filterTable(WatchHistoryFilterType.MOVIES, WatchHistoryTimespanType.TODAY)));      //$NON-NLS-1$
			node_mov.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Yesterday"), () -> filterTable(WatchHistoryFilterType.MOVIES, WatchHistoryTimespanType.YESTERDAY)));  //$NON-NLS-1$
			node_mov.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.SevenDays"), () -> filterTable(WatchHistoryFilterType.MOVIES, WatchHistoryTimespanType.SEVEN_DAYS))); //$NON-NLS-1$
			node_mov.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Month"),     () -> filterTable(WatchHistoryFilterType.MOVIES, WatchHistoryTimespanType.THIS_MONTH))); //$NON-NLS-1$
			node_mov.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Year"),      () -> filterTable(WatchHistoryFilterType.MOVIES, WatchHistoryTimespanType.THIS_YEAR)));  //$NON-NLS-1$
			node_mov.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.All"),       () -> filterTable(WatchHistoryFilterType.MOVIES, WatchHistoryTimespanType.EVERYTHING))); //$NON-NLS-1$
		}
		treeTimespanRoot.add(node_mov);

		SimpleTreeNode node_ser = new SimpleTreeNode(Resources.ICN_HISTORY_SERIES.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Series")); //$NON-NLS-1$
		{
			node_ser.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Today"),     () -> filterTable(WatchHistoryFilterType.SERIES, WatchHistoryTimespanType.TODAY)));      //$NON-NLS-1$
			node_ser.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Yesterday"), () -> filterTable(WatchHistoryFilterType.SERIES, WatchHistoryTimespanType.YESTERDAY)));  //$NON-NLS-1$
			node_ser.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.SevenDays"), () -> filterTable(WatchHistoryFilterType.SERIES, WatchHistoryTimespanType.SEVEN_DAYS))); //$NON-NLS-1$
			node_ser.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Month"),     () -> filterTable(WatchHistoryFilterType.SERIES, WatchHistoryTimespanType.THIS_MONTH))); //$NON-NLS-1$
			node_ser.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.Year"),      () -> filterTable(WatchHistoryFilterType.SERIES, WatchHistoryTimespanType.THIS_YEAR)));  //$NON-NLS-1$
			node_ser.add(new SimpleTreeNode(Resources.ICN_HISTORY_ELEMENT.get(), LocaleBundle.getString("WatchHistoryFrame.tree.All"),       () -> filterTable(WatchHistoryFilterType.SERIES, WatchHistoryTimespanType.EVERYTHING))); //$NON-NLS-1$
		}
		treeTimespanRoot.add(node_ser);
		
		JTree tree = new JSimpleTree(treeTimespanRoot);
		tree.expandPath(new TreePath(node_all.getPath()));
		tree.setRootVisible(false);
		tree.setToggleClickCount(1);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new SimpleTreeRenderer());
		
		return tree;
	}
	
	private void loadDataFromMovieList() {
		data.clear();
		
		for (CCMovie mov : movielist.iteratorMovies()) {
			for (CCDateTime timestamp : mov.getViewedHistory().iterator().filter(d -> !d.isUnspecifiedDateTime())) {
				data.add(new WatchHistoryMovieElement(timestamp, mov));
			}
		}
		
		for (CCEpisode episode : movielist.iteratorEpisodes()) {
			for (CCDateTime timestamp : episode.getViewedHistory().iterator().filter(d -> !d.isUnspecifiedDateTime())) {
				data.add(new WatchHistoryEpisodeElement(timestamp, episode));
			}
		}
		
		Collections.sort(data);
		
		filterTable(WatchHistoryFilterType.ALL, WatchHistoryTimespanType.LAST_HUNDRED);
		
		tableMain.autoResize();
	}

	private void filterTable(WatchHistoryFilterType filter, WatchHistoryTimespanType time) {
		List<WatchHistoryElement> filtered = new ArrayList<>();

		CCDate nowD = CCDateTime.getCurrentDateTime().getDate();
		
		for (WatchHistoryElement elem : CCStreams.iterate(data).reverse()) {
			if (elem.isMovie() && filter == WatchHistoryFilterType.SERIES) continue;
			if (elem.isEpisode() && filter == WatchHistoryFilterType.MOVIES) continue;

			CCDate d = elem.getTimestamp().getDate();
			
			switch (time) {
			case TODAY:
				if (d.isEqual(nowD)) filtered.add(elem);
				break;
			case YESTERDAY:
				if (d.isEqual(nowD.getSubDay(1))) filtered.add(elem);
				break;
			case SEVEN_DAYS:
				if (nowD.isGreaterEqualsThan(d) && d.getDayDifferenceTo(nowD) <= 7) filtered.add(elem);
				break;
			case THIS_MONTH:
				if (d.getYear() == nowD.getYear() && d.getMonth() == nowD.getMonth()) filtered.add(elem);
				break;
			case THIS_YEAR:
				if (d.getYear() == nowD.getYear()) filtered.add(elem);
				break;
			case LAST_HUNDRED:
				if (filtered.size() < 256) filtered.add(elem);
				break;
			case EVERYTHING:
				filtered.add(elem);
				break;
			}
		}

		tableMain.setData(filtered);
	}

	public void onSelect(WatchHistoryElement elem) {
		lblCover.setAndResizeCover(elem.getCover());
		edName1.setText(elem.getFullNamePart1());
		edName2.setText(elem.getFullNamePart2());
		edName3.setText(elem.getFullNamePart3());
		
		DefaultListModel<CCDateTime> dt = new DefaultListModel<>();
		for (CCDateTime stamp : elem.getHistory().iterator()) dt.addElement(stamp);
		
		listHistory.setModel(dt);
	}
}
