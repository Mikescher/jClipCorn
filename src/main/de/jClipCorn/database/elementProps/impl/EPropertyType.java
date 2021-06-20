package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum EPropertyType implements ContinoousEnum<EPropertyType> {
	OBJECTIVE_METADATA(0),
	USER_METADATA(1),
	LOCAL_FILE_REF(2),
	DATABASE_REF(3),
	DATABASE_PRIMARY_ID(4),
	DATABASE_READONLY(5);

	private final int id;

	private final static String[] NAMES = {"ObjectiveMetadata", "UserMetadata", "LocalFileReference", "DatabaseRef", "DatabasePrimaryID", "DatabaseReadonlyField"};

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
