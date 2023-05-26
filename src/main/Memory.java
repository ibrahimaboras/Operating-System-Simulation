package main;
import javax.lang.model.element.VariableElement;
import java.util.*;

public class Memory {
    private static final int MEMORY_SIZE = 40;
    private HashMap<String, Object> memory;
    private int currentSize;

    public Memory() {
        this.memory = new HashMap<>();
        this.currentSize = 0;
    }

    public boolean allocateMemory(Process process) {
        process.setRequiredMemory(1 + 4 + process.getInstructions().size());
        int startIndex = findAvailableMemory(process.getRequiredMemory());
        if (startIndex != -1) {
            int endIndex = startIndex + 4 + process.getInstructions().size();
            process.setMemoryBoundaries(startIndex, endIndex);

            memory.put(startIndex + "", process);
            //System.out.println(memory.get(startIndex + ""));

            int track = startIndex + 1;
            for (int i = startIndex + 1, j = 0; i <= process.getInstructions().size(); i++, j++) {
                memory.put(i + "", process.getInstructions().get(j));
                track++;
            }
            memory.put(track + "", new Variable("x", null)); track++;
            memory.put(track + "", new Variable("y", null)); track++;
            memory.put(track + "", new Variable("z", null));


            process.setState("Ready");
            if(memory.get(startIndex + "") instanceof Process){
                ((Process)(memory.get(startIndex + ""))).setState("Ready");

            this.currentSize += process.getRequiredMemory();
            }
            

            return true;
        }
        // yousef
        else{
            Process removedProcess;

        }
        return false;
    }

    public void deallocateMemory(Process process) {
        int startIndex = process.getStartIndex();
        int endIndex = process.getEndIndex();
        for (int i = startIndex; i <= endIndex; i++) {
            memory.remove(i);
        }
        shiftHashMapKeys();
    }

    public void shiftHashMapKeys() {
        HashMap<String, Object> shiftedMap = new HashMap<>();
        int counter = 1;

        for (Map.Entry<String, Object> entry : this.memory.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            shiftedMap.put(String.valueOf(counter), value);
            counter++;
        }
        this.memory = shiftedMap;

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
        for (int i = 0; i < MEMORY_SIZE; i++) {
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
        if(this.memory.size() < MEMORY_SIZE) {
            this.memory.put(varName, new Variable(varName, value));
            return true;
        } else {
            return false;
        }
    }

    public Object read(String varName) {
        if(this.memory.containsKey(varName)) {
            if(this.memory.get(varName) instanceof Variable){
                return ((Variable)(this.memory.get(varName))).getValue();
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

    public void setVariable(String address, Object insert){
        ((Variable)(this.getMemoryWord(String.valueOf(address)))).setValue(insert);
    }   

    public static void main(String[] args) {
        Memory mem = new Memory();
        Process pro = new Process();
        List<String> instructions = new ArrayList<>();
        instructions.add("assign x y");
        instructions.add("betengan");
        instructions.add("woah");
        pro.setInstructions(instructions);

        mem.allocateMemory(pro);

        for (int i = 0; i < 8; i++) {
            if(mem.getMemoryWord(i + "") instanceof Process)
                System.out.println(((Process)(mem.getMemoryWord(i + ""))).getState());
            System.out.println(mem.getMemory().get(i + ""));    
        }
        //System.out.println("qwrfsad");
        
    }


}


// public class Memory {
//     public static final int MEMORY_SIZE = 40;
//     private Map<String, Variable> memoryMap;

//     public Memory() {
//         this.memoryMap = new HashMap<>();
//     }

//     public boolean write(String varName, Object value) {
//         if(this.memoryMap.size() < MEMORY_SIZE) {
//             this.memoryMap.put(varName, new Variable(varName, value));
//             return true;
//         } else {
//             return false;
//         }
//     }

//     public Variable getMemoryWord(String varName) {
//         Variable var = null;
//         if (this.memoryMap.containsKey(varName)) {
//             var = this.memoryMap.get(varName);
//         }
//         return var;
//     }


//         public Object read(String varName) {
//         if(this.memoryMap.containsKey(varName)) {
//             return this.memoryMap.get(varName).getValue();
//         } else {
//             return null;
//         }
//     }

//     public boolean isAvailable() {
//         return this.memoryMap.size() < MEMORY_SIZE;
//     }

//     public void remove(String varName) {
//         this.memoryMap.remove(varName);
//     }

//     public Map<String, Variable> getMemoryMap() {
//         return this.memoryMap;
//     }

//     }

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



//
//public class Memory {
//    private Integer[] memory;
//    private final int MEMORY_SIZE = 40;
//
//    public Memory() {
//        memory = new Integer[MEMORY_SIZE];
//    }
//
//    public synchronized boolean allocate(int processId, int processSize) {
//        // Search for a block of free memory
//        for (int i = 0; i < memory.length; i++) {
//            if (memory[i] == null) {
//                // Check if the block is big enough
//                int j = i;
//                while (j < memory.length && memory[j] == null && j - i < processSize) {
//                    j++;
//                }
//                // If the block is big enough, allocate it
//                if (j - i == processSize) {
//                    for (int k = i; k < j; k++) {
//                        memory[k] = processId;
//                    }
//                    return true;
//                }
//            }
//        }
//        // No suitable block was found
//        return false;
//    }
//
//    public synchronized void deallocate(int processId) {
//        // Free any blocks belonging to this process
//        for (int i = 0; i < memory.length; i++) {
//            if (memory[i] != null && memory[i] == processId) {
//                memory[i] = null;
//            }
//        }
//    }
//
//    // Method for printing memory state
//    public void printMemoryState() {
//        for (int i = 0; i < memory.length; i++) {
//            System.out.print(memory[i] + " ");
//        }
//        System.out.println();
//    }
//}
//
