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
import de.jClipCorn.util.lambda.Func3to0;
import de.jClipCorn.util.listener.DoubleProgressCallbackProgressBarHelper;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CheckDatabaseFrame extends JCCFrame
{
	private List<DatabaseError> errorList;

	private Thread activeThread = null;
	private DoubleProgressCallbackProgressBarHelper activeCB = null;
	private boolean autofixRunning = false;
	private boolean seriesStructureCheckAllowed = true;

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

		applyPreset(DatabaseValidatorOptions.deserialize(ccprops().PROP_CHECKDATABASE_OPTIONS.getValue()));

		pbProgress1.setVisible(false);
		lblProgress1.setVisible(false);
		pbProgress2.setVisible(false);
		lblProgress2.setVisible(false);
		btnCancelAutofix.setVisible(false);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (activeThread == null) dispose();
			}
		});

		updateUI();
	}

	private void updateUI() { // Threadsafe
		SwingUtils.invokeLater(() ->
		{
			final boolean idle = (activeThread == null);
			final boolean hasErrors = (errorList != null && !errorList.isEmpty());

			btnValidate.setEnabled(idle);
			btnAutofix.setEnabled(idle && hasErrors);
			btnFixSelected.setEnabled(idle && hasErrors);

			cbValMovies.setEnabled(idle);
			cbValSeries.setEnabled(idle);
			cbValSeasons.setEnabled(idle);
			cbValEpisodes.setEnabled(idle);
			cbValCovers.setEnabled(idle);
			cbValGroups.setEnabled(idle);
			cbValOnlineRefs.setEnabled(idle);
			cbValCoverFiles.setEnabled(idle);
			cbValVideoFiles.setEnabled(idle);
			cbValDatabase.setEnabled(idle);
			cbValDuplicates.setEnabled(idle);
			cbValSeriesStructure.setEnabled(idle && seriesStructureCheckAllowed);
			cbValEmptyDirs.setEnabled(idle);
			cbValNfoFiles.setEnabled(idle);
			cbValNfoFullCompare.setEnabled(idle);

			btnPresetFull.setEnabled(idle);
			btnPresetDefault.setEnabled(idle);
			btnPresetQuick.setEnabled(idle);

			btnCancelAutofix.setVisible(autofixRunning);
		});
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

	// reads the current checkbox state into a DatabaseValidatorOptions object
	private DatabaseValidatorOptions optionsFromCheckboxes() {
		var opts = new DatabaseValidatorOptions();

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
		opts.ValidateNfoFiles                  = cbValNfoFiles.isSelected();
		opts.ValidateNfoFullComparison         = cbValNfoFullCompare.isSelected();

		return opts;
	}

	private void cbxAnyItemStateChanged(ItemEvent e) {
		ccprops().PROP_CHECKDATABASE_OPTIONS.setValue(optionsFromCheckboxes().serialize());
	}

	private void presetFull()    { applyPreset(DatabaseValidatorOptions.presetFull());    startValidate(); }
	private void presetDefault() { applyPreset(DatabaseValidatorOptions.presetDefault()); startValidate(); }
	private void presetQuick()   { applyPreset(DatabaseValidatorOptions.presetQuick());   startValidate(); }

	private void applyPreset(DatabaseValidatorOptions o) {
		cbValMovies.setSelected(o.ValidateMovies);
		cbValSeries.setSelected(o.ValidateSeries);
		cbValSeasons.setSelected(o.ValidateSeasons);
		cbValEpisodes.setSelected(o.ValidateEpisodes);
		cbValCovers.setSelected(o.ValidateCovers);
		cbValCoverFiles.setSelected(o.ValidateCoverFiles);
		cbValVideoFiles.setSelected(o.ValidateVideoFiles);
		cbValGroups.setSelected(o.ValidateGroups);
		cbValOnlineRefs.setSelected(o.ValidateOnlineReferences);
		cbValDuplicates.setSelected(o.ValidateDuplicateFilesByPath || o.ValidateDuplicateFilesByMediaInfo);
		cbValDatabase.setSelected(o.ValidateDatabaseConsistence);
		cbValSeriesStructure.setSelected(o.ValidateSeriesStructure && seriesStructureCheckAllowed);
		cbValEmptyDirs.setSelected(o.FindEmptyDirectories);
		cbValNfoFiles.setSelected(o.ValidateNfoFiles);
		cbValNfoFullCompare.setSelected(o.ValidateNfoFullComparison);
	}

	private void autoFix() {
		if (activeThread != null) { updateUI(); return; }

		startFixThread("THREAD_AUTOFIX_DB", errorList, (success, cancelled, fixed) -> //$NON-NLS-1$
		{
			if (cancelled) {
				DialogHelper.showDispatchLocalInformation(CheckDatabaseFrame.this, "CheckDatabaseDialog.Autofix.dialogCancelled"); //$NON-NLS-1$
			} else if (success) {
				DialogHelper.showDispatchLocalInformation(CheckDatabaseFrame.this, "CheckDatabaseDialog.Autofix.dialogSuccessfull"); //$NON-NLS-1$
			} else {
				DialogHelper.showDispatchLocalInformation(CheckDatabaseFrame.this, "CheckDatabaseDialog.Autofix.dialogUnsuccessfull"); //$NON-NLS-1$
			}
		});
	}

	// Runs DatabaseAutofixer.fixErrors on {@code toFix} in a background thread with progress, cancellation and UI-blocking.
	// {@code onDone} is invoked on the EDT with (fullSuccess, cancelled, listOfFixedErrors).
	private void startFixThread(String threadName, final List<DatabaseError> toFix, final Func3to0<Boolean, Boolean, List<DatabaseError>> onDone) {
		if (activeThread != null) { updateUI(); return; }

		final List<DatabaseError> context = errorList;

		pbProgress1.setVisible(true);
		lblProgress1.setVisible(true);
		pbProgress2.setVisible(false);
		lblProgress2.setVisible(false);

		btnCancelAutofix.setEnabled(true);

		final DoubleProgressCallbackProgressBarHelper cb = new DoubleProgressCallbackProgressBarHelper(pbProgress1, lblProgress1, pbProgress2, lblProgress2);
		activeCB = cb;
		autofixRunning = true;

		activeThread = new Thread(() ->
		{
			boolean succ = false;
			List<DatabaseError> fixed = new ArrayList<>();
			try {
				SwingUtils.invokeLater(() -> MainFrame.getInstance().beginBlockingIntermediate());
				var r = DatabaseAutofixer.fixErrors(movielist, context, toFix, cb);
				fixed = r.Item1;
				succ  = r.Item2;
			} finally {
				final boolean fSucc = succ;
				final boolean cancelled = cb.isCancelled();
				final List<DatabaseError> fFixed = fixed;
				SwingUtils.invokeLater(() -> MainFrame.getInstance().endBlockingIntermediate());
				activeThread = null;
				activeCB = null;
				autofixRunning = false;
				SwingUtils.invokeLater(() ->
				{
					pbProgress1.setVisible(false);
					lblProgress1.setVisible(false);
					onDone.invoke(fSucc, cancelled, fFixed);
					updateUI();
				});
			}
		}, threadName);
		activeThread.start();

		updateUI();
	}

	private void cancelAutofix(ActionEvent e) {
		if (activeCB != null) activeCB.cancel();
		btnCancelAutofix.setEnabled(false); // disable directly, the running action will finish and then cancel
	}

	private void startValidate() {
		if (activeThread != null) { updateUI(); return; }

		DatabaseValidatorOptions opts = optionsFromCheckboxes();
		opts.IgnoreDuplicateIfos = ccprops().PROP_VALIDATE_DUP_IGNORE_IFO.getValue();

		autofixRunning = false;

		pbProgress1.setVisible(true);
		lblProgress1.setVisible(true);
		pbProgress2.setVisible(true);
		lblProgress2.setVisible(true);

		activeThread = new Thread(() ->
		{
			List<DatabaseError> errors = new ArrayList<>();
			try {
				CCDatabaseValidator validator = new CCDatabaseValidator(movielist);
				validator.validate(errors, opts, new DoubleProgressCallbackProgressBarHelper(pbProgress1, lblProgress1, pbProgress2, lblProgress2));
			} catch (Exception e) {
				// A failing validation must not silently kill the worker thread - log it and still
				// surface whatever errors were already collected before the failure.
				CCLog.addError(e);
			} finally {
				errorList = errors;
				updateLists();
				activeThread = null;
				endThread();
			}
		}, "THREAD_VALIDATE_DATABASE"); //$NON-NLS-1$
		activeThread.start();

		updateUI();
	}

	private void endThread() {
		SwingUtils.invokeLater(() ->
		{
			pbProgress1.setVisible(false);
			lblProgress1.setVisible(false);
			pbProgress2.setVisible(false);
			lblProgress2.setVisible(false);

			updateUI();

			lblInfo.setText(LocaleBundle.getFormattedString("CheckDatabaseDialog.lblInfo.text_2", errorList.size())); //$NON-NLS-1$
			edMetadata.setText(Str.Empty);
		});
	}

	private void fixSelected() {
		if (activeThread != null) { updateUI(); return; }

		List<DatabaseError> errlist = lsMain.getSelectedValuesList();

		if (errlist == null || errlist.isEmpty()) {
			DialogHelper.showDispatchInformation(this, getTitle(), LocaleBundle.getString("CheckDatabaseDialog.fixSelectedMessage.NoSelection")); //$NON-NLS-1$
			return;
		}


		for (DatabaseError err : errlist) {
			if (! DatabaseAutofixer.canFix(errorList, err)) {
				DialogHelper.showDispatchInformation(this, getTitle(), LocaleBundle.getString("CheckDatabaseDialog.fixSelectedMessage.Unfixable") + "\n\n" + err.getFullErrorString()); //$NON-NLS-1$
				return;
			}
		}

		final List<DatabaseError> toFix = new ArrayList<>(errlist);

		startFixThread("THREAD_FIXSELECTED_DB", toFix, (success, cancelled, fixed) -> //$NON-NLS-1$
		{
			errorList.removeAll(fixed);
			updateLists();

			if (cancelled) {
				DialogHelper.showDispatchLocalInformation(CheckDatabaseFrame.this, "CheckDatabaseDialog.Autofix.dialogCancelled"); //$NON-NLS-1$
			} else if (success) {
				DialogHelper.showDispatchInformation(CheckDatabaseFrame.this, getTitle(), LocaleBundle.getString("CheckDatabaseDialog.fixSelectedMessage.Fixed")); //$NON-NLS-1$
			} else {
				DialogHelper.showDispatchInformation(CheckDatabaseFrame.this, getTitle(), LocaleBundle.getString("CheckDatabaseDialog.fixSelectedMessage.Failed")); //$NON-NLS-1$
			}
		});
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
		btnPresetFull = new JButton();
		btnPresetDefault = new JButton();
		btnPresetQuick = new JButton();
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
		pnlProgress = new JPanel();
		pbProgress1 = new JProgressBar();
		lblProgress1 = new JLabel();
		btnCancelAutofix = new JButton();
		pbProgress2 = new JProgressBar();
		lblProgress2 = new JLabel();
		panel2 = new JPanel();
		cbValMovies = new JCheckBox();
		icnValMovies = new JLabel();
		cbValCovers = new JCheckBox();
		icnValCovers = new JLabel();
		cbValCoverFiles = new JCheckBox();
		icnValCoverFiles = new JLabel();
		cbValDatabase = new JCheckBox();
		icnValDatabase = new JLabel();
		cbValSeries = new JCheckBox();
		icnValSeries = new JLabel();
		cbValGroups = new JCheckBox();
		icnValGroups = new JLabel();
		cbValVideoFiles = new JCheckBox();
		icnValVideoFiles = new JLabel();
		cbValDuplicates = new JCheckBox();
		icnValDuplicates = new JLabel();
		cbValSeasons = new JCheckBox();
		icnValSeasons = new JLabel();
		cbValOnlineRefs = new JCheckBox();
		icnValOnlineRefs = new JLabel();
		cbValNfoFiles = new JCheckBox();
		icnValNfoFiles = new JLabel();
		cbValSeriesStructure = new JCheckBox();
		icnValSeriesStructure = new JLabel();
		cbValEpisodes = new JCheckBox();
		icnValEpisodes = new JLabel();
		cbValEmptyDirs = new JCheckBox();
		icnValEmptyDirs = new JLabel();
		cbValNfoFullCompare = new JCheckBox();
		icnValNfoFullCompare = new JLabel();

		//======== this ========
		setTitle(LocaleBundle.getString("CheckDatabaseDialog.this.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(650, 400));
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $lcgap, 226dlu, $ugap",
			"$ugap, default, $lgap, default:grow, 2*($lgap, default), $ugap"));

		//======== panel1 ========
		{
			panel1.setLayout(new FormLayout(
				"5*(default, $lcgap), default:grow, 2*($lcgap, default)",
				"default"));

			//---- btnValidate ----
			btnValidate.setText(LocaleBundle.getString("CheckDatabaseDialog.btnValidate.text"));
			btnValidate.setFont(btnValidate.getFont().deriveFont(btnValidate.getFont().getStyle() | Font.BOLD));
			btnValidate.addActionListener(e -> startValidate());
			panel1.add(btnValidate, CC.xy(1, 1));

			//---- btnPresetFull ----
			btnPresetFull.setText(LocaleBundle.getString("CheckDatabaseFrame.btnPresetFull.text"));
			btnPresetFull.addActionListener(e -> presetFull());
			panel1.add(btnPresetFull, CC.xy(3, 1));

			//---- btnPresetDefault ----
			btnPresetDefault.setText(LocaleBundle.getString("CheckDatabaseFrame.btnPresetDefault.text"));
			btnPresetDefault.addActionListener(e -> presetDefault());
			panel1.add(btnPresetDefault, CC.xy(5, 1));

			//---- btnPresetQuick ----
			btnPresetQuick.setText(LocaleBundle.getString("CheckDatabaseFrame.btnPresetQuick.text"));
			btnPresetQuick.addActionListener(e -> presetQuick());
			panel1.add(btnPresetQuick, CC.xy(7, 1));

			//---- lblInfo ----
			lblInfo.setText(LocaleBundle.getString("CheckDatabaseDialog.lblInfo.text"));
			panel1.add(lblInfo, CC.xy(9, 1));

			//---- btnAutofix ----
			btnAutofix.setText(LocaleBundle.getString("CheckDatabaseDialog.btnAutofix.text"));
			btnAutofix.addActionListener(e -> autoFix());
			panel1.add(btnAutofix, CC.xy(13, 1));

			//---- btnFixSelected ----
			btnFixSelected.setText(LocaleBundle.getString("CheckDatabaseDialog.btnFixSelected.text"));
			btnFixSelected.addActionListener(e -> fixSelected());
			panel1.add(btnFixSelected, CC.xy(15, 1));
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
					edMetadata.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 21));
					scrollPane3.setViewportView(edMetadata);
				}
				splitPane2.setBottomComponent(scrollPane3);
			}
			splitPane1.setRightComponent(splitPane2);
		}
		contentPane.add(splitPane1, CC.xywh(2, 4, 3, 1, CC.FILL, CC.FILL));

		//======== pnlProgress ========
		{
			pnlProgress.setLayout(new FormLayout(
				"default:grow, $lcgap, 226dlu, $lcgap, default",
				"default, $lgap, default"));
			pnlProgress.add(pbProgress1, CC.xy(1, 1, CC.FILL, CC.DEFAULT));

			//---- lblProgress1 ----
			lblProgress1.setText("<dynamic>");
			pnlProgress.add(lblProgress1, CC.xy(3, 1));

			//---- btnCancelAutofix ----
			btnCancelAutofix.setText(LocaleBundle.getString("UIGeneric.btnCancel.text"));
			btnCancelAutofix.addActionListener(e -> cancelAutofix(e));
			pnlProgress.add(btnCancelAutofix, CC.xywh(5, 1, 1, 3, CC.DEFAULT, CC.FILL));
			pnlProgress.add(pbProgress2, CC.xy(1, 3, CC.FILL, CC.DEFAULT));

			//---- lblProgress2 ----
			lblProgress2.setText("<dynamic>");
			pnlProgress.add(lblProgress2, CC.xy(3, 3));
		}
		contentPane.add(pnlProgress, CC.xywh(2, 6, 3, 1, CC.FILL, CC.DEFAULT));

		//======== panel2 ========
		{
			panel2.setLayout(new FormLayout(
				"0dlu:grow, $lcgap, default, $ugap, 1dlu:grow, $lcgap, default, $ugap, 1dlu:grow, $lcgap, default, $ugap, 1dlu:grow, $lcgap, default",
				"3*(default, $lgap), default"));

			//---- cbValMovies ----
			cbValMovies.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValMovies"));
			cbValMovies.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValMovies, CC.xy(1, 1));

			//---- icnValMovies ----
			icnValMovies.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValMovies.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValMovies.tooltip"));
			panel2.add(icnValMovies, CC.xy(3, 1));

			//---- cbValCovers ----
			cbValCovers.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValCovers.text"));
			cbValCovers.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValCovers, CC.xy(5, 1));

			//---- icnValCovers ----
			icnValCovers.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValCovers.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValCovers.tooltip"));
			panel2.add(icnValCovers, CC.xy(7, 1));

			//---- cbValCoverFiles ----
			cbValCoverFiles.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValCoverFiles"));
			cbValCoverFiles.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValCoverFiles, CC.xy(9, 1));

			//---- icnValCoverFiles ----
			icnValCoverFiles.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValCoverFiles.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValCoverFiles.tooltip"));
			panel2.add(icnValCoverFiles, CC.xy(11, 1));

			//---- cbValDatabase ----
			cbValDatabase.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValDatabase"));
			cbValDatabase.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValDatabase, CC.xy(13, 1));

			//---- icnValDatabase ----
			icnValDatabase.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValDatabase.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValDatabase.tooltip"));
			panel2.add(icnValDatabase, CC.xy(15, 1));

			//---- cbValSeries ----
			cbValSeries.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValSeries"));
			cbValSeries.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValSeries, CC.xy(1, 3));

			//---- icnValSeries ----
			icnValSeries.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValSeries.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValSeries.tooltip"));
			panel2.add(icnValSeries, CC.xy(3, 3));

			//---- cbValGroups ----
			cbValGroups.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValGroups.text"));
			cbValGroups.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValGroups, CC.xy(5, 3));

			//---- icnValGroups ----
			icnValGroups.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValGroups.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValGroups.tooltip"));
			panel2.add(icnValGroups, CC.xy(7, 3));

			//---- cbValVideoFiles ----
			cbValVideoFiles.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValVideoFiles"));
			cbValVideoFiles.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValVideoFiles, CC.xy(9, 3));

			//---- icnValVideoFiles ----
			icnValVideoFiles.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValVideoFiles.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValVideoFiles.tooltip"));
			panel2.add(icnValVideoFiles, CC.xy(11, 3));

			//---- cbValDuplicates ----
			cbValDuplicates.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValDuplicates"));
			cbValDuplicates.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValDuplicates, CC.xy(13, 3));

			//---- icnValDuplicates ----
			icnValDuplicates.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValDuplicates.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValDuplicates.tooltip"));
			panel2.add(icnValDuplicates, CC.xy(15, 3));

			//---- cbValSeasons ----
			cbValSeasons.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValSeasons"));
			cbValSeasons.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValSeasons, CC.xy(1, 5));

			//---- icnValSeasons ----
			icnValSeasons.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValSeasons.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValSeasons.tooltip"));
			panel2.add(icnValSeasons, CC.xy(3, 5));

			//---- cbValOnlineRefs ----
			cbValOnlineRefs.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValOnlineRefs.text"));
			cbValOnlineRefs.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValOnlineRefs, CC.xy(5, 5));

			//---- icnValOnlineRefs ----
			icnValOnlineRefs.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValOnlineRefs.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValOnlineRefs.tooltip"));
			panel2.add(icnValOnlineRefs, CC.xy(7, 5));

			//---- cbValNfoFiles ----
			cbValNfoFiles.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValNfoFiles.text"));
			cbValNfoFiles.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValNfoFiles, CC.xy(9, 5));

			//---- icnValNfoFiles ----
			icnValNfoFiles.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValNfoFiles.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValNfoFiles.tooltip"));
			panel2.add(icnValNfoFiles, CC.xy(11, 5));

			//---- cbValSeriesStructure ----
			cbValSeriesStructure.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValSeriesStructure"));
			cbValSeriesStructure.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValSeriesStructure, CC.xy(13, 5));

			//---- icnValSeriesStructure ----
			icnValSeriesStructure.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValSeriesStructure.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValSeriesStructure.tooltip"));
			panel2.add(icnValSeriesStructure, CC.xy(15, 5));

			//---- cbValEpisodes ----
			cbValEpisodes.setText(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValEpisodes"));
			cbValEpisodes.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValEpisodes, CC.xy(1, 7));

			//---- icnValEpisodes ----
			icnValEpisodes.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValEpisodes.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValEpisodes.tooltip"));
			panel2.add(icnValEpisodes, CC.xy(3, 7));

			//---- cbValEmptyDirs ----
			cbValEmptyDirs.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValEmptyDirs"));
			cbValEmptyDirs.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValEmptyDirs, CC.xy(9, 7));

			//---- icnValEmptyDirs ----
			icnValEmptyDirs.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValEmptyDirs.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValEmptyDirs.tooltip"));
			panel2.add(icnValEmptyDirs, CC.xy(11, 7));

			//---- cbValNfoFullCompare ----
			cbValNfoFullCompare.setText(LocaleBundle.getString("CheckDatabaseFrame.cbValNfoFullCompare.text"));
			cbValNfoFullCompare.addItemListener(e -> cbxAnyItemStateChanged(e));
			panel2.add(cbValNfoFullCompare, CC.xy(13, 7));

			//---- icnValNfoFullCompare ----
			icnValNfoFullCompare.setIcon(new ImageIcon(getClass().getResource("/icons/table/type/info_16x16.png")));
			icnValNfoFullCompare.setToolTipText(LocaleBundle.getString("CheckDatabaseFrame.cbValNfoFullCompare.tooltip"));
			panel2.add(icnValNfoFullCompare, CC.xy(15, 7));
		}
		contentPane.add(panel2, CC.xywh(2, 8, 3, 1));
		setSize(1200, 700);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panel1;
	private JButton btnValidate;
	private JButton btnPresetFull;
	private JButton btnPresetDefault;
	private JButton btnPresetQuick;
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
	private JPanel pnlProgress;
	private JProgressBar pbProgress1;
	private JLabel lblProgress1;
	private JButton btnCancelAutofix;
	private JProgressBar pbProgress2;
	private JLabel lblProgress2;
	private JPanel panel2;
	private JCheckBox cbValMovies;
	private JLabel icnValMovies;
	private JCheckBox cbValCovers;
	private JLabel icnValCovers;
	private JCheckBox cbValCoverFiles;
	private JLabel icnValCoverFiles;
	private JCheckBox cbValDatabase;
	private JLabel icnValDatabase;
	private JCheckBox cbValSeries;
	private JLabel icnValSeries;
	private JCheckBox cbValGroups;
	private JLabel icnValGroups;
	private JCheckBox cbValVideoFiles;
	private JLabel icnValVideoFiles;
	private JCheckBox cbValDuplicates;
	private JLabel icnValDuplicates;
	private JCheckBox cbValSeasons;
	private JLabel icnValSeasons;
	private JCheckBox cbValOnlineRefs;
	private JLabel icnValOnlineRefs;
	private JCheckBox cbValNfoFiles;
	private JLabel icnValNfoFiles;
	private JCheckBox cbValSeriesStructure;
	private JLabel icnValSeriesStructure;
	private JCheckBox cbValEpisodes;
	private JLabel icnValEpisodes;
	private JCheckBox cbValEmptyDirs;
	private JLabel icnValEmptyDirs;
	private JCheckBox cbValNfoFullCompare;
	private JLabel icnValNfoFullCompare;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
