package ca.concordia.ui;

import ca.concordia.model.Session;
import ca.concordia.service.AuthenticationService;
import ca.concordia.service.BankingService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final AuthenticationService authService;
    private final BankingService bankingService;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    private final LoginPanel loginPanel;
    private final MenuPanel menuPanel;
    private final WithdrawPanel withdrawPanel;
    private final DepositPanel depositPanel;
    private final TransferPanel transferPanel;
    private final BalancePanel balancePanel;
    private final ChangePinPanel changePinPanel;
    private final PayTuitionPanel payTuitionPanel;

    private Session currentSession;

    public MainFrame(AuthenticationService authService, BankingService bankingService) {
        this.authService = authService;
        this.bankingService = bankingService;

        setTitle("iBank — Concordia University");
        setSize(1000, 650);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginPanel = new LoginPanel(authService, this);
        menuPanel = new MenuPanel(this);
        withdrawPanel = new WithdrawPanel(bankingService, this);
        depositPanel = new DepositPanel(bankingService, this);
        transferPanel = new TransferPanel(bankingService, this);
        balancePanel = new BalancePanel(bankingService, this);
        changePinPanel = new ChangePinPanel(bankingService, this);
        payTuitionPanel = new PayTuitionPanel(bankingService, this);

        cardPanel.add(loginPanel,     "login");
        cardPanel.add(menuPanel,      "menu");
        cardPanel.add(withdrawPanel,  "withdraw");
        cardPanel.add(depositPanel,   "deposit");
        cardPanel.add(transferPanel,  "transfer");
        cardPanel.add(balancePanel,   "balance");
        cardPanel.add(changePinPanel, "changepin");
        cardPanel.add(payTuitionPanel,"paytuition");

        add(cardPanel);
        cardLayout.show(cardPanel, "login");
    }

    public void showMenu(Session session) {
        this.currentSession = session;
        menuPanel.setSession(session);
        cardLayout.show(cardPanel, "menu");
    }

    public void showPanel(String name) {
        if (currentSession == null) return;

        switch (name) {
            case "withdraw"   -> withdrawPanel.setSession(currentSession);
            case "deposit"    -> depositPanel.setSession(currentSession);
            case "transfer"   -> transferPanel.setSession(currentSession);
            case "balance"    -> balancePanel.setSession(currentSession);
            case "changepin"  -> changePinPanel.setSession(currentSession);
            case "paytuition" -> payTuitionPanel.setSession(currentSession);
        }
        cardLayout.show(cardPanel, name);
    }

    public void logout() {
        if (currentSession != null) {
            authService.logout(currentSession);
            currentSession = null;
        }
        loginPanel.reset();
        cardLayout.show(cardPanel, "login");
    }
}