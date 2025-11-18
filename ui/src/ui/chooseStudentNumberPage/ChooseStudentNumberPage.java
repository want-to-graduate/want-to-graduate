package ui.chooseStudentNumberPage;

import javax.swing.*;

import ui.PageNavigator;
import ui.Pages;

import java.awt.*;
import java.util.function.IntConsumer;

public class ChooseStudentNumberPage extends JPanel {

    private PageNavigator navigator;
    private final IntConsumer onYearSelected;


    private static String selectedYear;

    
    private static int selectedEntranceYear;

    public ChooseStudentNumberPage(PageNavigator navigator, IntConsumer onYearSelected) {
        this.navigator = navigator;
        this.onYearSelected = onYearSelected;

        

        // 화면 레이아웃 설정: 가운데 정렬을 위한 GridBagLayout 사용
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10); // 컴포넌트들 사이 여백
        gbc.gridx = 0;
        gbc.gridy = 0;

        // 제목 라벨
        JLabel title = new JLabel("자신의 학번을 선택해주세요.");
        title.setFont(new Font("나눔고딕", Font.BOLD, 22));
        title.setForeground(new Color(50, 50, 50));
        add(title, gbc);

        // 버튼 영역으로 한 줄 내려감
        gbc.gridy++;

        // 학번 버튼들을 담을 패널 (2행 2열 격자)
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        buttonPanel.setBackground(Color.WHITE);

        // 각 학번 버튼 생성 (라벨, 실제 학번 값)
        JButton btn25 = createYearButton("25학번", "25");
        JButton btn24 = createYearButton("24학번", "24");
        JButton btn23 = createYearButton("23학번", "23");
        JButton btn22 = createYearButton("22학번", "22");

        // 패널에 버튼 추가
        buttonPanel.add(btn25);
        buttonPanel.add(btn24);
        buttonPanel.add(btn23);
        buttonPanel.add(btn22);

        // 화면에 버튼 패널 추가
        add(buttonPanel, gbc);

        btn25.addActionListener(e -> {
            onYearSelected.accept(2025); 
        });
        btn24.addActionListener(e -> {
            onYearSelected.accept(2024); 
        });
        btn23.addActionListener(e -> {
            onYearSelected.accept(2023); 
        });
        btn22.addActionListener(e -> {
            onYearSelected.accept(2022); 
        });
    }

    /**
     * 학번 선택 버튼을 만들어 주는 헬퍼 메서드
     * @param label    버튼에 보여줄 텍스트
     * @param yearValue 내부적으로 사용할 학번 값 문자열 
     */
    private JButton createYearButton(String label, String yearValue) {
        JButton btn = new JButton(label);

        btn.setFont(new Font("나눔고딕", Font.BOLD, 16));
        btn.setPreferredSize(new Dimension(140, 50));
        btn.setBackground(new Color(235, 235, 235));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));

        // 버튼 클릭 시 실행되는 코드
        btn.addActionListener(e -> {
            
            selectedYear = yearValue;


            try {
                selectedEntranceYear = Integer.parseInt(yearValue);
            } catch (NumberFormatException ex) {

                selectedEntranceYear = 0;
                System.out.println("학번 파싱 오류: " + yearValue);
            }

            System.out.println("선택한 학번(문자열) : " + selectedYear);
            System.out.println("선택한 학번(정수)   : " + selectedEntranceYear);

            // 다음 페이지(과목 선택 페이지)로 이동
            navigator.navigateTo(Pages.SELECT_COURSE_PAGE);
        });

        return btn;
    }

    

    
    public static int getSelectedEntranceYear() {
        return selectedEntranceYear;
    }
}