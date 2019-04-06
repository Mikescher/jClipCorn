package de.jClipCorn.gui.frames.coverCropFrame;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.gui.guiComponents.CoverLabel;
import de.jClipCorn.gui.guiComponents.SimpleDoubleBufferStrategy;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.listener.ImageCropperResultListener;

public class CoverCropDialog extends JDialog {
	private static final long serialVersionUID = -8515018980639001617L;

	private final static int MAX_DRAG_PIXELDISTANCE = 5;
	private final static int MIN_WIDTH = 50;
	private final static int MIN_HEIGHT = 50;
	
	private final static float CROP_TRANSPARENCY = 0.85f;
	
	private final static int CROPSTATE_NOTHING 		= 0b00000;
	
	private final static int CROPSTATE_TOP 			= 0b01000;
	private final static int CROPSTATE_RIGHT 		= 0b00100;
	private final static int CROPSTATE_BOTTOM 		= 0b00010;
	private final static int CROPSTATE_LEFT 		= 0b00001;
	
	private final static int CROPSTATE_TOPLEFT 		= CROPSTATE_TOP 	| CROPSTATE_LEFT;
	private final static int CROPSTATE_TOPRIGHT 	= CROPSTATE_TOP 	| CROPSTATE_RIGHT;
	private final static int CROPSTATE_BOTTOMLEFT 	= CROPSTATE_BOTTOM 	| CROPSTATE_LEFT;
	private final static int CROPSTATE_BOTTOMRIGHT 	= CROPSTATE_BOTTOM 	| CROPSTATE_RIGHT;
	
	private final static int CROPSTATE_DRAG 		= 0b10000;
	
	private ImageCropperResultListener listener;

	private BufferedImage img;
	private int imgheight, imgwidth;
	
	private SimpleDoubleBufferStrategy stratImage;
	private SimpleDoubleBufferStrategy stratZoom;
	
	private double displayScale = 1;
	
	private Point crop_tl = new Point();
	private Point crop_br = new Point();
	
	private Point currZoomPos = new Point();
	
	private Point dragStartPoint = new Point();
	private Point dragStartCrop_tl = new Point();
	private Point dragStartCrop_br = new Point();
	
	private int dragMode = CROPSTATE_NOTHING;
	
	private TexturePaint transparencyPattern;
	
	private JPanel contentPane;
	
	private JPanel pnlBottom;
	private JPanel pnlMain;
	private JButton btnOk;
	private JPanel pnlImage;
	private JPanel pnlSidebar;
	private JPanel pnlSidebarOptionInner;
	private JPanel pnlInnerPreview;
	private CoverLabel lblCoverPreviewBig;
	private CoverLabel lblCoverPreviewSmall;
	private Component strut_2;
	private JPanel pnlPreview;
	private JLabel lblPreview;
	private Component strut_1;
	private Component strut_3;
	private JCheckBox chckbxLockRatio;
	private JButton btnAutocalcCrop;
	private JCheckBox chckbxShowCropOutline;
	private JCheckBox chckbxShowImageBorders;
	private JPanel pnlSidebarOptions;
	private JPanel pnlZoom;
	private JLabel lblZoom;
	private JLabel lblImage;
	private JButton btnResetCrop;
	private JCheckBox chckbxShowTransparency;
	private JSpinner spnZoom;
	private Component horizontalStrut;
	private JLabel lblSize;
	private JLabel lblPosition;
	private JLabel lblRatio;
	private JLabel lblMouse;
	private JPanel pnlFunctions;
	private JPanel pnlImageInner;
	private JButton btnRotateCcw;
	private JButton btnRotateCw;
	private JButton btnFlipX;
	private JButton btnFlipY;
	private JLabel lblRatioState;
	private JCheckBox chckbxSeriesPreview;
	private JButton btnCancel;
	private JPanel panel;
	private JPanel panel_1;
	private JButton btnResizeTop;
	private JButton btnResizeLeft;
	private JButton btnResizeRight;
	private JButton btnResizeBottom;
	
	/**
	 * Constructor (isSeries = false)
	 * 
	 * @wbp.parser.constructor
	 */
	public CoverCropDialog(Component owner, BufferedImage i, ImageCropperResultListener rl) {
		super();
		
		this.img = ImageUtilities.deepCopyImage(i);
		imageChanged();
		autoTransparencyCalcCrops();
		
		this.listener = rl;
		
		initGUI();
		setLocationRelativeTo(owner);
	}
	
	public CoverCropDialog(Component owner, BufferedImage i, ImageCropperResultListener rl, boolean isSeries) {
		super();
		
		this.img = ImageUtilities.deepCopyImage(i);
		imageChanged();
		autoTransparencyCalcCrops();
		
		this.listener = rl;
		
		initGUI();
		setLocationRelativeTo(owner);
		
		chckbxSeriesPreview.setSelected(isSeries);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("CoverCropFrame.this.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Resources.IMG_FRAME_ICON.get());
		setMinimumSize(new Dimension(800, 600));
		setSize(new Dimension(1000, 625));
		setModal(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				// nothing
			}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				recalcSizes();
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
				// nothing
			}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {
				// nothing
			}
		});
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {
				firstInit();
				repaintAll();
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
				// nothing
			}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// nothing
			}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// nothing
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				// nothing
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
				// nothing
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {
				// nothing
			}
		});
		
		pnlBottom = new JPanel();
		contentPane.add(pnlBottom, BorderLayout.SOUTH);
		
		btnOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (listener != null) listener.editingFinished(getNewCroppedImage());
				dispose();
			}
		});
		pnlBottom.add(btnOk);
		
		btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (listener != null) listener.editingCanceled();
				dispose();
			}
		});
		pnlBottom.add(btnCancel);
		
		pnlMain = new JPanel();
		contentPane.add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(new BorderLayout(0, 0));
		
		pnlImage = new JPanel();
		pnlMain.add(pnlImage);
		pnlImage.setLayout(new BorderLayout(0, 0));
		
		pnlFunctions = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pnlFunctions.getLayout();
		flowLayout.setVgap(2);
		flowLayout.setAlignOnBaseline(true);
		flowLayout.setAlignment(FlowLayout.LEADING);
		pnlImage.add(pnlFunctions, BorderLayout.NORTH);
		
		btnResetCrop = new JButton(LocaleBundle.getString("CoverCropFrame.btnReset.text")); //$NON-NLS-1$
		pnlFunctions.add(btnResetCrop);
		
		btnAutocalcCrop = new JButton(LocaleBundle.getString("CoverCropFrame.btnAutoCalc.text")); //$NON-NLS-1$
		pnlFunctions.add(btnAutocalcCrop);
		
		btnRotateCw = new JButton(LocaleBundle.getString("CoverCropFrame.btnRotateCW.text")); //$NON-NLS-1$
		btnRotateCw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				rotate90(1);
			}
		});
		pnlFunctions.add(btnRotateCw);
		
		btnRotateCcw = new JButton(LocaleBundle.getString("CoverCropFrame.btnRotateCCW.text")); //$NON-NLS-1$
		btnRotateCcw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rotate90(-1);
			}
		});
		pnlFunctions.add(btnRotateCcw);
		
		btnFlipX = new JButton(LocaleBundle.getString("CoverCropFrame.btnFlipX.text")); //$NON-NLS-1$
		btnFlipX.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				flip(true, false);
			}
		});
		pnlFunctions.add(btnFlipX);
		
		btnFlipY = new JButton(LocaleBundle.getString("CoverCropFrame.btnFlipY.text")); //$NON-NLS-1$
		btnFlipY.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				flip(false, true);
			}
		});
		pnlFunctions.add(btnFlipY);
		
		pnlImageInner = new JPanel();
		pnlImageInner.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlImage.add(pnlImageInner, BorderLayout.CENTER);
		pnlImageInner.setLayout(new BorderLayout(0, 0));
		
		lblImage = new JLabel() {
			private static final long serialVersionUID = 1093249790L;

			@Override
			protected void paintComponent(Graphics g) {
				repaintMainImage(g);
			}
		};
		pnlImageInner.add(lblImage);
		lblImage.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				endDrag(e);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				startDrag(getDragModeForMousePos(e), e);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				endDrag(e);
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// nothing
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// nothing
			}
		});
		lblImage.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				repaintZoomImage(lblZoom.getGraphics(), e);
				updateCursor(e);
				updateMouseInfoLabel(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				repaintZoomImage(lblZoom.getGraphics(), e);
				updateCursor(e);
				updateDrag(e);
				updateMouseInfoLabel(e);
			}
		});
		btnAutocalcCrop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				autoCalcCrops();
				repaintAll();
			}
		});
		btnResetCrop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetCrops();
			}
		});
		
		pnlSidebar = new JPanel();
		pnlMain.add(pnlSidebar, BorderLayout.EAST);
		pnlSidebar.setLayout(new BorderLayout(0, 0));
		
		pnlPreview = new JPanel();
		pnlPreview.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlSidebar.add(pnlPreview, BorderLayout.SOUTH);
		pnlPreview.setLayout(new BorderLayout(0, 0));
		
		pnlInnerPreview = new JPanel();
		pnlPreview.add(pnlInnerPreview, BorderLayout.CENTER);
		pnlInnerPreview.setLayout(new BoxLayout(pnlInnerPreview, BoxLayout.X_AXIS));
		
		strut_1 = Box.createHorizontalStrut(5);
		pnlInnerPreview.add(strut_1);
		
		lblCoverPreviewBig = new CoverLabel(false) {
			private static final long serialVersionUID = 1093249790L;

			@Override
			protected void paintComponent(Graphics g) {
				repaintPreviewImagesBig(g);
			}
		};
		pnlInnerPreview.add(lblCoverPreviewBig);
		
		strut_2 = Box.createHorizontalStrut(5);
		pnlInnerPreview.add(strut_2);
		
		lblCoverPreviewSmall = new CoverLabel(true) {
			private static final long serialVersionUID = 1093249790L;

			@Override
			protected void paintComponent(Graphics g) {
				repaintPreviewImagesSmall(g);
			}
		};
		lblCoverPreviewSmall.setAlignmentY(Component.TOP_ALIGNMENT);
		pnlInnerPreview.add(lblCoverPreviewSmall);
		
		strut_3 = Box.createHorizontalStrut(5);
		pnlInnerPreview.add(strut_3);
		
		panel = new JPanel();
		pnlInnerPreview.add(panel);
		panel.setOpaque(false);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("default:grow"), //$NON-NLS-1$
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		panel_1 = new JPanel();
		panel_1.setOpaque(false);
		panel.add(panel_1, "2, 2, left, top"); //$NON-NLS-1$
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(18dlu;pref):grow"), //$NON-NLS-1$
				ColumnSpec.decode("max(18dlu;pref):grow"), //$NON-NLS-1$
				ColumnSpec.decode("max(18dlu;pref):grow"),}, //$NON-NLS-1$
			new RowSpec[] {
				RowSpec.decode("max(18dlu;pref):grow"), //$NON-NLS-1$
				RowSpec.decode("max(18dlu;pref):grow"), //$NON-NLS-1$
				RowSpec.decode("max(18dlu;pref):grow"),})); //$NON-NLS-1$
		
		btnResizeTop = new JButton("▲"); //$NON-NLS-1$
		btnResizeTop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int widthCurr = crop_br.x - crop_tl.x;
				int heightCurr = crop_br.y - crop_tl.y;
				
				int neededH = (int) Math.round(widthCurr / ImageUtilities.COVER_RATIO);

				setBottomCropChecked(crop_br.y - (heightCurr - neededH));
				
				updateCropInfoLabel();
				repaintAll();
			}
		});
		btnResizeTop.setMargin(new Insets(0, 0, 0, 0));
		panel_1.add(btnResizeTop, "2, 1, fill, fill"); //$NON-NLS-1$
		
		btnResizeLeft = new JButton("◀"); //$NON-NLS-1$
		btnResizeLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int widthCurr = crop_br.x - crop_tl.x;
				int heightCurr = crop_br.y - crop_tl.y;
				
				int neededW = (int) Math.round(heightCurr * ImageUtilities.COVER_RATIO);
				
				setRightCropChecked(crop_br.x - (widthCurr - neededW));
				
				updateCropInfoLabel();
				repaintAll();
			}
		});
		btnResizeLeft.setMargin(new Insets(0, 0, 0, 0));
		panel_1.add(btnResizeLeft, "1, 2, fill, fill"); //$NON-NLS-1$
		
		btnResizeRight = new JButton("▶"); //$NON-NLS-1$
		btnResizeRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int widthCurr = crop_br.x - crop_tl.x;
				int heightCurr = crop_br.y - crop_tl.y;
				
				int neededW = (int) Math.round(heightCurr * ImageUtilities.COVER_RATIO);
				
				setLeftCropChecked(crop_tl.x + (widthCurr - neededW));
				
				updateCropInfoLabel();
				repaintAll();
			}
		});
		btnResizeRight.setMargin(new Insets(0, 0, 0, 0));
		panel_1.add(btnResizeRight, "3, 2, fill, fill"); //$NON-NLS-1$
		
		btnResizeBottom = new JButton("▼"); //$NON-NLS-1$
		btnResizeBottom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int widthCurr = crop_br.x - crop_tl.x;
				int heightCurr = crop_br.y - crop_tl.y;
				
				int neededH = (int) Math.round(widthCurr / ImageUtilities.COVER_RATIO);
				
				setTopCropChecked(crop_tl.y + (heightCurr - neededH));
				
				updateCropInfoLabel();
				repaintAll();
			}
		});
		btnResizeBottom.setMargin(new Insets(0, 0, 0, 0));
		panel_1.add(btnResizeBottom, "2, 3, fill, fill"); //$NON-NLS-1$
		
		lblPreview = new JLabel(LocaleBundle.getString("CoverCropFrame.btnPreview.text")); //$NON-NLS-1$
		lblPreview.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
		pnlPreview.add(lblPreview, BorderLayout.NORTH);
		
		pnlSidebarOptions = new JPanel();
		pnlSidebar.add(pnlSidebarOptions, BorderLayout.CENTER);
		pnlSidebarOptions.setLayout(new BorderLayout(2, 0));
		
		pnlSidebarOptionInner = new JPanel();
		pnlSidebarOptionInner.setBorder(new LineBorder(new Color(0, 0, 0)));
		pnlSidebarOptions.add(pnlSidebarOptionInner, BorderLayout.WEST);
		
		chckbxLockRatio = new JCheckBox(LocaleBundle.getString("CoverCropFrame.btnLockRatio.text")); //$NON-NLS-1$
		chckbxLockRatio.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				repaintAll();
			}
		});
		pnlSidebarOptionInner.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("center:80dlu:grow"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("right:max(30dlu;default)"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("23px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("23px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("23px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("23px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("155px"),})); //$NON-NLS-1$
		pnlSidebarOptionInner.add(chckbxLockRatio, "2, 2, 3, 1, left, center"); //$NON-NLS-1$
		
		chckbxShowCropOutline = new JCheckBox(LocaleBundle.getString("CoverCropFrame.btnShowCropOutline.text")); //$NON-NLS-1$
		chckbxShowCropOutline.setSelected(true);
		chckbxShowCropOutline.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				repaintAll();
			}
		});
		pnlSidebarOptionInner.add(chckbxShowCropOutline, "2, 4, 3, 1, left, center"); //$NON-NLS-1$
		
		chckbxShowImageBorders = new JCheckBox(LocaleBundle.getString("CoverCropFrame.btnShowImageBorders.text")); //$NON-NLS-1$
		chckbxShowImageBorders.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				repaintAll();
			}
		});
		pnlSidebarOptionInner.add(chckbxShowImageBorders, "2, 6, 3, 1, left, center"); //$NON-NLS-1$
		
		chckbxShowTransparency = new JCheckBox(LocaleBundle.getString("CoverCropFrame.btnShowTransparency.text")); //$NON-NLS-1$
		chckbxShowTransparency.setSelected(true);
		chckbxShowTransparency.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				repaintAll();
			}
		});
		pnlSidebarOptionInner.add(chckbxShowTransparency, "2, 8, 3, 1, left, center"); //$NON-NLS-1$
		
		chckbxSeriesPreview = new JCheckBox(LocaleBundle.getString("CoverCropFrame.btnShowSeriesPreview.text")); //$NON-NLS-1$
		chckbxSeriesPreview.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				repaintAll();
			}
		});
		pnlSidebarOptionInner.add(chckbxSeriesPreview, "2, 10, left, default"); //$NON-NLS-1$
		
		lblPosition = new JLabel(LocaleBundle.getDeformattedString("CoverCropFrame.lblPosition.text")); //$NON-NLS-1$
		pnlSidebarOptionInner.add(lblPosition, "2, 12, 3, 1"); //$NON-NLS-1$
		
		lblSize = new JLabel(LocaleBundle.getDeformattedString("CoverCropFrame.lblSize.text")); //$NON-NLS-1$
		pnlSidebarOptionInner.add(lblSize, "2, 14, 3, 1"); //$NON-NLS-1$
		
		lblRatio = new JLabel(LocaleBundle.getDeformattedString("CoverCropFrame.lblRatio.text")); //$NON-NLS-1$
		pnlSidebarOptionInner.add(lblRatio, "2, 16, left, default"); //$NON-NLS-1$
		
		lblRatioState = new JLabel(LocaleBundle.getString("CoverCropFrame.lblOK.textOK")); //$NON-NLS-1$
		lblRatioState.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		pnlSidebarOptionInner.add(lblRatioState, "4, 16"); //$NON-NLS-1$
		
		lblMouse = new JLabel(LocaleBundle.getDeformattedString("CoverCropFrame.lblMouse.text")); //$NON-NLS-1$
		pnlSidebarOptionInner.add(lblMouse, "2, 18, 3, 1"); //$NON-NLS-1$
		
		pnlZoom = new JPanel();
		pnlSidebarOptions.add(pnlZoom, BorderLayout.CENTER);
		pnlZoom.setLayout(new BorderLayout(0, 0));
		
		spnZoom = new JSpinner();
		spnZoom.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				repaintZoomImage(lblZoom.getGraphics(), null);
			}
		});
		spnZoom.setModel(new SpinnerNumberModel(2, 1, 10, 1));
		pnlZoom.add(spnZoom, BorderLayout.NORTH);
		
		horizontalStrut = Box.createHorizontalStrut(250);
		pnlZoom.add(horizontalStrut, BorderLayout.SOUTH);
		
		lblZoom = new JLabel() {
			private static final long serialVersionUID = 1093249790L;

			@Override
			protected void paintComponent(Graphics g) {
				repaintZoomImage(g, null);
			}
		};
		pnlZoom.add(lblZoom, BorderLayout.CENTER);
	}
	
	private void firstInit() {
		stratImage = new SimpleDoubleBufferStrategy(lblImage);
		stratZoom = new SimpleDoubleBufferStrategy(lblZoom);
	}

	private void imageChanged() {
		imgheight = img.getHeight();
		imgwidth = img.getWidth();
		crop_tl = new Point(0, 0);
		crop_br = new Point(imgwidth, imgheight);
		
		BufferedImage pattern = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics gpat = pattern.getGraphics();
		gpat.setColor(Color.LIGHT_GRAY);
		gpat.fillRect(0, 0, 16, 16);
		gpat.setColor(Color.WHITE);
		gpat.fillRect(0, 0, 8, 8);
		gpat.fillRect(8, 8, 16, 16);
		transparencyPattern = new TexturePaint(pattern, new Rectangle(0, 0, 16, 16));
		
		updateCropInfoLabel();
	}
	
	private void autoCalcCrops() {
		Rectangle r =  ImageUtilities.getNonColorImageRect(img, img.getRGB(0,  0));
		
		crop_tl.x = r.x;
		crop_tl.y = r.y;
		crop_br.x = r.x + r.width;
		crop_br.y = r.y + r.height;
		
		updateCropInfoLabel();
	}
	
	private void autoTransparencyCalcCrops() {
		Rectangle r =  ImageUtilities.getNonTransparentImageRect(img);
		
		crop_tl.x = r.x;
		crop_tl.y = r.y;
		crop_br.x = r.x + r.width;
		crop_br.y = r.y + r.height;
		
		updateCropInfoLabel();
	}
	
	private void updateCropInfoLabel() {
		if (lblPosition == null || lblSize == null || lblRatio == null) return;

		int widthCurr = crop_br.x - crop_tl.x;
		int heightCurr = crop_br.y - crop_tl.y;
		int widthStorage = ImageUtilities.calcImageSizeForStorage(widthCurr, heightCurr).Item1;
		int heightStorage = ImageUtilities.calcImageSizeForStorage(widthCurr, heightCurr).Item2;
		
		lblPosition.setText(LocaleBundle.getFormattedString("CoverCropFrame.lblPosition.text", crop_tl.x, crop_tl.y)); //$NON-NLS-1$
		lblSize.setText(LocaleBundle.getFormattedString("CoverCropFrame.lblSize.text", widthCurr, heightCurr, widthStorage, heightStorage)); //$NON-NLS-1$
		lblRatio.setText(LocaleBundle.getFormattedString("CoverCropFrame.lblRatio.text", (crop_br.x - crop_tl.x * 1d) / (crop_br.y - crop_tl.y), ImageUtilities.COVER_RATIO)); //$NON-NLS-1$
		
		lblRatioState.setText(LocaleBundle.getString(isRatioAcceptable() ? "CoverCropFrame.lblOK.textOK" : "CoverCropFrame.lblOK.textNOK")); //$NON-NLS-1$ //$NON-NLS-2$
		lblRatioState.setForeground(isRatioAcceptable() ? Color.GREEN : Color.RED);
	}
	
	private boolean isRatioAcceptable() {
		return ImageUtilities.isImageRatioAcceptable(crop_br.x - crop_tl.x, crop_br.y - crop_tl.y);
	}

	public void repaintAll() {
		recalcSizes();
		
		repaintMainImage(lblImage.getGraphics());
		repaintZoomImage(lblZoom.getGraphics(), null);
		repaintPreviewImagesBig(lblCoverPreviewBig.getGraphics());
		repaintPreviewImagesSmall(lblCoverPreviewSmall.getGraphics());
		
		updateCropInfoLabel();
	}

	private void recalcSizes() {
		displayScale = getDispScale();
	}
	
	private double getDispScale() {
		int newWidth = lblImage.getWidth();
		int newHeight = lblImage.getHeight();
		
		double ratio = img.getWidth() / (img.getHeight() * 1d);
		double newRatio = newWidth / (newHeight*1d);
		
		double nW;
		double nH;
		
		if (ratio > newRatio) {
			nW = newWidth;
			nH = (int) (1 / ratio * newWidth)*1d;
			
			return nH / img.getHeight();
		} else {
			nH = newHeight;
			nW = (int) (ratio * newHeight)*1d;
			
			return nW / img.getWidth();
		}
	}
	
	private void repaintPreviewImagesBig(Graphics g) {
		if (g == null) return;
		
		if (chckbxShowTransparency.isSelected())
			((Graphics2D)g).setPaint(transparencyPattern);
		else
			g.setColor(Color.WHITE);
		g.fillRect(0, 0, ImageUtilities.BASE_COVER_WIDTH, ImageUtilities.BASE_COVER_HEIGHT);
		
		if (chckbxSeriesPreview.isSelected()) {
			BufferedImage bid = ImageUtilities.resizeCoverImageForFullSizeUI(getCroppedImage());
			ImageUtilities.makeFullSizeSeriesCover(bid);
			g.drawImage(bid, 0, 0, null);
		} else {
			g.drawImage(ImageUtilities.resizeCoverImageForFullSizeUI(getCroppedImage()), 0, 0, null);
		}
		
		if(chckbxShowImageBorders.isSelected()) {
			g.setColor(Color.BLACK);
			g.drawLine(0, 0, 0, ImageUtilities.BASE_COVER_HEIGHT-1);
			g.drawLine(0, ImageUtilities.BASE_COVER_HEIGHT-1, ImageUtilities.BASE_COVER_WIDTH-1, ImageUtilities.BASE_COVER_HEIGHT-1);
			g.drawLine(ImageUtilities.BASE_COVER_WIDTH-1, ImageUtilities.BASE_COVER_HEIGHT-1, ImageUtilities.BASE_COVER_WIDTH-1, 0);
			g.drawLine(ImageUtilities.BASE_COVER_WIDTH-1, 0, 0, 0);
		}
	}

	private BufferedImage getCroppedImage() {
		return img.getSubimage(crop_tl.x, crop_tl.y, crop_br.x - crop_tl.x, crop_br.y - crop_tl.y);
	}

	private BufferedImage getNewCroppedImage() {
	    BufferedImage b = new BufferedImage(crop_br.x - crop_tl.x, crop_br.y - crop_tl.y, img.getType());
	    Graphics g = b.getGraphics();
	    g.drawImage(img, -crop_tl.x, -crop_tl.y, null);
	    g.dispose();
	    return b;
	}
	
	private void repaintPreviewImagesSmall(Graphics g) {
		if (g == null) return;
		
		if (chckbxShowTransparency.isSelected())
			((Graphics2D)g).setPaint(transparencyPattern);
		else
			g.setColor(Color.WHITE);
		g.fillRect(0, 0, ImageUtilities.BASE_COVER_WIDTH, ImageUtilities.BASE_COVER_HEIGHT);
		
		if (chckbxSeriesPreview.isSelected()) {
			BufferedImage bid = ImageUtilities.resizeCoverImageForFullSizeUI(getCroppedImage());
			ImageUtilities.makeFullSizeSeriesCover(bid);
			g.drawImage(ImageUtilities.resizeCoverImageForHalfSizeUI(bid), 0, 0, null);
		} else
			g.drawImage(ImageUtilities.resizeCoverImageForHalfSizeUI(getCroppedImage()), 0, 0, null);
		
		if(chckbxShowImageBorders.isSelected()) {
			g.setColor(Color.BLACK);
			g.drawLine(0, 0, 0, ImageUtilities.HALF_COVER_HEIGHT-1);
			g.drawLine(0, ImageUtilities.HALF_COVER_HEIGHT-1, ImageUtilities.HALF_COVER_WIDTH-1, ImageUtilities.HALF_COVER_HEIGHT-1);
			g.drawLine(ImageUtilities.HALF_COVER_WIDTH-1, ImageUtilities.HALF_COVER_HEIGHT-1, ImageUtilities.HALF_COVER_WIDTH-1, 0);
			g.drawLine(ImageUtilities.HALF_COVER_WIDTH-1, 0, 0, 0);
		}
	}
	
	private void repaintZoomImage(Graphics g_zoom, MouseEvent e) {
		if (stratZoom == null) return;
		if (g_zoom == null) return;
		
		Graphics2D g = (Graphics2D) stratZoom.getCurrent().getGraphics();
		
		int w = lblZoom.getWidth();
		int rh = lblZoom.getHeight();
		int h = Math.min(rh, 250);
		int zoom = (int) ((spnZoom == null) ? 2 : spnZoom.getValue());
		
		Point mp;
		
		if (e == null) {
			mp = new Point(currZoomPos);
		} else {
			mp = e.getLocationOnScreen();
			mp.x -= lblImage.getLocationOnScreen().x;
			mp.y -= lblImage.getLocationOnScreen().y;
			
			currZoomPos = new Point(mp);
		}
		
		g.setColor(Color.BLACK);
		
		if (mp.x < 0 || mp.y < 0 || mp.x > lblImage.getWidth() || mp.y > lblImage.getHeight()) {
			g.fillRect(0, 0, w, rh);
			return;
		}
		
		int posx = (int) (mp.x / displayScale - w/(2*zoom));
		int posy = (int) (mp.y / displayScale - h/(2*zoom));
		
		int capx = (int) (mp.x / displayScale + w/(2*zoom));
		int capy = (int) (mp.y / displayScale + h/(2*zoom));
		
		if (capx > imgwidth) {
			posx -= capx - imgwidth;
			capx = imgwidth;
		}
		
		if (capy > imgheight) {
			posy -= capy - imgheight;
			capy = imgheight;
		}
		
		if (posx < 0) {
			capx -= posx;
			posx = 0;
		}
		
		if (posy < 0) {
			capy -= posy;
			posy = 0;
		}
		
		if (chckbxShowTransparency.isSelected())
			g.setPaint(transparencyPattern);
		else 
			g.setColor(Color.BLACK);
		g.fillRect(0, 0, w, h);

		g.setColor(Color.BLACK);
		g.drawImage(img, 0, 0, w, h, posx, posy, capx, capy, null);
		
		int ch_x = (int) (w * (mp.x - posx * displayScale) / (displayScale * (capx - posx)));
		int ch_y = (int) (h * (mp.y - posy * displayScale) / (displayScale * (capy - posy)));
		
		if (ch_y <= h) {
			g.drawLine(0, ch_y, w, ch_y);
		}	
		
		g.drawLine(ch_x, 0, ch_x, h);
		
		g_zoom.drawImage(stratZoom.getCurrent(), 0, 0, null);
		
		stratZoom.doSwitch();
	}
	
	private void repaintMainImage(Graphics g_img) {
		if (stratImage == null) return;
		if (g_img == null) return;
		
		Graphics2D g = (Graphics2D) stratImage.getCurrent().getGraphics();
		
		if (chckbxShowTransparency.isSelected())
			g.setPaint(transparencyPattern);
		else
			g.setColor(Color.WHITE);
		g.fillRect(0, 0, lblImage.getWidth(), lblImage.getHeight());
		
		int w = (int)(imgwidth * displayScale);
		int h = (int)(imgheight * displayScale);
		
		int t = (int) (crop_tl.y * displayScale);
		int r = (int) (crop_br.x * displayScale);
		int b = (int) (crop_br.y * displayScale);
		int l = (int) (crop_tl.x * displayScale);
		
		g.drawImage(img, 0, 0, w, h, null);
		
		if(chckbxShowImageBorders.isSelected()) {
			g.setColor(Color.BLACK);
			g.drawLine(0, 0, 0, h-1);
			g.drawLine(0, h-1, w-1, h-1);
			g.drawLine(w-1, h-1, w-1, 0);
			g.drawLine(w-1, 0, 0, 0);
		}
		
		// Draw Crop Outline

		if (chckbxShowCropOutline.isSelected()) {
			g.setColor(Color.BLACK);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, CROP_TRANSPARENCY));
			g.fillRect(0, 0, l, h);
			g.fillRect(l, 0, r - l, t);
			g.fillRect(r, 0, w - r, h);
			g.fillRect(l, b, r - l, h - b);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		}

		g.setColor(((dragMode & CROPSTATE_TOP) != 0) ? Color.RED : Color.BLACK);
		g.drawLine(l, t, r, t);
		g.setColor(((dragMode & CROPSTATE_RIGHT) != 0) ? Color.RED : Color.BLACK);
		g.drawLine(r, t, r, b);
		g.setColor(((dragMode & CROPSTATE_BOTTOM) != 0) ? Color.RED : Color.BLACK);
		g.drawLine(l, b, r, b);
		g.setColor(((dragMode & CROPSTATE_LEFT) != 0) ? Color.RED : Color.BLACK);
		g.drawLine(l, t, l, b);
		g.setColor(Color.BLACK);
		
		g_img.drawImage(stratImage.getCurrent(), 0, 0, null);
		
		stratImage.doSwitch();
	}
	
	private void updateCursor(MouseEvent e) {
		int drag = getDragModeForMousePos(e);
		
		if (drag == CROPSTATE_NOTHING) {
			lblImage.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		} else {
			setDragCursor(drag);
		}
	}

	private void setDragCursor(int drag) {
		if (drag == CROPSTATE_TOP) {
			lblImage.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_TOPRIGHT) {
			lblImage.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_RIGHT) {
			lblImage.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_BOTTOMRIGHT) {
			lblImage.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_BOTTOM) {
			lblImage.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_BOTTOMLEFT) {
			lblImage.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_LEFT) {
			lblImage.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_TOPLEFT) {
			lblImage.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
		} else if (drag == CROPSTATE_DRAG) {
			lblImage.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		} else {
			lblImage.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		}
	}

	private int getDragModeForMousePos(MouseEvent e) {
		int drag = CROPSTATE_NOTHING;
		
		double dist_top = Math.abs(e.getY() - crop_tl.y * displayScale);
		double dist_right = Math.abs(e.getX() - crop_br.x * displayScale);
		double dist_bottom = Math.abs(e.getY() - crop_br.y * displayScale);
		double dist_left = Math.abs(e.getX() - crop_tl.x * displayScale);
		
		if (dist_top < MAX_DRAG_PIXELDISTANCE) {
			drag |= CROPSTATE_TOP;
		} else if (dist_bottom < MAX_DRAG_PIXELDISTANCE) {
			drag |= CROPSTATE_BOTTOM;
		}
		
		if (dist_right < MAX_DRAG_PIXELDISTANCE) {
			drag |= CROPSTATE_RIGHT;
		} else if (dist_left < MAX_DRAG_PIXELDISTANCE) {
			drag |= CROPSTATE_LEFT;
		}
		
		if (drag == CROPSTATE_NOTHING && e.getY() > crop_tl.y * displayScale && e.getX() < crop_br.x * displayScale && e.getY() < crop_br.y * displayScale && e.getX() > crop_tl.x * displayScale) {
			drag = CROPSTATE_DRAG;
		}
		
		if (chckbxLockRatio.isSelected()) {
			drag = simplifyDragMode(drag);
		}

		return drag;
	}
	
	private int simplifyDragMode(int drag) {
		if ((drag & CROPSTATE_RIGHT) != 0) return CROPSTATE_RIGHT;
		if ((drag & CROPSTATE_BOTTOM) != 0) return CROPSTATE_BOTTOM;
		if ((drag & CROPSTATE_TOP) != 0) return CROPSTATE_TOP;
		if ((drag & CROPSTATE_LEFT) != 0) return CROPSTATE_LEFT;
		if ((drag & CROPSTATE_DRAG) != 0) return CROPSTATE_DRAG;
		
		return CROPSTATE_NOTHING;
	}

	private void startDrag(int mode, MouseEvent e) {
		dragMode = mode;
		
		dragStartPoint = new Point(e.getX(), e.getY());
		dragStartCrop_tl = new Point(crop_tl);
		dragStartCrop_br = new Point(crop_br);
		
		updateDrag(e);
	}
	
	private void endDrag(MouseEvent e) {
		updateDrag(e);
		
		dragMode = CROPSTATE_NOTHING;
		
		repaintAll();
	}
	
	private void updateDrag(MouseEvent e) {
		if (dragMode == CROPSTATE_NOTHING) return;
		
		if ((dragMode & CROPSTATE_TOP) != 0) {
			int next = (int)(e.getY() / displayScale);
			setTopCropChecked(next);
			
			if (chckbxLockRatio.isSelected()) {
				setRightCropChecked((int) ((crop_br.y - crop_tl.y) * ImageUtilities.COVER_RATIO) + crop_tl.x);
			}
		}
		
		if ((dragMode & CROPSTATE_RIGHT) != 0) {
			int next = (int)(e.getX() / displayScale);
			setRightCropChecked(next);
			
			if (chckbxLockRatio.isSelected()) {
				setBottomCropChecked((int) ((crop_br.x - crop_tl.x) / ImageUtilities.COVER_RATIO) + crop_tl.y);
			}
		}
		
		if ((dragMode & CROPSTATE_BOTTOM) != 0) {
			int next = (int)(e.getY() / displayScale);
			setBottomCropChecked(next);
			
			if (chckbxLockRatio.isSelected()) {
				setRightCropChecked((int) ((crop_br.y - crop_tl.y) * ImageUtilities.COVER_RATIO) + crop_tl.x);
			}
		}
		
		if ((dragMode & CROPSTATE_LEFT) != 0) {
			int next = (int)(e.getX() / displayScale);
			setLeftCropChecked(next);
			
			if (chckbxLockRatio.isSelected()) {
				setBottomCropChecked((int) ((crop_br.x - crop_tl.x) / ImageUtilities.COVER_RATIO) + crop_tl.y);
			}
		}
		
		if ((dragMode & CROPSTATE_DRAG) != 0) {
			int movX = e.getX() - dragStartPoint.x;
			int movY = e.getY() - dragStartPoint.y;
			
			movX = (int) (movX / displayScale);
			movY = (int) (movY / displayScale);
			
			if (movX < 0) {
				setLeftCropChecked(dragStartCrop_tl.x + movX);
				setRightCropChecked(dragStartCrop_br.x + movX);
			} else {
				setRightCropChecked(dragStartCrop_br.x + movX);
				setLeftCropChecked(dragStartCrop_tl.x + movX);
			}
			
			if (movY < 0) {
				setTopCropChecked(dragStartCrop_tl.y + movY);
				setBottomCropChecked(dragStartCrop_br.y + movY);
			} else {
				setBottomCropChecked(dragStartCrop_br.y + movY);
				setTopCropChecked(dragStartCrop_tl.y + movY);
			}
		}
		
		setDragCursor(dragMode);
		
		repaintMainImage(lblImage.getGraphics());
		
		updateCropInfoLabel();
	}

	private void setLeftCropChecked(int next) {
		next = Math.min(crop_br.x-MIN_WIDTH, next);
		next = Math.max(0, next);
		crop_tl.x = next;
	}

	private void setBottomCropChecked(int next) {
		next = Math.min(imgheight, next);
		next = Math.max(crop_tl.y + MIN_HEIGHT, next);
		crop_br.y = next;
	}

	private void setRightCropChecked(int next) {
		next = Math.max(crop_tl.x+MIN_WIDTH, next);
		next = Math.min(imgwidth, next);
		crop_br.x = next;
	}

	private void setTopCropChecked(int next) {
		next = Math.max(0, next);
		next = Math.min(crop_br.y-MIN_HEIGHT, next);
		crop_tl.y = next;
	}

	private void rotate90(int dir) {
		BufferedImage newb = new BufferedImage(imgheight, imgwidth, BufferedImage.TYPE_4BYTE_ABGR);
		
		AffineTransform at = new AffineTransform();
		at.translate(imgheight / 2, imgwidth / 2);
		at.rotate(Math.PI / 2 * dir);
		at.translate(-imgwidth / 2, -imgheight / 2);
		
		((Graphics2D)newb.getGraphics()).drawImage(img, at, null);
		img = newb;
		imageChanged();
		repaintAll();
	}

	private void flip(boolean flipx, boolean flipy) {
		BufferedImage newb = new BufferedImage(imgwidth, imgheight, BufferedImage.TYPE_4BYTE_ABGR);
		
		AffineTransform at = new AffineTransform();
		at.translate(flipx ? imgwidth : 0, flipy ? imgheight : 0);
		at.scale(flipx ? -1 : 1, flipy ? -1 : 1);
		
		((Graphics2D)newb.getGraphics()).drawImage(img, at, null);
		img = newb;
		imageChanged();
		repaintAll();
	}

	private void updateMouseInfoLabel(MouseEvent e) {
		int x = (int)(e.getX() / displayScale);
		int y = (int)(e.getY() / displayScale);
		
		String col = "?"; //$NON-NLS-1$
		
		if (x >= 0 && y >= 0 && x < img.getWidth() && y < img.getHeight()) {
			int clr = img.getRGB(x, y);
			col = String.format("#%02X%02X%02X", (clr & 0x00ff0000) >> 16, (clr & 0x0000ff00) >> 8, (clr & 0x000000ff) >> 0); //$NON-NLS-1$
		}
		
		lblMouse.setText(LocaleBundle.getFormattedString("CoverCropFrame.lblMouse.text", x, y, col)); //$NON-NLS-1$
	}

	private void resetCrops() {
		crop_tl.x = 0;
		crop_tl.y = 0;
		crop_br.x = imgwidth;
		crop_br.y = imgheight;
		
		repaintAll();
	}
}
