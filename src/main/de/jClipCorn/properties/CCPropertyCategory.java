package de.jClipCorn.properties;

import de.jClipCorn.gui.localization.LocaleBundle;

public class CCPropertyCategory {
	public final String Name;
	public final int Index;
	
	public CCPropertyCategory(int idx, String name) {
		Index = idx;
		Name = name;
	}

	public CCPropertyCategory() {
		Index = -1;
		Name = null;
	}
	
	public boolean isVisble() {
		return Name != null;
	}

	@Override
	public int hashCode() {
		return (Name == null) ? 0 : Name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		CCPropertyCategory other = (CCPropertyCategory) obj;
		if (Name == null) {
			if (other.Name != null)
				return false;
		} else if (!Name.equals(other.Name))
			return false;
		
		return true;
	}

	public String getCaption() {
		return LocaleBundle.getString("Settingsframe.tabbedCpt.Caption_" + Name); //$NON-NLS-1$
	}
}
