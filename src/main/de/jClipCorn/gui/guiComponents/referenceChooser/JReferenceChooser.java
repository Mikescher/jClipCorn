package de.jClipCorn.gui.guiComponents.referenceChooser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
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

	private List<ActionListener> _changeListener = new ArrayList<>();

	public JReferenceChooser() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		
		btnAdditional = new JButton("+0"); //$NON-NLS-1$
		add(btnAdditional, BorderLayout.EAST);
		btnAdditional.addActionListener(e ->
		{
			if ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
				ReferenceChooserDialog dialog = new ReferenceChooserDialog(mainChooser.getValue(), _additional, JReferenceChooser.this);
				dialog.setVisible(true);
			} else {
				ReferenceChooserPopup popup = new ReferenceChooserPopup(_additional, JReferenceChooser.this);
				popup.setVisible(true);
			}
		});
		btnAdditional.setMargin(new Insets(2, 4, 2, 4));
		btnAdditional.setFocusable(false);
		
		mainChooser = new JSingleReferenceChooser();
		add(mainChooser, BorderLayout.CENTER);
	}

	public void addChangeListener(ActionListener a) {
		_changeListener.add(a);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(80, 20);
	}

	public CCOnlineReferenceList getValue() {
		return CCOnlineReferenceList.create(mainChooser.getValue(), _additional);
	}

	public void setValue(CCOnlineReferenceList ref) {
		mainChooser.setValue(ref.Main);
		_additional = new ArrayList<>(ref.Additional);
		updateUIControls();

		for (ActionListener a : _changeListener) a.actionPerformed(new ActionEvent(ref, -1, ""));
	}
	
	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);

		mainChooser.setEnabled(flag);
		btnAdditional.setEnabled(flag);
	}
	
	private void updateUIControls() {
		mainChooser.updateUIControls();
		btnAdditional.setText("+"+_additional.size()); //$NON-NLS-1$
	}

	public void setMain(CCSingleOnlineReference a) {
		mainChooser.setValue(a);
		updateUIControls();

		for (ActionListener ac : _changeListener) ac.actionPerformed(new ActionEvent(a, -1, ""));
	}
	
	public void setAdditional(List<CCSingleOnlineReference> a) {
		_additional = CCStreams
				.iterate(a)
				//.filter(r -> !ObjectUtils.IsEqual(r, mainChooser.getValue()))
				.unique()
				.enumerate();
		updateUIControls();

		for (ActionListener ac : _changeListener) ac.actionPerformed(new ActionEvent(a, -1, ""));
	}
}
