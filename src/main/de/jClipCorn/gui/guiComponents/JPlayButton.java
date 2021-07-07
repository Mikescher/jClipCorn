package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;

import javax.swing.*;

public class JPlayButton extends JButton
{
	public enum Mode { PLAY, PLAY_HIDDEN, PLAY_ACTIVE, PLAY_QUEUED }

	private Mode _mode = Mode.PLAY;

	public JPlayButton()
	{
		super(Resources.ICN_MENUBAR_PLAY.get32x32());
	}

	@Override
	public void setText(String text)
	{
		super.setText(Str.Empty);
	}

	public void setMode(Mode m) {
		_mode = m;

		switch (_mode) {
			case PLAY:        setIcon(Resources.ICN_MENUBAR_PLAY.get32x32());        break;
			case PLAY_HIDDEN: setIcon(Resources.ICN_MENUBAR_HIDDENPLAY.get32x32());  break;
			case PLAY_ACTIVE: setIcon(Resources.ICN_MENUBAR_PLAY_ACTIVE.get32x32()); break;
			case PLAY_QUEUED: setIcon(Resources.ICN_MENUBAR_PLAY_QUEUED.get32x32()); break;
		}
	}

	public Mode getMode() {
		return _mode;
	}
}
