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

    private final Color backgroundColor = new Color(23, 24, 32);
    private final Color accentColor = new Color(58, 58, 75);

    private final Color darkText = new Color(83, 83, 109);

    private final Font editorFont = new Font("Consolas", Font.PLAIN, 20);
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

        g2d.setColor(backgroundColor);
        g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

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

        if(textEngine.formattedData == null) return;

        textEngine.fileLineLength = textEngine.formattedData.length;

        fileContentHeight = textEngine.formattedData.length * getLineHeight(g2d, editorFont);

        g2d.setColor(darkText);

        for (int line = 0; line < textEngine.formattedData.length; line++) {

            int lineX = x + 5;
            int lineY = y + 50 - textEngine.scrollHeight;

            if(lineY >= (-textEngine.outOfScreenRenderBuffer) && lineY <= (this.getHeight() + textEngine.outOfScreenRenderBuffer)) {
                g2d.drawString(String.valueOf(line + 1), lineX, lineY);
            }

            y += textEngine.lineSpacing;
        }

        y = textEditor.y;

        g2d.setColor(Color.white);

        for (int line = 0; line < textEngine.formattedData.length; line++) {

            int lineX = x + textEngine.lineHeight * 2;
            int lineY = y + 50 - textEngine.scrollHeight;

            g2d.setColor(Color.red);
            if(line == textEngine.cursorPosition.y) {

                if(textEngine.formattedData[line].length == 0) {
                    g2d.fillRect(lineX, lineY - textEngine.lineHeight, 10, textEngine.lineHeight + 7);
                } else {
                    String l = new String(textEngine.formattedData[line], 0, textEngine.cursorPosition.x);
                    int X = x + textEngine.lineHeight * 2 + stringWidth(g2d, editorFont, l);

                    int w = stringWidth(g2d, editorFont, String.valueOf(textEngine.formattedData[line][textEngine.cursorPosition.x]));

                    g2d.fillRect(X, lineY - textEngine.lineHeight, 2, textEngine.lineHeight + 7);
                }
            }

            g2d.setColor(Color.white);

            if(lineY >= (-textEngine.outOfScreenRenderBuffer) && lineY <= (this.getHeight() + textEngine.outOfScreenRenderBuffer)) {
                g2d.drawChars(textEngine.formattedData[line],0, textEngine.formattedData[line].length, lineX, lineY);
            }

            y += textEngine.lineSpacing;
        }

        g2d.setColor(backgroundColor);
        g2d.fillRect(textEditor.x, this.getHeight() - 25, textEditor.size.width, 25);

        g2d.setColor(accentColor);
        g2d.fillRect(textEditor.x, this.getHeight() - 25, textEditor.size.width, tilingManager.hitboxSize * 2);

        g2d.setFont(utilFont);
        g2d.setColor(darkText);
        g2d.drawString((textEngine.currentFilePath + " - lines: " + textEngine.fileLineLength), textEditor.x, this.getHeight() - 10);
    }

    private void renderFilePath(Graphics2D g2d) {
        g2d.setColor(backgroundColor);

        Tile filePath = tilingManager.getFilePathTile();

        g2d.fillRect(filePath.x, filePath.y, filePath.size.width, filePath.size.height);

        g2d.setColor(accentColor);
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

    public void updateCurrentLineLength() {
        textEngine.selectedLineLength = textEngine.formattedData[textEngine.cursorPosition.y].length;
    }

    public int fileContentHeight() {
        return fileContentHeight;
    }

    private int stringWidth(Graphics2D g2d, Font font, String string) {
        return g2d.getFontMetrics(font).stringWidth(string);
    }

    private int stringHeight(Graphics2D g2d, Font font) {
        return g2d.getFontMetrics(font).getHeight();
    }
}
