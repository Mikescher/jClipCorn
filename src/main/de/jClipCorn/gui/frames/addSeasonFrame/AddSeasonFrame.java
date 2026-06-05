package de.jClipCorn.gui.frames.addSeasonFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.SeasonDataPack;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.guiComponents.JAutoCompleteTextField;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.util.comparator.CCAnimeSeasonComparator;
import de.jClipCorn.gui.guiComponents.jYearSpinner.JYearSpinner;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.listener.UpdateCallbackListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AddSeasonFrame extends JCCFrame implements UserDataProblemHandler, ParseResultHandler
{
	private final CCSeries parent;

	private final UpdateCallbackListener listener;

	public AddSeasonFrame(Component owner, CCSeries ser, UpdateCallbackListener ucl)
	{
		super(ser.getMovieList());
		this.parent = ser;
		this.listener = ucl;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		ccprops().PROP_FSIZE_ADDSEASONFRAME.applyOrSkip(this);

		this.edTitle.setText(this.parent.guessNextSeasonTitle().orElse(Str.Empty));
	}

	@Override
	public void setCover(BufferedImage nci) {
		edCvrControl.setCover(nci);
	}

	private void cancel() {
		this.dispose();
	}

	private void onBtnOK(boolean check) throws Exception {
		List<UserDataProblem> problems = new ArrayList<>();

		boolean probvalue = !check || checkUserData(problems);

		boolean fatalErr = false;

		// some problems are too fatal
		if (!edCvrControl.isCoverSet()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER));
			probvalue = false;
			fatalErr = true;
		}
		if (edTitle.getText().isEmpty()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			probvalue = false;
			fatalErr = true;
		}

		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(this, movielist, problems, this, !fatalErr);
			amied.setVisible(true);
			return;
		}

		parent.createNewSeason(newS -> {

			//#####################################################################################

			newS.Title.set(edTitle.getText());
			newS.Year.set(spnYear.getValue());
			newS.OnlineReference.set(edReference.getValue());
			newS.AnimeSeason.set(CCStringList.create(edAnimeSeason.getValues()));
			newS.AnimeStudio.set(CCStringList.create(edAnimeStudio.getValues()));
			newS.setCover(edCvrControl.getResizedImageForStorage());

			//#####################################################################################
		});

		if (listener != null) {
			listener.onUpdate(null);
		}

		dispose();
	}

	public boolean checkUserData(List<UserDataProblem> ret)
	{
		var spack = new SeasonDataPack
		(
			edTitle.getText(),
			spnYear.getValue(),
			edCvrControl.getResizedImageForStorage(),
			CCUserScore.RATING_NO,
			Str.Empty
		);

		UserDataProblem.testSeasonData(ret, movielist, null, spack);

		return ret.isEmpty();
	}

	@Override
	public void onAMIEDIgnoreClicked() {
		try {
			onBtnOK(false);
		} catch (Exception e) {
			CCLog.addError(e);
		}
	}

	@Override
	public String getFullTitle() {
		return parent.getTitle() + ": " + edTitle.getText(); //$NON-NLS-1$
	}

	@Override
	public String getTitleForParser() {
		return edTitle.getText();
	}

	@Override
	public CCOnlineReferenceList getSearchReference() {
		return edReference.getValue();
	}

	@Override
	public void setMovieFormat(CCFileFormat cmf) {
		// NOP
	}

	@Override
	public void setMovieName(String name) {
		// NOP
	}

	@Override
	public void setZyklus(String mZyklusTitle) {
		// NOP
	}

	@Override
	public void setZyklusNumber(int iRoman) {
		// NOP
	}

	@Override
	public void setFilesize(CCFileSize size) {
		// NOP
	}

	@Override
	public void setYear(int y) {
		// NOP
	}

	@Override
	public void setGenre(int gid, CCGenre movGenre) {
		// NOP
	}

	@Override
	public void setFSK(CCFSK fsk) {
		// NOP
	}

	@Override
	public void setLength(int l) {
		// NOP
	}

	@Override
	public void setScore(CCOnlineScore s) {
		// NOP
	}

	@Override
	public void setOnlineReference(CCOnlineReferenceList ref) {
		edReference.setValue(ref);
	}

	@Override
	public void setAnimeSeason(CCStringList animeSeason) {
		edAnimeSeason.setValues(animeSeason.ccstream().sort(new CCAnimeSeasonComparator()).toList());
	}

	@Override
	public void setAnimeStudio(CCStringList animeStudio) {
		edAnimeStudio.setValues(animeStudio.ccstream().toList());
	}

	@Override
	public void onFinishInserting() {
		// NOP
	}

	private void onOK(ActionEvent e) {
		try {
			onBtnOK(true);
		} catch (Exception ex) {
			CCLog.addError(ex);
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label1 = new JLabel();
		edTitle = new JTextField();
		edCvrControl = new EditCoverControl(this, this);
		label2 = new JLabel();
		spnYear = new JYearSpinner();
		label3 = new JLabel();
		edReference = new JReferenceChooser(movielist);
		lblAnimeSeason = new JLabel();
		edAnimeSeason = new JAutoCompleteTextField(() -> movielist.getAnimeSeasonList(), true);
		lblAnimeStudio = new JLabel();
		edAnimeStudio = new JAutoCompleteTextField(() -> movielist.getAnimeStudioList(), true);
		panel1 = new JPanel();
		btnOK = new JButton();
		btnABort = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("AddSeasonFrame.this.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $ugap, default:grow, $ugap, default, $ugap",
			"$ugap, 5*(default, $lgap), default:grow, $lgap, default, $ugap"));

		//---- label1 ----
		label1.setText(LocaleBundle.getString("AddMovieFrame.label_1.text"));
		contentPane.add(label1, CC.xy(2, 2));
		contentPane.add(edTitle, CC.xy(4, 2));
		contentPane.add(edCvrControl, CC.xywh(6, 2, 1, 11));

		//---- label2 ----
		label2.setText(LocaleBundle.getString("AddMovieFrame.lblYear.text"));
		contentPane.add(label2, CC.xy(2, 4));
		contentPane.add(spnYear, CC.xy(4, 4));

		//---- label3 ----
		label3.setText(LocaleBundle.getString("AddMovieFrame.lblOnlineID.text"));
		contentPane.add(label3, CC.xy(2, 6));
		contentPane.add(edReference, CC.xy(4, 6));

		//---- lblAnimeSeason ----
		lblAnimeSeason.setText(LocaleBundle.getString("AddSeasonFrame.lblAnimeSeason.text"));
		contentPane.add(lblAnimeSeason, CC.xy(2, 8));

		//---- edAnimeSeason ----
		edAnimeSeason.setOverFlowMode(de.jClipCorn.gui.guiComponents.OverFlowMode.WRAP);
		contentPane.add(edAnimeSeason, CC.xy(4, 8));

		//---- lblAnimeStudio ----
		lblAnimeStudio.setText(LocaleBundle.getString("AddSeasonFrame.lblAnimeStudio.text"));
		contentPane.add(lblAnimeStudio, CC.xy(2, 10));

		//---- edAnimeStudio ----
		edAnimeStudio.setOverFlowMode(de.jClipCorn.gui.guiComponents.OverFlowMode.WRAP);
		contentPane.add(edAnimeStudio, CC.xy(4, 10));

		//======== panel1 ========
		{
			panel1.setLayout(new FlowLayout());

			//---- btnOK ----
			btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text"));
			btnOK.addActionListener(e -> onOK(e));
			panel1.add(btnOK);

			//---- btnABort ----
			btnABort.setText(LocaleBundle.getString("UIGeneric.btnCancel.text"));
			btnABort.addActionListener(e -> cancel());
			panel1.add(btnABort);
		}
		contentPane.add(panel1, CC.xywh(2, 14, 5, 1, CC.FILL, CC.FILL));
		setSize(600, 400);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label1;
	private JTextField edTitle;
	private EditCoverControl edCvrControl;
	private JLabel label2;
	private JYearSpinner spnYear;
	private JLabel label3;
	private JReferenceChooser edReference;
	private JLabel lblAnimeSeason;
	private JAutoCompleteTextField edAnimeSeason;
	private JLabel lblAnimeStudio;
	private JAutoCompleteTextField edAnimeStudio;
	private JPanel panel1;
	private JButton btnOK;
	private JButton btnABort;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
