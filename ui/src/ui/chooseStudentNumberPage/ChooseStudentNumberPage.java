package ui.ChooseStudentNumberPage;

import javax.swing.*;
import javax.swing.text.*;
import ui.PageNavigator;
import ui.Pages;
import java.awt.*;
import java.util.function.Consumer;

public class ChooseStudentNumberPage extends JPanel {

    private final Consumer<String> onYearSelected;
    private final PageNavigator navigator;
    private final JTextField studentIdField;
    private final JButton confirmButton;

    public ChooseStudentNumberPage(PageNavigator navigator, Consumer<String> onYearSelected) {
        this.navigator = navigator;
        this.onYearSelected = onYearSelected;

        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel title = new JLabel("학번을 입력해주세요.");
        title.setFont(new Font("나눔고딕", Font.BOLD, 22));
        title.setForeground(new Color(50, 50, 50));
        add(title, gbc);

        gbc.gridy++;

        studentIdField = new JTextField();
        studentIdField.setPreferredSize(new Dimension(240, 40));
        studentIdField.setFont(new Font("나눔고딕", Font.PLAIN, 18));
        studentIdField.setHorizontalAlignment(JTextField.CENTER);

        // 숫자만, 9자리 제한
        ((AbstractDocument) studentIdField.getDocument()).setDocumentFilter(new DigitLengthFilter(9));

        add(studentIdField, gbc);

        gbc.gridy++;

        confirmButton = new JButton("다음");
        confirmButton.setFont(new Font("나눔고딕", Font.BOLD, 16));
        confirmButton.setPreferredSize(new Dimension(120, 40));
        confirmButton.setFocusPainted(false);
        add(confirmButton, gbc);

        confirmButton.addActionListener(e -> handleNext());
    }

    private void handleNext() {
        String fullId = studentIdField.getText().trim();

        if (fullId.length() != 9) {
            JOptionPane.showMessageDialog(this, "학번은 정확히 9자리여야 합니다.");
            return;
        }

        
        onYearSelected.accept(fullId);

        // 다음 페이지 이동
        navigator.navigateTo(Pages.SELECT_COURSE_PAGE);
    }

    // 숫자만 입력 받도록
    static class DigitLengthFilter extends DocumentFilter {
        private final int maxLength;

        public DigitLengthFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) return;
            if (isNumeric(string) && fb.getDocument().getLength() + string.length() <= maxLength) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null) return;
            if (isNumeric(text) && fb.getDocument().getLength() - length + text.length() <= maxLength) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isNumeric(String str) {
            return str.matches("\\d+");
        }
    }
}