package de.jClipCorn.gui.frames.updateCodecFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.helper.ThreadUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateCodecFrame extends JCCFrame
{
	private FilterState selectedFilter = FilterState.ALL;

	private Thread collThread = null;
	private boolean cancelBackground;

	private enum FilterState { ALL, CHANGED, CHANGED_LANG, CHANGED_SUBS, CHANGED_LEN, CHANGED_LEN_DYN, CHANGED_MEDIAINFO, ELEM_ERROR, ELEM_WARN }

	public UpdateCodecFrame(Component owner, CCMovieList ml)
	{
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);

		initTable();
	}

	private void postInit()
	{
		//
	}

	private void showMediaInfo() {
		String title = (tableMain.getSelectedElement()==null) ? Str.Empty : tableMain.getSelectedElement().getFullDisplayTitle();
		GenericTextDialog.showText(UpdateCodecFrame.this, title, edMediaInfo.getText(), false);
	}

	private void updateSelectedLanguages() {
		if (movielist.isReadonly()) return;

		List<UpdateCodecTableElement> data = tableMain.getSelectedDataCopy();

		int count = 0;

		for (UpdateCodecTableElement elem : data) {
			if (!elem.Processed) continue;
			if (elem.MQResult == null) continue;

			CCDBLanguageSet v = elem.getNewLanguage();
			if (v.isEmpty()) continue;

			if (!CCDBLanguageSet.equals(v, elem.Element.language().get())) {elem.Element.language().set(v); count++; }
		}

		DialogHelper.showDispatchInformation(this, LocaleBundle.getString("Dialogs.CodecUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.CodecUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$

		tableMain.forceDataChangedRedraw();
	}

	private void updateSelectedSubtitles() {
		if (movielist.isReadonly()) return;

		List<UpdateCodecTableElement> data = tableMain.getSelectedDataCopy();

		int count = 0;

		for (UpdateCodecTableElement elem : data) {
			if (!elem.Processed) continue;
			if (elem.MQResult == null) continue;

			CCDBLanguageList v = elem.getNewSubtitles();

			if (!CCDBLanguageList.equals(v, elem.Element.subtitles().get())) {elem.Element.subtitles().set(v); count++; }
		}

		DialogHelper.showDispatchInformation(this, LocaleBundle.getString("Dialogs.CodecUpdateSuccess_caption"), LocaleBundle.getFormattedString("Dialogs.CodecUpdateSuccess", count)); //$NON-NLS-1$ //$NON-NLS-2$

		tableMain.forceDataChangedRedraw();
	}

	private void updateSelectedLengths() {
		if (movielist.isReadonly()) return;

		List<UpdateCodecTableElement> data = tableMain.getSelectedDataCopy();

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

	private void updateSelectedMediaInfos() {
		if (movielist.isReadonly()) return;

		List<UpdateCodecTableElement> data = tableMain.getSelectedDataCopy();

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

	private void setFilterAll() {
		setFiltered(FilterState.ALL, true, true);
	}

	private void setFilterLang() {
		setFiltered(FilterState.CHANGED, true, true);
	}

	private void setFilterChanged() {
		setFiltered(FilterState.CHANGED_LANG, true, true);
	}

	private void setFilterSubs() {
		setFiltered(FilterState.CHANGED_SUBS, true, true);
	}

	private void setFilterLen() {
		setFiltered(FilterState.CHANGED_LEN, true, true);
	}

	private void setFilterDynLen() {
		setFiltered(FilterState.CHANGED_LEN_DYN, true, true);
	}

	private void setFilterMediaInfo() {
		setFiltered(FilterState.CHANGED_MEDIAINFO, true, true);
	}

	private void setFilterError() {
		setFiltered(FilterState.ELEM_ERROR, true, true);
	}

	private void setFilterWarn() {
		setFiltered(FilterState.ELEM_WARN, true, true);
	}

	private void onDynLengthChanged() {
		setFiltered(selectedFilter, true, true);
	}

	public void setSelection(UpdateCodecTableElement element)
	{
		if (element == null) {
			edMediaInfo.setText(Str.Empty);
		} else {
			if (!element.Processed) edMediaInfo.setText(Str.Empty);
			else if (element.MQError != null) edMediaInfo.setText(element.MQError.trim());
			else if (element.MQResultString != null) edMediaInfo.setText(element.MQResultString.trim());
			else edMediaInfo.setText("Error ???"); //$NON-NLS-1$
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
		btnShowFilteredSubs.setSelected(state == FilterState.CHANGED_SUBS);
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
				case CHANGED_SUBS:
					tableMain.setFilter(e -> e.Processed && e.hasSubsDiff());
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

	private void queryMetadata() {
		if (collThread != null && collThread.isAlive()) {
			cancelBackground = true;
		} else {
			var mqp = ccprops().PROP_PLAY_MEDIAINFO_PATH.getValue();
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
			java.util.List<UpdateCodecTableElement> data = tableMain.getDataCopy();

			SwingUtils.invokeAndWaitSafe(() ->
			{
				btnStartCollectingData.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect2"));  //$NON-NLS-1$
				btnUpdateSelectedLang.setEnabled(false);
				btnUpdateSelectedSubs.setEnabled(false);
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

					java.util.List<CCPath> parts = elem.Element.getParts();

					if (parts.size() == 0) throw new MediaQueryException("Element has no associated files"); //$NON-NLS-1$

					StringBuilder raw = new StringBuilder();
					List<MediaQueryResult> dat = new ArrayList<>();

					for (int pi = 0; pi < parts.size(); pi++) {
						dat.add(new MediaQueryRunner(movielist).query(parts.get(pi).toFSPath(this), false));

						if (cancelBackground) return;

						if (pi > 0) raw.append("\n\n--------------------------------------\n\n"); //$NON-NLS-1$
						raw.append(new MediaQueryRunner(movielist).queryRaw(parts.get(pi).toFSPath(this)));

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
				btnUpdateSelectedSubs.setEnabled(!movielist.isReadonly());
				btnUpdateSelectedLen.setEnabled(!movielist.isReadonly());
				btnUpdateSelectedMediaInfo.setEnabled(!movielist.isReadonly());
			});
			collThread = null;
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel2 = new JPanel();
		btnStartCollectingData = new JButton();
		progressBar = new JProgressBar();
		btnShowAll = new JToggleButton();
		btnShowFilteredLen = new JToggleButton();
		btnShowFilteredDynLen = new JToggleButton();
		btnShowFilteredErr = new JToggleButton();
		btnShowFiltered = new JToggleButton();
		btnShowFilteredLang = new JToggleButton();
		btnShowFilteredWarn = new JToggleButton();
		btnShowFilteredSubs = new JToggleButton();
		btnShowFilteredMediaInfo = new JToggleButton();
		tableMain = new UpdateCodecTable(this);
		panel1 = new JPanel();
		btnUpdateSelectedLang = new JButton();
		scrollPane = new JScrollPane();
		edMediaInfo = new JTextArea();
		btnUpdateSelectedSubs = new JButton();
		btnUpdateSelectedLen = new JButton();
		btnUpdateSelectedMediaInfo = new JButton();
		label1 = new JLabel();
		spinnerDynLen = new JSpinner();
		cbAutoScroll = new JCheckBox();
		button6 = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("UpdateCodecFrame.Title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(750, 450));
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default, $lgap, default:grow, $lgap, default, $ugap")); //$NON-NLS-1$

		//======== panel2 ========
		{
			panel2.setLayout(new FormLayout(
				"default, $ugap, default:grow, 2*($ugap, default), default, $ugap, default", //$NON-NLS-1$
				"3*(default, $lgap), default")); //$NON-NLS-1$

			//---- btnStartCollectingData ----
			btnStartCollectingData.setText(LocaleBundle.getString("UpdateMetadataFrame.BtnCollect1")); //$NON-NLS-1$
			btnStartCollectingData.addActionListener(e -> queryMetadata());
			panel2.add(btnStartCollectingData, CC.xy(1, 1));
			panel2.add(progressBar, CC.xy(3, 1, CC.FILL, CC.FILL));

			//---- btnShowAll ----
			btnShowAll.setText(LocaleBundle.getString("UpdateMetadataFrame.SwitchFilter1")); //$NON-NLS-1$
			btnShowAll.setSelected(true);
			btnShowAll.addActionListener(e -> setFilterAll());
			panel2.add(btnShowAll, CC.xy(5, 1));

			//---- btnShowFilteredLen ----
			btnShowFilteredLen.setText(LocaleBundle.getString("UpdateCodecFrame.Filter3")); //$NON-NLS-1$
			btnShowFilteredLen.addActionListener(e -> setFilterLen());
			panel2.add(btnShowFilteredLen, CC.xy(7, 1));

			//---- btnShowFilteredDynLen ----
			btnShowFilteredDynLen.setText(LocaleBundle.getString("UpdateCodecFrame.Filter4")); //$NON-NLS-1$
			btnShowFilteredDynLen.addActionListener(e -> setFilterDynLen());
			panel2.add(btnShowFilteredDynLen, CC.xy(8, 1));

			//---- btnShowFilteredErr ----
			btnShowFilteredErr.setText(LocaleBundle.getString("UpdateCodecFrame.Filter5")); //$NON-NLS-1$
			btnShowFilteredErr.addActionListener(e -> setFilterError());
			panel2.add(btnShowFilteredErr, CC.xy(10, 1));

			//---- btnShowFiltered ----
			btnShowFiltered.setText(LocaleBundle.getString("UpdateCodecFrame.Filter1")); //$NON-NLS-1$
			btnShowFiltered.addActionListener(e -> setFilterChanged());
			panel2.add(btnShowFiltered, CC.xy(5, 3));

			//---- btnShowFilteredLang ----
			btnShowFilteredLang.setText(LocaleBundle.getString("UpdateCodecFrame.Filter2")); //$NON-NLS-1$
			btnShowFilteredLang.addActionListener(e -> setFilterLang());
			panel2.add(btnShowFilteredLang, CC.xy(7, 3));

			//---- btnShowFilteredWarn ----
			btnShowFilteredWarn.setText(LocaleBundle.getString("UpdateCodecFrame.Filter6")); //$NON-NLS-1$
			btnShowFilteredWarn.addActionListener(e -> setFilterWarn());
			panel2.add(btnShowFilteredWarn, CC.xy(10, 3));

			//---- btnShowFilteredSubs ----
			btnShowFilteredSubs.setText(LocaleBundle.getString("UpdateCodecFrame.Filter8")); //$NON-NLS-1$
			btnShowFilteredSubs.addActionListener(e -> setFilterSubs());
			panel2.add(btnShowFilteredSubs, CC.xy(7, 5));

			//---- btnShowFilteredMediaInfo ----
			btnShowFilteredMediaInfo.setText(LocaleBundle.getString("UpdateCodecFrame.Filter7")); //$NON-NLS-1$
			btnShowFilteredMediaInfo.addActionListener(e -> setFilterMediaInfo());
			panel2.add(btnShowFilteredMediaInfo, CC.xy(7, 7));
		}
		contentPane.add(panel2, CC.xy(2, 2));
		contentPane.add(tableMain, CC.xy(2, 4, CC.FILL, CC.FILL));

		//======== panel1 ========
		{
			panel1.setLayout(new FormLayout(
				"default, $lcgap, [100dlu,default], $lcgap, default:grow, $lcgap, default", //$NON-NLS-1$
				"5*(default, $lgap), default")); //$NON-NLS-1$

			//---- btnUpdateSelectedLang ----
			btnUpdateSelectedLang.setText(LocaleBundle.getString("UpdateCodecFrame.Button1")); //$NON-NLS-1$
			btnUpdateSelectedLang.setEnabled(false);
			btnUpdateSelectedLang.addActionListener(e -> updateSelectedLanguages());
			panel1.add(btnUpdateSelectedLang, CC.xywh(1, 1, 3, 1));

			//======== scrollPane ========
			{

				//---- edMediaInfo ----
				edMediaInfo.setEditable(false);
				scrollPane.setViewportView(edMediaInfo);
			}
			panel1.add(scrollPane, CC.xywh(5, 1, 1, 11));

			//---- btnUpdateSelectedSubs ----
			btnUpdateSelectedSubs.setText(LocaleBundle.getString("UpdateCodecFrame.btnUpdateSelectedSubs.text")); //$NON-NLS-1$
			btnUpdateSelectedSubs.setEnabled(false);
			btnUpdateSelectedSubs.addActionListener(e -> updateSelectedSubtitles());
			panel1.add(btnUpdateSelectedSubs, CC.xywh(1, 3, 3, 1));

			//---- btnUpdateSelectedLen ----
			btnUpdateSelectedLen.setText(LocaleBundle.getString("UpdateCodecFrame.Button2")); //$NON-NLS-1$
			btnUpdateSelectedLen.setEnabled(false);
			btnUpdateSelectedLen.addActionListener(e -> updateSelectedLengths());
			panel1.add(btnUpdateSelectedLen, CC.xywh(1, 5, 3, 1));

			//---- btnUpdateSelectedMediaInfo ----
			btnUpdateSelectedMediaInfo.setText(LocaleBundle.getString("UpdateCodecFrame.Button3")); //$NON-NLS-1$
			btnUpdateSelectedMediaInfo.setEnabled(false);
			btnUpdateSelectedMediaInfo.addActionListener(e -> updateSelectedMediaInfos());
			panel1.add(btnUpdateSelectedMediaInfo, CC.xywh(1, 7, 3, 1));

			//---- label1 ----
			label1.setText(LocaleBundle.getString("UpdateCodecFrame.Label1")); //$NON-NLS-1$
			panel1.add(label1, CC.xy(1, 9));

			//---- spinnerDynLen ----
			spinnerDynLen.setModel(new SpinnerNumberModel(15.0, 0.0, 100.0, 1.0));
			spinnerDynLen.addChangeListener(e -> onDynLengthChanged());
			panel1.add(spinnerDynLen, CC.xy(3, 9));

			//---- cbAutoScroll ----
			cbAutoScroll.setText(LocaleBundle.getString("UpdateCodecFrame.CBScroll")); //$NON-NLS-1$
			panel1.add(cbAutoScroll, CC.xywh(1, 11, 3, 1));

			//---- button6 ----
			button6.setText("..."); //$NON-NLS-1$
			button6.addActionListener(e -> showMediaInfo());
			panel1.add(button6, CC.xy(7, 11));
		}
		contentPane.add(panel1, CC.xy(2, 6));
		setSize(1200, 650);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panel2;
	private JButton btnStartCollectingData;
	private JProgressBar progressBar;
	private JToggleButton btnShowAll;
	private JToggleButton btnShowFilteredLen;
	private JToggleButton btnShowFilteredDynLen;
	private JToggleButton btnShowFilteredErr;
	private JToggleButton btnShowFiltered;
	private JToggleButton btnShowFilteredLang;
	private JToggleButton btnShowFilteredWarn;
	private JToggleButton btnShowFilteredSubs;
	private JToggleButton btnShowFilteredMediaInfo;
	private UpdateCodecTable tableMain;
	private JPanel panel1;
	private JButton btnUpdateSelectedLang;
	private JScrollPane scrollPane;
	private JTextArea edMediaInfo;
	private JButton btnUpdateSelectedSubs;
	private JButton btnUpdateSelectedLen;
	private JButton btnUpdateSelectedMediaInfo;
	private JLabel label1;
	private JSpinner spinnerDynLen;
	private JCheckBox cbAutoScroll;
	private JButton button6;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
