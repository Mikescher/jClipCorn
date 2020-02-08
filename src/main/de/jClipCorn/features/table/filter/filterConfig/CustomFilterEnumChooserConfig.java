package de.jClipCorn.features.table.filter.filterConfig;

import de.jClipCorn.gui.guiComponents.enumComboBox.CCEnumComboBox;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import java.util.Random;

public class CustomFilterEnumChooserConfig<T extends ContinoousEnum<T>> extends CustomFilterConfig {

	private final Func0to1<T> valueGetter;
	private final Func1to0<T> valueSetter;
	private final EnumWrapper<T> enumWrapper;
	
	public CustomFilterEnumChooserConfig(Func0to1<T> get, Func1to0<T> set, EnumWrapper<T> wrap) {
		valueGetter = get;
		valueSetter = set;
		enumWrapper = wrap;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {
		T initial = valueGetter.invoke();
		
		CCEnumComboBox<T> cbx = new CCEnumComboBox<>(enumWrapper);

		if (initial != null) cbx.setSelectedEnum(initial);
		
		cbx.addActionListener(e ->
		{
			valueSetter.invoke(cbx.getSelectedEnum());
			onChange.run();
		});
		
		return cbx;
	}

	@Override
	public void setValueRandom(Random r) {
		valueSetter.invoke(enumWrapper.randomValue(r));
	}
}
