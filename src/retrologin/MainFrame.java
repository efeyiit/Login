package retrologin;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private GamePanel gamePanel;

    // Login ekranı için tercih edilen boyutlar
    private static final int LOGIN_WIDTH = 800;
    private static final int LOGIN_HEIGHT = 700;

    // Oyun ekranı için tercih edilen boyutlar (GamePanel'in kendi boyutlarına + çerçeve payı)
    // GamePanel'in yeni boyutları SCREEN_WIDTH=800, SCREEN_HEIGHT=700
    private static final int GAME_AREA_WIDTH = GamePanel.SCREEN_WIDTH;
    private static final int GAME_AREA_HEIGHT = GamePanel.SCREEN_HEIGHT;
    private static final int FRAME_PADDING_HEIGHT = 80; // Başlık çubuğu ve pencere çerçevesi için tahmini pay

    public MainFrame() {
        setTitle("Retro Arcade Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null); // Ekranın ortasında başlat

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        gamePanel = new GamePanel(this);

        mainPanel.add(loginPanel, "Login");
        mainPanel.add(gamePanel, "Game");

        add(mainPanel);

        showLoginPanel();
    }

    public void showLoginPanel() {
        // Login ekranına geçerken pencere boyutunu ayarla
        setSize(LOGIN_WIDTH, LOGIN_HEIGHT);
        setLocationRelativeTo(null); // Yeniden ortala

        cardLayout.show(mainPanel, "Login");
        loginPanel.resetFields();
        loginPanel.requestFocusInWindow();
        setTitle("Retro Arcade Login");
    }

    public void showGamePanel(String username) {
        // Oyun ekranına geçerken pencere boyutunu ayarla
        setSize(GAME_AREA_WIDTH, GAME_AREA_HEIGHT + FRAME_PADDING_HEIGHT);
        setLocationRelativeTo(null); // Yeniden ortala

        gamePanel.setWelcomeText(username);
        gamePanel.startGame();
        cardLayout.show(mainPanel, "Game");
        gamePanel.requestFocusInWindow();
        setTitle("Retro Snake Game - " + username);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}