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

}
