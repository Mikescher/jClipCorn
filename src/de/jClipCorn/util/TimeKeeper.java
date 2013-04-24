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
}
