package de.jClipCorn.gui.frames.watchHistoryFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryElement;
import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryEpisodeElement;
import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryMovieElement;
import de.jClipCorn.gui.guiComponents.cover.CoverLabelFullsize;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.jSimpleTree.JSimpleTree;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeNode;
import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeRenderer;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.CCDBUpdateAdapter;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WatchHistoryFrame extends JCCFrame {
	private static final long serialVersionUID = 7210078286644540662L;
	
	private final List<WatchHistoryElement> data = new ArrayList<>();

	private Tuple<WatchHistoryFilterType, WatchHistoryTimespanType> _currentFilter = null;

	private CCDBUpdateListener _mlListener;

	private DefaultMutableTreeNode treeTimespanRoot;

	public WatchHistoryFrame(Component owner, CCMovieList mlist) {
		super(mlist);
		
		initComponents();
		postInit();

		setLocationRelativeTo(owner);

		loadDataFromMovieList(false);
	}

	private void postInit()
	{
		listHistory.setCellRenderer(new WatchHistoryListCellRenderer());

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

		treeTimespan.setModel(new DefaultTreeModel(treeTimespanRoot));
		treeTimespan.expandPath(new TreePath(node_all.getPath()));
		treeTimespan.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeTimespan.setCellRenderer(new SimpleTreeRenderer());

		movielist.addChangeListener(_mlListener = new CCDBUpdateAdapter() {
			@Override
			public void onChangeDatabaseElement(CCDatabaseElement root, ICCDatabaseStructureElement el, String[] props) {
				if (CCStreams.iterate(props).any(p -> Str.equals(p, "ViewedHistory"))) {
					loadDataFromMovieList(true);
				}
			}
		});
	}

	private void loadDataFromMovieList(boolean isReload)
	{
		data.clear();

		for (CCMovie mov : movielist.iteratorMovies()) {
			for (CCDateTime timestamp : mov.ViewedHistory.get().ccstream().filter(d -> !d.isUnspecifiedDateTime())) {
				data.add(new WatchHistoryMovieElement(timestamp, mov));
			}
		}

		for (CCEpisode episode : movielist.iteratorEpisodes()) {
			for (CCDateTime timestamp : episode.ViewedHistory.get().ccstream().filter(d -> !d.isUnspecifiedDateTime())) {
				data.add(new WatchHistoryEpisodeElement(timestamp, episode));
			}
		}

		Collections.sort(data);

		if (isReload && _currentFilter != null)
		{
			var vh = tableMain.getHorizontalScrollBar().getValue();
			var vv = tableMain.getVerticalScrollBar().getValue();

			filterTable(_currentFilter.Item1, _currentFilter.Item2);

			tableMain.getHorizontalScrollBar().setValue(vh);
			tableMain.getVerticalScrollBar().setValue(vv);
		}
		else
		{
			filterTable(WatchHistoryFilterType.ALL, WatchHistoryTimespanType.LAST_HUNDRED);

			tableMain.autoResize();
		}
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
		_currentFilter = Tuple.Create(filter, time);
	}

	public void onSelect(WatchHistoryElement elem) {
		lblCover.setAndResizeCover(elem.getCover());
		edName1.setText(elem.getFullNamePart1());
		edName2.setText(elem.getFullNamePart2());
		edName3.setText(elem.getFullNamePart3());

		DefaultListModel<CCDateTime> dt = new DefaultListModel<>();
		for (CCDateTime stamp : elem.getHistory().ccstream()) dt.addElement(stamp);

		listHistory.setModel(dt);
	}

	private void onTreeExpanded(TreeExpansionEvent e) {
		for (int i = 0; i < treeTimespanRoot.getChildCount(); i++) {
			TreePath tp = new TreePath(((DefaultMutableTreeNode)treeTimespanRoot.getChildAt(i)).getPath());
			if (! tp.equals(e.getPath())) {
				treeTimespan.collapsePath(tp);
			}
		}
	}

	private void onWindowClosed() {
		movielist.removeChangeListener(_mlListener);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		scrollPane2 = new JScrollPane();
		treeTimespan = new JSimpleTree();
		pnlRight = new JPanel();
		tableMain = new WatchHistoryTable(this);
		panel1 = new JPanel();
		label1 = new JLabel();
		edName1 = new ReadableTextField();
		edName2 = new ReadableTextField();
		edName3 = new ReadableTextField();
		label2 = new JLabel();
		scrollPane1 = new JScrollPane();
		listHistory = new JList<>();
		lblCover = new CoverLabelFullsize(movielist);

		//======== this ========
		setTitle(LocaleBundle.getString("WatchHistoryFrame.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(600, 415));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				onWindowClosed();
			}
		});
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $rgap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default:grow, $rgap, default, $ugap")); //$NON-NLS-1$

		//======== scrollPane2 ========
		{

			//---- treeTimespan ----
			treeTimespan.setRootVisible(false);
			treeTimespan.setToggleClickCount(1);
			treeTimespan.addTreeExpansionListener(new TreeExpansionListener() {
				@Override
				public void treeCollapsed(TreeExpansionEvent e) {}
				@Override
				public void treeExpanded(TreeExpansionEvent e) {
					onTreeExpanded(e);
				}
			});
			scrollPane2.setViewportView(treeTimespan);
		}
		contentPane.add(scrollPane2, CC.xy(2, 2, CC.DEFAULT, CC.FILL));

		//======== pnlRight ========
		{
			pnlRight.setLayout(new FormLayout(
				"default:grow", //$NON-NLS-1$
				"default:grow, $lgap, default")); //$NON-NLS-1$
			pnlRight.add(tableMain, CC.xy(1, 1, CC.FILL, CC.FILL));

			//======== panel1 ========
			{
				panel1.setLayout(new FormLayout(
					"default, $ugap, 2*(default:grow, $rgap), default:grow", //$NON-NLS-1$
					"default, $lgap, default")); //$NON-NLS-1$

				//---- label1 ----
				label1.setText(LocaleBundle.getString("WatchHistoryFrame.lblName")); //$NON-NLS-1$
				panel1.add(label1, CC.xy(1, 1, CC.RIGHT, CC.DEFAULT));
				panel1.add(edName1, CC.xy(3, 1));
				panel1.add(edName2, CC.xy(5, 1));
				panel1.add(edName3, CC.xy(7, 1));

				//---- label2 ----
				label2.setText(LocaleBundle.getString("WatchHistoryFrame.title")); //$NON-NLS-1$
				panel1.add(label2, CC.xy(1, 3, CC.RIGHT, CC.TOP));

				//======== scrollPane1 ========
				{

					//---- listHistory ----
					listHistory.setVisibleRowCount(4);
					scrollPane1.setViewportView(listHistory);
				}
				panel1.add(scrollPane1, CC.xywh(3, 3, 5, 1));
			}
			pnlRight.add(panel1, CC.xy(1, 3));
		}
		contentPane.add(pnlRight, CC.xywh(4, 2, 1, 3));
		contentPane.add(lblCover, CC.xy(2, 4));
		setSize(875, 660);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollPane scrollPane2;
	private JSimpleTree treeTimespan;
	private JPanel pnlRight;
	private WatchHistoryTable tableMain;
	private JPanel panel1;
	private JLabel label1;
	private ReadableTextField edName1;
	private ReadableTextField edName2;
	private ReadableTextField edName3;
	private JLabel label2;
	private JScrollPane scrollPane1;
	private JList<CCDateTime> listHistory;
	private CoverLabelFullsize lblCover;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
