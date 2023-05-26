package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class CreateTestFile {
    public static void main(String[] args) throws Exception {
        // Create instances of OS components
        Process process = new Process();
        Memory memory = new Memory();
        Interpreter interpreter = new Interpreter();
        Scheduler scheduler = new Scheduler(2);
        Mutex mutex = new Mutex();

        // Create a list of program file names
        List<String> programFiles = Arrays.asList("program1.txt", "program2.txt", "program3.txt");

        // For each program file...
        for (String fileName : programFiles) {
            // Read the program file into memory
//            process = interpreter("program.txt");

            // If the process was loaded successfully...
            if (process != null) {
                // Add the process to the scheduler's ready queue
                scheduler.addProcess(process);
            }
        }

        while (scheduler.hasProcessesToExecute()) {
            // Get the next process to execute
            process = scheduler.getNextProcess();

            // Execute the process
            interpreter.interpret(scheduler, memory, process);

            // If the process is not finished, add it back to the scheduler's ready queue
            if (!process.isFinished()) {
                scheduler.addProcess(process);
            }
        }
    }
    }

