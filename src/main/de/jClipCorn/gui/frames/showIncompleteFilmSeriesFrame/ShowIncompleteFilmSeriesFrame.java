package de.jClipCorn.gui.frames.showIncompleteFilmSeriesFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.localization.LocaleBundle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ShowIncompleteFilmSeriesFrame extends JCCDialog {
	private static final long serialVersionUID = 4396672523833844038L;

	public ShowIncompleteFilmSeriesFrame(Component owner, CCMovieList ml) {
		super(ml);

		initComponents();
		postInit();

		setLocationRelativeTo(owner);

		scan();
	}

	private void postInit() {
		setMinimumSize(new Dimension(350, 350));
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

	private void onListMouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && listMain.getSelectedValue() != null) {
			PreviewMovieFrame.show(this, listMain.getSelectedValue().target, true);
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		scrlMain = new JScrollPane();
		listMain = new JList<>();
		pnlBottom = new JPanel();
		btnRescan = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("ShowIncompleteFilmSeriesFrame.this.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $ugap",
			"$ugap, default:grow, $lgap, default, $ugap"));

		//======== scrlMain ========
		{
			scrlMain.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			//---- listMain ----
			listMain.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					onListMouseClicked(e);
				}
			});
			scrlMain.setViewportView(listMain);
		}
		contentPane.add(scrlMain, CC.xy(2, 2, CC.FILL, CC.FILL));

		//======== pnlBottom ========
		{
			pnlBottom.setLayout(new FlowLayout());

			//---- btnRescan ----
			btnRescan.setText(LocaleBundle.getString("ShowIncompleteFilmSeriesFrame.btnRescan"));
			btnRescan.addActionListener(e -> scan());
			pnlBottom.add(btnRescan);
		}
		contentPane.add(pnlBottom, CC.xy(2, 4, CC.FILL, CC.FILL));
		setSize(450, 550);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JScrollPane scrlMain;
	private JList<MissingZyklusElement> listMain;
	private JPanel pnlBottom;
	private JButton btnRescan;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
