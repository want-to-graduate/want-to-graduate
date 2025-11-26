package ui;

import java.awt.CardLayout;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import graduate.Course;
import graduate.CourseType;
import graduate.Manager;
import graduate.Student;
import graduate.StudentCourseCount;
import ui.MainPage.MainPage;
import ui.chooseStudentNumberPage.ChooseStudentNumberPage;
import ui.selectCoursePage.SelectCoursePage;
import ui.GraduationResultPage.GraduationResultPage;
import ui.selectMscCoursePage.SelectMscCoursePage;

public class MainApp {

    private JFrame frame;          // 메인 윈도우
    private JPanel rootPanel;      // 여러 페이지를 담는 루트 패널
    private CardLayout cardLayout; // 페이지 전환용 레이아웃

    // 백엔드
    private final StudentCourseCount scc;          // 과/졸업요건/학생파일 관리
    private final Manager<Course> courseMgr;       // 전체 과목 Manager

    // 현재 로그인한(?) 학생 정보
    private String fullId = "";                    // 전체 학번 (예: 202015071)
    private String departmentName = "컴공";        // 학과 이름
    private boolean isDoubleMajor = false;         // 복수전공 여부
    private int generalCredit = 50;                // 교양 학점 (임시 기본값)

    private Student currentStudent;

    // 화면들
    private SelectCoursePage selectCoursePage;
    private SelectMscCoursePage selectMscCoursePage;
    private GraduationResultPage graduationResultPage;

    public MainApp() {

        
        scc = new StudentCourseCount();
        scc.run();                        
        courseMgr = scc.getCourseMgr();   

        
        frame = new JFrame("졸업 요건 확인 프로그램");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null); 

        
        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);

        
        PageNavigator navigator = pageKey -> {
            cardLayout.show(rootPanel, pageKey);
            rootPanel.revalidate();
            rootPanel.repaint();
        };

        
        Consumer<String> onFullIdSelected = (inputFullId) -> {
            System.out.println("입력한 학번: " + inputFullId);
            this.fullId = inputFullId;
            this.currentStudent = new Student();

        
            List<Integer> savedIds = scc.loadStudentFile(fullId);
            System.out.println("불러온 과목 개수: " + savedIds.size());

        
            currentStudent.inputStudent(
                    fullId,
                    departmentName,
                    isDoubleMajor,
                    generalCredit,
                    scc.getDepMgr()
            );

            
            if (!savedIds.isEmpty()) {
                currentStudent.loadStudentCourses(savedIds, courseMgr);
            } else {
            
                System.out.println("신규 학번, 파일 생성");
                scc.saveStudentFile(currentStudent);
            }

            
            List<Course> curriculumCourses =
                    currentStudent.getGraduationRule().getCourses();

            List<Course> mscCourses =
                    courseMgr.filterBy(c -> c.getType() == CourseType.MSC);

            
            if (selectCoursePage != null) {
                rootPanel.remove(selectCoursePage);
            }
            if (selectMscCoursePage != null) {
                rootPanel.remove(selectMscCoursePage);
            }
            if (graduationResultPage != null) {
                rootPanel.remove(graduationResultPage);
            }

            
            selectCoursePage = new SelectCoursePage(
                    navigator,
                    curriculumCourses,   // 전공 과목 목록
                    fullId,
                    savedIds            // 이미 들은 과목 id 리스트 
                   
            );

            selectMscCoursePage = new SelectMscCoursePage(
                    navigator,
                    fullId,
                    mscCourses,
                    savedIds            
            );

            graduationResultPage = new GraduationResultPage(
                    navigator,
                    fullId             
            );

            
            rootPanel.add(selectCoursePage, Pages.SELECT_COURSE_PAGE);
            rootPanel.add(selectMscCoursePage, Pages.SELECT_MSC_PAGE);
            rootPanel.add(graduationResultPage, Pages.GRADUATION_RESULT_PAGE);

            
            navigator.navigateTo(Pages.SELECT_COURSE_PAGE);
        };

        
        MainPage mainPage = new MainPage(navigator);
        ChooseStudentNumberPage choosePage = new ChooseStudentNumberPage(
                navigator,
                onFullIdSelected       
        );

        rootPanel.add(mainPage, Pages.MAIN_PAGE);
        rootPanel.add(choosePage, Pages.CHOOSE_STUDENT_NUMBER_PAGE);

        
        cardLayout.show(rootPanel, Pages.MAIN_PAGE);

        frame.setContentPane(rootPanel);
        frame.setVisible(true);
    }

    
    public void navigateTo(String pageKey) {
        cardLayout.show(rootPanel, pageKey);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    

    public String getFullId() {
        return fullId;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public Manager<Course> getCourseMgr() {
        return courseMgr;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}