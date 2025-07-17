import java.util.*;

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
        Queue<Process> queue = new LinkedList<>();
        List<Process> result = new ArrayList<>();
        int currentTime = 0;
        int completed = 0;
        int index = 0;
        String prevPid = "";

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        while (completed < processes.size()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                queue.offer(processes.get(index++));
            }

            if (!queue.isEmpty()) {
                Process p = queue.poll();

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
                    queue.offer(processes.get(index++));
                }

                if (p.remainingTime > 0) {
                    queue.offer(p);
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

    public static List<Process> mlfq(List<Process> processes, int baseQuantum) {
        executionLog.clear();

        // 4 levels of queues
        ArrayList<Process>[] queues = new ArrayList[4];
        for (int i = 0; i < 4; i++) queues[i] = new ArrayList<>();

        List<Process> result = new ArrayList<>();
        List<Process> all = new ArrayList<>(processes);  
        all.sort(Comparator.comparingInt(p -> p.arrivalTime));  

        int[] quantums = { baseQuantum, baseQuantum * 2, baseQuantum * 4, baseQuantum * 8 };
        int time = 0, completed = 0, index = 0;
        String prevPid = "";

        // Array to track levels of each process
        String[] pidList = new String[processes.size()];
        int[] levels = new int[processes.size()];
        for (int i = 0; i < processes.size(); i++) {
            pidList[i] = processes.get(i).pid;
            levels[i] = 0;
        }

        while (completed < processes.size()) {
            // Enqueue newly arrived processes to Q0
            while (index < all.size() && all.get(index).arrivalTime <= time) {
                queues[0].add(all.get(index));
                for (int i = 0; i < pidList.length; i++) {
                    if (pidList[i].equals(all.get(index).pid)) {
                        levels[i] = 0;
                        break;
                    }
                }
                index++;
            }

            // Select next process from highest-priority non-empty queue
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

            // Context switch delay
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

            int q = quantums[currentLevel];
            int execTime = Math.min(q, current.remainingTime);

            // Simulate execution
            for (int i = 0; i < execTime; i++) {
                executionLog.add(current.pid + "[Q" + currentLevel + "]");
                time++;
            }

            current.remainingTime -= execTime;

            // Add new arrivals again during execution
            while (index < all.size() && all.get(index).arrivalTime <= time) {
                queues[0].add(all.get(index));
                for (int i = 0; i < pidList.length; i++) {
                    if (pidList[i].equals(all.get(index).pid)) {
                        levels[i] = 0;
                        break;
                    }
                }
                index++;
            }

            if (current.remainingTime > 0) {
                // Demote to next lower level (up to Q3)
                int nextLevel = Math.min(3, currentLevel + 1);
                queues[nextLevel].add(current);
                for (int i = 0; i < pidList.length; i++) {
                    if (pidList[i].equals(current.pid)) {
                        levels[i] = nextLevel;
                        break;
                    }
                }
            } else {
                current.completionTime = time;
                current.turnaroundTime = current.completionTime - current.arrivalTime;
                current.waitingTime = current.turnaroundTime - current.burstTime;
                result.add(current);
                completed++;
            }

            prevPid = current.pid;
        }

        return result;
    }

    public static double average(List<Process> processes, String metric) {
        return processes.stream().mapToDouble(p -> {
            switch (metric) {
                case "TAT": return p.turnaroundTime;
                case "RT": return p.responseTime;
                default: return 0;
        }}).average().orElse(0);
    }
}
