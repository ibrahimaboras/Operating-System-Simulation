package main;
import java.util.*;

import javafx.util.Pair;

public class Process {
    private static int nextId = 1;

    private int id;
    private String state;
    private int programCounter;
    //private Memory memory;
    private List<String> instructions;
    
    private int startIndex;
    private int endIndex;

    private int requiredMemory;

    public Process() {
        this.id = nextId++;
        this.state = "New";
        this.programCounter = 0;
        //this.memory = new Memory();
        this.instructions = new ArrayList<>();
        this.startIndex = 0;
        this.endIndex = 0;
        this.requiredMemory = 0;
    }

    public int getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void incrementProgramCounter() {
        this.programCounter++;
    }

    // public Memory getMemory() {
    //     return memory;
    // }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    // public void setMemoryBoundaries(Pair<Integer, Integer> memoryBoundaries) {
    //     this.memoryBoundaries = memoryBoundaries;
    // }

    // public int getStartBoundary() {
    //     return memoryBoundaries.getKey();
    // }

     

    public boolean isFinished() {
        return programCounter >= instructions.size();
    }

    // public void setStartIndex(int startIndex) {
    //     this.startIndex = startIndex;
    // }

    // public void setEndIndex(int endIndex) {
    //     this.endIndex = endIndex;
    // }

    public int getRequiredMemory() {
        return requiredMemory;
    }

    public void setRequiredMemory(int requiredMemory) {
        this.requiredMemory = requiredMemory;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setMemoryBoundaries(int startIndex2, int endIndex2) {
        this.startIndex = startIndex2;
        this.endIndex = endIndex2;
    }


    
}

//
//class Process {
//    private int processId;
//    private String processState;
//    private int programCounter;
//    private String[] memoryBoundaries;
//
//    public Process(int id) {
//        this.processId = id;
//        this.processState = "READY";
//        this.programCounter = 0;
//        this.memoryBoundaries = new String[40]; // default memory size
//    }
//
//    // Getters
//    public int getProcessId() {
//        return this.processId;
//    }
//
//    public String getProcessState() {
//        return this.processState;
//    }
//
//    public int getProgramCounter() {
//        return this.programCounter;
//    }
//
//    public String[] getMemoryBoundaries() {
//        return this.memoryBoundaries;
//    }
//
//    // Setters
//    public void setProcessId(int processId) {
//        this.processId = processId;
//    }
//
//    public void setProcessState(String processState) {
//        this.processState = processState;
//    }
//
//    public void setProgramCounter(int programCounter) {
//        this.programCounter = programCounter;
//    }
//
//    public void setMemoryBoundaries(String[] memoryBoundaries) {
//        this.memoryBoundaries = memoryBoundaries;
//    }
//}
//
