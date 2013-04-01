package de.jClipCorn.gui.frames.exportJxmlBKPFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.ReadableTextField;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.FileChooserHelper;
import de.jClipCorn.util.PathFormatter;

public class ExportJxmlBKPDialog extends JDialog implements Runnable {
	private static final long serialVersionUID = 8171441711402095045L;
	
	private JProgressBar progressBar;
	private JButton btnSave;
	private ReadableTextField edPath;
	private JButton button;
	
	private String savepath = ""; //$NON-NLS-1$
	private final CCMovieList movielist;
	
	public ExportJxmlBKPDialog(Component owner, CCMovieList list) {
		super();
		this.movielist = list;
		
		initGUI();
		
		setLocationRelativeTo(owner);
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
			
			savepath = p;
		}
	}

	@Override
	public void run() {
		Document xml = movielist.getAsXML();
		
		try {
			FileOutputStream ostream = new FileOutputStream(savepath);
			ZipOutputStream zos = new ZipOutputStream(ostream);
			
			ZipEntry xmlentry = new ZipEntry("database.xml"); //$NON-NLS-1$
			zos.putNextEntry(xmlentry);
			XMLOutputter xout = new XMLOutputter();
			xout.setFormat(Format.getPrettyFormat());
			xout.output(xml, zos);
			
			zipDir(movielist.getCoverCache().getCoverDirectory().getParentFile(), movielist.getCoverCache().getCoverDirectory(), zos);
			
			zos.close();
		} catch (IOException e) {
			CCLog.addError(e);
		}
		
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
	
	private static void zipDir(File owner, File zipDir, ZipOutputStream zos) {
		try {
			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			for (int i = 0; i < dirList.length; i++) {
				File f = new File(zipDir, dirList[i]);
				if (f.isDirectory()) {
					continue;
				}
				
				FileInputStream fis = new FileInputStream(f);
				ZipEntry anEntry = new ZipEntry(f.getAbsolutePath().replace(owner.getAbsolutePath() + '\\', "")); //$NON-NLS-1$
				zos.putNextEntry(anEntry);
				
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				fis.close();
			}
		} catch (Exception e) {
			CCLog.addError(e);
		}
	}
}
