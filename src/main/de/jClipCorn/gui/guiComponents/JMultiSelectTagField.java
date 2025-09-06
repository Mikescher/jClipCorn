package de.jClipCorn.gui.guiComponents;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.guiComponents.tags.TagsChangedListener;
import de.jClipCorn.util.Str;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A multi-value tag field component that displays selected values as removable tags
 * and provides a dropdown with searchable checkbox list for selection.
 */
public class JMultiSelectTagField extends JPanel {
    private static final long serialVersionUID = 1L;

    private final Supplier<List<String>> valueSupplier;
    private final Set<String> selectedValues;
    private final JPanel tagPanel;
    private final JButton dropdownButton;
    private JPopupMenu popup;
    private JTextField searchField;
    private JPanel checkboxPanel;
    private JScrollPane checkboxScrollPane;
    private List<JCheckBox> checkboxes;
    private final boolean designmode;
    private final EventListenerList listenerList = new EventListenerList();

    @DesignCreate
    private static JMultiSelectTagField designCreate() {
        return new JMultiSelectTagField(ArrayList::new);
    }

    public JMultiSelectTagField(Supplier<List<String>> valueSupplier) {
        this(valueSupplier, false);
    }

    private JMultiSelectTagField(Supplier<List<String>> valueSupplier, boolean designmode) {
        super(new BorderLayout());
        this.valueSupplier = valueSupplier;
        this.selectedValues = new HashSet<>();
        this.checkboxes = new ArrayList<>();
        this.designmode = designmode;
        
        // Initialize final fields
        this.tagPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        this.dropdownButton = new JButton("▼");

        initComponents();
        setupListeners();
    }

    private void initComponents() {
        // Main panel with tag display area and dropdown button
        setBorder(new CompoundBorder(
            new LineBorder(Color.GRAY, 1),
            new EmptyBorder(2, 2, 2, 2)
        ));

        // Tag display area setup
        tagPanel.setBackground(Color.WHITE);
        tagPanel.setOpaque(true);

        // Dropdown button setup
        dropdownButton.setPreferredSize(new Dimension(20, 20));
        dropdownButton.setMargin(new Insets(0, 0, 0, 0));
        dropdownButton.setFocusable(false);

        add(tagPanel, BorderLayout.CENTER);
        add(dropdownButton, BorderLayout.EAST);

        // Initialize popup
        initializePopup();
        
        // Update display
        updateTagDisplay();
    }

    private void initializePopup() {
        popup = new JPopupMenu();
        popup.setLayout(new BorderLayout());

        // Search field at top
        searchField = new JTextField();
        searchField.setBorder(new CompoundBorder(
            new LineBorder(Color.LIGHT_GRAY, 1),
            new EmptyBorder(4, 4, 4, 4)
        ));
        popup.add(searchField, BorderLayout.NORTH);

        // Checkbox panel
        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.setBackground(Color.WHITE);

        checkboxScrollPane = new JScrollPane(checkboxPanel);
        checkboxScrollPane.setPreferredSize(new Dimension(250, 200));
        checkboxScrollPane.setBorder(null);
        checkboxScrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI());
        checkboxScrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI());

        popup.add(checkboxScrollPane, BorderLayout.CENTER);
    }

    private void setupListeners() {
        // Dropdown button listener
        dropdownButton.addActionListener(e -> togglePopup());

        // Tag panel click listener - open dropdown when clicking on empty space
        tagPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!popup.isVisible()) {
                    showPopup();
                }
            }
        });

        // Search field listeners
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterCheckboxes();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterCheckboxes();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterCheckboxes();
            }
        });

        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String text = searchField.getText().trim();
                    if (!text.isEmpty() && !selectedValues.contains(text)) {
                        addValue(text);
                        searchField.setText("");
                        filterCheckboxes();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    hidePopup();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        // Close popup when clicking outside
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}

            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!popup.isAncestorOf(e.getOppositeComponent())) {
                        hidePopup();
                    }
                });
            }
        });
    }

    private void togglePopup() {
        if (popup.isVisible()) {
            hidePopup();
        } else {
            showPopup();
        }
    }

    private void showPopup() {
        if (!designmode) {
            populateCheckboxes();
            searchField.setText("");
            filterCheckboxes();

            // Set popup width to match component width
            int componentWidth = getWidth();
            checkboxScrollPane.setPreferredSize(new Dimension(componentWidth, 200));
            popup.setPopupSize(componentWidth, popup.getPreferredSize().height);

            Point location = getLocationOnScreen();
            popup.show(this, 0, getHeight());
            popup.setLocation(location.x, location.y + getHeight());

            SwingUtilities.invokeLater(() -> searchField.requestFocus());
        }
    }

    private void hidePopup() {
        if (popup.isVisible()) {
            popup.setVisible(false);
        }
    }

    private void populateCheckboxes() {
        checkboxes.clear();
        checkboxPanel.removeAll();

        List<String> allValues = designmode ? new ArrayList<>() : valueSupplier.get();
        if (allValues == null) allValues = new ArrayList<>();

        // Add all available values plus any selected values not in the list
        Set<String> allPossibleValues = new HashSet<>(allValues);
        allPossibleValues.addAll(selectedValues);

        List<String> sortedValues = allPossibleValues.stream()
            .sorted(String::compareToIgnoreCase)
            .collect(Collectors.toList());

        for (String value : sortedValues) {
            JCheckBox checkbox = new JCheckBox(value);
            checkbox.setSelected(selectedValues.contains(value));
            checkbox.setBackground(Color.WHITE);
            checkbox.setOpaque(true);
            checkbox.setBorder(new EmptyBorder(2, 4, 2, 4));

            checkbox.addActionListener(e -> {
                if (checkbox.isSelected()) {
                    addValue(value);
                } else {
                    removeValue(value);
                }
            });

            checkboxes.add(checkbox);
            checkboxPanel.add(checkbox);
        }

        checkboxPanel.revalidate();
        checkboxPanel.repaint();
    }

    private void filterCheckboxes() {
        String filter = searchField.getText().toLowerCase().trim();

        for (JCheckBox checkbox : checkboxes) {
            boolean matches = filter.isEmpty() || 
                             checkbox.getText().toLowerCase().contains(filter);
            checkbox.setVisible(matches);
        }

        checkboxPanel.revalidate();
        checkboxPanel.repaint();
    }

    private void addValue(String value) {
        if (value != null && !value.trim().isEmpty()) {
            selectedValues.add(value.trim());
            updateTagDisplay();
            firePropertyChange("selectedValues", null, getValues());
            fireChangeEvent();
        }
    }

    private void removeValue(String value) {
        selectedValues.remove(value);
        updateTagDisplay();
        firePropertyChange("selectedValues", null, getValues());
        fireChangeEvent();

        // Update checkbox if visible
        for (JCheckBox checkbox : checkboxes) {
            if (checkbox.getText().equals(value)) {
                checkbox.setSelected(false);
                break;
            }
        }
    }

    private void updateTagDisplay() {
        tagPanel.removeAll();

        for (String value : selectedValues.stream().sorted().collect(Collectors.toList())) {
            JPanel tag = createTag(value);
            tagPanel.add(tag);
        }

        tagPanel.revalidate();
        tagPanel.repaint();
    }

    private JPanel createTag(String value) {
        JPanel tag = new JPanel(new BorderLayout(2, 0));
        tag.setBorder(new CompoundBorder(
            new LineBorder(Color.LIGHT_GRAY, 1),
            new EmptyBorder(1, 4, 1, 2)
        ));
        tag.setBackground(new Color(230, 240, 255));

        JLabel label = new JLabel(value);
        label.setFont(label.getFont().deriveFont(11f));
        tag.add(label, BorderLayout.CENTER);

        JButton removeButton = new JButton("×");
        removeButton.setPreferredSize(new Dimension(16, 16));
        removeButton.setMargin(new Insets(0, 0, 0, 0));
        removeButton.setFont(removeButton.getFont().deriveFont(10f));
        removeButton.setForeground(Color.GRAY);
        removeButton.setBorderPainted(false);
        removeButton.setContentAreaFilled(false);
        removeButton.setFocusable(false);
        removeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        removeButton.addActionListener(e -> removeValue(value));
        tag.add(removeButton, BorderLayout.EAST);

        return tag;
    }

    // Public API methods
    public List<String> getValues() {
        return new ArrayList<>(selectedValues);
    }

    public void setValues(List<String> values) {
        selectedValues.clear();
        if (values != null) {
            for (String value : values) {
                if (value != null && !value.trim().isEmpty()) {
                    selectedValues.add(value.trim());
                }
            }
        }
        updateTagDisplay();
        fireChangeEvent();
    }

    public void clearValues() {
        selectedValues.clear();
        updateTagDisplay();
        fireChangeEvent();
    }

    public boolean hasValues() {
        return !selectedValues.isEmpty();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dropdownButton.setEnabled(enabled);
        tagPanel.setEnabled(enabled);
    }

    /**
     * Adds a change listener that is notified when the selected values change.
     */
    public void addChangeListener(TagsChangedListener listener) {
        listenerList.add(TagsChangedListener.class, listener);
    }

    /**
     * Removes a change listener.
     */
    public void removeChangeListener(TagsChangedListener listener) {
        listenerList.remove(TagsChangedListener.class, listener);
    }

    /**
     * Fires change events to all registered listeners.
     */
    private void fireChangeEvent() {
        for (TagsChangedListener listener : listenerList.getListeners(TagsChangedListener.class)) {
            listener.tagsChanged(new ActionEvent(this, -1, Str.Empty));
        }
    }
}