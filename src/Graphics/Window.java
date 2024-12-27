package Graphics;

import Editor.TextEngine;
import LayoutManagement.Tile;
import LayoutManagement.TilingManager;
import LayoutManagement.Vec2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Window extends JFrame {

    public Dimension screenSize = getScreenDimension();

    public TilingManager tilingManager = new TilingManager(screenSize);

    private boolean isDraggingFilePath = false;

    private boolean isFullscreen = false;

    public Canvas canvas = new Canvas(screenSize, tilingManager);

    public TextEngine textEngine;

    public Window(TextEngine textEngine) {

        this.textEngine = textEngine;

        this.setTitle("Text Editor");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(screenSize);
        this.add(canvas);

        this.addAltOptions();

        this.addKeyListener();
        this.addMouseListeners();
        this.addComponentListener();

        this.setVisible(true);

    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private Dimension getScreenDimension() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    private void addAltOptions() {
        // Set macOS-specific property to use the top menu bar
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F'); // Alt + F to open the File menu

        JMenuItem load = new JMenuItem("Load");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open a file chooser dialog
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    // Get the selected file's path
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    textEngine.setCurrentFilePath(filePath);
                }
            }
        });

        // Add the "Load" menu item to the "File" menu
        fileMenu.add(load);

        // Add the "File" menu to the menu bar
        menuBar.add(fileMenu);

        // Attach the menu bar to the frame
        this.setJMenuBar(menuBar);
    }

    private void addKeyListener() {
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE -> System.exit(0);
                    case KeyEvent.VK_F10 -> toggleFullscreen();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void addMouseListeners() {
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(doesMouseInteractWithTiling(new Vec2D(e.getX(), e.getY()))) isDraggingFilePath = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDraggingFilePath = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(isDraggingFilePath) {
                    int x = tilingManager.getTextEditorTile().x;

                    tilingManager.setFilePathWindowWidth(e.getX());
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateCursor(new Vec2D(e.getX(), e.getY()));
            }
        });

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                canvas.scrollHeight -= e.getWheelRotation();

                if(canvas.scrollHeight < 0) canvas.scrollHeight = 0;
            }
        });
    }

    private void addComponentListener() {
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                canvas.updateTilingManager();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }

    private void toggleFullscreen() {
        // First, ensure the frame is hidden before making any changes
        this.setVisible(false);

        if (!isFullscreen) {
            this.dispose();
            this.setUndecorated(true);
            this.setResizable(false);
            this.setBackground(Color.black);

            // Get the screen dimensions from the `getScreenDimension()` method
            Dimension screenSize = getScreenDimension();

            // Set the window size to cover the full screen
            this.setSize(screenSize);

            // Set fullscreen mode using the GraphicsDevice
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            gd.setFullScreenWindow(this); // Make the window fullscreen

            // Make the frame visible after changes
            this.setVisible(true);

            isFullscreen = true;
        } else {
            this.dispose();
            this.setUndecorated(false);
            this.setResizable(true);
            this.setBackground(null);
            this.setSize(getScreenDimension());

            // Set the frame back to normal
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            gd.setFullScreenWindow(null); // Exit fullscreen mode

            // Make the frame visible after changes
            this.setVisible(true);

            isFullscreen = false;
        }
    }


    private boolean doesMouseInteractWithTiling(Vec2D pos) {
        Tile tile = tilingManager.getFilePathTile();

        return pos.x >= (tile.x + tile.size.width - tilingManager.hitboxSize) &&
                pos.x <= (tile.x + tile.size.width + tilingManager.hitboxSize) &&
                pos.y >= 0 &&
                pos.y <= this.getHeight();
    }

    private void updateCursor(Vec2D pos) {
        if(doesMouseInteractWithTiling(pos)) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        } else this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
