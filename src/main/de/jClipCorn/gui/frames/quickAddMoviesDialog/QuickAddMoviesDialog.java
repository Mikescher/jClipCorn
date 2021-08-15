package de.jClipCorn.gui.frames.quickAddMoviesDialog;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.guiComponents.JFSPathTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class QuickAddMoviesDialog extends JCCDialog {
	private static final long serialVersionUID = 5295134501386181860L;

	private final JPanel contentPanel = new JPanel();

	private final FSPath[] inputFiles;
	private JFSPathTextField edRoot;
	private QuickAddMoviesTable lstData;
	private JProgressBar progressBar1;
	private JProgressBar progressBar2;
	
	/**
	 * @wbp.parser.constructor
	 */
	public QuickAddMoviesDialog(JFrame owner, CCMovieList mlist, FSPath[] files) {
		super(mlist);

		inputFiles = files;
		
		initGUI();

		setLocationRelativeTo(owner);
		
		edRoot.setPath(mlist.getCommonMoviesPath().toFSPath(this));
		if (edRoot.getPath().isEmpty()) {
			CCMovie m = mlist.iteratorMovies().lastOrNull();
			if (m != null) {
				edRoot.setPath(m.Parts.get(0).toFSPath(this).getParent());
			}
		}
		lstData.setData(CCStreams.iterate(files).map(this::getPaths).enumerate());

		lstData.autoResize();
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("QuickAddMoviesDialog.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setBounds(100, 100, 500, 300);
		setMinimumSize(new Dimension(300, 300));
		setModal(true);

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),})); //$NON-NLS-1$
		
		edRoot = new JFSPathTextField();
		contentPanel.add(edRoot, "1, 1, fill, default"); //$NON-NLS-1$
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
		edRoot.setColumns(10);
		
		lstData = new QuickAddMoviesTable(this);
		contentPanel.add(lstData, "1, 3, fill, fill"); //$NON-NLS-1$

		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("51px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("96px"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("13px"), //$NON-NLS-1$
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("13px"), //$NON-NLS-1$
				FormSpecs.LINE_GAP_ROWSPEC,}));
						
		progressBar1 = new JProgressBar();
		buttonPane.add(progressBar1, "2, 2, fill, fill"); //$NON-NLS-1$
		progressBar1.setVisible(false);

		JButton okButton = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		okButton.addActionListener((e) -> onOK());
		buttonPane.add(okButton, "4, 2, 1, 3, fill, fill"); //$NON-NLS-1$
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		cancelButton.addActionListener((e) -> dispose());
		buttonPane.add(cancelButton, "6, 2, 1, 3, fill, fill"); //$NON-NLS-1$

		progressBar2 = new JProgressBar();
		buttonPane.add(progressBar2, "2, 4, fill, fill"); //$NON-NLS-1$
		progressBar2.setVisible(false);
	}

	private Tuple<FSPath, CCPath> getPaths(FSPath f0) {
		var f1 = CCPath.createFromFSPath(edRoot.getPath().append(f0.getFilenameWithExt()), this);
		
		return Tuple.Create(f0, f1);
	}

	private volatile int progressValueCache;
	
	private void onOK()
	{
		for (var file : inputFiles) {
			var p = getPaths(file);

			var src = p.Item1;
			var dst = p.Item2.toFSPath(this);

			if (!src.fileExists()) return;

			if (dst.fileExists() && !src.equalsOnFilesystem(dst)) return;

			if (!dst.isValidPath()) return;
		}

		contentPanel.setEnabled(false);
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
				SwingUtils.invokeLater(() -> contentPanel.setEnabled(true) );
			}
		}).start();
	}
}
