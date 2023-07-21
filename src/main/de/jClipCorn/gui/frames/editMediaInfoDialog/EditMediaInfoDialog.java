package de.jClipCorn.gui.frames.editMediaInfoDialog;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.ITrackMetadata;
import de.jClipCorn.features.metadata.MetadataSourceType;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.features.metadata.VideoMetadata;
import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.features.metadata.impl.MetadataRunner;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.guiComponents.JFSPathTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.TimeZone;

public class EditMediaInfoDialog extends JCCDialog
{
	private MIDialogResultSet[] _results = new MIDialogResultSet[0];
	private MediaInfoResultHandler _handler = null;
	private MetadataSourceType _hintType = null;

	private Color colOK;
	private Color colErr;

	public EditMediaInfoDialog(Component owner, CCMovieList ml)
	{
		super(ml);

		initComponents();
		postInit(null, null, null);

		setLocationRelativeTo(owner);
	}

	public EditMediaInfoDialog(Component owner, CCMovieList ml, FSPath path, VideoMetadata r, MediaInfoResultHandler h) {
		super(ml);

		initComponents();
		postInit(path, r.toPartialMediaInfo(), h);

		setLocationRelativeTo(owner);
	}

	public EditMediaInfoDialog(Component owner, CCMovieList ml, FSPath path, PartialMediaInfo r, MediaInfoResultHandler h) {
		super(ml);

		initComponents();
		postInit(path, r, h);

		setLocationRelativeTo(owner);
	}

	public EditMediaInfoDialog(Component owner, CCMovieList ml, FSPath path, MediaInfoResultHandler h) {
		super(ml);

		initComponents();
		postInit(path, null, h);

		setLocationRelativeTo(owner);
	}

	private void postInit(FSPath path, PartialMediaInfo r, MediaInfoResultHandler h)
	{
		colOK = lblVideoBitdepth.getForeground();
		colErr = Color.RED;

		_handler = h;

		_results = new MIDialogResultSet[]
		{
			new MIDialogResultSet(this, MetadataSourceType.MEDIAINFO,    btnRunMediaInfo,   btnShowMediaInfo,   btnHintsMediaInfo,   btnApplyMediaInfo),
			new MIDialogResultSet(this, MetadataSourceType.FFPROBE_FULL, btnRunFFProbeFull, btnShowFFProbeFull, btnHintsFFProbeFull, btnApplyFFProbeFull),
			new MIDialogResultSet(this, MetadataSourceType.FFPROBE_FAST, btnRunFFProbeFast, btnShowFFProbeFast, btnHintsFFProbeFast, btnApplyFFProbeFast),
			new MIDialogResultSet(this, MetadataSourceType.MP4BOX,       btnRunMP4Box,      btnShowMP4Box,      btnHintsMP4Box,      btnApplyMP4Box),
		};

		for (MIDialogResultSet mdrs : _results) mdrs.init();

		edFilepath.setPath(path == null ? FSPath.Empty : path);

		if (r != null)
		{
			MIDialogResultSet rs = CCStreams.iterate(_results).singleOrNull(p -> p.Type == MetadataSourceType.MEDIAINFO);
			rs.updateData(r, null);
			doApply(r, Opt.empty());
			doShowHints(Opt.of(r), MetadataSourceType.MEDIAINFO, Opt.empty());
		}
	}

	public void doShowHints(Opt<PartialMediaInfo> mi, MetadataSourceType typ, Opt<VideoMetadata> vmd) {
		doUpdateEnabled(false);

		if (_hintType == typ && typ != null) {
			typ = null;
			mi = Opt.empty();
		}

		_hintType = typ;

		lblHintCDate.setText(mi.flatMap(p -> p.CDate).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintMDate.setText(mi.flatMap(p -> p.MDate).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintFilesize.setText(mi.flatMap(p -> p.Filesize).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintDuration.setText(mi.flatMap(p -> p.Duration).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintBitrate.setText(mi.flatMap(p -> p.Bitrate).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintVideoFormat.setText(mi.flatMap(p -> p.VideoFormat).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintVideoWidth.setText(mi.flatMap(p -> p.Width).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintVideoHeight.setText(mi.flatMap(p -> p.Height).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintVideoFramerate.setText(mi.flatMap(p -> p.Framerate).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintVideoBitdepth.setText(mi.flatMap(p -> p.Bitdepth).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintVideoFramecount.setText(mi.flatMap(p -> p.Framecount).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintVideoCodec.setText(mi.flatMap(p -> p.VideoCodec).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintAudioFormat.setText(mi.flatMap(p -> p.AudioFormat).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintAudioChannels.setText(mi.flatMap(p -> p.AudioChannels).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintAudioCodec.setText(mi.flatMap(p -> p.AudioCodec).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintAudioSamplerate.setText(mi.flatMap(p -> p.AudioSamplerate).mapOrElse(p -> "("+p+")", Str.Empty));
		lblHintChecksum.setText(mi.flatMap(p -> p.Checksum).mapOrElse(p -> p, Str.Empty));

		// -----------------------------------------------------------

		lblHintCDate.setToolTipText(lblHintCDate.getText());
		lblHintMDate.setToolTipText(lblHintMDate.getText());
		lblHintFilesize.setToolTipText(lblHintFilesize.getText());
		lblHintDuration.setToolTipText(lblHintDuration.getText());
		lblHintBitrate.setToolTipText(lblHintBitrate.getText());
		lblHintVideoFormat.setToolTipText(lblHintVideoFormat.getText());
		lblHintVideoWidth.setToolTipText(lblHintVideoWidth.getText());
		lblHintVideoHeight.setToolTipText(lblHintVideoHeight.getText());
		lblHintVideoFramerate.setToolTipText(lblHintVideoFramerate.getText());
		lblHintVideoBitdepth.setToolTipText(lblHintVideoBitdepth.getText());
		lblHintVideoFramecount.setToolTipText(lblHintVideoFramecount.getText());
		lblHintVideoCodec.setToolTipText(lblHintVideoCodec.getText());
		lblHintAudioFormat.setToolTipText(lblHintAudioFormat.getText());
		lblHintAudioChannels.setToolTipText(lblHintAudioChannels.getText());
		lblHintAudioCodec.setToolTipText(lblHintAudioCodec.getText());
		lblHintAudioSamplerate.setToolTipText(lblHintAudioSamplerate.getText());
		lblHintChecksum.setToolTipText(lblHintChecksum.getText());

		if (vmd.isPresent()) {
			var t1 = CCStreams.iterate(vmd.get().VideoTracks).cast(ITrackMetadata.class);
			var t2 = CCStreams.iterate(vmd.get().AudioTracks).cast(ITrackMetadata.class);
			var t3 = CCStreams.iterate(vmd.get().SubtitleTracks).cast(ITrackMetadata.class);
			tabTracks.setData(t1.append(t2).append(t3).toList());
		} else {
			tabTracks.clearData();
		}
	}

	public void doQuery(MetadataRunner source, MIDialogResultSet set) {
		if (! source.isConfiguredAndRunnable()) {
			DialogHelper.showLocalError(this, "Dialogs.MetaDataSourceNotFound"); //$NON-NLS-1$
			return;
		}

		doUpdateEnabled(true);
		progressBar.setIndeterminate(true);

		final var filename = edFilepath.getPath();
		new Thread(() ->
		{
			try
			{
				var mqr = source.run(filename);
				SwingUtils.invokeLater(() ->
				{
					set.updateData(mqr.toPartialMediaInfo(), mqr);
					doShowHints(Opt.of(mqr.toPartialMediaInfo()), set.Type, Opt.of(mqr));
				});
			}
			catch (IOException e) {
				CCLog.addWarning(e);
				SwingUtils.invokeLater(() -> {
					GenericTextDialog.showText(
							this,
							getTitle(),
							e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e), //$NON-NLS-1$ //$NON-NLS-2$
							false);
				});
			}
			catch (MetadataQueryException e) {
				CCLog.addWarning(e);
				SwingUtils.invokeLater(() -> {
					GenericTextDialog.showText(
							this,
							getTitle(),
							e.getMessage() + "\n\n" + ExceptionUtils.getMessage(e) + "\n\n" + e.MessageLong + "\n\n" + ExceptionUtils.getStackTrace(e), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							false);
				});
			}
			finally
			{
				SwingUtils.invokeLater(() -> {
					doUpdateEnabled(false);
					progressBar.setIndeterminate(false);
				});
			}
		}, "MDATA_" + set.Type.asString().toUpperCase()).start(); //$NON-NLS-1$
	}

	public void doShowOutput(String txt) {
		GenericTextDialog.showText(this, "MediaInfo", txt, true); //$NON-NLS-1$
	}

	public void doApply(PartialMediaInfo mi, Opt<VideoMetadata> vmd) {
		mi.CDate.ifPresent(v -> ctrlCDate.setValue(v));
		mi.MDate.ifPresent(v -> ctrlMDate.setValue(v));
		mi.Checksum.ifPresent(v -> ctrlChecksum.setText(v));
		mi.Filesize.ifPresent(v -> ctrlFilesize.setValue(v.getBytes()));
		mi.Duration.ifPresent(v -> ctrlDuration.setValue(v));
		mi.Bitrate.ifPresent(v -> ctrlBitrate.setValue(v));

		mi.VideoFormat.ifPresent(v -> ctrlVideoFormat.setText(v));
		mi.Width.ifPresent(v -> ctrlVideoWidth.setValue(v));
		mi.Height.ifPresent(v -> ctrlVideoHeight.setValue(v));
		mi.Framerate.ifPresent(v -> ctrlVideoFramerate.setValue(v));
		mi.Bitdepth.ifPresent(v -> ctrlVideoBitdepth.setValue(v));
		mi.Framecount.ifPresent(v -> ctrlVideoFramecount.setValue(v));
		mi.VideoCodec.ifPresent(v -> ctrlVideoCodec.setText(v));

		mi.AudioFormat.ifPresent(v -> ctrlAudioFormat.setText(v));
		mi.AudioChannels.ifPresent(v -> ctrlAudioChannels.setValue(v));
		mi.AudioCodec.ifPresent(v -> ctrlAudioCodec.setText(v));
		mi.AudioSamplerate.ifPresent(v -> ctrlAudioSamplerate.setValue(v));

		if (vmd.isPresent()) {
			var t1 = CCStreams.iterate(vmd.get().VideoTracks).cast(ITrackMetadata.class);
			var t2 = CCStreams.iterate(vmd.get().AudioTracks).cast(ITrackMetadata.class);
			var t3 = CCStreams.iterate(vmd.get().SubtitleTracks).cast(ITrackMetadata.class);
			tabTracks.setData(t1.append(t2).append(t3).toList());
		} else {
			tabTracks.clearData();
		}
	}

	public void doUpdateEnabled(boolean isRunning) {
		for (MIDialogResultSet r : _results) r.updateEnabled(isRunning);
	}

	private CCMediaInfo getData()
	{
		long cdate    = (long)ctrlCDate.getValue();
		long mdate    = (long)ctrlMDate.getValue();
		String chksum = ctrlChecksum.getText();
		long fsize    = (long)ctrlFilesize.getValue();
		double durat  = (double)ctrlDuration.getValue();
		int brate     = (int)ctrlBitrate.getValue();
		String vfmt   = ctrlVideoFormat.getText();
		String afmt   = ctrlAudioFormat.getText();
		int width     = (int)ctrlVideoWidth.getValue();
		int height    = (int)ctrlVideoHeight.getValue();
		double frate  = (double)ctrlVideoFramerate.getValue();
		short bdepth  = (short)ctrlVideoBitdepth.getValue();
		int fcount    = (int)ctrlVideoFramecount.getValue();
		String vcodec = ctrlVideoCodec.getText();
		String acodec = ctrlAudioCodec.getText();
		short achnls  = (short)ctrlAudioChannels.getValue();
		int srate     = (int)ctrlAudioSamplerate.getValue();

		lblGeneralCDate.setForeground(colOK);
		lblGeneralMDate.setForeground(colOK);
		lblGeneralFilesize.setForeground(colOK);
		lblGeneralDuration.setForeground(colOK);
		lblGeneralBitrate.setForeground(colOK);
		lblVideoFormat.setForeground(colOK);
		lblAudioFormat.setForeground(colOK);
		lblVideoWidth.setForeground(colOK);
		lblVideoHeight.setForeground(colOK);
		lblVideoFramerate.setForeground(colOK);
		lblVideoBitdepth.setForeground(colOK);
		lblVideoFramecount.setForeground(colOK);
		lblVideoCodec.setForeground(colOK);
		lblAudioCodec.setForeground(colOK);
		lblAudioChannels.setForeground(colOK);
		lblAudioSamplerate.setForeground(colOK);
		lblChecksum.setForeground(colOK);

		boolean err = false;

		if (cdate <= 0)                     { lblGeneralCDate.setForeground(colErr);    err = true; }
		if (mdate <= 0)                     { lblGeneralMDate.setForeground(colErr);    err = true; }
		if (fsize <= 0)                     { lblGeneralFilesize.setForeground(colErr); err = true; }
		if (durat <= 0)                     { lblGeneralDuration.setForeground(colErr); err = true; }
		if (brate <= 0)                     { lblGeneralBitrate.setForeground(colErr);  err = true; }
		if (Str.isNullOrWhitespace(vfmt))   { lblVideoFormat.setForeground(colErr);     err = true; }
		if (Str.isNullOrWhitespace(afmt))   { lblAudioFormat.setForeground(colErr);     err = true; }
		if (width  <= 0)                    { lblVideoWidth.setForeground(colErr);      err = true; }
		if (height <= 0)                    { lblVideoHeight.setForeground(colErr);     err = true; }
		if (frate  <= 0)                    { lblVideoFramerate.setForeground(colErr);  err = true; }
		if (bdepth <= 0)                    { lblVideoBitdepth.setForeground(colErr);   err = true; }
		if (fcount <= 0)                    { lblVideoFramecount.setForeground(colErr); err = true; }
		if (Str.isNullOrWhitespace(vcodec)) { lblVideoCodec.setForeground(colErr);      err = true; }
		if (Str.isNullOrWhitespace(acodec)) { lblAudioCodec.setForeground(colErr);      err = true; }
		if (achnls <= 0)                    { lblAudioChannels.setForeground(colErr);   err = true; }
		if (srate  <= 0)                    { lblAudioSamplerate.setForeground(colErr); err = true; }
		if (Str.isNullOrWhitespace(chksum)) { lblChecksum.setForeground(colErr);        err = true; }

		if (err) return null;

		return CCMediaInfo.create(cdate, mdate, new CCFileSize(fsize), chksum, durat, brate, vfmt, width, height, frate, bdepth, fcount, vcodec, afmt, achnls, acodec, srate);
	}

	private void onOK(ActionEvent e) {
		CCMediaInfo mi = getData();
		if (mi == null) return;

		if (_handler != null) _handler.onApplyMediaInfo(mi);

		dispose();
	}

	private void ctrlCDateChanged(ChangeEvent e) {
		lblFullCDate.setText(CCDateTime.createFromFileTimestamp((long)ctrlCDate.getValue(), TimeZone.getDefault()).toStringISO());
	}

	private void ctrlMDateChanged(ChangeEvent e) {
		lblFullMDate.setText(CCDateTime.createFromFileTimestamp((long)ctrlMDate.getValue(), TimeZone.getDefault()).toStringISO());
	}

	private void ctrlFilesizeChanged(ChangeEvent e) {
		lblFullFilesize1.setText(FileSizeFormatter.formatPrecise(new CCFileSize((long)ctrlFilesize.getValue())));
	}

	private void ctrlDurationChanged(ChangeEvent e) {
		int m = (int)Math.round(((double)ctrlDuration.getValue())/60);
		lblFullDuration1.setText(TimeIntervallFormatter.formatShort(m));
		lblFullDuration2.setText(TimeIntervallFormatter.format(m));
	}

	private void ctrlBitrateChanged(ChangeEvent e) {
		lblFullBitrate.setText(Str.spacegroupformat((int)Math.round(((int)ctrlBitrate.getValue())/1000.0)) + " kbit/s"); //$NON-NLS-1$
	}

	private void ctrlVideoFramecountChanged(ChangeEvent e) {
		lblFullFramecount.setText(Str.spacegroupformat((int)ctrlVideoFramecount.getValue()));
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		pnlTop = new JPanel();
		edFilepath = new JFSPathTextField();
		btnRunMediaInfo = new MIButton();
		btnShowMediaInfo = new JButton();
		btnHintsMediaInfo = new JButton();
		btnApplyMediaInfo = new JButton();
		progressBar = new JProgressBar();
		btnRunFFProbeFull = new MIButton();
		btnShowFFProbeFull = new JButton();
		btnHintsFFProbeFull = new JButton();
		btnApplyFFProbeFull = new JButton();
		btnRunFFProbeFast = new MIButton();
		btnShowFFProbeFast = new JButton();
		btnHintsFFProbeFast = new JButton();
		btnApplyFFProbeFast = new JButton();
		btnRunMP4Box = new MIButton();
		btnShowMP4Box = new JButton();
		btnHintsMP4Box = new JButton();
		btnApplyMP4Box = new JButton();
		pnlGeneral = new JPanel();
		lblGeneralCDate = new JLabel();
		ctrlCDate = new JSpinner();
		lblHintCDate = new JLabel();
		lblFullCDate = new JLabel();
		lblGeneralMDate = new JLabel();
		ctrlMDate = new JSpinner();
		lblHintMDate = new JLabel();
		lblFullMDate = new JLabel();
		lblGeneralFilesize = new JLabel();
		ctrlFilesize = new JSpinner();
		lblHintFilesize = new JLabel();
		lblFullFilesize1 = new JLabel();
		lblGeneralDuration = new JLabel();
		ctrlDuration = new JSpinner();
		lblHintDuration = new JLabel();
		lblFullDuration1 = new JLabel();
		lblFullDuration2 = new JLabel();
		lblGeneralBitrate = new JLabel();
		ctrlBitrate = new JSpinner();
		lblHintBitrate = new JLabel();
		lblFullBitrate = new JLabel();
		lblChecksum = new JLabel();
		lblHintChecksum = new JLabel();
		ctrlChecksum = new JTextField();
		pnlVideo = new JPanel();
		lblVideoFormat = new JLabel();
		ctrlVideoFormat = new JTextField();
		lblHintVideoFormat = new JLabel();
		lblVideoWidth = new JLabel();
		ctrlVideoWidth = new JSpinner();
		lblHintVideoWidth = new JLabel();
		lblVideoHeight = new JLabel();
		ctrlVideoHeight = new JSpinner();
		lblHintVideoHeight = new JLabel();
		lblVideoFramerate = new JLabel();
		ctrlVideoFramerate = new JSpinner();
		lblHintVideoFramerate = new JLabel();
		lblVideoBitdepth = new JLabel();
		ctrlVideoBitdepth = new JSpinner();
		lblHintVideoBitdepth = new JLabel();
		lblVideoFramecount = new JLabel();
		ctrlVideoFramecount = new JSpinner();
		lblHintVideoFramecount = new JLabel();
		lblFullFramecount = new JLabel();
		lblVideoCodec = new JLabel();
		ctrlVideoCodec = new JTextField();
		lblHintVideoCodec = new JLabel();
		panel1 = new JPanel();
		tabTracks = new MetadataTrackTable(this);
		pnlAudio = new JPanel();
		lblAudioFormat = new JLabel();
		ctrlAudioFormat = new JTextField();
		lblHintAudioFormat = new JLabel();
		lblAudioChannels = new JLabel();
		ctrlAudioChannels = new JSpinner();
		lblHintAudioChannels = new JLabel();
		lblAudioCodec = new JLabel();
		ctrlAudioCodec = new JTextField();
		lblHintAudioCodec = new JLabel();
		lblAudioSamplerate = new JLabel();
		ctrlAudioSamplerate = new JSpinner();
		lblHintAudioSamplerate = new JLabel();
		btnOK = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("EditMediaInfoDialog.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, 2*(0dlu:grow, $lcgap), 0dlu:grow, $ugap", //$NON-NLS-1$
			"$ugap, default, 2*($lgap, default:grow), $lgap, default, $ugap")); //$NON-NLS-1$

		//======== pnlTop ========
		{
			pnlTop.setLayout(new FormLayout(
				"default:grow, 4*($lcgap, default)", //$NON-NLS-1$
				"3*(default, $lgap), default")); //$NON-NLS-1$
			pnlTop.add(edFilepath, CC.xy(1, 1));

			//---- btnRunMediaInfo ----
			btnRunMediaInfo.setMetadataSourceType(MetadataSourceType.MEDIAINFO);
			pnlTop.add(btnRunMediaInfo, CC.xy(3, 1));

			//---- btnShowMediaInfo ----
			btnShowMediaInfo.setText(LocaleBundle.getString("EditMediaInfoDialog.btnShow")); //$NON-NLS-1$
			pnlTop.add(btnShowMediaInfo, CC.xy(5, 1));

			//---- btnHintsMediaInfo ----
			btnHintsMediaInfo.setText(LocaleBundle.getString("EditMediaInfoDialog.btnHints")); //$NON-NLS-1$
			pnlTop.add(btnHintsMediaInfo, CC.xy(7, 1));

			//---- btnApplyMediaInfo ----
			btnApplyMediaInfo.setText(LocaleBundle.getString("UIGeneric.btnApply.text")); //$NON-NLS-1$
			pnlTop.add(btnApplyMediaInfo, CC.xy(9, 1));
			pnlTop.add(progressBar, CC.xy(1, 3, CC.FILL, CC.FILL));

			//---- btnRunFFProbeFull ----
			btnRunFFProbeFull.setMetadataSourceType(MetadataSourceType.FFPROBE_FULL);
			pnlTop.add(btnRunFFProbeFull, CC.xy(3, 3));

			//---- btnShowFFProbeFull ----
			btnShowFFProbeFull.setText(LocaleBundle.getString("EditMediaInfoDialog.btnShow")); //$NON-NLS-1$
			pnlTop.add(btnShowFFProbeFull, CC.xy(5, 3));

			//---- btnHintsFFProbeFull ----
			btnHintsFFProbeFull.setText(LocaleBundle.getString("EditMediaInfoDialog.btnHints")); //$NON-NLS-1$
			pnlTop.add(btnHintsFFProbeFull, CC.xy(7, 3));

			//---- btnApplyFFProbeFull ----
			btnApplyFFProbeFull.setText(LocaleBundle.getString("UIGeneric.btnApply.text")); //$NON-NLS-1$
			pnlTop.add(btnApplyFFProbeFull, CC.xy(9, 3));

			//---- btnRunFFProbeFast ----
			btnRunFFProbeFast.setMetadataSourceType(MetadataSourceType.FFPROBE_FAST);
			pnlTop.add(btnRunFFProbeFast, CC.xy(3, 5));

			//---- btnShowFFProbeFast ----
			btnShowFFProbeFast.setText(LocaleBundle.getString("EditMediaInfoDialog.btnShow")); //$NON-NLS-1$
			pnlTop.add(btnShowFFProbeFast, CC.xy(5, 5));

			//---- btnHintsFFProbeFast ----
			btnHintsFFProbeFast.setText(LocaleBundle.getString("EditMediaInfoDialog.btnHints")); //$NON-NLS-1$
			pnlTop.add(btnHintsFFProbeFast, CC.xy(7, 5));

			//---- btnApplyFFProbeFast ----
			btnApplyFFProbeFast.setText(LocaleBundle.getString("UIGeneric.btnApply.text")); //$NON-NLS-1$
			pnlTop.add(btnApplyFFProbeFast, CC.xy(9, 5));

			//---- btnRunMP4Box ----
			btnRunMP4Box.setMetadataSourceType(MetadataSourceType.MP4BOX);
			pnlTop.add(btnRunMP4Box, CC.xy(3, 7));

			//---- btnShowMP4Box ----
			btnShowMP4Box.setText(LocaleBundle.getString("EditMediaInfoDialog.btnShow")); //$NON-NLS-1$
			pnlTop.add(btnShowMP4Box, CC.xy(5, 7));

			//---- btnHintsMP4Box ----
			btnHintsMP4Box.setText(LocaleBundle.getString("EditMediaInfoDialog.btnHints")); //$NON-NLS-1$
			pnlTop.add(btnHintsMP4Box, CC.xy(7, 7));

			//---- btnApplyMP4Box ----
			btnApplyMP4Box.setText(LocaleBundle.getString("UIGeneric.btnApply.text")); //$NON-NLS-1$
			pnlTop.add(btnApplyMP4Box, CC.xy(9, 7));
		}
		contentPane.add(pnlTop, CC.xywh(2, 2, 5, 1, CC.FILL, CC.FILL));

		//======== pnlGeneral ========
		{
			pnlGeneral.setBorder(new TitledBorder(LocaleBundle.getString("EditMediaInfoDialog.header1"))); //$NON-NLS-1$
			pnlGeneral.setLayout(new FormLayout(
				"$lcgap, default, $lcgap, default:grow, $lcgap, 60dlu, $lcgap", //$NON-NLS-1$
				"13*($lgap, default), $lgap")); //$NON-NLS-1$

			//---- lblGeneralCDate ----
			lblGeneralCDate.setText(LocaleBundle.getString("EditMediaInfoDialog.CDate")); //$NON-NLS-1$
			lblGeneralCDate.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlGeneral.add(lblGeneralCDate, CC.xy(2, 2));

			//---- ctrlCDate ----
			ctrlCDate.setModel(new SpinnerNumberModel(0L, null, null, 1L));
			ctrlCDate.addChangeListener(e -> ctrlCDateChanged(e));
			pnlGeneral.add(ctrlCDate, CC.xy(4, 2));

			//---- lblHintCDate ----
			lblHintCDate.setText("<dynamic>"); //$NON-NLS-1$
			pnlGeneral.add(lblHintCDate, CC.xy(6, 2));

			//---- lblFullCDate ----
			lblFullCDate.setText("<dynamic>"); //$NON-NLS-1$
			pnlGeneral.add(lblFullCDate, CC.xy(4, 4));

			//---- lblGeneralMDate ----
			lblGeneralMDate.setText(LocaleBundle.getString("EditMediaInfoDialog.MDate")); //$NON-NLS-1$
			lblGeneralMDate.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlGeneral.add(lblGeneralMDate, CC.xy(2, 6));

			//---- ctrlMDate ----
			ctrlMDate.setModel(new SpinnerNumberModel(0L, null, null, 1L));
			ctrlMDate.addChangeListener(e -> ctrlMDateChanged(e));
			pnlGeneral.add(ctrlMDate, CC.xy(4, 6));

			//---- lblHintMDate ----
			lblHintMDate.setText("<dynamic>"); //$NON-NLS-1$
			pnlGeneral.add(lblHintMDate, CC.xy(6, 6));

			//---- lblFullMDate ----
			lblFullMDate.setText("<dynamic>"); //$NON-NLS-1$
			pnlGeneral.add(lblFullMDate, CC.xy(4, 8));

			//---- lblGeneralFilesize ----
			lblGeneralFilesize.setText(LocaleBundle.getString("EditMediaInfoDialog.Filesize")); //$NON-NLS-1$
			lblGeneralFilesize.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlGeneral.add(lblGeneralFilesize, CC.xy(2, 10));

			//---- ctrlFilesize ----
			ctrlFilesize.setModel(new SpinnerNumberModel(0L, null, null, 1L));
			ctrlFilesize.addChangeListener(e -> ctrlFilesizeChanged(e));
			pnlGeneral.add(ctrlFilesize, CC.xy(4, 10));

			//---- lblHintFilesize ----
			lblHintFilesize.setText("<dynamic>"); //$NON-NLS-1$
			pnlGeneral.add(lblHintFilesize, CC.xy(6, 10));

			//---- lblFullFilesize1 ----
			lblFullFilesize1.setText("<dynamic>"); //$NON-NLS-1$
			pnlGeneral.add(lblFullFilesize1, CC.xy(4, 12));

			//---- lblGeneralDuration ----
			lblGeneralDuration.setText(LocaleBundle.getString("EditMediaInfoDialog.Duration")); //$NON-NLS-1$
			lblGeneralDuration.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlGeneral.add(lblGeneralDuration, CC.xy(2, 14));

			//---- ctrlDuration ----
			ctrlDuration.setModel(new SpinnerNumberModel(0.0, null, null, 1.0));
			ctrlDuration.addChangeListener(e -> ctrlDurationChanged(e));
			pnlGeneral.add(ctrlDuration, CC.xy(4, 14));

			//---- lblHintDuration ----
			lblHintDuration.setText("<dynamic>"); //$NON-NLS-1$
			pnlGeneral.add(lblHintDuration, CC.xy(6, 14));

			//---- lblFullDuration1 ----
			lblFullDuration1.setText("<dynamic>"); //$NON-NLS-1$
			pnlGeneral.add(lblFullDuration1, CC.xy(4, 16));

			//---- lblFullDuration2 ----
			lblFullDuration2.setText("<dynamic>"); //$NON-NLS-1$
			pnlGeneral.add(lblFullDuration2, CC.xy(4, 18));

			//---- lblGeneralBitrate ----
			lblGeneralBitrate.setText(LocaleBundle.getString("EditMediaInfoDialog.Bitrate")); //$NON-NLS-1$
			lblGeneralBitrate.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlGeneral.add(lblGeneralBitrate, CC.xy(2, 20));

			//---- ctrlBitrate ----
			ctrlBitrate.setModel(new SpinnerNumberModel(0, null, null, 1));
			ctrlBitrate.addChangeListener(e -> ctrlBitrateChanged(e));
			pnlGeneral.add(ctrlBitrate, CC.xy(4, 20));

			//---- lblHintBitrate ----
			lblHintBitrate.setText("<dynamic>"); //$NON-NLS-1$
			pnlGeneral.add(lblHintBitrate, CC.xy(6, 20));

			//---- lblFullBitrate ----
			lblFullBitrate.setText("<dynamic>"); //$NON-NLS-1$
			pnlGeneral.add(lblFullBitrate, CC.xy(4, 22));

			//---- lblChecksum ----
			lblChecksum.setText(LocaleBundle.getString("EditMediaInfoDialog.Checksum")); //$NON-NLS-1$
			lblChecksum.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlGeneral.add(lblChecksum, CC.xy(2, 24));

			//---- lblHintChecksum ----
			lblHintChecksum.setText("<dynamic>"); //$NON-NLS-1$
			pnlGeneral.add(lblHintChecksum, CC.xywh(4, 24, 3, 1));
			pnlGeneral.add(ctrlChecksum, CC.xywh(2, 26, 5, 1));
		}
		contentPane.add(pnlGeneral, CC.xywh(2, 4, 1, 3, CC.FILL, CC.FILL));

		//======== pnlVideo ========
		{
			pnlVideo.setBorder(new TitledBorder(LocaleBundle.getString("EditMediaInfoDialog.header2"))); //$NON-NLS-1$
			pnlVideo.setLayout(new FormLayout(
				"$lcgap, default, $lcgap, default:grow, $lcgap, 60dlu, $lcgap", //$NON-NLS-1$
				"8*($lgap, default), $lgap")); //$NON-NLS-1$

			//---- lblVideoFormat ----
			lblVideoFormat.setText(LocaleBundle.getString("EditMediaInfoDialog.Format")); //$NON-NLS-1$
			lblVideoFormat.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlVideo.add(lblVideoFormat, CC.xy(2, 2));
			pnlVideo.add(ctrlVideoFormat, CC.xy(4, 2));

			//---- lblHintVideoFormat ----
			lblHintVideoFormat.setText("<dynamic>"); //$NON-NLS-1$
			pnlVideo.add(lblHintVideoFormat, CC.xy(6, 2));

			//---- lblVideoWidth ----
			lblVideoWidth.setText(LocaleBundle.getString("EditMediaInfoDialog.Width")); //$NON-NLS-1$
			lblVideoWidth.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlVideo.add(lblVideoWidth, CC.xy(2, 4));
			pnlVideo.add(ctrlVideoWidth, CC.xy(4, 4));

			//---- lblHintVideoWidth ----
			lblHintVideoWidth.setText("<dynamic>"); //$NON-NLS-1$
			pnlVideo.add(lblHintVideoWidth, CC.xy(6, 4));

			//---- lblVideoHeight ----
			lblVideoHeight.setText(LocaleBundle.getString("EditMediaInfoDialog.Height")); //$NON-NLS-1$
			lblVideoHeight.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlVideo.add(lblVideoHeight, CC.xy(2, 6));
			pnlVideo.add(ctrlVideoHeight, CC.xy(4, 6));

			//---- lblHintVideoHeight ----
			lblHintVideoHeight.setText("<dynamic>"); //$NON-NLS-1$
			pnlVideo.add(lblHintVideoHeight, CC.xy(6, 6));

			//---- lblVideoFramerate ----
			lblVideoFramerate.setText(LocaleBundle.getString("EditMediaInfoDialog.Framerate")); //$NON-NLS-1$
			lblVideoFramerate.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlVideo.add(lblVideoFramerate, CC.xy(2, 8));

			//---- ctrlVideoFramerate ----
			ctrlVideoFramerate.setModel(new SpinnerNumberModel(0.0, null, null, 1.0));
			pnlVideo.add(ctrlVideoFramerate, CC.xy(4, 8));

			//---- lblHintVideoFramerate ----
			lblHintVideoFramerate.setText("<dynamic>"); //$NON-NLS-1$
			pnlVideo.add(lblHintVideoFramerate, CC.xy(6, 8));

			//---- lblVideoBitdepth ----
			lblVideoBitdepth.setText(LocaleBundle.getString("EditMediaInfoDialog.Bitdepth")); //$NON-NLS-1$
			lblVideoBitdepth.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlVideo.add(lblVideoBitdepth, CC.xy(2, 10));

			//---- ctrlVideoBitdepth ----
			ctrlVideoBitdepth.setModel(new SpinnerNumberModel((short) 0, null, null, (short) 1));
			pnlVideo.add(ctrlVideoBitdepth, CC.xy(4, 10));

			//---- lblHintVideoBitdepth ----
			lblHintVideoBitdepth.setText("<dynamic>"); //$NON-NLS-1$
			pnlVideo.add(lblHintVideoBitdepth, CC.xy(6, 10));

			//---- lblVideoFramecount ----
			lblVideoFramecount.setText(LocaleBundle.getString("EditMediaInfoDialog.Framecount")); //$NON-NLS-1$
			lblVideoFramecount.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlVideo.add(lblVideoFramecount, CC.xy(2, 12));

			//---- ctrlVideoFramecount ----
			ctrlVideoFramecount.addChangeListener(e -> ctrlVideoFramecountChanged(e));
			pnlVideo.add(ctrlVideoFramecount, CC.xy(4, 12));

			//---- lblHintVideoFramecount ----
			lblHintVideoFramecount.setText("<dynamic>"); //$NON-NLS-1$
			pnlVideo.add(lblHintVideoFramecount, CC.xy(6, 12));

			//---- lblFullFramecount ----
			lblFullFramecount.setText("<dynamic>"); //$NON-NLS-1$
			pnlVideo.add(lblFullFramecount, CC.xy(4, 14));

			//---- lblVideoCodec ----
			lblVideoCodec.setText(LocaleBundle.getString("EditMediaInfoDialog.Codec")); //$NON-NLS-1$
			lblVideoCodec.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlVideo.add(lblVideoCodec, CC.xy(2, 16));
			pnlVideo.add(ctrlVideoCodec, CC.xy(4, 16));

			//---- lblHintVideoCodec ----
			lblHintVideoCodec.setText("<dynamic>"); //$NON-NLS-1$
			pnlVideo.add(lblHintVideoCodec, CC.xy(6, 16));
		}
		contentPane.add(pnlVideo, CC.xy(4, 4, CC.FILL, CC.FILL));

		//======== panel1 ========
		{
			panel1.setBorder(new TitledBorder(LocaleBundle.getString("EditMediaInfoDialog.Tracks"))); //$NON-NLS-1$
			panel1.setLayout(new FormLayout(
				"$lcgap, default:grow, $lcgap", //$NON-NLS-1$
				"$lgap, default:grow, $lcgap")); //$NON-NLS-1$
			panel1.add(tabTracks, CC.xy(2, 2, CC.DEFAULT, CC.FILL));
		}
		contentPane.add(panel1, CC.xywh(6, 4, 1, 3, CC.FILL, CC.FILL));

		//======== pnlAudio ========
		{
			pnlAudio.setBorder(new TitledBorder(LocaleBundle.getString("EditMediaInfoDialog.header3"))); //$NON-NLS-1$
			pnlAudio.setLayout(new FormLayout(
				"$lcgap, default, $lcgap, default:grow, $lcgap, 60dlu, $lcgap", //$NON-NLS-1$
				"4*($lgap, default), $lgap")); //$NON-NLS-1$

			//---- lblAudioFormat ----
			lblAudioFormat.setText(LocaleBundle.getString("EditMediaInfoDialog.Format")); //$NON-NLS-1$
			lblAudioFormat.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlAudio.add(lblAudioFormat, CC.xy(2, 2));
			pnlAudio.add(ctrlAudioFormat, CC.xy(4, 2));

			//---- lblHintAudioFormat ----
			lblHintAudioFormat.setText("<dynamic>"); //$NON-NLS-1$
			pnlAudio.add(lblHintAudioFormat, CC.xy(6, 2));

			//---- lblAudioChannels ----
			lblAudioChannels.setText(LocaleBundle.getString("EditMediaInfoDialog.Channels")); //$NON-NLS-1$
			lblAudioChannels.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlAudio.add(lblAudioChannels, CC.xy(2, 4));

			//---- ctrlAudioChannels ----
			ctrlAudioChannels.setModel(new SpinnerNumberModel((short) 0, null, null, (short) 1));
			pnlAudio.add(ctrlAudioChannels, CC.xy(4, 4));

			//---- lblHintAudioChannels ----
			lblHintAudioChannels.setText("<dynamic>"); //$NON-NLS-1$
			pnlAudio.add(lblHintAudioChannels, CC.xy(6, 4));

			//---- lblAudioCodec ----
			lblAudioCodec.setText(LocaleBundle.getString("EditMediaInfoDialog.Codec")); //$NON-NLS-1$
			lblAudioCodec.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlAudio.add(lblAudioCodec, CC.xy(2, 6));
			pnlAudio.add(ctrlAudioCodec, CC.xy(4, 6));

			//---- lblHintAudioCodec ----
			lblHintAudioCodec.setText("<dynamic>"); //$NON-NLS-1$
			pnlAudio.add(lblHintAudioCodec, CC.xy(6, 6));

			//---- lblAudioSamplerate ----
			lblAudioSamplerate.setText(LocaleBundle.getString("EditMediaInfoDialog.Samplerate")); //$NON-NLS-1$
			lblAudioSamplerate.setHorizontalAlignment(SwingConstants.TRAILING);
			pnlAudio.add(lblAudioSamplerate, CC.xy(2, 8));
			pnlAudio.add(ctrlAudioSamplerate, CC.xy(4, 8));

			//---- lblHintAudioSamplerate ----
			lblHintAudioSamplerate.setText("<dynamic>"); //$NON-NLS-1$
			pnlAudio.add(lblHintAudioSamplerate, CC.xy(6, 8));
		}
		contentPane.add(pnlAudio, CC.xy(4, 6, CC.FILL, CC.FILL));

		//---- btnOK ----
		btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(e -> onOK(e));
		contentPane.add(btnOK, CC.xy(6, 8, CC.RIGHT, CC.DEFAULT));
		setSize(1300, 775);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel pnlTop;
	private JFSPathTextField edFilepath;
	private MIButton btnRunMediaInfo;
	private JButton btnShowMediaInfo;
	private JButton btnHintsMediaInfo;
	private JButton btnApplyMediaInfo;
	private JProgressBar progressBar;
	private MIButton btnRunFFProbeFull;
	private JButton btnShowFFProbeFull;
	private JButton btnHintsFFProbeFull;
	private JButton btnApplyFFProbeFull;
	private MIButton btnRunFFProbeFast;
	private JButton btnShowFFProbeFast;
	private JButton btnHintsFFProbeFast;
	private JButton btnApplyFFProbeFast;
	private MIButton btnRunMP4Box;
	private JButton btnShowMP4Box;
	private JButton btnHintsMP4Box;
	private JButton btnApplyMP4Box;
	private JPanel pnlGeneral;
	private JLabel lblGeneralCDate;
	private JSpinner ctrlCDate;
	private JLabel lblHintCDate;
	private JLabel lblFullCDate;
	private JLabel lblGeneralMDate;
	private JSpinner ctrlMDate;
	private JLabel lblHintMDate;
	private JLabel lblFullMDate;
	private JLabel lblGeneralFilesize;
	private JSpinner ctrlFilesize;
	private JLabel lblHintFilesize;
	private JLabel lblFullFilesize1;
	private JLabel lblGeneralDuration;
	private JSpinner ctrlDuration;
	private JLabel lblHintDuration;
	private JLabel lblFullDuration1;
	private JLabel lblFullDuration2;
	private JLabel lblGeneralBitrate;
	private JSpinner ctrlBitrate;
	private JLabel lblHintBitrate;
	private JLabel lblFullBitrate;
	private JLabel lblChecksum;
	private JLabel lblHintChecksum;
	private JTextField ctrlChecksum;
	private JPanel pnlVideo;
	private JLabel lblVideoFormat;
	private JTextField ctrlVideoFormat;
	private JLabel lblHintVideoFormat;
	private JLabel lblVideoWidth;
	private JSpinner ctrlVideoWidth;
	private JLabel lblHintVideoWidth;
	private JLabel lblVideoHeight;
	private JSpinner ctrlVideoHeight;
	private JLabel lblHintVideoHeight;
	private JLabel lblVideoFramerate;
	private JSpinner ctrlVideoFramerate;
	private JLabel lblHintVideoFramerate;
	private JLabel lblVideoBitdepth;
	private JSpinner ctrlVideoBitdepth;
	private JLabel lblHintVideoBitdepth;
	private JLabel lblVideoFramecount;
	private JSpinner ctrlVideoFramecount;
	private JLabel lblHintVideoFramecount;
	private JLabel lblFullFramecount;
	private JLabel lblVideoCodec;
	private JTextField ctrlVideoCodec;
	private JLabel lblHintVideoCodec;
	private JPanel panel1;
	private MetadataTrackTable tabTracks;
	private JPanel pnlAudio;
	private JLabel lblAudioFormat;
	private JTextField ctrlAudioFormat;
	private JLabel lblHintAudioFormat;
	private JLabel lblAudioChannels;
	private JSpinner ctrlAudioChannels;
	private JLabel lblHintAudioChannels;
	private JLabel lblAudioCodec;
	private JTextField ctrlAudioCodec;
	private JLabel lblHintAudioCodec;
	private JLabel lblAudioSamplerate;
	private JSpinner ctrlAudioSamplerate;
	private JLabel lblHintAudioSamplerate;
	private JButton btnOK;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
