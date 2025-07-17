import SchedulerLogic.SchedulerLogic;

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

    private int pidCounter = 1;

    public SchedulerGUI() {
        setTitle("CPU Scheduling Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLayout(new BorderLayout());

        // Top controls
        JPanel topPanel = new JPanel();

        algorithmBox = new JComboBox<>(new String[]{"FIFO", "SJF", "SRTF", "Round Robin", "MLFQ"});
        quantumField = new JTextField("2", 5);
        baseQuantumField = new JTextField("2", 5);
        contextSwitchField = new JTextField("0", 5);
        JButton runButton = createStyledButton("Run");
        JButton addProcessButton = createStyledButton("Add Process");
        JButton randomButton = createStyledButton("Randomizer");
        JButton resetButton = createStyledButton("Reset");

        topPanel.add(new JLabel("Algorithm:")); topPanel.add(algorithmBox);
        topPanel.add(new JLabel("Quantum:")); topPanel.add(quantumField);
        topPanel.add(new JLabel("MLFQ Base:")); topPanel.add(baseQuantumField);
        topPanel.add(new JLabel("Context Switch:")); topPanel.add(contextSwitchField);
        topPanel.add(addProcessButton);
        topPanel.add(randomButton);
        topPanel.add(runButton);
        topPanel.add(resetButton);

        add(topPanel, BorderLayout.NORTH);

        // Process table
        tableModel = new DefaultTableModel(new Object[]{"PID", "Arrival", "Burst"}, 0);
        processTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(processTable);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScroll, BorderLayout.CENTER);

        // Result 
        JPanel southPanel = new JPanel(new BorderLayout());

        // Gantt Chart Panel Container 
        animationPanelContainer = new JPanel(new BorderLayout());
        animationPanelContainer.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        animationPanelContainer.setPreferredSize(new Dimension(1000, 250));
        southPanel.add(animationPanelContainer, BorderLayout.CENTER);

        // Result Area
        resultArea = new JTextArea(6, 80);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createTitledBorder("Results"));
        southPanel.add(resultArea, BorderLayout.SOUTH);

        JPanel mainCenterPanel = new JPanel();
        mainCenterPanel.setLayout(new BorderLayout());
        mainCenterPanel.add(centerPanel, BorderLayout.NORTH);
        mainCenterPanel.add(southPanel, BorderLayout.CENTER);

        add(mainCenterPanel, BorderLayout.CENTER);


        // Listeners
        runButton.addActionListener(e -> runScheduler());
        randomButton.addActionListener(e -> generateRandomProcesses());
        addProcessButton.addActionListener(e -> showAddProcessDialog());
        resetButton.addActionListener(e -> resetAll());

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBackground(new Color(230, 230, 230));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 35));
        return button;
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
            case "FIFO": result = SchedulerLogic.fifo(processes); break;
            case "SJF": result = SchedulerLogic.sjf(processes); break;
            case "SRTF": result = SchedulerLogic.srtf(processes); break;
            case "Round Robin": result = SchedulerLogic.roundRobin(processes, quantum); break;
            case "MLFQ": result = SchedulerLogic.mlfq(processes, baseQuantum); break;
        }

        displayResults(result);
        animateGanttChart();
    }

    private void displayResults(List<Process> processes) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-5s %-12s %-10s %-15s %-15s %-15s\n",
                "PID", "Arrival", "Burst", "Completion", "Turnaround", "Response"));

        for (Process p : processes) {
            sb.append(String.format("%-5s %-12d %-10d %-15d %-15d %-15d\n",
                    p.pid, p.arrivalTime, p.burstTime, p.completionTime, p.turnaroundTime, p.responseTime));
        }

        double avgTAT = SchedulerLogic.average(processes, "TAT");
        double avgRT = SchedulerLogic.average(processes, "RT");

        sb.append("\nAverage Turnaround Time: " + String.format("%.2f", avgTAT));
        sb.append("\nAverage Response Time: " + String.format("%.2f", avgRT));

        resultArea.setText(sb.toString());
    }

    private void animateGanttChart() {
        animationPanelContainer.removeAll();
        ganttChartPanel = new GanttChartPanel(SchedulerLogic.executionLog);
        animationPanelContainer.add(new JScrollPane(ganttChartPanel), BorderLayout.CENTER);
        ganttChartPanel.startAnimation();
        animationPanelContainer.revalidate();
        animationPanelContainer.repaint();
    }

    private void generateRandomProcesses() {
        try {
            String inputCount = JOptionPane.showInputDialog(this, "Number of processes:");
            int count = Integer.parseInt(inputCount);
            Random rand = new Random();
            tableModel.setRowCount(0);
            pidCounter = 1;

            for (int i = 0; i < count; i++) {
                String pid = "P" + pidCounter++;
                int at = rand.nextInt(10);
                int bt = rand.nextInt(9) + 1;
                tableModel.addRow(new Object[]{pid, at, bt});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }

    private void showAddProcessDialog() {
        JTextField atField = new JTextField(5);
        JTextField btField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Arrival Time:"));
        panel.add(atField);
        panel.add(new JLabel("Burst Time:"));
        panel.add(btField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Process",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int at = Integer.parseInt(atField.getText());
                int bt = Integer.parseInt(btField.getText());
                String pid = "P" + pidCounter++;
                tableModel.addRow(new Object[]{pid, at, bt});
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers.");
            }
        }
    }

    private void resetAll() {
        tableModel.setRowCount(0);
        resultArea.setText("");
        animationPanelContainer.removeAll();
        animationPanelContainer.repaint();
        pidCounter = 1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SchedulerGUI::new);
    }
}
