package Graphics;

import Editor.TextEngine;
import LayoutManagement.Tile;
import LayoutManagement.TilingManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Canvas extends JPanel {

    private ScheduledExecutorService scheduler;

    private TilingManager tilingManager;

    private TextEngine textEngine;

    private int fileContentHeight = 0;

    public StringBuilder textData = null;

    private final Color backgroundColor = new Color(23, 24, 32);
    private final Color accentColor = new Color(58, 58, 75);

    private final Color darkText = new Color(83, 83, 109);

    private final Font editorFont = new Font("Consolas", Font.PLAIN, 24);
    private final Font utilFont = new Font("Arial", Font.PLAIN, 15);

    public Canvas(Dimension screenSize, TilingManager tilingManager, TextEngine textEngine) {
        this.setSize(screenSize);

        this.tilingManager = tilingManager;
        this.textEngine = textEngine;

        startRender();

        this.setFont(editorFont);
    }

    @Override
    public void paintComponent(Graphics g) {

        textEngine.lineSpacing = getLineHeight(g, editorFont);

        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.clearRect(0, 0, this.getWidth(), this.getHeight());

        renderTextEditor(g2d);
        renderFilePath(g2d);
    }

    private void renderTextEditor(Graphics2D g2d) {
        g2d.setColor(backgroundColor);
        g2d.setFont(editorFont);
        Tile textEditor = tilingManager.getTextEditorTile();
        int x = textEditor.x;
        int y = textEditor.y;
        g2d.fillRect(x, y, textEditor.size.width, textEditor.size.height);

        if(textData == null) return;

        char[][] formattedData = splitLinesIntoCharArrays(textData);

        fileContentHeight = formattedData.length * getLineHeight(g2d, editorFont);

        g2d.setColor(darkText);

        for (int line = 0; line < formattedData.length; line++) {

            int lineX = x + 5;
            int lineY = y + 50 - textEngine.scrollHeight;

            if(lineY >= (-textEngine.outOfScreenRenderBuffer) && lineY <= (this.getHeight() + textEngine.outOfScreenRenderBuffer)) {
                g2d.drawString(String.valueOf(line + 1), lineX, lineY);
            }

            y += textEngine.lineSpacing;
        }

        y = textEditor.y;

        g2d.setColor(Color.white);

        for (int line = 0; line < formattedData.length; line++) {

            int lineX = x + textEngine.lineHeight * 2;
            int lineY = y + 50 - textEngine.scrollHeight;

            if(lineY >= (-textEngine.outOfScreenRenderBuffer) && lineY <= (this.getHeight() + textEngine.outOfScreenRenderBuffer)) {
                g2d.drawChars(formattedData[line],0, formattedData[line].length, lineX, lineY);
            }

            y += textEngine.lineSpacing;
        }

        g2d.setColor(backgroundColor);
        g2d.fillRect(textEditor.x, this.getHeight() - 25, textEditor.size.width, 25);

        g2d.setColor(accentColor);
        g2d.fillRect(textEditor.x, this.getHeight() - 25, textEditor.size.width, tilingManager.hitboxSize * 2);

        g2d.setFont(utilFont);
        g2d.setColor(darkText);
        g2d.drawString(textEngine.currentFilePath, textEditor.x, this.getHeight() - 10);
    }

    private void renderFilePath(Graphics2D g2d) {
        g2d.setColor(backgroundColor);

        Tile filePath = tilingManager.getFilePathTile();

        g2d.fillRect(filePath.x, filePath.y, filePath.size.width, filePath.size.height);

        g2d.setColor(accentColor);
        g2d.fillRect(filePath.x + filePath.size.width - tilingManager.hitboxSize, filePath.y, tilingManager.hitboxSize * 2, filePath.size.height);
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

    public int getLineHeight(Graphics g, Font font) {
        // Set the font for the Graphics object
        g.setFont(font);

        // Get the FontMetrics object for the current font
        FontMetrics metrics = g.getFontMetrics(font);

        // Calculate the line height: ascent + descent + leading
        int lineHeight = metrics.getAscent() + metrics.getDescent() + metrics.getLeading();

        return lineHeight;
    }

    public void updateTilingManager() {
        tilingManager.updateScreenSize(this.getSize());
    }

    public int fileContentHeight() {
        return fileContentHeight;
    }
}
