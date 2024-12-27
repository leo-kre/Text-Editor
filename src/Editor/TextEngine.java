package Editor;

import Graphics.Window;

public class TextEngine {

    public Window window;
    public FileHandler fileHandler;

    public String currentFilePath;
    public String currentFolderPath;



    public TextEngine() {
        window = new Window(this);

        fileHandler = new FileHandler();
    }

    public void setCurrentFilePath(String path) {
        this.currentFilePath = path;

        StringBuilder fileData = fileHandler.load(this.currentFilePath);

        if(fileData.isEmpty()) {
            window.showMessage("Empty File");
            return;
        }

        renderTextData(fileData);

    }

    private void renderTextData(StringBuilder data) {
        window.canvas.textData = data;
    }
}
