package de.jClipCorn.gui.frames.inputErrorFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;

public class InputErrorDialog extends JDialog {
	private static final long serialVersionUID = 2988199599783528024L;

	private final JPanel contentPanel = new JPanel();
	private JScrollPane scrollPane;
	private JLabel lblYouHaveErrors;
	private JPanel buttonPane;
	private JButton okButton;
	private JButton cancelButton;
	private JList<String> lsErrors;
	private DefaultListModel<String> lsErrorModel;
	
	private UserDataProblemHandler owner;
	
	public InputErrorDialog(List<UserDataProblem> problems, UserDataProblemHandler owner, Component parent) {
		super();
		this.owner = owner;
		initGUI(parent);
		fillMemo(problems);
	}

	private void initGUI(Component parent) {
		setTitle(LocaleBundle.getString("AddMovieInputErrorDialog.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
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
	
	private void fillMemo(List<UserDataProblem> problems) {
		if (problems == null) {
			return;
		}
		
		lblYouHaveErrors.setText(LocaleBundle.getFormattedString("AddMovieInputErrorDialog.lblErrors.text", problems.size())); //$NON-NLS-1$
		
		lsErrorModel.clear();
		
		for (UserDataProblem udp : problems) {
			lsErrorModel.addElement(udp.getText());
		}
	}
	
	private void onIgnoreClicked() {
		owner.onAMIEDIgnoreClicked();
		dispose();
	}
}