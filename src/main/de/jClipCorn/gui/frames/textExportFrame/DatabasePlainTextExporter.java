package de.jClipCorn.gui.frames.textExportFrame;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.helper.SimpleFileUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DatabasePlainTextExporter extends DatabaseTextExporter {

	@Override
	protected String getFormatName() {
		return "Plaintext"; //$NON-NLS-1$
	}

	@Override
	@SuppressWarnings("nls")
	protected String create() {
		List<CCDatabaseElement> elements = getOrderedList();
		
		StringBuilder builder = new StringBuilder();
		
		List<CCMovie> last = null;
		for (CCDatabaseElement ccd_el : elements)
		{
			if (ccd_el.isMovie()) {
				CCMovie mov = (CCMovie) ccd_el;

				if (last != null && (mov.getZyklus().isEmpty() || !last.get(0).getZyklus().getTitle().equals(mov.getZyklus().getTitle()))) {
					// ###### flush ######

					last = flushZyklusList(builder, last);
				}

				if (mov.getZyklus().isSet() && last != null && last.get(0).getZyklus().getTitle().equals(mov.getZyklus().getTitle())) {
					// ###### add #######

					last.add(mov);
				} else if (mov.getZyklus().isSet()) {
					// ###### create ######

					last = new ArrayList<>();
					last.add(mov);
				} else if (last == null && mov.getZyklus().isEmpty()) {
					// ###### out ######

					if (addViewed) builder.append(mov.isViewed() ? "[X] " : "[ ] ");
					builder.append(mov.getTitle());

					if (addLanguage) builder.append(" [").append(mov.getLanguage().toOutputString()).append("]");
					if (addFormat) builder.append(" (").append(mov.getFormat().asString().toUpperCase()).append(")");
					if (addQuality) builder.append(" (").append(mov.getMediaInfoCategory().getLongText()).append(")");
					if (addYear) builder.append(" (").append(mov.getYear()).append(")");
					if (addSize) builder.append(" (").append(mov.getFilesize().getFormatted()).append(")");

					builder.append(System.lineSeparator());
				} else {
					// ###### error ######

					CCLog.addError("WTF");
					return null;
				}
			} else if (ccd_el.isSeries()) {
				CCSeries ser = (CCSeries) ccd_el;

				if (last != null)
					last = flushZyklusList(builder, last);

				if (addViewed) builder.append(ser.isViewed() ? "[X] " : "[ ] ");
				builder.append(ser.getTitle());

				if (addLanguage) builder.append(" [").append(ser.getSemiCommonOrAllLanguages().toOutputString()).append("]");
				if (addFormat) builder.append(" (").append(ser.getFormat().asString().toUpperCase()).append(")");
				if (addQuality) builder.append(" (").append(ser.getMediaInfoCategory().getLongText()).append(")");
				if (addYear) builder.append(" (").append(ser.getYearRange().asString()).append(")");
				if (addSize) builder.append(" (").append(ser.getFilesize().getFormatted()).append(")");

				builder.append(SimpleFileUtils.LINE_END);

				for (int j = 0; j < ser.getSeasonCount(); j++) {
					CCSeason season = ser.getSeasonByArrayIndex(j);

					builder.append("\t");
					builder.append(season.getTitle());
					if (addYear) builder.append(" (").append(season.getYear()).append(")");

					builder.append(System.lineSeparator());

					for (int k = 0; k < season.getEpisodeCount(); k++) {
						CCEpisode episode = season.getEpisodeByArrayIndex(k);

						builder.append("\t\t");

						if (addViewed) builder.append(episode.isViewed() ? "[X] " : "[ ] ");
						if (season.getEpisodeCount() <= 90)
							builder.append(String.format("%02d", episode.getEpisodeNumber())).append(" - ").append(episode.getTitle());
						else
							builder.append(String.format("%03d", episode.getEpisodeNumber())).append(" - ").append(episode.getTitle());

						if (addFormat) builder.append(" (").append(episode.getFormat().asString().toUpperCase()).append(")");
						if (addQuality) builder.append(" (").append(episode.getMediaInfoCategory().getLongText()).append(")");
						if (addSize) builder.append(" (").append(episode.getFilesize().getFormatted()).append(")");

						builder.append(System.lineSeparator());
					}
				}
			}
		}

		return builder.toString();
	}

	@SuppressWarnings("nls")
	private List<CCMovie> flushZyklusList(StringBuilder builder, List<CCMovie> last) {
		int len = 0;
		int vcount = 0;
		
		CCFileSize bytesum = CCFileSize.ZERO;
		
		for (CCMovie melem : last) {
			bytesum = CCFileSize.add(bytesum, melem.getFilesize());
			len = Math.max(len, melem.getZyklus().getRoman().length());
			if (melem.isViewed()) vcount++;			
		}
		
		if (addViewed) builder.append((vcount == last.size()) ? "[X] " : ((vcount == 0) ? "[ ] " : "[/] "));
		builder.append(last.get(0).getZyklus().getTitle());
		if (addSize) builder.append(" (").append(FileSizeFormatter.format(bytesum)).append(")");
		builder.append(SimpleFileUtils.LINE_END);		
		
		for (CCMovie mov : last) {
			builder.append("\t");
			if (addViewed) builder.append(mov.isViewed() ? "[X] " : "[ ] ");
			builder.append(mov.getZyklus().getTitle());
			builder.append(" ");
			builder.append(StringUtils.rightPad(mov.getZyklus().getRoman(), len));
			builder.append(" - ");
			builder.append(mov.getTitle());
			
			if (addLanguage) builder.append(" [").append(mov.getLanguage().toOutputString()).append("]");
			if (addFormat) builder.append(" (").append(mov.getFormat().asString().toUpperCase()).append(")");
			if (addQuality) builder.append(" (").append(mov.getMediaInfoCategory().getLongText()).append(")");
			if (addYear) builder.append(" (").append(mov.getYear()).append(")");
			if (addSize) builder.append(" (").append(mov.getFilesize().getFormatted()).append(")");

			builder.append(SimpleFileUtils.LINE_END);
		}

		return null;
	}

	@Override
	protected String getFileExtension() {
		return ExportHelper.EXTENSION_TEXTEXPORT_PLAIN;
	}
}
