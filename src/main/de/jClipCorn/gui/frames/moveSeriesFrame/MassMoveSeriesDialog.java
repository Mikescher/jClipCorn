package de.jClipCorn.gui.frames.moveSeriesFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.gui.guiComponents.DefaultReadOnlyTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.helper.DialogHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class MassMoveSeriesDialog extends JDialog {
	private static final long serialVersionUID = 8795232362998343872L;

	private final CCMovieList movielist;
	private final List<CCEpisode> episodes;
	
	private JLabel lblReplace;
	private JTextField edSearch;
	private JTextField edReplace;
	private JButton btnNewButton;
	private JButton btnTest;
	private JScrollPane scrollPane;
	private JTable tabTest;
	private JPanel pnlTop;
	private JPanel pnlBottom;
	private JPanel pnlLeft;
	private JLabel lblWith;

	public MassMoveSeriesDialog(Component owner, CCMovieList mlist) {
		super();
		setSize(new Dimension(800, 600));
		this.movielist = mlist;
		this.episodes = mlist.iteratorEpisodes().enumerate();
		
		initGUI();
		
		setLocationRelativeTo(owner);
		
		init();
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setModal(true);
		setTitle(LocaleBundle.getString("MassMoveSeriesFrame.this.title")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BorderLayout(0, 0));
		
		pnlLeft = new JPanel();
		pnlTop.add(pnlLeft, BorderLayout.CENTER);
		pnlLeft.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("38px:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		lblReplace = new JLabel(LocaleBundle.getString("MassMoveSeriesFrame.lblReplace.text")); //$NON-NLS-1$
		pnlLeft.add(lblReplace, "2, 2, left, center"); //$NON-NLS-1$
		
		edSearch = new JTextField();
		pnlLeft.add(edSearch, "2, 4, 3, 1, fill, center"); //$NON-NLS-1$
		edSearch.setColumns(10);
		
		lblWith = new JLabel(LocaleBundle.getString("MassMoveSeriesFrame.lblWith.text")); //$NON-NLS-1$
		pnlLeft.add(lblWith, "2, 6"); //$NON-NLS-1$
		
		edReplace = new JTextField();
		pnlLeft.add(edReplace, "2, 8, 3, 1, fill, center"); //$NON-NLS-1$
		edReplace.setColumns(10);
		
		btnNewButton = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		pnlLeft.add(btnNewButton, "2, 10, center, top"); //$NON-NLS-1$
		
		btnTest = new JButton(LocaleBundle.getString("MassMoveSeriesFrame.btnTest.text")); //$NON-NLS-1$
		pnlLeft.add(btnTest, "4, 10, center, top"); //$NON-NLS-1$
		btnTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startTest();
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startReplace();
			}
		});
		
		pnlBottom = new JPanel();
		getContentPane().add(pnlBottom);
		pnlBottom.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		pnlBottom.add(scrollPane);
		
		tabTest = new JTable();
		tabTest.setDefaultRenderer(Object.class, new MoveSeriesTestTableCellRenderer());
		tabTest.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(tabTest);
	}
	
	private void init() {
		edSearch.setText(movielist.getCommonSeriesPath().toString());
	}
	
	private void startReplace() {
		if (! DialogHelper.showLocaleYesNo(this, "Dialogs.MassMoveSeries")) { //$NON-NLS-1$
			return;
		}
		
		for (CCEpisode ep : episodes) {
			var p = ep.getPart().toString();
			p = p.replace(edSearch.getText(), edReplace.getText());

			ep.Part.set(CCPath.create(p));
		}
		
		dispose();
	}
	
	private void startTest() {
		Vector<Vector<String>> data = new Vector<>();

		for (CCEpisode ep : episodes) {
			var oldp = ep.getPart();
			var newp = CCPath.create(ep.getPart().toString().replace(edSearch.getText(), edReplace.getText()));

			String identOld = oldp.toFSPath().exists() ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$
			String identNew = newp.toFSPath().exists() ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$

			Vector<String> tmp = new Vector<>();

			tmp.add("2" + ep.getSeries().getTitle()); //$NON-NLS-1$
				
			tmp.add(identOld + oldp);
			tmp.add(identNew + newp);
			
			data.add(tmp);
		}
		
		DefaultTableModel dtm = new DefaultReadOnlyTableModel();
		dtm.addColumn(LocaleBundle.getString("MoveSeriesFrame.Table.HeaderSeries")); //$NON-NLS-1$
		dtm.addColumn(LocaleBundle.getString("MoveSeriesFrame.Table.HeaderOld")); //$NON-NLS-1$
		dtm.addColumn(LocaleBundle.getString("MoveSeriesFrame.Table.HeaderNew")); //$NON-NLS-1$
		
		for (Vector<String> row : data) {
			dtm.addRow(row);
		}

		tabTest.setModel(dtm);
	}
}
