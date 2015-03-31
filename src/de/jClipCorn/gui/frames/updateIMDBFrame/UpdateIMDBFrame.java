package de.jClipCorn.gui.frames.updateIMDBFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;

import javax.swing.JTable;
import javax.swing.JProgressBar;
import javax.swing.JButton;

import java.awt.FlowLayout;

import javax.swing.JScrollPane;

import java.awt.Font;
import java.awt.Window.Type;

public class UpdateIMDBFrame extends JFrame {
	private static final long serialVersionUID = 7905808546872514399L;

	private CCMovieList movielist;
	
	private JPanel contentPane;
	private JTable table;
	private JProgressBar progressBar;
	private JPanel panel;
	private JButton btnStart;
	private JButton btnStop;
	private JScrollPane scrollPane;
	private JButton btnUpdate;

	/**
	 * Create the frame.
	 */
	public UpdateIMDBFrame(CCMovieList mlist, Component owner) {
		super();
		
		this.movielist = mlist;
		
		initGUI();
		setLocationRelativeTo(owner);
	}
	
	private void initGUI() {
		setTitle("update IMDb-Scores");
		setIconImage(CachedResourceLoader.getImage(Resources.IMG_FRAME_ICON));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.PREF_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("15dlu"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(panel, "2, 2, fill, fill");
		
		btnStart = new JButton("Start");
		btnStart.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(btnStart);
		
		btnStop = new JButton("Stop");
		btnStop.setEnabled(false);
		panel.add(btnStop);
		
		btnUpdate = new JButton("Update IMDb-Scores");
		btnUpdate.setEnabled(false);
		panel.add(btnUpdate);
		
		progressBar = new JProgressBar();
		contentPane.add(progressBar, "2, 4, default, fill");
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, "2, 6, fill, fill");
		
		table = new JTable();
		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);
	}

}
