package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CreateTestFile {

    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception{

        Memory memory = new Memory();

        //SystemCall systemCall = new SystemCall(memory);

        Scheduler scheduler = new Scheduler(2);
        Interpreter interpreter = new Interpreter();

        interpreter.createProcess(memory, scheduler);
       // interpreter.createProcess("program3", memory, scheduler);

        //printHashMapValues(memory.getMemory());

        scheduler.executeProcesses(memory, interpreter);
    }

    public static void printHashMapValues(HashMap<String, Object> map) {
        for (Object value : map.values()) {
            System.out.println(value);
        }
    }

    }

