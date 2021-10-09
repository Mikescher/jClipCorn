package de.jClipCorn.gui.guiComponents.referenceChooser;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.gui.guiComponents.language.LanguageChangedListener;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class JReferenceChooser extends JPanel {
	private static final long serialVersionUID = 2696192041815168280L;

	private JButton btnAdditional;
	private JSingleReferenceChooser mainChooser;

	private List<CCSingleOnlineReference> _additional = new ArrayList<>();

	private final CCMovieList movielist;

	@DesignCreate
	private static JReferenceChooser designCreate() { return new JReferenceChooser(CCMovieList.createStub()); }

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

	public void addReferenceChangedListener(final ReferenceChangedListener l) {
		listenerList.add(ReferenceChangedListener.class, l);
	}
	public void removeReferenceChangedListener(final ReferenceChangedListener l) {
		listenerList.add(ReferenceChangedListener.class, l);
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

		for (var l: listenerList.getListeners(LanguageChangedListener.class)) l.languageChanged(new ActionEvent(this, -1, Str.Empty));
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

		for (var l: listenerList.getListeners(LanguageChangedListener.class)) l.languageChanged(new ActionEvent(this, -1, Str.Empty));
	}
	
	public void setAdditional(List<CCSingleOnlineReference> a) {
		_additional = CCStreams
				.iterate(a)
				//.filter(r -> !ObjectUtils.IsEqual(r, mainChooser.getValue()))
				.unique()
				.enumerate();
		updateUIControls();

		for (var l: listenerList.getListeners(LanguageChangedListener.class)) l.languageChanged(new ActionEvent(this, -1, Str.Empty));
	}
}
