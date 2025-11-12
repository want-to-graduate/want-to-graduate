package ui.MainPage;

import javax.swing.*;
import java.awt.*;

public class MainPage extends JPanel {
    public MainPage() {  
        setLayout(new GridBagLayout());  // 격자 형태
        setBackground(Color.WHITE);      // 배경색을 흰색으로 설정

        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // 여백 설정
        gbc.gridx = 0;  
        gbc.gridy = 0;  

        
        JLabel title = new JLabel("졸업 요건 확인 프로그램"); // 제목 
        title.setFont(new Font("나눔고딕", Font.BOLD, 22));  // 폰트 설정
        title.setForeground(new Color(50, 50, 50));          // 글자색 
        add(title, gbc);  

        
        gbc.gridy++;
        gbc.gridy++;

        // 부제목
        JLabel desc = new JLabel("당신의 수강 이력을 기반으로 졸업 가능 여부를 확인합니다.");
        desc.setFont(new Font("나눔고딕", Font.PLAIN, 14));  
        desc.setForeground(new Color(90, 90, 90));           
        add(desc, gbc);

        
        gbc.gridy++;
        gbc.insets = new Insets(40, 10, 10, 10); // 위쪽 40px 여백 추가



        // 버튼들을 담을 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);  

        // 버튼 2개 생성
        JButton startBtn = new JButton("시작하기");

        // 버튼 폰트 설정
        Font btnFont = new Font("나눔고딕", Font.BOLD, 14);
        startBtn.setFont(btnFont);
        

        // 버튼 색상 설정
        startBtn.setBackground(new Color(230, 230, 230));

        // 외곽선 제거
        startBtn.setFocusPainted(false);

        // 가로 120px, 세로 40px
        startBtn.setPreferredSize(new Dimension(120, 40));

        
        buttonPanel.add(startBtn);
        // buttonPanel.add(Box.createHorizontalStrut(20)); // 버튼 사이의 공백


        
        add(buttonPanel, gbc); // 버튼 패널 추가
    }
}
