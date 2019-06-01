package de.jClipCorn.util.sqlwrapper;

public enum CCSQLType {
	INTEGER,
	VARCHAR,
	BLOB,
	BIT,
	BIGINT,
	DATE,
	TINYINT,
	SMALLINT;

	public boolean isCallableAsString() {
		switch (this) {
			case BIT:
			case BIGINT:
			case INTEGER:
			case BLOB:
			case TINYINT:
			case SMALLINT:
				return false;
			case VARCHAR:
			case DATE:
				return true;
		}

		return false;
	}

	public boolean isCallableAsInteger() {
		switch (this) {
			case BIT:
			case BIGINT:
			case INTEGER:
			case TINYINT:
			case SMALLINT:
				return true;
			case BLOB:
			case VARCHAR:
			case DATE:
				return false;
		}

		return false;
	}

	public boolean isCallableAsBoolean() {
		switch (this) {
			case BIT:
				return true;
			case BIGINT:
			case INTEGER:
			case TINYINT:
			case SMALLINT:
			case BLOB:
			case VARCHAR:
			case DATE:
				return false;
		}

		return false;
	}

	public boolean isCallableAsShort() {
		switch (this) {
			case BIT:
			case TINYINT:
			case SMALLINT:
				return true;
			case BIGINT:
			case INTEGER:
			case BLOB:
			case VARCHAR:
			case DATE:
				return false;
		}

		return false;
	}

	public boolean isCallableAsLong() {
		switch (this) {
			case BIT:
			case BIGINT:
			case INTEGER:
			case TINYINT:
			case SMALLINT:
				return true;
			case BLOB:
			case VARCHAR:
			case DATE:
				return false;
		}

		return false;
	}

	public boolean isCallableAsDate() {
		switch (this) {
			case BIT:
			case BIGINT:
			case INTEGER:
			case TINYINT:
			case SMALLINT:
			case BLOB:
			case VARCHAR:
				return false;
			case DATE:
				return true;
		}

		return false;
	}
}
