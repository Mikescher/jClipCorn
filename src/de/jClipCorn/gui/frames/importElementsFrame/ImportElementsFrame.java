package de.jClipCorn.gui.frames.importElementsFrame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.frames.importElementsFrame.contentList.ElementCellRenderer;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;

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
	private JPanel panel;
	private JPanel panel_1;
	private JCheckBox chcbxResetViewed;
	private JCheckBox chckbxOnlyCover;
	private JLabel lblCover;
	private JLabel lblViewed;
	private JLabel lblChilds;
	private JButton btnEditAdd;
	
	private DefaultListModel<Element> listModel;
	private CCMovieList movielist;
	private JCheckBox chckbxResetDate;

	public ImportElementsFrame(String xmlcontent, CCMovieList movielist) {
		super();
		this.movielist = movielist;
		
		initGUI();
		setLocationRelativeTo(null);
		
		initData(xmlcontent);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("ImportElementsFrame.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
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
		
		panel = new JPanel();
		pnlTop.add(panel, BorderLayout.EAST);
		panel.setLayout(new BorderLayout(0, 0));
		
		btnAddAll = new JButton(LocaleBundle.getString("ImportElementsFrame.btnAddAll.caption")); //$NON-NLS-1$
		btnAddAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onAddAll();
			}
		});
		panel.add(btnAddAll);
		
		panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.EAST);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		chckbxOnlyCover = new JCheckBox(CCProperties.getInstance().PROP_IMPORT_ONLYWITHCOVER.getDescription());
		chckbxOnlyCover.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CCProperties.getInstance().PROP_IMPORT_ONLYWITHCOVER.setValue(chckbxOnlyCover.isSelected());
			}
		});
		chckbxOnlyCover.setSelected(CCProperties.getInstance().PROP_IMPORT_ONLYWITHCOVER.getValue());
		panel_1.add(chckbxOnlyCover, BorderLayout.EAST);
		
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
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(45dlu;default)"), //$NON-NLS-1$
				ColumnSpec.decode("50dlu"), //$NON-NLS-1$
				ColumnSpec.decode("5dlu"), //$NON-NLS-1$
				ColumnSpec.decode("max(50dlu;default)"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("14px"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
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
				onAdd();
			}
		});
		btnAdd.setEnabled(false);
		pnlInfo.add(btnAdd, "7, 12"); //$NON-NLS-1$
		
		chckbxResetDate = new JCheckBox(CCProperties.getInstance().PROP_IMPORT_RESETADDDATE.getDescription());
		pnlInfo.add(chckbxResetDate, "2, 14, 6, 1"); //$NON-NLS-1$
		chckbxResetDate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CCProperties.getInstance().PROP_IMPORT_RESETADDDATE.setValue(chckbxResetDate.isSelected());
			}
		});
		chckbxResetDate.setSelected(CCProperties.getInstance().PROP_IMPORT_RESETADDDATE.getValue());
		
		chcbxResetViewed = new JCheckBox(CCProperties.getInstance().PROP_IMPORT_RESETVIEWED.getDescription());
		pnlInfo.add(chcbxResetViewed, "2, 16, 6, 1"); //$NON-NLS-1$
		chcbxResetViewed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CCProperties.getInstance().PROP_IMPORT_RESETVIEWED.setValue(chcbxResetViewed.isSelected());
			}
		});
		chcbxResetViewed.setSelected(CCProperties.getInstance().PROP_IMPORT_RESETVIEWED.getValue());
		
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
			if (value.getName().equalsIgnoreCase("movie")) { 
				if (value.getAttributeValue("zyklus").isEmpty()) { 
					lblName.setText(value.getAttributeValue("title"));
				} else {
					lblName.setText(value.getAttributeValue("zyklus") + " " + value.getAttributeValue("zyklusnumber") + " - " + value.getAttributeValue("title"));
				}
				
				lblChilds.setText("0");
				
				lblViewed.setText(LocaleBundle.getString((value.getAttributeValue("viewed").equals("true")) ? ("ImportElementsFrame.common.bool_true") : ("ImportElementsFrame.common.bool_false")));
			} else {
				lblName.setText(value.getAttributeValue("title"));
				
				int count = 0;
				List<Element> childs = value.getChildren();
				for (Element e : childs) {
					count += e.getChildren().size();
				}
				lblChilds.setText("" + count);

				lblViewed.setText("");
			}
			
			lblCover.setText(LocaleBundle.getString((value.getAttributeValue("coverdata") != null) ? ("ImportElementsFrame.common.bool_true") : ("ImportElementsFrame.common.bool_false")));
		
			btnEditAdd.setEnabled(value.getName().equalsIgnoreCase("movie"));
			btnAdd.setEnabled(true);
		} else {
			lblChilds.setText("");
			lblCover.setText("");
			lblName.setText("");
			lblViewed.setText("");
			
			btnEditAdd.setEnabled(false);
			btnAdd.setEnabled(false);
		}
	}
	
	private void onAdd() {
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
		
	private void onAddMovie(Element value, int index) {
		CCMovie mov = movielist.createNewEmptyMovie();
		
		mov.parseFromXML(value, chckbxResetDate.isSelected(), chcbxResetViewed.isSelected());
		
		listModel.remove(index);
	}
	
	private void onAddSeries(Element value, int index) {
		CCSeries ser = movielist.createNewEmptySeries();
		
		ser.parseFromXML(value, chckbxResetDate.isSelected(), chcbxResetViewed.isSelected());
		
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
		
		amf.parseFromXML(value, chckbxResetDate.isSelected(), chcbxResetViewed.isSelected());
		
		amf.setVisible(true);
		
		listModel.remove(lbContent.getSelectedIndex());
	}
	
	@SuppressWarnings("nls")
	private void onAddAll() {
		for (int i = listModel.size()-1; i >= 0; i--) {
			Element value = listModel.get(i);
			
			if ((! chckbxOnlyCover.isSelected()) || (value.getAttributeValue("coverdata") != null)) {
				if (value.getName().equalsIgnoreCase("movie")) {  //$NON-NLS-1$
					onAddMovie(value, i);
				} else if (value.getName().equalsIgnoreCase("series")) { //$NON-NLS-1$
					onAddSeries(value, i);
				}
			}
		}
	}
}
