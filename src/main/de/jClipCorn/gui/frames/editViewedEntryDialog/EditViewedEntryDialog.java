package de.jClipCorn.gui.frames.editViewedEntryDialog;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.databaseElement.columnTypes.CCViewedEntry;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.jCCTimeSpinner.JCCTimeSpinner;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import java.awt.*;

/**
 * Edits the datetime and the comment of one viewed-history entry.
 * Does not need the movielist (it is null) - do not call {@link #ccprops()} here.
 */
public class EditViewedEntryDialog extends JCCDialog
{
	private final CCViewedEntry entry;
	private final Func1to0<CCViewedEntry> onOkay;

	public EditViewedEntryDialog(Component owner, CCViewedEntry entry, Func1to0<CCViewedEntry> onOkay)
	{
		super(null, owner == null ? null : SwingUtilities.getWindowAncestor(owner));

		this.entry  = entry;
		this.onOkay = onOkay;

		initComponents();
		postInit();

		setLocationRelativeTo(owner);
	}

	private void postInit()
	{
		var dt = entry.datetime;

		cbDate.setSelected(!dt.isUnspecifiedDateTime());
		cbTime.setSelected(!dt.isUnspecifiedDateTime() && !dt.time.isUnspecifiedTime());

		spnrDate.setValue(dt.isUnspecifiedDateTime() ? CCDate.getCurrentDate() : dt.date);
		spnrTime.setValue(cbTime.isSelected() ? dt.time : CCTime.getMidnight());

		edComment.setText(entry.comment);

		updateEnabledStates();

		getRootPane().setDefaultButton(btnOkay);

		pack();
	}

	private void updateEnabledStates()
	{
		cbTime.setEnabled(cbDate.isSelected());
		spnrDate.setEnabled(cbDate.isSelected());
		spnrTime.setEnabled(cbDate.isSelected() && cbTime.isSelected());
	}

	private CCDateTime getInputDateTime()
	{
		if (!cbDate.isSelected()) return CCDateTime.getUnspecified();
		if (!cbTime.isSelected()) return CCDateTime.create(spnrDate.getValue());
		return CCDateTime.create(spnrDate.getValue(), spnrTime.getValue());
	}

	private void onOkay()
	{
		onOkay.invoke(CCViewedEntry.create(getInputDateTime(), edComment.getText().trim()));
		dispose();
	}

	private void onCancel()
	{
		dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		cbDate = new JCheckBox();
		spnrDate = new JCCDateSpinner();
		cbTime = new JCheckBox();
		spnrTime = new JCCTimeSpinner();
		lblComment = new JLabel();
		edComment = new JTextField();
		pnlButtons = new JPanel();
		btnOkay = new JButton();
		btnCancel = new JButton();

		//======== this ========
		setTitle(LocaleBundle.getString("EditViewedEntryDialog.this.title"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default, $lcgap, default:grow, $ugap",
			"$ugap, 2*(default, $lgap), 2*(default, $ugap)"));

		//---- cbDate ----
		cbDate.setText(LocaleBundle.getString("EditViewedEntryDialog.cbDate.text"));
		cbDate.addItemListener(e -> updateEnabledStates());
		contentPane.add(cbDate, CC.xy(2, 2));
		contentPane.add(spnrDate, CC.xy(4, 2));

		//---- cbTime ----
		cbTime.setText(LocaleBundle.getString("EditViewedEntryDialog.cbTime.text"));
		cbTime.addItemListener(e -> updateEnabledStates());
		contentPane.add(cbTime, CC.xy(2, 4));
		contentPane.add(spnrTime, CC.xy(4, 4));

		//---- lblComment ----
		lblComment.setText(LocaleBundle.getString("EditViewedEntryDialog.lblComment.text"));
		contentPane.add(lblComment, CC.xy(2, 6));

		//---- edComment ----
		edComment.setColumns(20);
		edComment.addActionListener(e -> onOkay());
		contentPane.add(edComment, CC.xy(4, 6));

		//======== pnlButtons ========
		{
			pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));

			//---- btnOkay ----
			btnOkay.setText(LocaleBundle.getString("UIGeneric.btnOK.text"));
			btnOkay.addActionListener(e -> onOkay());
			pnlButtons.add(btnOkay);

			//---- btnCancel ----
			btnCancel.setText(LocaleBundle.getString("UIGeneric.btnCancel.text"));
			btnCancel.addActionListener(e -> onCancel());
			pnlButtons.add(btnCancel);
		}
		contentPane.add(pnlButtons, CC.xywh(2, 8, 3, 1, CC.FILL, CC.DEFAULT));
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JCheckBox cbDate;
	private JCCDateSpinner spnrDate;
	private JCheckBox cbTime;
	private JCCTimeSpinner spnrTime;
	private JLabel lblComment;
	private JTextField edComment;
	private JPanel pnlButtons;
	private JButton btnOkay;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
