package de.jClipCorn.gui.frames.allRatingsFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.JCCDialog;
import de.jClipCorn.gui.localization.LocaleBundle;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Map.Entry;

public class AllRatingsDialog extends JCCDialog {
	private static final long serialVersionUID = 568186116244028190L;

	public AllRatingsDialog(Map<String, Integer> list, Component owner, CCMovieList ml) {
		super(ml);

		initComponents();
		postInit(list);

		setLocationRelativeTo(owner);
	}

	private void postInit(Map<String, Integer> list) {
		int y = 0;
		int count = 0;
		int sum = 0;
		for (Entry<String, Integer> element : list.entrySet()) {
			int cy = 11 + y++ * 25;

			JProgressBar progressBar = new JProgressBar();
			progressBar.setBounds(156, cy, 256, 14);
			progressBar.setMaximum(21);
			progressBar.setValue(element.getValue());
			panel.add(progressBar);

			JLabel lblLand = new JLabel(element.getKey());
			lblLand.setBounds(10, cy, 80, 14);
			panel.add(lblLand);

			JLabel lblRating = new JLabel(element.getValue() + ""); //$NON-NLS-1$
			lblRating.setBounds(100, cy, 46, 14);
			panel.add(lblRating);

			count++;
			sum += element.getValue();
		}
		panel.setPreferredSize(new Dimension(0, 11 + y * 25));

		if (count > 0) {
			lblAverage.setText(((int)((sum / (count * 1d)) * 10)) / 10d + ""); //$NON-NLS-1$
		} else {
			lblAverage.setText("  -  "); //$NON-NLS-1$
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane = new JScrollPane();
        panel = new JPanel();
        pnlBottom = new JPanel();
        lblBottom = new JLabel();
        lblAverage = new JLabel();

        //======== this ========
        setTitle(LocaleBundle.getString("AllRatingsFrame.this.title")); //$NON-NLS-1$
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        var contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$ugap, default:grow, $ugap", //$NON-NLS-1$
            "$ugap, default:grow, $lgap, default, $ugap")); //$NON-NLS-1$

        //======== scrollPane ========
        {
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

            //======== panel ========
            {
                panel.setLayout(null);

                {
                    // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < panel.getComponentCount(); i++) {
                        Rectangle bounds = panel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = panel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    panel.setMinimumSize(preferredSize);
                    panel.setPreferredSize(preferredSize);
                }
            }
            scrollPane.setViewportView(panel);
        }
        contentPane.add(scrollPane, CC.xy(2, 2, CC.FILL, CC.FILL));

        //======== pnlBottom ========
        {
            pnlBottom.setLayout(new FlowLayout(FlowLayout.LEFT));

            //---- lblBottom ----
            lblBottom.setText(LocaleBundle.getString("AllRatingsFrame.lblAberage.text")); //$NON-NLS-1$
            pnlBottom.add(lblBottom);

            //---- lblAverage ----
            lblAverage.setText("???"); //$NON-NLS-1$
            pnlBottom.add(lblAverage);
        }
        contentPane.add(pnlBottom, CC.xy(2, 4, CC.FILL, CC.DEFAULT));
        setSize(450, 300);
        setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane;
    private JPanel panel;
    private JPanel pnlBottom;
    private JLabel lblBottom;
    private JLabel lblAverage;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
