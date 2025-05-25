package retrologin;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

public class LoginPanel extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel feedbackLabel; 
    private JLabel captchaQuestionLabel;
    private JTextField captchaAnswerField;

    // sifre kriter seyleri
    private JLabel lengthCriteriaLabel;
    private JLabel uppercaseCriteriaLabel;
    private JLabel lowercaseCriteriaLabel;
    private JLabel digitCriteriaLabel;
    private JLabel specialCharCriteriaLabel;
    private JLabel passwordStrengthLabel;

    private String currentCaptchaAnswer;
    private HashMap<String, String> users = new HashMap<>(); 

    private Font retroFontSmall;
    private Font retroFontMedium;
    private Font retroFontLarge;
    private Font retroFontTitle;

    private ImageIcon backgroundGif;

    private Timer animationTimer;
    private final String titleText = "SNAKE GAME";
    private int[] letterYOffsets;
    private Random random = new Random();

    private MainFrame mainFrame;
    private JLabel animatedTitleLabel; 

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // font yukluo
        retroFontSmall = RetroArcadeFontLoader.loadPixelFont(16f);
        retroFontMedium = RetroArcadeFontLoader.loadPixelFont(24f);
        retroFontLarge = RetroArcadeFontLoader.loadPixelFont(36f);
        retroFontTitle = RetroArcadeFontLoader.loadPixelFont(48f);

        // denemek icin varolan hesaplar
        users.put("testuser", "Password123!");
        users.put("admin", "Admin123$");
        users.put("newuser", "Pass123!");

        // arkaplan resmini yukluo
        try {
            backgroundGif = new ImageIcon("retro_background.gif");
            if (backgroundGif.getIconWidth() == -1 || backgroundGif.getIconHeight() == -1) {
                System.err.println("GIF file could not be loaded or is invalid: retro_background.gif. Please check filename and path.");
                backgroundGif = null; // Set to null on error
            }
        } catch (Exception e) {
            System.err.println("Error loading background GIF: " + e.getMessage());
            backgroundGif = null;
        }

        setupUI(); 
        setupAnimation(); 
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK); // calismazsa gif siyah olsun die

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10); // General padding

        // dans eden harfler baslik iicn
        animatedTitleLabel = new JLabel(titleText) {
            @Override
            protected void paintComponent(Graphics g) {
               
            }
        };
        animatedTitleLabel.setFont(retroFontTitle);
        animatedTitleLabel.setForeground(Color.WHITE); 
        animatedTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        gbc.weighty = 0.2; 
        gbc.anchor = GridBagConstraints.CENTER; 
        add(animatedTitleLabel, gbc);

        // username kismi
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST; 
        JLabel usernameLabel = new JLabel("USERNAME:");
        usernameLabel.setFont(retroFontMedium);
        usernameLabel.setForeground(new Color(255, 255, 0)); 
        usernameLabel.setOpaque(true);
        usernameLabel.setBackground(new Color(0, 0, 0, 180)); 
        add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST; 
        usernameField = new JTextField(15);
        usernameField.setFont(retroFontSmall);
        usernameField.setBackground(new Color(50, 50, 50, 200));
        usernameField.setForeground(Color.CYAN);
        usernameField.setCaretColor(Color.CYAN); 
        usernameField.setBorder(BorderFactory.createLineBorder(Color.CYAN.darker(), 2));
        add(usernameField, gbc);

        // sifre kismi
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel passwordLabel = new JLabel("PASSWORD:");
        passwordLabel.setFont(retroFontMedium);
        passwordLabel.setForeground(new Color(0, 255, 255)); 
        passwordLabel.setOpaque(true);
        passwordLabel.setBackground(new Color(0, 0, 0, 180));
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        passwordField = new JPasswordField(15);
        passwordField.setFont(retroFontSmall);
        passwordField.setBackground(new Color(50, 50, 50, 200));
        passwordField.setForeground(Color.CYAN);
        passwordField.setCaretColor(Color.CYAN);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.CYAN.darker(), 2));
        add(passwordField, gbc);

        // sifre gostermesi icin kutucuk 
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setFont(retroFontSmall);
        showPasswordCheckBox.setForeground(Color.MAGENTA);
        showPasswordCheckBox.setOpaque(true);
        showPasswordCheckBox.setBackground(new Color(0, 0, 0, 180));
        add(showPasswordCheckBox, gbc);

        // sifre guclu orta zayif gosterge
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 10, 2, 10); 

        lengthCriteriaLabel = new JLabel("• Min 8 Characters");
        lengthCriteriaLabel.setFont(retroFontSmall);
        lengthCriteriaLabel.setForeground(Color.GRAY);
        lengthCriteriaLabel.setOpaque(true);
        lengthCriteriaLabel.setBackground(new Color(0, 0, 0, 150)); 
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(lengthCriteriaLabel, gbc);

        uppercaseCriteriaLabel = new JLabel("• At least 1 Uppercase Letter");
        uppercaseCriteriaLabel.setFont(retroFontSmall);
        uppercaseCriteriaLabel.setForeground(Color.GRAY);
        uppercaseCriteriaLabel.setOpaque(true);
        uppercaseCriteriaLabel.setBackground(new Color(0, 0, 0, 150));
        gbc.gridy = 5;
        add(uppercaseCriteriaLabel, gbc);

        lowercaseCriteriaLabel = new JLabel("• At least 1 Lowercase Letter");
        lowercaseCriteriaLabel.setFont(retroFontSmall);
        lowercaseCriteriaLabel.setForeground(Color.GRAY);
        lowercaseCriteriaLabel.setOpaque(true);
        lowercaseCriteriaLabel.setBackground(new Color(0, 0, 0, 150));
        gbc.gridy = 6;
        add(lowercaseCriteriaLabel, gbc);

        digitCriteriaLabel = new JLabel("• At least 1 Digit");
        digitCriteriaLabel.setFont(retroFontSmall);
        digitCriteriaLabel.setForeground(Color.GRAY);
        digitCriteriaLabel.setOpaque(true);
        digitCriteriaLabel.setBackground(new Color(0, 0, 0, 150));
        gbc.gridy = 7;
        add(digitCriteriaLabel, gbc);

        specialCharCriteriaLabel = new JLabel("• At least 1 Special Character (!@#$%^&*-)");
        specialCharCriteriaLabel.setFont(retroFontSmall);
        specialCharCriteriaLabel.setForeground(Color.GRAY);
        specialCharCriteriaLabel.setOpaque(true);
        specialCharCriteriaLabel.setBackground(new Color(0, 0, 0, 150));
        gbc.gridy = 8;
        add(specialCharCriteriaLabel, gbc);

        gbc.insets = new Insets(8, 10, 8, 10); 
        passwordStrengthLabel = new JLabel("Password Strength: N/A");
        passwordStrengthLabel.setFont(retroFontMedium);
        passwordStrengthLabel.setForeground(Color.WHITE);
        passwordStrengthLabel.setOpaque(true);
        passwordStrengthLabel.setBackground(new Color(0, 0, 0, 180));
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.CENTER;
        add(passwordStrengthLabel, gbc);

        // captha kismi
        gbc.gridy = 10;
        captchaQuestionLabel = new JLabel();
        captchaQuestionLabel.setFont(retroFontMedium);
        captchaQuestionLabel.setForeground(Color.ORANGE);
        captchaQuestionLabel.setOpaque(true);
        captchaQuestionLabel.setBackground(new Color(0, 0, 0, 180));
        add(captchaQuestionLabel, gbc);
        generateCaptcha(); 

        gbc.gridy = 11;
        captchaAnswerField = new JTextField(10);
        captchaAnswerField.setFont(retroFontSmall);
        captchaAnswerField.setBackground(new Color(50, 50, 50, 200));
        captchaAnswerField.setForeground(Color.CYAN);
        captchaAnswerField.setCaretColor(Color.CYAN);
        captchaAnswerField.setBorder(BorderFactory.createLineBorder(Color.CYAN.darker(), 2));
        add(captchaAnswerField, gbc);

        // login register buton kismi
        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        loginButton = new JButton("LOGIN");
        loginButton.setFont(retroFontMedium);
        loginButton.setBackground(new Color(0, 100, 0)); 
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        add(loginButton, gbc);

        gbc.gridy = 13;
        registerButton = new JButton("REGISTER");
        registerButton.setFont(retroFontMedium);
        registerButton.setBackground(new Color(200, 100, 0));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        add(registerButton, gbc);

        // feedback yazisinin ayarlari
        gbc.gridy = 14;
        gbc.weighty = 0.3; 
        gbc.fill = GridBagConstraints.NONE;
        feedbackLabel = new JLabel("");
        feedbackLabel.setFont(retroFontSmall);
        feedbackLabel.setForeground(Color.RED); 
        feedbackLabel.setOpaque(true);
        feedbackLabel.setBackground(new Color(0, 0, 0, 180));
        add(feedbackLabel, gbc);

        // sifre gosterip gostermeme kosulu
        showPasswordCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPasswordCheckBox.isSelected()) {
                    passwordField.setEchoChar((char) 0); 
                } else {
                    passwordField.setEchoChar('*'); 
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String captchaInput = captchaAnswerField.getText().trim();

                // kutucuklar bossa
                if (username.isEmpty() || password.isEmpty() || captchaInput.isEmpty()) {
                    setFeedback("All fields must be filled!", Color.RED);
                    return;
                }

                // captha yanlis cevaplandiysa
                if (!captchaInput.equalsIgnoreCase(currentCaptchaAnswer)) {
                    setFeedback("Incorrect CAPTCHA! Please try again.", Color.RED);
                    generateCaptcha(); // Generate new CAPTCHA
                    captchaAnswerField.setText(""); // Clear CAPTCHA field
                    return;
                }

                // 2. hesap yoksa
                if (!users.containsKey(username)) {
                    setFeedback("No account found with this username.", Color.RED);
                    generateCaptcha(); // Generate new CAPTCHA on failed login attempt
                    captchaAnswerField.setText("");
                    return;
                }

                // basarili giris feedbacki
                if (authenticate(username, password)) {
                    setFeedback("Login Successful! Welcome, " + username + "!", Color.GREEN);
                    mainFrame.showGamePanel(username); // Redirect to main game
                } else {
                    // yanlis sifre girerse
                    setFeedback("Invalid password. Please try again.", Color.RED);
                    generateCaptcha(); // Generate new CAPTCHA on failed login
                    captchaAnswerField.setText("");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    setFeedback("Username and password cannot be empty!", Color.RED);
                    return;
                }

                if (users.containsKey(username)) {
                    setFeedback("Username '" + username + "' already exists!", Color.RED);
                    return;
                }

                if (!isPasswordValid(password)) {
                    setFeedback("Your password does not meet all specified criteria!", Color.RED);
                    return;
                }

                
                addUser(username, password);
                setFeedback("Registration Successful for " + username + "! You can now log in.", Color.GREEN);
                usernameField.setText("");
                passwordField.setText("");
                captchaAnswerField.setText("");
                generateCaptcha();
                checkPasswordStrength(new String(passwordField.getPassword())); // Reset password strength
            }
        });

        // sifrenin gucunu kotnrol etme kosullari
        DocumentListener fieldListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkFields();
                if (e.getDocument() == passwordField.getDocument()) {
                    checkPasswordStrength(new String(passwordField.getPassword()));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkFields();
                if (e.getDocument() == passwordField.getDocument()) {
                    checkPasswordStrength(new String(passwordField.getPassword()));
                }
            }

            @Override
      public void changedUpdate(DocumentEvent e) {
               
            }
        };

        usernameField.getDocument().addDocumentListener(fieldListener);
        passwordField.getDocument().addDocumentListener(fieldListener);
        captchaAnswerField.getDocument().addDocumentListener(fieldListener);

      
        checkFields();
        checkPasswordStrength(new String(passwordField.getPassword()));
    }

    // dans eden baslikla arkaplan yukleme kosullari
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

       
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        
        if (backgroundGif != null) {
            g2d.drawImage(backgroundGif.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

      
        Font titleFont = retroFontTitle;
        g2d.setFont(titleFont);

        FontMetrics fm = g2d.getFontMetrics(titleFont);
        String currentTitleText = titleText;

        
        int labelX = animatedTitleLabel.getX();
        int labelY = animatedTitleLabel.getY();
        int labelWidth = animatedTitleLabel.getWidth();
        int labelHeight = animatedTitleLabel.getHeight();

        int textWidth = fm.stringWidth(currentTitleText);
        int textAscent = fm.getAscent();

        int initialX = labelX + (labelWidth - textWidth) / 2;
        int initialY = labelY + (labelHeight - fm.getHeight()) / 2 + textAscent;

        int currentX = initialX;

        for (int i = 0; i < currentTitleText.length(); i++) {
            String letter = String.valueOf(currentTitleText.charAt(i));
            int letterWidth = fm.stringWidth(letter);

         
            g2d.setColor(new Color(50, 50, 50));
            g2d.drawString(letter, currentX + 4, initialY + 4 + letterYOffsets[i]);

          
            GradientPaint gp = new GradientPaint(
                currentX, initialY - textAscent, new Color(255, 0, 255), // Magenta
                currentX + letterWidth, initialY - textAscent, new Color(255, 255, 0) // Yellow
            );
            g2d.setPaint(gp);
            g2d.drawString(letter, currentX, initialY + letterYOffsets[i]);

            currentX += letterWidth;
        }
    }

    // sifre kosullari 
    private boolean isPasswordValid(String password) {
        boolean hasMinLength = password.length() >= 8;
        boolean hasUppercase = Pattern.compile(".*[A-Z].*").matcher(password).matches();
        boolean hasLowercase = Pattern.compile(".*[a-z].*").matcher(password).matches();
        boolean hasDigit = Pattern.compile(".*\\d.*").matcher(password).matches();
        boolean hasSpecialChar = Pattern.compile(".*[!@#$%^&*-].*").matcher(password).matches();

        return hasMinLength && hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }

    // sifre gucunu ve bu kosul yazilarini degistirme isi
    private void checkPasswordStrength(String password) {
        int criteriaMet = 0;
        Color metColor = Color.GREEN;
        Color unmetColor = Color.RED;
        Color defaultColor = Color.GRAY;

        if (password.length() >= 8) {
            lengthCriteriaLabel.setForeground(metColor);
            criteriaMet++;
        } else {
            lengthCriteriaLabel.setForeground(password.isEmpty() ? defaultColor : unmetColor);
        }

        if (Pattern.compile(".*[A-Z].*").matcher(password).matches()) {
            uppercaseCriteriaLabel.setForeground(metColor);
            criteriaMet++;
        } else {
            uppercaseCriteriaLabel.setForeground(password.isEmpty() ? defaultColor : unmetColor);
        }

        if (Pattern.compile(".*[a-z].*").matcher(password).matches()) {
            lowercaseCriteriaLabel.setForeground(metColor);
            criteriaMet++;
        } else {
            lowercaseCriteriaLabel.setForeground(password.isEmpty() ? defaultColor : unmetColor);
        }

        if (Pattern.compile(".*\\d.*").matcher(password).matches()) {
            digitCriteriaLabel.setForeground(metColor);
            criteriaMet++;
        } else {
            digitCriteriaLabel.setForeground(password.isEmpty() ? defaultColor : unmetColor);
        }

        if (Pattern.compile(".*[!@#$%^&*-].*").matcher(password).matches()) {
            specialCharCriteriaLabel.setForeground(metColor);
            criteriaMet++;
        } else {
            specialCharCriteriaLabel.setForeground(password.isEmpty() ? defaultColor : unmetColor);
        }

        if (password.isEmpty()) {
            passwordStrengthLabel.setText("Password Strength: N/A");
            passwordStrengthLabel.setForeground(Color.WHITE);
        } else if (criteriaMet == 5) {
            passwordStrengthLabel.setText("Password Strength: STRONG!");
            passwordStrengthLabel.setForeground(Color.GREEN.brighter());
        } else if (criteriaMet >= 3) {
            passwordStrengthLabel.setText("Password Strength: MEDIUM");
            passwordStrengthLabel.setForeground(Color.ORANGE);
        } else {
            passwordStrengthLabel.setText("Password Strength: WEAK");
            passwordStrengthLabel.setForeground(Color.RED);
        }
    }

    // asa yukari yapan basligin hiizni ayarlama 300 yazan yer 300 milisaniyede 1 hareket etmesi fln  
    private void setupAnimation() {
        letterYOffsets = new int[titleText.length()];
        animationTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < letterYOffsets.length; i++) {
                  
                    letterYOffsets[i] = random.nextInt(5) - 2; 
                }
                repaint(); 
            }
        });
        animationTimer.start();
    }

    // captha olusturma 
    private void generateCaptcha() {
        Random rand = new Random();
        int num1 = rand.nextInt(10) + 1; 
        int num2 = rand.nextInt(10) + 1; 
        int operation = rand.nextInt(3); // 0 gelirse toplama 1 gelirsecikarma 2 gelirse carpma oluo 

        String question;
        int answer;

        switch (operation) {
            case 0:
                question = num1 + " + " + num2 + " = ?";
                answer = num1 + num2;
                break;
            case 1:
                // sonuc negatif cikmasin die buyuk olan solda olmasi icin temp yontemi 
                if (num1 < num2) {
                    int temp = num1;
                    num1 = num2;
                    num2 = temp;
                }
                question = num1 + " - " + num2 + " = ?";
                answer = num1 - num2;
                break;
            case 2:
                question = num1 + " * " + num2 + " = ?";
                answer = num1 * num2;
                break;
            default:
                question = ""; 
                answer = 0;
        }
        captchaQuestionLabel.setText(question);
        currentCaptchaAnswer = String.valueOf(answer); 
    }

    // butonlar metinler dolmadan aktif olmama zikkimi
    private void checkFields() {
        boolean usernameEmpty = usernameField.getText().trim().isEmpty();
        boolean passwordEmpty = new String(passwordField.getPassword()).trim().isEmpty();
        boolean captchaEmpty = captchaAnswerField.getText().trim().isEmpty();
        boolean passwordMeetsAllCriteria = isPasswordValid(new String(passwordField.getPassword()));

        
        loginButton.setEnabled(!usernameEmpty && !passwordEmpty && !captchaEmpty);

      
        registerButton.setEnabled(!usernameEmpty && !passwordEmpty && passwordMeetsAllCriteria);

        
        if (feedbackLabel.getText().isEmpty()) {
            setFeedback("", Color.RED); 
        }
    }

    // bura degiscek muhtemelen ama kullanici var mi yok mu kotnrol edio
    private boolean authenticate(String username, String password) {
      
        return users.get(username).equals(password);
    }

    // yeni kullanici ekleme kismi
    public void addUser(String username, String password) {
        if (!users.containsKey(username)) {
            users.put(username, password);
            System.out.println("New user registered: " + username);
        } else {
            System.out.println("User " + username + " already exists.");
        }
    }

    // metinleri sifirlama 
    public void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        captchaAnswerField.setText("");
        generateCaptcha();
        checkPasswordStrength(""); 
        setFeedback("", Color.RED); 
    }

    // feedback mesajlarini belirlio
    private void setFeedback(String message, Color color) {
        feedbackLabel.setText(message);
        feedbackLabel.setForeground(color);
    }
}