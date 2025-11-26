package ui.GeneralAndDoublePage;

import ui.MainApp;
import ui.PageNavigator;
import ui.Pages;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import java.awt.*;


public class GeneralAndDoublePage extends JPanel {

    private final JTextField generalCreditsField;

    private final PageNavigator navigator;

    private JToggleButton singleMajorButton;
    private JToggleButton doubleMajorButton;
    private boolean isDoubleMajor = false;

    public GeneralAndDoublePage(PageNavigator navigator) {
        this.navigator = navigator;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel cardPanel = new JPanel(new BorderLayout(0, 20)); // 내부 카드
        cardPanel.setBackground(Color.WHITE); // 카드 배경 색
        cardPanel.setBorder(new CompoundBorder( // 카드 테두리
                new EmptyBorder(30, 40, 30, 40),
                new LineBorder(new Color(0xE2E4E8), 0, true)   
        ));
        add(cardPanel, BorderLayout.CENTER);
        
        // 제목 영역
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS)); // 수직 정렬
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("교양과 복수전공 유무를 입력해주세요."); // 제목
        title.setFont(new Font("나눔고딕", Font.BOLD, 22));
        title.setForeground(new Color(0x111827));

        JLabel subtitle = new JLabel("이수한 교양 학점과 복수전공 유무를 선택해주세요."); // 안내 문구
        subtitle.setFont(new Font("나눔고딕", Font.PLAIN, 13));
        subtitle.setForeground(new Color(0x6B7280));

        titlePanel.add(title); // 제목 생성
        titlePanel.add(Box.createVerticalStrut(8)); // 간격
        titlePanel.add(subtitle); // 부제목 생성

        cardPanel.add(titlePanel, BorderLayout.NORTH);

        generalCreditsField = new JTextField();
        generalCreditsField.setPreferredSize(new Dimension(200, 36));
        generalCreditsField.setMaximumSize(new Dimension(200, 36));
        generalCreditsField.setFont(new Font("나눔고딕", Font.PLAIN, 16));
        generalCreditsField.setHorizontalAlignment(JTextField.CENTER);

        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
        generalPanel.setOpaque(false);

        JLabel generalLabel = new JLabel("교양 학점");
        generalLabel.setFont(new Font("나눔고딕", Font.BOLD, 14));
        generalLabel.setForeground(new Color(0x111827));

        generalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        generalCreditsField.setAlignmentX(Component.CENTER_ALIGNMENT);
        generalCreditsField.setMaximumSize(new Dimension(200, 36));

        generalPanel.add(Box.createVerticalStrut(100));

        generalPanel.add(generalLabel);
        generalPanel.add(Box.createVerticalStrut(8));
        generalPanel.add(generalCreditsField);

        // 전공 유형 섹션
        generalPanel.add(Box.createVerticalStrut(32));

        JLabel majorLabel = new JLabel("전공 유형");
        majorLabel.setFont(new Font("나눔고딕", Font.BOLD, 14));
        majorLabel.setForeground(new Color(0x111827));
        majorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel majorButtonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        majorButtonRow.setOpaque(false);

        singleMajorButton = new JToggleButton("단일 전공");
        doubleMajorButton = new JToggleButton("복수 전공");

        styleToggleButton(singleMajorButton);
        styleToggleButton(doubleMajorButton);

        // 기본값: 단일 전공 선택
        singleMajorButton.setSelected(true);
        applyToggleStyle();

        singleMajorButton.addActionListener(e -> {
            isDoubleMajor = false;
            singleMajorButton.setSelected(true);
            doubleMajorButton.setSelected(false);
            applyToggleStyle();
        });

        doubleMajorButton.addActionListener(e -> {
            isDoubleMajor = true;
            singleMajorButton.setSelected(false);
            doubleMajorButton.setSelected(true);
            applyToggleStyle();
        });

        majorButtonRow.add(singleMajorButton);
        majorButtonRow.add(doubleMajorButton);

        generalPanel.add(majorLabel);
        generalPanel.add(Box.createVerticalStrut(8));
        generalPanel.add(majorButtonRow);

        cardPanel.add(generalPanel, BorderLayout.CENTER);

        // 하단 네비게이션 버튼 영역
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftButtonPanel.setOpaque(false);

        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightButtonPanel.setOpaque(false);

        JButton backButton = new JButton("← 학번 다시 선택");
        JButton nextButton = new JButton("다음");

        styleSecondaryButton(backButton);
        stylePrimaryButton(nextButton);

        backButton.addActionListener(e -> navigator.navigateTo(Pages.CHOOSE_STUDENT_NUMBER_PAGE));
        nextButton.addActionListener(e -> {
            int generalCredits = getGeneralCredits();
            boolean doubleMajor = isDoubleMajor();

            MainApp app = MainApp.getInstance();
            app.getGraduationResultPage().updateGeneralInfo(generalCredits, doubleMajor);

            navigator.navigateTo(Pages.SELECT_COURSE_PAGE);
        });

        leftButtonPanel.add(backButton);
        rightButtonPanel.add(nextButton);

        bottomPanel.add(leftButtonPanel, BorderLayout.WEST);
        bottomPanel.add(rightButtonPanel, BorderLayout.EAST);

        cardPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("나눔고딕", Font.BOLD, 14));
        button.setBackground(new Color(0xE0F2FE));
        button.setForeground(new Color(0x0F172A));
        button.setBorder(new LineBorder(new Color(0x7DD3FC), 1, true));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 36));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("나눔고딕", Font.PLAIN, 13));
        button.setForeground(new Color(0x374151));
        button.setBackground(new Color(0xF9FAFB));
        button.setBorder(new LineBorder(new Color(0xD1D5DB), 1, true));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 32));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleToggleButton(JToggleButton button) {
        button.setFont(new Font("나눔고딕", Font.PLAIN, 13));
        button.setForeground(new Color(0x374151));
        button.setBackground(new Color(0xF9FAFB));
        button.setBorder(new LineBorder(new Color(0xD1D5DB), 1, true));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(110, 32));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void applyToggleStyle() {
        if (singleMajorButton == null || doubleMajorButton == null) return;

        if (singleMajorButton.isSelected()) {
            singleMajorButton.setBackground(new Color(0xE0F2FE));
            singleMajorButton.setForeground(new Color(0x0F172A));
            singleMajorButton.setBorder(new LineBorder(new Color(0x7DD3FC), 1, true));
        } else {
            singleMajorButton.setBackground(new Color(0xF9FAFB));
            singleMajorButton.setForeground(new Color(0x374151));
            singleMajorButton.setBorder(new LineBorder(new Color(0xD1D5DB), 1, true));
        }

        if (doubleMajorButton.isSelected()) {
            doubleMajorButton.setBackground(new Color(0xE0F2FE));
            doubleMajorButton.setForeground(new Color(0x0F172A));
            doubleMajorButton.setBorder(new LineBorder(new Color(0x7DD3FC), 1, true));
        } else {
            doubleMajorButton.setBackground(new Color(0xF9FAFB));
            doubleMajorButton.setForeground(new Color(0x374151));
            doubleMajorButton.setBorder(new LineBorder(new Color(0xD1D5DB), 1, true));
        }
    }
    
    // 교양 학점 반환
    public int getGeneralCredits() {
        String text = generalCreditsField.getText();
        if (text == null) return 0;
        text = text.trim();
        if (text.isEmpty()) return 0;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // 복수 전공 여부 반환
    public boolean isDoubleMajor() {
        return isDoubleMajor;
    }

}