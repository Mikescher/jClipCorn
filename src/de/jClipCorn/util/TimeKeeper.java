package de.jClipCorn.util;

public class TimeKeeper {
	static long time_ms = 0;

	public static void start() {
		time_ms = System.currentTimeMillis();
	}
	
	@SuppressWarnings("nls")
	public static void stop() {
		long milllis = System.currentTimeMillis() - time_ms;
		
		StackTraceElement e = new Throwable().getStackTrace()[1];
		
		System.out.println(e.getFileName().substring(e.getFileName().lastIndexOf('.', e.getFileName().lastIndexOf('.') - 1) + 1) + " -> " + e.getMethodName() + "()  (Line " + e.getLineNumber() + "): " + milllis + "ms");
	}
	
	public static void freeze() {
		System.out.print("FREEZE>> "); //$NON-NLS-1$
		int i = 0;
		while (i < 10) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// Empty Block
			}
			System.out.print("."); //$NON-NLS-1$
		}
	}
}
