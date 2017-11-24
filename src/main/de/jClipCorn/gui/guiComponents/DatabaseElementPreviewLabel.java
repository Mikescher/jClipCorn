package de.jClipCorn.gui.guiComponents;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.actionTree.ActionSource;
import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.gui.frames.coverPreviewFrame.CoverPreviewFrame;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.helper.ImageUtilities;

public class DatabaseElementPreviewLabel extends CoverLabel {
	private static final long serialVersionUID = -2960790547511297760L;
	
	private final static int TIMER_DELAY = 444;

	private boolean isError = false;
	private boolean timerSwitch = false;
	private Timer timer;
	
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
	
	public boolean isError() {
		return isError;
	}

	public void setDatabaseElement(CCDatabaseElement el) {
		if (isError()) {
			setErrorDisplay(false);
		}
		
		element = el;
		
		boolean drawSCorner = CCProperties.getInstance().PROP_MAINFRAME_SHOWCOVERCORNER.getValue()  && el.isSeries();
		boolean drawTag = CCProperties.getInstance().PROP_MAINFRAME_SHOWTAGS.getValue() && el.isMovie() && ((CCMovie)el).getTags().hasTags();
		boolean drawGroups = CCProperties.getInstance().PROP_MAINFRAME_SHOWGROUPS.getValue() && el.hasGroups();
		
		if (drawSCorner || drawTag || drawGroups) {
			BufferedImage bi = ImageUtilities.resizeCoverImageForFullSizeUI(el.getCover());
			
			if (drawSCorner) {
				ImageUtilities.makeFullSizeSeriesCover(bi);
			}
			
			if (drawTag) {
				((CCMovie)el).getTags().drawOnImage(bi, false);
			}
			
			if (drawGroups) {
				el.getGroups().drawOnImage(el.getMovieList(), bi);
			}

			super.setCoverDirect(bi, el.getCover());
		} else {
			super.setAndResizeCover(el.getCover());
		}
	}
	
	public void setErrorDisplay(boolean disp) {
		if (disp ^ isError()) {
			isError = disp;
			if (! isError()) {
				setIcon(getStandardIcon());
			}

			element = null;
		}
	}

	public void onTimer() {
		timerSwitch = !timerSwitch;

		if (isError()) {
			if (timerSwitch) {
				setIcon(CachedResourceLoader.getIcon(Resources.IMG_COVER_ERROR_ON));
			} else {
				setIcon(CachedResourceLoader.getIcon(Resources.IMG_COVER_ERROR_OFF));
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && isError()) {
			CCActionTree.getInstance().find("ShowLog").execute(ActionSource.DIRECT_CLICK); //$NON-NLS-1$
			setErrorDisplay(false);
		}
		else if (e.getClickCount() == 2) {
			if (element != null) new CoverPreviewFrame(this, element).setVisible(true);
		}
	}
}
