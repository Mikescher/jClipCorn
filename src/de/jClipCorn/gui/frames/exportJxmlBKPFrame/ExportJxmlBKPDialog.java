package de.jClipCorn.gui.frames.exportJxmlBKPFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.ExtendedFocusTraversalOnArray;
import de.jClipCorn.util.FileChooserHelper;
import de.jClipCorn.util.PathFormatter;

public class ExportJxmlBKPDialog extends JDialog implements Runnable { //TODO Test me please
	private static final long serialVersionUID = 8171441711402095045L;
	
	private JProgressBar progressBar;
	private JButton btnSave;
	private ReadableTextField edPath;
	private JButton button;
	
	private File savefile;
	private final CCMovieList movielist;
	
	public ExportJxmlBKPDialog(Component owner, CCMovieList list) {
		super();
		this.movielist = list;
		
		initGUI();
		
		setLocationRelativeTo(owner);
		setFocusTraversalPolicy(new ExtendedFocusTraversalOnArray(new Component[]{button, btnSave}));
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("ExportJxmlBKPDialog.this.title")); //$NON-NLS-1$
		setSize(new Dimension(300, 130));
		setResizable(false);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(10, 76, 274, 15);
		getContentPane().add(progressBar);
		
		btnSave = new JButton(LocaleBundle.getString("ExportJxmlBKPDialog.btnSave.title")); //$NON-NLS-1$
		btnSave.setEnabled(false);
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				progressBar.setIndeterminate(true);
				ExportJxmlBKPDialog.this.setEnabled(false);
				
				(new Thread(ExportJxmlBKPDialog.this)).start();
			}
		});
		btnSave.setBounds(102, 42, 89, 23);
		getContentPane().add(btnSave);
		
		edPath = new ReadableTextField();
		edPath.setBounds(10, 11, 224, 20);
		getContentPane().add(edPath);
		edPath.setColumns(10);
		
		button = new JButton("..."); //$NON-NLS-1$
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openFileChooser();
			}
		});
		button.setBounds(244, 10, 40, 23);
		getContentPane().add(button);
	}
	
	private void openFileChooser() {
		JFileChooser fc = new JFileChooser(PathFormatter.getRealSelfDirectory());
		
		fc.setFileFilter(FileChooserHelper.createFileFilter(LocaleBundle.getString("ExportJxmlBKPDialog.filechooser.description"), "jxmlbkp")); //$NON-NLS-1$ //$NON-NLS-2$
		fc.setAcceptAllFileFilterUsed(false);
		
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String p = fc.getSelectedFile().getAbsolutePath();
			
			if (! StringUtils.endsWithIgnoreCase(p, ".jxmlbkp")) { //$NON-NLS-1$
				p += ".jxmlbkp"; //$NON-NLS-1$
			}
			
			edPath.setText(p);
			btnSave.setEnabled(true);
			
			savefile = new File(p);
		}
	}

	@Override
	public void run() {
		ExportHelper.export(savefile, movielist);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ExportJxmlBKPDialog.this.setEnabled(true);
				progressBar.setIndeterminate(false);
				btnSave.setEnabled(false);
				edPath.setText(""); //$NON-NLS-1$
			}
		});
	}
}
