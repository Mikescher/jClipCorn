package de.jClipCorn.gui.frames.parseWatchDataFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.DefaultReadOnlyTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.parser.watchdata.WatchDataChangeSet;
import de.jClipCorn.util.parser.watchdata.WatchDataParser;

public class ParseWatchDataFrame extends JFrame {
	private static final long serialVersionUID = 7237565086883500709L;
	
	private final CCMovieList movielist;
	
	private List<WatchDataChangeSet> changeSet = null;
	
	private JPanel pnlContent;
	private JPanel pnlButtons;
	private JButton btnExecute;
	private JButton btnCancel;
	private JPanel pnlLeft;
	private JPanel pnlRight;
	private JScrollPane scrollPnlData;
	private JTextArea memoData;
	private Component horizontalStrut;
	private Component verticalStrut;
	private Component verticalStrut_1;
	private JPanel pnlLog;
	private JPanel pnlResults;
	private JScrollPane scrollPane_1;
	private JTextArea memoLog;
	private JScrollPane scrollPnlTable;
	private JTable tableResults;
	private Component verticalStrut_2;
	private Component verticalStrut_3;
	private Component horizontalStrut_2;
	private Component horizontalStrut_3;
	private Component verticalStrut_4;
	private JButton btnShowExample;

	public ParseWatchDataFrame(Component owner, CCMovieList ml) {
		super();
		this.movielist = ml;
		
		initGUI();
		
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setSize(new Dimension(1000, 550));
		setMinimumSize(new Dimension(600, 500));
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setTitle(LocaleBundle.getString("ParseWatchDataFrame.this.title")); //$NON-NLS-1$
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		pnlContent = new JPanel();
		getContentPane().add(pnlContent, BorderLayout.CENTER);
		pnlContent.setLayout(new GridLayout(1, 0, 5, 0));
		
		pnlLeft = new JPanel();
		pnlContent.add(pnlLeft);
		pnlLeft.setLayout(new BorderLayout(0, 0));
		
		scrollPnlData = new JScrollPane();
		pnlLeft.add(scrollPnlData, BorderLayout.CENTER);
		
		memoData = new JTextArea();
		memoData.setFont(new Font("Courier New", Font.PLAIN, 12)); //$NON-NLS-1$
		memoData.setTabSize(2);
		memoData.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				dataUpdated();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				dataUpdated();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				dataUpdated();
			}
		});
		scrollPnlData.setViewportView(memoData);
		
		horizontalStrut = Box.createHorizontalStrut(5);
		pnlLeft.add(horizontalStrut, BorderLayout.WEST);
		
		verticalStrut = Box.createVerticalStrut(5);
		pnlLeft.add(verticalStrut, BorderLayout.SOUTH);
		
		verticalStrut_1 = Box.createVerticalStrut(5);
		pnlLeft.add(verticalStrut_1, BorderLayout.NORTH);
		
		pnlRight = new JPanel();
		pnlContent.add(pnlRight);
		pnlRight.setLayout(new GridLayout(2, 1, 0, 0));
		
		pnlResults = new JPanel();
		pnlRight.add(pnlResults);
		pnlResults.setLayout(new BorderLayout(0, 0));
		
		scrollPnlTable = new JScrollPane();
		pnlResults.add(scrollPnlTable);
		
		tableResults = new JTable();
		tableResults.setFillsViewportHeight(true);
		scrollPnlTable.setViewportView(tableResults);
		
		verticalStrut_2 = Box.createVerticalStrut(5);
		pnlResults.add(verticalStrut_2, BorderLayout.NORTH);
		
		verticalStrut_3 = Box.createVerticalStrut(5);
		pnlResults.add(verticalStrut_3, BorderLayout.SOUTH);
		
		horizontalStrut_2 = Box.createHorizontalStrut(5);
		pnlResults.add(horizontalStrut_2, BorderLayout.EAST);
		
		pnlLog = new JPanel();
		pnlRight.add(pnlLog);
		pnlLog.setLayout(new BorderLayout(0, 0));
		
		scrollPane_1 = new JScrollPane();
		pnlLog.add(scrollPane_1);
		
		memoLog = new JTextArea();
		memoLog.setEditable(false);
		scrollPane_1.setViewportView(memoLog);
		
		horizontalStrut_3 = Box.createHorizontalStrut(5);
		pnlLog.add(horizontalStrut_3, BorderLayout.EAST);
		
		verticalStrut_4 = Box.createVerticalStrut(5);
		pnlLog.add(verticalStrut_4, BorderLayout.SOUTH);
		
		pnlButtons = new JPanel();
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);
		
		btnExecute = new JButton(LocaleBundle.getString("ParseWatchDataFrame.btnExecute.text")); //$NON-NLS-1$
		btnExecute.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (WatchDataChangeSet w : changeSet) {
					w.execute();
				}
				
				dispose();
			}
		});
		btnExecute.setEnabled(false);
		pnlButtons.add(btnExecute);
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		pnlButtons.add(btnCancel);
		
		btnShowExample = new JButton(LocaleBundle.getString("ParseWatchDataFrame.btnExamples.text")); //$NON-NLS-1$
		btnShowExample.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					memoData.setText(SimpleFileUtils.readTextResource("/watchdata_example.txt", this.getClass())); //$NON-NLS-1$
					memoLog.setText(""); //$NON-NLS-1$
				} catch (IOException e) {
					CCLog.addError(e);
				}
			}
		});
		pnlButtons.add(btnShowExample);
	}
	
	@SuppressWarnings("nls")
	private void dataUpdated() {
		List<String> errors = new ArrayList<>();
		List<WatchDataChangeSet> set;
				
		set = WatchDataParser.parse(movielist, memoData.getText(), errors);
		
		memoLog.setText("");
		for (String err : errors) {
			memoLog.append(err + "\n");
		}
		
		btnExecute.setEnabled(errors.isEmpty());
		
		updateResults(set);
	}
	
	private void updateResults(List<WatchDataChangeSet> change) {
		changeSet = change;
		
		Vector<String> titles = new Vector<>();
		titles.add(LocaleBundle.getString("ParseWatchDataFrame.tableResults.header_0")); //$NON-NLS-1$
		titles.add(LocaleBundle.getString("ParseWatchDataFrame.tableResults.header_1")); //$NON-NLS-1$
		titles.add(LocaleBundle.getString("ParseWatchDataFrame.tableResults.header_2")); //$NON-NLS-1$
		titles.add(LocaleBundle.getString("ParseWatchDataFrame.tableResults.header_3")); //$NON-NLS-1$
		
		DefaultTableModel tm = new DefaultReadOnlyTableModel(titles, 0);
		
		for (WatchDataChangeSet wcds : change) {
			tm.addRow(new String[]{wcds.getDate(), wcds.getName(), wcds.getSubInfo(), wcds.getChange()});
		}
		
		tableResults.setModel(tm);
	}
}

/*
 TESTDATA
 
Lucifer
	[17.08.2016 17:55:36]: E01
	[17.08.2016 19:30:58]: E02

Lucifer [S01]
	[17.08.2016 17:55:36]: E03
	[17.08.2016 19:30:58]: E04

Lucifer
	E05: [17.08.2016 17:55:36]
	E06: 17.08.2016
	17.08.2016: E07,E08

Survivor {{++}}

Rain Man [11.1.2000]
Ted {{++}} [11.1.2000 12:5:22]

[17.07.2016 17:55:36]: Company
[17.08.2016 17:55:36]: Fargo {{---}}
[17.07.2016]: Heat 
 
 
*/
