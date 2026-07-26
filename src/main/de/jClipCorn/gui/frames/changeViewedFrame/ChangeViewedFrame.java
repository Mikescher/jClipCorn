package de.jClipCorn.gui.frames.changeViewedFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.PropertyCheckbox;
import de.jClipCorn.gui.guiComponents.cover.CoverLabel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.datetime.CCDateTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ChangeViewedFrame extends JCCFrame {
	private static final long serialVersionUID = 9048482551231383355L;

	private int position;
	private boolean running = false;

	public ChangeViewedFrame(Component owner, CCMovieList ml) {
		super(ml);

		initComponents();

		initMap();
		setFocusable(true);

		setLocationRelativeTo(owner);
		init();
	}

	private void init() {
		position = -1;
		running = true;

		nextMovie();
	}

	private void onViewed()   { actionNextMovie(true); }
	private void onUnviewed() { actionNextMovie(false); }

	private void actionNextMovie(boolean viewed) {
		if (! running) return;

		CCMovie mov = movielist.getDatabaseElementBySort(position).asMovie();
		if (viewed && !mov.isViewed()) mov.ViewedHistory.add(CCDateTime.getUnspecified());
		if (!viewed) mov.ViewedHistory.set(CCDateTimeList.createEmpty());

		nextMovie();
	}

	private void nextMovie() {
		position++;

		if (position < movielist.getElementCount()) {
			CCDatabaseElement del = movielist.getDatabaseElementBySort(position);
			if (del.isMovie()) {
				CCMovie mov = del.asMovie();

				if (cbOnlyUnviewed.isSelected()) {
					if (mov.isViewed()) {
						nextMovie();
						return;
					}
				}

				lblCover.setAndResizeCover(mov.getCover());
				lblCurrent.setIcon((mov.isViewed() ? (Resources.ICN_TABLE_VIEWED_TRUE.get()) : (Resources.ICN_TABLE_VIEWED_FALSE.get())));
				lblTitle.setText(mov.getCompleteTitle());
			} else {
				nextMovie();
				return;
			}
		} else {
			btnUnviewed.setEnabled(false);
			btnViewed.setEnabled(false);
			running = false;
			return;
		}
	}

	@SuppressWarnings("nls")
	private void initMap() {
		InputMap map = ((JPanel) getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap act = ((JPanel) getContentPane()).getActionMap();

		map.put(KeyStroke.getKeyStroke('1'), "KEYPRESSED_1");
		map.put(KeyStroke.getKeyStroke('2'), "KEYPRESSED_2");

		act.put("KEYPRESSED_1", new AbstractAction() {
			private static final long serialVersionUID = -4772892852387370715L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(true);
			}
		});
		act.put("KEYPRESSED_2", new AbstractAction() {
			private static final long serialVersionUID = -4772892852387370715L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				actionNextMovie(false);
			}
		});
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		lblTitle = new JLabel();
		lblCover = new CoverLabel(movielist, false);
		lblCurrent = new JLabel();
		btnViewed = new JButton();
		btnUnviewed = new JButton();
		lblKeyViewed = new JLabel();
		lblKeyUnviewed = new JLabel();
		cbOnlyUnviewed = new PropertyCheckbox(ccprops().PROP_MASSCHANGEVIEWED_ONLYUNVIEWED);

		//======== this ========
		setTitle(LocaleBundle.getString("ChangedViewedFrame.this.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"2*($ugap, default:grow), $ugap",
			"$ugap, 4*(default, $lgap), 2*(default, $ugap)"));

		//---- lblTitle ----
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblTitle, CC.xywh(2, 2, 3, 1, CC.FILL, CC.DEFAULT));
		contentPane.add(lblCover, CC.xywh(2, 4, 3, 1, CC.CENTER, CC.TOP));

		//---- lblCurrent ----
		lblCurrent.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblCurrent, CC.xywh(2, 6, 3, 1, CC.FILL, CC.DEFAULT));

		//---- btnViewed ----
		btnViewed.setText(LocaleBundle.getString("FilterTree.Viewed.Viewed"));
		btnViewed.addActionListener(e -> onViewed());
		contentPane.add(btnViewed, CC.xy(2, 8, CC.FILL, CC.DEFAULT));

		//---- btnUnviewed ----
		btnUnviewed.setText(LocaleBundle.getString("FilterTree.Viewed.Unviewed"));
		btnUnviewed.addActionListener(e -> onUnviewed());
		contentPane.add(btnUnviewed, CC.xy(4, 8, CC.FILL, CC.DEFAULT));

		//---- lblKeyViewed ----
		lblKeyViewed.setText("(1)");
		lblKeyViewed.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblKeyViewed, CC.xy(2, 10, CC.FILL, CC.DEFAULT));

		//---- lblKeyUnviewed ----
		lblKeyUnviewed.setText("(2)");
		lblKeyUnviewed.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblKeyUnviewed, CC.xy(4, 10, CC.FILL, CC.DEFAULT));
		contentPane.add(cbOnlyUnviewed, CC.xywh(2, 12, 3, 1));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel lblTitle;
	private CoverLabel lblCover;
	private JLabel lblCurrent;
	private JButton btnViewed;
	private JButton btnUnviewed;
	private JLabel lblKeyViewed;
	private JLabel lblKeyUnviewed;
	private PropertyCheckbox cbOnlyUnviewed;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
