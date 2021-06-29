package de.jClipCorn.gui.frames.updateCodecFrame;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.helper.ThreadUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateCodecFrame extends JFrame {
	private static final long serialVersionUID = -2163175118745491143L;

	private enum FilterState { ALL, CHANGED, CHANGED_LANG, CHANGED_LEN, CHANGED_LEN_DYN, CHANGED_MEDIAINFO, ELEM_ERROR, ELEM_WARN }
	
	private final CCMovieList movielist;
	private FilterState selectedFilter;
	
	private UpdateCodecTable tableMain;
	private JToggleButton btnShowFiltered;
	private JToggleButton btnShowAll;
	private JToggleButton btnShowFilteredDynLen;
	private JToggleButton btnShowFilteredLang;
	private JToggleButton btnShowFilteredLen;
	private JToggleButton btnShowFilteredErr;
	private JToggleButton btnShowFilteredWarn;
	private JButton btnStartCollectingData;
	private JProgressBar progressBar;
	private JPanel panel;

	private Thread collThread = null;
	private boolean cancelBackground;
	private JButton btnUpdateSelectedLang;
	private JButton btnUpdateSelectedLen;
	private JTextArea textArea;
	private JPanel panel_1;
	private JButton button;
	private JScrollPane scrollPane;
	private JCheckBox cbAutoScroll;
	private JSpinner spinnerDynLen;
	private JPanel panel_2;
	private JLabel lblApproxLength;
	private JToggleButton btnShowFilteredMediaInfo;
	private JButton btnUpdateSelectedMediaInfo;
	
	public UpdateCodecFrame(Component owner, CCMovieList mlist) {
		super();
		setSize(new Dimension(1200, 600));
		movielist = mlist;

		initGUI();
		setLocationRelativeTo(owner);
		
		initTable();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("UpdateCodecFrame.Title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		btnStartCollectingData = new JButton(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect1")); //$NON-NLS-1$
		btnStartCollectingData.addActionListener(this::queryMetadata);
		getContentPane().add(btnStartCollectingData, "2, 2"); //$NON-NLS-1$
		
		progressBar = new JProgressBar();
		getContentPane().add(progressBar, "4, 2, default, fill"); //$NON-NLS-1$
		
		panel_1 = new JPanel();
		getContentPane().add(panel_1, "5, 2, fill, fill"); //$NON-NLS-1$
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		btnShowAll = new JToggleButton("All"); //$NON-NLS-1$
		panel_1.add(btnShowAll, "1, 1"); //$NON-NLS-1$
		btnShowAll.addActionListener(e -> setFiltered(FilterState.ALL, true, true));
		btnShowAll.setSelected(true);
		
		btnShowFiltered = new JToggleButton(LocaleBundle.getString("UpdateCodecFrame.Filter1")); //$NON-NLS-1$
		panel_1.add(btnShowFiltered, "2, 1"); //$NON-NLS-1$
		btnShowFiltered.addActionListener(e -> setFiltered(FilterState.CHANGED, true, true));
		
		btnShowFilteredLang = new JToggleButton(LocaleBundle.getString("UpdateCodecFrame.Filter2")); //$NON-NLS-1$
		panel_1.add(btnShowFilteredLang, "3, 1"); //$NON-NLS-1$
		btnShowFilteredLang.addActionListener(e -> setFiltered(FilterState.CHANGED_LANG, true, true));
		
		btnShowFilteredLen = new JToggleButton(LocaleBundle.getString("UpdateCodecFrame.Filter3")); //$NON-NLS-1$
		panel_1.add(btnShowFilteredLen, "4, 1"); //$NON-NLS-1$
		btnShowFilteredLen.addActionListener(e -> setFiltered(FilterState.CHANGED_LEN, true, true));
		
		btnShowFilteredDynLen = new JToggleButton(LocaleBundle.getString("UpdateCodecFrame.Filter4")); //$NON-NLS-1$
		panel_1.add(btnShowFilteredDynLen, "5, 1"); //$NON-NLS-1$
		btnShowFilteredDynLen.addActionListener(e -> setFiltered(FilterState.CHANGED_LEN_DYN, true, true));
		
		btnShowFilteredMediaInfo = new JToggleButton(LocaleBundle.getString("UpdateCodecFrame.Filter7")); //$NON-NLS-1$
		panel_1.add(btnShowFilteredMediaInfo, "7, 1"); //$NON-NLS-1$
		btnShowFilteredMediaInfo.addActionListener(e -> setFiltered(FilterState.CHANGED_MEDIAINFO, true, true));

		btnShowFilteredErr = new JToggleButton(LocaleBundle.getString("UpdateCodecFrame.Filter5")); //$NON-NLS-1$
		panel_1.add(btnShowFilteredErr, "8, 1"); //$NON-NLS-1$
		btnShowFilteredErr.addActionListener(e -> setFiltered(FilterState.ELEM_ERROR, true, true));

		btnShowFilteredWarn = new JToggleButton(LocaleBundle.getString("UpdateCodecFrame.Filter6")); //$NON-NLS-1$
		panel_1.add(btnShowFilteredWarn, "9, 1"); //$NON-NLS-1$
		btnShowFilteredWarn.addActionListener(e -> setFiltered(FilterState.ELEM_WARN, true, true));
		
		tableMain = new UpdateCodecTable(this);
		getContentPane().add(tableMain, "2, 4, 4, 1, fill, fill"); //$NON-NLS-1$

		
		panel = new JPanel();
		getContentPane().add(panel, "2, 6, 4, 1, fill, fill"); //$NON-NLS-1$
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.PREF_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.PREF_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("16dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("16dlu"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("20dlu"),})); //$NON-NLS-1$
		
		btnUpdateSelectedLang = new JButton(LocaleBundle.getString("UpdateCodecFrame.Button1")); //$NON-NLS-1$
		btnUpdateSelectedLang.setEnabled(false);
		btnUpdateSelectedLang.addActionListener(e -> updateSelectedLanguages(true));
		panel.add(btnUpdateSelectedLang, "1, 1, fill, fill"); //$NON-NLS-1$
		
		btnUpdateSelectedLen = new JButton(LocaleBundle.getString("UpdateCodecFrame.Button2")); //$NON-NLS-1$
		btnUpdateSelectedLen.setEnabled(false);
		btnUpdateSelectedLen.addActionListener(e -> updateSelectedLengths(true));
		
		btnUpdateSelectedMediaInfo = new JButton(LocaleBundle.getString("UpdateCodecFrame.Button3")); //$NON-NLS-1$
		btnUpdateSelectedMediaInfo.setEnabled(false);
		btnUpdateSelectedMediaInfo.addActionListener(e -> updateSelectedMediaInfos(true));
		panel.add(btnUpdateSelectedMediaInfo, "1, 5"); //$NON-NLS-1$
		
		scrollPane = new JScrollPane();
		panel.add(scrollPane, "2, 1, 1, 9, fill, fill"); //$NON-NLS-1$
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);
		panel.add(btnUpdateSelectedLen, "1, 3, fill, fill"); //$NON-NLS-1$
		
		button = new JButton("..."); //$NON-NLS-1$
		button.addActionListener(e ->
		{
			String title = (tableMain.getSelectedElement()==null) ? Str.Empty : tableMain.getSelectedElement().getFullDisplayTitle();
			GenericTextDialog.showText(UpdateCodecFrame.this, title, textArea.getText(), false);
		});
		
		panel_2 = new JPanel();
		panel.add(panel_2, "1, 7, fill, fill"); //$NON-NLS-1$
		panel_2.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.PREF_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		lblApproxLength = new JLabel("Approx. Length (%):"); //$NON-NLS-1$
		panel_2.add(lblApproxLength, "1, 1"); //$NON-NLS-1$
		
		spinnerDynLen = new JSpinner();
		panel_2.add(spinnerDynLen, "3, 1"); //$NON-NLS-1$
		spinnerDynLen.setModel(new SpinnerNumberModel(15.0, 0.0, 100.0, 1));
		spinnerDynLen.addChangeListener(e -> { setFiltered(selectedFilter, true, true); });
		
		cbAutoScroll = new JCheckBox(LocaleBundle.getString("UpdateCodecFrame.CBScroll")); //$NON-NLS-1$
		panel.add(cbAutoScroll, "1, 9, left, bottom"); //$NON-NLS-1$
		panel.add(button, "3, 9, right, bottom"); //$NON-NLS-1$
	}

	public void setSelection(UpdateCodecTableElement element)
	{
		if (element == null) {
			textArea.setText(Str.Empty);
		} else {
			if (!element.Processed) textArea.setText(Str.Empty);
			else if (element.MQError != null) textArea.setText(element.MQError.trim());
			else if (element.MQResultString != null) textArea.setText(element.MQResultString.trim());
			else textArea.setText("Error ???"); //$NON-NLS-1$
		}
		SwingUtils.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
	}

	private void setFiltered(FilterState state, boolean triggerUpdate, boolean force) {
		boolean changed = (selectedFilter != state);
		
		selectedFilter = state;

		btnShowAll.setSelected(state == FilterState.ALL);
		btnShowFiltered.setSelected(state == FilterState.CHANGED);
		btnShowFilteredDynLen.setSelected(state == FilterState.CHANGED_LEN_DYN);
		btnShowFilteredErr.setSelected(state == FilterState.ELEM_ERROR);
		btnShowFilteredLang.setSelected(state == FilterState.CHANGED_LANG);
		btnShowFilteredLen.setSelected(state == FilterState.CHANGED_LEN);
		btnShowFilteredWarn.setSelected(state == FilterState.ELEM_WARN);
		btnShowFilteredMediaInfo.setSelected(state == FilterState.CHANGED_MEDIAINFO);

		if (triggerUpdate && (changed || force)) {
			switch (state) {
			case ALL:
				tableMain.resetFilter();
				break;
			case CHANGED:
				tableMain.setFilter(e -> e.Processed && e.hasDiff(((double)spinnerDynLen.getValue())/100.0));
				break;
			case CHANGED_LANG:
				tableMain.setFilter(e -> e.Processed && e.hasLangDiff());
				break;
			case CHANGED_LEN:
				tableMain.setFilter(e -> e.Processed && e.hasLenDiff(0.0));
				break;
			case CHANGED_MEDIAINFO:
				tableMain.setFilter(e -> e.Processed && e.hasMediaInfoDiff());
				break;
			case CHANGED_LEN_DYN:
				tableMain.setFilter(e -> e.Processed && e.hasLenDiff(((double)spinnerDynLen.getValue())/100.0));
				break;
			case ELEM_ERROR:
				tableMain.setFilter(e -> e.Processed && e.MQError != null);
				break;
			case ELEM_WARN:
				tableMain.setFilter(e -> e.Processed && e.hasDiff(-1) && e.getOldLanguage().ccstream().any(ol -> !e.getNewLanguage().contains(ol)) );
				break;
			}
		}
	}
	
	private void initTable() {
		tableMain.setData(movielist
				.iteratorPlayables()
				.filter(p -> p.format().get() != CCFileFormat.IFO)
				.filter(p -> p.format().get() != CCFileFormat.IMG)
				.map(UpdateCodecTableElement::new)
				.enumerate());

		tableMain.autoResize();
	}
	
	private void queryMetadata(ActionEvent e) {
		if (collThread != null && collThread.isAlive()) {
			cancelBackground = true;
		} else {
			var mqp = CCProperties.getInstance().PROP_PLAY_MEDIAINFO_PATH.getValue();
			if (FSPath.isNullOrEmpty(mqp) || !mqp.fileExists() || !mqp.canExecute()) {
				DialogHelper.showLocalError(this, "Dialogs.MediaInfoNotFound"); //$NON-NLS-1$
				return;
			}

			cancelBackground = false;
			collThread = new Thread(this::run, "THREAD_UPDATE_CODEC_COLLECT"); //$NON-NLS-1$
			collThread.start();
		}
	}
	
	private void run() {
		try {
			List<UpdateCodecTableElement> data = tableMain.getDataCopy();

			SwingUtils.invokeAndWaitSafe(() ->
			{
				btnStartCollectingData.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect2"));  //$NON-NLS-1$
				btnUpdateSelectedLang.setEnabled(false);
				btnUpdateSelectedLen.setEnabled(false);
				btnUpdateSelectedMediaInfo.setEnabled(false);
			});
			ThreadUtils.setProgressbarAndWait(progressBar, 0, 0, data.size() + 1);

			int i = 1;
			for (UpdateCodecTableElement elem : data) {
				ThreadUtils.setProgressbarAndWait(progressBar, i);
				i++;

				if (elem.MQError != null || elem.MQResult != null) continue;

				try {

					List<String> parts = elem.Element.getParts();

					if (parts.size() == 0) throw new MediaQueryException("Element has no associated files"); //$NON-NLS-1$

					StringBuilder raw = new StringBuilder();
					List<MediaQueryResult> dat = new ArrayList<>();

					for (int pi = 0; pi < parts.size(); pi++) {
						dat.add(MediaQueryRunner.query(PathFormatter.fromCCPath(parts.get(pi)), false));

						if (cancelBackground) return;

						if (pi > 0) raw.append("\n\n--------------------------------------\n\n"); //$NON-NLS-1$
						raw.append(MediaQueryRunner.queryRaw(PathFormatter.fromCCPath(parts.get(pi))));

						if (cancelBackground) return;
					}

					elem.MQError = null;
					elem.MQResult = dat;
					elem.MQResultString = raw.toString();
					elem.Processed = true;

					elem.check();

				} catch (MediaQueryException e) {
					elem.MQError = e.getMessage() + "\n\n\n" + e.MessageLong; //$NON-NLS-1$
					elem.MQResult = null;
					elem.MQResultString = Str.Empty;
					elem.Processed = true;
				} catch (IOException e) {
					elem.MQError = e.getMessage();
					elem.MQResult = null;
					elem.MQResultString = Str.Empty;
					elem.Processed = true;
				} catch (Exception e) {
					elem.MQError = ExceptionUtils.getMessage(e) + "\n\n" + ExceptionUtils.getStackTrace(e); //$NON-NLS-1$
					elem.MQResult = null;
					elem.MQResultString = Str.Empty;
					elem.Processed = true;
				}

				SwingUtils.invokeLater(() -> {
					tableMain.changeData(elem, elem);
					if (cbAutoScroll.isSelected()) tableMain.scrollIntoView(elem);
				});

				if (cancelBackground) return;
			}
		} catch (Exception e) {
			CCLog.addUndefinied(collThread, e);
		} finally {
			ThreadUtils.setProgressbarAndWait(progressBar, 0, 0, 1);
			SwingUtils.invokeAndWaitSafe(() -> 
			{ 
				btnStartCollectingData.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect3"));  //$NON-NLS-1$
				btnUpdateSelectedLang.setEnabled(!movielist.isReadonly());
				btnUpdateSelectedLen.setEnabled(!movielist.isReadonly());
				btnUpdateSelectedMediaInfo.setEnabled(!movielist.isReadonly());
			});
			collThread = null;
		}
	}

	private void updateSelectedLanguages(boolean onlySelected) {
		if (movielist.isReadonly()) return;

		List<UpdateCodecTableElement> data = onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy();

		int count = 0;

		for (UpdateCodecTableElement elem : data) {
			if (!elem.Processed) continue;
			if (elem.MQResult == null) continue;

			CCDBLanguageList v = elem.getNewLanguage();
			if (v.isEmpty()) continue;

			if (!CCDBLanguageList.equals(v, elem.Element.language().get())) {elem.Element.language().set(v); count++; }
		}

		DialogHelper.showDispatchInformation(this, LocaleBundle.getString("Dialogs.CodecUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.CodecUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$

		tableMain.forceDataChangedRedraw();
	}

	private void updateSelectedLengths(boolean onlySelected) {
		if (movielist.isReadonly()) return;

		List<UpdateCodecTableElement> data = onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy();

		int count = 0;

		for (UpdateCodecTableElement elem : data) {
			if (!elem.Processed) continue;
			if (elem.MQResult == null) continue;

			int v = elem.getNewDuration();
			if (v == -1) continue;

			if (v != elem.Element.length().get()) { elem.Element.length().set(v); count++; }
		}

		DialogHelper.showDispatchInformation(this, LocaleBundle.getString("Dialogs.CodecUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.CodecUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$

		tableMain.forceDataChangedRedraw();
	}

	private void updateSelectedMediaInfos(boolean onlySelected) {
		if (movielist.isReadonly()) return;

		List<UpdateCodecTableElement> data = onlySelected ? tableMain.getSelectedDataCopy() : tableMain.getDataCopy();

		int count = 0;

		for (UpdateCodecTableElement elem : data) {
			if (!elem.Processed) continue;
			if (elem.MQResult == null) continue;

			CCMediaInfo v = elem.getNewMediaInfo();
			if (v.isUnset()) continue;

			if (!elem.Element.mediaInfo().get().equals(v)) { elem.Element.mediaInfo().set(v); count++; }
		}

		DialogHelper.showDispatchInformation(this, LocaleBundle.getString("Dialogs.CodecUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.CodecUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$

		tableMain.forceDataChangedRedraw();
	}
}
