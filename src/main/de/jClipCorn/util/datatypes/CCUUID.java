package de.jClipCorn.util.datatypes;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.UUIDFormatException;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * The identity of a database row (movies, series, seasons, episodes, covers).
 *
 * Values are UUIDv7 (RFC 9562 §5.7): the leading 48 bit are a unix-ms timestamp, so the fixed-width
 * canonical hex form sorts lexicographically in creation order. Everything that relies on
 * {@code ORDER BY ID} to mean "ordered by insertion" depends on that.
 */
public class CCUUID implements Comparable<CCUUID> {

	/** nil UUID - the "unset"/"no reference" sentinel (e.g. an element without a cover) */
	public final static CCUUID EMPTY = new CCUUID(new UUID(0, 0));

	private final static long VERSION_7    = 0x7000L;
	private final static long VARIANT_RFC  = 0x8000_0000_0000_0000L;
	private final static long MASK_RAND_A  = 0x0FFFL;
	private final static long MASK_RAND_B  = 0x3FFF_FFFF_FFFF_FFFFL;
	private final static long MASK_TS      = 0x0000_FFFF_FFFF_FFFFL;

	/**
	 * 2000-01-01T00:00:00Z - far enough in the past that every runtime-generated UUID
	 * (real wall-clock) sorts after every migration-derived one.
	 */
	private final static long MIGRATION_EPOCH_MS = 946_684_800_000L;

	private final static String MIGRATION_HASH_PREFIX = "jcc-uuidv7-migration"; //$NON-NLS-1$

	/**
	 * {@link UUID#fromString} alone is not enough: it also accepts short, overlong and signed groups
	 * ({@code 1-1-1-1-1}, {@code ...-+0c04fd430c8}) and maps them onto a *different*, perfectly valid
	 * id. Such an id then silently misses every {@code userdata.X.ID = main.X.ID} join.
	 */
	private final static Pattern CANONICAL = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"); //$NON-NLS-1$

	private final static SecureRandom RANDOM = new SecureRandom();

	private static long _lastTs  = -1;
	private static long _lastMsb = 0;
	private static long _lastLsb = 0;

	private final UUID _value;

	private CCUUID(@NotNull UUID v) {
		_value = v;
	}

	public static CCUUID of(UUID v) {
		if (v == null) return EMPTY;
		return new CCUUID(v);
	}

	/**
	 * Upper- and lowercase hex are both accepted; both hex cases and the surrounding whitespace are
	 * normalised away, so {@code parse(v).toString()} is always the canonical lowercase form.
	 */
	public static CCUUID parse(String v) throws UUIDFormatException {
		if (Str.isNullOrWhitespace(v)) throw new UUIDFormatException("Cannot parse empty string as UUID"); //$NON-NLS-1$

		v = v.trim();
		if (!CANONICAL.matcher(v).matches()) throw new UUIDFormatException("Not a canonical UUID: '" + v + "'"); //$NON-NLS-1$ //$NON-NLS-2$

		return new CCUUID(UUID.fromString(v));
	}

	/** Parses {@code v}, falling back to {@link #EMPTY} for null/empty/malformed input. */
	public static CCUUID parseOrEmpty(String v) {
		try {
			return parse(v);
		} catch (UUIDFormatException e) {
			return EMPTY;
		}
	}

	public static boolean isValid(String v) {
		if (Str.isNullOrWhitespace(v)) return false;

		return CANONICAL.matcher(v.trim()).matches();
	}

	/**
	 * A fresh UUIDv7. Monotonic per RFC 9562 §6.2 method 2: several ids created within the same
	 * millisecond still sort in creation order, which is what {@code ORDER BY ID} relies on.
	 */
	public static synchronized CCUUID generate() {
		long ts = System.currentTimeMillis();

		long msb;
		long lsb;

		if (ts > _lastTs) {
			msb = ((ts & MASK_TS) << 16) | VERSION_7 | (RANDOM.nextLong() & MASK_RAND_A);
			lsb = (RANDOM.nextLong() & MASK_RAND_B) | VARIANT_RFC;
		} else {
			// same millisecond (or clock went backwards) -> increment the 74bit rand_a||rand_b counter
			long randA = _lastMsb & MASK_RAND_A;
			long randB = (_lastLsb & MASK_RAND_B) + 1;

			ts = _lastTs;

			if (randB > MASK_RAND_B) {
				randB = 0;
				randA++;
				if (randA > MASK_RAND_A) {
					randA = 0;
					ts++;
				}
			}

			msb = ((ts & MASK_TS) << 16) | VERSION_7 | randA;
			lsb = randB | VARIANT_RFC;
		}

		_lastTs  = ts;
		_lastMsb = msb;
		_lastLsb = lsb;

		return new CCUUID(new UUID(msb, lsb));
	}

	/**
	 * The UUID that the v34-&gt;v35 migration assigns to the row that used to have the integer id
	 * {@code oldId} in {@code table}.
	 *
	 * Pure function of its arguments, so two installations migrate the same source database into
	 * byte-identical ids without coordinating, and history rows pointing at long-deleted entities
	 * can still be rewritten.
	 */
	public static CCUUID deriveMigrationUUID(String table, int oldId) {
		// strictly increasing in oldId -> preserves the previous insertion order
		long tsMs = MIGRATION_EPOCH_MS + oldId;

		byte[] h = sha256(MIGRATION_HASH_PREFIX + '\0' + table + '\0' + oldId);

		long msb = (tsMs & MASK_TS) << 16;
		msb |= VERSION_7;
		msb |= (((h[0] & 0xFFL) << 4) | ((h[1] & 0xF0L) >>> 4)) & MASK_RAND_A;

		long lsb = 0;
		for (int i = 2; i < 10; i++) lsb = (lsb << 8) | (h[i] & 0xFFL);
		lsb &= MASK_RAND_B;
		lsb |= VARIANT_RFC;

		return new CCUUID(new UUID(msb, lsb));
	}

	private static byte[] sha256(String v) {
		try {
			return MessageDigest.getInstance("SHA-256").digest(v.getBytes(StandardCharsets.UTF_8)); //$NON-NLS-1$
		} catch (NoSuchAlgorithmException e) {
			throw new Error(e);
		}
	}

	public UUID asUUID() {
		return _value;
	}

	public boolean isEmpty() {
		return _value.getMostSignificantBits() == 0 && _value.getLeastSignificantBits() == 0;
	}

	/**
	 * A short form for UI captions where a full UUID would not fit.
	 *
	 * Taken from the *end* of the value: the leading characters are the millisecond timestamp and are
	 * identical for everything created in the same ~65s window (and for whole id ranges after the
	 * migration), while the trailing ones are random.
	 */
	public String toShortString() {
		return toString().substring(28);
	}

	/** Matches the full canonical form or the short form that the UI shows. */
	public boolean matches(String v) {
		if (Str.isNullOrWhitespace(v)) return false;

		v = v.trim();
		return v.equalsIgnoreCase(toString()) || v.equalsIgnoreCase(toShortString());
	}

	@Override
	public String toString() {
		return _value.toString();
	}

	@Override
	public int compareTo(CCUUID o) {
		// unsigned, so the order matches the order of the canonical hex representation
		int c = Long.compareUnsigned(_value.getMostSignificantBits(), o._value.getMostSignificantBits());
		if (c != 0) return c;
		return Long.compareUnsigned(_value.getLeastSignificantBits(), o._value.getLeastSignificantBits());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		return _value.equals(((CCUUID) o)._value);
	}

	@Override
	public int hashCode() {
		return _value.hashCode();
	}

	public static boolean isEqual(CCUUID a, CCUUID b) {
		if (a == null) return b == null;
		return a.equals(b);
	}
}
