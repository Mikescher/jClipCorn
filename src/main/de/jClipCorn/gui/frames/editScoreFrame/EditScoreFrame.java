package de.jClipCorn.gui.frames.editScoreFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.gui.guiComponents.*;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.cover.*;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EditScoreFrame extends JCCFrame
{
	private record SeasonScoreRow(CCSeason season, JLabel lblIcon, CCEnumComboBox<CCUserScore> cbxScore, JTextArea memoComment) {}

	private final ICCDatabaseStructureElement element;

	private final List<SeasonScoreRow> seasonRows = new ArrayList<>();

	public EditScoreFrame(Component owner, CCMovieList ml, ICCDatabaseStructureElement elem)
	{
		super(ml);
		this.element = elem;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		ccprops().PROP_FSIZE_EDITSCOREFRAME.applyOrSkip(this);

		this.cbxScore.setSelectedEnum(element.score().get());
		this.memoComment.setText(element.scoreComment().get());
		this.lblScoreIcon.setIcon(cbxScore.getSelectedEnum().getIcon(false));

		this.ctrlCover.setAndResizeCover(element.getSelfOrParentCover());

		if (element instanceof CCSeries series) initSeasonRows(series);

		this.pnlContent.setFillViewportHeight(true);

		this.cbxScore.setEnabled(!movielist.isReadonly());
		this.memoComment.setEnabled(!movielist.isReadonly());
		this.btnOK.setEnabled(!movielist.isReadonly());

		setTitle(LocaleBundle.getFormattedString("EditScoreFrame.title", element.getQualifiedTitle()));
	}

	private void initSeasonRows(CCSeries series)
	{
		var seasons = CCStreams.iterate(series.getSeasonsSorted()).filter(CCSeason::hasUserRating).toList();
		if (seasons.isEmpty()) return;

		var layout = (FormLayout) pnlContent.getLayout();

		// pref (rows=11) instead of 0dlu: with multiple text areas the outer scrollpane has to scroll once they no longer fit
		lblTitle.setText(element.title().get());
		lblTitle.setVisible(true);
		layout.setConstraints(cbxScore, CC.xy(5, 1));
		layout.setRowSpec(3, RowSpec.decode("pref:grow"));

		for (var season : seasons)
		{
			layout.appendRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
			layout.appendRow(FormSpecs.DEFAULT_ROWSPEC);
			layout.appendRow(FormSpecs.LINE_GAP_ROWSPEC);
			layout.appendRow(RowSpec.decode("pref:grow"));

			var rowHeader = layout.getRowCount() - 2;
			var rowMemo   = layout.getRowCount();

			var lblIcon = new JLabel(season.Score.get().getIcon(false));

			var lblSeason = new JLabel(season.getTitle());
			lblSeason.setFont(lblTitle.getFont());

			var cbx = new CCEnumComboBox<CCUserScore>(CCUserScore.getWrapper());
			cbx.setSelectedEnum(season.Score.get());
			cbx.addItemListener(e -> lblIcon.setIcon(cbx.getSelectedEnum().getIcon(false)));
			cbx.setEnabled(!movielist.isReadonly());

			var memo = new JTextArea();
			memo.setLineWrap(true);
			memo.setWrapStyleWord(true);
			memo.setRows(memoComment.getRows());
			memo.setText(season.ScoreComment.get());
			memo.setEnabled(!movielist.isReadonly());

			var scroll = new JScrollPane(memo);
			scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			pnlContent.add(lblIcon,   CC.xy(1, rowHeader, CC.FILL, CC.FILL));
			pnlContent.add(lblSeason, CC.xy(3, rowHeader));
			pnlContent.add(cbx,       CC.xy(5, rowHeader));
			pnlContent.add(scroll,    CC.xywh(1, rowMemo, 5, 1, CC.DEFAULT, CC.FILL));

			seasonRows.add(new SeasonScoreRow(season, lblIcon, cbx, memo));
		}
	}

	private void onOkay() {

		element.score().set(this.cbxScore.getSelectedEnum());
		element.scoreComment().set(Str.trim(this.memoComment.getText()));

		for (var row : seasonRows)
		{
			var score = row.cbxScore().getSelectedEnum();
			var comm  = Str.trim(row.memoComment().getText());

			if (row.season().Score.get() != score) row.season().Score.set(score);
			if (!row.season().ScoreComment.get().equals(comm)) row.season().ScoreComment.set(comm);
		}

		dispose();
	}

	private void cbxScoreItemStateChanged() {
		lblScoreIcon.setIcon(cbxScore.getSelectedEnum().getIcon(false));
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ctrlCover = new CoverLabelFullsize(movielist);
		scrollContent = new JScrollPane();
		pnlContent = new JScrollablePanel();
		lblScoreIcon = new JLabel();
		lblTitle = new JLabel();
		cbxScore = new CCEnumComboBox<CCUserScore>(CCUserScore.getWrapper());
		scrollPane1 = new JScrollPane();
		memoComment = new JTextArea();
		btnOK = new JButton();

		//======== this ========
		setTitle("<dynamic>");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $ugap, 0dlu:grow, $ugap",
			"$ugap, default:grow, $lgap, default, $ugap"));

		//---- ctrlCover ----
		ctrlCover.setText("text");
		contentPane.add(ctrlCover, CC.xywh(2, 2, 1, 3));

		//======== scrollContent ========
		{
			scrollContent.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollContent.setViewportBorder(null);
			scrollContent.setBorder(null);

			//======== pnlContent ========
			{
				pnlContent.setLayout(new FormLayout(
					"12dlu, $lcgap, default, $lcgap, 0dlu:grow",
					"default, $lgap, 0dlu:grow"));
				pnlContent.add(lblScoreIcon, CC.xy(1, 1, CC.FILL, CC.FILL));

				//---- lblTitle ----
				lblTitle.setVisible(false);
				lblTitle.setFont(lblTitle.getFont().deriveFont(lblTitle.getFont().getStyle() | Font.BOLD));
				pnlContent.add(lblTitle, CC.xy(3, 1));

				//---- cbxScore ----
				cbxScore.addItemListener(e -> cbxScoreItemStateChanged());
				pnlContent.add(cbxScore, CC.xywh(3, 1, 3, 1));

				//======== scrollPane1 ========
				{
					scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

					//---- memoComment ----
					memoComment.setLineWrap(true);
					memoComment.setWrapStyleWord(true);
					memoComment.setRows(11);
					scrollPane1.setViewportView(memoComment);
				}
				pnlContent.add(scrollPane1, CC.xywh(1, 3, 5, 1, CC.DEFAULT, CC.FILL));
			}
			scrollContent.setViewportView(pnlContent);
		}
		contentPane.add(scrollContent, CC.xy(4, 2, CC.DEFAULT, CC.FILL));

		//---- btnOK ----
		btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text"));
		btnOK.addActionListener(e -> onOkay());
		contentPane.add(btnOK, CC.xy(4, 4));
		setSize(650, 320);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private CoverLabelFullsize ctrlCover;
	private JScrollPane scrollContent;
	private JScrollablePanel pnlContent;
	private JLabel lblScoreIcon;
	private JLabel lblTitle;
	private CCEnumComboBox<CCUserScore> cbxScore;
	private JScrollPane scrollPane1;
	private JTextArea memoComment;
	private JButton btnOK;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
