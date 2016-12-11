package de.jClipCorn.gui.frames.updateMetadataFrame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.online.metadata.Metadataparser;
import de.jClipCorn.online.metadata.OnlineMetadata;
import de.jClipCorn.util.helper.ThreadUtils;

public class UpdateMetadataFrame extends JFrame {
	private static final long serialVersionUID = -2163175118745491143L;
	
	private enum FilterState { ALL, CHANGED }
	
	private final CCMovieList movielist;
	private FilterState selectedFilter;
	
	private UpdateMetadataTable tableMain;
	private JToggleButton btnShowFiltered;
	private JToggleButton btnShowAll;
	private JButton btnStartCollectingData;
	private JProgressBar progressBar;
	private JPanel panel;
	private JButton btnUpdateAllOnlinescore;

	private Thread collThread = null;
	private boolean cancelBackground;
	
	public UpdateMetadataFrame(Component owner, CCMovieList mlist) {
		super();
		setSize(new Dimension(803, 459));
		movielist = mlist;

		initGUI();
		setLocationRelativeTo(owner);
		
		initTable();
	}

	private void initGUI() {
		setTitle(LocaleBundle.getString("TextExportFrame.this.title")); //$NON-NLS-1$
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("21dlu"),
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		btnStartCollectingData = new JButton("Start collecting data");
		btnStartCollectingData.addActionListener(this::QueryOnline);
		getContentPane().add(btnStartCollectingData, "2, 2");
		
		progressBar = new JProgressBar();
		getContentPane().add(progressBar, "4, 2, default, fill");
		
		btnShowAll = new JToggleButton("All");
		btnShowAll.addActionListener(e -> setFiltered(FilterState.ALL, true));
		btnShowAll.setSelected(true);
		getContentPane().add(btnShowAll, "6, 2");
		
		btnShowFiltered = new JToggleButton("Filtered");
		btnShowFiltered.addActionListener(e -> setFiltered(FilterState.CHANGED, true));
		getContentPane().add(btnShowFiltered, "7, 2");
		
		tableMain = new UpdateMetadataTable(this);
		getContentPane().add(tableMain, "2, 4, 6, 1, fill, fill");
		
		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		getContentPane().add(panel, "2, 6, 6, 1, fill, fill");
		
		btnUpdateAllOnlinescore = new JButton("Update all Onlinescore");
		panel.add(btnUpdateAllOnlinescore);
	}

	private void setFiltered(FilterState state, boolean triggerUpdate) {
		boolean changed = (selectedFilter != state);
		
		selectedFilter = state;

		btnShowAll.setSelected(state == FilterState.ALL);
		btnShowFiltered.setSelected(state == FilterState.CHANGED);
		
		if (triggerUpdate && changed) {
			// table set filter
		}
	}

	private void initTable() {
		tableMain.setData(movielist.iteratorElements().map(p -> new UpdateMetadataTableElement(p)).enumerate());
		tableMain.autoResize();
	}
	
	private void QueryOnline(ActionEvent e) {
		if (collThread != null && collThread.isAlive()) {
			cancelBackground = true;
		} else {
			cancelBackground = false;
			collThread = new Thread(this::Run, "THREAD_UPDATE_METADATA_COLLECT"); //$NON-NLS-1$
			collThread.start();
		}
	}
	
	private void Run() {
		try {
			List<UpdateMetadataTableElement> data =tableMain.getDataCopy();
			
			ThreadUtils.invokeAndWaitSafe(() -> { btnStartCollectingData.setText("Stop collecting data"); });
			ThreadUtils.setProgressbarAndWait(progressBar, 0, 0, data.size()+1);
			
			int i = 1;
			for (UpdateMetadataTableElement elem : data) {
				ThreadUtils.setProgressbarAndWait(progressBar, i);
				i++;
				
				CCOnlineReference ref = elem.Element.getOnlineReference();
				if (! ref.isSet()) continue;
				
				Metadataparser mp = ref.getMetadataParser();
				if (mp == null) continue;
				
				try {
					OnlineMetadata md = mp.getMetadata(ref, false);
					if (md != null) {
						elem.OnlineMeta = md;
						ThreadUtils.invokeAndWaitSafe(() -> { tableMain.changeData(elem, elem); });
					}
					
				} catch (Exception e) {
					CCLog.addDebug(e.toString());
				}
				
				if (cancelBackground) return;
			}
		} finally {
			ThreadUtils.setProgressbarAndWait(progressBar, 0, 0, 1);
			ThreadUtils.invokeAndWaitSafe(() -> { btnStartCollectingData.setText("Start collecting data"); });
			collThread = null;
		}

	}
}
