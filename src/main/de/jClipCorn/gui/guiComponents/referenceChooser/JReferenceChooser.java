package de.jClipCorn.gui.guiComponents.referenceChooser;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.util.stream.CCStreams;

public class JReferenceChooser extends JPanel {
	private static final long serialVersionUID = 2696192041815168280L;

	private JButton btnAdditional;
	private JSingleReferenceChooser mainChooser;

	private List<CCSingleOnlineReference> _additional = new ArrayList<>();
	
	public JReferenceChooser() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		
		btnAdditional = new JButton("+0"); //$NON-NLS-1$
		add(btnAdditional, BorderLayout.EAST);
		btnAdditional.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ReferenceChooserPopup popup = new ReferenceChooserPopup(_additional, JReferenceChooser.this);
				popup.setVisible(true);
			}
		});
		btnAdditional.setMargin(new Insets(2, 4, 2, 4));
		
		mainChooser = new JSingleReferenceChooser();
		add(mainChooser, BorderLayout.CENTER);
	}

	public CCOnlineReferenceList getValue() {
		return CCOnlineReferenceList.create(mainChooser.getValue(), _additional);
	}

	public void setValue(CCOnlineReferenceList ref) {
		mainChooser.setValue(ref.Main);
		_additional = new ArrayList<>(ref.Additional);
		updateUIControls();
	}
	
	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);

		mainChooser.setEnabled(flag);
		btnAdditional.setEnabled(flag);
	}
	
	public void updateUIControls() {
		mainChooser.updateUIControls();
		btnAdditional.setText("+"+_additional.size()); //$NON-NLS-1$
	}
	
	public void setAdditional(List<CCSingleOnlineReference> a) {
		_additional = CCStreams
				.iterate(a)
				//.filter(r -> !ObjectUtils.IsEqual(r, mainChooser.getValue()))
				.unique()
				.enumerate();
		updateUIControls();
	}
}
