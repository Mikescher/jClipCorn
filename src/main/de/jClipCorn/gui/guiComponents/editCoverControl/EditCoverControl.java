package de.jClipCorn.gui.guiComponents.editCoverControl;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.gui.frames.coverCropFrame.CoverCropDialog;
import de.jClipCorn.gui.frames.findCoverFrame.FindCoverDialog;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jSplitButton.JSplitButton;
import de.jClipCorn.gui.guiComponents.tags.TagsChangedListener;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FileChooserHelper;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.ClipboardUtilities;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.http.HTTPUtilities;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EditCoverControl extends AbstractEditCoverControl {
	private static final long serialVersionUID = -4086336311809789696L;

	public final static int CTRL_WIDTH = ImageUtilities.BASE_COVER_WIDTH;
	public final static int CTRL_HEIGHT = ImageUtilities.BASE_COVER_HEIGHT + 34;

	@DesignCreate
	private static EditCoverControl designCreate() { return new EditCoverControl(ICCWindow.Dummy.frame(), null); }

	private BufferedImage fullImage;

	private final ParseResultHandler owner;
	private final JFileChooser coverFileChooser;

	private final ICCWindow ownerWindow; //JDialog or JWindow
	
	private CoverLabel lblCover;
	private JSplitButton btnFind;
	private JButton btnCrop;
	private JLabel lblResolution;
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

		coverFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("EditCoverControl.coverFileChooser.description", "png", "bmp", "gif", "jpg", "jpeg")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

		coverFileChooser.setDialogTitle(LocaleBundle.getString("EditCoverControl.coverFileChooser.title")); //$NON-NLS-1$

		initGUI();

		btnCrop.setEnabled(isEnabled() && isCoverSet());
	}

	private void initGUI() {
		setLayout(null);

		lblCover = new CoverLabel(ownerWindow.getMovieList(), false);
		lblCover.setPosition(0, 34);
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
		    	lblResolution.setVisible(true); 
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
		btnFind.setBounds(102, 0, 80, 23);
		add(btnFind, 0);

		btnCrop = new JButton(LocaleBundle.getString("EditCoverControl.btnCrop.text")); //$NON-NLS-1$
		btnCrop.addActionListener(e -> showCropDialog());
		btnCrop.setBounds(0, 0, 70, 23);
		add(btnCrop, 0);

		setSize(CTRL_WIDTH, CTRL_HEIGHT);
		setPreferredSize(new Dimension(CTRL_WIDTH, CTRL_HEIGHT));
	}

	public void addCoverChangedListener(final CoverChangedListener l) {
		listenerList.add(CoverChangedListener.class, l);
	}
	public void removeCoverChangedListener(final CoverChangedListener l) {
		listenerList.add(CoverChangedListener.class, l);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(CTRL_WIDTH, CTRL_HEIGHT);
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(CTRL_WIDTH, CTRL_HEIGHT);
	}
	
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(CTRL_WIDTH, CTRL_HEIGHT);
	}
	
	@Override
	public Dimension getSize() {
		return new Dimension(CTRL_WIDTH, CTRL_HEIGHT);
	}

	private void showChooseCoverDialog() {
		int returnval = coverFileChooser.showOpenDialog(this);

		if (returnval != JFileChooser.APPROVE_OPTION) {
			return;
		}

		try {
			BufferedImage read = ImageIO.read(coverFileChooser.getSelectedFile());
			setCover(read);
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}

	private void showFindCoverDialog() {
		(new FindCoverDialog((Component)ownerWindow, ownerWindow.getMovieList(), this, CCDBElementTyp.MOVIE)).setVisible(true);
	}

	private void showCropDialog() {
		if (!isCoverSet())
			return;

		(new CoverCropDialog(this, ownerWindow.getMovieList(), fullImage, this)).setVisible(true);
	}

	private boolean parseURL() {
		String url = ClipboardUtilities.getURL();

		if (url == null)
			return false;

		BufferedImage img = HTTPUtilities.getImage(ownerWindow.getMovieList(), url);

		if (img == null)
			return false;

		setCover(img);

		return true;
	}

	private boolean parseImage() {
		BufferedImage img = ClipboardUtilities.getImage();

		if (img == null)
			return false;

		setCover(img);

		return true;
	}

	public boolean isCoverSet() {
		return fullImage != null;
	}

	public BufferedImage getFullImage() {
		return fullImage;
	}

	public BufferedImage getResizedImageForStorage() {
		if (!isCoverSet())
			return null;

		return ImageUtilities.resizeCoverImageForStorage(getFullImage(), ownerWindow.ccprops());
	}

	@Override
	public String getFullTitle() {
		return owner.getFullTitle();
	}

	@Override
	public String getTitleForParser() {
		return owner.getTitleForParser();
	}

	@Override
	public void setCover(BufferedImage nci) {
		if (nci != null) {
			this.fullImage = nci;

			BufferedImage resized = ImageUtilities.resizeCoverImageForFullSizeUI(nci);
			
			if (!ImageUtilities.isImageRatioAcceptable(nci.getWidth(), nci.getHeight())) {
				Point tr = ImageUtilities.getTopRightNonTransparentPixel(resized);
				
				resized.getGraphics().drawImage(Resources.ICN_WARNING_TRIANGLE.getImage32x32(), tr.x - 32 - 2, tr.y + 2, null);
			}
					
			lblCover.setCoverDirect(resized, nci);

			lblResolution.setText(nci.getWidth() + " x " + nci.getHeight()); //$NON-NLS-1$
			Point tl = ImageUtilities.getTopLeftNonTransparentPixel(resized);
			lblResolution.setBounds(lblCover.getBounds().x + tl.x, lblCover.getBounds().y + tl.y, (int)lblResolution.getPreferredSize().getWidth(), (int)lblResolution.getPreferredSize().getHeight());
		} else {
			this.fullImage = null;

			lblCover.clearCover();
			
			lblResolution.setText(""); //$NON-NLS-1$
		}

		btnCrop.setEnabled(isCoverSet() && isEnabled());

		for (var a : listenerList.getListeners(TagsChangedListener.class)) a.tagsChanged(new ActionEvent(nci, -1, Str.Empty));
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
		btnCrop.setEnabled(e && isCoverSet());
		btnFind.setEnabled(e);
	}
}
