package de.jClipCorn.gui.frames.compareDatabaseFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

public class CompareDatabaseFrame extends JFrame
{
	private final CCMovieList movielist;

	private CompareState state = null;

	public CompareDatabaseFrame(Component owner, CCMovieList ml)
	{
		super();

		movielist = ml;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setTitle(LocaleBundle.getString("CompareDatabaseFrame.this.title")); //$NON-NLS-1$

		edDatabaseName.setText(CCProperties.getInstance().PROP_DATABASE_NAME.getDefault());

		updateUI();
	}

	private void updateUI()
	{
		btnCompare.setEnabled(!Str.isNullOrWhitespace(edDatabasePath.getText()));
		btnCreatePatch.setEnabled(state != null);
	}

	private void openDatabase(ActionEvent e)
	{
		state = null;

		final JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setCurrentDirectory(new File(PathFormatter.getRealSelfDirectory()));

		if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) { updateUI(); return; }

		edDatabasePath.setText(chooser.getSelectedFile().getAbsolutePath());
		updateUI();
	}

	private void startComparison(ActionEvent ae)
	{
		try
		{
			compare();
		}
		catch (Throwable e)
		{
			DialogHelper.showDispatchError(this, "Error", e.toString()); //$NON-NLS-1$
		}

		updateUI();
	}

	private void compare() throws Exception {
		var mlExt = CCMovieList.loadExtern(null, edDatabasePath.getText(), edDatabaseName.getText(), true);

		if (!mlExt.databaseExists()) throw new Exception("Database " + edDatabasePath.getText() + " | " + edDatabaseName.getText() + " not found");

		mlExt.connectExternal(false);
		try
		{
			compare(mlExt);
		}
		finally
		{
			mlExt.disconnectDatabase(true);
		}
	}

	private void compare(CCMovieList mlExt) throws Exception
	{
		var mlLoc = movielist;

		var state = new CompareState();

		{
			var movsLoc = mlLoc.iteratorMovies().toList();
			var movsExt = mlExt.iteratorMovies().toList();

			// Movies with the same checksum are matches
			for (var mloc : new ArrayList<>(movsLoc))
			{
				if (mloc.MediaInfo.get().isUnset() || Str.isNullOrWhitespace(mloc.MediaInfo.get().getChecksum())) continue;

				var mext = CCStreams.iterate(movsExt).singleOrDefault(m -> Str.equals(m.MediaInfo.get().getChecksum(), mloc.MediaInfo.get().getChecksum()), null, null);
				if (mext == null) continue;

				state.Movies.add(new CompareState.MovieMatch(mloc, mext));
				movsLoc.remove(mloc);
				movsExt.remove(mext);
			}

			// Movies with (exactly) the same online-refs + same language
			for (var mloc : new ArrayList<>(movsLoc))
			{
				if (mloc.OnlineReference.get().totalCount() == 0) continue;

				var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
				{
					return m.OnlineReference.get().totalCount() > 0 && mloc.OnlineReference.get().equalsAnyOrder(m.OnlineReference.get()) && mloc.Language.get().isEqual(m.Language.get());
				}, null, null);
				if (mext == null) continue;

				state.Movies.add(new CompareState.MovieMatch(mloc, mext));
				movsLoc.remove(mloc);
				movsExt.remove(mext);
			}

			// Movies with (exactly) the same online-refs (but evtl different language)
			for (var mloc : new ArrayList<>(movsLoc))
			{
				if (mloc.OnlineReference.get().totalCount() == 0) continue;

				var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
				{
					return m.OnlineReference.get().totalCount() > 0 && mloc.OnlineReference.get().equalsAnyOrder(m.OnlineReference.get());
				}, null, null);
				if (mext == null) continue;

				state.Movies.add(new CompareState.MovieMatch(mloc, mext));
				movsLoc.remove(mloc);
				movsExt.remove(mext);
			}

			// Movies with (exactly) the same online-refs
			for (var mloc : new ArrayList<>(movsLoc))
			{
				if (mloc.OnlineReference.get().totalCount() == 0) continue;

				var mext = CCStreams.iterate(movsExt).singleOrDefault(m -> m.OnlineReference.get().totalCount() > 0 && mloc.OnlineReference.get().equalsAnyOrder(m.OnlineReference.get()), null, null);
				if (mext == null) continue;

				state.Movies.add(new CompareState.MovieMatch(mloc, mext));
				movsLoc.remove(mloc);
				movsExt.remove(mext);
			}

			// Movies with which contains at least 1 online ref with the same value
			for (var mloc : new ArrayList<>(movsLoc))
			{
				if (mloc.OnlineReference.get().totalCount() == 0) continue;

				var mext = CCStreams.iterate(movsExt).singleOrDefault(m -> m.OnlineReference.get().totalCount() > 0 && mloc.OnlineReference.get().equalsAnyNonEmptySubset(m.OnlineReference.get()), null, null);
				if (mext == null) continue;

				state.Movies.add(new CompareState.MovieMatch(mloc, mext));
				movsLoc.remove(mloc);
				movsExt.remove(mext);
			}

			for (var mloc : movsLoc)
			{
				state.Movies.add(new CompareState.MovieMatch(mloc, null));
			}
			movsLoc.clear();

			for (var mext : movsExt)
			{
				state.Movies.add(new CompareState.MovieMatch(null, mext));
			}
			movsExt.clear();
		}

	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		edDatabasePath = new ReadableTextField();
		btnOpenDatabase = new JButton();
		label2 = new JLabel();
		edDatabaseName = new JTextField();
		label1 = new JLabel();
		scrollPane1 = new JScrollPane();
		edRules = new JTextArea();
		btnCompare = new JButton();
		tabbedPane1 = new JTabbedPane();
		panel1 = new JPanel();
		panel2 = new JPanel();
		panel3 = new JPanel();
		panel4 = new JPanel();
		panel5 = new JPanel();
		btnCreatePatch = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("CompareDatabaseFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $lcgap, default:grow, $lcgap, default, $ugap", //$NON-NLS-1$
			"$ugap, 3*(default, $lgap), default:grow, $lgap, default, $lgap, default:grow, $lgap, default, $ugap")); //$NON-NLS-1$
		contentPane.add(edDatabasePath, CC.xywh(2, 2, 3, 1, CC.FILL, CC.FILL));

		//---- btnOpenDatabase ----
		btnOpenDatabase.setText("..."); //$NON-NLS-1$
		btnOpenDatabase.addActionListener(e -> openDatabase(e));
		contentPane.add(btnOpenDatabase, CC.xy(6, 2));

		//---- label2 ----
		label2.setText(LocaleBundle.getString("BatchEditFrame.lblDBName")); //$NON-NLS-1$
		contentPane.add(label2, CC.xy(2, 4));
		contentPane.add(edDatabaseName, CC.xy(4, 4));

		//---- label1 ----
		label1.setText("Rules:"); //$NON-NLS-1$
		contentPane.add(label1, CC.xywh(2, 6, 3, 1));

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(edRules);
		}
		contentPane.add(scrollPane1, CC.xywh(2, 8, 5, 1, CC.FILL, CC.FILL));

		//---- btnCompare ----
		btnCompare.setText(LocaleBundle.getString("BatchEditFrame.btnCompare")); //$NON-NLS-1$
		btnCompare.addActionListener(e -> startComparison(e));
		contentPane.add(btnCompare, CC.xywh(2, 10, 5, 1));

		//======== tabbedPane1 ========
		{

			//======== panel1 ========
			{
				panel1.setLayout(new BorderLayout());
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.tabDelete"), panel1); //$NON-NLS-1$

			//======== panel2 ========
			{
				panel2.setLayout(new BorderLayout());
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.tabUpdateMeta"), panel2); //$NON-NLS-1$

			//======== panel3 ========
			{
				panel3.setLayout(new BorderLayout());
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.tabUpdateCover"), panel3); //$NON-NLS-1$

			//======== panel4 ========
			{
				panel4.setLayout(new BorderLayout());
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.tabUpdateFile"), panel4); //$NON-NLS-1$

			//======== panel5 ========
			{
				panel5.setLayout(new BorderLayout());
			}
			tabbedPane1.addTab(LocaleBundle.getString("BatchEditFrame.btnAdd"), panel5); //$NON-NLS-1$
		}
		contentPane.add(tabbedPane1, CC.xywh(2, 12, 5, 1, CC.FILL, CC.FILL));

		//---- btnCreatePatch ----
		btnCreatePatch.setText(LocaleBundle.getString("BatchEditFrame.btnCreatePatch")); //$NON-NLS-1$
		contentPane.add(btnCreatePatch, CC.xywh(2, 14, 5, 1));
		setSize(650, 500);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private ReadableTextField edDatabasePath;
	private JButton btnOpenDatabase;
	private JLabel label2;
	private JTextField edDatabaseName;
	private JLabel label1;
	private JScrollPane scrollPane1;
	private JTextArea edRules;
	private JButton btnCompare;
	private JTabbedPane tabbedPane1;
	private JPanel panel1;
	private JPanel panel2;
	private JPanel panel3;
	private JPanel panel4;
	private JPanel panel5;
	private JButton btnCreatePatch;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
