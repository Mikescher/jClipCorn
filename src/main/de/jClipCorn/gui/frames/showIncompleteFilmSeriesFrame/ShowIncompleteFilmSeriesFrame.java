package de.jClipCorn.gui.frames.showIncompleteFilmSeriesFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.localization.LocaleBundle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ShowIncompleteFilmSeriesFrame extends JCCDialog {
	private static final long serialVersionUID = 4396672523833844038L;
	
	private JPanel contentPane;

	private JList<MissingZyklusElement> listMain;
	private JPanel pnlBottom;
	private JButton btnRescan;
	private JScrollPane scrlMain;

	public ShowIncompleteFilmSeriesFrame(Component owner, CCMovieList ml) {
		super(ml);
		setMinimumSize(new Dimension(350, 350));
		setSize(new Dimension(450, 550));
		initGUI();
		setLocationRelativeTo(owner);
		
		scan();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("ShowIncompleteFilmSeriesFrame.this.title")); //$NON-NLS-1$

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		pnlBottom = new JPanel();
		contentPane.add(pnlBottom, BorderLayout.SOUTH);
		
		btnRescan = new JButton(LocaleBundle.getString("ShowIncompleteFilmSeriesFrame.btnRescan")); //$NON-NLS-1$
		btnRescan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scan();
			}
		});
		pnlBottom.add(btnRescan);
		
		scrlMain = new JScrollPane();
		scrlMain.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrlMain, BorderLayout.CENTER);
		
		listMain = new JList<>();
		listMain.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && listMain.getSelectedValue() != null) {
					PreviewMovieFrame.show(ShowIncompleteFilmSeriesFrame.this, listMain.getSelectedValue().target, true);
				}
			}
		});
		scrlMain.setViewportView(listMain);
	}

	private void scan() {
		DefaultListModel<MissingZyklusElement> mdl = new DefaultListModel<>();
		
		Map<String, List<CCMovie>> zyklusList = movielist.listAllZyklus();

		for (Entry<String, List<CCMovie>> zyklus : zyklusList.entrySet()) {
			int maxZyklusNumber = 0;
			for (CCMovie m : zyklus.getValue()) maxZyklusNumber = Math.max(maxZyklusNumber, m.getZyklus().getNumber());

			CCMovie firstMov = zyklus.getValue().get(0);
			for (CCMovie m : zyklus.getValue()) {
				if (m.getZyklus().getNumber() < firstMov.getZyklus().getNumber()) firstMov = m;
			}
			
			for (int zid = 1; zid < maxZyklusNumber; zid++) {
				boolean found = false;
				for (CCMovie m : zyklus.getValue()) {
					if (m.getZyklus().getNumber() == zid) {
						found = true;
						break;
					}
				}
				
				if (!found) mdl.addElement(new MissingZyklusElement(new CCMovieZyklus(firstMov.getZyklus().getTitle(), zid), firstMov));
			}
		}
		
		listMain.setModel(mdl);
	}
}
