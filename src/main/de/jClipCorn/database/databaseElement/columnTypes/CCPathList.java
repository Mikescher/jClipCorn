package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.CCPath;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CCPathList {
	public static final int PARTCOUNT_MAX = 6;
	
	private final List<CCPath> paths;
	
	public static final CCPathList EMPTY = new CCPathList();
	
	private CCPathList() {
		this.paths = new ArrayList<>();
	}
	
	private CCPathList(List<CCPath> paths) {
		this.paths = new ArrayList<>(paths);
	}
	
	public static CCPathList create(CCPath... paths) {
		List<CCPath> list = new ArrayList<>();
		for (CCPath p : paths) {
			if (!p.isEmpty()) list.add(p);
		}
		return new CCPathList(list);
	}
	
	public static CCPathList createFromJSON(String json) {
		if (Str.isNullOrWhitespace(json)) return EMPTY;
		
		try {
			JSONArray arr = new JSONArray(json);
			List<CCPath> list = new ArrayList<>();
			for (int i = 0; i < arr.length(); i++) {
				String pathStr = arr.getString(i);
				if (!Str.isNullOrWhitespace(pathStr)) {
					list.add(CCPath.create(pathStr));
				}
			}
			return new CCPathList(list);
		} catch (JSONException e) {
			return EMPTY;
		}
	}
	
	public String asJSONArray() {
		JSONArray arr = new JSONArray();
		for (CCPath p : paths) {
			arr.put(p.toString());
		}
		return arr.toString();
	}
	
	public CCPath get(int index) {
		if (index < 0 || index >= paths.size()) return CCPath.Empty;
		return paths.get(index);
	}
	
	public CCPath[] getAsArray() {
		CCPath[] result = new CCPath[PARTCOUNT_MAX];
		for (int i = 0; i < PARTCOUNT_MAX; i++) {
			result[i] = get(i);
		}
		return result;
	}
	
	public List<CCPath> getList() {
		return new ArrayList<>(paths);
	}
	
	public int getCount() {
		return paths.size();
	}
	
	public boolean isEmpty() {
		return paths.isEmpty();
	}
	
	public CCPathList set(int index, CCPath path) {
		if (index < 0 || index >= PARTCOUNT_MAX) {
			throw new IllegalArgumentException("Invalid index: " + index);
		}
		
		List<CCPath> newPaths = new ArrayList<>();
		
		// Ensure list has enough elements
		for (int i = 0; i <= index; i++) {
			if (i < paths.size()) {
				newPaths.add(paths.get(i));
			} else {
				newPaths.add(CCPath.Empty);
			}
		}

		newPaths.set(index, path);

		// Remove trailing empty paths
		while (!newPaths.isEmpty() && newPaths.get(newPaths.size() - 1).isEmpty()) {
			newPaths.remove(newPaths.size() - 1);
		}
		
		return new CCPathList(newPaths);
	}
	
	public CCPathList set(CCPath p0, CCPath p1, CCPath p2, CCPath p3, CCPath p4, CCPath p5) {
		List<CCPath> newPaths = new ArrayList<>(List.of(p0, p1, p2, p3, p4, p5));

		while (!newPaths.isEmpty() && newPaths.get(newPaths.size() - 1).isEmpty()) {
			newPaths.remove(newPaths.size() - 1);
		}

		return new CCPathList(newPaths);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof CCPathList)) return false;
		
		CCPathList other = (CCPathList) obj;
		if (paths.size() != other.paths.size()) return false;
		
		for (int i = 0; i < paths.size(); i++) {
			if (!paths.get(i).equals(other.paths.get(i))) return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return paths.hashCode();
	}
	
	@Override
	public String toString() {
		return asJSONArray();
	}
}