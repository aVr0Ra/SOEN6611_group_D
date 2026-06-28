package ca.concordia.ui;

import ca.concordia.model.Account;
import ca.concordia.model.AccountType;
import ca.concordia.model.TransactionResult;
import ca.concordia.service.BankingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PayTuitionPanel extends BaseOperationPanel {

    private JComboBox<AccountEntry> tuitionCombo;
    private JTextField amountField;

    public PayTuitionPanel(BankingService bankingService, MainFrame mainFrame) {
        super(bankingService, mainFrame);
        buildUI();
    }

    private void buildUI() {
        add(buildTopBar("Pay Tuition/Payer les frais de scolarité"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 247, 250));
        form.setBorder(new EmptyBorder(30, 60, 30, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        form.add(makeLabel("Pay From/Payer à partir de (Chequing / Savings)"), gbc);

        accountCombo = new JComboBox<>();
        accountCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        accountCombo.setPreferredSize(new Dimension(260, 34));
        gbc.gridy = 1;
        form.add(accountCombo, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(makeLabel("Tuition Account/Compte de frais de scolarité"), gbc);

        tuitionCombo = new JComboBox<>();
        tuitionCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tuitionCombo.setPreferredSize(new Dimension(260, 34));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(tuitionCombo, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(makeLabel("Amount/Montant ($)"), gbc);

        amountField = makeTextField();
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(amountField, gbc);

        JButton payBtn = makeActionButton("Pay Tuition/Payer les frais de scolarité");
        gbc.gridy = 6;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(payBtn, gbc);

        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 7;
        gbc.insets = new Insets(8, 0, 0, 0);
        form.add(resultLabel, gbc);

        payBtn.addActionListener(e -> doPayTuition());
        amountField.addActionListener(e -> doPayTuition());

        add(form, BorderLayout.CENTER);
    }

    private void doPayTuition() {
        AccountEntry fromEntry = (AccountEntry) accountCombo.getSelectedItem();
        AccountEntry tuitionEntry = (AccountEntry) tuitionCombo.getSelectedItem();

        if (fromEntry == null || tuitionEntry == null) {
            showResult(false, "Please select both accounts./Veuillez sélectionner les deux comptes.");
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

        TransactionResult result = bankingService.payTuition(
                session,
                fromEntry.getAccount().getAccountNumber(),
                tuitionEntry.getAccount().getAccountNumber(),
                cents
        );
        if (result.isSuccess()) {
            onSessionSet();
        }
        showResult(result.isSuccess(), result.getMessage());
    }

    @Override
    protected void onSessionSet() {
        if (accountCombo == null || tuitionCombo == null) return;

        List<Account> all = bankingService.getAccounts(session);

        List<Account> regular = all.stream()
                .filter(a -> a.getAccountType() != AccountType.TUITION)
                .collect(Collectors.toList());

        List<Account> tuition = all.stream()
                .filter(a -> a.getAccountType() == AccountType.TUITION)
                .collect(Collectors.toList());

        populateAccountCombo(accountCombo, regular);
        populateAccountCombo(tuitionCombo, tuition);

        if (resultLabel != null) resultLabel.setText(" ");
        if (amountField != null) amountField.setText("");
    }
}
