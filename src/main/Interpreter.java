package main;
import java.util.*;
import java.io.*;

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

    private Map<String, Mutex> mutexes;
    Mutex output;
    Mutex input;
    Mutex fileM;
    int time = 0;

    public Interpreter() {
        this.mutexes = new HashMap<>();
        // mutexes.put("userInput", new Mutex());
        // mutexes.put("userOutput", new Mutex());
        // mutexes.put("file", new Mutex());
    }

    public void createProcess(Memory mem, Scheduler scheduler) throws IOException {

            // Process newProcess = new Process();

            // Specify the directory path
            String directoryPath = "src/Programs";
    
            // Create a File object for the directory
            File directory = new File(directoryPath);
            
            //String filePath = "src/Programs/" + file + ".txt";

            // FileReader fileReader = new FileReader(new File(filePath));

            // BufferedReader br = new BufferedReader(fileReader);
            // String line = "";
            // List<String> instructions = new ArrayList<>();
            // while((line = br.readLine()) != null){
            //     instructions.add(line);
            // }
            // newProcess.setInstructions(instructions);
            // mem.allocateMemory(newProcess);
            // scheduler.addProcess(newProcess);

    
            //Check if the directory exists
            if (directory.exists() && directory.isDirectory()) {
                // Get an array of all files in the directory
                File[] files = directory.listFiles();
    
                // Loop through each file
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    Process newProcess;

                    if(i == 0) newProcess = new Process(0);
                    else if(i == 1) newProcess = new Process(2);
                    else newProcess = new Process(4);


                    if (file.isFile()) {
                        // Perform operations on the file
                        System.out.println(file.getName());
                        FileReader fileReader = new FileReader(file);

                        BufferedReader br = new BufferedReader(fileReader);
                        String line = "";
                        List<String> instructions = new ArrayList<>();
                        while((line = br.readLine()) != null){
                            instructions.add(line);
                        }
                        newProcess.setInstructions(instructions);
                        mem.allocateMemory(newProcess);
                        if(i == 0) scheduler.addProcess(newProcess);
                    }
                }
            } else {
                System.out.println("Invalid directory path");
            }
        

    //    FileReader file = new FileReader("program1.txt");

    //     BufferedReader br = new BufferedReader(file);
    //     String line = "";
    //     while((line = br.readLine()) != null){
            
    //     }
    }

    // public static void main(String[] args) {
    //     //FileReader woah = new FileReader("program1.txt");

    //     File file = new File("src/resources/program1.txt");
    //     if (file.isFile()) {
    //         // Perform operations on the file
    //         System.out.println(file.getName());
    //     }
    // }

    public void interpret(Scheduler schedular, Memory mem, Process process) throws IOException {
        String instruction = process.getInstructions().get(process.getProgramCounter());
        String[] parts = instruction.split(" ");
        String command = parts[0];

        boolean flag = false;

        switch (command) {
            case "print":
                String indexPrint = "";
                switch(parts[1]){
                    case "x":
                        indexPrint = String.valueOf(process.getEndIndex() - 2);
                        break;
                    case "y":
                        indexPrint = String.valueOf(process.getEndIndex() - 1);
                        break;
                    case "z":
                        indexPrint = String.valueOf(process.getEndIndex());
                }
                synchronized (mutexes.get("userOutput")) {
                    System.out.println("Print Statement: " + mem.read(indexPrint));
                    flag = true;
                }
                if(flag){
                    schedular.addToBlockedQueue(process);
                    schedular.addToOutput(process);
                }
                break;
            case "assign":
                String index = "";
                String value = parts[2];
                switch(parts[1]){
                    case "x":
                        index = String.valueOf(process.getEndIndex() - 2);
                        break;
                    case "y":
                        index = String.valueOf(process.getEndIndex() - 1);
                        break;
                    case "z":
                        index = String.valueOf(process.getEndIndex());
                }
                if (value.equals("input")) {
                    // userInputMutex.acquire();
                    synchronized (mutexes.get("userInput")){
                        Scanner scanner = new Scanner(System.in);

                        System.out.print("Please enter a value: ");
                        String inputValue = scanner.nextLine();
                
                        //System.out.println("Value: " + inputValue);
                        try {
                            int intFromUser = Integer.parseInt(inputValue);
                            mem.write(index, inputValue);
                            // System.out.println("Index: " + index);
                            // System.out.println(((Variable)(mem.getMemory().get(index))).getValue());
                        } catch (NumberFormatException e) {
                            mem.write(index, inputValue);
                        }
                
                        scanner.close();
                    // userInputMutex.release();
                    }
                }
                else 
                    mem.write(index, parts[2]);
                    // System.out.println("Index: " + index);
                    // System.out.println(((Variable)(mem.getMemory().get(index))).getValue());
                break;
            case "writeFile":
                String indexWrite1 = "";
                String indexWrite2 = "";

                Variable part1;
                Variable part2;

                synchronized (mutexes.get("file")) {
                    switch(parts[1]){
                        case "x":
                            indexWrite1 = String.valueOf(process.getEndIndex() - 2);
                            break;
                        case "y":
                            indexWrite1 = String.valueOf(process.getEndIndex() - 1);
                            break;
                        case "z":
                            indexWrite1 = String.valueOf(process.getEndIndex());
                    }

                    part1 = (Variable)(mem.getMemory().get(indexWrite1));

                    System.out.println(part1.getName() + "Value: " + part1.getValue());

                    switch(parts[2]){
                        case "x":
                            indexWrite2 = String.valueOf(process.getEndIndex() - 2);
                            break;
                        case "y":
                            indexWrite2 = String.valueOf(process.getEndIndex() - 1);
                            break;
                        case "z":
                            indexWrite2 = String.valueOf(process.getEndIndex());
                    }

                    part2 = (Variable)(mem.getMemory().get(indexWrite2));

                    String fileName = part1.getValue() + ".txt";
                    // System.out.println(fileName);
                    // File file = new File(fileName);
                    try (Writer writer = new BufferedWriter(new FileWriter( "src/resources/" + fileName))) {
                        writer.write(mem.read(indexWrite2).toString());
                        flag = true;
                    }
                    if(flag){
                        schedular.addToBlockedQueue(process);
                        schedular.addToFile(process);
                    }
                }
                break;
            case "readFile":
                String indexReader = "";
                String file = "";
                Variable reader;

                synchronized (mutexes.get("file")) {

                    switch(parts[1]){
                        case "x":
                            indexReader = String.valueOf(process.getEndIndex() - 2);
                            break;
                        case "y":
                            indexReader = String.valueOf(process.getEndIndex() - 1);
                            break;
                        case "z":
                            indexReader = String.valueOf(process.getEndIndex());
                    }

                    reader = (Variable)(mem.getMemory().get(indexReader));

                    file = (String)reader.getValue() + ".txt";

                    try (Scanner scanner = new Scanner(new File( "src/resources/" + file))) {
                        while (scanner.hasNextLine()) {
                            System.out.println(scanner.nextLine());
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

                int start = 0;
                int end = 0;

                switch(parts[1]){
                    case "x":
                        start = Integer.parseInt((String)((Variable)(mem.getMemory().get(String.valueOf(process.getEndIndex() - 2)))).getValue());
                        break;
                    case "y":
                        start = Integer.parseInt((String)((Variable)(mem.getMemory().get(String.valueOf(process.getEndIndex() - 1)))).getValue());
                        break;
                    case "z":
                        start = Integer.parseInt((String)((Variable)(mem.getMemory().get(String.valueOf(process.getEndIndex())))).getValue());
                }

                switch(parts[2]){
                    case "x":
                        end = Integer.parseInt((String)((Variable)(mem.getMemory().get(String.valueOf(process.getEndIndex() - 2)))).getValue());
                        break;
                    case "y":
                        end = Integer.parseInt((String)((Variable)(mem.getMemory().get(String.valueOf(process.getEndIndex() - 1)))).getValue());
                        break;
                    case "z":
                        end = Integer.parseInt((String)((Variable)(mem.getMemory().get(String.valueOf(process.getEndIndex())))).getValue());
                }


                if(output.getPro().getId() == process.getId()){
                    for (int i = start; i <= end; i++) {
                        System.out.println(i);
                    }
                }
                
                else{
                    schedular.addToBlockedQueue(process);
                    schedular.addToOutput(process);
                }
                break;
            case "semWait":
                switch(parts[1]){
                    case "userOutput":
                        output = new Mutex(process);
                    case "userInput":
                        input = new Mutex(process);
                    case "file":
                        fileM = new Mutex(process);
                }
                break;
            case "semSignal":
                
                    System.out.println(mutexes.get(parts[1]).isLocked());

                    switch (parts[1]){
                        case "userOutput":
                            if(!(schedular.getBlockedOutput().isEmpty()))
                                schedular.removeFromOutput();
                            break;
                        case "userInput":
                            if(!(schedular.getBlockedInput().isEmpty()))
                                schedular.removeFromInput();
                            break;

                        case "file":
                            if(!(schedular.getBlockedFile().isEmpty()))
                                schedular.removeFromFile();
                            break;
                    }
                    mutexes.get(parts[1]).unlock();
                
            
            
        time++;
        process.incrementProgramCounter();
    }
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
