package de.jClipCorn.gui.frames.textExportFrame;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.features.serialization.ExportHelper;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseJSONExporter extends DatabaseTextExporter {

	@Override
	protected String getFormatName() {
		return "JSON"; //$NON-NLS-1$
	}

	@Override
	@SuppressWarnings("nls")
	protected String create() {
		List<CCDatabaseElement> elements = getOrderedList();
		
		StringBuilder builder = new StringBuilder();

		builder.append("[");
		builder.append(System.lineSeparator());
		
		for (int i = 0; i < elements.size(); i++) {
			CCDatabaseElement ccd_el = elements.get(i);
			
			boolean last = (i == elements.size()-1);
			
			builder.append("\t");
			builder.append("{");
			builder.append(System.lineSeparator());

			if (ccd_el.isMovie()) {				
				CCMovie mov = (CCMovie) ccd_el;
				
				List<String> attributes = new ArrayList<>();
				
				attributes.add("\t\t\"type\": 0");
				if (mov.getZyklus().isSet()) {
					attributes.add("\t\t\"zyklus\": \"" + simpleEscape(mov.getZyklus().getTitle()) + "\"");
					attributes.add("\t\t\"zyklusNummer\": " + mov.getZyklus().getNumber());
				}
				attributes.add("\t\t\"titel\": \"" + simpleEscape(mov.getTitle()) + "\"");
				if (addLanguage) attributes.add("\t\t\"languages\": [" + mov.getLanguage().ccstream().stringjoin(l -> "\""+l.getLongString()+"\"", ", ") + "]");
				if (addFormat) attributes.add("\t\t\"format\": \"" + mov.getFormat().asString() + "\"");
				if (addYear) attributes.add("\t\t\"year\": " + mov.getYear());
				if (addSize) attributes.add("\t\t\"size\": " + mov.getFilesize().getBytes());
				if (addViewed) attributes.add("\t\t\"viewed\": " + (mov.isViewed() ? "true" : "false"));
				
				builder.append(StringUtils.join(attributes, "," + System.lineSeparator()));
				builder.append(System.lineSeparator());
			} else if (ccd_el.isSeries()) {
				CCSeries ser = (CCSeries) ccd_el;
				
				builder.append("\t\t");
				builder.append("\"titel\": \"").append(simpleEscape(ser.getTitle())).append("\",");
				builder.append(System.lineSeparator());
				
				if (addLanguage) {
					builder.append("\t\t");
					builder.append(System.lineSeparator());
				}
				
				builder.append("\t\t");
				builder.append("\"seasons\": [");
				builder.append(System.lineSeparator());
				
				for (int j = 0; j < ser.getSeasonCount(); j++) {
					boolean last_season = (j == ser.getSeasonCount()-1);
					
					CCSeason season = ser.getSeasonByArrayIndex(j);
					
					builder.append("\t\t\t");
					builder.append("{");
					builder.append(System.lineSeparator());
					
					builder.append("\t\t\t\t");
					builder.append("\"titel\": \"").append(simpleEscape(season.getTitle())).append("\",");
					builder.append(System.lineSeparator());

					if (addYear) {
						builder.append("\t\t\t\t");
						builder.append("\"year\": " + season.getYear() + ",");
						builder.append(System.lineSeparator());
					}
					
					builder.append("\t\t\t\t");
					builder.append("\"episodes\": [");
					builder.append(System.lineSeparator());
					
					for (int k = 0; k < season.getEpisodeCount(); k++) {
						boolean last_episode = (k == season.getEpisodeCount()-1);
						
						CCEpisode episode = season.getEpisodeByArrayIndex(k);
						
						builder.append("\t\t\t\t\t");
						builder.append("{");
						builder.append(System.lineSeparator());
						
						List<String> attributes = new ArrayList<>();
						
						attributes.add("\t\t\t\t\t\t\"titel\": \"" + simpleEscape(episode.getTitle()) + "\"");

						if (addLanguage) attributes.add("\t\t\t\t\t\t\"languages\": [" + episode.getLanguage().ccstream().stringjoin(l -> "\""+l.getLongString()+"\"", ", ") + "\"");
						if (addFormat) attributes.add("\t\t\t\t\t\t\"format\": \"" + episode.getFormat().asString() + "\"");
						if (addSize) attributes.add("\t\t\t\t\t\t\"size\": " + episode.getFilesize().getBytes());
						if (addViewed) attributes.add("\t\t\t\t\t\t\"viewed\": " + (episode.isViewed() ? "true" : "false"));

						builder.append(StringUtils.join(attributes, "," + System.lineSeparator()));
						builder.append(System.lineSeparator());
						
						builder.append("\t\t\t\t\t");
						if (last_episode)
							builder.append("}");
						else
							builder.append("},");
						builder.append(System.lineSeparator());
					}
					
					builder.append("\t\t\t\t");
					builder.append("]");
					builder.append(System.lineSeparator());
					
					builder.append("\t\t\t");
					if (last_season)
						builder.append("}");
					else
						builder.append("},");
					builder.append(System.lineSeparator());
				}
				
				builder.append("\t\t");
				builder.append("]");
				builder.append(System.lineSeparator());
			}

			builder.append("\t");
			builder.append(last ? "}" : "},");
			builder.append(System.lineSeparator());
			builder.append(System.lineSeparator());
		}
		
		builder.append("]");
		builder.append(System.lineSeparator());
		
		return StringUtils.chomp(StringUtils.chomp(builder.toString()));
	}

	@Override
	protected String getFileExtension() {
		return ExportHelper.EXTENSION_TEXTEXPORT_JSON;
	}
}
