package de.jClipCorn.gui.frames.quickAddMoviesDialog;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.helper.SimpleFileUtils;
import org.apache.commons.io.FileUtils;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.frames.addMovieFrame.AddMovieFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.stream.CCStreams;

public class QuickAddMoviesDialog extends JDialog {
	private static final long serialVersionUID = 5295134501386181860L;

	private final JPanel contentPanel = new JPanel();

	private final CCMovieList movielist;
	private final File[] inputFiles;
	private JTextField edRoot;
	private QuickAddMoviesTable lstData;
	private JProgressBar progressBar1;
	private JProgressBar progressBar2;
	
	/**
	 * @wbp.parser.constructor
	 */
	public QuickAddMoviesDialog(JFrame owner, CCMovieList mlist, File[] files) {
		super();

		movielist = mlist;
		inputFiles = files;
		
		initGUI();

		setLocationRelativeTo(owner);
		
		edRoot.setText(PathFormatter.fromCCPath(mlist.getCommonMoviesPath()));
		if (!PathFormatter.isValid(edRoot.getText())) {
			CCMovie m = mlist.iteratorMovies().lastOrNull();
			if (m != null) {
				edRoot.setText(new File(m.getAbsolutePart(0)).getParent());
			}
		}
		lstData.setData(CCStreams.iterate(files).map(this::getPaths).enumerate());

		lstData.autoResize();
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("QuickAddMoviesDialog.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
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
		
		edRoot = new JTextField();
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
		
		lstData = new QuickAddMoviesTable();
		contentPanel.add(lstData, "1, 3, fill, fill"); //$NON-NLS-1$

		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("51px"),
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("96px"),
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("13px"),
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("13px"),
				FormSpecs.LINE_GAP_ROWSPEC,}));
						
		progressBar1 = new JProgressBar();
		buttonPane.add(progressBar1, "2, 2, fill, fill");
		progressBar1.setVisible(false);

		JButton okButton = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		okButton.addActionListener((e) -> onOK());
		buttonPane.add(okButton, "4, 2, 1, 3, fill, fill");
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		cancelButton.addActionListener((e) -> dispose());
		buttonPane.add(cancelButton, "6, 2, 1, 3, fill, fill");

		progressBar2 = new JProgressBar();
		buttonPane.add(progressBar2, "2, 4, fill, fill");
		progressBar2.setVisible(false);
	}

	private Tuple<String, String> getPaths(File f) {
		String f0 = f.getAbsolutePath();
		String f1 = PathFormatter.getCCPath(PathFormatter.combine(edRoot.getText(), PathFormatter.getFilenameWithExt(f0)));
		
		return Tuple.Create(f0, f1);
	}

	private volatile int progressValueCache;
	
	private void onOK()
	{
		for (File file : inputFiles) {
			Tuple<String, String> p = getPaths(file);

			String src = p.Item1;
			String dst = p.Item2;
			String fullDst = PathFormatter.fromCCPath(dst);

			if (!PathFormatter.fileExists(src)) return;

			if (PathFormatter.fileExists(fullDst) && !fullDst.equalsIgnoreCase(dst)) return;

			if (!PathFormatter.isValid(fullDst)) return;
		}

		contentPanel.setEnabled(false);
		new Thread(() ->
		{
			try
			{
				progressValueCache = 0;
				try {
					SwingUtilities.invokeAndWait(() ->
					{
						progressBar1.setVisible(true);
						progressBar1.setMaximum(inputFiles.length + 1);

						progressBar2.setVisible(true);
						progressBar2.setMaximum(100);
					});
				} catch (InterruptedException | InvocationTargetException e) { /* */ }

				List<String> destinations = new ArrayList<>();

				int i = 0;
				for (File file : inputFiles)
				{
					i++;

					Tuple<String, String> p = getPaths(file);

					String src = p.Item1;
					String dst = PathFormatter.fromCCPath(p.Item2);

					final int iv = i;
					SwingUtilities.invokeLater(() ->
					{
						progressBar1.setValue(iv);
						progressBar2.setValue(0);
					});
					progressValueCache = 0;

					try {

						//FileUtils.copyFile(new File(src), new File(dst));
						SimpleFileUtils.copyWithProgress(new File(src), new File(dst), (val, max) ->
						{
							int newvalue = (int)(((val * 100) / max));
							if (progressValueCache != newvalue)
							{
								progressValueCache = newvalue;
								SwingUtilities.invokeLater(() -> { progressBar2.setValue(newvalue); });
							}
						});
					} catch (IOException e) {
						CCLog.addError(e);
						continue;
					}

					destinations.add(dst);
				}

				SwingUtilities.invokeLater(() ->
				{
					progressBar1.setValue(progressBar1.getMaximum());
					progressBar2.setValue(progressBar2.getMaximum());
				});

				SwingUtilities.invokeLater(() ->
				{
					for (String dst : destinations) {
						AddMovieFrame amf = new AddMovieFrame(this, movielist, dst);
						amf.setVisible(true);
					}

					dispose();
				});

			} finally {
				SwingUtilities.invokeLater(() -> contentPanel.setEnabled(true) );
			}
		}).start();
	}
}
