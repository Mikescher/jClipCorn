package de.jClipCorn.features.metadata.impl;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.metadata.*;
import de.jClipCorn.features.metadata.exceptions.InnerMediaQueryException;
import de.jClipCorn.features.metadata.exceptions.InnerMediaQueryExceptionWithXML;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.ErrOpt;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.helper.ProcessHelper;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;
import de.jClipCorn.util.xml.CCXMLParser;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MediaInfoRunner extends MetadataRunner {

	// https://mediaarea.net/mediainfo

	// https://mediaarea.net/mediainfo/mediainfo_2_0.xsd

	private final CCMovieList movielist;

	public MediaInfoRunner(CCMovieList ml) {
		this.movielist = ml;
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	@Override
	@SuppressWarnings("nls")
	public VideoMetadata run(FSPath file) throws IOException, MetadataQueryException
	{
		var mqpath = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue().getPathAndArgs();
		if (! mqpath.Item1.exists()) throw new MediaQueryException("MediaQuery not found");

		var args = new ArrayList<String>();
		args.add(file.toAbsolutePathString());
		args.add("--Output=XML");
		args.addAll(Arrays.asList(mqpath.Item2));

		Tuple3<Integer, String, String> proc = ProcessHelper.procExec(mqpath.Item1.toAbsolutePathString(), args.toArray(new String[0]));

		if (proc.Item1 != 0) throw new MediaQueryException("MediaQuery returned " + proc.Item1, proc.Item2 + "\n\n\n\n" + proc.Item3);

		BasicFileAttributes attr = file.readFileAttr();

		String mqxml = proc.Item2;

		try
		{
			var hash = ChecksumHelper.fastVideoHash(file);

			return parse(proc.Item2, hash, attr, file);
		}
		catch (InnerMediaQueryException e)
		{
			throw new MediaQueryException(e.getMessage(), mqxml, e);
		}
		catch (CCXMLException e)
		{
			throw new MediaQueryException(e.Message, ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + e.XMLContent, e);
		}
		catch (Exception e)
		{
			throw new MediaQueryException("Could not parse Output XML", ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e) + "\n\n\n--------------\n\n\n" + mqxml, e);
		}
	}

	@SuppressWarnings("nls")
	public VideoMetadata parse(String output, String fvhash, BasicFileAttributes attr, FSPath filename) throws InnerMediaQueryException, CCXMLException
	{
		CCXMLParser doc = CCXMLParser.parse(output);
		var mqxml = doc.getXMLString();

		try
		{
			CCXMLElement root = doc.getRoot();
			if (root == null) throw new InnerMediaQueryException("no root xml element");

			CCXMLElement media = root.getFirstChildOrThrow("media");
			if (media == null) throw new InnerMediaQueryException("no media xml element");

			ErrOpt<String,  MetadataError> format         = ErrOpt.empty();
			ErrOpt<String,  MetadataError> format_Version = ErrOpt.empty();
			ErrOpt<Long,    MetadataError> fileSize       = ErrOpt.empty();
			ErrOpt<Double,  MetadataError> duration       = ErrOpt.empty();
			ErrOpt<Integer, MetadataError> overallBitRate = ErrOpt.empty();
			ErrOpt<Double,  MetadataError> frameRate      = ErrOpt.empty();

			List<VideoTrackMetadata>    vtracks = new ArrayList<>();
			List<AudioTrackMetadata>    atracks = new ArrayList<>();
			List<SubtitleTrackMetadata> stracks = new ArrayList<>();

			var trackIndex = 1;
			for (CCXMLElement track : media.getAllChildren("track"))
			{
				String strtype = track.getAttributeValueOrThrow("type");
				switch (strtype) {
					case "General":
						if (track.hasChildWithNonWhitespaceValue("Format"))         format          = ErrOpt.of(() -> track.getFirstChildValueOrThrow("Format"),            MetadataError::Wrap);
						if (track.hasChildWithNonWhitespaceValue("Format_Version")) format_Version  = ErrOpt.of(() -> track.getFirstChildValueOrThrow("Format_Version"),    MetadataError::Wrap);
						if (track.hasChildWithNonWhitespaceValue("FileSize"))       fileSize        = ErrOpt.of(() -> track.getFirstChildLongValueOrThrow("FileSize"),      MetadataError::Wrap);
						if (track.hasChildWithNonWhitespaceValue("Duration"))       duration        = ErrOpt.of(() -> track.getFirstChildDoubleValueOrThrow("Duration"),    MetadataError::Wrap);
						if (track.hasChildWithNonWhitespaceValue("OverallBitRate")) overallBitRate  = ErrOpt.of(() -> track.getFirstChildIntValueOrThrow("OverallBitRate"), MetadataError::Wrap);
						if (track.hasChildWithNonWhitespaceValue("FrameRate"))      frameRate       = ErrOpt.of(() -> track.getFirstChildDoubleValueOrThrow("FrameRate"),   MetadataError::Wrap);

						break;
					case "Video":
						var vdata = parseVideoTrack(trackIndex, vtracks.size()+1, track);
						vtracks.add(vdata);
						break;
					case "Audio":
						var adata = parseAudioTrack(trackIndex, atracks.size()+1, track);
						atracks.add(adata);
						break;
					case "Text":
						var sdata = parseSubtitleTrack(trackIndex, stracks.size()+1, track);
						stracks.add(sdata);
						break;
					case "Menu":
					case "Other":
					case "Image":
						// Ignored
						break;
					default:
						throw new InnerMediaQueryException("Unknown Track Type: " + strtype);
				}

				trackIndex++;
			}

			if (vtracks.size() == 1 && vtracks.get(0).Duration.isPresent()) duration = ErrOpt.of(vtracks.get(0).Duration.get());

			List<ErrOpt<CCDBLanguage, MetadataError>> alng = extractAudioLangs(atracks);
			List<ErrOpt<CCDBLanguage, MetadataError>> slng = extractSubLangs(stracks);

			var cdate = attr.creationTime().toMillis();
			var mdate = attr.lastModifiedTime().toMillis();

			return new VideoMetadata
			(
				getSourceType(),
				filename,
				output,
				ErrOpt.of(cdate),
				ErrOpt.of(mdate),
				ErrOpt.of(fvhash),
				format,
				format_Version,
				fileSize,
				duration,
				overallBitRate,
				frameRate,
				vtracks,
				atracks,
				stracks,
				alng,
				slng
			);
		}
		catch (InnerMediaQueryException e)
		{
			throw new InnerMediaQueryExceptionWithXML(e, mqxml);
		}

	}

	@SuppressWarnings("nls")
	private VideoTrackMetadata parseVideoTrack(int idxTotal, int idxTrackType, CCXMLElement xml)
	{
		ErrOpt<String,  MetadataError> format          = ErrOpt.empty();
		ErrOpt<String,  MetadataError> format_Profile  = ErrOpt.empty();
		ErrOpt<String,  MetadataError> codecID         = ErrOpt.empty();
		ErrOpt<Integer, MetadataError> bitRate         = ErrOpt.empty();
		ErrOpt<Integer, MetadataError> bitRateNominal  = ErrOpt.empty();
		ErrOpt<Integer, MetadataError> width           = ErrOpt.empty();
		ErrOpt<Integer, MetadataError> height          = ErrOpt.empty();
		ErrOpt<Double,  MetadataError> frameRate       = ErrOpt.empty();
		ErrOpt<Integer, MetadataError> frameCount      = ErrOpt.empty();
		ErrOpt<Short,   MetadataError> bitDepth        = ErrOpt.empty();
		ErrOpt<Double,  MetadataError> duration        = ErrOpt.empty();
		ErrOpt<Boolean, MetadataError> vdefault        = ErrOpt.empty();

		if (xml.hasChildWithNonWhitespaceValue("Format"))          format         = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("Format"),             MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("Format_Profile"))  format_Profile = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("Format_Profile"),     MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("CodecID"))         codecID        = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("CodecID"),            MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("BitRate"))         bitRate        = ErrOpt.of(() -> xml.getFirstChildIntValueOrThrow("BitRate"),         MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("Width"))           width          = ErrOpt.of(() -> xml.getFirstChildIntValueOrThrow("Width"),           MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("Height"))          height         = ErrOpt.of(() -> xml.getFirstChildIntValueOrThrow("Height"),          MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("FrameRate"))       frameRate      = ErrOpt.of(() -> xml.getFirstChildDoubleValueOrThrow("FrameRate"),    MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("FrameCount"))      frameCount     = ErrOpt.of(() -> xml.getFirstChildIntValueOrThrow("FrameCount"),      MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("BitDepth"))        bitDepth       = ErrOpt.of(() -> (short)xml.getFirstChildIntValueOrThrow("BitDepth"), MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("Duration"))        duration       = ErrOpt.of(() -> xml.getFirstChildDoubleValueOrThrow("Duration"),     MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("Default"))         vdefault       = ErrOpt.of(() -> parseBool(xml.getFirstChildValueOrThrow("Default")), MetadataError::Wrap);

		if (xml.hasChildWithNonWhitespaceValue("BitRate_Nominal")) bitRateNominal = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("BitRate_Nominal").contains("/") ? -1 : xml.getFirstChildIntValueOrThrow("BitRate_Nominal"), MetadataError::Wrap);

		return new VideoTrackMetadata
		(
			idxTotal,
			idxTrackType,
			format,
			format_Profile,
			codecID,
			bitRate,
			bitRateNominal,
			width,
			height,
			frameRate,
			frameCount,
			bitDepth,
			duration,
			vdefault
		);
	}

	@SuppressWarnings("nls")
	private AudioTrackMetadata parseAudioTrack(int idxTotal, int idxTrackType, CCXMLElement xml)
	{
		ErrOpt<String,  MetadataError> format         = ErrOpt.empty();
		ErrOpt<String,  MetadataError> language       = ErrOpt.empty();
		ErrOpt<String,  MetadataError> title          = ErrOpt.empty();
		ErrOpt<String,  MetadataError> codecID        = ErrOpt.empty();
		ErrOpt<Short,   MetadataError> channels       = ErrOpt.empty();
		ErrOpt<Integer, MetadataError> samplingrate   = ErrOpt.empty();
		ErrOpt<Integer, MetadataError> bitRate        = ErrOpt.empty();
		ErrOpt<Integer, MetadataError> bitRateNominal = ErrOpt.empty();
		ErrOpt<Boolean, MetadataError> adefault       = ErrOpt.empty();


		if (xml.hasChildWithNonWhitespaceValue("Format"))           format         = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("Format"),             MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("Language"))         language       = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("Language"),           MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("Title"))            title          = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("Title"),              MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("CodecID"))          codecID        = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("CodecID"),            MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("Channels"))         channels       = ErrOpt.of(() -> (short)xml.getFirstChildIntValueOrThrow("Channels"), MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("SamplingRate"))     samplingrate   = ErrOpt.of(() -> xml.getFirstChildIntValueOrThrow("SamplingRate"),    MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("BitRate"))          bitRate        = ErrOpt.of(() -> xml.getFirstChildIntValueOrThrow("BitRate"),         MetadataError::Wrap);

		if (xml.hasChildWithNonWhitespaceValue("BitRate_Nominal"))  bitRateNominal = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("BitRate_Nominal").contains("/") ? -1 : xml.getFirstChildIntValueOrThrow("BitRate_Nominal"), MetadataError::Wrap);

		if (xml.hasChildWithNonWhitespaceValue("Default"))          adefault       = ErrOpt.of(() -> parseBool(xml.getFirstChildValueOrThrow("Default")), MetadataError::Wrap);

		return new AudioTrackMetadata
		(
			idxTotal,
			idxTrackType,
			format,
			language,
			title,
			codecID,
			channels,
			samplingrate,
			bitRate,
			bitRateNominal,
			adefault
		);
	}
	
	@SuppressWarnings("nls")
	private SubtitleTrackMetadata parseSubtitleTrack(int idxTotal, int idxTrackType, CCXMLElement xml)
	{
		ErrOpt<String,  MetadataError> format   = ErrOpt.empty();
		ErrOpt<String,  MetadataError> title    = ErrOpt.empty();
		ErrOpt<String,  MetadataError> codecID  = ErrOpt.empty();
		ErrOpt<String,  MetadataError> language = ErrOpt.empty();
		ErrOpt<Boolean, MetadataError> sdefault = ErrOpt.empty();

		if (xml.hasChildWithNonWhitespaceValue("Format"))   format   = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("Format"),              MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("Title"))    title    = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("Title"),               MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("CodecID"))  codecID  = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("CodecID"),             MetadataError::Wrap);
		if (xml.hasChildWithNonWhitespaceValue("Language")) language = ErrOpt.of(() -> xml.getFirstChildValueOrThrow("Language"),            MetadataError::Wrap);

		if (xml.hasChildWithNonWhitespaceValue("Default"))  sdefault = ErrOpt.of(() -> parseBool(xml.getFirstChildValueOrThrow("Default")),  MetadataError::Wrap);

		return new SubtitleTrackMetadata
		(
			idxTotal,
			idxTrackType,
			format,
			title,
			language,
			codecID,
			sdefault
		);
	}

	@SuppressWarnings("nls")
	private boolean parseBool(String value) throws InnerMediaQueryException {
		if (value.equals("Yes")) return true;
		if (value.equals("No")) return false;
		throw new InnerMediaQueryException("Unknown boolean := '" + value + "'");
	}

	@Override
	public MetadataSourceType getSourceType() {
		return MetadataSourceType.MEDIAINFO;
	}

	@Override
	public boolean isConfiguredAndRunnable() {
		return ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue().existsAndCanExecute();
	}
}
