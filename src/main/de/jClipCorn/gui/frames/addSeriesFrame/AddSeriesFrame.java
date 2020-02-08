package de.jClipCorn.gui.frames.addSeriesFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.parseOnlineFrame.ParseOnlineDialog;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.groupListEditor.GroupListEditor;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.exceptions.EnumValueNotFoundException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AddSeriesFrame extends JFrame implements ParseResultHandler, UserDataProblemHandler {
	private static final long serialVersionUID = -4500039578109890172L;
	
	private CCMovieList movieList;
	
	private JLabel label;
	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;
	private JLabel label_4;
	private JLabel label_5;
	private JLabel label_6;
	private JLabel label_7;
	private CCEnumComboBox<CCGenre> cbxGenre0;
	private CCEnumComboBox<CCGenre> cbxGenre1;
	private CCEnumComboBox<CCGenre> cbxGenre2;
	private CCEnumComboBox<CCGenre> cbxGenre3;
	private CCEnumComboBox<CCGenre> cbxGenre4;
	private CCEnumComboBox<CCGenre> cbxGenre5;
	private CCEnumComboBox<CCGenre> cbxGenre6;
	private CCEnumComboBox<CCGenre> cbxGenre7;
	private JButton btnParse;
	private JLabel label_8;
	private JTextField edTitle;
	private JLabel label_11;
	private CCEnumComboBox<CCOptionalFSK> cbxFSK;
	private JLabel label_12;
	private JSpinner spnOnlinescore;
	private JLabel label_13;
	private JButton btnOK;
	private JButton btnCancel;
	private EditCoverControl edCvrControl;
	private JLabel label_9;
	private JReferenceChooser edReference;
	private GroupListEditor edGroups;
	private JLabel lblGroups;

	public AddSeriesFrame(Component owner, CCMovieList mlist) {
		setSize(new Dimension(675, 500));
		movieList = mlist;
		
		setResizable(false);
		
		initGUI();
		setDefaultValues();
		
		setLocationRelativeTo(owner);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("AddSeriesFrame.this.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);

		label = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
		label.setBounds(10, 133, 59, 16);
		getContentPane().add(label);

		cbxGenre0 = new CCEnumComboBox<>(CCGenre.getWrapper());
		cbxGenre0.setBounds(87, 130, 212, 22);
		getContentPane().add(cbxGenre0);

		cbxGenre1 = new CCEnumComboBox<>(CCGenre.getWrapper());
		cbxGenre1.setBounds(87, 160, 212, 22);
		getContentPane().add(cbxGenre1);

		cbxGenre2 = new CCEnumComboBox<>(CCGenre.getWrapper());
		cbxGenre2.setBounds(87, 190, 212, 22);
		getContentPane().add(cbxGenre2);

		label_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
		label_1.setBounds(10, 193, 59, 16);
		getContentPane().add(label_1);

		label_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
		label_2.setBounds(10, 163, 59, 16);
		getContentPane().add(label_2);

		label_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
		label_3.setBounds(10, 223, 59, 16);
		getContentPane().add(label_3);

		cbxGenre3 = new CCEnumComboBox<>(CCGenre.getWrapper());
		cbxGenre3.setBounds(87, 220, 212, 22);
		getContentPane().add(cbxGenre3);

		cbxGenre4 = new CCEnumComboBox<>(CCGenre.getWrapper());
		cbxGenre4.setBounds(87, 250, 212, 22);
		getContentPane().add(cbxGenre4);

		label_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
		label_4.setBounds(10, 253, 59, 16);
		getContentPane().add(label_4);

		label_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
		label_5.setBounds(10, 283, 59, 16);
		getContentPane().add(label_5);

		cbxGenre5 = new CCEnumComboBox<>(CCGenre.getWrapper());
		cbxGenre5.setBounds(87, 280, 212, 22);
		getContentPane().add(cbxGenre5);

		cbxGenre6 = new CCEnumComboBox<>(CCGenre.getWrapper());
		cbxGenre6.setBounds(87, 310, 212, 22);
		getContentPane().add(cbxGenre6);

		label_6 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
		label_6.setBounds(10, 313, 59, 16);
		getContentPane().add(label_6);

		label_7 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
		label_7.setBounds(10, 343, 59, 16);
		getContentPane().add(label_7);

		cbxGenre7 = new CCEnumComboBox<>(CCGenre.getWrapper());
		cbxGenre7.setBounds(87, 340, 212, 22);
		getContentPane().add(cbxGenre7);

		btnParse = new JButton(LocaleBundle.getString("AddSeriesFrame.btnParse.text")); //$NON-NLS-1$
		btnParse.addActionListener(e -> showIMDBParser());
		btnParse.setFont(new Font("Tahoma", Font.BOLD, 15)); //$NON-NLS-1$
		btnParse.setBounds(445, 80, 212, 42);
		getContentPane().add(btnParse);

		label_8 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		label_8.setBounds(10, 12, 52, 16);
		getContentPane().add(label_8);

		edTitle = new JTextField();
		edTitle.setColumns(10);
		edTitle.setBounds(87, 10, 212, 20);
		getContentPane().add(edTitle);

		label_11 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
		label_11.setBounds(10, 73, 71, 16);
		getContentPane().add(label_11);

		cbxFSK = new CCEnumComboBox<>(CCOptionalFSK.getWrapper());
		cbxFSK.setBounds(87, 70, 212, 22);
		getContentPane().add(cbxFSK);

		label_12 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
		label_12.setBounds(10, 42, 87, 16);
		getContentPane().add(label_12);

		spnOnlinescore = new JSpinner();
		spnOnlinescore.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		spnOnlinescore.setBounds(106, 40, 193, 20);
		getContentPane().add(spnOnlinescore);

		label_13 = new JLabel("/ 10"); //$NON-NLS-1$
		label_13.setBounds(311, 40, 52, 16);
		getContentPane().add(label_13);

		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(e ->
		{
			try {
				onBtnOK(true);
			} catch (EnumFormatException | EnumValueNotFoundException e1) {
				CCLog.addError(e1);
			}
		});
		btnOK.setBounds(176, 437, 116, 25);
		getContentPane().add(btnOK);

		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(e -> cancel());
		btnCancel.setBounds(311, 437, 116, 25);
		getContentPane().add(btnCancel);
		
		edCvrControl = new EditCoverControl(this, this);
		edCvrControl.setBounds(475, 133, EditCoverControl.CTRL_WIDTH, EditCoverControl.CTRL_HEIGHT);
		getContentPane().add(edCvrControl);
		
		label_9 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlineID.text")); //$NON-NLS-1$
		label_9.setBounds(368, 12, 87, 16);
		getContentPane().add(label_9);
		
		edReference = new JReferenceChooser();
		edReference.setBounds(445, 10, 212, 20);
		getContentPane().add(edReference);
		
		edGroups = new GroupListEditor(movieList);
		edGroups.setBounds(445, 40, 212, 22);
		getContentPane().add(edGroups);
		
		lblGroups = new JLabel(LocaleBundle.getString("EditSeriesFrame.lblGroups.text")); //$NON-NLS-1$
		lblGroups.setBounds(368, 43, 87, 16);
		getContentPane().add(lblGroups);
	}
	
	private void setDefaultValues() {
		cbxFSK.setSelectedEnum(CCOptionalFSK.NULL);
	}

	@Override
	public void setCover(BufferedImage nci) {
		edCvrControl.setCover(nci);
	}
	
	private void showIMDBParser() {
		(new ParseOnlineDialog(this, this, CCDBElementTyp.SERIES)).setVisible(true);
	}

	@Override
	public String getFullTitle() {
		return edTitle.getText();
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
		// No such field
	}

	@Override
	public void setFilepath(int p, String t) {
		// No such field
	}

	@Override
	public void setMovieName(String name) {
		edTitle.setText(name);
	}

	@Override
	public void setZyklus(String mZyklusTitle) {
		// No such field
	}

	@Override
	public void setZyklusNumber(int iRoman) {
		// No such field
	}

	@Override
	public void setFilesize(CCFileSize size) {
		// No such field
	}

	@Override
	public void setYear(int y) {
		// No such field
	}

	@Override
	public void setGenre(int gid, CCGenre serGenre) {
		switch (gid) {
			case 0: cbxGenre0.setSelectedEnum(serGenre); break;
			case 1: cbxGenre1.setSelectedEnum(serGenre); break;
			case 2: cbxGenre2.setSelectedEnum(serGenre); break;
			case 3: cbxGenre3.setSelectedEnum(serGenre); break;
			case 4: cbxGenre4.setSelectedEnum(serGenre); break;
			case 5: cbxGenre5.setSelectedEnum(serGenre); break;
			case 6: cbxGenre6.setSelectedEnum(serGenre); break;
			case 7: cbxGenre7.setSelectedEnum(serGenre); break;
		}
	}

	@Override
	public void setFSK(CCFSK fsk) {
		cbxFSK.setSelectedEnum(fsk.asOptionalFSK());
	}

	@Override
	public void setLength(int l) {
		// No such field
	}
	
	@Override
	public void setOnlineReference(CCOnlineReferenceList ref) {
		edReference.setValue(ref);
	}
	
	@Override
	public void onFinishInserting() {
		// nothing
	}

	@Override
	public void setScore(CCOnlineScore s) {
		spnOnlinescore.setValue(s.asInt());
	}
	
	private void cancel() {
		this.dispose();
	}
	
	private void onBtnOK(boolean check) throws EnumFormatException, EnumValueNotFoundException {
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
		
		CCSeries newS = movieList.createNewEmptySeries();
		
		newS.beginUpdating();
		
		//#####################################################################################
		
		newS.setTitle(edTitle.getText());

		newS.setOnlinescore((int) spnOnlinescore.getValue());
		
		newS.setFsk(cbxFSK.getSelectedEnum().asFSK());
		
		newS.setOnlineReference(edReference.getValue());
		
		newS.setGenre(cbxGenre0.getSelectedEnum(), 0);
		newS.setGenre(cbxGenre1.getSelectedEnum(), 1);
		newS.setGenre(cbxGenre2.getSelectedEnum(), 2);
		newS.setGenre(cbxGenre3.getSelectedEnum(), 3);
		newS.setGenre(cbxGenre4.getSelectedEnum(), 4);
		newS.setGenre(cbxGenre5.getSelectedEnum(), 5);
		newS.setGenre(cbxGenre6.getSelectedEnum(), 6);
		newS.setGenre(cbxGenre7.getSelectedEnum(), 7);
		
		newS.setGroups(edGroups.getValue());
		
		newS.setCover(edCvrControl.getResizedImageForStorage());
		
		//#####################################################################################
		
		newS.endUpdating();
		
		EditSeriesFrame esf = new EditSeriesFrame(this, newS, null);
		esf.setVisible(true);
		
		dispose();
	}
	
	public boolean checkUserData(List<UserDataProblem> ret) {
		String title = edTitle.getText();
		
		int oscore = (int) spnOnlinescore.getValue();
		
		int fskidx = cbxFSK.getSelectedEnum().asInt();
		
		int gen0 = cbxGenre0.getSelectedEnum().asInt();
		int gen1 = cbxGenre1.getSelectedEnum().asInt();
		int gen2 = cbxGenre2.getSelectedEnum().asInt();
		int gen3 = cbxGenre3.getSelectedEnum().asInt();
		int gen4 = cbxGenre4.getSelectedEnum().asInt();
		int gen5 = cbxGenre5.getSelectedEnum().asInt();
		int gen6 = cbxGenre6.getSelectedEnum().asInt();
		int gen7 = cbxGenre7.getSelectedEnum().asInt();
		
		CCOnlineReferenceList ref = edReference.getValue();
		
		UserDataProblem.testSeriesData(ret, edCvrControl.getResizedImageForStorage(), title, oscore, gen0, gen1, gen2, gen3, gen4, gen5, gen6, gen7, fskidx, ref);
		
		return ret.isEmpty();
	}
	
	@Override
	public void onAMIEDIgnoreClicked() {
		try {
			onBtnOK(false);
		} catch (EnumFormatException | EnumValueNotFoundException e) {
			CCLog.addError(e);
		}
	}
}
