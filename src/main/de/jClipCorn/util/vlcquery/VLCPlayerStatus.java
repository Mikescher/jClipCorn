package de.jClipCorn.util.vlcquery;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum VLCPlayerStatus implements ContinoousEnum<VLCPlayerStatus> {
	PLAYING(0),
	PAUSED(1),
	STOPPED(2),
	DISABLED(3),
	NOT_RUNNING(4),
	ERROR(5);

	private final static String[] NAMES =
	{
		LocaleBundle.getString("VLCPlayerStatus.PLAYING"),     //$NON-NLS-1$
		LocaleBundle.getString("VLCPlayerStatus.PAUSED"),      //$NON-NLS-1$
		LocaleBundle.getString("VLCPlayerStatus.STOPPED"),     //$NON-NLS-1$
		LocaleBundle.getString("VLCPlayerStatus.DISABLED"),    //$NON-NLS-1$
		LocaleBundle.getString("VLCPlayerStatus.NOT_RUNNING"), //$NON-NLS-1$
		LocaleBundle.getString("VLCPlayerStatus.ERROR"),       //$NON-NLS-1$
	};

	private final int id;

	private static final EnumWrapper<VLCPlayerStatus> wrapper = new EnumWrapper<>(ERROR);

	VLCPlayerStatus(int val) {
		id = val;
	}

	public static EnumWrapper<VLCPlayerStatus> getWrapper() {
		return wrapper;
	}

	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	@Override
	public String[] getList() {
		return NAMES;
	}

	@Override
	public VLCPlayerStatus[] evalues() {
		return VLCPlayerStatus.values();
	}
}