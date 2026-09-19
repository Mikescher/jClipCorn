package de.jClipCorn.util.helper;

import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.properties.ICCPropertySource;
import de.jClipCorn.util.datatypes.Opt;

import java.io.IOException;

public class MediaInfoHelper {

	/**
	 * Re-read cdate/mdate of the (first) part from disk and store them in the elements MediaInfo.
	 *
	 * Call this after a file was moved/copied - the timestamps are properties of the directory entry, not of the
	 * content, so they change even though the file itself is untouched (and the validator would report them as
	 * ERROR_MEDIAINFO_CDATE_CHANGED / ERROR_MEDIAINFO_MDATE_CHANGED afterwards).
	 *
	 * Does nothing when no MediaInfo was ever collected for this element - a half-filled MediaInfo is worse than none.
	 */
	public static boolean refreshMediaInfoFileDates(ICCPropertySource ccps, ICCPlayableElement elem) {
		var mi = elem.mediaInfo().get();
		if (mi.isFullyEmpty()) return false;

		var parts = elem.getParts();
		if (parts.isEmpty()) return false;

		var path = parts.get(0).toFSPath(ccps);
		if (path.isEmpty()) return false;

		try {
			var attr = path.readFileAttr();

			elem.mediaInfo().CDate.set(Opt.of(attr.creationTime().toMillis()));
			elem.mediaInfo().MDate.set(Opt.of(attr.lastModifiedTime().toMillis()));

			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
