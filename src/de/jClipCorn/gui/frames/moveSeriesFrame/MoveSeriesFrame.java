package de.jClipCorn.gui.frames.moveSeriesFrame;

import java.awt.Component;

import javax.swing.JFrame;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DialogHelper;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MoveSeriesFrame extends JFrame {
	private static final long serialVersionUID = 8795232362998343872L;
	
	private final CCSeries series;
	
	private JLabel lblReplace;
	private JTextField edSearch;
	private JLabel lblWith;
	private JTextField edReplace;
	private JButton btnNewButton;

	public MoveSeriesFrame(Component owner, CCSeries series) {
		super();
		setSize(new Dimension(225, 185));
		this.series = series;
		
		initGUI();
		
		setLocationRelativeTo(owner);
		
		init();
	}
	
	private void initGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setResizable(false);
		getContentPane().setLayout(null);
		
		lblReplace = new JLabel(LocaleBundle.getString("MoveSeriesFrame.lblReplace.text")); //$NON-NLS-1$
		lblReplace.setBounds(10, 11, 46, 14);
		getContentPane().add(lblReplace);
		
		edSearch = new JTextField();
		edSearch.setBounds(10, 36, 199, 20);
		getContentPane().add(edSearch);
		edSearch.setColumns(10);
		
		lblWith = new JLabel(LocaleBundle.getString("MoveSeriesFrame.lblWith.text")); //$NON-NLS-1$
		lblWith.setBounds(10, 67, 46, 14);
		getContentPane().add(lblWith);
		
		edReplace = new JTextField();
		edReplace.setBounds(10, 92, 199, 20);
		getContentPane().add(edReplace);
		edReplace.setColumns(10);
		
		btnNewButton = new JButton(LocaleBundle.getString("AddMovieFrame.btnOK.text")); //$NON-NLS-1$
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startReplace();
			}
		});
		btnNewButton.setBounds(61, 123, 89, 23);
		getContentPane().add(btnNewButton);
	}
	
	private void init() {
		setTitle(series.getTitle());
		edSearch.setText(getCommonPathStart());
	}
	
	private String getCommonPathStart() {
		for (int c = 0;;c++) {
			Character ckt = null;
			for (int seasi = 0; seasi < series.getSeasonCount(); seasi++) {
				CCSeason season = series.getSeason(seasi);
				for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
					if (c >= season.getEpisode(epi).getPart().length()) {
						return season.getEpisode(epi).getPart().substring(0, c);
					}
					
					if (ckt == null) {
						ckt = season.getEpisode(epi).getPart().charAt(c);
					} else {
						if (! ckt.equals(season.getEpisode(epi).getPart().charAt(c))) {
							return season.getEpisode(epi).getPart().substring(0, c);
						}
					}
				}
			}
		}
	}
	
	private void startReplace() {
		if (! DialogHelper.showLocaleYesNo(this, "Dialogs.MoveSeries")) { //$NON-NLS-1$
			return;
		}
		
		
		for (int seasi = 0; seasi < series.getSeasonCount(); seasi++) {
			CCSeason season = series.getSeason(seasi);
			for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
				CCEpisode ep = season.getEpisode(epi);
				
				ep.setPart(ep.getPart().replace(edSearch.getText(), edReplace.getText()));
			}
		}
		
		dispose();
	}
}
