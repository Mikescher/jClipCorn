package de.jClipCorn.properties.impl;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.property.CCExecutableProperty;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.ProcessHelper;

import java.io.IOException;

public class MP4BoxPathConf implements CCExecutableProperty.ExecPathValidator {

	public static MP4BoxPathConf INST = new MP4BoxPathConf();

	@Override
	@SuppressWarnings("nls")
	public boolean Validate(FSPath p) {
		if (!p.exists()) {
			CCLog.addWarning(Str.format("Failed to verify mp4box binary '"+p+"' - file does not exist"));
			return false;
		}
		if (!p.isFile()) {
			CCLog.addWarning(Str.format("Failed to verify mp4box binary '"+p+"' - path is not a file"));
			return false;
		}
		if (!p.canExecute()) {
			CCLog.addWarning(Str.format("Failed to verify mp4box binary '"+p+"' - file is not executable"));
			return false;
		}

		try
		{
			Tuple3<Integer, String, String> proc = ProcessHelper.procExec(p.toAbsolutePathString(), "-version");

			CCLog.addInformation(Str.format("Tried to verify mp4box binary\n[Path]: {0}\n[ExitCode]: {1}\n\n[stdout]:\n{2}\n\n[stderr]:\n{3}", p.toString(), proc.Item1, proc.Item2, proc.Item3));

			if (proc.Item1 != 0)
			{
				CCLog.addWarning("Failed to verify program mp4box (version command failed)");
				return false;
			}

			if (!proc.Item3.contains("MP4Box - GPAC version"))
			{
				CCLog.addWarning("Failed to verify program mp4box (version command returned wrong data)");
				return false;
			}

			return true;
		}
		catch (IOException e)
		{
			CCLog.addWarning(Str.format("Failed to verify mp4box binary"));
			return false;
		}
	}
}
