package de.jClipCorn.gui.frames.exportElementsFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.FileChooserHelper;

public class ExportElementsFrame extends JFrame {
	private static final long serialVersionUID = 1568672663044965879L;
	
	private static ExportElementsFrame instance = null;
	
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JList<CCDatabaseElement> lsElements;
	private DefaultListModel<CCDatabaseElement> lsModel;
	private JPanel pnlBottom;
	private JButton btnExport;
	
	public ExportElementsFrame(Component owner) {
		super();
		initGUI();
		setLocationRelativeTo(owner);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("ExportElementsFrame.this.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		lsElements = new JList<>();
		lsElements.setModel(lsModel = new DefaultListModel<>());
		scrollPane.setViewportView(lsElements);
		
		pnlBottom = new JPanel();
		contentPane.add(pnlBottom, BorderLayout.SOUTH);
		
		btnExport = new JButton(LocaleBundle.getString("ExportElementsFrame.btnExport.caption")); //$NON-NLS-1$
		btnExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onExport();
			}
		});
		pnlBottom.add(btnExport);
		
		setMinimumSize(new Dimension(250, 300));
		setSize(new Dimension(300, 350));
	}
	
	private void onExport() {
		final List<CCDatabaseElement> list = new ArrayList<>();
		
		for (int i = 0; i < lsModel.size(); i++) {
			list.add(lsModel.get(i));
		}
		
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(FileChooserHelper.createLocalFileFilter("ExportHelper.filechooser_jmccexport.description", ExportHelper.EXTENSION_MULTIPLEEXPORT)); //$NON-NLS-1$
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		int returnval = chooser.showSaveDialog(this);

		if (returnval == JFileChooser.APPROVE_OPTION) {
			final boolean includeCover = 0 == DialogHelper.showLocaleOptions(this, "ExportHelper.dialogs.exportCover"); //$NON-NLS-1$

			File f = PathFormatter.forceExtension(chooser.getSelectedFile(), ExportHelper.EXTENSION_MULTIPLEEXPORT);

			new Thread(() -> ExportHelper.exportDBElements(f, list, includeCover, true), "THREAD_EXPORT_JMCCEXPORT").start(); //$NON-NLS-1$
		}
	}
	
	public void addElement(CCDatabaseElement el) {
		lsModel.addElement(el);
	}
	
	public static ExportElementsFrame getVisibleInstance(Component owner) {
		if (instance == null) {
			instance = new ExportElementsFrame(owner);
			instance.setVisible(true);
			return instance;
		}
		
		if (! instance.isVisible()) {
			instance.dispose();
			instance = new ExportElementsFrame(owner);
			instance.setVisible(true);
			return instance;
		}
		
		return instance;
	}
	
	public static void addElementToList(Component owner, CCDatabaseElement el) {
		getVisibleInstance(owner).addElement(el);
	}
	
	public static void clearAndDispose() {
		if (instance != null) {
			instance.dispose();
			instance = null;
		}
	}
}
