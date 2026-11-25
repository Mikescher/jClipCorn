package de.jClipCorn.gui.guiComponents.editCoverControl;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.gui.frames.coverCropFrame.CoverCropDialog;
import de.jClipCorn.gui.frames.findCoverFrame.FindCoverDialog;
import de.jClipCorn.gui.guiComponents.cover.CoverLabel;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jSplitButton.JSplitButton;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.ClipboardUtilities;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.lambda.Func0to1WithException;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class EditCoverControl extends AbstractEditCoverControl {
	private static final long serialVersionUID = -4086336311809789696L;

	public final static int CTRL_WIDTH = ImageUtilities.BASE_COVER_WIDTH;
	public final static int CTRL_HEIGHT = ImageUtilities.BASE_COVER_HEIGHT + 34;

	@DesignCreate
	private static EditCoverControl designCreate() { return new EditCoverControl(ICCWindow.Dummy.frame(), null); }

	// HiDPI/fractional-scaling factor - the surrounding (dlu-based) layout scales with the font, so this
	// fixed-pixel control must scale too, otherwise it stays tiny and the buttons truncate their labels.
	private final double uiScale;

	private int ctrlWidth;
	private int ctrlHeight;

	private BufferedImage fullImage;
	private BufferedImage storageImage;

	// incremented by every cover change; a background load only applies its result if it is still the latest
	private int loadGeneration = 0;
	private boolean loading = false;
	private int loadingFrame = 0;
	private Icon[] loadingIcons;
	private final Timer loadingTimer = new Timer(100, e -> nextLoadingFrame());

	private final ParseResultHandler owner;
	private final JFileChooser coverFileChooser;

	private final ICCWindow ownerWindow; //JDialog or JWindow
	
	private CoverLabel lblCover;
	private JSplitButton btnFind;
	private JButton btnCrop;
	private JLabel lblResolution;
	private JLabel lblLoading;
	private JPopupMenu popupMenu;
	private JMenuItem mntmFind;
	private JMenuItem mntmOpen;
	private JMenuItem mntmPasteurl;
	private JMenuItem mntmPasteImg;

	public EditCoverControl(@NotNull ICCWindow owner, ParseResultHandler handler) {
		super();

		this.coverFileChooser = new JFileChooser(FilesystemUtils.getAbsoluteSelfDirectory(owner.ccprops()).toFile());
		this.owner = handler;
		this.ownerWindow = owner;
		this.uiScale = computeUIScale();
		this.ctrlWidth = scale(ImageUtilities.BASE_COVER_WIDTH);
		this.ctrlHeight = scale(ImageUtilities.BASE_COVER_HEIGHT) + scale(34);

		coverFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("EditCoverControl.coverFileChooser.description", "png", "bmp", "gif", "jpg", "jpeg")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

		coverFileChooser.setDialogTitle(LocaleBundle.getString("EditCoverControl.coverFileChooser.title")); //$NON-NLS-1$

		initGUI();

		updateButtonState();
	}

	private void initGUI() {
		setLayout(null);

		int coverW = scale(ImageUtilities.BASE_COVER_WIDTH);
		int coverH = scale(ImageUtilities.BASE_COVER_HEIGHT);

		lblCover = new CoverLabel(ownerWindow.getMovieList(), false);
		lblCover.setDisplayScale(uiScale);
		add(lblCover, 1);

		lblResolution = new JLabel();
		lblResolution.setBounds(0, 0, 0, 0);
		lblResolution.setForeground(Color.WHITE);
		lblResolution.setBackground(Color.DARK_GRAY);
		lblResolution.setOpaque(true);
		lblResolution.setFocusable(false);
    	lblResolution.setVisible(false); 
		add(lblResolution, 0);
		
		lblCover.addMouseListener(new MouseAdapter() {
		    @Override
			public void mouseEntered(MouseEvent e) { 
		    	lblResolution.setVisible(!loading);
		    }

		    @Override
			public void mouseExited(MouseEvent e) { 
		    	lblResolution.setVisible(false); 
		    }
		});

		popupMenu = new JPopupMenu();
		{
			mntmFind = new JMenuItem(LocaleBundle.getString("EditCoverControl.btnFindExtended.text")); //$NON-NLS-1$
			mntmFind.addActionListener(e -> showFindCoverDialog());
			popupMenu.add(mntmFind);

			popupMenu.addSeparator();

			mntmOpen = new JMenuItem(LocaleBundle.getString("EditCoverControl.btnOpen.text")); //$NON-NLS-1$
			mntmOpen.addActionListener(e -> showChooseCoverDialog());
			popupMenu.add(mntmOpen);

			mntmPasteurl = new JMenuItem(LocaleBundle.getString("EditCoverControl.btnPasteURL.text")); //$NON-NLS-1$
			mntmPasteurl.addActionListener(e -> parseURL());
			popupMenu.add(mntmPasteurl); //

			mntmPasteImg = new JMenuItem(LocaleBundle.getString("EditCoverControl.btnPasteIMG.text")); //$NON-NLS-1$
			mntmPasteImg.addActionListener(e -> parseImage());
			popupMenu.add(mntmPasteImg);
		}

		btnFind = new JSplitButton(LocaleBundle.getString("EditCoverControl.btnFind.text")); //$NON-NLS-1$
		btnFind.addButtonClickedActionListener(e -> showFindCoverDialog());
		btnFind.setPopupMenu(popupMenu);
		add(btnFind, 0);

		btnCrop = new JButton(LocaleBundle.getString("EditCoverControl.btnCrop.text")); //$NON-NLS-1$
		btnCrop.addActionListener(e -> showCropDialog());
		add(btnCrop, 0);

		// Size the buttons to their preferred (font-dependent) size so the labels never truncate,
		// then place crop at the left and find at the right of the top bar. The find button needs
		// extra room because JSplitButton paints its dropdown arrow over the rightmost splitWidth.
		Dimension dCrop = btnCrop.getPreferredSize();
		Dimension dFind = btnFind.getPreferredSize();
		int findWidth = dFind.width + btnFind.getSplitWidth();
		int barHeight = Math.max(dCrop.height, dFind.height);
		btnCrop.setBounds(0, 0, dCrop.width, barHeight);
		btnFind.setBounds(coverW - findWidth, 0, findWidth, barHeight);

		int coverY = Math.max(scale(34), barHeight + scale(6));
		lblCover.setPosition(0, coverY);

		lblLoading = new JLabel();
		lblLoading.setBounds(0, coverY, coverW, coverH);
		lblLoading.setVisible(false);
		add(lblLoading, 0);

		ctrlWidth = coverW;
		ctrlHeight = coverY + coverH;

		setSize(ctrlWidth, ctrlHeight);
		setPreferredSize(new Dimension(ctrlWidth, ctrlHeight));
	}

	private int scale(int value) {
		return (int)Math.round(value * uiScale);
	}

	private static double computeUIScale() {
		Font f = UIManager.getFont("Label.font"); //$NON-NLS-1$
		if (f == null) f = UIManager.getFont("Button.font"); //$NON-NLS-1$
		double size = (f != null) ? f.getSize2D() : 12.0;
		return Math.max(1.0, size / 12.0);
	}

	public void addCoverChangedListener(final CoverChangedListener l) {
		listenerList.add(CoverChangedListener.class, l);
	}
	public void removeCoverChangedListener(final CoverChangedListener l) {
		listenerList.remove(CoverChangedListener.class, l);
	}

	public void addCoverLoadingListener(final CoverLoadingListener l) {
		listenerList.add(CoverLoadingListener.class, l);
	}
	public void removeCoverLoadingListener(final CoverLoadingListener l) {
		listenerList.remove(CoverLoadingListener.class, l);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(ctrlWidth, ctrlHeight);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(ctrlWidth, ctrlHeight);
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(ctrlWidth, ctrlHeight);
	}

	@Override
	public Dimension getSize() {
		return new Dimension(ctrlWidth, ctrlHeight);
	}

	private void showChooseCoverDialog() {
		int returnval = coverFileChooser.showOpenDialog(this);

		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = coverFileChooser.getSelectedFile();
		loadCoverAsync(() -> ImageIO.read(file));
	}

	private void showFindCoverDialog() {
		(new FindCoverDialog((Component)ownerWindow, ownerWindow.getMovieList(), this, CCDBElementTyp.MOVIE)).setVisible(true);
	}

	private void showCropDialog() {
		if (!isCoverSet())
			return;

		(new CoverCropDialog(this, ownerWindow.getMovieList(), fullImage, this)).setVisible(true);
	}

	private void parseURL() {
		loadCoverAsync(() -> {
			String url = ClipboardUtilities.getURL();
			if (url == null) return null;
			return HTTPUtilities.getImage(ownerWindow.getMovieList(), url);
		});
	}

	private void parseImage() {
		loadCoverAsync(ClipboardUtilities::getImage);
	}

	public boolean isCoverSet() {
		return fullImage != null;
	}

	/**
	 * true while a cover is fetched/prepared in the background - the shown cover is not the final one yet
	 */
	public boolean isLoading() {
		return loading;
	}

	public BufferedImage getFullImage() {
		return fullImage;
	}

	public BufferedImage getResizedImageForStorage() {
		if (!isCoverSet())
			return null;

		if (storageImage == null) storageImage = ImageUtilities.resizeCoverImageForStorage(getFullImage(), ownerWindow.ccprops());

		return storageImage;
	}

	@Override
	public String getFullTitle() {
		return owner.getFullTitle();
	}

	@Override
	public String getTitleForParser() {
		return owner.getTitleForParser();
	}

	/**
	 * Prepares the cover in a background thread, {@link #isLoading()} until it is applied.
	 * Use {@link #setCoverImmediate} for already stored (small) covers, it fires no delayed change event.
	 */
	@Override
	public void setCover(BufferedImage nci) {
		if (nci == null) { setCoverImmediate(null); return; }

		loadCoverAsync(() -> nci);
	}

	/**
	 * Sets the cover synchronously and cancels any pending background load.
	 */
	public void setCoverImmediate(BufferedImage nci) {
		loadGeneration++;
		setLoading(false);

		applyCover(nci == null ? null : prepareCover(nci, Resources.ICN_WARNING_TRIANGLE.getImage32x32(), false));
	}

	/**
	 * A supplier result of null keeps the current cover.
	 */
	private void loadCoverAsync(Func0to1WithException<BufferedImage> supplier) {
		final int gen = ++loadGeneration;
		setLoading(true);

		// CachedResourceLoader is not thread-safe
		final BufferedImage warnIcon = Resources.ICN_WARNING_TRIANGLE.getImage32x32();

		Thread t = new Thread(() ->
		{
			PreparedCover result = null;
			try {
				BufferedImage img = supplier.invoke();
				if (img != null) result = prepareCover(img, warnIcon, true);
			} catch (Throwable e) {
				CCLog.addError(e);
			}

			final PreparedCover fresult = result;
			SwingUtils.invokeLater(() ->
			{
				if (gen != loadGeneration) return;

				setLoading(false);
				if (fresult != null) applyCover(fresult);
			});
		}, "THREAD_LOAD_COVER"); //$NON-NLS-1$
		t.setDaemon(true);
		t.start();
	}

	private record PreparedCover(BufferedImage full, BufferedImage display, BufferedImage storage) {}

	private PreparedCover prepareCover(BufferedImage nci, BufferedImage warnIcon, boolean withStorage) {
		BufferedImage resized = ImageUtilities.resizeCoverImageForFullSizeUI(nci, scale(ImageUtilities.BASE_COVER_WIDTH), scale(ImageUtilities.BASE_COVER_HEIGHT));

		if (!ImageUtilities.isImageRatioAcceptable(nci.getWidth(), nci.getHeight())) {
			Point tr = ImageUtilities.getTopRightNonTransparentPixel(resized);

			resized.getGraphics().drawImage(warnIcon, tr.x - 32 - 2, tr.y + 2, null);
		}

		BufferedImage storage = withStorage ? ImageUtilities.resizeCoverImageForStorage(nci, ownerWindow.ccprops()) : null;

		return new PreparedCover(nci, resized, storage);
	}

	private void applyCover(PreparedCover pc) {
		if (pc != null) {
			this.fullImage = pc.full;
			this.storageImage = pc.storage;

			lblCover.setCoverDirect(pc.display, pc.full);

			lblResolution.setText(pc.full.getWidth() + " x " + pc.full.getHeight()); //$NON-NLS-1$
			Point tl = ImageUtilities.getTopLeftNonTransparentPixel(pc.display);
			lblResolution.setBounds(lblCover.getBounds().x + tl.x, lblCover.getBounds().y + tl.y, (int)lblResolution.getPreferredSize().getWidth(), (int)lblResolution.getPreferredSize().getHeight());
		} else {
			this.fullImage = null;
			this.storageImage = null;

			lblCover.clearCover();

			lblResolution.setText(""); //$NON-NLS-1$
		}

		updateButtonState();

		for (var a : listenerList.getListeners(CoverChangedListener.class)) a.coverChanged(new ActionEvent(this, -1, Str.Empty));
	}

	private void setLoading(boolean v) {
		if (loading == v) return;
		loading = v;

		if (v) {
			loadingFrame = 0;
			lblLoading.setIcon(getLoadingIcons()[0]);
			lblLoading.setVisible(true);
			lblResolution.setVisible(false);
			loadingTimer.start();
		} else {
			loadingTimer.stop();
			lblLoading.setVisible(false);
			lblLoading.setIcon(null);
		}

		updateButtonState();

		for (var a : listenerList.getListeners(CoverLoadingListener.class)) a.loadingChanged(v);
	}

	private void nextLoadingFrame() {
		var icons = getLoadingIcons();
		loadingFrame = (loadingFrame + 1) % icons.length;
		lblLoading.setIcon(icons[loadingFrame]);
	}

	private Icon[] getLoadingIcons() {
		if (loadingIcons == null) {
			var refs = new IconRef[] {
				Resources.ICN_COVER_LOADING_P0, Resources.ICN_COVER_LOADING_P1, Resources.ICN_COVER_LOADING_P2, Resources.ICN_COVER_LOADING_P3,
				Resources.ICN_COVER_LOADING_P4, Resources.ICN_COVER_LOADING_P5, Resources.ICN_COVER_LOADING_P6, Resources.ICN_COVER_LOADING_P7,
			};
			var icons = new Icon[refs.length];
			for (int i = 0; i < refs.length; i++) {
				icons[i] = (uiScale == 1.0)
					? refs[i].get()
					: new ImageIcon(ImageUtilities.getScaledInstance(refs[i].getImage(), lblLoading.getWidth(), lblLoading.getHeight()));
			}
			loadingIcons = icons;
		}
		return loadingIcons;
	}

	private void updateButtonState() {
		btnCrop.setEnabled(isEnabled() && isCoverSet() && !loading);
		btnFind.setEnabled(isEnabled() && !loading);
	}

	@Override
	public CCOnlineReferenceList getSearchReference() {
		return owner.getSearchReference();
	}

	@Override
	public void editingFinished(BufferedImage i) {
		setCover(i);
	}

	@Override
	public void setEnabled(boolean e) {
		super.setEnabled(e);
		updateButtonState();
	}
}
