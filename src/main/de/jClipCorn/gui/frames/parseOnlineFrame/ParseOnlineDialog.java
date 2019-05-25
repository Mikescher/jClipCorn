package de.jClipCorn.gui.frames.parseOnlineFrame;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.gui.frames.allRatingsFrame.AllRatingsDialog;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.ReadableCombobox;
import de.jClipCorn.gui.guiComponents.ReadableSpinner;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.features.online.metadata.Metadataparser;
import de.jClipCorn.features.online.metadata.OnlineMetadata;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.ExtendedFocusTraversalOnArray;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.gui.guiComponents.referenceChooser.JSingleReferenceChooser;

public class ParseOnlineDialog extends JDialog {
	private static final long serialVersionUID = 3777677368743220383L;
	
	private DefaultListModel<ParseOnlineDialogElement> mdlLsDBList;
	private BufferedImage imgCoverBI = null;
	private Map<String, Integer> cbFSKlsAll = null;
	
	private final ParseResultHandler owner;
	private final CCDBElementTyp typ;
	
	private CCSingleOnlineReference selectedReference = CCSingleOnlineReference.EMPTY;
	
	private JList<ParseOnlineDialogElement> lsDBList;
	private JPanel panel;
	private JTextField edSearchName;
	private JButton btnParse;
	private JPanel pnlMain;
	private ReadableTextField edTitle;
	private JCheckBox cbTitle;
	private JCheckBox cbYear;
	private JCheckBox cbScore;
	private JCheckBox cbLength;
	private JCheckBox cbFSK;
	private JLabel lblTitle;
	private JLabel lblYear;
	private JLabel lblScore;
	private JLabel lblLength;
	private JLabel lblFsk;
	private ReadableCombobox<String> cbxFSK;
	private ReadableSpinner spnLength;
	private ReadableSpinner spnScore;
	private ReadableSpinner spnYear;
	private JCheckBox cbGenre0;
	private JLabel lblGenre;
	private ReadableCombobox<String> cbxGenre0;
	private ReadableCombobox<String> cbxGenre2;
	private JLabel lblGenre_2;
	private JCheckBox cbGenre2;
	private ReadableCombobox<String> cbxGenre3;
	private JLabel lblGenre_3;
	private JCheckBox cbGenre3;
	private ReadableCombobox<String> cbxGenre1;
	private JLabel lblGenre_1;
	private JCheckBox cbGenre1;
	private ReadableCombobox<String> cbxGenre4;
	private ReadableCombobox<String> cbxGenre5;
	private ReadableCombobox<String> cbxGenre6;
	private ReadableCombobox<String> cbxGenre7;
	private JLabel lblGenre_7;
	private JCheckBox cbGenre7;
	private JCheckBox cbGenre6;
	private JLabel lblGenre_6;
	private JLabel lblGenre_5;
	private JCheckBox cbGenre5;
	private JCheckBox cbGenre4;
	private JLabel lblGenre_4;
	private CoverLabel imgCover;
	private JLabel lblCover;
	private JCheckBox cbCover;
	private JProgressBar pbarSearch;
	private JScrollPane scrollPane;
	private JButton btnRef;
	private JButton btnOk;
	private JButton btnFSKAll;
	private JButton btnExtendedParse;
	private JSingleReferenceChooser ctrlAltRef;

	public ParseOnlineDialog(Component owner, ParseResultHandler handler, CCDBElementTyp typ) {
		setResizable(false);
		this.owner = handler;
		this.typ = typ;
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initGUI();
		setLocationRelativeTo(owner);
		initComboboxes();
		resetAll();
		
		edSearchName.setText(handler.getFullTitle());

		setFocusTraversalPolicy(new ExtendedFocusTraversalOnArray(new Component[]{btnParse, btnExtendedParse, lsDBList, cbTitle, cbYear, cbScore, cbLength, cbFSK, cbCover, cbGenre0, cbGenre1, cbGenre2, cbGenre3, cbGenre4, cbGenre5, cbGenre6, cbGenre7, btnRef, btnFSKAll, btnOk}));
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("parseImDBFrame.this.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setModal(true);
		setBounds(0, 0, 807, 538);
		getContentPane().setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 53, 217, 421);
		getContentPane().add(scrollPane);
		
		lsDBList = new JList<>(mdlLsDBList = new DefaultListModel<>());
		lsDBList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (! e.getValueIsAdjusting()) {
					updateMainPanel();
				}
			}
		});
		lsDBList.setCellRenderer(new ParseOnlineDialogRenderer());
		scrollPane.setViewportView(lsDBList);
		
		panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setBounds(0, 0, 803, 42);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		edSearchName = new JTextField();
		edSearchName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					runSearchAsync(false);
				}
			}
		});
		edSearchName.setBounds(10, 11, 525, 20);
		panel.add(edSearchName);
		edSearchName.setColumns(10);
		
		btnParse = new JButton(LocaleBundle.getString("parseImDBFrame.btnParse.text")); //$NON-NLS-1$
		btnParse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runSearchAsync(false);
			}
		});
		btnParse.setBounds(547, 10, 89, 23);
		panel.add(btnParse);
		
		pnlMain = new JPanel();
		pnlMain.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlMain.setBounds(237, 53, 556, 448);
		getContentPane().add(pnlMain);
		pnlMain.setLayout(null);
		
		edTitle = new ReadableTextField();
		edTitle.setBounds(120, 10, 169, 20);
		pnlMain.add(edTitle);
		edTitle.setColumns(10);
		
		cbTitle = new JCheckBox();
		cbTitle.setBounds(6, 7, 21, 23);
		pnlMain.add(cbTitle);
		
		cbYear = new JCheckBox();
		cbYear.setBounds(6, 37, 21, 23);
		pnlMain.add(cbYear);
		
		cbScore = new JCheckBox();
		cbScore.setBounds(6, 67, 21, 23);
		pnlMain.add(cbScore);
		
		cbLength = new JCheckBox();
		cbLength.setBounds(6, 97, 21, 23);
		pnlMain.add(cbLength);
		
		cbFSK = new JCheckBox();
		cbFSK.setBounds(6, 127, 21, 23);
		pnlMain.add(cbFSK);
		
		lblTitle = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		lblTitle.setBounds(33, 10, 46, 14);
		pnlMain.add(lblTitle);
		
		lblYear = new JLabel(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
		lblYear.setBounds(33, 40, 46, 14);
		pnlMain.add(lblYear);
		
		lblScore = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
		lblScore.setBounds(33, 70, 77, 14);
		pnlMain.add(lblScore);
		
		lblLength = new JLabel(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
		lblLength.setBounds(33, 98, 46, 18);
		pnlMain.add(lblLength);
		
		lblFsk = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
		lblFsk.setBounds(33, 130, 46, 14);
		pnlMain.add(lblFsk);
		
		cbxFSK = new ReadableCombobox<>();
		cbxFSK.setEnabled(false);
		cbxFSK.setBounds(120, 130, 169, 20);
		pnlMain.add(cbxFSK);
		
		spnLength = new ReadableSpinner();
		spnLength.setBounds(120, 100, 169, 20);
		spnLength.setEditor(new JSpinner.NumberEditor(spnLength, "0")); //$NON-NLS-1$
		pnlMain.add(spnLength);
		
		spnScore = new ReadableSpinner();
		spnScore.setEnabled(false);
		spnScore.setBounds(120, 70, 169, 20);
		spnScore.setEditor(new JSpinner.NumberEditor(spnScore, "0")); //$NON-NLS-1$
		pnlMain.add(spnScore);
		
		spnYear = new ReadableSpinner();
		spnYear.setBounds(120, 40, 169, 20);
		spnYear.setEditor(new JSpinner.NumberEditor(spnYear, "0")); //$NON-NLS-1$
		pnlMain.add(spnYear);
		
		cbGenre0 = new JCheckBox();
		cbGenre0.setBounds(295, 7, 21, 23);
		pnlMain.add(cbGenre0);
		
		lblGenre = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
		lblGenre.setBounds(322, 10, 46, 14);
		pnlMain.add(lblGenre);
		
		cbxGenre0 = new ReadableCombobox<>();
		cbxGenre0.setEnabled(false);
		cbxGenre0.setBounds(377, 10, 169, 20);
		pnlMain.add(cbxGenre0);
		
		cbxGenre2 = new ReadableCombobox<>();
		cbxGenre2.setEnabled(false);
		cbxGenre2.setBounds(377, 70, 169, 20);
		pnlMain.add(cbxGenre2);
		
		lblGenre_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
		lblGenre_2.setBounds(322, 70, 46, 14);
		pnlMain.add(lblGenre_2);
		
		cbGenre2 = new JCheckBox();
		cbGenre2.setBounds(295, 67, 21, 23);
		pnlMain.add(cbGenre2);
		
		cbxGenre3 = new ReadableCombobox<>();
		cbxGenre3.setEnabled(false);
		cbxGenre3.setBounds(377, 100, 169, 20);
		pnlMain.add(cbxGenre3);
		
		lblGenre_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
		lblGenre_3.setBounds(322, 100, 46, 14);
		pnlMain.add(lblGenre_3);
		
		cbGenre3 = new JCheckBox();
		cbGenre3.setBounds(295, 97, 21, 23);
		pnlMain.add(cbGenre3);
		
		cbxGenre1 = new ReadableCombobox<>();
		cbxGenre1.setEnabled(false);
		cbxGenre1.setBounds(377, 40, 169, 20);
		pnlMain.add(cbxGenre1);
		
		lblGenre_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
		lblGenre_1.setBounds(322, 40, 46, 14);
		pnlMain.add(lblGenre_1);
		
		cbGenre1 = new JCheckBox();
		cbGenre1.setBounds(295, 37, 21, 23);
		pnlMain.add(cbGenre1);
		
		cbxGenre4 = new ReadableCombobox<>();
		cbxGenre4.setEnabled(false);
		cbxGenre4.setBounds(377, 130, 169, 20);
		pnlMain.add(cbxGenre4);
		
		cbxGenre5 = new ReadableCombobox<>();
		cbxGenre5.setEnabled(false);
		cbxGenre5.setBounds(377, 160, 169, 20);
		pnlMain.add(cbxGenre5);
		
		cbxGenre6 = new ReadableCombobox<>();
		cbxGenre6.setEnabled(false);
		cbxGenre6.setBounds(377, 190, 169, 20);
		pnlMain.add(cbxGenre6);
		
		cbxGenre7 = new ReadableCombobox<>();
		cbxGenre7.setEnabled(false);
		cbxGenre7.setBounds(377, 220, 169, 20);
		pnlMain.add(cbxGenre7);
		
		lblGenre_7 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
		lblGenre_7.setBounds(322, 220, 46, 14);
		pnlMain.add(lblGenre_7);
		
		cbGenre7 = new JCheckBox();
		cbGenre7.setBounds(295, 217, 21, 23);
		pnlMain.add(cbGenre7);
		
		cbGenre6 = new JCheckBox();
		cbGenre6.setBounds(295, 187, 21, 23);
		pnlMain.add(cbGenre6);
		
		lblGenre_6 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
		lblGenre_6.setBounds(322, 190, 46, 14);
		pnlMain.add(lblGenre_6);
		
		lblGenre_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
		lblGenre_5.setBounds(322, 160, 46, 14);
		pnlMain.add(lblGenre_5);
		
		cbGenre5 = new JCheckBox();
		cbGenre5.setBounds(295, 157, 21, 23);
		pnlMain.add(cbGenre5);
		
		cbGenre4 = new JCheckBox();
		cbGenre4.setBounds(295, 127, 21, 23);
		pnlMain.add(cbGenre4);
		
		lblGenre_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
		lblGenre_4.setBounds(322, 130, 46, 14);
		pnlMain.add(lblGenre_4);
		
		imgCover = new CoverLabel(false);
		imgCover.setHorizontalAlignment(SwingConstants.CENTER);
		imgCover.setBounds(107, 164, 182, 254);
		pnlMain.add(imgCover);
		
		lblCover = new JLabel(LocaleBundle.getString("AddMovieFrame.lblCover.text")); //$NON-NLS-1$
		lblCover.setBounds(33, 160, 46, 14);
		pnlMain.add(lblCover);
		
		cbCover = new JCheckBox();
		cbCover.setBounds(6, 157, 21, 23);
		pnlMain.add(cbCover);
		
		btnRef = new JButton(Resources.ICN_REF_00_BUTTON.get());
		btnRef.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (lsDBList.getSelectedIndex() >= 0) {
					HTTPUtilities.openInBrowser(lsDBList.getSelectedValue().Reference.getURL());
				}
			}
		});
		btnRef.setBounds(377, 287, 169, 23);
		pnlMain.add(btnRef);
		
		btnOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				insertDataIntoFrame();
				dispose();
			}
		});
		btnOk.setBounds(469, 414, 77, 23);
		pnlMain.add(btnOk);
		
		btnFSKAll = new JButton(LocaleBundle.getString("parseImDBFrame.btnFSKAll.text")); //$NON-NLS-1$
		btnFSKAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showAllRatingsDialog();
			}
		});
		btnFSKAll.setBounds(377, 354, 169, 23);
		pnlMain.add(btnFSKAll);
		
		ctrlAltRef = new JSingleReferenceChooser();
		ctrlAltRef.setBounds(377, 322, 169, 20);
		ctrlAltRef.setEnabled(false);
		pnlMain.add(ctrlAltRef);
		
		pbarSearch = new JProgressBar();
		pbarSearch.setBounds(10, 485, 217, 16);
		getContentPane().add(pbarSearch);
		
		btnExtendedParse = new JButton(LocaleBundle.getString("parseImDBFrame.btnParseExtended.text")); //$NON-NLS-1$
		btnExtendedParse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runSearchAsync(true);
			}
		});
		btnExtendedParse.setBounds(648, 10, 145, 23);
		panel.add(btnExtendedParse);
	}
	
	private void showAllRatingsDialog() {
		if (lsDBList.getSelectedIndex() >= 0 && cbFSKlsAll != null) {
			(new AllRatingsDialog(cbFSKlsAll, this)).setVisible(true);
		}
	}
	
	private void initComboboxes() {
		cbxFSK.setModel(new DefaultComboBoxModel<>(CCFSK.getWrapper().getList()));
		
		cbxGenre0.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre1.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre2.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre3.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre4.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre5.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre6.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
		cbxGenre7.setModel(new DefaultComboBoxModel<>(CCGenre.getTrimmedList()));
	}
	
	private void resetAll() {
		resetFields();
		resetCheckboxes();
		
		btnOk.setEnabled(false);
		
		imgCoverBI = null;
		cbFSKlsAll = null;
	}
	
	private void resetFields() {
		edTitle.setText(""); //$NON-NLS-1$
		spnLength.setValue(0);
		spnScore.setValue(0);
		spnYear.setValue(0);
		cbxFSK.setSelectedIndex(-1);
		imgCover.clearCover();
		
		cbxGenre0.setSelectedIndex(-1);
		cbxGenre1.setSelectedIndex(-1);
		cbxGenre2.setSelectedIndex(-1);
		cbxGenre3.setSelectedIndex(-1);
		cbxGenre4.setSelectedIndex(-1);
		cbxGenre5.setSelectedIndex(-1);
		cbxGenre6.setSelectedIndex(-1);
		cbxGenre7.setSelectedIndex(-1);
		
		btnRef.setIcon(Resources.ICN_REF_00_BUTTON.get());
	}
	
	private void resetCheckboxes() {
		cbCover.setSelected(false);
		cbFSK.setSelected(false);
		cbLength.setSelected(false);
		cbScore.setSelected(false);
		cbTitle.setSelected(false);
		cbYear.setSelected(false);
		
		cbGenre0.setSelected(false);
		cbGenre1.setSelected(false);
		cbGenre2.setSelected(false);
		cbGenre3.setSelected(false);
		cbGenre4.setSelected(false);
		cbGenre5.setSelected(false);
		cbGenre6.setSelected(false);
		cbGenre7.setSelected(false);
	}
	
	private void updateCheckBoxes() {
		cbCover.setSelected((imgCover.getIcon() != null) && (imgCoverBI != null));
		cbFSK.setSelected(cbxFSK.getSelectedIndex() >= 0);
		cbLength.setSelected((int)spnLength.getValue() > 0);
		cbScore.setSelected((int)spnScore.getValue() > 0);
		cbTitle.setSelected(false);
		cbYear.setSelected((int)spnYear.getValue() > 0);
		
		cbGenre0.setSelected(cbxGenre0.getSelectedIndex() > 0);
		cbGenre1.setSelected(cbxGenre1.getSelectedIndex() > 0);
		cbGenre2.setSelected(cbxGenre2.getSelectedIndex() > 0);
		cbGenre3.setSelected(cbxGenre3.getSelectedIndex() > 0);
		cbGenre4.setSelected(cbxGenre4.getSelectedIndex() > 0);
		cbGenre5.setSelected(cbxGenre5.getSelectedIndex() > 0);
		cbGenre6.setSelected(cbxGenre6.getSelectedIndex() > 0);
		cbGenre7.setSelected(cbxGenre7.getSelectedIndex() > 0);
	}

	private void runSearchAsync(boolean parseAll) {
		btnParse.setEnabled(false);
		pbarSearch.setIndeterminate(true);

		mdlLsDBList.clear();
		lsDBList.setSelectedIndex(-1);

		ThreadGroup group = new ThreadGroup("THREADGROUP_SEARCH_ONLINE"); //$NON-NLS-1$

		for (Metadataparser p : Metadataparser.listParser()) {
			Thread t = new Thread(group, () -> runSearch(p, parseAll), "THREAD_SEARCH_ONLINE_" + p.getImplType().toString() + "_PA"); //$NON-NLS-1$ //$NON-NLS-2$
			t.start();
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (group.activeCount() > 0) {
						Thread.sleep(8);
					}
				} catch (InterruptedException e) {
					CCLog.addError(e);
				}
				
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							pbarSearch.setIndeterminate(false);
							btnParse.setEnabled(true);
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					CCLog.addError(e);
				}
			}
		}, "THREAD_SEARCH_ONLINE_JOIN").start(); //$NON-NLS-1$
	}
	
	private void runSearch(Metadataparser parser, boolean parseAll) {

		if (Str.isNullOrWhitespace(edSearchName.getText())) return;

		final List<Tuple<String, CCSingleOnlineReference>> links;
		if (parseAll) {
			links = parser.searchByText(edSearchName.getText(), OnlineSearchType.BOTH);
		} else if (typ == CCDBElementTyp.MOVIE) {
			links = parser.searchByText(edSearchName.getText(), OnlineSearchType.MOVIES);
		} else {
			links = parser.searchByText(edSearchName.getText(), OnlineSearchType.SERIES);
		}
		
		if (links == null) return;
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					int ordering = 0;
					for (Tuple<String, CCSingleOnlineReference> result : links) {
						mdlLsDBList.addElement(new ParseOnlineDialogElement(result.Item1, ordering++, result.Item2));
					}
					
					resortListModel(mdlLsDBList);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
			return;
		}
	}
	
	private <T extends Comparable<T>> void resortListModel(DefaultListModel<T> model) {
	    List<T> list = new ArrayList<>();
	    for (int i = 0; i < model.size(); i++) {
	        list.add(model.get(i));
	    }
	    
	    Collections.sort(list);
	    
	    model.removeAllElements();
	    for (T s : list) {
	    	model.addElement(s);
	    }
	}
	
	private void parseAndDisplayRef(final CCSingleOnlineReference ref) {
		final Metadataparser parser = ref.getMetadataParser();
		
		if (parser == null) return;
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					resetAll();
					pbarSearch.setIndeterminate(true);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
			return;
		}
		
		final OnlineMetadata md = parser.getMetadata(ref, true);
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					if (md.Title != null) edTitle.setText(md.Title);
					if (md.Year != null) spnYear.setValue(md.Year);
					if (md.OnlineScore != null) spnScore.setValue(md.OnlineScore);
					if (md.Length != null) spnLength.setValue(md.Length);
					if (md.FSK != null) cbxFSK.setSelectedIndex(md.FSK.asInt());
					if (md.Cover != null) { imgCover.setAndResizeCover(md.Cover); imgCoverBI = md.Cover; }
					if (md.FSKList !=null) cbFSKlsAll =md.FSKList;
					
					if (md.Genres != null) cbxGenre0.setSelectedIndex(md.Genres.getGenre(0).asInt());
					if (md.Genres != null) cbxGenre1.setSelectedIndex(md.Genres.getGenre(1).asInt());
					if (md.Genres != null) cbxGenre2.setSelectedIndex(md.Genres.getGenre(2).asInt());
					if (md.Genres != null) cbxGenre3.setSelectedIndex(md.Genres.getGenre(3).asInt());
					if (md.Genres != null) cbxGenre4.setSelectedIndex(md.Genres.getGenre(4).asInt());
					if (md.Genres != null) cbxGenre5.setSelectedIndex(md.Genres.getGenre(5).asInt());
					if (md.Genres != null) cbxGenre6.setSelectedIndex(md.Genres.getGenre(6).asInt());
					if (md.Genres != null) cbxGenre7.setSelectedIndex(md.Genres.getGenre(7).asInt());
					
					btnRef.setIcon(selectedReference.getIconButton());
					if (md.AltRef != null) ctrlAltRef.setValue(md.AltRef); else ctrlAltRef.setValue(CCSingleOnlineReference.EMPTY);
					
					btnOk.setEnabled(true);
					
					updateCheckBoxes();
					
					pbarSearch.setIndeterminate(false);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
			return;
		}	
	}

	private void updateMainPanel() {
		if (lsDBList.getSelectedIndex() < 0) {
			return;
		}
		
		selectedReference = lsDBList.getSelectedValue().Reference;
		
		new Thread(() -> parseAndDisplayRef(selectedReference), "THREAD_PARSE_MOVIE").start(); //$NON-NLS-1$
	}
	
	private void insertDataIntoFrame() {
		owner.setOnlineReference(CCOnlineReferenceList.create(selectedReference, ctrlAltRef.getValue()));
		
		if (cbTitle.isSelected()) {
			owner.setMovieName(edTitle.getText());
		}
		
		if (cbYear.isSelected()) {
			owner.setYear((int) spnYear.getValue());
		}
		
		if (cbScore.isSelected()) {
			owner.setScore((int) spnScore.getValue());
		}
		
		if (cbLength.isSelected()) {
			owner.setLength((int) spnLength.getValue());
		}
		
		if (cbFSK.isSelected()) {
			owner.setFSK(cbxFSK.getSelectedIndex());
		}
		
		if (cbCover.isSelected()) {
			owner.setCover(imgCoverBI);
		}
		
		if (cbGenre0.isSelected()) {
			owner.setGenre(0, cbxGenre0.getSelectedIndex());
		}
		
		if (cbGenre1.isSelected()) {
			owner.setGenre(1, cbxGenre1.getSelectedIndex());
		}
		
		if (cbGenre2.isSelected()) {
			owner.setGenre(2, cbxGenre2.getSelectedIndex());
		}
		
		if (cbGenre3.isSelected()) {
			owner.setGenre(3, cbxGenre3.getSelectedIndex());
		}
		
		if (cbGenre4.isSelected()) {
			owner.setGenre(4, cbxGenre4.getSelectedIndex());
		}
		
		if (cbGenre5.isSelected()) {
			owner.setGenre(5, cbxGenre5.getSelectedIndex());
		}
		
		if (cbGenre6.isSelected()) {
			owner.setGenre(6, cbxGenre6.getSelectedIndex());
		}
		
		if (cbGenre7.isSelected()) {
			owner.setGenre(7, cbxGenre7.getSelectedIndex());
		}
		
		owner.onFinishInserting();
	}
}
