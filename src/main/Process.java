package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Process implements Serializable {
    private static int nextId = 1;
    private int id;
    private String state;
    private int programCounter;
    private List<String> instructions;
    private int startIndex;
    private int endIndex;
    private int requiredMemory;
    private int time;

    public Process(int time) {
        this.id = nextId++;
        this.state = "New";
        this.programCounter = 0;
        this.instructions = new ArrayList<>();
        this.startIndex = 0;
        this.endIndex = 0;
        this.requiredMemory = 0;
        this.time = time;
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

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public boolean isFinished() {
        return programCounter >= instructions.size();
    }

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

    public void setMemoryBoundaries(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Process ID: ").append(id).append("\n");
        sb.append("State: ").append(state).append("\n");
        sb.append("Program Counter: ").append(programCounter).append("\n");
        sb.append("Instructions: ").append(instructions).append("\n");
        sb.append("Start Index: ").append(startIndex).append("\n");
        sb.append("End Index: ").append(endIndex).append("\n");
        sb.append("Required Memory: ").append(requiredMemory).append("\n");
        sb.append("Time: ").append(time).append("\n");
        return sb.toString();
    }
}
