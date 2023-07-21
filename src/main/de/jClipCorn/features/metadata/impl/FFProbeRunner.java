package de.jClipCorn.features.metadata.impl;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.metadata.*;
import de.jClipCorn.features.metadata.exceptions.FFProbeQueryException;
import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.ErrOpt;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.exceptions.FVHException;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.helper.ProcessHelper;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class FFProbeRunner extends MetadataRunner {

	// https://trac.ffmpeg.org/wiki/FFprobeTips

	// https://stackoverflow.com/a/28376817/1761622

	private final CCMovieList movielist;

	public FFProbeRunner(CCMovieList ml) {
		this.movielist = ml;
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	@Override
	@SuppressWarnings("nls")
	public VideoMetadata run(FSPath filename) throws IOException, MetadataQueryException
	{
		var ffppath = ccprops().PROP_PLAY_FFPROBE_PATH.getValue().getPathAndArgs();
		if (! ffppath.Item1.exists()) throw new FFProbeQueryException("FFProbe not found"); //$NON-NLS-1$

		BasicFileAttributes attr = filename.readFileAttr();

		var args = new ArrayList<String>();
		args.addAll(Arrays.asList(getArgs(filename)));
		args.addAll(Arrays.asList(ffppath.Item2));

		Tuple3<Integer, String, String> proc = ProcessHelper.procExec(ffppath.Item1.toAbsolutePathString(), args.toArray(new String[0]));

		if (proc.Item1 != 0) throw new FFProbeQueryException("FFProbe returned " + proc.Item1, proc.Item2 + "\n\n\n\n" + proc.Item3); //$NON-NLS-1$ //$NON-NLS-2$

		String ffjson = proc.Item2;

		try
		{
			var fvhash = ChecksumHelper.fastVideoHash(filename);

			return parse(ffjson, fvhash, attr, filename);

		} catch (JSONException e) {
			throw new FFProbeQueryException(e.getMessage(), ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + ffjson); //$NON-NLS-1$
		} catch (Exception e) {
			throw new FFProbeQueryException("Could not parse Output JSON", ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + ffjson); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	@SuppressWarnings("nls")
	public VideoMetadata parse(String ffjson, String fvhash, BasicFileAttributes attr, FSPath filename) throws IOException, FVHException {
		JSONObject root = new JSONObject(new JSONTokener(ffjson));

		JSONArray streams = root.getJSONArray("streams");

		var vstreams = new ArrayList<VideoTrackMetadata>();
		var astreams = new ArrayList<AudioTrackMetadata>();
		var sstreams = new ArrayList<SubtitleTrackMetadata>();

		var streamIndex = 1;
		for (var strm : streams)
		{
			if (! (strm instanceof JSONObject)) continue;

			var jobj = (JSONObject)strm;

			if (jobj.getString("codec_type").equalsIgnoreCase("video"))
			{
				var pobj = this.parseVideoStream(streamIndex, vstreams.size()+1, jobj);
				vstreams.add(pobj);
			}
			else if (jobj.getString("codec_type").equalsIgnoreCase("audio"))
			{
				var pobj = this.parseAudioStream(streamIndex, astreams.size()+1, jobj);
				astreams.add(pobj);
			}
			else if (jobj.getString("codec_type").equalsIgnoreCase("subtitle"))
			{
				var pobj = this.parseSubtitleStream(streamIndex, sstreams.size()+1, jobj);
				sstreams.add(pobj);
			}

			streamIndex++;
		}

		JSONObject formatobj = root.getJSONObject("format");

		var duration = getDoubleFromStr(formatobj, "duration");
		var fsize    = getLngFromStr(formatobj, "size");
		var bitrate  = getIntFromStr(formatobj, "bit_rate");

		long cdate = attr.creationTime().toMillis();
		long mdate = attr.lastModifiedTime().toMillis();

		var maintrack = CCStreams.iterate(vstreams).first(p -> p.Default.isPresent() && p.Default.get());
		if (maintrack.isEmpty()) maintrack = CCStreams.iterate(vstreams).first();

		List<ErrOpt<CCDBLanguage, MetadataError>> alng = extractAudioLangs(astreams);
		List<ErrOpt<CCDBLanguage, MetadataError>> slng = extractSubLangs(sstreams);

		return new VideoMetadata
		(
			getSourceType(),
			filename,
			ffjson,
			ErrOpt.of(cdate),
			ErrOpt.of(mdate),
			ErrOpt.of(fvhash),
			ErrOpt.empty(),
			ErrOpt.empty(),
			fsize,
			duration,
			bitrate,
			maintrack.<MetadataError>toErrOpt().mapFlat(p -> p.FrameRate),
			vstreams,
			astreams,
			sstreams,
			alng,
			slng
		);
	}

	@SuppressWarnings("nls")
	private VideoTrackMetadata parseVideoStream(int idxTotal, int idxTrackType, JSONObject obj)
	{
		var vidCodecName    = getStr(obj, "codec_name");
		var vidCdecLongName = getStr(obj, "codec_long_name");
		var vidProfile      = getStr(obj, "profile");
		var width           = getIntDirect(obj, "width");
		var height          = getIntDirect(obj, "height");
		var bitdepth        = getIntFromStr(obj, "bits_per_raw_sample");

		var framecount = getIntFromStr(obj, "nb_read_frames");
		if (framecount.isEmpty()) framecount = getIntFromStr(obj, "nb_frames");

		var framerate = getExtDoubleFromStr(obj, "avg_frame_rate");
		if (framerate.isEmpty()) framerate = getExtDoubleFromStr(obj, "r_frame_rate");

		var vdefault = ErrOpt.<Boolean, MetadataError>empty();
		if (obj.has("disposition") && obj.get("disposition") instanceof JSONObject)
			vdefault = getIntDirect(obj.getJSONObject("disposition"), "default").map(p -> p == 1);

		return new VideoTrackMetadata
		(
			idxTotal,
			idxTrackType,
			vidCodecName,
			vidProfile,
			vidCdecLongName,
			ErrOpt.empty(),
			ErrOpt.empty(),
			width,
			height,
			framerate,
			framecount,
			bitdepth.map(p -> p.shortValue()),
			ErrOpt.empty(),
			vdefault
		);
	}

	@SuppressWarnings("nls")
	private AudioTrackMetadata parseAudioStream(int idxTotal, int idxTrackType, JSONObject obj)
	{
		var codecName     = getStr(obj, "codec_name");
		var codecLongName = getStr(obj, "codec_long_name");
		var sampleRate    = getIntFromStr(obj, "sample_rate");
		var channels      = getIntDirect(obj, "channels").map(Integer::shortValue);
		var bitrate       = getIntFromStr(obj, "bit_rate");

		var adefault = ErrOpt.<Boolean, MetadataError>empty();
		if (obj.has("disposition") && obj.get("disposition") instanceof JSONObject)
			adefault = getIntDirect(obj.getJSONObject("disposition"), "default").map(p -> p == 1);

		var language = ErrOpt.<String, MetadataError>empty();
		var title = ErrOpt.<String, MetadataError>empty();
		if (obj.has("tags") && obj.get("tags") instanceof JSONObject) {
			language = getStr(obj.getJSONObject("tags"), "language");
			title = getStr(obj.getJSONObject("tags"), "title");
		}

		return new AudioTrackMetadata
		(
			idxTotal,
			idxTrackType,
			codecName,
			language,
			title,
			codecLongName,
			channels,
			sampleRate,
			bitrate,
			ErrOpt.empty(),
			adefault
		);
	}

	@SuppressWarnings("nls")
	private SubtitleTrackMetadata parseSubtitleStream(int idxTotal, int idxTrackType, JSONObject obj)
	{
		var codecName     = getStr(obj, "codec_name");
		var codecLongName = getStr(obj, "codec_long_name");

		var sdefault = ErrOpt.<Boolean, MetadataError>empty();
		if (obj.has("disposition") && obj.get("disposition") instanceof JSONObject)
			sdefault = getIntDirect(obj.getJSONObject("disposition"), "default").map(p -> p == 1);

		var language = ErrOpt.<String, MetadataError>empty();
		var title = ErrOpt.<String, MetadataError>empty();
		if (obj.has("tags") && obj.get("tags") instanceof JSONObject) {
			language = getStr(obj.getJSONObject("tags"), "language");
			title = getStr(obj.getJSONObject("tags"), "title");
		}

		return new SubtitleTrackMetadata
		(
			idxTotal,
			idxTrackType,
			codecName,
			title,
			codecLongName,
			language,
			sdefault
		);
	}

	private static ErrOpt<String, MetadataError> getStr(JSONObject obj, String key) {
		if (obj == null) return ErrOpt.empty();
		if (!obj.has(key)) return ErrOpt.empty();
		if (obj.isNull(key)) return ErrOpt.empty();
		return ErrOpt.of(obj.getString(key));
	}

	private static ErrOpt<Integer, MetadataError> getIntDirect(JSONObject obj, String key) {
		if (obj == null) return ErrOpt.empty();
		if (!obj.has(key)) return ErrOpt.empty();
		if (obj.isNull(key)) return ErrOpt.empty();
		return ErrOpt.of(() -> obj.getInt(key), MetadataError::Wrap);
	}

	private static ErrOpt<Integer, MetadataError> getIntFromStr(JSONObject obj, String key) {
		if (obj == null) return ErrOpt.empty();
		if (!obj.has(key)) return ErrOpt.empty();
		if (obj.isNull(key)) return ErrOpt.empty();
		return ErrOpt.of(() -> Integer.parseInt(obj.getString(key)), MetadataError::Wrap);
	}

	private static ErrOpt<Long, MetadataError> getLngFromStr(JSONObject obj, String key) {
		if (obj == null) return ErrOpt.empty();
		if (!obj.has(key)) return ErrOpt.empty();
		if (obj.isNull(key)) return ErrOpt.empty();
		return ErrOpt.of(() -> Long.parseLong(obj.getString(key)), MetadataError::Wrap);
	}

	private static ErrOpt<Double, MetadataError> getDoubleFromStr(JSONObject obj, String key) {
		if (obj == null) return ErrOpt.empty();
		if (!obj.has(key)) return ErrOpt.empty();
		if (obj.isNull(key)) return ErrOpt.empty();
		return ErrOpt.of(() -> Double.parseDouble(obj.getString(key)), MetadataError::Wrap);
	}

	private static ErrOpt<Double, MetadataError> getExtDoubleFromStr(JSONObject obj, String key) {
		if (obj == null) return ErrOpt.empty();
		if (!obj.has(key)) return ErrOpt.empty();
		if (obj.isNull(key)) return ErrOpt.empty();
		return ErrOpt.of(() -> parseExtDouble(obj.getString(key)), MetadataError::Wrap);
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

	protected abstract String[] getArgs(FSPath filename);

	@Override
	public boolean isConfiguredAndRunnable() {
		return ccprops().PROP_PLAY_FFPROBE_PATH.getValue().existsAndCanExecute();
	}
}
