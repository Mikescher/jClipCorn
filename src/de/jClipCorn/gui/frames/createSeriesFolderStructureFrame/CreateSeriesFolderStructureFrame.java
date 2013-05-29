package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class CreateSeriesFolderStructureFrame extends JFrame {
	private static final long serialVersionUID = 8494757660196292481L;
	
	private CCSeries series;
	
	private JPanel pnlTop;
	private CoverLabel lblCover;
	private JPanel pnlLeft;
	private JLabel lblTitel;
	private ReadableTextField edPath;
	private JButton btnChoose;
	private JScrollPane scrlPnlBottom;
	private JList lsTest;
	private JButton btnOk;
	private JButton btnTest;

	public CreateSeriesFolderStructureFrame(CCSeries ser) {
		super();
		this.series = ser;
		
		initGUI();
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle(LocaleBundle.getString("CreateSeriesFolderStructureFrame.this.title")); //$NON-NLS-1$
		
		pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BorderLayout(0, 0));
		
		lblCover = new CoverLabel(false);
		pnlTop.add(lblCover, BorderLayout.WEST);
		
		pnlLeft = new JPanel();
		pnlTop.add(pnlLeft, BorderLayout.CENTER);
		pnlLeft.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		lblTitel = new JLabel();
		lblTitel.setFont(new Font("Tahoma", Font.PLAIN, 14)); //$NON-NLS-1$
		pnlLeft.add(lblTitel, "2, 2, 5, 1"); //$NON-NLS-1$
		
		edPath = new ReadableTextField();
		pnlLeft.add(edPath, "2, 4, 3, 1, fill, default"); //$NON-NLS-1$
		edPath.setColumns(10);
		
		btnChoose = new JButton(LocaleBundle.getString("AddMovieFrame.btnChoose.text")); //$NON-NLS-1$
		btnChoose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onBtnChoose();
			}
		});
		pnlLeft.add(btnChoose, "6, 4"); //$NON-NLS-1$
		
		btnOk = new JButton(LocaleBundle.getString("AddMovieFrame.btnOK.text")); //$NON-NLS-1$
		btnOk.setEnabled(false);
		pnlLeft.add(btnOk, "2, 8, center, default"); //$NON-NLS-1$
		
		btnTest = new JButton(LocaleBundle.getString("MoveSeriesFrame.btnTest.text")); //$NON-NLS-1$
		btnTest.setEnabled(false);
		pnlLeft.add(btnTest, "4, 8, center, default"); //$NON-NLS-1$
		
		scrlPnlBottom = new JScrollPane();
		scrlPnlBottom.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrlPnlBottom, BorderLayout.CENTER);
		
		lsTest = new JList();
		scrlPnlBottom.setViewportView(lsTest);
		
		setSize(550, 600);
	}
	
	private void onBtnChoose() {
		JFileChooser folderchooser = new JFileChooser(series.getCommonPathStart());
		folderchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		if (folderchooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			if (new File(folderchooser.getSelectedFile().getAbsolutePath()).isDirectory()) {
				edPath.setText(folderchooser.getSelectedFile().getAbsolutePath());
			}
		}
	}
}
