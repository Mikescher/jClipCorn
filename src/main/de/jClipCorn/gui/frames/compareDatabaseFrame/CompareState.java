package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.databaseElement.CCMovie;

import java.util.ArrayList;
import java.util.List;

public class CompareState {

	public static class MovieMatch {
		public CCMovie MovieLocal;
		public CCMovie MovieExtern;

		public MovieMatch(CCMovie mloc, CCMovie mext) {
			MovieLocal  = mloc;
			MovieExtern = mext;
		}
	}

	public List<MovieMatch> Movies = new ArrayList<>();

}
