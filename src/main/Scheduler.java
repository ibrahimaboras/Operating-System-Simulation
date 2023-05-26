package main;

import java.time.temporal.Temporal;
import java.util.*;

import javax.swing.text.html.HTMLDocument.BlockElement;

public class Scheduler {
    private Queue<Process> readyQueue;
    private Queue<Process> blockQueue;
    private Queue<Process> blockedOutput;
    private Queue<Process> blockedInput;
    private Queue<Process> blockedFile;
    private int timeSlice;
    private Process currentProcess;

    private int time;

    public Scheduler(int timeSlice) {
        this.readyQueue = new LinkedList<>();
        this.blockQueue = new LinkedList<>();
        this.blockedOutput = new LinkedList<>();
        this.blockedInput = new LinkedList<>();
        this.blockedFile = new LinkedList<>();
        this.timeSlice = timeSlice;
        this.time = 0;
    }

    public void addProcess(Process process) {
        this.readyQueue.add(process);
    }

    public void addToBlockedQueue(Process process) {
        this.blockQueue.add(process);
    }

    public void addToOutput(Process process) {
        this.blockedOutput.add(process);
    }

    public void addToInput(Process process) {
        this.blockedInput.add(process);
    }

    public void addToFile(Process process) {
        this.blockedFile.add(process);
    }

    public void removeFromOutput() {
        Process polled = this.blockedOutput.poll();
        this.blockQueue.remove(polled);
        this.readyQueue.add(polled);
    }

    public void removeFromInput() {
        Process polled = this.blockedInput.poll();
        this.blockQueue.remove(polled);
        this.readyQueue.add(polled);
    }

    public void removeFromFile() {
        Process polled = this.blockedFile.poll();
        this.blockQueue.remove(polled);
        this.readyQueue.add(polled);
    }

    public void executeProcesses(Memory mem, Interpreter interpreter) throws Exception {
        while (!readyQueue.isEmpty()) {
            currentProcess = readyQueue.poll();
            System.out.println("Executing process: " + currentProcess.getId());

            boolean flag = false;

            // for (Object value : mem.getMemory().values()) {
            //     System.out.println(value);
            // }
            
            for(int i = 0; i < mem.getMemory().size(); i++){
               // System.out.println(mem.getMemory().get(String.valueOf(i)));
                if(mem.getMemory().get(String.valueOf(i)) instanceof Process){
                    Process temp = (Process) (mem.getMemory().get(String.valueOf(i)));

                    if(temp.getId() == currentProcess.getId()){
                        flag = true;
                        break;
                    }
                    i += temp.getRequiredMemory() - 1;
                }
            }

            if(!flag){
                mem.allocateMemory(currentProcess);
                mem.clearDisk(currentProcess);
            }


            for (int i = 0; i < timeSlice; i++) {
                ArrayList<Process> processes = mem.loadData(i); 
                
                if(!processes.isEmpty()){
                    for(Process pro : processes){
                        readyQueue.add(pro);
                    }
                }

                if (currentProcess.getProgramCounter() < currentProcess.getInstructions().size()) {
                    interpreter.interpret(this, mem, currentProcess);

                    time++;
                } else {
                    time++;
                    break;
                }
            }
            if (currentProcess.getProgramCounter() < currentProcess.getInstructions().size()) {
                readyQueue.add(currentProcess);
            } else {
                System.out.println("Finished process: " + currentProcess.getId());
                mem.deallocateMemory(currentProcess);
            }
        }
    }

    public Process getNextProcess() {
        // Using poll instead of remove to avoid an exception in case the queue is empty
        return this.readyQueue.poll();
    }

    public boolean hasProcessesToExecute() {
        return !this.readyQueue.isEmpty();
    }

    public Queue<Process> getReadyQueue() {
        return readyQueue;
    }

    public Queue<Process> getBlockQueue() {
        return blockQueue;
    }

    public Queue<Process> getBlockedOutput() {
        return blockedOutput;
    }

    public Queue<Process> getBlockedInput() {
        return blockedInput;
    }

    public Queue<Process> getBlockedFile() {
        return blockedFile;
    }

    public int getTimeSlice() {
        return timeSlice;
    }

    public Process getCurrentProcess() {
        return currentProcess;
    }


    
}

//
// import java.util.LinkedList;
// import java.util.Queue;
//
// public class Scheduler {
// private Queue<Process> readyQueue;
// private Queue<Process> blockedQueue;
// private final int TIME_SLICE = 2; // Number of instructions each process gets
// to execute per turn
//
// public Scheduler() {
// this.readyQueue = new LinkedList<>();
// this.blockedQueue = new LinkedList<>();
// }
//
// // Adds a process to the ready queue
// public void addProcess(Process process) {
// readyQueue.add(process);
// }
//
// // Adds a process to the blocked queue
// public void blockProcess(Process process) {
// blockedQueue.add(process);
// }
//
// // Moves a process from the blocked queue to the ready queue
// public void unblockProcess(Process process) {
// blockedQueue.remove(process);
// addProcess(process);
// }
//
// public void schedule() {
// while (!readyQueue.isEmpty()) {
// // Get the next process in the queue (but don't remove it yet)
// Process currentProcess = readyQueue.peek();
//
// // Execute the process's next instructions
// for (int i = 0; i < TIME_SLICE; i++) {
// if (currentProcess.getProcessState().equals("READY")) {
// System.out.println("Executing instruction " +
// (currentProcess.getProgramCounter() + 1)
// + " of process " + currentProcess.getProcessId());
//
// // Here we are just incrementing the program counter. In a real
// implementation, you would
// // have the process actually perform the instruction here.
// currentProcess.setProgramCounter(currentProcess.getProgramCounter() + 1);
// }
// // If the process is blocked, move it to the blocked queue
// else if (currentProcess.getProcessState().equals("BLOCKED")) {
// blockProcess(currentProcess);
// break; // Exit the loop early
// }
// }
//
// // After the process has had its turn, remove it from the front of the queue
// readyQueue.poll();
//
// // If the process is not finished and not blocked, add it back to the end of
// the queue
// if (!currentProcess.getProcessState().equals("FINISHED") &&
// !currentProcess.getProcessState().equals("BLOCKED")) {
// readyQueue.add(currentProcess);
// }
// }
// }
//
// // Checks if there are any blocked processes that are ready to be unblocked
// public void checkBlockedProcesses() {
// Queue<Process> tempQueue = new LinkedList<>();
// while (!blockedQueue.isEmpty()) {
// Process process = blockedQueue.poll();
// if (/* condition to check if the process can be unblocked */) {
// unblockProcess(process);
// } else {
// tempQueue.add(process);
// }
// }
// // Replace the original blocked queue with the temporary queue (which still
// contains the processes that couldn't be unblocked)
// blockedQueue = tempQueue;
// }
// }
