package de.jClipCorn.gui.frames.quickAddMoviesDialog;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
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
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		okButton.addActionListener((e) -> onOK());
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		cancelButton.addActionListener((e) -> dispose());
		buttonPane.add(cancelButton);
	}

	private Tuple<String, String> getPaths(File f) {
		String f0 = f.getAbsolutePath();
		String f1 = PathFormatter.getCCPath(PathFormatter.combine(edRoot.getText(), PathFormatter.getFilenameWithExt(f0)), CCProperties.getInstance().PROP_ADD_MOVIE_RELATIVE_AUTO.getValue());
		
		return Tuple.Create(f0, f1);
	}
	
	private void onOK() {
		
		for (File file : inputFiles) {
			Tuple<String, String> p = getPaths(file);

			String src = p.Item1;
			String dst = p.Item2;
			String fullDst = PathFormatter.fromCCPath(dst);
			
			if (!PathFormatter.fileExists(src)) return;
			
			if (PathFormatter.fileExists(fullDst) && !fullDst.equalsIgnoreCase(dst)) return;
			
			if (!PathFormatter.isValid(fullDst)) return;
		}
		
		for (File file : inputFiles) {
			Tuple<String, String> p = getPaths(file);

			String src = p.Item1;
			String dst = PathFormatter.fromCCPath(p.Item2);
			
			try {
				FileUtils.copyFile(new File(src), new File(dst));
			} catch (IOException e) {
				CCLog.addError(e);
				continue;
			}
			
			AddMovieFrame amf = new AddMovieFrame(this, movielist, dst);
			amf.setVisible(true);
		}
		
		dispose();
	}
}
