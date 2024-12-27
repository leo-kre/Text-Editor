package Editor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileHandler {

    public StringBuilder load(String path) {
        // Use a StringBuilder to store the file content
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }

            return content;

        } catch (IOException e) {
            // Handle exceptions (e.g., file not found or read error)
            System.err.println("An error occurred while loading the file:");
            e.printStackTrace();
        }

        return null;
    }


    public void save(String path) {

    }


}
