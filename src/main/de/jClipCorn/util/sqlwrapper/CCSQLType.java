package de.jClipCorn.util.sqlwrapper;

import de.jClipCorn.features.log.CCLog;

import java.sql.Types;

public enum CCSQLType {
	INTEGER,
	VARCHAR,
	BLOB,
	BIT,
	BIGINT,
	DATE,
	TINYINT,
	SMALLINT,
	REAL;

	public boolean isCallableAsString() {
		switch (this) {
			case REAL: case BIT: case BIGINT: case INTEGER: case BLOB: case TINYINT: case SMALLINT: return false;
			case VARCHAR: case DATE: return true;
		}
		return false;
	}

	public boolean isCallableAsInteger() {
		switch (this) {
			case BIT: case BIGINT: case INTEGER: case TINYINT: case SMALLINT: return true;
			case REAL: case BLOB: case VARCHAR: case DATE: return false;
		}
		return false;
	}

	public boolean isCallableAsBoolean() {
		switch (this) {
			case BIT: return true;
			case REAL: case BIGINT: case INTEGER: case TINYINT: case SMALLINT: case BLOB: case VARCHAR: case DATE: return false;
		}
		return false;
	}

	public boolean isCallableAsShort() {
		switch (this) {
			case BIT: case TINYINT: case SMALLINT: return true;
			case REAL: case BIGINT: case INTEGER: case BLOB: case VARCHAR: case DATE: return false;
		}
		return false;
	}

	public boolean isCallableAsLong() {
		switch (this) {
			case BIT: case BIGINT: case INTEGER: case TINYINT: case SMALLINT: return true;
			case REAL: case BLOB: case VARCHAR: case DATE: return false;
		}
		return false;
	}

	public boolean isCallableAsDate() {
		switch (this) {
			case REAL: case BIT: case BIGINT: case INTEGER: case TINYINT: case SMALLINT: case BLOB: case VARCHAR: return false;
			case DATE: return true;
		}
		return false;
	}

	public boolean isCallableAsBlob() {
		switch (this) {
			case REAL: case BIT: case BIGINT: case INTEGER: case TINYINT: case SMALLINT: case DATE: case VARCHAR: return false;
			case BLOB: return true;
		}
		return false;
	}

	public boolean isCallableAsFloat() {
		switch (this) {
			case BLOB: case BIT: case BIGINT: case INTEGER: case TINYINT: case SMALLINT: case DATE: case VARCHAR: return false;
			case REAL: return true;
		}
		return false;
	}

	public int getSQLType() {
		switch (this)
		{
			case INTEGER:  return Types.INTEGER;
			case VARCHAR:  return Types.VARCHAR;
			case BLOB:     return Types.BLOB;
			case BIT:      return Types.BIT;
			case BIGINT:   return Types.BIGINT;
			case DATE:     return Types.DATE;
			case TINYINT:  return Types.TINYINT;
			case SMALLINT: return Types.SMALLINT;
			case REAL:     return Types.REAL;
		}

		CCLog.addDefaultSwitchError(this, this);
		return Types.INTEGER;
	}
}
