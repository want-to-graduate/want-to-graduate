package ui.GraduationResultPage;

import ui.PageNavigator;
import ui.Pages;

import graduate.StudentCourseCount;
import graduate.Student;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraduationResultPage extends JPanel {

    private final PageNavigator navigator;
    private final JTextArea resultArea = new JTextArea();

    /**
     * @param navigator             페이지 전환용 
     * @param entryYear             입학년도 
     * @param selectedCourseIndexes 사용자가 선택한 과목들의 인덱스 리스트
     */
    public GraduationResultPage(PageNavigator navigator,
                                int entryYear,
                                List<Integer> selectedCourseIndexes) {
        this.navigator = navigator;

        // 기본 레이아웃 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 헤더
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("졸업 요건 진단 결과");
        title.setFont(new Font("나눔고딕", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        // 뒤로가기 버튼 
        if (navigator != null) {
            JButton backButton = new JButton("← 과목 선택으로 돌아가기");
            backButton.setFocusPainted(false);
            backButton.addActionListener(e -> navigator.navigateTo(Pages.SELECT_COURSE_PAGE));
            header.add(backButton, BorderLayout.EAST);
        }

        add(header, BorderLayout.NORTH);

        // 결과 표시
        resultArea.setEditable(false);
        resultArea.setFont(new Font("나눔고딕", Font.PLAIN, 13));
        resultArea.setLineWrap(true);          // 줄 길면 자동 줄바꿈
        resultArea.setWrapStyleWord(true);     // 단어 기준 줄바꿈
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // 졸업 계산
        List<String> messages = computeResult(entryYear, selectedCourseIndexes);

        // 결과 출력
        showMessages(messages);
    }

    /**
     * 졸업 요건 계산 메서드
     *
     * @param entryYear             입학년도
     * @param selectedCourseIndexes 사용자가 선택한 과목들의 인덱스 리스트
     * @return 졸업 요건 체크 결과 메시지 리스트
     */
    private List<String> computeResult(int entryYear, List<Integer> selectedCourseIndexes) {
        
        StudentCourseCount scc = new StudentCourseCount();
        scc.run();

        
        Student student = new Student();
        
        if (entryYear == 2025) {
            entryYear = 25;
        }
        if (entryYear == 2024) {
            entryYear = 24;
        }
        if (entryYear == 2023) {
            entryYear = 23;
        }
        if (entryYear == 2022) {
            entryYear = 22;
        }

        student.inputStudent(entryYear, "컴공", false, 50, 30, scc.getDepMgr());

        
        if (selectedCourseIndexes != null && !selectedCourseIndexes.isEmpty()) {
            student.selectCourses(selectedCourseIndexes, scc.getCourseMgr());
        }

        
        return student.checkGraduation();
    }

    
    private void showMessages(List<String> messages) {
        StringBuilder sb = new StringBuilder();

        sb.append("■ 졸업 요건 진단 결과\n\n");

        if (messages == null || messages.isEmpty()) {
            sb.append("표시할 결과가 없습니다.\n");
        } else {
            for (String msg : messages) {
                sb.append(" - ").append(msg).append("\n");
            }
        }

        resultArea.setText(sb.toString());
        resultArea.setCaretPosition(0);  
    }
}