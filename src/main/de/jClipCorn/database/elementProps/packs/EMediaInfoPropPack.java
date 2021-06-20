package de.jClipCorn.database.elementProps.packs;

import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.database.elementProps.impl.*;

public class EMediaInfoPropPack {

	public final ELongProp   CDate;
	public final ELongProp   MDate;
	public final EStringProp Checksum;
	public final ELongProp   Filesize;
	public final EDoubleProp Duration;
	public final EIntProp    Bitrate;
	public final EStringProp VideoFormat;
	public final EIntProp    Width;
	public final EIntProp    Height;
	public final EDoubleProp Framerate;
	public final EShortProp  Bitdepth;
	public final EIntProp    Framecount;
	public final EStringProp VideoCodec;
	public final EStringProp AudioFormat;
	public final EShortProp  AudioChannels;
	public final EStringProp AudioCodec;
	public final EIntProp    AudioSamplerate;

	private CCMediaInfo _cache = null;
	private IEProperty[] _properties = null;
	private boolean _ignoreCacheUpdates = false;

	public EMediaInfoPropPack(String namePrefix, CCMediaInfo startValue, IPropertyParent parent, EPropertyType proptype)
	{
		CDate           = new ELongProp(  namePrefix + ".CDate",           startValue.getCDate(),           parent, proptype);
		MDate           = new ELongProp(  namePrefix + ".MDate",           startValue.getMDate(),           parent, proptype);
		Checksum        = new EStringProp(namePrefix + ".Checksum",        startValue.getChecksum(),        parent, proptype);
		Filesize        = new ELongProp(  namePrefix + ".Filesize",        startValue.getFilesize(),        parent, proptype);
		Duration        = new EDoubleProp(namePrefix + ".Duration",        startValue.getDuration(),        parent, proptype);
		Bitrate         = new EIntProp(   namePrefix + ".Bitrate",         startValue.getBitrate(),         parent, proptype);
		VideoFormat     = new EStringProp(namePrefix + ".VideoFormat",     startValue.getVideoFormat(),     parent, proptype);
		Width           = new EIntProp(   namePrefix + ".Width",           startValue.getWidth(),           parent, proptype);
		Height          = new EIntProp(   namePrefix + ".Height",          startValue.getHeight(),          parent, proptype);
		Framerate       = new EDoubleProp(namePrefix + ".Framerate",       startValue.getFramerate(),       parent, proptype);
		Bitdepth        = new EShortProp( namePrefix + ".Bitdepth",        startValue.getBitdepth(),        parent, proptype);
		Framecount      = new EIntProp(   namePrefix + ".Framecount",      startValue.getFramecount(),      parent, proptype);
		VideoCodec      = new EStringProp(namePrefix + ".VideoCodec",      startValue.getVideoCodec(),      parent, proptype);
		AudioFormat     = new EStringProp(namePrefix + ".AudioFormat",     startValue.getAudioFormat(),     parent, proptype);
		AudioChannels   = new EShortProp( namePrefix + ".AudioChannels",   startValue.getAudioChannels(),   parent, proptype);
		AudioCodec      = new EStringProp(namePrefix + ".AudioCodec",      startValue.getAudioCodec(),      parent, proptype);
		AudioSamplerate = new EIntProp(   namePrefix + ".AudioSamplerate", startValue.getAudioSamplerate(), parent, proptype);

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

		_cache = new CCMediaInfo
		(
			CDate.get(),
			MDate.get(),
			Filesize.get(),
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
			AudioSamplerate.get(),
			Checksum.get()
		);
	}

	public CCMediaInfo get() {
		return _cache;
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

			CDate           .set(v.getCDate());
			MDate           .set(v.getMDate());
			Checksum        .set(v.getChecksum());
			Filesize        .set(v.getFilesize());
			Duration        .set(v.getDuration());
			Bitrate         .set(v.getBitrate());
			VideoFormat     .set(v.getVideoFormat());
			Width           .set(v.getWidth());
			Height          .set(v.getHeight());
			Framerate       .set(v.getFramerate());
			Bitdepth        .set(v.getBitdepth());
			Framecount      .set(v.getFramecount());
			VideoCodec      .set(v.getVideoCodec());
			AudioFormat     .set(v.getAudioFormat());
			AudioChannels   .set(v.getAudioChannels());
			AudioCodec      .set(v.getAudioCodec());
			AudioSamplerate .set(v.getAudioSamplerate());
		}
		finally
		{
			_ignoreCacheUpdates = false;
			updateCache();
		}
	}
}
