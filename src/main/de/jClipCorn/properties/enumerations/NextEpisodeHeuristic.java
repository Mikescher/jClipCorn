package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum NextEpisodeHeuristic implements ContinoousEnum<NextEpisodeHeuristic> {
	AUTOMATIC(0), 
	FIRST_UNWATCHED(1), 
	NEXT_EPISODE(2), 
	CONTINUOUS(3);
	
	@SuppressWarnings("nls")
	private final static String[] NAMES = {
		LocaleBundle.getString("NextEpisodeHeuristic.Opt0"),
		LocaleBundle.getString("NextEpisodeHeuristic.Opt1"),
		LocaleBundle.getString("NextEpisodeHeuristic.Opt2"),
		LocaleBundle.getString("NextEpisodeHeuristic.Opt3"),
	};
	
	private int id;

	private static final EnumWrapper<NextEpisodeHeuristic> wrapper = new EnumWrapper<>(AUTOMATIC);

	private NextEpisodeHeuristic(int val) {
		id = val;
	}
	
	public static EnumWrapper<NextEpisodeHeuristic> getWrapper() {
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

	public static int compare(NextEpisodeHeuristic s1, NextEpisodeHeuristic s2) {
		return Integer.compare(s1.asInt(), s2.asInt());
	}
	
	@Override
	public String[] getList() {
		return NAMES;
	}
	
	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	@Override
	public NextEpisodeHeuristic[] evalues() {
		return NextEpisodeHeuristic.values();
	}
}
