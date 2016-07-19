package de.jClipCorn.database.databaseElement.columnTypes;

public class CCGroup implements Comparable<CCGroup> {
	public final String Name;
	
	private CCGroup(String n) {
		Name = n;
	}
	
	public static CCGroup create(String name) {
		CCGroup g = new CCGroup(name);
		return g;
	}

	@Override
	public int compareTo(CCGroup o) {
		return Name.compareTo(o.Name);
	}
	
	@Override
	public String toString() {
		return Name;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof CCGroup) && ((CCGroup)o).Name.equals(Name);
	}

	@Override
	public int hashCode() {
		return Name.hashCode();
	}
}
