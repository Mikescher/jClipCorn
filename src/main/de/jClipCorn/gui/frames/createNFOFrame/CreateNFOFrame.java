package de.jClipCorn.gui.frames.createNFOFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.nfo.NFOEntry;
import de.jClipCorn.features.nfo.NFOGenerator;
import de.jClipCorn.features.nfo.NFOStatus;
import de.jClipCorn.gui.guiComponents.JCCFrame;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("nls")
public class CreateNFOFrame extends JCCFrame {

    private List<NFOEntry> _entries = null;
    private Thread activeThread = null;

    public CreateNFOFrame(Component owner, CCMovieList ml)
    {
        super(ml);

        initComponents();
        postInit();

        setLocationRelativeTo(owner);
        updateUI();
    }

    private void postInit()
    {
        // Nothing special needed
    }

    private void updateUI() {
        btnGenerate.setEnabled(activeThread == null);

        var hasChanges = _entries != null && CCStreams.iterate(_entries).any(p -> p.getStatus() !=  NFOStatus.UNCHANGED && p.getStatus() !=  NFOStatus.ERROR);

        btnApply.setEnabled(activeThread == null && hasChanges);

        if (_entries != null) {
            int create = CCStreams.iterate(_entries).count(p ->    p.getStatus() ==  NFOStatus.CREATE);
            int changed = CCStreams.iterate(_entries).count(p ->   p.getStatus() ==  NFOStatus.CHANGED);
            int delete = CCStreams.iterate(_entries).count(p ->    p.getStatus() ==  NFOStatus.DELETE);
            int unchanged = CCStreams.iterate(_entries).count(p -> p.getStatus() ==  NFOStatus.UNCHANGED);
            int error = CCStreams.iterate(_entries).count(p ->     p.getStatus() ==  NFOStatus.ERROR);

            lblSummary.setText(LocaleBundle.getFormattedString(
                "CreateNFOFrame.summary",
                create, changed, delete, unchanged, error, _entries.size()));
        } else {
            lblSummary.setText(" ");
        }
    }

    private void onGenerate(ActionEvent ae) {
        if (activeThread != null) {
            updateUI();
            return;
        }

        activeThread = new Thread(() -> {
            try {
                SwingUtils.invokeLater(() -> {
                    MainFrame.getInstance().beginBlockingIntermediate();
                    progressBar.setValue(0);
                });

                _entries = NFOGenerator.generateEntries(movielist, (current, max, message) -> {
                    SwingUtils.invokeLater(() -> {
                        progressBar.setMaximum(max);
                        progressBar.setValue(current);
                        lblProgress.setText(message);
                    });
                });

                SwingUtils.invokeLater(() -> {
                    tableMain.setData(_entries);
                    tableMain.autoResize();
                });

            } catch (Exception e) {
                SwingUtils.invokeLater(() -> DialogHelper.showDispatchError(this, "Error", e.toString()));
            } finally {
                activeThread = null;
                SwingUtils.invokeLater(() -> {
                    progressBar.setValue(0);
                    lblProgress.setText(" ");
                    MainFrame.getInstance().endBlockingIntermediate();
                    updateUI();
                });
            }
        });
        activeThread.start();
        updateUI();
    }

    private void onApply(ActionEvent ae) {
        if (activeThread != null || _entries == null) {
            updateUI();
            return;
        }

        var entries = _entries;

        activeThread = new Thread(() -> {
            try {
                SwingUtils.invokeLater(() -> {
                    MainFrame.getInstance().beginBlockingIntermediate();
                    progressBar.setMaximum(entries.size());
                    progressBar.setValue(0);
                });

                int count = 0;
                int errors = 0;
                for (NFOEntry entry : entries) {
                    if (entry.getStatus() == NFOStatus.UNCHANGED) {
                        count++;
                        continue;
                    }
                    if (entry.getStatus() == NFOStatus.ERROR) {
                        count++;
                        continue;
                    }

                    try {
                        NFOGenerator.applyEntry(entry);
                    } catch (IOException e) {
                        errors++;
                    }

                    final int c = count;
                    SwingUtils.invokeLater(() -> {
                        progressBar.setValue(c);
                        lblProgress.setText(c + " / " + entries.size());
                    });
                    count++;
                }

                final int finalErrors = errors;
                SwingUtils.invokeLater(() -> {
                    if (finalErrors > 0) {
                        DialogHelper.showDispatchInformation(this,
                                LocaleBundle.getString("CreateNFOFrame.done_with_errors_caption"),
                                LocaleBundle.getFormattedString("CreateNFOFrame.done_with_errors", finalErrors));
                    } else {
                        DialogHelper.showDispatchLocalInformation(this, "CreateNFOFrame.done");
                    }
                });

                // Regenerate to update status
                _entries = NFOGenerator.generateEntries(movielist);

                SwingUtils.invokeLater(() -> {
                    tableMain.setData(_entries);
                    tableMain.autoResize();
                });

            } catch (Exception e) {
                SwingUtils.invokeLater(() -> DialogHelper.showDispatchError(this, "Error", e.toString()));
            } finally {
                activeThread = null;
                SwingUtils.invokeLater(() -> {
                    progressBar.setValue(0);
                    lblProgress.setText(" ");
                    MainFrame.getInstance().endBlockingIntermediate();
                    updateUI();
                });
            }
        });
        activeThread.start();
        updateUI();
    }

    private void onClose(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        btnGenerate = new JButton();
        lblSummary = new JLabel();
        tableMain = new NFOEntryTable(this);
        btnApply = new JButton();
        progressBar = new JProgressBar();
        lblProgress = new JLabel();

        //======== this ========
        setTitle(LocaleBundle.getString("CreateNFOFrame.title")); //$NON-NLS-1$
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        var contentPane = getContentPane();
        contentPane.setLayout(new FormLayout(
            "$ugap, default, $lcgap, default:grow, $lcgap, [150dlu,default], $ugap", //$NON-NLS-1$
            "$ugap, default, $lgap, default:grow, $lgap, default, $lgap")); //$NON-NLS-1$

        //---- btnGenerate ----
        btnGenerate.setText(LocaleBundle.getString("CreateNFOFrame.btnGenerate")); //$NON-NLS-1$
        btnGenerate.setFont(btnGenerate.getFont().deriveFont(btnGenerate.getFont().getStyle() | Font.BOLD));
        btnGenerate.addActionListener(e -> onGenerate(e));
        contentPane.add(btnGenerate, CC.xy(2, 2));

        //---- lblSummary ----
        lblSummary.setText(" "); //$NON-NLS-1$
        contentPane.add(lblSummary, CC.xywh(4, 2, 3, 1));
        contentPane.add(tableMain, CC.xywh(2, 4, 5, 1, CC.FILL, CC.FILL));

        //---- btnApply ----
        btnApply.setText(LocaleBundle.getString("CreateNFOFrame.btnApply")); //$NON-NLS-1$
        btnApply.setFont(btnApply.getFont().deriveFont(btnApply.getFont().getStyle() | Font.BOLD));
        btnApply.setEnabled(false);
        btnApply.addActionListener(e -> onApply(e));
        contentPane.add(btnApply, CC.xy(2, 6));
        contentPane.add(progressBar, CC.xy(4, 6));

        //---- lblProgress ----
        lblProgress.setText(" "); //$NON-NLS-1$
        contentPane.add(lblProgress, CC.xy(6, 6));
        setSize(1000, 687);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JButton btnGenerate;
    private JLabel lblSummary;
    private NFOEntryTable tableMain;
    private JButton btnApply;
    private JProgressBar progressBar;
    private JLabel lblProgress;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
