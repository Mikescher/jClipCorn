package de.jClipCorn.gui.frames.compareDatabaseFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.exportElementsFrame.ExportElementsFrame;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ExtendedFocusTraversalOnArray;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.listener.ProgressCallbackProgressBarHelper;

public class CompareDatabaseFrame extends JFrame {
	private static final long serialVersionUID = 5114410004487986632L;

	private JPanel pnlTop;
	private JPanel pnlMain;
	private JLabel lblNewLabel;
	private JLabel lblDatabase;
	private ReadableTextField edDB1;
	private ReadableTextField edDB2;
	private JButton btnOpenDB1;
	private JButton btnOpenDB2;
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
	private JLabel lblDatabase_1;
	private JLabel lblNameDB1;
	private JLabel lblChecksumFile;
	private JLabel lblChecksumCover;
	private JLabel lblCheckSumDB1File;
	private JLabel lblCheckSumDB1Cover;
	private JLabel lblDatabase_2;
	private JLabel lblNameDB2;
	private JLabel label_2;
	private JLabel label_3;
	private JLabel lblCheckSumDB2File;
	private JLabel lblCheckSumDB2Cover;
	private JScrollPane scrollPane;
	private JFileChooser fchooser;
	private CCMovieList movielist;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane_2;
	private JScrollPane scrollPane_3;
	private JScrollPane scrollPane_4;
	private JScrollPane scrollPane_5;
	private JList<CompareElement> lsDB1;
	private JList<CompareElement> lsDB2;
	private JList<CompareElement> lsMissDB1;
	private JList<CompareElement> lsMissDB2;
	private JList<CompareElement> lsDiffFiles;
	private JList<CompareElement> lsDiffCover;
	private JLabel lblPathDB1;
	private JLabel lblPathDB2;
	private JLabel lblLocalIdDB1;
	private JLabel lblLocalIdDB2;
	private JLabel lblLocalId;
	private JLabel lblLocalId_1;
	private JPanel pnlTabMissingDB1Inner;
	private JPanel pnlTabMissingDB1Bottom;
	private JButton btnExportAllDB1;
	private JPanel pnlTabMissingDB2Inner;
	private JPanel pnlTabMissingDB2Bottom;
	private JButton btnExportAllDB2;

	/**
	 * @wbp.parser.constructor
	 */
	public CompareDatabaseFrame(Component owner, CCMovieList mlist) {
		super();
		this.movielist = mlist;

		initGUI();
		setLocationRelativeTo(owner);
		setFocusTraversalPolicy(new ExtendedFocusTraversalOnArray(new Component[]{btnOpenDB1, btnOpenDB2, btnGenerate, btnCompare}));

		initFileChooser();
	}
	
	public CompareDatabaseFrame(Component owner, CCMovieList mlist, File f) {
		super();
		this.movielist = mlist;

		initGUI();
		setLocationRelativeTo(owner);
		setFocusTraversalPolicy(new ExtendedFocusTraversalOnArray(new Component[]{btnOpenDB1, btnOpenDB2, btnGenerate, btnCompare}));

		initFileChooser();
		
		edDB1.setText(f.getAbsolutePath());
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("CompareDatabaseFrame.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("fill:15dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));

		lblNewLabel = new JLabel(LocaleBundle.getString("CompareDatabaseFrame.LblDatabase1.text")); //$NON-NLS-1$
		pnlTop.add(lblNewLabel, "2, 2, right, default"); //$NON-NLS-1$

		edDB1 = new ReadableTextField();
		pnlTop.add(edDB1, "4, 2, fill, default"); //$NON-NLS-1$
		edDB1.setColumns(10);

		btnOpenDB1 = new JButton("..."); //$NON-NLS-1$
		btnOpenDB1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openFile1();
			}
		});
		pnlTop.add(btnOpenDB1, "6, 2"); //$NON-NLS-1$

		btnGenerate = new JButton(LocaleBundle.getString("CompareDatabaseFrame.BtnGenerateCompareFile.text")); //$NON-NLS-1$
		btnGenerate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveCompareFile();
			}
		});
		pnlTop.add(btnGenerate, "8, 2"); //$NON-NLS-1$

		lblDatabase = new JLabel(LocaleBundle.getString("CompareDatabaseFrame.LblDatabase2.text")); //$NON-NLS-1$
		pnlTop.add(lblDatabase, "2, 4, right, default"); //$NON-NLS-1$

		edDB2 = new ReadableTextField();
		pnlTop.add(edDB2, "4, 4, fill, default"); //$NON-NLS-1$
		edDB2.setColumns(10);

		btnOpenDB2 = new JButton("..."); //$NON-NLS-1$
		btnOpenDB2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile2();
			}
		});
		pnlTop.add(btnOpenDB2, "6, 4"); //$NON-NLS-1$

		btnCompare = new JButton(LocaleBundle.getString("CompareDatabaseFrame.BtnCompare.text")); //$NON-NLS-1$
		btnCompare.setEnabled(false);
		btnCompare.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startCompare();
			}
		});
		pnlTop.add(btnCompare, "8, 4"); //$NON-NLS-1$

		progressBar = new JProgressBar();
		pnlTop.add(progressBar, "2, 6, 7, 1"); //$NON-NLS-1$

		pnlMain = new JPanel();
		getContentPane().add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow(100)"), //$NON-NLS-1$
				ColumnSpec.decode("default:grow"), }, //$NON-NLS-1$
				new RowSpec[] { RowSpec.decode("364px:grow"), //$NON-NLS-1$
						FormSpecs.RELATED_GAP_ROWSPEC, }));

		tabPnlMain = new JTabbedPane(JTabbedPane.TOP);
		tabPnlMain.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		pnlMain.add(tabPnlMain, "2, 1, fill, fill"); //$NON-NLS-1$

		pnlTabDB1 = new JPanel();
		tabPnlMain.addTab(LocaleBundle.getString("CompareDatabaseFrame.tabPnlMain.database1.caption"), null, pnlTabDB1, null); //$NON-NLS-1$
		pnlTabDB1.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		pnlTabDB1.add(scrollPane, BorderLayout.CENTER);

		lsDB1 = new JList<>();
		lsDB1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsDB1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onSelection(lsDB1, e);
			}
		});
		scrollPane.setViewportView(lsDB1);

		pnlTabDB2 = new JPanel();
		tabPnlMain.addTab(LocaleBundle.getString("CompareDatabaseFrame.tabPnlMain.database2.caption"), null, pnlTabDB2, null); //$NON-NLS-1$
		pnlTabDB2.setLayout(new BorderLayout(0, 0));

		scrollPane_1 = new JScrollPane();
		pnlTabDB2.add(scrollPane_1, BorderLayout.CENTER);

		lsDB2 = new JList<>();
		lsDB2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsDB2.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onSelection(lsDB2, e);
			}
		});
		scrollPane_1.setViewportView(lsDB2);

		pnlTabMissingDB1 = new JPanel();
		tabPnlMain.addTab(LocaleBundle.getString("CompareDatabaseFrame.tabPnlMain.missingdb1.caption"), null, pnlTabMissingDB1, null); //$NON-NLS-1$
		pnlTabMissingDB1.setLayout(new BorderLayout(0, 0));
		
		pnlTabMissingDB1Inner = new JPanel();
		pnlTabMissingDB1.add(pnlTabMissingDB1Inner, BorderLayout.CENTER);
		pnlTabMissingDB1Inner.setLayout(new BorderLayout(0, 0));

		scrollPane_2 = new JScrollPane();
		pnlTabMissingDB1Inner.add(scrollPane_2, BorderLayout.CENTER);

		lsMissDB1 = new JList<>();
		lsMissDB1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsMissDB1.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						onSelection(lsMissDB1, e);
					}
				});
		scrollPane_2.setViewportView(lsMissDB1);

		pnlTabMissingDB1Bottom = new JPanel();
		pnlTabMissingDB1Inner.add(pnlTabMissingDB1Bottom, BorderLayout.SOUTH);

		btnExportAllDB1 = new JButton(LocaleBundle.getString("CompareDatabaseFrame.btnExport.caption")); //$NON-NLS-1$
		btnExportAllDB1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exportMoviesDB2(); // Sie fehlen in DB1, deshalb DB2 exportieren
			}
		});
		btnExportAllDB1.setEnabled(false);
		pnlTabMissingDB1Bottom.add(btnExportAllDB1);

		pnlTabMissingDB2 = new JPanel();
		tabPnlMain.addTab(LocaleBundle.getString("CompareDatabaseFrame.tabPnlMain.missingdb2.caption"), null, pnlTabMissingDB2, null); //$NON-NLS-1$
		pnlTabMissingDB2.setLayout(new BorderLayout(0, 0));
		
		pnlTabMissingDB2Inner = new JPanel();
		pnlTabMissingDB2.add(pnlTabMissingDB2Inner, BorderLayout.CENTER);
		pnlTabMissingDB2Inner.setLayout(new BorderLayout(0, 0));

		scrollPane_3 = new JScrollPane();
		pnlTabMissingDB2Inner.add(scrollPane_3);

		lsMissDB2 = new JList<>();
		lsMissDB2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsMissDB2.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onSelection(lsMissDB2, e);
			}
		});
		scrollPane_3.setViewportView(lsMissDB2);

		pnlTabMissingDB2Bottom = new JPanel();
		pnlTabMissingDB2.add(pnlTabMissingDB2Bottom, BorderLayout.SOUTH);

		btnExportAllDB2 = new JButton(LocaleBundle.getString("CompareDatabaseFrame.btnExport.caption")); //$NON-NLS-1$
		btnExportAllDB2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportMoviesDB1();// Sie fehlen in DB2, deshalb DB1 exportieren
			}
		});
		btnExportAllDB2.setEnabled(false);
		pnlTabMissingDB2Bottom.add(btnExportAllDB2);

		pnlTabDifferentFiles = new JPanel();
		tabPnlMain.addTab(LocaleBundle.getString("CompareDatabaseFrame.tabPnlMain.differentfiles.caption"), null, pnlTabDifferentFiles, null); //$NON-NLS-1$
		pnlTabDifferentFiles.setLayout(new BorderLayout(0, 0));

		scrollPane_4 = new JScrollPane();
		pnlTabDifferentFiles.add(scrollPane_4, BorderLayout.CENTER);

		lsDiffFiles = new JList<>();
		lsDiffFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsDiffFiles.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onSelection(lsDiffFiles, e);
			}
		});
		scrollPane_4.setViewportView(lsDiffFiles);

		pnlTabDifferentCover = new JPanel();
		tabPnlMain.addTab(LocaleBundle.getString("CompareDatabaseFrame.tabPnlMain.differentcovers.caption"), null, pnlTabDifferentCover, null); //$NON-NLS-1$
		pnlTabDifferentCover.setLayout(new BorderLayout(0, 0));

		scrollPane_5 = new JScrollPane();
		pnlTabDifferentCover.add(scrollPane_5, BorderLayout.CENTER);

		lsDiffCover = new JList<>();
		lsDiffCover.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsDiffCover.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onSelection(lsDiffCover, e);
			}
		});
		scrollPane_5.setViewportView(lsDiffCover);

		pnlInfo = new JPanel();
		pnlMain.add(pnlInfo, "3, 1, fill, fill"); //$NON-NLS-1$
		pnlInfo.setLayout(new GridLayout(2, 1, 0, 0));

		pnlInfoDB1 = new JPanel();
		pnlInfo.add(pnlInfoDB1);
		pnlInfoDB1.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
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
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));

		lblDatabase_1 = new JLabel(LocaleBundle.getString("CompareDatabaseFrame.LblDatabase1.text")); //$NON-NLS-1$
		lblDatabase_1.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		lblDatabase_1.setHorizontalAlignment(SwingConstants.CENTER);
		pnlInfoDB1.add(lblDatabase_1, "2, 2, 3, 1"); //$NON-NLS-1$

		lblNameDB1 = new JLabel();
		lblNameDB1.setHorizontalAlignment(SwingConstants.CENTER);
		pnlInfoDB1.add(lblNameDB1, "2, 4, 3, 1"); //$NON-NLS-1$

		lblChecksumFile = new JLabel(LocaleBundle.getString("CompareDatabaseFrame.LblChecksumFile.text")); //$NON-NLS-1$
		pnlInfoDB1.add(lblChecksumFile, "2, 6"); //$NON-NLS-1$

		lblCheckSumDB1File = new JLabel();
		pnlInfoDB1.add(lblCheckSumDB1File, "4, 6"); //$NON-NLS-1$

		lblChecksumCover = new JLabel(LocaleBundle.getString("CompareDatabaseFrame.LblChecksumCover.text")); //$NON-NLS-1$
		pnlInfoDB1.add(lblChecksumCover, "2, 8"); //$NON-NLS-1$

		lblCheckSumDB1Cover = new JLabel();
		pnlInfoDB1.add(lblCheckSumDB1Cover, "4, 8"); //$NON-NLS-1$
		
		lblPathDB1 = new JLabel();
		pnlInfoDB1.add(lblPathDB1, "2, 12, 3, 1"); //$NON-NLS-1$
		
		lblLocalId = new JLabel(LocaleBundle.getString("CompareDatabaseFrame.LblLocalID.text")); //$NON-NLS-1$
		pnlInfoDB1.add(lblLocalId, "2, 10"); //$NON-NLS-1$
		
		lblLocalIdDB1 = new JLabel();
		pnlInfoDB1.add(lblLocalIdDB1, "4, 10"); //$NON-NLS-1$

		pnlInfoDB2 = new JPanel();
		pnlInfo.add(pnlInfoDB2);
		pnlInfoDB2.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
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
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));

		lblDatabase_2 = new JLabel(LocaleBundle.getString("CompareDatabaseFrame.LblDatabase2.text")); //$NON-NLS-1$
		lblDatabase_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblDatabase_2.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		pnlInfoDB2.add(lblDatabase_2, "2, 2, 3, 1"); //$NON-NLS-1$

		lblNameDB2 = new JLabel();
		lblNameDB2.setHorizontalAlignment(SwingConstants.CENTER);
		pnlInfoDB2.add(lblNameDB2, "2, 4, 3, 1"); //$NON-NLS-1$

		label_2 = new JLabel(LocaleBundle.getString("CompareDatabaseFrame.LblChecksumFile.text")); //$NON-NLS-1$
		pnlInfoDB2.add(label_2, "2, 6"); //$NON-NLS-1$

		lblCheckSumDB2File = new JLabel();
		pnlInfoDB2.add(lblCheckSumDB2File, "4, 6"); //$NON-NLS-1$

		label_3 = new JLabel(LocaleBundle.getString("CompareDatabaseFrame.LblChecksumCover.text")); //$NON-NLS-1$
		pnlInfoDB2.add(label_3, "2, 8"); //$NON-NLS-1$

		lblCheckSumDB2Cover = new JLabel();
		pnlInfoDB2.add(lblCheckSumDB2Cover, "4, 8"); //$NON-NLS-1$
		
		lblPathDB2 = new JLabel();
		pnlInfoDB2.add(lblPathDB2, "2, 12, 3, 1"); //$NON-NLS-1$
		
		lblLocalId_1 = new JLabel(LocaleBundle.getString("CompareDatabaseFrame.LblLocalID.text")); //$NON-NLS-1$
		pnlInfoDB2.add(lblLocalId_1, "2, 10"); //$NON-NLS-1$
		
		lblLocalIdDB2 = new JLabel();
		pnlInfoDB2.add(lblLocalIdDB2, "4, 10"); //$NON-NLS-1$
		
		setSize(850, 500);
	}

	private void initFileChooser() {
		fchooser = new JFileChooser(PathFormatter.getRealSelfDirectory());

		fchooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jcccf.description", ExportHelper.EXTENSION_COMPAREFILE)); //$NON-NLS-1$

		fchooser.setAcceptAllFileFilterUsed(false);
	}

	private void openFile1() {
		if (fchooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			edDB1.setText(fchooser.getSelectedFile().getAbsolutePath());
		}
		
		btnCompare.setEnabled((! (edDB1.getText().isEmpty() || edDB2.getText().isEmpty())));
	}

	private void openFile2() {
		if (fchooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			edDB2.setText(fchooser.getSelectedFile().getAbsolutePath());
		}
		
		btnCompare.setEnabled((! (edDB1.getText().isEmpty() || edDB2.getText().isEmpty())));
	}

	private void saveCompareFile() {
		if (fchooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String path = PathFormatter.forceExtension(fchooser.getSelectedFile().getAbsolutePath(), ExportHelper.EXTENSION_COMPAREFILE);

			setEnabled(false);

			progressBar.setMaximum(movielist.getElementCount());
			progressBar.setValue(0);

			final String fpath = path;

			new Thread(new Runnable() {
				@Override
				public void run() {
					generateCompareFile(fpath);

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							CompareDatabaseFrame.this.setEnabled(true);
							progressBar.setValue(0);
						}
					});
				}
			}, "THREAD_GENERATE_DBCOMPARE_FILE").start(); //$NON-NLS-1$
		}
	}

	@SuppressWarnings("nls")
	private void generateCompareFile(String path) {
		Document xml = new Document(new Element("database"));

		Element root = xml.getRootElement();

		root.setAttribute("version", Main.VERSION);
		root.setAttribute("dbversion", Main.DBVERSION);
		root.setAttribute("date", CCDate.getCurrentDate().getSimpleStringRepresentation());
		root.setAttribute("elementcount", movielist.getElementCount() + "");

		for (Iterator<CCMovie> it = movielist.iteratorMovies(); it.hasNext();) {
			CCDatabaseElement del = it.next();

			del.generateXML(root, true, true, false);

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
			SimpleFileUtils.writeTextFile(path, xmlstring);
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}

	private void startCompare() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final List<CompareElement> fl = DatabaseComparator.compare(new File(edDB1.getText()), new File(edDB2.getText()), new ProgressCallbackProgressBarHelper(progressBar));

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateGUI(fl);
					}
				});
			}
		}, "THREAD_COMPARE_DATABASES").start(); //$NON-NLS-1$
	}

	private void updateGUI(List<CompareElement> list) {
		DefaultListModel<CompareElement> lmDB1 = new DefaultListModel<>();
		DefaultListModel<CompareElement> lmDB2 = new DefaultListModel<>();
		DefaultListModel<CompareElement> lmMissDB1 = new DefaultListModel<>();
		DefaultListModel<CompareElement> lmMissDB2 = new DefaultListModel<>();
		DefaultListModel<CompareElement> lmDiffFiles = new DefaultListModel<>();
		DefaultListModel<CompareElement> lmDiffCover = new DefaultListModel<>();

		lsDB1.setModel(lmDB1);
		lsDB2.setModel(lmDB2);
		lsMissDB1.setModel(lmMissDB1);
		lsMissDB2.setModel(lmMissDB2);
		lsDiffFiles.setModel(lmDiffFiles);
		lsDiffCover.setModel(lmDiffCover);

		for (int i = 0; i < list.size(); i++) {
			CompareElement cel = list.get(i);

			if (cel.isInDB1()) {
				lmDB1.addElement(cel);
			}

			if (cel.isInDB2()) {
				lmDB2.addElement(cel);
			}

			if (!cel.isInDB1()) {
				lmMissDB1.addElement(cel);
			}

			if (!cel.isInDB2()) {
				lmMissDB2.addElement(cel);
			}

			if (cel.isDifferentCover()) {
				lmDiffCover.addElement(cel);
			}

			if (cel.isDifferentFiles()) {
				lmDiffFiles.addElement(cel);
			}
		}
		
		btnExportAllDB1.setEnabled(true);
		btnExportAllDB2.setEnabled(true);
	}
	
	private void onSelection(JList<CompareElement> list, ListSelectionEvent e) {
		if (! e.getValueIsAdjusting()) {
			CompareElement scel = list.getModel().getElementAt(((DefaultListSelectionModel) (e.getSource())).getLeadSelectionIndex());
			
			if (scel.isInDB1()) {
				lblLocalIdDB1.setText(scel.getCSLIDDB1() + ""); //$NON-NLS-1$
				lblNameDB1.setText(scel.getCompleteTitle());
				lblCheckSumDB1File.setText(scel.getCSFileDB1());
				lblCheckSumDB1Cover.setText(scel.getCSCoverDB1());
				lblPathDB1.setText(scel.getPathDB1());
			} else {
				lblLocalIdDB1.setText(""); //$NON-NLS-1$
				lblNameDB1.setText(""); //$NON-NLS-1$
				lblCheckSumDB1File.setText(""); //$NON-NLS-1$
				lblCheckSumDB1Cover.setText(""); //$NON-NLS-1$
				lblPathDB1.setText(""); //$NON-NLS-1$
			}
			
			if (scel.isInDB2()) {
				lblLocalIdDB2.setText(scel.getCSLIDDB2() + ""); //$NON-NLS-1$
				lblNameDB2.setText(scel.getCompleteTitle());
				lblCheckSumDB2File.setText(scel.getCSFileDB2());
				lblCheckSumDB2Cover.setText(scel.getCSCoverDB2());
				lblPathDB2.setText(scel.getPathDB2());
			} else {
				lblLocalIdDB2.setText(""); //$NON-NLS-1$
				lblNameDB2.setText(""); //$NON-NLS-1$
				lblCheckSumDB2File.setText(""); //$NON-NLS-1$
				lblCheckSumDB2Cover.setText(""); //$NON-NLS-1$
				lblPathDB2.setText(""); //$NON-NLS-1$
			}
		}
	}
	
	private void exportMoviesDB1() {
		ExportElementsFrame.clearAndDispose();
		
		for (int i = 0; i < lsMissDB2.getModel().getSize(); i++) {
			int id = lsMissDB2.getModel().getElementAt(i).getCSLIDDB1();
			CCDatabaseElement d = movielist.findDatabaseElement(id);
			if (d != null) {
				ExportElementsFrame.addElementToList(this, movielist, d);
			}
		}
	}
	
	private void exportMoviesDB2() {
		ExportElementsFrame.clearAndDispose();
		
		for (int i = 0; i < lsMissDB1.getModel().getSize(); i++) {
			int id = lsMissDB1.getModel().getElementAt(i).getCSLIDDB2();
			CCDatabaseElement d = movielist.findDatabaseElement(id);
			if (d != null) {
				ExportElementsFrame.addElementToList(this, movielist, d);
			}
		}
	}
}
