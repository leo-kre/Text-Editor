package LayoutManagement;

import java.awt.*;

public class TilingManager {

    private Dimension screenSize;

    public final int hitboxSize = 3;

    private Tile textEditorTile;
    private Tile filePathTile;

    private boolean showFilePath = true;
    private int filePathWindowWidth = 200;

    public TilingManager(Dimension screenSize) {
        this.screenSize = screenSize;

        textEditorTile = new Tile(filePathWindowWidth, 0, new Dimension(screenSize.width - filePathWindowWidth, screenSize.height));
        filePathTile = new Tile(0, 0, new Dimension(filePathWindowWidth, screenSize.height));
    }

    public void updateScreenSize(Dimension screenSize) {
        this.screenSize = screenSize;

        this.updateTiling();
    }

    public void setFilePathWindowWidth(int width) {
        this.filePathWindowWidth = width;
        this.updateTiling();
    }

    private void updateTiling() {
        textEditorTile = new Tile(filePathWindowWidth, 0, new Dimension(screenSize.width - filePathWindowWidth, screenSize.height));
        filePathTile = new Tile(0, 0, new Dimension(filePathWindowWidth, screenSize.height));
    }

    public Tile getTextEditorTile() {
        return textEditorTile;
    }

    public Tile getFilePathTile() {
        return filePathTile;
    }

}

