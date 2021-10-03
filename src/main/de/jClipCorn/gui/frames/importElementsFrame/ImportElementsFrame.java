package de.jClipCorn.gui.frames.importElementsFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.xmlimport.DatabaseXMLImporter;
import de.jClipCorn.features.serialization.xmlimport.ImportOptions;
import de.jClipCorn.features.serialization.xmlimport.ImportState;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.PropertyCheckbox;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.SerializationException;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;
import de.jClipCorn.util.xml.CCXMLParser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ImportElementsFrame extends JCCFrame {
	private static final long serialVersionUID = -7243383487017811810L;
	
	private JPanel contentPane;
	private JPanel pnlTop;
	private JPanel pnlCenter;
	private JLabel lblElementsFound;
	private JButton btnAddAll;
	private JScrollPane scrollPane;
	private JList<CCXMLElement> lbContent;
	private JPanel pnlInfo;
	private JLabel lblTXT2;
	private JLabel lblName;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblTXT;
	private JButton btnAdd;
	private JPanel pnlTopRight;
	private JPanel pnlTopRightInner;
	private PropertyCheckbox chckbxOnlyCover;
	private JLabel lblCover;
	private PropertyCheckbox chcbxResetViewed;
	private PropertyCheckbox chcbxResetScore;
	private PropertyCheckbox chckbxResetDate;
	private PropertyCheckbox chckbxResetTags;
	private JLabel lblViewed;
	private JLabel lblChilds;
	private JButton btnEditAdd;
	
	private DefaultListModel<CCXMLElement> listModel;

	private int data_xmlver = 1;
	private CCXMLParser document;

	public ImportElementsFrame(Component owner, String xmlcontent, CCMovieList movielist) {
		super(movielist);

		initGUI();
		setLocationRelativeTo(owner);
		
		initData(xmlcontent);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("ImportElementsFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(2, 2, 2, 2));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		pnlTop = new JPanel();
		pnlTop.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BorderLayout(0, 0));
		
		lblElementsFound = new JLabel(LocaleBundle.getFormattedString("ImportElementsFrame.lblInfo.text", 0)); //$NON-NLS-1$
		lblElementsFound.setVerticalAlignment(SwingConstants.TOP);
		pnlTop.add(lblElementsFound);
		
		pnlTopRight = new JPanel();
		pnlTop.add(pnlTopRight, BorderLayout.EAST);
		pnlTopRight.setLayout(new BorderLayout(0, 0));
		
		btnAddAll = new JButton(LocaleBundle.getString("ImportElementsFrame.btnAddAll.caption")); //$NON-NLS-1$
		btnAddAll.addActionListener(arg0 ->
		{
			try {
				onAddAll();
			} catch (CCFormatException | CCXMLException | SerializationException e) {
				DialogHelper.showDispatchError(ImportElementsFrame.this, LocaleBundle.getString("Dialogs.GenericCaption.Error"), LocaleBundle.getString("LogMessage.FormatErrorInExport")); //$NON-NLS-1$ //$NON-NLS-2$
				CCLog.addWarning(LocaleBundle.getString("LogMessage.FormatErrorInExport"), e); //$NON-NLS-1$
			}
		});
		pnlTopRight.add(btnAddAll);
		
		pnlTopRightInner = new JPanel();
		pnlTopRight.add(pnlTopRightInner, BorderLayout.EAST);
		pnlTopRightInner.setLayout(new BorderLayout(0, 0));
		
		chckbxOnlyCover = new PropertyCheckbox(ccprops().PROP_IMPORT_ONLYWITHCOVER);
		pnlTopRightInner.add(chckbxOnlyCover, BorderLayout.EAST);
		
		pnlCenter = new JPanel();
		contentPane.add(pnlCenter, BorderLayout.CENTER);
		pnlCenter.setLayout(new BorderLayout(2, 0));
		
		scrollPane = new JScrollPane();
		pnlCenter.add(scrollPane, BorderLayout.CENTER);
		
		lbContent = new JList<>();
		lbContent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(lbContent);
		lbContent.setModel(listModel = new DefaultListModel<>());
		lbContent.setCellRenderer(new ElementCellRenderer());
		lbContent.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				updateInfoPanel();
			}
		});
		
		pnlInfo = new JPanel();
		pnlInfo.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlCenter.add(pnlInfo, BorderLayout.EAST);
		pnlInfo.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(45dlu;default)"), //$NON-NLS-1$
				ColumnSpec.decode("50dlu"), //$NON-NLS-1$
				ColumnSpec.decode("5dlu"), //$NON-NLS-1$
				ColumnSpec.decode("max(50dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("14px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
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
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		lblTXT2 = new JLabel(LocaleBundle.getString("ImportElementsFrame.lblName.caption")); //$NON-NLS-1$
		pnlInfo.add(lblTXT2, "2, 2, left, top"); //$NON-NLS-1$
		
		lblName = new JLabel();
		pnlInfo.add(lblName, "4, 2, 4, 1"); //$NON-NLS-1$
		
		lblNewLabel = new JLabel(LocaleBundle.getString("ImportElementsFrame.lblCover.caption")); //$NON-NLS-1$
		pnlInfo.add(lblNewLabel, "2, 4"); //$NON-NLS-1$
		
		lblCover = new JLabel();
		pnlInfo.add(lblCover, "4, 4, 4, 1"); //$NON-NLS-1$
		
		lblTXT = new JLabel(LocaleBundle.getString("ImportElementsFrame.lblViewed.caption")); //$NON-NLS-1$
		pnlInfo.add(lblTXT, "2, 6"); //$NON-NLS-1$
		
		lblViewed = new JLabel();
		pnlInfo.add(lblViewed, "4, 6, 4, 1"); //$NON-NLS-1$
		
		lblNewLabel_1 = new JLabel(LocaleBundle.getString("ImportElementsFrame.lblChilds.caption")); //$NON-NLS-1$
		pnlInfo.add(lblNewLabel_1, "2, 8"); //$NON-NLS-1$
		
		lblChilds = new JLabel();
		pnlInfo.add(lblChilds, "4, 8, 4, 1"); //$NON-NLS-1$
		
		btnEditAdd = new JButton(LocaleBundle.getString("ImportElementsFrame.btnEditAndAdd.caption")); //$NON-NLS-1$
		btnEditAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onEdit();
			}
		});
		btnEditAdd.setEnabled(false);
		pnlInfo.add(btnEditAdd, "2, 12, 4, 1"); //$NON-NLS-1$
		
		btnAdd = new JButton(LocaleBundle.getString("ImportElementsFrame.btnAdd.caption")); //$NON-NLS-1$
		btnAdd.addActionListener(arg0 ->
		{
			try {
				onAdd();
			} catch (CCFormatException | CCXMLException | SerializationException e) {
				DialogHelper.showDispatchError(ImportElementsFrame.this, LocaleBundle.getString("Dialogs.GenericCaption.Error"), LocaleBundle.getString("LogMessage.FormatErrorInExportf")); //$NON-NLS-1$ //$NON-NLS-2$
				CCLog.addWarning(LocaleBundle.getString("LogMessage.FormatErrorInExport"), e); //$NON-NLS-1$
			}
		});
		btnAdd.setEnabled(false);
		pnlInfo.add(btnAdd, "7, 12"); //$NON-NLS-1$
		
		chckbxResetDate = new PropertyCheckbox(ccprops().PROP_IMPORT_RESETADDDATE);
		pnlInfo.add(chckbxResetDate, "2, 14, 6, 1"); //$NON-NLS-1$
		
		chcbxResetViewed = new PropertyCheckbox(ccprops().PROP_IMPORT_RESETVIEWED);
		pnlInfo.add(chcbxResetViewed, "2, 16, 6, 1"); //$NON-NLS-1$
		
		chcbxResetScore = new PropertyCheckbox(ccprops().PROP_IMPORT_RESETSCORE);
		pnlInfo.add(chcbxResetScore, "2, 18, 6, 1"); //$NON-NLS-1$
		
		chckbxResetTags = new PropertyCheckbox(ccprops().PROP_IMPORT_RESETTAGS);
		pnlInfo.add(chckbxResetTags, "2, 20, 6, 1"); //$NON-NLS-1$
		
		setSize(new Dimension(650, 350));
		setMinimumSize(new Dimension(550, 250));
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
	
	private void onAdd() throws CCFormatException, CCXMLException, SerializationException {
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
		
	private void onAddMovie(CCXMLElement value, int index) throws CCFormatException, CCXMLException, SerializationException {
		CCMovie mov = movielist.createNewEmptyMovie();
		DatabaseXMLImporter.parseSingleMovie(mov, value, f->null, new ImportState(document, data_xmlver, new ImportOptions(chckbxResetDate.isSelected(), chcbxResetViewed.isSelected(), chcbxResetScore.isSelected(), chckbxResetTags.isSelected(), false)));
		listModel.remove(index);
	}
	
	private void onAddSeries(CCXMLElement value, int index) throws CCFormatException, CCXMLException, SerializationException {
		CCSeries ser = movielist.createNewEmptySeries();
		DatabaseXMLImporter.parseSingleSeries(ser, value, f->null, new ImportState(document, data_xmlver, new ImportOptions(chckbxResetDate.isSelected(), chcbxResetViewed.isSelected(), chcbxResetScore.isSelected(), chckbxResetTags.isSelected(), false)));
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
		} catch (CCFormatException | SerializationException | CCXMLException e) {
			CCLog.addError(e);
			return;
		}
		
		amf.setVisible(true);
		
		listModel.remove(lbContent.getSelectedIndex());
	}

	@SuppressWarnings("nls")
	private void onAddAll() throws CCFormatException, CCXMLException, SerializationException {
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
}
