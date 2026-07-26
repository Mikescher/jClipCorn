package de.jClipCorn.gui.frames.textExportFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.filesystem.FilesystemUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TextExportFrame extends JCCFrame {
	private static final long serialVersionUID = -807033167837187549L;

	public TextExportFrame(CCMovieList mlist, Component owner) {
		super(mlist);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit() {
		cbFormat.setModel(new DefaultComboBoxModel<>(new DatabaseTextExporter[] {
				new DatabasePlainTextExporter(),
				new DatabaseJSONExporter(),
				new DatabaseXMLExporter(),
		}));

		cbxOrder.setModel(new DefaultComboBoxModel<>(new TextExportOrder[] {
				TextExportOrder.TITLE,
				TextExportOrder.TITLE_SMART,
				TextExportOrder.ADD_DATE,
				TextExportOrder.YEAR,
		}));
		cbxOrder.setSelectedIndex(1);
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
		if (expo == null) return;

		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_txt.description", expo.getFileExtension())); //$NON-NLS-1$
		chooser.setCurrentDirectory(FilesystemUtils.getRealSelfDirectory().toFile());

		if (chooser.showSaveDialog(this)  == JFileChooser.APPROVE_OPTION) {
			start();

			try {
				FSPath.create(chooser.getSelectedFile()).writeAsUTF8TextFile(memoResult.getText());
			} catch (IOException e) {
				CCLog.addError(e);
				dispose();
			}
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        pnlOptions = new JPanel();
        lblFormat = new JLabel();
        cbFormat = new JComboBox<>();
        lblOrder = new JLabel();
        cbxOrder = new JComboBox<>();
        cbxIncludeSeries = new JCheckBox();
        cbxIncludeLanguage = new JCheckBox();
        cbxIncludeYear = new JCheckBox();
        cbxIncludeFormat = new JCheckBox();
        cbxIncludeQuality = new JCheckBox();
        cbxIncludeSize = new JCheckBox();
        cbxIncludeViewed = new JCheckBox();
        btnCreate = new JButton();
        scrollPane = new JScrollPane();
        memoResult = new JTextArea();
        btnExport = new JButton();

        //======== this ========
        setTitle(LocaleBundle.getString("TextExportFrame.this.title")); //$NON-NLS-1$
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        var contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$ugap, default:grow, $ugap", //$NON-NLS-1$
            "$ugap, default, $lgap, default:grow, $lgap, default, $ugap")); //$NON-NLS-1$

        //======== pnlOptions ========
        {
            pnlOptions.setLayout(new FormLayout(
                "default, $lcgap, default:grow, $ugap, default", //$NON-NLS-1$
                "default, $lgap, default, $ugap, 7*(default)")); //$NON-NLS-1$

            //---- lblFormat ----
            lblFormat.setText(LocaleBundle.getString("TextExportFrame.lblFormat.text")); //$NON-NLS-1$
            pnlOptions.add(lblFormat, CC.xy(1, 1));
            pnlOptions.add(cbFormat, CC.xy(3, 1, CC.FILL, CC.DEFAULT));

            //---- lblOrder ----
            lblOrder.setText(LocaleBundle.getString("TextExportFrame.lblOrder.text")); //$NON-NLS-1$
            pnlOptions.add(lblOrder, CC.xy(1, 3));
            pnlOptions.add(cbxOrder, CC.xy(3, 3, CC.FILL, CC.DEFAULT));

            //---- cbxIncludeSeries ----
            cbxIncludeSeries.setText(LocaleBundle.getString("TextExportFrame.cbxIncludeSeries.text")); //$NON-NLS-1$
            cbxIncludeSeries.setSelected(true);
            pnlOptions.add(cbxIncludeSeries, CC.xywh(1, 5, 3, 1, CC.LEFT, CC.DEFAULT));

            //---- cbxIncludeLanguage ----
            cbxIncludeLanguage.setText(LocaleBundle.getString("TextExportFrame.cbxIncludeLanguage.text")); //$NON-NLS-1$
            pnlOptions.add(cbxIncludeLanguage, CC.xywh(1, 6, 3, 1, CC.LEFT, CC.DEFAULT));

            //---- cbxIncludeYear ----
            cbxIncludeYear.setText(LocaleBundle.getString("TextExportFrame.cbxIncludeYear.text")); //$NON-NLS-1$
            cbxIncludeYear.setSelected(true);
            pnlOptions.add(cbxIncludeYear, CC.xywh(1, 7, 3, 1, CC.LEFT, CC.DEFAULT));

            //---- cbxIncludeFormat ----
            cbxIncludeFormat.setText(LocaleBundle.getString("TextExportFrame.cbxIncludeFormat.text")); //$NON-NLS-1$
            pnlOptions.add(cbxIncludeFormat, CC.xywh(1, 8, 3, 1, CC.LEFT, CC.DEFAULT));

            //---- cbxIncludeQuality ----
            cbxIncludeQuality.setText(LocaleBundle.getString("TextExportFrame.cbxIncludeQuality.text")); //$NON-NLS-1$
            pnlOptions.add(cbxIncludeQuality, CC.xywh(1, 9, 3, 1, CC.LEFT, CC.DEFAULT));

            //---- cbxIncludeSize ----
            cbxIncludeSize.setText(LocaleBundle.getString("TextExportFrame.cbxIncludeSize.text")); //$NON-NLS-1$
            pnlOptions.add(cbxIncludeSize, CC.xywh(1, 10, 3, 1, CC.LEFT, CC.DEFAULT));

            //---- cbxIncludeViewed ----
            cbxIncludeViewed.setText(LocaleBundle.getString("TextExportFrame.cbxIncludeViewed.text")); //$NON-NLS-1$
            pnlOptions.add(cbxIncludeViewed, CC.xywh(1, 11, 3, 1, CC.LEFT, CC.DEFAULT));

            //---- btnCreate ----
            btnCreate.setText(LocaleBundle.getString("TextExportFrame.btnCreate.text")); //$NON-NLS-1$
            btnCreate.addActionListener(e -> start());
            pnlOptions.add(btnCreate, CC.xywh(5, 5, 1, 7, CC.DEFAULT, CC.BOTTOM));
        }
        contentPane.add(pnlOptions, CC.xy(2, 2, CC.FILL, CC.DEFAULT));

        //======== scrollPane ========
        {

            //---- memoResult ----
            memoResult.setEditable(false);
            memoResult.setTabSize(2);
            scrollPane.setViewportView(memoResult);
        }
        contentPane.add(scrollPane, CC.xy(2, 4, CC.FILL, CC.FILL));

        //---- btnExport ----
        btnExport.setText(LocaleBundle.getString("TextExportFrame.btnExport.text")); //$NON-NLS-1$
        btnExport.addActionListener(e -> export());
        contentPane.add(btnExport, CC.xy(2, 6, CC.RIGHT, CC.DEFAULT));
        setSize(600, 620);
        setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel pnlOptions;
    private JLabel lblFormat;
    private JComboBox<DatabaseTextExporter> cbFormat;
    private JLabel lblOrder;
    private JComboBox<TextExportOrder> cbxOrder;
    private JCheckBox cbxIncludeSeries;
    private JCheckBox cbxIncludeLanguage;
    private JCheckBox cbxIncludeYear;
    private JCheckBox cbxIncludeFormat;
    private JCheckBox cbxIncludeQuality;
    private JCheckBox cbxIncludeSize;
    private JCheckBox cbxIncludeViewed;
    private JButton btnCreate;
    private JScrollPane scrollPane;
    private JTextArea memoResult;
    private JButton btnExport;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
