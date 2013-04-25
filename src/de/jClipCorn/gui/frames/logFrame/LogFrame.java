package de.jClipCorn.gui.frames.logFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.log.CCLogChangedListener;
import de.jClipCorn.gui.log.CCLogType;
import java.awt.Color;
import java.awt.Font;

public class LogFrame extends JFrame implements CCLogChangedListener{ //TODO First Display then load things in GUI (instant show when click on toolbar)
	private static final long serialVersionUID = -8838227410250810646L;
		
	private JTabbedPane tpnlMainPanel;
	private JPanel tabErrors;
	private JPanel tabWarnings;
	private JPanel tabInformations;
	private JPanel tabUndefinied;
	private JList<String> lsErrors;
	private JTextArea memoErrors;
	private JScrollPane spnErrors;
	private JList<String> lsWarnings;
	private JList<String> lsInformations;
	private JList<String> lsUndefinied;
	private JTextArea memoWarnings;
	private JScrollPane spnWarnings;
	private JTextArea memoInformations;
	private JTextArea memoUndefinied;
	private JScrollPane spnInformations;
	private JScrollPane spnUndefinied;
	private JScrollPane spnErrorList;
	private JScrollPane spnWarningsList;
	private JScrollPane spnInformationsList;
	private JScrollPane spnUndefiniedList;

	public LogFrame(Component owner) {
		initGUI();
		setLocationRelativeTo(owner);
		setModels();
		
		CCLog.addChangeListener(this);
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setTitle(LocaleBundle.getString("CCLogFrame.this.title")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		setMinimumSize(new Dimension(1000, 350));
		
		tpnlMainPanel = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tpnlMainPanel);
		
		tabErrors = new JPanel();
		tpnlMainPanel.addTab(LocaleBundle.getString("CCLog.Errors") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_ERROR) + ")", null, tabErrors, null);   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		tabErrors.setLayout(new GridLayout(1, 2, 5, 0));
		
		spnErrorList = new JScrollPane();
		tabErrors.add(spnErrorList);
		
		lsErrors = new JList<>();
		spnErrorList.setViewportView(lsErrors);
		lsErrors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsErrors.setVisibleRowCount(16);
		
		spnErrors = new JScrollPane();
		spnErrors.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tabErrors.add(spnErrors);
		
		memoErrors = new JTextArea();
		memoErrors.setFont(new Font("Lucida Console", Font.PLAIN, 13)); //$NON-NLS-1$
		memoErrors.setBackground(Color.BLACK);
		memoErrors.setForeground(Color.GREEN);
		memoErrors.setEditable(false);
		spnErrors.setViewportView(memoErrors);
		
		tabWarnings = new JPanel();
		tpnlMainPanel.addTab(LocaleBundle.getString("CCLog.Warnings") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_WARNING) + ")", null, tabWarnings, null);   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		tabWarnings.setLayout(new GridLayout(1, 2, 5, 0));
		
		spnWarningsList = new JScrollPane();
		tabWarnings.add(spnWarningsList);
		
		lsWarnings = new JList<>();
		spnWarningsList.setViewportView(lsWarnings);
		lsWarnings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsWarnings.setVisibleRowCount(16);
		
		spnWarnings = new JScrollPane();
		spnWarnings.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tabWarnings.add(spnWarnings);
		
		memoWarnings = new JTextArea();
		memoWarnings.setFont(new Font("Lucida Console", Font.PLAIN, 13)); //$NON-NLS-1$
		memoWarnings.setBackground(Color.BLACK);
		memoWarnings.setForeground(Color.GREEN);
		memoWarnings.setEditable(false);
		spnWarnings.setViewportView(memoWarnings);
		
		tabInformations = new JPanel();
		tpnlMainPanel.addTab(LocaleBundle.getString("CCLog.Informations") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_INFORMATION) + ")", null, tabInformations, null);  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		tabInformations.setLayout(new GridLayout(1, 2, 5, 0));
		
		spnInformationsList = new JScrollPane();
		tabInformations.add(spnInformationsList);
		
		lsInformations = new JList<>();
		spnInformationsList.setViewportView(lsInformations);
		lsInformations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsInformations.setVisibleRowCount(16);
		
		spnInformations = new JScrollPane();
		spnInformations.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tabInformations.add(spnInformations);
		
		memoInformations = new JTextArea();
		memoInformations.setFont(new Font("Lucida Console", Font.PLAIN, 13)); //$NON-NLS-1$
		memoInformations.setBackground(Color.BLACK);
		memoInformations.setForeground(Color.GREEN);
		memoInformations.setEditable(false);
		spnInformations.setViewportView(memoInformations);
		
		tabUndefinied = new JPanel();
		tpnlMainPanel.addTab(LocaleBundle.getString("CCLog.Undefinieds") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_UNDEFINED) + ")", null, tabUndefinied, null);   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		tabUndefinied.setLayout(new GridLayout(1, 2, 5, 0));
		
		spnUndefiniedList = new JScrollPane();
		tabUndefinied.add(spnUndefiniedList);
		
		lsUndefinied = new JList<>();
		spnUndefiniedList.setViewportView(lsUndefinied);
		lsUndefinied.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsUndefinied.setVisibleRowCount(16);
		
		spnUndefinied = new JScrollPane();
		spnUndefinied.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tabUndefinied.add(spnUndefinied);
		
		memoUndefinied = new JTextArea();
		memoUndefinied.setFont(new Font("Lucida Console", Font.PLAIN, 13)); //$NON-NLS-1$
		memoUndefinied.setBackground(Color.BLACK);
		memoUndefinied.setForeground(Color.GREEN);
		memoUndefinied.setEditable(false);
		spnUndefinied.setViewportView(memoUndefinied);
	}
	
	private void setModels() {
		lsErrors.setModel(new LogListModel(lsErrors, CCLogType.LOG_ELEM_ERROR, memoErrors));
		lsWarnings.setModel(new LogListModel(lsWarnings, CCLogType.LOG_ELEM_WARNING, memoWarnings));
		lsInformations.setModel(new LogListModel(lsInformations, CCLogType.LOG_ELEM_INFORMATION, memoInformations));
		lsUndefinied.setModel(new LogListModel(lsUndefinied, CCLogType.LOG_ELEM_UNDEFINED, memoUndefinied));
	}
	
	@Override
	public void onChanged() {
		tpnlMainPanel.setTitleAt(0, LocaleBundle.getString("CCLog.Error") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_ERROR) + ")");   			//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		tpnlMainPanel.setTitleAt(1, LocaleBundle.getString("CCLog.Warnings") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_WARNING) + ")");   		//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		tpnlMainPanel.setTitleAt(2, LocaleBundle.getString("CCLog.Informations") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_INFORMATION) + ")");	//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		tpnlMainPanel.setTitleAt(3, LocaleBundle.getString("CCLog.Undefinieds") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_UNDEFINED) + ")");   	//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
	}
}
