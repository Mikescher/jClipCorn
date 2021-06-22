package de.jClipCorn.util.helper;

import de.jClipCorn.gui.guiComponents.TagPanel;
import de.jClipCorn.gui.guiComponents.dateTimeListEditor.DateTimeListEditor;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.groupListEditor.GroupListEditor;
import de.jClipCorn.gui.guiComponents.jMediaInfoControl.JMediaInfoControl;
import de.jClipCorn.gui.guiComponents.language.LanguageChooser;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.util.adapter.ActionLambdaAdapter;
import de.jClipCorn.util.adapter.ChangeLambdaAdapter;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.adapter.ItemChangeLambdaAdapter;
import de.jClipCorn.util.lambda.Func0to0;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class DirtyUtil
{
	public static void initDirtyListener(Func0to0 lstr, JComponent ...components)
	{
		for (var c: components)
		{
			if (c instanceof JTextField) ((JTextField)c).getDocument().addDocumentListener(new DocumentLambdaAdapter(lstr));
			else if (c instanceof JReferenceChooser) ((JReferenceChooser)c).addChangeListener(new ActionLambdaAdapter(lstr));
			else if (c instanceof LanguageChooser) ((LanguageChooser)c).addChangeListener(new ActionLambdaAdapter(lstr));
			else if (c instanceof JSpinner) ((JSpinner)c).addChangeListener(new ChangeLambdaAdapter(lstr));
			else if (c instanceof JComboBox<?>) ((JComboBox<?>)c).addItemListener(new ItemChangeLambdaAdapter(lstr, ItemEvent.SELECTED));
			else if (c instanceof TagPanel) ((TagPanel)c).addChangeListener(new ActionLambdaAdapter(lstr));
			else if (c instanceof GroupListEditor) ((GroupListEditor)c).addChangeListener(new ActionLambdaAdapter(lstr));
			else if (c instanceof EditCoverControl) ((EditCoverControl)c).addChangeListener(new ActionLambdaAdapter(lstr));
			else if (c instanceof JMediaInfoControl) ((JMediaInfoControl)c).addChangeListener(new ActionLambdaAdapter(lstr));
			else if (c instanceof DateTimeListEditor) ((DateTimeListEditor)c).addChangeListener(new ActionLambdaAdapter(lstr));
			else throw new IllegalArgumentException("Component " + c.getClass().getSimpleName() + " is not supported in initDirtyListener");

		}
	}
}
