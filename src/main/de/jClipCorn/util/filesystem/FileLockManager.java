package de.jClipCorn.util.filesystem;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.helper.ApplicationHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Cross plattfom lock file implementation
 *
 * Lock file content: {"pid": 1234, "c": "<acquired, ISO-8601 UTC>", "u": "<last heartbeat, ISO-8601 UTC>", "host": "..."}
 * While held, the file is rewritten every HEARTBEAT_INTERVAL_MINUTES, so external tools can tell a held lock from a stale one by `u` / mtime.
 */
public class FileLockManager {
	private static final String LOCK_EXTENSION = ".~lock"; //$NON-NLS-1$

	public static final long HEARTBEAT_INTERVAL_MINUTES = 60;

	/** A lock from another host is held as long as its `u` is younger than this - its PID means nothing here. */
	public static final long FOREIGN_LOCK_TIMEOUT_MINUTES = 3 * HEARTBEAT_INTERVAL_MINUTES;

	/** `c` is truncated to seconds and process start times are not exact either. */
	private static final long PROCESS_START_TOLERANCE_SECONDS = 60;

	private record LockOwner(Long pid, String host, Instant created, Instant updated) {
		boolean isUs() {
			return pid != null && pid == ProcessHandle.current().pid() && !isForeignHost();
		}

		boolean isForeignHost() {
			// pre-JSON lock files carry no host
			return host != null && !host.isEmpty() && !host.equals(ApplicationHelper.getHostname());
		}

		boolean isHeld() {
			if (isForeignHost()) {
				return updated != null && updated.isAfter(Instant.now().minus(FOREIGN_LOCK_TIMEOUT_MINUTES, ChronoUnit.MINUTES));
			}

			if (pid == null) return false;

			var proc = ProcessHandle.of(pid).filter(ProcessHandle::isAlive);
			if (proc.isEmpty()) return false;

			// a process that started after the lock was taken has only reused the PID of the dead holder
			var started = proc.get().info().startInstant();
			return created == null || started.isEmpty() || !started.get().isAfter(created.plusSeconds(PROCESS_START_TOLERANCE_SECONDS));
		}
	}

	private static final Object _sync = new Object();

	/** lock file -> time it was acquired (the `c` field) */
	private static final Map<FSPath, Instant> _heldLocks = new HashMap<>();

	private static ScheduledExecutorService _heartbeat = null;

	public static boolean tryLockFile(FSPath path, boolean registerShutdownError) throws IOException {
		synchronized (_sync) {
			var lockFile = getLockFile(path);

			if (lockFile.exists()) {
				var owner = readLockFile(lockFile);

				if (owner.isUs()) {
					var created = _heldLocks.getOrDefault(lockFile, owner.created() != null ? owner.created() : now());
					holdLock(lockFile, created);
					return true;
				}

				if (owner.isHeld()) return false;

				if (registerShutdownError) {
					CCLog.addWarning(LocaleBundle.getString("LogMessage.IncorrectShutdown")); //$NON-NLS-1$
				}
			}

			var now = now();
			writeLockFile(lockFile, now, now);
			holdLock(lockFile, now);
			return true;
		}
	}

	public static boolean unlockFile(FSPath path) throws IOException {
		synchronized (_sync) {
			var lockFile = getLockFile(path);

			_heldLocks.remove(lockFile);

			if (lockFile.exists()) {
				var owner = readLockFile(lockFile);

				if (owner.isUs() || !owner.isHeld()) {
					lockFile.deleteWithException();
					return true;
				}

				return false;
			}

			return true;
		}
	}

	public static boolean isLocked(FSPath path) throws IOException {
		var lockFile = getLockFile(path);

		if (! lockFile.exists()) {
			return false;
		}

		return readLockFile(lockFile).isHeld();
	}

	/** Rewrites every lock this process holds with a fresh `u` (and thereby a fresh mtime). */
	public static void refreshHeldLocks() {
		synchronized (_sync) {
			var it = _heldLocks.entrySet().iterator();
			while (it.hasNext()) {
				var entry = it.next();
				var lockFile = entry.getKey();

				try {
					if (! lockFile.exists()) {
						CCLog.addWarning("Lock file vanished while held, recreating it: " + lockFile); //$NON-NLS-1$
					} else if (! readLockFile(lockFile).isUs()) {
						CCLog.addWarning("Lock file was taken over by another process, no longer refreshing it: " + lockFile); //$NON-NLS-1$
						it.remove();
						continue;
					}

					writeLockFile(lockFile, entry.getValue(), now());
				} catch (Exception e) {
					CCLog.addWarning("Cannot refresh lock file " + lockFile, e); //$NON-NLS-1$
				}
			}
		}
	}

	private static void holdLock(FSPath lockFile, Instant created) {
		_heldLocks.put(lockFile, created);

		if (_heartbeat == null) {
			_heartbeat = Executors.newSingleThreadScheduledExecutor(r ->
			{
				// daemon, so a lock that is never released can not keep the JVM alive
				Thread t = new Thread(r, "LOCKFILE_HEARTBEAT"); //$NON-NLS-1$
				t.setDaemon(true);
				return t;
			});
			_heartbeat.scheduleWithFixedDelay(FileLockManager::refreshHeldLocks, HEARTBEAT_INTERVAL_MINUTES, HEARTBEAT_INTERVAL_MINUTES, TimeUnit.MINUTES);
		}
	}

	private static FSPath getLockFile(FSPath path) {
		return FSPath.create(path.toString() + LOCK_EXTENSION);
	}

	private static Instant now() {
		return Instant.now().truncatedTo(ChronoUnit.SECONDS);
	}

	@SuppressWarnings("nls")
	private static void writeLockFile(FSPath lockFile, Instant created, Instant updated) throws IOException {
		lockFile.writeAsUTF8TextFile("{"
				+ "\"pid\": " + ProcessHandle.current().pid() + ", "
				+ "\"c\": " + JSONObject.quote(created.toString()) + ", "
				+ "\"u\": " + JSONObject.quote(updated.toString()) + ", "
				+ "\"host\": " + JSONObject.quote(ApplicationHelper.getHostname())
				+ "}");
	}

	@SuppressWarnings("nls")
	private static LockOwner readLockFile(FSPath lockFile) throws IOException {
		var content = lockFile.readAsUTF8TextFile().trim();

		// pre-JSON format: the bare PID
		if (! content.startsWith("{")) return new LockOwner(parsePid(content), null, null, null);

		try {
			var json = new JSONObject(content);

			return new LockOwner(
					parsePid(json.optString("pid", "")),
					json.optString("host", ""),
					parseInstant(json.optString("c", "")),
					parseInstant(json.optString("u", "")));
		} catch (JSONException e) {
			// half-written or foreign content - nobody we can identify holds it
			return new LockOwner(null, "", null, null);
		}
	}

	private static Long parsePid(String value) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static Instant parseInstant(String value) {
		try {
			return Instant.parse(value);
		} catch (DateTimeParseException e) {
			return null;
		}
	}
}
