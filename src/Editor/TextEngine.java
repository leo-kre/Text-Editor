package Editor;

import Graphics.Window;
import LayoutManagement.Vec2D;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TextEngine {

    public Window window;
    public FileHandler fileHandler;

    public char[][] formattedData;

    public String currentFilePath;
    public String currentFolderPath;

    private ScheduledExecutorService scheduler;

    public int lineHeight = 20;
    public int lineSpacing = 20;
    public final int scrollSpeed = 8;
    public final int outOfScreenRenderBuffer = 100;

    public int fileLineLength;
    public int selectedLineLength;

    public Vec2D cursorPosition = new Vec2D(25, 3);
    public int targetCursorXPosition = 25;

    public int scrollHeight = 0;

    private final int TPS = 120;

    public TextEngine() {
        window = new Window(this);

        fileHandler = new FileHandler();

        startUpdateLoop();
    }

    private void startUpdateLoop() {
        int fpsInMs = 1000 / TPS;

        scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(this::update);
        }, 0, fpsInMs, TimeUnit.MILLISECONDS);
    }

    private void update() {
        window.update();
    }

    public void setCurrentFilePath(String path) {
        this.currentFilePath = path;

        StringBuilder fileData = fileHandler.load(this.currentFilePath);

        if(fileData == null || fileData.isEmpty()) {
            window.showMessage("Empty File");
            return;
        }

        renderTextData(fileData);

    }

    public void write(char character, int x, int y) {

        char[][] arr2 = new char[formattedData.length][];

        for(int line = 0; line < formattedData.length; line++) {
            if(line == y) {
                arr2[line] = insertChar(formattedData[line], character, x);
            } else {
                arr2[line] = formattedData[line];
            }
        }

        formattedData = arr2;
        this.cursorPosition.x++;
        this.targetCursorXPosition = this.cursorPosition.x;
    }

    public void remove(int x, int y) {

        System.out.println(this.selectedLineLength);

        char[][] arr2 = new char[formattedData.length][];

        for(int line = 0; line < formattedData.length; line++) {
            if(line == this.cursorPosition.y) {
                arr2[line] = removeChar(formattedData[line], x - 1);
            } else {
                arr2[line] = formattedData[line];
            }

        }



        formattedData = arr2;
        this.cursorPosition.x--;
        this.targetCursorXPosition = this.cursorPosition.x;
    }

    private void renderTextData(StringBuilder data) {
        this.formattedData = splitLinesIntoCharArrays(data);
    }

    private char[] insertChar(char[] arr, char character, int position) {
        char[] arr2 = new char[arr.length + 1];

        boolean insertedChar = false;

        for(int i = 0; i < arr.length; i++) {
            if(i == position) {
                arr2[i] = character;
                insertedChar = true;
            }

            if(!insertedChar) {
                arr2[i] = arr[i];
            } else {
                arr2[i + 1] = arr[i];
            }
        }

        return arr2;
    }

    private char[] removeChar(char[] arr, int position) {
        char[] arr2 = new char[arr.length - 1];

        boolean removedChar = false;

        for(int i = 0; i < arr.length - 1; i++) {
           if(i == position) {
               removedChar = true;
           }

            if(!removedChar) {
                arr2[i] = arr[i];
            } else {
                arr2[i] = arr[i + 1];
            }
        }

        return arr2;
    }

    private static char[][] splitLinesIntoCharArrays(StringBuilder sb) {
        // Convert StringBuilder to String and split by newlines
        String[] lines = sb.toString().split("\n");

        // Create a 2D array to hold the char arrays
        char[][] result = new char[lines.length][];

        // Convert each line to a char array and store it in the result
        for (int i = 0; i < lines.length; i++) {
            result[i] = lines[i].toCharArray();
        }

        return result;
    }

}
