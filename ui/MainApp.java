package ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ui.MainPage.MainPage;
import ui.chooseStudentNumberPage.ChooseStudentNumberPage;

public class MainApp {
    JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Main Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            MainPage mainPage = new MainPage();
            frame.setContentPane(mainPage);

            frame.setVisible(true);
        });
    }
}