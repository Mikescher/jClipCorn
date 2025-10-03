package de.jClipCorn.gui.frames.addSeriesFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.SeriesDataPack;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.features.userdataProblem.UserDataProblemHandler;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.parseOnlineFrame.ParseOnlineDialog;
import de.jClipCorn.gui.guiComponents.JAutoCompleteTextField;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.guiComponents.groupListEditor.GroupListEditor;
import de.jClipCorn.gui.guiComponents.onlinescore.*;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.EnumValueNotFoundException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class AddSeriesFrame extends JCCFrame implements ParseResultHandler, UserDataProblemHandler
{
	public AddSeriesFrame(Component owner, CCMovieList mlist)
	{
		super(mlist);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
		setMinimumSize(getSize());
	}

	private void postInit()
	{
		ccprops().PROP_FSIZE_ADDSERIESFRAME.applyOrSkip(this);

		cbxFSK.setSelectedEnum(CCOptionalFSK.NULL);
	}

	private void onParseOnline(ActionEvent e)
	{
		(new ParseOnlineDialog(this, movielist, this, CCDBElementTyp.SERIES)).setVisible(true);
	}

	private void onOK(ActionEvent e)
	{
		try
		{
			onBtnOK(true);
		}
		catch (EnumValueNotFoundException e1)
		{
			CCLog.addError(e1);
		}
	}

	private void onCancel(ActionEvent e)
	{
		this.dispose();
	}

	private void onBtnOK(boolean check) throws EnumValueNotFoundException {
		java.util.List<UserDataProblem> problems = new ArrayList<>();

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

		CCSeries newS = movielist.createNewEmptySeries();

		newS.beginUpdating();

		//#####################################################################################

		newS.Title.set(edTitle.getText());

		newS.OnlineScore.set(spnOnlinescore.getValue());

		newS.FSK.set(cbxFSK.getSelectedEnum().asFSK());

		newS.OnlineReference.set(edReference.getValue());

		newS.Genres.set(cbxGenre0.getSelectedEnum(), 0);
		newS.Genres.set(cbxGenre1.getSelectedEnum(), 1);
		newS.Genres.set(cbxGenre2.getSelectedEnum(), 2);
		newS.Genres.set(cbxGenre3.getSelectedEnum(), 3);
		newS.Genres.set(cbxGenre4.getSelectedEnum(), 4);
		newS.Genres.set(cbxGenre5.getSelectedEnum(), 5);
		newS.Genres.set(cbxGenre6.getSelectedEnum(), 6);
		newS.Genres.set(cbxGenre7.getSelectedEnum(), 7);

		newS.Groups.set(edGroups.getValue());

		newS.AnimeSeason.set(CCStringList.create(edAnimeSeason.getValues()));
		newS.AnimeStudio.set(CCStringList.create(edAnimeStudio.getValues()));

		newS.setCover(edCvrControl.getResizedImageForStorage());

		//#####################################################################################

		newS.endUpdating();

		EditSeriesFrame esf = new EditSeriesFrame(this, newS, null);
		esf.setVisible(true);

		dispose();
	}

	private boolean checkUserData(java.util.List<UserDataProblem> ret)
	{
		var spack = new SeriesDataPack
		(
			edTitle.getText(),
			CCGenreList.create(cbxGenre0.getSelectedEnum(), cbxGenre1.getSelectedEnum(), cbxGenre2.getSelectedEnum(), cbxGenre3.getSelectedEnum(), cbxGenre4.getSelectedEnum(), cbxGenre5.getSelectedEnum(), cbxGenre6.getSelectedEnum(), cbxGenre7.getSelectedEnum()),
			spnOnlinescore.getValue(),
			cbxFSK.getSelectedEnum().asFSKOrNull(),
			CCUserScore.RATING_NO,
			Str.Empty,
			edReference.getValue(),
			edGroups.getValue(),
			CCTagList.EMPTY,
			edCvrControl.getResizedImageForStorage()
		);

		UserDataProblem.testSeriesData(ret, movielist, null, spack);

		return ret.isEmpty();
	}

	@Override
	public void onAMIEDIgnoreClicked() {
		try {
			onBtnOK(false);
		} catch (EnumValueNotFoundException e) {
			CCLog.addError(e);
		}
	}

	@Override
	public void setCover(BufferedImage nci) {
		edCvrControl.setCover(nci);
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
	public void setAnimeSeason(CCStringList animeSeason) {
		edAnimeSeason.setValues(animeSeason.ccstream().toList());
	}

	@Override
	public void setAnimeStudio(CCStringList animeStudio) {
		edAnimeStudio.setValues(animeStudio.ccstream().toList());
	}

	@Override
	public void onFinishInserting() {
		// nothing
	}

	@Override
	public void setScore(CCOnlineScore s) {
		spnOnlinescore.setValue(s);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        label1 = new JLabel();
        edTitle = new JTextField();
        label13 = new JLabel();
        edReference = new JReferenceChooser(movielist);
        label2 = new JLabel();
        spnOnlinescore = new OnlineScoreControl();
        label14 = new JLabel();
        edGroups = new GroupListEditor(movielist);
        label3 = new JLabel();
        cbxFSK = new CCEnumComboBox<>(CCOptionalFSK.getWrapper());
        label4 = new JLabel();
        cbxGenre0 = new CCEnumComboBox<>(CCGenre.getWrapper());
        button3 = new JButton();
        edCvrControl = new EditCoverControl(this, this);
        label5 = new JLabel();
        cbxGenre1 = new CCEnumComboBox<>(CCGenre.getWrapper());
        label6 = new JLabel();
        cbxGenre2 = new CCEnumComboBox<>(CCGenre.getWrapper());
        label7 = new JLabel();
        cbxGenre3 = new CCEnumComboBox<>(CCGenre.getWrapper());
        label8 = new JLabel();
        cbxGenre4 = new CCEnumComboBox<>(CCGenre.getWrapper());
        label9 = new JLabel();
        cbxGenre5 = new CCEnumComboBox<>(CCGenre.getWrapper());
        label10 = new JLabel();
        cbxGenre6 = new CCEnumComboBox<>(CCGenre.getWrapper());
        label11 = new JLabel();
        cbxGenre7 = new CCEnumComboBox<>(CCGenre.getWrapper());
        lblAnimeSeason = new JLabel();
        edAnimeSeason = new JAutoCompleteTextField(() -> movielist.getAnimeSeasonList(), true);
        lblAnimeStudio = new JLabel();
        edAnimeStudio = new JAutoCompleteTextField(() -> movielist.getAnimeStudioList(), true);
        panel1 = new JPanel();
        button1 = new JButton();
        button2 = new JButton();

        //======== this ========
        setTitle(LocaleBundle.getString("AddSeriesFrame.this.title")); //$NON-NLS-1$
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(null);
        var contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$rgap, default, $ugap, default:grow, $lcgap, 13dlu, 2*(default, $lcgap), default:grow, $lcgap", //$NON-NLS-1$
            "2*($rgap, default), $lgap, default, 30dlu, $ugap, 8*(default, $lgap), 10dlu, 2*($lgap, default), $lgap, 0dlu:grow, default, $lgap")); //$NON-NLS-1$

        //---- label1 ----
        label1.setText(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
        contentPane.add(label1, CC.xy(2, 2));
        contentPane.add(edTitle, CC.xy(4, 2));

        //---- label13 ----
        label13.setText(LocaleBundle.getString("AddMovieFrame.lblOnlineID.text")); //$NON-NLS-1$
        contentPane.add(label13, CC.xy(7, 2));
        contentPane.add(edReference, CC.xywh(9, 2, 3, 1, CC.DEFAULT, CC.CENTER));

        //---- label2 ----
        label2.setText(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
        contentPane.add(label2, CC.xy(2, 4));
        contentPane.add(spnOnlinescore, CC.xy(4, 4));

        //---- label14 ----
        label14.setText(LocaleBundle.getString("EditSeriesFrame.lblGroups.text")); //$NON-NLS-1$
        contentPane.add(label14, CC.xy(7, 4));
        contentPane.add(edGroups, CC.xywh(9, 4, 3, 1, CC.DEFAULT, CC.CENTER));

        //---- label3 ----
        label3.setText(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
        contentPane.add(label3, CC.xy(2, 6));
        contentPane.add(cbxFSK, CC.xy(4, 6));

        //---- label4 ----
        label4.setText(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
        contentPane.add(label4, CC.xy(2, 9));
        contentPane.add(cbxGenre0, CC.xy(4, 9));

        //---- button3 ----
        button3.setText(LocaleBundle.getString("AddSeriesFrame.btnParse.text")); //$NON-NLS-1$
        button3.setFont(button3.getFont().deriveFont(button3.getFont().getStyle() | Font.BOLD, button3.getFont().getSize() + 3f));
        button3.addActionListener(e -> onParseOnline(e));
        contentPane.add(button3, CC.xywh(9, 7, 3, 1, CC.DEFAULT, CC.FILL));
        contentPane.add(edCvrControl, CC.xywh(11, 9, 1, 19, CC.RIGHT, CC.TOP));

        //---- label5 ----
        label5.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
        contentPane.add(label5, CC.xy(2, 11));
        contentPane.add(cbxGenre1, CC.xy(4, 11));

        //---- label6 ----
        label6.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
        contentPane.add(label6, CC.xy(2, 13));
        contentPane.add(cbxGenre2, CC.xy(4, 13));

        //---- label7 ----
        label7.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
        contentPane.add(label7, CC.xy(2, 15));
        contentPane.add(cbxGenre3, CC.xy(4, 15));

        //---- label8 ----
        label8.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
        contentPane.add(label8, CC.xy(2, 17));
        contentPane.add(cbxGenre4, CC.xy(4, 17));

        //---- label9 ----
        label9.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
        contentPane.add(label9, CC.xy(2, 19));
        contentPane.add(cbxGenre5, CC.xy(4, 19));

        //---- label10 ----
        label10.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
        contentPane.add(label10, CC.xy(2, 21));
        contentPane.add(cbxGenre6, CC.xy(4, 21));

        //---- label11 ----
        label11.setText(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
        contentPane.add(label11, CC.xy(2, 23));
        contentPane.add(cbxGenre7, CC.xy(4, 23));

        //---- lblAnimeSeason ----
        lblAnimeSeason.setText(LocaleBundle.getString("AddSeriesFrame.lblAnimeSeason.text")); //$NON-NLS-1$
        contentPane.add(lblAnimeSeason, CC.xy(2, 27));
        contentPane.add(edAnimeSeason, CC.xy(4, 27));

        //---- lblAnimeStudio ----
        lblAnimeStudio.setText(LocaleBundle.getString("AddSeriesFrame.lblAnimeStudio.text")); //$NON-NLS-1$
        contentPane.add(lblAnimeStudio, CC.xy(2, 29));
        contentPane.add(edAnimeStudio, CC.xy(4, 29));

        //======== panel1 ========
        {
            panel1.setLayout(new FlowLayout());

            //---- button1 ----
            button1.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
            button1.addActionListener(e -> onOK(e));
            panel1.add(button1);

            //---- button2 ----
            button2.setText(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
            button2.addActionListener(e -> onCancel(e));
            panel1.add(button2);
        }
        contentPane.add(panel1, CC.xywh(2, 32, 10, 1));
        pack();
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel label1;
    private JTextField edTitle;
    private JLabel label13;
    private JReferenceChooser edReference;
    private JLabel label2;
    private OnlineScoreControl spnOnlinescore;
    private JLabel label14;
    private GroupListEditor edGroups;
    private JLabel label3;
    private CCEnumComboBox<CCOptionalFSK> cbxFSK;
    private JLabel label4;
    private CCEnumComboBox<CCGenre> cbxGenre0;
    private JButton button3;
    private EditCoverControl edCvrControl;
    private JLabel label5;
    private CCEnumComboBox<CCGenre> cbxGenre1;
    private JLabel label6;
    private CCEnumComboBox<CCGenre> cbxGenre2;
    private JLabel label7;
    private CCEnumComboBox<CCGenre> cbxGenre3;
    private JLabel label8;
    private CCEnumComboBox<CCGenre> cbxGenre4;
    private JLabel label9;
    private CCEnumComboBox<CCGenre> cbxGenre5;
    private JLabel label10;
    private CCEnumComboBox<CCGenre> cbxGenre6;
    private JLabel label11;
    private CCEnumComboBox<CCGenre> cbxGenre7;
    private JLabel lblAnimeSeason;
    private JAutoCompleteTextField edAnimeSeason;
    private JLabel lblAnimeStudio;
    private JAutoCompleteTextField edAnimeStudio;
    private JPanel panel1;
    private JButton button1;
    private JButton button2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
