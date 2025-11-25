package ui.selectMscCoursePage;

import graduate.Course;
import graduate.StudentCourseCount;
import ui.PageNavigator;
import ui.Pages;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import graduate.Student;



public class SelectMscCoursePage extends JPanel {

    private final PageNavigator navigator;

    // 학생 전체 학번 (예: 202211548)
    private final String fullId;

    // MSC 과목 목록
    private final List<Course> courseList;

    // 이미 담긴 MSC 과목 ID 목록
    private final List<Integer> selectedMscCourseIds = new ArrayList<>();

    // 새로 담은 MSC 과목 리스트
    private final List<Course> selectedMscCourses = new ArrayList<>();

    // 테이블 모델 & 테이블
    private final DefaultTableModel tableModel;
    private final JTable courseTable;

    private final StudentCourseCount scc;
    private final Student student;

    public SelectMscCoursePage(
            PageNavigator navigator,
            String fullId,
            List<Course> mscCourses,
            List<Integer> selectedIndexes
    ) {
        this.navigator = navigator;
        this.courseList = mscCourses != null ? mscCourses : new ArrayList<>();
        this.fullId = fullId;

        this.scc = new StudentCourseCount();
        this.scc.run();

        this.student = new Student();
        this.student.inputStudent(fullId, "컴공", false, 50, scc.getDepMgr());

        // 기존 학생 파일에서 수강 과목 ID 리스트 불러오기
        List<Integer> existingIds = scc.loadStudentFile(fullId);

        if (existingIds != null && !existingIds.isEmpty()) {
            // 기존에 파일에 있던 과목 ID들을 내부 리스트에 반영
            for (Integer id : existingIds) {
                if (!selectedMscCourseIds.contains(id)) {
                    selectedMscCourseIds.add(id);
                }
            }
            // Student 객체에도 기존 수강 과목 반영
            student.loadStudentCourses(existingIds, scc.getCourseMgr());
        }

        if (selectedIndexes != null) {
            for (Integer id : selectedIndexes) {
                if (!selectedMscCourseIds.contains(id)) {
                    selectedMscCourseIds.add(id);
                }
            }
        }

        // 이미 담겨 있던 MSC 과목들을 selectedMscCourses 리스트에도 반영
        // (ID 목록과 실제 Course 객체 리스트를 통합해서 관리)
        if (!this.selectedMscCourseIds.isEmpty()) {
            for (Course c : this.courseList) {
                if (this.selectedMscCourseIds.contains(c.getId())
                        && !this.selectedMscCourses.contains(c)) {
                    this.selectedMscCourses.add(c);
                }
            }
        }

        // ===== 전체 배경 =====
        setLayout(new BorderLayout());
        setBackground(new Color(0xF5F6F8));

        // ===== 카드 패널 (일반 과목 페이지와 같은 스타일) =====
        JPanel cardPanel = new JPanel(new BorderLayout(0, 20));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new CompoundBorder(
                new EmptyBorder(30, 40, 30, 40),
                new LineBorder(new Color(0xE2E4E8), 0, true)
        ));
        add(cardPanel, BorderLayout.CENTER);

        // ===== 상단 헤더 영역 =====
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("MSC 과목 선택");
        titleLabel.setFont(new Font("나눔고딕", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0x111827));

        JLabel subTitleLabel = new JLabel("수강한 MSC(수학·과학·컴퓨터) 과목을 선택해 주세요.");
        subTitleLabel.setFont(new Font("나눔고딕", Font.PLAIN, 13));
        subTitleLabel.setForeground(new Color(0x6B7280));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subTitleLabel);

        cardPanel.add(headerPanel, BorderLayout.NORTH);

        // ===== 중앙 테이블 영역 =====
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        cardPanel.add(centerPanel, BorderLayout.CENTER);

        String[] columnNames = {"ID", "학과 코드", "교과목명", "학점", "이수구분", "(학년-학기)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        courseTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer,
                                             int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                if (comp instanceof JComponent jc) {
                    jc.setBorder(new EmptyBorder(0, 16, 0, 16)); // 셀 좌우 패딩
                }

                boolean alreadySelected = isRowAlreadySelected(row);
                boolean selected = isRowSelected(row);

                if (selected) {
                    comp.setBackground(new Color(0xE5F0FF)); // 파란 선택
                } else if (alreadySelected) {
                    comp.setBackground(new Color(0xFEF3C7)); // 노란색(기존에 담긴 과목)
                } else {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF9FAFB));
                }
                comp.setForeground(new Color(0x111827));
                return comp;
            }
        };

        courseTable.setFont(new Font("나눔고딕", Font.PLAIN, 14));
        courseTable.setRowHeight(40);
        courseTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        courseTable.setFillsViewportHeight(true);
        courseTable.setShowHorizontalLines(false);
        courseTable.setShowVerticalLines(false);
        courseTable.setIntercellSpacing(new Dimension(0, 8));
        courseTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        TableColumnModel columnModel = courseTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(60);   // ID
        columnModel.getColumn(1).setPreferredWidth(90);   // 학과 코드
        columnModel.getColumn(2).setPreferredWidth(350);  // 교과목명
        columnModel.getColumn(3).setPreferredWidth(60);   // 학점
        columnModel.getColumn(4).setPreferredWidth(90);   // 이수구분
        columnModel.getColumn(5).setPreferredWidth(90);   // (학년-학기)

        // 모든 컬럼 가운데 정렬
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }

        // 헤더 스타일 (일반 과목 페이지와 동일)
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
        scrollPane.setBorder(new EmptyBorder(10, 0, 10, 0)); // 위아래 여백
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // ===== 하단 버튼 영역 (일반 과목 페이지와 동일 스타일) =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomPanel.setOpaque(false);

        JButton addButton = new JButton("MSC 과목 담기");
        JButton showButton = new JButton("지금까지 담은 MSC 과목 보기");
        JButton nextButton = new JButton("졸업 결과 보기");

        stylePrimaryButton(addButton);
        styleSecondaryButton(showButton);
        stylePrimaryButton(nextButton);

        addButton.addActionListener(e -> addSelectedMscCourses());
        showButton.addActionListener(e -> printAccumulatedMscCourses());
        
        nextButton.addActionListener(e -> {
            navigator.navigateTo(Pages.GRADUATION_RESULT_PAGE);
        });

        bottomPanel.add(addButton);
        bottomPanel.add(showButton);
        bottomPanel.add(nextButton);

        cardPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 테이블 데이터 채우기
        fillTableWithMscCourses();
    }

    // ===== 데이터 채우기 =====
    private void fillTableWithMscCourses() {
        tableModel.setRowCount(0);
        for (Course c : courseList) {
            String line = c.toString();
            String[] tokens = line.split(" ");

            String id = tokens[0];
            String code = tokens[1];
            String courseName = tokens[2];
            String creditText = tokens[3];
            String category = tokens[4];
            String yearSemester = tokens[5];

            tableModel.addRow(new Object[]{id, code, courseName, creditText, category, yearSemester});
        }
    }

    // ===== 선택/누적 로직 =====
    private void addSelectedMscCourses() {
        int[] selectedRows = courseTable.getSelectedRows();
        if (selectedRows.length == 0) {
            System.out.println("선택된 MSC 과목이 없습니다.");
            return;
        }

        int newAdded = 0;
        List<Integer> newIds = new ArrayList<>();

        for (int rowIndex : selectedRows) {
            if (rowIndex < 0 || rowIndex >= courseList.size()) continue;

            Course c = courseList.get(rowIndex);
            if (!selectedMscCourses.contains(c)) {
                selectedMscCourses.add(c);
            }

            Object idObj = tableModel.getValueAt(rowIndex, 0);
            if (idObj == null) continue;

            try {
                int courseId = Integer.parseInt(idObj.toString());
                // 이미 파일/메모리에 있는 ID면 건너뜀
                if (!selectedMscCourseIds.contains(courseId)) {
                    selectedMscCourseIds.add(courseId);
                    newIds.add(courseId);
                    newAdded++;
                }
            } catch (NumberFormatException ignored) {}
        }

        // 새로 담긴 ID가 있을 때만 Student/파일에 반영
        if (!newIds.isEmpty()) {
            // Student 객체에 새 MSC 과목 반영 (ID 기반)
            student.loadStudentCourses(newIds, scc.getCourseMgr());
            // 전체 수강 목록을 파일로 저장 (기존 + 새 MSC 포함)
            scc.saveStudentFile(student);
        }

        if (newAdded == 0) {
            System.out.println("이미 담겨 있는 MSC 과목만 선택되었습니다.");
        } else {
            System.out.println("새로 담은 MSC 과목: " + newAdded + "개");
        }

        courseTable.repaint();
    }

    private boolean isRowAlreadySelected(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= courseList.size()) return false;
        Course c = courseList.get(rowIndex);
        return selectedMscCourseIds.contains(c.getId());
    }

    private void printAccumulatedMscCourses() {
        if (selectedMscCourses.isEmpty()) {
            System.out.println("담긴 MSC 과목이 없습니다.");
            return;
        }
        System.out.println("=== 지금까지 담은 MSC 과목 목록 ===");
        for (Course c : selectedMscCourses) {
            System.out.println(c);
        }
        System.out.println("================================");
    }

    public List<Integer> getSelectedMscCourseIds() {
        return new ArrayList<>(selectedMscCourseIds);
    }

    // ===== 버튼 스타일 =====
    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("나눔고딕", Font.BOLD, 14));
        button.setForeground(new Color(0x111827));
        button.setBackground(new Color(0x2563EB));
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(new Color(0x1D4ED8), 1, true));
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("나눔고딕", Font.BOLD, 14));
        button.setForeground(new Color(0x111827));
        button.setBackground(new Color(0xE5E7EB));
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(new Color(0xD1D5DB), 1, true));
        button.setPreferredSize(new Dimension(190, 40));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}