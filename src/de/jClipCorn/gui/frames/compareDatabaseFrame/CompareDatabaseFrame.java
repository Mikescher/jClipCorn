package de.jClipCorn.gui.frames.compareDatabaseFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.FileChooserHelper;
import de.jClipCorn.util.PathFormatter;
import de.jClipCorn.util.TextFileUtils;

public class CompareDatabaseFrame extends JFrame {
	private static final long serialVersionUID = 5114410004487986632L;
	
	private static final String EXTENSION = "jcccf";
	
	private JPanel pnlTop;
	private JPanel pnlMain;
	private JLabel lblNewLabel;
	private JLabel lblDatabase;
	private ReadableTextField edDB1;
	private ReadableTextField edDB2;
	private JButton button;
	private JButton button_1;
	private JButton btnGenerate;
	private JProgressBar progressBar;
	private JButton btnCompare;
	private JPanel pnlInfo;
	private JTabbedPane tabPnlMain;
	private JPanel pnlTabDB1;
	private JPanel pnlTabDB2;
	private JPanel pnlTabMissingDB1;
	private JPanel pnlTabMissingDB2;
	private JPanel pnlTabDifferentFiles;
	private JPanel pnlTabDifferentCover;
	private JPanel pnlInfoDB2;
	private JPanel pnlInfoDB1;
	private JLabel lblDB1Cover;
	private JLabel lblDatabase_1;
	private JLabel lblZyklusUndTitle;
	private JLabel lblChecksumFile;
	private JLabel lblChecksumCover;
	private JLabel lblCheckSumDB1File;
	private JLabel lblCheckSumDB1Cover;
	private JLabel lblDatabase_2;
	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;
	private JLabel lblCheckSumDB2File;
	private JLabel lblCheckSumDB2Cover;
	private JLabel label_6;
	private JScrollPane scrollPane;
	private JList lsDB1;
	
	private JFileChooser fchooser;
	private CCMovieList movielist;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane_2;
	private JScrollPane scrollPane_3;
	private JScrollPane scrollPane_4;
	private JScrollPane scrollPane_5;
	private JList lsDB2;
	private JList lsMissDB1;
	private JList lsMissDB2;
	private JList lsDiffFiles;
	private JList lsDiffCover;
	
	public CompareDatabaseFrame(Component owner, CCMovieList mlist) {
		super();
		this.movielist = mlist;
		
		initGUI();
		setLocationRelativeTo(owner);
		
		initFileChooser();
	}
	
	private void initGUI() {
		setTitle("Compare shit together");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("fill:default"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		lblNewLabel = new JLabel("Database 1");
		pnlTop.add(lblNewLabel, "2, 2, right, default");
		
		edDB1 = new ReadableTextField();
		pnlTop.add(edDB1, "4, 2, 3, 1, fill, default");
		edDB1.setColumns(10);
		
		button = new JButton("...");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openFile1();
			}
		});
		pnlTop.add(button, "8, 2");
		
		lblDatabase = new JLabel("Database 2");
		pnlTop.add(lblDatabase, "2, 4, right, default");
		
		edDB2 = new ReadableTextField();
		pnlTop.add(edDB2, "4, 4, fill, default");
		edDB2.setColumns(10);
		
		button_1 = new JButton("...");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile2();
			}
		});
		pnlTop.add(button_1, "6, 4");
		
		btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveCompareFile();
			}
		});
		pnlTop.add(btnGenerate, "8, 4");
		
		progressBar = new JProgressBar();
		pnlTop.add(progressBar, "2, 6, 5, 1");
		
		btnCompare = new JButton("Compare");
		btnCompare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startCompare();
			}
		});
		pnlTop.add(btnCompare, "8, 6");
		
		pnlMain = new JPanel();
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow(100)"),
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				RowSpec.decode("364px:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		tabPnlMain = new JTabbedPane(JTabbedPane.TOP);
		tabPnlMain.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		pnlMain.add(tabPnlMain, "2, 1, fill, fill");
		
		pnlTabDB1 = new JPanel();
		tabPnlMain.addTab("Database 1", null, pnlTabDB1, null);
		pnlTabDB1.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		pnlTabDB1.add(scrollPane, BorderLayout.CENTER);
		
		lsDB1 = new JList();
		lsDB1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(lsDB1);
		
		pnlTabDB2 = new JPanel();
		tabPnlMain.addTab("Database 2", null, pnlTabDB2, null);
		pnlTabDB2.setLayout(new BorderLayout(0, 0));
		
		scrollPane_1 = new JScrollPane();
		pnlTabDB2.add(scrollPane_1, BorderLayout.CENTER);
		
		lsDB2 = new JList();
		lsDB2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_1.setViewportView(lsDB2);
		
		pnlTabMissingDB1 = new JPanel();
		tabPnlMain.addTab("Missing in DB 1", null, pnlTabMissingDB1, null);
		pnlTabMissingDB1.setLayout(new BorderLayout(0, 0));
		
		scrollPane_2 = new JScrollPane();
		pnlTabMissingDB1.add(scrollPane_2, BorderLayout.CENTER);
		
		lsMissDB1 = new JList();
		lsMissDB1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_2.setViewportView(lsMissDB1);
		
		pnlTabMissingDB2 = new JPanel();
		tabPnlMain.addTab("Missing in DB 2", null, pnlTabMissingDB2, null);
		pnlTabMissingDB2.setLayout(new BorderLayout(0, 0));
		
		scrollPane_3 = new JScrollPane();
		pnlTabMissingDB2.add(scrollPane_3, BorderLayout.CENTER);
		
		lsMissDB2 = new JList();
		lsMissDB2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_3.setViewportView(lsMissDB2);
		
		pnlTabDifferentFiles = new JPanel();
		tabPnlMain.addTab("Different Files", null, pnlTabDifferentFiles, null);
		pnlTabDifferentFiles.setLayout(new BorderLayout(0, 0));
		
		scrollPane_4 = new JScrollPane();
		pnlTabDifferentFiles.add(scrollPane_4, BorderLayout.CENTER);
		
		lsDiffFiles = new JList();
		lsDiffFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_4.setViewportView(lsDiffFiles);
		
		pnlTabDifferentCover = new JPanel();
		tabPnlMain.addTab("Different Cover", null, pnlTabDifferentCover, null);
		pnlTabDifferentCover.setLayout(new BorderLayout(0, 0));
		
		scrollPane_5 = new JScrollPane();
		pnlTabDifferentCover.add(scrollPane_5, BorderLayout.CENTER);
		
		lsDiffCover = new JList();
		lsDiffCover.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane_5.setViewportView(lsDiffCover);
		
		pnlInfo = new JPanel();
		pnlMain.add(pnlInfo, "3, 1, fill, fill");
		pnlInfo.setLayout(new GridLayout(2, 1, 0, 0));
		
		pnlInfoDB1 = new JPanel();
		pnlInfo.add(pnlInfoDB1);
		pnlInfoDB1.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		lblDatabase_1 = new JLabel("Database 1");
		lblDatabase_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDatabase_1.setHorizontalAlignment(SwingConstants.CENTER);
		pnlInfoDB1.add(lblDatabase_1, "2, 2, 5, 1");
		
		lblZyklusUndTitle = new JLabel("ZYKLUS UND TITLE");
		lblZyklusUndTitle.setHorizontalAlignment(SwingConstants.CENTER);
		pnlInfoDB1.add(lblZyklusUndTitle, "2, 4, 5, 1");
		
		lblChecksumFile = new JLabel("Checksum File");
		pnlInfoDB1.add(lblChecksumFile, "2, 6");
		
		lblCheckSumDB1File = new JLabel("AAAAAAAAAAAAAAAA");
		pnlInfoDB1.add(lblCheckSumDB1File, "4, 6");
		
		lblChecksumCover = new JLabel("Checksum Cover");
		pnlInfoDB1.add(lblChecksumCover, "2, 8");
		
		lblDB1Cover = new JLabel("");
		lblDB1Cover.setIcon(new ImageIcon("C:\\Users\\ZEUS\\Eigene EDV\\java\\Eclipse\\workspace\\jClipCorn\\data\\cvr sml.png"));
		pnlInfoDB1.add(lblDB1Cover, "6, 6, 1, 5, right, bottom");
		
		lblCheckSumDB1Cover = new JLabel("AAAAAAAAAAAAAAAA");
		pnlInfoDB1.add(lblCheckSumDB1Cover, "4, 8");
		
		pnlInfoDB2 = new JPanel();
		pnlInfo.add(pnlInfoDB2);
		pnlInfoDB2.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		lblDatabase_2 = new JLabel("Database 2");
		lblDatabase_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblDatabase_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		pnlInfoDB2.add(lblDatabase_2, "2, 2, 5, 1");
		
		label_1 = new JLabel("ZYKLUS UND TITLE");
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		pnlInfoDB2.add(label_1, "2, 4, 5, 1");
		
		label_2 = new JLabel("Checksum File");
		pnlInfoDB2.add(label_2, "2, 6");
		
		lblCheckSumDB2File = new JLabel("AAAAAAAAAAAAAAAA");
		pnlInfoDB2.add(lblCheckSumDB2File, "4, 6");
		
		label_3 = new JLabel("Checksum Cover");
		pnlInfoDB2.add(label_3, "2, 8");
		
		lblCheckSumDB2Cover = new JLabel("AAAAAAAAAAAAAAAA");
		pnlInfoDB2.add(lblCheckSumDB2Cover, "4, 8");
		
		label_6 = new JLabel("");
		label_6.setIcon(new ImageIcon("C:\\Users\\ZEUS\\Eigene EDV\\java\\Eclipse\\workspace\\jClipCorn\\data\\cvr sml.png"));
		pnlInfoDB2.add(label_6, "6, 6, 1, 5");
	}
	
	private void initFileChooser() {
		fchooser = new JFileChooser(PathFormatter.getRealSelfDirectory());
		
		fchooser.setFileFilter(FileChooserHelper.createFileFilter("jClipcorn-Compare-File (*.jcccf)", EXTENSION));
		
		fchooser.setAcceptAllFileFilterUsed(false);
	}
	
	private void openFile1() {
		if (fchooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			edDB1.setText(fchooser.getSelectedFile().getAbsolutePath());
		}
	}
	
	private void openFile2() {
		if (fchooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			edDB2.setText(fchooser.getSelectedFile().getAbsolutePath());
		}
	}
	
	private void saveCompareFile() {
		if (fchooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String path = fchooser.getSelectedFile().getAbsolutePath();
			
			if (! StringUtils.endsWithIgnoreCase(path, '.' + EXTENSION)) {
				path += '.' + EXTENSION;
			}
			
			setEnabled(false);
			
			progressBar.setMaximum(movielist.getElementCount());
			progressBar.setValue(0);
			
			final String fpath = path;
			
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					generateCompareFile(fpath);
					
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							CompareDatabaseFrame.this.setEnabled(true);
						}
					});
				}
			});
			
			t.start();
		}
	}
	
	@SuppressWarnings("nls")
	private void generateCompareFile(String path) {
		Document xml = new Document(new Element("database"));
		
		Element root = xml.getRootElement();
		
		root.setAttribute("version", Main.VERSION);
		root.setAttribute("dbversion", Main.DBVERSION);
		root.setAttribute("date", new CCDate().getSimpleStringRepresentation());
		root.setAttribute("elementcount", movielist.getElementCount() + "");
		
		for (Iterator<CCMovie> it = movielist.iteratorMovies(); it.hasNext();) { //TODO Optoin include series
			CCDatabaseElement del = it.next();
			
			del.generateXML(root, true, true);
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					progressBar.setValue(progressBar.getValue() + 1);
				}
			});
		}
		
		XMLOutputter xout = new XMLOutputter();
		xout.setFormat(Format.getPrettyFormat());
		String xmlstring = xout.outputString(xml);
		
		try {
			TextFileUtils.writeTextFile(path, xmlstring);
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}
	
	private void startCompare() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final ArrayList<CompareElement> l = DatabaseComparator.compare(new File(edDB1.getText()), new File(edDB1.getText()), progressBar);
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateGUI(l);
					}
				});
			}
		}).start();
	}
	
	private void updateGUI(ArrayList<CompareElement> list) {
		DefaultListModel<String> lmDB1 = new DefaultListModel<>();
		DefaultListModel<String> lmDB2 = new DefaultListModel<>();
		DefaultListModel<String> lmMissDB1 = new DefaultListModel<>();
		DefaultListModel<String> lmMissDB2 = new DefaultListModel<>();
		DefaultListModel<String> lmDiffFiles = new DefaultListModel<>();
		DefaultListModel<String> lmDiffCover = new DefaultListModel<>();
		
		lsDB1.setModel(lmDB1);
		lsDB2.setModel(lmDB2);
		lsMissDB1.setModel(lmMissDB1);
		lsMissDB2.setModel(lmMissDB2);
		lsDiffFiles.setModel(lmDiffFiles);
		lsDiffCover.setModel(lmDiffCover);
		
		for (int i = 0; i < list.size(); i++) {
			CompareElement cel = list.get(i);
			
			if (cel.isInDB1()) {
				lmDB1.addElement(cel.getTitle());
			}
			
			if (cel.isInDB2()) {
				lmDB2.addElement(cel.getTitle());
			}
			
			if (! cel.isInDB1()) {
				lmMissDB1.addElement(cel.getTitle());
			}
			
			if (! cel.isInDB2()) {
				lmMissDB2.addElement(cel.getTitle());
			}
			
			if (cel.isDifferentCover()) {
				lmDiffCover.addElement(cel.getTitle());
			}
			
			if (cel.isDifferentFiles()) {
				lmDiffFiles.addElement(cel.getTitle());
			}
		}
	}
}
