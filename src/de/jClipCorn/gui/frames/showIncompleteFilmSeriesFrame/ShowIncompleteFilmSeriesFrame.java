package de.jClipCorn.gui.frames.showIncompleteFilmSeriesFrame;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ShowIncompleteFilmSeriesFrame extends JDialog {
	private static final long serialVersionUID = 4396672523833844038L;
	
	private JPanel contentPane;

	private CCMovieList movielist;
	private JList<MissingZyklusElement> listMain;
	private JPanel pnlBottom;
	private JButton btnRescan;
	private JScrollPane scrlMain;

	public ShowIncompleteFilmSeriesFrame(Component owner, CCMovieList list) {
		super();
		setMinimumSize(new Dimension(350, 350));
		setSize(new Dimension(450, 550));
		this.movielist = list;
		initGUI();
		setLocationRelativeTo(owner);
		
		scan();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("ShowIncompleteFilmSeriesFrame.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
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
					new PreviewMovieFrame(ShowIncompleteFilmSeriesFrame.this, listMain.getSelectedValue().target).setVisible(true);
				}
			}
		});
		scrlMain.setViewportView(listMain);
	}

	private void scan() {
		DefaultListModel<MissingZyklusElement> mdl = new DefaultListModel<>();
		
		HashMap<String, List<CCMovie>> zyklusList = movielist.listAllZyklus();

		zyklusLoop: 
			for (Entry<String, List<CCMovie>> zyklus : zyklusList.entrySet()) {
				int maxZyklusNumber = 0;
				for (CCMovie m : zyklus.getValue()) maxZyklusNumber = Math.max(maxZyklusNumber, m.getZyklus().getNumber());

				for (int zid = 1; zid < maxZyklusNumber; zid++) {
					CCMovie firstMov = zyklus.getValue().get(0);
					for (CCMovie m : zyklus.getValue()) {
						if (m.getZyklus().getNumber() < firstMov.getZyklus().getNumber()) firstMov = m;
						if (m.getZyklus().getNumber() == zid) continue zyklusLoop;
					}
					
					mdl.addElement(new MissingZyklusElement(new CCMovieZyklus(firstMov.getZyklus().getTitle(), zid), firstMov));
				}
			}
		
		listMain.setModel(mdl);
	}
}
