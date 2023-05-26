package main;
import java.util.*;
import java.io.*;

import java.util.HashMap;

// public class Interpreter {
//     //private HashMap<String, Integer> memory;
//     private Mutex fileMutex;
//     private Mutex userInputMutex;
//     private Mutex userOutputMutex;

//     public Interpreter() {
//         //this.memory = new HashMap<>();
//         this.fileMutex = new Mutex("file");
//         this.userInputMutex = new Mutex("userInput");
//         this.userOutputMutex = new Mutex("userOutput");
//     }

//     public void executeInstruction(Memory memory, Process pro) {
//         String instruction = pro.getInstructions().get(pro.getProgramCounter());
//         String[] tokens = instruction.split(" ");

//         if (tokens[0].equals("readFile")) {
//             String filename = tokens[1];
//             // Acquire file mutex
//             fileMutex.acquire();
//             // Read file logic
//             System.out.println("Reading file: " + filename);
//             // Release file mutex
//             fileMutex.release();
//         } else if (tokens[0].equals("writeFile")) {
//             String filename = tokens[1];
//             String data = tokens[2];
//             // Acquire file mutex
//             fileMutex.acquire();
//             // Write file logic
//             System.out.println("Writing data: " + data + " to file: " + filename);
//             // Release file mutex
//             fileMutex.release();
//         } else if (tokens[0].equals("print")) {
//             String output = tokens[1];
//             // Acquire user output mutex
//             userOutputMutex.acquire();
//             // Print output logic
//             System.out.println("Output: " + output);
//             // Release user output mutex
//             userOutputMutex.release();
//         } else if (tokens[0].equals("assign")) {
//             int location = 0;
//             String variable = tokens[1];
//             String value = tokens[2];
//             // Acquire user input mutex if value is "input"
//             if (value.equals("input")) {
//                 userInputMutex.acquire();
//                 System.out.println("Please enter a value for variable: " + variable);
//                 // Simulating user input
//                 value = "User input value";
//                 userInputMutex.release();
//             }
//             // Assign value to variable in memory
//             // -> mem -> variables places -> x, y, z -> Integer
//             switch(variable){
//                 case "x":
//                     location = pro.getEndIndex() - 2;
//                     break;
//                 case "y":
//                     location = pro.getEndIndex() - 1;
//                     break;
//                 case "z":
//                     location = pro.getEndIndex();
//             }

//             memory.setVariable(String.valueOf(location), value);
//             //memory.put(variable, Integer.parseInt(value));
//         }
//     }
// }


public class Interpreter {

    private Map<String, Object> mutexes;

    public Interpreter() {
        this.mutexes = new HashMap<>();
        mutexes.put("userInput", new Object());
        mutexes.put("userOutput", new Object());
        mutexes.put("file", new Object());
    }

    public void createProcess() throws IOException {
        Process newProcess = new Process();

        FileReader file = new FileReader("program1.txt");

        BufferedReader br = new BufferedReader(file);
        String line = "";
        while((line = br.readLine()) != null){
            
        }
    }

    public void interpret(Scheduler schedular, Memory mem, Process process) throws IOException {
        String instruction = process.getInstructions().get(process.getProgramCounter());
        String[] parts = instruction.split(" ");
        String command = parts[0];

        boolean flag = false;

        switch (command) {
            case "print":
                synchronized (mutexes.get("userOutput")) {
                    System.out.println(mem.read(parts[1]));
                    flag = true;
                }
                if(flag){
                    schedular.addToBlockedQueue(process);
                    schedular.addToOutput(process);
                }
                break;
            case "assign":
                mem.write(parts[1], parts[2]);
                break;
            case "writeFile":
                synchronized (mutexes.get("file")) {
                    try (Writer writer = new BufferedWriter(new FileWriter(parts[1]))) {
                        writer.write(mem.read(parts[2]).toString());
                        flag = true;
                    }
                    if(flag){
                        schedular.addToBlockedQueue(process);
                        schedular.addToFile(process);
                    }
                }
                break;
            case "readFile":
                synchronized (mutexes.get("file")) {
                    try (Scanner scanner = new Scanner(new File(parts[1]))) {
                        if (scanner.hasNextLine()) {
                            mem.write(parts[1], scanner.nextLine());
                        }
                        flag = true;
                    }

                    if(flag){
                         schedular.addToBlockedQueue(process);
                         schedular.addToFile(process);
                    }
                }
                break;
            case "printFromTo":
                int start = Integer.parseInt(parts[1]);
                int end = Integer.parseInt(parts[2]);
                synchronized (mutexes.get("userOutput")) {
                    for (int i = start; i <= end; i++) {
                        System.out.println(i);
                    }
                    flag = true;
                }
                if(flag){
                    schedular.addToBlockedQueue(process);
                    schedular.addToOutput(process);
               }
                break;
            case "semWait":
                synchronized (mutexes.get(parts[1])) {
                    try {
                        mutexes.get(parts[1]).wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "semSignal":
                synchronized (mutexes.get(parts[1])) {
                    mutexes.get(parts[1]).notify();
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid command: " + command);
        }

        process.incrementProgramCounter();
    }
}

//
//import java.io.*;
//import java.util.*;
//
//public class Interpreter {
//    private SystemCall systemCall = new SystemCall();
//    private Memory memory = new Memory();
//    private Mutex mutex = new Mutex();
//    private Scheduler scheduler = new Scheduler();
//
//    public void parse(String filename) throws FileNotFoundException {
//        File file = new File(filename);
//        Scanner scanner = new Scanner(file);
//
//        while (scanner.hasNextLine()) {
//            String command = scanner.nextLine().trim();
//            String[] parts = command.split(" ");
//
//            Process process = new Process();
//            process.setCommand(command);
//
//            switch (parts[0]) {
//                case "assign":
//                    process.setOperationType(OperationType.ASSIGN);
//                    process.setVariableName(parts[1]);
//                    process.setValue(parts[2]);
//                    break;
//                case "print":
//                    process.setOperationType(OperationType.PRINT);
//                    process.setVariableName(parts[1]);
//                    break;
//                case "writeFile":
//                    process.setOperationType(OperationType.WRITE_FILE);
//                    process.setFileName(parts[1]);
//                    process.setValue(parts[2]);
//                    break;
//                case "readFile":
//                    process.setOperationType(OperationType.READ_FILE);
//                    process.setFileName(parts[1]);
//                    break;
//                case "printFromTo":
//                    process.setOperationType(OperationType.PRINT_FROM_TO);
//                    process.setValue(parts[1]);
//                    process.setEndValue(parts[2]);
//                    break;
//                case "semWait":
//                    process.setOperationType(OperationType.SEM_WAIT);
//                    process.setMutexName(parts[1]);
//                    break;
//                case "semSignal":
//                    process.setOperationType(OperationType.SEM_SIGNAL);
//                    process.setMutexName(parts[1]);
//                    break;
//                default:
//                    throw new IllegalArgumentException("Unknown command: " + parts[0]);
//            }
//
//            scheduler.addProcess(process);
//        }
//
//        scanner.close();
//    }
//
//    public void execute(Process process) {
//        switch (process.getOperationType()) {
//            case ASSIGN:
//                systemCall.writeDataToMemory(memory, process.getVariableName(), process.getValue());
//                break;
//            case PRINT:
//                String value = systemCall.readDataFromMemory(memory, process.getVariableName());
//                systemCall.printDataToScreen(value);
//                break;
//            case WRITE_FILE:
//                systemCall.writeDataToFile(process.getFileName(), process.getValue());
//                break;
//            case READ_FILE:
//                String content = systemCall.readDataFromFile(process.getFileName());
//                systemCall.printDataToScreen(content);
//                break;
//            case PRINT_FROM_TO:
//                int start = Integer.parseInt(process.getValue());
//                int end = Integer.parseInt(process.getEndValue());
//                for (int i = start; i <= end; i++) {
//                    systemCall.printDataToScreen(String.valueOf(i));
//                }
//                break;
//            case SEM_WAIT:
//                mutex.acquire(process.getMutexName());
//                break;
//            case SEM_SIGNAL:
//                mutex.release(process.getMutexName());
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown operation: " + process.getOperationType());
//        }
//    }
//}
//
