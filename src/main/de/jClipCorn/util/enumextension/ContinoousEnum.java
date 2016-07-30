package de.jClipCorn.util.enumextension;

public interface ContinoousEnum<T> {
	public int asInt();
	
	public String asString();
	
	public String[] getList();
	public T[] evalues();
}
