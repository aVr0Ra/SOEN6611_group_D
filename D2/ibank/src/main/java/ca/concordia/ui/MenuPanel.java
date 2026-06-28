package ca.concordia.ui;

import ca.concordia.model.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuPanel extends JPanel {

    private final MainFrame mainFrame;
    private JLabel welcomeLabel;

    public MenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 80, 160));
        topBar.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel brand = new JLabel("iBank");
        brand.setFont(new Font("SansSerif", Font.BOLD, 20));
        brand.setForeground(Color.WHITE);
        topBar.add(brand, BorderLayout.WEST);

        welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        welcomeLabel.setForeground(new Color(200, 220, 255));
        topBar.add(welcomeLabel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(245, 247, 250));
        center.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        String[][] buttons = {
                {"Withdraw",        "Deposit"},
                {"Transfer Funds",  "Balance Inquiry"},
                {"Change PIN",      "Pay Tuition"},
        };

        String[][] panels = {
                {"withdraw",        "deposit"},
                {"transfer",        "balance"},
                {"changepin",       "paytuition"},
        };

        for (int row = 0; row < buttons.length; row++) {
            for (int col = 0; col < buttons[row].length; col++) {
                final String panelName = panels[row][col];
                JButton btn = makeMenuButton(buttons[row][col]);
                btn.addActionListener(e -> mainFrame.showPanel(panelName));
                gbc.gridx = col;
                gbc.gridy = row;
                center.add(btn, gbc);
            }
        }

        add(center, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setBackground(new Color(235, 238, 243));
        bottomBar.setBorder(new EmptyBorder(8, 16, 8, 16));

        JButton logoutBtn = new JButton("Log Out");
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        logoutBtn.setForeground(new Color(160, 50, 50));
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> mainFrame.logout());
        bottomBar.add(logoutBtn);

        add(bottomBar, BorderLayout.SOUTH);
    }

    private JButton makeMenuButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(30, 80, 160));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 205, 230), 1),
                new EmptyBorder(18, 12, 18, 12)
        ));
        return btn;
    }

    public void setSession(Session session) {
        welcomeLabel.setText("Welcome, " + session.getUserName());
    }
}