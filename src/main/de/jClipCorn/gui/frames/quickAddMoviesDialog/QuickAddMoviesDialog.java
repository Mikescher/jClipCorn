package de.jClipCorn.gui.frames.quickAddMoviesDialog;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.guiComponents.JFSPathTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class QuickAddMoviesDialog extends JCCDialog
{
	private final FSPath[] inputFiles;

	private volatile int progressValueCache;

	public QuickAddMoviesDialog(JFrame owner, CCMovieList ml, FSPath[] files)
	{
		super(ml);

		inputFiles = files;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		edRoot.setPath(movielist.getCommonMoviesPath().toFSPath(this));
		if (edRoot.getPath().isEmpty()) {
			CCMovie m = movielist.iteratorMovies().lastOrNull();
			if (m != null) {
				edRoot.setPath(m.Parts.get(0).toFSPath(this).getParent());
			}
		}
		lstData.setData(CCStreams.iterate(inputFiles).map(this::getPaths).enumerate());

		lstData.autoResize();

		edRoot.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				lstData.setData(CCStreams.iterate(inputFiles).map(QuickAddMoviesDialog.this::getPaths).enumerate());
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				lstData.setData(CCStreams.iterate(inputFiles).map(QuickAddMoviesDialog.this::getPaths).enumerate());
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				lstData.setData(CCStreams.iterate(inputFiles).map(QuickAddMoviesDialog.this::getPaths).enumerate());
			}
		});

		progressBar1.setVisible(false);
		progressBar2.setVisible(false);
	}

	private Tuple<FSPath, CCPath> getPaths(FSPath f0) {
		var f1 = CCPath.createFromFSPath(edRoot.getPath().append(f0.getFilenameWithExt()), this);

		return Tuple.Create(f0, f1);
	}

	private void onOkay()
	{
		for (var file : inputFiles) {
			var p = getPaths(file);

			var src = p.Item1;
			var dst = p.Item2.toFSPath(this);

			if (!src.fileExists()) return;

			if (dst.fileExists() && !src.equalsOnFilesystem(dst)) return;

			if (!dst.isValidPath()) return;
		}

		rootpnl.setEnabled(false);
		new Thread(() ->
		{
			try
			{
				progressValueCache = 0;
				try {
					SwingUtils.invokeAndWait(() ->
					{
						progressBar1.setVisible(true);
						progressBar1.setMaximum(inputFiles.length + 1);

						progressBar2.setVisible(true);
						progressBar2.setMaximum(100);
					});
				} catch (InterruptedException | InvocationTargetException e) { /* */ }

				List<FSPath> destinations = new ArrayList<>();

				int i = 0;
				for (var file : inputFiles)
				{
					i++;

					var p = getPaths(file);

					var src = p.Item1;
					var dst = p.Item2.toFSPath(this);

					final int iv = i;
					SwingUtils.invokeLater(() ->
					{
						progressBar1.setValue(iv);
						progressBar2.setValue(0);
					});
					progressValueCache = 0;

					try {

						//FileUtils.copyFile(new File(src), new File(dst));
						SimpleFileUtils.copyWithProgress(src, dst, (val, max) ->
						{
							int newvalue = (int)(((val * 100) / max));
							if (progressValueCache != newvalue)
							{
								progressValueCache = newvalue;
								SwingUtils.invokeLater(() -> { progressBar2.setValue(newvalue); });
							}
						});
					} catch (IOException e) {
						CCLog.addError(e);
						continue;
					}

					destinations.add(dst);
				}

				SwingUtils.invokeLater(() ->
				{
					progressBar1.setValue(progressBar1.getMaximum());
					progressBar2.setValue(progressBar2.getMaximum());
				});

				SwingUtils.invokeLater(() ->
				{
					for (var dst : destinations) {
						AddMovieFrame amf = new AddMovieFrame(this, movielist, dst);
						amf.setVisible(true);
					}

					dispose();
				});

			} finally {
				SwingUtils.invokeLater(() -> rootpnl.setEnabled(true) );
			}
		}).start();
	}

	private void onCancel() {
		dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		rootpnl = new JPanel();
		edRoot = new JFSPathTextField();
		lstData = new QuickAddMoviesTable(this);
		progressBar1 = new JProgressBar();
		button1 = new JButton();
		button2 = new JButton();
		progressBar2 = new JProgressBar();

		//======== this ========
		setTitle(LocaleBundle.getString("QuickAddMoviesDialog.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
		setMinimumSize(new Dimension(300, 300));
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"default:grow", //$NON-NLS-1$
			"default:grow")); //$NON-NLS-1$

		//======== rootpnl ========
		{
			rootpnl.setLayout(new FormLayout(
				"$ugap, default:grow, 2*($lcgap, default), $ugap", //$NON-NLS-1$
				"$ugap, default, $lgap, 3dlu:grow, $ugap, [12dlu,default], $lgap, [12dlu,default], $ugap")); //$NON-NLS-1$
			rootpnl.add(edRoot, CC.xywh(2, 2, 5, 1));
			rootpnl.add(lstData, CC.xywh(2, 4, 5, 1, CC.FILL, CC.FILL));
			rootpnl.add(progressBar1, CC.xy(2, 6, CC.DEFAULT, CC.FILL));

			//---- button1 ----
			button1.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
			button1.addActionListener(e -> onOkay());
			rootpnl.add(button1, CC.xywh(4, 6, 1, 3));

			//---- button2 ----
			button2.setText(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
			button2.addActionListener(e -> onCancel());
			rootpnl.add(button2, CC.xywh(6, 6, 1, 3));
			rootpnl.add(progressBar2, CC.xy(2, 8, CC.DEFAULT, CC.FILL));
		}
		contentPane.add(rootpnl, CC.xy(1, 1, CC.FILL, CC.FILL));
		setSize(500, 300);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel rootpnl;
	private JFSPathTextField edRoot;
	private QuickAddMoviesTable lstData;
	private JProgressBar progressBar1;
	private JButton button1;
	private JButton button2;
	private JProgressBar progressBar2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
