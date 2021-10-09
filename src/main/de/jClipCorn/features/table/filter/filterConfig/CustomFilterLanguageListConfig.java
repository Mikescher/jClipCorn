package de.jClipCorn.features.table.filter.filterConfig;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;
import de.jClipCorn.gui.guiComponents.language.LanguageSetChooser;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import java.util.Random;

public class CustomFilterLanguageListConfig extends CustomFilterConfig {

	private final Func0to1<CCDBLanguageSet> valueGetter;
	private final Func1to0<CCDBLanguageSet> valueSetter;

	public CustomFilterLanguageListConfig(CCMovieList ml, Func0to1<CCDBLanguageSet> get, Func1to0<CCDBLanguageSet> set) {
		super(ml);

		valueGetter = get;
		valueSetter = set;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {
		LanguageSetChooser chsr = new LanguageSetChooser(valueGetter.invoke());

		chsr.addLanguageChangedListener(e -> { valueSetter.invoke(chsr.getValue()); onChange.run(); });
		
		return chsr;
	}

	@Override
	public void setValueRandom(Random r) {
		valueSetter.invoke(CCDBLanguageSet.randomValue(r));
	}
}
