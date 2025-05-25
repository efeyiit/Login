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

    // --- Oyun Ekranının TOPLAM Boyutları ---
    static final int SCREEN_WIDTH = 800;
    static final int PLAYABLE_AREA_WIDTH = 600;
    static final int PLAYABLE_AREA_HEIGHT = 500; // Oynanabilir alanın yüksekliği

    static final int UNIT_SIZE = 25;

    // Üstteki bilgi çubuğu için yükseklik
    static final int TOP_INFO_PANEL_HEIGHT = 60; // Yeni, daha kısa yükseklik

    // Oynanabilir Alanın Ofsetleri (Y koordinatı)
    static final int X_OFFSET = 0;
    static final int Y_OFFSET = TOP_INFO_PANEL_HEIGHT; // Oyun alanı, bilgi panelinin hemen altında başlayacak

    // TOPLAM EKRAN YÜKSEKLİĞİ: Üst bilgi paneli + Oynanabilir alan yüksekliği
    // JFrame'in kenarlıkları nedeniyle biraz daha fazla yer bırakabiliriz
    static final int SCREEN_HEIGHT = TOP_INFO_PANEL_HEIGHT + PLAYABLE_AREA_HEIGHT;


    // Sağ taraftaki skor paneli için genişlik
    static final int HIGH_SCORE_PANEL_WIDTH = 200;


    static final int GAME_UNITS = (PLAYABLE_AREA_WIDTH * PLAYABLE_AREA_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

    static final int DELAY = 90;

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
    // Bu bayrağı yalnızca ilk açılış için kullanacağız
    boolean initialWaitingToStart = true;
    boolean waitingToStartForCurrentGame = true; // Her yeni oyun başlangıcı için

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

    // --- RETRO RENK PALETİ TANIMLAMALARI ---
    private static final Color COLOR_BACKGROUND_DARK = new Color(15, 15, 30); // Çok koyu mavi/mor
    private static final Color COLOR_PLAYABLE_AREA_BG = new Color(20, 20, 50); // Koyu Mavi/Mor tonu (Retro Arka Plan)
    private static final Color COLOR_GRID_LINES = new Color(30, 30, 80); // Daha koyu ve belirgin ızgara (retro)
    private static final Color COLOR_TOP_PANEL_BG = new Color(25, 25, 40); // Üst panelin arka planı
    private static final Color COLOR_HIGH_SCORE_PANEL_BG = new Color(10, 10, 20); // Yüksek skor panelinin arka planı
    private static final Color COLOR_SEPARATOR_LINES = new Color(70, 70, 100); // Ayırıcı çizgiler

    private static final Color COLOR_SNAKE_HEAD = new Color(0, 255, 0); // Parlak Yeşil (Yılan Başı)
    private static final Color COLOR_SNAKE_BODY = new Color(0, 200, 200); // Daha açık Camgöbeği (Yılan Gövdesi)
    private static final Color COLOR_APPLE = new Color(255, 69, 0); // Parlak Turuncu (Retro Elma)

    private static final Color COLOR_TEXT_PLAYER_SCORE = new Color(0, 255, 255); // Canlı Camgöbeği
    private static final Color COLOR_TEXT_HIGH_SCORES_TITLE = new Color(255, 255, 0); // Parlak Sarı
    private static final Color COLOR_TEXT_GAME_OVER = new Color(255, 0, 0); // Parlak Kırmızı
    private static final Color COLOR_TEXT_PAUSED = new Color(255, 255, 0); // Parlak Sarı
    private static final Color COLOR_TEXT_WELCOME_TITLE = new Color(0, 255, 0); // Parlak Yeşil (Retro Snake Başlığı)
    private static final Color COLOR_TEXT_WELCOME_PRESS_KEY = new Color(200, 200, 200); // Açık gri (Press Any Key)

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
        // Panelin tercih edilen boyutunu tam olarak hesaplanan SCREEN_WIDTH ve SCREEN_HEIGHT olarak ayarla
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        // Genel arka planı daha koyu bir siyaha yakın renk yapalım
        this.setBackground(COLOR_BACKGROUND_DARK); // Retro için koyu gri/siyah
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
        // Kullanıcı adı ayarlandığında, ilk başlangıç ekranını göster
        initialWaitingToStart = true;
        waitingToStartForCurrentGame = true;
        startGame(); // startGame çağrısı zaten başlangıç durumuna getiriyor
    }

    public void startGame() {
        applesEaten = 0;
        bodyParts = 6;
        direction = 'R';
        running = false;
        gameOver = false;
        paused = false;
        // Sadece ilk açılışta veya 'R' ile yeniden başlatıldığında bekleme ekranını göster
        if (!initialWaitingToStart) { // Eğer ilk açılış değilse, oyun hemen başlasın
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
        if (running) { // Eğer oyun hemen başlayacaksa timer'ı çalıştır
            timer.start();
        }
        repaint();
    }

    private void resumeGame() {
        // initialWaitingToStart'ı false yap, böylece bir daha bu mesajı görmeyiz
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
        // --- Üstteki Bilgi Paneli (Player ve Score) ---
        g.setColor(COLOR_TOP_PANEL_BG);
        g.fillRect(0, 0, SCREEN_WIDTH, TOP_INFO_PANEL_HEIGHT);

        // Player ve Mevcut Skor yazıları
        g.setColor(COLOR_TEXT_PLAYER_SCORE); // Canlı Camgöbeği rengi
        g.setFont(retroFontMedium);
        String playerText = "PLAYER: " + (username != null ? username.toUpperCase() : "GUEST");
        String scoreText = "SCORE: " + applesEaten;

        FontMetrics playerMetrics = getFontMetrics(retroFontMedium);
        // Yazıların dikeyde ortalanması
        int textY = TOP_INFO_PANEL_HEIGHT / 2 + playerMetrics.getAscent() / 2;

        g.drawString(playerText, 20, textY);
        g.drawString(scoreText, PLAYABLE_AREA_WIDTH - playerMetrics.stringWidth(scoreText) - 20, textY);

        // --- Yüksek Skorlar Paneli (Ekranın en sağında) ---
        g.setColor(COLOR_HIGH_SCORE_PANEL_BG);
        g.fillRect(SCREEN_WIDTH - HIGH_SCORE_PANEL_WIDTH, 0, HIGH_SCORE_PANEL_WIDTH, SCREEN_HEIGHT);

        // Paneller arası ince çizgiler (Daha belirgin hale getirildi)
        g.setColor(COLOR_SEPARATOR_LINES); // Açık gri bir çizgi
        g.fillRect(PLAYABLE_AREA_WIDTH, 0, 2, SCREEN_HEIGHT); // Yüksek skor paneli ile oyun alanı arası
        g.fillRect(0, TOP_INFO_PANEL_HEIGHT, SCREEN_WIDTH - HIGH_SCORE_PANEL_WIDTH, 2); // Üst panel ile oyun alanı arası

        // Oyun alanının arka planını her zaman çiz
        g.setColor(COLOR_PLAYABLE_AREA_BG);
        g.fillRect(X_OFFSET, Y_OFFSET, PLAYABLE_AREA_WIDTH, PLAYABLE_AREA_HEIGHT);


        if (initialWaitingToStart) { // Yalnızca ilk açılışta bekleme ekranını göster
            drawWaitingToStartScreen(g);
        } else if (running) {
            // Izgara Çizgilerini Sadece Oynanabilir Alan İçinde Çiz
            g.setColor(COLOR_GRID_LINES); // Daha koyu ve belirgin ızgara (retro)
            for (int i = 0; i <= PLAYABLE_AREA_WIDTH / UNIT_SIZE; i++) {
                g.drawLine(X_OFFSET + i * UNIT_SIZE, Y_OFFSET, X_OFFSET + i * UNIT_SIZE, Y_OFFSET + PLAYABLE_AREA_HEIGHT);
            }
            for (int i = 0; i <= PLAYABLE_AREA_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(X_OFFSET, Y_OFFSET + i * UNIT_SIZE, X_OFFSET + PLAYABLE_AREA_WIDTH, Y_OFFSET + i * UNIT_SIZE);
            }

            // Elmayı çiz
            g.setColor(COLOR_APPLE); // Parlak Turuncu (Retro Elma)
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Yılanı çiz
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(COLOR_SNAKE_HEAD); // Parlak Yeşil (Yılan Başı)
                    g.fillRect(x[i], inty[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(COLOR_SNAKE_BODY); // Daha açık Camgöbeği (Yılan Gövdesi)
                    g.fillRect(x[i], inty[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
        } else if (gameOver) {
            gameOverScreen(g);
        } else if (paused) {
            drawPausedScreen(g);
        }

        // Yüksek skor panelini çiz (en sağda)
        drawHighScorePanel(g);
    }

    // Yüksek skor panelini çizen metod
    private void drawHighScorePanel(Graphics g) {
        int panelX = SCREEN_WIDTH - HIGH_SCORE_PANEL_WIDTH;

        g.setColor(COLOR_TEXT_HIGH_SCORES_TITLE); // Parlak Sarı
        g.setFont(retroFontSmall);
        String highScoresTitle = "HIGH SCORES";
        int highScoresTitleWidth = getFontMetrics(g.getFont()).stringWidth(highScoresTitle);
        g.drawString(highScoresTitle, panelX + (HIGH_SCORE_PANEL_WIDTH - highScoresTitleWidth) / 2, g.getFont().getSize() + 20);

        int scoreYOffset = g.getFont().getSize() + 40;
        int displayCount = 0;
        g.setColor(COLOR_TEXT_PLAYER_SCORE); // Camgöbeği rengi (skor listesi için)
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
        // Oynanabilir alanın merkezini hesapla
        int playableAreaCenterX = X_OFFSET + PLAYABLE_AREA_WIDTH / 2;
        int playableAreaCenterY = Y_OFFSET + PLAYABLE_AREA_HEIGHT / 2;

        g.setColor(COLOR_TEXT_WELCOME_TITLE); // Parlak Yeşil
        g.setFont(retroFontTitle);
        FontMetrics metricsTitle = getFontMetrics(g.getFont());
        String gameTitle = "RETRO SNAKE";
        // Oyun başlığını oynanabilir alanın merkezine hizala
        int titleX = playableAreaCenterX - metricsTitle.stringWidth(gameTitle) / 2;
        int titleY = playableAreaCenterY - metricsTitle.getAscent() / 2 - 30;
        g.drawString(gameTitle, titleX, titleY);

        g.setColor(COLOR_TEXT_WELCOME_PRESS_KEY); // Açık Gri
        g.setFont(retroFontLarge);
        FontMetrics metrics = getFontMetrics(g.getFont());
        String pressKeyText = "Press Any Key to Start";
        // "Press Any Key to Start" mesajını oyun başlığının altına ortala
        int pressKeyX = playableAreaCenterX - metrics.stringWidth(pressKeyText) / 2;
        int pressKeyY = titleY + metricsTitle.getHeight() + 20;
        g.drawString(pressKeyText, pressKeyX, pressKeyY);
    }

    public void newApple() {
        // Elmanın x ve y koordinatlarını oynanabilir alan içinde rastgele belirle
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
        // Duvarlara çarpma kontrolü, oynanabilir alanın sınırları içinde kalmasını sağlar
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
        // Oynanabilir alanın merkezini hesapla
        int playableAreaCenterX = X_OFFSET + PLAYABLE_AREA_WIDTH / 2;
        int playableAreaCenterY = Y_OFFSET + PLAYABLE_AREA_HEIGHT / 2;

        g.setColor(COLOR_TEXT_GAME_OVER); // Parlak Kırmızı
        g.setFont(retroFontLarge);
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        String gameOverText = "GAME OVER!";
           
        // "GAME OVER!" yazısını oynanabilir alanın merkezine hizala
        int goTextX = playableAreaCenterX - metrics1.stringWidth(gameOverText) / 2;
        int goTextY = playableAreaCenterY - metrics1.getAscent() / 2 - 30; // Dikey konumu ayarla
        g.drawString(gameOverText, goTextX, goTextY);

        g.setColor(COLOR_TEXT_PLAYER_SCORE); // Camgöbeği rengi (skor için)
        g.setFont(retroFontMedium);
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        String scoreText = "Score: " + applesEaten;
        // Skor yazısını oynanabilir alanın merkezine hizala
        int scoreTextX = playableAreaCenterX - metrics2.stringWidth(scoreText) / 2;
        int scoreTextY = goTextY + metrics1.getHeight() + 10;
        g.drawString(scoreText, scoreTextX, scoreTextY);

        g.setColor(COLOR_TEXT_HIGH_SCORES_TITLE); // Parlak Sarı
        g.setFont(retroFontSmall);
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        String restartText = "Press 'R' to Restart or 'ESC' to Logout";
        // Yeniden başlatma/Çıkış yazısını oynanabilir alanın merkezine hizala
        int restartTextX = playableAreaCenterX - metrics3.stringWidth(restartText) / 2;
        int restartTextY = scoreTextY + metrics2.getHeight() + 10;
        g.drawString(restartText, restartTextX, restartTextY);
    }

    public void drawPausedScreen(Graphics g) {
        // Oynanabilir alanın merkezini hesapla
        int playableAreaCenterX = X_OFFSET + PLAYABLE_AREA_WIDTH / 2;
        int playableAreaCenterY = Y_OFFSET + PLAYABLE_AREA_HEIGHT / 2;

        g.setColor(COLOR_TEXT_PAUSED); // Parlak Sarı
        g.setFont(retroFontGameOver);
        FontMetrics metrics = getFontMetrics(g.getFont());
        String pauseText = "PAUSED";
        // "PAUSED" yazısını oynanabilir alanın merkezine hizala
        int pauseTextX = playableAreaCenterX - metrics.stringWidth(pauseText) / 2;
        int pauseTextY = playableAreaCenterY - metrics.getAscent() / 2;
        g.drawString(pauseText, pauseTextX, pauseTextY);

        g.setColor(COLOR_TEXT_PLAYER_SCORE); // Camgöbeği rengi
        g.setFont(retroFontMedium);
        String resumeText = "Press 'P' to Resume";
        // Devam etme yazısını oynanabilir alanın merkezine hizala
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
            // Sadece ilk açılışta herhangi bir tuşa basılmasını bekliyoruz
            if (initialWaitingToStart) {
                resumeGame(); // İlk oyunu başlat
                return;
            }

            // Eğer oyun bittiyse veya duraklatıldıysa veya ilk başlangıç ekranı değilse tuşları dinle
            if (gameOver || paused || !running) { // !running durumu restart ve logout için
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_R:
                        if (gameOver) {
                            initialWaitingToStart = false; // Yeniden başlatmada ilk başlangıç ekranını atla
                            startGame();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (gameOver || paused || !running) {
                            if (timer != null) {
                                timer.stop();
                            }
                            initialWaitingToStart = true; // Logout yaparken tekrar ilk başlangıç ekranına dön
                            mainFrame.showLoginPanel(); // MainFrame'deki login panelini göster
                        }
                        break;
                    case KeyEvent.VK_P:
                        if (!gameOver && !waitingToStartForCurrentGame) { // Oyun bitmediyse ve başlamadıysa
                            paused = !paused;
                            if (paused) {
                                timer.stop();
                            } else {
                                timer.start();
                            }
                        }
                        break;
                }
                return; // Tuş olayını işledikten sonra çık
            }
            
            // Oyun çalışıyorsa yılan hareketini kontrol et
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