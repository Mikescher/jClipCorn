package de.jClipCorn.features.metadata.impl;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.metadata.MetadataError;
import de.jClipCorn.features.metadata.MetadataSourceType;
import de.jClipCorn.features.metadata.VideoMetadata;
import de.jClipCorn.features.metadata.exceptions.MP4BoxQueryException;
import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.ErrOpt;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.helper.ProcessHelper;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONException;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MP4BoxRunner extends MetadataRunner {

	private final CCMovieList movielist;

	public MP4BoxRunner(CCMovieList ml) {
		this.movielist = ml;
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	@SuppressWarnings("nls")
	@Override
	public VideoMetadata run(FSPath filename) throws IOException, MetadataQueryException
	{
		var boxppath = ccprops().PROP_PLAY_MP4BOX_PATH.getValue().getPathAndArgs();
		if (! boxppath.Item1.exists()) throw new MP4BoxQueryException("MP4Box not found"); //$NON-NLS-1$

		BasicFileAttributes attr = filename.readFileAttr();

		var args = new ArrayList<String>();
		args.add("-info"); //$NON-NLS-1$
		args.add(filename.toAbsolutePathString());
		args.addAll(Arrays.asList(boxppath.Item2));

		Tuple3<Integer, String, String> proc = ProcessHelper.procExec(boxppath.Item1.toAbsolutePathString(), args.toArray(new String[0]));

		if (proc.Item1 != 0) throw new MP4BoxQueryException("MP4Box returned " + proc.Item1, proc.Item2 + "\n\n\n\n" + proc.Item3); //$NON-NLS-1$ //$NON-NLS-2$

		String ffoutput = proc.Item3;

		try	{
			var fvhash = ChecksumHelper.fastVideoHash(filename);

			return parse(ffoutput, fvhash, attr, filename);

		} catch (JSONException e) {
			throw new MP4BoxQueryException(e.getMessage(), ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + ffoutput); //$NON-NLS-1$
		} catch (Exception e) {
			throw new MP4BoxQueryException("Could not parse Output JSON", ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + ffoutput); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	@SuppressWarnings("nls")
	public VideoMetadata parse(String ffoutput, String fvhash, BasicFileAttributes attr, FSPath filename) {
		var cdate    = ErrOpt.<Long,   MetadataError>of(attr.creationTime().toMillis());
		var mdate    = ErrOpt.<Long,   MetadataError>of(attr.lastModifiedTime().toMillis());
		var checksum = ErrOpt.<String, MetadataError>of(fvhash);

		var duration = ErrOpt.<Double, MetadataError>empty();

		for (String line : SimpleFileUtils.splitLines(ffoutput))
		{
			Matcher m1 = Pattern.compile("^\\s+Computed Duration ([0-9]+):([0-9]+):([0-9]+).([0-9]+) - Indicated Duration .*$").matcher(line);
			if (m1.matches()) {

				duration = ErrOpt.of(() ->
				{
					double h = Integer.parseInt(m1.group(1));
					double m = Integer.parseInt(m1.group(2));
					double s = Integer.parseInt(m1.group(3));

					return h*3600 + m*60 + s;
				}, MetadataError::Wrap);
			}
		}

		return new VideoMetadata
		(
			getSourceType(),
			filename,
			ffoutput,
			cdate,
			mdate,
			checksum,
			ErrOpt.empty(),
			ErrOpt.empty(),
			ErrOpt.empty(),
			duration,
			ErrOpt.empty(),
			ErrOpt.empty(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>()
		);
	}

	@Override
	public MetadataSourceType getSourceType() {
		return MetadataSourceType.MP4BOX;
	}

	@Override
	public boolean isConfiguredAndRunnable() {
		return ccprops().PROP_PLAY_MP4BOX_PATH.getValue().existsAndCanExecute();
	}
}
