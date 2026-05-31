package de.jClipCorn.gui.frames.initialConfigFrame;

import java.awt.*;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.guiComponents.JReadableFSPathTextField;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.enumerations.AppTheme;
import de.jClipCorn.properties.enumerations.UILanguage;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.helper.SwingUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;

public class InitialConfigFrame extends JCCDialog {
	private static final long serialVersionUID = -2244186919409771706L;

	private boolean result = false;
	private boolean isInitialized = false;

	public InitialConfigFrame(CCMovieList ml) {
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(null);
	}

	private void postInit() {
		setModal(true);

		lblGreeting.setBackground(UIManager.getColor("Label.background")); //$NON-NLS-1$
		lblGreeting.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
		lblGreeting.setBorder(UIManager.getBorder("Label.border")); //$NON-NLS-1$

		cbxLanguage.setModel(new DefaultComboBoxModel<>(UILanguage.getWrapper().getList()));
		cbxLanguage.setSelectedIndex(ccprops().PROP_UI_LANG.getValue().asInt());

		cbCheckForUpdates.setSelected(ccprops().PROP_COMMON_CHECKFORUPDATES.getValue());

		cbxLooknFeel.setSelectedEnum(ccprops().PROP_UI_APPTHEME.getValue());

		cbBackups.setSelected(ccprops().PROP_BACKUP_CREATEBACKUPS.getValue());

		isInitialized = true;
	}

	private void updateLanguage() {
		if (!isInitialized) return;

		LocaleBundle.updateLangManual(cbxLanguage.getSelectedIndex());

		setTitle(LocaleBundle.getString("InitialConfigFrame.title")); //$NON-NLS-1$
		lblGreeting.setText(LocaleBundle.getString("InitialConfigFrame.greeting")); //$NON-NLS-1$
		btnStart.setText(LocaleBundle.getString("InitialConfigFrame.btnStart")); //$NON-NLS-1$
		btnExit.setText(LocaleBundle.getString("InitialConfigFrame.btnExit")); //$NON-NLS-1$
		lblSprache.setText(LocaleBundle.getString("InitialConfigFrame.lblLanguage")); //$NON-NLS-1$
		lblCheckForUpdates.setText(LocaleBundle.getString("InitialConfigFrame.lblUpdates")); //$NON-NLS-1$
		lblLooknfeel.setText(LocaleBundle.getString("InitialConfigFrame.lblLooknFeel")); //$NON-NLS-1$
		lblCreatePeriodicallyBackups.setText(LocaleBundle.getString("InitialConfigFrame.lblBackups")); //$NON-NLS-1$

		cbxLanguage.repaint();
		cbxLooknFeel.repaint();
	}

	private void updateLNF() {
		if (!isInitialized) return;

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

	private void onVLCChooser() {
		final String end = "vlc.exe"; //$NON-NLS-1$
		JFileChooser vc = new JFileChooser();

		vc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		vc.setFileFilter(FileChooserHelper.createFullValidateFileFilter("Filter: " + end, val -> StringUtils.endsWithIgnoreCase(val, end))); //$NON-NLS-1$

		vc.setDialogTitle(LocaleBundle.getString("Settingsframe.dlg.title")); //$NON-NLS-1$

		if (vc.showOpenDialog(InitialConfigFrame.this) == JFileChooser.APPROVE_OPTION) {
			edVLCPath.setPath(FSPath.create(vc.getSelectedFile()));
		}
	}

	private void onStart() {
		ccprops().PROP_COMMON_CHECKFORUPDATES.setValue(cbCheckForUpdates.isSelected());

		ccprops().PROP_BACKUP_CREATEBACKUPS.setValue(cbBackups.isSelected());

		if (ccprops().PROP_UI_LANG.getValue().asInt() != cbxLanguage.getSelectedIndex()) {
			ccprops().PROP_UI_LANG.setValue(UILanguage.getWrapper().findOrDefault(cbxLanguage.getSelectedIndex(), UILanguage.ENGLISCH));
			LocaleBundle.updateLang(ccprops());
		}

		if (ccprops().PROP_UI_APPTHEME.getValue() != cbxLooknFeel.getSelectedEnum()) {
			ccprops().PROP_UI_APPTHEME.setValue(cbxLooknFeel.getSelectedEnum());
			LookAndFeelManager.setLookAndFeel(cbxLooknFeel.getSelectedEnum(), false);
		}

		if (!edVLCPath.getPath().isEmpty()) ccprops().PROP_PLAY_VLC_PATH.setValueSingle(edVLCPath.getPath());

		result = true;

		setVisible(false);
		dispose();
	}

	private void onExit() {
		result = false;

		setVisible(false);
		dispose();
	}

	public static boolean ShowWizard(CCMovieList ml) {
		InitialConfigFrame f = new InitialConfigFrame(ml);

		f.setVisible(true);

		return f.result;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		lblGreeting = new JTextArea();
		lblSprache = new JLabel();
		cbxLanguage = new JComboBox<>();
		lblCheckForUpdates = new JLabel();
		cbCheckForUpdates = new JCheckBox();
		lblLooknfeel = new JLabel();
		cbxLooknFeel = new CCEnumComboBox<AppTheme>(AppTheme.getWrapper());
		lblCreatePeriodicallyBackups = new JLabel();
		cbBackups = new JCheckBox();
		lblVlcPfad = new JLabel();
		pnlVlc = new JPanel();
		edVLCPath = new JReadableFSPathTextField();
		btnVLCChooser = new JButton();
		pnlButtons = new JPanel();
		btnExit = new JButton();
		btnStart = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("InitialConfigFrame.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $lcgap, default:grow, $ugap",
			"2*($ugap, default), 4*($lgap, default), default:grow, default, $ugap"));

		//---- lblGreeting ----
		lblGreeting.setText(LocaleBundle.getString("InitialConfigFrame.greeting"));
		lblGreeting.setEditable(false);
		lblGreeting.setLineWrap(true);
		lblGreeting.setWrapStyleWord(true);
		lblGreeting.setRows(4);
		contentPane.add(lblGreeting, CC.xywh(2, 2, 3, 1, CC.FILL, CC.FILL));

		//---- lblSprache ----
		lblSprache.setText(LocaleBundle.getString("InitialConfigFrame.lblLanguage"));
		contentPane.add(lblSprache, CC.xy(2, 4));

		//---- cbxLanguage ----
		cbxLanguage.addActionListener(e -> updateLanguage());
		contentPane.add(cbxLanguage, CC.xy(4, 4));

		//---- lblCheckForUpdates ----
		lblCheckForUpdates.setText(LocaleBundle.getString("InitialConfigFrame.lblUpdates"));
		contentPane.add(lblCheckForUpdates, CC.xy(2, 6));
		contentPane.add(cbCheckForUpdates, CC.xy(4, 6));

		//---- lblLooknfeel ----
		lblLooknfeel.setText(LocaleBundle.getString("InitialConfigFrame.lblLooknFeel"));
		contentPane.add(lblLooknfeel, CC.xy(2, 8));

		//---- cbxLooknFeel ----
		cbxLooknFeel.addActionListener(e -> updateLNF());
		contentPane.add(cbxLooknFeel, CC.xy(4, 8));

		//---- lblCreatePeriodicallyBackups ----
		lblCreatePeriodicallyBackups.setText(LocaleBundle.getString("InitialConfigFrame.lblBackups"));
		contentPane.add(lblCreatePeriodicallyBackups, CC.xy(2, 10));
		contentPane.add(cbBackups, CC.xy(4, 10));

		//---- lblVlcPfad ----
		lblVlcPfad.setText(LocaleBundle.getString("Settingsframe.tabbedPnl.PROP_PLAY_VLC_PATH"));
		contentPane.add(lblVlcPfad, CC.xy(2, 12));

		//======== pnlVlc ========
		{
			pnlVlc.setLayout(new FormLayout(
				"default:grow, $lcgap, default",
				"default"));

			//---- edVLCPath ----
			edVLCPath.setColumns(10);
			pnlVlc.add(edVLCPath, CC.xy(1, 1, CC.FILL, CC.DEFAULT));

			//---- btnVLCChooser ----
			btnVLCChooser.setText("...");
			btnVLCChooser.addActionListener(e -> onVLCChooser());
			pnlVlc.add(btnVLCChooser, CC.xy(3, 1));
		}
		contentPane.add(pnlVlc, CC.xy(4, 12, CC.FILL, CC.DEFAULT));

		//======== pnlButtons ========
		{
			pnlButtons.setLayout(new FormLayout(
				"default, default:grow, default",
				"default"));

			//---- btnExit ----
			btnExit.setText(LocaleBundle.getString("InitialConfigFrame.btnExit"));
			btnExit.addActionListener(e -> onExit());
			pnlButtons.add(btnExit, CC.xy(1, 1));

			//---- btnStart ----
			btnStart.setText(LocaleBundle.getString("InitialConfigFrame.btnStart"));
			btnStart.addActionListener(e -> onStart());
			pnlButtons.add(btnStart, CC.xy(3, 1));
		}
		contentPane.add(pnlButtons, CC.xywh(2, 14, 3, 1, CC.FILL, CC.DEFAULT));
		setSize(501, 465);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTextArea lblGreeting;
	private JLabel lblSprache;
	private JComboBox<String> cbxLanguage;
	private JLabel lblCheckForUpdates;
	private JCheckBox cbCheckForUpdates;
	private JLabel lblLooknfeel;
	private CCEnumComboBox<AppTheme> cbxLooknFeel;
	private JLabel lblCreatePeriodicallyBackups;
	private JCheckBox cbBackups;
	private JLabel lblVlcPfad;
	private JPanel pnlVlc;
	private JReadableFSPathTextField edVLCPath;
	private JButton btnVLCChooser;
	private JPanel pnlButtons;
	private JButton btnExit;
	private JButton btnStart;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
