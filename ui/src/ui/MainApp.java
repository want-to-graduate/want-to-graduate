package ui;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import graduate.Course;
import graduate.Manager;
import graduate.Student;
import graduate.StudentCourseCount;
import ui.MainPage.MainPage;
import ui.chooseStudentNumberPage.ChooseStudentNumberPage;
import ui.selectCoursePage.SelectCoursePage;
import ui.GraduationResultPage.GraduationResultPage;

public class MainApp {

    
    private JFrame frame;          // 메인 윈도우
    private JPanel rootPanel;      // 여러 페이지를 담는 루트 패널
    private CardLayout cardLayout; // 페이지 전환용 레이아웃


    private final StudentCourseCount scc;   
    private final Manager<Course> courseMgr;    // 전체 과목 Manager (백엔드에서 제공)


    private String fullId = "";               // 전체 학번
    private String departmentName = "컴공";        // 학과
    private boolean isDoubleMajor = false;        // 복수전공 여부
    private int generalCredit = 0;                // 교양 학점
    private int mscCredit = 0;                    // MSC 학점 

    
    private List<Integer> selectedCourseIndexes = new ArrayList<>();

    
    private Student currentStudent;

    
    private SelectCoursePage selectCoursePage;

    public MainApp() {
        
        scc = new StudentCourseCount();
        scc.run();                     
        courseMgr = scc.getCourseMgr();

        
        frame = new JFrame("졸업 요건 확인 프로그램");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null); // 화면 가운데 정렬

        
        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);

        
        PageNavigator navigator = pageKey -> {
            cardLayout.show(rootPanel, pageKey);
            rootPanel.revalidate();
            rootPanel.repaint();
        };

        // 학번 선택 페이지에서 사용
        Consumer<String> onFullIdSelected = (fullId) -> {
            System.out.println("입력한 학번: " + fullId);

            this.fullId = fullId;
            
            currentStudent = new Student();

            List<Integer> listSelectId = scc.loadStudentFile(fullId);
            System.out.println("불러온 학번이 선택한 과목 수 : " + listSelectId.size());

            if (!(listSelectId.isEmpty())) {
                System.out.println("기존에 저장된 학번입니다.");
                currentStudent.inputStudent(
                    fullId,
                    "컴공",
                    false,
                    50,
                    scc.getDepMgr()
                );
            } else {
                System.out.println("없는 학번이에여,,, 학번 파일 새로 만들게여,,,");
                currentStudent.inputStudent(
                    fullId,
                    "컴공",
                    false,
                    50,
                    scc.getDepMgr()
                );
                scc.saveStudentFile(currentStudent);
                System.out.println("해당 학번 파일 만들어써여...");
            }

            
            List<Course> curriculumCourses = currentStudent
                    .getGraduationRule()
                    .getCourses();

            
            Consumer<List<Integer>> onResultRequested = (List<Integer> selectedIndexes) -> {
            
                this.selectedCourseIndexes = selectedIndexes;

                GraduationResultPage resultPage = new GraduationResultPage(
                    navigator,
                    this.fullId,
                    selectedIndexes
                );
                
                rootPanel.add(resultPage, Pages.GRADUATION_RESULT_PAGE);
                navigator.navigateTo(Pages.GRADUATION_RESULT_PAGE);
            };
            
            if (selectCoursePage != null) {
                rootPanel.remove(selectCoursePage);
            }

            
            selectCoursePage = new SelectCoursePage(
                navigator,
                curriculumCourses,    // 해당 학번/학과 교과 목록
                fullId,
                onResultRequested     // "결과 보기" 버튼이 눌렸을 때
            );

            
            rootPanel.add(selectCoursePage, Pages.SELECT_COURSE_PAGE);
            navigator.navigateTo(Pages.SELECT_COURSE_PAGE);
        };

        // 페이지 생성
        MainPage mainPage = new MainPage(navigator);
        ChooseStudentNumberPage choosePage = new ChooseStudentNumberPage(navigator, onFullIdSelected);

        // 페이지 등록
        rootPanel.add(mainPage, Pages.MAIN_PAGE);
        rootPanel.add(choosePage, Pages.CHOOSE_STUDENT_NUMBER_PAGE);
        

        // 시작 페이지
        cardLayout.show(rootPanel, Pages.MAIN_PAGE);

        
        frame.setContentPane(rootPanel);
        frame.setVisible(true);
    }

    
    public void navigateTo(String pageKey) {
        cardLayout.show(rootPanel, pageKey);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    // --- Getter ---

    public String getFullId() {
        return fullId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public boolean isDoubleMajor() {
        return isDoubleMajor;
    }

    public int getGeneralCredit() {
        return generalCredit;
    }

    public int getMscCredit() {
        return mscCredit;
    }

    public List<Integer> getSelectedCourseIndexes() {
        return selectedCourseIndexes;
    }

    public Manager<Course> getCourseMgr() {
        return courseMgr;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}