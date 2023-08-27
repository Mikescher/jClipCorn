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
import java.util.HashSet;
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

	@SuppressWarnings("nls")
	private void postInit()
	{
		btnFixSelected.setEnabled(false);
		btnAutofix.setEnabled(false);
		lsCategories.setCellRenderer(new ErrorTypeListCellRenderer());

		lblInfo.setText(LocaleBundle.getFormattedString("CheckDatabaseDialog.lblInfo.text", movielist.getElementCount()));

		lblProgress1.setText(Str.Empty);
		lblProgress2.setText(Str.Empty);

		var props = ccprops().PROP_CHECKDATABASE_OPTIONS.getValue();

		cbValMovies.setSelected(           props.contains("MOVIES")            );
		cbValSeries.setSelected(           props.contains("SERIES")            );
		cbValSeasons.setSelected(          props.contains("SEASONS")           );
		cbValEpisodes.setSelected(         props.contains("EPISODES")          );

		cbValCovers.setSelected(           props.contains("COVERS")            );
		cbValGroups.setSelected(           props.contains("GROUPS")            );
		cbValOnlineRefs.setSelected(       props.contains("ONLINEREFS")        );

		cbValCoverFiles.setSelected(       props.contains("COVER_FILES")       );
		cbValVideoFiles.setSelected(       props.contains("VIDEO_FILES")       );

		cbValDatabase.setSelected(         props.contains("DATABASE")          );
		cbValDuplicates.setSelected(       props.contains("DUPLICATES")        );
		cbValSeriesStructure.setSelected(  props.contains("SERIES_STRUCTURE")  );
		cbValEmptyDirs.setSelected(        props.contains("EMPTY_DIRS")        );

		if (!ccprops().PROP_VALIDATE_CHECK_SERIES_STRUCTURE.getValue()) {
			cbValSeriesStructure.setSelected(false);
			cbValSeriesStructure.setEnabled(false);
		}
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

	@SuppressWarnings("nls")
	private void cbxAnyItemStateChanged(ItemEvent e) {
		var v = new HashSet<String>();
		if (cbValMovies.isSelected())          v.add("MOVIES");
		if (cbValSeries.isSelected())          v.add("SERIES");
		if (cbValSeasons.isSelected())         v.add("SEASONS");
		if (cbValEpisodes.isSelected())        v.add("EPISODES");

		if (cbValCovers.isSelected())          v.add("COVERS");
		if (cbValGroups.isSelected())          v.add("GROUPS");
		if (cbValOnlineRefs.isSelected())      v.add("ONLINEREFS");

		if (cbValCoverFiles.isSelected())      v.add("COVER_FILES");
		if (cbValVideoFiles.isSelected())      v.add("VIDEO_FILES");
		
		if (cbValDatabase.isSelected())        v.add("DATABASE");
		if (cbValDuplicates.isSelected())      v.add("DUPLICATES");
		if (cbValSeriesStructure.isSelected()) v.add("SERIES_STRUCTURE");
		if (cbValEmptyDirs.isSelected())       v.add("EMPTY_DIRS");

		ccprops().PROP_CHECKDATABASE_OPTIONS.setValue(v);
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

		DatabaseValidatorOptions opts = new DatabaseValidatorOptions();

		opts.ValidateMovies                    = cbValMovies.isSelected();
		opts.ValidateSeries                    = cbValSeries.isSelected();
		opts.ValidateSeasons                   = cbValSeasons.isSelected();
		opts.ValidateEpisodes                  = cbValEpisodes.isSelected();

		opts.ValidateCovers                    = cbValCovers.isSelected();
		opts.ValidateCoverFiles                = cbValCoverFiles.isSelected();
		opts.ValidateVideoFiles                = cbValVideoFiles.isSelected();
		opts.ValidateGroups                    = cbValGroups.isSelected();
		opts.ValidateOnlineReferences          = cbValOnlineRefs.isSelected();

		opts.ValidateDuplicateFilesByPath      = cbValDuplicates.isSelected();
		opts.ValidateDuplicateFilesByMediaInfo = cbValDuplicates.isSelected();
		opts.ValidateDatabaseConsistence       = cbValDatabase.isSelected();
		opts.ValidateSeriesStructure           = cbValSeriesStructure.isSelected();
		opts.FindEmptyDirectories              = cbValEmptyDirs.isSelected();

		opts.IgnoreDuplicateIfos               = ccprops().PROP_VALIDATE_DUP_IGNORE_IFO.getValue();

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
		cbValCovers = new JCheckBox();
		cbValCoverFiles = new JCheckBox();
		cbValDatabase = new JCheckBox();
		cbValSeries = new JCheckBox();
		cbValGroups = new JCheckBox();
		cbValVideoFiles = new JCheckBox();
		cbValDuplicates = new JCheckBox();
		cbValSeasons = new JCheckBox();
		cbValOnlineRefs = new JCheckBox();
		cbValSeriesStructure = new JCheckBox();
		cbValEpisodes = new JCheckBox();
		cbValEmptyDirs = new JCheckBox();

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
				"0dlu:grow, 3*($lcgap, 1dlu:grow)", //$NON-NLS-1$
				"3*(default, $lgap), default")); //$NON-NLS-1$

			//---- cbValMovies ----
			cbValMovies.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValMovies")); //$NON-NLS-1$
			cbValMovies.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValMovies, CC.xy(1, 1));

			//---- cbValCovers ----
			cbValCovers.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValCovers.text")); //$NON-NLS-1$
			cbValCovers.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValCovers, CC.xy(3, 1));

			//---- cbValCoverFiles ----
			cbValCoverFiles.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValCoverFiles")); //$NON-NLS-1$
			cbValCoverFiles.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValCoverFiles, CC.xy(5, 1));

			//---- cbValDatabase ----
			cbValDatabase.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValDatabase")); //$NON-NLS-1$
			cbValDatabase.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValDatabase, CC.xy(7, 1));

			//---- cbValSeries ----
			cbValSeries.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValSeries")); //$NON-NLS-1$
			cbValSeries.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValSeries, CC.xy(1, 3));

			//---- cbValGroups ----
			cbValGroups.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValGroups.text")); //$NON-NLS-1$
			cbValGroups.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValGroups, CC.xy(3, 3));

			//---- cbValVideoFiles ----
			cbValVideoFiles.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValVideoFiles")); //$NON-NLS-1$
			cbValVideoFiles.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValVideoFiles, CC.xy(5, 3));

			//---- cbValDuplicates ----
			cbValDuplicates.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValDuplicates")); //$NON-NLS-1$
			cbValDuplicates.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValDuplicates, CC.xy(7, 3));

			//---- cbValSeasons ----
			cbValSeasons.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValSeasons")); //$NON-NLS-1$
			cbValSeasons.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValSeasons, CC.xy(1, 5));

			//---- cbValOnlineRefs ----
			cbValOnlineRefs.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValOnlineRefs.text")); //$NON-NLS-1$
			cbValOnlineRefs.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValOnlineRefs, CC.xy(3, 5));

			//---- cbValSeriesStructure ----
			cbValSeriesStructure.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValSeriesStructure")); //$NON-NLS-1$
			cbValSeriesStructure.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValSeriesStructure, CC.xy(7, 5));

			//---- cbValEpisodes ----
			cbValEpisodes.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValEpisodes")); //$NON-NLS-1$
			cbValEpisodes.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValEpisodes, CC.xy(1, 7));

			//---- cbValEmptyDirs ----
			cbValEmptyDirs.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValEmptyDirs")); //$NON-NLS-1$
			cbValEmptyDirs.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValEmptyDirs, CC.xy(7, 7));
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
	private JCheckBox cbValCovers;
	private JCheckBox cbValCoverFiles;
	private JCheckBox cbValDatabase;
	private JCheckBox cbValSeries;
	private JCheckBox cbValGroups;
	private JCheckBox cbValVideoFiles;
	private JCheckBox cbValDuplicates;
	private JCheckBox cbValSeasons;
	private JCheckBox cbValOnlineRefs;
	private JCheckBox cbValSeriesStructure;
	private JCheckBox cbValEpisodes;
	private JCheckBox cbValEmptyDirs;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
