package de.jClipCorn.gui.guiComponents.jMediaInfoControl;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.gui.frames.editMediaInfoDialog.EditMediaInfoDialog;
import de.jClipCorn.gui.frames.editMediaInfoDialog.MediaInfoResultHandler;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;
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

	private PartialMediaInfo value = null;
	private MediaQueryResult queryResult = null;

	private final List<ActionListener> _changeListener = new ArrayList<>();

	private final Func0to1<FSPath> _pathProvider;
	private final CCMovieList movielist;

	@DesignCreate
	private static JMediaInfoControl designCreate()
	{
		return new JMediaInfoControl(null, () -> null);
	}

	public JMediaInfoControl(CCMovieList ml, Func0to1<FSPath> pathProvider) {
		super();
		_pathProvider = pathProvider;
		movielist = ml;

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
				new EditMediaInfoDialog(JMediaInfoControl.this, movielist, _pathProvider.invoke(), value, JMediaInfoControl.this).setVisible(true);
			else if (queryResult != null)
				new EditMediaInfoDialog(JMediaInfoControl.this, movielist, _pathProvider.invoke(), queryResult, JMediaInfoControl.this).setVisible(true);
			else
				new EditMediaInfoDialog(JMediaInfoControl.this, movielist, _pathProvider.invoke(), JMediaInfoControl.this).setVisible(true);
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

	public PartialMediaInfo getValue() {
		if (value == null) return PartialMediaInfo.EMPTY;
		return value;
	}

	public void setValue(PartialMediaInfo ref) {
		for (ActionListener ac : _changeListener) ac.actionPerformed(new ActionEvent(ref==null ? CCMediaInfo.EMPTY : ref, -1, Str.Empty));

		value = ref;
		queryResult = null;

		updateUIControls();
	}
	
	public void setValue(MediaQueryResult r) {
		PartialMediaInfo cc = r.toPartial();

		for (ActionListener ac : _changeListener) ac.actionPerformed(new ActionEvent(cc, -1, Str.Empty));

		value = cc;
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
			if (queryResult == null) edit.setText(Str.Empty);
			else edit.setText(LocaleBundle.getString("JMediaInfoControl.partial")); //$NON-NLS-1$
			image.setIcon(Resources.ICN_TABLE_QUALITY_0.get());
		} else {
			edit.setText(value.toMediaInfo().getCategory(null).getLongText());
			image.setIcon(value.toMediaInfo().getCategory(null).getIcon());
			image.setToolTipText(value.toMediaInfo().getCategory(null).getTooltip());
		}
	}

	@Override
	public void onApplyMediaInfo(CCMediaInfo mi) {
		setValue(mi.toPartial());
	}
}