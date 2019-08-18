package de.jClipCorn.util.listener;

public interface DoubleProgressCallbackListener
{
	DoubleProgressCallbackListener EMPTY = new DoubleProgressCallbackListener() {
		@Override public void setValueBoth(int valueRoot, int valueSub, String txtRoot, String txtSub) { /**/ }
		@Override public void setValueSub(int valueSub, String txtSub) { /**/ }
		@Override public void setMaxAndResetValueBoth(int maxRoot, int maxSub) { /**/ }
		@Override public void setMaxAndResetValueSub(int maxSub) { /**/ }
		@Override public void stepRootAndResetSub(String msgRoot, int maxSub) { /**/ }
		@Override public void setSubMax(int maxSub) { /**/ }
		@Override public void reset() { /**/ }
		@Override public void stepSub(String msgSub) { /**/ }
	};

	void setValueBoth(int valueRoot, int valueSub, String txtRoot, String txtSub);
	void setValueSub(int valueSub, String txtSub);
	
	void setMaxAndResetValueBoth(int maxRoot, int maxSub);
	void setMaxAndResetValueSub(int maxSub);

	void stepRootAndResetSub(String msgRoot, int maxSub);

	void setSubMax(int maxSub);

	void reset();

	void stepSub(String msgSub);
}
