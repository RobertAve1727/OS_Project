import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

class GanttChartPanel extends JPanel {
    private List<String> log;
    private int currentTick = 0;
    private Timer timer;
    private Set<String> processSet = new LinkedHashSet<>();
    private final Map<String, Color> pastelColors = new HashMap<>();

    public GanttChartPanel(List<String> log) {
    this.log = log;
    extractProcesses();
    int rows = Math.max(1, processSet.size() + 1); 
    setPreferredSize(new Dimension(Math.max(1000, log.size() * 40), rows * 50)); 
    setBackground(Color.WHITE);
}


    private void extractProcesses() {
        for (String pid : log) {
            if (!pid.equals("IDLE") && !pid.equals("CS")) {
                String cleanPid = pid.replaceAll("\\[.*]", "");
                processSet.add(cleanPid);
                if (!pastelColors.containsKey(cleanPid)) {
                    pastelColors.put(cleanPid, generatePastelColor(cleanPid));
                }
            }
        }
    }

    public void startAnimation() {
        currentTick = 0;
        if (timer != null && timer.isRunning()) timer.stop();

        timer = new Timer(300, e -> {
            if (currentTick < log.size()) {
                repaint();
                currentTick++;
            } else {
                timer.stop();
            }
        });
        timer.start();
    }

    private Color generatePastelColor(String pid) {
        Random rand = new Random(pid.hashCode());
        int r = (rand.nextInt(128) + 127);
        int g = (rand.nextInt(128) + 127);
        int b = (rand.nextInt(128) + 127);
        return new Color(r, g, b);
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
            String pid = entry.replaceAll("\\[.*]", "");
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
            g.setColor(pastelColors.getOrDefault(pid, Color.PINK));
            g.fillRect(x, y, boxWidth, boxHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, boxWidth, boxHeight);
            g.drawString(entry, x + 5, y + 22);
        }

        // Row labels
        int labelX = 10;
        for (Map.Entry<String, Integer> entry : rowIndex.entrySet()) {
            int y = yStart + entry.getValue() * 60 + 20;
            g.setColor(Color.BLACK);
            g.drawString(entry.getKey(), labelX, y);
        }

        int y = yStart + rowIndex.size() * 60 + 20;
        g.drawString("System", labelX, y);

        // Tick labels
        for (int i = 0; i < currentTick && i < log.size(); i++) {
            int x = marginLeft + i * boxWidth;
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(i), x + 12, 20);
        }
    }
}
