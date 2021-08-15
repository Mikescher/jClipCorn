package de.jClipCorn.gui.guiComponents.dateTimeListEditor;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.gui.guiComponents.jCCDateSpinner.JCCDateSpinner;
import de.jClipCorn.gui.guiComponents.jCCTimeSpinner.JCCTimeSpinner;
import de.jClipCorn.gui.guiComponents.jSplitButton.JSplitButton;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCTime;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class DateTimeListEditor extends JPanel {
	private static final long serialVersionUID = -1991426029921952573L;
	
	private final static Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
	
	private JSplitButton btnAdd;
	private JPanel pnlTop;
	private JCCDateSpinner spnrDate;
	private JLabel lblCount;
	private JPanel pnlCount;
	private JPanel pnlInput;
	private JScrollPane scrollPane;
	private JPanel pnlContent;
	private JCCTimeSpinner spnrTime;
	private JCheckBox cbDate;
	private JCheckBox cbTime;
	private JPopupMenu splitPopup;

	private Color defaultPanelBackground = COLOR_TRANSPARENT;
	
	private final List<JPanel> listPanels = new ArrayList<>();
	private final List<CCDateTime> data;

	private final List<ActionListener> _changeListener = new ArrayList<>();

	public DateTimeListEditor() {
		initGUI();
		
		data = new ArrayList<>();
		
		updateContentList(null);
	}
	
	public DateTimeListEditor(CCDateTimeList lst) {
		initGUI();

		data = lst.ccstream().enumerate();
		
		updateContentList(null);
	}
	
	private void initGUI() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		this.setLayout(new BorderLayout());
		
		splitPopup = new JPopupMenu();
		{
			JMenuItem miAdd = new JMenuItem(LocaleBundle.getString("CCDate.Editor.Add")); //$NON-NLS-1$
			miAdd.addActionListener(this::onAdd);
			splitPopup.add(miAdd);

			JMenuItem miNow = new JMenuItem(LocaleBundle.getString("CCDate.Editor.Now")); //$NON-NLS-1$
			miNow.addActionListener(this::onSetNow);
			splitPopup.add(miNow);

			JMenuItem miUnknown = new JMenuItem(LocaleBundle.getString("CCDate.Editor.Unspecified")); //$NON-NLS-1$
			miUnknown.addActionListener(this::onSetUnknown);
			splitPopup.add(miUnknown);
		}
		
		pnlTop = new JPanel();
		add(pnlTop, BorderLayout.NORTH);
		pnlTop.setLayout(new BorderLayout(0, 0));
		
		pnlCount = new JPanel();
		pnlCount.setAlignmentY(Component.TOP_ALIGNMENT);
		pnlCount.setBorder(new EmptyBorder(4, 4, 4, 4));
		pnlTop.add(pnlCount, BorderLayout.WEST);
		pnlCount.setLayout(new BorderLayout(0, 0));
		
		lblCount = new JLabel("??"); //$NON-NLS-1$
		lblCount.setVerticalAlignment(SwingConstants.TOP);
		pnlCount.add(lblCount);
		lblCount.setHorizontalAlignment(SwingConstants.LEFT);
		
		pnlInput = new JPanel();
		pnlTop.add(pnlInput, BorderLayout.EAST);
		
		cbDate = new JCheckBox();
		cbDate.setSelected(true);
		cbDate.addItemListener(e -> updateEnabledStates());
		//cbDate.addItemListener(new ItemChangeLambdaAdapter(this::triggerOnChanged, -1));
		cbDate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { focusPanel(null); }
			@Override
			public void mousePressed(MouseEvent e) { focusPanel(null); }
			@Override
			public void mouseReleased(MouseEvent e) { focusPanel(null); }
		});
		pnlInput.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.PREF_COLSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				FormSpecs.PREF_COLSPEC,
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("max(35dlu;pref)"), //$NON-NLS-1$
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("21px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("21px"), //$NON-NLS-1$
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		pnlInput.add(cbDate, "1, 2, right, top"); //$NON-NLS-1$
		
		spnrDate = new JCCDateSpinner();
		//spnrDate.addChangeListener(new ChangeLambdaAdapter(this::triggerOnChanged));
		spnrDate.addChangeListener(e -> { focusPanel(null); });
		pnlInput.add(spnrDate, "3, 2, left, top"); //$NON-NLS-1$
		
		cbTime = new JCheckBox();
		cbTime.setSelected(true);
		cbTime.addItemListener(e -> updateEnabledStates());
		//cbTime.addItemListener(new ItemChangeLambdaAdapter(this::triggerOnChanged, -1));
		cbTime.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { focusPanel(null); }
			@Override
			public void mousePressed(MouseEvent e) { focusPanel(null); }
			@Override
			public void mouseReleased(MouseEvent e) { focusPanel(null); }
		});
		pnlInput.add(cbTime, "1, 4, right, top"); //$NON-NLS-1$
		
		spnrTime = new JCCTimeSpinner();
		//spnrTime.addChangeListener(new ChangeLambdaAdapter(this::triggerOnChanged));
		spnrTime.addChangeListener(e -> { focusPanel(null); });
		pnlInput.add(spnrTime, "3, 4, fill, top"); //$NON-NLS-1$
		
		btnAdd = new JSplitButton("+"); //$NON-NLS-1$
		btnAdd.setHorizontalAlignment(SwingConstants.LEFT);
		btnAdd.addButtonClickedActionListener(this::onAdd);
		btnAdd.setFont(new Font("Tahoma", Font.BOLD, 11)); //$NON-NLS-1$
		btnAdd.setPopupMenu(splitPopup);
		pnlInput.add(btnAdd, "5, 2, 1, 3, fill, center"); //$NON-NLS-1$
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
		
		pnlContent = new JPanel();
		pnlContent.setBackground(Color.WHITE);
		scrollPane.setViewportView(pnlContent);
		pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
	}

	public void addChangeListener(ActionListener a) {
		_changeListener.add(a);
	}

	private void triggerOnChanged() {
		for (ActionListener ac : _changeListener) ac.actionPerformed(new ActionEvent(this, -1, Str.Empty));
	}

	private void resortList() {
		HashSet<CCDateTime> hash = new HashSet<>(data);
	    
	    List<CCDateTime> list = new ArrayList<>(hash);
	    Collections.sort(list);
	    
	    data.clear();
		data.addAll(list);
	    
	    lblCount.setText(Integer.toString(data.size()));
	}
	
	public void setValue(CCDateTimeList dtlist) {
		var diff = dtlist.isEqual(getValue());

		data.clear();
		data.addAll(dtlist.ccstream().enumerate());
		
		updateContentList(null);

		if (diff) triggerOnChanged();
	}

	public CCDateTimeList getValue() {
		return CCDateTimeList.create(data);
	}
	
	private void onAdd(ActionEvent e) {
		CCDateTime v = getCurrentInputValue();
		
		data.add(getCurrentInputValue());
		
		updateContentList(v);
		
		triggerOnChanged();
	}
	
	private CCDateTime getCurrentInputValue() {
		if (! cbDate.isSelected()) {
			return (CCDateTime.getUnspecified());
		} else if (! cbTime.isSelected()) {
			return (CCDateTime.create(spnrDate.getValue()));
		} else {
			return (CCDateTime.create(spnrDate.getValue(), spnrTime.getValue()));
		}
	}
	
	private void onSetNow(ActionEvent e) {
		cbDate.setSelected(true);
		cbTime.setSelected(true);
		spnrDate.setEnabled(true);
		spnrTime.setEnabled(true);
		
		spnrDate.setValue(CCDate.getCurrentDate());
		spnrTime.setValue(CCTime.getCurrentTime());

		focusPanel(null);
		
		//triggerOnChanged();
	}
	
	private void onSetUnknown(ActionEvent e) {
		cbDate.setSelected(false);
		cbTime.setSelected(false);
		spnrDate.setEnabled(false);
		spnrTime.setEnabled(false);

		spnrDate.setValue(CCDate.getMinimumDate());
		spnrTime.setValue(CCTime.getMidnight());

		focusPanel(null);
		
		//triggerOnChanged();
	}

	private void onRemove(CCDateTime element) {
		for (int i = 0; i < data.size(); i++) {
			if (element.isExactEqual(data.get(i))) {
				data.remove(i);
				updateContentList(null);
				break;
			}
		}
		
		triggerOnChanged();
	}

	private void onDisplay(CCDateTime element) {
		if (element.isUnspecifiedDateTime()) {
			cbDate.setSelected(false);
			cbTime.setSelected(false);
			spnrDate.setEnabled(false);
			spnrTime.setEnabled(false);

			spnrDate.setValue(CCDate.getMinimumDate());
			spnrTime.setValue(CCTime.getMidnight());
		} else if (element.time.isUnspecifiedTime()) {
			cbDate.setSelected(true);
			cbTime.setSelected(false);
			spnrDate.setEnabled(true);
			spnrTime.setEnabled(false);
			
			spnrDate.setValue(element.date);
			spnrTime.setValue(CCTime.getMidnight());
		} else {
			cbDate.setSelected(true);
			cbTime.setSelected(true);
			spnrDate.setEnabled(true);
			spnrTime.setEnabled(true);
			
			spnrDate.setValue(element.date);
			spnrTime.setValue(element.time);
		}

		cbTime.setEnabled(cbDate.isSelected());
	}
	
	private void updateEnabledStates() {
		cbTime.setEnabled(cbDate.isSelected());
		
		if (! cbDate.isSelected()) {
			spnrDate.setEnabled(false);
			spnrTime.setEnabled(false);
		} else if (! cbTime.isSelected()) {
			spnrDate.setEnabled(true);
			spnrTime.setEnabled(false);
		} else {
			spnrDate.setEnabled(true);
			spnrTime.setEnabled(true);
		}
	}
	
	private void updateContentList(CCDateTime selector) {
		resortList();
		
		listPanels.clear();
		pnlContent.removeAll();
		pnlContent.validate();
		
		JPanel afterSelPanel = null;
		int idx = 0;
		for (CCDateTime element : data) {
			JPanel pnl = new JPanel(new BorderLayout());

			JLabel lbl = new JLabel(element.toStringUINormal());
			JLabel btn = new JLabel(Resources.ICN_FRAMES_DELETE.get16x16());
			
			btn.setHorizontalAlignment(SwingConstants.CENTER);
			btn.setVerticalAlignment(SwingConstants.CENTER);
			btn.setBorder(new EmptyBorder(0, 2, 0, 2));
			
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) { focusPanel(pnl); onDisplay(element); }
				
				@Override
				public void mouseReleased(MouseEvent e) { focusPanel(pnl); onDisplay(element); }
			});
			
			btn.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) { onRemove(element); }
			});
			
			pnl.add(lbl, BorderLayout.CENTER);
			pnl.add(btn, BorderLayout.EAST);
			
			defaultPanelBackground = pnl.getBackground();
			pnl.setBackground((idx++ % 2 == 0) ? defaultPanelBackground : Color.LIGHT_GRAY);

			int pw = pnl.getPreferredSize().width;
			int ph = pnl.getPreferredSize().height;
			
			pnl.setPreferredSize(new Dimension(pw, ph));
			pnl.setMaximumSize(new Dimension(99999, ph));
			pnl.setMinimumSize(new Dimension(0, ph));
			
			if (element.isExactEqual(selector)) afterSelPanel = pnl;
			
			pnlContent.add(pnl);
			listPanels.add(pnl);
		}

		pnlContent.revalidate();
		pnlContent.repaint();
		
		scrollPane.revalidate();
		
		if (afterSelPanel != null) focusPanel(afterSelPanel);
	}
	
	private void focusPanel(JPanel pnl) {
		int idx2 = 0;
		for (JPanel other : listPanels) {
			if (other != pnl)
				other.setBackground((idx2 % 2 == 0) ? defaultPanelBackground : Color.LIGHT_GRAY);
			else
				other.setBackground(Color.CYAN);
			
			idx2++;
		}

		pnlContent.revalidate();
		pnlContent.repaint();
	}
}
