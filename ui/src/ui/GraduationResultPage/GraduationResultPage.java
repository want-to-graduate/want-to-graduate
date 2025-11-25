package ui.GraduationResultPage;

import ui.PageNavigator;

import graduate.StudentCourseCount;
import graduate.Student;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraduationResultPage extends JPanel {

    // 페이지 전환용
    private final PageNavigator navigator;

    // 상단 상태/가이드 문구
    private final JLabel statusLabel = new JLabel();
    private final JLabel guideLabel = new JLabel();

    // 상세 결과를 그리드 형태로 보여줄 패널
    private final JPanel resultListPanel = new JPanel();

    /**
     * @param navigator             페이지 전환용 (현재는 구조상 보유)
     * @param entryYear             입학년도
     * @param selectedCourseIndexes 사용자가 선택한 과목들의 인덱스 리스트
     */
    public GraduationResultPage(PageNavigator navigator,
                                String fullId,
                                List<Integer> selectedCourseIndexes) {
        this.navigator = navigator;

        // 전체 패널 기본 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        
        // 헤더
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 80));
        header.setBackground(Color.WHITE);

        // 제목
        JLabel title = new JLabel("졸업 요건 진단 결과");
        title.setFont(new Font("나눔고딕", Font.BOLD, 28));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(25, 0, 20, 0));

        
        header.add(title, BorderLayout.CENTER); // 헤더에 제목 추가
        add(header, BorderLayout.NORTH); // 헤더를 상단에 추가

        // 내용 영역
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 40, 5, 40); // 좌우 여백

        // 1행 : 상태 문구
        statusLabel.setFont(new Font("나눔고딕", Font.BOLD, 22));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(statusLabel, gbc);

        // 2행: 안내 문구
        guideLabel.setFont(new Font("나눔고딕", Font.PLAIN, 14));
        guideLabel.setForeground(new Color(90, 90, 90));
        gbc.gridy = 1;
        contentPanel.add(guideLabel, gbc);

        // 3행: 부족 항목 리스트
        resultListPanel.setLayout(new GridBagLayout());
        resultListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(resultListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        contentPanel.add(scrollPane, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // 졸업 계산 후 UI에 반영
        List<String> messages = computeResult(fullId, selectedCourseIndexes);
        showMessages(messages);
    }

    /**
     * 졸업 요건 계산 메서드
     *
     * @param entryYear             입학년도
     * @param selectedCourseIndexes 사용자가 선택한 과목들의 인덱스 리스트
     * @return 졸업 요건 체크 결과 메시지 리스트
     */
    private List<String> computeResult(String fullId, List<Integer> selectedCourseIndexes) {

        StudentCourseCount scc = new StudentCourseCount();
        scc.run();

        Student student = new Student();

        // 학생 기본 정보 입력
        student.inputStudent(fullId, "컴공", false, 50, scc.getDepMgr());

        // 선택한 과목 반영
        if (selectedCourseIndexes != null && !selectedCourseIndexes.isEmpty()) {
            student.selectCourses(selectedCourseIndexes, scc.getCourseMgr());
        }

        return student.checkGraduation(); // 졸업 요건 체크 및 결과 메시지 반환
    }

    /**
     * 계산된 졸업 요건 결과 메시지를 UI에 반영
     *
     * @param messages 졸업 요건 체크 결과 메시지 리스트
     */
    private void showMessages(List<String> messages) {
        if (messages == null || messages.isEmpty()) { // 메세지가 비어 있을 경우
            statusLabel.setText("표시할 결과가 없습니다.");
            statusLabel.setForeground(Color.DARK_GRAY);
            

            return;
        }

        String last = messages.get(messages.size() - 1); // 메세지를 가져와서
        boolean pass = last.contains("졸업 가능합니다"); // "졸업 가능합니다"라는 글자가 있을 경우 pass

        // 내용 초기화
        resultListPanel.removeAll();

        GridBagConstraints rowGbc = new GridBagConstraints();
        rowGbc.gridx = 0;
        rowGbc.gridy = 0;
        rowGbc.weightx = 1.0;
        rowGbc.fill = GridBagConstraints.HORIZONTAL;
        rowGbc.insets = new Insets(10, 0, 10, 0);

        if (pass) {
            statusLabel.setText("🎉 졸업 요건을 모두 충족했어요!");
            statusLabel.setForeground(new Color(20, 150, 90));
            guideLabel.setText("선택한 과목 기준으로 모든 졸업 요건을 만족했습니다.");

            
            JPanel row = createResultRow("✅", "모든 졸업 요건을 충족했습니다.", "");
            resultListPanel.add(row, rowGbc);
        } else {
            statusLabel.setText("아직 졸업까지 조금 더 필요해요.");
            statusLabel.setForeground(new Color(230, 140, 0));
            guideLabel.setText("아래 부족한 항목을 채우면 졸업 요건을 만족할 수 있어요.");

            for (String msg : messages) { // 메세지들을 돌면서 
                if (!msg.contains("부족")) { // 부족이라는 메세지가 포함되어 있지 않으면
                    continue; // 넘어감
                }

                // 카드 형태로 나눔
                String title = msg; // 전체 메세지
                String detail = ""; // 상세 메세지
                int idx = msg.indexOf("부족"); // 부족이라는 단어의 위치를 찾음
                if (idx != -1) { // 부족이라는 단어가 있으면
                    title = msg.substring(0, idx + 2).trim(); // 뭐가 부족한지를 제목으로
                    detail = msg.substring(idx + 2).trim();     // 그 뒤의 내용을 분리
                }

                JPanel row = createResultRow("•", title, detail); // 카드 형태로 만듦
                resultListPanel.add(row, rowGbc);
                rowGbc.gridy++;

            }
        }

        resultListPanel.revalidate();
        resultListPanel.repaint();
    }

    // 카드 만들기 메서드
    private JPanel createResultRow(String iconText, String title, String detail) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(new Color(248, 249, 252));
        row.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 8);

        // 아이콘
        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        c.gridx = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        row.add(iconLabel, c);

        // 제목
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("나눔고딕", Font.BOLD, 14));
        c.gridx = 1;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.WEST;
        row.add(titleLabel, c);

        // 상세
        if (!detail.isEmpty()) {
            JLabel detailLabel = new JLabel(detail);
            detailLabel.setFont(new Font("나눔고딕", Font.PLAIN, 13));
            detailLabel.setForeground(new Color(100, 100, 100));
            c.gridx = 2;
            c.weightx = 0;
            c.anchor = GridBagConstraints.EAST;
            row.add(detailLabel, c);
        }

        return row;
    }
}