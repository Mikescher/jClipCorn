package de.jClipCorn.gui.frames.addSeriesFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.editSeriesFrame.EditSeriesFrame;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.frames.parseImDBFrame.ParseImDBDialog;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.guiComponents.referenceChooser.JReferenceChooser;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.parser.ParseResultHandler;
import de.jClipCorn.util.userdataProblem.UserDataProblem;
import de.jClipCorn.util.userdataProblem.UserDataProblemHandler;

public class AddSeriesFrame extends JFrame implements ParseResultHandler, UserDataProblemHandler {
	private static final long serialVersionUID = -4500039578109890172L;
	
	private CCMovieList movieList;
	
	private JLabel label;
	private JComboBox<String> cbxGenre0;
	private JComboBox<String> cbxGenre1;
	private JComboBox<String> cbxGenre2;
	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;
	private JComboBox<String> cbxGenre3;
	private JComboBox<String> cbxGenre4;
	private JLabel label_4;
	private JLabel label_5;
	private JComboBox<String> cbxGenre5;
	private JComboBox<String> cbxGenre6;
	private JLabel label_6;
	private JLabel label_7;
	private JComboBox<String> cbxGenre7;
	private JButton btnParse;
	private JLabel label_8;
	private JTextField edTitle;
	private JLabel label_10;
	private JComboBox<String> cbxLanguage;
	private JLabel label_11;
	private JComboBox<String> cbxFSK;
	private JLabel label_12;
	private JSpinner spnOnlinescore;
	private JLabel label_13;
	private JButton btnOK;
	private JButton btnCancel;
	private EditCoverControl edCvrControl;
	private JLabel label_9;
	private JReferenceChooser edReference;

	public AddSeriesFrame(Component owner, CCMovieList mlist) {
		setSize(new Dimension(620, 545));
		movieList = mlist;
		
		setResizable(false);
		
		initGUI();
		setDefaultValues();
		
		setLocationRelativeTo(owner);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("AddSeriesFrame.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);

		label = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre.text")); //$NON-NLS-1$
		label.setBounds(10, 194, 59, 16);
		getContentPane().add(label);

		cbxGenre0 = new JComboBox<>();
		cbxGenre0.setBounds(87, 188, 212, 22);
		getContentPane().add(cbxGenre0);

		cbxGenre1 = new JComboBox<>();
		cbxGenre1.setBounds(87, 224, 212, 22);
		getContentPane().add(cbxGenre1);

		cbxGenre2 = new JComboBox<>();
		cbxGenre2.setBounds(87, 260, 212, 22);
		getContentPane().add(cbxGenre2);

		label_1 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_2.text")); //$NON-NLS-1$
		label_1.setBounds(10, 266, 59, 16);
		getContentPane().add(label_1);

		label_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_1.text")); //$NON-NLS-1$
		label_2.setBounds(10, 230, 59, 16);
		getContentPane().add(label_2);

		label_3 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_3.text")); //$NON-NLS-1$
		label_3.setBounds(10, 301, 59, 16);
		getContentPane().add(label_3);

		cbxGenre3 = new JComboBox<>();
		cbxGenre3.setBounds(87, 295, 212, 22);
		getContentPane().add(cbxGenre3);

		cbxGenre4 = new JComboBox<>();
		cbxGenre4.setBounds(87, 330, 212, 22);
		getContentPane().add(cbxGenre4);

		label_4 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_4.text")); //$NON-NLS-1$
		label_4.setBounds(10, 336, 59, 16);
		getContentPane().add(label_4);

		label_5 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_5.text")); //$NON-NLS-1$
		label_5.setBounds(10, 372, 59, 16);
		getContentPane().add(label_5);

		cbxGenre5 = new JComboBox<>();
		cbxGenre5.setBounds(87, 366, 212, 22);
		getContentPane().add(cbxGenre5);

		cbxGenre6 = new JComboBox<>();
		cbxGenre6.setBounds(87, 402, 212, 22);
		getContentPane().add(cbxGenre6);

		label_6 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_6.text")); //$NON-NLS-1$
		label_6.setBounds(10, 408, 59, 16);
		getContentPane().add(label_6);

		label_7 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblGenre_7.text")); //$NON-NLS-1$
		label_7.setBounds(10, 443, 59, 16);
		getContentPane().add(label_7);

		cbxGenre7 = new JComboBox<>();
		cbxGenre7.setBounds(87, 437, 212, 22);
		getContentPane().add(cbxGenre7);

		btnParse = new JButton(LocaleBundle.getString("AddSeriesFrame.btnParse.text")); //$NON-NLS-1$
		btnParse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showIMDBParser();
			}
		});
		btnParse.setFont(new Font("Tahoma", Font.BOLD, 15)); //$NON-NLS-1$
		btnParse.setBounds(381, 14, 212, 42);
		getContentPane().add(btnParse);

		label_8 = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		label_8.setBounds(10, 14, 52, 16);
		getContentPane().add(label_8);

		edTitle = new JTextField();
		edTitle.setColumns(10);
		edTitle.setBounds(87, 12, 212, 20);
		getContentPane().add(edTitle);

		label_10 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblSprache.text")); //$NON-NLS-1$
		label_10.setBounds(10, 50, 59, 16);
		getContentPane().add(label_10);

		cbxLanguage = new JComboBox<>();
		cbxLanguage.setBounds(87, 47, 212, 22);
		getContentPane().add(cbxLanguage);

		label_11 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblFsk.text")); //$NON-NLS-1$
		label_11.setBounds(10, 157, 71, 16);
		getContentPane().add(label_11);

		cbxFSK = new JComboBox<>();
		cbxFSK.setBounds(87, 154, 212, 22);
		getContentPane().add(cbxFSK);

		label_12 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlinescore.text")); //$NON-NLS-1$
		label_12.setBounds(10, 122, 87, 16);
		getContentPane().add(label_12);

		spnOnlinescore = new JSpinner();
		spnOnlinescore.setModel(new SpinnerNumberModel(0, 0, 10, 1));
		spnOnlinescore.setBounds(106, 121, 193, 20);
		getContentPane().add(spnOnlinescore);

		label_13 = new JLabel("/ 10"); //$NON-NLS-1$
		label_13.setBounds(311, 122, 52, 16);
		getContentPane().add(label_13);

		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnOK(true);
			}
		});
		btnOK.setBounds(197, 484, 95, 25);
		getContentPane().add(btnOK);

		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		btnCancel.setBounds(311, 484, 95, 25);
		getContentPane().add(btnCancel);
		
		edCvrControl = new EditCoverControl(this, this);
		edCvrControl.setBounds(381, 67, EditCoverControl.CTRL_WIDTH, EditCoverControl.CTRL_HEIGHT);
		getContentPane().add(edCvrControl);
		
		label_9 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblOnlineID.text")); //$NON-NLS-1$
		label_9.setBounds(10, 86, 87, 16);
		getContentPane().add(label_9);
		
		edReference = new JReferenceChooser();
		edReference.setBounds(87, 86, 212, 20);
		getContentPane().add(edReference);
	}
	
	private void setDefaultValues() {
		cbxLanguage.setModel(new DefaultComboBoxModel<>(CCMovieLanguage.getList()));
		
		DefaultComboBoxModel<String> cbFSKdcbm;
		cbxFSK.setModel(cbFSKdcbm = new DefaultComboBoxModel<>(CCMovieFSK.getList()));
		cbFSKdcbm.addElement(" "); //$NON-NLS-1$
		cbxFSK.setSelectedIndex(cbFSKdcbm.getSize() - 1);
		
		cbxGenre0.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre1.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre2.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre3.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre4.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre5.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre6.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));
		cbxGenre7.setModel(new DefaultComboBoxModel<>(CCMovieGenre.getTrimmedList()));

	}

	@Override
	public void setCover(BufferedImage nci) {
		edCvrControl.setCover(nci);
	}
	
	private void showIMDBParser() {
		(new ParseImDBDialog(this, this, CCMovieTyp.SERIES)).setVisible(true);
	}

	@Override
	public String getFullTitle() {
		return edTitle.getText();
	}

	@Override
	public void setMovieFormat(CCMovieFormat cmf) {
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
	public void setFilesize(long size) {
		// No such field
	}

	@Override
	public void setMovieLanguage(CCMovieLanguage lang) {
		cbxLanguage.setSelectedIndex(lang.asInt());
	}

	@Override
	public void setQuality(CCMovieQuality q) {
		// No such field
	}

	@Override
	public void setYear(int y) {
		// No such field
	}

	@Override
	public void setGenre(int gid, int movGenre) {
		switch (gid) {
		case 0:
			cbxGenre0.setSelectedIndex(movGenre);
			break;
		case 1:
			cbxGenre1.setSelectedIndex(movGenre);
			break;
		case 2:
			cbxGenre2.setSelectedIndex(movGenre);
			break;
		case 3:
			cbxGenre3.setSelectedIndex(movGenre);
			break;
		case 4:
			cbxGenre4.setSelectedIndex(movGenre);
			break;
		case 5:
			cbxGenre5.setSelectedIndex(movGenre);
			break;
		case 6:
			cbxGenre6.setSelectedIndex(movGenre);
			break;
		case 7:
			cbxGenre7.setSelectedIndex(movGenre);
			break;
		}
	}

	@Override
	public void setFSK(int fsk) {
		cbxFSK.setSelectedIndex(fsk);
	}

	@Override
	public void setLength(int l) {
		// No such field
	}
	
	@Override
	public void setOnlineReference(CCOnlineReference ref) {
		// No such field
	}
	
	@Override
	public void onFinishInserting() {
		// nothing
	}

	@Override
	public void setScore(int s) {
		spnOnlinescore.setValue(s);
	}
	
	private void cancel() {
		this.dispose();
	}
	
	private void onBtnOK(boolean check) {
		List<UserDataProblem> problems = new ArrayList<>();

		// some problems are too fatal
		if (! edCvrControl.isCoverSet()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER));
			check = false;
		}
		if (edTitle.getText().isEmpty()) {
			problems.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
			check = false;
		}
		
		boolean probvalue = check && checkUserData(problems);
		
		if (! probvalue) {
			InputErrorDialog amied = new InputErrorDialog(problems, this, this);
			amied.setVisible(true);
			return;
		}
		
		CCSeries newS = movieList.createNewEmptySeries();
		
		newS.beginUpdating();
		
		//#####################################################################################
		
		newS.setTitle(edTitle.getText());
		
		newS.setLanguage(cbxLanguage.getSelectedIndex());
		
		newS.setOnlinescore((int) spnOnlinescore.getValue());
		
		newS.setFsk(cbxFSK.getSelectedIndex());
		
		newS.setOnlineReference(edReference.getValue());
		
		newS.setGenre(CCMovieGenre.find(cbxGenre0.getSelectedIndex()), 0);
		newS.setGenre(CCMovieGenre.find(cbxGenre1.getSelectedIndex()), 1);
		newS.setGenre(CCMovieGenre.find(cbxGenre2.getSelectedIndex()), 2);
		newS.setGenre(CCMovieGenre.find(cbxGenre3.getSelectedIndex()), 3);
		newS.setGenre(CCMovieGenre.find(cbxGenre4.getSelectedIndex()), 4);
		newS.setGenre(CCMovieGenre.find(cbxGenre5.getSelectedIndex()), 5);
		newS.setGenre(CCMovieGenre.find(cbxGenre6.getSelectedIndex()), 6);
		newS.setGenre(CCMovieGenre.find(cbxGenre7.getSelectedIndex()), 7);
		
		newS.setCover(edCvrControl.getResizedImage());
		
		//#####################################################################################
		
		newS.endUpdating();
		
		EditSeriesFrame esf = new EditSeriesFrame(this, newS, null);
		esf.setVisible(true);
		
		dispose();
	}
	
	public boolean checkUserData(List<UserDataProblem> ret) {
		String title = edTitle.getText();
		
		int oscore = (int) spnOnlinescore.getValue();
		
		int fskidx = cbxFSK.getSelectedIndex();
		
		int gen0 = cbxGenre0.getSelectedIndex();
		int gen1 = cbxGenre1.getSelectedIndex();
		int gen2 = cbxGenre2.getSelectedIndex();
		int gen3 = cbxGenre3.getSelectedIndex();
		int gen4 = cbxGenre4.getSelectedIndex();
		int gen5 = cbxGenre5.getSelectedIndex();
		int gen6 = cbxGenre6.getSelectedIndex();
		int gen7 = cbxGenre7.getSelectedIndex();
		
		CCOnlineReference ref = edReference.getValue();
		
		UserDataProblem.testSeriesData(ret, edCvrControl.getResizedImage(), title, oscore, gen0, gen1, gen2, gen3, gen4, gen5, gen6, gen7, fskidx, ref);
		
		return ret.isEmpty();
	}
	
	@Override
	public void onAMIEDIgnoreClicked() {
		onBtnOK(false);
	}
}
