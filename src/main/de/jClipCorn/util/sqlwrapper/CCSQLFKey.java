package de.jClipCorn.util.sqlwrapper;

public class CCSQLFKey {
	public final CCSQLColDef   ColumnLocal;
	public final CCSQLTableDef TableForeign;
	public final CCSQLColDef   ColumnForeign;

	public CCSQLFKey(CCSQLColDef lc, CCSQLTableDef ft, CCSQLColDef fc) {
		ColumnLocal   = lc;
		TableForeign  = ft;
		ColumnForeign = fc;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CCSQLFKey ccsqlfKey = (CCSQLFKey) o;

		if (!ColumnLocal.equals(ccsqlfKey.ColumnLocal)) return false;
		if (!TableForeign.equals(ccsqlfKey.TableForeign)) return false;
		return ColumnForeign.equals(ccsqlfKey.ColumnForeign);
	}

	@Override
	public int hashCode() {
		int result = ColumnLocal.hashCode();
		result = 31 * result + TableForeign.hashCode();
		result = 31 * result + ColumnForeign.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return ColumnLocal.Name+" -> "+TableForeign.Name+"."+ColumnForeign.Name; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
