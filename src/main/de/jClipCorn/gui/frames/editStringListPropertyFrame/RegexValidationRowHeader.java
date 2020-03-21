package de.jClipCorn.gui.frames.editStringListPropertyFrame;

import de.jClipCorn.properties.property.CCRegexListProperty;
import de.jClipCorn.util.helper.SwingUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

@SuppressWarnings("nls")
public class RegexValidationRowHeader extends JPanel implements CaretListener, DocumentListener, PropertyChangeListener {
	private static final long serialVersionUID = 1473745315486951979L;
	
	public final static float LEFT = 0.0f;
	public final static float CENTER = 0.5f;
	public final static float RIGHT = 1.0f;

	private final static Border OUTER = new MatteBorder(0, 0, 0, 2, Color.GRAY);

	@SuppressWarnings("hiding")
	private final static int HEIGHT = Integer.MAX_VALUE - 1000000;
	
	@SuppressWarnings("hiding")
	private final static int WIDTH = 15;

	// Text component this TextTextLineNumber component is in sync with

	private final JTextComponent component;

	private int borderGap;

	private HashMap<String, FontMetrics> fonts;
	private final CCRegexListProperty target;
	
	/**
	 * Create a line number component for a text component. This minimum display width will be based on 3 digits.
	 *
	 * @param component
	 *            the related text component
	 */
	public RegexValidationRowHeader(JTextComponent component, CCRegexListProperty prop) {
		super();
		
		this.component = component;

		setFont( component.getFont() );

		setBorderGap( 5 );

		component.getDocument().addDocumentListener(this);
		component.addCaretListener( this );
		component.addPropertyChangeListener("font", this);
		
		target = prop;
	}

	/**
	 * Gets the border gap
	 *
	 * @return the border gap in pixels
	 */
	public int getBorderGap() {
		return borderGap;
	}

	/**
	 * The border gap is used in calculating the left and right insets of the border. Default value is 5.
	 *
	 * @param borderGap
	 *            the gap in pixels
	 */
	public void setBorderGap(int borderGap) {
		this.borderGap = borderGap;
		Border inner = new EmptyBorder(0, borderGap, 0, borderGap);
		setBorder(new CompoundBorder(OUTER, inner));
		setPreferredWidth();
	}

	private void setPreferredWidth() {
		int width = WIDTH;
		Insets insets = getInsets();
		int preferredWidth = insets.left + insets.right + width;

		Dimension d = getPreferredSize();
		d.setSize(preferredWidth, HEIGHT);
		setPreferredSize(d);
		setSize(d);
	}

	/**
	 * Draw the line numbers
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		FontMetrics fontMetrics = component.getFontMetrics(component.getFont());
		Insets insets = getInsets();

		Rectangle clip = g.getClipBounds();
		int rowStartOffset = component.viewToModel2D(new Point(0, clip.y));
		int endOffset = component.viewToModel2D(new Point(0, clip.y + clip.height));

		while (rowStartOffset <= endOffset) {
			try {
				String line = component.getText(rowStartOffset, endOffset - rowStartOffset);
				if (line.contains("\n")) line = line.substring(0, line.indexOf("\n"));
				
				if (! line.trim().isEmpty()) {
					if (target.testRegex(line))
						g.setColor(Color.GREEN);
					else
						g.setColor(Color.RED);
					
					int x = insets.left;
					int y = getOffsetY(rowStartOffset, fontMetrics);
					
					g.fillOval(x, y - WIDTH, WIDTH, WIDTH);
				}

				rowStartOffset = Utilities.getRowEnd(component, rowStartOffset) + 1;
			} catch (Exception e) {
				break;
			}
		}
	}

	/*
	 * Determine the Y offset for the current row
	 */
	private int getOffsetY(int rowStartOffset, FontMetrics fontMetrics) throws BadLocationException {
		Rectangle r = component.modelToView2D(rowStartOffset).getBounds();
		int lineHeight = fontMetrics.getHeight();
		int y = r.y + r.height;
		int descent = 0;

		if (r.height == lineHeight) 
		{
			descent = fontMetrics.getDescent();
		} 
		else 
		{
			Element root = component.getDocument().getDefaultRootElement();
			int index = root.getElementIndex(rowStartOffset);
			Element line = root.getElement(index);

			for (int i = 0; i < line.getElementCount(); i++) {
				Element child = line.getElement(i);
				AttributeSet as = child.getAttributes();
				String fontFamily = (String) as.getAttribute(StyleConstants.FontFamily);
				Integer fontSize = (Integer) as.getAttribute(StyleConstants.FontSize);
				String key = fontFamily + fontSize;

				FontMetrics fm = fonts.get(key);

				if (fm == null) {
					Font font = new Font(fontFamily, Font.PLAIN, fontSize);
					fm = component.getFontMetrics(font);
					fonts.put(key, fm);
				}

				descent = Math.max(descent, fm.getDescent());
			}
		}

		return y - descent;
	}

	//
	// Implement CaretListener interface
	//
	@Override
	public void caretUpdate(CaretEvent e) {
		// NOP
	}

	//
	// Implement DocumentListener interface
	//
	@Override
	public void changedUpdate(DocumentEvent e) {
		documentChanged();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		documentChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		documentChanged();
	}

	/*
	 * A document change may affect the number of displayed lines of text. Therefore the lines numbers will also change.
	 */
	private void documentChanged() {
		SwingUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				repaint();
			}
		});
	}

	//
	// Implement PropertyChangeListener interface
	//
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue() instanceof Font) {
			repaint();
		}
	}

}
