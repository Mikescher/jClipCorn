package de.jClipCorn.gui.frames.exportElementsFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.DialogHelper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ExportElementsFrame extends JCCFrame {
	private static final long serialVersionUID = 1568672663044965879L;

	private static ExportElementsFrame instance = null;

	private DefaultListModel<CCDatabaseElement> lsModel;

	public ExportElementsFrame(Component owner, CCMovieList ml) {
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit() {
		setType(Type.UTILITY);

		lsModel = new DefaultListModel<>();
		lsElements.setModel(lsModel);
	}

	private void onExport() {
		final List<CCDatabaseElement> list = new ArrayList<>();

		for (int i = 0; i < lsModel.size(); i++) {
			list.add(lsModel.get(i));
		}

		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jmccexport.description", ExportHelper.EXTENSION_MULTIPLEEXPORT)); //$NON-NLS-1$
		chooser.setCurrentDirectory(FilesystemUtils.getRealSelfDirectory().toFile());

		int returnval = chooser.showSaveDialog(this);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			final boolean includeCover = 0 == DialogHelper.showLocaleOptions(this, "ExportHelper.dialogs.exportCover"); //$NON-NLS-1$

			var f = FSPath.create(chooser.getSelectedFile()).forceExtension(ExportHelper.EXTENSION_MULTIPLEEXPORT);

			new Thread(() -> ExportHelper.exportDBElements(f, list, includeCover, true), "THREAD_EXPORT_JMCCEXPORT").start(); //$NON-NLS-1$
		}
	}

	public void addElement(CCDatabaseElement el) {
		lsModel.addElement(el);
	}

	public static ExportElementsFrame getVisibleInstance(Component owner, CCMovieList ml) {
		if (instance == null) {
			instance = new ExportElementsFrame(owner, ml);
			instance.setVisible(true);
			return instance;
		}

		if (! instance.isVisible()) {
			instance.dispose();
			instance = new ExportElementsFrame(owner, ml);
			instance.setVisible(true);
			return instance;
		}

		if (instance.movielist != ml) {
			instance.dispose();
			instance = new ExportElementsFrame(owner, ml);
			instance.setVisible(true);
			return instance;
		}

		return instance;
	}

	public static void addElementToList(Component owner, CCDatabaseElement el) {
		getVisibleInstance(owner, el.getMovieList()).addElement(el);
	}

	public static void clearAndDispose() {
		if (instance != null) {
			instance.dispose();
			instance = null;
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		scrollPane = new JScrollPane();
		lsElements = new JList<>();
		pnlBottom = new JPanel();
		btnExport = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("ExportElementsFrame.this.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(250, 300));
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $ugap",
			"$ugap, default:grow, $lgap, default, $ugap"));

		//======== scrollPane ========
		{
			scrollPane.setViewportView(lsElements);
		}
		contentPane.add(scrollPane, CC.xy(2, 2, CC.FILL, CC.FILL));

		//======== pnlBottom ========
		{
			pnlBottom.setLayout(new FlowLayout());

			//---- btnExport ----
			btnExport.setText(LocaleBundle.getString("ExportElementsFrame.btnExport.caption"));
			btnExport.addActionListener(e -> onExport());
			pnlBottom.add(btnExport);
		}
		contentPane.add(pnlBottom, CC.xy(2, 4, CC.FILL, CC.DEFAULT));
		setSize(300, 350);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollPane scrollPane;
	private JList<CCDatabaseElement> lsElements;
	private JPanel pnlBottom;
	private JButton btnExport;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
