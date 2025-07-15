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
        }
    return processes;
    }

}
