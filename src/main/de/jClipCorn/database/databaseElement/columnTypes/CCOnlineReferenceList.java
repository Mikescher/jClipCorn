package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.exceptions.OnlineRefFormatException;
import de.jClipCorn.util.helper.ObjectUtils;
import de.jClipCorn.util.stream.CCIterable;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CCOnlineReferenceList implements CCIterable<CCSingleOnlineReference> {
	public static final CCOnlineReferenceList EMPTY = new CCOnlineReferenceList(CCSingleOnlineReference.EMPTY, new ArrayList<>());

	private static final String SEPERATOR = ";"; //$NON-NLS-1$

	public final CCSingleOnlineReference Main;
	public final List<CCSingleOnlineReference> Additional;
	
	private CCOnlineReferenceList(CCSingleOnlineReference main, List<CCSingleOnlineReference> add) {
		this.Main = main;
		this.Additional = Collections.unmodifiableList(add);
	}

	@Override
	public @NotNull Iterator<CCSingleOnlineReference> iterator() {
		return ccstream();
	}

	public CCStream<CCSingleOnlineReference> ccstream() {
		return CCStreams.single(Main).append(Additional).filter(CCSingleOnlineReference::isSet);
	}

	public boolean isEmpty() {
		return Main.isUnset() && Additional.isEmpty();
	}
	
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof CCOnlineReferenceList)) return false;
		
		CCOnlineReferenceList gl = (CCOnlineReferenceList) o;

		if (!ObjectUtils.IsEqual(gl.Main, this.Main)) return false;
		if (gl.Additional.size() != this.Additional.size()) return false;
		
		for (int i = 0; i < Additional.size(); i++) {
			if (!ObjectUtils.IsEqual(gl.Additional.get(i), this.Additional.get(i))) return false;
		}
		
		return true;
	}

	public boolean equals(CCOnlineReferenceList gl) {
		if (!ObjectUtils.IsEqual(gl.Main, this.Main)) return false;
		if (gl.Additional.size() != this.Additional.size()) return false;

		for (int i = 0; i < Additional.size(); i++) {
			if (!ObjectUtils.IsEqual(gl.Additional.get(i), this.Additional.get(i))) return false;
		}

		return true;
	}

	public boolean equalsIgnoreAdditionalOrder(CCOnlineReferenceList gl) {
		if (gl == null) return false;
		
		if (!ObjectUtils.IsEqual(gl.Main, this.Main)) return false;
		if (gl.Additional.size() != this.Additional.size()) return false;

		for (int i = 0; i < Additional.size(); i++) {
			if (!gl.Additional.contains(this.Additional.get(i))) return false;
		}
		
		return true;
	}

	public boolean equalsAnyOrder(CCOnlineReferenceList other) {
		var a = this.ccstream().autosortByProperty(CCSingleOnlineReference::toSerializationString);
		var b = other.ccstream().autosortByProperty(CCSingleOnlineReference::toSerializationString);

		return CCStreams.equalsElementwise(a, b, CCSingleOnlineReference::equals);
	}

	public boolean equalsAnyNonEmptySubset(CCOnlineReferenceList other) {
		for (var r : this.ccstream()) {
			if (other.ccstream().contains(r, CCSingleOnlineReference::equals)) return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hc = Main.hashCode();
		for (CCSingleOnlineReference g : Additional) {
			hc = 17*hc ^ hc ^ g.hashCode();
		}
		return hc;
	}

	public static CCOnlineReferenceList create(CCSingleOnlineReference primary, List<CCSingleOnlineReference> additional) {
		return new CCOnlineReferenceList(primary, additional);
	}

	public static CCOnlineReferenceList create(CCSingleOnlineReference primary, CCSingleOnlineReference secondary) {
		if (primary == null) return EMPTY;
		if (secondary == null || secondary.isUnset()) return new CCOnlineReferenceList(primary, new ArrayList<>());
		return new CCOnlineReferenceList(primary, Collections.singletonList(secondary));
	}

	public static CCOnlineReferenceList parse(String data) throws OnlineRefFormatException {
		CCSingleOnlineReference main = null;
		List<CCSingleOnlineReference> lst = new ArrayList<>();
		
		for (String str : data.split(SEPERATOR)) {
			if (main == null) main = CCSingleOnlineReference.parse(str);
			else lst.add(CCSingleOnlineReference.parse(str));
		}
		
		if (main == null) main = CCSingleOnlineReference.EMPTY;
		
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
		for (CCSingleOnlineReference sor : Additional) if (sor.isInvalid()) return false;
		for (CCSingleOnlineReference sor : Additional) if (sor.isUnset()) return false;
		if (Main.isUnset() && Additional.size()>0) return false;
		return true;
	}

	public boolean isMainSet() {
		return Main.isSet();
	}

	public boolean hasAdditional() {
		return !Additional.isEmpty();
	}
	
	public String toSourceConcatString() {
		return CCStreams.iterate(this).stringjoin(r -> r.type.asString(), " | "); //$NON-NLS-1$
	}

	public CCOnlineReferenceList addAdditional(CCSingleOnlineReference ref) {
		return new CCOnlineReferenceList(this.Main, CCStreams.iterate(Additional).append(ref).unique().enumerate());
	}

	public int totalCount() {
		return (Main.isSet() ? 1 : 0) + Additional.size();
	}
}
