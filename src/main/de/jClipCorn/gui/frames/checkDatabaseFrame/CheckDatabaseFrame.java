package de.jClipCorn.gui.frames.checkDatabaseFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.databaseErrors.*;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.adapter.ItemChangeLambdaAdapter;
import de.jClipCorn.util.datatypes.CountAppendix;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.DoubleProgressCallbackProgressBarHelper;
import de.jClipCorn.util.listener.ProgressCallbackProgressBarHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CheckDatabaseFrame extends JFrame {
	private static final long serialVersionUID = 8481907373850170115L;

	private final CCMovieList movielist;
	
	private List<DatabaseError> errorList;
	
	private final JPanel contentPanel = new JPanel();
	private JPanel pnlTop;
	private JScrollPane scrlPnlRight;
	private JList<DatabaseError> lsMain;
	private JButton btnValidate;
	private JLabel lblInfo;
	private JProgressBar pbProgress1;
	private JButton btnAutofix;
	private JScrollPane scrlPnlLeft;
	private JList<CountAppendix<DatabaseErrorType>> lsCategories;
	private JSplitPane pnlCenter;
	private JButton btnFixselected;
	private JPanel panel;
	private JCheckBox cbValMovies;
	private JCheckBox cbValSeries;
	private JCheckBox cbValSeasons;
	private JCheckBox cbValEpisodes;
	private JCheckBox cbValCoverFiles;
	private JCheckBox cbValAdditional;
	private JCheckBox cbValVideoFiles;
	private JProgressBar pbProgress2;
	private JLabel lblProgress1;
	private JLabel lblProgress2;
	
	public CheckDatabaseFrame(CCMovieList ml, MainFrame owner) {
		super();
		this.movielist = ml; 
		
		initGUI();
		
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle(LocaleBundle.getString("CheckDatabaseDialog.this.title")); //$NON-NLS-1$
		setBounds(100, 100, 750, 500);
		
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("225dlu"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.PREF_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,}));
		
		pnlTop = new JPanel();
		FlowLayout fl_pnlTop = (FlowLayout) pnlTop.getLayout();
		fl_pnlTop.setAlignment(FlowLayout.LEFT);
		contentPanel.add(pnlTop, "1, 1, 4, 1, fill, top"); //$NON-NLS-1$
		
		btnValidate = new JButton(LocaleBundle.getString("CheckDatabaseDialog.btnValidate.text")); //$NON-NLS-1$
		btnValidate.addActionListener(arg0 -> startValidate());
		pnlTop.add(btnValidate);
		
		pbProgress1 = new JProgressBar();
		contentPanel.add(pbProgress1, "2, 4, fill, fill"); //$NON-NLS-1$
		
		lblInfo = new JLabel();
		pnlTop.add(lblInfo);
		
		btnAutofix = new JButton(LocaleBundle.getString("CheckDatabaseDialog.btnAutofix.text")); //$NON-NLS-1$
		btnAutofix.addActionListener(arg0 -> autoFix());
		btnAutofix.setEnabled(false);
		pnlTop.add(btnAutofix);
		
		pnlCenter = new JSplitPane();
		pnlCenter.setContinuousLayout(true);
		contentPanel.add(pnlCenter, "1, 2, 4, 1, fill, fill"); //$NON-NLS-1$
		
		scrlPnlLeft = new JScrollPane();
		pnlCenter.setLeftComponent(scrlPnlLeft);
		
		lsCategories = new JList<>();
		lsCategories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsCategories.setCellRenderer(new ErrorTypeListCellRenderer());
		scrlPnlLeft.setViewportView(lsCategories);
		
		scrlPnlRight = new JScrollPane();
		pnlCenter.setRightComponent(scrlPnlRight);
		scrlPnlRight.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		lsMain = new JList<>();
		lsMain.setCellRenderer(new DatabaseErrorListCellRenderer());
		lsMain.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrlPnlRight.setViewportView(lsMain);
		lsMain.addMouseListener(new MouseListener() {			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					onDblClick();
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// nothing
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// nothing
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// nothing
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// nothing
			}
		});
		
		lsCategories.addListSelectionListener(arg0 ->
		{
			ListModel<DatabaseError> lm = lsMain.getModel();
			if (lm instanceof DatabaseErrorListModel) {
				if (lsCategories.getSelectedValue() != null)
					((DatabaseErrorListModel)lm).updateFilter(lsCategories.getSelectedValue().Value);
				else
					((DatabaseErrorListModel)lm).updateFilter(null);
			}
		});
		
		btnFixselected = new JButton(LocaleBundle.getString("CheckDatabaseDialog.btnFixSelected.text")); //$NON-NLS-1$
		btnFixselected.addActionListener(arg0 -> fixSelected());
		btnFixselected.setEnabled(false);
		pnlTop.add(btnFixselected);

		lblInfo.setText(LocaleBundle.getFormattedString("CheckDatabaseDialog.lblInfo.text", movielist.getElementCount())); //$NON-NLS-1$

		lblProgress1 = new JLabel(""); //$NON-NLS-1$
		contentPanel.add(lblProgress1, "4, 4, fill, fill"); //$NON-NLS-1$

		pbProgress2 = new JProgressBar();
		contentPanel.add(pbProgress2, "2, 6, fill, fill"); //$NON-NLS-1$

		lblProgress2 = new JLabel(""); //$NON-NLS-1$
		contentPanel.add(lblProgress2, "4, 6, fill, fill"); //$NON-NLS-1$

		panel = new JPanel();
		contentPanel.add(panel, "1, 8, 4, 1, fill, fill"); //$NON-NLS-1$
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				ColumnSpec.decode("12dlu"), //$NON-NLS-1$
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,}));

		cbValMovies = new JCheckBox(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValMovies")); //$NON-NLS-1$
		cbValMovies.setSelected(CCProperties.getInstance().PROP_CHECKDATABASE_OPT_MOVIES.getValue());
		cbValMovies.addItemListener(new ItemChangeLambdaAdapter(() -> CCProperties.getInstance().PROP_CHECKDATABASE_OPT_MOVIES.setValue(cbValMovies.isSelected()), -1));
		panel.add(cbValMovies, "2, 1, fill, fill"); //$NON-NLS-1$

		cbValCoverFiles = new JCheckBox(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValCoverFiles")); //$NON-NLS-1$
		cbValCoverFiles.setSelected(CCProperties.getInstance().PROP_CHECKDATABASE_OPT_COVERS.getValue());
		cbValCoverFiles.addItemListener(new ItemChangeLambdaAdapter(() -> CCProperties.getInstance().PROP_CHECKDATABASE_OPT_COVERS.setValue(cbValCoverFiles.isSelected()), -1));
		panel.add(cbValCoverFiles, "4, 1, fill, fill"); //$NON-NLS-1$

		cbValSeries = new JCheckBox(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValSeries")); //$NON-NLS-1$
		cbValSeries.setSelected(CCProperties.getInstance().PROP_CHECKDATABASE_OPT_SERIES.getValue());
		cbValSeries.addItemListener(new ItemChangeLambdaAdapter(() -> CCProperties.getInstance().PROP_CHECKDATABASE_OPT_SERIES.setValue(cbValSeries.isSelected()), -1));
		panel.add(cbValSeries, "2, 3, fill, fill"); //$NON-NLS-1$

		cbValVideoFiles = new JCheckBox(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValVideoFiles")); //$NON-NLS-1$
		cbValVideoFiles.setSelected(CCProperties.getInstance().PROP_CHECKDATABASE_OPT_FILES.getValue());
		cbValVideoFiles.addItemListener(new ItemChangeLambdaAdapter(() -> CCProperties.getInstance().PROP_CHECKDATABASE_OPT_FILES.setValue(cbValVideoFiles.isSelected()), -1));
		panel.add(cbValVideoFiles, "4, 3, fill, fill"); //$NON-NLS-1$

		cbValSeasons = new JCheckBox(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValSeasons")); //$NON-NLS-1$
		cbValSeasons.setSelected(CCProperties.getInstance().PROP_CHECKDATABASE_OPT_SEASONS.getValue());
		cbValSeasons.addItemListener(new ItemChangeLambdaAdapter(() -> CCProperties.getInstance().PROP_CHECKDATABASE_OPT_SEASONS.setValue(cbValSeasons.isSelected()), -1));
		panel.add(cbValSeasons, "2, 5, fill, fill"); //$NON-NLS-1$

		cbValEpisodes = new JCheckBox(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValEpisodes")); //$NON-NLS-1$
		cbValEpisodes.setSelected(CCProperties.getInstance().PROP_CHECKDATABASE_OPT_EPISODES.getValue());
		cbValEpisodes.addItemListener(new ItemChangeLambdaAdapter(() -> CCProperties.getInstance().PROP_CHECKDATABASE_OPT_EPISODES.setValue(cbValEpisodes.isSelected()), -1));
		panel.add(cbValEpisodes, "2, 7, fill, fill"); //$NON-NLS-1$

		cbValAdditional = new JCheckBox(LocaleBundle.getString("CheckDatabaseDialog.checkbox.cbValAdditional")); //$NON-NLS-1$
		cbValAdditional.setSelected(CCProperties.getInstance().PROP_CHECKDATABASE_OPT_EXTRA.getValue());
		cbValAdditional.addItemListener(new ItemChangeLambdaAdapter(() -> CCProperties.getInstance().PROP_CHECKDATABASE_OPT_EXTRA.setValue(cbValAdditional.isSelected()), -1));
		panel.add(cbValAdditional, "4, 7, fill, fill"); //$NON-NLS-1$
	}
	
	private void onDblClick() {
		if (lsMain.getSelectedIndex() >= 0) {
			lsMain.getSelectedValue().startEditing(this);
		}
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
			
			boolean succ = DatabaseAutofixer.fixErrors(errorList, new ProgressCallbackProgressBarHelper(pbProgress1, 500));
			endFixThread(succ);
		}, "THREAD_AUTOFIX_DB").start(); //$NON-NLS-1$
	}
	
	private void endThread() {
		SwingUtils.invokeLater(() ->
		{
			btnValidate.setEnabled(true);
			btnFixselected.setEnabled(errorList.size() > 0);
			btnAutofix.setEnabled(errorList.size() > 0);
			lblInfo.setText(LocaleBundle.getFormattedString("CheckDatabaseDialog.lblInfo.text_2", errorList.size())); //$NON-NLS-1$
		});
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
	
	private void startValidate() {
		btnFixselected.setEnabled(false);
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
			CCProperties.getInstance().PROP_VALIDATE_CHECK_SERIES_STRUCTURE.getValue(),
			CCProperties.getInstance().PROP_VALIDATE_DUP_IGNORE_IFO.getValue()
		);

		new Thread(() ->
		{
			pbProgress1.setVisible(true);
			lblProgress1.setVisible(true);
			pbProgress2.setVisible(true);
			lblProgress2.setVisible(true);
			
			List<DatabaseError> errors = new ArrayList<>();

			var validator = new CCDatabaseValidator(movielist);
			validator.validate(errors, opts, new DoubleProgressCallbackProgressBarHelper(pbProgress1, lblProgress1, pbProgress2, lblProgress2));

			errorList = errors;

			updateLists();

			endThread();
		}, "THREAD_VALIDATE_DATABASE").start(); //$NON-NLS-1$
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

	private String typeToString(CountAppendix<DatabaseErrorType> v) {
		if (v.getCount() == 0) {
			return LocaleBundle.getString(String.format("CheckDatabaseDialog.Errornames.ERR_%02d", v.Value.getType())); //$NON-NLS-1$
		} else {
			return LocaleBundle.getString(String.format("CheckDatabaseDialog.Errornames.ERR_%02d", v.Value.getType())) + " (" + v.getCount() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
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
}
