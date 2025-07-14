import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SchedulerGUI extends JFrame {
    private JTable processTable;
    private DefaultTableModel tableModel;

    public SchedulerGUI() {
        setTitle("CPU Scheduling Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Process input table (early setup)
        tableModel = new DefaultTableModel(new Object[]{"PID", "Arrival", "Burst"}, 0);
        processTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(processTable);

        // Sample static data (for layout testing)
        tableModel.addRow(new Object[]{"P1", 0, 5});
        tableModel.addRow(new Object[]{"P2", 1, 3});

        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SchedulerGUI::new);
    }
}
