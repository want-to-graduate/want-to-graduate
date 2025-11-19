package ui.selectCoursePage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


// 백엔드 쪽 클래스 import (패키지명 graduate 기준)
import graduate.Course;
import graduate.Student;
import graduate.StudentCourseCount;
import ui.PageNavigator;
import ui.Pages;

public class SelectCoursePage extends JPanel {

    private final PageNavigator navigator;     // 페이지 네비게이터
    private final int entryYear;              // 선택한 학번의 "입학 연도" (예: 25 → 2025 처럼 의미만 보관)
    private final List<Integer> selectedCourseIndexes = new ArrayList<>();

    // 선택 가능한 과목 목록 
    private final List<Course> courseList = new ArrayList<>();

    // 사용자가 담은 과목들 (누적)
    private final List<Course> selectedCourses = new ArrayList<>();

    // 테이블
    private DefaultTableModel tableModel;
    private JTable courseTable;

    private final Consumer<List<Integer>> onResultRequested;


    /**
     * @param navigator 페이지 전환용
     * @param courses   과목 목록
     * @param entryYear 선택한 학번
     */

    public SelectCoursePage(PageNavigator navigator, List<Course> courses, int entryYear, Consumer<List<Integer>> onResultRequested) {
        this.navigator = navigator;
        this.entryYear = entryYear;
        this.onResultRequested = onResultRequested;

        StudentCourseCount scc = new StudentCourseCount();
        scc.run();

        Student student = new Student();
        student.inputStudent(entryYear, "컴공", false, 50, 30, scc.getDepMgr());

        // 과목 복사
        if (courses != null) {
            this.courseList.addAll(student.getGraduationRule().getCourses());
        }

        // UI
        setLayout(new BorderLayout()); // 전체 레이아웃
        setBackground(new Color(0xF5F6F8)); // 전체 배경

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

        JLabel title = new JLabel(entryYear + "학번 교육과정 과목 선택"); // 입력 받은 학번의 교육 과정 과목 선택 제목
        title.setFont(new Font("나눔고딕", Font.BOLD, 22));
        title.setForeground(new Color(0x111827));

        JLabel subtitle = new JLabel("수강한 과목을 선택해 주세요."); // 안내 문구
        subtitle.setFont(new Font("나눔고딕", Font.PLAIN, 13));
        subtitle.setForeground(new Color(0x6B7280));

        titlePanel.add(title); // 제목 생성
        titlePanel.add(Box.createVerticalStrut(8)); // 간격
        titlePanel.add(subtitle); // 부제목 생성

        cardPanel.add(titlePanel, BorderLayout.NORTH);

        // 테이블
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        cardPanel.add(centerPanel, BorderLayout.CENTER); 

        
        String[] columnNames = {"과목 정보"}; // 열 이름
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 사용자가 직접 셀 수정 못하게
                return false;
            }
        };

        // 과목 데이터를 채움, Course에서 받아오는 과목 ArrayList의 내용
        for (Course c : courseList) { // courseList의 각 과목 전체를 출력
            String line = c.toString();
            tableModel.addRow(new Object[]{ line });
        }

        // 스타일링
        courseTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer,
                                             int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                if (comp instanceof JComponent jc) {
                    // 셀 안쪽 좌우 여백
                    jc.setBorder(new EmptyBorder(0, 16, 0, 16));
                }

                // 선택/비선택 배경색
                if (isRowSelected(row)) {
                    comp.setBackground(new Color(0xE5F0FF));  // 연한 파란색
                } else {
                    if (row % 2 == 0) {
                        comp.setBackground(Color.WHITE);
                    } else {
                        comp.setBackground(new Color(0xF9FAFB));
                    }
                }
                comp.setForeground(new Color(0x111827));
                return comp;
            }
        };

        courseTable.setFont(new Font("나눔고딕", Font.PLAIN, 14)); // 테이블의 폰트
        courseTable.setRowHeight(40); // 테이블 행 높이
        courseTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // 여러개 선택
        courseTable.setFillsViewportHeight(true); // 빈 공간 채우기
        courseTable.setShowHorizontalLines(false); // 가로선 없음
        courseTable.setShowVerticalLines(false); // 세로선 없음
        courseTable.setIntercellSpacing(new Dimension(0, 8)); // 셀 간격

        // 헤더 스타일링
        var header = courseTable.getTableHeader();
        header.setOpaque(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            private final Color headerBg = new Color(0xF3F4F6);
            private final Color headerFg = new Color(0x4B5563);
            private final Font headerFont = new Font("나눔고딕", Font.PLAIN, 13);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, false, false, row, column);
                lbl.setFont(headerFont);
                lbl.setForeground(headerFg);
                lbl.setBackground(headerBg);
                lbl.setHorizontalAlignment(CENTER);
                lbl.setBorder(new EmptyBorder(8, 16, 8, 16));
                lbl.setBorder(BorderFactory.createMatteBorder(
                        0, 0, 1, 0, new Color(0xE5E7EB)
                ));
                return lbl;
            }
        });
        header.setPreferredSize(new Dimension(0, 36));

        courseTable.setSelectionBackground(new Color(0xE5F0FF));
        courseTable.setSelectionForeground(new Color(0x111827));

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(new EmptyBorder(10, 0, 10, 0));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // 하단 버튼 선택
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setOpaque(false);

        JButton addButton = new JButton("선택 과목 담기"); // 선택한 과목을 담는 버튼
        JButton showButton = new JButton("지금까지 담은 과목 보기"); // 지금까지 담은 버튼을 console로 확인하는 버튼
        JButton resultButton = new JButton("졸업 결과 보기"); // 졸업의 결과를 확인할 수 있는 페이지로 이동하는 버튼

        stylePrimaryButton(addButton); // 주요 버튼 스타일
        styleSecondaryButton(showButton); // 보조 버튼 스타일
        stylePrimaryButton(resultButton); // 주요 버튼 스타일

        // 버튼 클릭 시 동작 연결
        addButton.addActionListener(e -> addSelectedCourses()); // 선택한 과목을 누적
        showButton.addActionListener(e -> printAccumulatedCourses()); // 지금까지 담은 과목 출력
        resultButton.addActionListener(e -> {
            if (onResultRequested != null) {
                // 현재까지 담긴 과목 인덱스를 복사해서 MainApp으로 전달
                onResultRequested.accept(new ArrayList<>(selectedCourseIndexes));
            }
        });
        // resultButton.addActionListener(e -> {
        //     List<String> messages = getResult();   // 결과 받아오기

        //     System.out.println("=== 졸업 요건 체크 결과 ===");
        //     for (String msg : messages) {
        //         System.out.println(msg);
        //     }
        //     System.out.println("==========================");
        // });

        bottomPanel.add(addButton);
        bottomPanel.add(showButton);
        bottomPanel.add(resultButton);

        cardPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    
    // 버튼 스타일링
    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("나눔고딕", Font.BOLD, 14));
        button.setForeground(new Color(0x111827));
        button.setBackground(new Color(0x2563EB));
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(new Color(0x1D4ED8), 1, true));
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // 버튼 스타일링
    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("나눔고딕", Font.BOLD, 14));
        button.setForeground(new Color(0x111827));
        button.setBackground(new Color(0xE5E7EB));
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(new Color(0xD1D5DB), 1, true));
        button.setPreferredSize(new Dimension(190, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    // 선택한 과목들을 누적하는 메서드
    private void addSelectedCourses() {
        int[] selectedRows = courseTable.getSelectedRows(); // 선택된 행 인덱스 배열
        
        if (selectedRows.length == 0) { // 행이 0이면 출력
            System.out.println("선택된 과목이 없습니다.");
            return;
        }

        
        int newAdded = 0; // 새로 담긴 과목 수 

        for (int rowIndex : selectedRows) {
            Course c = courseList.get(rowIndex);

            int code = c.getId();

            String line = (String) tableModel.getValueAt(rowIndex, 0);
            String firstToken = line.split(" ")[0];

            if (!selectedCourses.contains(c)) { // 과목이 누적 목록에 없으면
                selectedCourses.add(c);
            }
            

            try {
                int courseIndex = Integer.parseInt(firstToken);

                boolean alreadyAddedIndex = selectedCourseIndexes.contains(courseIndex);
                
                if (!alreadyAddedIndex) {
                    selectedCourseIndexes.add(courseIndex);
                    System.out.println("새로 담은 과목 코드: " + courseIndex);
                    newAdded++;
                }
            } catch (NumberFormatException e) {
                // 첫 번째 토큰이 정수가 아닌 경우 무시
                System.out.println("code 오류남");
            }
        }

        if (newAdded == 0) { // 새로 담긴 과목이 없으면
            System.out.println("이미 담겨 있는 과목만 선택되었습니다."); // 출력
        } else {
            System.out.println("새로 담은 과목: " + newAdded + "개"); // 새로담으면 출력
        }
    }

    // 지금까지 담긴 과목들을 콘솔에 출력
    private void printAccumulatedCourses() {
        if (selectedCourses.isEmpty()) { // 담긴 과목이 없으면
            System.out.println("아직 담긴 과목이 없습니다.");
            return;
        }

        System.out.println("=== 지금까지 담은 과목 목록 ===");
        for (Course c : selectedCourses) {
            
            System.out.println(c.toString()); // 과목 정보들을 출력
        }
        System.out.println("================================");
    }

    
    public List<Course> getSelectedCourses() { // 선택한 과목 목록 반환
        return new ArrayList<>(selectedCourses);  
    }

    // public List<String> getResult() {
    //     StudentCourseCount scc = new StudentCourseCount();
    //     scc.run();

    //     Student student = new Student();
    //     student.inputStudent(entryYear, "컴공", false, 50, 30, scc.getDepMgr());

    //     // 선택한 과목들 설정
    //     student.selectCourses(selectedCourseIndexes, scc.getCourseMgr());

    //     // 졸업 요건 체크
    //     return student.checkGraduation();
    // }

    
    // public int getEntryYear() {
    //     return entryYear;
    // }
}