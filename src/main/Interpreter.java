package main;
import java.util.*;
import java.io.*;

public class Interpreter {

    SystemCall systemCall;

    private Map<String, Mutex> mutexes;
    Mutex output;
    Mutex input;
    Mutex fileM;
    int time = 0;

    public Interpreter() {

        systemCall = new SystemCall();

        this.mutexes = new HashMap<>();
    }

    public void createProcess(Memory mem, Scheduler scheduler) throws IOException {

            // Process newProcess = new Process();

            // Specify the directory path
            String directoryPath = "src/Programs";
    
            // Create a File object for the directory
            File directory = new File(directoryPath);
            
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
        

    }


    public void interpret(Scheduler schedular, Memory mem, Process process) throws IOException {
        process.setState("Running");
        mem.setProcessState(process.getId(), "Running");
        String instruction = process.getInstructions().get(process.getProgramCounter());

        System.out.println("Instruction Currently Executing: \n\t" + instruction);

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
                            break;
                        case "readFile":
                            indexPrint = readFileHelper(schedular, mem, process, parts[2]);
                    }
                    if(output.getPro().getId() == process.getId()){
                        if(parts[1].equals("readFile"))
                            systemCall.print(indexPrint);
                        else
                            systemCall.print((String)(systemCall.readMemory(mem, Integer.parseInt(indexPrint))));
                        flag = true;
                        process.incrementProgramCounter();

                    }
                    else{
                    // mem.deallocateMemory(process);
                        process.setState("Blocked");
                        mem.setProcessState(process.getId(), "Blocked");
                    // mem.allocateMemory(process);
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
                System.out.println(index);
                
                if(parts.length == 4 ){
                    String temp = readFileHelper(schedular, mem, process, parts[3]);
                    systemCall.writeMemory(mem, index, temp);
                }
                else{
                    if (value.equals("input")) {
                        // userInputMutex.acquire();
                        if(input.getPro().getId() == process.getId()){
                            //systemCall.input();
                            System.out.println("Process: " + process.getId());
                            String inputValue = systemCall.input();
                    
                            //System.out.println("Value: " + inputValue);
                            try {
                                int intFromUser = Integer.parseInt(inputValue);
                                systemCall.writeMemory(mem, index, inputValue);
                                // System.out.println("Index: " + index);
                                // System.out.println(((Variable)(mem.getMemory().get(index))).getValue());
                            } catch (NumberFormatException e) {
                                systemCall.writeMemory(mem, index, inputValue);
                            }
                    
                            //scanner.close();
                        // userInputMutex.release();
                        process.incrementProgramCounter();

                        }
                        else{
                        //  mem.deallocateMemory(process);
                            process.setState("Blocked");
                            mem.setProcessState(process.getId(), "Blocked");
                        //  mem.allocateMemory(process);
                            schedular.addToBlockedQueue(process);
                            schedular.addToInput(process);
                        }
                    }
                    else{
                        systemCall.writeMemory(mem, index, parts[2]);
                        process.incrementProgramCounter();
                    }
                }
                    // System.out.println("Index: " + index);
                    // System.out.println(((Variable)(mem.getMemory().get(index))).getValue());
                break;
            case "writeFile":
                String indexWrite1 = "";
                String indexWrite2 = "";

                String part1;
                Variable part2;

                if(fileM.getPro().getId() == process.getId()) {
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

                    // part1 = (Variable)(mem.getMemory().get(indexWrite1));
                    part1 = (String)(systemCall.readMemory(mem, Integer.parseInt(indexWrite1)));

                    //System.out.println(part1.getName() + "Value: " + part1.getValue());

                    switch(parts[2]){
                        case "x":
                            indexWrite2 = String.valueOf(process.getEndIndex() - 2);
                            break;
                        case "y":
                            indexWrite2 = String.valueOf(process.getEndIndex() - 1);
                            break;
                        case "z":
                            indexWrite2 = String.valueOf(process.getEndIndex());
                            break;
                        case "readFile":
                            indexWrite2 = readFileHelper(schedular, mem, process, parts[3]);
                    }

                    //part2 = (Variable)(mem.getMemory().get(indexWrite2));

                    String fileName = part1 + ".txt";
                    // System.out.println(fileName);
                    // File file = new File(fileName);
                    if(parts[2].equals("readFile"))
                        systemCall.writeFile(mem, fileName, indexWrite2);
                    else
                        systemCall.writeFile(mem, fileName, (String)(systemCall.readMemory(mem, Integer.parseInt(indexWrite2))));; // mem.read(indexWrite2).toString()
                    // try (Writer writer = new BufferedWriter(new FileWriter( "src/resources/" + fileName))) {
                    //     writer.write(mem.read(indexWrite2).toString());
                    // }
                    process.incrementProgramCounter();
                }
                else{
                   // mem.deallocateMemory(process);
                    process.setState("Blocked");
                    mem.setProcessState(process.getId(), "Blocked");
                   // mem.allocateMemory(process);
                    schedular.addToBlockedQueue(process);
                    schedular.addToFile(process);
                    
                }
                break;
            case "readFile":
                readFileHelper(schedular, mem, process, parts[1]);
                
                break;
            case "printFromTo":

                int start = 0;
                int end = 0;

                switch(parts[1]){
                    case "x":
                        System.out.println(process.getEndIndex() - 2);
                       // if(systemCall.readMemory(mem, process.getEndIndex() - 2) instanceof String) System.out.println("A7A");
                        start = Integer.parseInt((String)(((systemCall.readMemory(mem, process.getEndIndex() - 2))))); ; //(mem.getMemory().get(String.valueOf(process.getEndIndex() - 2)))
                        break;
                    case "y":
                        start = Integer.parseInt((String)(((systemCall.readMemory(mem, process.getEndIndex() - 1)))));
                        break;
                    case "z":
                        start = Integer.parseInt((String)(((systemCall.readMemory(mem, process.getEndIndex())))));
                }

                switch(parts[2]){
                    case "x":
                        end = Integer.parseInt((String)(((systemCall.readMemory(mem, process.getEndIndex() - 2)))));
                        break;
                    case "y":
                        end = Integer.parseInt((String)(((systemCall.readMemory(mem, process.getEndIndex() - 1)))));
                        break;
                    case "z":
                        end = Integer.parseInt((String)(((systemCall.readMemory(mem, process.getEndIndex())))));
                }


                if(output.getPro().getId() == process.getId()){
                    for (int i = start; i <= end; i++) {
                        systemCall.print(String.valueOf(i));
                    }
                    process.incrementProgramCounter();
                }
                
                else{
                 //   mem.deallocateMemory(process);
                    process.setState("Blocked");
                    mem.setProcessState(process.getId(), "Blocked");
                 //   mem.allocateMemory(process);
                    schedular.addToBlockedQueue(process);
                    schedular.addToOutput(process);
                }
                break;
            case "semWait":
                switch(parts[1]){
                    case "userOutput":
                        if(output == null){
                            output = new Mutex(process);
                            process.incrementProgramCounter();
                        }
                        else{
                          //  mem.deallocateMemory(process);
                            process.setState("Blocked");
                            mem.setProcessState(process.getId(), "Blocked");
                          //  mem.allocateMemory(process);
                            schedular.addToBlockedQueue(process);
                            schedular.addToOutput(process);
                        }
                        break;
                    case "userInput":
                        if(input == null){
                            input = new Mutex(process);
                            process.incrementProgramCounter();
                        }
                        else{
                        //    mem.deallocateMemory(process);
                            process.setState("Blocked");
                            mem.setProcessState(process.getId(), "Blocked");
                        //    mem.allocateMemory(process);
                            schedular.addToBlockedQueue(process);
                            schedular.addToInput(process);
                        }
                        //input = new Mutex(process);
                        break;
                    case "file":
                        if(fileM == null){
                            fileM = new Mutex(process);
                            process.incrementProgramCounter();
                        }
                        else{
                           // mem.deallocateMemory(process);
                            process.setState("Blocked");
                            mem.setProcessState(process.getId(), "Blocked");
                        //    mem.allocateMemory(process);
                            schedular.addToBlockedQueue(process);
                            schedular.addToFile(process);
                        }
                       // fileM = new Mutex(process);
                        break;
                }
                //process.incrementProgramCounter();

                break;
            case "semSignal":

                switch(parts[1]){
                    case "userOutput":
                        output = null;
                        break;
                    case "userInput":
                        input = null;
                        break;
                    case "file":
                        fileM = null;
                        break;
            }
                
                    //System.out.println(mutexes.get(parts[1]).isLocked());

                    switch (parts[1]){
                        case "userOutput":
                            if(!(schedular.getBlockedOutput().isEmpty())){
                                schedular.removeFromOutput();
                            }
                            break;
                        case "userInput":
                            if(!(schedular.getBlockedInput().isEmpty())){
                                schedular.removeFromInput();
                            }
                            break;

                        case "file":
                            if(!(schedular.getBlockedFile().isEmpty())){
                                schedular.removeFromFile();
                            }
                            break;
                    }
                   // mutexes.get(parts[1]).unlock();
                   process.incrementProgramCounter();

                break;
            default:
                throw new IllegalArgumentException("Invalid command: " + command);
        }
        time++;
    }

    private String readFileHelper(Scheduler schedular, Memory mem, Process process, String parts) throws IOException {
        String indexReader = "";
        String file = "";
        String reader;

        if(fileM.getPro().getId() == process.getId()){

            switch(parts){
                case "x":
                    indexReader = String.valueOf(process.getEndIndex() - 2);
                    break;
                case "y":
                    indexReader = String.valueOf(process.getEndIndex() - 1);
                    break;
                case "z":
                    indexReader = String.valueOf(process.getEndIndex());
            }

            reader = (String)(systemCall.readMemory(mem, Integer.parseInt(indexReader))); //(mem.getMemory().get(indexReader)); 

            file = reader + ".txt";


            process.incrementProgramCounter();
            return systemCall.readFile(file);                    


        }

            else{
               // mem.deallocateMemory(process);
                process.setState("Blocked");
                mem.setProcessState(process.getId(), "Blocked");
               // mem.allocateMemory(process);
                 schedular.addToBlockedQueue(process);
                 schedular.addToFile(process);
                 return null;
            }
    }
}
