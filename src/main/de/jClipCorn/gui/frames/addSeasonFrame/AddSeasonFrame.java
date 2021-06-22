package de.jClipCorn.gui.frames.addSeasonFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.SeasonDataPack;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.jYearSpinner.JYearSpinner;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.listener.UpdateCallbackListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AddSeasonFrame extends JFrame implements UserDataProblemHandler, ParseResultHandler
{
	private final CCSeries parent;

	private final UpdateCallbackListener listener;

	public AddSeasonFrame(Component owner, CCSeries ser, UpdateCallbackListener ucl)
	{
		super();
		this.parent = ser;
		this.listener = ucl;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());
	}

	@Override
	public void setCover(BufferedImage nci) {
		edCvrControl.setCover(nci);
	}

	private void cancel() {
		this.dispose();
	}

	private void onBtnOK(boolean check) {
		List<UserDataProblem> problems = new ArrayList<>();

		boolean probvalue = !check || checkUserData(problems);

		// some problems are too fatal
		if (probvalue && ! edCvrControl.isCoverSet()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER));
			probvalue = false;
		}
		if (probvalue && edTitle.getText().isEmpty()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			probvalue = false;
		}

		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(problems, this, this);
			amied.setVisible(true);
			return;
		}

		CCSeason newS = parent.createNewEmptySeason();

		newS.beginUpdating();

		//#####################################################################################

		newS.Title.set(edTitle.getText());
		newS.Year.set(spnYear.getValue());
		newS.setCover(edCvrControl.getResizedImageForStorage());

		//#####################################################################################

		newS.endUpdating();

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
						(int) spnYear.getValue(),
						edCvrControl.getResizedImageForStorage()
				);

		UserDataProblem.testSeasonData(ret, parent.getMovieList(), null, spack);

		return ret.isEmpty();
	}

	@Override
	public void onAMIEDIgnoreClicked() {
		onBtnOK(false);
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
		return CCOnlineReferenceList.EMPTY;
	}

	@Override
	public void setMovieFormat(CCFileFormat cmf) {
		// NOP
	}

	@Override
	public void setFilepath(int p, String t) {
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
		// NOP
	}

	@Override
	public void onFinishInserting() {
		// NOP
	}

	private void onOK(ActionEvent e) {
		onBtnOK(true);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		label1 = new JLabel();
		edTitle = new JTextField();
		edCvrControl = new EditCoverControl();
		label2 = new JLabel();
		spnYear = new JYearSpinner();
		panel1 = new JPanel();
		btnOK = new JButton();
		btnABort = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("AddSeasonFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $ugap, default:grow, $ugap, default, $ugap", //$NON-NLS-1$
			"$ugap, 2*(default, $lgap), default:grow, $lgap, default, $ugap")); //$NON-NLS-1$

		//---- label1 ----
		label1.setText(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		contentPane.add(label1, CC.xy(2, 2));
		contentPane.add(edTitle, CC.xy(4, 2));
		contentPane.add(edCvrControl, CC.xywh(6, 2, 1, 5));

		//---- label2 ----
		label2.setText(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
		contentPane.add(label2, CC.xy(2, 4));
		contentPane.add(spnYear, CC.xy(4, 4));

		//======== panel1 ========
		{
			panel1.setLayout(new FlowLayout());

			//---- btnOK ----
			btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
			btnOK.addActionListener(e -> onOK(e));
			panel1.add(btnOK);

			//---- btnABort ----
			btnABort.setText(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
			btnABort.addActionListener(e -> cancel());
			panel1.add(btnABort);
		}
		contentPane.add(panel1, CC.xywh(2, 8, 5, 1, CC.FILL, CC.FILL));
		setSize(500, 400);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel label1;
	private JTextField edTitle;
	private EditCoverControl edCvrControl;
	private JLabel label2;
	private JYearSpinner spnYear;
	private JPanel panel1;
	private JButton btnOK;
	private JButton btnABort;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
