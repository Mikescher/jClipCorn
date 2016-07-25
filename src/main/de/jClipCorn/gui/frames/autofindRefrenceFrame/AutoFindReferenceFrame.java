package de.jClipCorn.gui.frames.autofindRefrenceFrame;

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
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
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
	private JButton btnApply;
	private JLabel lblLocal;
	private JLabel lblOnline;
	private JTextField edTitleLocal;
	private JTextField edTitleOnline;
	private CoverLabel cvrLocal;
	private CoverLabel cvrOnline;
	private JTextField edYearLocal;
	private JTextField edRefLocal;
	private JTextField edYearOnline;
	private JTextField edRefOnline;
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
		
		btnApply = new JButton(LocaleBundle.getString("AutoFindReferencesFrame.btnApply")); //$NON-NLS-1$
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionApply();
			}
		});
		btnApply.setBounds(194, 355, 153, 26);
		pnlRight.add(btnApply);
		
		lblLocal = new JLabel();
		lblLocal.setText(LocaleBundle.getString("AutoFindReferencesFrame.lblLocal")); //$NON-NLS-1$
		lblLocal.setBounds(12, 12, 153, 16);
		pnlRight.add(lblLocal);
		
		lblOnline = new JLabel();
		lblOnline.setText(LocaleBundle.getString("AutoFindReferencesFrame.lblOnline")); //$NON-NLS-1$
		lblOnline.setBounds(194, 12, 153, 16);
		pnlRight.add(lblOnline);
		
		edTitleLocal = new JTextField();
		edTitleLocal.setEditable(false);
		edTitleLocal.setBounds(12, 54, 153, 20);
		pnlRight.add(edTitleLocal);
		edTitleLocal.setColumns(10);
		
		edTitleOnline = new JTextField();
		edTitleOnline.setEditable(false);
		edTitleOnline.setColumns(10);
		edTitleOnline.setBounds(194, 54, 153, 20);
		pnlRight.add(edTitleOnline);
		
		cvrLocal = new CoverLabel(true);
		cvrLocal.setBounds(12, 150, 91, 127);
		pnlRight.add(cvrLocal);
		
		cvrOnline = new CoverLabel(true);
		cvrOnline.setBounds(194, 150, 91, 127);
		pnlRight.add(cvrOnline);
		
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
		
		edYearOnline = new JTextField();
		edYearOnline.setEditable(false);
		edYearOnline.setColumns(10);
		edYearOnline.setBounds(194, 86, 153, 20);
		pnlRight.add(edYearOnline);
		
		edRefOnline = new JTextField();
		edRefOnline.setEditable(false);
		edRefOnline.setColumns(10);
		edRefOnline.setBounds(194, 118, 153, 20);
		pnlRight.add(edRefOnline);
		
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
	}

	private void onClose() {
		isThreadRunning = false;
	}

	private void refreshRightPanel() {
		edTitleLocal.setText(""); //$NON-NLS-1$
		edTitleOnline.setText(""); //$NON-NLS-1$
		edTitleIMDB.setText(""); //$NON-NLS-1$
		
		edYearLocal.setText(""); //$NON-NLS-1$
		edYearOnline.setText(""); //$NON-NLS-1$
		edYearImDB.setText(""); //$NON-NLS-1$
		
		edRefLocal.setText(""); //$NON-NLS-1$
		edRefOnline.setText(""); //$NON-NLS-1$
		edRefIMDB.setText(""); //$NON-NLS-1$
		
		cvrLocal.setIcon(null);
		cvrOnline.setIcon(null);
		cvrImDB.setIcon(null);
		
		btnApply.setEnabled(false);
		btnIgnore.setEnabled(false);
		
		if (listResults.getSelectedIndex() < 0) {
			return;
		}
		
		AutoFindRefElement value = listResults.getSelectedValue();

		edTitleLocal.setText(value.local.getTitle());
		
		if (value.tmdbMeta != null) edTitleOnline.setText(value.tmdbMeta.Title);

		if (value.local.isMovie()){
			edYearLocal.setText(Integer.toString(((CCMovie)value.local).getYear()));
			if (value.tmdbMeta != null) edYearOnline.setText(Integer.toString(value.tmdbMeta.Year));
		} else {
			edYearLocal.setText(""); //$NON-NLS-1$
			edYearOnline.setText(""); //$NON-NLS-1$
		}
		
		if (value.imdbMeta != null && value.imdbMeta.Year > 0) edYearImDB.setText(Integer.toString(value.imdbMeta.Year));
		if (value.imdbMeta != null) cvrImDB.setIcon(new ImageIcon(ImageUtilities.resizeHalfCoverImage(value.imdbMeta.Cover)));
		if (value.imdbMeta != null) edTitleIMDB.setText(value.imdbMeta.Title);
		if (value.imdbMeta != null) edRefIMDB.setText(value.imdbMeta.Reference.toSerializationString());
				
		edRefLocal.setText(value.local.getOnlineReference().toSerializationString());
		edRefOnline.setText(value.tmdbRef.toSerializationString());

		cvrLocal.setIcon(new ImageIcon(value.local.getHalfsizeCover()));
		if (value.tmdbCover != null) cvrOnline.setIcon(new ImageIcon(ImageUtilities.resizeHalfCoverImage(value.tmdbCover)));
		
		btnApply.setEnabled(value.tmdbRef.isSet());
		btnIgnore.setEnabled(true);
	}

	private void initMap() {
		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kfm.addKeyEventDispatcher(new KeyEventDispatcher() {

			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
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
		btnApply.setEnabled(false);
		btnIgnore.setEnabled(false);
		btnSearch.setEnabled(false);
		btnApplyImdb.setEnabled(false);

		pbProgress.setValue(0);
		
		List<CCDatabaseElement> elements = new ArrayList<>();
		for (Iterator<CCDatabaseElement> it = database.iterator(); it.hasNext();) {
			CCDatabaseElement el = it.next();
			
			if (el.getOnlineReference().isUnset())
				elements.add(el);
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
								btnApply.setEnabled(true);
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
		}, "THREAD_IMGPARSER_IMDB_1").start(); //$NON-NLS-1$
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
