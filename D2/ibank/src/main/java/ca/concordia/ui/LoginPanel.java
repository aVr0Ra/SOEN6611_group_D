package ca.concordia.ui;

import ca.concordia.exception.AuthenticationException;
import ca.concordia.model.Session;
import ca.concordia.service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final AuthenticationService authService;
    private final MainFrame mainFrame;

    private JTextField cardNumberField;
    private JPasswordField pinField;
    private JLabel errorLabel;

    public LoginPanel(AuthenticationService authService, MainFrame mainFrame) {
        this.authService = authService;
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220), 1),
                new EmptyBorder(40, 50, 40, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel title = new JLabel("iBank");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(30, 80, 160));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 24, 0);
        card.add(title, gbc);

        gbc.insets = new Insets(6, 0, 2, 0);

        JLabel cardLabel = new JLabel("Card Number");
        cardLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridy = 1;
        card.add(cardLabel, gbc);

        cardNumberField = new JTextField(18);
        cardNumberField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cardNumberField.setPreferredSize(new Dimension(280, 36));
        gbc.gridy = 2;
        card.add(cardNumberField, gbc);

        JLabel pinLabel = new JLabel("PIN");
        pinLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.gridy = 3;
        gbc.insets = new Insets(14, 0, 2, 0);
        card.add(pinLabel, gbc);

        pinField = new JPasswordField(18);
        pinField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pinField.setPreferredSize(new Dimension(280, 36));
        gbc.gridy = 4;
        gbc.insets = new Insets(6, 0, 2, 0);
        card.add(pinField, gbc);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(200, 50, 50));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5;
        gbc.insets = new Insets(8, 0, 4, 0);
        card.add(errorLabel, gbc);

        JButton loginButton = new JButton("Log In");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBackground(new Color(30, 80, 160));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(280, 40));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        gbc.insets = new Insets(14, 0, 0, 0);
        card.add(loginButton, gbc);

        loginButton.addActionListener(e -> attemptLogin());
        pinField.addActionListener(e -> attemptLogin());
        cardNumberField.addActionListener(e -> pinField.requestFocus());

        add(card);
    }

    private void attemptLogin() {
        String cardNumber = cardNumberField.getText().trim();
        String pin = new String(pinField.getPassword());

        try {
            Session session = authService.authenticateUser(cardNumber, pin);
            errorLabel.setText(" ");
            pinField.setText("");
            cardNumberField.setText("");
            mainFrame.showMenu(session);
        } catch (AuthenticationException ex) {
            errorLabel.setText(ex.getMessage());
            pinField.setText("");
        }
    }

    public void reset() {
        cardNumberField.setText("");
        pinField.setText("");
        errorLabel.setText(" ");
        cardNumberField.requestFocus();
    }
}