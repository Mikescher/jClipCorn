package de.jClipCorn.gui.frames.omniParserFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.guiComponents.VerticalScrollPaneSynchronizer;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.listener.OmniParserCallbackListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OmniParserFrame extends JCCDialog
{
	private boolean fullycreated = false;

	private OmniParserCallbackListener callbacklistener;
	private List<String> old_titles;
	private FSPath filechooserdirectory;

	public OmniParserFrame(Component owner, CCMovieList ml, OmniParserCallbackListener listener, List<String> oldtitles, FSPath chooserdir, String initial, boolean modal)
	{
		super(ml);

		this.callbacklistener = listener;
		this.old_titles = oldtitles;
		this.filechooserdirectory = chooserdir;

		initComponents();
		postInit(modal, initial);

		setLocationRelativeTo(owner);
	}

	private void postInit(boolean modal, String initial)
	{
		(new VerticalScrollPaneSynchronizer(scrlPnlFormattedText, scrlPnlParsedText)).start();
		setModal(modal);

		memoPlaintext.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::onPlainTextChanged));
		memoFormattedText.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::onFormattedTextChanged));
		memoParsedText.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::onParsedTextChanged));

		fullycreated = true;

		memoPlaintext.setText(initial);
	}

	private void onLoadFromFolder(ActionEvent evt) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setCurrentDirectory(filechooserdirectory.toFile());

		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();

			if (! f.isDirectory()) {
				f = f.getParentFile();
			}

			String[] list = f.list((dir, name) -> new File(dir, name).isDirectory());
			if (list == null) list = new String[0];

			StringBuilder result = new StringBuilder();

			for (int i = 0; i < list.length; i++) {
				if (i > 0) result.append(SimpleFileUtils.LINE_END);
				result.append(list[i]);
			}

			memoPlaintext.setText(result.toString());
		}
	}

	private void onLoadFromFiles(ActionEvent evt) {
		JFileChooser chooser = new JFileChooser() {
			private static final long serialVersionUID = -6544797024751874403L;

			@Override
			public void approveSelection() {
				if (getSelectedFile().isFile()) setSelectedFile(getSelectedFile().getParentFile());
				super.approveSelection();
			}
		};
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setApproveButtonText(LocaleBundle.getString("OmniParserFrame.folderchooser.okBtn")); //$NON-NLS-1$
		chooser.setCurrentDirectory(filechooserdirectory.toFile());

		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			var f = FSPath.create(chooser.getSelectedFile());

			if (! f.isDirectory()) f = f.getParent();

			FSPath[] list = f.list(FSPath::isFile).toArray(new FSPath[0]);

			StringBuilder result = new StringBuilder();

			for (int i = 0; i < list.length; i++) {
				if (i > 0) result.append(SimpleFileUtils.LINE_END);
				result.append(list[i].getFilenameWithoutExt());
			}

			memoPlaintext.setText(result.toString());
		}
	}

	private void loadFromClipBoard(ActionEvent evt) {
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transfer = sysClip.getContents(null);

		String data;
		try {
			data = (String) transfer.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException | IOException e) {
			return; // Can't load
		}

		memoPlaintext.setText(data);
	}

	private void onPlainTextChanged() {
		if (! fullycreated) return;
		memoFormattedText.setText(OmniTextFormatter.format(memoPlaintext.getText(), chckbxSplitLines.isSelected()));
	}

	private void onFormattedTextChanged() {
		if (! fullycreated) return;
		memoParsedText.setText(OmniTextParser.parse(memoFormattedText.getText(), chckbxRepUmlauts.isSelected(), chckbxRemInforStrings.isSelected(), chckbxRecogSpaceChars.isSelected(), chckbxRemRepStrings.isSelected()));
	}

	private void onParsedTextChanged() {
		if (! fullycreated) return;
		updateCompareTable();
	}

	private void onOK(ActionEvent e) {
		callbacklistener.updateTitles(getTitleList());
		dispose();
	}

	private void onCancel(ActionEvent e) {
		dispose();
	}

	private void updateCompareTable() {
		String input = memoParsedText.getText();

		List<String> list = new ArrayList<>(Arrays.asList(SimpleFileUtils.splitLines(input)));

		var model = new ArrayList<Tuple<String, String>>();

		for (int i = 0; i < old_titles.size(); i++) {
			var vOld = old_titles.get(i);
			var vNew = (i < list.size()) ? (list.get(i)) : (old_titles.get(i));
			model.add(Tuple.Create(vOld, vNew));
		}

		tableCompare.setData(model);
	}

	private List<String> getTitleList() {
		String input = memoParsedText.getText();

		List<String> r = new ArrayList<>(Arrays.asList(SimpleFileUtils.splitLines(input)));
		while (r.size() < old_titles.size()) r.add(Str.Empty);
		return r;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel1 = new JPanel();
		btnLoadFromFiles = new JButton();
		btnLoadFromFolder = new JButton();
		btnLoadFromClipboard = new JButton();
		panel3 = new JPanel();
		label1 = new JLabel();
		scrollPane1 = new JScrollPane();
		memoPlaintext = new JTextArea();
		panel4 = new JPanel();
		label2 = new JLabel();
		scrlPnlFormattedText = new JScrollPane();
		memoFormattedText = new JTextArea();
		panel5 = new JPanel();
		label3 = new JLabel();
		scrlPnlParsedText = new JScrollPane();
		memoParsedText = new JTextArea();
		panel6 = new JPanel();
		label4 = new JLabel();
		tableCompare = new OmniParserResultTable(this);
		panel7 = new JPanel();
		chckbxSplitLines = new JCheckBox();
		chckbxRepUmlauts = new JCheckBox();
		chckbxRemInforStrings = new JCheckBox();
		chckbxRecogSpaceChars = new JCheckBox();
		chckbxRemRepStrings = new JCheckBox();
		panel2 = new JPanel();
		btnOK = new JButton();
		btnCancel = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("OmniParserFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, 0dlu:grow(0.3), $lcgap, 0dlu:grow(0.4), $lcgap, 0dlu:grow(0.1), $lcgap, pref, $ugap", //$NON-NLS-1$
			"$ugap, default, 2*($lgap, 0dlu:grow), $lgap, default, $ugap")); //$NON-NLS-1$

		//======== panel1 ========
		{
			panel1.setLayout(new FormLayout(
				"2*(default, $lcgap), default", //$NON-NLS-1$
				"default")); //$NON-NLS-1$

			//---- btnLoadFromFiles ----
			btnLoadFromFiles.setText(LocaleBundle.getString("OmniParserFrame.btnLoadFromFiles.text")); //$NON-NLS-1$
			btnLoadFromFiles.addActionListener(e -> onLoadFromFiles(e));
			panel1.add(btnLoadFromFiles, CC.xy(1, 1));

			//---- btnLoadFromFolder ----
			btnLoadFromFolder.setText(LocaleBundle.getString("OmniParserFrame.btnLoadFromFolder.text")); //$NON-NLS-1$
			btnLoadFromFolder.addActionListener(e -> onLoadFromFolder(e));
			panel1.add(btnLoadFromFolder, CC.xy(3, 1));

			//---- btnLoadFromClipboard ----
			btnLoadFromClipboard.setText(LocaleBundle.getString("OmniParserFrame.btnLoadFromClipboard.text")); //$NON-NLS-1$
			btnLoadFromClipboard.addActionListener(e -> loadFromClipBoard(e));
			panel1.add(btnLoadFromClipboard, CC.xy(5, 1));
		}
		contentPane.add(panel1, CC.xywh(2, 2, 7, 1));

		//======== panel3 ========
		{
			panel3.setLayout(new FormLayout(
				"default:grow", //$NON-NLS-1$
				"default, $lgap, default:grow")); //$NON-NLS-1$

			//---- label1 ----
			label1.setText(LocaleBundle.getString("OmniParserFrame.labelStep1.text")); //$NON-NLS-1$
			label1.setHorizontalAlignment(SwingConstants.CENTER);
			panel3.add(label1, CC.xy(1, 1));

			//======== scrollPane1 ========
			{

				//---- memoPlaintext ----
				memoPlaintext.setLineWrap(true);
				scrollPane1.setViewportView(memoPlaintext);
			}
			panel3.add(scrollPane1, CC.xy(1, 3, CC.FILL, CC.FILL));
		}
		contentPane.add(panel3, CC.xywh(2, 4, 1, 3, CC.FILL, CC.FILL));

		//======== panel4 ========
		{
			panel4.setLayout(new FormLayout(
				"default:grow", //$NON-NLS-1$
				"default, $lgap, default:grow")); //$NON-NLS-1$

			//---- label2 ----
			label2.setText(LocaleBundle.getString("OmniParserFrame.labelStep2.text")); //$NON-NLS-1$
			label2.setHorizontalAlignment(SwingConstants.CENTER);
			panel4.add(label2, CC.xy(1, 1));

			//======== scrlPnlFormattedText ========
			{
				scrlPnlFormattedText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrlPnlFormattedText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrlPnlFormattedText.setViewportView(memoFormattedText);
			}
			panel4.add(scrlPnlFormattedText, CC.xy(1, 3, CC.FILL, CC.FILL));
		}
		contentPane.add(panel4, CC.xywh(4, 4, 3, 1, CC.FILL, CC.FILL));

		//======== panel5 ========
		{
			panel5.setLayout(new FormLayout(
				"default:grow", //$NON-NLS-1$
				"default, $lgap, default:grow")); //$NON-NLS-1$

			//---- label3 ----
			label3.setText(LocaleBundle.getString("OmniParserFrame.labelStep3.text")); //$NON-NLS-1$
			label3.setHorizontalAlignment(SwingConstants.CENTER);
			panel5.add(label3, CC.xy(1, 1));

			//======== scrlPnlParsedText ========
			{
				scrlPnlParsedText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrlPnlParsedText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrlPnlParsedText.setViewportView(memoParsedText);
			}
			panel5.add(scrlPnlParsedText, CC.xy(1, 3, CC.FILL, CC.FILL));
		}
		contentPane.add(panel5, CC.xy(8, 4, CC.FILL, CC.FILL));

		//======== panel6 ========
		{
			panel6.setLayout(new FormLayout(
				"default:grow", //$NON-NLS-1$
				"default, $lgap, default:grow")); //$NON-NLS-1$

			//---- label4 ----
			label4.setText(LocaleBundle.getString("OmniParserFrame.labelStep4.text")); //$NON-NLS-1$
			label4.setHorizontalAlignment(SwingConstants.CENTER);
			panel6.add(label4, CC.xy(1, 1));
			panel6.add(tableCompare, CC.xy(1, 3, CC.FILL, CC.FILL));
		}
		contentPane.add(panel6, CC.xywh(4, 6, 3, 1, CC.FILL, CC.FILL));

		//======== panel7 ========
		{
			panel7.setBorder(new TitledBorder(LocaleBundle.getString("OmniParserFrame.labelOptions.text"))); //$NON-NLS-1$
			panel7.setLayout(new FormLayout(
				"$lcgap, default:grow, $lcgap", //$NON-NLS-1$
				"4*(default, $lgap), default")); //$NON-NLS-1$

			//---- chckbxSplitLines ----
			chckbxSplitLines.setText(LocaleBundle.getString("OmniParserFrame.chkbxSplit.text")); //$NON-NLS-1$
			chckbxSplitLines.setSelected(true);
			chckbxSplitLines.addChangeListener(e -> onPlainTextChanged());
			panel7.add(chckbxSplitLines, CC.xy(2, 1));

			//---- chckbxRepUmlauts ----
			chckbxRepUmlauts.setText(LocaleBundle.getString("OmniParserFrame.chkbxReplUmlauts.text")); //$NON-NLS-1$
			chckbxRepUmlauts.addChangeListener(e -> onFormattedTextChanged());
			panel7.add(chckbxRepUmlauts, CC.xy(2, 3));

			//---- chckbxRemInforStrings ----
			chckbxRemInforStrings.setText(LocaleBundle.getString("OmniParserFrame.chkbxRemInfoStr.text")); //$NON-NLS-1$
			chckbxRemInforStrings.setSelected(true);
			chckbxRemInforStrings.addChangeListener(e -> onFormattedTextChanged());
			panel7.add(chckbxRemInforStrings, CC.xy(2, 5));

			//---- chckbxRecogSpaceChars ----
			chckbxRecogSpaceChars.setText(LocaleBundle.getString("OmniParserFrame.chkbxRecogSpace.text")); //$NON-NLS-1$
			chckbxRecogSpaceChars.setSelected(true);
			chckbxRecogSpaceChars.addChangeListener(e -> onFormattedTextChanged());
			panel7.add(chckbxRecogSpaceChars, CC.xy(2, 7));

			//---- chckbxRemRepStrings ----
			chckbxRemRepStrings.setText(LocaleBundle.getString("OmniParserFrame.chkbxRemRepPhrases.text")); //$NON-NLS-1$
			chckbxRemRepStrings.setSelected(true);
			chckbxRemRepStrings.addChangeListener(e -> onFormattedTextChanged());
			panel7.add(chckbxRemRepStrings, CC.xy(2, 9));
		}
		contentPane.add(panel7, CC.xy(8, 6, CC.DEFAULT, CC.FILL));

		//======== panel2 ========
		{
			panel2.setLayout(new FormLayout(
				"0dlu:grow, default, $lcgap, default, 0dlu:grow", //$NON-NLS-1$
				"default")); //$NON-NLS-1$

			//---- btnOK ----
			btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
			btnOK.addActionListener(e -> onOK(e));
			panel2.add(btnOK, CC.xy(2, 1));

			//---- btnCancel ----
			btnCancel.setText(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
			btnCancel.addActionListener(e -> onCancel(e));
			panel2.add(btnCancel, CC.xy(4, 1));
		}
		contentPane.add(panel2, CC.xywh(2, 8, 7, 1));
		setSize(1100, 570);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panel1;
	private JButton btnLoadFromFiles;
	private JButton btnLoadFromFolder;
	private JButton btnLoadFromClipboard;
	private JPanel panel3;
	private JLabel label1;
	private JScrollPane scrollPane1;
	private JTextArea memoPlaintext;
	private JPanel panel4;
	private JLabel label2;
	private JScrollPane scrlPnlFormattedText;
	private JTextArea memoFormattedText;
	private JPanel panel5;
	private JLabel label3;
	private JScrollPane scrlPnlParsedText;
	private JTextArea memoParsedText;
	private JPanel panel6;
	private JLabel label4;
	private OmniParserResultTable tableCompare;
	private JPanel panel7;
	private JCheckBox chckbxSplitLines;
	private JCheckBox chckbxRepUmlauts;
	private JCheckBox chckbxRemInforStrings;
	private JCheckBox chckbxRecogSpaceChars;
	private JCheckBox chckbxRemRepStrings;
	private JPanel panel2;
	private JButton btnOK;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
