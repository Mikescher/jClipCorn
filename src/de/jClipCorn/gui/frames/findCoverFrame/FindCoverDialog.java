package de.jClipCorn.gui.frames.findCoverFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.HTTPUtilities;
import de.jClipCorn.util.ImageUtilities;
import de.jClipCorn.util.parser.ParseResultHandler;
import de.jClipCorn.util.parser.imageparser.GoogleImageParser;
import de.jClipCorn.util.parser.imageparser.ImDBImageParser;

public class FindCoverDialog extends JDialog {
	private static final long serialVersionUID = -2582872730470224144L;

	private final ParseResultHandler owner;
	private final CCMovieTyp typ;

	private BufferedImage currentSelectedImage = null;

	private Thread thread_1;
	private Thread thread_2;
	private Thread thread_3;

	private boolean thread_1_finished = true;
	private boolean thread_2_finished = true;
	private boolean thread_3_finished = true;

	private ArrayList<BufferedImage> thread_1_images;
	private ArrayList<BufferedImage> thread_2_images;
	private ArrayList<BufferedImage> thread_3_images;

	private JPanel contentPane;
	private JLabel imgMain;
	private JPanel pnlLeft;
	private JButton btnOK;
	private JPanel pnlTop;
	private JTextField edSearchValue;
	private JButton btnParse;
	private JProgressBar progBar1;
	private JProgressBar progBar2;
	private JProgressBar progBar3;
	private JScrollPane scrlbx1;
	private JPanel pnlList1;
	private JScrollPane scrlbx2;
	private JScrollPane scrlbx3;
	private JPanel pnlList2;
	private JPanel pnlList3;
	private JButton btnStop;
	private JPanel pnlCenter;
	private JPanel pnlProgressInner;
	private Component verticalStrut;
	private Component verticalStrut_1;
	private Component verticalStrut_2;
	private Component verticalStrut_3;
	private JPanel pnlBtnOKOuter;
	private JPanel pnlButtonOkInner;
	private JPanel pnlButtons;
	private JPanel pnEdSearchValue;
	private JPanel pnlProgressOuter;
	private Box verticalBox;

	/**
	 * Create the frame.
	 */
	public FindCoverDialog(Component parent, ParseResultHandler owner, CCMovieTyp typ) {
		this.owner = owner;
		this.typ = typ;
		initGUI(parent);
	}

	private void initGUI(Component parent) {
		setTitle(LocaleBundle.getString("FindCoverDialog.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setModal(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 875, 634);
		setLocationRelativeTo(parent);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(10, 10));

		pnlLeft = new JPanel();
		pnlLeft.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(pnlLeft, BorderLayout.EAST);
		pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));

		verticalStrut_3 = Box.createVerticalStrut(5);
		pnlLeft.add(verticalStrut_3);

		imgMain = new JLabel();
		pnlLeft.add(imgMain);
		imgMain.setIcon(CachedResourceLoader.getImageIcon(Resources.IMG_COVER_STANDARD));

		verticalBox = Box.createVerticalBox();
		pnlLeft.add(verticalBox);

		pnlProgressOuter = new JPanel();
		pnlLeft.add(pnlProgressOuter);
		pnlProgressOuter.setLayout(new BoxLayout(pnlProgressOuter, BoxLayout.X_AXIS));

		pnlProgressInner = new JPanel();
		pnlProgressOuter.add(pnlProgressInner);
		pnlProgressInner.setLayout(new BoxLayout(pnlProgressInner, BoxLayout.Y_AXIS));

		verticalStrut_2 = Box.createVerticalStrut(5);
		pnlProgressInner.add(verticalStrut_2);

		progBar1 = new JProgressBar();
		pnlProgressInner.add(progBar1);

		verticalStrut = Box.createVerticalStrut(5);
		pnlProgressInner.add(verticalStrut);

		progBar2 = new JProgressBar();
		pnlProgressInner.add(progBar2);

		verticalStrut_1 = Box.createVerticalStrut(5);
		pnlProgressInner.add(verticalStrut_1);

		progBar3 = new JProgressBar();
		pnlProgressInner.add(progBar3);

		pnlBtnOKOuter = new JPanel();
		pnlLeft.add(pnlBtnOKOuter);
		pnlBtnOKOuter.setLayout(new BorderLayout(10, 0));

		pnlButtonOkInner = new JPanel();
		pnlBtnOKOuter.add(pnlButtonOkInner, BorderLayout.SOUTH);
		pnlButtonOkInner.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		btnOK = new JButton(LocaleBundle.getString("FindCoverDialog.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onBtnOKlicked();
			}
		});
		pnlButtonOkInner.add(btnOK);
		btnOK.setAlignmentX(Component.CENTER_ALIGNMENT);

		pnlTop = new JPanel();
		pnlTop.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BorderLayout(0, 0));

		pnEdSearchValue = new JPanel();
		pnlTop.add(pnEdSearchValue, BorderLayout.WEST);
		pnEdSearchValue.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		edSearchValue = new JTextField(owner.getFullTitle());
		edSearchValue.setColumns(50);
		pnEdSearchValue.add(edSearchValue);
		edSearchValue.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					startParsing();
				}
			}
		});

		pnlButtons = new JPanel();
		FlowLayout fl_pnlButtons = (FlowLayout) pnlButtons.getLayout();
		fl_pnlButtons.setAlignment(FlowLayout.LEFT);
		pnlTop.add(pnlButtons, BorderLayout.CENTER);

		btnParse = new JButton(LocaleBundle.getString("FindCoverDialog.btnParse.text")); //$NON-NLS-1$
		pnlButtons.add(btnParse);

		btnStop = new JButton(LocaleBundle.getString("FindCoverDialog.btnStop.text")); //$NON-NLS-1$
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopParsing();
			}
		});
		btnStop.setEnabled(false);
		pnlButtons.add(btnStop);
		btnParse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startParsing();
			}
		});

		pnlCenter = new JPanel();
		contentPane.add(pnlCenter, BorderLayout.CENTER);
		pnlCenter.setLayout(new GridLayout(3, 0, 0, 10));

		scrlbx1 = new JScrollPane();
		pnlCenter.add(scrlbx1);
		scrlbx1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrlbx1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		pnlList1 = new JPanel();
		scrlbx1.setViewportView(pnlList1);
		pnlList1.setLayout(new BoxLayout(pnlList1, BoxLayout.X_AXIS));

		scrlbx2 = new JScrollPane();
		pnlCenter.add(scrlbx2);
		scrlbx2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrlbx2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		pnlList2 = new JPanel();
		scrlbx2.setViewportView(pnlList2);
		pnlList2.setLayout(new BoxLayout(pnlList2, BoxLayout.X_AXIS));

		scrlbx3 = new JScrollPane();
		pnlCenter.add(scrlbx3);
		scrlbx3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrlbx3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		pnlList3 = new JPanel();
		scrlbx3.setViewportView(pnlList3);
		pnlList3.setLayout(new BoxLayout(pnlList3, BoxLayout.X_AXIS));
	}

	private void onBtnOKlicked() {
		if (currentSelectedImage != null && threadsAreFinished()) {
			owner.setCover(currentSelectedImage);
			dispose();
		}
	}

	private boolean threadsAreFinished() {
		return thread_1_finished && thread_2_finished && thread_3_finished;
	}

	private void startParsing() {
		onStartThreads();

		thread_1 = (new Thread(new Runnable() {
			@Override
			public void run() {
				parseGoogleImages();
			}
		}));

		thread_2 = (new Thread(new Runnable() {
			@Override
			public void run() {
				parseImDBImages();
			}
		}));

		thread_3 = (new Thread(new Runnable() {
			@Override
			public void run() {
				parseCoverSearch();
			}
		}));

		thread_1.start();
		thread_2.start();
		thread_3.start();
	}

	@SuppressWarnings("deprecation")
	private void stopParsing() {
		if (thread_1 != null) {
			thread_1.suspend();
		}
		if (thread_2 != null) {
			thread_2.suspend();
		}
		if (thread_3 != null) {
			thread_3.suspend();
		}

		onEndThreads();
	}

	private void onStartThreads() {
		clearScrollPanes();

		btnParse.setEnabled(false);
		btnStop.setEnabled(true);

		imgMain.setIcon(CachedResourceLoader.getImageIcon(Resources.IMG_COVER_STANDARD));
		currentSelectedImage = null;

		thread_1_finished = false;
		thread_2_finished = false;
		thread_3_finished = false;
	}

	private void onEndThreads() {
		thread_1_finished = true;
		thread_2_finished = true;
		thread_3_finished = true;

		resetProgressbars();
		btnParse.setEnabled(true);
		btnStop.setEnabled(false);

		pnlList1.setPreferredSize(new Dimension(thread_1_images.size() * 101 + 10, 0));
		pnlList1.validate();
		scrlbx1.revalidate();

		pnlList2.setPreferredSize(new Dimension(thread_2_images.size() * 101 + 10, 0));
		pnlList2.validate();
		scrlbx2.revalidate();

		pnlList3.setPreferredSize(new Dimension(thread_3_images.size() * 101 + 10, 0));
		pnlList3.validate();
		scrlbx3.revalidate();
	}

	private void clearScrollPanes() {
		pnlList1.removeAll();
		pnlList2.removeAll();
		pnlList3.removeAll();
	}

	private void resetProgressbars() {
		progBar1.setValue(0);
		progBar2.setValue(0);
		progBar3.setValue(0);
	}

	private void parseGoogleImages() {
		thread_1_images = new ArrayList<>();

		// #################################################################################

		String url = GoogleImageParser.getSearchURL(edSearchValue.getText());
		String json = HTTPUtilities.getHTML(url, false);
		ArrayList<String> links = GoogleImageParser.extractImageLinks(json);

		setProgressbarMaxThreadsafe(1, links.size());

		int pos = 0;
		for (String s : links) {
			setProgressbarPosThreadsafe(1, pos++);

			BufferedImage biu = HTTPUtilities.getImage(s);
			if (biu != null) {
				thread_1_images.add(ImageUtilities.resizeCoverImage(biu));

				addCoverLabelThreadsafe(1, thread_1_images.size() - 1, ImageUtilities.resizeHalfCoverImage(biu));
			}
		}
		setProgressbarPosThreadsafe(1, pos);

		// #################################################################################

		thread_1_finished = true;
		if (threadsAreFinished()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					onEndThreads();
				}
			});
		}
	}

	private void parseImDBImages() {
		thread_2_images = new ArrayList<>();

		// #################################################################################

		setProgressbarMaxThreadsafe(2, 24);
		int pbpos = 1;

		String searchurl = ImDBImageParser.getSearchURL(edSearchValue.getText(), typ);
		String searchhtml = HTTPUtilities.getHTML(searchurl, true);
		String direkturl = ImDBImageParser.getFirstSearchResult(searchhtml);
		if (!direkturl.isEmpty()) {
			String direkthtml = HTTPUtilities.getHTML(direkturl, true);

			setProgressbarPosThreadsafe(2, pbpos++);

			BufferedImage imgMain = ImDBImageParser.getMainpageImage(direkthtml);
			if (imgMain != null) {
				thread_2_images.add(ImageUtilities.resizeCoverImage(imgMain));

				addCoverLabelThreadsafe(2, thread_2_images.size() - 1, ImageUtilities.resizeHalfCoverImage(imgMain));
			}

			setProgressbarPosThreadsafe(2, pbpos++);

			String posterurl = ImDBImageParser.getCoverUrlPoster(direkturl);
			String posterhtml = HTTPUtilities.getHTML(posterurl, true);

			ArrayList<String> posterlinks = ImDBImageParser.extractImageLinks(posterhtml);

			if (posterlinks.size() > 0) {
				int currCID = 0;
				for (String url : posterlinks) {
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(url, true);

					BufferedImage imgurl = ImDBImageParser.getDirectImage(urlhtml);
					if (imgMain != null) {
						thread_2_images.add(ImageUtilities.resizeCoverImage(imgurl));

						addCoverLabelThreadsafe(2, thread_2_images.size() - 1, ImageUtilities.resizeHalfCoverImage(imgurl));
					}
					setProgressbarPosThreadsafe(2, pbpos++);
				}
			} else {
				String allurl = ImDBImageParser.getCoverUrlAll(direkturl);
				String allhtml = HTTPUtilities.getHTML(allurl, true);

				ArrayList<String> alllinks = ImDBImageParser.extractImageLinks(allhtml);

				int currCID = 0;
				for (String url : alllinks) {
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(url, true);

					BufferedImage imgurl = ImDBImageParser.getDirectImage(urlhtml);
					if (imgMain != null) {
						thread_2_images.add(ImageUtilities.resizeCoverImage(imgurl));

						addCoverLabelThreadsafe(2, thread_2_images.size() - 1, ImageUtilities.resizeHalfCoverImage(imgurl));
					}
					setProgressbarPosThreadsafe(2, pbpos++);
				}
			}
		}
		
		setProgressbarPosThreadsafe(2, 24);

		// #################################################################################

		thread_2_finished = true;
		if (threadsAreFinished()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					onEndThreads();
				}
			});
		}
	}

	private void parseCoverSearch() {
		thread_3_images = new ArrayList<>();

		// #################################################################################

		setProgressbarMaxThreadsafe(3, 24);
		int pbpos = 1;

		String searchurl = ImDBImageParser.getSearchURL(edSearchValue.getText(), typ);
		String searchhtml = HTTPUtilities.getHTML(searchurl, true);
		String direkturl = ImDBImageParser.getSecondSearchResult(searchhtml);
		if (!direkturl.isEmpty()) {
			String direkthtml = HTTPUtilities.getHTML(direkturl, true);

			setProgressbarPosThreadsafe(3, pbpos++);

			BufferedImage imgMain = ImDBImageParser.getMainpageImage(direkthtml);
			if (imgMain != null) {
				thread_3_images.add(ImageUtilities.resizeCoverImage(imgMain));

				addCoverLabelThreadsafe(3, thread_3_images.size() - 1, ImageUtilities.resizeHalfCoverImage(imgMain));
			}

			setProgressbarPosThreadsafe(3, pbpos++);

			String posterurl = ImDBImageParser.getCoverUrlPoster(direkturl);
			String posterhtml = HTTPUtilities.getHTML(posterurl, true);

			ArrayList<String> posterlinks = ImDBImageParser.extractImageLinks(posterhtml);

			if (posterlinks.size() > 0) {
				int currCID = 0;
				for (String url : posterlinks) {
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(url, true);

					BufferedImage imgurl = ImDBImageParser.getDirectImage(urlhtml);
					if (imgMain != null) {
						thread_3_images.add(ImageUtilities.resizeCoverImage(imgurl));

						addCoverLabelThreadsafe(3, thread_3_images.size() - 1, ImageUtilities.resizeHalfCoverImage(imgurl));
					}
					setProgressbarPosThreadsafe(3, pbpos++);
				}
			} else {
				String allurl = ImDBImageParser.getCoverUrlAll(direkturl);
				String allhtml = HTTPUtilities.getHTML(allurl, true);

				ArrayList<String> alllinks = ImDBImageParser.extractImageLinks(allhtml);

				int currCID = 0;
				for (String url : alllinks) {
					if (currCID++ >= 23)
						continue;

					String urlhtml = HTTPUtilities.getHTML(url, true);

					BufferedImage imgurl = ImDBImageParser.getDirectImage(urlhtml);
					if (imgMain != null) {
						thread_3_images.add(ImageUtilities.resizeCoverImage(imgurl));

						addCoverLabelThreadsafe(3, thread_3_images.size() - 1, ImageUtilities.resizeHalfCoverImage(imgurl));
					}
					setProgressbarPosThreadsafe(3, pbpos++);
				}
			}
		}
		
		setProgressbarPosThreadsafe(4, 24);

		// #################################################################################

		thread_3_finished = true;
		if (threadsAreFinished()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					onEndThreads();
				}
			});
		}
	}

	private void setProgressbarMaxThreadsafe(final int pbar, final int max) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				switch (pbar) {
				case 1:
					progBar1.setMaximum(max);
					break;
				case 2:
					progBar2.setMaximum(max);
					break;
				case 3:
					progBar3.setMaximum(max);
					break;
				}
			}
		});
	}

	private void setProgressbarPosThreadsafe(final int pbar, final int pos) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				switch (pbar) {
				case 1:
					progBar1.setValue(pos);
					break;
				case 2:
					progBar2.setValue(pos);
					break;
				case 3:
					progBar3.setValue(pos);
					break;
				}
			}
		});
	}

	private void addCoverLabelThreadsafe(final int plist, final int id, final BufferedImage bi) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JLabel newlbl;

				switch (plist) {
				case 1:
					pnlList1.add(Box.createHorizontalStrut(10));

					newlbl = new JLabel();
					newlbl.setIcon(new ImageIcon(bi));
					pnlList1.add(newlbl);

					pnlList1.setPreferredSize(new Dimension(thread_1_images.size() * 101 + 10, 0));
					pnlList1.validate();
					scrlbx1.revalidate();

					break;
				case 2:
					pnlList2.add(Box.createHorizontalStrut(10));

					newlbl = new JLabel();
					newlbl.setIcon(new ImageIcon(bi));
					pnlList2.add(newlbl);

					pnlList2.setPreferredSize(new Dimension(thread_2_images.size() * 101 + 10, 0));
					pnlList2.validate();
					scrlbx2.revalidate();

					break;
				case 3:
					pnlList3.add(Box.createHorizontalStrut(10));

					newlbl = new JLabel();
					newlbl.setIcon(new ImageIcon(bi));
					pnlList3.add(newlbl);

					pnlList3.setPreferredSize(new Dimension(thread_3_images.size() * 101 + 10, 0));
					pnlList3.validate();
					scrlbx3.revalidate();

					break;
				default:
					return;
				}

				newlbl.addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(MouseEvent arg0) {
						//
					}

					@Override
					public void mousePressed(MouseEvent arg0) {
						//
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
						//
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
						//
					}

					@Override
					public void mouseClicked(MouseEvent arg0) {
						onCoverClicked(plist, id);
					}
				});
			}
		});
	}

	private void onCoverClicked(int row, int column) {
		if (!threadsAreFinished())
			return;

		switch (row) {
		case 1:
			currentSelectedImage = thread_1_images.get(column);
			break;
		case 2:
			currentSelectedImage = thread_2_images.get(column);
			break;
		case 3:
			currentSelectedImage = thread_3_images.get(column);
			break;
		default:
			return;
		}
		imgMain.setIcon(new ImageIcon(currentSelectedImage));
	}
}
