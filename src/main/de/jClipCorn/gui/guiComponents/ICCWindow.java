package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.ICCPropertySource;

public interface ICCWindow extends ICCPropertySource {

	// JCCDialog && JCCFrame

	CCMovieList getMovieList();
	CCProperties ccprops();

	class Dummy {
		public static JCCFrame frame() {
			return new JCCFrame(CCMovieList.createStub());
		}
	}
}
