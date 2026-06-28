package ca.concordia.ui;

import ca.concordia.model.TransactionResult;
import ca.concordia.service.BankingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BalancePanel extends BaseOperationPanel {

    private JLabel balanceDisplay;

    public BalancePanel(BankingService bankingService, MainFrame mainFrame) {
        super(bankingService, mainFrame);
        buildUI();
    }

    private void buildUI() {
        add(buildTopBar("Balance Inquiry"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 247, 250));
        form.setBorder(new EmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        form.add(makeLabel("Account"), gbc);

        accountCombo = new JComboBox<>();
        accountCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        accountCombo.setPreferredSize(new Dimension(260, 34));
        gbc.gridy = 1;
        form.add(accountCombo, gbc);

        JButton checkBtn = makeActionButton("Check Balance");
        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(checkBtn, gbc);

        balanceDisplay = new JLabel(" ");
        balanceDisplay.setFont(new Font("SansSerif", Font.BOLD, 22));
        balanceDisplay.setForeground(new Color(30, 80, 160));
        balanceDisplay.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 4, 0);
        form.add(balanceDisplay, gbc);

        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        gbc.insets = new Insets(4, 0, 0, 0);
        form.add(resultLabel, gbc);

        checkBtn.addActionListener(e -> doCheckBalance());

        add(form, BorderLayout.CENTER);
    }

    private void doCheckBalance() {
        AccountEntry entry = (AccountEntry) accountCombo.getSelectedItem();
        if (entry == null) {
            showResult(false, "Please select an account.");
            return;
        }

        TransactionResult result = bankingService.checkBalance(session, entry.getAccount().getAccountNumber());
        showResult(result.isSuccess(), result.getMessage());
        if (result.isSuccess() && result.hasBalance()) {
            balanceDisplay.setText(String.format("$%.2f CAD", result.getBalance() / 100.0));
        } else {
            balanceDisplay.setText(" ");
        }
    }

    @Override
    protected void onSessionSet() {
        populateAccountCombo(accountCombo, bankingService.getAccounts(session));
        if (resultLabel != null) resultLabel.setText(" ");
        if (balanceDisplay != null) balanceDisplay.setText(" ");
    }
}