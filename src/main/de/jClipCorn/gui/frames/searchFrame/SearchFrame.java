package de.jClipCorn.gui.frames.searchFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class SearchFrame extends JFrame {
	private static final long serialVersionUID = 8249406636361562910L;
	private JPanel pnTop;
	private JTextField edSearch;
	private Component verticalStrut;
	private Component verticalStrut_1;
	private Component horizontalStrut;
	private Component horizontalStrut_1;
	private Component horizontalStrut_2;
	private Component horizontalStrut_3;
	private Component verticalStrut_2;
	private JScrollPane scrollPane;
	private JList<Object> lsMain;
	private DefaultListModel<Object> lsmdl;

	private CCMovieList movielist;
	
	public SearchFrame(CCMovieList list, Component owner) {
		super();
		this.movielist = list;
		initGUI();
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setTitle(LocaleBundle.getString("SearchFrame.this.title")); //$NON-NLS-1$
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		pnTop = new JPanel();
		getContentPane().add(pnTop, BorderLayout.NORTH);
		pnTop.setLayout(new BorderLayout(0, 0));
		
		edSearch = new JTextField();
		edSearch.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				onUpdate();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				onUpdate();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				onUpdate();
			}
		});
		edSearch.addActionListener((e) -> { onSearchFieldEnter(); });
		edSearch.addKeyListener(new KeyAdapter() { @Override public void keyPressed(KeyEvent e)
		{
			super.keyPressed(e);
			if (e.getKeyCode() == KeyEvent.VK_DOWN) onSearchFieldDown();
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) dispose();
		} });
		pnTop.add(edSearch, BorderLayout.CENTER);
		edSearch.setColumns(40);
		
		verticalStrut = Box.createVerticalStrut(5);
		pnTop.add(verticalStrut, BorderLayout.NORTH);
		
		verticalStrut_1 = Box.createVerticalStrut(10);
		pnTop.add(verticalStrut_1, BorderLayout.SOUTH);
		
		horizontalStrut = Box.createHorizontalStrut(5);
		pnTop.add(horizontalStrut, BorderLayout.WEST);
		
		horizontalStrut_1 = Box.createHorizontalStrut(5);
		pnTop.add(horizontalStrut_1, BorderLayout.EAST);
		
		horizontalStrut_2 = Box.createHorizontalStrut(5);
		getContentPane().add(horizontalStrut_2, BorderLayout.WEST);
		
		horizontalStrut_3 = Box.createHorizontalStrut(5);
		getContentPane().add(horizontalStrut_3, BorderLayout.EAST);
		
		verticalStrut_2 = Box.createVerticalStrut(5);
		getContentPane().add(verticalStrut_2, BorderLayout.SOUTH);
		
		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		lsMain = new JList<>();
		lsMain.setVisibleRowCount(12);
		lsMain.setModel(lsmdl = new DefaultListModel<>());
		lsMain.addMouseListener(new MouseListener() {			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					onDblClick();
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// nothing
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// nothing
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// nothing
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// nothing
			}
		});
		lsMain.addKeyListener(new KeyAdapter() { @Override public void keyPressed(KeyEvent e)
		{
			if (e.getKeyCode() == KeyEvent.VK_UP && lsMain.getModel().getSize() > 1 && lsMain.getSelectedIndex() == 0)
				onListUp();
			else if (e.getKeyCode() == KeyEvent.VK_ENTER && lsMain.getSelectedIndex() != -1)
				onListEnter();
			else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				dispose();
			else
				super.keyPressed(e);
		} });
		scrollPane.setViewportView(lsMain);
		
		pack();

		this.addWindowFocusListener(new WindowFocusListener() {
			@Override public void windowGainedFocus(WindowEvent e) { }
			@Override public void windowLostFocus(WindowEvent e)   { dispose(); }
		});
	}
	
	private void onUpdate() {
		String searchString = edSearch.getText().trim();
		
		lsmdl.clear();

		if (searchString.isEmpty()) return;

		for (CCMovie mov : movielist.iteratorMovies())
		{
			boolean movFound = false;

			if ((mov.getLocalID() + "").equals(searchString)) { //$NON-NLS-1$
				addToList(mov);
				continue;
			}
			
			if (StringUtils.containsIgnoreCase(mov.getTitle(), searchString)) {
				addToList(mov);
				continue;
			}
			
			if (StringUtils.containsIgnoreCase(mov.getZyklus().getTitle(), searchString)) {
				addToList(mov);
				continue;
			}
			
			for (CCSingleOnlineReference soref : mov.getOnlineReference()) {
				if (StringUtils.equalsIgnoreCase(soref.type.asString(), searchString)) {
					addToList(mov);
					movFound = true;
					break;
				}
				
				if (StringUtils.equalsIgnoreCase(soref.type.getIdentifier(), searchString)) {
					addToList(mov);
					movFound = true;
					break;
				}

				if (soref.toSerializationString().equals(searchString) || soref.id.equals(searchString)) {
					addToList(mov);
					movFound = true;
					break;
				}
			}
			if (movFound) continue;
			
			if (mov.getGroups().containsIgnoreCase(searchString)) {
				addToList(mov);
				continue;
			}

			for (int i = 0; i < mov.getPartcount(); i++) {
				if (StringUtils.containsIgnoreCase(mov.getAbsolutePart(i), searchString)) {
					addToList(mov);
					movFound = true;
					break;
				}
			}
			if (movFound) continue;

			for (CCDBLanguage lang : mov.getLanguage())
			{
				if (lang.asString().equalsIgnoreCase(searchString)) {
					addToList(mov);
					movFound = true;
					break;
				}
				if (lang.getLongString().equalsIgnoreCase(searchString)) {
					addToList(mov);
					movFound = true;
					break;
				}
			}
			if (movFound) continue;

			if (mov.getMediaInfo().isSet())
			{
				if (mov.getMediaInfo().getVideoCodec().equalsIgnoreCase(searchString)) {
					addToList(mov);
					continue;
				}

				if (mov.getMediaInfo().getVideoFormat().equalsIgnoreCase(searchString)) {
					addToList(mov);
					continue;
				}

				if (mov.getMediaInfo().getAudioCodec().equalsIgnoreCase(searchString)) {
					addToList(mov);
					continue;
				}

				if (mov.getMediaInfo().getAudioFormat().equalsIgnoreCase(searchString)) {
					addToList(mov);
					continue;
				}
			}

		}
		
		for (CCSeries ser : movielist.iteratorSeries()) {
			for (int i = 0; i < ser.getSeasonCount(); i++) {
				CCSeason sea = ser.getSeasonByArrayIndex(i);
				
				for (int j = 0; j < sea.getEpisodeCount(); j++) {
					CCEpisode epi = sea.getEpisodeByArrayIndex(j);

					boolean epiFound = false;
					
					if (StringUtils.containsIgnoreCase(epi.getTitle(), searchString)) {
						addToList(epi);
						continue;
					}


					for (CCDBLanguage lang : epi.getLanguage())
					{
						if (lang.asString().equalsIgnoreCase(searchString)) {
							addToList(epi);
							epiFound = true;
							break;
						}
						if (lang.getLongString().equalsIgnoreCase(searchString)) {
							addToList(epi);
							epiFound = true;
							break;
						}
					}
					if (epiFound) continue;

					if (epi.getMediaInfo().isSet())
					{
						if (epi.getMediaInfo().getVideoCodec().equalsIgnoreCase(searchString)) {
							addToList(epi);
							continue;
						}

						if (epi.getMediaInfo().getVideoFormat().equalsIgnoreCase(searchString)) {
							addToList(epi);
							continue;
						}

						if (epi.getMediaInfo().getAudioCodec().equalsIgnoreCase(searchString)) {
							addToList(epi);
							continue;
						}

						if (epi.getMediaInfo().getAudioFormat().equalsIgnoreCase(searchString)) {
							addToList(epi);
							continue;
						}
					}
				}
				
				if (StringUtils.containsIgnoreCase(sea.getTitle(), searchString)) {
					addToList(sea);
					continue;
				}
			}
			
			if (StringUtils.containsIgnoreCase(ser.getTitle(), searchString)) {
				addToList(ser);
				continue;
			}

			for (CCSingleOnlineReference soref : ser.getOnlineReference()) {
				if (StringUtils.equalsIgnoreCase(soref.type.asString(), searchString)) {
					addToList(ser);
					continue;
				}
				
				if (StringUtils.equalsIgnoreCase(soref.type.getIdentifier(), searchString)) {
					addToList(ser);
					continue;
				}

				if (soref.toSerializationString().equals(searchString) || soref.id.equals(searchString)) {
					addToList(ser);
					continue;
				}
			}
			
			if (ser.getGroups().containsIgnoreCase(searchString)) {
				addToList(ser);
				continue;
			}
		}
	}

	private void onDblClick() {
		if (lsMain.getSelectedIndex() >= 0)
		{
			Object v = lsMain.getSelectedValue();
			if (v instanceof CCMovie)   PreviewMovieFrame.show(this,  (CCMovie)   v);
			if (v instanceof CCSeries)  PreviewSeriesFrame.show(this, (CCSeries)  v);
			if (v instanceof CCSeason)  PreviewSeriesFrame.show(this, (CCSeason)  v);
			if (v instanceof CCEpisode) PreviewSeriesFrame.show(this, (CCEpisode) v);
		}
	}

	private void onSearchFieldEnter() {
		if (lsMain.getModel().getSize() == 1)
		{
			Object v = lsMain.getModel().getElementAt(0);
			if (v instanceof CCMovie)   { PreviewMovieFrame.show(this,  (CCMovie)   v); this.dispose(); }
			if (v instanceof CCSeries)  { PreviewSeriesFrame.show(this, (CCSeries)  v); this.dispose(); }
			if (v instanceof CCSeason)  { PreviewSeriesFrame.show(this, (CCSeason)  v); this.dispose(); }
			if (v instanceof CCEpisode) { PreviewSeriesFrame.show(this, (CCEpisode) v); this.dispose(); }
		}
		else if (lsMain.getModel().getSize() > 1)
		{
			lsMain.setSelectedIndex(0);
			lsMain.requestFocus();
		}
	}

	private void onSearchFieldDown()
	{
		if (lsMain.getModel().getSize() > 0)
		{
			lsMain.setSelectedIndex(0);
			lsMain.requestFocus();
		}
	}

	private void onListUp()
	{
		edSearch.requestFocus();
		edSearch.select(edSearch.getText().length(), edSearch.getText().length());
		lsMain.setSelectedIndex(-1);
		lsMain.clearSelection();
	}

	private void onListEnter()
	{
		Object v = lsMain.getSelectedValue();
		if (v instanceof CCMovie)   { PreviewMovieFrame.show(this,  (CCMovie)   v); this.dispose(); }
		if (v instanceof CCSeries)  { PreviewSeriesFrame.show(this, (CCSeries)  v); this.dispose(); }
		if (v instanceof CCSeason)  { PreviewSeriesFrame.show(this, (CCSeason)  v); this.dispose(); }
		if (v instanceof CCEpisode) { PreviewSeriesFrame.show(this, (CCEpisode) v); this.dispose(); }
	}
	
	private void addToList(CCMovie m) {
		lsmdl.addElement(m);
	}
	
	private void addToList(CCSeries s) {
		lsmdl.addElement(s);
	}
	
	private void addToList(CCSeason s) {
		lsmdl.addElement(s);
	}
	
	private void addToList(CCEpisode e) {
		lsmdl.addElement(e);
	}
}
