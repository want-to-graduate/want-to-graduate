package ui.chooseStudentNumberPage;

import javax.swing.*;
import java.awt.*;
import ui.PageNavigator;
import ui.Pages;

public class ChooseStudentNumberPage extends JPanel {

    private PageNavigator navigator;

    public ChooseStudentNumberPage(PageNavigator navigator) {
        this.navigator = navigator;

        setLayout(new GridBagLayout());  // 격자 형태
        setBackground(Color.WHITE);      // 배경색을 흰색으로 설정

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel title = new JLabel("자신의 학번을 선택해주세요."); // 제목 
        title.setFont(new Font("나눔고딕", Font.BOLD, 22));  // 폰트 설정
        title.setForeground(new Color(50, 50, 50));          // 글자색 
        add(title, gbc);  

        gbc.gridy++;

        // 버튼들을 담을 패널
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 30, 30)); // 2행 2열, 가로세로 30px 간격
        buttonPanel.setBackground(Color.WHITE);  

        // 버튼 생성
        JButton btn25 = createYearButton("25학번", "25");
        JButton btn24 = createYearButton("24학번", "24");
        JButton btn23 = createYearButton("23학번", "23");
        JButton btn22 = createYearButton("22학번", "22");

        buttonPanel.add(btn25);
        buttonPanel.add(btn24);
        buttonPanel.add(btn23);
        buttonPanel.add(btn22);

        add(buttonPanel, gbc); // 버튼 패널 추가

        


    }

    private JButton createYearButton(String label, String yearValue) { // 버튼 생성 메서드
        JButton btn = new JButton(label); // 버튼을 생성 (label을 입력받음)

        btn.setFont(new Font("나눔고딕", Font.BOLD, 16)); // 폰트 설정
        btn.setPreferredSize(new Dimension(140, 50)); // 크기 설정
        btn.setBackground(new Color(235, 235, 235)); // 배경색
        btn.setFocusPainted(false); // 마우스 클릭시 테두리 제거
        btn.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 2)); // 테두리 설정

        btn.addActionListener(e -> { // 이벤트리스너
            System.out.println("선택한 학번 : " + yearValue);
            navigator.navigateTo(Pages.SELECT_COURSE_PAGE);
        });

        

        return btn; // 버튼 반환
    }
}
