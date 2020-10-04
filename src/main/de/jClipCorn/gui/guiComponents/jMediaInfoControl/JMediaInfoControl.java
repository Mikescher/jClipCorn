package de.jClipCorn.gui.guiComponents.jMediaInfoControl;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.gui.frames.editMediaInfoDialog.EditMediaInfoDialog;
import de.jClipCorn.gui.frames.editMediaInfoDialog.MediaInfoResultHandler;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.lambda.Func0to1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class JMediaInfoControl extends JPanel implements MediaInfoResultHandler
{
	private static final long serialVersionUID = 2696192041815168280L;

	private JLabel image;
	private JButton btnEdit;
	private ReadableTextField edit;

	private CCMediaInfo value = null;
	private MediaQueryResult queryResult = null;

	private final List<ActionListener> _changeListener = new ArrayList<>();

	private final Func0to1<String> _pathProvider;

	@DesignCreate
	private static JMediaInfoControl designCreate()
	{
		return new JMediaInfoControl(() -> null);
	}

	public JMediaInfoControl(Func0to1<String> pathProvider) {
		super();
		_pathProvider = pathProvider;

		initGUI();
		updateUIControls();
	}

	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		
		btnEdit = new JButton("..."); //$NON-NLS-1$
		add(btnEdit, BorderLayout.EAST);
		btnEdit.addActionListener(e ->
		{
			if (value != null)
				new EditMediaInfoDialog(JMediaInfoControl.this, _pathProvider.invoke(), value, JMediaInfoControl.this).setVisible(true);
			else if (queryResult != null)
				new EditMediaInfoDialog(JMediaInfoControl.this, _pathProvider.invoke(), queryResult, JMediaInfoControl.this).setVisible(true);
			else
				new EditMediaInfoDialog(JMediaInfoControl.this, _pathProvider.invoke(), JMediaInfoControl.this).setVisible(true);
		});
		btnEdit.setMargin(new Insets(2, 4, 2, 4));
		btnEdit.setFocusable(false);
		
		edit = new ReadableTextField();
		add(edit, BorderLayout.CENTER);
		
		image = new JLabel();
		image.setPreferredSize(new Dimension(18, 16));
		image.setSize(new Dimension(18, 16));
		image.setMinimumSize(new Dimension(18, 16));
		image.setIconTextGap(2);
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
		
		for (ActionListener ac : _changeListener) ac.actionPerformed(new ActionEvent(ref==null ? CCMediaInfo.EMPTY : ref, -1, Str.Empty));

		value = ref;
		queryResult = null;

		updateUIControls();
	}
	
	public void setValue(MediaQueryResult r) {
		CCMediaInfo cc = r.toMediaInfo();
		if (cc.isSet())
		{
			for (ActionListener ac : _changeListener) ac.actionPerformed(new ActionEvent(cc, -1, Str.Empty));

			value = cc;
			queryResult = r;

			updateUIControls();
		}
		else
		{
			for (ActionListener ac : _changeListener) ac.actionPerformed(new ActionEvent(CCMediaInfo.EMPTY, -1, Str.Empty));

			value = null;
			queryResult = r;

			updateUIControls();
		}


	}
	
	@Override
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);

		btnEdit.setEnabled(flag);
	}
	
	private void updateUIControls() {
		if (value == null) {
			if (queryResult == null) edit.setText(Str.Empty);
			else edit.setText(LocaleBundle.getString("JMediaInfoControl.partial")); //$NON-NLS-1$
			image.setIcon(Resources.ICN_TABLE_QUALITY_0.get());
		} else {
			edit.setText(value.getCategory(null).getLongText());
			image.setIcon(value.getCategory(null).getIcon());
			image.setToolTipText(value.getCategory(null).getTooltip());
		}
	}

	@Override
	public void onApplyMediaInfo(CCMediaInfo mi) {
		setValue(mi);
	}
}