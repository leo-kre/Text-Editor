package Graphics;

import LayoutManagement.Tile;
import LayoutManagement.TilingManager;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Canvas extends JPanel {

    private ScheduledExecutorService scheduler;

    private TilingManager tilingManager;

    public StringBuilder textData = null;

    public int scrollHeight = 0;

    public Canvas(Dimension screenSize, TilingManager tilingManager) {
        this.setSize(screenSize);

        this.tilingManager = tilingManager;

        startRender();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.BLACK);
        g2d.clearRect(0, 0, this.getWidth(), this.getHeight());

        renderTextEditor(g2d);
        renderFilePath(g2d);
    }

    private void renderTextEditor(Graphics2D g2d) {
        g2d.setColor(Color.BLUE);
        Tile textEditor = tilingManager.getTextEditorTile();
        int x = textEditor.x;
        int y = textEditor.y;
        g2d.fillRect(x, y, textEditor.size.width, textEditor.size.height);

        g2d.setColor(Color.WHITE);

        if(textData == null) return;

        char[][] formattedData = splitLinesIntoCharArrays(textData);

        for (int line = 0; line < formattedData.length; line++) {

            g2d.drawString(String.valueOf(line), x + 10, y + 50 + scrollHeight);

            g2d.drawChars(formattedData[line], 0, formattedData[line].length, x + 50, y + 50 + scrollHeight);

            y += 15;
        }
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

    private void renderFilePath(Graphics2D g2d) {
        g2d.setColor(Color.RED);

        Tile filePath = tilingManager.getFilePathTile();

        g2d.fillRect(filePath.x, filePath.y, filePath.size.width, filePath.size.height);

        g2d.setColor(Color.gray);
        g2d.fillRect(filePath.x + filePath.size.width - tilingManager.hitboxSize, filePath.y, tilingManager.hitboxSize * 2, filePath.size.height);
    }

    private void startRender() {

        int fpsInMs = 1000 / getMonitorRefreshRate();

        scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(this::repaint);
        }, 0, fpsInMs, TimeUnit.MILLISECONDS);
    }

    private int getMonitorRefreshRate() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        DisplayMode mode = gd.getDisplayMode();
        return mode.getRefreshRate();
    }

    public void updateTilingManager() {
        tilingManager.updateScreenSize(this.getSize());
    }
}
