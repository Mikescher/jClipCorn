package de.jClipCorn.gui.frames.editMediaInfoDialog;

import de.jClipCorn.features.metadata.MetadataSourceType;

import javax.swing.*;

public class MIButton extends JButton {

	private MetadataSourceType _mdSource = null;

	public MetadataSourceType getMetadataSourceType() {
		return _mdSource;
	}

	public void setMetadataSourceType(MetadataSourceType t) {
		_mdSource = t;
		super.setText(t.asString());
		super.setIcon(t.getIcon().get16x16());
	}

}
