import java.util.*;

import javax.swing.JOptionPane;

class Process {
    String pid;
    int arrivalTime, burstTime, remainingTime;
    int completionTime, turnaroundTime, waitingTime, responseTime;
    boolean started = false;

    public Process(String pid, int arrivalTime, int burstTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }
}

public class SchedulerLogic {
    public static List<String> executionLog = new ArrayList<>();
    public static int contextSwitchDelay = 0;

    public static List<Process> fifo(List<Process> processes) {
        executionLog.clear();
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        String prevPid = "";

        for (Process p : processes) {
            while (currentTime < p.arrivalTime) {
                executionLog.add("IDLE");
                currentTime++;
            }

            if (!prevPid.isEmpty() && !prevPid.equals(p.pid)) {
                for (int i = 0; i < contextSwitchDelay; i++) {
                    executionLog.add("CS");
                    currentTime++;
                }
            }

            p.responseTime = currentTime - p.arrivalTime;

            for (int i = 0; i < p.burstTime; i++) {
                executionLog.add(p.pid); 
                currentTime++;
            }

            p.completionTime = currentTime;
            p.turnaroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;

            prevPid = p.pid;
        }
        return processes;
    }

    public static List<Process> sjf(List<Process> processes) {
        executionLog.clear();
        
        List<Process> readyQueue = new ArrayList<>();
        List<Process> completed = new ArrayList<>();
        int currentTime = 0;
        int index = 0;
        String prevPid = "";

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        while (completed.size() < processes.size()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                readyQueue.add(processes.get(index++));
            }

            readyQueue.sort(Comparator.comparingInt(p -> p.burstTime));

            if (!readyQueue.isEmpty()) {
                Process p = readyQueue.remove(0);

                if (!prevPid.isEmpty() && !prevPid.equals(p.pid)) {
                    for (int i = 0; i < contextSwitchDelay; i++) {
                        executionLog.add("CS");
                        currentTime++;
                    }
                }

                p.responseTime = currentTime - p.arrivalTime;
                for (int i = 0; i < p.burstTime; i++) {
                    executionLog.add(p.pid);
                    currentTime++;
                }

                p.completionTime = currentTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
                completed.add(p);
                prevPid = p.pid;
            } else {
                executionLog.add("IDLE");
                currentTime++;
            }
        }
        return completed;
    }

    public static List<Process> srtf(List<Process> processes) {
        executionLog.clear();
        int time = 0;
        int completed = 0;
        String prevPid = "";
        List<Process> result = new ArrayList<>();

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        while (completed < processes.size()) {
            Process shortest = null;
            int minRem = Integer.MAX_VALUE;

            for (Process p : processes) {
                if (p.arrivalTime <= time && p.remainingTime > 0 && p.remainingTime < minRem) {
                    shortest = p;
                    minRem = p.remainingTime;
                }
            }

            if (shortest == null) {
                executionLog.add("IDLE");
                time++;
                continue;
            }

            if (!shortest.pid.equals(prevPid) && !prevPid.isEmpty()) {
                for (int i = 0; i < contextSwitchDelay; i++) {
                    executionLog.add("CS");
                    time++;
                }
            }

            if (!shortest.started) {
                shortest.responseTime = time - shortest.arrivalTime;
                shortest.started = true;
            }

            shortest.remainingTime--;
            executionLog.add(shortest.pid);
            time++;

            if (shortest.remainingTime == 0) {
                shortest.completionTime = time;
                shortest.turnaroundTime = shortest.completionTime - shortest.arrivalTime;
                shortest.waitingTime = shortest.turnaroundTime - shortest.burstTime;
                result.add(shortest);
                completed++;
            }

            prevPid = shortest.pid;
        }

        return result;
    }

    public static List<Process> roundRobin(List<Process> processes, int quantum) {
        executionLog.clear();
        List<Process> queue = new ArrayList<>();  
        List<Process> result = new ArrayList<>();

        int currentTime = 0;
        int completed = 0;
        int index = 0;
        String prevPid = "";

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));  

        while (completed < processes.size()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                queue.add(processes.get(index));
                index++;
            }

            if (!queue.isEmpty()) {
                Process p = queue.remove(0); 

                if (!prevPid.isEmpty() && !prevPid.equals(p.pid)) {
                    for (int i = 0; i < contextSwitchDelay; i++) {
                        executionLog.add("CS");
                        currentTime++;
                    }
                }

                if (!p.started) {
                    p.responseTime = currentTime - p.arrivalTime;
                    p.started = true;
                }

                int execTime = Math.min(quantum, p.remainingTime);
                for (int i = 0; i < execTime; i++) {
                    executionLog.add(p.pid);
                    currentTime++;
                }

                p.remainingTime -= execTime;

                while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                    queue.add(processes.get(index));
                    index++;
                }

                if (p.remainingTime > 0) {
                    queue.add(p);
                } else {
                    p.completionTime = currentTime;
                    p.turnaroundTime = p.completionTime - p.arrivalTime;
                    p.waitingTime = p.turnaroundTime - p.burstTime;
                    result.add(p);
                    completed++;
                }

                prevPid = p.pid;
            } else {
                executionLog.add("IDLE");
                currentTime++;
            }
        }

        return result;
    }

    public static List<Process> mlfq(List<Process> processes, int quantum, int mlfqBase) {
        if (quantum <= 0 || mlfqBase <= 0) {
            JOptionPane.showMessageDialog(null, "Quantum and MLFQ base must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }

        executionLog.clear();

        ArrayList<Process>[] queues = new ArrayList[4];
        for (int i = 0; i < 4; i++) queues[i] = new ArrayList<>();

        List<Process> result = new ArrayList<>();
        List<Process> all = new ArrayList<>(processes);
        all.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int time = 0, completed = 0, index = 0;
        String prevPid = "";

        Map<String, Integer> levelMap = new HashMap<>();
        Map<String, Integer> levelTimeUsed = new HashMap<>();

        for (Process p : processes) {
            levelMap.put(p.pid, 0);
            levelTimeUsed.put(p.pid, 0);
        }

        while (completed < processes.size()) {
            while (index < all.size() && all.get(index).arrivalTime <= time) {
                Process p = all.get(index);
                queues[0].add(p);
                levelMap.put(p.pid, 0);
                levelTimeUsed.put(p.pid, 0);
                index++;
            }

            Process current = null;
            int currentLevel = -1;
            for (int i = 0; i < 4; i++) {
                if (!queues[i].isEmpty()) {
                    current = queues[i].remove(0);
                    currentLevel = i;
                    break;
                }
            }

            if (current == null) {
                executionLog.add("IDLE");
                time++;
                continue;
            }

            if (!prevPid.isEmpty() && !prevPid.equals(current.pid)) {
                for (int i = 0; i < contextSwitchDelay; i++) {
                    executionLog.add("CS");
                    time++;
                }
            }

            if (!current.started) {
                current.responseTime = time - current.arrivalTime;
                current.started = true;
            }

            String pid = current.pid;
            int used = levelTimeUsed.get(pid);
            int remainingAllotment = mlfqBase - used;

            int slice = Math.min(quantum, Math.min(current.remainingTime, remainingAllotment));

            for (int i = 0; i < slice; i++) {
                executionLog.add(pid + "[Q" + currentLevel + "]");
                time++;
            }

            current.remainingTime -= slice;
            levelTimeUsed.put(pid, used + slice);

            while (index < all.size() && all.get(index).arrivalTime <= time) {
                Process p = all.get(index);
                queues[0].add(p);
                levelMap.put(p.pid, 0);
                levelTimeUsed.put(p.pid, 0);
                index++;
            }

            if (current.remainingTime > 0) {
                int newUsed = levelTimeUsed.get(pid);
                if (newUsed >= mlfqBase && currentLevel < 3) {
                    int nextLevel = currentLevel + 1;
                    queues[nextLevel].add(current);
                    levelMap.put(pid, nextLevel);
                    levelTimeUsed.put(pid, 0); // Reset time used at new level
                } else {
                    queues[currentLevel].add(current);
                }
            } else {
                current.completionTime = time;
                current.turnaroundTime = current.completionTime - current.arrivalTime;
                current.waitingTime = current.turnaroundTime - current.burstTime;
                result.add(current);
                completed++;
            }

            prevPid = pid;
        }

        return result;
    }



    public static double average(List<Process> processes, String metric) {
        double total = 0;
        int count = processes.size();

        for (Process p : processes) {
            if (metric.equals("TAT")) {
                total += p.turnaroundTime;
            } else if (metric.equals("RT")) {
                total += p.responseTime;
            }
        }

        return count > 0 ? total / count : 0;
    }
}
