package de.jClipCorn.gui.guiComponents.referenceChooser;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.datatypes.Tuple;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ReferenceChooserDialog extends JCCDialog {
	private static final long serialVersionUID = -3812579297074052719L;
	
	private final JReferenceChooser parent;
	private JSingleReferenceChooser chsrMain;
	private List<JSingleSubReferenceChooser> chsrAdd;

	private JPanel pnlData;

	public ReferenceChooserDialog(CCMovieList ml, CCSingleOnlineReference dataMain, List<CCSingleOnlineReference> dataAdditional, JReferenceChooser parent) {
		super(ml);

		this.parent = parent;
				
		initGUI();
		updateControls(dataMain, dataAdditional);
		setSize(380, Math.max(250, 165 + 32*dataAdditional.size()));


		setLocationRelativeTo(parent);
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("ReferenceChooserDialog.title")); //$NON-NLS-1$
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setType(Type.UTILITY);
		setMinimumSize(new Dimension(250, 200));
		setModal(true);

		getContentPane().setLayout(new BorderLayout());

		JPanel pnlBase = new JPanel();
		{
			pnlBase.setBorder(new EmptyBorder(5, 5, 5, 5));
			pnlBase.setLayout(new BorderLayout());
			getContentPane().add(pnlBase, BorderLayout.CENTER);

			JScrollPane pnlScroll = new JScrollPane();
			{
				pnlBase.add(pnlScroll, BorderLayout.CENTER);

				pnlData = new JPanel();
				{
					pnlData.setLayout(new FormLayout());
					pnlScroll.setViewportView(pnlData);
				}
			}

			JPanel pnlOpt = new JPanel();
			{
				FlowLayout flowLayout;
				pnlOpt.setLayout(flowLayout = new FlowLayout());
				flowLayout.setAlignment(FlowLayout.RIGHT);
				pnlBase.add(pnlOpt, BorderLayout.SOUTH);

				JButton btnAppend = new JButton(LocaleBundle.getString("ReferenceChooserDialog.btnAppend")); //$NON-NLS-1$
				btnAppend.addActionListener(e -> insertNewRef(chsrAdd.size()));
				pnlOpt.add(btnAppend);
			}
		}

		JPanel pnlBottom = new JPanel();
		{
			FlowLayout flowLayout;
			pnlBottom.setLayout(flowLayout = new FlowLayout());
			flowLayout.setAlignment(FlowLayout.RIGHT);
			getContentPane().add(pnlBottom, BorderLayout.SOUTH);

			JButton btnCancel = new JButton(LocaleBundle.getString("UIGeneric.btnCancel.text")); //$NON-NLS-1$
			btnCancel.addActionListener(e -> { setVisible(false); dispose(); });
			pnlBottom.add(btnCancel);

			JButton btnOk = new JButton(LocaleBundle.getString("UIGeneric.btnOK.text")); //$NON-NLS-1$
			btnOk.setFont(new Font(btnOk.getFont().getFontName(), Font.BOLD, btnOk.getFont().getSize()));
			btnOk.addActionListener(e -> onOK());
			pnlBottom.add(btnOk);
		}

		JRootPane root = getRootPane();
		root.registerKeyboardAction(e -> { setVisible(false); dispose(); }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	private void updateControls(CCSingleOnlineReference dataMain, List<CCSingleOnlineReference> dataAdditional) {
		pnlData.removeAll();

		ArrayList<RowSpec> rspec = new ArrayList<>();
		ArrayList<ColumnSpec> cspec = new ArrayList<>();

		cspec.add(ColumnSpec.decode("max(15dlu;default):grow")); //$NON-NLS-1$
		cspec.add(FormSpecs.RELATED_GAP_COLSPEC);
		cspec.add(ColumnSpec.decode("default")); //$NON-NLS-1$
		cspec.add(ColumnSpec.decode("default")); //$NON-NLS-1$
		cspec.add(ColumnSpec.decode("default")); //$NON-NLS-1$
		cspec.add(ColumnSpec.decode("default")); //$NON-NLS-1$

		rspec.add(RowSpec.decode("24px")); //$NON-NLS-1$
		rspec.add(RowSpec.decode("5dlu")); //$NON-NLS-1$
		for (int i = 0; i < dataAdditional.size(); i++) {
			rspec.add(FormSpecs.RELATED_GAP_ROWSPEC);
			rspec.add(RowSpec.decode("24px")); //$NON-NLS-1$
		}
		rspec.add(RowSpec.decode("default:grow")); //$NON-NLS-1$

		pnlData.setLayout(new FormLayout(cspec.toArray(new ColumnSpec[0]), rspec.toArray(new RowSpec[0])));

		chsrMain = new JSingleReferenceChooser(movielist);
		chsrMain.setValue(dataMain);
		pnlData.add(chsrMain, "1, 1, 6, 1, fill, fill"); //$NON-NLS-1$

		chsrAdd = new ArrayList<>();

		for (int i = 0; i < dataAdditional.size(); i++) {
			final int fi = i;

			JSingleSubReferenceChooser c = new JSingleSubReferenceChooser(movielist);
			c.setValue(dataAdditional.get(i));
			pnlData.add(c, "1, "+(4 + i*2)+", fill, fill"); //$NON-NLS-1$ //$NON-NLS-2$

			JButton btnUp = new JButton(Resources.ICN_GENERIC_BULLET_UP.get());
			JButton btnDn = new JButton(Resources.ICN_GENERIC_BULLET_DOWN.get());
			JButton btnIn = new JButton(Resources.ICN_GENERIC_BULLET_ADD.get());
			JButton btnRm = new JButton(Resources.ICN_GENERIC_BULLET_REM.get());

			pnlData.add(btnUp, "3, "+(4 + i*2)+", fill, fill"); //$NON-NLS-1$ //$NON-NLS-2$
			pnlData.add(btnDn, "4, "+(4 + i*2)+", fill, fill"); //$NON-NLS-1$ //$NON-NLS-2$
			pnlData.add(btnIn, "5, "+(4 + i*2)+", fill, fill"); //$NON-NLS-1$ //$NON-NLS-2$
			pnlData.add(btnRm, "6, "+(4 + i*2)+", fill, fill"); //$NON-NLS-1$ //$NON-NLS-2$

			btnUp.addActionListener(e -> moveRefUp(fi));
			btnDn.addActionListener(e -> moveRefDown(fi));
			btnIn.addActionListener(e -> insertNewRef(fi));
			btnRm.addActionListener(e -> removeRef(fi));

			btnUp.setMargin(new Insets(0, 0, 0, 0));
			btnDn.setMargin(new Insets(0, 0, 0, 0));
			btnIn.setMargin(new Insets(0, 0, 0, 0));
			btnRm.setMargin(new Insets(0, 0, 0, 0));

			btnUp.setEnabled(i>0);
			btnDn.setEnabled(i<(dataAdditional.size()-1));

			chsrAdd.add(c);
		}

		validate();
		pnlData.revalidate();

		repaint();
		pnlData.repaint();
	}

	private void onOK() {
		Tuple<CCSingleOnlineReference, List<CCSingleOnlineReference>> val = getActual();

		List<CCSingleOnlineReference> value = new ArrayList<>();
		for	(CCSingleOnlineReference soref : val.Item2)
		{
			if (soref == null) continue;
			if (soref.isUnset()) continue;
			if (soref.isInvalid()) continue;
			value.add(soref);
		}

		parent.setMain(val.Item1);
		parent.setAdditional(value);
		dispose();
	}

	private Tuple<CCSingleOnlineReference, List<CCSingleOnlineReference>> getActual() {
		CCSingleOnlineReference m = chsrMain.getValue();

		List<CCSingleOnlineReference> l = new ArrayList<>();
		for (JSingleSubReferenceChooser c : chsrAdd) l.add(c.getValue());

		return Tuple.Create(m, l);
	}

	private void removeRef(int idx) {
		Tuple<CCSingleOnlineReference, List<CCSingleOnlineReference>> d = getActual();
		d.Item2.remove(idx);
		updateControls(d.Item1, d.Item2);
	}

	private void insertNewRef(int idx) {
		Tuple<CCSingleOnlineReference, List<CCSingleOnlineReference>> d = getActual();
		if (idx == d.Item2.size()) d.Item2.add(CCSingleOnlineReference.EMPTY);
		else d.Item2.add(idx+1, CCSingleOnlineReference.EMPTY);
		updateControls(d.Item1, d.Item2);
	}

	private void moveRefDown(int idx) {
		Tuple<CCSingleOnlineReference, List<CCSingleOnlineReference>> d = getActual();
		CCSingleOnlineReference r = d.Item2.remove(idx);
		d.Item2.add(idx+1, r);
		updateControls(d.Item1, d.Item2);
	}

	private void moveRefUp(int idx) {
		Tuple<CCSingleOnlineReference, List<CCSingleOnlineReference>> d = getActual();
		CCSingleOnlineReference r = d.Item2.remove(idx);
		d.Item2.add(idx-1, r);
		updateControls(d.Item1, d.Item2);
	}
}
