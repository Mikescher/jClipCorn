package de.jClipCorn.gui.frames.inputErrorFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InputErrorDialog extends JCCDialog
{
	private final boolean canIgnore;
	private final UserDataProblemHandler handler;
	private final List<Tuple<String, UserDataProblem>> problems;
	private final boolean showsource;

	public InputErrorDialog(Component owner, CCMovieList ml, java.util.List<UserDataProblem> problems, UserDataProblemHandler udph, boolean canIgnore)
	{
		super(ml);

		this.handler = udph;
		this.problems = CCStreams.iterate(problems).map(p -> Tuple.Create(Str.Empty, p)).unique(p -> p.Item1+"\t"+p.Item2.getPID()+"\t"+p.Item2.getText()).toList();
		this.showsource = false;
		this.canIgnore = canIgnore;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	public InputErrorDialog(Component owner, CCMovieList ml, List<Tuple<String, UserDataProblem>> problems, UserDataProblemHandler udph, boolean showsource, boolean canIgnore)
	{
		super(ml);

		this.handler = udph;
		this.problems = CCStreams.iterate(problems).unique(p -> p.Item1+"\t"+p.Item2.getPID()+"\t"+p.Item2.getText()).toList();
		this.showsource = showsource;
		this.canIgnore = canIgnore;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		if (problems == null) return;

		lblYouHaveErrors.setText(LocaleBundle.getFormattedString("AddMovieInputErrorDialog.lblErrors.text", problems.size())); //$NON-NLS-1$

		btnIgnore.setEnabled(canIgnore);

		var lsErrorModel = new DefaultListModel<String>();

		lsErrorModel.clear();

		for (Tuple<String, UserDataProblem> udp : problems) {
			if (showsource)
				lsErrorModel.addElement("[" + udp.Item1 + "] " + udp.Item2.getText()); //$NON-NLS-1$ //$NON-NLS-2$
			else
				lsErrorModel.addElement(udp.Item2.getText());
		}

		lsErrors.setModel(lsErrorModel);
	}

	private void onOkay() {
		dispose();
	}

	private void onIgnore() {
		handler.onAMIEDIgnoreClicked();
		dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		lblYouHaveErrors = new JLabel();
		scrollPane1 = new JScrollPane();
		lsErrors = new JList<>();
		panel1 = new JPanel();
		button1 = new JButton();
		btnIgnore = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("AddMovieInputErrorDialog.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default, $lgap, default:grow, $lgap, default, $ugap")); //$NON-NLS-1$

		//---- lblYouHaveErrors ----
		lblYouHaveErrors.setText(LocaleBundle.getString("AddMovieInputErrorDialog.lblErrors.text")); //$NON-NLS-1$
		lblYouHaveErrors.setHorizontalAlignment(SwingConstants.CENTER);
		lblYouHaveErrors.setForeground(Color.red);
		lblYouHaveErrors.setFont(lblYouHaveErrors.getFont().deriveFont(lblYouHaveErrors.getFont().getStyle() | Font.BOLD));
		contentPane.add(lblYouHaveErrors, CC.xy(2, 2));

		//======== scrollPane1 ========
		{
			scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane1.setViewportView(lsErrors);
		}
		contentPane.add(scrollPane1, CC.xy(2, 4, CC.DEFAULT, CC.FILL));

		//======== panel1 ========
		{
			panel1.setLayout(new FlowLayout(FlowLayout.RIGHT));

			//---- button1 ----
			button1.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
			button1.addActionListener(e -> onOkay());
			panel1.add(button1);

			//---- btnIgnore ----
			btnIgnore.setText(LocaleBundle.getString("AddMovieInputErrorDialog.btnIgnore.text")); //$NON-NLS-1$
			btnIgnore.addActionListener(e -> onIgnore());
			panel1.add(btnIgnore);
		}
		contentPane.add(panel1, CC.xy(2, 6, CC.FILL, CC.FILL));
		setSize(650, 400);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel lblYouHaveErrors;
	private JScrollPane scrollPane1;
	private JList<String> lsErrors;
	private JPanel panel1;
	private JButton button1;
	private JButton btnIgnore;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
