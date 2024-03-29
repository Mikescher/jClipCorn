package de.jClipCorn.gui.frames.initialConfigFrame;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.gui.guiComponents.JReadableFSPathTextField;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.AppTheme;
import de.jClipCorn.properties.enumerations.CCDatabaseDriver;
import de.jClipCorn.properties.enumerations.UILanguage;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;

public class InitialConfigFrame extends JDialog {
	private static final long serialVersionUID = -2244186919409771706L;
	
	private boolean result = false;
	
	private JTextArea lblGreeting;
	private JLabel lblDatenbanktyp;
	private JComboBox<CCDatabaseDriver> cbxDatabaseDriver;
	private JButton btnExit;
	private JButton btnStart;
	private JLabel lblSprache;
	private JComboBox<String> cbxLanguage;
	private JCheckBox cbCheckForUpdates;
	private JLabel lblCheckForUpdates;
	private JLabel lblLooknfeel;
	private CCEnumComboBox<AppTheme> cbxLooknFeel;
	private JLabel lblDatenbankname;
	private JTextField edDatabasename;
	private JLabel lblCreatePeriodicallyBackups;
	private JCheckBox cbBackups;
	private JLabel lblVlcPfad;
	private JReadableFSPathTextField edVLCPath;
	private JButton btnVLCChooser;

	private final CCProperties ccprops;

	public InitialConfigFrame(CCProperties ccprops) {
		super();
		this.ccprops = ccprops;
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(LocaleBundle.getString("InitialConfigFrame.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setModal(true);
		setResizable(false);
		setBounds(100, 100, 501, 465);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		lblGreeting = new JTextArea();
		lblGreeting.setEditable(false);
		lblGreeting.setLineWrap(true);
		lblGreeting.setWrapStyleWord(true);
		lblGreeting.setText(LocaleBundle.getString("InitialConfigFrame.greeting")); //$NON-NLS-1$
		lblGreeting.setBounds(12, 12, 471, 90);
		lblGreeting.setBackground(UIManager.getColor("Label.background")); //$NON-NLS-1$
		lblGreeting.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
		lblGreeting.setBorder(UIManager.getBorder("Label.border")); //$NON-NLS-1$
		getContentPane().add(lblGreeting);
		
		lblDatenbanktyp = new JLabel(LocaleBundle.getString("InitialConfigFrame.lblDatabaseType")); //$NON-NLS-1$
		lblDatenbanktyp.setBounds(12, 151, 243, 16);
		getContentPane().add(lblDatenbanktyp);

		cbxDatabaseDriver = new JComboBox<>(new DefaultComboBoxModel<>(CCDatabaseDriver.getSelectableValues()));
		cbxDatabaseDriver.setSelectedItem(ccprops.PROP_DATABASE_DRIVER.getValue());
		cbxDatabaseDriver.setBounds(273, 147, 175, 25);
		getContentPane().add(cbxDatabaseDriver);
		
		btnStart = new JButton(LocaleBundle.getString("InitialConfigFrame.btnStart")); //$NON-NLS-1$
		btnStart.addActionListener(e -> onStart());
		btnStart.setBounds(385, 399, 98, 26);
		getContentPane().add(btnStart);
		
		btnExit = new JButton(LocaleBundle.getString("InitialConfigFrame.btnExit")); //$NON-NLS-1$
		btnExit.addActionListener(e -> onExit());
		btnExit.setBounds(12, 399, 98, 26);
		getContentPane().add(btnExit);
		
		lblSprache = new JLabel(LocaleBundle.getString("InitialConfigFrame.lblLanguage")); //$NON-NLS-1$
		lblSprache.setBounds(12, 188, 243, 16);
		getContentPane().add(lblSprache);
		
		cbxLanguage = new JComboBox<>();
		cbxLanguage.setModel(new DefaultComboBoxModel<>(UILanguage.getWrapper().getList()));
		cbxLanguage.setSelectedIndex(ccprops.PROP_UI_LANG.getValue().asInt());
		cbxLanguage.addActionListener(e -> updateLanguage());
		cbxLanguage.setBounds(273, 184, 175, 25);
		getContentPane().add(cbxLanguage);
		
		cbCheckForUpdates = new JCheckBox();
		cbCheckForUpdates.setSelected(ccprops.PROP_COMMON_CHECKFORUPDATES.getValue());
		cbCheckForUpdates.setBounds(269, 221, 175, 24);
		cbCheckForUpdates.setSelected(true);
		getContentPane().add(cbCheckForUpdates);
		
		lblCheckForUpdates = new JLabel(LocaleBundle.getString("InitialConfigFrame.lblUpdates")); //$NON-NLS-1$
		lblCheckForUpdates.setBounds(12, 225, 243, 16);
		getContentPane().add(lblCheckForUpdates);
		
		lblLooknfeel = new JLabel(LocaleBundle.getString("InitialConfigFrame.lblLooknFeel")); //$NON-NLS-1$
		lblLooknfeel.setBounds(12, 262, 243, 16);
		getContentPane().add(lblLooknfeel);
		
		cbxLooknFeel = new CCEnumComboBox<>(AppTheme.getWrapper());
		cbxLooknFeel.setSelectedEnum(ccprops.PROP_UI_APPTHEME.getValue());
		cbxLooknFeel.addActionListener(e -> updateLNF());
		cbxLooknFeel.setBounds(273, 258, 175, 25);
		getContentPane().add(cbxLooknFeel);
		
		lblDatenbankname = new JLabel(LocaleBundle.getString("InitialConfigFrame.lblDatabasename")); //$NON-NLS-1$
		lblDatenbankname.setBounds(12, 114, 243, 16);
		getContentPane().add(lblDatenbankname);
		
		edDatabasename = new JTextField();
		edDatabasename.setBounds(273, 112, 175, 20);
		edDatabasename.setText(ccprops.PROP_DATABASE_NAME.getValue());
		getContentPane().add(edDatabasename);
		edDatabasename.setColumns(10);
		
		lblCreatePeriodicallyBackups = new JLabel(LocaleBundle.getString("InitialConfigFrame.lblBackups")); //$NON-NLS-1$
		lblCreatePeriodicallyBackups.setBounds(12, 299, 243, 16);
		getContentPane().add(lblCreatePeriodicallyBackups);
		
		cbBackups = new JCheckBox();
		cbBackups.setSelected(ccprops.PROP_BACKUP_CREATEBACKUPS.getValue());
		cbBackups.setBounds(269, 295, 175, 24);
		getContentPane().add(cbBackups);
		
		lblVlcPfad = new JLabel(LocaleBundle.getString("Settingsframe.tabbedPnl.PROP_PLAY_VLC_PATH")); //$NON-NLS-1$
		lblVlcPfad.setBounds(12, 336, 55, 16);
		getContentPane().add(lblVlcPfad);
		
		edVLCPath = new JReadableFSPathTextField();
		edVLCPath.setBounds(273, 334, 135, 20);
		getContentPane().add(edVLCPath);
		edVLCPath.setColumns(10);
		
		btnVLCChooser = new JButton("..."); //$NON-NLS-1$
		btnVLCChooser.addActionListener(e -> 
		{
			final String end = "vlc.exe"; //$NON-NLS-1$
			JFileChooser vc = new JFileChooser();

			vc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			vc.setFileFilter(FileChooserHelper.createFullValidateFileFilter("Filter: " + end, val -> StringUtils.endsWithIgnoreCase(val, end))); //$NON-NLS-1$
			
			vc.setDialogTitle(LocaleBundle.getString("Settingsframe.dlg.title")); //$NON-NLS-1$

			if (vc.showOpenDialog(InitialConfigFrame.this) == JFileChooser.APPROVE_OPTION) {
				edVLCPath.setPath(FSPath.create(vc.getSelectedFile()));
			}
		});
		btnVLCChooser.setBounds(412, 331, 36, 26);
		getContentPane().add(btnVLCChooser);
	}
	
	private void updateLanguage() {
		LocaleBundle.updateLangManual(cbxLanguage.getSelectedIndex());
		
		setTitle(LocaleBundle.getString("InitialConfigFrame.title")); //$NON-NLS-1$
		lblGreeting.setText(LocaleBundle.getString("InitialConfigFrame.greeting")); //$NON-NLS-1$
		lblDatenbanktyp.setText(LocaleBundle.getString("InitialConfigFrame.lblDatabaseType")); //$NON-NLS-1$
		btnStart.setText(LocaleBundle.getString("InitialConfigFrame.btnStart")); //$NON-NLS-1$
		btnExit.setText(LocaleBundle.getString("InitialConfigFrame.btnExit")); //$NON-NLS-1$
		lblSprache.setText(LocaleBundle.getString("InitialConfigFrame.lblLanguage")); //$NON-NLS-1$
		lblCheckForUpdates.setText(LocaleBundle.getString("InitialConfigFrame.lblUpdates")); //$NON-NLS-1$
		lblLooknfeel.setText(LocaleBundle.getString("InitialConfigFrame.lblLooknFeel")); //$NON-NLS-1$
		lblDatenbankname.setText(LocaleBundle.getString("InitialConfigFrame.lblDatabasename")); //$NON-NLS-1$
		lblCreatePeriodicallyBackups.setText(LocaleBundle.getString("InitialConfigFrame.lblBackups")); //$NON-NLS-1$

		cbxLanguage.repaint();
		cbxLooknFeel.repaint();
		cbxDatabaseDriver.repaint();
	}
	
	private void updateLNF() {
		final var theme = cbxLooknFeel.getSelectedEnum();
		
		new Thread(() ->
		{
			try { Thread.sleep(250); } catch (InterruptedException e) { /**/ }

			SwingUtils.invokeLater(() ->
			{
				if (cbxLooknFeel.getSelectedEnum() != theme) return;

				LookAndFeelManager.setLookAndFeel(cbxLooknFeel.getSelectedEnum(), false);

				SwingUtilities.updateComponentTreeUI(InitialConfigFrame.this);

				lblGreeting.setBackground(UIManager.getColor("Label.background")); //$NON-NLS-1$
				lblGreeting.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
				lblGreeting.setBorder(UIManager.getBorder("Label.border")); //$NON-NLS-1$
			});
		}, "THREAD_DEFFERED_LNF").start(); //$NON-NLS-1$
	}
	
	private void onStart() {
		if (cbxDatabaseDriver.getSelectedItem() == null) return;

		if (!CCDatabase.validateDatabaseName(edDatabasename.getText())) {
			DialogHelper.showLocalError(this, "Dialogs.DatabasenameAssertion"); //$NON-NLS-1$
			
			return;
		}

		if (((CCDatabaseDriver)cbxDatabaseDriver.getSelectedItem()).isDeprecated()) {
			boolean b = DialogHelper.showLocaleYesNo(this, "Dialogs.DeprecatedDriver"); //$NON-NLS-1$
			if (!b) return;
		}

		ccprops.PROP_DATABASE_DRIVER.setValue((CCDatabaseDriver) cbxDatabaseDriver.getSelectedItem());
		
		ccprops.PROP_COMMON_CHECKFORUPDATES.setValue(cbCheckForUpdates.isSelected());
		
		ccprops.PROP_DATABASE_NAME.setValue(edDatabasename.getText());
		
		ccprops.PROP_BACKUP_CREATEBACKUPS.setValue(cbBackups.isSelected());
		
		if (ccprops.PROP_UI_LANG.getValue().asInt() != cbxLanguage.getSelectedIndex()) {
			ccprops.PROP_UI_LANG.setValue(UILanguage.getWrapper().findOrDefault(cbxLanguage.getSelectedIndex(), UILanguage.ENGLISCH));
			LocaleBundle.updateLang(ccprops);
		}

		if (ccprops.PROP_UI_APPTHEME.getValue() != cbxLooknFeel.getSelectedEnum()) {
			ccprops.PROP_UI_APPTHEME.setValue(cbxLooknFeel.getSelectedEnum());
			LookAndFeelManager.setLookAndFeel(cbxLooknFeel.getSelectedEnum(), false);
		}
				
		if (!edVLCPath.getPath().isEmpty()) ccprops.PROP_PLAY_VLC_PATH.setValueSingle(edVLCPath.getPath());
		
		result = true;

		setVisible(false);
		dispose();
	}
	
	private void onExit() {
		result = false;

		setVisible(false);
		dispose();
	}

	public static boolean ShowWizard(CCProperties ccprops) {
		InitialConfigFrame f = new InitialConfigFrame(ccprops);
		
		f.setVisible(true);
		
		return f.result;
	}
}
