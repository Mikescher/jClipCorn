package de.jClipCorn.test;

import de.jClipCorn.util.helper.Debouncer;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class TestDebouncer extends ClipCornBaseTest {

	@Test
	public void testBurstRunsActionOnce() throws Exception {
		AtomicInteger runs  = new AtomicInteger(0);
		CountDownLatch latch = new CountDownLatch(1);

		try (Debouncer d = new Debouncer("TEST_DEBOUNCE", 150, () -> { runs.incrementAndGet(); latch.countDown(); })) {
			for (int i = 0; i < 5; i++) { d.trigger(); Thread.sleep(20); }

			assertEquals(0, runs.get());
			assertTrue(latch.await(5, TimeUnit.SECONDS));
			Thread.sleep(200);
			assertEquals(1, runs.get());
		}
	}

	@Test
	public void testFlushRunsPendingActionAndWaits() {
		AtomicInteger runs = new AtomicInteger(0);

		Debouncer d = new Debouncer("TEST_DEBOUNCE", 60_000, runs::incrementAndGet);
		d.trigger();
		d.flushAndClose(5000);

		assertEquals(1, runs.get());
	}

	@Test
	public void testCloseWithoutPendingDoesNotRun() {
		AtomicInteger runs = new AtomicInteger(0);

		Debouncer d = new Debouncer("TEST_DEBOUNCE", 60_000, runs::incrementAndGet);
		d.flushAndClose(5000);

		assertEquals(0, runs.get());
	}

	@Test
	public void testTriggerAfterCloseIsIgnored() throws Exception {
		AtomicInteger runs = new AtomicInteger(0);

		Debouncer d = new Debouncer("TEST_DEBOUNCE", 50, runs::incrementAndGet);
		d.flushAndClose(5000);
		d.trigger();
		Thread.sleep(200);

		assertEquals(0, runs.get());
	}
}
