package de.jClipCorn.util.mediaquery;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.exceptions.InnerMediaQueryException;
import de.jClipCorn.util.exceptions.MediaQueryException;
import de.jClipCorn.util.helper.ProcessHelper;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;
import de.jClipCorn.util.xml.CCXMLParser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class MediaQueryRunner {

	// https://mediaarea.net/mediainfo

	// https://mediaarea.net/mediainfo/mediainfo_2_0.xsd

	@SuppressWarnings("nls")
	public static MediaQueryResult query(String filename) throws IOException, MediaQueryException {
		String mqpath = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();

		if (! new File(mqpath).exists()) throw new MediaQueryException("MediaQuery not found");

		Tuple3<Integer, String, String> proc = ProcessHelper.procExec(mqpath, filename, "--Output=XML");

		if (proc.Item1 != 0) throw new MediaQueryException("MediaQuery returned " + proc.Item1, proc.Item2 + "\n\n\n\n" + proc.Item3);

		String mqxml = proc.Item2;

		try	{
			CCXMLParser doc = CCXMLParser.parse(proc.Item2);
			mqxml = doc.getXMLString();

			CCXMLElement root = doc.getRoot();
			if (root == null) throw new InnerMediaQueryException("no root xml element");

			CCXMLElement media = root.getFirstChildOrThrow("media");
			if (media == null) throw new InnerMediaQueryException("no media xml element");

			return MediaQueryResult.parse(media);
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
}
