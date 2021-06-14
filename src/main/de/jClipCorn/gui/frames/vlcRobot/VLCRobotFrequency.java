package de.jClipCorn.gui.frames.vlcRobot;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum VLCRobotFrequency implements ContinoousEnum<VLCRobotFrequency> {
	MAXIMUM(0),
	MS_0250(1),
	MS_0500(2),
	MS_1000(3),
	MS_1500(4),
	MS_2000(5);

	private final static String[] NAMES =
	{
		"Maximum", //$NON-NLS-1$
		"250 ms",  //$NON-NLS-1$
		"500 ms",  //$NON-NLS-1$
		"1 sec",   //$NON-NLS-1$
		"1.5 sec", //$NON-NLS-1$
		"2 sec",   //$NON-NLS-1$
	};

	private final int id;

	private static final EnumWrapper<VLCRobotFrequency> wrapper = new EnumWrapper<>(MAXIMUM);

	VLCRobotFrequency(int val) {
		id = val;
	}

	public static EnumWrapper<VLCRobotFrequency> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
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
	public VLCRobotFrequency[] evalues() {
		return VLCRobotFrequency.values();
	}
}