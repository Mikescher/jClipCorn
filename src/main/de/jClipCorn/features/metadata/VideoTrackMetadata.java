package de.jClipCorn.features.metadata;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.util.datatypes.ErrOpt;
import org.jetbrains.annotations.NotNull;

public class VideoTrackMetadata implements ITrackMetadata {
	         public final int                            TotalIndex;
	         public final int                            TypeIndex;
	@NotNull public final ErrOpt<String,  MetadataError> Format;
	@NotNull public final ErrOpt<String,  MetadataError> Format_Profile;
	@NotNull public final ErrOpt<String,  MetadataError> CodecID;
	@NotNull public final ErrOpt<Integer, MetadataError> BitRate;
	@NotNull public final ErrOpt<Integer, MetadataError> BitRateNominal;
	@NotNull public final ErrOpt<Integer, MetadataError> Width;
	@NotNull public final ErrOpt<Integer, MetadataError> Height;
	@NotNull public final ErrOpt<Double,  MetadataError> FrameRate;
	@NotNull public final ErrOpt<Integer, MetadataError> FrameCount;
	@NotNull public final ErrOpt<Short,   MetadataError> BitDepth;
	@NotNull public final ErrOpt<Double,  MetadataError> Duration;
	@NotNull public final ErrOpt<Boolean, MetadataError> Default;

	public VideoTrackMetadata
	(
			     int                            total_index,
			     int                            type_index,
		@NotNull ErrOpt<String,  MetadataError> format,
		@NotNull ErrOpt<String,  MetadataError> format_Profile,
		@NotNull ErrOpt<String,  MetadataError> codecID,
		@NotNull ErrOpt<Integer, MetadataError> bitRate,
		@NotNull ErrOpt<Integer, MetadataError> bitRateNominal,
		@NotNull ErrOpt<Integer, MetadataError> width,
		@NotNull ErrOpt<Integer, MetadataError> height,
		@NotNull ErrOpt<Double,  MetadataError> frameRate,
		@NotNull ErrOpt<Integer, MetadataError> frameCount,
		@NotNull ErrOpt<Short,   MetadataError> bitDepth,
		@NotNull ErrOpt<Double,  MetadataError> duration,
		@NotNull ErrOpt<Boolean, MetadataError> aDefault
	)
	{
		TotalIndex = total_index;
		TypeIndex = type_index;
		Format = format;
		Format_Profile = format_Profile;
		CodecID = codecID;
		BitRate = bitRate;
		BitRateNominal = bitRateNominal;
		Width = width;
		Height = height;
		FrameRate = frameRate;
		FrameCount = frameCount;
		BitDepth = bitDepth;
		Duration = duration;
		Default = aDefault;
	}

	@Override
	public int getTypeIndex() {
		return TypeIndex;
	}

	@Override
	public ErrOpt<CCDBLanguage, MetadataError> calcCCDBLanguage() {
		return ErrOpt.empty();
	}

	@Override
	@SuppressWarnings("nls")
	public String getType() {
		return "video";
	}

	@Override
	public ErrOpt<String, MetadataError> getLanguageText() {
		return ErrOpt.empty();
	}

	@Override
	public ErrOpt<String, MetadataError> getTitle() {
		return ErrOpt.empty();
	}
}
