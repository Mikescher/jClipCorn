package de.jClipCorn.features.metadata.mediaquery;

import de.jClipCorn.features.metadata.MetadataSource;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.features.metadata.exceptions.InnerMediaQueryException;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.helper.ProcessHelper;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;
import de.jClipCorn.util.xml.CCXMLParser;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class MediaQueryRunner implements MetadataSource {

	// https://mediaarea.net/mediainfo

	// https://mediaarea.net/mediainfo/mediainfo_2_0.xsd

	@SuppressWarnings("nls")
	public static MediaQueryResult query(String filename, boolean doNotValidateLangs) throws IOException, MediaQueryException {
		String mqpath = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();

		File mqfile = new File(mqpath);

		if (! mqfile.exists()) throw new MediaQueryException("MediaQuery not found");

		Tuple3<Integer, String, String> proc = ProcessHelper.procExec(mqpath, filename, "--Output=XML");

		if (proc.Item1 != 0) throw new MediaQueryException("MediaQuery returned " + proc.Item1, proc.Item2 + "\n\n\n\n" + proc.Item3);

		BasicFileAttributes attr = Files.readAttributes(new File(filename).toPath(), BasicFileAttributes.class);

		String mqxml = proc.Item2;

		try	{
			CCXMLParser doc = CCXMLParser.parse(proc.Item2);
			mqxml = doc.getXMLString();

			CCXMLElement root = doc.getRoot();
			if (root == null) throw new InnerMediaQueryException("no root xml element");

			CCXMLElement media = root.getFirstChildOrThrow("media");
			if (media == null) throw new InnerMediaQueryException("no media xml element");

			var hash = ChecksumHelper.fastVideoHash(new File(filename));

			return MediaQueryResult.parse(mqxml, attr.creationTime().toMillis(), attr.lastModifiedTime().toMillis(), hash, media, doNotValidateLangs);
		} catch (InnerMediaQueryException e) {
			throw new MediaQueryException(e.getMessage(), mqxml);
		} catch (CCXMLException e) {
			throw new MediaQueryException(e.Message, ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + e.XMLContent);
		} catch (Exception e) {
			throw new MediaQueryException("Could not parse Output XML", ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + mqxml);
		}
	}

	@SuppressWarnings("nls")
	public static String queryRaw(String filename) throws IOException, MediaQueryException {
		String mqpath = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();

		if (! new File(mqpath).exists()) throw new MediaQueryException("MediaQuery not found");

		Tuple3<Integer, String, String> proc = ProcessHelper.procExec(mqpath, filename);

		if (proc.Item1 != 0) throw new MediaQueryException("MediaQuery returned " + proc.Item1, proc.Item2 + "\n\n\n\n" + proc.Item3);

		return proc.Item2;
	}

	@Override
	public PartialMediaInfo run(String filename) throws IOException, MetadataQueryException {
		return query(filename, true).toPartial();
	}

	@Override
	public String getFullOutput(String filename, PartialMediaInfo result) throws IOException, MetadataQueryException {
		return queryRaw(filename);
	}

	@Override
	public boolean isConfiguredAndRunnable() {
		String mqpath = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (Str.isNullOrWhitespace(mqpath)) return false;

		File f = new File(mqpath);

		if (!f.exists()) return false;
		if (!f.isFile()) return false;
		if (!f.canExecute()) return false;

		return true;
	}
}
