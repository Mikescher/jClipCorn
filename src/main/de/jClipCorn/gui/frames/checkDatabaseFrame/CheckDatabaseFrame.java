package de.jClipCorn.gui.frames.checkDatabaseFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.databaseErrors.*;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.CountAppendix;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.DoubleProgressCallbackProgressBarHelper;
import de.jClipCorn.util.listener.ProgressCallbackProgressBarHelper;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CheckDatabaseFrame extends JCCFrame
{
	private List<DatabaseError> errorList;

	public CheckDatabaseFrame(CCMovieList ml, MainFrame owner)
	{
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		btnFixSelected.setEnabled(false);
		btnAutofix.setEnabled(false);
		lsCategories.setCellRenderer(new ErrorTypeListCellRenderer());

		lblInfo.setText(LocaleBundle.getFormattedString("CheckDatabaseDialog.lblInfo.text", movielist.getElementCount()));

		lblProgress1.setText(Str.Empty);
		lblProgress2.setText(Str.Empty);

		cbValMovies.setSelected(ccprops().PROP_CHECKDATABASE_OPT_MOVIES.getValue());
		cbValCoverFiles.setSelected(ccprops().PROP_CHECKDATABASE_OPT_COVERS.getValue());
		cbValSeries.setSelected(ccprops().PROP_CHECKDATABASE_OPT_SERIES.getValue());
		cbValVideoFiles.setSelected(ccprops().PROP_CHECKDATABASE_OPT_FILES.getValue());
		cbValSeasons.setSelected(ccprops().PROP_CHECKDATABASE_OPT_SEASONS.getValue());
		cbValEpisodes.setSelected(ccprops().PROP_CHECKDATABASE_OPT_EPISODES.getValue());
		cbValAdditional.setSelected(ccprops().PROP_CHECKDATABASE_OPT_EXTRA.getValue());
	}

	private void onErrorSelected() {
		var err = lsMain.getSelectedValue();
		if (err == null) { edMetadata.setText(Str.Empty); return; }

		var len1 = CCStreams.iterate(err.Metadata).map(p -> p.Item1.length()).autoMaxOrDefault(0);

		StringBuilder b = new StringBuilder();
		for (var md: err.Metadata)
		{
			b.append(StringUtils.rightPad(md.Item1, len1)).append("  ->  ").append(md.Item2).append("\n");
		}

		edMetadata.setText(b.toString());
	}

	private void onCategorySelected() {
		ListModel<DatabaseError> lm = lsMain.getModel();
		if (lm instanceof DatabaseErrorListModel) {
			if (lsCategories.getSelectedValue() != null)
				((DatabaseErrorListModel)lm).updateFilter(lsCategories.getSelectedValue().Value);
			else
				((DatabaseErrorListModel)lm).updateFilter(null);
		}
		edMetadata.setText(Str.Empty);
	}

	private void onErrorClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && lsMain.getSelectedIndex() >= 0) lsMain.getSelectedValue().startEditing(this);
	}

	private void cbValMoviesItemStateChanged(ItemEvent e) {
		ccprops().PROP_CHECKDATABASE_OPT_MOVIES.setValue(cbValMovies.isSelected());
	}

	private void cbValSeriesItemStateChanged(ItemEvent e) {
		ccprops().PROP_CHECKDATABASE_OPT_SERIES.setValue(cbValSeries.isSelected());
	}

	private void cbValSeasonsItemStateChanged(ItemEvent e) {
		ccprops().PROP_CHECKDATABASE_OPT_EXTRA.setValue(cbValAdditional.isSelected());
	}

	private void cbValEpisodesItemStateChanged(ItemEvent e) {
		ccprops().PROP_CHECKDATABASE_OPT_EPISODES.setValue(cbValEpisodes.isSelected());
	}

	private void cbValCoverFilesItemStateChanged(ItemEvent e) {
		ccprops().PROP_CHECKDATABASE_OPT_COVERS.setValue(cbValCoverFiles.isSelected());
	}

	private void cbValVideoFilesItemStateChanged(ItemEvent e) {
		ccprops().PROP_CHECKDATABASE_OPT_FILES.setValue(cbValVideoFiles.isSelected());
	}

	private void cbValAdditionalItemStateChanged(ItemEvent e) {
		ccprops().PROP_CHECKDATABASE_OPT_SEASONS.setValue(cbValSeasons.isSelected());
	}

	private void autoFix() {
		btnValidate.setEnabled(false);
		btnAutofix.setEnabled(false);

		new Thread(() ->
		{
			pbProgress1.setVisible(true);
			lblProgress1.setVisible(false);
			pbProgress2.setVisible(false);
			lblProgress2.setVisible(false);

			boolean succ = DatabaseAutofixer.fixErrors(movielist, errorList, new ProgressCallbackProgressBarHelper(pbProgress1, 500));
			endFixThread(succ);
		}, "THREAD_AUTOFIX_DB").start(); //$NON-NLS-1$
	}

	private void endFixThread(final boolean success) {
		SwingUtils.invokeLater(() ->
		{
			btnValidate.setEnabled(true);
			btnAutofix.setEnabled(true);

			if (success) {
				DialogHelper.showDispatchLocalInformation(CheckDatabaseFrame.this, "CheckDatabaseDialog.Autofix.dialogSuccessfull"); //$NON-NLS-1$
			} else {
				DialogHelper.showDispatchLocalInformation(CheckDatabaseFrame.this, "CheckDatabaseDialog.Autofix.dialogUnsuccessfull"); //$NON-NLS-1$
			}
		});
	}

	private void startValidate() {
		btnFixSelected.setEnabled(false);
		btnValidate.setEnabled(false);
		btnAutofix.setEnabled(false);

		DatabaseValidatorOptions opts = new DatabaseValidatorOptions
		(
			cbValMovies.isSelected(),
			cbValSeries.isSelected(),
			cbValSeasons.isSelected(),
			cbValEpisodes.isSelected(),
			cbValAdditional.isSelected(),
			cbValCoverFiles.isSelected(),
			cbValVideoFiles.isSelected(),
			cbValAdditional.isSelected(),
			cbValAdditional.isSelected(),
			cbValAdditional.isSelected(),
			cbValAdditional.isSelected(),
			ccprops().PROP_VALIDATE_CHECK_SERIES_STRUCTURE.getValue(),
			ccprops().PROP_VALIDATE_DUP_IGNORE_IFO.getValue()
		);

		new Thread(() ->
		{
			pbProgress1.setVisible(true);
			lblProgress1.setVisible(true);
			pbProgress2.setVisible(true);
			lblProgress2.setVisible(true);

			List<DatabaseError> errors = new ArrayList<>();

			CCDatabaseValidator validator = new CCDatabaseValidator(movielist);
			validator.validate(errors, opts, new DoubleProgressCallbackProgressBarHelper(pbProgress1, lblProgress1, pbProgress2, lblProgress2));

			errorList = errors;

			updateLists();

			endThread();
		}, "THREAD_VALIDATE_DATABASE").start(); //$NON-NLS-1$
	}

	private void endThread() {
		SwingUtils.invokeLater(() ->
		{
			btnValidate.setEnabled(true);
			btnFixSelected.setEnabled(errorList.size() > 0);
			btnAutofix.setEnabled(errorList.size() > 0);
			lblInfo.setText(LocaleBundle.getFormattedString("CheckDatabaseDialog.lblInfo.text_2", errorList.size())); //$NON-NLS-1$
			edMetadata.setText(Str.Empty);
		});
	}

	private void fixSelected() {
		List<DatabaseError> errlist = lsMain.getSelectedValuesList();

		if (errlist == null || errlist.isEmpty()) {
			DialogHelper.showDispatchInformation(this, getTitle(), LocaleBundle.getString("CheckDatabaseDialog.fixSelectedMessage.NoSelection")); //$NON-NLS-1$
			return;
		}

		if (! DatabaseAutofixer.canFix(errorList, errlist)) {
			DialogHelper.showDispatchInformation(this, getTitle(), LocaleBundle.getString("CheckDatabaseDialog.fixSelectedMessage.Unfixable")); //$NON-NLS-1$
			return;
		}

		boolean hasFixedAll = true;
		for (DatabaseError err : errlist) {
			if (err.autoFix()) {
				errorList.remove(err);
			} else {
				hasFixedAll = false;
			}
		}

		if (hasFixedAll) {
			DialogHelper.showDispatchInformation(this, getTitle(), LocaleBundle.getString("CheckDatabaseDialog.fixSelectedMessage.Fixed")); //$NON-NLS-1$
		} else {
			DialogHelper.showDispatchInformation(this, getTitle(), LocaleBundle.getString("CheckDatabaseDialog.fixSelectedMessage.Failed")); //$NON-NLS-1$
		}

		updateLists();
	}

	private void updateLists() { // Threadsafe
		DatabaseErrorListModel dlm = new DatabaseErrorListModel();
		DefaultListModel<CountAppendix<DatabaseErrorType>> clm = new DefaultListModel<>();

		clm.addElement(null); //null = "All" (not sure if good code or lazy code)

		for (DatabaseError de : errorList) {
			dlm.addElement(de);

			boolean found = false;

			for (int i = 0; i < clm.getSize(); i++) {
				CountAppendix<DatabaseErrorType> idxval = clm.get(i);
				if (idxval != null && de.getType().equals(idxval.Value)) {
					found = true;
					clm.get(i).incCount();
					break;
				}
			}

			if (! found) {
				clm.addElement(new CountAppendix<>(de.getType(), 1, this::typeToString));
			}
		}

		setMainListModel(dlm);

		if (clm.size() <= 1) {
			clm.clear();
		}

		setCategoriesListModel(clm);
	}

	private void setMainListModel(final ListModel<DatabaseError> lm) {
		if (SwingUtilities.isEventDispatchThread()) {
			lsMain.setModel(lm);
		} else {
			try {
				SwingUtils.invokeAndWait(() -> lsMain.setModel(lm));
			} catch (InvocationTargetException | InterruptedException e) {
				CCLog.addError(e);
			}
		}
	}

	private void setCategoriesListModel(final ListModel<CountAppendix<DatabaseErrorType>> lm) {
		if (SwingUtilities.isEventDispatchThread()) {
			lsCategories.setModel(lm);
		} else {
			try {
				SwingUtils.invokeAndWait(() -> lsCategories.setModel(lm));
			} catch (InvocationTargetException | InterruptedException e) {
				CCLog.addError(e);
			}
		}
	}

	private String typeToString(CountAppendix<DatabaseErrorType> v) {
		if (v.getCount() == 0) {
			return LocaleBundle.getString(String.format("CheckDatabaseDialog.Errornames.ERR_%02d", v.Value.getType())); //$NON-NLS-1$
		} else {
			return LocaleBundle.getString(String.format("CheckDatabaseDialog.Errornames.ERR_%02d", v.Value.getType())) + " (" + v.getCount() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel1 = new JPanel();
		btnValidate = new JButton();
		lblInfo = new JLabel();
		btnAutofix = new JButton();
		btnFixSelected = new JButton();
		splitPane1 = new JSplitPane();
		scrollPane1 = new JScrollPane();
		lsCategories = new JList<>();
		splitPane2 = new JSplitPane();
		scrollPane2 = new JScrollPane();
		lsMain = new JList<>();
		scrollPane3 = new JScrollPane();
		edMetadata = new JTextArea();
		pbProgress1 = new JProgressBar();
		lblProgress1 = new JLabel();
		pbProgress2 = new JProgressBar();
		lblProgress2 = new JLabel();
		panel2 = new JPanel();
		cbValMovies = new JCheckBox();
		cbValCoverFiles = new JCheckBox();
		cbValSeries = new JCheckBox();
		cbValVideoFiles = new JCheckBox();
		cbValSeasons = new JCheckBox();
		cbValEpisodes = new JCheckBox();
		cbValAdditional = new JCheckBox();

		//======== this ========
		setTitle(LocaleBundle.getString("CheckDatabaseDialog.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(650, 400));
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $lcgap, 226dlu, $ugap", //$NON-NLS-1$
			"$ugap, default, $lgap, default:grow, 3*($lgap, default), $ugap")); //$NON-NLS-1$

		//======== panel1 ========
		{
			panel1.setLayout(new FormLayout(
				"2*(default, $lcgap), default:grow, 2*($lcgap, default)", //$NON-NLS-1$
				"default")); //$NON-NLS-1$

			//---- btnValidate ----
			btnValidate.setText(LocaleBundle.getString("CheckDatabaseDialog.btnValidate.text")); //$NON-NLS-1$
			btnValidate.setFont(btnValidate.getFont().deriveFont(btnValidate.getFont().getStyle() | Font.BOLD));
			btnValidate.addActionListener(e -> startValidate());
			panel1.add(btnValidate, CC.xy(1, 1));

			//---- lblInfo ----
			lblInfo.setText(LocaleBundle.getString("CheckDatabaseDialog.lblInfo.text")); //$NON-NLS-1$
			panel1.add(lblInfo, CC.xy(3, 1));

			//---- btnAutofix ----
			btnAutofix.setText(LocaleBundle.getString("CheckDatabaseDialog.btnAutofix.text")); //$NON-NLS-1$
			btnAutofix.addActionListener(e -> autoFix());
			panel1.add(btnAutofix, CC.xy(7, 1));

			//---- btnFixSelected ----
			btnFixSelected.setText(LocaleBundle.getString("CheckDatabaseDialog.btnFixSelected.text")); //$NON-NLS-1$
			btnFixSelected.addActionListener(e -> fixSelected());
			panel1.add(btnFixSelected, CC.xy(9, 1));
		}
		contentPane.add(panel1, CC.xywh(2, 2, 3, 1));

		//======== splitPane1 ========
		{
			splitPane1.setContinuousLayout(true);
			splitPane1.setResizeWeight(0.25);

			//======== scrollPane1 ========
			{
				scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

				//---- lsCategories ----
				lsCategories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				lsCategories.addListSelectionListener(e -> onCategorySelected());
				scrollPane1.setViewportView(lsCategories);
			}
			splitPane1.setLeftComponent(scrollPane1);

			//======== splitPane2 ========
			{
				splitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
				splitPane2.setResizeWeight(0.7);
				splitPane2.setContinuousLayout(true);

				//======== scrollPane2 ========
				{
					scrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

					//---- lsMain ----
					lsMain.addListSelectionListener(e -> onErrorSelected());
					lsMain.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							onErrorClicked(e);
						}
					});
					scrollPane2.setViewportView(lsMain);
				}
				splitPane2.setTopComponent(scrollPane2);

				//======== scrollPane3 ========
				{

					//---- edMetadata ----
					edMetadata.setEditable(false);
					edMetadata.setFont(new Font("Noto Mono", Font.PLAIN, 13)); //$NON-NLS-1$
					scrollPane3.setViewportView(edMetadata);
				}
				splitPane2.setBottomComponent(scrollPane3);
			}
			splitPane1.setRightComponent(splitPane2);
		}
		contentPane.add(splitPane1, CC.xywh(2, 4, 3, 1, CC.FILL, CC.FILL));
		contentPane.add(pbProgress1, CC.xy(2, 6));

		//---- lblProgress1 ----
		lblProgress1.setText("<dynamic>"); //$NON-NLS-1$
		contentPane.add(lblProgress1, CC.xy(4, 6));
		contentPane.add(pbProgress2, CC.xy(2, 8));

		//---- lblProgress2 ----
		lblProgress2.setText("<dynamic>"); //$NON-NLS-1$
		contentPane.add(lblProgress2, CC.xy(4, 8));

		//======== panel2 ========
		{
			panel2.setLayout(new FormLayout(
				"0dlu:grow, $lcgap, 1dlu:grow", //$NON-NLS-1$
				"3*(default, $lgap), default")); //$NON-NLS-1$

			//---- cbValMovies ----
			cbValMovies.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValMovies")); //$NON-NLS-1$
			cbValMovies.addItemListener(e -> cbValMoviesItemStateChanged(e));
			panel2.add(cbValMovies, CC.xy(1, 1));

			//---- cbValCoverFiles ----
			cbValCoverFiles.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValCoverFiles")); //$NON-NLS-1$
			cbValCoverFiles.addItemListener(e -> cbValCoverFilesItemStateChanged(e));
			panel2.add(cbValCoverFiles, CC.xy(3, 1));

			//---- cbValSeries ----
			cbValSeries.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValSeries")); //$NON-NLS-1$
			cbValSeries.addItemListener(e -> cbValSeriesItemStateChanged(e));
			panel2.add(cbValSeries, CC.xy(1, 3));

			//---- cbValVideoFiles ----
			cbValVideoFiles.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValVideoFiles")); //$NON-NLS-1$
			cbValVideoFiles.addItemListener(e -> cbValVideoFilesItemStateChanged(e));
			panel2.add(cbValVideoFiles, CC.xy(3, 3));

			//---- cbValSeasons ----
			cbValSeasons.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValSeasons")); //$NON-NLS-1$
			cbValSeasons.addItemListener(e -> cbValSeasonsItemStateChanged(e));
			panel2.add(cbValSeasons, CC.xy(1, 5));

			//---- cbValEpisodes ----
			cbValEpisodes.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValEpisodes")); //$NON-NLS-1$
			cbValEpisodes.addItemListener(e -> cbValEpisodesItemStateChanged(e));
			panel2.add(cbValEpisodes, CC.xy(1, 7));

			//---- cbValAdditional ----
			cbValAdditional.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValAdditional")); //$NON-NLS-1$
			cbValAdditional.addItemListener(e -> cbValAdditionalItemStateChanged(e));
			panel2.add(cbValAdditional, CC.xy(3, 7));
		}
		contentPane.add(panel2, CC.xywh(2, 10, 3, 1));
		setSize(1200, 700);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panel1;
	private JButton btnValidate;
	private JLabel lblInfo;
	private JButton btnAutofix;
	private JButton btnFixSelected;
	private JSplitPane splitPane1;
	private JScrollPane scrollPane1;
	private JList<CountAppendix<DatabaseErrorType>> lsCategories;
	private JSplitPane splitPane2;
	private JScrollPane scrollPane2;
	private JList<DatabaseError> lsMain;
	private JScrollPane scrollPane3;
	private JTextArea edMetadata;
	private JProgressBar pbProgress1;
	private JLabel lblProgress1;
	private JProgressBar pbProgress2;
	private JLabel lblProgress2;
	private JPanel panel2;
	private JCheckBox cbValMovies;
	private JCheckBox cbValCoverFiles;
	private JCheckBox cbValSeries;
	private JCheckBox cbValVideoFiles;
	private JCheckBox cbValSeasons;
	private JCheckBox cbValEpisodes;
	private JCheckBox cbValAdditional;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
