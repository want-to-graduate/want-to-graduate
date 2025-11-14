package ui.selectCoursePage;

import com.google.gson.Gson;
import ui.PageNavigator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SelectCoursePage extends JPanel {

    private PageNavigator navigator; // 페이지 네비게이터

    private ArrayList<CourseInfo> courseList = new ArrayList<>(); // JSON에서 읽어온 전체 과목 리스트 테스트

    private ArrayList<CourseInfo> selectedCourses = new ArrayList<>(); // 사용자가 담은 과목들을 누적해서 담아두는 리스트

    // 리스트 모델과 테이블
    private DefaultTableModel tableModel;
    private JTable courseTable;

    public SelectCoursePage(PageNavigator navigator) {
        this.navigator = navigator; // 네비게이터 할당

        
        setLayout(new BorderLayout()); // 배경 레이아웃
        setBackground(new Color(0xF5F6F8)); // 배경 색

        
        JPanel cardPanel = new JPanel(new BorderLayout(0, 20)); // 위아래 20px 간격
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new CompoundBorder(
                new EmptyBorder(30, 40, 30, 40), // 내부 여백
                new LineBorder(new Color(0xE2E4E8), 0, true) // 외부 테두리
        ));

        
        add(cardPanel, BorderLayout.CENTER); // 패널을 가운데에 배치

        // 1. JSON 파일 읽어서 courseList 채우기
        loadCoursesFromJson("datafile/cs_curriculum_2025.json");

        
        JPanel titlePanel = new JPanel(); // 타이틀 패널
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false); // 배경 투명

        JLabel title = new JLabel("2025 교육과정 과목 선택", SwingConstants.LEFT); // 제목
        title.setFont(new Font("나눔고딕", Font.BOLD, 22)); // 폰트
        title.setForeground(new Color(0x111827)); // 색상

        JLabel subtitle = new JLabel("수강한 과목을 선택해 주세요."); // 부제목
        subtitle.setFont(new Font("나눔고딕", Font.PLAIN, 13)); // 폰트
        subtitle.setForeground(new Color(0x6B7280)); // 색상

        titlePanel.add(title); // 제목 추가
        titlePanel.add(Box.createVerticalStrut(8)); // 제목과 부제목 사이 간격
        titlePanel.add(subtitle); // 부제목 추가

        cardPanel.add(titlePanel, BorderLayout.NORTH); // 카드 패널 배치

        JPanel centerPanel = new JPanel(new BorderLayout()); // 중앙 패널
        centerPanel.setOpaque(false); 
        cardPanel.add(centerPanel, BorderLayout.CENTER); // 카드 패널 중앙 배치


        
        String[] columnNames = {"과목번호", "교과목명", "학점", "이수구분"}; // 테이블 컬럼명
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                
                return false; // 수정 못하게
            }
        };

        for (CourseInfo c : courseList) { // courseList의 과목들을 테이블 모델에 추가
            Object[] row = { c.code, c.name, c.credits + "학점", c.category };
            tableModel.addRow(row);
        }

        // Table 스타일 (이건 GPT가 생성했어요..)
        courseTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer,
                                            int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                
                if (c instanceof JComponent jc) {
                    jc.setBorder(new EmptyBorder(0, 16, 0, 16));
                }
                if (isRowSelected(row)) {
                    c.setBackground(new Color(0xE5F0FF));  
                } else {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(0xF9FAFB)); 
                    }
                }

                c.setForeground(new Color(0x111827)); 
                return c;
            }
        };

        courseTable.setFont(new Font("나눔고딕", Font.PLAIN, 14)); // 폰트
        courseTable.setRowHeight(40);  // 행 높이
        courseTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // 다중 선택
        courseTable.setFillsViewportHeight(true); // 빈 공간도 채우기

        
        courseTable.setShowHorizontalLines(false); // 수평선 제거
        courseTable.setShowVerticalLines(false); // 수직선 제거
        courseTable.setIntercellSpacing(new Dimension(0, 8)); // 행 사이 간격 띄우기

        // 헤더 스타일
        var header = courseTable.getTableHeader();
        header.setOpaque(false); // 배경은 렌더러에서 처리

        // 이것도 GPT가 생성
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
                lbl.setHorizontalAlignment(CENTER); // 중앙정렬

                lbl.setBorder(new EmptyBorder(8, 16, 8, 16));

                lbl.setBorder(BorderFactory.createMatteBorder(
                        0, 0, 1, 0, new Color(0xE5E7EB)
                ));

                return lbl;
            }
        });

        // 높이 설정
        header.setPreferredSize(new Dimension(0, 36));


        courseTable.setSelectionBackground(new Color(0xE5F0FF)); // 선택하면 색상 변경
        courseTable.setSelectionForeground(new Color(0x111827)); // 선택된 글자 색상

        JScrollPane scrollPane = new JScrollPane(courseTable); // 스크롤 패널에 테이블 추가
        scrollPane.setBorder(new EmptyBorder(10, 0, 10, 0)); // 테두리 설정
        centerPanel.add(scrollPane, BorderLayout.CENTER); // 중앙 패널에 스크롤 패널 추가

        // 버튼 영역
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setOpaque(false);

        JButton addButton = new JButton("선택 과목 담기"); // "담기" 버튼
        JButton showButton = new JButton("지금까지 담은 과목 보기"); // "보기" 버튼

        stylePrimaryButton(addButton);
        styleSecondaryButton(showButton);

        // 담기 버튼을 누르면 현재 선택된 과목을 selectedCourses에 추가
        addButton.addActionListener(e -> addSelectedCourses());

        // 보기 버튼을 누르면 지금까지 누적된 과목 출력
        showButton.addActionListener(e -> printAccumulatedCourses());

        bottomPanel.add(addButton);
        bottomPanel.add(showButton);

        cardPanel.add(bottomPanel, BorderLayout.SOUTH); 
    }


    private void stylePrimaryButton(JButton button) { // 버튼 스타일
        button.setFont(new Font("나눔고딕", Font.BOLD, 14));
        button.setForeground(new Color(0x111827));
        button.setBackground(new Color(0x2563EB)); 
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(new Color(0xD1D5DB), 1, true));
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) { // 버튼 스타일
        button.setFont(new Font("나눔고딕", Font.BOLD, 14));
        button.setForeground(new Color(0x111827));
        button.setBackground(new Color(0xE5E7EB)); 
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(new Color(0xD1D5DB), 1, true));
        button.setPreferredSize(new Dimension(190, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    
    private void loadCoursesFromJson(String path) { // JSON 파일에서 과목 데이터 읽기
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(path)) {
            CurriculumData data = gson.fromJson(reader, CurriculumData.class);

            if (data == null || data.terms == null) {
                System.out.println("JSON 파싱 결과가 비어 있습니다.");
                return;
            }

            for (TermData term : data.terms) {
                if (term.courses != null) {
                    courseList.addAll(term.courses);
                }
            }

        } catch (IOException e) {
            System.out.println("JSON 파일 읽기 오류: " + e.getMessage());
        }
    }

    
    private void addSelectedCourses() { // 선택된 과목들을 selectedCourses에 누적해서 담기
        int[] selectedRows = courseTable.getSelectedRows();

        if (selectedRows.length == 0) {
            System.out.println("선택된 과목이 없습니다.");
            return;
        }

        int newAdded = 0;
        for (int rowIndex : selectedRows) {
            CourseInfo c = courseList.get(rowIndex);
            boolean alreadyAdded = selectedCourses.stream()
                    .anyMatch(sc -> sc.code.equals(c.code));
            if (!alreadyAdded) { // 중복되지 않은 과목만 추가
                selectedCourses.add(c);
                newAdded++;
            }
        }

        if (newAdded == 0) { // 모두 중복된 경우
            System.out.println("이미 담긴 과목입니다.");
        } else { // 새로 추가된 과목이 있는 경우
            System.out.println("새로 담은 과목: " + newAdded + "개");
        }
    }

    // 담긴 과목 추렭
    private void printAccumulatedCourses() {
        if (selectedCourses.isEmpty()) {
            System.out.println("아직 담긴 과목이 없습니다.");
            return;
        }

        System.out.println("=== 지금까지 담은 과목 목록 ===");
        for (CourseInfo c : selectedCourses) {
            System.out.printf("%s | %s | %d학점 | %s%n",
                    c.code, c.name, c.credits, c.category);
        }
        System.out.println("================================");
    }
    private static class CurriculumData {
        ArrayList<TermData> terms;
    }

    private static class TermData {
        ArrayList<CourseInfo> courses;
    }

    private static class CourseInfo {
        String code;
        String name;
        int credits;
        String category;
    }

}