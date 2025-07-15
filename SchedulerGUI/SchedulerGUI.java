import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SchedulerGUI extends JFrame {
    private final JComboBox<String> algorithmBox;
    private final JTextField quantumField;
    private final JTable processTable;
    private final DefaultTableModel tableModel;
    private final JTextArea resultArea; // Placeholder for future results

    public SchedulerGUI() {
        setTitle("CPU Scheduling Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
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

        resultArea = new JTextArea(6, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        add(resultScroll, BorderLayout.SOUTH);

        runButton.addActionListener(e -> runScheduler());

        setVisible(true);
    }

    private void runScheduler() {

        resultArea.setText("Run button pressed.\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SchedulerGUI::new);
    }
}