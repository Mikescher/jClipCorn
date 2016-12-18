package de.jClipCorn.properties.property;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.FileChooserHelper;

public class CCPathProperty extends CCStringProperty {
	public enum CCPathPropertyMode { FILES, DIRECTORIES }

	private final String filterEnd;
	private final CCPathPropertyMode filterMode;
	
	public CCPathProperty(CCPropertyCategory cat, CCProperties prop, String ident, String standard, String filter, CCPathPropertyMode mode) {
		super(cat, prop, ident, standard);
		filterEnd = filter;
		filterMode = mode;
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
					setComponentValueToValue(firstComponent, vc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		return btnChoose;
	}
	
	@Override
	protected String transformToStorage(String value) {
		return PathFormatter.convertDevicePathToStoragePath(value);
	}
	
	@Override
	protected String transformFromStorage(String value) {
		return PathFormatter.convertStoragePathToDevicePath(value);
	}
}
