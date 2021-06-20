package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum EPropertyType implements ContinoousEnum<EPropertyType> {
	OBJECTIVE_METADATA(0),        // Metadata - should be equal across different databases - only dependent on entry
	USER_METADATA(1),             // Metadata - should be different across different databases - can be choosen freely by the individual user
	LOCAL_FILE_REF_SUBJECTIVE(2), // Depends on a file on the filesystem, can be different even if two databases have the same file
	LOCAL_FILE_REF_OBJECTIVE(3),  // Depends on a file on the filesystem, are equal if they reference the same file
	DATABASE_REF(4),              // A reference to another row/table in the database
	DATABASE_PRIMARY_ID(5),       // The primary id in the database (readonly)
	DATABASE_READONLY(6);         // A non-writable database field (only written once, on creation) (readonly)

	private final int id;

	private final static String[] NAMES =
	{
		"ObjectiveMetadata",
		"UserMetadata",
		"LocalFileReferenceSubjective",
		"LocalFileReferenceObjective",
		"DatabaseRef",
		"DatabasePrimaryID",
		"DatabaseReadonlyField"
	};

	private static final EnumWrapper<EPropertyType> wrapper = new EnumWrapper<>(OBJECTIVE_METADATA);

	EPropertyType(int val) {
		id = val;
	}

	public static EnumWrapper<EPropertyType> getWrapper() {
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
	public EPropertyType[] evalues() {
		return EPropertyType.values();
	}

	public boolean isReadonly() {
		return (this == DATABASE_PRIMARY_ID) || (this == DATABASE_READONLY);
	}
}
