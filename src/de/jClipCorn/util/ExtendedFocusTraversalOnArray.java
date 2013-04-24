package de.jClipCorn.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.List;

import javax.swing.JSpinner;

public class ExtendedFocusTraversalOnArray extends FocusTraversalPolicy {
	private final Component m_Components[];

	public ExtendedFocusTraversalOnArray(Component components[]) {
		m_Components = components;
		
		fixSpinner();
	}
	
	public ExtendedFocusTraversalOnArray(List<Component> components) {
		m_Components = new Component[components.size()];
		
		for (int i = 0; i < components.size(); i++) {
			m_Components[i] = components.get(i);
		}
		
		fixSpinner();
	}
	
	private void fixSpinner() {
		for (int i = 0; i < m_Components.length; i++) {
			if (m_Components[i] instanceof JSpinner) {
				if (((JSpinner)m_Components[i]).getEditor() instanceof JSpinner.DefaultEditor) {
					m_Components[i] = ((JSpinner.DefaultEditor)((JSpinner)m_Components[i]).getEditor()).getTextField();
				}
			}
		}
	}

	private int indexCycle(int index, int delta) {
		int size = m_Components.length;
		int next = (index + delta + size) % size;
		return next;
	}

	private Component cycle(Component currentComponent, int delta) {
		int index = -1;
		loop: for (int i = 0; i < m_Components.length; i++) {
			Component component = m_Components[i];
			for (Component c = currentComponent; c != null; c = c.getParent()) {
				if (component == c) { // Das == sollte stimmen - sagt der GoogleCode ...
					index = i;
					break loop;
				}
			}
		}
		int initialIndex = index;
		while (true) {
			int newIndex = indexCycle(index, delta);
			if (newIndex == initialIndex) {
				break;
			}
			index = newIndex;
			//
			Component component = m_Components[newIndex];
			if (component.isEnabled() && component.isVisible() && component.isFocusable()) {
				return component;
			}
		}
		return currentComponent;
	}

	@Override
	public Component getComponentAfter(Container container, Component component) {
		return cycle(component, 1);
	}

	@Override
	public Component getComponentBefore(Container container, Component component) {
		return cycle(component, -1);
	}

	@Override
	public Component getFirstComponent(Container container) {
		return m_Components[0];
	}

	@Override
	public Component getLastComponent(Container container) {
		return m_Components[m_Components.length - 1];
	}

	@Override
	public Component getDefaultComponent(Container container) {
		return getFirstComponent(container);
	}
}
