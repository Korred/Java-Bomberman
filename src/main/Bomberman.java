package main;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JFrame;

import network.NetworkData;

import draw.Drawable;
import draw.GamePanel;
import map.Map;
import game.Bomb;
import game.GameData;
import game.Player;

public class Bomberman {

    private Container      pane;

    public Bomberman(JFrame menuFrame, boolean fullscreen, int playerCount, String mapName) {
        GameData.frame = new JFrame();
        GameData.bomberman = this;
        if (GameData.server != null) {
            GameData.gameOver = false;
            GameData.drawables = new LinkedList<Drawable>();
            GameData.bombs = new LinkedList<Bomb>();
            GameData.players = new Vector<Player>();

            if (mapName.equals("Random")) {
                GameData.map = new Map(13, 13, playerCount == 1);
            } else {
                GameData.map = new Map(mapName);
            }
            
            GameData.players.add(new Player("Spieler 1", 1, 1, 200));
            //TODO make more than one player
            //if (!singlePlayer) GameData.players.add(new Player("Spieler 2", 11, 11, 200));
        }

        GameData.gamePanel = new GamePanel();
        pane = GameData.frame.getContentPane();
        GameData.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (fullscreen) {
            // Remove title bar etc.
            GameData.frame.setUndecorated(true);
            // Set size to fill the whole screen
            GameData.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            // Make fullscreen window stay on top of other windows
            GameData.frame.setAlwaysOnTop(true);
        }
        
        // Remove layout so things can be placed manually
        pane.setLayout(null);
        // Add GamePanel
        pane.add(GameData.gamePanel);
        // Set background color to black
        pane.setBackground(Color.BLACK);
        // add Player 1

        // Register resize/show events
        ComponentListener componentListener = new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                resizeGamePanel();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                resizeGamePanel();
            }

            @Override
            public void componentHidden(ComponentEvent e) {}

            @Override
            public void componentMoved(ComponentEvent e) {}

        };
        pane.addComponentListener(componentListener);

        KeyListener keyListener = new KeyListener() {
            // This is just for milestone 2, will be changed later

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyChar()) {

                    case 'W':
                    case 'w':
                    case 'A':
                    case 'a':
                    case 'S':
                    case 's':
                    case 'D':
                    case 'd':
                        //GameData.players.get(0).stopMove();
                    break;

                    case 'I':
                    case 'i':
                    case 'J':
                    case 'j':
                    case 'K':
                    case 'k':
                    case 'L':
                    case 'l':
                        //if (!Bomberman.singlePlayer) GameData.players.get(1).stopMove();
                    break;

                    case KeyEvent.VK_ESCAPE:
                        //exit("Spiel beendet.");
                    break;

                    default:
                    break;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                switch (e.getKeyChar()) {

                    case 'W':
                    case 'w':
                        //GameData.players.get(0).startMove(1);
                    break;

                    case 'A':
                    case 'a':
                        //GameData.players.get(0).startMove(4);
                    break;

                    case 'S':
                    case 's':
                        //GameData.players.get(0).startMove(3);
                    break;

                    case 'D':
                    case 'd':
                        //GameData.players.get(0).startMove(2);
                    break;

                    case 'E':
                    case 'e':
                        //if (GameData.players.get(0).hasBomb()) GameData.players.get(0).putBomb();
                    break;
                    default:
                    break;
                }
            }
        };
        GameData.frame.addKeyListener(keyListener);
        GameData.frame.setSize(600, 600);
        GameData.frame.setVisible(true);
        
        Thread gameLoop = new Thread() {
            public void run() {
                while (GameData.frame.isVisible()) {
                    NetworkData networkData = new NetworkData(GameData.drawables, GameData.map);
                    GameData.server.send(networkData);
                    GameData.frame.repaint();
                    try {
                        Thread.sleep(1000 / 60);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
        gameLoop.start();
    }
    
    /**
     * Resize the GamePanel so it always has maximum size while still preserving
     * aspect ratio.
     */
    public void resizeGamePanel() {
        if (GameData.networkData == null) {
            System.out.println("Waiting for server");
            return;
        }
        Map map = GameData.networkData.map;
        int mx = map.getWidth();
        int my = map.getHeight();
        int width = pane.getWidth();
        int height = pane.getHeight();
        // Calculate width required to display the image at full height while
        // preserving aspect ratio
        int maxWidth = height * mx / my;
        // If it fits on screen draw it
        if (maxWidth <= width) {
            int blackBarWidth = (width - maxWidth) / 2;
            GameData.gamePanel.setBounds(blackBarWidth, 0, maxWidth, height);
        } else {
            // If it does not fit calculate height which preserves aspect ratio
            // and use that instead
            int maxHeight = width * my / mx;
            int blackBarHeight = (height - maxHeight) / 2;
            GameData.gamePanel.setBounds(0, blackBarHeight, width, maxHeight);
        }
    }

}
