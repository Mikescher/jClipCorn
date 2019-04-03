package de.jClipCorn.util;

import java.util.Stack;

import de.jClipCorn.features.log.CCLog;

public class TimeKeeper {
	static Stack<Long> time_ms = new Stack<>();

	public static void start() {
		time_ms.push(System.currentTimeMillis());
	}
	
	@SuppressWarnings("nls")
	public static void stopAndPrint() {
		if (time_ms.isEmpty()) {
			CCLog.addDebug("TimeKeeper ERROR: Stack is empty");
			return;
		}
		
		long milllis = System.currentTimeMillis() - time_ms.pop();
		
		StackTraceElement e = new Throwable().getStackTrace()[1];
		
		CCLog.addDebug(e.getFileName().substring(e.getFileName().lastIndexOf('.', e.getFileName().lastIndexOf('.') - 1) + 1) + " -> " + e.getMethodName() + "()  (Line " + e.getLineNumber() + "): " + milllis + "ms");
	}
	
	@SuppressWarnings("nls")
	public static long stop() {
		if (time_ms.isEmpty()) {
			CCLog.addDebug("TimeKeeper ERROR: Stack is empty");
			return -1;
		}
		
		return System.currentTimeMillis() - time_ms.pop();
	}
	
	public static void freeze() {
		CCLog.addDebug("FREEZE>> "); //$NON-NLS-1$
		int i = 20;
		while (i --> 0) { // the "goes to" operator :3
			try {
				Thread.sleep(175);
			} catch (InterruptedException e) {
				// Empty Block
			}
			System.out.print("."); //$NON-NLS-1$
		}
		System.out.println();
	}
}
