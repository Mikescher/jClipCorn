package de.jClipCorn.gui.frames.searchFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.guiComponents.LambdaListCellRenderer;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.lambda.Func1to1;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;

public class SearchFrame extends JFrame
{
	private final CCMovieList movielist;

	private DefaultListModel<ICCDatabaseStructureElement> listModel;

	public SearchFrame(CCMovieList mlist, Component owner)
	{
		super();
		movielist = mlist;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	@SuppressWarnings("nls")
	private void postInit()
	{
		setIconImage(Resources.IMG_FRAME_ICON.get());
		lsMain.setModel(listModel = new DefaultListModel<>());
		lsMain.setCellRenderer(new LambdaListCellRenderer<ICCDatabaseStructureElement>(v ->
		{
			if (v instanceof CCMovie)
				return ((CCMovie)v).getCompleteTitle();

			if (v instanceof CCSeries)
				return v.getTitle();

			if (v instanceof CCSeason)
				return Str.format("{0} - {1}", ((CCSeason)v).getSeries().getTitle(), v.getTitle());

			if (v instanceof CCEpisode)
				return Str.format("{0} - {1} - E{2,number,00} {3}",
						((CCEpisode)v).getSeries().getTitle(),
						((CCEpisode)v).getSeason().getTitle(),
						((CCEpisode)v).getEpisodeNumber(),
						v.getTitle());

			CCLog.addDefaultSwitchError(SearchFrame.this, v);
			return v.toString();
		}));
	}

	private void onSearchFieldEnter()
	{
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

	private void onSearchFieldKeyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_DOWN) onSearchFieldDown();
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) dispose();
	}

	private void onSearchFieldDown()
	{
		if (lsMain.getModel().getSize() > 0)
		{
			lsMain.setSelectedIndex(0);
			lsMain.requestFocus();
		}
	}

	private void onListMouseClicked(MouseEvent e)
	{
		if (e.getClickCount() == 2) onDblClick();
	}

	private void onListKeyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_UP && lsMain.getModel().getSize() > 1 && lsMain.getSelectedIndex() == 0)
			onListUp();
		else if (e.getKeyCode() == KeyEvent.VK_ENTER && lsMain.getSelectedIndex() != -1)
			onListEnter();
		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			dispose();
	}

	private void onFocusLost()
	{
		dispose();
	}

	private void onUpdate() {
		String searchString = edSearch.getText().trim();

		listModel.clear();

		if (searchString.isEmpty()) return;
		if (searchString.length() < 3) return;

		java.util.List<ICCDatabaseStructureElement> result = new ArrayList<>();

		for (CCMovie mov : movielist.iteratorMovies())
		{
			if (searchString.equals("{all}")) { result.add(mov); continue; } //$NON-NLS-1$

			if ((mov.getLocalID() + Str.Empty).equals(searchString)) { result.add(mov); continue; }

			if (StringUtils.containsIgnoreCase(mov.getTitle(), searchString)) { result.add(mov); continue; }

			if (StringUtils.containsIgnoreCase(mov.getZyklus().getTitle(), searchString)) { result.add(mov); continue; }

			boolean movFound = false;
			for (CCSingleOnlineReference soref : mov.getOnlineReference())
			{
				if (StringUtils.equalsIgnoreCase(soref.type.asString(), searchString)) { result.add(mov); movFound = true; break; }

				if (StringUtils.equalsIgnoreCase(soref.type.getIdentifier(), searchString)) { result.add(mov); movFound = true; break; }

				if (soref.toSerializationString().equals(searchString) || soref.id.equals(searchString)) { result.add(mov); movFound = true; break; }
			}
			if (movFound) continue;

			if (mov.getGroups().containsIgnoreCase(searchString)) { result.add(mov); continue; }

			for (int i = 0; i < mov.getPartcount(); i++)
			{
				if (StringUtils.containsIgnoreCase(mov.getAbsolutePart(i), searchString)) { result.add(mov); movFound = true; break; }
			}
			if (movFound) continue;

			for (CCDBLanguage lang : mov.getLanguage())
			{
				if (lang.asString().equalsIgnoreCase(searchString)) { result.add(mov); movFound = true; break; }

				if (lang.getLongString().equalsIgnoreCase(searchString)) { result.add(mov); movFound = true; break; }
			}
			if (movFound) continue;

			if (mov.getMediaInfo().isSet())
			{
				if (mov.getMediaInfo().getVideoCodec().equalsIgnoreCase(searchString)) { result.add(mov); continue; }

				if (mov.getMediaInfo().getVideoFormat().equalsIgnoreCase(searchString)) { result.add(mov); continue; }

				if (mov.getMediaInfo().getAudioCodec().equalsIgnoreCase(searchString)) { result.add(mov); continue; }

				if (mov.getMediaInfo().getAudioFormat().equalsIgnoreCase(searchString)) { result.add(mov); continue; }
			}

		}

		for (CCSeries ser : movielist.iteratorSeries())
		{
			for (int i = 0; i < ser.getSeasonCount(); i++)
			{
				CCSeason sea = ser.getSeasonByArrayIndex(i);

				for (int j = 0; j < sea.getEpisodeCount(); j++)
				{
					CCEpisode epi = sea.getEpisodeByArrayIndex(j);

					if (searchString.equals("{all}")) { result.add(epi); continue; } //$NON-NLS-1$

					if (StringUtils.containsIgnoreCase(epi.getTitle(), searchString)) { result.add(epi); continue; }

					boolean epiFound = false;
					for (CCDBLanguage lang : epi.getLanguage())
					{
						if (lang.asString().equalsIgnoreCase(searchString)) { result.add(epi); epiFound = true; break; }

						if (lang.getLongString().equalsIgnoreCase(searchString)) { result.add(epi); epiFound = true; break; }
					}
					if (epiFound) continue;

					if (epi.getMediaInfo().isSet())
					{
						if (epi.getMediaInfo().getVideoCodec().equalsIgnoreCase(searchString)) { result.add(epi); continue; }

						if (epi.getMediaInfo().getVideoFormat().equalsIgnoreCase(searchString)) { result.add(epi); continue; }

						if (epi.getMediaInfo().getAudioCodec().equalsIgnoreCase(searchString)) { result.add(epi); continue; }

						if (epi.getMediaInfo().getAudioFormat().equalsIgnoreCase(searchString)) { result.add(epi); continue; }
					}
				}

				if (searchString.equals("{all}")) { result.add(sea); continue; } //$NON-NLS-1$

				if (StringUtils.containsIgnoreCase(sea.getTitle(), searchString)) { result.add(sea); continue; }
			}

			if (searchString.equals("{all}")) { result.add(ser); continue; } //$NON-NLS-1$

			if (StringUtils.containsIgnoreCase(ser.getTitle(), searchString)) { result.add(ser); continue; }

			for (CCSingleOnlineReference soref : ser.getOnlineReference())
			{
				if (StringUtils.equalsIgnoreCase(soref.type.asString(), searchString)) { result.add(ser); continue; }

				if (StringUtils.equalsIgnoreCase(soref.type.getIdentifier(), searchString)) { result.add(ser); continue; }

				if (soref.toSerializationString().equals(searchString) || soref.id.equals(searchString)) { result.add(ser); continue; }
			}

			if (ser.getGroups().containsIgnoreCase(searchString)) { result.add(ser); continue; }
		}

		intellisort(result);

		//if (result.size() > 1024) result = result.subList(0, 1024);

		listModel.addAll(result);
	}

	@SuppressWarnings("ConstantConditions")
	private void intellisort(java.util.List<ICCDatabaseStructureElement> values)
	{
		//
		//   Order should be like
		// ========================
		//
		//
		// viewed movies (ordered by viewcount -> userscore -> name)
		//
		// (partial)viewed series (ordered by ext_view_state -> userscore -> name)
		//     seasons of that series that also match (ordered by seasonnumber)
		//         episode of that season that also match (ordered by viewed -> viewcount -> title)
		//
		// not-viewed movies (ordered by viewcount -> userscore -> name)
		//
		// not-viewed series (ordered by ext_view_state -> userscore -> name)
		//     seasons of that series that also match (ordered by seasonnumber)
		//         episode of that season that also match (ordered by viewed -> viewcount -> episode_number)
		//
		//

		var valueset = new HashSet<>(values);

		values.sort((v1, v2) ->
		{
			if (v1 == v2) return 0;

			var v1_tl_viewed = (boolean)poly(v1, CCMovie::isViewed, CCSeries::isViewedOrPartialViewed);
			var v2_tl_viewed = (boolean)poly(v2, CCMovie::isViewed, CCSeries::isViewedOrPartialViewed);
			if (v1_tl_viewed != v2_tl_viewed) return cmp(v1_tl_viewed, v2_tl_viewed);

			var v1_tl_ismov = (boolean)poly(v1, m -> true, s -> false);
			var v2_tl_ismov = (boolean)poly(v2, m -> true, s -> false);
			if (v1_tl_ismov != v2_tl_ismov) return cmp(v1_tl_ismov, v2_tl_ismov);

			if (v1_tl_ismov && v2_tl_ismov)
			{
				var v1_mov_vcount = (int)poly_mov(v1, m -> m.getViewedHistory().count());
				var v2_mov_vcount = (int)poly_mov(v2, m -> m.getViewedHistory().count());
				if (v1_mov_vcount != v2_mov_vcount) return -1 * cmp(v1_mov_vcount, v2_mov_vcount);

				var v1_mov_uscore = (int)poly_mov(v1, m -> m.getScore().getOrder());
				var v2_mov_uscore = (int)poly_mov(v2, m -> m.getScore().getOrder());
				if (v1_mov_uscore != v2_mov_uscore) return -1 * cmp(v1_mov_uscore, v2_mov_uscore);

				var v1_mov_zyklus = poly_mov(v1, m -> m.getZyklus().getTitle());
				var v2_mov_zyklus = poly_mov(v2, m -> m.getZyklus().getTitle());
				if (!v1_mov_zyklus.equals(v2_mov_zyklus)) return v1_mov_zyklus.compareTo(v2_mov_zyklus);

				var v1_mov_zyklusidx = (int)poly_mov(v1, m -> m.getZyklus().getNumber());
				var v2_mov_zyklusidx = (int)poly_mov(v2, m -> m.getZyklus().getNumber());
				if (v1_mov_zyklusidx != v2_mov_zyklusidx) return cmp(v1_mov_zyklusidx, v2_mov_zyklusidx);

				var v1_mov_title = poly_mov(v1, CCMovie::getTitle);
				var v2_mov_title = poly_mov(v2, CCMovie::getTitle);
				return v1_mov_title.compareTo(v2_mov_title);
			}

			var v1_tl_ser = poly(v1, p -> null, p->p);
			var v2_tl_ser = poly(v2, p -> null, p->p);
			if (v1_tl_ser != v2_tl_ser)
			{
				var v1_tlser_included = valueset.contains(v1_tl_ser);
				var v2_tlser_included = valueset.contains(v2_tl_ser);
				if (v1_tlser_included != v2_tlser_included) return cmp(v1_tlser_included, v2_tlser_included);

				var v1_ser_viewed = (int)poly_tl_ser(v1, s -> s.getExtendedViewedState().getType().getTriStateInt());
				var v2_ser_viewed = (int)poly_tl_ser(v2, s -> s.getExtendedViewedState().getType().getTriStateInt());
				if (v1_ser_viewed != v2_ser_viewed) return -1 * cmp(v1_ser_viewed, v2_ser_viewed);

				var v1_ser_uscore = (int)poly_tl_ser(v1, m -> m.getScore().getOrder());
				var v2_ser_uscore = (int)poly_tl_ser(v2, m -> m.getScore().getOrder());
				if (v1_ser_uscore != v2_ser_uscore) return -1 * cmp(v1_ser_uscore, v2_ser_uscore);

				var v1_ser_title = poly_tl_ser(v1, CCSeries::getTitle);
				var v2_ser_title = poly_tl_ser(v2, CCSeries::getTitle);
				return v1_ser_title.compareTo(v2_ser_title);
			}

			var v1_tl_sea = poly(v1, p -> null, p->null, p->p, CCEpisode::getSeason);
			var v2_tl_sea = poly(v2, p -> null, p->null, p->p, CCEpisode::getSeason);
			if (v1_tl_sea != v2_tl_sea && v1_tl_sea != null && v2_tl_sea != null)
			{
				return cmp(v1_tl_sea.getSeasonNumber(), v2_tl_sea.getSeasonNumber());
			}

			var v1_type = (int)poly(v1, p->0, p->1, p->2, p->3);
			var v2_type = (int)poly(v2, p->0, p->1, p->2, p->3);
			if (v1_type != v2_type) return cmp(v1_type, v2_type);

			if (v1_type == 3 && v2_type == 3) // both episode
			{
				var v1_epi_vcount = (int)poly_epi(v1, m -> m.getViewedHistory().count());
				var v2_epi_vcount = (int)poly_epi(v2, m -> m.getViewedHistory().count());
				if (v1_epi_vcount != v2_epi_vcount) return -1 * cmp(v1_epi_vcount, v2_epi_vcount);

				var v1_epi_title = (int)poly_epi(v1, CCEpisode::getEpisodeNumber);
				var v2_epi_title = (int)poly_epi(v2, CCEpisode::getEpisodeNumber);
				return cmp(v1_epi_title, v2_epi_title);
			}

			CCLog.addUndefinied(Str.format("SearchFrame compare function failed for: '[{0}]{1}' <> '[{2}]{3}'", v1.getClass().getSimpleName(), v1.toString(), v2.getClass().getSimpleName(), v2.toString())); //$NON-NLS-1$
			return 0;
		});
	}

	private int cmp(boolean a, boolean b)
	{
		if (a == b) return 0;
		if (a) return -1; else return +1;
	}

	private int cmp(int a, int b)
	{
		return Integer.compare(a, b);
	}

	private static <T> T poly(ICCDatabaseStructureElement e, Func1to1<CCMovie, T> l1, Func1to1<CCSeries, T> l2, Func1to1<CCSeason, T> l3, Func1to1<CCEpisode, T> l4)
	{
		if (e instanceof CCMovie)   return l1.invoke((CCMovie)   e);
		if (e instanceof CCSeries)  return l2.invoke((CCSeries)  e);
		if (e instanceof CCSeason)  return l3.invoke((CCSeason)  e);
		if (e instanceof CCEpisode) return l4.invoke((CCEpisode) e);

		CCLog.addDefaultSwitchError(SearchFrame.class, e);
		return null;
	}

	private static <T> T poly(ICCDatabaseStructureElement e, Func1to1<CCMovie, T> l1, Func1to1<CCSeries, T> l2)
	{
		if (e instanceof CCMovie)   return l1.invoke((CCMovie)    e);
		if (e instanceof CCSeries)  return l2.invoke((CCSeries)   e);
		if (e instanceof CCSeason)  return l2.invoke(((CCSeason)  e).getSeries());
		if (e instanceof CCEpisode) return l2.invoke(((CCEpisode) e).getSeries());

		CCLog.addDefaultSwitchError(SearchFrame.class, e);
		return null;
	}

	private static <T> T poly_mov(ICCDatabaseStructureElement e, Func1to1<CCMovie, T> l1)
	{
		if (e instanceof CCMovie) return l1.invoke((CCMovie) e);

		CCLog.addDefaultSwitchError(SearchFrame.class, e);
		return null;
	}

	private static <T> T poly_epi(ICCDatabaseStructureElement e, Func1to1<CCEpisode, T> l1)
	{
		if (e instanceof CCEpisode) return l1.invoke((CCEpisode) e);

		CCLog.addDefaultSwitchError(SearchFrame.class, e);
		return null;
	}

	private static <T> T poly_tl_ser(ICCDatabaseStructureElement e, Func1to1<CCSeries, T> l2)
	{
		if (e instanceof CCSeries)  return l2.invoke((CCSeries)   e);
		if (e instanceof CCSeason)  return l2.invoke(((CCSeason)  e).getSeries());
		if (e instanceof CCEpisode) return l2.invoke(((CCEpisode) e).getSeries());

		CCLog.addDefaultSwitchError(SearchFrame.class, e);
		return null;
	}

	private void onDblClick()
	{
		if (lsMain.getSelectedIndex() >= 0)
		{
			Object v = lsMain.getSelectedValue();
			if (v instanceof CCMovie)   PreviewMovieFrame.show(this,  (CCMovie)   v);
			if (v instanceof CCSeries)  PreviewSeriesFrame.show(this, (CCSeries)  v);
			if (v instanceof CCSeason)  PreviewSeriesFrame.show(this, (CCSeason)  v);
			if (v instanceof CCEpisode) PreviewSeriesFrame.show(this, (CCEpisode) v);
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

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		edSearch = new JTextField();
		scrollPane1 = new JScrollPane();
		lsMain = new JList<>();

		//======== this ========
		setTitle(LocaleBundle.getString("SearchFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				onFocusLost();
			}
		});
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $ugap", //$NON-NLS-1$
			"$ugap, default, $pgap, 0dlu:grow, $ugap")); //$NON-NLS-1$

		//---- edSearch ----
		edSearch.addActionListener(e -> onSearchFieldEnter());
		edSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				onSearchFieldKeyPressed(e);
			}
		});
		edSearch.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void removeUpdate(DocumentEvent arg0)  { onUpdate(); }
			@Override public void insertUpdate(DocumentEvent arg0)  { onUpdate(); }
			@Override public void changedUpdate(DocumentEvent arg0) { onUpdate(); }
		});
		contentPane.add(edSearch, CC.xy(2, 2));

		//======== scrollPane1 ========
		{

			//---- lsMain ----
			lsMain.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lsMain.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					onListMouseClicked(e);
				}
			});
			lsMain.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					onListKeyPressed(e);
				}
			});
			scrollPane1.setViewportView(lsMain);
		}
		contentPane.add(scrollPane1, CC.xy(2, 4, CC.FILL, CC.FILL));
		setSize(600, 400);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTextField edSearch;
	private JScrollPane scrollPane1;
	private JList<ICCDatabaseStructureElement> lsMain;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
