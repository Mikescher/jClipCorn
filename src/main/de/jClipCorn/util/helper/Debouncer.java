package de.jClipCorn.util.helper;

import de.jClipCorn.features.log.CCLog;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Runs {@code action} once, {@code delayMillis} after the most recent {@link #trigger()} - every
 * trigger restarts the timer, so a burst of changes results in a single run once the burst ends.
 * <p>
 * {@link #trigger()} does no work beyond rescheduling and is safe to call from the EDT; the action
 * itself always runs on the debouncer's own daemon thread and never concurrently with itself.
 */
public class Debouncer implements AutoCloseable {

	private final ScheduledExecutorService exec;
	private final long                     delayMillis;
	private final Runnable                 action;

	private ScheduledFuture<?> pending;
	private boolean            closed;

	public Debouncer(String threadName, long delayMillis, Runnable action) {
		this.delayMillis = delayMillis;
		this.action      = action;
		this.exec        = Executors.newSingleThreadScheduledExecutor(r ->
		{
			// daemon, so a forgotten close() can never keep the JVM alive
			Thread t = new Thread(r, threadName);
			t.setDaemon(true);
			return t;
		});
	}

	public synchronized void trigger() {
		if (closed) return;

		if (pending != null) pending.cancel(false);
		pending = exec.schedule(this::run, delayMillis, TimeUnit.MILLISECONDS);
	}

	private void run() {
		synchronized (this) { pending = null; }

		try {
			action.run();
		} catch (Throwable e) {
			CCLog.addError(e);
		}
	}

	/**
	 * Runs a still-pending action immediately and waits for it to finish - call this before the
	 * resources the action uses (the database connection) are torn down.
	 */
	public void flushAndClose(long timeoutMillis) {
		boolean hadPending;
		synchronized (this) {
			if (closed) return;
			closed     = true;
			hadPending = (pending != null) && pending.cancel(false);
			pending    = null;
		}

		if (hadPending) exec.submit(this::run);

		exec.shutdown();
		try {
			exec.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void close() {
		flushAndClose(5000);
	}
}
