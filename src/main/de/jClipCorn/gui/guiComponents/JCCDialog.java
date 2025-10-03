package de.jClipCorn.gui.guiComponents;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class JCCDialog extends JDialog implements ICCWindow
{
	protected final CCMovieList movielist;

	private boolean frameSizeDebugInitialized = false;
	private boolean titleIsPatchedWithSize = true;

	private String _titleCache = null;

	@DesignCreate
	private static JCCDialog designCreate() { return new JCCDialog(null); }

	public JCCDialog(CCMovieList ml)
	{
		super();
		movielist = ml;

		setIconImage(Resources.IMG_FRAME_ICON.get());
	}

	@Override
	public CCMovieList getMovieList() {
		return movielist;
	}

	@Override
	public CCProperties ccprops()
	{
		return movielist.ccprops();
	}

	public void onSizeAppliedViaProp() {
		if (!frameSizeDebugInitialized) {
			frameSizeDebugInitialized = true;
			this.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					super.componentResized(e);
					JCCDialog.this.patchTitleWithSize();
				}
			});
			this.patchTitleWithSize();
		}
	}

	@Override
	public void setTitle(String v) {
		_titleCache = v;
		super.setTitle(v);

		if (frameSizeDebugInitialized) {
			this.patchTitleWithSize();
		}
	}

	public String getTitle() {
		if (_titleCache != null) return _titleCache;
		return super.getTitle();
	}

	private void patchTitleWithSize() {
		if (_titleCache == null) _titleCache = this.getTitle();

		if (ccprops().PROP_FSIZE_DEBUG.getValue()) {
			super.setTitle(_titleCache + "  " + "<" + this.getWidth() + " x " + this.getHeight() + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			titleIsPatchedWithSize = true;
		} else if (titleIsPatchedWithSize) {
			super.setTitle(_titleCache);
			titleIsPatchedWithSize = frameSizeDebugInitialized;
		}
	}
}
