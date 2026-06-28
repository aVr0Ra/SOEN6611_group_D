package ca.concordia.ui;

import ca.concordia.model.Account;
import ca.concordia.model.Session;
import ca.concordia.service.BankingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public abstract class BaseOperationPanel extends JPanel {

    protected final BankingService bankingService;
    protected final MainFrame mainFrame;
    protected Session session;

    protected JLabel resultLabel;
    protected JComboBox<AccountEntry> accountCombo;

    public BaseOperationPanel(BankingService bankingService, MainFrame mainFrame) {
        this.bankingService = bankingService;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
    }

    protected JPanel buildTopBar(String title) {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 80, 160));
        topBar.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        titleLabel.setForeground(Color.WHITE);
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = new JButton("< Back");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(30, 80, 160));
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> mainFrame.showPanel("menu"));
        topBar.add(backBtn, BorderLayout.EAST);

        return topBar;
    }

    protected JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return label;
    }

    protected JTextField makeTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(260, 34));
        return field;
    }

    protected JButton makeActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(new Color(30, 80, 160));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(260, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    protected void showResult(boolean success, String message) {
        resultLabel.setText(message);
        resultLabel.setForeground(success ? new Color(30, 130, 60) : new Color(190, 50, 50));
    }

    protected void populateAccountCombo(JComboBox<AccountEntry> combo, List<Account> accounts) {
        combo.removeAllItems();
        for (Account acc : accounts) {
            combo.addItem(new AccountEntry(acc));
        }
    }

    public void setSession(Session session) {
        this.session = session;
        onSessionSet();
    }

    protected abstract void onSessionSet();

    protected static class AccountEntry {
        private final Account account;

        public AccountEntry(Account account) {
            this.account = account;
        }

        public Account getAccount() {
            return account;
        }

        @Override
        public String toString() {
            return "#" + account.getAccountNumber() + " — " + account.getAccountType()
                    + " — " + String.format("$%.2f CAD", account.getBalance() / 100.0);
        }
    }
}