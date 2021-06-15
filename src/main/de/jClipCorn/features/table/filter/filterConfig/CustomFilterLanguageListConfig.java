package de.jClipCorn.features.table.filter.filterConfig;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.gui.guiComponents.language.LanguageChooser;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to0;

import javax.swing.*;
import java.util.Random;

public class CustomFilterLanguageListConfig extends CustomFilterConfig {

	private final Func0to1<CCDBLanguageList> valueGetter;
	private final Func1to0<CCDBLanguageList> valueSetter;

	public CustomFilterLanguageListConfig(CCMovieList ml, Func0to1<CCDBLanguageList> get, Func1to0<CCDBLanguageList> set) {
		super(ml);

		valueGetter = get;
		valueSetter = set;
	}
	
	@Override
	public JComponent getComponent(Runnable onChange) {
		LanguageChooser chsr = new LanguageChooser(valueGetter.invoke());

		chsr.addChangeListener(e -> { valueSetter.invoke(chsr.getValue()); onChange.run(); });
		
		return chsr;
	}

	@Override
	public void setValueRandom(Random r) {
		valueSetter.invoke(CCDBLanguageList.randomValue(r));
	}
}
