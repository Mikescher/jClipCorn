package de.jClipCorn.gui.guiComponents;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.guiComponents.tags.TagsChangedListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A multi-value tag field component that displays selected values as removable tags
 * and provides a dropdown with searchable checkbox list for selection.
 * 
 * This replaces the old text-based autocomplete with a modern tag-based interface.
 * All instances now use multi-value behavior - single value use case has been removed.
 */
public class JAutoCompleteTextField extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JMultiSelectTagField tagField;
    private final boolean designmode;

	@DesignCreate
	private static JAutoCompleteTextField designCreate()
	{
		return new JAutoCompleteTextField(ArrayList::new, true);
	}

	/**
     * Creates a multi-select tag field (formerly single-value autocomplete)
     * @param valueSupplier Supplier that provides the list of possible values
     */
    public JAutoCompleteTextField(Supplier<List<String>> valueSupplier) {
        this(valueSupplier, true, false);
    }

    /**
     * Creates a multi-select tag field 
     * @param valueSupplier Supplier that provides the list of possible values
     * @param multiValue ignored - all instances are now multi-value
     */
	public JAutoCompleteTextField(Supplier<List<String>> valueSupplier, boolean multiValue) {
		this(valueSupplier, true, false);
	}

	private JAutoCompleteTextField(Supplier<List<String>> valueSupplier, boolean multiValue, boolean designmode) {
		super(new BorderLayout());
		this.designmode = designmode;
		this.tagField = new JMultiSelectTagField(valueSupplier);
		
		initComponents();
	}

    private void initComponents() {
        add(tagField, BorderLayout.CENTER);
    }

    // Backward compatibility API methods
    
    /**
     * Gets the values as a list. Always returns multiple values now.
     */
    public List<String> getValues() {
        return tagField.getValues();
    }

    /**
     * Sets the values from a list.
     */
    public void setValues(List<String> values) {
        tagField.setValues(values);
    }

    /**
     * Gets the value as a single string (backward compatibility)
     * Returns the first selected value, or empty string if none selected.
     */
    public String getValue() {
        List<String> values = getValues();
        return values.isEmpty() ? "" : values.get(0);
    }

    /**
     * Sets the value as a single string (backward compatibility)
     * Clears existing values and sets a single value.
     */
    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            tagField.clearValues();
        } else {
            List<String> values = new ArrayList<>();
            values.add(value.trim());
            tagField.setValues(values);
        }
    }

    /**
     * Clears all selected values.
     */
    public void clearValues() {
        tagField.clearValues();
    }

    /**
     * Returns true if any values are selected.
     */
    public boolean hasValues() {
        return tagField.hasValues();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        tagField.setEnabled(enabled);
    }

    // Delegate focus methods to tag field
    @Override
    public boolean requestFocusInWindow() {
        return tagField.requestFocusInWindow();
    }

    @Override
    public void requestFocus() {
        tagField.requestFocus();
    }

    /**
     * Adds a change listener that is notified when the selected values change.
     */
    public void addChangeListener(TagsChangedListener listener) {
        tagField.addChangeListener(listener);
    }
}