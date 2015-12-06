package de.jClipCorn.gui.guiComponents.editCoverControl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.gpl.JSplitButton.JSplitButton;
import org.gpl.JSplitButton.action.SplitButtonActionListener;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.frames.coverCropFrame.CoverCropDialog;
import de.jClipCorn.gui.frames.findCoverFrame.FindCoverDialog;
import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ClipboardUtilities;
import de.jClipCorn.util.helper.FileChooserHelper;
import de.jClipCorn.util.helper.HTTPUtilities;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.parser.ParseResultHandler;

public class EditCoverControl extends AbstractEditCoverControl {
	private static final long serialVersionUID = -4086336311809789696L;

	public final static int CTRL_WIDTH = ImageUtilities.COVER_WIDTH;
	public final static int CTRL_HEIGHT = 288;

	private BufferedImage fullImage;

	private final ParseResultHandler owner;
	private final JFileChooser coverFileChooser;

	private CoverLabel lblCover;
	private JSplitButton btnFind;
	private JButton btnCrop;
	private JPopupMenu popupMenu;
	private JMenuItem mntmFind;
	private JMenuItem mntmOpen;
	private JMenuItem mntmPasteurl;
	private JMenuItem mntmPasteImg;

	public EditCoverControl() { // For WindowBuilder
		this(null);
	}

	public EditCoverControl(ParseResultHandler handler) {
		super();

		this.coverFileChooser = new JFileChooser(PathFormatter.getAbsoluteSelfDirectory());
		this.owner = handler;

		coverFileChooser.setFileFilter(FileChooserHelper.createLocalFileFilter("EditCoverControl.coverFileChooser.description", "png", "bmp", "gif", "jpg", "jpeg")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

		coverFileChooser.setDialogTitle(LocaleBundle.getString("EditCoverControl.coverFileChooser.title")); //$NON-NLS-1$

		initGUI();

		btnCrop.setEnabled(isEnabled() && isCoverSet());
	}

	private void initGUI() {
		setLayout(null);

		lblCover = new CoverLabel(false);
		lblCover.setBounds(0, 34, ImageUtilities.COVER_WIDTH, ImageUtilities.COVER_HEIGHT);
		add(lblCover);

		popupMenu = new JPopupMenu();
		{
			mntmFind = new JMenuItem(LocaleBundle.getString("EditCoverControl.btnFindExtended.text")); //$NON-NLS-1$
			mntmFind.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showFindCoverDialog();
				}
			});
			popupMenu.add(mntmFind);

			popupMenu.addSeparator();

			mntmOpen = new JMenuItem(LocaleBundle.getString("EditCoverControl.btnOpen.text")); //$NON-NLS-1$
			mntmOpen.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showChooseCoverDialog();
				}
			});
			popupMenu.add(mntmOpen);

			mntmPasteurl = new JMenuItem(LocaleBundle.getString("EditCoverControl.btnPasteURL.text")); //$NON-NLS-1$
			mntmPasteurl.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					parseURL();
				}
			});
			popupMenu.add(mntmPasteurl); //

			mntmPasteImg = new JMenuItem(LocaleBundle.getString("EditCoverControl.btnPasteIMG.text")); //$NON-NLS-1$
			mntmPasteImg.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					parseImage();
				}
			});
			popupMenu.add(mntmPasteImg);
		}

		btnFind = new JSplitButton(LocaleBundle.getString("EditCoverControl.btnFind.text")); //$NON-NLS-1$
		btnFind.addSplitButtonActionListener(new SplitButtonActionListener() {
			@Override
			public void splitButtonClicked(ActionEvent arg0) {
				// NOP
			}

			@Override
			public void buttonClicked(ActionEvent arg0) {
				showFindCoverDialog();
			}
		});
		btnFind.setPopupMenu(popupMenu);
		btnFind.setBounds(102, 0, 80, 23);
		add(btnFind);

		btnCrop = new JButton(LocaleBundle.getString("EditCoverControl.btnCrop.text")); //$NON-NLS-1$
		btnCrop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showCropDialog();
			}
		});
		btnCrop.setBounds(0, 0, 70, 23);
		add(btnCrop);

		setSize(CTRL_WIDTH, CTRL_HEIGHT);
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
		(new FindCoverDialog(this, this, CCMovieTyp.MOVIE)).setVisible(true);
	}

	private void showCropDialog() {
		if (!isCoverSet())
			return;

		(new CoverCropDialog(this, fullImage, this)).setVisible(true);
	}

	private boolean parseURL() {
		String url = ClipboardUtilities.getURL();

		if (url == null)
			return false;

		BufferedImage img = HTTPUtilities.getImage(url);

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

	public BufferedImage getResizedImage() {
		if (!isCoverSet())
			return null;

		return ImageUtilities.resizeCoverImage(getFullImage());
	}

	@Override
	public String getFullTitle() {
		return owner.getFullTitle();
	}

	@Override
	public void setCover(BufferedImage nci) {
		if (nci != null) {
			this.fullImage = nci;

			lblCover.setIcon(new ImageIcon(ImageUtilities.resizeCoverImage(fullImage)));
		} else {
			this.fullImage = null;

			lblCover.setIcon(null);
		}

		btnCrop.setEnabled(isCoverSet() && isCoverSet());
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
