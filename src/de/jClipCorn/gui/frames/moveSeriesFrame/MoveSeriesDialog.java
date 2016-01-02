package de.jClipCorn.gui.frames.moveSeriesFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.DefaultReadOnlyTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;

public class MoveSeriesDialog extends JDialog {
	private static final long serialVersionUID = 8795232362998343872L;
	
	private final CCSeries series;
	
	private JLabel lblReplace;
	private JTextField edSearch;
	private JTextField edReplace;
	private JButton btnNewButton;
	private CoverLabel lblCover;
	private JLabel lblTitel;
	private JButton btnTest;
	private JScrollPane scrollPane;
	private JTable tabTest;
	private JPanel pnlTop;
	private JPanel pnlBottom;
	private JPanel pnlLeft;
	private JLabel lblWith;

	public MoveSeriesDialog(Component owner, CCSeries series) {
		super();
		setSize(new Dimension(500, 600));
		this.series = series;
		
		initGUI();
		
		setLocationRelativeTo(owner);
		
		init();
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setModal(true);
		setTitle(LocaleBundle.getString("MoveSeriesFrame.this.title")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BorderLayout(0, 0));
		
		lblCover = new CoverLabel(false);
		pnlTop.add(lblCover, BorderLayout.WEST);
		
		pnlLeft = new JPanel();
		pnlTop.add(pnlLeft, BorderLayout.CENTER);
		pnlLeft.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("38px:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("23px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
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
		
		lblTitel = new JLabel();
		pnlLeft.add(lblTitel, "2, 2, 3, 1, left, center"); //$NON-NLS-1$
		lblTitel.setFont(new Font("Tahoma", Font.PLAIN, 14)); //$NON-NLS-1$
		
		lblReplace = new JLabel(LocaleBundle.getString("MoveSeriesFrame.lblReplace.text")); //$NON-NLS-1$
		pnlLeft.add(lblReplace, "2, 6, left, center"); //$NON-NLS-1$
		
		edSearch = new JTextField();
		pnlLeft.add(edSearch, "2, 8, 3, 1, fill, center"); //$NON-NLS-1$
		edSearch.setColumns(10);
		
		lblWith = new JLabel(LocaleBundle.getString("MoveSeriesFrame.lblWith.text")); //$NON-NLS-1$
		pnlLeft.add(lblWith, "2, 10"); //$NON-NLS-1$
		
		edReplace = new JTextField();
		pnlLeft.add(edReplace, "2, 12, 3, 1, fill, center"); //$NON-NLS-1$
		edReplace.setColumns(10);
		
		btnNewButton = new JButton(LocaleBundle.getString("AddMovieFrame.btnOK.text")); //$NON-NLS-1$
		pnlLeft.add(btnNewButton, "2, 14, center, top"); //$NON-NLS-1$
		
		btnTest = new JButton(LocaleBundle.getString("MoveSeriesFrame.btnTest.text")); //$NON-NLS-1$
		pnlLeft.add(btnTest, "4, 14, center, top"); //$NON-NLS-1$
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
		lblTitel.setText(series.getTitle());
		lblCover.setIcon(series.getCoverIcon());
		edSearch.setText(series.getCommonPathStart(false));
	}
	
	private void startReplace() {
		if (! DialogHelper.showLocaleYesNo(this, "Dialogs.MoveSeries")) { //$NON-NLS-1$
			return;
		}
		
		for (int seasi = 0; seasi < series.getSeasonCount(); seasi++) {
			CCSeason season = series.getSeason(seasi);
			for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
				CCEpisode ep = season.getEpisode(epi);
				
				ep.setPart(ep.getPart().replace(edSearch.getText(), edReplace.getText()));
			}
		}
		
		dispose();
	}
	
	private void startTest() {
		Vector<Vector<String>> data = new Vector<>();
		
		for (int seasi = 0; seasi < series.getSeasonCount(); seasi++) {
			CCSeason season = series.getSeason(seasi);
			for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
				CCEpisode ep = season.getEpisode(epi);
				
				String oldp = ep.getPart();
				String newp = ep.getPart().replace(edSearch.getText(), edReplace.getText());

				String identOld = new File(PathFormatter.fromCCPath(oldp)).exists() ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$
				String identNew = new File(PathFormatter.fromCCPath(newp)).exists() ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$
				
				Vector<String> tmp = new Vector<>();
				tmp.add(identOld + oldp);
				tmp.add(identNew + newp);
				
				data.add(tmp);
			}
		}
		
		DefaultTableModel dtm = new DefaultReadOnlyTableModel();
		dtm.addColumn("Old"); //$NON-NLS-1$
		dtm.addColumn("New"); //$NON-NLS-1$
		
		for (Vector<String> row : data) {
			dtm.addRow(row);
		}

		tabTest.setModel(dtm);
	}
}
