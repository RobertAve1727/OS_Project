import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

class GanttChartPanel extends JPanel {
    private List<String> log;
    private int currentTick = 0;
    private javax.swing.Timer timer;
    private Set<String> processSet = new LinkedHashSet<>();

    public GanttChartPanel(List<String> log) {
        this.log = log;
        extractProcesses();
        setPreferredSize(new Dimension(Math.max(1000, log.size() * 40), processSet.size() * 60 + 60));
        setBackground(Color.WHITE);
    }

    private void extractProcesses() {
        for (String pid : log) {
            if (!pid.equals("IDLE") && !pid.equals("CS")) {
                processSet.add(pid.replaceAll("\\[.*]", ""));  // clean MLFQ labels like "P1[Q0]"
            }
        }
    }

    public void startAnimation() {
        currentTick = 0;
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new javax.swing.Timer(300, e -> {
            if (currentTick < log.size()) {
                repaint();
                currentTick++;
            } else {
                timer.stop();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int boxWidth = 40;
        int boxHeight = 35;
        int marginLeft = 60;
        int yStart = 40;

        Map<String, Integer> rowIndex = new HashMap<>();
        int row = 0;
        for (String pid : processSet) {
            rowIndex.put(pid, row++);
        }

        for (int i = 0; i < currentTick && i < log.size(); i++) {
            String entry = log.get(i);
            String pid = entry.replaceAll("\\[.*]", ""); // Remove MLFQ annotations
            int x = marginLeft + i * boxWidth;

            if (entry.equals("IDLE") || entry.equals("CS")) {
                int y = yStart + rowIndex.size() * 60;
                g.setColor(entry.equals("IDLE") ? Color.LIGHT_GRAY : Color.ORANGE);
                g.fillRect(x, y, boxWidth, boxHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, boxWidth, boxHeight);
                g.drawString(entry, x + 3, y + 22);
                continue;
            }

            Integer yRow = rowIndex.get(pid);
            if (yRow == null) continue;

            int y = yStart + yRow * 60;
            g.setColor(getColorFor(pid));
            g.fillRect(x, y, boxWidth, boxHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, boxWidth, boxHeight);
            g.drawString(entry, x + 5, y + 22);
        }

        // Draw labels for each row
        int labelX = 10;
        for (Map.Entry<String, Integer> entry : rowIndex.entrySet()) {
            int y = yStart + entry.getValue() * 60 + 20;
            g.setColor(Color.BLACK);
            g.drawString(entry.getKey(), labelX, y);
        }

        // System row label
        if (!processSet.isEmpty()) {
            int y = yStart + processSet.size() * 60 + 20;
            g.drawString("System", labelX, y);
        }

        // Tick timeline
        for (int i = 0; i < currentTick && i < log.size(); i++) {
            int x = marginLeft + i * boxWidth;
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(i), x + 12, 20);
        }
    }

    private Color getColorFor(String pid) {
        int hash = pid.hashCode();
        return new Color((hash >> 16) & 0xFF, (hash >> 8) & 0xFF, hash & 0xFF);
    }
}
