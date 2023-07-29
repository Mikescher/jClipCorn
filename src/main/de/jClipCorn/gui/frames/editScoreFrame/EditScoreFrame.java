package de.jClipCorn.gui.frames.editScoreFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.guiComponents.cover.*;
import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;

import javax.swing.*;
import java.awt.*;

public class EditScoreFrame extends JCCFrame
{
	private final ICCDatabaseStructureElement element;

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
		this.cbxScore.setSelectedEnum(element.score().get());
		this.memoComment.setText(element.scoreComment().get());
		this.lblScoreIcon.setIcon(cbxScore.getSelectedEnum().getIcon(false));

		this.ctrlCover.setAndResizeCover(element.getSelfOrParentCover());

		this.cbxScore.setEnabled(!movielist.isReadonly());
		this.memoComment.setEnabled(!movielist.isReadonly());
		this.btnOK.setEnabled(!movielist.isReadonly());

		setTitle(LocaleBundle.getFormattedString("EditScoreFrame.title", element.getQualifiedTitle()));
	}

	private void onOkay() {

		element.score().set(this.cbxScore.getSelectedEnum());
		element.scoreComment().set(Str.trim(this.memoComment.getText()));

		dispose();
	}

	private void cbxScoreItemStateChanged() {
		lblScoreIcon.setIcon(cbxScore.getSelectedEnum().getIcon(false));
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		ctrlCover = new CoverLabelFullsize(movielist);
		lblScoreIcon = new JLabel();
		cbxScore = new CCEnumComboBox<CCUserScore>(CCUserScore.getWrapper());
		scrollPane1 = new JScrollPane();
		memoComment = new JTextArea();
		btnOK = new JButton();

		//======== this ========
		setTitle("<dynamic>"); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $ugap, 12dlu, $lcgap, 0dlu:grow, $ugap", //$NON-NLS-1$
			"$ugap, default, $lgap, default:grow, $lgap, default, $ugap")); //$NON-NLS-1$

		//---- ctrlCover ----
		ctrlCover.setText("text"); //$NON-NLS-1$
		contentPane.add(ctrlCover, CC.xywh(2, 2, 1, 5));
		contentPane.add(lblScoreIcon, CC.xy(4, 2, CC.FILL, CC.FILL));

		//---- cbxScore ----
		cbxScore.addItemListener(e -> cbxScoreItemStateChanged());
		contentPane.add(cbxScore, CC.xy(6, 2));

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(memoComment);
		}
		contentPane.add(scrollPane1, CC.xywh(4, 4, 3, 1, CC.DEFAULT, CC.FILL));

		//---- btnOK ----
		btnOK.setText(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
		btnOK.addActionListener(e -> onOkay());
		contentPane.add(btnOK, CC.xywh(4, 6, 3, 1));
		setSize(650, 320);
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private CoverLabelFullsize ctrlCover;
	private JLabel lblScoreIcon;
	private CCEnumComboBox<CCUserScore> cbxScore;
	private JScrollPane scrollPane1;
	private JTextArea memoComment;
	private JButton btnOK;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
