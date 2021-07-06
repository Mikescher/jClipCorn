package de.jClipCorn.util.sqlwrapper;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CCSQLTableDef {

	public final String Name;
	public final CCSQLColDef Primary;
	public final List<CCSQLColDef> Columns;
	public final List<CCSQLFKey> ForeignKeys;

	public CCSQLTableDef(String n, CCSQLColDef p, CCSQLColDef[] cols, CCSQLFKey[] fkeys) {
		Name    = n;

		Primary = p;

		Columns = new ArrayList<>(Arrays.asList(cols));
		if (p != null) Columns.add(0, p);

		ForeignKeys = Arrays.asList(fkeys);
	}

	public CCStream<CCSQLColDef> getNonPrimaryColumns() {
		return CCStreams.iterate(Columns).filter(c -> c != Primary);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		return isEqual((CCSQLTableDef) o);
	}

	@Override
	public int hashCode() {
		int result = Name != null ? Name.hashCode() : 0;
		result = 31 * result + (Primary != null ? Primary.hashCode() : 0);
		result = 31 * result + (Columns != null ? Columns.hashCode() : 0);
		return result;
	}

	public boolean isEqual(CCSQLTableDef other) {
		if (other == null) return false;
		if (!Str.equals(Name, other.Name)) return false;

		if ((Primary==null) != (other.Primary==null)) return false;
		if (Columns.size() != other.Columns.size()) return false;

		if (Primary != null && !Primary.isEqual(other.Primary)) return false;

		for (CCSQLColDef def : Columns) {
			CCSQLColDef o = CCStreams.iterate(other.Columns).singleOrNull(c -> Str.equals(c.Name, def.Name));
			if (o == null) return false;
			if (!def.isEqual(o)) return false;
		}

		return true;
	}
}
