package de.jClipCorn.gui.guiComponents;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.actionTree.ActionSource;
import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.gui.frames.coverPreviewFrame.CoverPreviewFrame;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.lambda.Func0to0;

public class DatabaseElementPreviewLabel extends CoverLabel {
	private static final long serialVersionUID = -2960790547511297760L;
	
	private final static int TIMER_DELAY = 100;

	private enum DEPLMode {
		MODE_DEFAULT,
		MODE_COVER,
		MODE_ERROR,
		MODE_LOADING,
		MODE_SYNCLOADING,
	}
	
	private volatile DEPLMode mode = DEPLMode.MODE_DEFAULT;
	private volatile int timerCounter = 0;
	private volatile boolean threadLoadFinished = false;
	private Timer timer;

	private volatile BufferedImage v_image_normal;
	private volatile BufferedImage v_image_hover;
	private volatile BufferedImage v_image_original;
	
	private BufferedImage image_normal;
	private BufferedImage image_hover;
	private BufferedImage image_original;
	private CCDatabaseElement element;
	
	public DatabaseElementPreviewLabel() {
		super(false);
		
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				onTimer();
			}
		}, 0, TIMER_DELAY);
	}
	
	private BufferedImage getImageWithoutOverlay(CCDatabaseElement el) {
		boolean drawSCorner = CCProperties.getInstance().PROP_MAINFRAME_SHOWCOVERCORNER.getValue()  && el.isSeries();
		
		if (drawSCorner) {
			BufferedImage biorig = el.getCover();
			
			BufferedImage bi = ImageUtilities.resizeCoverImageForFullSizeUI(biorig);
			if (bi == biorig) bi = ImageUtilities.deepCopyImage(bi);
			
			if (drawSCorner) {
				ImageUtilities.makeFullSizeSeriesCover(bi);
			}

			return bi;
		} else {
			return ImageUtilities.resizeCoverImageForFullSizeUI(el.getCover());
		}
	}
	
	private BufferedImage getImageWithOverlay(CCDatabaseElement el, boolean alpha) {
		boolean drawSCorner = CCProperties.getInstance().PROP_MAINFRAME_SHOWCOVERCORNER.getValue()  && el.isSeries();
		boolean drawTag = CCProperties.getInstance().PROP_MAINFRAME_SHOWTAGS.getValue() && el.isMovie() && ((CCMovie)el).getTags().hasTags();
		boolean drawGroups = CCProperties.getInstance().PROP_MAINFRAME_SHOWGROUPS.getValue() && el.hasGroups();
		
		if (drawSCorner || drawTag || drawGroups) {
			BufferedImage biorig = el.getCover();
			
			BufferedImage bi = ImageUtilities.resizeCoverImageForFullSizeUI(biorig);
			if (bi == biorig) bi = ImageUtilities.deepCopyImage(bi);
			
			if (drawSCorner) {
				ImageUtilities.makeFullSizeSeriesCover(bi);
			}
			
			if (drawTag) {
				((CCMovie)el).getTags().drawOnImage(bi, false);
			}
			
			if (drawGroups) {
				el.getGroups().drawOnImage(el.getMovieList(), bi, alpha);
			}

			return bi;
		} else {
			return ImageUtilities.resizeCoverImageForFullSizeUI(el.getCover());
		}
	}
	
	public void onTimer() {

		int tcOld = timerCounter;
		int tcNew = tcOld + 1;
		timerCounter = tcNew;
		
		switch (mode) {
		case MODE_SYNCLOADING:
			// nothing
			break;
			
		case MODE_COVER:
			// nothing
			break;
			
		case MODE_DEFAULT:
			// nothing
			break;
			
		case MODE_ERROR:
			boolean bOld = (tcOld/4)%2 == 0;
			boolean bNew = (tcNew/4)%2 == 0;
			if (bOld != bNew) {
				if (bNew)
					swingInvoke(() -> {setIcon(CachedResourceLoader.getIcon(Resources.IMG_COVER_ERROR_ON));});
				else
					swingInvoke(() -> {setIcon(CachedResourceLoader.getIcon(Resources.IMG_COVER_ERROR_OFF));});
			}
			break;
			
		case MODE_LOADING:
			if (tcNew % 16 ==  0) swingInvoke(() -> {setIcon(CachedResourceLoader.getIcon(Resources.IMG_COVER_LOADING_P7));});
			if (tcNew % 16 ==  2) swingInvoke(() -> {setIcon(CachedResourceLoader.getIcon(Resources.IMG_COVER_LOADING_P0));});
			if (tcNew % 16 ==  4) swingInvoke(() -> {setIcon(CachedResourceLoader.getIcon(Resources.IMG_COVER_LOADING_P1));});
			if (tcNew % 16 ==  6) swingInvoke(() -> {setIcon(CachedResourceLoader.getIcon(Resources.IMG_COVER_LOADING_P2));});
			if (tcNew % 16 ==  8) swingInvoke(() -> {setIcon(CachedResourceLoader.getIcon(Resources.IMG_COVER_LOADING_P3));});
			if (tcNew % 16 == 10) swingInvoke(() -> {setIcon(CachedResourceLoader.getIcon(Resources.IMG_COVER_LOADING_P4));});
			if (tcNew % 16 == 12) swingInvoke(() -> {setIcon(CachedResourceLoader.getIcon(Resources.IMG_COVER_LOADING_P5));});
			if (tcNew % 16 == 14) swingInvoke(() -> {setIcon(CachedResourceLoader.getIcon(Resources.IMG_COVER_LOADING_P6));});
			break;
		}
	}
	
	private void swingInvoke(Func0to0 f) {
		try	{
			SwingUtilities.invokeAndWait(() -> f.invoke());
		} catch (InterruptedException e) {
			CCLog.addError(e);
		} catch (InvocationTargetException e) {
			CCLog.addError(e);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && mode == DEPLMode.MODE_ERROR) {
			CCActionTree.getInstance().find("ShowLog").execute(ActionSource.DIRECT_CLICK); //$NON-NLS-1$
			setModeDefault();
		}
		else if (e.getClickCount() == 2) {
			if (element != null) new CoverPreviewFrame(this, element).setVisible(true);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (mode == DEPLMode.MODE_COVER && image_hover != null) setCoverDirect(image_hover, image_original);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (mode == DEPLMode.MODE_COVER && image_hover != null) setCoverDirect(image_normal, image_original);
	}
	
	public void setModeDefault() {
		timerCounter = 0;

		element        = null;
		image_hover    = null;
		image_normal   = null;
		image_original = null;

		mode = DEPLMode.MODE_DEFAULT;
		super.setCoverDirect(image_normal, image_original);
	}
	
	public void setModeError() {
		timerCounter = 0;

		element        = null;
		image_hover    = null;
		image_normal   = null;
		image_original = null;
		
		mode = DEPLMode.MODE_ERROR;
		super.setCoverDirect(CachedResourceLoader.getImage(Resources.IMG_COVER_ERROR_ON), null);
	}
	
	public void setModeCover(CCDatabaseElement el) {
		if (CCProperties.getInstance().PROP_MAINFRAME_ASYNC_COVER_LOADING.getValue()) {

			if (el.getMovieList().getCoverCache().isCached(el.getCoverName()))
				setModeCoverSync(el);
			else
				setModeCoverAsync(el);
				
		} else {

			setModeCoverSync(el);
			
		}
	}
	
	private void setModeCoverSync(CCDatabaseElement el) {
		timerCounter = 0;
	
		element = el;
		
		if (CCProperties.getInstance().PROP_MAINFRAME_SHOW_GROUP_ONLY_ON_HOVER.getValue()) {
			image_original = el.getCover();
			image_normal   = getImageWithoutOverlay(el);
			image_hover    = getImageWithOverlay(el, false);
		} else {
			image_original = el.getCover();
			image_normal   = getImageWithOverlay(el, true);
			image_hover    = image_normal;
		}

		mode = DEPLMode.MODE_COVER;
		
		super.setCoverDirect(image_normal, image_original);
	}

	private void setModeCoverAsync(CCDatabaseElement el) {
		
		if (mode == DEPLMode.MODE_LOADING) {

			element        = el;
			image_hover    = null;
			image_normal   = null;
			image_original = null;

			Thread t = new Thread(() -> LoadAsync(el));
			t.start();
			
		} else {

			element        = el;
			image_hover    = null;
			image_normal   = null;
			image_original = null;

			mode = DEPLMode.MODE_SYNCLOADING;
			threadLoadFinished = false;
			Thread t = new Thread(() -> LoadAsync(el));
			t.start();
			long starttime = System.currentTimeMillis();
			while (System.currentTimeMillis() - starttime < 100) {
				if (threadLoadFinished) { 
					mode = DEPLMode.MODE_COVER; 
					image_original = v_image_original;
					image_normal   = v_image_normal;
					image_hover    = v_image_hover;
					super.setCoverDirect(v_image_normal, v_image_original);
					return; 
				} // fast loading
				
				try { Thread.sleep(0); } catch (InterruptedException e) { /**/ }
			}

			timerCounter = 0;
			mode = DEPLMode.MODE_LOADING;
		}
	}

	private void LoadAsync(CCDatabaseElement el) {

		BufferedImage i1;
		BufferedImage i2;
		BufferedImage i3;
		
		if (CCProperties.getInstance().PROP_MAINFRAME_SHOW_GROUP_ONLY_ON_HOVER.getValue()) {
			i1 = el.getCover();
			i2 = getImageWithoutOverlay(el);
			i3 = getImageWithOverlay(el, false);
		} else {
			i1 = el.getCover();
			i2 = getImageWithOverlay(el, true);
			i3 = i2;
		}
		
		if (element != el) return;
		
		threadLoadFinished = true;

		v_image_original = i1;
		v_image_normal   = i2;
		v_image_hover    = i3;
		
		swingInvoke(() -> 
		{
			if (element != el) return;

			image_original = i1;
			image_normal   = i2;
			image_hover    = i3;

			mode = DEPLMode.MODE_COVER;
			super.setCoverDirect(image_normal, image_original);
		});
	}

	public boolean isErrorMode() {
		return mode == DEPLMode.MODE_ERROR;
	}
	
}
