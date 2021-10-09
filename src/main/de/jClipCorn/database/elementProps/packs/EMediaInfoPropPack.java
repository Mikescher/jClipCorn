package de.jClipCorn.database.elementProps.packs;

import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.database.elementProps.impl.*;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.util.datatypes.Opt;

public class EMediaInfoPropPack extends EPropertyPack {

	public final EOptLongProp     CDate;
	public final EOptLongProp     MDate;
	public final EOptStringProp   Checksum;
	public final EOptFileSizeProp Filesize;
	public final EOptDoubleProp   Duration;
	public final EOptIntProp      Bitrate;
	public final EOptStringProp   VideoFormat;
	public final EOptIntProp      Width;
	public final EOptIntProp      Height;
	public final EOptDoubleProp   Framerate;
	public final EOptShortProp    Bitdepth;
	public final EOptIntProp      Framecount;
	public final EOptStringProp   VideoCodec;
	public final EOptStringProp   AudioFormat;
	public final EOptShortProp    AudioChannels;
	public final EOptStringProp   AudioCodec;
	public final EOptIntProp      AudioSamplerate;

	private CCMediaInfo _cache = null;
	private PartialMediaInfo _cachePartial = null;
	private IEProperty[] _properties = null;
	private boolean _ignoreCacheUpdates = false;

	public EMediaInfoPropPack(String namePrefix, PartialMediaInfo startValue, IPropertyParent parent)
	{
		CDate           = new EOptLongProp(    namePrefix + ".CDate",           startValue.CDate,            parent, EPropertyType.LOCAL_FILE_REF_SUBJECTIVE);
		MDate           = new EOptLongProp(    namePrefix + ".MDate",           startValue.MDate,            parent, EPropertyType.LOCAL_FILE_REF_SUBJECTIVE);
		Checksum        = new EOptStringProp(  namePrefix + ".Checksum",        startValue.Checksum,         parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		Filesize        = new EOptFileSizeProp(namePrefix + ".Filesize",        startValue.Filesize,         parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		Duration        = new EOptDoubleProp(  namePrefix + ".Duration",        startValue.Duration,         parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		Bitrate         = new EOptIntProp(     namePrefix + ".Bitrate",         startValue.Bitrate,          parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		VideoFormat     = new EOptStringProp(  namePrefix + ".VideoFormat",     startValue.VideoFormat,      parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		Width           = new EOptIntProp(     namePrefix + ".Width",           startValue.Width,            parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		Height          = new EOptIntProp(     namePrefix + ".Height",          startValue.Height,           parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		Framerate       = new EOptDoubleProp(  namePrefix + ".Framerate",       startValue.Framerate,        parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		Bitdepth        = new EOptShortProp(   namePrefix + ".Bitdepth",        startValue.Bitdepth,         parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		Framecount      = new EOptIntProp(     namePrefix + ".Framecount",      startValue.Framecount,       parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		VideoCodec      = new EOptStringProp(  namePrefix + ".VideoCodec",      startValue.VideoCodec,       parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		AudioFormat     = new EOptStringProp(  namePrefix + ".AudioFormat",     startValue.AudioFormat,      parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		AudioChannels   = new EOptShortProp(   namePrefix + ".AudioChannels",   startValue.AudioChannels,    parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		AudioCodec      = new EOptStringProp(  namePrefix + ".AudioCodec",      startValue.AudioCodec,       parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
		AudioSamplerate = new EOptIntProp(     namePrefix + ".AudioSamplerate", startValue.AudioSamplerate,  parent, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);

		CDate.addChangeListener((_1, _2, _3) -> updateCache());
		MDate.addChangeListener((_1, _2, _3) -> updateCache());
		Checksum.addChangeListener((_1, _2, _3) -> updateCache());
		Filesize.addChangeListener((_1, _2, _3) -> updateCache());
		Duration.addChangeListener((_1, _2, _3) -> updateCache());
		Bitrate.addChangeListener((_1, _2, _3) -> updateCache());
		VideoFormat.addChangeListener((_1, _2, _3) -> updateCache());
		Width.addChangeListener((_1, _2, _3) -> updateCache());
		Height.addChangeListener((_1, _2, _3) -> updateCache());
		Framerate.addChangeListener((_1, _2, _3) -> updateCache());
		Bitdepth.addChangeListener((_1, _2, _3) -> updateCache());
		Framecount.addChangeListener((_1, _2, _3) -> updateCache());
		VideoCodec.addChangeListener((_1, _2, _3) -> updateCache());
		AudioFormat.addChangeListener((_1, _2, _3) -> updateCache());
		AudioChannels.addChangeListener((_1, _2, _3) -> updateCache());
		AudioCodec.addChangeListener((_1, _2, _3) -> updateCache());
		AudioSamplerate.addChangeListener((_1, _2, _3) -> updateCache());

		updateCache();
	}

	private void updateCache() {
		if (_ignoreCacheUpdates) return;

		_cachePartial = PartialMediaInfo.create
		(
			Opt.empty(),
			CDate.get(),
			MDate.get(),
			Filesize.get(),
			Checksum.get(),
			Duration.get(),
			Bitrate.get(),
			VideoFormat.get(),
			Width.get(),
			Height.get(),
			Framerate.get(),
			Bitdepth.get(),
			Framecount.get(),
			VideoCodec.get(),
			AudioFormat.get(),
			AudioChannels.get(),
			AudioCodec.get(),
			AudioSamplerate.get()
		);

		_cache = _cachePartial.toMediaInfo();
	}

	public CCMediaInfo get() {
		return _cache;
	}

	public PartialMediaInfo getPartial() {
		return _cachePartial;
	}

	public IEProperty[] getProperties()
	{
		if (_properties == null) _properties = listProperties();
		return _properties;
	}

	private IEProperty[] listProperties() {
		return new IEProperty[]
		{
			CDate,
			MDate,
			Checksum,
			Filesize,
			Duration,
			Bitrate,
			VideoFormat,
			Width,
			Height,
			Framerate,
			Bitdepth,
			Framecount,
			VideoCodec,
			AudioFormat,
			AudioChannels,
			AudioCodec,
			AudioSamplerate,
		};
	}

	public void set(CCMediaInfo v) {
		try
		{
			_ignoreCacheUpdates = true;

			CDate           .set(Opt.of(v.getCDate()));
			MDate           .set(Opt.of(v.getMDate()));
			Checksum        .set(Opt.of(v.getChecksum()));
			Filesize        .set(Opt.of(v.getFilesize()));
			Duration        .set(Opt.of(v.getDuration()));
			Bitrate         .set(Opt.of(v.getBitrate()));
			VideoFormat     .set(Opt.of(v.getVideoFormat()));
			Width           .set(Opt.of(v.getWidth()));
			Height          .set(Opt.of(v.getHeight()));
			Framerate       .set(Opt.of(v.getFramerate()));
			Bitdepth        .set(Opt.of(v.getBitdepth()));
			Framecount      .set(Opt.of(v.getFramecount()));
			VideoCodec      .set(Opt.of(v.getVideoCodec()));
			AudioFormat     .set(Opt.of(v.getAudioFormat()));
			AudioChannels   .set(Opt.of(v.getAudioChannels()));
			AudioCodec      .set(Opt.of(v.getAudioCodec()));
			AudioSamplerate .set(Opt.of(v.getAudioSamplerate()));
		}
		finally
		{
			_ignoreCacheUpdates = false;
			updateCache();
		}
	}

	public void set(PartialMediaInfo v) {
		try
		{
			_ignoreCacheUpdates = true;

			CDate           .set(v.CDate);
			MDate           .set(v.MDate);
			Checksum        .set(v.Checksum);
			Filesize        .set(v.Filesize);
			Duration        .set(v.Duration);
			Bitrate         .set(v.Bitrate);
			VideoFormat     .set(v.VideoFormat);
			Width           .set(v.Width);
			Height          .set(v.Height);
			Framerate       .set(v.Framerate);
			Bitdepth        .set(v.Bitdepth);
			Framecount      .set(v.Framecount);
			VideoCodec      .set(v.VideoCodec);
			AudioFormat     .set(v.AudioFormat);
			AudioChannels   .set(v.AudioChannels);
			AudioCodec      .set(v.AudioCodec);
			AudioSamplerate .set(v.AudioSamplerate);
		}
		finally
		{
			_ignoreCacheUpdates = false;
			updateCache();
		}
	}
}
