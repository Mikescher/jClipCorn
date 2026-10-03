package de.jClipCorn.test;

import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileLockManager;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.helper.ApplicationHelper;
import org.json.JSONObject;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestFileLockManager extends ClipCornBaseTest {

	private static final long OWN_PID = ProcessHandle.current().pid();
	private static final Instant LONG_AGO = Instant.parse("2000-01-01T00:00:00Z");

	private FSPath createLockTarget() {
		var path = SimpleFileUtils.getSystemTempFile("db");
		var lockFile = lockFileOf(path);
		CLEANUP.push(() -> {
			FileLockManager.unlockFile(path);
			if (lockFile.exists()) lockFile.deleteWithException();
		});
		return path;
	}

	private static FSPath lockFileOf(FSPath path) {
		return FSPath.create(path + ".~lock");
	}

	private static JSONObject readLock(FSPath path) throws Exception {
		return new JSONObject(lockFileOf(path).readAsUTF8TextFile());
	}

	private static String writeLock(FSPath path, long pid, Instant created, Instant updated, String host) throws Exception {
		var content = "{\"pid\": " + pid + ", \"c\": \"" + created + "\", \"u\": \"" + updated + "\", \"host\": " + JSONObject.quote(host) + "}";
		lockFileOf(path).writeAsUTF8TextFile(content);
		return content;
	}

	private static Instant now() {
		return Instant.now().truncatedTo(ChronoUnit.SECONDS);
	}

	private static long otherLocalPid() {
		return ProcessHandle.current().parent().orElseThrow().pid();
	}

	@Test
	public void testLockWritesJson() throws Exception {
		var path = createLockTarget();

		assertTrue(FileLockManager.tryLockFile(path, false));

		var json = readLock(path);
		assertEquals(OWN_PID, json.getLong("pid"));
		assertEquals(ApplicationHelper.getHostname(), json.getString("host"));
		assertEquals(json.getString("c"), json.getString("u"));
		Instant.parse(json.getString("c"));

		assertTrue(FileLockManager.isLocked(path));
	}

	@Test
	public void testRefreshUpdatesOnlyU() throws Exception {
		var path = createLockTarget();
		var lockFile = lockFileOf(path);

		assertTrue(FileLockManager.tryLockFile(path, false));
		var created = readLock(path).getString("c");

		writeLock(path, OWN_PID, LONG_AGO, LONG_AGO, ApplicationHelper.getHostname());
		Files.setLastModifiedTime(lockFile.toPath(), FileTime.from(LONG_AGO));

		FileLockManager.refreshHeldLocks();

		var json = readLock(path);
		assertEquals(created, json.getString("c"));
		assertTrue(Instant.parse(json.getString("u")).isAfter(LONG_AGO));
		assertTrue(Files.getLastModifiedTime(lockFile.toPath()).toInstant().isAfter(LONG_AGO));
	}

	@Test
	public void testRefreshRecreatesVanishedLock() throws Exception {
		var path = createLockTarget();

		assertTrue(FileLockManager.tryLockFile(path, false));
		var created = readLock(path).getString("c");

		lockFileOf(path).deleteWithException();
		FileLockManager.refreshHeldLocks();

		assertEquals(created, readLock(path).getString("c"));
	}

	@Test
	public void testRefreshLeavesForeignLockAlone() throws Exception {
		var path = createLockTarget();

		assertTrue(FileLockManager.tryLockFile(path, false));

		var foreign = writeLock(path, 1, LONG_AGO, LONG_AGO, "some-other-host");

		FileLockManager.refreshHeldLocks();

		assertEquals(foreign, lockFileOf(path).readAsUTF8TextFile());
	}

	@Test
	public void testUnlockStopsRefresh() throws Exception {
		var path = createLockTarget();

		assertTrue(FileLockManager.tryLockFile(path, false));
		assertTrue(FileLockManager.unlockFile(path));
		assertFalse(lockFileOf(path).exists());

		FileLockManager.refreshHeldLocks();

		assertFalse(lockFileOf(path).exists());
	}

	@Test
	public void testLegacyPidLockIsRecognized() throws Exception {
		var path = createLockTarget();

		lockFileOf(path).writeAsUTF8TextFile(Long.toString(OWN_PID));

		assertTrue(FileLockManager.isLocked(path));
		assertTrue(FileLockManager.tryLockFile(path, false));
		assertTrue(FileLockManager.unlockFile(path));
		assertFalse(lockFileOf(path).exists());
	}

	@Test
	public void testUnreadableLockIsTakenOver() throws Exception {
		var path = createLockTarget();

		lockFileOf(path).writeAsUTF8TextFile("{\"pid\": 12");

		assertFalse(FileLockManager.isLocked(path));
		assertTrue(FileLockManager.tryLockFile(path, false));
		assertEquals(OWN_PID, readLock(path).getLong("pid"));
	}

	@Test
	public void testLiveLocalProcessHoldsLock() throws Exception {
		var path = createLockTarget();

		var content = writeLock(path, otherLocalPid(), now(), now(), ApplicationHelper.getHostname());

		assertTrue(FileLockManager.isLocked(path));
		assertFalse(FileLockManager.tryLockFile(path, false));
		assertFalse(FileLockManager.unlockFile(path));
		assertEquals(content, lockFileOf(path).readAsUTF8TextFile());
	}

	@Test
	public void testReusedPidIsStale() throws Exception {
		var path = createLockTarget();

		// the process behind this PID started long after the lock was taken
		writeLock(path, otherLocalPid(), LONG_AGO, LONG_AGO, ApplicationHelper.getHostname());

		assertFalse(FileLockManager.isLocked(path));
		assertTrue(FileLockManager.tryLockFile(path, false));
		assertEquals(OWN_PID, readLock(path).getLong("pid"));
	}

	@Test
	public void testDeadPidIsStale() throws Exception {
		var path = createLockTarget();

		writeLock(path, Integer.MAX_VALUE, now(), now(), ApplicationHelper.getHostname());

		assertFalse(FileLockManager.isLocked(path));
		assertTrue(FileLockManager.tryLockFile(path, false));
	}

	@Test
	public void testForeignHostWithRecentHeartbeatHoldsLock() throws Exception {
		var path = createLockTarget();

		// our own PID on another host is somebody else
		var content = writeLock(path, OWN_PID, LONG_AGO, now().minus(FileLockManager.HEARTBEAT_INTERVAL_MINUTES, ChronoUnit.MINUTES), "some-other-host");

		assertTrue(FileLockManager.isLocked(path));
		assertFalse(FileLockManager.tryLockFile(path, false));
		assertFalse(FileLockManager.unlockFile(path));
		assertEquals(content, lockFileOf(path).readAsUTF8TextFile());
	}

	@Test
	public void testForeignHostWithOldHeartbeatIsStale() throws Exception {
		var path = createLockTarget();

		writeLock(path, OWN_PID, LONG_AGO, now().minus(FileLockManager.FOREIGN_LOCK_TIMEOUT_MINUTES + 1, ChronoUnit.MINUTES), "some-other-host");

		assertFalse(FileLockManager.isLocked(path));
		assertTrue(FileLockManager.tryLockFile(path, false));
		assertEquals(ApplicationHelper.getHostname(), readLock(path).getString("host"));
	}
}
