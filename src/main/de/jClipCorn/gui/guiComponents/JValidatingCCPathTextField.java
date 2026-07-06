package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.properties.ICCPropertySource;
import de.jClipCorn.util.adapter.DocumentLambdaAdapter;
import de.jClipCorn.util.filesystem.CCPath;

import javax.swing.*;
import java.awt.*;

/**
 * A {@link JCCPathTextField} that gives live visual feedback about the entered {@link CCPath}:
 * <ul>
 *     <li>red    - the path contains illegal symbols (syntactically invalid)</li>
 *     <li>yellow - the path is valid but does not resolve to an existing directory</li>
 *     <li>normal - the path is empty (= unconfigured) or resolves to an existing directory</li>
 * </ul>
 * The path is resolved against the supplied {@link ICCPropertySource} (usually {@code CCProperties}).
 */
public class JValidatingCCPathTextField extends JCCPathTextField {

	private final ICCPropertySource ccprops;

	public JValidatingCCPathTextField(ICCPropertySource ccprops) {
		this(ccprops, 0);
	}

	public JValidatingCCPathTextField(ICCPropertySource ccprops, int columns) {
		super(columns);
		this.ccprops = ccprops;

		getDocument().addDocumentListener(new DocumentLambdaAdapter(this::updateValidationColor));

		updateValidationColor();
	}

	@Override
	public void setPath(CCPath t) {
		super.setPath(t);
		updateValidationColor();
	}

	@SuppressWarnings("nls")
	private void updateValidationColor() {
		var normal = UIManager.getColor("TextField.background");

		CCPath p = getPath();

		if (p.isEmpty()) {
			setBackground(normal); // empty == unconfigured, allowed
		} else if (CCPath.containsIllegalSymbols(p)) {
			setBackground(Color.RED);
		} else if (!p.toFSPath(ccprops).directoryExists()) {
			setBackground(Color.YELLOW);
		} else {
			setBackground(normal);
		}
	}
}
