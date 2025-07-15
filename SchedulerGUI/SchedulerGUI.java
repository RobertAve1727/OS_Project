import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SchedulerGUI extends JFrame {
    private final JComboBox<String> algorithmBox;
    private final JTextField quantumField;
    private final JTable processTable;
    private final DefaultTableModel tableModel;

    public SchedulerGUI() {
        setTitle("CPU Scheduling Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        algorithmBox = new JComboBox<>(new String[]{"FCFS", "SJF", "SRTF", "Round Robin", "MLFQ"});
        quantumField = new JTextField("2", 5);
        JButton runButton = new JButton("Run");

        topPanel.add(new JLabel("Algorithm:"));
        topPanel.add(algorithmBox);
        topPanel.add(new JLabel("Quantum:"));
        topPanel.add(quantumField);
        topPanel.add(runButton);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"PID", "Arrival", "Burst"}, 0);
        processTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(processTable);

        add(tableScroll, BorderLayout.CENTER);

        tableModel.addRow(new Object[]{"P1", 0, 5});
        tableModel.addRow(new Object[]{"P2", 1, 3});

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SchedulerGUI::new);
    }
}