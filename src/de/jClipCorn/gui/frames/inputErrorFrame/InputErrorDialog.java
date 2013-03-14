package de.jClipCorn.gui.frames.inputErrorFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.userdataProblem.UserDataProblem;
import de.jClipCorn.util.userdataProblem.UserDataProblemHandler;

public class InputErrorDialog extends JDialog {
	private static final long serialVersionUID = 2988199599783528024L;

	private final JPanel contentPanel = new JPanel();

	private UserDataProblemHandler owner;
	private JList<String> lsErrors;
	DefaultListModel<String> lsErrorModel;
	
	public InputErrorDialog(ArrayList<UserDataProblem> problems, UserDataProblemHandler owner, Component parent) {
		super();
		this.owner = owner;
		initGUI(parent);
		fillMemo(problems);
	}

	private void initGUI(Component owner) {
		setTitle(LocaleBundle.getString("AddMovieInputErrorDialog.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setModal(true);
		setResizable(false);
		setBounds(100, 100, 500, 300);
		setLocationRelativeTo(owner);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			contentPanel.add(scrollPane);
			{
				lsErrors = new JList<>(lsErrorModel = new DefaultListModel<String>());
				scrollPane.setViewportView(lsErrors);
			}
		}
		{
			JLabel lblYouHaveErrors = new JLabel(LocaleBundle.getString("AddMovieInputErrorDialog.lblErrors.text")); //$NON-NLS-1$
			lblYouHaveErrors.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
			lblYouHaveErrors.setForeground(Color.RED);
			lblYouHaveErrors.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblYouHaveErrors, BorderLayout.NORTH);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton(LocaleBundle.getString("AddMovieInputErrorDialog.btnOK.text")); //$NON-NLS-1$
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton(LocaleBundle.getString("AddMovieInputErrorDialog.btnIgnore.text")); //$NON-NLS-1$
				cancelButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						onIgnoreClicked();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
	}
	
	private void fillMemo(ArrayList<UserDataProblem> problems) {
		if (problems == null)
			return;
		
		lsErrorModel.clear();
		
		for (UserDataProblem udp : problems) {
			lsErrorModel.addElement(udp.getText());
		}
	}
	
	private void onIgnoreClicked() {
		owner.onAMIEDIgnoreClicked();
	}
}

