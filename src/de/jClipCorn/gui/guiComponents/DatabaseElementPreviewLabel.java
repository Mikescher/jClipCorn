package de.jClipCorn.gui.guiComponents;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.helper.ImageUtilities;

public class DatabaseElementPreviewLabel extends CoverLabel {
	private static final long serialVersionUID = -2960790547511297760L;
	
	private final static int TIMER_DELAY = 444;

	private boolean isError = false;
	private boolean timerSwitch = false;
	private Timer timer;
	
	public DatabaseElementPreviewLabel() {
		super(false);
		addMouseListener(this);
		
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
		
		if (CCProperties.getInstance().PROP_MAINFRAME_AUTOMATICRESETWATCHLATER.getValue()  && el.isSeries()) {
			BufferedImage bi = ImageUtilities.deepCopyImage(el.getCover());
			
			ImageUtilities.makeSeriesCover(bi);
			
			setIcon(new ImageIcon(bi));
		} else if (CCProperties.getInstance().PROP_MAINFRAME_SHOWTAGS.getValue() && el.isMovie() && ((CCMovie)el).getTags().hasTags()) {
			BufferedImage bi = ImageUtilities.deepCopyImage(el.getCover());
			
			CCMovie m = (CCMovie)el;
			
			m.getTags().drawOnImage(bi, false);
			
			setIcon(new ImageIcon(bi));
		} else {
			super.setIcon(el.getCoverIcon());
		}
	}
	
	public void setErrorDisplay(boolean disp) {
		if (disp ^ isError()) {
			isError = disp;
			if (! isError()) {
				setIcon(getStandardIcon());
			}
		}
	}

	public void onTimer() {
		timerSwitch = !timerSwitch;

		if (isError()) {
			if (timerSwitch) {
				setIcon(CachedResourceLoader.getImageIcon(Resources.IMG_COVER_ERROR_ON));
			} else {
				setIcon(CachedResourceLoader.getImageIcon(Resources.IMG_COVER_ERROR_OFF));
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && isError()) {
			CCActionTree.getInstance().find("ShowLog").execute(); //$NON-NLS-1$
			setErrorDisplay(false);
		}
	}
}
