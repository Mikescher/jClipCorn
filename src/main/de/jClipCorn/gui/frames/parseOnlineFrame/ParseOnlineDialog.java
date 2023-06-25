package de.jClipCorn.gui.frames.parseOnlineFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.features.online.metadata.Metadataparser;
import de.jClipCorn.features.online.metadata.OnlineMetadata;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.gui.frames.allRatingsFrame.AllRatingsDialog;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.guiComponents.ReadableSpinner;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.guiComponents.cover.CoverLabelFullsize;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCReadableEnumComboBox;
import de.jClipCorn.gui.guiComponents.jYearSpinner.*;
import de.jClipCorn.gui.guiComponents.onlinescore.OnlineScoreControl;
import de.jClipCorn.gui.guiComponents.referenceChooser.JSingleReferenceChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.ExtendedFocusTraversalOnArray;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.http.HTTPUtilities;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ParseOnlineDialog extends JCCDialog
{
	private DefaultListModel<ParseOnlineDialogElement> mdlLsDBList;
	private BufferedImage imgCoverBI = null;
	private Map<String, Integer> cbFSKlsAll = null;

	private final ParseResultHandler owner;
	private final CCDBElementTyp typ;

	private CCSingleOnlineReference selectedReference = CCSingleOnlineReference.EMPTY;

	public ParseOnlineDialog(Component owner, CCMovieList ml, ParseResultHandler handler, CCDBElementTyp typ)
	{
		super(ml);

		this.owner = handler;
		this.typ = typ;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		lsDBList.setCellRenderer(new ParseOnlineDialogRenderer());
		lsDBList.setModel(mdlLsDBList = new DefaultListModel<>());
		edSearchName.setText(owner.getFullTitle());

		setFocusTraversalPolicy(new ExtendedFocusTraversalOnArray(new Component[]{btnParse, btnExtendedParse, lsDBList, cbTitle, cbYear, cbScore, cbLength, cbFSK, cbCover, cbGenre0, cbGenre1, cbGenre2, cbGenre3, cbGenre4, cbGenre5, cbGenre6, cbGenre7, btnRef, btnFSKAll, btnApply}));


		resetAll();
	}

	private void showAllRatingsDialog() {
		if (lsDBList.getSelectedIndex() >= 0 && cbFSKlsAll != null) {
			(new AllRatingsDialog(cbFSKlsAll, this, movielist)).setVisible(true);
		}
	}

	private void openRefInBrowser() {
		if (lsDBList.getSelectedIndex() >= 0) HTTPUtilities.openInBrowser(lsDBList.getSelectedValue().Reference.getURL(ccprops()));
	}

	private void onRefSelected(ListSelectionEvent e) {
		if (! e.getValueIsAdjusting()) updateMainPanel();
	}

	private void onSearchKeyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) runSearchAsync(false);
	}

	private void startParse() {
		runSearchAsync(false);
	}

	private void startExtendedParse() {
		runSearchAsync(true);
	}

	private void onApply() {
		applyData();
		dispose();
	}

	private void resetAll() {
		resetFields();
		resetCheckboxes();

		btnApply.setEnabled(false);

		imgCoverBI = null;
		cbFSKlsAll = null;
	}

	private void resetFields() {
		edTitle.setText(Str.Empty);
		spnLength.setValue(0);
		ctrlScore.setValue(CCOnlineScore.EMPTY);
		spnYear.setValue(0);
		cbxFSK.clearSelectedEnum();
		imgCover.clearCover();

		cbxGenre0.clearSelectedEnum();
		cbxGenre1.clearSelectedEnum();
		cbxGenre2.clearSelectedEnum();
		cbxGenre3.clearSelectedEnum();
		cbxGenre4.clearSelectedEnum();
		cbxGenre5.clearSelectedEnum();
		cbxGenre6.clearSelectedEnum();
		cbxGenre7.clearSelectedEnum();

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
		cbFSK.setSelected(cbxFSK.hasSelection());
		cbLength.setSelected((int)spnLength.getValue() > 0);
		cbScore.setSelected(!ctrlScore.getValue().isEmpty());
		cbTitle.setSelected(edTitle.getText().equalsIgnoreCase(owner.getTitleForParser()));
		cbYear.setSelected((int)spnYear.getValue() > 0);

		cbGenre0.setSelected(cbxGenre0.hasSelection());
		cbGenre1.setSelected(cbxGenre1.hasSelection());
		cbGenre2.setSelected(cbxGenre2.hasSelection());
		cbGenre3.setSelected(cbxGenre3.hasSelection());
		cbGenre4.setSelected(cbxGenre4.hasSelection());
		cbGenre5.setSelected(cbxGenre5.hasSelection());
		cbGenre6.setSelected(cbxGenre6.hasSelection());
		cbGenre7.setSelected(cbxGenre7.hasSelection());
	}

	private void runSearchAsync(boolean parseAll) {
		btnParse.setEnabled(false);
		pbarSearch.setIndeterminate(true);

		mdlLsDBList.clear();
		lsDBList.setSelectedIndex(-1);

		ThreadGroup group = new ThreadGroup("THREADGROUP_SEARCH_ONLINE"); //$NON-NLS-1$

		for (Metadataparser p : Metadataparser.listParser(movielist)) {
			Thread t = new Thread(group, () -> runSearch(p, parseAll), "THREAD_SEARCH_ONLINE_" + p.getImplType().toString() + "_PA"); //$NON-NLS-1$ //$NON-NLS-2$
			t.start();
		}

		new Thread(() ->
		{
			try {
				while (group.activeCount() > 0) {
					Thread.sleep(8);
				}
			} catch (InterruptedException e) {
				CCLog.addError(e);
			}

			try {
				SwingUtils.invokeAndWait(() ->
				{
					pbarSearch.setIndeterminate(false);
					btnParse.setEnabled(true);
				});
			} catch (InvocationTargetException | InterruptedException e) {
				CCLog.addError(e);
			}
		}, "THREAD_SEARCH_ONLINE_JOIN").start(); //$NON-NLS-1$
	}

	private void runSearch(Metadataparser parser, boolean parseAll) {

		if (Str.isNullOrWhitespace(edSearchName.getText())) return;

		final java.util.List<Tuple<String, CCSingleOnlineReference>> links;
		if (parseAll) {
			links = parser.searchByText(edSearchName.getText(), OnlineSearchType.BOTH);
		} else if (typ == CCDBElementTyp.MOVIE) {
			links = parser.searchByText(edSearchName.getText(), OnlineSearchType.MOVIES);
		} else {
			links = parser.searchByText(edSearchName.getText(), OnlineSearchType.SERIES);
		}

		if (links == null) return;

		try {
			SwingUtils.invokeAndWait(() ->
			{
				int ordering = 0;
				for (Tuple<String, CCSingleOnlineReference> result : links) {
					mdlLsDBList.addElement(new ParseOnlineDialogElement(result.Item1, ordering++, result.Item2));
				}

				resortListModel(mdlLsDBList);
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
		final Metadataparser parser = ref.getMetadataParser(movielist);

		if (parser == null) return;

		try {
			SwingUtils.invokeAndWait(() ->
			{
				resetAll();
				pbarSearch.setIndeterminate(true);
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
			return;
		}

		final OnlineMetadata md = parser.getMetadata(ref, true);

		try {
			SwingUtils.invokeAndWait(() ->
			{
				if (md.Title != null) edTitle.setText(md.Title);
				if (md.Year != null) spnYear.setValue(md.Year);
				if (md.OnlineScore != null) ctrlScore.setValue(md.OnlineScore);
				if (md.Length != null) spnLength.setValue(md.Length);
				if (md.FSK != null) cbxFSK.setSelectedEnum(md.FSK);
				if (md.Cover != null) { imgCover.setAndResizeCover(md.Cover); imgCoverBI = md.Cover; }
				if (md.FSKList !=null) cbFSKlsAll =md.FSKList;

				if (md.Genres != null) cbxGenre0.setSelectedEnum(md.Genres.getGenre(0));
				if (md.Genres != null) cbxGenre1.setSelectedEnum(md.Genres.getGenre(1));
				if (md.Genres != null) cbxGenre2.setSelectedEnum(md.Genres.getGenre(2));
				if (md.Genres != null) cbxGenre3.setSelectedEnum(md.Genres.getGenre(3));
				if (md.Genres != null) cbxGenre4.setSelectedEnum(md.Genres.getGenre(4));
				if (md.Genres != null) cbxGenre5.setSelectedEnum(md.Genres.getGenre(5));
				if (md.Genres != null) cbxGenre6.setSelectedEnum(md.Genres.getGenre(6));
				if (md.Genres != null) cbxGenre7.setSelectedEnum(md.Genres.getGenre(7));

				btnRef.setIcon(selectedReference.getIconButton());
				if (md.AltRef != null) ctrlAltRef.setValue(md.AltRef); else ctrlAltRef.setValue(CCSingleOnlineReference.EMPTY);

				btnApply.setEnabled(true);

				updateCheckBoxes();

				pbarSearch.setIndeterminate(false);
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

	private void applyData() {
		owner.setOnlineReference(CCOnlineReferenceList.create(selectedReference, ctrlAltRef.getValue()));

		if (cbTitle.isSelected()) owner.setMovieName(edTitle.getText());

		if (cbYear.isSelected()) owner.setYear((int) spnYear.getValue());

		if (cbScore.isSelected()) owner.setScore(ctrlScore.getValue());

		if (cbLength.isSelected()) owner.setLength((int) spnLength.getValue());

		if (cbFSK.isSelected()) owner.setFSK(cbxFSK.getSelectedEnum());

		if (cbCover.isSelected()) owner.setCover(imgCoverBI);

		if (cbGenre0.isSelected()) owner.setGenre(0, cbxGenre0.getSelectedEnum());
		if (cbGenre1.isSelected()) owner.setGenre(1, cbxGenre1.getSelectedEnum());
		if (cbGenre2.isSelected()) owner.setGenre(2, cbxGenre2.getSelectedEnum());
		if (cbGenre3.isSelected()) owner.setGenre(3, cbxGenre3.getSelectedEnum());
		if (cbGenre4.isSelected()) owner.setGenre(4, cbxGenre4.getSelectedEnum());
		if (cbGenre5.isSelected()) owner.setGenre(5, cbxGenre5.getSelectedEnum());
		if (cbGenre6.isSelected()) owner.setGenre(6, cbxGenre6.getSelectedEnum());
		if (cbGenre7.isSelected()) owner.setGenre(7, cbxGenre7.getSelectedEnum());

		owner.onFinishInserting();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel1 = new JPanel();
		edSearchName = new JTextField();
		btnParse = new JButton();
		btnExtendedParse = new JButton();
		panel2 = new JPanel();
		scrollPane1 = new JScrollPane();
		lsDBList = new JList<>();
		panel3 = new JPanel();
		cbTitle = new JCheckBox();
		label1 = new JLabel();
		edTitle = new ReadableTextField();
		cbGenre0 = new JCheckBox();
		label7 = new JLabel();
		cbxGenre0 = new CCReadableEnumComboBox<CCGenre>(CCGenre.getWrapper());
		cbYear = new JCheckBox();
		label2 = new JLabel();
		spnYear = new JYearSpinner();
		cbGenre1 = new JCheckBox();
		label8 = new JLabel();
		cbxGenre1 = new CCReadableEnumComboBox<CCGenre>(CCGenre.getWrapper());
		cbScore = new JCheckBox();
		label3 = new JLabel();
		ctrlScore = new OnlineScoreControl();
		cbGenre2 = new JCheckBox();
		label9 = new JLabel();
		cbxGenre2 = new CCReadableEnumComboBox<CCGenre>(CCGenre.getWrapper());
		cbLength = new JCheckBox();
		label4 = new JLabel();
		spnLength = new ReadableSpinner();
		cbGenre3 = new JCheckBox();
		label10 = new JLabel();
		cbxGenre3 = new CCReadableEnumComboBox<CCGenre>(CCGenre.getWrapper());
		cbFSK = new JCheckBox();
		label5 = new JLabel();
		cbxFSK = new CCReadableEnumComboBox<CCFSK>(CCFSK.getWrapper());
		cbGenre4 = new JCheckBox();
		label11 = new JLabel();
		cbxGenre4 = new CCReadableEnumComboBox<CCGenre>(CCGenre.getWrapper());
		cbCover = new JCheckBox();
		label6 = new JLabel();
		imgCover = new CoverLabelFullsize(movielist);
		cbGenre5 = new JCheckBox();
		label12 = new JLabel();
		cbxGenre5 = new CCReadableEnumComboBox<CCGenre>(CCGenre.getWrapper());
		cbGenre6 = new JCheckBox();
		label13 = new JLabel();
		cbxGenre6 = new CCReadableEnumComboBox<CCGenre>(CCGenre.getWrapper());
		cbGenre7 = new JCheckBox();
		label14 = new JLabel();
		cbxGenre7 = new CCReadableEnumComboBox<CCGenre>(CCGenre.getWrapper());
		btnRef = new JButton();
		ctrlAltRef = new JSingleReferenceChooser(movielist);
		btnFSKAll = new JButton();
		btnApply = new JButton();
		pbarSearch = new JProgressBar();

		//======== this ========
		setTitle(LocaleBundle.getString("parseImDBFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(900, 525));
		setModal(true);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default, $lgap, default:grow, $ugap")); //$NON-NLS-1$

		//======== panel1 ========
		{
			panel1.setLayout(new FormLayout(
				"default:grow, $ugap, default, $lcgap, default", //$NON-NLS-1$
				"default")); //$NON-NLS-1$

			//---- edSearchName ----
			edSearchName.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					onSearchKeyPressed(e);
				}
			});
			panel1.add(edSearchName, CC.xy(1, 1));

			//---- btnParse ----
			btnParse.setText(LocaleBundle.getString("parseImDBFrame.btnParse.text")); //$NON-NLS-1$
			btnParse.addActionListener(e -> startParse());
			panel1.add(btnParse, CC.xy(3, 1));

			//---- btnExtendedParse ----
			btnExtendedParse.setText(LocaleBundle.getString("parseImDBFrame.btnParseExtended.text")); //$NON-NLS-1$
			btnExtendedParse.addActionListener(e -> startExtendedParse());
			panel1.add(btnExtendedParse, CC.xy(5, 1));
		}
		contentPane.add(panel1, CC.xy(2, 2, CC.FILL, CC.FILL));

		//======== panel2 ========
		{
			panel2.setBorder(new BevelBorder(BevelBorder.LOWERED));
			panel2.setLayout(new FormLayout(
				"$lcgap, 175dlu, $lcgap, 0dlu:grow, $lcgap", //$NON-NLS-1$
				"$lgap, default:grow, $lgap, default, $lgap")); //$NON-NLS-1$

			//======== scrollPane1 ========
			{

				//---- lsDBList ----
				lsDBList.addListSelectionListener(e -> onRefSelected(e));
				scrollPane1.setViewportView(lsDBList);
			}
			panel2.add(scrollPane1, CC.xy(2, 2, CC.FILL, CC.FILL));

			//======== panel3 ========
			{
				panel3.setBorder(new EtchedBorder());
				panel3.setLayout(new FormLayout(
					"2*($lcgap, default), $lcgap, 240px, 3*($lcgap, default), $lcgap, 0dlu:grow, $lcgap", //$NON-NLS-1$
					"8*($lgap, default), $lgap, default:grow, 3*($lgap, default), $lgap, $pgap, $lgap, default, $lgap")); //$NON-NLS-1$
				panel3.add(cbTitle, CC.xy(2, 2));

				//---- label1 ----
				label1.setText(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
				panel3.add(label1, CC.xy(4, 2));
				panel3.add(edTitle, CC.xy(6, 2));
				panel3.add(cbGenre0, CC.xy(10, 2));

				//---- label7 ----
				label7.setText(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
				panel3.add(label7, CC.xy(12, 2));

				//---- cbxGenre0 ----
				cbxGenre0.setEnabled(false);
				panel3.add(cbxGenre0, CC.xy(14, 2));
				panel3.add(cbYear, CC.xy(2, 4));

				//---- label2 ----
				label2.setText(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
				panel3.add(label2, CC.xy(4, 4));

				//---- spnYear ----
				spnYear.setEnabled(false);
				panel3.add(spnYear, CC.xy(6, 4));
				panel3.add(cbGenre1, CC.xy(10, 4));

				//---- label8 ----
				label8.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
				panel3.add(label8, CC.xy(12, 4));

				//---- cbxGenre1 ----
				cbxGenre1.setEnabled(false);
				panel3.add(cbxGenre1, CC.xy(14, 4));
				panel3.add(cbScore, CC.xy(2, 6));

				//---- label3 ----
				label3.setText(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
				panel3.add(label3, CC.xy(4, 6));

				//---- ctrlScore ----
				ctrlScore.setReadOnly(true);
				panel3.add(ctrlScore, CC.xy(6, 6));
				panel3.add(cbGenre2, CC.xy(10, 6));

				//---- label9 ----
				label9.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
				panel3.add(label9, CC.xy(12, 6));

				//---- cbxGenre2 ----
				cbxGenre2.setEnabled(false);
				panel3.add(cbxGenre2, CC.xy(14, 6));
				panel3.add(cbLength, CC.xy(2, 8));

				//---- label4 ----
				label4.setText(LocaleBundle.getString("AddMovieFrame.lblLength.text")); //$NON-NLS-1$
				panel3.add(label4, CC.xy(4, 8));

				//---- spnLength ----
				spnLength.setEnabled(true);
				panel3.add(spnLength, CC.xy(6, 8));
				panel3.add(cbGenre3, CC.xy(10, 8));

				//---- label10 ----
				label10.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
				panel3.add(label10, CC.xy(12, 8));

				//---- cbxGenre3 ----
				cbxGenre3.setEnabled(false);
				panel3.add(cbxGenre3, CC.xy(14, 8));
				panel3.add(cbFSK, CC.xy(2, 10));

				//---- label5 ----
				label5.setText(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
				panel3.add(label5, CC.xy(4, 10));

				//---- cbxFSK ----
				cbxFSK.setEnabled(false);
				panel3.add(cbxFSK, CC.xy(6, 10));
				panel3.add(cbGenre4, CC.xy(10, 10));

				//---- label11 ----
				label11.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
				panel3.add(label11, CC.xy(12, 10));

				//---- cbxGenre4 ----
				cbxGenre4.setEnabled(false);
				panel3.add(cbxGenre4, CC.xy(14, 10));
				panel3.add(cbCover, CC.xy(2, 12));

				//---- label6 ----
				label6.setText(LocaleBundle.getString("AddMovieFrame.lblCover.text")); //$NON-NLS-1$
				panel3.add(label6, CC.xy(4, 12));

				//---- imgCover ----
				imgCover.setText("text"); //$NON-NLS-1$
				panel3.add(imgCover, CC.xywh(6, 12, 1, 17, CC.FILL, CC.TOP));
				panel3.add(cbGenre5, CC.xy(10, 12));

				//---- label12 ----
				label12.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
				panel3.add(label12, CC.xy(12, 12));

				//---- cbxGenre5 ----
				cbxGenre5.setEnabled(false);
				panel3.add(cbxGenre5, CC.xy(14, 12));
				panel3.add(cbGenre6, CC.xy(10, 14));

				//---- label13 ----
				label13.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
				panel3.add(label13, CC.xy(12, 14));

				//---- cbxGenre6 ----
				cbxGenre6.setEnabled(false);
				panel3.add(cbxGenre6, CC.xy(14, 14));
				panel3.add(cbGenre7, CC.xy(10, 16));

				//---- label14 ----
				label14.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
				panel3.add(label14, CC.xy(12, 16));

				//---- cbxGenre7 ----
				cbxGenre7.setEnabled(false);
				panel3.add(cbxGenre7, CC.xy(14, 16));

				//---- btnRef ----
				btnRef.setText(" "); //$NON-NLS-1$
				btnRef.addActionListener(e -> openRefInBrowser());
				panel3.add(btnRef, CC.xy(14, 20));

				//---- ctrlAltRef ----
				ctrlAltRef.setEnabled(false);
				panel3.add(ctrlAltRef, CC.xy(14, 22));

				//---- btnFSKAll ----
				btnFSKAll.setText(LocaleBundle.getString("parseImDBFrame.btnFSKAll.text")); //$NON-NLS-1$
				btnFSKAll.addActionListener(e -> showAllRatingsDialog());
				panel3.add(btnFSKAll, CC.xy(14, 24));

				//---- btnApply ----
				btnApply.setText(LocaleBundle.getString("UIGeneric.btnApply.text")); //$NON-NLS-1$
				btnApply.setFont(btnApply.getFont().deriveFont(btnApply.getFont().getStyle() | Font.BOLD));
				btnApply.addActionListener(e -> onApply());
				panel3.add(btnApply, CC.xy(14, 28));
			}
			panel2.add(panel3, CC.xywh(4, 2, 1, 3, CC.FILL, CC.FILL));
			panel2.add(pbarSearch, CC.xy(2, 4));
		}
		contentPane.add(panel2, CC.xy(2, 4, CC.FILL, CC.FILL));
		setSize(1100, 595);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panel1;
	private JTextField edSearchName;
	private JButton btnParse;
	private JButton btnExtendedParse;
	private JPanel panel2;
	private JScrollPane scrollPane1;
	private JList<ParseOnlineDialogElement> lsDBList;
	private JPanel panel3;
	private JCheckBox cbTitle;
	private JLabel label1;
	private ReadableTextField edTitle;
	private JCheckBox cbGenre0;
	private JLabel label7;
	private CCReadableEnumComboBox<CCGenre> cbxGenre0;
	private JCheckBox cbYear;
	private JLabel label2;
	private JYearSpinner spnYear;
	private JCheckBox cbGenre1;
	private JLabel label8;
	private CCReadableEnumComboBox<CCGenre> cbxGenre1;
	private JCheckBox cbScore;
	private JLabel label3;
	private OnlineScoreControl ctrlScore;
	private JCheckBox cbGenre2;
	private JLabel label9;
	private CCReadableEnumComboBox<CCGenre> cbxGenre2;
	private JCheckBox cbLength;
	private JLabel label4;
	private ReadableSpinner spnLength;
	private JCheckBox cbGenre3;
	private JLabel label10;
	private CCReadableEnumComboBox<CCGenre> cbxGenre3;
	private JCheckBox cbFSK;
	private JLabel label5;
	private CCReadableEnumComboBox<CCFSK> cbxFSK;
	private JCheckBox cbGenre4;
	private JLabel label11;
	private CCReadableEnumComboBox<CCGenre> cbxGenre4;
	private JCheckBox cbCover;
	private JLabel label6;
	private CoverLabelFullsize imgCover;
	private JCheckBox cbGenre5;
	private JLabel label12;
	private CCReadableEnumComboBox<CCGenre> cbxGenre5;
	private JCheckBox cbGenre6;
	private JLabel label13;
	private CCReadableEnumComboBox<CCGenre> cbxGenre6;
	private JCheckBox cbGenre7;
	private JLabel label14;
	private CCReadableEnumComboBox<CCGenre> cbxGenre7;
	private JButton btnRef;
	private JSingleReferenceChooser ctrlAltRef;
	private JButton btnFSKAll;
	private JButton btnApply;
	private JProgressBar pbarSearch;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
