package ca.concordia.ui;

import ca.concordia.model.TransactionResult;
import ca.concordia.service.BankingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TransferPanel extends BaseOperationPanel {

    private JComboBox<AccountEntry> toCombo;
    private JTextField amountField;

    public TransferPanel(BankingService bankingService, MainFrame mainFrame) {
        super(bankingService, mainFrame);
        buildUI();
    }

    private void buildUI() {
        add(buildTopBar("Transfer Funds"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 247, 250));
        form.setBorder(new EmptyBorder(30, 60, 30, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        form.add(makeLabel("From Account"), gbc);

        accountCombo = new JComboBox<>();
        accountCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        accountCombo.setPreferredSize(new Dimension(260, 34));
        gbc.gridy = 1;
        form.add(accountCombo, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(makeLabel("To Account"), gbc);

        toCombo = new JComboBox<>();
        toCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        toCombo.setPreferredSize(new Dimension(260, 34));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(toCombo, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(makeLabel("Amount ($)"), gbc);

        amountField = makeTextField();
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(amountField, gbc);

        JButton transferBtn = makeActionButton("Transfer");
        gbc.gridy = 6;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(transferBtn, gbc);

        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 7;
        gbc.insets = new Insets(8, 0, 0, 0);
        form.add(resultLabel, gbc);

        transferBtn.addActionListener(e -> doTransfer());
        amountField.addActionListener(e -> doTransfer());

        add(form, BorderLayout.CENTER);
    }

    private void doTransfer() {
        AccountEntry fromEntry = (AccountEntry) accountCombo.getSelectedItem();
        AccountEntry toEntry = (AccountEntry) toCombo.getSelectedItem();

        if (fromEntry == null || toEntry == null) {
            showResult(false, "Please select both accounts.");
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

        TransactionResult result = bankingService.transfer(
                session,
                fromEntry.getAccount().getAccountNumber(),
                toEntry.getAccount().getAccountNumber(),
                cents
        );
        if (result.isSuccess()) {
            onSessionSet();
        }
        showResult(result.isSuccess(), result.getMessage());
    }

    @Override
    protected void onSessionSet() {
        List<ca.concordia.model.Account> accounts = bankingService.getAccounts(session);
        populateAccountCombo(accountCombo, accounts);
        populateAccountCombo(toCombo, accounts);
        if (resultLabel != null) resultLabel.setText(" ");
        if (amountField != null) amountField.setText("");
    }
}
