package de.jClipCorn.util.enumextension;

public interface ContinoousEnum<T> {
	int asInt();
	
	String asString();
	
	String[] getList();
	T[] evalues();

	IEnumWrapper wrapper();
}
