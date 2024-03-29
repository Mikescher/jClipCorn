package de.jClipCorn.gui.frames.editMediaInfoDialog;

import de.jClipCorn.features.metadata.MetadataSourceType;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.metadata.VideoMetadata;
import de.jClipCorn.util.datatypes.Opt;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MIDialogResultSet {
	public final MetadataSourceType Type;

	private Opt<CCMediaInfo> Data1 = Opt.empty();
	private Opt<String>           Data2 = Opt.empty();
	private Opt<VideoMetadata>    Data3 = Opt.empty();

	private final EditMediaInfoDialog owner;

	private final JButton btnRun;
	private final JButton btnShow;
	private final JButton btnHint;
	private final JButton btnApply;

	public MIDialogResultSet(EditMediaInfoDialog ownr, MetadataSourceType t, JButton bRun, JButton bShow, JButton bHint, JButton bApply) {
		Type = t;
		owner = ownr;
		btnRun = bRun;
		btnShow = bShow;
		btnHint = bHint;
		btnApply = bApply;
	}

	public void init() {
		updateEnabled(false);

		btnRun.addActionListener(this::onRun);
		btnShow.addActionListener(this::onShow);
		btnHint.addActionListener(this::onHint);
		btnApply.addActionListener(this::onApply);
	}

	private void onApply(ActionEvent actionEvent) {
		Data1.ifPresent(d -> owner.doApply(d, Data3));
	}

	private void onHint(ActionEvent actionEvent) {
		Data1.ifPresent(d -> owner.doShowHints(Opt.of(d), Type, Data3));
	}

	private void onShow(ActionEvent actionEvent) {
		Data2.ifPresent(owner::doShowOutput);
	}

	private void onRun(ActionEvent actionEvent) {
		owner.doQuery(Type.getRunner(owner.getMovieList()), this);
	}

	public void updateEnabled(boolean isRunning) {
		btnRun.setEnabled(!isRunning);
		btnShow.setEnabled(!isRunning && Data1.isPresent() && Data2.isPresent());
		btnHint.setEnabled(!isRunning && Data1.isPresent());
		btnApply.setEnabled(!isRunning && Data1.isPresent());
	}

	public void updateData(CCMediaInfo mi, VideoMetadata vmd) {
		Data1 = Opt.of(mi);
		Data2 = Opt.ofNullable(vmd).flatMap(p -> Opt.ofNullable(p.RawOutput));
		Data3 = Opt.ofNullable(vmd);
	}
}
