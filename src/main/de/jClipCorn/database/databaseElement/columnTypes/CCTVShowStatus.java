package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

import javax.swing.*;
import java.awt.*;

/**
 * Whether the story of a series is complete - set by the user, TMDb only gives a hint
 * (TMDb "Ended" does not distinguish a finished story from a show that just stopped).
 */
public enum CCTVShowStatus implements ContinoousEnum<CCTVShowStatus> {
	UNKNOWN(0),
	UNFINISHED(1),
	FINISHED(2),
	CANCELLED(3);

	private final static String[] NAMES = {
		LocaleBundle.getString("CCTVShowStatus.Unknown"),    //$NON-NLS-1$
		LocaleBundle.getString("CCTVShowStatus.Unfinished"), //$NON-NLS-1$
		LocaleBundle.getString("CCTVShowStatus.Finished"),   //$NON-NLS-1$
		LocaleBundle.getString("CCTVShowStatus.Cancelled"),  //$NON-NLS-1$
	};

	private final int id;

	private final static EnumWrapper<CCTVShowStatus> wrapper = new EnumWrapper<>(UNKNOWN);

	CCTVShowStatus(int val) {
		id = val;
	}

	public static EnumWrapper<CCTVShowStatus> getWrapper() {
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
	public CCTVShowStatus[] evalues() {
		return CCTVShowStatus.values();
	}

	public MultiSizeIconRef getIconRef() {
		switch (this) {
			case UNFINISHED: return Resources.ICN_TABLE_SHOWSTATUS_UNFINISHED;
			case FINISHED:   return Resources.ICN_TABLE_SHOWSTATUS_FINISHED;
			case CANCELLED:  return Resources.ICN_TABLE_SHOWSTATUS_CANCELLED;
			default:         return Resources.ICN_TABLE_SHOWSTATUS_UNKNOWN;
		}
	}

	public ImageIcon getIcon() {
		return getIconRef().get16x16();
	}

	/** Background of the badge drawn over the cover in the main window. */
	public Color getOverlayColor() {
		switch (this) {
			case UNFINISHED: return new Color(243, 156, 18);
			case FINISHED:   return new Color(39, 174, 96);
			case CANCELLED:  return new Color(192, 57, 43);
			default:         return new Color(127, 140, 141);
		}
	}

	public static int compare(CCTVShowStatus a, CCTVShowStatus b) {
		return Integer.compare(a.asInt(), b.asInt());
	}

	/**
	 * Maps the TMDb tv-status (https://developer.themoviedb.org/reference/tv-series-details),
	 * or null for values that say nothing about the story ("Pilot", unknown strings).
	 */
	@SuppressWarnings("nls")
	public static CCTVShowStatus fromTMDbStatus(String status) {
		if (status == null) return null;
		switch (status.trim().toLowerCase()) {
			case "ended":            return FINISHED;
			case "canceled":
			case "cancelled":        return CANCELLED;
			case "returning series":
			case "in production":
			case "planned":          return UNFINISHED;
			default:                 return null;
		}
	}
}
