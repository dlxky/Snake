import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * The main game window for the Snake Game application.
 * Creates and manages the game panel and window properties.
 */
public class SnakeGame extends JFrame {

    /**
     * Constructs the game window and initializes the game panel.
     */
    public SnakeGame() {
        add(new GamePanel());
        setTitle("Snake Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Main method to launch the game.
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        new SnakeGame();
    }
}

/**
 * The main game panel that handles game logic, rendering, and user input.
 * Implements the core gameplay mechanics including snake movement, collision detection,
 * food spawning, and score tracking.
 */
class GamePanel extends JPanel implements ActionListener, KeyListener {
    /** Width of the game window in pixels */
    private static final int WIDTH = 600;
    /** Total height of the game window including scoreboard */
    private static final int HEIGHT = 650;
    /** Size of each game grid tile in pixels */
    private static final int TILE_SIZE = 25;
    /** Delay between game updates in milliseconds */
    private static final int DELAY = 100;
    /** Height of the scoreboard area at the top */
    private static final int SCOREBOARD_HEIGHT = 50;

    /** List of points representing snake body segments */
    private final ArrayList<Point> snake = new ArrayList<>();
    /** Current food position */
    private Point food;
    /** Type of current food (normal, gold, bad) */
    private String foodType = "normal";
    /** Current movement direction (U, D, L, R) */
    private char direction = 'R';
    /** Game running state flag */
    private boolean running = false;
    /** Timer for game updates */
    private javax.swing.Timer timer;
    /** Timer for game duration tracking */
    private javax.swing.Timer gameTimer;
    /** Current player score */
    private int score = 0;
    /** Timestamp when game started */
    private long startTime;

    /**
     * Constructs the game panel and initializes game state.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        startGame();
    }

    /**
     * Resets and starts a new game session.
     * Initializes snake position, score, timers, and game state.
     */
    private void startGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        spawnFood();
        direction = 'R';
        running = true;
        score = 0;
        startTime = System.currentTimeMillis();

        if (timer != null) timer.stop();
        if (gameTimer != null) gameTimer.stop();
        
        timer = new javax.swing.Timer(DELAY, this);
        timer.start();
        
        gameTimer = new javax.swing.Timer(1000, e -> repaint());
        gameTimer.start();
    }

    /**
     * Generates new food at a random position with random type.
     * Food types have different probabilities:
     * - 10% chance for bad (purple) food
     * - 10% chance for gold food
     * - 80% chance for normal (red) food
     */
    private void spawnFood() {
        Random rand = new Random();
        int x = rand.nextInt(WIDTH / TILE_SIZE);
        int y = rand.nextInt((HEIGHT - SCOREBOARD_HEIGHT) / TILE_SIZE);
        double chance = rand.nextDouble();
        
        if (chance < 0.1) {
            foodType = "bad";
        } else if (chance < 0.2) {
            foodType = "gold";
        } else {
            foodType = "normal";
        }
        food = new Point(x, y);
    }

    /**
     * Updates snake position and checks for collisions.
     * Handles food consumption effects:
     * - Normal food: +1 score, grow by 1 segment
     * - Gold food: +3 score, grow by 1 segment
     * - Bad food: -5 score, shrink by 2 segments
     */
    private void move() {
        if (!running) return;

        Point head = new Point(snake.get(0));
        switch (direction) {
            case 'U' -> head.y--;
            case 'D' -> head.y++;
            case 'L' -> head.x--;
            case 'R' -> head.x++;
        }

        // Wall collision check
        if (head.x < 0 || head.x >= WIDTH/TILE_SIZE || 
            head.y < 0 || head.y >= (HEIGHT - SCOREBOARD_HEIGHT)/TILE_SIZE) {
            gameOver();
            return;
        }

        // Self collision check
        for (Point body : snake) {
            if (head.equals(body)) {
                gameOver();
                return;
            }
        }

        snake.add(0, head);

        // Food consumption
        if (head.equals(food)) {
            switch (foodType) {
                case "normal" -> score += 1;
                case "gold" -> score += 3;
                case "bad" -> {
                    score = Math.max(0, score - 5);
                    if (snake.size() > 1) {
                        snake.remove(snake.size() - 1);
                        if (snake.size() > 1) snake.remove(snake.size() - 1);
                    }
                }
            }
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    /**
     * Handles game over condition.
     * Stops timers, shows final score, and restarts the game.
     */
    private void gameOver() {
        running = false;
        timer.stop();
        gameTimer.stop();
        JOptionPane.showMessageDialog(this, "Game Over! Score: " + score);
        startGame();
    }

    /**
     * Game loop callback that updates game state and repaints.
     * @param e ActionEvent from game timer
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    /**
     * Renders all game components.
     * @param g Graphics object for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw scoreboard
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, SCOREBOARD_HEIGHT);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 20, 30);
        
        long elapsedMillis = System.currentTimeMillis() - startTime;
        String time = String.format("Time: %02d:%02d", 
            (elapsedMillis / 60000) % 60, 
            (elapsedMillis / 1000) % 60);
        g.drawString(time, WIDTH - 150, 30);

        // Draw snake
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * TILE_SIZE, SCOREBOARD_HEIGHT + p.y * TILE_SIZE, TILE_SIZE - 2, TILE_SIZE - 2);
        }

        // Draw food with appropriate color
        switch (foodType) {
            case "bad" -> g.setColor(new Color(128, 0, 128)); // Purple
            case "gold" -> g.setColor(Color.YELLOW);
            default -> g.setColor(Color.RED);
        }
        g.fillRect(food.x * TILE_SIZE, SCOREBOARD_HEIGHT + food.y * TILE_SIZE, TILE_SIZE - 2, TILE_SIZE - 2);
    }

    /**
     * Handles keyboard input for snake direction control.
     * @param e KeyEvent containing pressed key information
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                if (direction != 'D') direction = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') direction = 'D';
                break;
            case KeyEvent.VK_LEFT:
                if (direction != 'R') direction = 'L';
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') direction = 'R';
                break;
        }
    }

    /** Unused key listener methods */
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}