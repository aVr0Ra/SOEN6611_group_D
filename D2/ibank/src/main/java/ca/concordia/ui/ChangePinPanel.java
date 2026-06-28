package ca.concordia.ui;

import ca.concordia.model.TransactionResult;
import ca.concordia.service.BankingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ChangePinPanel extends BaseOperationPanel {

    private JPasswordField oldPinField;
    private JPasswordField newPinField;
    private JPasswordField confirmPinField;

    public ChangePinPanel(BankingService bankingService, MainFrame mainFrame) {
        super(bankingService, mainFrame);
        buildUI();
    }

    private void buildUI() {
        add(buildTopBar("Change PIN/Changer le NIP"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 247, 250));
        form.setBorder(new EmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        form.add(makeLabel("Current PIN/NIP actuel"), gbc);

        oldPinField = new JPasswordField();
        oldPinField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        oldPinField.setPreferredSize(new Dimension(260, 34));
        gbc.gridy = 1;
        form.add(oldPinField, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(makeLabel("New PIN/Nouveau NIP"), gbc);

        newPinField = new JPasswordField();
        newPinField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        newPinField.setPreferredSize(new Dimension(260, 34));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(newPinField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(makeLabel("Confirm New PIN/Confirmer le nouveau NIP"), gbc);

        confirmPinField = new JPasswordField();
        confirmPinField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        confirmPinField.setPreferredSize(new Dimension(260, 34));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(confirmPinField, gbc);

        JButton changeBtn = makeActionButton("Change PIN/Changer le NIP");
        gbc.gridy = 6;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(changeBtn, gbc);

        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 7;
        gbc.insets = new Insets(8, 0, 0, 0);
        form.add(resultLabel, gbc);

        changeBtn.addActionListener(e -> doChangePin());
        confirmPinField.addActionListener(e -> doChangePin());
        oldPinField.addActionListener(e -> newPinField.requestFocus());
        newPinField.addActionListener(e -> confirmPinField.requestFocus());

        add(form, BorderLayout.CENTER);
    }

    private void doChangePin() {
        String oldPin = new String(oldPinField.getPassword());
        String newPin = new String(newPinField.getPassword());
        String confirmPin = new String(confirmPinField.getPassword());

        if (!newPin.equals(confirmPin)) {
            showResult(false, "New PINs do not match/Les nouveaux NIP ne correspondent pas.");
            newPinField.setText("");
            confirmPinField.setText("");
            return;
        }

        TransactionResult result = bankingService.changePin(session, oldPin, newPin);
        showResult(result.isSuccess(), result.getMessage());
        oldPinField.setText("");
        newPinField.setText("");
        confirmPinField.setText("");
    }

    @Override
    protected void onSessionSet() {
        if (oldPinField != null) oldPinField.setText("");
        if (newPinField != null) newPinField.setText("");
        if (confirmPinField != null) confirmPinField.setText("");
        if (resultLabel != null) resultLabel.setText(" ");
    }
}