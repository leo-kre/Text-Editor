package Components;

import java.awt.*;

public class TilingManager {

    private Dimension screenSize;

    public TilingManager(Dimension screenSize) {
        this.screenSize = screenSize;
    }

    public void updateScreenSize(Dimension screenSize) {
        this.screenSize = screenSize;
    }
}
