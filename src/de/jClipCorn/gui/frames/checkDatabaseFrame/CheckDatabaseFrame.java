package de.jClipCorn.gui.frames.checkDatabaseFrame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseErrors.DatabaseAutofixer;
import de.jClipCorn.database.databaseErrors.DatabaseError;
import de.jClipCorn.database.databaseErrors.DatabaseErrorType;
import de.jClipCorn.database.databaseErrors.DatabaseValidator;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
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
	private JList<DatabaseErrorType> lsCategories;
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
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setTitle(LocaleBundle.getString("CheckDatabaseDialog.this.title")); //$NON-NLS-1$
		setBounds(100, 100, 750, 400);
		
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 2));
		
		pnlTop = new JPanel();
		FlowLayout fl_pnlTop = (FlowLayout) pnlTop.getLayout();
		fl_pnlTop.setAlignment(FlowLayout.LEFT);
		contentPanel.add(pnlTop, BorderLayout.NORTH);
		
		btnValidate = new JButton(LocaleBundle.getString("CheckDatabaseDialog.btnValidate.text")); //$NON-NLS-1$
		btnValidate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startValidate();
			}
		});
		pnlTop.add(btnValidate);
		
		pBar = new JProgressBar();
		contentPanel.add(pBar, BorderLayout.SOUTH);
		
		lblInfo = new JLabel();
		pnlTop.add(lblInfo);
		
		btnAutofix = new JButton(LocaleBundle.getString("CheckDatabaseDialog.btnAutofix.text")); //$NON-NLS-1$
		btnAutofix.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				autoFix();
			}
		});
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
		
		lsCategories.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				ListModel<DatabaseError> lm = lsMain.getModel();
				if (lm instanceof DatabaseErrorListModel) {
					((DatabaseErrorListModel)lm).updateFilter(lsCategories.getSelectedValue());
				}
			}
		});
		
		btnFixselected = new JButton(LocaleBundle.getString("CheckDatabaseDialog.btnFixSelected.text")); //$NON-NLS-1$
		btnFixselected.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fixSelected();
			}
		});
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
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean succ = DatabaseAutofixer.fixErrors(errorList, new ProgressCallbackProgressBarHelper(pBar));
				endFixThread(succ);
			}
		}, "THREAD_AUTOFIX_DB").start(); //$NON-NLS-1$
	}
	
	private void endThread() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				btnValidate.setEnabled(true);
				btnFixselected.setEnabled(errorList.size() > 0);
				btnAutofix.setEnabled(errorList.size() > 0);
				lblInfo.setText(LocaleBundle.getFormattedString("CheckDatabaseDialog.lblInfo.text_2", errorList.size())); //$NON-NLS-1$
			}
		});
	}
	
	private void endFixThread(final boolean success) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				btnValidate.setEnabled(true);
				btnAutofix.setEnabled(true);
				
				if (success) {
					DialogHelper.showLocalInformation(CheckDatabaseFrame.this, "CheckDatabaseDialog.Autofix.dialogSuccessfull"); //$NON-NLS-1$
				} else {
					DialogHelper.showLocalInformation(CheckDatabaseFrame.this, "CheckDatabaseDialog.Autofix.dialogUnsuccessfull"); //$NON-NLS-1$
				}
			}
		});
	}
	
	private void setMainListModel(final ListModel<DatabaseError> lm) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					lsMain.setModel(lm);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
		}
	}
	
	private void setCategoriesListModel(final ListModel<DatabaseErrorType> lm) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					lsCategories.setModel(lm);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			CCLog.addError(e);
		}
	}
	
	private void startValidate() {
		btnFixselected.setEnabled(false);
		btnValidate.setEnabled(false);
		btnAutofix.setEnabled(false);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<DatabaseError> errors = new ArrayList<>();
				
				DatabaseValidator.startValidate(errors, movielist, new ProgressCallbackProgressBarHelper(pBar));
				
				errorList = errors;
				
				updateLists();
				
				endThread();
			}
		}, "THREAD_VALIDATE_DATABASE").start(); //$NON-NLS-1$
	}

	private void updateLists() { // Threadsafe
		DatabaseErrorListModel dlm = new DatabaseErrorListModel();
		DefaultListModel<DatabaseErrorType> clm = new DefaultListModel<>();
		
		clm.addElement(null); //null = "All" (not sure if good code or lazy code)
		
		for (DatabaseError de : errorList) {
			dlm.addElement(de);
			
			boolean found = false;
			
			for (int i = 0; i < clm.getSize(); i++) {
				if (! found && de.getType().equals(clm.get(i))) {
					found = true;
					clm.get(i).incCount();
				}
			}
			
			if (! found) {
				clm.addElement(de.getType().copy(1));
			}
		}
		
		setMainListModel(dlm);
		
		if (clm.size() <= 1) {
			clm.clear();
		}
		
		setCategoriesListModel(clm);
	}
	
	private void fixSelected() {
		DatabaseError e = lsMain.getSelectedValue();
		
		if (e == null) {
			DialogHelper.showInformation(this, getTitle(), LocaleBundle.getString("CheckDatabaseDialog.fixSelectedMessage.NoSelection")); //$NON-NLS-1$
			return;
		}
		
		if (! DatabaseAutofixer.canFix(errorList, e)) {
			DialogHelper.showInformation(this, getTitle(), LocaleBundle.getString("CheckDatabaseDialog.fixSelectedMessage.Unfixable")); //$NON-NLS-1$
			return;
		}
		
		if (e.autoFix()) {
			DialogHelper.showInformation(this, getTitle(), LocaleBundle.getString("CheckDatabaseDialog.fixSelectedMessage.Fixed")); //$NON-NLS-1$
			
			errorList.remove(e);
			updateLists();
		} else {
			DialogHelper.showInformation(this, getTitle(), LocaleBundle.getString("CheckDatabaseDialog.fixSelectedMessage.Failed")); //$NON-NLS-1$
		}
	}
}
