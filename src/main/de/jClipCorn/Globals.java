package de.jClipCorn;

import java.util.HashMap;
import java.util.Map;

import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.frames.mainFrame.clipStatusbar.ClipStatusBar;
import de.jClipCorn.util.MapStopWatch;

@SuppressWarnings("nls")
public final class Globals {
	private Globals () { throw new InstantiationError(); }
	
	public static int TIMING_LOAD_TOTAL            = 0x00;
	public static int TIMING_LOAD_PROPERTIES       = 0x01;
	public static int TIMING_LOAD_TESTREADONLY     = 0x02;
	public static int TIMING_LOAD_PRELOADRESOURCES = 0x03;
	public static int TIMING_SCAN_DRIVES           = 0x04;
	public static int TIMING_INIT_BACKUPMANAGER    = 0x05;
	public static int TIMING_DATABASE_CONNECT      = 0x06;
	public static int TIMING_MOVIELIST_FILL        = 0x07;
	
	public static Map<Integer, String> TIMING_IDS = new HashMap<>();
	
	public static MapStopWatch TIMINGS = new MapStopWatch();

	static {
		TIMING_IDS.put(TIMING_LOAD_TOTAL,            "LOAD_TOTAL");
		TIMING_IDS.put(TIMING_LOAD_PROPERTIES,       "LOAD_PROPERTIES");
		TIMING_IDS.put(TIMING_LOAD_TESTREADONLY,     "LOAD_TESTREADONLY");
		TIMING_IDS.put(TIMING_LOAD_PRELOADRESOURCES, "LOAD_PRELOADRESOURCES");
		TIMING_IDS.put(TIMING_SCAN_DRIVES,           "SCAN_DRIVES");
		TIMING_IDS.put(TIMING_INIT_BACKUPMANAGER,    "INIT_BACKUPMANAGER");
		TIMING_IDS.put(TIMING_DATABASE_CONNECT,      "DATABASE_CONNECT");
		TIMING_IDS.put(TIMING_MOVIELIST_FILL,        "MOVIELIST_FILL");
		
		TIMINGS.setOnTimerStop((id, time) -> {
			//CCLog.addDebug("Timer " + TIMING_IDS.get(id) + " finished := " + time + "ms");
			
			MainFrame inst = MainFrame.getInstance();
			if (inst != null) {
				ClipStatusBar sb = inst.getStatusBar();
				if (sb != null) sb.updateLables_Starttime();
			}
		});
	}
}
