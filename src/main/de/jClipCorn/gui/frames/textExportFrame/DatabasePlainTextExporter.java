package de.jClipCorn.gui.frames.textExportFrame;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.helper.TextFileUtils;

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
		for (int i = 0; i < elements.size(); i++) {
			CCDatabaseElement ccd_el = elements.get(i);
			
			if (ccd_el.isMovie()) {				
				CCMovie mov = (CCMovie) ccd_el;
				
				if (last != null && (mov.getZyklus().isEmpty() || ! last.get(0).getZyklus().getTitle().equals(mov.getZyklus().getTitle()))) {
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
				} else if (last == null && mov.getZyklus().isEmpty()){
					// ###### out ######
					
					if (addViewed) builder.append(mov.isViewed() ? "[X] " : "[ ] ");
					builder.append(mov.getTitle());
					
					if (addLanguage) builder.append(" [" + mov.getLanguage().getShortString() + "]");
					if (addFormat) builder.append(" (" + mov.getFormat().asString().toUpperCase() + ")");
					if (addQuality) builder.append(" (" + mov.getQuality().asString() + ")");
					if (addYear) builder.append(" (" + mov.getYear() + ")");
					if (addSize) builder.append(" (" + mov.getFilesize().getFormatted() + ")");

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
				
				if (addLanguage) builder.append(" [" + ser.getLanguage().getShortString() + "]");
				if (addFormat) builder.append(" (" + ser.getFormat().asString().toUpperCase() + ")");
				if (addQuality) builder.append(" (" + ser.getQuality().asString() + ")");
				if (addYear) builder.append(" (" + ser.getYearRange().asString() + ")");
				if (addSize) builder.append(" (" + ser.getFilesize().getFormatted() + ")");

				builder.append(TextFileUtils.LINE_END);
				
				for (int j = 0; j < ser.getSeasonCount(); j++) {
					CCSeason season = ser.getSeason(j);
					
					builder.append("\t");
					builder.append(season.getTitle());
					if (addYear) builder.append(" (" + season.getYear() + ")");
					
					builder.append(System.lineSeparator());
					
					for (int k = 0; k < season.getEpisodeCount(); k++) {
						CCEpisode episode = season.getEpisode(k);
						
						builder.append("\t\t");

						if (addViewed) builder.append(episode.isViewed() ? "[X] " : "[ ] ");
						if (season.getEpisodeCount() <= 90)
							builder.append(String.format("%02d", episode.getEpisodeNumber()) + " - " + episode.getTitle());
						else
							builder.append(String.format("%03d", episode.getEpisodeNumber()) + " - " + episode.getTitle());

						if (addFormat) builder.append(" (" + episode.getFormat().asString().toUpperCase() + ")");
						if (addQuality) builder.append(" (" + episode.getQuality().asString() + ")");
						if (addSize) builder.append(" (" + episode.getFilesize().getFormatted() + ")");

						builder.append(System.lineSeparator());
					}
				}
			}
		}
		
		if (last != null) 
			last = flushZyklusList(builder, last);
		
		return builder.toString();
	}

	@SuppressWarnings("nls")
	private List<CCMovie> flushZyklusList(StringBuilder builder, List<CCMovie> last) {
		int len = 0;
		int vcount = 0;
		
		CCMovieSize bytesum = new CCMovieSize();
		
		for (CCMovie melem : last) {
			bytesum.add(melem.getFilesize());
			len = Math.max(len, melem.getZyklus().getRoman().length());
			if (melem.isViewed()) vcount++;			
		}
		
		if (addViewed) builder.append((vcount == last.size()) ? "[X] " : ((vcount == 0) ? "[ ] " : "[/] "));
		builder.append(last.get(0).getZyklus().getTitle());
		if (addSize) builder.append(" (" + FileSizeFormatter.format(bytesum) + ")");
		builder.append(TextFileUtils.LINE_END);		
		
		for (CCMovie mov : last) {
			builder.append("\t");
			if (addViewed) builder.append(mov.isViewed() ? "[X] " : "[ ] ");
			builder.append(mov.getZyklus().getTitle());
			builder.append(" ");
			builder.append(StringUtils.rightPad(mov.getZyklus().getRoman(), len));
			builder.append(" - ");
			builder.append(mov.getTitle());
			
			if (addLanguage) builder.append(" [" + mov.getLanguage().getShortString() + "]");
			if (addFormat) builder.append(" (" + mov.getFormat().asString().toUpperCase() + ")");
			if (addQuality) builder.append(" (" + mov.getQuality().asString() + ")");
			if (addYear) builder.append(" (" + mov.getYear() + ")");
			if (addSize) builder.append(" (" + mov.getFilesize().getFormatted() + ")");

			builder.append(TextFileUtils.LINE_END);
		}
		
		last = null;
		return last;
	}

	@Override
	protected String getFileExtension() {
		return ExportHelper.EXTENSION_TEXTEXPORT_PLAIN;
	}
}
