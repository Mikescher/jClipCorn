package de.jClipCorn.gui.frames.checkDatabaseFrame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.databaseErrors.DatabaseAutofixer;
import de.jClipCorn.features.databaseErrors.DatabaseError;
import de.jClipCorn.features.databaseErrors.DatabaseErrorType;
import de.jClipCorn.features.databaseErrors.DatabaseValidator;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.datatypes.CountAppendix;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.listener.ProgressCallbackProgressBarHelper;

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
	private JProgressBar pBar;
	private JButton btnAutofix;
	private JScrollPane scrlPnlLeft;
	private JList<CountAppendix<DatabaseErrorType>> lsCategories;
	private JSplitPane pnlCenter;
	private JButton btnFixselected;
	
	public CheckDatabaseFrame(CCMovieList ml, MainFrame owner) {
		super();
		this.movielist = ml; 
		
		initGUI();
		
		setLocationRelativeTo(owner);
		
		lblInfo.setText(LocaleBundle.getFormattedString("CheckDatabaseDialog.lblInfo.text", ml.getElementCount())); //$NON-NLS-1$
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle(LocaleBundle.getString("CheckDatabaseDialog.this.title")); //$NON-NLS-1$
		setBounds(100, 100, 750, 400);
		
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 2));
		
		pnlTop = new JPanel();
		FlowLayout fl_pnlTop = (FlowLayout) pnlTop.getLayout();
		fl_pnlTop.setAlignment(FlowLayout.LEFT);
		contentPanel.add(pnlTop, BorderLayout.NORTH);
		
		btnValidate = new JButton(LocaleBundle.getString("CheckDatabaseDialog.btnValidate.text")); //$NON-NLS-1$
		btnValidate.addActionListener(arg0 -> startValidate());
		pnlTop.add(btnValidate);
		
		pBar = new JProgressBar();
		contentPanel.add(pBar, BorderLayout.SOUTH);
		
		lblInfo = new JLabel();
		pnlTop.add(lblInfo);
		
		btnAutofix = new JButton(LocaleBundle.getString("CheckDatabaseDialog.btnAutofix.text")); //$NON-NLS-1$
		btnAutofix.addActionListener(arg0 -> autoFix());
		btnAutofix.setEnabled(false);
		pnlTop.add(btnAutofix);
		
		pnlCenter = new JSplitPane();
		pnlCenter.setContinuousLayout(true);
		contentPanel.add(pnlCenter, BorderLayout.CENTER);
		
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
				((DatabaseErrorListModel)lm).updateFilter(lsCategories.getSelectedValue().Value);
			}
		});
		
		btnFixselected = new JButton(LocaleBundle.getString("CheckDatabaseDialog.btnFixSelected.text")); //$NON-NLS-1$
		btnFixselected.addActionListener(arg0 -> fixSelected());
		btnFixselected.setEnabled(false);
		pnlTop.add(btnFixselected);
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
			boolean succ = DatabaseAutofixer.fixErrors(errorList, new ProgressCallbackProgressBarHelper(pBar));
			endFixThread(succ);
		}, "THREAD_AUTOFIX_DB").start(); //$NON-NLS-1$
	}
	
	private void endThread() {
		SwingUtilities.invokeLater(() ->
		{
			btnValidate.setEnabled(true);
			btnFixselected.setEnabled(errorList.size() > 0);
			btnAutofix.setEnabled(errorList.size() > 0);
			lblInfo.setText(LocaleBundle.getFormattedString("CheckDatabaseDialog.lblInfo.text_2", errorList.size())); //$NON-NLS-1$
		});
	}
	
	private void endFixThread(final boolean success) {
		SwingUtilities.invokeLater(() ->
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
				SwingUtilities.invokeAndWait(() -> lsMain.setModel(lm));
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
				SwingUtilities.invokeAndWait(() -> lsCategories.setModel(lm));
			} catch (InvocationTargetException | InterruptedException e) {
				CCLog.addError(e);
			}
		}
	}
	
	private void startValidate() {
		btnFixselected.setEnabled(false);
		btnValidate.setEnabled(false);
		btnAutofix.setEnabled(false);
		
		new Thread(() ->
		{
			List<DatabaseError> errors = new ArrayList<>();

			DatabaseValidator.startValidate(errors, movielist, new ProgressCallbackProgressBarHelper(pBar));

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

	@SuppressWarnings("nls")
	private String typeToString(CountAppendix<DatabaseErrorType> v) {
		if (v.getCount() == 0) {
			return LocaleBundle.getString(String.format("CheckDatabaseDialog.Errornames.ERR_%02d", v.Value.getType()));
		} else {
			return LocaleBundle.getString(String.format("CheckDatabaseDialog.Errornames.ERR_%02d", v.Value.getType())) + " (" + v.getCount() + ")";
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
