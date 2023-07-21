package de.jClipCorn.features.metadata;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.metadata.impl.MetadataRunner;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.ErrOpt;
import org.jetbrains.annotations.NotNull;

public class AudioTrackMetadata implements ITrackMetadata {
	         public final int                            TotalIndex;
	         public final int                            TypeIndex;
	@NotNull public final ErrOpt<String,  MetadataError> Format;
	@NotNull public final ErrOpt<String,  MetadataError> Language;
	@NotNull public final ErrOpt<String,  MetadataError> Title;
	@NotNull public final ErrOpt<String,  MetadataError> CodecID;
	@NotNull public final ErrOpt<Short,   MetadataError> Channels;
	@NotNull public final ErrOpt<Integer, MetadataError> Samplingrate;
	@NotNull public final ErrOpt<Integer, MetadataError> BitRate;
	@NotNull public final ErrOpt<Integer, MetadataError> BitRateNominal;
	@NotNull public final ErrOpt<Boolean, MetadataError> Default;

	public AudioTrackMetadata
	(
		         int                            total_index,
				 int                            type_index,
		@NotNull ErrOpt<String,  MetadataError> format,
		@NotNull ErrOpt<String,  MetadataError> language,
		@NotNull ErrOpt<String,  MetadataError> title,
		@NotNull ErrOpt<String,  MetadataError> codecID,
		@NotNull ErrOpt<Short,   MetadataError> channels,
		@NotNull ErrOpt<Integer, MetadataError> samplingrate,
		@NotNull ErrOpt<Integer, MetadataError> bitRate,
		@NotNull ErrOpt<Integer, MetadataError> bitRateNominal,
		@NotNull ErrOpt<Boolean, MetadataError> aDefault
	)
	{
		TotalIndex = total_index;
		TypeIndex = type_index;
		Format = format;
		Language = language;
		Title = title;
		CodecID = codecID;
		Channels = channels;
		Samplingrate = samplingrate;
		BitRate = bitRate;
		BitRateNominal = bitRateNominal;
		Default = aDefault;
	}

	@Override
	public int getTypeIndex() {
		return TypeIndex;
	}

	@Override
	public ErrOpt<CCDBLanguage, MetadataError> calcCCDBLanguage() {
		if (Language.isEmpty() && this.Title.isEmpty()) return ErrOpt.empty();
		if (Language.isError()) return ErrOpt.err(Language.getErr());
		if (Title.isError() && !Language.isPresent()) return ErrOpt.err(Title.getErr());
		return ErrOpt.of(() -> MetadataRunner.getLanguage(Language.orElse(Str.Empty), Title.orElse(Str.Empty)), MetadataError::Wrap);
	}

	@Override
	@SuppressWarnings("nls")
	public String getType() {
		return "audio";
	}

	@Override
	public ErrOpt<String, MetadataError> getLanguageText() {
		return Language;
	}

	@Override
	public ErrOpt<String, MetadataError> getTitle() {
		return Title;
	}
}
