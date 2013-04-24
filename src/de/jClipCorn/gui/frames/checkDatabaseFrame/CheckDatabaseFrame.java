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
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseErrors.DatabaseAutofixer;
import de.jClipCorn.database.databaseErrors.DatabaseError;
import de.jClipCorn.database.databaseErrors.DatabaseValidator;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.DialogHelper;
import de.jClipCorn.util.ProgressCallbackHelper;

public class CheckDatabaseFrame extends JFrame {
	private static final long serialVersionUID = 8481907373850170115L;

	private final CCMovieList movielist;
	
	private List<DatabaseError> errorList;
	
	private final JPanel contentPanel = new JPanel();
	private JPanel pnlTop;
	private JScrollPane scrollPane;
	private JList<DatabaseError> lsMain;
	private JButton btnValidate;
	private JLabel lblInfo;
	private JProgressBar pBar;
	private JButton btnAutofix;
	
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
		contentPanel.setLayout(new BorderLayout(0, 0));
		
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
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		
		lsMain = new JList<>();
		scrollPane.setViewportView(lsMain);
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
				boolean succ = DatabaseAutofixer.fixErrors(errorList, new ProgressCallbackHelper(pBar));
				endFixThread(succ);
			}
		}, "THREAD_AUTOFIX_DB").start(); //$NON-NLS-1$
	}
	
	private void endThread() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				btnValidate.setEnabled(true);
				btnAutofix.setEnabled(true);
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
	
	private void setListModel(final ListModel<DatabaseError> lm) {
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
	
	private void startValidate() {
		btnValidate.setEnabled(false);
		btnAutofix.setEnabled(false);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<DatabaseError> errors = new ArrayList<>();
				
				DatabaseValidator.startValidate(errors, movielist, new ProgressCallbackHelper(pBar));
				
				DefaultListModel<DatabaseError> dlm = new DefaultListModel<>();
				
				for (DatabaseError de : errors) {
					dlm.addElement(de);
				}
				
				setListModel(dlm);
				
				errorList = errors;
				
				endThread();
			}
		}, "THREAD_VALIDATE_DATABASE").start(); //$NON-NLS-1$
	}
}
