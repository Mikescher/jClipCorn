package de.jClipCorn.database.databaseElement.columnTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.jClipCorn.util.exceptions.OnlineRefFormatException;
import de.jClipCorn.util.stream.CCStreams;

public class CCOnlineReferenceList implements Iterable<CCSingleOnlineReference> {
	private static final String SEPERATOR = ";"; //$NON-NLS-1$

	public final CCSingleOnlineReference Main;
	public final List<CCSingleOnlineReference> Additional;
	
	private CCOnlineReferenceList(CCSingleOnlineReference main, List<CCSingleOnlineReference> add) {
		this.Main = main;
		this.Additional = Collections.unmodifiableList(add);
	}

	@Override
	public Iterator<CCSingleOnlineReference> iterator() {
		return CCStreams.single(Main).append(Additional).filter(r -> r.isSet());
	}
	
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof CCOnlineReferenceList)) return false;
		
		CCOnlineReferenceList gl = (CCOnlineReferenceList) o;

		if (gl.Main != this.Main) return false;
		if (gl.Additional.size() != this.Additional.size()) return false;
		
		for (int i = 0; i < Additional.size(); i++) {
			if (! gl.Additional.get(i).equals(Additional.get(i))) return false;
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		int hc = Main.hashCode();
		for (CCSingleOnlineReference g : Additional) {
			hc = 17*hc ^ hc ^ g.hashCode();
		}
		return hc;
	}

	public static CCOnlineReferenceList createEmpty() {
		return new CCOnlineReferenceList(CCSingleOnlineReference.createNone(), new ArrayList<>());
	}

	public static CCOnlineReferenceList create(CCSingleOnlineReference primary, List<CCSingleOnlineReference> additional) {
		return new CCOnlineReferenceList(primary, additional);
	}

	public static CCOnlineReferenceList create(CCSingleOnlineReference primary, CCSingleOnlineReference secondary) {
		if (primary == null) return createEmpty();
		if (secondary == null || secondary.isUnset()) return new CCOnlineReferenceList(primary, new ArrayList<>());
		return new CCOnlineReferenceList(primary, Arrays.asList(secondary));
	}

	public static CCOnlineReferenceList parse(String data) throws OnlineRefFormatException {
		CCSingleOnlineReference main = null;
		List<CCSingleOnlineReference> lst = new ArrayList<>();
		
		for (String str : data.split(SEPERATOR)) {
			if (main == null) main = CCSingleOnlineReference.parse(str);
			else lst.add(CCSingleOnlineReference.parse(str));
		}
		
		if (main == null) main = CCSingleOnlineReference.createNone();
		
		return new CCOnlineReferenceList(main, lst);
	}

	public String toSerializationString() {
		if (Main.isUnset() && Additional.isEmpty()) return ""; //$NON-NLS-1$
		
		StringBuilder b = new StringBuilder();
		b.append(Main.toSerializationString());
		
		for (CCSingleOnlineReference sor : Additional) b.append(SEPERATOR).append(sor.toSerializationString());
		
		return b.toString();
	}

	public boolean isValid() {
		if (!Main.isValid()) return false;
		for (CCSingleOnlineReference sor : Additional) if (!sor.isValid()) return false;
		return true;
	}

	public boolean isMainSet() {
		return Main.isSet();
	}

	public boolean hasAdditional() {
		return !Additional.isEmpty();
	}
}
