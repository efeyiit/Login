package retrologin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 800;
    static final int PLAYABLE_AREA_WIDTH = 600;
    static final int PLAYABLE_AREA_HEIGHT = 500;

    static final int UNIT_SIZE = 25;

    static final int TOP_INFO_PANEL_HEIGHT = 60;

    // oyun alaninin kordinat zimbirtilari
    static final int X_OFFSET = 0;
    static final int Y_OFFSET = TOP_INFO_PANEL_HEIGHT;

    static final int SCREEN_HEIGHT = TOP_INFO_PANEL_HEIGHT + PLAYABLE_AREA_HEIGHT;


    static final int HIGH_SCORE_PANEL_WIDTH = 200;


    static final int GAME_UNITS = (PLAYABLE_AREA_WIDTH * PLAYABLE_AREA_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

    static final int DELAY = 90; // yilanin hiz ayari milisaniye basina bisiler

    final int x[] = new int[GAME_UNITS];
    final int inty[] = new int[GAME_UNITS];

    int bodyParts = 3;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean gameOver = false;
    boolean paused = false;
    boolean initialWaitingToStart = true;
    boolean waitingToStartForCurrentGame = true;

    Timer timer;
    Random random;

    private Font retroFontSmall;
    private Font retroFontMedium;
    private Font retroFontLarge;
    private Font retroFontGameOver;
    private Font retroFontTitle;

    private String username;
    private MainFrame mainFrame;

    private ArrayList<ScoreEntry> highScores = new ArrayList<>();

    private static final Color COLOR_BACKGROUND_DARK = new Color(15, 15, 30);
    private static final Color COLOR_PLAYABLE_AREA_BG = new Color(20, 20, 50);
    private static final Color COLOR_GRID_LINES = new Color(30, 30, 80);
    private static final Color COLOR_TOP_PANEL_BG = new Color(25, 25, 40);
    private static final Color COLOR_HIGH_SCORE_PANEL_BG = new Color(10, 10, 20);
    private static final Color COLOR_SEPARATOR_LINES = new Color(70, 70, 100);

    private static final Color COLOR_SNAKE_HEAD = new Color(0, 255, 0);
    private static final Color COLOR_SNAKE_BODY = new Color(0, 200, 200);
    private static final Color COLOR_APPLE = new Color(255, 69, 0);

    private static final Color COLOR_TEXT_PLAYER_SCORE = new Color(0, 255, 255);
    private static final Color COLOR_TEXT_HIGH_SCORES_TITLE = new Color(255, 255, 0);
    private static final Color COLOR_TEXT_GAME_OVER = new Color(255, 0, 0);
    private static final Color COLOR_TEXT_PAUSED = new Color(255, 255, 0);
    private static final Color COLOR_TEXT_WELCOME_TITLE = new Color(0, 255, 0);
    private static final Color COLOR_TEXT_WELCOME_PRESS_KEY = new Color(200, 200, 200);

    private static class ScoreEntry implements Comparable<ScoreEntry> {
        String username;
        int score;

        public ScoreEntry(String username, int score) {
            this.username = username;
            this.score = score;
        }

        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score);
        }
    }

    public GamePanel(MainFrame mainFrame) {
        random = new Random();
        this.mainFrame = mainFrame;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(COLOR_BACKGROUND_DARK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        retroFontSmall = RetroArcadeFontLoader.loadPixelFont(16f);
        retroFontMedium = RetroArcadeFontLoader.loadPixelFont(24f);
        retroFontLarge = RetroArcadeFontLoader.loadPixelFont(36f);
        retroFontGameOver = RetroArcadeFontLoader.loadPixelFont(50f);
        retroFontTitle = RetroArcadeFontLoader.loadPixelFont(45f);
    }

    public void setWelcomeText(String username) {
        this.username = username;
        initialWaitingToStart = true;
        waitingToStartForCurrentGame = true;
        startGame();
    }

    public void startGame() {
        applesEaten = 0;
        bodyParts = 3;
        direction = 'R';
        running = false;
        gameOver = false;
        paused = false;
        if (!initialWaitingToStart) { // ilk kez acilmiosa oyun ekrani direkt baslasin oyun die
            waitingToStartForCurrentGame = false;
            running = true;
        } else {
            waitingToStartForCurrentGame = true;
        }


        x[0] = X_OFFSET;
        inty[0] = Y_OFFSET;
        for (int i = 1; i < bodyParts; i++) {
            x[i] = x[0] - i * UNIT_SIZE;
            inty[i] = inty[0];
        }

        newApple();

        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        if (running) {
            timer.start();
        }
        repaint();
    }

    private void resumeGame() {
        initialWaitingToStart = false;
        waitingToStartForCurrentGame = false;
        running = true;
        timer.start();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.setColor(COLOR_TOP_PANEL_BG);
        g.fillRect(0, 0, SCREEN_WIDTH, TOP_INFO_PANEL_HEIGHT);

        g.setColor(COLOR_TEXT_PLAYER_SCORE);
        g.setFont(retroFontMedium);
        String playerText = "PLAYER: " + (username != null ? username.toUpperCase() : "GUEST");
        String scoreText = "SCORE: " + applesEaten;

        FontMetrics playerMetrics = getFontMetrics(retroFontMedium);
        int textY = TOP_INFO_PANEL_HEIGHT / 2 + playerMetrics.getAscent() / 2;

        g.drawString(playerText, 20, textY);
        g.drawString(scoreText, PLAYABLE_AREA_WIDTH - playerMetrics.stringWidth(scoreText) - 20, textY);

        g.setColor(COLOR_HIGH_SCORE_PANEL_BG);
        g.fillRect(SCREEN_WIDTH - HIGH_SCORE_PANEL_WIDTH, 0, HIGH_SCORE_PANEL_WIDTH, SCREEN_HEIGHT);

        g.setColor(COLOR_SEPARATOR_LINES);
        g.fillRect(PLAYABLE_AREA_WIDTH, 0, 2, SCREEN_HEIGHT);
        g.fillRect(0, TOP_INFO_PANEL_HEIGHT, SCREEN_WIDTH - HIGH_SCORE_PANEL_WIDTH, 2);

        g.setColor(COLOR_PLAYABLE_AREA_BG);
        g.fillRect(X_OFFSET, Y_OFFSET, PLAYABLE_AREA_WIDTH, PLAYABLE_AREA_HEIGHT);


        if (initialWaitingToStart) { // bi kerelik bekleme ekrani olcak baslaamk icin tusa basin yazsiis yani
            drawWaitingToStartScreen(g);
        } else if (running) {
            g.setColor(COLOR_GRID_LINES);
            for (int i = 0; i <= PLAYABLE_AREA_WIDTH / UNIT_SIZE; i++) {
                g.drawLine(X_OFFSET + i * UNIT_SIZE, Y_OFFSET, X_OFFSET + i * UNIT_SIZE, Y_OFFSET + PLAYABLE_AREA_HEIGHT);
            }
            for (int i = 0; i <= PLAYABLE_AREA_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(X_OFFSET, Y_OFFSET + i * UNIT_SIZE, X_OFFSET + PLAYABLE_AREA_WIDTH, Y_OFFSET + i * UNIT_SIZE);
            }

            g.setColor(COLOR_APPLE);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(COLOR_SNAKE_HEAD);
                    g.fillRect(x[i], inty[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(COLOR_SNAKE_BODY);
                    g.fillRect(x[i], inty[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
        } else if (gameOver) {
            gameOverScreen(g);
        } else if (paused) {
            drawPausedScreen(g);
        }

        // skor panelini siralayan ve cizen kodlar
        drawHighScorePanel(g);
    }

    private void drawHighScorePanel(Graphics g) {
        int panelX = SCREEN_WIDTH - HIGH_SCORE_PANEL_WIDTH;

        g.setColor(COLOR_TEXT_HIGH_SCORES_TITLE);
        g.setFont(retroFontSmall);
        String highScoresTitle = "HIGH SCORES";
        int highScoresTitleWidth = getFontMetrics(g.getFont()).stringWidth(highScoresTitle);
        g.drawString(highScoresTitle, panelX + (HIGH_SCORE_PANEL_WIDTH - highScoresTitleWidth) / 2, g.getFont().getSize() + 20);

        int scoreYOffset = g.getFont().getSize() + 40;
        int displayCount = 0;
        g.setColor(COLOR_TEXT_PLAYER_SCORE);
        for (ScoreEntry entry : highScores) {
            if (displayCount >= 10) break;
            String scoreLine = (displayCount + 1) + ". " + entry.username.toUpperCase() + ": " + entry.score;
            int scoreLineWidth = getFontMetrics(g.getFont()).stringWidth(scoreLine);
            g.drawString(scoreLine, panelX + (HIGH_SCORE_PANEL_WIDTH - scoreLineWidth) / 2, scoreYOffset);
            scoreYOffset += g.getFont().getSize() + 5;
            displayCount++;
        }
    }

    public void drawWaitingToStartScreen(Graphics g) {
        //
        int playableAreaCenterX = X_OFFSET + PLAYABLE_AREA_WIDTH / 2;
        int playableAreaCenterY = Y_OFFSET + PLAYABLE_AREA_HEIGHT / 2;

        g.setColor(COLOR_TEXT_WELCOME_TITLE);
        g.setFont(retroFontTitle);
        FontMetrics metricsTitle = getFontMetrics(g.getFont());
        String gameTitle = "RETRO SNAKE";
        int titleX = playableAreaCenterX - metricsTitle.stringWidth(gameTitle) / 2;
        int titleY = playableAreaCenterY - metricsTitle.getAscent() / 2 - 30;
        g.drawString(gameTitle, titleX, titleY);

        g.setColor(COLOR_TEXT_WELCOME_PRESS_KEY);
        g.setFont(retroFontLarge);
        FontMetrics metrics = getFontMetrics(g.getFont());
        String pressKeyText = "Press Any Key to Start";
        int pressKeyX = playableAreaCenterX - metrics.stringWidth(pressKeyText) / 2;
        int pressKeyY = titleY + metricsTitle.getHeight() + 20;
        g.drawString(pressKeyText, pressKeyX, pressKeyY);
    }

    public void newApple() {
        // elmayi rasgele olustursun die
        appleX = X_OFFSET + random.nextInt(PLAYABLE_AREA_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = Y_OFFSET + random.nextInt(PLAYABLE_AREA_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void checkApple() {
        if ((x[0] == appleX) && (inty[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            inty[i] = inty[i - 1];
        }

        switch (direction) {
            case 'U':
                inty[0] = inty[0] - UNIT_SIZE;
                break;
            case 'D':
                inty[0] = inty[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (inty[0] == inty[i])) {
                running = false;
                gameOver = true;
            }
        }
        // duvara carparsa yani oyun sinirlarini gecerse oyun bitsin kosulu
        if (x[0] < X_OFFSET || x[0] >= X_OFFSET + PLAYABLE_AREA_WIDTH ||
            inty[0] < Y_OFFSET || inty[0] >= Y_OFFSET + PLAYABLE_AREA_HEIGHT) {
            running = false;
            gameOver = true;
        }

        if (!running) {
            timer.stop();
            if (gameOver && username != null && !username.isEmpty()) {
                highScores.add(new ScoreEntry(this.username, this.applesEaten));
                Collections.sort(highScores);
            }
        }
    }

    public void gameOverScreen(Graphics g) {
        //
        int playableAreaCenterX = X_OFFSET + PLAYABLE_AREA_WIDTH / 2;
        int playableAreaCenterY = Y_OFFSET + PLAYABLE_AREA_HEIGHT / 2;

        g.setColor(COLOR_TEXT_GAME_OVER);
        g.setFont(retroFontLarge);
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        String gameOverText = "GAME OVER!";
        int goTextX = playableAreaCenterX - metrics1.stringWidth(gameOverText) / 2;
        int goTextY = playableAreaCenterY - metrics1.getAscent() / 2 - 30;
        g.drawString(gameOverText, goTextX, goTextY);

        g.setColor(COLOR_TEXT_PLAYER_SCORE);
        g.setFont(retroFontMedium);
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        String scoreText = "Score: " + applesEaten;
        int scoreTextX = playableAreaCenterX - metrics2.stringWidth(scoreText) / 2;
        int scoreTextY = goTextY + metrics1.getHeight() + 10;
        g.drawString(scoreText, scoreTextX, scoreTextY);

        g.setColor(COLOR_TEXT_HIGH_SCORES_TITLE);
        g.setFont(retroFontSmall);
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        String restartText = "Press 'R' to Restart or 'ESC' to Logout";
        int restartTextX = playableAreaCenterX - metrics3.stringWidth(restartText) / 2;
        int restartTextY = scoreTextY + metrics2.getHeight() + 10;
        g.drawString(restartText, restartTextX, restartTextY);
    }

    public void drawPausedScreen(Graphics g) {
        //
        int playableAreaCenterX = X_OFFSET + PLAYABLE_AREA_WIDTH / 2;
        int playableAreaCenterY = Y_OFFSET + PLAYABLE_AREA_HEIGHT / 2;

        g.setColor(COLOR_TEXT_PAUSED);
        g.setFont(retroFontGameOver);
        FontMetrics metrics = getFontMetrics(g.getFont());
        String pauseText = "PAUSED";
        int pauseTextX = playableAreaCenterX - metrics.stringWidth(pauseText) / 2;
        int pauseTextY = playableAreaCenterY - metrics.getAscent() / 2;
        g.drawString(pauseText, pauseTextX, pauseTextY);

        g.setColor(COLOR_TEXT_PLAYER_SCORE);
        g.setFont(retroFontMedium);
        String resumeText = "Press 'P' to Resume";
        int resumeTextX = playableAreaCenterX - getFontMetrics(g.getFont()).stringWidth(resumeText) / 2;
        int resumeTextY = pauseTextY + metrics.getHeight() + 20;
        g.drawString(resumeText, resumeTextX, resumeTextY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // bi tusa basip oyunu baslatma komutlari
            if (initialWaitingToStart) {
                resumeGame();
                return;
            }

            if (gameOver || paused || !running) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_R:
                        if (gameOver) {
                            initialWaitingToStart = false;
                            startGame();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (gameOver || paused || !running) {
                            if (timer != null) {
                                timer.stop();
                            }
                            initialWaitingToStart = true;
                            mainFrame.showLoginPanel();
                        }
                        break;
                    case KeyEvent.VK_P:
                        if (!gameOver && !waitingToStartForCurrentGame) {
                            paused = !paused;
                            if (paused) {
                                timer.stop();
                            } else {
                                timer.start();
                            }
                        }
                        break;
                }
                return;
            }
            // yilanin kotnroller
            if (running && !paused) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') {
                            direction = 'U';
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') {
                            direction = 'D';
                        }
                        break;
                }
            }
        }
    }
}