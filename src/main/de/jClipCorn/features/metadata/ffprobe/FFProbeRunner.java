package de.jClipCorn.features.metadata.ffprobe;

import de.jClipCorn.features.metadata.MetadataSource;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.features.metadata.exceptions.FFProbeQueryException;
import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.helper.ProcessHelper;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class FFProbeRunner implements MetadataSource {

	// https://trac.ffmpeg.org/wiki/FFprobeTips

	// https://stackoverflow.com/a/28376817/1761622

	@SuppressWarnings("nls")
	public FFProbeResult query(String filename) throws IOException, FFProbeQueryException {
		String ffppath = CCProperties.getInstance().PROP_PLAY_FFPROBE_PATH.getValue();

		File ffpfile = new File(ffppath);

		if (! ffpfile.exists()) throw new FFProbeQueryException("FFProbe not found"); //$NON-NLS-1$

		BasicFileAttributes attr = Files.readAttributes(new File(filename).toPath(), BasicFileAttributes.class);

		Tuple3<Integer, String, String> proc = ProcessHelper.procExec(ffppath, getArgs(filename));

		if (proc.Item1 != 0) throw new FFProbeQueryException("FFProbe returned " + proc.Item1, proc.Item2 + "\n\n\n\n" + proc.Item3); //$NON-NLS-1$ //$NON-NLS-2$

		String ffjson = proc.Item2;

		try	{
			JSONObject root = new JSONObject(new JSONTokener(ffjson));

			JSONArray streams = root.getJSONArray("streams"); //$NON-NLS-1$
			JSONObject vstream = CCStreams.iterate(streams).ofType(JSONObject.class).firstOrNull(p -> p.getString("codec_type").equalsIgnoreCase("video"));
			JSONObject astream = CCStreams.iterate(streams).ofType(JSONObject.class).firstOrNull(p -> p.getString("codec_type").equalsIgnoreCase("audio"));
			JSONObject formatobj = root.getJSONObject("format");

			String vidCodecName    = getStr(vstream, "codec_name"); //$NON-NLS-1$
			String vidCdecLongName = getStr(vstream, "codec_long_name"); //$NON-NLS-1$
			String vidProfile      = getStr(vstream, "profile"); //$NON-NLS-1$
			int width              = getIntDirect(vstream, "width"); //$NON-NLS-1$
			int height             = getIntDirect(vstream, "height"); //$NON-NLS-1$
			int bitdepth           = getIntFromStr(vstream, "bits_per_raw_sample"); //$NON-NLS-1$

			int framecount = getIntFromStr(vstream, "nb_read_frames"); //$NON-NLS-1$
			if (framecount == -1) framecount = getIntFromStr(vstream, "nb_frames"); //$NON-NLS-1$

			double framerate = getExtDoubleFromStr(vstream, "avg_frame_rate"); //$NON-NLS-1$
			if (framerate == -1)  framerate = getExtDoubleFromStr(vstream, "r_frame_rate"); //$NON-NLS-1$

			double duration = getDoubleFromStr(formatobj, "duration");
			long fsize      = getLngFromStr(formatobj, "size");
			int bitrate     = getIntFromStr(formatobj, "bit_rate");

			long cdate = attr.creationTime().toMillis();
			long mdate = attr.lastModifiedTime().toMillis();

			String audCodecName     = getStr(astream, "codec_name"); //$NON-NLS-1$
			String audCodecLongName = getStr(astream, "codec_long_name"); //$NON-NLS-1$
			int audSampleRate       = getIntFromStr(astream, "sample_rate"); //$NON-NLS-1$
			int audChannels         = getIntFromStr(astream, "channels"); //$NON-NLS-1$


			return new FFProbeResult(
					ffjson,
					cdate, mdate, fsize,
					vidCodecName, vidCdecLongName, vidProfile,
					width, height,
					(short)bitdepth, framecount, framerate,
					duration, bitrate,
					audCodecName, audCodecLongName, audSampleRate, (short)audChannels);

		} catch (JSONException e) {
			throw new FFProbeQueryException(e.getMessage(), ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + ffjson); //$NON-NLS-1$
		} catch (Exception e) {
			throw new FFProbeQueryException("Could not parse Output JSON", ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + ffjson); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	protected abstract String[] getArgs(String filename);

	private static String getStr(JSONObject obj, String key) {
		if (obj == null) return Str.Empty;
		if (!obj.has(key)) return null;
		if (obj.isNull(key)) return null;
		return obj.getString(key);
	}

	private static int getIntDirect(JSONObject obj, String key) {
		if (obj == null) return -1;
		if (!obj.has(key)) return -1;
		if (obj.isNull(key)) return -1;
		return obj.getInt(key);
	}

	private static int getIntFromStr(JSONObject obj, String key) {
		if (obj == null) return -1;
		if (!obj.has(key)) return -1;
		if (obj.isNull(key)) return -1;
		return Integer.parseInt(obj.getString(key));
	}

	private static long getLngFromStr(JSONObject obj, String key) {
		if (obj == null) return -1;
		if (!obj.has(key)) return -1;
		if (obj.isNull(key)) return -1;
		return Long.parseLong(obj.getString(key));
	}

	private static double getDoubleFromStr(JSONObject obj, String key) {
		if (obj == null) return -1;
		if (!obj.has(key)) return -1;
		if (obj.isNull(key)) return -1;
		return Double.parseDouble(obj.getString(key));
	}

	private static double getExtDoubleFromStr(JSONObject obj, String key) {
		if (obj == null) return -1;
		if (!obj.has(key)) return -1;
		if (obj.isNull(key)) return -1;
		return parseExtDouble(obj.getString(key));
	}

	private static double parseExtDouble(String str) {
		if (str.contains("/")) { //$NON-NLS-1$
			String[] split = str.split("/"); //$NON-NLS-1$
			double a = Integer.parseInt(split[0]);
			double b = Integer.parseInt(split[1]);

			if (b == 0) return -1;

			return a/b;
		}

		return Integer.parseInt(str);
	}

	@Override
	public PartialMediaInfo run(String filename) throws IOException, MetadataQueryException {
		return query(filename).toPartial();
	}

	@Override
	public String getFullOutput(String filename, PartialMediaInfo result) throws IOException, MetadataQueryException {
		return result.RawOutput.orElse(Str.Empty);
	}

	@Override
	public boolean isConfiguredAndRunnable() {
		String ffpath = CCProperties.getInstance().PROP_PLAY_FFPROBE_PATH.getValue();
		if (Str.isNullOrWhitespace(ffpath)) return false;

		File f = new File(ffpath);

		if (!f.exists()) return false;
		if (!f.isFile()) return false;
		if (!f.canExecute()) return false;

		return true;
	}
}
