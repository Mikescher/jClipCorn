package de.jClipCorn.gui.guiComponents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.gui.frames.editMediaInfoDialog.EditMediaInfoDialog;
import de.jClipCorn.gui.frames.editMediaInfoDialog.MediaInfoResultHandler;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.mediaquery.MediaQueryResult;

public class JMediaInfoControl extends JPanel implements MediaInfoResultHandler {
	private static final long serialVersionUID = 2696192041815168280L;

	private JLabel image;
	private JButton btnEdit;
	private ReadableTextField edit;

	private CCMediaInfo value = null;
	private MediaQueryResult queryResult = null;

	private List<ActionListener> _changeListener = new ArrayList<>();

	public JMediaInfoControl() {
		initGUI();
		updateUIControls();
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		
		btnEdit = new JButton("..."); //$NON-NLS-1$
		add(btnEdit, BorderLayout.EAST);
		btnEdit.addActionListener(e ->
		{
			if (value != null) new EditMediaInfoDialog(JMediaInfoControl.this, value, JMediaInfoControl.this);
			else if (queryResult != null) new EditMediaInfoDialog(JMediaInfoControl.this, queryResult, JMediaInfoControl.this);
			else new EditMediaInfoDialog(JMediaInfoControl.this, JMediaInfoControl.this);
		});
		btnEdit.setMargin(new Insets(2, 4, 2, 4));
		btnEdit.setFocusable(false);
		
		edit = new ReadableTextField();
		edit.setEditable(false);
		add(edit, BorderLayout.CENTER);
		
		image = new JLabel();
		image.setPreferredSize(new Dimension(16, 16));
		image.setSize(new Dimension(16, 16));
		image.setMinimumSize(new Dimension(16, 16));
		add(image, BorderLayout.WEST);
	}

	public void addChangeListener(ActionListener a) {
		_changeListener.add(a);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(80, 20);
	}

	public CCMediaInfo getValue() {
		if (value == null) return CCMediaInfo.EMPTY;
		return value;
	}

	public void setValue(CCMediaInfo ref) {
		if (!ref.isSet()) ref = null;
		
		for (ActionListener ac : _changeListener) ac.actionPerformed(new ActionEvent(ref, -1, Str.Empty));
		
		value = ref;
		queryResult = null;

		updateUIControls();
	}
	
	public void setValue(MediaQueryResult r) {
		for (ActionListener ac : _changeListener) ac.actionPerformed(new ActionEvent(null, -1, Str.Empty));
		
		value = null;
		queryResult = r;

		updateUIControls();
	}
	
	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);

		btnEdit.setEnabled(flag);
	}
	
	private void updateUIControls() {
		if (value == null) {
			edit.setText(Str.Empty);
			image.setIcon(Resources.ICN_TABLE_QUALITY_0.get());
		}
		else {
			edit.setText(value.getCategory().getTooltip());
			image.setIcon(value.getCategory().getIcon());
		}
	}

	@Override
	public void onApplyMediaInfo(CCMediaInfo mi) {
		setValue(mi);
	}
}