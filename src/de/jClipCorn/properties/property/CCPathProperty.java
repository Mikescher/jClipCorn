package de.jClipCorn.properties.property;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.FileChooserHelper;
import de.jClipCorn.util.Validator;

public class CCPathProperty extends CCStringProperty {
	private final String filterEnd;
	
	public CCPathProperty(int cat, CCProperties prop, String ident, String standard, String filter) {
		super(cat, prop, ident, standard);
		filterEnd = filter;
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
				vc.setFileFilter(FileChooserHelper.createFullValidateFileFilter("Filter: " + end, new Validator<String>() { //$NON-NLS-1$
							@Override
							public boolean validate(String val) {
								return StringUtils.endsWithIgnoreCase(val, end);
							}
						}));
				vc.setDialogTitle(LocaleBundle.getString("Settingsframe.dlg.title")); //$NON-NLS-1$

				if (vc.showOpenDialog(firstComponent.getParent()) == JFileChooser.APPROVE_OPTION) {
					setComponentValueToValue(firstComponent, vc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		return btnChoose;
	}
}
