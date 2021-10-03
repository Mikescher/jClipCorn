package de.jClipCorn.gui.guiComponents.iconComponents;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;

import javax.swing.*;

public class OnlineScoreDisplay extends JLabel {

	private CCOnlineScore _value = CCOnlineScore.STARS_0_0;

	public OnlineScoreDisplay() {
		super();
		updateIcon();
	}

	@Override
	@Deprecated
	public void setIcon(Icon i) {
		//no
		updateIcon();
	}

	@Override
	@Deprecated
	public void setText(String t) {
		//no
		updateIcon();
	}

	public void setOnlineScore(CCOnlineScore sc) {
		_value = sc;
		updateIcon();
	}

	public CCOnlineScore getOnlineScore() {
		return _value;
	}

	private void updateIcon() {
		if (_value == null) super.setIcon(null); else super.setIcon(_value.getIcon());
	}
}
