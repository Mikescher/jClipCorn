package de.jClipCorn.gui.frames.importElementsFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.xmlimport.DatabaseXMLImporter;
import de.jClipCorn.features.serialization.xmlimport.ImportOptions;
import de.jClipCorn.features.serialization.xmlimport.ImportState;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.PropertyCheckbox;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;
import de.jClipCorn.util.xml.CCXMLParser;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.List;

public class ImportElementsFrame extends JCCFrame {
	private static final long serialVersionUID = -7243383487017811810L;

	private DefaultListModel<CCXMLElement> listModel;

	private int data_xmlver = 1;
	private CCXMLParser document;

	public ImportElementsFrame(Component owner, String xmlcontent, CCMovieList movielist) {
		super(movielist);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);

		initData(xmlcontent);
	}

	private void postInit() {
		lbContent.setModel(listModel = new DefaultListModel<>());
		lbContent.setCellRenderer(new ElementCellRenderer());

		lblElementsFound.setText(LocaleBundle.getFormattedString("ImportElementsFrame.lblInfo.text", 0)); //$NON-NLS-1$
	}

	@SuppressWarnings("nls")
	private void initData(String xmlcontent) {
		try {
			document = CCXMLParser.parse(xmlcontent);

			CCXMLElement root = document.getRoot("database");

			data_xmlver = root.getAttributeIntValueOrDefault("xmlversion", 1);

			List<CCXMLElement> elements = root.getAllChildren(new String[]{"movie", "series"}).enumerate();

			lblElementsFound.setText(LocaleBundle.getFormattedString("ImportElementsFrame.lblInfo.text", elements.size()));

			for (CCXMLElement e : elements) {
				listModel.addElement(e);
			}

		} catch (CCXMLException e) {
			CCLog.addError(e);
			return;
		}
	}

	@SuppressWarnings("nls")
	private void updateInfoPanel() {
		try {
			if (lbContent.getSelectedValue() != null) {
				CCXMLElement value = lbContent.getSelectedValue();

				if (value.getName().equalsIgnoreCase("movie")) {
					if (value.getAttributeValueOrThrow("zyklus").isEmpty()) {
						lblName.setText(value.getAttributeValueOrThrow("title"));
					} else {
						lblName.setText(value.getAttributeValueOrThrow("zyklus") + " " + value.getAttributeValueOrThrow("zyklusnumber") + " - " + value.getAttributeValueOrThrow("title"));
					}

					lblChilds.setText("0"); //$NON-NLS-1$

					lblViewed.setText(LocaleBundle.getString((value.getAttributeValueOrThrow("viewed").equals("true")) ? ("ImportElementsFrame.common.bool_true") : ("ImportElementsFrame.common.bool_false")));
				} else {
					lblName.setText(value.getAttributeValueOrThrow("title")); //$NON-NLS-1$

					int count = value.getAllChildren().sumInt(c -> c.getAllChildren().count());
					lblChilds.setText("" + count); //$NON-NLS-1$

					lblViewed.setText(""); //$NON-NLS-1$
				}

				lblCover.setText(LocaleBundle.getString((value.getAttributeValueOrThrow("coverdata") != null) ? ("ImportElementsFrame.common.bool_true") : ("ImportElementsFrame.common.bool_false"))); //$NON-NLS-1$

				btnEditAdd.setEnabled(value.getName().equalsIgnoreCase("movie")); //$NON-NLS-1$
				btnAdd.setEnabled(true);
			} else {
				lblChilds.setText(""); //$NON-NLS-1$
				lblCover.setText(""); //$NON-NLS-1$
				lblName.setText(""); //$NON-NLS-1$
				lblViewed.setText(""); //$NON-NLS-1$

				btnEditAdd.setEnabled(false);
				btnAdd.setEnabled(false);
			}
		}
		catch (CCXMLException e)
		{
			CCLog.addError(e);
		}
	}

	private void onAddAction() {
		try {
			onAdd();
		} catch (Exception e) {
			DialogHelper.showDispatchError(ImportElementsFrame.this, LocaleBundle.getString("Dialogs.GenericCaption.Error"), LocaleBundle.getString("LogMessage.FormatErrorInExport")); //$NON-NLS-1$ //$NON-NLS-2$
			CCLog.addWarning(LocaleBundle.getString("LogMessage.FormatErrorInExport"), e); //$NON-NLS-1$
		}
	}

	private void onAddAllAction() {
		try {
			onAddAll();
		} catch (Exception e) {
			DialogHelper.showDispatchError(ImportElementsFrame.this, LocaleBundle.getString("Dialogs.GenericCaption.Error"), LocaleBundle.getString("LogMessage.FormatErrorInExport")); //$NON-NLS-1$ //$NON-NLS-2$
			CCLog.addWarning(LocaleBundle.getString("LogMessage.FormatErrorInExport"), e); //$NON-NLS-1$
		}
	}

	private void onAdd() throws Exception {
		if (lbContent.getSelectedValue() == null) {
			return;
		}

		CCXMLElement value = lbContent.getSelectedValue();

		if (value.getName().equalsIgnoreCase("movie")) {  //$NON-NLS-1$
			onAddMovie(value, lbContent.getSelectedIndex());
		} else if (value.getName().equalsIgnoreCase("series")) { //$NON-NLS-1$
			onAddSeries(value, lbContent.getSelectedIndex());
		}
	}

	private void onAddMovie(CCXMLElement value, int index) throws Exception {
		movielist.createNewMovie(mov -> DatabaseXMLImporter.parseSingleMovie(mov, value, f->null, new ImportState(document, data_xmlver, new ImportOptions(chckbxResetDate.isSelected(), chcbxResetViewed.isSelected(), chcbxResetScore.isSelected(), chckbxResetTags.isSelected(), false))));
		listModel.remove(index);
	}

	private void onAddSeries(CCXMLElement value, int index) throws Exception {
		movielist.createNewSeries(ser -> DatabaseXMLImporter.parseSingleSeries(ser, value, f->null, new ImportState(document, data_xmlver, new ImportOptions(chckbxResetDate.isSelected(), chcbxResetViewed.isSelected(), chcbxResetScore.isSelected(), chckbxResetTags.isSelected(), false))));
		listModel.remove(index);
	}

	private void onEdit() {
		if (lbContent.getSelectedValue() == null) {
			return;
		}

		CCXMLElement value = lbContent.getSelectedValue();

		if (! value.getName().equalsIgnoreCase("movie")) { //$NON-NLS-1$
			return;
		}

		AddMovieFrame amf = new AddMovieFrame(this, movielist);

		try {
			CCMovie tmpMov = new CCMovie(CCMovieList.createStub(), -1);
			tmpMov.setDefaultValues(false);
			DatabaseXMLImporter.parseSingleMovie(tmpMov, value, f->null, new ImportState(document, data_xmlver, new ImportOptions(chckbxResetDate.isSelected(), chcbxResetViewed.isSelected(), chcbxResetScore.isSelected(), false, true)));
		} catch (Exception e) {
			CCLog.addError(e);
			return;
		}

		amf.setVisible(true);

		listModel.remove(lbContent.getSelectedIndex());
	}

	@SuppressWarnings("nls")
	private void onAddAll() throws Exception {
		for (int i = listModel.size()-1; i >= 0; i--) {
			CCXMLElement value = listModel.get(i);

			if ((! chckbxOnlyCover.isSelected()) || value.hasAttribute("coverdata"))
			{
				if (value.getName().equalsIgnoreCase("movie"))   //$NON-NLS-1$
				{
					onAddMovie(value, i);
				}
				else if (value.getName().equalsIgnoreCase("series"))  //$NON-NLS-1$
				{
					onAddSeries(value, i);
				}
			}
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		pnlTop = new JPanel();
		lblElementsFound = new JLabel();
		btnAddAll = new JButton();
		chckbxOnlyCover = new PropertyCheckbox(ccprops().PROP_IMPORT_ONLYWITHCOVER);
		scrollPane = new JScrollPane();
		lbContent = new JList<>();
		pnlInfo = new JPanel();
		lblTXT2 = new JLabel();
		lblName = new JLabel();
		lblNewLabel = new JLabel();
		lblCover = new JLabel();
		lblTXT = new JLabel();
		lblViewed = new JLabel();
		lblNewLabel_1 = new JLabel();
		lblChilds = new JLabel();
		btnEditAdd = new JButton();
		btnAdd = new JButton();
		chckbxResetDate = new PropertyCheckbox(ccprops().PROP_IMPORT_RESETADDDATE);
		chcbxResetViewed = new PropertyCheckbox(ccprops().PROP_IMPORT_RESETVIEWED);
		chcbxResetScore = new PropertyCheckbox(ccprops().PROP_IMPORT_RESETSCORE);
		chckbxResetTags = new PropertyCheckbox(ccprops().PROP_IMPORT_RESETTAGS);

		//======== this ========
		setTitle(LocaleBundle.getString("ImportElementsFrame.this.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(550, 250));
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $rgap, default, $ugap",
			"$ugap, default, $lgap, default:grow, $ugap"));

		//======== pnlTop ========
		{
			pnlTop.setLayout(new FormLayout(
				"default:grow, 2*($rgap, default)",
				"default"));

			//---- lblElementsFound ----
			lblElementsFound.setVerticalAlignment(SwingConstants.TOP);
			pnlTop.add(lblElementsFound, CC.xy(1, 1, CC.FILL, CC.FILL));

			//---- btnAddAll ----
			btnAddAll.setText(LocaleBundle.getString("ImportElementsFrame.btnAddAll.caption"));
			btnAddAll.addActionListener(e -> onAddAllAction());
			pnlTop.add(btnAddAll, CC.xy(3, 1));
			pnlTop.add(chckbxOnlyCover, CC.xy(5, 1));
		}
		contentPane.add(pnlTop, CC.xywh(2, 2, 3, 1, CC.FILL, CC.FILL));

		//======== scrollPane ========
		{

			//---- lbContent ----
			lbContent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lbContent.addListSelectionListener(e -> updateInfoPanel());
			scrollPane.setViewportView(lbContent);
		}
		contentPane.add(scrollPane, CC.xy(2, 4, CC.FILL, CC.FILL));

		//======== pnlInfo ========
		{
			pnlInfo.setBorder(new EtchedBorder());
			pnlInfo.setLayout(new FormLayout(
				"$lcgap, default, $rgap, [45dlu,default], 50dlu, 5dlu, [50dlu,default], $rgap",
				"$lgap, 14px, 3*($rgap, default), $rgap, default:grow, 5*($rgap, default), $rgap"));

			//---- lblTXT2 ----
			lblTXT2.setText(LocaleBundle.getString("ImportElementsFrame.lblName.caption"));
			pnlInfo.add(lblTXT2, CC.xy(2, 2, CC.LEFT, CC.TOP));
			pnlInfo.add(lblName, CC.xywh(4, 2, 4, 1));

			//---- lblNewLabel ----
			lblNewLabel.setText(LocaleBundle.getString("ImportElementsFrame.lblCover.caption"));
			pnlInfo.add(lblNewLabel, CC.xy(2, 4));
			pnlInfo.add(lblCover, CC.xywh(4, 4, 4, 1));

			//---- lblTXT ----
			lblTXT.setText(LocaleBundle.getString("ImportElementsFrame.lblViewed.caption"));
			pnlInfo.add(lblTXT, CC.xy(2, 6));
			pnlInfo.add(lblViewed, CC.xywh(4, 6, 4, 1));

			//---- lblNewLabel_1 ----
			lblNewLabel_1.setText(LocaleBundle.getString("ImportElementsFrame.lblChilds.caption"));
			pnlInfo.add(lblNewLabel_1, CC.xy(2, 8));
			pnlInfo.add(lblChilds, CC.xywh(4, 8, 4, 1));

			//---- btnEditAdd ----
			btnEditAdd.setText(LocaleBundle.getString("ImportElementsFrame.btnEditAndAdd.caption"));
			btnEditAdd.setEnabled(false);
			btnEditAdd.addActionListener(e -> onEdit());
			pnlInfo.add(btnEditAdd, CC.xywh(2, 12, 4, 1));

			//---- btnAdd ----
			btnAdd.setText(LocaleBundle.getString("ImportElementsFrame.btnAdd.caption"));
			btnAdd.setEnabled(false);
			btnAdd.addActionListener(e -> onAddAction());
			pnlInfo.add(btnAdd, CC.xy(7, 12));
			pnlInfo.add(chckbxResetDate, CC.xywh(2, 14, 6, 1));
			pnlInfo.add(chcbxResetViewed, CC.xywh(2, 16, 6, 1));
			pnlInfo.add(chcbxResetScore, CC.xywh(2, 18, 6, 1));
			pnlInfo.add(chckbxResetTags, CC.xywh(2, 20, 6, 1));
		}
		contentPane.add(pnlInfo, CC.xy(4, 4, CC.FILL, CC.FILL));
		setSize(950, 660);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel pnlTop;
	private JLabel lblElementsFound;
	private JButton btnAddAll;
	private PropertyCheckbox chckbxOnlyCover;
	private JScrollPane scrollPane;
	private JList<CCXMLElement> lbContent;
	private JPanel pnlInfo;
	private JLabel lblTXT2;
	private JLabel lblName;
	private JLabel lblNewLabel;
	private JLabel lblCover;
	private JLabel lblTXT;
	private JLabel lblViewed;
	private JLabel lblNewLabel_1;
	private JLabel lblChilds;
	private JButton btnEditAdd;
	private JButton btnAdd;
	private PropertyCheckbox chckbxResetDate;
	private PropertyCheckbox chcbxResetViewed;
	private PropertyCheckbox chcbxResetScore;
	private PropertyCheckbox chckbxResetTags;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
