package uptc.edu.co.view.controllers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import uptc.edu.co.i18n.MessageService;

public abstract class AbstractViewController {
    protected final JFrame parent;
    protected final MessageService messages;

    protected AbstractViewController(JFrame parent, MessageService messages) {
        this.parent = parent;
        this.messages = messages;
    }

    protected JPanel createFormPanel() {
        return new JPanel(new GridLayout(0, 2, 8, 8));
    }

    protected void addFormRow(JPanel form, String key, Component field) {
        form.add(new JLabel(messages.get(key)));
        form.add(field);
    }

    protected boolean confirmForm(JPanel form, String titleKey) {
        int result = JOptionPane.showConfirmDialog(parent, form, messages.get(titleKey), JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        return result == JOptionPane.OK_OPTION;
    }

    protected String readInput(String key) {
        return JOptionPane.showInputDialog(parent, messages.get(key));
    }

    protected File chooseExportFile(String defaultFileName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultFileName));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return chooser.getSelectedFile();
    }

    protected void showMessage(String message) {
        JOptionPane.showMessageDialog(parent, message);
    }

    protected JFrame createListFrame(String titleKey) {
        JFrame frame = new JFrame(messages.get(titleKey));
        frame.setSize(760, 420);
        frame.setLocationRelativeTo(parent);
        return frame;
    }

    protected void showListWindow(JFrame frame, JTable table, Component south) {
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        if (south != null) {
            frame.add(south, BorderLayout.SOUTH);
        }
        frame.setVisible(true);
    }

    protected JTable createAlignedTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        for (int index = 0; index < table.getColumnCount(); index++) {
            table.getColumnModel().getColumn(index).setCellRenderer(leftRenderer);
        }
        return table;
    }

    protected DefaultTableModel nonEditableModel(Object[] columns) {
        return new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    protected String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    protected boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    protected int calculatePages(int size, int pageSize) {
        return size == 0 ? 1 : (size + pageSize - 1) / pageSize;
    }
}
