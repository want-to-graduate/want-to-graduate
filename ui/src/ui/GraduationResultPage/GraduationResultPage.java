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
    private final String fullId;   // 결과를 계산할 학생 전체 학번

    // GeneralAndDoublePage에서 입력받은 값 (초기값은 0 / false 로 시작하고, 나중에 setter로 갱신)
    private int generalCredits = 0;      // 이수한 교양 학점
    private boolean isDoubleMajor = false;   // 복수전공 여부 (false = 단일전공)

    private final JLabel statusLabel = new JLabel();
    private final JLabel guideLabel = new JLabel();

    
    private final JPanel resultListPanel = new JPanel();

    /**
     * @param navigator 페이지 전환용
     * @param fullId    학생 전체 학번
     */
    public GraduationResultPage(PageNavigator navigator,
                                String fullId) {
        this.navigator = navigator;
        this.fullId = fullId;

        
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

        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // 내용
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 40, 5, 40); // 좌우 여백

        // 상태
        statusLabel.setFont(new Font("나눔고딕", Font.BOLD, 22));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(statusLabel, gbc);

        // 안내
        guideLabel.setFont(new Font("나눔고딕", Font.PLAIN, 14));
        guideLabel.setForeground(new Color(90, 90, 90));
        gbc.gridy = 1;
        contentPanel.add(guideLabel, gbc);

        // 리스트
        resultListPanel.setLayout(new GridBagLayout());
        resultListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(resultListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        contentPanel.add(scrollPane, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // 결과 계산
        refreshResult();

        // 페이지가 보일 때마다 새로고침
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshResult();
            }
        });
    }

    /**
     * GeneralAndDoublePage에서 입력한 교양 학점 / 전공 유형 정보를 나중에 주입할 때 사용한다.
     * 값이 주입되기 전에는 기본값 0 / false로 동작한다.
     */
    public void updateGeneralInfo(int generalCredits, boolean isDoubleMajor) {
        this.generalCredits = generalCredits;
        this.isDoubleMajor = isDoubleMajor;
    }

    // 결과 새로고침
    private void refreshResult() {
        List<String> messages = computeResult(this.fullId);
        showMessages(messages);
    }

    // 졸업 요건 계산 로직
    private List<String> computeResult(String fullId) {
        
        StudentCourseCount scc = new StudentCourseCount();
        scc.run();

        
        Student student = new Student();


        List<Integer> courseIds = scc.loadStudentFile(fullId);

        student.inputStudent(fullId, "컴공", isDoubleMajor, generalCredits, scc.getDepMgr());

        
        if (courseIds != null && !courseIds.isEmpty()) {
            student.loadStudentCourses(courseIds, scc.getCourseMgr());
        }

        
        List<String> messages = student.checkGraduation();
        return messages;
    }

    /**
     * 
     *
     * @param messages 졸업 요건 체크 결과 메시지 리스트
     */
    private void showMessages(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            statusLabel.setText("표시할 결과가 없습니다.");
            statusLabel.setForeground(Color.DARK_GRAY);
            guideLabel.setText("");
            resultListPanel.removeAll();
            resultListPanel.revalidate();
            resultListPanel.repaint();
            return;
        }

        String last = messages.get(messages.size() - 1); 
        boolean pass = last.contains("졸업 가능합니다");
        boolean hideFinalFail = true; // 졸업 실패 문장 숨김

        
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
            guideLabel.setText("현재까지 저장된 수강 이력 기준으로 모든 졸업 요건을 만족했습니다.");

            
            JPanel summaryRow = createResultRow("✅", "모든 졸업 요건을 충족했습니다.", "");
            resultListPanel.add(summaryRow, rowGbc);
            rowGbc.gridy++;

            
            for (String msg : messages) {
                // 학부 기초에 대한 문장에서 /0이면 메세지를 출력하지 않음
                if (msg.contains("학부기초필수") || msg.contains("학부기초선택")) {
                    
                    if (msg.contains("/0과목")) {
                        continue;
                    }
                }

                // 마지막 성공 문장을 숨김
                if (hideFinalFail && msg.contains("졸업 가능합니다! 축하합니다!")) {
                    continue;
                }

                String title = msg;
                String detail = "";
                
                int idx2 = msg.indexOf("충족");
                
                if (idx2 != -1) {
                    title = msg.substring(0, idx2 + 2).trim();
                    detail = msg.substring(idx2 + 2).trim();
                }

                JPanel row = createResultRow("•", title, detail);
                resultListPanel.add(row, rowGbc);
                rowGbc.gridy++;
            }
        } else {
            statusLabel.setText("아직 졸업까지 조금 더 필요해요.");
            statusLabel.setForeground(new Color(230, 140, 0));
            guideLabel.setText("아래 부족한 항목을 채우면 졸업 요건을 만족할 수 있어요.");

            for (String msg : messages) {
                // 학부 기초에 대한 문장에서 /0이면 메세지를 출력하지 않음
                if (msg.contains("학부기초필수") || msg.contains("학부기초선택")) {
                    
                    if (msg.contains("/0과목")) {
                        continue;
                    }
                }

                // 마지막 실패 문장은 숨김
                if (hideFinalFail && msg.contains("졸업요건을 만족하지 못했습니다")) {
                    continue;
                }
                

                String title = msg;
                String detail = "";
                int idx = msg.indexOf("부족");
                
                if (idx != -1) {
                    title = msg.substring(0, idx + 2).trim(); 
                    detail = msg.substring(idx + 2).trim();   
                }

                JPanel row = createResultRow("•", title, detail);
                resultListPanel.add(row, rowGbc);
                rowGbc.gridy++;
            }
        }

        resultListPanel.revalidate();
        resultListPanel.repaint();
    }

    
    private JPanel createResultRow(String iconText, String title, String detail) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(new Color(248, 249, 252));
        row.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 8);

        
        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("나눔고딕", Font.BOLD, 16));
        c.gridx = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        row.add(iconLabel, c);

        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("나눔고딕", Font.BOLD, 14));
        c.gridx = 1;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.WEST;
        row.add(titleLabel, c);

        
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