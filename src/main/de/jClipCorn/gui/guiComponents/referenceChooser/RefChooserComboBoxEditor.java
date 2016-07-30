package de.jClipCorn.gui.guiComponents.referenceChooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;

public class RefChooserComboBoxEditor extends BasicComboBoxEditor {
    private final JPanel component;
    private final JLabel label;
     
    private CCOnlineRefType value =  CCOnlineRefType.NONE;
    
    public RefChooserComboBoxEditor(JComboBox<CCOnlineRefType> cbx) {
    	super();

    	component = new JPanel(new BorderLayout());
    	component.setBackground(Color.WHITE);
    	component.setBorder(new MatteBorder(1, 1, 1, 1, SystemColor.windowBorder));
    	
    	label = new JLabel();
    	
    	label.setText(""); //$NON-NLS-1$
    	label.setIcon(CCOnlineRefType.NONE.getIcon16x16());
    	
    	component.add(label, BorderLayout.CENTER);
    }
     
    @Override
	public Component getEditorComponent() {
        return component;
    }
     
    @Override
	public Object getItem() {
        return value;
    }
     
    @Override
	public void setItem(Object item) {
        if (item == null || !(item instanceof CCOnlineRefType)) {
            return;
        }

        value = (CCOnlineRefType) item;

    	label.setText(""); //$NON-NLS-1$
    	label.setIcon(value.getIcon16x16());     
    	label.repaint();
    }   
}
