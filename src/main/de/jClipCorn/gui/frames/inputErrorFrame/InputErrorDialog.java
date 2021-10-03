package de.jClipCorn.gui.frames.inputErrorFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class InputErrorDialog extends JCCDialog {
	private static final long serialVersionUID = 2988199599783528024L;

	private final JPanel contentPanel = new JPanel();
	private JScrollPane scrollPane;
	private JLabel lblYouHaveErrors;
	private JPanel buttonPane;
	private JButton okButton;
	private JButton cancelButton;
	private JList<String> lsErrors;
	private DefaultListModel<String> lsErrorModel;

	private final UserDataProblemHandler owner;

	public InputErrorDialog(CCMovieList ml, List<UserDataProblem> problems, UserDataProblemHandler owner, Component parent) {
		super(ml);
		this.owner = owner;
		initGUI(parent);
		fillMemo(CCStreams.iterate(problems).map(p -> Tuple.Create(Str.Empty, p)).toList(), false);
	}

	public InputErrorDialog(CCMovieList ml, List<Tuple<String, UserDataProblem>> problems, UserDataProblemHandler owner, Component parent, boolean showsource) {
		super(ml);
		this.owner = owner;
		initGUI(parent);
		fillMemo(problems, showsource);
	}

	private void initGUI(Component parent) {
		setTitle(LocaleBundle.getString("AddMovieInputErrorDialog.this.title")); //$NON-NLS-1$

		setModal(true);
		setResizable(false);
		setBounds(100, 100, 500, 300);
		setLocationRelativeTo(parent);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		contentPanel.add(scrollPane);

		lsErrors = new JList<>(lsErrorModel = new DefaultListModel<>());
		scrollPane.setViewportView(lsErrors);

		lblYouHaveErrors = new JLabel(LocaleBundle.getString("AddMovieInputErrorDialog.lblErrors.text")); //$NON-NLS-1$
		lblYouHaveErrors.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		lblYouHaveErrors.setForeground(Color.RED);
		lblYouHaveErrors.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(lblYouHaveErrors, BorderLayout.NORTH);

		buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		okButton = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton(LocaleBundle.getString("AddMovieInputErrorDialog.btnIgnore.text")); //$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onIgnoreClicked();
			}
		});
		buttonPane.add(cancelButton);
	}
	
	private void fillMemo(List<Tuple<String, UserDataProblem>> problems, boolean showsource) {
		if (problems == null) {
			return;
		}
		
		lblYouHaveErrors.setText(LocaleBundle.getFormattedString("AddMovieInputErrorDialog.lblErrors.text", problems.size())); //$NON-NLS-1$
		
		lsErrorModel.clear();
		
		for (Tuple<String, UserDataProblem> udp : problems) {
			if (showsource)
				lsErrorModel.addElement("[" + udp.Item1 + "] " + udp.Item2.getText()); //$NON-NLS-1$ //$NON-NLS-2$
			else
				lsErrorModel.addElement(udp.Item2.getText());
		}
	}
	
	private void onIgnoreClicked() {
		owner.onAMIEDIgnoreClicked();
		dispose();
	}
}