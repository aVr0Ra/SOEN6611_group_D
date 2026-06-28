package ca.concordia.ui;

import ca.concordia.model.TransactionResult;
import ca.concordia.service.BankingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WithdrawPanel extends BaseOperationPanel {

    private JTextField amountField;

    public WithdrawPanel(BankingService bankingService, MainFrame mainFrame) {
        super(bankingService, mainFrame);
        buildUI();
    }

    private void buildUI() {
        add(buildTopBar("Withdraw Cash"), BorderLayout.NORTH);

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

        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(makeLabel("Amount (multiples of $20)"), gbc);

        amountField = makeTextField();
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(amountField, gbc);

        JButton withdrawBtn = makeActionButton("Withdraw");
        gbc.gridy = 4;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(withdrawBtn, gbc);

        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5;
        gbc.insets = new Insets(8, 0, 0, 0);
        form.add(resultLabel, gbc);

        withdrawBtn.addActionListener(e -> doWithdraw());
        amountField.addActionListener(e -> doWithdraw());

        add(form, BorderLayout.CENTER);
    }

    private void doWithdraw() {
        AccountEntry entry = (AccountEntry) accountCombo.getSelectedItem();
        if (entry == null) {
            showResult(false, "Please select an account.");
            return;
        }

        String raw = amountField.getText().trim();
        int cents;
        try {
            double dollars = Double.parseDouble(raw);
            cents = (int) Math.round(dollars * 100);
        } catch (NumberFormatException ex) {
            showResult(false, "Please enter a valid amount.");
            return;
        }

        TransactionResult result = bankingService.withdraw(session, entry.getAccount().getAccountNumber(), cents);
        showResult(result.isSuccess(), result.getMessage());
        if (result.isSuccess()) {
            amountField.setText("");
            onSessionSet();
        }
    }

    @Override
    protected void onSessionSet() {
        populateAccountCombo(accountCombo, bankingService.getAccounts(session));
        if (resultLabel != null) resultLabel.setText(" ");
        if (amountField != null) amountField.setText("");
    }
}