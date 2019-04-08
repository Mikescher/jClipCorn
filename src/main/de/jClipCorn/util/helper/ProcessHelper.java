package de.jClipCorn.util.helper;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.stream.CCStreams;

import java.io.*;

public class ProcessHelper {

	private static class StreamGobbler extends Thread
	{
		private StringBuilder result;

		private InputStream is;
		private String type;

		public StreamGobbler(InputStream is, String type)
		{
			this.is = is;
			this.type = type;
			this.result = new StringBuilder();
		}

		public void run()
		{
			try
			{
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);

				char[] bfr = new char[128];
				for(;;)
				{
					int bfr_len = br.read(bfr);
					if (bfr_len < 0) break;
					if (bfr_len == 0) continue;

					result.append(bfr, 0, bfr_len);
				}
			} catch (Exception ioe)
			{
				CCLog.addUndefinied(this, ioe);
			}
		}

		public String get() {
			if (isAlive()) return Str.Empty;
			return result.toString();
		}

		public void waitFor() {
			while (this.isAlive()) ThreadUtils.safeSleep(1);
		}
	}

	public static Tuple3<Integer, String, String> procExec(String cmd, String... args) throws IOException
	{
		Runtime rt = Runtime.getRuntime();
		String[] commands = CCStreams.iterate(args).prepend(cmd).toArray(new String[0]);
		Process proc = rt.exec(commands);

		StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR"); //$NON-NLS-1$
		StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT"); //$NON-NLS-1$

		errorGobbler.start();
		outputGobbler.start();

		try {
			int exitVal = proc.waitFor();

			errorGobbler.waitFor();
			outputGobbler.waitFor();

			return Tuple3.Create(exitVal, outputGobbler.get(), errorGobbler.get());
		}
		catch (InterruptedException e)
		{
			return Tuple3.Create(proc.exitValue(), outputGobbler.get(), errorGobbler.get());
		}
	}
}
