package de.jClipCorn.gui.frames.autofindRefrenceFrame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.editMovieFrame.EditMovieFrame;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.helper.HTTPUtilities;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.parser.onlineparser.ImDBParser;
import de.jClipCorn.util.parser.onlineparser.ImDBParser.IMDBLimitedResult;
import de.jClipCorn.util.parser.onlineparser.TMDBParser;
import de.jClipCorn.util.parser.onlineparser.TMDBParser.TMDBFullResult;

public class AutoFindReferenceFrame extends JFrame {
	private static final long serialVersionUID = 4658458278263596774L;
	
	private boolean isThreadRunning = false;
	private final CCMovieList database;
	
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
	public AutoFindReferenceFrame(Component parent, CCMovieList db) {
		super();
		
		database = db;
		
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
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
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
		
		cvrLocal = new CoverLabel(true);
		cvrLocal.setBounds(12, 150, 91, 127);
		pnlRight.add(cvrLocal);
		
		cvrTmdb = new CoverLabel(true);
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
		
		cvrImDB = new CoverLabel(true);
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
		
		cvrLocal.setIcon(null);
		cvrImDB.setIcon(null);
		cvrTmdb.setIcon(null);
		
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
		
		if (value.imdbMeta != null && value.imdbMeta.Year > 0) edYearImDB.setText(Integer.toString(value.imdbMeta.Year));
		if (value.imdbMeta != null && value.imdbMeta.Cover != null) cvrImDB.setIcon(new ImageIcon(ImageUtilities.resizeHalfCoverImage(value.imdbMeta.Cover)));
		if (value.imdbMeta != null && value.imdbMeta.Title != null) edTitleIMDB.setText(value.imdbMeta.Title);
		if (value.imdbMeta != null && value.imdbMeta.Reference != null) edRefIMDB.setText(value.imdbMeta.Reference.toSerializationString());
				
		edRefLocal.setText(value.local.getOnlineReference().toSerializationString());
		edRefTmdb.setText(value.tmdbRef.toSerializationString());

		cvrLocal.setIcon(new ImageIcon(value.local.getHalfsizeCover()));
		if (value.tmdbCover != null) cvrTmdb.setIcon(new ImageIcon(ImageUtilities.resizeHalfCoverImage(value.tmdbCover)));
		
		btnApplyTmdb.setEnabled(value.tmdbRef.isSet());
		btnApplyImdb.setEnabled(value.imdbMeta != null && value.imdbMeta.Reference != null && value.imdbMeta.Reference.isSet());
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
		for (Iterator<CCDatabaseElement> it = database.iterator(); it.hasNext();) {
			CCDatabaseElement el = it.next();
			
			if (el.getOnlineReference().isUnset())
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
						SwingUtilities.invokeAndWait(new Runnable() {
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
						SwingUtilities.invokeAndWait(new Runnable() {
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
		
		int count = 0;
		for (CCDatabaseElement element : source) {
			if (! isThreadRunning) return null;
			
			try {
				final int fcount = count++;
				SwingUtilities.invokeAndWait(new Runnable() {
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
				CCOnlineReference ref;
				
				if (element.isMovie())
					ref = TMDBParser.findMovieDirect(element.getTitle());
				else
					ref = TMDBParser.findSeriesDirect(element.getTitle());
				
				IMDBLimitedResult imdbMeta = null;
				
				if (ref.isUnset()) {
					CCOnlineReference iref = ImDBParser.getFirstResultReference(element.getTitle(), !element.isMovie());
					if (iref != null && iref.type == CCOnlineRefType.IMDB)
						imdbMeta = ImDBParser.getMetadata(iref);
					
					result.add(new AutoFindRefElement(element, CCOnlineReference.createNone(), null, null, imdbMeta));
				} else {
					TMDBFullResult meta = TMDBParser.getMetadata(ref.id);
					
					BufferedImage img = null;
					if (meta != null && ! meta.CoverPath.isEmpty())
						img = HTTPUtilities.getImage(meta.CoverPath);
					
					if (meta != null && meta.ImdbRef != null && meta.ImdbRef.isSet()) {
						imdbMeta = ImDBParser.getMetadata(meta.ImdbRef);
					} else {
						CCOnlineReference iref = ImDBParser.getFirstResultReference(element.getTitle(), !element.isMovie());
						if (iref != null && iref.type == CCOnlineRefType.IMDB)
							imdbMeta = ImDBParser.getMetadata(iref);
					}
					
					result.add(new AutoFindRefElement(element, ref, meta, img, imdbMeta));
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
		
		if (value.tmdbRef.isUnset()) {
			return;
		}
		
		value.local.setOnlineReference(value.tmdbRef);
		
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
		
		if (value.imdbMeta == null || value.imdbMeta.Reference.isUnset()) {
			return;
		}
		
		value.local.setOnlineReference(value.imdbMeta.Reference);
		
		if (listResults.getSelectedIndex() + 1 >= listModel.size()) {
			listResults.setSelectedIndex(-1);
			return;
		}

		listResults.setSelectedIndex(listResults.getSelectedIndex() + 1);
	}
}
