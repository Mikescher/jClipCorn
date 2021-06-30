package de.jClipCorn.features.metadata.mediaquery;

import de.jClipCorn.features.metadata.MetadataSource;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.features.metadata.exceptions.InnerMediaQueryException;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.helper.ProcessHelper;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;
import de.jClipCorn.util.xml.CCXMLParser;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class MediaQueryRunner implements MetadataSource {

	// https://mediaarea.net/mediainfo

	// https://mediaarea.net/mediainfo/mediainfo_2_0.xsd

	@SuppressWarnings("nls")
	public static MediaQueryResult query(FSPath filename, boolean doNotValidateLangs) throws IOException, MediaQueryException {
		var mqpath = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (! mqpath.exists()) throw new MediaQueryException("MediaQuery not found");

		Tuple3<Integer, String, String> proc = ProcessHelper.procExec(mqpath.toAbsolutePathString(), filename.toString(), "--Output=XML");

		if (proc.Item1 != 0) throw new MediaQueryException("MediaQuery returned " + proc.Item1, proc.Item2 + "\n\n\n\n" + proc.Item3);

		BasicFileAttributes attr = Files.readAttributes(filename.toPath(), BasicFileAttributes.class);

		String mqxml = proc.Item2;

		try	{
			CCXMLParser doc = CCXMLParser.parse(proc.Item2);
			mqxml = doc.getXMLString();

			CCXMLElement root = doc.getRoot();
			if (root == null) throw new InnerMediaQueryException("no root xml element");

			CCXMLElement media = root.getFirstChildOrThrow("media");
			if (media == null) throw new InnerMediaQueryException("no media xml element");

			var hash = ChecksumHelper.fastVideoHash(filename);

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
	public static String queryRaw(FSPath filename) throws IOException, MediaQueryException {
		var mqpath = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (!mqpath.exists()) throw new MediaQueryException("MediaQuery not found");

		Tuple3<Integer, String, String> proc = ProcessHelper.procExec(mqpath.toAbsolutePathString(), filename.toString());

		if (proc.Item1 != 0) throw new MediaQueryException("MediaQuery returned " + proc.Item1, proc.Item2 + "\n\n\n\n" + proc.Item3);

		return proc.Item2;
	}

	@Override
	public PartialMediaInfo run(FSPath filename) throws IOException, MetadataQueryException {
		return query(filename, true).toPartial();
	}

	@Override
	public String getFullOutput(FSPath filename, PartialMediaInfo result) throws IOException, MetadataQueryException {
		return queryRaw(filename);
	}

	@Override
	public boolean isConfiguredAndRunnable() {
		var mqpath = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
		if (FSPath.isNullOrEmpty(mqpath)) return false;

		if (!mqpath.exists())     return false;
		if (!mqpath.isFile())     return false;
		if (!mqpath.canExecute()) return false;

		return true;
	}
}
