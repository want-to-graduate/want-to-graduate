package ui.selectCoursePage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


// 백엔드 쪽 클래스 import (패키지명 graduate 기준)
import graduate.Course;
import graduate.Student;
import graduate.StudentCourseCount;
import ui.PageNavigator;
import ui.Pages;

public class SelectCoursePage extends JPanel {

    private final PageNavigator navigator;     // 페이지 네비게이터
    private final String fullId;               // 선택한 학번의 전체 ID (예: 202015071)
    private final List<Integer> selectedCourseIndexes = new ArrayList<>(); // 사용자가 선택한 과목 ID 목록    
    private final List<Course> courseList = new ArrayList<>(); // 전체 과목 리스트
    private final List<Course> selectedCourses = new ArrayList<>(); // 선택한 과목 리스트
    
    private final DefaultTableModel tableModel; // 테이블 모델
    private final JTable courseTable; // 과목 테이블

    private final StudentCourseCount scc; // StudentCourseCount 객체
    private final Student student; // Student 객체


    /**
     * @param navigator 페이지 네비게이터
     * @param courses 선택 가능한 과목 목록 
     * @param fullId 학생의 전체 ID
     * @param alreadySelectedList 이미 선택된 과목 ID 목록
     */

    public SelectCoursePage(
                                PageNavigator navigator, 
                                List<Course> courses, 
                                String fullId, 
                                List<Integer> alreadySelectedList) {
        this.navigator = navigator; // 페이지 넘기기 위한 네비게이터
        this.fullId = fullId; // 학생의 ID
        

        this.scc = new StudentCourseCount(); // 선택된 학과 정보를 넣기 위해 StudentCourseCount 생성
        this.scc.run(); // 실행

        this.student = new Student(); // 학생 객체 생성
        this.student.inputStudent(fullId, "컴공", false, 50, scc.getDepMgr()); // 학생 안에 정보를 삽입

        // 과목 복사
        if (courses != null) {
            this.courseList.addAll(student.getGraduationRule().getCourses()); // 학생 객체에 해당 학생의 커리 큘럼에 맞는 과목 목록을 삽입
        }

        if (alreadySelectedList != null && !alreadySelectedList.isEmpty()) { // 이미 들은 과목에 대한 정보가 있으면
            student.loadStudentCourses(alreadySelectedList, scc.getCourseMgr()); // 들은 과목에 대한 리스트를 로드
            this.selectedCourseIndexes.addAll(alreadySelectedList); // 해당 리스트를 모두 selectedCourseIndexes에 삽입
            this.selectedCourses.addAll(student.getTakenCourses()); // 학생이 들은 과목들을 selectedCourses에 삽입
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

        JLabel title = new JLabel(student.getYear() + "학번 교육과정 과목 선택"); // 입력 받은 학번의 교육 과정 과목 선택 제목
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

        
        String[] columnNames = {"ID", "학과 코드", "교과목명", "학점", "이수구분", "(학년-학기)"}; // 열 이름
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 사용자가 직접 셀 수정 못하게
                return false;
            }
        };

        
        // 전체 과목을 표의 형태로 생성
        for (Course c : courseList) { 
            String line = c.toString();
            String[] tokens = line.split(" ");

            String id = tokens[0];
            String code = tokens[1];
            String courseName = tokens[2];
            String creditText =  tokens[3];
            String category = tokens[4];
            String yearSemester = tokens[5];

            tableModel.addRow(new Object[]{id, code, courseName, creditText, category, yearSemester});

        }

        
        courseTable = new JTable(tableModel) { // 과목 테이블을 생성
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer,
                                             int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                if (comp instanceof JComponent jc) {
        
                    jc.setBorder(new EmptyBorder(0, 16, 0, 16)); 
                }

                boolean isCurrentlySelected = isRowSelected(row); // 선택된 행
                boolean isPreviouslyTaken = isRowAlreadySelected(row); // 이미 들은 행

                if (isCurrentlySelected) { // 행을 선택했을 때
                    comp.setBackground(new Color(0xE5F0FF)); // 파란색 배경
                } else if (isPreviouslyTaken) { // 이미 들은 수업일때
                    comp.setBackground(new Color(0xFEF3C7)); // 노란색 배경
                } else { // 모두 아니면
                    
                    if (row % 2 == 0) { // 행이 짝수행이면
                        comp.setBackground(Color.WHITE); // 흰색
                    } else {
                        comp.setBackground(new Color(0xF9FAFB)); // 회색
                    }
                }
                comp.setForeground(new Color(0x111827)); // 기본 색
                return comp; // 리턴
            }
        };

        courseTable.setFont(new Font("나눔고딕", Font.PLAIN, 14)); // 테이블의 폰트
        courseTable.setRowHeight(40); // 테이블 행 높이
        courseTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // 여러개 선택
        courseTable.setFillsViewportHeight(true); // 빈 공간 채우기
        courseTable.setShowHorizontalLines(false); // 가로선 없음
        courseTable.setShowVerticalLines(false); // 세로선 없음
        courseTable.setIntercellSpacing(new Dimension(0, 8)); // 셀 간격

        // 열 크기/정렬 설정
        courseTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        TableColumnModel columnModel = courseTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(60);   // ID
        columnModel.getColumn(1).setPreferredWidth(90);   // 학과 코드
        columnModel.getColumn(2).setPreferredWidth(350);  // 교과목명 
        columnModel.getColumn(3).setPreferredWidth(60);   // 학점
        columnModel.getColumn(4).setPreferredWidth(90);   // 이수구분
        columnModel.getColumn(5).setPreferredWidth(90);   // (학년-학기)

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // 가운데 정렬
        for (int i = 0; i < 6; i++) {
            columnModel.getColumn(i).setCellRenderer(centerRenderer);

        }
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

        JScrollPane scrollPane = new JScrollPane(courseTable); // 스크롤 패널에 테이블 추가
        scrollPane.setBorder(new EmptyBorder(10, 0, 10, 0));  // 스크롤 패널 테두리
        centerPanel.add(scrollPane, BorderLayout.CENTER); // 패널에 스크롤 패널 추가

        // 하단 버튼 선택
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setOpaque(false);

        JButton addButton = new JButton("선택 과목 담기"); // 선택한 과목을 담는 버튼
        JButton showButton = new JButton("지금까지 담은 과목 보기"); // 지금까지 담은 과목을 console로 확인하는 버튼
        JButton resultButton = new JButton("MSC 과목 담기"); // 졸업의 결과를 확인할 수 있는 페이지로 이동하는 버튼

        stylePrimaryButton(addButton); // 주요 버튼 스타일
        styleSecondaryButton(showButton); // 보조 버튼 스타일
        stylePrimaryButton(resultButton); // 주요 버튼 스타일

        // 버튼 클릭 시 동작 연결
        addButton.addActionListener(e -> addSelectedCourses()); // 선택한 과목을 누적
        showButton.addActionListener(e -> printAccumulatedCourses()); // 지금까지 담은 과목 출력
        resultButton.addActionListener(e -> {
            navigator.navigateTo(Pages.SELECT_MSC_PAGE); // MSC 페이지로 이동
        });

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
    
        
    private void addSelectedCourses() {
        int[] selectedRows = courseTable.getSelectedRows(); // 선택된 행 인덱스 배열

        if (selectedRows.length == 0) {
            System.out.println("선택된 과목이 없습니다.");
            return;
        }

        int newAdded = 0;                        // 새로 담긴 과목 수
        List<Integer> newlyAddedIds = new ArrayList<>(); // 이번에 새로 추가된 id만 모으기

        for (int rowIndex : selectedRows) {
            // 0번 컬럼은 ID
            Object value = tableModel.getValueAt(rowIndex, 0); // ID 값 가져오기
            if (value == null) { // value가 없으면 넘어감
                continue;
            }

            int courseId; // 과목 ID를 정의
            try {
                courseId = Integer.parseInt(value.toString()); // ID를 파싱을 해서 courseId에 삽입
            } catch (NumberFormatException e) { // 오류가 발생하면
                System.out.println("ID 파싱 오류: " + value); // 오류 출력하고
                continue; // 넘어감
            }

            
            if (selectedCourseIndexes.contains(courseId)) { // 이미 해당 과목 id가 selectedCourseIndexes에 포함되어 있으면
                continue; // 넘어감
            }

            // selectedCourseIndexes에 새 ID 추가
            selectedCourseIndexes.add(courseId);
            newlyAddedIds.add(courseId); // 이번에 새로 추가된 id만 모으기

            
            Course c = courseList.get(rowIndex); // 과목 목록에서 해당 과목을 가져오고
            if (!selectedCourses.contains(c)) { // 이미 selectedCourses에 과목 목록이 없으면
                selectedCourses.add(c); // selectedCourses에 추가
            }

            newAdded++; 
            System.out.println("새로 담은 과목 코드: " + courseId);
        }

        if (newlyAddedIds.isEmpty()) { // 새로 담긴 과목이 없으면
            System.out.println("이미 담겨 있는 과목만 선택되었습니다.");
            return;
        }

        
        Student fileStudent = new Student(); // 학생 객체를 생성하고
        fileStudent.inputStudent(fullId, "컴공", false, 50, scc.getDepMgr()); // 학생 정보를 삽입한뒤

        fileStudent.loadStudentCourses(selectedCourseIndexes, scc.getCourseMgr()); // 이미 들은 과목들을 가져오고

        scc.saveStudentFile(fileStudent); // 학생 파일을 새롭게 저장

        System.out.println("새로 담은 과목: " + newAdded + "개");
        courseTable.repaint(); // 테이블을 다시 그림
    }

    
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

    private boolean isRowAlreadySelected(int rowIndex) { // 이미 선택된 행인지 확인
        Object value = tableModel.getValueAt(rowIndex, 0); // ID 값 가져오기
        if (value == null) { // 값이 없으면
            return false;
        }

        try {
            int id = Integer.parseInt(value.toString()); // ID를 파싱하고
            return selectedCourseIndexes.contains(id); // selectedCourseIndexes에 포함되어 있는지 반환
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
