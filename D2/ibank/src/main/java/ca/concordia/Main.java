package ca.concordia;

import ca.concordia.data.UserDatabase;
import ca.concordia.model.AccountType;
import ca.concordia.model.Session;
import ca.concordia.model.TransactionResult;
import ca.concordia.model.User;
import ca.concordia.service.BankingService;
import ca.concordia.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
