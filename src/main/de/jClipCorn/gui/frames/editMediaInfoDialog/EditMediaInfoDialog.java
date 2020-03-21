package de.jClipCorn.gui.frames.editMediaInfoDialog;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.MetadataSource;
import de.jClipCorn.features.metadata.MetadataSourceType;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.adapter.ChangeLambdaAdapter;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.util.TimeZone;

public class EditMediaInfoDialog extends JDialog {
	private static final long serialVersionUID = -9200470525584039395L;
	
	private JTextField edFilepath;
	private JPanel pnlGeneral;
	private JPanel pnlVideo;
	private JPanel pnlAudio;
	private JButton btnOK;
	private JPanel panel_3;
	private JButton btnRunMediaInfo;
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
	private JButton btnApplyMediaInfo;
	private JButton btnShowMediaInfo;
	private JLabel lblFullCDate;
	private JLabel lblFullMDate;
	private JLabel lblFullFilesize1;
	private JLabel lblFullDuration1;
	private JLabel lblFullDuration2;
	private JLabel lblFullBitrate;
	private JLabel lblFullFramecount;
	private JButton btnRunFFProbe;
	private JButton btnShowFFProbe;
	private JButton btnApplyFFProbe;
	private JProgressBar progressBar;
	private JButton btnRunFFMpeg;
	private JButton btnShowFFMpeg;
	private JButton btnApplyFFMpeg;
	private JButton btnHintsMediaInfo;
	private JButton btnHintsFFProbe;
	private JButton btnHintsFFMpeg;
	private JButton btnRunMP4Box;
	private JButton btnShowMP4Box;
	private JButton btnHintsMP4Box;
	private JButton btnApplyMP4Box;

	private MIDialogResultSet[] _results = new MIDialogResultSet[0];
	private MediaInfoResultHandler _handler = null;
	private MetadataSourceType _hintType = null;

	private Color colOK;
	private Color colErr;

	/**	 
	 * @wbp.parser.constructor
	 */
	@SuppressWarnings("unused")
	private EditMediaInfoDialog(Component owner) {
		super();
		
		initGUI();

		initSources();
		
		setLocationRelativeTo(owner);
		
		doShowHints(Opt.empty(), null);
	}

	public EditMediaInfoDialog(Component owner, String path, MediaQueryResult r, MediaInfoResultHandler h) {
		super();
		_handler = h;
		
		initGUI();
		edFilepath.setText(path == null ? Str.Empty : path);

		initSources();

		setLocationRelativeTo(owner);

		MIDialogResultSet rs = CCStreams.iterate(_results).singleOrNull(p -> p.Type == MetadataSourceType.MEDIAINFO);
		rs.updateData(r.toPartial());
		doApply(r.toPartial());
		doShowHints(Opt.of(r.toPartial()), MetadataSourceType.MEDIAINFO);
	}

	public EditMediaInfoDialog(Component owner, String path, CCMediaInfo r, MediaInfoResultHandler h) {
		super();
		_handler = h;
		
		initGUI();
		edFilepath.setText(path == null ? Str.Empty : path);

		initSources();

		setLocationRelativeTo(owner);

		MIDialogResultSet rs = CCStreams.iterate(_results).singleOrNull(p -> p.Type == MetadataSourceType.MEDIAINFO);
		rs.updateData(r.toPartial());
		doApply(r.toPartial());
		doShowHints(Opt.of(r.toPartial()), MetadataSourceType.MEDIAINFO);
	}

	public EditMediaInfoDialog(Component owner, String path, MediaInfoResultHandler h) {
		super();
		_handler = h;
		
		initGUI();
		edFilepath.setText(path == null ? Str.Empty : path);

		initSources();

		setLocationRelativeTo(owner);

		doShowHints(Opt.empty(), null);
	}

	private void initSources() {
		_results = new MIDialogResultSet[]
		{
			new MIDialogResultSet(this, MetadataSourceType.MEDIAINFO, btnRunMediaInfo, btnShowMediaInfo, btnHintsMediaInfo, btnApplyMediaInfo),
			new MIDialogResultSet(this, MetadataSourceType.FFPROBE,   btnRunFFProbe,   btnShowFFProbe,   btnHintsFFProbe,   btnApplyFFProbe),
			new MIDialogResultSet(this, MetadataSourceType.FFMPEG,    btnRunFFMpeg,    btnShowFFMpeg,    btnHintsFFMpeg,    btnApplyFFMpeg),
			new MIDialogResultSet(this, MetadataSourceType.MP4BOX,    btnRunMP4Box,    btnShowMP4Box,    btnHintsMP4Box,    btnApplyMP4Box),
		};
		
		for (MIDialogResultSet r : _results) r.init();
	}
	
	private void initGUI() {
		setModal(true);
		setBounds(100, 100, 1000, 537);
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
				ColumnSpec.decode("max(30dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(30dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(30dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(30dlu;default)"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("max(12dlu;pref)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(12dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(12dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(12dlu;default)"),})); //$NON-NLS-1$
		
		edFilepath = new JTextField();
		panel_3.add(edFilepath, "1, 1, fill, center"); //$NON-NLS-1$
		edFilepath.setColumns(10);
		
		btnRunMediaInfo = new JButton();
		panel_3.add(btnRunMediaInfo, "3, 1, fill, fill"); //$NON-NLS-1$
		
		btnApplyMediaInfo = new JButton();

		btnShowMediaInfo = new JButton();
		panel_3.add(btnShowMediaInfo, "5, 1, fill, fill"); //$NON-NLS-1$
		
		btnHintsMediaInfo = new JButton();
		panel_3.add(btnHintsMediaInfo, "7, 1, fill, fill"); //$NON-NLS-1$
		panel_3.add(btnApplyMediaInfo, "9, 1, fill, fill"); //$NON-NLS-1$
		
		btnRunFFProbe = new JButton();

		progressBar = new JProgressBar();
		panel_3.add(progressBar, "1, 3, fill, fill"); //$NON-NLS-1$
		panel_3.add(btnRunFFProbe, "3, 3, default, fill"); //$NON-NLS-1$
		
		btnShowFFProbe = new JButton();
		panel_3.add(btnShowFFProbe, "5, 3, fill, fill"); //$NON-NLS-1$
		
		btnApplyFFProbe = new JButton();

		btnHintsFFProbe = new JButton();
		panel_3.add(btnHintsFFProbe, "7, 3, fill, fill"); //$NON-NLS-1$
		panel_3.add(btnApplyFFProbe, "9, 3, fill, fill"); //$NON-NLS-1$
		
		btnRunFFMpeg = new JButton();
		panel_3.add(btnRunFFMpeg, "3, 5, default, fill"); //$NON-NLS-1$
		
		btnShowFFMpeg = new JButton();
		panel_3.add(btnShowFFMpeg, "5, 5, fill, fill"); //$NON-NLS-1$
		
		btnHintsFFMpeg = new JButton();
		panel_3.add(btnHintsFFMpeg, "7, 5, fill, fill"); //$NON-NLS-1$
		
		btnApplyFFMpeg = new JButton();
		panel_3.add(btnApplyFFMpeg, "9, 5, fill, fill"); //$NON-NLS-1$
		
		btnRunMP4Box = new JButton();
		panel_3.add(btnRunMP4Box, "3, 7, default, fill"); //$NON-NLS-1$
		
		btnShowMP4Box = new JButton();
		panel_3.add(btnShowMP4Box, "5, 7, fill, fill"); //$NON-NLS-1$
		
		btnHintsMP4Box = new JButton();
		panel_3.add(btnHintsMP4Box, "7, 7, fill, fill"); //$NON-NLS-1$
		
		btnApplyMP4Box = new JButton();
		panel_3.add(btnApplyMP4Box, "9, 7, fill, fill"); //$NON-NLS-1$
		
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
				FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		lblGeneralCDate = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.CDate")); //$NON-NLS-1$
		pnlGeneral.add(lblGeneralCDate, "2, 2, right, default"); //$NON-NLS-1$
		
		ctrlCDate = new JSpinner();
		ctrlCDate.setModel(new SpinnerNumberModel(0L, null, null, 1L));
		ctrlCDate.addChangeListener(new ChangeLambdaAdapter(() -> lblFullCDate.setText(CCDateTime.createFromFileTimestamp((long)ctrlCDate.getValue(), TimeZone.getDefault()).toStringISO())));
		pnlGeneral.add(ctrlCDate, "4, 2, fill, default"); //$NON-NLS-1$
		
		lblHintCDate = new JLabel(""); //$NON-NLS-1$
		pnlGeneral.add(lblHintCDate, "6, 2, fill, fill"); //$NON-NLS-1$
		
		lblFullCDate = new JLabel(""); //$NON-NLS-1$
		lblFullCDate.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlGeneral.add(lblFullCDate, "4, 4, fill, fill"); //$NON-NLS-1$
		
		lblGeneralMDate = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.MDate")); //$NON-NLS-1$
		pnlGeneral.add(lblGeneralMDate, "2, 6, right, default"); //$NON-NLS-1$
		
		ctrlMDate = new JSpinner();
		ctrlMDate.setModel(new SpinnerNumberModel(0L, null, null, 1L));
		ctrlMDate.addChangeListener(new ChangeLambdaAdapter(() -> lblFullMDate.setText(CCDateTime.createFromFileTimestamp((long)ctrlMDate.getValue(), TimeZone.getDefault()).toStringISO())));
		pnlGeneral.add(ctrlMDate, "4, 6, fill, default"); //$NON-NLS-1$
		
		lblHintMDate = new JLabel(""); //$NON-NLS-1$
		pnlGeneral.add(lblHintMDate, "6, 6, fill, fill"); //$NON-NLS-1$
		
		lblFullMDate = new JLabel(""); //$NON-NLS-1$
		lblFullMDate.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlGeneral.add(lblFullMDate, "4, 8, fill, fill"); //$NON-NLS-1$
		
		lblGeneralFilesize = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Filesize")); //$NON-NLS-1$
		pnlGeneral.add(lblGeneralFilesize, "2, 10, right, default"); //$NON-NLS-1$
		
		ctrlFilesize = new JSpinner();
		ctrlFilesize.setModel(new SpinnerNumberModel(0L, null, null, 1L));
		ctrlFilesize.addChangeListener(new ChangeLambdaAdapter(() -> lblFullFilesize1.setText(FileSizeFormatter.formatPrecise((long)ctrlFilesize.getValue()))));
		pnlGeneral.add(ctrlFilesize, "4, 10"); //$NON-NLS-1$
		
		lblHintFilesize = new JLabel(""); //$NON-NLS-1$
		pnlGeneral.add(lblHintFilesize, "6, 10, fill, fill"); //$NON-NLS-1$
		
		lblFullFilesize1 = new JLabel(""); //$NON-NLS-1$
		lblFullFilesize1.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlGeneral.add(lblFullFilesize1, "4, 12, fill, fill"); //$NON-NLS-1$
		
		lblGeneralDuration = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Duration")); //$NON-NLS-1$
		pnlGeneral.add(lblGeneralDuration, "2, 14, right, default"); //$NON-NLS-1$
		
		ctrlDuration = new JSpinner();
		ctrlDuration.setModel(new SpinnerNumberModel((double) 0, null, null, 1d));
		ctrlDuration.addChangeListener(new ChangeLambdaAdapter(() ->
		{
			int m = (int)Math.round(((double)ctrlDuration.getValue())/60);
			lblFullDuration1.setText(TimeIntervallFormatter.formatShort(m));
			lblFullDuration2.setText(TimeIntervallFormatter.format(m));
		}));
		pnlGeneral.add(ctrlDuration, "4, 14"); //$NON-NLS-1$
		
		lblHintDuration = new JLabel(""); //$NON-NLS-1$
		pnlGeneral.add(lblHintDuration, "6, 14, fill, fill"); //$NON-NLS-1$
		
		lblFullDuration1 = new JLabel(""); //$NON-NLS-1$
		lblFullDuration1.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlGeneral.add(lblFullDuration1, "4, 16, fill, fill"); //$NON-NLS-1$
		
		lblFullDuration2 = new JLabel(""); //$NON-NLS-1$
		lblFullDuration2.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlGeneral.add(lblFullDuration2, "4, 18, fill, fill"); //$NON-NLS-1$
		
		lblGeneralBitrate = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Bitrate")); //$NON-NLS-1$
		pnlGeneral.add(lblGeneralBitrate, "2, 20, right, default"); //$NON-NLS-1$
		
		ctrlBitrate = new JSpinner();
		ctrlBitrate.setModel(new SpinnerNumberModel(0, null, null, 1));
		ctrlBitrate.addChangeListener(new ChangeLambdaAdapter(() -> lblFullBitrate.setText(Str.spacegroupformat((int)Math.round(((int)ctrlBitrate.getValue())/1000.0)) + " kbit/s"))); //$NON-NLS-1$
		pnlGeneral.add(ctrlBitrate, "4, 20"); //$NON-NLS-1$
		
		lblHintBitrate = new JLabel(""); //$NON-NLS-1$
		pnlGeneral.add(lblHintBitrate, "6, 20, fill, fill"); //$NON-NLS-1$
		
		lblFullBitrate = new JLabel(""); //$NON-NLS-1$
		lblFullBitrate.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlGeneral.add(lblFullBitrate, "4, 22, fill, fill"); //$NON-NLS-1$
		
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
				FormSpecs.LABEL_COMPONENT_GAP_ROWSPEC,
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
		ctrlVideoFramerate.setModel(new SpinnerNumberModel((double) 0, null, null, 1d));
		pnlVideo.add(ctrlVideoFramerate, "4, 8"); //$NON-NLS-1$
		
		lblHintVideoFramerate = new JLabel(""); //$NON-NLS-1$
		pnlVideo.add(lblHintVideoFramerate, "6, 8, fill, fill"); //$NON-NLS-1$
		
		lblVideoBitdepth = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Bitdepth")); //$NON-NLS-1$
		pnlVideo.add(lblVideoBitdepth, "2, 10, right, default"); //$NON-NLS-1$
		
		ctrlVideoBitdepth = new JSpinner();
		ctrlVideoBitdepth.setModel(new SpinnerNumberModel((short) 0, null, null, (short) 1));
		pnlVideo.add(ctrlVideoBitdepth, "4, 10"); //$NON-NLS-1$
		
		lblHintVideoBitdepth = new JLabel(""); //$NON-NLS-1$
		pnlVideo.add(lblHintVideoBitdepth, "6, 10, fill, fill"); //$NON-NLS-1$
		
		lblVideoFramecount = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Framecount")); //$NON-NLS-1$
		pnlVideo.add(lblVideoFramecount, "2, 12, right, default"); //$NON-NLS-1$
		
		ctrlVideoFramecount = new JSpinner();
		ctrlVideoFramecount.setModel(new SpinnerNumberModel(0, null, null, 1));
		ctrlVideoFramecount.addChangeListener(new ChangeLambdaAdapter(() -> lblFullFramecount.setText(Str.spacegroupformat((int)ctrlVideoFramecount.getValue()))));
		pnlVideo.add(ctrlVideoFramecount, "4, 12"); //$NON-NLS-1$
		
		lblHintVideoFramecount = new JLabel(""); //$NON-NLS-1$
		pnlVideo.add(lblHintVideoFramecount, "6, 12, fill, fill"); //$NON-NLS-1$
		
		lblFullFramecount = new JLabel(""); //$NON-NLS-1$
		lblFullFramecount.setHorizontalAlignment(SwingConstants.RIGHT);
		pnlVideo.add(lblFullFramecount, "4, 14, fill, fill"); //$NON-NLS-1$
		
		lblVideoCodec = new JLabel(LocaleBundle.getString("EditMediaInfoDialog.Codec")); //$NON-NLS-1$
		pnlVideo.add(lblVideoCodec, "2, 16, right, default"); //$NON-NLS-1$
		
		ctrlVideoCodec = new JTextField();
		pnlVideo.add(ctrlVideoCodec, "4, 16, fill, default"); //$NON-NLS-1$
		ctrlVideoCodec.setColumns(10);
		
		lblHintVideoCodec = new JLabel(""); //$NON-NLS-1$
		pnlVideo.add(lblHintVideoCodec, "6, 16, fill, fill"); //$NON-NLS-1$
		
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
		ctrlAudioChannels.setModel(new SpinnerNumberModel((short) 0, null, null, (short) 1));
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
		
		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(e -> onOK());
		getContentPane().add(btnOK, "6, 6, right, default"); //$NON-NLS-1$

		colOK = lblVideoBitdepth.getForeground();
		colErr = Color.RED;
	}

	public void doShowHints(Opt<PartialMediaInfo> mi, MetadataSourceType typ) {
		doUpdateEnabled(false);

		if (_hintType == typ && typ != null) {
			typ = null;
			mi = Opt.empty();
		}

		_hintType = typ;

		lblHintCDate.setText(mi.flatten(p -> p.CreationDate).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintMDate.setText(mi.flatten(p -> p.ModificationDate).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintFilesize.setText(mi.flatten(p -> p.Filesize).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintDuration.setText(mi.flatten(p -> p.Duration).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintBitrate.setText(mi.flatten(p -> p.Bitrate).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintVideoFormat.setText(mi.flatten(p -> p.VideoFormat).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintVideoWidth.setText(mi.flatten(p -> p.PixelSize).mapOrElse(p -> "("+p.Item1+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintVideoHeight.setText(mi.flatten(p -> p.PixelSize).mapOrElse(p -> "("+p.Item2+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintVideoFramerate.setText(mi.flatten(p -> p.Framerate).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintVideoBitdepth.setText(mi.flatten(p -> p.Bitdepth).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintVideoFramecount.setText(mi.flatten(p -> p.FrameCount).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintVideoCodec.setText(mi.flatten(p -> p.VideoCodec).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintAudioFormat.setText(mi.flatten(p -> p.AudioFormat).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintAudioChannels.setText(mi.flatten(p -> p.AudioChannels).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$
		lblHintAudioSamplerate.setText(mi.flatten(p -> p.AudioCodec).mapOrElse(p -> "("+p+")", Str.Empty)); //$NON-NLS-1$ //$NON-NLS-2$

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
	}

	public void doQuery(MetadataSource source, MIDialogResultSet set) {
		if (! source.isConfiguredAndRunnable()) {
			DialogHelper.showLocalError(this, "Dialogs.MetaDataSourceNotFound"); //$NON-NLS-1$
			return;
		}

		doUpdateEnabled(true);
		progressBar.setIndeterminate(true);

		final String filename = edFilepath.getText();
		new Thread(() ->
		{
			try
			{
				PartialMediaInfo mqr = source.run(filename);
				String raw = source.getFullOutput(filename, mqr);
				SwingUtils.invokeLater(() ->
				{
					set.updateData(mqr, raw);
					doShowHints(Opt.of(mqr), set.Type);
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

	public void doApply(PartialMediaInfo mi) {
		mi.CreationDate.ifPresent(v -> ctrlCDate.setValue(v));
		mi.ModificationDate.ifPresent(v -> ctrlMDate.setValue(v));
		mi.Filesize.ifPresent(v -> ctrlFilesize.setValue(v.getBytes()));
		mi.Duration.ifPresent(v -> ctrlDuration.setValue(v));
		mi.Bitrate.ifPresent(v -> ctrlBitrate.setValue(v));

		mi.VideoFormat.ifPresent(v -> ctrlVideoFormat.setText(v));
		mi.PixelSize.ifPresent(v -> ctrlVideoWidth.setValue(v.Item1));
		mi.PixelSize.ifPresent(v -> ctrlVideoHeight.setValue(v.Item2));
		mi.Framerate.ifPresent(v -> ctrlVideoFramerate.setValue(v));
		mi.Bitdepth.ifPresent(v -> ctrlVideoBitdepth.setValue(v));
		mi.FrameCount.ifPresent(v -> ctrlVideoFramecount.setValue(v));
		mi.VideoCodec.ifPresent(v -> ctrlVideoCodec.setText(v));

		mi.AudioFormat.ifPresent(v -> ctrlAudioFormat.setText(v));
		mi.AudioChannels.ifPresent(v -> ctrlAudioChannels.setValue(v));
		mi.AudioCodec.ifPresent(v -> ctrlAudioCodec.setText(v));
		mi.AudioSamplerate.ifPresent(v -> ctrlAudioSamplerate.setValue(v));
	}

	public void doUpdateEnabled(boolean isRunning) {
		for (MIDialogResultSet r : _results) r.updateEnabled(isRunning);
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

	public String getCurrentInputFilepath() {
		return edFilepath.getText();
	}
}
