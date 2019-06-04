package de.jClipCorn.util.listener;

public interface DoubleProgressCallbackListener
{
	void setValueBoth(int valueRoot, int valueSub, String txtRoot, String txtSub);
	void setValueSub(int valueSub, String txtSub);
	
	void setMaxAndResetValueBoth(int maxRoot, int maxSub);
	void setMaxAndResetValueSub(int maxSub);

	void stepRootAndResetSub(String msgRoot, int maxSub);

	void setSubMax(int maxSub);

	void reset();

	void stepSub(String msgSub);
}
