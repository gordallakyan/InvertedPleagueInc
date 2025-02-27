package view;

import controller.GameController;
import model.Country;
import model.TransportConnection;
import model.AirConnection;
import model.RoadConnection;
import model.SeaConnection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import javax.swing.Timer;

public class GameView extends JFrame {
    private GameController controller;
    private List<Country> countries;
    private List<TransportConnection> connections;
    private BufferedImage worldMap;
    private BufferedImage planeIcon;
    private BufferedImage carIcon;
    private BufferedImage cruiseIcon;
    private final int BASE_WIDTH = 1332;
    private final int BASE_HEIGHT = 768;
    private JLabel timeLabel;
    private JLabel pointsLabel;
    private JLabel scoreLabel;
    private Timer animationTimer;
    private List<TransportAnimation> activeAnimations = new ArrayList<>();

    public GameView(GameController controller, List<Country> countries, List<TransportConnection> connections) {
        super("Coronavirus AntiPlague - Game");
        this.controller = controller;
        this.countries = countries;
        this.connections = connections;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(BASE_WIDTH, BASE_HEIGHT);
        setResizable(true);
        try {
            InputStream mapStream   = getClass().getResourceAsStream("/images/mapka.jpg");
            InputStream planeStream = getClass().getResourceAsStream("/images/plane.png");
            InputStream carStream   = getClass().getResourceAsStream("/images/car.png");
            InputStream cruiseStream= getClass().getResourceAsStream("/images/cruise.png");
            if (mapStream != null)    worldMap   = ImageIO.read(mapStream);
            if (planeStream != null)  planeIcon  = ImageIO.read(planeStream);
            if (carStream != null)    carIcon    = ImageIO.read(carStream);
            if (cruiseStream != null) cruiseIcon = ImageIO.read(cruiseStream);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading images!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        JPanel mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                float scaleX = (float) getWidth()  / (float) BASE_WIDTH;
                float scaleY = (float) getHeight() / (float) BASE_HEIGHT;
                g2d.scale(scaleX, scaleY);
                if (worldMap != null) {
                    g2d.drawImage(worldMap, 0, 0, BASE_WIDTH, BASE_HEIGHT, null);
                }
                g2d.setColor(Color.BLUE);
                for (TransportConnection conn : connections) {
                    if (conn.isOpen()) {
                        int x1 = conn.getFrom().getX() + 15;
                        int y1 = conn.getFrom().getY() + 15;
                        int x2 = conn.getTo().getX()   + 15;
                        int y2 = conn.getTo().getY()   + 15;
                        g2d.drawLine(x1, y1, x2, y2);
                    }
                }
                for (Country c : countries) {
                    c.draw(g2d);
                }
                for (TransportAnimation anim : activeAnimations) {
                    BufferedImage img = null;
                    if (anim.conn instanceof AirConnection) {
                        img = planeIcon;
                    } else if (anim.conn instanceof RoadConnection) {
                        img = carIcon;
                    } else if (anim.conn instanceof SeaConnection) {
                        img = cruiseIcon;
                    }
                    if (img != null) {
                        g2d.drawImage(img, (int)anim.x, (int)anim.y, 32, 32, null);
                    }
                }
                g2d.dispose();
            }
        };
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeLabel = new JLabel("Time: 0s");
        pointsLabel = new JLabel("Points: 0");
        scoreLabel = new JLabel("Infected: ?");
        topPanel.add(timeLabel);
        topPanel.add(pointsLabel);
        topPanel.add(scoreLabel);
        JButton upgradesButton = new JButton("Upgrades");
        topPanel.add(upgradesButton);
        upgradesButton.addActionListener(e -> {
            UpgradesDialog dialog = new UpgradesDialog(this, controller);
            dialog.setVisible(true);
        });
        add(topPanel, BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_Q) {
                    controller.backToMainMenu(GameView.this);
                }
            }
        });
        mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                float scaleX = (float) mapPanel.getWidth()  / (float) BASE_WIDTH;
                float scaleY = (float) mapPanel.getHeight() / (float) BASE_HEIGHT;
                int realX = (int)(e.getX() / scaleX);
                int realY = (int)(e.getY() / scaleY);
                Country clicked = getCountryAt(realX, realY);
                if (clicked != null) {
                    controller.countryClicked(clicked, GameView.this);
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();
        startAnimationTimer();
    }

    private Country getCountryAt(int realX, int realY) {
        for (Country c : countries) {
            if (realX >= c.getX() && realX < c.getX() + 30
                    && realY >= c.getY() && realY < c.getY() + 30) {
                return c;
            }
        }
        return null;
    }

    public void updateTime(int seconds) {
        timeLabel.setText("Time: " + seconds + "s");
    }

    public void updatePoints(long pts) {
        pointsLabel.setText("Points: " + pts);
    }

    public void updateStatus(String text) {
        scoreLabel.setText(text);
    }

    public void refreshMap() {
        repaint();
    }

    private void startAnimationTimer() {
        animationTimer = new Timer(3000, e -> {
            TransportConnection conn = getRandomOpenConnection();
            if (conn != null) {
                startTransportAnimation(conn);
            }
        });
        animationTimer.setRepeats(true);
        animationTimer.start();
    }

    private TransportConnection getRandomOpenConnection() {
        List<TransportConnection> openConns = new ArrayList<>();
        for (TransportConnection c : connections) {
            if (c.isOpen()) {
                openConns.add(c);
            }
        }
        if (openConns.isEmpty()) return null;
        return openConns.get(new Random().nextInt(openConns.size()));
    }

    private void startTransportAnimation(TransportConnection conn) {
        int x1 = conn.getFrom().getX() + 15;
        int y1 = conn.getFrom().getY() + 15;
        int x2 = conn.getTo().getX()   + 15;
        int y2 = conn.getTo().getY()   + 15;
        double dx = (x2 - x1) / 20.0;
        double dy = (y2 - y1) / 20.0;
        TransportAnimation anim = new TransportAnimation(conn, x1, y1, dx, dy);
        activeAnimations.add(anim);
        Timer movement = new Timer(100, new ActionListener() {
            int steps = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                anim.x += anim.dx;
                anim.y += anim.dy;
                refreshMap();
                steps++;
                if (steps >= 20) {
                    ((Timer)e.getSource()).stop();
                    activeAnimations.remove(anim);
                    refreshMap();
                }
            }
        });
        movement.setRepeats(true);
        movement.start();
    }

    private static class TransportAnimation {
        TransportConnection conn;
        double x, y, dx, dy;
        TransportAnimation(TransportConnection conn, double x, double y, double dx, double dy) {
            this.conn = conn;
            this.x = x;
            this.y = y;
            this.dx = dx;
            this.dy = dy;
        }
    }
}
