package de.jClipCorn.gui.frames.findCoverFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.ExtendedFocusTraversalOnArray;
import de.jClipCorn.util.ProgressCallbackProgressBarHelper;
import de.jClipCorn.util.UpdateCallbackListener;
import de.jClipCorn.util.parser.ParseResultHandler;

public class FindCoverDialog extends JDialog {
	private static final long serialVersionUID = -5790203846014201695L;

	private JPanel pnlTopInner;
	private JTextField edSearchTerm;
	private JButton btnParse;
	private JPanel pnlTop;
	private Component horizontalStrut;
	private Component verticalStrut;
	private Component verticalStrut_1;
	private Component horizontalStrut_1;
	private JPanel pnlBottom;
	private JButton btnOk;
	private CoverPanel pnlCover;
	private JProgressBar progressBar;
	private JScrollPane scrollPane;
	
	private CoverImageParser parser;
	
	private final ParseResultHandler handler;
	private final CCMovieTyp typ;
	private JButton btnStop;
	
	public FindCoverDialog(Component owner, ParseResultHandler handler, CCMovieTyp typ) {
		super();
		this.handler = handler;
		this.typ = typ;
		
		initGUI();
		
		setLocationRelativeTo(owner);
		setFocusTraversalPolicy(new ExtendedFocusTraversalOnArray(new Component[]{edSearchTerm, btnParse, btnStop, btnOk}));
	}
	
	private void initGUI() {
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle(LocaleBundle.getString("FindCoverDialog.this.title")); //$NON-NLS-1$
		
		pnlTop = new JPanel();
		getContentPane().add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BorderLayout(0, 0));
		
		pnlTopInner = new JPanel();
		pnlTop.add(pnlTopInner);
		pnlTopInner.setLayout(new BorderLayout(5, 5));
		
		edSearchTerm = new JTextField(handler.getFullTitle());
		edSearchTerm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startParse();
			}
		});
		pnlTopInner.add(edSearchTerm);
		edSearchTerm.setColumns(10);
		
		btnParse = new JButton(LocaleBundle.getString("FindCoverDialog.btnParse.text")); //$NON-NLS-1$
		btnParse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startParse();
			}
		});
		pnlTopInner.add(btnParse, BorderLayout.EAST);
		
		progressBar = new JProgressBar();
		pnlTopInner.add(progressBar, BorderLayout.SOUTH);
		
		btnStop = new JButton(LocaleBundle.getString("FindCoverDialog.btnStop.text")); //$NON-NLS-1$
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				stopParse();
			}
		});
		btnStop.setEnabled(false);
		pnlTopInner.add(btnStop, BorderLayout.WEST);
		
		horizontalStrut = Box.createHorizontalStrut(5);
		pnlTop.add(horizontalStrut, BorderLayout.WEST);
		
		verticalStrut = Box.createVerticalStrut(5);
		pnlTop.add(verticalStrut, BorderLayout.SOUTH);
		
		verticalStrut_1 = Box.createVerticalStrut(5);
		pnlTop.add(verticalStrut_1, BorderLayout.NORTH);
		
		horizontalStrut_1 = Box.createHorizontalStrut(5);
		pnlTop.add(horizontalStrut_1, BorderLayout.EAST);
		
		pnlBottom = new JPanel();
		getContentPane().add(pnlBottom, BorderLayout.SOUTH);
		
		btnOk = new JButton(LocaleBundle.getString("FindCoverDialog.btnOK.text")); //$NON-NLS-1$
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		pnlBottom.add(btnOk);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		pnlCover = new CoverPanel(scrollPane);
		scrollPane.setViewportView(pnlCover);
		
		setSize(650, 550);
	}
	
	private void onOK() {
		if (parser != null) {
			parser.stop();
		}
		
		BufferedImage img = pnlCover.getSelectedCover();
		
		if (img != null) {
			handler.setCover(img);
		}
		
		dispose();
	}
	
	private void stopParse() {
		parser.stop();
	}
	
	private void startParse() {
		pnlCover.reset();
		btnParse.setEnabled(false);
		btnStop.setEnabled(true);
		
		UpdateCallbackListener finishlistener = new UpdateCallbackListener() {
			@Override
			public void onUpdate(Object o) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						btnParse.setEnabled(true);
						btnStop.setEnabled(false);
					}
				});
			}
		};
		
		parser = new CoverImageParser(new ProgressCallbackProgressBarHelper(progressBar), pnlCover, finishlistener, typ, edSearchTerm.getText());
		parser.start();
	}
}
