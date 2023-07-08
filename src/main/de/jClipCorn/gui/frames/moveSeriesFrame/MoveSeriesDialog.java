package de.jClipCorn.gui.frames.moveSeriesFrame;

import java.awt.event.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.gui.guiComponents.*;
import de.jClipCorn.gui.guiComponents.JCCFrame;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.jClipCorn.gui.guiComponents.cover.*;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.RegExHelper;
import de.jClipCorn.util.helper.SwingUtils;

public class MoveSeriesDialog extends JCCDialog
{
	private final CCSeries series;

	public MoveSeriesDialog(Component owner, CCSeries series)
	{
		super(series.getMovieList());

		this.series = series;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		lblTitle.setText(series.getTitle());

		lblCover.setAndResizeCover(series.getCover());

		edSearch.setText(series.getCommonPathStart(false).toString());
		edReplace.setText(series.getCommonPathStart(false).toString());

		edSearch.getDocument().addDocumentListener(new DocumentLambdaAdapter(() -> btnOK.setEnabled(false)));
		edReplace.getDocument().addDocumentListener(new DocumentLambdaAdapter(() -> btnOK.setEnabled(false)));
		cbxRegex.addActionListener((e) -> btnOK.setEnabled(false));
	}

	private void startTest(ActionEvent e)
	{
		new Thread(() ->
		{
			try
			{
				SwingUtils.invokeLater(this::disableAll);

				int total = series.getEpisodeCount();
				int curr  = 1;
				SwingUtils.invokeLater(() -> this.progress.setMaximum(total));

				Vector<MassMoveEntry> data = new Vector<>();

				for (int seasi = 0; seasi < series.getSeasonCount(); seasi++)
				{
					CCSeason season = series.getSeasonByArrayIndex(seasi);
					for (int epi = 0; epi < season.getEpisodeCount(); epi++)
					{
						CCEpisode ep = season.getEpisodeByArrayIndex(epi);

						var entry = new MassMoveEntry();

						entry.entry = ep;

						entry.PathOld = ep.getPart();

						entry.PathNew = replacePath(edSearch.getText(), edReplace.getText(), cbxRegex.isSelected(), entry.PathOld);

						entry.OldIsValid = entry.PathOld.toFSPath(this).exists();
						entry.NewIsValid = entry.PathNew.toFSPath(this).exists();

						data.add(entry);

						final int _v = curr++;
						SwingUtils.invokeLater(() -> this.progress.setValue(_v));
					}
				}

				SwingUtils.invokeLater(() ->
				{
					tabTest.setData(data);
					this.btnOK.setEnabled(true);
					this.progress.setValue(0);
				});
			}
			finally
			{
				SwingUtils.invokeLater(this::enableAll);
			}
		}, "THREAD_MASSMOVE_TEST").start(); //$NON-NLS-1$
	}

	private void startReplace(ActionEvent e) {
		if (! DialogHelper.showLocaleYesNo(this, "Dialogs.MoveSeries")) { //$NON-NLS-1$
			return;
		}

		for (int seasi = 0; seasi < series.getSeasonCount(); seasi++) {
			CCSeason season = series.getSeasonByArrayIndex(seasi);
			for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
				CCEpisode ep = season.getEpisodeByArrayIndex(epi);

				var newValue = replacePath(edSearch.getText(), edReplace.getText(), cbxRegex.isSelected(), ep.getPart());

				ep.Part.set(newValue);
			}
		}

		dispose();
	}

	private CCPath replacePath(String search, String repl, boolean regexp, CCPath value) {
		if (regexp)
		{
			try
			{
				var newVal = Pattern.compile(search).matcher(value.toString()).replaceAll(repl); // replaceAll allows back-references aka $1
				return CCPath.create(newVal);
			}
			catch (PatternSyntaxException e)
			{
				return CCPath.Empty;
			}
		}
		else
		{
			return CCPath.create(value.toString().replace(edSearch.getText(), edReplace.getText()));
		}
	}

	private void enableAll() {
		this.edSearch.setEnabled(true);
		this.edReplace.setEnabled(true);
		this.cbxRegex.setEnabled(true);
		this.btnOK.setEnabled(true);
		this.btnTest.setEnabled(true);
	}

	private void disableAll() {
		this.edSearch.setEnabled(false);
		this.edReplace.setEnabled(false);
		this.cbxRegex.setEnabled(false);
		this.btnOK.setEnabled(false);
		this.btnTest.setEnabled(false);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		lblCover = new CoverLabelFullsize(movielist);
		panel1 = new JPanel();
		lblTitle = new JLabel();
		label1 = new JLabel();
		edSearch = new JTextField();
		label2 = new JLabel();
		edReplace = new JTextField();
		cbxRegex = new JCheckBox();
		btnOK = new JButton();
		btnTest = new JButton();
		progress = new JProgressBar();
		tabTest = new MassMoveTable(this, false, false);

		//======== this ========
		setTitle(LocaleBundle.getString("MoveSeriesFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $lcgap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default, $lgap, default:grow, $ugap")); //$NON-NLS-1$
		contentPane.add(lblCover, CC.xy(2, 2));

		//======== panel1 ========
		{
			panel1.setLayout(new FormLayout(
				"0dlu:grow, $lcgap, 0dlu:grow", //$NON-NLS-1$
				"6*(default, $lgap), 0dlu:grow, $lgap, default, $lgap, pref, $lgap")); //$NON-NLS-1$

			//---- lblTitle ----
			lblTitle.setText("<dynamic>"); //$NON-NLS-1$
			lblTitle.setFont(lblTitle.getFont().deriveFont(lblTitle.getFont().getStyle() | Font.BOLD, lblTitle.getFont().getSize() + 3f));
			lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
			panel1.add(lblTitle, CC.xywh(1, 1, 3, 1));

			//---- label1 ----
			label1.setText(LocaleBundle.getString("MoveSeriesFrame.lblReplace.text")); //$NON-NLS-1$
			panel1.add(label1, CC.xy(1, 3));
			panel1.add(edSearch, CC.xywh(1, 5, 3, 1));

			//---- label2 ----
			label2.setText(LocaleBundle.getString("MoveSeriesFrame.lblWith.text")); //$NON-NLS-1$
			panel1.add(label2, CC.xy(1, 7));
			panel1.add(edReplace, CC.xywh(1, 9, 3, 1));

			//---- cbxRegex ----
			cbxRegex.setText(LocaleBundle.getString("MoveSeriesFrame.cbxRegex")); //$NON-NLS-1$
			panel1.add(cbxRegex, CC.xy(1, 11));

			//---- btnOK ----
			btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
			btnOK.setEnabled(false);
			btnOK.addActionListener(e -> startReplace(e));
			panel1.add(btnOK, CC.xy(1, 15));

			//---- btnTest ----
			btnTest.setText(LocaleBundle.getString("MoveSeriesFrame.btnTest.text")); //$NON-NLS-1$
			btnTest.addActionListener(e -> startTest(e));
			panel1.add(btnTest, CC.xy(3, 15));
			panel1.add(progress, CC.xywh(1, 17, 3, 1, CC.DEFAULT, CC.FILL));
		}
		contentPane.add(panel1, CC.xy(4, 2, CC.FILL, CC.FILL));
		contentPane.add(tabTest, CC.xywh(2, 4, 3, 1, CC.FILL, CC.FILL));
		setSize(705, 750);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private CoverLabelFullsize lblCover;
	private JPanel panel1;
	private JLabel lblTitle;
	private JLabel label1;
	private JTextField edSearch;
	private JLabel label2;
	private JTextField edReplace;
	private JCheckBox cbxRegex;
	private JButton btnOK;
	private JButton btnTest;
	private JProgressBar progress;
	private MassMoveTable tabTest;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
