package de.jClipCorn.gui.frames.omniParserFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.gui.guiComponents.DefaultReadOnlyTableModel;
import de.jClipCorn.gui.guiComponents.VerticalScrollPaneSynchronizer;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.listener.OmniParserCallbackListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OmniParserFrame extends JDialog {
	private static final long serialVersionUID = -4511912406011331076L;
	
	private final static String[] COLUMN_HEADERS = {LocaleBundle.getString("OmniParserFrame.Header.title_A"), LocaleBundle.getString("OmniParserFrame.Header.title_B")};  //$NON-NLS-1$//$NON-NLS-2$
	
	private boolean fullycreated = false;
	
	private OmniParserCallbackListener callbacklistener;
	private List<String> old_titles;
	private FSPath filechooserdirectory;
	
	private JPanel contentPane;
	private JTextArea memoFormattedText;
	private JTextArea memoParsedText;
	private JScrollPane scrlPnlFormattedText;
	private JScrollPane scrlPnlParsedText;
	private JScrollPane scrlPnlCheck;
	private JTextArea memoPlaintext;
	private JScrollPane scrlPnlPlainText;
	private JPanel pnlBottom;
	private JButton btnOK;
	private JButton btnCancel;
	private JPanel pnlTop;
	private JButton btnLoadFromFiles;
	private JButton btnLoadFromFolder;
	private JButton btnLoadFromClipboard;
	private JPanel pnlOptions;
	private JCheckBox chckbxRepUmlauts;
	private JCheckBox chckbxRemInforStrings;
	private JCheckBox chckbxRecogSpaceChars;
	private JCheckBox chckbxSplitLines;
	private JCheckBox chckbxRemRepStrings;
	private JTable tableCompare;
	private JPanel pnlPlainText;
	private JLabel lblPlaintext;
	private JPanel pnlFormattedText;
	private JPanel pnlParsedText;
	private JPanel pnlCheck;
	private JLabel lblFormattedText;
	private JLabel lblParsedText;
	private JLabel lblCheck;

	public OmniParserFrame(Component owner, OmniParserCallbackListener listener, List<String> oldtitles, FSPath chooserdir, String initial, boolean modal) {
		super();
		
		this.callbacklistener = listener;
		this.old_titles = oldtitles;
		this.filechooserdirectory = chooserdir;
			
		initGUI();
		(new VerticalScrollPaneSynchronizer(scrlPnlFormattedText, scrlPnlParsedText)).start();
		
		setLocationRelativeTo(owner);
		setModal(modal);
		
		fullycreated = true;

		memoPlaintext.setText(initial);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("OmniParserFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setResizable(true);
		setBounds(100, 100, 891, 531);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("min:grow(3)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("min:grow(4)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("min:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("70dlu:grow(2)"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.MIN_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("125dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.MIN_ROWSPEC,}));
		
		pnlTop = new JPanel();
		FlowLayout fl_pnlTop = (FlowLayout) pnlTop.getLayout();
		fl_pnlTop.setVgap(0);
		fl_pnlTop.setAlignment(FlowLayout.LEFT);
		contentPane.add(pnlTop, "2, 2, 7, 1, fill, fill"); //$NON-NLS-1$
		
		btnLoadFromFiles = new JButton(LocaleBundle.getString("OmniParserFrame.btnLoadFromFiles.text")); //$NON-NLS-1$
		btnLoadFromFiles.addActionListener(e -> onLoadFromFiles());
		pnlTop.add(btnLoadFromFiles);
		
		btnLoadFromFolder = new JButton(LocaleBundle.getString("OmniParserFrame.btnLoadFromFolder.text")); //$NON-NLS-1$
		btnLoadFromFolder.addActionListener(e -> onLoadFromFolder());
		pnlTop.add(btnLoadFromFolder);
		
		btnLoadFromClipboard = new JButton(LocaleBundle.getString("OmniParserFrame.btnLoadFromClipboard.text")); //$NON-NLS-1$
		btnLoadFromClipboard.addActionListener(e -> loadFromClipBoard());
		pnlTop.add(btnLoadFromClipboard);
		
		pnlPlainText = new JPanel();
		contentPane.add(pnlPlainText, "2, 4, 1, 3, fill, fill"); //$NON-NLS-1$
		pnlPlainText.setLayout(new BorderLayout(0, 0));
		
		scrlPnlPlainText = new JScrollPane();
		pnlPlainText.add(scrlPnlPlainText);
		
		memoPlaintext = new JTextArea();
		memoPlaintext.setLineWrap(true);
		memoPlaintext.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::onPlainTextChanged));
		scrlPnlPlainText.setViewportView(memoPlaintext);
		
		lblPlaintext = new JLabel(LocaleBundle.getString("OmniParserFrame.labelStep1.text")); //$NON-NLS-1$
		lblPlaintext.setHorizontalAlignment(SwingConstants.CENTER);
		pnlPlainText.add(lblPlaintext, BorderLayout.NORTH);
		
		pnlFormattedText = new JPanel();
		contentPane.add(pnlFormattedText, "4, 4, 3, 1, fill, fill"); //$NON-NLS-1$
		pnlFormattedText.setLayout(new BorderLayout(0, 0));
		
		scrlPnlFormattedText = new JScrollPane();
		pnlFormattedText.add(scrlPnlFormattedText);
		scrlPnlFormattedText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrlPnlFormattedText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		memoFormattedText = new JTextArea();
		memoFormattedText.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::onFormattedTextChanged));
		scrlPnlFormattedText.setViewportView(memoFormattedText);
		
		pnlParsedText = new JPanel();
		contentPane.add(pnlParsedText, "8, 4, fill, fill"); //$NON-NLS-1$
		pnlParsedText.setLayout(new BorderLayout(0, 0));
		
		scrlPnlParsedText = new JScrollPane();
		pnlParsedText.add(scrlPnlParsedText);
		scrlPnlParsedText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrlPnlParsedText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		memoParsedText = new JTextArea();
		memoParsedText.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::onParsedTextChanged));
		scrlPnlParsedText.setViewportView(memoParsedText);
		
		pnlCheck = new JPanel();
		contentPane.add(pnlCheck, "4, 6, fill, fill"); //$NON-NLS-1$
		pnlCheck.setLayout(new BorderLayout(0, 0));
		
		scrlPnlCheck = new JScrollPane();
		pnlCheck.add(scrlPnlCheck);
		
		tableCompare = new JTable();
		tableCompare.setFillsViewportHeight(true);
		tableCompare.setEnabled(true);
		scrlPnlCheck.setViewportView(tableCompare);
		
		lblCheck = new JLabel(LocaleBundle.getString("OmniParserFrame.labelStep4.text")); //$NON-NLS-1$
		lblCheck.setHorizontalAlignment(SwingConstants.CENTER);
		pnlCheck.add(lblCheck, BorderLayout.NORTH);
		
		pnlOptions = new JPanel();
		pnlOptions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), LocaleBundle.getString("OmniParserFrame.labelOptions.text"), TitledBorder.CENTER, TitledBorder.TOP, null, null)); //$NON-NLS-1$ //$NON-NLS-2$
		contentPane.add(pnlOptions, "6, 6, 3, 1, fill, fill"); //$NON-NLS-1$
		pnlOptions.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.MIN_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		chckbxSplitLines = new JCheckBox(LocaleBundle.getString("OmniParserFrame.chkbxSplit.text")); //$NON-NLS-1$
		chckbxSplitLines.setSelected(true);
		chckbxSplitLines.addItemListener(e -> onPlainTextChanged());
		pnlOptions.add(chckbxSplitLines, "2, 2"); //$NON-NLS-1$
		
		chckbxRepUmlauts = new JCheckBox(LocaleBundle.getString("OmniParserFrame.chkbxReplUmlauts.text")); //$NON-NLS-1$
		chckbxRepUmlauts.addItemListener(e -> onFormattedTextChanged());
		pnlOptions.add(chckbxRepUmlauts, "2, 4"); //$NON-NLS-1$
		
		chckbxRemInforStrings = new JCheckBox(LocaleBundle.getString("OmniParserFrame.chkbxRemInfoStr.text")); //$NON-NLS-1$
		chckbxRemInforStrings.addItemListener(e -> onFormattedTextChanged());
		chckbxRemInforStrings.setSelected(true);
		pnlOptions.add(chckbxRemInforStrings, "2, 6"); //$NON-NLS-1$
		
		chckbxRecogSpaceChars = new JCheckBox(LocaleBundle.getString("OmniParserFrame.chkbxRecogSpace.text")); //$NON-NLS-1$
		chckbxRecogSpaceChars.addItemListener(e -> onFormattedTextChanged());
		chckbxRecogSpaceChars.setSelected(true);
		pnlOptions.add(chckbxRecogSpaceChars, "2, 8"); //$NON-NLS-1$
		
		chckbxRemRepStrings = new JCheckBox(LocaleBundle.getString("OmniParserFrame.chkbxRemRepPhrases.text")); //$NON-NLS-1$
		chckbxRemRepStrings.setSelected(true);
		chckbxRemRepStrings.addItemListener(e -> onFormattedTextChanged());
		pnlOptions.add(chckbxRemRepStrings, "2, 10"); //$NON-NLS-1$
		
		pnlBottom = new JPanel();
		contentPane.add(pnlBottom, "2, 8, 7, 1, fill, fill"); //$NON-NLS-1$
		
		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(e -> { callbacklistener.updateTitles(getTitleList()); dispose(); });
		pnlBottom.add(btnOK);
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(e -> dispose());
		pnlBottom.add(btnCancel);
		
		lblParsedText = new JLabel(LocaleBundle.getString("OmniParserFrame.labelStep3.text")); //$NON-NLS-1$
		lblParsedText.setHorizontalAlignment(SwingConstants.CENTER);
		pnlParsedText.add(lblParsedText, BorderLayout.NORTH);
		
		lblFormattedText = new JLabel(LocaleBundle.getString("OmniParserFrame.labelStep2.text")); //$NON-NLS-1$
		lblFormattedText.setHorizontalAlignment(SwingConstants.CENTER);
		pnlFormattedText.add(lblFormattedText, BorderLayout.NORTH);
	}
	
	private void onLoadFromFolder() {
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
	
	private void onLoadFromFiles() {
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
	
	private void loadFromClipBoard() {
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
	
	private void updateCompareTable() {
		String input = memoParsedText.getText();

		List<String> list = new ArrayList<>(Arrays.asList(SimpleFileUtils.splitLines(input)));
		
		DefaultTableModel model = new DefaultReadOnlyTableModel(old_titles.size(), 2);
		model.setColumnIdentifiers(COLUMN_HEADERS);
		
		for (int i = 0; i < old_titles.size(); i++) {
			model.setValueAt(old_titles.get(i), i, 0);
			model.setValueAt((i < list.size()) ? (list.get(i)) : (old_titles.get(i)), i, 1);
		}
		
		tableCompare.setModel(model);
	}
	
	private List<String> getTitleList() {
		String input = memoParsedText.getText();
		
		List<String> r = new ArrayList<>(Arrays.asList(SimpleFileUtils.splitLines(input)));
		while (r.size() < old_titles.size()) r.add(Str.Empty);
		return r;
	}
}