package de.jClipCorn.gui.frames.compareDatabaseFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SimpleFileUtils;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.DoubleProgressCallbackProgressBarHelper;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class CompareDatabaseFrame extends JFrame
{
	private final CCMovieList movielist;

	private CompareState currState = null;
	private Thread activeThread = null;

	public CompareDatabaseFrame(Component owner, CCMovieList ml)
	{
		super();

		movielist = ml;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle(LocaleBundle.getString("CompareDatabaseFrame.this.title")); //$NON-NLS-1$

		edDatabaseName.setText(CCProperties.getInstance().PROP_DATABASE_NAME.getDefault());

		try {
			edRules.setText(SimpleFileUtils.readTextResource("/compare_rules_example.txt", this.getClass())); //$NON-NLS-1$
		} catch (IOException e) {
			CCLog.addError(e);
		}

		updateUI();
	}

	private void updateUI()
	{
		var running = (activeThread != null);

		btnCompare.setEnabled(!running && !Str.isNullOrWhitespace(edDatabasePath.getText()));
		btnCreatePatch.setEnabled(!running && currState != null);
		pnlTabs.setEnabled(!running);

		edDatabaseName.setEnabled(!running);
		edRules.setEnabled(!running);
		edDatabasePath.setEnabled(!running);

		if (currState != null)
		{
			var it = CCStreams
					.<ComparisonMatch>empty()
					.append(CCStreams.iterate(currState.Movies).cast())
					.append(CCStreams.iterate(currState.Series).cast())
					.append(CCStreams.iterate(currState.AllSeasons).cast())
					.append(CCStreams.iterate(currState.AllEpisodes).cast());

			tableDeletedEntries.setData(it.filter(ComparisonMatch::getNeedsDelete).toList());
			tableUpdateCover   .setData(it.filter(ComparisonMatch::getNeedsUpdateCover).toList());
			tableUpdateFile    .setData(it.filter(ComparisonMatch::getNeedsUpdateFile).toList());
			tableUpdateMetadata.setData(it.filter(ComparisonMatch::getNeedsUpdateMetadata).toList());
			tableAddedEntry    .setData(it.filter(ComparisonMatch::getNeedsCreateNew).toList());
			tableUnchangedEntry.setData(it.filter(p -> !p.getNeedsAnything()).toList());

			tableDeletedEntries.autoResize();
			tableUpdateCover   .autoResize();
			tableUpdateFile    .autoResize();
			tableUpdateMetadata.autoResize();
			tableAddedEntry    .autoResize();
			tableUnchangedEntry.autoResize();

			pnlTabs.setTitleAt(0, LocaleBundle.getString("BatchEditFrame.tabDelete")           + " (" + tableDeletedEntries.getDataDirect().size() + ")");
			pnlTabs.setTitleAt(1, LocaleBundle.getString("BatchEditFrame.tabUpdateMeta")       + " (" + tableUpdateCover   .getDataDirect().size() + ")");
			pnlTabs.setTitleAt(2, LocaleBundle.getString("BatchEditFrame.tabUpdateCover")      + " (" + tableUpdateFile    .getDataDirect().size() + ")");
			pnlTabs.setTitleAt(3, LocaleBundle.getString("BatchEditFrame.tabUpdateFile")       + " (" + tableUpdateMetadata.getDataDirect().size() + ")");
			pnlTabs.setTitleAt(4, LocaleBundle.getString("BatchEditFrame.tabAddedEntries")     + " (" + tableAddedEntry    .getDataDirect().size() + ")");
			pnlTabs.setTitleAt(5, LocaleBundle.getString("BatchEditFrame.tabUnchangedEntries") + " (" + tableUnchangedEntry.getDataDirect().size() + ")");

			btnCreatePatch.setText(LocaleBundle.getString("BatchEditFrame.btnCreatePatch") + " (" + FileSizeFormatter.format(currState.estimatePatchSize()) + ")");
		}
		else
		{
			tableDeletedEntries.clearData();
			tableUpdateCover   .clearData();
			tableUpdateFile    .clearData();
			tableUpdateMetadata.clearData();
			tableAddedEntry    .clearData();
			tableUnchangedEntry.clearData();

			pnlTabs.setTitleAt(0, LocaleBundle.getString("BatchEditFrame.tabDelete"));
			pnlTabs.setTitleAt(1, LocaleBundle.getString("BatchEditFrame.tabUpdateMeta"));
			pnlTabs.setTitleAt(2, LocaleBundle.getString("BatchEditFrame.tabUpdateCover"));
			pnlTabs.setTitleAt(3, LocaleBundle.getString("BatchEditFrame.tabUpdateFile"));
			pnlTabs.setTitleAt(4, LocaleBundle.getString("BatchEditFrame.tabAddedEntries"));
			pnlTabs.setTitleAt(5, LocaleBundle.getString("BatchEditFrame.tabUnchangedEntries"));

			btnCreatePatch.setText(LocaleBundle.getString("BatchEditFrame.btnCreatePatch"));
		}

		edEntryDiff.setText(Str.Empty);

		progressBar1.setMaximum(1);progressBar1.setValue(0);lblProgress1.setText(Str.Empty);
		progressBar2.setMaximum(1);progressBar2.setValue(0);lblProgress2.setText(Str.Empty);

		btnCancel.setEnabled(activeThread != null);
	}

	private void openDatabase(ActionEvent e)
	{
		currState = null;

		final JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) { updateUI(); return; }

		edDatabasePath.setText(chooser.getSelectedFile().getAbsolutePath());
		updateUI();
	}

	private void startComparison(ActionEvent ae)
	{
		currState = null;

		var cb = new DoubleProgressCallbackProgressBarHelper(progressBar1, lblProgress1, progressBar2, lblProgress2);

		var rulestr = edRules.getText();

		activeThread = new Thread(() ->
		{
			try
			{
				var ruleset = CompareDatabaseRuleset.parse(rulestr);

				currState = CDFWorkerCompare.compare(cb, ruleset, edDatabasePath.getText(), edDatabaseName.getText(), movielist);

				SwingUtils.invokeLater(this::updateUI);
			}
			catch (Throwable e)
			{
				DialogHelper.showDispatchError(this, "Error", e.toString()); //$NON-NLS-1$
			}
			finally
			{
				activeThread = null;
				SwingUtils.invokeLater(this::updateUI);
				cb.reset();
			}
		});
		activeThread.start();

		updateUI();
	}

	public void showDiffStr(String diffStr) {
		edEntryDiff.setText(diffStr);
	}

	private void showRules(ActionEvent e) {
		GenericTextDialog.showEditableText(this, LocaleBundle.getString("BatchEditFrame.lblRules"), edRules.getText(), true, edRules::setText);
	}

	@SuppressWarnings("nls")
	private void startCreatingPatch(ActionEvent ae)
	{
		var cb = new DoubleProgressCallbackProgressBarHelper(progressBar1, lblProgress1, progressBar2, lblProgress2);
		var state = currState;

		if (state == null) return;
		if (activeThread != null) return;

		var fchooser = new JFileChooser(edDatabasePath.getText());
		fchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (fchooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

		var dir = fchooser.getSelectedFile().getAbsolutePath();

		var porcelain = cbPorcelain.isSelected(); // no expensive file copies - for testing...

		activeThread = new Thread(() ->
		{
			try
			{
				CDFWorkerPatch.createPatch(state, dir, cb, porcelain);
			}
			catch (Throwable e)
			{
				DialogHelper.showDispatchError(this, "Error", e.toString()); //$NON-NLS-1$
			}
			finally
			{
				activeThread = null;
				SwingUtils.invokeLater(this::updateUI);
				cb.reset();
			}
		});
		activeThread.start();

		updateUI();
	}

	@SuppressWarnings("deprecation")
	private void cancelThread(ActionEvent ae)
	{
		if (activeThread == null) { updateUI(); return; }

		activeThread.stop();
		updateUI();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		edDatabasePath = new ReadableTextField();
		btnOpenDatabase = new JButton();
		label2 = new JLabel();
		edDatabaseName = new JTextField();
		label1 = new JLabel();
		btnShowRules = new JButton();
		scrollPane1 = new JScrollPane();
		edRules = new JTextArea();
		btnCompare = new JButton();
		pnlTabs = new JTabbedPane();
		tabDeletedEntries = new JPanel();
		tableDeletedEntries = new ShowMatchesTable(this, false, true);
		tabUpdateMetadata = new JPanel();
		tableUpdateMetadata = new ShowMatchesTable(this, true, true);
		tabUpdateCover = new JPanel();
		tableUpdateCover = new ShowMatchesTable(this, true, true);
		tabUpdateFile = new JPanel();
		tableUpdateFile = new ShowMatchesTable(this, true, true);
		tabAddedEntry = new JPanel();
		tableAddedEntry = new ShowMatchesTable(this, true, false);
		tabUnchangedEntry = new JPanel();
		tableUnchangedEntry = new ShowMatchesTable(this, true, true);
		scrollPane2 = new JScrollPane();
		edEntryDiff = new JTextArea();
		btnCreatePatch = new JButton();
		btnCancel = new JButton();
		cbPorcelain = new JCheckBox();
		progressBar1 = new JProgressBar();
		lblProgress1 = new JLabel();
		progressBar2 = new JProgressBar();
		lblProgress2 = new JLabel();

		//======== this ========
		setTitle(LocaleBundle.getString("CompareDatabaseFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $lcgap, default:grow, $lcgap, default, $lcgap, 70dlu, $ugap", //$NON-NLS-1$
			"$ugap, 3*(default, $lgap), 80dlu, $lgap, 20dlu, $lgap, default:grow, $lgap, 80dlu, 3*($lgap, default), $ugap")); //$NON-NLS-1$
		contentPane.add(edDatabasePath, CC.xywh(2, 2, 5, 1, CC.FILL, CC.FILL));

		//---- btnOpenDatabase ----
		btnOpenDatabase.setText("..."); //$NON-NLS-1$
		btnOpenDatabase.addActionListener(e -> openDatabase(e));
		contentPane.add(btnOpenDatabase, CC.xy(8, 2));

		//---- label2 ----
		label2.setText(LocaleBundle.getString("BatchEditFrame.lblDBName")); //$NON-NLS-1$
		contentPane.add(label2, CC.xy(2, 4));
		contentPane.add(edDatabaseName, CC.xywh(4, 4, 3, 1));

		//---- label1 ----
		label1.setText(LocaleBundle.getString("BatchEditFrame.lblRules")); //$NON-NLS-1$
		contentPane.add(label1, CC.xywh(2, 6, 5, 1));

		//---- btnShowRules ----
		btnShowRules.setText("->"); //$NON-NLS-1$
		btnShowRules.addActionListener(e -> showRules(e));
		contentPane.add(btnShowRules, CC.xy(8, 6, CC.RIGHT, CC.DEFAULT));

		//======== scrollPane1 ========
		{

			//---- edRules ----
			edRules.setText("[from_ressources]"); //$NON-NLS-1$
			scrollPane1.setViewportView(edRules);
		}
		contentPane.add(scrollPane1, CC.xywh(2, 8, 7, 1, CC.FILL, CC.FILL));

		//---- btnCompare ----
		btnCompare.setText(LocaleBundle.getString("BatchEditFrame.btnCompare")); //$NON-NLS-1$
		btnCompare.addActionListener(e -> startComparison(e));
		contentPane.add(btnCompare, CC.xywh(2, 10, 7, 1));

		//======== pnlTabs ========
		{

			//======== tabDeletedEntries ========
			{
				tabDeletedEntries.setLayout(new BorderLayout());
				tabDeletedEntries.add(tableDeletedEntries, BorderLayout.CENTER);
			}
			pnlTabs.addTab(LocaleBundle.getString("BatchEditFrame.tabDelete"), tabDeletedEntries); //$NON-NLS-1$

			//======== tabUpdateMetadata ========
			{
				tabUpdateMetadata.setLayout(new BorderLayout());
				tabUpdateMetadata.add(tableUpdateMetadata, BorderLayout.CENTER);
			}
			pnlTabs.addTab(LocaleBundle.getString("BatchEditFrame.tabUpdateMeta"), tabUpdateMetadata); //$NON-NLS-1$

			//======== tabUpdateCover ========
			{
				tabUpdateCover.setLayout(new BorderLayout());
				tabUpdateCover.add(tableUpdateCover, BorderLayout.CENTER);
			}
			pnlTabs.addTab(LocaleBundle.getString("BatchEditFrame.tabUpdateCover"), tabUpdateCover); //$NON-NLS-1$

			//======== tabUpdateFile ========
			{
				tabUpdateFile.setLayout(new BorderLayout());
				tabUpdateFile.add(tableUpdateFile, BorderLayout.CENTER);
			}
			pnlTabs.addTab(LocaleBundle.getString("BatchEditFrame.tabUpdateFile"), tabUpdateFile); //$NON-NLS-1$

			//======== tabAddedEntry ========
			{
				tabAddedEntry.setLayout(new BorderLayout());
				tabAddedEntry.add(tableAddedEntry, BorderLayout.CENTER);
			}
			pnlTabs.addTab(LocaleBundle.getString("BatchEditFrame.tabAddedEntries"), tabAddedEntry); //$NON-NLS-1$

			//======== tabUnchangedEntry ========
			{
				tabUnchangedEntry.setLayout(new BorderLayout());
				tabUnchangedEntry.add(tableUnchangedEntry, BorderLayout.CENTER);
			}
			pnlTabs.addTab(LocaleBundle.getString("BatchEditFrame.tabUnchangedEntries"), tabUnchangedEntry); //$NON-NLS-1$
		}
		contentPane.add(pnlTabs, CC.xywh(2, 12, 7, 1, CC.FILL, CC.FILL));

		//======== scrollPane2 ========
		{

			//---- edEntryDiff ----
			edEntryDiff.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			edEntryDiff.setEditable(false);
			scrollPane2.setViewportView(edEntryDiff);
		}
		contentPane.add(scrollPane2, CC.xywh(2, 14, 7, 1, CC.DEFAULT, CC.FILL));

		//---- btnCreatePatch ----
		btnCreatePatch.setText(LocaleBundle.getString("BatchEditFrame.btnCreatePatch")); //$NON-NLS-1$
		btnCreatePatch.addActionListener(e -> startCreatingPatch(e));
		contentPane.add(btnCreatePatch, CC.xywh(2, 16, 3, 1));

		//---- btnCancel ----
		btnCancel.setText(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(e -> cancelThread(e));
		contentPane.add(btnCancel, CC.xy(6, 16));

		//---- cbPorcelain ----
		cbPorcelain.setText(LocaleBundle.getString("BatchEditFrame.cbPorcelain")); //$NON-NLS-1$
		contentPane.add(cbPorcelain, CC.xy(8, 16));
		contentPane.add(progressBar1, CC.xywh(2, 18, 5, 1));
		contentPane.add(lblProgress1, CC.xy(8, 18));
		contentPane.add(progressBar2, CC.xywh(2, 20, 5, 1, CC.DEFAULT, CC.FILL));
		contentPane.add(lblProgress2, CC.xy(8, 20));
		setSize(800, 725);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private ReadableTextField edDatabasePath;
	private JButton btnOpenDatabase;
	private JLabel label2;
	private JTextField edDatabaseName;
	private JLabel label1;
	private JButton btnShowRules;
	private JScrollPane scrollPane1;
	private JTextArea edRules;
	private JButton btnCompare;
	private JTabbedPane pnlTabs;
	private JPanel tabDeletedEntries;
	private ShowMatchesTable tableDeletedEntries;
	private JPanel tabUpdateMetadata;
	private ShowMatchesTable tableUpdateMetadata;
	private JPanel tabUpdateCover;
	private ShowMatchesTable tableUpdateCover;
	private JPanel tabUpdateFile;
	private ShowMatchesTable tableUpdateFile;
	private JPanel tabAddedEntry;
	private ShowMatchesTable tableAddedEntry;
	private JPanel tabUnchangedEntry;
	private ShowMatchesTable tableUnchangedEntry;
	private JScrollPane scrollPane2;
	private JTextArea edEntryDiff;
	private JButton btnCreatePatch;
	private JButton btnCancel;
	private JCheckBox cbPorcelain;
	private JProgressBar progressBar1;
	private JLabel lblProgress1;
	private JProgressBar progressBar2;
	private JLabel lblProgress2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
