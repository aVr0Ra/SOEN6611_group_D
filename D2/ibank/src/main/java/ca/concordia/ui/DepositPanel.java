package ca.concordia.ui;

import ca.concordia.model.TransactionResult;
import ca.concordia.service.BankingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DepositPanel extends BaseOperationPanel {

    private JTextField amountField;

    public DepositPanel(BankingService bankingService, MainFrame mainFrame) {
        super(bankingService, mainFrame);
        buildUI();
    }

    private void buildUI() {
        add(buildTopBar("Deposit/Dépôt"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 247, 250));
        form.setBorder(new EmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        form.add(makeLabel("Account/Compte"), gbc);

        accountCombo = new JComboBox<>();
        accountCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        accountCombo.setPreferredSize(new Dimension(260, 34));
        gbc.gridy = 1;
        form.add(accountCombo, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(makeLabel("Amount/Montant  ($)"), gbc);

        amountField = makeTextField();
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(amountField, gbc);

        JButton depositBtn = makeActionButton("Deposit/Dépôt");
        gbc.gridy = 4;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(depositBtn, gbc);

        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5;
        gbc.insets = new Insets(8, 0, 0, 0);
        form.add(resultLabel, gbc);

        depositBtn.addActionListener(e -> doDeposit());
        amountField.addActionListener(e -> doDeposit());

        add(form, BorderLayout.CENTER);
    }

    private void doDeposit() {
        AccountEntry entry = (AccountEntry) accountCombo.getSelectedItem();
        if (entry == null) {
            showResult(false, "Please select an account./Veuillez sélectionner un compte.");
            return;
        }

        String raw = amountField.getText().trim();
        int cents;
        try {
            double dollars = Double.parseDouble(raw);
            cents = (int) Math.round(dollars * 100);
        } catch (NumberFormatException ex) {
            showResult(false, "Please enter a valid amount./Veuillez entrer un montant valide.");
            return;
        }

        TransactionResult result = bankingService.deposit(session, entry.getAccount().getAccountNumber(), cents);
        if (result.isSuccess()) {
            onSessionSet();
        }
        showResult(result.isSuccess(), result.getMessage());
    }

    @Override
    protected void onSessionSet() {
        populateAccountCombo(accountCombo, bankingService.getAccounts(session));
        if (resultLabel != null) resultLabel.setText(" ");
        if (amountField != null) amountField.setText("");
    }
}
