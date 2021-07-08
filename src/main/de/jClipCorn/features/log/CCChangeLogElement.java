package de.jClipCorn.features.log;

import de.jClipCorn.util.datetime.CCTime;

import java.util.Arrays;

public class CCChangeLogElement {
	public final String   RootType;
	public final int      RootID;
	public final String   ActualType;
	public final int      ActualID;
	public final String[] Properties;
	public final CCTime   Time;

	public CCChangeLogElement(String rootType, int rootID, String actualType, int actualID, String[] properties) {
		this.RootType   = rootType;
		this.RootID     = rootID;
		this.ActualType = actualType;
		this.ActualID   = actualID;
		this.Properties = properties;

		this.Time = new CCTime();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CCChangeLogElement that = (CCChangeLogElement) o;

		if (RootID != that.RootID) return false;
		if (ActualID != that.ActualID) return false;
		if (RootType != null ? !RootType.equals(that.RootType) : that.RootType != null) return false;
		if (ActualType != null ? !ActualType.equals(that.ActualType) : that.ActualType != null) return false;
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		if (!Arrays.equals(Properties, that.Properties)) return false;
		return Time != null ? Time.equals(that.Time) : that.Time == null;
	}

	@Override
	public int hashCode() {
		int result = RootType != null ? RootType.hashCode() : 0;
		result = 31 * result + RootID;
		result = 31 * result + (ActualType != null ? ActualType.hashCode() : 0);
		result = 31 * result + ActualID;
		result = 31 * result + Arrays.hashCode(Properties);
		result = 31 * result + (Time != null ? Time.hashCode() : 0);
		return result;
	}
}
