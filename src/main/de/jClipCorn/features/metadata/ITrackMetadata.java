package de.jClipCorn.features.metadata;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.util.datatypes.ErrOpt;

public interface ITrackMetadata
{
	int getTypeIndex();
	ErrOpt<CCDBLanguage, MetadataError> calcCCDBLanguage();
	String getType();
	ErrOpt<String, MetadataError> getLanguageText();
	ErrOpt<String, MetadataError> getTitle();
}
