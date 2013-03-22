package de.jClipCorn.gui.frames.checkDatabaseFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseErrors.DatabaseError;
import de.jClipCorn.database.databaseErrors.DatabaseValidator;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.ProgressCallbackListener;

public class CheckDatabaseDialog extends JDialog {
	private static final long serialVersionUID = 8481907373850170115L;

	private final CCMovieList movielist;
	
	private final JPanel contentPanel = new JPanel();
	private JPanel pnlTop;
	private JScrollPane scrollPane;
	private JList<String> lsMain;
	private JButton btnValidate;
	private JLabel lblInfo;
	private JProgressBar pBar;
	
	public CheckDatabaseDialog(CCMovieList ml, MainFrame owner) {
		super();
		this.movielist = ml;
		
		initGUI(owner);
		
		lblInfo.setText(LocaleBundle.getFormattedString("CheckDatabaseDialog.lblInfo.text", ml.getElementCount())); //$NON-NLS-1$
		
		pBar = new JProgressBar();
		contentPanel.add(pBar, BorderLayout.SOUTH);
	}
	
	private void initGUI(Component owner) {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setTitle(LocaleBundle.getString("CheckDatabaseDialog.this.title")); //$NON-NLS-1$
		setModal(false); //Modality is sucking ***
		setBounds(100, 100, 750, 400);
		setLocationRelativeTo(owner);
		
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
		
		lblInfo = new JLabel();
		pnlTop.add(lblInfo);
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		
		lsMain = new JList<>();
		scrollPane.setViewportView(lsMain);
	}
	
	private void stepProgress() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					pBar.setValue(pBar.getValue() + 1);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void endThread() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					pBar.setValue(0);
					btnValidate.setEnabled(true);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void setListModel(final ListModel<String> lm) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					lsMain.setModel(lm);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void startValidate() {
		btnValidate.setEnabled(false);
		pBar.setMaximum(movielist.getElementCount() * 3);
		pBar.setValue(0);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<DatabaseError> errors = new ArrayList<>();
				
				DatabaseValidator.startValidate(errors, movielist, new ProgressCallbackListener() {
					@Override
					public void step() {
						stepProgress();
					}
				});
				
				DefaultListModel<String> dlm = new DefaultListModel<>();
				
				for (DatabaseError de : errors) {
					dlm.addElement(de.getFullErrorString());
				}
				
				setListModel(dlm);
				
				endThread();
			}
		}).start();
	}
}
