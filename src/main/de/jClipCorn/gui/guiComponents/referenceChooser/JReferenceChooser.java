package de.jClipCorn.gui.guiComponents.referenceChooser;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class JReferenceChooser extends JPanel {
	private static final long serialVersionUID = 2696192041815168280L;

	private JButton btnAdditional;
	private JSingleReferenceChooser mainChooser;

	private List<CCSingleOnlineReference> _additional = new ArrayList<>();

	private final List<ActionListener> _changeListener = new ArrayList<>();

	private final CCMovieList movielist;

	public JReferenceChooser(CCMovieList ml) {
		super();
		movielist = ml;
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		
		btnAdditional = new JButton("+0"); //$NON-NLS-1$
		add(btnAdditional, BorderLayout.EAST);
		btnAdditional.addActionListener(e ->
		{
			if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
				ReferenceChooserDialog dialog = new ReferenceChooserDialog(movielist, mainChooser.getValue(), _additional, JReferenceChooser.this);
				dialog.setVisible(true);
			} else {
				ReferenceChooserPopup popup = new ReferenceChooserPopup(movielist, _additional, JReferenceChooser.this);
				popup.setVisible(true);
			}
		});
		btnAdditional.setMargin(new Insets(2, 4, 2, 4));
		btnAdditional.setFocusable(false);
		
		mainChooser = new JSingleReferenceChooser(movielist);
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

		for (ActionListener a : _changeListener) a.actionPerformed(new ActionEvent(ref, -1, Str.Empty));
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

		for (ActionListener ac : _changeListener) ac.actionPerformed(new ActionEvent(a, -1, Str.Empty));
	}
	
	public void setAdditional(List<CCSingleOnlineReference> a) {
		_additional = CCStreams
				.iterate(a)
				//.filter(r -> !ObjectUtils.IsEqual(r, mainChooser.getValue()))
				.unique()
				.enumerate();
		updateUIControls();

		for (ActionListener ac : _changeListener) ac.actionPerformed(new ActionEvent(a, -1, Str.Empty));
	}
}
