package de.jClipCorn.gui.frames.addSeasonFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.frames.inputErrorFrame.InputErrorDialog;
import de.jClipCorn.gui.guiComponents.editCoverControl.EditCoverControl;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.helper.ExtendedFocusTraversalOnArray;
import de.jClipCorn.util.listener.UpdateCallbackListener;
import de.jClipCorn.util.parser.onlineparser.ParseResultHandler;
import de.jClipCorn.util.userdataProblem.UserDataProblem;
import de.jClipCorn.util.userdataProblem.UserDataProblemHandler;

public class AddSeasonFrame extends JFrame implements UserDataProblemHandler, ParseResultHandler {
	private static final long serialVersionUID = -5479523926638394942L;
	
	private final CCSeries parent;
	
	private final UpdateCallbackListener listener;
	
	private JLabel label;
	private JTextField edTitle;
	private JButton btnCancel;
	private JButton btnOK;
	private JLabel label_2;
	private JSpinner spnYear;
	private EditCoverControl edCvrControl;

	public AddSeasonFrame(Component owner, CCSeries ser, UpdateCallbackListener ucl){
		super();
		setSize(new Dimension(500, 400));
		this.parent = ser;
		this.listener = ucl;
		
		initGUI();
		
		setLocationRelativeTo(owner);
		setFocusTraversalPolicy(new ExtendedFocusTraversalOnArray(new Component[]{edTitle, spnYear, btnOK, btnCancel}));
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getFormattedString("AddSeasonFrame.this.title", parent.getTitle())); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		getContentPane().setLayout(null);
		
		label = new JLabel(LocaleBundle.getString("AddMovieFrame.label_1.text")); //$NON-NLS-1$
		label.setBounds(12, 15, 52, 16);
		getContentPane().add(label);
		
		edTitle = new JTextField();
		edTitle.setColumns(10);
		edTitle.setBounds(76, 13, 212, 20);
		getContentPane().add(edTitle);
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		btnCancel.setBounds(244, 334, 95, 25);
		getContentPane().add(btnCancel);
		
		btnOK = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnOK(true);
			}
		});
		btnOK.setBounds(137, 334, 95, 25);
		getContentPane().add(btnOK);
		
		label_2 = new JLabel(LocaleBundle.getString("AddMovieFrame.lblYear.text")); //$NON-NLS-1$
		label_2.setBounds(12, 48, 52, 16);
		getContentPane().add(label_2);
		
		spnYear = new JSpinner();
		spnYear.setModel(new SpinnerNumberModel(1900, 1900, null, 1));
		spnYear.setEditor(new JSpinner.NumberEditor(spnYear, "0")); //$NON-NLS-1$
		spnYear.setBounds(76, 44, 212, 20);
		getContentPane().add(spnYear);
		
		edCvrControl = new EditCoverControl(this, this);
		edCvrControl.setBounds(298, 16, EditCoverControl.CTRL_WIDTH, EditCoverControl.CTRL_HEIGHT);
		getContentPane().add(edCvrControl);
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

		newS.setTitle(edTitle.getText());
		newS.setYear((int) spnYear.getValue());
		newS.setCover(edCvrControl.getResizedImage());
		
		//#####################################################################################
		
		newS.endUpdating();
		
		if (listener != null) {
			listener.onUpdate(null);
		}
		
		dispose();
	}
	
	public boolean checkUserData(List<UserDataProblem> ret) {
		String title = edTitle.getText();
		int year = (int) spnYear.getValue();

		UserDataProblem.testSeasonData(ret, edCvrControl.getResizedImage(), title, year);
		
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
	public CCOnlineReference getSearchReference() {
		return CCOnlineReference.createNone();
	}

	@Override
	public void setMovieFormat(CCMovieFormat cmf) {
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
	public void setFilesize(long size) {
		// NOP
	}

	@Override
	public void setMovieLanguage(CCMovieLanguage lang) {
		// NOP
	}

	@Override
	public void setQuality(CCMovieQuality q) {
		// NOP
	}

	@Override
	public void setYear(int y) {
		// NOP
	}

	@Override
	public void setGenre(int gid, int movGenre) {
		// NOP
	}

	@Override
	public void setFSK(int fsk) {
		// NOP
	}

	@Override
	public void setLength(int l) {
		// NOP
	}

	@Override
	public void setScore(int s) {
		// NOP
	}
	
	@Override
	public void setOnlineReference(CCOnlineReference ref) {
		// NOP
	}
	
	@Override
	public void onFinishInserting() {
		// NOP
	}
}
