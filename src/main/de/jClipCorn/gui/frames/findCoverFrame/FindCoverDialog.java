package de.jClipCorn.gui.frames.findCoverFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.features.online.cover.CoverImageParser;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.guiComponents.ScalablePane;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.helper.ExtendedFocusTraversalOnArray;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.listener.ProgressCallbackProgressBarHelper;
import de.jClipCorn.util.listener.UpdateCallbackListener;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FindCoverDialog extends JCCDialog {
	private static final long serialVersionUID = -5790203846014201695L;

	private CoverImageParser parser;

	private final ParseResultHandler handler;
	private final CCDBElementTyp typ;

	public FindCoverDialog(Component owner, CCMovieList ml, ParseResultHandler handler, CCDBElementTyp typ) {
		super(ml);
		this.handler = handler;
		this.typ = typ;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit() {
		setModal(true);

		edSearchTerm.setText(handler.getFullTitle());

		pnlCover.onSelectEvent = this::onCoverSelected;

		setFocusTraversalPolicy(new ExtendedFocusTraversalOnArray(new Component[]{edSearchTerm, btnParse, btnStop, btnOk}));
	}

	private void onCoverSelected(BufferedImage bi) {
		pnlPreview.setImage(bi);
		lblSize.setText(bi.getWidth() + " x " + bi.getHeight()); //$NON-NLS-1$
	}

	private void onOK() {
		if (parser != null) {
			parser.stop();
		}

		BufferedImage img = pnlCover.getSelectedCover();

		if (img != null) {
			handler.setCover(img);
		}

		dispose();
	}

	private void stopParse() {
		parser.stop();
	}

	private void startParse() {
		pnlCover.reset();
		btnParse.setEnabled(false);
		btnStop.setEnabled(true);

		UpdateCallbackListener finishlistener = o -> SwingUtils.invokeLater(() ->
		{
			btnParse.setEnabled(true);
			btnStop.setEnabled(false);
		});

		parser = new CoverImageParser(movielist, new ProgressCallbackProgressBarHelper(progressBar, 100), pnlCover, finishlistener, typ, edSearchTerm.getText(), handler.getSearchReference());
		parser.start();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		pnlTop = new JPanel();
		btnStop = new JButton();
		edSearchTerm = new JTextField();
		btnParse = new JButton();
		progressBar = new JProgressBar();
		splitPane = new JSplitPane();
		scrollPane = new JScrollPane();
		pnlCover = new CoverPanel(scrollPane);
		pnCenterRight = new JPanel();
		pnlPreview = new ScalablePane(Resources.IMG_COVER_STANDARD.get(), true);
		lblSize = new JLabel();
		pnlBottom = new JPanel();
		btnOk = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("FindCoverDialog.this.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"default:grow",
			"default, default:grow, default"));

		//======== pnlTop ========
		{
			pnlTop.setLayout(new FormLayout(
				"$ugap, default, $lcgap, default:grow, $lcgap, default, $ugap",
				"$ugap, default, $lgap, default, $ugap"));

			//---- btnStop ----
			btnStop.setText(LocaleBundle.getString("FindCoverDialog.btnStop.text"));
			btnStop.setEnabled(false);
			btnStop.addActionListener(e -> stopParse());
			pnlTop.add(btnStop, CC.xy(2, 2));

			//---- edSearchTerm ----
			edSearchTerm.setColumns(10);
			edSearchTerm.addActionListener(e -> startParse());
			pnlTop.add(edSearchTerm, CC.xy(4, 2, CC.FILL, CC.DEFAULT));

			//---- btnParse ----
			btnParse.setText(LocaleBundle.getString("FindCoverDialog.btnParse.text"));
			btnParse.addActionListener(e -> startParse());
			pnlTop.add(btnParse, CC.xy(6, 2));
			pnlTop.add(progressBar, CC.xywh(2, 4, 5, 1, CC.FILL, CC.DEFAULT));
		}
		contentPane.add(pnlTop, CC.xy(1, 1, CC.FILL, CC.FILL));

		//======== splitPane ========
		{
			splitPane.setResizeWeight(0.88);

			//======== scrollPane ========
			{
				scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				scrollPane.setViewportView(pnlCover);
			}
			splitPane.setLeftComponent(scrollPane);

			//======== pnCenterRight ========
			{
				pnCenterRight.setLayout(new FormLayout(
					"default:grow",
					"default:grow, $lgap, default"));
				pnCenterRight.add(pnlPreview, CC.xy(1, 1, CC.FILL, CC.FILL));

				//---- lblSize ----
				lblSize.setText("?");
				lblSize.setHorizontalAlignment(SwingConstants.CENTER);
				pnCenterRight.add(lblSize, CC.xy(1, 3, CC.FILL, CC.DEFAULT));
			}
			splitPane.setRightComponent(pnCenterRight);
		}
		contentPane.add(splitPane, CC.xy(1, 2, CC.FILL, CC.FILL));

		//======== pnlBottom ========
		{
			pnlBottom.setLayout(new FormLayout(
				"default:grow, default, default:grow",
				"$ugap, default, $ugap"));

			//---- btnOk ----
			btnOk.setText(LocaleBundle.getString("UIGeneric.btnOK.text"));
			btnOk.addActionListener(e -> onOK());
			pnlBottom.add(btnOk, CC.xy(2, 2));
		}
		contentPane.add(pnlBottom, CC.xy(1, 3, CC.FILL, CC.DEFAULT));
		setSize(800, 480);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel pnlTop;
	private JButton btnStop;
	private JTextField edSearchTerm;
	private JButton btnParse;
	private JProgressBar progressBar;
	private JSplitPane splitPane;
	private JScrollPane scrollPane;
	private CoverPanel pnlCover;
	private JPanel pnCenterRight;
	private ScalablePane pnlPreview;
	private JLabel lblSize;
	private JPanel pnlBottom;
	private JButton btnOk;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
