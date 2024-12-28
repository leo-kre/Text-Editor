package Editor;

import Graphics.Window;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TextEngine {

    public Window window;
    public FileHandler fileHandler;

    public String currentFilePath;
    public String currentFolderPath;

    private ScheduledExecutorService scheduler;

    public int lineHeight = 20;
    public int lineSpacing = 20;
    public final int scrollSpeed = 8;
    public final int outOfScreenRenderBuffer = 100;

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
        window.updateScrollingHeight();
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

    private void renderTextData(StringBuilder data) {
        window.canvas.textData = data;
    }
}
