package main;
import java.io.*;
import java.util.*;

public class SystemCall {
    private Memory memory;
    private Scanner scanner;

    public SystemCall(Memory memory) {
        this.memory = memory;
        this.scanner = new Scanner(System.in);
    }

    public String readFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.lineSeparator());
        }
        reader.close();
        return builder.toString();
    }

    public void writeFile(String filename, String text) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(text);
        writer.close();
    }

    public void print(String text) {
        System.out.println(text);
    }

    public String input() {
        System.out.print("Please enter a value: ");
        return scanner.nextLine();
    }

    public Object readMemory(int address) {
        if(memory.getMemoryWord(String.valueOf(address)) instanceof Variable)
            return ((Variable)(memory.getMemoryWord(String.valueOf(address)))).getValue();
        return memory.getMemoryWord(String.valueOf(address));
    }

    public void writeMemory(String address, Object value) {
        memory.setVariable(address, value);
    }
}

//
//import java.io.*;
//
//public class SystemCall {
//    private static final String BASE_DIR = "data";
//
//    public static String readDataFromFile(String filename) throws IOException {
//        BufferedReader reader = new BufferedReader(new FileReader(BASE_DIR + "/" + filename));
//        String line;
//        StringBuilder content = new StringBuilder();
//
//        while ((line = reader.readLine()) != null) {
//            content.append(line);
//        }
//
//        reader.close();
//        return content.toString();
//    }
//
//    public static void writeDataToFile(String filename, String content) throws IOException {
//        BufferedWriter writer = new BufferedWriter(new FileWriter(BASE_DIR + "/" + filename));
//        writer.write(content);
//        writer.close();
//    }
//
//    public static void printDataToScreen(String content) {
//        System.out.println(content);
//    }
//
//    public static String takeTextInput() throws IOException {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        return reader.readLine();
//    }
//
//    public static String readDataFromMemory(Memory memory, int address) {
//        return memory.getData(address);
//    }
//
//    public static void writeDataToMemory(Memory memory, int address, String data) {
//        memory.setData(address, data);
//    }
//}
//
