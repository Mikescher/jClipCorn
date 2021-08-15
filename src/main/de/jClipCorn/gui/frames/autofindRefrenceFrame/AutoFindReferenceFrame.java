package de.jClipCorn.gui.frames.autofindRefrenceFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.features.online.metadata.OnlineMetadata;
import de.jClipCorn.features.online.metadata.imdb.IMDBParserCommon;
import de.jClipCorn.features.online.metadata.tmdb.TMDBParser;
import de.jClipCorn.gui.frames.editMovieFrame.EditMovieFrame;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.helper.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AutoFindReferenceFrame extends JCCFrame {
	private static final long serialVersionUID = 4658458278263596774L;
	
	private boolean isThreadRunning = false;

	private JPanel contentPane;
	private JScrollPane list;
	private DefaultListModel<AutoFindRefElement> listModel;
	private JList<AutoFindRefElement> listResults;
	private JPanel pnlRight;
	private JButton btnIgnore;
	private JButton btnApplyTmdb;
	private JLabel lblLocal;
	private JLabel lblOnline;
	private JTextField edTitleLocal;
	private JTextField edTitleTmdb;
	private CoverLabel cvrLocal;
	private CoverLabel cvrTmdb;
	private JTextField edYearLocal;
	private JTextField edRefLocal;
	private JTextField edYearTmdb;
	private JTextField edRefTmdb;
	private JPanel pnlLeft;
	private JButton btnSearch;
	private JProgressBar pbProgress;
	private JLabel lblCurrentElement;
	private JTextField edTitleIMDB;
	private JLabel lblImdb;
	private JTextField edRefIMDB;
	private JButton btnApplyImdb;
	private CoverLabel cvrImDB;
	private JTextField edYearImDB;
	private JButton btnEdit;

	/**
	 * Create the frame.
	 */
	public AutoFindReferenceFrame(Component parent, CCMovieList ml) {
		super(ml);
		
		initGUI();
		initMap();

		setLocationRelativeTo(parent);
	}

	private void initGUI() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				onClose();
			}
		});
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle(LocaleBundle.getString("AutoFindReferencesFrame.title")); //$NON-NLS-1$
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 870, 431);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		pnlLeft = new JPanel();
		pnlLeft.setBounds(5, 5, 291, 393);
		contentPane.add(pnlLeft);
		pnlLeft.setLayout(null);
		
		list = new JScrollPane();
		list.setBounds(12, 12, 267, 315);
		pnlLeft.add(list);
		list.setPreferredSize(new Dimension(300, 0));
		
		listResults = new JList<>();
		listResults.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				refreshRightPanel();
			}
		});
		listResults.setModel(listModel = new DefaultListModel<>());
		listResults.setCellRenderer(new AutoFindRefRenderer());
		list.setViewportView(listResults);
		
		btnSearch = new JButton(LocaleBundle.getString("AutoFindReferencesFrame.btnSearch")); //$NON-NLS-1$
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startSearch();
			}
		});
		btnSearch.setBounds(12, 339, 98, 42);
		pnlLeft.add(btnSearch);
		
		pbProgress = new JProgressBar();
		pbProgress.setBounds(122, 339, 157, 16);
		pnlLeft.add(pbProgress);
		
		lblCurrentElement = new JLabel();
		lblCurrentElement.setBounds(122, 365, 157, 16);
		pnlLeft.add(lblCurrentElement);
		
		pnlRight = new JPanel();
		pnlRight.setBounds(308, 5, 544, 393);
		contentPane.add(pnlRight);
		pnlRight.setLayout(null);
		
		btnIgnore = new JButton(LocaleBundle.getString("AutoFindReferencesFrame.btnNext")); //$NON-NLS-1$
		btnIgnore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionIgnore();
			}
		});
		btnIgnore.setBounds(12, 355, 153, 26);
		pnlRight.add(btnIgnore);
		
		btnApplyTmdb = new JButton(LocaleBundle.getString("AutoFindReferencesFrame.btnApply")); //$NON-NLS-1$
		btnApplyTmdb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionApply();
			}
		});
		btnApplyTmdb.setBounds(194, 355, 153, 26);
		pnlRight.add(btnApplyTmdb);
		
		lblLocal = new JLabel();
		lblLocal.setText(LocaleBundle.getString("AutoFindReferencesFrame.lblLocal")); //$NON-NLS-1$
		lblLocal.setBounds(12, 12, 153, 16);
		pnlRight.add(lblLocal);
		
		lblOnline = new JLabel();
		lblOnline.setText(LocaleBundle.getString("AutoFindReferencesFrame.lblTMDB")); //$NON-NLS-1$
		lblOnline.setBounds(194, 12, 153, 16);
		pnlRight.add(lblOnline);
		
		edTitleLocal = new JTextField();
		edTitleLocal.setEditable(false);
		edTitleLocal.setBounds(12, 54, 153, 20);
		pnlRight.add(edTitleLocal);
		edTitleLocal.setColumns(10);
		
		edTitleTmdb = new JTextField();
		edTitleTmdb.setEditable(false);
		edTitleTmdb.setColumns(10);
		edTitleTmdb.setBounds(194, 54, 153, 20);
		pnlRight.add(edTitleTmdb);
		
		cvrLocal = new CoverLabel(movielist, true);
		cvrLocal.setBounds(12, 150, 91, 127);
		pnlRight.add(cvrLocal);
		
		cvrTmdb = new CoverLabel(movielist, true);
		cvrTmdb.setBounds(194, 150, 91, 127);
		pnlRight.add(cvrTmdb);
		
		edYearLocal = new JTextField();
		edYearLocal.setEditable(false);
		edYearLocal.setColumns(10);
		edYearLocal.setBounds(12, 86, 153, 20);
		pnlRight.add(edYearLocal);
		
		edRefLocal = new JTextField();
		edRefLocal.setEditable(false);
		edRefLocal.setColumns(10);
		edRefLocal.setBounds(12, 118, 153, 20);
		pnlRight.add(edRefLocal);
		
		edYearTmdb = new JTextField();
		edYearTmdb.setEditable(false);
		edYearTmdb.setColumns(10);
		edYearTmdb.setBounds(194, 86, 153, 20);
		pnlRight.add(edYearTmdb);
		
		edRefTmdb = new JTextField();
		edRefTmdb.setEditable(false);
		edRefTmdb.setColumns(10);
		edRefTmdb.setBounds(194, 118, 153, 20);
		pnlRight.add(edRefTmdb);
		
		edTitleIMDB = new JTextField();
		edTitleIMDB.setEditable(false);
		edTitleIMDB.setColumns(10);
		edTitleIMDB.setBounds(379, 54, 153, 20);
		pnlRight.add(edTitleIMDB);
		
		lblImdb = new JLabel();
		lblImdb.setText(LocaleBundle.getString("AutoFindReferencesFrame.lblImDB")); //$NON-NLS-1$
		lblImdb.setBounds(379, 12, 153, 16);
		pnlRight.add(lblImdb);
		
		edRefIMDB = new JTextField();
		edRefIMDB.setEditable(false);
		edRefIMDB.setColumns(10);
		edRefIMDB.setBounds(379, 118, 153, 20);
		pnlRight.add(edRefIMDB);
		
		btnApplyImdb = new JButton(LocaleBundle.getString("AutoFindReferencesFrame.btnApplyImDB")); //$NON-NLS-1$
		btnApplyImdb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionApplyImDB();
			}
		});
		btnApplyImdb.setBounds(379, 355, 153, 26);
		pnlRight.add(btnApplyImdb);
		
		cvrImDB = new CoverLabel(movielist, true);
		cvrImDB.setBounds(379, 150, 91, 127);
		pnlRight.add(cvrImDB);
		
		edYearImDB = new JTextField();
		edYearImDB.setEditable(false);
		edYearImDB.setColumns(10);
		edYearImDB.setBounds(379, 86, 153, 20);
		pnlRight.add(edYearImDB);
		
		btnEdit = new JButton(LocaleBundle.getString("AutoFindReferencesFrame.btnEdit")); //$NON-NLS-1$
		btnEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onEdit();
			}
		});
		btnEdit.setBounds(12, 289, 91, 26);
		pnlRight.add(btnEdit);
	}

	protected void onEdit() {
		if (isThreadRunning) return;
		
		if (listResults.getSelectedIndex() < 0) {
			return;
		}
		
		AutoFindRefElement value = listResults.getSelectedValue();
		
		if (value.local.isMovie()) {
			new EditMovieFrame(this, (CCMovie) value.local, null).setVisible(true);
		} else {
			new EditSeriesFrame(this, (CCSeries) value.local, null).setVisible(true);
		}
	}

	private void onClose() {
		isThreadRunning = false;
	}

	private void refreshRightPanel() {
		edTitleLocal.setText(""); //$NON-NLS-1$
		edTitleTmdb.setText(""); //$NON-NLS-1$
		edTitleIMDB.setText(""); //$NON-NLS-1$
		
		edYearLocal.setText(""); //$NON-NLS-1$
		edYearTmdb.setText(""); //$NON-NLS-1$
		edYearImDB.setText(""); //$NON-NLS-1$
		
		edRefLocal.setText(""); //$NON-NLS-1$
		edRefTmdb.setText(""); //$NON-NLS-1$
		edRefIMDB.setText(""); //$NON-NLS-1$
		
		cvrLocal.clearCover();
		cvrImDB.clearCover();
		cvrTmdb.clearCover();
		
		btnIgnore.setEnabled(false);
		btnApplyTmdb.setEnabled(false);
		btnApplyImdb.setEnabled(false);

		btnApplyTmdb.setBackground(null);
		btnApplyImdb.setBackground(null);
		
		if (listResults.getSelectedIndex() < 0) {
			return;
		}
		
		AutoFindRefElement value = listResults.getSelectedValue();

		edTitleLocal.setText(value.local.getTitle());
		
		if (value.tmdbMeta != null) edTitleTmdb.setText(value.tmdbMeta.Title);

		if (value.local.isMovie()){
			edYearLocal.setText(Integer.toString(((CCMovie)value.local).getYear()));
			if (value.tmdbMeta != null) edYearTmdb.setText(Integer.toString(value.tmdbMeta.Year));
		} else {
			edYearLocal.setText(""); //$NON-NLS-1$
			edYearTmdb.setText(""); //$NON-NLS-1$
		}
		
		if (value.imdbMeta != null && value.imdbMeta.Year != null) edYearImDB.setText(Integer.toString(value.imdbMeta.Year));
		if (value.imdbMeta != null && value.imdbMeta.Cover != null) cvrImDB.setAndResizeCover(value.imdbMeta.Cover);
		if (value.imdbMeta != null && value.imdbMeta.Title != null) edTitleIMDB.setText(value.imdbMeta.Title);
		if (value.imdbMeta != null && value.imdbMeta.Source != null) edRefIMDB.setText(value.imdbMeta.Source.toSerializationString());
				
		edRefLocal.setText(value.local.getOnlineReference().toSerializationString());
		edRefTmdb.setText(value.tmdbMeta.Source.toSerializationString());

		cvrLocal.setAndResizeCover(value.local.getCover());
		if (value.tmdbMeta.Cover != null) cvrTmdb.setAndResizeCover(value.tmdbMeta.Cover);
		
		btnApplyTmdb.setEnabled(value.tmdbMeta != null);
		btnApplyImdb.setEnabled(value.imdbMeta != null && value.imdbMeta.Source != null && value.imdbMeta.Source.isSet());
		btnIgnore.setEnabled(true);
		
		if (! edYearLocal.getText().equals(edYearImDB.getText()) && btnApplyImdb.isEnabled()) btnApplyImdb.setBackground(Color.RED);
		if (! edYearLocal.getText().equals(edYearTmdb.getText()) && btnApplyTmdb.isEnabled()) btnApplyTmdb.setBackground(Color.RED);
	}

	private void initMap() {
		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kfm.addKeyEventDispatcher(new KeyEventDispatcher() {

			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (! AutoFindReferenceFrame.this.isActive()) return false;
				
				KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
				
				if (!keyStroke.isOnKeyRelease() && keyStroke.getKeyCode() == KeyEvent.VK_P && keyStroke.getModifiers() == 0) {
					actionIgnore();
					return true;
				} else if (!keyStroke.isOnKeyRelease() && keyStroke.getKeyCode() == KeyEvent.VK_Q && keyStroke.getModifiers() == 0) {
					actionApply();
					return true;
				} else if (!keyStroke.isOnKeyRelease() && keyStroke.getKeyCode() == KeyEvent.VK_I && keyStroke.getModifiers() == 0) {
					actionApplyImDB();
					return true;
				}

				return false;
			}
		});
	}

	private void startSearch() {
		isThreadRunning = true;

		pnlLeft.setEnabled(false);
		pnlRight.setEnabled(false);
		btnApplyTmdb.setEnabled(false);
		btnIgnore.setEnabled(false);
		btnSearch.setEnabled(false);
		btnApplyImdb.setEnabled(false);

		pbProgress.setValue(0);
		
		List<CCDatabaseElement> elements = new ArrayList<>();
		for (CCDatabaseElement el : movielist.iteratorElements()) {
			if (el.getOnlineReference().Main.isUnset())
				elements.add(el);
			
			if (elements.size() >= 250) // 250 max, prevent OutOfMemory and other fun stuff
				break;
		}
		
		pbProgress.setMaximum(elements.size());
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					List<AutoFindRefElement> result = AutoFindReferenceFrame.this.run(elements);
					if (result != null) {
						SwingUtils.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								listModel.clear();
								for (AutoFindRefElement afre : result) {
									listModel.addElement(afre);
								}
								listResults.setSelectedIndex(0);
							}
						});
					}
				} catch (Exception e) {
					CCLog.addError(e);
				} finally {
					try {
						SwingUtils.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								isThreadRunning = false;

								pnlLeft.setEnabled(true);
								pnlRight.setEnabled(true);
								btnApplyTmdb.setEnabled(true);
								btnApplyImdb.setEnabled(true);
								btnIgnore.setEnabled(true);
								btnSearch.setEnabled(true);
								
								pbProgress.setValue(0);
								lblCurrentElement.setText(""); //$NON-NLS-1$
							}
						});
					} catch (InvocationTargetException | InterruptedException e) {
						CCLog.addError(e);
					}
				}
			}
		}, "THREAD_AUTOFINDREF").start(); //$NON-NLS-1$
	}
	
	private List<AutoFindRefElement> run(List<CCDatabaseElement> source) {
		List<AutoFindRefElement> result = new ArrayList<>();

		TMDBParser tmdbParser = new TMDBParser(movielist);
		IMDBParserCommon imdbParser = IMDBParserCommon.GetConfiguredParser(movielist);
		
		int count = 0;
		for (CCDatabaseElement element : source) {
			if (! isThreadRunning) return null;
			
			try {
				final int fcount = count++;
				SwingUtils.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						lblCurrentElement.setText(element.getTitle());
						pbProgress.setValue(fcount);
					}
				});
			} catch (InvocationTargetException | InterruptedException e1) {
				CCLog.addError(e1);
			}
			
			try {
				CCSingleOnlineReference tmdbReference;
				OnlineSearchType searchtype = element.isMovie() ? OnlineSearchType.MOVIES : OnlineSearchType.SERIES;
				
				if (element.isMovie())
					tmdbReference = tmdbParser.findMovieDirect(element.getTitle());
				else
					tmdbReference = tmdbParser.findSeriesDirect(element.getTitle());
				
				OnlineMetadata imdbMeta = null;
				
				if (tmdbReference.isUnset()) {
					CCSingleOnlineReference imdbReference = imdbParser.getFirstResultReference(element.getTitle(), searchtype);
					if (imdbReference != null && imdbReference.type == CCOnlineRefType.IMDB)
						imdbMeta = imdbParser.getMetadata(imdbReference, true);
					
					result.add(new AutoFindRefElement(element, null, imdbMeta));
				} else {
					OnlineMetadata tmdbMeta = tmdbParser.getMetadata(tmdbReference, true);
					
					if (tmdbMeta != null && tmdbMeta.AltRef != null && tmdbMeta.AltRef.isSet() && tmdbMeta.AltRef.type == CCOnlineRefType.IMDB) {
						imdbMeta = imdbParser.getMetadata(tmdbMeta.AltRef, true);
					} else {
						CCSingleOnlineReference imdbReference = imdbParser.getFirstResultReference(element.getTitle(), searchtype);
						if (imdbReference != null && imdbReference.type == CCOnlineRefType.IMDB)
							imdbMeta = imdbParser.getMetadata(imdbReference, true);
					}
					
					result.add(new AutoFindRefElement(element, tmdbMeta, imdbMeta));
				}
				
			} catch (Exception e) {
				CCLog.addError(e);
			}
		}
		
		return result;
	}

	private void actionIgnore() {
		if (isThreadRunning) return;
		
		if (listResults.getSelectedIndex() < 0) {
			return;
		}
		
		if (listResults.getSelectedIndex() + 1 >= listModel.size()) {
			listResults.setSelectedIndex(-1);
			return;
		}

		listResults.setSelectedIndex(listResults.getSelectedIndex() + 1);
	}

	private void actionApply() {
		if (isThreadRunning) return;
		
		if (listResults.getSelectedIndex() < 0) {
			return;
		}

		AutoFindRefElement value = listResults.getSelectedValue();
		
		if (value.tmdbMeta == null) {
			return;
		}
		
		value.local.onlineReference().set(CCOnlineReferenceList.create(value.tmdbMeta.Source, value.tmdbMeta.AltRef));
		
		if (listResults.getSelectedIndex() + 1 >= listModel.size()) {
			listResults.setSelectedIndex(-1);
			return;
		}

		listResults.setSelectedIndex(listResults.getSelectedIndex() + 1);
	}
	
	private void actionApplyImDB() {
		if (isThreadRunning) return;
		
		if (listResults.getSelectedIndex() < 0) {
			return;
		}

		AutoFindRefElement value = listResults.getSelectedValue();
		
		if (value.imdbMeta == null || value.imdbMeta.Source.isUnset()) {
			return;
		}
		
		value.local.onlineReference().set(CCOnlineReferenceList.create(value.imdbMeta.Source, value.imdbMeta.AltRef));
		
		if (listResults.getSelectedIndex() + 1 >= listModel.size()) {
			listResults.setSelectedIndex(-1);
			return;
		}

		listResults.setSelectedIndex(listResults.getSelectedIndex() + 1);
	}
}
