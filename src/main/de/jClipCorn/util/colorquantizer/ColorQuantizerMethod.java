package de.jClipCorn.util.colorquantizer;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.colorquantizer.distinctselection.DistinctSelectionQuantizer;
import de.jClipCorn.util.colorquantizer.empty.EmptyColorQuantizer;
import de.jClipCorn.util.colorquantizer.octree.OctreeQuantizer;
import de.jClipCorn.util.colorquantizer.smartk8.K8QuantizerWrapper;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.lambda.Func0to1;

public enum ColorQuantizerMethod implements ContinoousEnum<ColorQuantizerMethod> {
	EMPTY(0, EmptyColorQuantizer::new),
	SIMPLE_OCTREE(1, OctreeQuantizer::new),
	HSL_DISTINCT_SELECTION(2, () -> new K8QuantizerWrapper(new DistinctSelectionQuantizer()));

	private final static String[] NAMES = {
			LocaleBundle.getString("ColorQuantizerMethod.EMPTY"),                   //$NON-NLS-1$
			LocaleBundle.getString("ColorQuantizerMethod.SIMPLE_OCTREE"),           //$NON-NLS-1$
			LocaleBundle.getString("ColorQuantizerMethod.HSL_DISTINCT_SELECTION"),  //$NON-NLS-1$
	};

	private final int id;
	private final Func0to1<ColorQuantizer> funcCreate;

	private static EnumWrapper<ColorQuantizerMethod> wrapper = new EnumWrapper<>(EMPTY);

	ColorQuantizerMethod(int val, Func0to1<ColorQuantizer> fcreate) {
		id = val;
		funcCreate = fcreate;
	}

	public static EnumWrapper<ColorQuantizerMethod> getWrapper() {
		return wrapper;
	}

	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	@Override
	public String[] getList() {
		return NAMES;
	}

	@Override
	public ColorQuantizerMethod[] evalues() {
		return ColorQuantizerMethod.values();
	}

	public ColorQuantizer create() {
		return funcCreate.invoke();
	}
}
