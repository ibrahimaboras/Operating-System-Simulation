package main;
import java.io.*;
import java.util.*;

public class SystemCall {
    // private Memory memory;
    public Scanner scanner;

    public SystemCall() {
        // this.memory = memory;
        this.scanner = new Scanner(System.in);
    }

    public String readFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("src/resources/" + filename));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.lineSeparator());
        }
        reader.close();
        return builder.toString();
    }

    public void writeFile(Memory memory, String filename, String text) throws IOException {
        File file = new File(filename);
        try (Writer writer = new BufferedWriter(new FileWriter( "src/resources/" + filename))) {
            writer.write(text);
        }
    }

    public void print(String text) {
        System.out.println("Printed Statement: " + text);
    }

    public String input() {
        System.out.print("Please enter a value: ");
        return scanner.nextLine();
    }

    public Object readMemory(Memory memory, int address) {
        if(memory.getMemory().get(String.valueOf(address)) instanceof Variable)
            return ((Variable)(memory.getMemory().get(String.valueOf(address)))).getValue();
        return memory.getMemory().get(String.valueOf(address));
    }

    public void writeMemory(Memory memory, String address, Object value) {
        memory.write(address, value);
    }
}
