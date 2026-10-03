package de.jClipCorn.properties.property;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.guiComponents.jCheckBoxList.JOrderableCheckBoxList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.properties.types.OrderedEnumSet;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class CCOrderedEnumSetProperty<T extends ContinoousEnum<T>> extends CCProperty<OrderedEnumSet<T>> {
	private final EnumWrapper<T> source;

	public CCOrderedEnumSetProperty(CCPropertyCategory cat, CCProperties prop, String ident, Collection<T> standardEnabled, EnumWrapper<T> source) {
		super(cat, getTypeClass(), prop, ident, new OrderedEnumSet<>(source.allDisplayValuesSorted(), standardEnabled));

		this.source = source;
	}

	@SuppressWarnings("unchecked")
	private static <T extends ContinoousEnum<T>> Class<OrderedEnumSet<T>> getTypeClass() {
		return (Class<OrderedEnumSet<T>>)(Class<?>)OrderedEnumSet.class;
	}

	@Override
	public Component getComponent() {
		return new JOrderableCheckBoxList<>(source::asString, standard.Order);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setComponentValueToValue(Component c, OrderedEnumSet<T> val) {
		((JOrderableCheckBoxList<T>)c).setValues(val.Order, val::isEnabled);
	}

	@SuppressWarnings("unchecked")
	@Override
	public OrderedEnumSet<T> getComponentValue(Component c) {
		var cbl = (JOrderableCheckBoxList<T>)c;
		return new OrderedEnumSet<>(cbl.getAllElements(), cbl.getCheckedElements());
	}

	@Override
	public Component getAlternativeComponent() {
		return new JTextField();
	}

	@Override
	public void setAlternativeComponentValueToValue(Component c, OrderedEnumSet<T> val) {
		((JTextField)c).setText(val.serialize());
	}

	@Override
	public OrderedEnumSet<T> getAlternativeComponentValue(Component c) {
		return OrderedEnumSet.parse(source, ((JTextField)c).getText(), standard).orElse(getValue());
	}

	@Override
	public String getLabelRowAlign() {
		return "top"; //$NON-NLS-1$
	}

	@Override
	public OrderedEnumSet<T> getValue() {
		String val = properties.getProperty(identifier);

		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		var result = OrderedEnumSet.parse(source, val, standard);
		if (result.isEmpty()) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorEnum", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		return result.get();
	}

	@Override
	public OrderedEnumSet<T> setValue(OrderedEnumSet<T> val) {
		if (val != null) {
			properties.setProperty(identifier, val.serialize());
		}

		return getValue();
	}

	@Override
	public boolean isValue(OrderedEnumSet<T> val) {
		if (val == null) return false;
		return Str.equals(val.serialize(), getValue().serialize());
	}
}
