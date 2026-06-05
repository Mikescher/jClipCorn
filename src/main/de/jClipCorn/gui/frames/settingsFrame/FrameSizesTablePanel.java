package de.jClipCorn.gui.frames.settingsFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.frames.addSeasonFrame.AddSeasonFrame;
import de.jClipCorn.gui.frames.addSeriesFrame.AddSeriesFrame;
import de.jClipCorn.gui.frames.batchEditFrame.BatchEditFrame;
import de.jClipCorn.gui.frames.coverCropFrame.CoverCropDialog;
import de.jClipCorn.gui.frames.createSeriesFolderStructureFrame.CreateSeriesFolderStructureFrame;
import de.jClipCorn.gui.frames.databaseHistoryFrame.DatabaseHistoryFrame;
import de.jClipCorn.gui.frames.editMovieFrame.EditMovieFrame;
import de.jClipCorn.gui.frames.editScoreFrame.EditScoreFrame;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.frames.logFrame.LogFrame;
import de.jClipCorn.gui.frames.moveSeriesFrame.MoveSeriesDialog;
import de.jClipCorn.gui.frames.parseOnlineFrame.ParseOnlineDialog;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.frames.scanFolderFrame.ScanFolderFrame;
import de.jClipCorn.gui.frames.statisticsFrame.StatisticsFrame;
import de.jClipCorn.gui.frames.watchHistoryFrame.WatchHistoryFrame;
import de.jClipCorn.gui.guiComponents.JCCFrameDimensionInput;
import de.jClipCorn.gui.guiComponents.referenceChooser.ReferenceChooserDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCFrameSizeProperty;
import de.jClipCorn.properties.types.FrameSizeVar;
import de.jClipCorn.properties.types.HostFrameSizeVar;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.DialogHelper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Table-style editor for the per-host frame sizes (see {@link CCFrameSizeProperty} / {@link HostFrameSizeVar}).
 * Rows are the configurable frames, columns are the default value and one column per host.
 */
public class FrameSizesTablePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int MIN_HOST_COLUMNS = 3;

	private final CCProperties properties;

	private final List<CCFrameSizeProperty>     rowProps        = new ArrayList<>();
	private final List<Class<? extends Window>> rowFrameClasses = new ArrayList<>();

	private final List<JTextField>                 hostHeaders   = new ArrayList<>();
	private final List<JCCFrameDimensionInput>     defaultInputs = new ArrayList<>();
	private final List<List<JCCFrameDimensionInput>> hostInputs  = new ArrayList<>(); // [row][hostColumn]

	public FrameSizesTablePanel(CCProperties properties) {
		super();
		this.properties = properties;

		buildRowDefinitions();
		buildGUI();
		loadValues();
	}

	@SuppressWarnings("nls")
	private void buildRowDefinitions() {
		add(properties.PROP_FSIZE_MAINFRAME,                        MainFrame.class);
		add(properties.PROP_FSIZE_ADDMOVIEFRAME,                    AddMovieFrame.class);
		add(properties.PROP_FSIZE_ADDSEASONFRAME,                   AddSeasonFrame.class);
		add(properties.PROP_FSIZE_ADDSERIESFRAME,                   AddSeriesFrame.class);
		add(properties.PROP_FSIZE_BATCHEDITFRAME,                   BatchEditFrame.class);
		add(properties.PROP_FSIZE_COVERCROPFRAME,                   CoverCropDialog.class);
		add(properties.PROP_FSIZE_CREATESERIESFOLDERSTRUCTUREFRAME, CreateSeriesFolderStructureFrame.class);
		add(properties.PROP_FSIZE_DATABASEHISTORYFRAME,            DatabaseHistoryFrame.class);
		add(properties.PROP_FSIZE_EDITMOVIEFRAME,                   EditMovieFrame.class);
		add(properties.PROP_FSIZE_EDITSCOREFRAME,                   EditScoreFrame.class);
		add(properties.PROP_FSIZE_EDITSERIESFRAME,                  EditSeriesFrame.class);
		add(properties.PROP_FSIZE_LOGFRAME,                         LogFrame.class);
		add(properties.PROP_FSIZE_MOVESERIESFRAME,                  MoveSeriesDialog.class);
		add(properties.PROP_FSIZE_PARSEONLINEFRAME,                 ParseOnlineDialog.class);
		add(properties.PROP_FSIZE_PREVIEWSERIESFRAME,               PreviewSeriesFrame.class);
		add(properties.PROP_FSIZE_PREVIEWMOVIEFRAME,                PreviewMovieFrame.class);
		add(properties.PROP_FSIZE_SCANFOLDERFRAME,                  ScanFolderFrame.class);
		add(properties.PROP_FSIZE_SETTINGSFRAME,                    SettingsFrame.class);
		add(properties.PROP_FSIZE_STATISTICSFRAME,                  StatisticsFrame.class);
		add(properties.PROP_FSIZE_WATCHHISTORYFRAME,                WatchHistoryFrame.class);
		add(properties.PROP_FSIZE_REFERENCECHOOSERDIALOG,           ReferenceChooserDialog.class);
	}

	private void add(CCFrameSizeProperty prop, Class<? extends Window> frameClass) {
		rowProps.add(prop);
		rowFrameClasses.add(frameClass);
	}

	private List<String> collectHosts() {
		var hosts = new LinkedHashSet<String>();

		var current = ApplicationHelper.getHostname();
		if (!Str.isNullOrWhitespace(current)) hosts.add(current);

		for (var prop : rowProps) {
			for (var h : prop.getValue().Hosts) {
				if (!Str.isNullOrWhitespace(h.Hostname)) hosts.add(h.Hostname);
			}
		}

		return new ArrayList<>(hosts);
	}

	@SuppressWarnings("nls")
	private void buildGUI() {
		var knownHosts = collectHosts();
		int hostCount  = Math.max(MIN_HOST_COLUMNS, knownHosts.size());

		var cols = new ArrayList<ColumnSpec>();
		cols.add(FormSpecs.RELATED_GAP_COLSPEC);
		cols.add(ColumnSpec.decode("default")); // 2: frame name
		cols.add(FormSpecs.RELATED_GAP_COLSPEC);
		cols.add(ColumnSpec.decode("default")); // 4: default value
		cols.add(FormSpecs.UNRELATED_GAP_COLSPEC);
		for (int j = 0; j < hostCount; j++) {
			cols.add(ColumnSpec.decode("default")); // 6 + j*2: host value
			cols.add(FormSpecs.RELATED_GAP_COLSPEC);
		}
		cols.add(FormSpecs.UNRELATED_GAP_COLSPEC);
		cols.add(ColumnSpec.decode("default")); // reset button
		final int resetCol = 7 + hostCount * 2;

		var rows = new ArrayList<RowSpec>();
		rows.add(FormSpecs.RELATED_GAP_ROWSPEC);
		rows.add(FormSpecs.DEFAULT_ROWSPEC); // 2: header
		for (int i = 0; i < rowProps.size(); i++) {
			rows.add(FormSpecs.RELATED_GAP_ROWSPEC);
			rows.add(FormSpecs.DEFAULT_ROWSPEC); // 4 + i*2: frame row
		}
		rows.add(FormSpecs.RELATED_GAP_ROWSPEC);

		setLayout(new FormLayout(cols.toArray(new ColumnSpec[0]), rows.toArray(new RowSpec[0])));

		// ---- header row ----
		var lblFrame = new JLabel(LocaleBundle.getString("FrameSizesTable.colFrame"));
		lblFrame.setFont(lblFrame.getFont().deriveFont(Font.BOLD));
		add(lblFrame, CC.xy(2, 2));

		var lblDefault = new JLabel(LocaleBundle.getString("FrameSizesTable.colDefault"));
		lblDefault.setFont(lblDefault.getFont().deriveFont(Font.BOLD));
		add(lblDefault, CC.xy(4, 2, CC.CENTER, CC.DEFAULT));

		var currentHost = ApplicationHelper.getHostname();
		for (int j = 0; j < hostCount; j++) {
			var tf = new JTextField(12);
			if (j < knownHosts.size()) tf.setText(knownHosts.get(j));
			if (!Str.isNullOrWhitespace(currentHost) && currentHost.equalsIgnoreCase(tf.getText())) {
				tf.setFont(tf.getFont().deriveFont(Font.BOLD));
				tf.setToolTipText(LocaleBundle.getString("FrameSizesTable.currentHost.tooltip"));
			}
			hostHeaders.add(tf);
			add(tf, CC.xy(6 + j * 2, 2));
		}

		// ---- frame rows ----
		for (int i = 0; i < rowProps.size(); i++) {
			final int rowIdx = i;
			var prop = rowProps.get(i);
			int rowY = 4 + i * 2;

			add(new JLabel(prop.getDescription()), CC.xy(2, rowY));

			var defInput = new JCCFrameDimensionInput(false);
			defInput.setCaptureAction(() -> doCapture(rowIdx, defInput));
			defaultInputs.add(defInput);
			add(defInput, CC.xy(4, rowY, CC.CENTER, CC.DEFAULT));

			var rowInputs = new ArrayList<JCCFrameDimensionInput>();
			for (int j = 0; j < hostCount; j++) {
				var cell = new JCCFrameDimensionInput(true);
				cell.setCaptureAction(() -> doCapture(rowIdx, cell));
				rowInputs.add(cell);
				add(cell, CC.xy(6 + j * 2, rowY));
			}
			hostInputs.add(rowInputs);

			var btnReset = new JButton(LocaleBundle.getString("Settingsframe.btnReset.title")); //$NON-NLS-1$
			btnReset.addActionListener(e -> doResetRow(rowIdx));
			add(btnReset, CC.xy(resetCol, rowY));
		}
	}

	/** Reset a single frame row to its built-in default and clear all host overrides (applied on OK). */
	private void doResetRow(int rowIdx) {
		var std = rowProps.get(rowIdx).getDefault();

		defaultInputs.get(rowIdx).setValue(std.Default, true);
		for (var cell : hostInputs.get(rowIdx)) cell.setValue(std.Default, false);
	}

	/** (Re)load the current property values into all inputs. */
	public void loadValues() {
		for (int i = 0; i < rowProps.size(); i++) {
			var val = rowProps.get(i).getValue();

			defaultInputs.get(i).setValue(val.Default, true);

			var rowInputs = hostInputs.get(i);
			for (int j = 0; j < rowInputs.size(); j++) {
				var header   = hostHeaders.get(j).getText().trim();
				var override = Str.isNullOrWhitespace(header) ? null : val.getForHost(header);
				if (override != null) {
					rowInputs.get(j).setValue(override, true);
				} else {
					rowInputs.get(j).setValue(val.Default, false);
				}
			}
		}
	}

	/**
	 * Write the edited values back to the properties.
	 * @return the identifiers of the properties that actually changed.
	 */
	public List<String> applyValues() {
		var changed = new ArrayList<String>();

		for (int i = 0; i < rowProps.size(); i++) {
			var prop = rowProps.get(i);

			var def = defaultInputs.get(i).getFrameSize();

			var entries = new ArrayList<HostFrameSizeVar.SingleHostFrameSize>();
			var rowInputs = hostInputs.get(i);
			for (int j = 0; j < rowInputs.size(); j++) {
				var header = hostHeaders.get(j).getText().trim();
				if (Str.isNullOrWhitespace(header)) continue;
				if (!rowInputs.get(j).isOverrideEnabled()) continue;
				entries.add(new HostFrameSizeVar.SingleHostFrameSize(header, rowInputs.get(j).getFrameSize()));
			}

			var newVal = new HostFrameSizeVar(def, entries);
			if (prop.setValueIfDiff(newVal).isPresent()) changed.add(prop.getIdentifier());
		}

		return changed;
	}

	private void doCapture(int rowIdx, JCCFrameDimensionInput target) {
		var win = findOpenWindow(rowFrameClasses.get(rowIdx));
		if (win == null) {
			DialogHelper.showLocalError(this, "FrameSizesTable.capture.noframe"); //$NON-NLS-1$
			return;
		}
		target.captureValue(new FrameSizeVar(win.getWidth(), win.getHeight()));
	}

	private static Window findOpenWindow(Class<? extends Window> cls) {
		if (cls == null) return null;
		for (Window w : Window.getWindows()) {
			if (cls.isInstance(w) && w.isShowing()) return w;
		}
		return null;
	}
}
