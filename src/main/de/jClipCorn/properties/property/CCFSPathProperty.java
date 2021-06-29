package de.jClipCorn.properties.property;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.JFSPathTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CCFSPathProperty extends CCProperty<FSPath> {
	public enum CCPathPropertyMode { FILES, DIRECTORIES }

	private final String filterEnd;
	private final CCPathPropertyMode filterMode;
	
	public CCFSPathProperty(CCPropertyCategory cat, CCProperties prop, String ident, FSPath standard, String filter, CCPathPropertyMode mode) {
		super(cat, FSPath.class, prop, ident, standard);
		filterEnd = filter;
		filterMode = mode;
	}

	@Override
	public Component getComponent() {
		return new JFSPathTextField(1);
	}

	@Override
	public void setComponentValueToValue(Component c, FSPath val) {
		((JFSPathTextField)c).setPath(val);
	}

	@Override
	public FSPath getComponentValue(Component c) {
		return ((JFSPathTextField)c).getPath();
	}

	@Override
	public FSPath getValue() {
		String val = properties.getProperty(identifier);

		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		return transformFromStorage(val);
	}

	@Override
	public FSPath setValue(FSPath val) {
		properties.setProperty(identifier, transformToStorage(val));

		return getValue();
	}

	@Override
	public boolean isValue(FSPath val) {
		return FSPath.isEqual(val, getValue());
	}

	public String getFilterEnding() {
		return filterEnd;
	}
	
	@Override
	public Component getSecondaryComponent(final Component firstComponent) {
		JButton btnChoose = new JButton("..."); //$NON-NLS-1$
		
		btnChoose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final String end = getFilterEnding();
				JFileChooser vc = new JFileChooser();
				
				switch (filterMode) {
				case FILES:
					vc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					break;
				case DIRECTORIES:
					vc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					break;
				default:
					CCLog.addDefaultSwitchError(this, filterMode);
					break;
				}
				
				if (end != null && !end.trim().isEmpty())
					vc.setFileFilter(FileChooserHelper.createFullValidateFileFilter("Filter: " + end, val -> StringUtils.endsWithIgnoreCase(val, end))); //$NON-NLS-1$
				
				vc.setDialogTitle(LocaleBundle.getString("Settingsframe.dlg.title")); //$NON-NLS-1$

				if (vc.showOpenDialog(firstComponent.getParent()) == JFileChooser.APPROVE_OPTION) {
					setComponentValueToValue(firstComponent, FSPath.create(vc.getSelectedFile()));
				}
			}
		});
		
		return btnChoose;
	}

	@SuppressWarnings("nls")
	protected String transformToStorage(FSPath pvalue) {
		var value = pvalue.toString();

		value = expandoReplace(value, FilesystemUtils.getTempPath().toString(), "%temp%");
		value = expandoReplace(value, FilesystemUtils.getHomePath().toString(), "%home%");

		value = value.replace(FSPath.SEPERATOR, "/");

		return value;
	}

	@SuppressWarnings("nls")
	protected FSPath transformFromStorage(String value) {
		value = value.replace("/", FSPath.SEPERATOR);

		value = expandoReplace(value, "%temp%", FilesystemUtils.getTempPath().toString());
		value = expandoReplace(value, "%home%", FilesystemUtils.getHomePath().toString());

		return FSPath.create(value);
	}

	private static String expandoReplace(String value, String search, String repl) {
		if (search.endsWith(FSPath.SEPERATOR)) search = search.substring(0, search.length()-1);
		if (repl.endsWith(FSPath.SEPERATOR)) repl = repl.substring(0, repl.length()-1);

		if (value.contains(search + FSPath.SEPERATOR)) {
			return value.replace(search + FSPath.SEPERATOR, repl + FSPath.SEPERATOR);
		} else if (value.contains(search)) {
			return value.replace(search, repl);
		} else {
			return value;
		}
	}
}
