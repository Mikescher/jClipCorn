package de.jClipCorn.gui.frames.logFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.log.CCLogChangedListener;
import de.jClipCorn.features.log.CCLogType;
import de.jClipCorn.gui.resources.Resources;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JButton;

public class LogFrame extends JFrame implements CCLogChangedListener{ 
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
	private JButton btnMoreErr;
	private JButton btnMoreUndef;
	private JButton btnMoreInfo;
	private JButton btnMoreWarn;

	public LogFrame(Component owner) {
		super();
		
		initGUI();
		setLocationRelativeTo(owner);
		setModels();
		
		DatabaseElementPreviewLabel cl = MainFrame.getInstance().getCoverLabel();
		if (cl.isErrorMode()) cl.setModeDefault();
		
		CCLog.addChangeListener(this);
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle(LocaleBundle.getString("CCLogFrame.this.title")); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		setMinimumSize(new Dimension(1000, 350));
		
		tpnlMainPanel = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tpnlMainPanel);
		
		tabErrors = new JPanel();
		tpnlMainPanel.addTab(LocaleBundle.getString("CCLog.Errors") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_ERROR) + ")", null, tabErrors, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		tabErrors.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("550px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		spnErrorList = new JScrollPane();
		tabErrors.add(spnErrorList, "1, 1, fill, fill"); //$NON-NLS-1$
		
		lsErrors = new JList<>();
		spnErrorList.setViewportView(lsErrors);
		lsErrors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsErrors.setVisibleRowCount(16);
		
		spnErrors = new JScrollPane();
		spnErrors.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tabErrors.add(spnErrors, "3, 1, fill, fill"); //$NON-NLS-1$
		
		memoErrors = new JTextArea();
		memoErrors.setFont(new Font("Lucida Console", Font.PLAIN, 13)); //$NON-NLS-1$
		memoErrors.setBackground(Color.BLACK);
		memoErrors.setForeground(Color.GREEN);
		memoErrors.setEditable(false);
		spnErrors.setViewportView(memoErrors);
		
		btnMoreErr = new JButton("..."); //$NON-NLS-1$
		btnMoreErr.addActionListener(e -> {
			if (!memoErrors.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoErrors.getText(), false); //$NON-NLS-1$
		});
		tabErrors.add(btnMoreErr, "3, 3, right, fill"); //$NON-NLS-1$
		
		tabWarnings = new JPanel();
		tpnlMainPanel.addTab(LocaleBundle.getString("CCLog.Warnings") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_WARNING) + ")", null, tabWarnings, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		tabWarnings.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("550px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		spnWarningsList = new JScrollPane();
		tabWarnings.add(spnWarningsList, "1, 1, fill, fill"); //$NON-NLS-1$
		
		lsWarnings = new JList<>();
		spnWarningsList.setViewportView(lsWarnings);
		lsWarnings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsWarnings.setVisibleRowCount(16);
		
		spnWarnings = new JScrollPane();
		spnWarnings.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tabWarnings.add(spnWarnings, "3, 1, fill, fill"); //$NON-NLS-1$
		
		memoWarnings = new JTextArea();
		memoWarnings.setFont(new Font("Lucida Console", Font.PLAIN, 13)); //$NON-NLS-1$
		memoWarnings.setBackground(Color.BLACK);
		memoWarnings.setForeground(Color.GREEN);
		memoWarnings.setEditable(false);
		spnWarnings.setViewportView(memoWarnings);
		
		btnMoreWarn = new JButton("..."); //$NON-NLS-1$
		btnMoreWarn.addActionListener(e -> {
			if (!memoWarnings.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoWarnings.getText(), false); //$NON-NLS-1$
		});
		tabWarnings.add(btnMoreWarn, "3, 3, right, fill"); //$NON-NLS-1$
		
		tabInformations = new JPanel();
		tpnlMainPanel.addTab(LocaleBundle.getString("CCLog.Informations") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_INFORMATION) + ")", null, tabInformations, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		tabInformations.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("550px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		spnInformationsList = new JScrollPane();
		tabInformations.add(spnInformationsList, "1, 1, fill, fill"); //$NON-NLS-1$
		
		lsInformations = new JList<>();
		spnInformationsList.setViewportView(lsInformations);
		lsInformations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsInformations.setVisibleRowCount(16);
		
		spnInformations = new JScrollPane();
		spnInformations.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tabInformations.add(spnInformations, "3, 1, fill, fill"); //$NON-NLS-1$
		
		memoInformations = new JTextArea();
		memoInformations.setFont(new Font("Lucida Console", Font.PLAIN, 13)); //$NON-NLS-1$
		memoInformations.setBackground(Color.BLACK);
		memoInformations.setForeground(Color.GREEN);
		memoInformations.setEditable(false);
		spnInformations.setViewportView(memoInformations);
		
		btnMoreInfo = new JButton("..."); //$NON-NLS-1$
		btnMoreInfo.addActionListener(e -> {
			if (!memoInformations.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoInformations.getText(), false); //$NON-NLS-1$
		});
		tabInformations.add(btnMoreInfo, "3, 3, right, fill"); //$NON-NLS-1$
		
		tabUndefinied = new JPanel();
		tpnlMainPanel.addTab(LocaleBundle.getString("CCLog.Undefinieds") + " (" + CCLog.getCount(CCLogType.LOG_ELEM_UNDEFINED) + ")", null, tabUndefinied, null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		tabUndefinied.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("550px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		spnUndefiniedList = new JScrollPane();
		tabUndefinied.add(spnUndefiniedList, "1, 1, fill, fill"); //$NON-NLS-1$
		
		lsUndefinied = new JList<>();
		spnUndefiniedList.setViewportView(lsUndefinied);
		lsUndefinied.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lsUndefinied.setVisibleRowCount(16);
		
		spnUndefinied = new JScrollPane();
		spnUndefinied.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tabUndefinied.add(spnUndefinied, "3, 1, fill, fill"); //$NON-NLS-1$
		
		memoUndefinied = new JTextArea();
		memoUndefinied.setFont(new Font("Lucida Console", Font.PLAIN, 13)); //$NON-NLS-1$
		memoUndefinied.setBackground(Color.BLACK);
		memoUndefinied.setForeground(Color.GREEN);
		memoUndefinied.setEditable(false);
		spnUndefinied.setViewportView(memoUndefinied);
		
		btnMoreUndef = new JButton("..."); //$NON-NLS-1$
		btnMoreUndef.addActionListener(e -> {
			if (!memoUndefinied.getText().isEmpty()) GenericTextDialog.showText(LogFrame.this, LocaleBundle.getString("CCLogFrame.this.title"), memoUndefinied.getText(), false); //$NON-NLS-1$
		});
		tabUndefinied.add(btnMoreUndef, "3, 3, right, fill"); //$NON-NLS-1$
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
