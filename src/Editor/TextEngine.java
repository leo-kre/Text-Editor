import Graphics.Window;

public class EditorEngine {

    public String currentFilePath;
    public String currentFolderPath;

    public EditorEngine() {
        Window window = new Window(this);

    }

    public void setCurrentFilePath(String path) {
        this.currentFilePath = path;

        System.out.println(this.currentFilePath);
    }
}
