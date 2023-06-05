package main;

import javax.lang.model.element.VariableElement;

import org.omg.Messaging.SyncScopeHelper;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.*;

public class Memory implements Serializable{
    private static final int MEMORY_SIZE = 40;
    private HashMap<String, Object> memory;
    private int currentSize;

    public Memory() {
        this.memory = new HashMap<>();
        this.currentSize = 0;
    }

    public boolean allocateMemory(Process process) {
        process.setRequiredMemory(4 + process.getInstructions().size());
        int startIndex = findAvailableMemory(process.getRequiredMemory());
        if (startIndex != -1) {
            int endIndex = startIndex + 4 + process.getInstructions().size() - 1;
            process.setMemoryBoundaries(startIndex, endIndex);

            memory.put(startIndex + "", process);
            // System.out.println(memory.get(startIndex + ""));

            int track = startIndex + 1;
            for (int i = startIndex + 1, j = 0; j < process.getInstructions().size(); i++, j++) {
                memory.put(i + "", process.getInstructions().get(j));
                track++;
            }
            // memory.put(track + "", new Variable("x", null));
            // track++;
            // memory.put(track + "", new Variable("y", null));
            // track++;
            // memory.put(track + "", new Variable("z", null));

            process.setState("Ready");
            if (memory.get(startIndex + "") instanceof Process) {
                ((Process) (memory.get(startIndex + ""))).setState("Ready");

                this.currentSize += process.getRequiredMemory();
            }

            return true;
        }
        // yousef
        else {
            Process removedProcess;
            for (int i = currentSize; i > 0; i--) {
                if (memory.get(i + "") instanceof Process) {
                    removedProcess = (Process) memory.get(i + "");
                    saveToDisk(removedProcess);
                    deallocateMemory(removedProcess);
                    break;
                }

            }
            allocateMemory(process);
        }
        return false;
    }

    
    public ArrayList<Process> loadData(int time) {
        ArrayList<Process> processList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : memory.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Process) {
                Process process = (Process) value;
                if (process.getTime() >= time) {
                    processList.add(process);
                }
            }
        }
        return processList;
    }

    private void saveToDisk(Process process) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(
                new FileOutputStream("src/resources/Disk.bin", true))) {
            outputStream.writeObject(process);
        } catch (IOException e) {
            System.out.println("Error saving process to disk: " + e.getMessage());
        }
    }

    public ArrayList<Process> loadFromDisk() {
        ArrayList<Process> processes = new ArrayList<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("src/resources/Disk.bin"))) {
            while (true) {
                Process process = (Process) inputStream.readObject();
                processes.add(process);
            }
        } catch (EOFException e) {
            // Reached end of file
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading processes from disk: " + e.getMessage());
        }

        return processes;
    }

    public void clearDisk(Process processToRemove) {
        ArrayList<Process> processes = loadFromDisk();
        // processes.remove(processToRemove);

        for (Process pro : processes) {
            if (pro.getId() == processToRemove.getId()) {
                processes.remove(pro);
                break;
            }

        }

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("src/resources/Disk.bin"))) {
            for (Process process : processes) {
                outputStream.writeObject(process);
            }
        } catch (IOException e) {
            System.out.println("Error clearing disk: " + e.getMessage());
        }
    }

    public void deallocateMemory(Process process) {
        int startIndex = process.getStartIndex();
        int endIndex = process.getEndIndex();
        int tempcurr = currentSize;
        for (int i = startIndex; i <= endIndex; i++) {
            memory.remove(String.valueOf(i));
            tempcurr--;
        }
        shiftHashMapKeys(startIndex, endIndex);
        currentSize = tempcurr;
    }

    public void shiftHashMapKeys(int start, int end) {
        HashMap<String, Object> temp = new HashMap<>();
        HashMap<String, Object> shiftedMap = new HashMap<>();
        int counter = 0;



        for (int i = end+1; i <= currentSize - 1; i++) {
            temp.put(String.valueOf(counter), this.memory.get(String.valueOf(i)));
            counter++;
        }

        counter = 0;
        // process  10 mem    ->    10   ->    11
        // Process end;
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(String.valueOf(i)) instanceof Process) {
                Process pro = (Process)(temp.get(String.valueOf(i)));
                pro.setMemoryBoundaries(i, i + pro.getRequiredMemory() - 1);
                shiftedMap.put(String.valueOf(counter), pro);
                // end = pro;
                // i += pro.getRequiredMemory() - 1;
            }
            else{
                shiftedMap.put(String.valueOf(i), temp.get(String.valueOf(i)));
            }
            counter++;
        }

        this.memory = shiftedMap;

    }

    public void printMemory() {
        System.out.println("Memory State:");
        for (Map.Entry<String, Object> entry : memory.entrySet()) {
            if(entry.getValue() instanceof Variable){
                String key = entry.getKey();
                Object value = ((Variable)(entry.getValue())).getValue();
                System.out.println(key + ": " + value);

            }
            else{
                String key = entry.getKey();
                Object value = entry.getValue();
                System.out.println(key + ": " + value);
            }
        }
    }

    public void displayMemoryState() {
        System.out.println("Memory State:");
        for (int i = 0; i < MEMORY_SIZE; i++) {
            Object variable = memory.get(i + "");
            if (variable == null) {
                System.out.println(i + ": Free");
            } else {
                System.out.println(i + ": " + variable);
            }
        }
    }

    private int findAvailableMemory(int requiredMemory) {
        int startIndex = -1;
        int count = 0;
        for (int i = this.currentSize; i < MEMORY_SIZE; i++) {
            if (!memory.containsKey(i)) {
                if (startIndex == -1) {
                    startIndex = i;
                }
                count++;
                if (count == requiredMemory) {
                    return startIndex;
                }
            } else {
                startIndex = -1;
                count = 0;
            }
        }
        return -1;
    }

    public boolean write(String varName, Object value) {
        if (this.memory.size() < MEMORY_SIZE) {
            this.memory.put(varName, new Variable(varName, value));
            return true;
        } else {
            return false;
        }
    }

    public Object read(String varName) {
        if (this.memory.containsKey(varName)) {
            if (this.memory.get(varName) instanceof Variable) {
                return ((Variable) (this.memory.get(varName))).getValue();
            }
            return this.memory.get(varName);
            // instance of variable
        } else {
            return null;
        }
    }

    public Object getMemoryWord(String varName) {
        Object var = null;
        if (this.memory.containsKey(varName)) {
            var = this.memory.get(varName);
        }
        return var;
    }

    public HashMap<String, Object> getMemory() {
        return memory;
    }

    public void setVariable(String address, Object insert) {
        ((Variable) (this.memory.get(String.valueOf(address)))).setValue(insert);
    }

    public void setProcessState(int processId, String state) {
        for (Map.Entry<String, Object> entry : memory.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Process) {
                Process process = (Process) value;
                if (process.getId() == processId) {
                    process.setState(state);
                    break;
                }
            }
        }
    }
    
    public void printDiskContents() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("src/resources/Disk.bin"))) {
            System.out.println("Disk Contents:");
            while (true) {
                Process process = (Process) inputStream.readObject();
                System.out.println(process);
            }
        } catch (EOFException e) {
            // Reached end of file
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading disk contents: " + e.getMessage());
        }
    }
    
}

// Nested class to represent Variable with its name and value.
class Variable {
    private String name;
    private Object value;

    public Variable(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
