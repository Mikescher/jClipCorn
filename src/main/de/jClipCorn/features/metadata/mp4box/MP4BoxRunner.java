package de.jClipCorn.features.metadata.mp4box;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.features.metadata.MetadataSource;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.features.metadata.exceptions.MP4BoxQueryException;
import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.helper.ProcessHelper;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MP4BoxRunner implements MetadataSource {

	// https://trac.ffmpeg.org/wiki/FFprobeTips

	// https://stackoverflow.com/a/28376817/1761622

	private final CCMovieList movielist;

	public MP4BoxRunner(CCMovieList ml) {
		this.movielist = ml;
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	@SuppressWarnings("nls")
	@Override
	public PartialMediaInfo run(FSPath filename) throws IOException, MetadataQueryException {
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
			var hash = ChecksumHelper.fastVideoHash(filename);

			return parse(ffoutput, hash, attr);

		} catch (JSONException e) {
			throw new MP4BoxQueryException(e.getMessage(), ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + ffoutput); //$NON-NLS-1$
		} catch (Exception e) {
			throw new MP4BoxQueryException("Could not parse Output JSON", ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + ffoutput); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	@NotNull
	public PartialMediaInfo parse(String ffoutput, String hash, BasicFileAttributes attr) {
		var rawout = Opt.of(ffoutput);
		var cdate = Opt.of(attr.creationTime().toMillis());
		var mdate = Opt.of(attr.lastModifiedTime().toMillis());
		var checksum = Opt.of(hash);

		var duration = Opt.<Double>empty();
		var width = Opt.<Integer>empty();
		var height = Opt.<Integer>empty();

		for (String line : SimpleFileUtils.splitLines(ffoutput))
		{
			Matcher m1 = Pattern.compile("^\\s+Computed Duration ([0-9]+):([0-9]+):([0-9]+).([0-9]+) - Indicated Duration .*$").matcher(line);
			if (m1.matches()) {
				double h = Integer.parseInt(m1.group(1));
				double m = Integer.parseInt(m1.group(2));
				double s = Integer.parseInt(m1.group(3));

				duration = Opt.of(h*3600 + m*60 + s);
				continue;
			}

			Matcher m2 = Pattern.compile("^.*Video - Visual Size ([0-9]+) x ([0-9]+)$").matcher(line);
			if (m2.matches()) {
				width  = Opt.of(Integer.parseInt(m2.group(1)));
				height = Opt.of(Integer.parseInt(m2.group(2)));
				continue;
			}
		}

		return PartialMediaInfo.create
				(
						rawout,
						cdate, mdate, Opt.of(new CCFileSize(attr.size())), checksum,
						duration, Opt.empty(),
						Opt.empty(), width, height, Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(),
						Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty()
				);
	}

	@Override
	public String getFullOutput(FSPath filename, PartialMediaInfo result) {
		return result.RawOutput.orElse(Str.Empty);
	}

	@Override
	public boolean isConfiguredAndRunnable() {
		return ccprops().PROP_PLAY_MP4BOX_PATH.getValue().existsAndCanExecute();
	}
}
