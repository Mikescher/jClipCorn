package de.jClipCorn.gui.frames.batchEditFrame;

import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.util.lambda.Func2to0WithException;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class BatchEditMethod<TParam>
{
	private final Func2to0WithException<BatchEditEpisodeData, TParam> _handler;

	public BatchEditMethod(Func2to0WithException<BatchEditEpisodeData, TParam> handler)
	{
		_handler = handler;
	}

	public void run(BatchEditFrame f, TParam param)
	{
		var idx = f.lsEpisodes.getSelectedIndex();
		f.lsEpisodes.setSelectedIndex(-1);

		StringBuilder err = new StringBuilder();
		for (BatchEditEpisodeData epdata : f.data) {
			try
			{
				_handler.invoke(epdata, param);
			} catch (Exception e) {
				err.append("[").append(epdata.episodeNumber).append("] ").append(epdata.title).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				err.append(ExceptionUtils.getMessage(e)); //$NON-NLS-1$
				err.append("\n\n"); //$NON-NLS-1$
			}
		}

		f.updateList();

		if (idx >= 0) f.lsEpisodes.setSelectedIndex(idx);

		if (!err.toString().isEmpty()) GenericTextDialog.showText(f, f.getTitle(), err.toString(), true);
	}
}
