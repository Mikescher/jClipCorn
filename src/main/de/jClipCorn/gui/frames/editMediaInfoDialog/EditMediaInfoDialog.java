package de.jClipCorn.gui.frames.editMediaInfoDialog;

import java.awt.*;

import javax.swing.*;

import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.MediaQueryException;
import de.jClipCorn.util.mediaquery.MediaQueryResult;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import de.jClipCorn.util.mediaquery.MediaQueryResultAudioTrack;
import de.jClipCorn.util.mediaquery.MediaQueryResultVideoTrack;
import de.jClipCorn.util.mediaquery.MediaQueryRunner;

import javax.swing.border.TitledBorder;
import java.io.IOException;

public class EditMediaInfoDialog extends JDialog {
	private static final long serialVersionUID = -9200470525584039395L;
	
	private JTextField edFilepath;
	private JPanel pnlGeneral;
	private JPanel pnlVideo;
	private JPanel pnlAudio;
	private JButton btnOK;
	private JPanel panel_3;
	private JButton btnMediaInfo;
	private JLabel lblGeneralCDate;
	private JSpinner ctrlCDate;
	private JLabel lblHintCDate;
	private JLabel lblGeneralMDate;
	private JLabel lblGeneralFilesize;
	private JLabel lblGeneralDuration;
	private JLabel lblGeneralBitrate;
	private JSpinner ctrlMDate;
	private JSpinner ctrlFilesize;
	private JSpinner ctrlDuration;
	private JSpinner ctrlBitrate;
	private JLabel lblHintMDate;
	private JLabel lblHintFilesize;
	private JLabel lblHintDuration;
	private JLabel lblHintBitrate;
	private JLabel lblVideoFormat;
	private JLabel lblVideoWidth;
	private JLabel lblVideoHeight;
	private JLabel lblVideoFramerate;
	private JLabel lblVideoBitdepth;
	private JLabel lblVideoFramecount;
	private JLabel lblVideoCodec;
	private JLabel lblHintVideoFormat;
	private JLabel lblHintVideoWidth;
	private JLabel lblHintVideoHeight;
	private JLabel lblHintVideoFramerate;
	private JLabel lblHintVideoBitdepth;
	private JLabel lblHintVideoFramecount;
	private JLabel lblHintVideoCodec;
	private JTextField ctrlVideoFormat;
	private JSpinner ctrlVideoWidth;
	private JSpinner ctrlVideoHeight;
	private JSpinner ctrlVideoFramerate;
	private JSpinner ctrlVideoBitdepth;
	private JSpinner ctrlVideoFramecount;
	private JTextField ctrlVideoCodec;
	private JLabel lblAudioFormat;
	private JLabel lblAudioChannels;
	private JLabel lblAudioCodec;
	private JLabel lblAudioSamplerate;
	private JTextField ctrlAudioFormat;
	private JSpinner ctrlAudioChannels;
	private JSpinner ctrlAudioSamplerate;
	private JTextField ctrlAudioCodec;
	private JLabel lblHintAudioFormat;
	private JLabel lblHintAudioChannels;
	private JLabel lblHintAudioCodec;
	private JLabel lblHintAudioSamplerate;
	private JButton btnApply;
	private JButton btnShow;

	private MediaQueryResult _mqData = null;
	private MediaInfoResultHandler _handler = null;

	private Color colOK;
	private Color colErr;

	/**	 
	 * @wbp.parser.constructor
	 */
	@SuppressWarnings("unused")
	private EditMediaInfoDialog(Component owner) {
		super();
		
		initGUI();

		setLocationRelativeTo(owner);
		
		updateHints();
	}

	public EditMediaInfoDialog(Component owner, String path, MediaQueryResult r, MediaInfoResultHandler h) {
		super();
		_mqData = r;
		_handler = h;
		
		initGUI();
		edFilepath.setText(path == null ? Str.Empty : path);

		setLocationRelativeTo(owner);
		
		updateHints();
		setValues(r);
	}

	public EditMediaInfoDialog(Component owner, String path, CCMediaInfo r, MediaInfoResultHandler h) {
		super();
		_handler = h;
		
		initGUI();
		edFilepath.setText(path == null ? Str.Empty : path);

		setLocationRelativeTo(owner);
		
		updateHints();
		setValues(r);
	}

	public EditMediaInfoDialog(Component owner, String path, MediaInfoResultHandler h) {
		super();
		_handler = h;
		
		initGUI();
		edFilepath.setText(path == null ? Str.Empty : path);

		setLocationRelativeTo(owner);
		
		updateHints();
	}
	
	private void initGUI() {
		setModal(true);
		setBounds(100, 100, 1000, 400);
		setTitle(LocaleBundle.getString("EditMediaInfoDialog.title")); //$NON-NLS-1$
		setMinimumSize(new Dimension(750, 350));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(true);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		panel_3 = new JPanel();
		getContentPane().add(panel_3, "2, 2, 5, 1, fill, fill"); //$NON-NLS-1$
		panel_3.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("16dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.PREF_ROWSPEC,}));
		
		edFilepath = new JTextField();
		panel_3.add(edFilepath, "1, 1, fill, center"); //$NON-NLS-1$
		edFilepath.setColumns(10);
		
		btnMediaInfo = new JButton(Resources.ICN_MENUBAR_UPDATECODECDATA.get16x16());
		btnMediaInfo.addActionListener((e) -> queryMediaInfo());
		panel_3.add(btnMediaInfo, "3, 1"); //$NON-NLS-1$
		
		btnApply = new JButton(LocaleBundle.getString("UIGeneric.btnApply.text")); //$NON-NLS-1$
		btnApply.setEnabled(false);
		btnApply.addActionListener((e) -> applyHints());
		
		btnShow = new JButton(LocaleBundle.getString("EditMediaInfoDialog.btnShow")); //$NON-NLS-1$
		btnShow.setEnabled(false);
		btnShow.addActionListener((e) -> showMediaInfo());
		panel_3.add(btnShow, "5, 1, fill, default"); //$NON-NLS-1$
		panel_3.add(btnApply, "7, 1"); //$NON-NLS-1$
		
		pnlGeneral = new JPanel();
		pnlGeneral.setBorder(new TitledBorder(null, LocaleBundle.getString("EditMediaInfoDialog.header1"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(pnlGeneral, "2, 4, fill, fill"); //$NON-NLS-1$
		pnlGeneral.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.PREF_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("60dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		lblGeneralCDate = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.CDate")); //$NON-NLS-1$
		pnlGeneral.add(lblGeneralCDate, "2, 2, right, default"); //$NON-NLS-1$
		
		ctrlCDate = new JSpinner();
		ctrlCDate.setModel(new SpinnerNumberModel(new Long(0), null, null, new Long(1)));
		pnlGeneral.add(ctrlCDate, "4, 2, fill, default"); //$NON-NLS-1$
		
		lblHintCDate = new JLabel(""); //$NON-NLS-1$
		pnlGeneral.add(lblHintCDate, "6, 2, fill, fill"); //$NON-NLS-1$
		
		lblGeneralMDate = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.MDate")); //$NON-NLS-1$
		pnlGeneral.add(lblGeneralMDate, "2, 4, right, default"); //$NON-NLS-1$
		
		ctrlMDate = new JSpinner();
		ctrlMDate.setModel(new SpinnerNumberModel(new Long(0), null, null, new Long(1)));
		pnlGeneral.add(ctrlMDate, "4, 4, fill, default"); //$NON-NLS-1$
		
		lblHintMDate = new JLabel(""); //$NON-NLS-1$
		pnlGeneral.add(lblHintMDate, "6, 4, fill, fill"); //$NON-NLS-1$
		
		lblGeneralFilesize = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Filesize")); //$NON-NLS-1$
		pnlGeneral.add(lblGeneralFilesize, "2, 6, right, default"); //$NON-NLS-1$
		
		ctrlFilesize = new JSpinner();
		ctrlFilesize.setModel(new SpinnerNumberModel(new Long(0), null, null, new Long(1)));
		pnlGeneral.add(ctrlFilesize, "4, 6"); //$NON-NLS-1$
		
		lblHintFilesize = new JLabel(""); //$NON-NLS-1$
		pnlGeneral.add(lblHintFilesize, "6, 6, fill, fill"); //$NON-NLS-1$
		
		lblGeneralDuration = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Duration")); //$NON-NLS-1$
		pnlGeneral.add(lblGeneralDuration, "2, 8, right, default"); //$NON-NLS-1$
		
		ctrlDuration = new JSpinner();
		ctrlDuration.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
		pnlGeneral.add(ctrlDuration, "4, 8"); //$NON-NLS-1$
		
		lblHintDuration = new JLabel(""); //$NON-NLS-1$
		pnlGeneral.add(lblHintDuration, "6, 8, fill, fill"); //$NON-NLS-1$
		
		lblGeneralBitrate = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Bitrate")); //$NON-NLS-1$
		pnlGeneral.add(lblGeneralBitrate, "2, 10, right, default"); //$NON-NLS-1$
		
		ctrlBitrate = new JSpinner();
		pnlGeneral.add(ctrlBitrate, "4, 10"); //$NON-NLS-1$
		
		lblHintBitrate = new JLabel(""); //$NON-NLS-1$
		pnlGeneral.add(lblHintBitrate, "6, 10, fill, fill"); //$NON-NLS-1$
		
		pnlVideo = new JPanel();
		pnlVideo.setBorder(new TitledBorder(null, LocaleBundle.getString("EditMediaInfoDialog.header2"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(pnlVideo, "4, 4, fill, fill"); //$NON-NLS-1$
		pnlVideo.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.PREF_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("60dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		lblVideoFormat = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Format")); //$NON-NLS-1$
		pnlVideo.add(lblVideoFormat, "2, 2, right, default"); //$NON-NLS-1$
		
		ctrlVideoFormat = new JTextField();
		pnlVideo.add(ctrlVideoFormat, "4, 2, fill, default"); //$NON-NLS-1$
		ctrlVideoFormat.setColumns(10);
		
		lblHintVideoFormat = new JLabel(""); //$NON-NLS-1$
		pnlVideo.add(lblHintVideoFormat, "6, 2, fill, fill"); //$NON-NLS-1$
		
		lblVideoWidth = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Width")); //$NON-NLS-1$
		pnlVideo.add(lblVideoWidth, "2, 4, right, default"); //$NON-NLS-1$
		
		ctrlVideoWidth = new JSpinner();
		pnlVideo.add(ctrlVideoWidth, "4, 4"); //$NON-NLS-1$
		
		lblHintVideoWidth = new JLabel(""); //$NON-NLS-1$
		pnlVideo.add(lblHintVideoWidth, "6, 4, fill, fill"); //$NON-NLS-1$
		
		lblVideoHeight = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Height")); //$NON-NLS-1$
		pnlVideo.add(lblVideoHeight, "2, 6, right, default"); //$NON-NLS-1$
		
		ctrlVideoHeight = new JSpinner();
		pnlVideo.add(ctrlVideoHeight, "4, 6"); //$NON-NLS-1$
		
		lblHintVideoHeight = new JLabel(""); //$NON-NLS-1$
		pnlVideo.add(lblHintVideoHeight, "6, 6, fill, fill"); //$NON-NLS-1$
		
		lblVideoFramerate = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Framerate")); //$NON-NLS-1$
		pnlVideo.add(lblVideoFramerate, "2, 8, right, default"); //$NON-NLS-1$
		
		ctrlVideoFramerate = new JSpinner();
		ctrlVideoFramerate.setModel(new SpinnerNumberModel(new Double(0), null, null, new Double(1)));
		pnlVideo.add(ctrlVideoFramerate, "4, 8"); //$NON-NLS-1$
		
		lblHintVideoFramerate = new JLabel(""); //$NON-NLS-1$
		pnlVideo.add(lblHintVideoFramerate, "6, 8, fill, fill"); //$NON-NLS-1$
		
		lblVideoBitdepth = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Bitdepth")); //$NON-NLS-1$
		pnlVideo.add(lblVideoBitdepth, "2, 10, right, default"); //$NON-NLS-1$
		
		ctrlVideoBitdepth = new JSpinner();
		ctrlVideoBitdepth.setModel(new SpinnerNumberModel(new Short((short) 0), null, null, new Short((short) 1)));
		pnlVideo.add(ctrlVideoBitdepth, "4, 10"); //$NON-NLS-1$
		
		lblHintVideoBitdepth = new JLabel(""); //$NON-NLS-1$
		pnlVideo.add(lblHintVideoBitdepth, "6, 10, fill, fill"); //$NON-NLS-1$
		
		lblVideoFramecount = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Framecount")); //$NON-NLS-1$
		pnlVideo.add(lblVideoFramecount, "2, 12, right, default"); //$NON-NLS-1$
		
		ctrlVideoFramecount = new JSpinner();
		pnlVideo.add(ctrlVideoFramecount, "4, 12"); //$NON-NLS-1$
		
		lblHintVideoFramecount = new JLabel(""); //$NON-NLS-1$
		pnlVideo.add(lblHintVideoFramecount, "6, 12, fill, fill"); //$NON-NLS-1$
		
		lblVideoCodec = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Codec")); //$NON-NLS-1$
		pnlVideo.add(lblVideoCodec, "2, 14, right, default"); //$NON-NLS-1$
		
		ctrlVideoCodec = new JTextField();
		pnlVideo.add(ctrlVideoCodec, "4, 14, fill, default"); //$NON-NLS-1$
		ctrlVideoCodec.setColumns(10);
		
		lblHintVideoCodec = new JLabel(""); //$NON-NLS-1$
		pnlVideo.add(lblHintVideoCodec, "6, 14, fill, fill"); //$NON-NLS-1$
		
		pnlAudio = new JPanel();
		pnlAudio.setBorder(new TitledBorder(null, LocaleBundle.getString("EditMediaInfoDialog.header3"), TitledBorder.LEADING, TitledBorder.TOP, null, null)); //$NON-NLS-1$
		getContentPane().add(pnlAudio, "6, 4, fill, fill"); //$NON-NLS-1$
		pnlAudio.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.PREF_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("1dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("60dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		lblAudioFormat = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Format")); //$NON-NLS-1$
		pnlAudio.add(lblAudioFormat, "2, 2, right, default"); //$NON-NLS-1$
		
		ctrlAudioFormat = new JTextField();
		pnlAudio.add(ctrlAudioFormat, "4, 2, fill, default"); //$NON-NLS-1$
		ctrlAudioFormat.setColumns(10);
		
		lblHintAudioFormat = new JLabel(""); //$NON-NLS-1$
		pnlAudio.add(lblHintAudioFormat, "6, 2, fill, fill"); //$NON-NLS-1$
		
		lblAudioChannels = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Channels")); //$NON-NLS-1$
		pnlAudio.add(lblAudioChannels, "2, 4, right, default"); //$NON-NLS-1$
		
		ctrlAudioChannels = new JSpinner();
		ctrlAudioChannels.setModel(new SpinnerNumberModel(new Short((short) 0), null, null, new Short((short) 1)));
		pnlAudio.add(ctrlAudioChannels, "4, 4"); //$NON-NLS-1$
		
		lblHintAudioChannels = new JLabel(""); //$NON-NLS-1$
		pnlAudio.add(lblHintAudioChannels, "6, 4, fill, fill"); //$NON-NLS-1$
		
		lblAudioCodec = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Codec")); //$NON-NLS-1$
		pnlAudio.add(lblAudioCodec, "2, 6, right, default"); //$NON-NLS-1$
		
		ctrlAudioCodec = new JTextField();
		pnlAudio.add(ctrlAudioCodec, "4, 6, fill, default"); //$NON-NLS-1$
		ctrlAudioCodec.setColumns(10);
		
		lblHintAudioCodec = new JLabel(""); //$NON-NLS-1$
		pnlAudio.add(lblHintAudioCodec, "6, 6, fill, fill"); //$NON-NLS-1$
		
		lblAudioSamplerate = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Samplerate")); //$NON-NLS-1$
		pnlAudio.add(lblAudioSamplerate, "2, 8, right, default"); //$NON-NLS-1$
		
		ctrlAudioSamplerate = new JSpinner();
		pnlAudio.add(ctrlAudioSamplerate, "4, 8"); //$NON-NLS-1$
		
		lblHintAudioSamplerate = new JLabel(""); //$NON-NLS-1$
		pnlAudio.add(lblHintAudioSamplerate, "6, 8, fill, fill"); //$NON-NLS-1$
		
		btnOK = new JButton("OK"); //$NON-NLS-1$
		btnOK.addActionListener(e -> onOK());
		getContentPane().add(btnOK, "6, 6, right, default"); //$NON-NLS-1$

		colOK = lblVideoBitdepth.getForeground();
		colErr = Color.RED;
	}

	private void updateHints() {
		btnApply.setEnabled(_mqData != null);
		btnShow.setEnabled(_mqData != null);

		if (_mqData != null && _mqData.CDate != -1) lblHintCDate.setText("("+_mqData.CDate+")"); else lblHintCDate.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (_mqData != null && _mqData.MDate != -1) lblHintMDate.setText("("+_mqData.MDate+")"); else lblHintMDate.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (_mqData != null && _mqData.FileSize != -1) lblHintFilesize.setText("("+_mqData.FileSize+")"); else lblHintFilesize.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (_mqData != null && _mqData.Duration != -1) lblHintDuration.setText("("+_mqData.Duration+")"); else lblHintDuration.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (_mqData != null && _mqData.getTotalBitrate() != -1) lblHintBitrate.setText("("+_mqData.getTotalBitrate()+")"); else lblHintBitrate.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$

		MediaQueryResultVideoTrack video = null;
		if (_mqData != null) video = _mqData.getDefaultVideoTrack();

		if (video != null && video.Format != null) lblHintVideoFormat.setText("("+video.Format+")"); else lblHintVideoFormat.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (video != null && video.Width != -1) lblHintVideoWidth.setText("("+video.Width+")"); else lblHintVideoWidth.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (video != null && video.Height != -1) lblHintVideoHeight.setText("("+video.Height+")"); else lblHintVideoHeight.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (video != null && video.FrameRate != -1) lblHintVideoFramerate.setText("("+video.FrameRate+")"); else lblHintVideoFramerate.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (video != null && video.BitDepth != -1) lblHintVideoBitdepth.setText("("+video.BitDepth+")"); else lblHintVideoBitdepth.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (video != null && video.FrameCount != -1) lblHintVideoFramecount.setText("("+video.FrameCount+")"); else lblHintVideoFramecount.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (video != null && video.CodecID != null) lblHintVideoCodec.setText("("+video.CodecID+")"); else lblHintVideoCodec.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$

		MediaQueryResultAudioTrack audio = null;
		if (_mqData != null) audio = _mqData.getDefaultAudioTrack();

		if (audio != null && audio.Format != null) lblHintAudioFormat.setText("("+audio.Format+")"); else lblHintAudioFormat.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (audio != null && audio.Channels != -1) lblHintAudioChannels.setText("("+audio.Channels+")"); else lblHintAudioChannels.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (audio != null && audio.CodecID != null) lblHintAudioCodec.setText("("+audio.CodecID+")"); else lblHintAudioCodec.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$
		if (audio != null && audio.Samplingrate != -1) lblHintAudioSamplerate.setText("("+audio.Samplingrate+")"); else lblHintAudioSamplerate.setText(Str.Empty); //$NON-NLS-1$ //$NON-NLS-2$

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
	}
	
	private void showMediaInfo() {
		if (_mqData == null) return;
		GenericTextDialog.showText(this, "MediaInfo", _mqData.Raw, true); //$NON-NLS-1$
	}

	private void queryMediaInfo() {
		btnMediaInfo.setEnabled(false);
		btnApply.setEnabled(false);
		btnShow.setEnabled(false);

		final String filename = edFilepath.getText();
		new Thread(() ->
		{
			try
			{
				MediaQueryResult mqr = MediaQueryRunner.query(filename);
				SwingUtilities.invokeLater(() ->
				{
					_mqData = mqr;
					updateHints();
				});
			}
			catch (MediaQueryException | IOException e) {
				CCLog.addError(e);
			}
			finally
			{
				SwingUtilities.invokeLater(() ->
				{
					btnMediaInfo.setEnabled(true);
					btnApply.setEnabled(_mqData != null);
					btnShow.setEnabled(_mqData != null);
				});
			}
		}, "MQUERY").start(); //$NON-NLS-1$
	}

	private void applyHints() {
		if (_mqData == null) return;

		if (_mqData.CDate != -1) ctrlCDate.setValue(_mqData.CDate);
		if (_mqData.MDate != -1) ctrlMDate.setValue(_mqData.MDate);
		if (_mqData.FileSize != -1) ctrlFilesize.setValue(_mqData.FileSize);
		if (_mqData.Duration != -1) ctrlDuration.setValue(_mqData.Duration);
		if (_mqData.getTotalBitrate() != -1) ctrlBitrate.setValue(_mqData.getTotalBitrate());

		MediaQueryResultVideoTrack video = _mqData.getDefaultVideoTrack();
		if (video != null)
		{
			if (video.Format != null) ctrlVideoFormat.setText(video.Format);
			if (video.Width != -1) ctrlVideoWidth.setValue(video.Width);
			if (video.Height != -1) ctrlVideoHeight.setValue(video.Height);
			if (video.FrameRate != -1) ctrlVideoFramerate.setValue(video.FrameRate);
			if (video.BitDepth != -1) ctrlVideoBitdepth.setValue(video.BitDepth);
			if (video.FrameCount != -1) ctrlVideoFramecount.setValue(video.FrameCount);
			if (video.CodecID != null) ctrlVideoCodec.setText(video.CodecID);
		}

		MediaQueryResultAudioTrack audio = _mqData.getDefaultAudioTrack();
		if (audio != null)
		{
			if (audio.Format != null) ctrlAudioFormat.setText(audio.Format);
			if (audio.Channels != -1) ctrlAudioChannels.setValue(audio.Channels);
			if (audio.CodecID != null) ctrlAudioCodec.setText(audio.CodecID);
			if (audio.Samplingrate != -1) ctrlAudioSamplerate.setValue(audio.Samplingrate);
		}
	}

	private CCMediaInfo getData()
	{
		long cdate    = (long)ctrlCDate.getValue();
		long mdate    = (long)ctrlMDate.getValue();
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

		boolean err = false;

		if (cdate <= 0) { lblGeneralCDate.setForeground(colErr); err = true; }
		if (mdate <= 0) { lblGeneralMDate.setForeground(colErr); err = true; }
		if (fsize <= 0) { lblGeneralFilesize.setForeground(colErr); err = true; }
		if (durat <= 0) { lblGeneralDuration.setForeground(colErr); err = true; }
		if (brate <= 0) { lblGeneralBitrate.setForeground(colErr); err = true; }
		if (Str.isNullOrWhitespace(vfmt)) { lblVideoFormat.setForeground(colErr); err = true; }
		if (Str.isNullOrWhitespace(afmt)) { lblAudioFormat.setForeground(colErr); err = true; }
		if (width <= 0)  { lblVideoWidth.setForeground(colErr); err = true; }
		if (height <= 0) { lblVideoHeight.setForeground(colErr); err = true; }
		if (frate <= 0)  { lblVideoFramerate.setForeground(colErr); err = true; }
		if (bdepth <= 0) { lblVideoBitdepth.setForeground(colErr); err = true; }
		if (fcount <= 0) { lblVideoFramecount.setForeground(colErr); err = true; }
		if (Str.isNullOrWhitespace(vcodec)) { lblVideoCodec.setForeground(colErr); err = true; }
		if (Str.isNullOrWhitespace(acodec)) { lblAudioCodec.setForeground(colErr); err = true; }
		if (achnls <= 0) { lblAudioChannels.setForeground(colErr); err = true; }
		if (srate <= 0)  { lblAudioSamplerate.setForeground(colErr); err = true; }

		if (err) return null;

		return new CCMediaInfo(cdate, mdate, fsize, durat, brate, vfmt, width, height, frate, bdepth, fcount, vcodec, afmt, achnls, acodec, srate);
	}

	private void onOK() {
		CCMediaInfo mi = getData();
		if (mi == null) return;

		if (_handler != null) _handler.onApplyMediaInfo(mi);

		dispose();
	}

	private void setValues(CCMediaInfo mi)
	{
		ctrlCDate.setValue(mi.getCDate());
		ctrlMDate.setValue(mi.getMDate());
		ctrlFilesize.setValue(mi.getFilesize());
		ctrlDuration.setValue(mi.getDuration());
		ctrlBitrate.setValue(mi.getBitrate());
		ctrlVideoFormat.setText(mi.getVideoFormat());
		ctrlAudioFormat.setText(mi.getAudioFormat());
		ctrlVideoWidth.setValue(mi.getWidth());
		ctrlVideoHeight.setValue(mi.getHeight());
		ctrlVideoFramerate.setValue(mi.getFramerate());
		ctrlVideoBitdepth.setValue(mi.getBitdepth());
		ctrlVideoFramecount.setValue(mi.getFramecount());
		ctrlVideoCodec.setText(mi.getVideoCodec());
		ctrlAudioCodec.setText(mi.getAudioCodec());
		ctrlAudioChannels.setValue(mi.getAudioChannels());
		ctrlAudioSamplerate.setValue(mi.getAudioSamplerate());
	}

	private void setValues(MediaQueryResult data)
	{
		if (data == null) return;

		if (data.CDate != -1) ctrlCDate.setValue(data.CDate);
		if (data.MDate != -1) ctrlMDate.setValue(data.MDate);
		if (data.FileSize != -1) ctrlFilesize.setValue(data.FileSize);
		if (data.Duration != -1) ctrlDuration.setValue(data.Duration);
		if (data.getTotalBitrate() != -1) ctrlBitrate.setValue(data.getTotalBitrate());

		MediaQueryResultVideoTrack video = data.getDefaultVideoTrack();
		if (video != null)
		{
			if (video.Format != null) ctrlVideoFormat.setText(video.Format);
			if (video.Width != -1) ctrlVideoWidth.setValue(video.Width);
			if (video.Height != -1) ctrlVideoHeight.setValue(video.Height);
			if (video.FrameRate != -1) ctrlVideoFramerate.setValue(video.FrameRate);
			if (video.BitDepth != -1) ctrlVideoBitdepth.setValue(video.BitDepth);
			if (video.FrameCount != -1) ctrlVideoFramecount.setValue(video.FrameCount);
			if (video.CodecID != null) ctrlVideoCodec.setText(video.CodecID);
		}

		MediaQueryResultAudioTrack audio = data.getDefaultAudioTrack();
		if (audio != null)
		{
			if (audio.Format != null) ctrlAudioFormat.setText(audio.Format);
			if (audio.Channels != -1) ctrlAudioChannels.setValue(audio.Channels);
			if (audio.CodecID != null) ctrlAudioCodec.setText(audio.CodecID);
			if (audio.Samplingrate != -1) ctrlAudioSamplerate.setValue(audio.Samplingrate);
		}
	}

}
