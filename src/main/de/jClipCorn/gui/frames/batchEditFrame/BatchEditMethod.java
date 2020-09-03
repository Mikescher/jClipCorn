package de.jClipCorn.gui.frames.batchEditFrame;

import de.jClipCorn.gui.frames.genericTextDialog.GenericTextDialog;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.lambda.Func3to0WithException;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;

public class BatchEditMethod<TParam>
{
	private final Func3to0WithException<BatchEditEpisodeData, TParam, Tuple<Integer, List<BatchEditEpisodeData>>> _handler;

	public BatchEditMethod(Func3to0WithException<BatchEditEpisodeData, TParam, Tuple<Integer, List<BatchEditEpisodeData>>> handler)
	{
		_handler = handler;
	}

	public void run(BatchEditFrame f, TParam param)
	{
		StringBuilder err = new StringBuilder();
		var alldata = f.data;

		var idx = f.lsEpisodes.getSelectedIndex();
		f.lsEpisodes.setSelectedIndex(-1);
		f.batchProgress.setValue(0);
		f.batchProgress.setMaximum(alldata.size()+1);
		f.setPanelEnabled(f.pnlRoot, false);

		new Thread(()->
		{
			int index = -1;
			for (BatchEditEpisodeData epdata : alldata)
			{
				try
				{
					index++;

					final int progress = index;
					SwingUtils.invokeLater(() -> f.batchProgress.setValue(progress));

					_handler.invoke(epdata, param, Tuple.Create(index, alldata));
				}
				catch (Exception e) {
					err.append("[").append(epdata.episodeNumber).append("] ").append(epdata.title).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					err.append(ExceptionUtils.getMessage(e));
					err.append("\n\n"); //$NON-NLS-1$
				}
			}

			SwingUtils.invokeLater(() ->
			{
				f.updateList();

				if (idx >= 0) f.lsEpisodes.setSelectedIndex(idx);

				if (!err.toString().isEmpty()) GenericTextDialog.showText(f, f.getTitle(), err.toString(), true);

				f.batchProgress.setValue(0);
				f.batchProgress.setMaximum(1);
				f.setPanelEnabled(f.pnlRoot, true);
			});

		}, "BATCHEDIT_ACTION").start(); //$NON-NLS-1$
	}
}
