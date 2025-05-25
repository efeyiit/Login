package retrologin;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private GamePanel gamePanel;

    
    private static final int LOGIN_WIDTH = 800;
    private static final int LOGIN_HEIGHT = 700;

    
    private static final int GAME_AREA_WIDTH = GamePanel.SCREEN_WIDTH;
    private static final int GAME_AREA_HEIGHT = GamePanel.SCREEN_HEIGHT;
    private static final int FRAME_PADDING_HEIGHT = 80; 

    public MainFrame() {
        setTitle("Retro Arcade Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null); 

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
       
        setSize(LOGIN_WIDTH, LOGIN_HEIGHT);
        setLocationRelativeTo(null); 

        cardLayout.show(mainPanel, "Login");
        loginPanel.resetFields();
        loginPanel.requestFocusInWindow();
        setTitle("Retro Arcade Login");
    }

    public void showGamePanel(String username) {
       
        setSize(GAME_AREA_WIDTH, GAME_AREA_HEIGHT + FRAME_PADDING_HEIGHT);
        setLocationRelativeTo(null); 

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