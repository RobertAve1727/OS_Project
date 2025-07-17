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
    

}
