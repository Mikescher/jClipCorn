package de.jClipCorn.util.ffprobe;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.exceptions.FFProbeQueryException;
import de.jClipCorn.util.helper.ProcessHelper;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;

public class FFProbeRunner {

	// https://trac.ffmpeg.org/wiki/FFprobeTips

	// https://stackoverflow.com/a/28376817/1761622

	@SuppressWarnings("nls")
	public static FFProbeResult query(String filename) throws IOException, FFProbeQueryException {
		String ffppath = CCProperties.getInstance().PROP_PLAY_FFPROBE_PATH.getValue();

		File ffpfile = new File(ffppath);

		if (! ffpfile.exists()) throw new FFProbeQueryException("FFProbe not found"); //$NON-NLS-1$

		Tuple3<Integer, String, String> proc = ProcessHelper.procExec(ffppath,
				"-i", filename, //$NON-NLS-1$
				"-print_format", "json", //$NON-NLS-1$ //$NON-NLS-2$
				"-loglevel", "fatal", //$NON-NLS-1$ //$NON-NLS-2$
				"-show_streams", //$NON-NLS-1$
				"-count_frames", //$NON-NLS-1$
				"-select_streams", "v"); //$NON-NLS-1$ //$NON-NLS-2$

		if (proc.Item1 != 0) throw new FFProbeQueryException("FFProbe returned " + proc.Item1, proc.Item2 + "\n\n\n\n" + proc.Item3); //$NON-NLS-1$ //$NON-NLS-2$

		String ffjson = proc.Item2;

		try	{
			JSONObject root = new JSONObject(new JSONTokener(ffjson));

			JSONArray streams = root.getJSONArray("streams"); //$NON-NLS-1$
			JSONObject obj = streams.getJSONObject(0);

			String codecName     = getStr(obj, "codec_name"); //$NON-NLS-1$
			String codecLongName = getStr(obj, "codec_long_name"); //$NON-NLS-1$
			String profile       = getStr(obj, "profile"); //$NON-NLS-1$
			int width            = getIntDirect(obj, "width"); //$NON-NLS-1$
			int height           = getIntDirect(obj, "height"); //$NON-NLS-1$
			int bitdepth         = getIntFromStr(obj, "bits_per_raw_sample"); //$NON-NLS-1$
			int framecount       = getIntFromStr(obj, "nb_read_frames"); //$NON-NLS-1$
			double framerate     = getDoubleFromStr(obj, "nb_read_frame"); //$NON-NLS-1$

			return new FFProbeResult(ffjson, codecName, codecLongName, profile, width, height, (short)bitdepth, framecount, framerate);

		} catch (JSONException e) {
			throw new FFProbeQueryException(e.getMessage(), ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + ffjson); //$NON-NLS-1$
		} catch (Exception e) {
			throw new FFProbeQueryException("Could not parse Output JSON", ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + ffjson); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	private static String getStr(JSONObject obj, String key) {
		if (!obj.has(key)) return null;
		if (obj.isNull(key)) return null;
		return obj.getString(key);
	}

	private static int getIntDirect(JSONObject obj, String key) {
		if (!obj.has(key)) return -1;
		if (obj.isNull(key)) return -1;
		return obj.getInt(key);
	}

	private static int getIntFromStr(JSONObject obj, String key) {
		if (!obj.has(key)) return -1;
		if (obj.isNull(key)) return -1;
		return Integer.parseInt(obj.getString(key));
	}

	private static double getDoubleFromStr(JSONObject obj, String key) {
		if (!obj.has(key)) return -1;
		if (obj.isNull(key)) return -1;
		return parseExtDouble(obj.getString(key));
	}

	private static double parseExtDouble(String str) {
		if (str.contains("/")) { //$NON-NLS-1$
			String[] split = str.split("/"); //$NON-NLS-1$
			double a = Integer.parseInt(split[0]);
			double b = Integer.parseInt(split[1]);

			return a/b;
		}

		return Integer.parseInt(str);
	}
}
