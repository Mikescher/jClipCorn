package de.jClipCorn.gui.frames.autofindRefrenceFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
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
import de.jClipCorn.gui.guiComponents.cover.CoverLabel;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.helper.SwingUtils;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AutoFindReferenceFrame extends JCCFrame {
	private static final long serialVersionUID = 4658458278263596774L;

	private boolean isThreadRunning = false;

	private DefaultListModel<AutoFindRefElement> listModel;

	/**
	 * Create the frame.
	 */
	public AutoFindReferenceFrame(Component parent, CCMovieList ml) {
		super(ml);

		initComponents();
		postInit();
		initMap();

		setLocationRelativeTo(parent);
	}

	private void postInit() {
		listResults.setModel(listModel = new DefaultListModel<>());
		listResults.setCellRenderer(new AutoFindRefRenderer());
	}

	protected void onEdit() {
		if (isThreadRunning) return;

		if (listResults.getSelectedIndex() < 0) {
			return;
		}

		AutoFindRefElement value = listResults.getSelectedValue();

		if (value.local.isMovie()) {
			new EditMovieFrame(this, value.local.asMovie(), null).setVisible(true);
		} else {
			new EditSeriesFrame(this, value.local.asSeries(), null).setVisible(true);
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
			edYearLocal.setText((value.local.asMovie()).getYear().mapOrElse(String::valueOf, "")); //$NON-NLS-1$
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

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		pnlLeft = new JPanel();
		list = new JScrollPane();
		listResults = new JList<>();
		btnSearch = new JButton();
		pbProgress = new JProgressBar();
		lblCurrentElement = new JLabel();
		pnlRight = new JPanel();
		lblLocal = new JLabel();
		lblOnline = new JLabel();
		lblImdb = new JLabel();
		edTitleLocal = new JTextField();
		edTitleTmdb = new JTextField();
		edTitleIMDB = new JTextField();
		edYearLocal = new JTextField();
		edYearTmdb = new JTextField();
		edYearImDB = new JTextField();
		edRefLocal = new JTextField();
		edRefTmdb = new JTextField();
		edRefIMDB = new JTextField();
		cvrLocal = new CoverLabel(movielist, true);
		cvrTmdb = new CoverLabel(movielist, true);
		cvrImDB = new CoverLabel(movielist, true);
		btnEdit = new JButton();
		btnIgnore = new JButton();
		btnApplyTmdb = new JButton();
		btnApplyImdb = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("AutoFindReferencesFrame.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				onClose();
			}
		});
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $ugap, default:grow, $ugap",
			"$ugap, default:grow, $ugap"));

		//======== pnlLeft ========
		{
			pnlLeft.setLayout(new FormLayout(
				"default, $lcgap, default:grow",
				"default:grow, 2*($lgap, default)"));

			//======== list ========
			{
				list.setPreferredSize(new Dimension(300, 0));

				//---- listResults ----
				listResults.addListSelectionListener(e -> refreshRightPanel());
				list.setViewportView(listResults);
			}
			pnlLeft.add(list, CC.xywh(1, 1, 3, 1, CC.FILL, CC.FILL));

			//---- btnSearch ----
			btnSearch.setText(LocaleBundle.getString("AutoFindReferencesFrame.btnSearch"));
			btnSearch.addActionListener(e -> startSearch());
			pnlLeft.add(btnSearch, CC.xywh(1, 3, 1, 3, CC.DEFAULT, CC.FILL));
			pnlLeft.add(pbProgress, CC.xy(3, 3, CC.FILL, CC.DEFAULT));
			pnlLeft.add(lblCurrentElement, CC.xy(3, 5, CC.FILL, CC.DEFAULT));
		}
		contentPane.add(pnlLeft, CC.xy(2, 2, CC.FILL, CC.FILL));

		//======== pnlRight ========
		{
			pnlRight.setLayout(new FormLayout(
				"2*(default:grow, $ugap), default:grow",
				"5*(default, $lgap), default, default:grow, $lgap, default"));

			//---- lblLocal ----
			lblLocal.setText(LocaleBundle.getString("AutoFindReferencesFrame.lblLocal"));
			pnlRight.add(lblLocal, CC.xy(1, 1));

			//---- lblOnline ----
			lblOnline.setText(LocaleBundle.getString("AutoFindReferencesFrame.lblTMDB"));
			pnlRight.add(lblOnline, CC.xy(3, 1));

			//---- lblImdb ----
			lblImdb.setText(LocaleBundle.getString("AutoFindReferencesFrame.lblImDB"));
			pnlRight.add(lblImdb, CC.xy(5, 1));

			//---- edTitleLocal ----
			edTitleLocal.setEditable(false);
			edTitleLocal.setColumns(10);
			pnlRight.add(edTitleLocal, CC.xy(1, 3, CC.FILL, CC.DEFAULT));

			//---- edTitleTmdb ----
			edTitleTmdb.setEditable(false);
			edTitleTmdb.setColumns(10);
			pnlRight.add(edTitleTmdb, CC.xy(3, 3, CC.FILL, CC.DEFAULT));

			//---- edTitleIMDB ----
			edTitleIMDB.setEditable(false);
			edTitleIMDB.setColumns(10);
			pnlRight.add(edTitleIMDB, CC.xy(5, 3, CC.FILL, CC.DEFAULT));

			//---- edYearLocal ----
			edYearLocal.setEditable(false);
			edYearLocal.setColumns(10);
			pnlRight.add(edYearLocal, CC.xy(1, 5, CC.FILL, CC.DEFAULT));

			//---- edYearTmdb ----
			edYearTmdb.setEditable(false);
			edYearTmdb.setColumns(10);
			pnlRight.add(edYearTmdb, CC.xy(3, 5, CC.FILL, CC.DEFAULT));

			//---- edYearImDB ----
			edYearImDB.setEditable(false);
			edYearImDB.setColumns(10);
			pnlRight.add(edYearImDB, CC.xy(5, 5, CC.FILL, CC.DEFAULT));

			//---- edRefLocal ----
			edRefLocal.setEditable(false);
			edRefLocal.setColumns(10);
			pnlRight.add(edRefLocal, CC.xy(1, 7, CC.FILL, CC.DEFAULT));

			//---- edRefTmdb ----
			edRefTmdb.setEditable(false);
			edRefTmdb.setColumns(10);
			pnlRight.add(edRefTmdb, CC.xy(3, 7, CC.FILL, CC.DEFAULT));

			//---- edRefIMDB ----
			edRefIMDB.setEditable(false);
			edRefIMDB.setColumns(10);
			pnlRight.add(edRefIMDB, CC.xy(5, 7, CC.FILL, CC.DEFAULT));
			pnlRight.add(cvrLocal, CC.xy(1, 9, CC.LEFT, CC.TOP));
			pnlRight.add(cvrTmdb, CC.xy(3, 9, CC.LEFT, CC.TOP));
			pnlRight.add(cvrImDB, CC.xy(5, 9, CC.LEFT, CC.TOP));

			//---- btnEdit ----
			btnEdit.setText(LocaleBundle.getString("AutoFindReferencesFrame.btnEdit"));
			btnEdit.addActionListener(e -> onEdit());
			pnlRight.add(btnEdit, CC.xy(1, 11, CC.LEFT, CC.DEFAULT));

			//---- btnIgnore ----
			btnIgnore.setText(LocaleBundle.getString("AutoFindReferencesFrame.btnNext"));
			btnIgnore.addActionListener(e -> actionIgnore());
			pnlRight.add(btnIgnore, CC.xy(1, 14, CC.FILL, CC.DEFAULT));

			//---- btnApplyTmdb ----
			btnApplyTmdb.setText(LocaleBundle.getString("AutoFindReferencesFrame.btnApply"));
			btnApplyTmdb.addActionListener(e -> actionApply());
			pnlRight.add(btnApplyTmdb, CC.xy(3, 14, CC.FILL, CC.DEFAULT));

			//---- btnApplyImdb ----
			btnApplyImdb.setText(LocaleBundle.getString("AutoFindReferencesFrame.btnApplyImDB"));
			btnApplyImdb.addActionListener(e -> actionApplyImDB());
			pnlRight.add(btnApplyImdb, CC.xy(5, 14, CC.FILL, CC.DEFAULT));
		}
		contentPane.add(pnlRight, CC.xy(4, 2, CC.FILL, CC.FILL));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel pnlLeft;
	private JScrollPane list;
	private JList<AutoFindRefElement> listResults;
	private JButton btnSearch;
	private JProgressBar pbProgress;
	private JLabel lblCurrentElement;
	private JPanel pnlRight;
	private JLabel lblLocal;
	private JLabel lblOnline;
	private JLabel lblImdb;
	private JTextField edTitleLocal;
	private JTextField edTitleTmdb;
	private JTextField edTitleIMDB;
	private JTextField edYearLocal;
	private JTextField edYearTmdb;
	private JTextField edYearImDB;
	private JTextField edRefLocal;
	private JTextField edRefTmdb;
	private JTextField edRefIMDB;
	private CoverLabel cvrLocal;
	private CoverLabel cvrTmdb;
	private CoverLabel cvrImDB;
	private JButton btnEdit;
	private JButton btnIgnore;
	private JButton btnApplyTmdb;
	private JButton btnApplyImdb;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
