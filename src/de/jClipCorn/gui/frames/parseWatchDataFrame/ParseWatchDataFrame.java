package de.jClipCorn.gui.frames.parseWatchDataFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.CCDate;

public class ParseWatchDataFrame extends JFrame {
	private static final long serialVersionUID = 7237565086883500709L;
	
	// ([A-Za-z].*)[ ]+\[S([0-9]+)\]
	private final static String REGEX_SERIES_HEADER = "([A-Za-z].*)[ ]+\\[S([0-9]+)\\]"; //$NON-NLS-1$
	// [\t ]+([0-9]+\.[0-9]+(\.[0-9]+)?)\:[\t ]*(E?\-?[0-9]+(\,[\t ]*E?\-?[0-9]+)*)
	private final static String REGEX_SERIES_CONTENT = "[\\t ]+([0-9]+\\.[0-9]+(\\.[0-9]+)?)\\:[\\t ]*(E?\\-?[0-9]+(\\,[\\t ]*E?\\-?[0-9]+)*)"; //$NON-NLS-1$
	//[^\[\]\n\r\t ][^\[\]\n\r]*
	private final static String REGEX_MOVIE_HEADER = "[^\\[\\]\\n\\r\\t ][^\\[\\]\\n\\r]*"; //$NON-NLS-1$
	
	private final static Pattern PATTERN_SERIES_HEADER = Pattern.compile(REGEX_SERIES_HEADER);
	private final static Pattern PATTERN_SERIES_CONTENT = Pattern.compile(REGEX_SERIES_CONTENT);
	private final static Pattern PATTERN_MOVIE_HEADER = Pattern.compile(REGEX_MOVIE_HEADER);
	
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
		
		btnCancel = new JButton(LocaleBundle.getString("ParseWatchDataFrame.btnCancel.text")); //$NON-NLS-1$
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
				memoData.setText(LocaleBundle.getString("ParseWatchDataFrame.example")); //$NON-NLS-1$
				memoLog.setText(""); //$NON-NLS-1$
			}
		});
		pnlButtons.add(btnShowExample);
	}
	
	@SuppressWarnings("nls")
	private void dataUpdated() {
		List<String> errors = new ArrayList<>();
		List<WatchDataChangeSet> set = new ArrayList<>();
		
		String data = memoData.getText();
		
		String[] lines = data.split("\\r?\\n");
		
		CCSeason currSeason = null;
		
		for (int i = 0; i < lines.length; i++) {
			String line = StringUtils.stripEnd(lines[i], null);
			
			if (line.trim().isEmpty()) continue;
					
			Matcher matcher;
			
			if ((matcher = PATTERN_SERIES_HEADER.matcher(line)).matches()) {
				String title = matcher.group(1).trim();
				String seasonNumber = matcher.group(2).trim();
				
				CCSeries s = null;
				
				for (Iterator<CCSeries> it = movielist.iteratorSeries(); it.hasNext();) {
					CCSeries curr = it.next();
					
					if (curr.getTitle().equalsIgnoreCase(title)) {
						s = curr;
						break;
					}
				}
				
				if (s == null) {
					errors.add(String.format("Line[%d] \"%s\" : Series \"%s\" not found", i, line.trim(), title));
					continue;
				}
				
				int sn = Integer.parseInt(seasonNumber) - 1;
				
				if (sn < 0 || sn >= s.getSeasonCount()) {
					errors.add(String.format("Line[%d] \"%s\" : Series \"%s\" has no Season with number %s (%d)", i, line.trim(), title, seasonNumber, sn));
					continue;
				}
				
				currSeason = s.getSeason(sn);
			} else if ((matcher = PATTERN_SERIES_CONTENT.matcher(line)).matches()) {
				String date = matcher.group(1).trim();
				String episodeList = matcher.group(3).trim();
				
				if (currSeason == null) {
					errors.add(String.format("Line[%d] \"%s\" : Missing series header token before this Line", i, line.trim()));
					continue;
				}
				
				CCDate d = CCDate.parse(date, "D.M.Y");
				
				if (d == null) {
					d = CCDate.parse(date, "D.M");
				}
				
				if (d == null || ! d.isValidDate()) {
					errors.add(String.format("Line[%d] \"%s\" : Date \"%s\" is no valid Date", i, line.trim(), date));
					continue;
				}
				
				String[] episodesarr = episodeList.split(",");

				for (String ep : episodesarr) {
					String e = ep.trim();
					if (e.isEmpty()) continue;
					
					boolean nState = true;
					
					if ( e.startsWith("E")) {
						e = e.substring(1);
					}
					
					if (e.startsWith("-")) {
						nState = false;
						e = e.substring(1);
					}
					
					int en;
					try {
						en = Integer.parseInt(e);
					} catch (NumberFormatException ex2) {
						errors.add(String.format("Line[%d] \"%s\" : The Episode \"%s\" could not be parsed (NaN)", i, line.trim(), e));
						continue;
					}
					
					CCEpisode epis = currSeason.getEpisodebyNumber(en);
					
					if (epis == null) {
						errors.add(String.format("Line[%d] \"%s\" : The Episode \"%s\" could not be found in the current Season", i, line.trim(), e));
						continue;
					}
					
					set.add(new EpisodeWatchDataChangedSet(d, epis, nState));
				}
			} else if ((matcher = PATTERN_MOVIE_HEADER.matcher(line)).matches()) {
				String title = matcher.group().trim();
				
				CCSeries s = null;
				CCMovie m = null;
				for (Iterator<CCSeries> it = movielist.iteratorSeries(); it.hasNext();) {
					CCSeries curr = it.next();
					
					if (curr.getTitle().equalsIgnoreCase(title)) {
						s = curr;
						break;
					}
				}
				for (Iterator<CCMovie> it = movielist.iteratorMovies(); it.hasNext();) {
					CCMovie curr = it.next();
					
					if (curr.getTitle().equalsIgnoreCase(title) || curr.getCompleteTitle().equalsIgnoreCase(title)) {
						
						if (m != null) {
							errors.add(String.format("Line[%d] \"%s\" : Movie \"%s\" has more then 1 results in database", i, line.trim(), title));
							continue;
						}
						
						m = curr;
					}
				}
				
				if (s == null && m == null) {
					errors.add(String.format("Line[%d] \"%s\" : Movie/Series \"%s\" not found", i, line.trim(), title));
					continue;
				}
				
				if (s != null && m != null) {
					errors.add(String.format("Line[%d] \"%s\" : Movie/Series \"%s\" is found more than one time", i, line.trim(), title));
					continue;
				}
				
				if (m != null) {
					set.add(new MovieWatchDataChangedSet(m, true));
					
					currSeason = null;
				} else if (s != null) {
					if (s.getSeasonCount() != 1) {
						errors.add(String.format("Line[%d] \"%s\" : Series \"%s\" has too many or too less Seasons (%d)", i, line.trim(), title, s.getSeasonCount()));
						continue;
					}
					
					currSeason = s.getSeason(0);
				}
			} else {
				errors.add(String.format("Line[%d] \"%s\" : Could not parse Line", i, line.trim()));
				continue;
			}
		}
		
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
		
		DefaultTableModel tm = new DefaultTableModel(titles, 0);
		
		for (WatchDataChangeSet wcds : change) {
			tm.addRow(new String[]{wcds.getDate(), wcds.getName(), wcds.getSubInfo(), wcds.getChange()});
		}
		
		tableResults.setModel(tm);
	}
}
