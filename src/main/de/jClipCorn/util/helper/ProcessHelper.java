package de.jClipCorn.util.helper;

import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.stream.CCStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessHelper {

	public static Tuple3<Integer, String, String> winExec(String cmd, String... args) throws IOException
	{
		Runtime rt = Runtime.getRuntime();
		String[] commands = CCStreams.iterate(args).prepend(cmd).toArray(new String[0]);
		Process proc = rt.exec(commands);

		StringBuilder stdout = new StringBuilder();
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		StringBuilder stderr = new StringBuilder();
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		for(;;)
		{
			boolean read = false;

			char[] s1 = new char[128];
			int s1_len = stdInput.read(s1);
			if (s1_len>0)
			{
				stdout.append(s1, 0, s1_len);
				read = true;
			}

			char[] s2 = new char[128];
			int s2_len = stdError.read(s2);
			if (s2_len>0)
			{
				stderr.append(s2, 0, s2_len);
				read = true;
			}

			if (!proc.isAlive() && !read) break;
		}

		return Tuple3.Create(proc.exitValue(), stdout.toString(), stderr.toString());
	}
}
