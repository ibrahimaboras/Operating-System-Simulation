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
        printQueue(blockQueue);

    }

    public void addToBlockedQueue(Process process) {
        this.blockQueue.add(process);
        printQueue(blockQueue);

    }

    public void addToOutput(Process process) {
        this.blockedOutput.add(process);
        printQueue(blockQueue);

    }

    public void addToInput(Process process) {
        this.blockedInput.add(process);
        printQueue(blockQueue);

    }

    public void addToFile(Process process) {
        this.blockedFile.add(process);
        printQueue(blockQueue);

    }

    public void removeFromOutput() {
        Process polled = this.blockedOutput.poll();
        this.blockQueue.remove(polled);
        polled.setState("Ready");
        this.readyQueue.add(polled);
        printQueue(blockQueue);
    }

    public void removeFromInput() {
        Process polled = this.blockedInput.poll();
        this.blockQueue.remove(polled);
        polled.setState("Ready");
        this.readyQueue.add(polled);
        printQueue(blockQueue);

    }

    public void removeFromFile() {
        Process polled = this.blockedFile.poll();
        this.blockQueue.remove(polled);
        polled.setState("Ready");
        this.readyQueue.add(polled);
        printQueue(blockQueue);

    }

    public void printQueues() {
        System.out.println("Ready Queue:");
        printQueue(readyQueue);

        System.out.println("Blocked Queue:");
        printQueue(blockQueue);

        System.out.println("Blocked Output:");
        printQueue(blockedOutput);

        System.out.println("Blocked Input:");
        printQueue(blockedInput);

        System.out.println("Blocked File:");
        printQueue(blockedFile);
    }

    private void printQueue(Queue<Process> queue) {
        for (Process process : queue) {
            System.out.println(process);
        }
        System.out.println();
    }

    public void executeProcesses(Memory mem, Interpreter interpreter) throws Exception {
        while (!readyQueue.isEmpty()) {
            currentProcess = readyQueue.poll();
            printQueue(blockQueue);
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


            for (int i = 0; i < timeSlice && !(currentProcess.getState().equals("Blocked")); i++) {
                // mem.printMemory();
                ArrayList<Process> processes = mem.loadData(i); 
                
                // if(!processes.isEmpty()){
                //     for(Process pro : processes){
                //         if(pro.getTime() == this.time){
                //            // pro.setState("Running");
                //             readyQueue.add(pro);
                //         }
                //         printQueue(blockQueue);

                //     }
                // }
                    if(time == 2){
                        System.out.println();
                    }
                if (currentProcess.getProgramCounter() < currentProcess.getInstructions().size()) {
                    System.out.println("\nTime Slice: " + time);
                    if(time == 27){
                        System.out.println();
                    }
                    interpreter.interpret(this, mem, currentProcess);

                    time++;
                } else {
                    time++;
                    break;
                }

                if(!processes.isEmpty()){
                    for(Process pro : processes){
                        if(pro.getTime() == this.time){
                           // pro.setState("Running");
                            readyQueue.add(pro);
                        }
                        printQueue(blockQueue);

                    }
                }
                mem.printMemory();
                System.out.println("\n\n");
                printQueues();
                mem.printDiskContents();
            }
            if (currentProcess.getProgramCounter() < currentProcess.getInstructions().size()) {
                if(!(currentProcess.getState().equals("Blocked"))){
                    currentProcess.setState("Ready");
                    mem.setProcessState(currentProcess.getId(), "Ready");
                    readyQueue.add(currentProcess);
                }
                printQueue(blockQueue);

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
