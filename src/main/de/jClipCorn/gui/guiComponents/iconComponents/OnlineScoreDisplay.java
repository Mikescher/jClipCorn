package de.jClipCorn.gui.guiComponents.iconComponents;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.util.Str;

import javax.swing.*;

public class OnlineScoreDisplay extends JLabel {

	private CCOnlineScore _value = CCOnlineScore.ZERO_OF_TEN;

	public OnlineScoreDisplay() {
		super();
		update();
	}

	@Override
	@Deprecated
	public void setIcon(Icon i) {
		//no
		update();
	}

	@Override
	@Deprecated
	public void setText(String t) {
		//no
		update();
	}

	@Override
	@Deprecated
	public void setToolTipText(String t) {
		//no
		update();
	}

	public void setOnlineScore(CCOnlineScore sc) {
		_value = sc;
		update();
	}

	public CCOnlineScore getOnlineScore() {
		return _value;
	}

	private void update() {
		if (_value == null) {
			super.setIcon(null);
			super.setToolTipText(Str.Empty);
		} else {
			super.setIcon(_value.getIcon());
			super.setToolTipText(_value.getDisplayString());
		}
	}
}
