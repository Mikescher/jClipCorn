package de.jClipCorn.gui.frames.textExportFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.helper.TextFileUtils;

public class TextExportFrame extends JFrame {
	private static final long serialVersionUID = -807033167837187549L;
	
	private CCMovieList movielist;
	
	private JTextArea memoResult;
	private JScrollPane scrollPane;
	private JComboBox<DatabaseTextExporter> cbFormat;
	private JButton btnCreate;
	private JButton btnExport;
	private JLabel lblFormat;
	private JCheckBox cbxIncludeSeries;
	private JCheckBox cbxIncludeLanguage;
	private JCheckBox cbxIncludeYear;
	private JCheckBox cbxIncludeFormat;
	private JCheckBox cbxIncludeQuality;
	private JCheckBox cbxIncludeSize;
	private JLabel lblOrder;
	private JComboBox<TextExportOrder> cbxOrder;
	private JCheckBox cbxIncludeViewed;
	
	public TextExportFrame(CCMovieList mlist, Component owner) {
		super();
		setSize(new Dimension(600, 620));
		movielist = mlist;

		initGUI();
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("TextExportFrame.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 256, 574, 290);
		getContentPane().add(scrollPane);
		
		memoResult = new JTextArea();
		memoResult.setEditable(false);
		memoResult.setTabSize(2);
		scrollPane.setViewportView(memoResult);
		
		cbFormat = new JComboBox<>();
		cbFormat.setModel(new DefaultComboBoxModel<>(new DatabaseTextExporter[] {
				new DatabasePlainTextExporter(),
				new DatabaseJSONExporter(),
				new DatabaseXMLExporter(),
		}));
		cbFormat.setBounds(66, 8, 200, 20);
		getContentPane().add(cbFormat);
		
		btnCreate = new JButton(LocaleBundle.getString("TextExportFrame.btnCreate.text")); //$NON-NLS-1$
		btnCreate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				start();
			}
		});
		btnCreate.setBounds(483, 218, 89, 23);
		getContentPane().add(btnCreate);
		
		btnExport = new JButton(LocaleBundle.getString("TextExportFrame.btnExport.text")); //$NON-NLS-1$
		btnExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		btnExport.setBounds(495, 557, 89, 23);
		getContentPane().add(btnExport);
		
		lblFormat = new JLabel(LocaleBundle.getString("TextExportFrame.lblFormat.text")); //$NON-NLS-1$
		lblFormat.setBounds(10, 11, 46, 14);
		getContentPane().add(lblFormat);
		
		cbxIncludeSeries = new JCheckBox(LocaleBundle.getString("TextExportFrame.cbxIncludeSeries.text")); //$NON-NLS-1$
		cbxIncludeSeries.setSelected(true);
		cbxIncludeSeries.setBounds(10, 57, 256, 23);
		getContentPane().add(cbxIncludeSeries);
		
		cbxIncludeLanguage = new JCheckBox(LocaleBundle.getString("TextExportFrame.cbxIncludeLanguage.text")); //$NON-NLS-1$
		cbxIncludeLanguage.setBounds(10, 83, 260, 23);
		getContentPane().add(cbxIncludeLanguage);
		
		cbxIncludeYear = new JCheckBox(LocaleBundle.getString("TextExportFrame.cbxIncludeYear.text")); //$NON-NLS-1$
		cbxIncludeYear.setSelected(true);
		cbxIncludeYear.setBounds(10, 109, 260, 23);
		getContentPane().add(cbxIncludeYear);
		
		cbxIncludeFormat = new JCheckBox(LocaleBundle.getString("TextExportFrame.cbxIncludeFormat.text")); //$NON-NLS-1$
		cbxIncludeFormat.setBounds(10, 135, 260, 23);
		getContentPane().add(cbxIncludeFormat);
		
		cbxIncludeQuality = new JCheckBox(LocaleBundle.getString("TextExportFrame.cbxIncludeQuality.text")); //$NON-NLS-1$
		cbxIncludeQuality.setBounds(10, 161, 260, 23);
		getContentPane().add(cbxIncludeQuality);
		
		cbxIncludeSize = new JCheckBox(LocaleBundle.getString("TextExportFrame.cbxIncludeSize.text")); //$NON-NLS-1$
		cbxIncludeSize.setBounds(10, 187, 256, 23);
		getContentPane().add(cbxIncludeSize);
		
		lblOrder = new JLabel(LocaleBundle.getString("TextExportFrame.lblOrder.text")); //$NON-NLS-1$
		lblOrder.setBounds(10, 36, 46, 14);
		getContentPane().add(lblOrder);
		
		cbxIncludeViewed = new JCheckBox(LocaleBundle.getString("TextExportFrame.cbxIncludeViewed.text")); //$NON-NLS-1$
		cbxIncludeViewed.setBounds(10, 213, 256, 23);
		getContentPane().add(cbxIncludeViewed);
		
		cbxOrder = new JComboBox<>();
		cbxOrder.setModel(new DefaultComboBoxModel<>(new TextExportOrder[] {
				TextExportOrder.TITLE,
				TextExportOrder.TITLE_SMART,
				TextExportOrder.ADD_DATE,
				TextExportOrder.YEAR,
		}));
		cbxOrder.setSelectedIndex(1);
		cbxOrder.setBounds(66, 33, 200, 20);
		getContentPane().add(cbxOrder);
	}
	
	private void start() {
		DatabaseTextExporter expo = (DatabaseTextExporter) cbFormat.getSelectedItem();
		
		String result = expo.generate(
				movielist,
				(TextExportOrder)cbxOrder.getSelectedItem(),
				cbxIncludeSeries.isSelected(), 
				cbxIncludeLanguage.isSelected(), 
				cbxIncludeYear.isSelected(), 
				cbxIncludeFormat.isSelected(), 
				cbxIncludeQuality.isSelected(), 
				cbxIncludeSize.isSelected(),
				cbxIncludeViewed.isSelected());
		
		memoResult.setText(result);
	}
	
	private void export() {
		DatabaseTextExporter expo = (DatabaseTextExporter) cbFormat.getSelectedItem();
		
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_txt.description", expo.getFileExtension())); //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));
		
		if (chooser.showSaveDialog(this)  == JFileChooser.APPROVE_OPTION) {
			start();
			
			try {
				TextFileUtils.writeTextFile(chooser.getSelectedFile(), memoResult.getText());
			} catch (IOException e) {
				CCLog.addError(e);
				dispose();
			}
		}
	}
}
