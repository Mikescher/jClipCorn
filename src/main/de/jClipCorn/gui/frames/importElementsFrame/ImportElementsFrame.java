package de.jClipCorn.gui.frames.importElementsFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.guiComponents.PropertyCheckbox;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.helper.DialogHelper;

public class ImportElementsFrame extends JFrame {
	private static final long serialVersionUID = -7243383487017811810L;
	
	private JPanel contentPane;
	private JPanel pnlTop;
	private JPanel pnlCenter;
	private JLabel lblElementsFound;
	private JButton btnAddAll;
	private JScrollPane scrollPane;
	private JList<Element> lbContent;
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
	
	private DefaultListModel<Element> listModel;
	private CCMovieList movielist;

	public ImportElementsFrame(Component owner, String xmlcontent, CCMovieList movielist) {
		super();
		this.movielist = movielist;
		
		initGUI();
		setLocationRelativeTo(owner);
		
		initData(xmlcontent);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("ImportElementsFrame.this.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
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
		btnAddAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					onAddAll();
				} catch (CCFormatException e) {
					DialogHelper.showError(ImportElementsFrame.this, LocaleBundle.getString("Dialogs.GenericCaption.Error"), LocaleBundle.getString("LogMessage.FormatErrorInExport")); //$NON-NLS-1$ //$NON-NLS-2$
					CCLog.addWarning(LocaleBundle.getString("LogMessage.FormatErrorInExport"), e); //$NON-NLS-1$
				}
			}
		});
		pnlTopRight.add(btnAddAll);
		
		pnlTopRightInner = new JPanel();
		pnlTopRight.add(pnlTopRightInner, BorderLayout.EAST);
		pnlTopRightInner.setLayout(new BorderLayout(0, 0));
		
		chckbxOnlyCover = new PropertyCheckbox(CCProperties.getInstance().PROP_IMPORT_ONLYWITHCOVER);
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
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					onAdd();
				} catch (CCFormatException e) {
					DialogHelper.showError(ImportElementsFrame.this, LocaleBundle.getString("Dialogs.GenericCaption.Error"), LocaleBundle.getString("LogMessage.FormatErrorInExportf")); //$NON-NLS-1$ //$NON-NLS-2$
					CCLog.addWarning(LocaleBundle.getString("LogMessage.FormatErrorInExport"), e); //$NON-NLS-1$
				}
			}
		});
		btnAdd.setEnabled(false);
		pnlInfo.add(btnAdd, "7, 12"); //$NON-NLS-1$
		
		chckbxResetDate = new PropertyCheckbox(CCProperties.getInstance().PROP_IMPORT_RESETADDDATE);
		pnlInfo.add(chckbxResetDate, "2, 14, 6, 1"); //$NON-NLS-1$
		
		chcbxResetViewed = new PropertyCheckbox(CCProperties.getInstance().PROP_IMPORT_RESETVIEWED);
		pnlInfo.add(chcbxResetViewed, "2, 16, 6, 1"); //$NON-NLS-1$
		
		chcbxResetScore = new PropertyCheckbox(CCProperties.getInstance().PROP_IMPORT_RESETSCORE);
		pnlInfo.add(chcbxResetScore, "2, 18, 6, 1"); //$NON-NLS-1$
		
		chckbxResetTags = new PropertyCheckbox(CCProperties.getInstance().PROP_IMPORT_RESETTAGS);
		pnlInfo.add(chckbxResetTags, "2, 20, 6, 1"); //$NON-NLS-1$
		
		setSize(new Dimension(650, 350));
		setMinimumSize(new Dimension(550, 250));
	}

	private void initData(String xmlcontent) {
		Document doc = null;
		try {
			doc = new SAXBuilder().build(new StringReader(xmlcontent));
		} catch (JDOMException | IOException e) {
			CCLog.addError(e);
			return;
		}

		Element root = doc.getRootElement();
		
		List<Element> elements = root.getChildren();
		
		lblElementsFound.setText(LocaleBundle.getFormattedString("ImportElementsFrame.lblInfo.text", elements.size())); //$NON-NLS-1$
		
		for (Element e : elements) {
			listModel.addElement(e);
		}
		
	}
	
	@SuppressWarnings("nls")
	private void updateInfoPanel() {
		if (lbContent.getSelectedValue() != null) {
			Element value = lbContent.getSelectedValue();
			if (value.getName().equalsIgnoreCase("movie")) {  //$NON-NLS-1$
				if (value.getAttributeValue("zyklus").isEmpty()) {  //$NON-NLS-1$
					lblName.setText(value.getAttributeValue("title")); //$NON-NLS-1$
				} else {
					lblName.setText(value.getAttributeValue("zyklus") + " " + value.getAttributeValue("zyklusnumber") + " - " + value.getAttributeValue("title")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				}
				
				lblChilds.setText("0"); //$NON-NLS-1$
				
				lblViewed.setText(LocaleBundle.getString((value.getAttributeValue("viewed").equals("true")) ? ("ImportElementsFrame.common.bool_true") : ("ImportElementsFrame.common.bool_false"))); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				lblName.setText(value.getAttributeValue("title")); //$NON-NLS-1$
				
				int count = 0;
				List<Element> childs = value.getChildren();
				for (Element e : childs) {
					count += e.getChildren().size();
				}
				lblChilds.setText("" + count); //$NON-NLS-1$

				lblViewed.setText(""); //$NON-NLS-1$
			}
			
			lblCover.setText(LocaleBundle.getString((value.getAttributeValue("coverdata") != null) ? ("ImportElementsFrame.common.bool_true") : ("ImportElementsFrame.common.bool_false"))); //$NON-NLS-1$
		
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
	
	private void onAdd() throws CCFormatException {
		if (lbContent.getSelectedValue() == null) {
			return;
		}
		
		Element value = lbContent.getSelectedValue();
		
		if (value.getName().equalsIgnoreCase("movie")) {  //$NON-NLS-1$
			onAddMovie(value, lbContent.getSelectedIndex());
		} else if (value.getName().equalsIgnoreCase("series")) { //$NON-NLS-1$
			onAddSeries(value, lbContent.getSelectedIndex());
		}
	}
		
	private void onAddMovie(Element value, int index) throws CCFormatException {
		CCMovie mov = movielist.createNewEmptyMovie();
		
		mov.parseFromXML(value, chckbxResetDate.isSelected(), chcbxResetViewed.isSelected(), chcbxResetScore.isSelected(), chckbxResetTags.isSelected(), false);
		
		listModel.remove(index);
	}
	
	private void onAddSeries(Element value, int index) throws CCFormatException {
		CCSeries ser = movielist.createNewEmptySeries();
		
		ser.parseFromXML(value, chckbxResetDate.isSelected(), chcbxResetViewed.isSelected(), chcbxResetScore.isSelected(), chckbxResetTags.isSelected(), false);
		
		listModel.remove(index);
	}
	
	private void onEdit() {
		if (lbContent.getSelectedValue() == null) {
			return;
		}
		
		Element value = lbContent.getSelectedValue();
		
		if (! value.getName().equalsIgnoreCase("movie")) { //$NON-NLS-1$
			return;
		}
		
		AddMovieFrame amf = new AddMovieFrame(this, movielist);
		
		try {
			amf.parseFromXML(value, chckbxResetDate.isSelected(), chcbxResetViewed.isSelected(), chcbxResetScore.isSelected());
		} catch (CCFormatException e) {
			CCLog.addError(e);
			return;
		}
		
		amf.setVisible(true);
		
		listModel.remove(lbContent.getSelectedIndex());
	}
	
	private void onAddAll() throws CCFormatException {
		for (int i = listModel.size()-1; i >= 0; i--) {
			Element value = listModel.get(i);
			
			if ((! chckbxOnlyCover.isSelected()) || (value.getAttributeValue("coverdata") != null)) { //$NON-NLS-1$
				if (value.getName().equalsIgnoreCase("movie")) {  //$NON-NLS-1$
					onAddMovie(value, i);
				} else if (value.getName().equalsIgnoreCase("series")) { //$NON-NLS-1$
					onAddSeries(value, i);
				}
			}
		}
	}
}
