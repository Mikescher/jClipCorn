package de.jClipCorn.util.helper;

import de.jClipCorn.gui.guiComponents.JAutoCompleteTextField;
import de.jClipCorn.gui.guiComponents.filesize.CCFileSizeSpinner;
import de.jClipCorn.gui.guiComponents.onlinescore.OnlineScoreControl;
import de.jClipCorn.gui.guiComponents.tags.TagPanel;
import de.jClipCorn.gui.guiComponents.dateTimeListEditor.DateTimeListEditor;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.groupListEditor.GroupListEditor;
import de.jClipCorn.gui.guiComponents.jMediaInfoControl.JMediaInfoControl;
import de.jClipCorn.gui.guiComponents.language.LanguageListChooser;
import de.jClipCorn.gui.guiComponents.language.LanguageSetChooser;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.util.adapter.ChangeLambdaAdapter;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.adapter.ItemChangeLambdaAdapter;
import de.jClipCorn.util.lambda.Func0to0;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class DirtyUtil
{
	public static void initDirtyListener(Func0to0 lstr, JComponent ...components) {
		for (var c: components) addDirtyListener(lstr, c);
	}

	public static void initDirtyListenerRecursive(Func0to0 lstr, JPanel parent) {
		initSingleDirtyListenerRecursive(lstr, parent);
	}

	public static void initDirtyListenerRecursive(Func0to0 lstr, JFrame parent) {
		initSingleDirtyListenerRecursive(lstr, parent);
	}

	public static void initDirtyListenerRecursive(Func0to0 lstr, JDialog parent) {
		initSingleDirtyListenerRecursive(lstr, parent);
	}

	private static void initSingleDirtyListenerRecursive(Func0to0 lstr, Component c)
	{
		if (c == null) return;

		if (c.getClass() == JPanel.class) {
			for (var sub: ((JPanel)c).getComponents()) initSingleDirtyListenerRecursive(lstr, sub);
		} else if (c.getClass() == JScrollPane.class) {
			var vp = ((JScrollPane)c).getViewport();
			if (vp != null) initSingleDirtyListenerRecursive(lstr, vp.getView());
		} else if (c instanceof JFrame) {
			for (var sub: ((JFrame)c).getContentPane().getComponents()) initSingleDirtyListenerRecursive(lstr, sub);
		} else if (c instanceof JDialog) {
			for (var sub: ((JDialog)c).getContentPane().getComponents()) initSingleDirtyListenerRecursive(lstr, sub);
		} else if (c instanceof JLabel) {
			// skip
		} else if (c instanceof JButton) {
			// skip
		} else if (c.getClass() == JList.class) {
			// skip
		} else {
			addDirtyListener(lstr, c);
		}
	}

	private static void addDirtyListener(Func0to0 lstr, Component c)
	{
		if (c instanceof JTextField) ((JTextField)c).getDocument().addDocumentListener(new DocumentLambdaAdapter(lstr));
		else if (c instanceof JAutoCompleteTextField) ((JAutoCompleteTextField)c).addChangeListener(e -> lstr.invoke());
		else if (c instanceof JReferenceChooser) ((JReferenceChooser)c).addReferenceChangedListener(e -> lstr.invoke());
		else if (c instanceof LanguageSetChooser) ((LanguageSetChooser)c).addLanguageChangedListener(e -> lstr.invoke());
		else if (c instanceof LanguageListChooser) ((LanguageListChooser)c).addLanguageChangedListener(e -> lstr.invoke());
		else if (c instanceof JSpinner) ((JSpinner)c).addChangeListener(new ChangeLambdaAdapter(lstr));
		else if (c instanceof JComboBox<?>) ((JComboBox<?>)c).addItemListener(new ItemChangeLambdaAdapter(lstr, ItemEvent.SELECTED));
		else if (c instanceof TagPanel) ((TagPanel)c).addTagsChangedListener(e -> lstr.invoke());
		else if (c instanceof GroupListEditor) ((GroupListEditor)c).addGroupListChangedListener(e -> lstr.invoke());
		else if (c instanceof EditCoverControl) ((EditCoverControl)c).addCoverChangedListener(e -> lstr.invoke());
		else if (c instanceof JMediaInfoControl) ((JMediaInfoControl)c).addMediaInfoChangedListener(e -> lstr.invoke());
		else if (c instanceof DateTimeListEditor) ((DateTimeListEditor)c).addValueChangedListener(e -> lstr.invoke());
		else if (c instanceof OnlineScoreControl) ((OnlineScoreControl)c).addOnlineScoreChangedListener(e -> lstr.invoke());
		else if (c instanceof CCFileSizeSpinner) ((CCFileSizeSpinner)c).addFileSizeChangedListener(e -> lstr.invoke());
		else if (c instanceof JTextArea) ((JTextArea)c).getDocument().addDocumentListener(new DocumentLambdaAdapter(lstr::invoke));
		else throw new IllegalArgumentException("Component " + c.getClass().getSimpleName() + " is not supported in initDirtyListener");
	}
}
