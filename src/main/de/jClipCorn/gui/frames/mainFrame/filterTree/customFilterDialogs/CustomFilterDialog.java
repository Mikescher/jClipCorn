package de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.AbstractCustomFilter;
import de.jClipCorn.util.listener.FinishListener;

public abstract class CustomFilterDialog extends JDialog implements WindowListener{
	private static final long serialVersionUID = -6822558028101935911L;
	
	private AbstractCustomFilter filter;
	private FinishListener finListener;
	
	private boolean hasClosed = false;

	public CustomFilterDialog(AbstractCustomFilter filter, FinishListener finListener) {
		super();
		this.filter = filter;
		this.finListener = finListener;
		
		init();
	}
	
	private void init() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setModal(true);
		setResizable(false);
		
		updateTitle();
		
		addWindowListener(this);
	}
	
	protected void updateTitle() {
		if (filter != null) setTitle(filter.getName());
	}
	
	protected AbstractCustomFilter getFilter() {
		return filter;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// Nothing
	}

	protected void onOK() {
		onAfterOK();
		
		if (finListener != null && ! hasClosed) {
			finListener.finish();
		}
		
		dispose();

		hasClosed = true;
	}
	
	protected abstract void onAfterOK();

	@Override
	public void windowClosing(WindowEvent arg0) {
		// Nothing
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// Nothing
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// Nothing
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// Nothing
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// Nothing
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// Nothing
	}
}
