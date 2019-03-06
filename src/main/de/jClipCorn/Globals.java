package de.jClipCorn;

import java.util.HashMap;
import java.util.Map;

import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.frames.mainFrame.clipStatusbar.ClipStatusBar;
import de.jClipCorn.util.MapStopWatch;

@SuppressWarnings("nls")
public final class Globals {
	private Globals () { throw new InstantiationError(); }

	public static int TIMING_INIT_TOTAL              = 0x000;
	public static int TIMING_INIT_LOAD_PROPERTIES    = 0x001;
	public static int TIMING_INIT_TESTREADONLY       = 0x002;
	public static int TIMING_INIT_PRELOADRESOURCES   = 0x003;

	public static int TIMING_LOAD_TOTAL              = 0x100;
	public static int TIMING_LOAD_INIT_BACKUPMANAGER = 0x102;
	public static int TIMING_LOAD_DATABASE_CONNECT   = 0x103;
	public static int TIMING_LOAD_MOVIELIST_FILL     = 0x104;

	public static int TIMING_STARTUP_TOTAL           = 0x200;

	public static int TIMING_BACKGROUND_SCAN_DRIVES  = 0x301;
	
	public final static Map<Integer, String> TIMING_IDS = new HashMap<>();
	
	public final static MapStopWatch TIMINGS = new MapStopWatch();

	static {
		TIMING_IDS.put(TIMING_INIT_LOAD_PROPERTIES,    "INIT_LOAD_PROPERTIES");
		TIMING_IDS.put(TIMING_INIT_TESTREADONLY,       "INIT_TESTREADONLY");
		TIMING_IDS.put(TIMING_INIT_PRELOADRESOURCES,   "INIT_PRELOADRESOURCES");
		TIMING_IDS.put(TIMING_INIT_TOTAL,              "INIT_TOTAL");

		TIMING_IDS.put(TIMING_LOAD_INIT_BACKUPMANAGER, "LOAD_INIT_BACKUPMANAGER");
		TIMING_IDS.put(TIMING_LOAD_DATABASE_CONNECT,   "LOAD_DATABASE_CONNECT");
		TIMING_IDS.put(TIMING_LOAD_MOVIELIST_FILL,     "LOAD_MOVIELIST_FILL");
		TIMING_IDS.put(TIMING_LOAD_TOTAL,              "LOAD_TOTAL");

		TIMING_IDS.put(TIMING_STARTUP_TOTAL,           "STARTUP_TOTAL");

		TIMING_IDS.put(TIMING_BACKGROUND_SCAN_DRIVES,  "BACKGROUND_SCAN_DRIVES");
		
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
