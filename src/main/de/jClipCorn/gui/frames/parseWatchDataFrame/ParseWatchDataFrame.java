package de.jClipCorn.gui.frames.parseWatchDataFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.parser.watchdata.WatchDataChangeSet;
import de.jClipCorn.util.parser.watchdata.WatchDataParser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseWatchDataFrame extends JCCFrame
{
	private List<WatchDataChangeSet> changeSet = null;

	public ParseWatchDataFrame(Component owner, CCMovieList ml)
	{
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		memoData.getDocument().addDocumentListener(new DocumentLambdaAdapter(this::dataUpdated));;
	}

	private void onExecute() {
		for (WatchDataChangeSet w : changeSet) {
			w.execute();
		}

		dispose();
	}

	private void onCancel() {
		dispose();
	}

	private void onShowExample() {
		try {
			memoData.setText(SimpleFileUtils.readTextResource("/watchdata_example.txt", this.getClass())); //$NON-NLS-1$
			memoLog.setText(""); //$NON-NLS-1$
			dataUpdated();
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		scrollPane1 = new JScrollPane();
		memoData = new JTextArea();
		tableResults = new ParseWatchDataTable(this);
		scrollPane2 = new JScrollPane();
		memoLog = new JTextArea();
		pnlBottom = new JPanel();
		btnExecute = new JButton();
		btnCancel = new JButton();
		btnShowExample = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("ParseWatchDataFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, 0dlu:grow, $lcgap, 0dlu:grow, $ugap", //$NON-NLS-1$
			"$ugap, 0dlu:grow, $lgap, 0dlu:grow, $ugap, default, $ugap")); //$NON-NLS-1$

		//======== scrollPane1 ========
		{

			//---- memoData ----
			memoData.setFont(new Font("Courier New", memoData.getFont().getStyle(), memoData.getFont().getSize())); //$NON-NLS-1$
			scrollPane1.setViewportView(memoData);
		}
		contentPane.add(scrollPane1, CC.xywh(2, 2, 1, 3, CC.FILL, CC.FILL));
		contentPane.add(tableResults, CC.xy(4, 2, CC.FILL, CC.FILL));

		//======== scrollPane2 ========
		{

			//---- memoLog ----
			memoLog.setFont(new Font(Font.MONOSPACED, memoLog.getFont().getStyle(), memoLog.getFont().getSize()));
			memoLog.setEditable(false);
			scrollPane2.setViewportView(memoLog);
		}
		contentPane.add(scrollPane2, CC.xy(4, 4, CC.FILL, CC.FILL));

		//======== pnlBottom ========
		{
			pnlBottom.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

			//---- btnExecute ----
			btnExecute.setText(LocaleBundle.getString("UIGeneric.btnApply.text")); //$NON-NLS-1$
			btnExecute.addActionListener(e -> onExecute());
			pnlBottom.add(btnExecute);

			//---- btnCancel ----
			btnCancel.setText(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
			btnCancel.addActionListener(e -> onCancel());
			pnlBottom.add(btnCancel);

			//---- btnShowExample ----
			btnShowExample.setText(LocaleBundle.getString("ParseWatchDataFrame.btnExamples.text")); //$NON-NLS-1$
			btnShowExample.addActionListener(e -> onShowExample());
			pnlBottom.add(btnShowExample);
		}
		contentPane.add(pnlBottom, CC.xywh(2, 6, 3, 1, CC.DEFAULT, CC.FILL));
		setSize(1100, 600);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	@SuppressWarnings("nls")
	private void dataUpdated() {
		java.util.List<String> errors = new ArrayList<>();
		List<WatchDataChangeSet> set;

		set = WatchDataParser.parse(movielist, memoData.getText(), errors);

		memoLog.setText("");
		for (String err : errors) {
			memoLog.append(err + "\n");
		}

		btnExecute.setEnabled(errors.isEmpty());

		updateResults(set);
	}

	private void updateResults(List<WatchDataChangeSet> change) {
		changeSet = change;

		tableResults.setData(change);
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollPane scrollPane1;
	private JTextArea memoData;
	private ParseWatchDataTable tableResults;
	private JScrollPane scrollPane2;
	private JTextArea memoLog;
	private JPanel pnlBottom;
	private JButton btnExecute;
	private JButton btnCancel;
	private JButton btnShowExample;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
