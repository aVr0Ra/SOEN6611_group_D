package ca.concordia;

import ca.concordia.data.UserDatabase;
import ca.concordia.data.demo;
import ca.concordia.service.AuthenticationService;
import ca.concordia.service.BankingService;
import ca.concordia.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        UserDatabase db = demo.createDemoDatabase();
        AuthenticationService authService = new AuthenticationService(db);
        BankingService bankingService = new BankingService(db);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(authService, bankingService);
            frame.setVisible(true);
        });
    }
}