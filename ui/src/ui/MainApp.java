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
import ui.SelectCoursePage.SelectCoursePage;
import ui.SelectMscCoursePage.SelectMscCoursePage;
import ui.ChooseStudentNumberPage.ChooseStudentNumberPage;
import ui.GraduationResultPage.GraduationResultPage;
import ui.GeneralAndDoublePage.GeneralAndDoublePage;

public class MainApp {

    private JFrame frame;          // 메인 윈도우
    private JPanel rootPanel;      // 여러 페이지를 담는 루트 패널
    private CardLayout cardLayout; // 페이지 전환용 레이아웃
    private static MainApp instance;

    // 백엔드
    private final StudentCourseCount scc;          // 과/졸업요건/학생파일 관리
    private final Manager<Course> courseMgr;       // 전체 과목 Manager

    // 현재 로그인한(?) 학생 정보
    private String fullId = "";                    // 전체 학번 (예: 202015071)
    private String departmentName = "컴공";        // 학과 이름

    // 교양 학점 / 전공 유형 정보 (GeneralAndDoublePage에서 입력받을 값)
    private int generalCredit = 0;                 // 이수한 교양 학점
    private boolean isDoubleMajor = false;         // 복수전공 여부 (false = 단일전공)

    private Student currentStudent;

    // 화면들
    private SelectCoursePage selectCoursePage;
    private SelectMscCoursePage selectMscCoursePage;
    private GraduationResultPage graduationResultPage;
    private GeneralAndDoublePage generalAndDoublePage;

    public MainApp() {
        instance = this;

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

            // 학번에 해당하는 기존 수강 과목 ID들을 파일에서 불러온다.
            List<Integer> savedIds = scc.loadStudentFile(fullId);
            System.out.println("불러온 과목 개수: " + savedIds.size());

            // 전공 커리큘럼 목록은 학과/학번 정보만 있으면 얻을 수 있으므로,
            // 임시 Student를 만들어 GraduationRule에서 과목 리스트만 가져온다.
            Student tempStudent = new Student();
            tempStudent.inputStudent(
                    fullId,
                    departmentName,
                    false,          // 전공 유형 / 교양 학점은 커리큘럼 목록에는 영향이 없다고 가정
                    0,
                    scc.getDepMgr()
            );

            List<Course> curriculumCourses =
                    tempStudent.getGraduationRule().getCourses();

            // MSC 과목 목록은 전체 과목 Manager에서 타입으로 필터링
            List<Course> mscCourses =
                    courseMgr.filterBy(c -> c.getType() == CourseType.MSC);

            // 기존 페이지가 있다면 제거
            if (selectCoursePage != null) {
                rootPanel.remove(selectCoursePage);
            }
            if (selectMscCoursePage != null) {
                rootPanel.remove(selectMscCoursePage);
            }
            if (graduationResultPage != null) {
                rootPanel.remove(graduationResultPage);
            }

            // 전공/MSC 선택 페이지 및 결과 페이지를 새로 생성
            selectCoursePage = new SelectCoursePage(
                    navigator,
                    curriculumCourses,
                    fullId,
                    savedIds
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

            // 학번 선택 후에는 교양/전공 유형 입력 페이지로 이동
            navigator.navigateTo(Pages.GENERAL_AND_DOUBLE_PAGE);
        };

        MainPage mainPage = new MainPage(navigator);
        ChooseStudentNumberPage choosePage = new ChooseStudentNumberPage(
                navigator,
                onFullIdSelected       
        );
        generalAndDoublePage = new GeneralAndDoublePage(navigator);

        rootPanel.add(mainPage, Pages.MAIN_PAGE);
        rootPanel.add(choosePage, Pages.CHOOSE_STUDENT_NUMBER_PAGE);
        rootPanel.add(generalAndDoublePage, Pages.GENERAL_AND_DOUBLE_PAGE);

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

    public GraduationResultPage getGraduationResultPage() {
        return graduationResultPage;
    }

    public static MainApp getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}