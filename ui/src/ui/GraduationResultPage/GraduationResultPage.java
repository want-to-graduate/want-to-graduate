package ui.GraduationResultPage;

import graduate.Student;
import graduate.StudentCourseCount;
import graduate.Course;
import graduate.GraduationRule;
import graduate.Manager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import ui.PageNavigator;
import ui.Pages;


public class GraduationResultPage extends JPanel {

    
    private final PageNavigator navigator;

    // 결과 텍스트를 보여줄 영역
    private final JTextArea resultArea = new JTextArea();

    /**
     * @param navigator         페이지 전환용
     * @param year              학번 
     * @param departmentName    학과 이름
     * @param isDoubleMajor     복수전공 여부
     * @param generalCredit     이미 이수한 교양 학점
     * @param mscCredit         이미 이수한 MSC 학점
     * @param selectedIndexes   사용자가 선택한 과목의 인덱스
     */
    public GraduationResultPage(PageNavigator navigator,
                                int year,
                                String departmentName,
                                boolean isDoubleMajor,
                                int generalCredit,
                                int mscCredit,
                                List<Integer> selectedIndexes) {

        this.navigator = navigator;

        // 전체 레이아웃 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 헤더 영역
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("졸업 요건 진단 결과");
        title.setFont(new Font("나눔고딕", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        
        if (navigator != null) {
            JButton backButton = new JButton("← 과목 선택으로 돌아가기");
            backButton.addActionListener(e -> navigator.navigateTo(Pages.SELECT_COURSE_PAGE));
            header.add(backButton, BorderLayout.EAST);
        }

        add(header, BorderLayout.NORTH);

        // 결과 표시 영역 설정
        resultArea.setEditable(false);
        resultArea.setFont(new Font("나눔고딕", Font.PLAIN, 13));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // 실제 계산 수행
        calculateAndShowResult(year, departmentName, isDoubleMajor,
                               generalCredit, mscCredit, selectedIndexes);
    }

    
    private void calculateAndShowResult(int year,
                                        String departmentName,
                                        boolean isDoubleMajor,
                                        int generalCredit,
                                        int mscCredit,
                                        List<Integer> selectedIndexes) {

        StringBuilder sb = new StringBuilder();

        try {
            
            StudentCourseCount scc = new StudentCourseCount();
            scc.run();

            Manager<GraduationRule> depMgr = scc.getDepMgr();
            Manager<Course> courseMgr = scc.getCourseMgr();

            
            Student student = new Student();
            student.inputStudent(
                    year,
                    departmentName,
                    isDoubleMajor,
                    generalCredit,
                    mscCredit,
                    depMgr
            );


            student.selectCourses(selectedIndexes, courseMgr);


            List<String> messages = student.checkGraduation();

            sb.append("■ 졸업 요건 진단 결과\n\n");
            for (String msg : messages) {
                sb.append(" - ").append(msg).append("\n");
            }
            sb.append("\n");

            // 5) 학생이 실제로 들은 과목 리스트 출력 (필요 시)
            sb.append("■ 이수한 전공 과목 목록\n\n");
            List<String> takenCourses = student.getTakenCourseList();
            for (String s : takenCourses) {
                sb.append(" - ").append(s).append("\n");
            }

            // sb.append("\n■ 학과 졸업 규칙 정보 (참고용)\n\n");
            // sb.append(student.getGraduationRule().toString()).append("\n");

        } catch (Exception e) {
            
            sb.setLength(0);
            sb.append("오류가 발생했습니다.\n\n")
              .append(e.getClass().getSimpleName()).append(": ")
              .append(e.getMessage());
            e.printStackTrace();
        }

        resultArea.setText(sb.toString());
        resultArea.setCaretPosition(0); 
    }
}