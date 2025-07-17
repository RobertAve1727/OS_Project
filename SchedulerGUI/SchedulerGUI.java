import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SchedulerGUI extends JFrame {
    private JComboBox<String> algorithmBox;
    private JTextField quantumField, baseQuantumField, contextSwitchField;
    private JTextArea resultArea;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private GanttChartPanel ganttChartPanel;
    private JPanel animationPanelContainer;

    public SchedulerGUI() {
        setTitle("CPU Scheduling Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        // Top controls
        JPanel topPanel = new JPanel();

        algorithmBox = new JComboBox<>(new String[]{"FCFS", "SJF", "SRTF", "Round Robin", "MLFQ"});
        quantumField = new JTextField("2", 5);
        baseQuantumField = new JTextField("2", 5);
        contextSwitchField = new JTextField("0", 5);
        JButton runButton = new JButton("Run");
        JButton randomButton = new JButton("Random Generator"); // 🔹 Added

        topPanel.add(new JLabel("Algorithm:")); topPanel.add(algorithmBox);
        topPanel.add(new JLabel("Quantum:")); topPanel.add(quantumField);
        topPanel.add(new JLabel("MLFQ Base:")); topPanel.add(baseQuantumField);
        topPanel.add(new JLabel("Context Switch:")); topPanel.add(contextSwitchField);
        topPanel.add(runButton);
        topPanel.add(randomButton); // 🔹 Added

        add(topPanel, BorderLayout.NORTH);

        // Table for process input
        tableModel = new DefaultTableModel(new Object[]{"PID", "Arrival", "Burst"}, 0);
        processTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(processTable);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);


        // Fill with some default data
        tableModel.addRow(new Object[]{"P1", 0, 5});
        tableModel.addRow(new Object[]{"P2", 1, 3});
        tableModel.addRow(new Object[]{"P3", 2, 8});
        tableModel.addRow(new Object[]{"P4", 3, 6});

        // Result + Gantt Panel Container (south)
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());

        resultArea = new JTextArea(8, 80);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        southPanel.add(new JScrollPane(resultArea), BorderLayout.NORTH);

        animationPanelContainer = new JPanel(new BorderLayout());
        southPanel.add(animationPanelContainer, BorderLayout.CENTER);

        add(southPanel, BorderLayout.SOUTH);

        // 🔹 Action Listeners
        runButton.addActionListener(e -> runScheduler());
        randomButton.addActionListener(e -> generateRandomProcesses()); // 🔹 Added

        setVisible(true);
    }

    private void runScheduler() {
        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String pid = tableModel.getValueAt(i, 0).toString();
            int at = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
            int bt = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
            processes.add(new Process(pid, at, bt));
        }

        String algo = algorithmBox.getSelectedItem().toString();
        int quantum = Integer.parseInt(quantumField.getText());
        int baseQuantum = Integer.parseInt(baseQuantumField.getText());
        int contextSwitch = Integer.parseInt(contextSwitchField.getText());

        SchedulerLogic.contextSwitchDelay = contextSwitch;
        List<Process> result = null;

        switch (algo) {
            case "FCFS":
                result = SchedulerLogic.fifo(processes);
                break;
            case "SJF":
                result = SchedulerLogic.sjf(processes);
                break;
        }

        animateGanttChart();
    }


    private void animateGanttChart() {
        animationPanelContainer.removeAll();

        ganttChartPanel = new GanttChartPanel(new ArrayList<>(SchedulerLogic.executionLog));
        JScrollPane scroll = new JScrollPane(ganttChartPanel);
        animationPanelContainer.add(scroll, BorderLayout.CENTER);

        ganttChartPanel.startAnimation();
        animationPanelContainer.revalidate();
        animationPanelContainer.repaint();
    }

    // 🔹 Added random generator method
    private void generateRandomProcesses() {
        try {
            String inputCount = JOptionPane.showInputDialog(this, "Number of processes:");
            String inputMaxArrival = JOptionPane.showInputDialog(this, "Maximum arrival time:");
            String inputMaxBurst = JOptionPane.showInputDialog(this, "Maximum burst time:");

            int count = Integer.parseInt(inputCount);
            int maxArrival = Integer.parseInt(inputMaxArrival);
            int maxBurst = Integer.parseInt(inputMaxBurst);

            tableModel.setRowCount(0); // Clear table
            Random rand = new Random();

            for (int i = 1; i <= count; i++) {
                String pid = "P" + i;
                int at = rand.nextInt(maxArrival + 1);
                int bt = rand.nextInt(maxBurst) + 1; // minimum burst time is 1
                tableModel.addRow(new Object[]{pid, at, bt});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SchedulerGUI::new);
    }
}
