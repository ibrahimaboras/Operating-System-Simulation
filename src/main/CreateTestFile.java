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

        Scheduler scheduler = new Scheduler(2);
        Interpreter interpreter = new Interpreter();

        interpreter.createProcess(memory, scheduler);

        scheduler.executeProcesses(memory, interpreter);
    }

    public static void printHashMapValues(HashMap<String, Object> map) {
        for (Object value : map.values()) {
            System.out.println(value);
        }
    }

    }

