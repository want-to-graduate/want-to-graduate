package ui;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import graduate.Course;
import graduate.Manager;
import graduate.StudentCourseCount;   // 백엔드 로더
import ui.GraduationResultPage.GraduationResultPage;
import ui.MainPage.MainPage;
import ui.chooseStudentNumberPage.ChooseStudentNumberPage;
import ui.selectCoursePage.SelectCoursePage;

public class MainApp {

    private JFrame frame;          // 메인 윈도우
    private JPanel rootPanel;      // 여러 페이지를 담는 루트 패널
    private CardLayout cardLayout; // 페이지 전환 레이아웃

    
    private StudentCourseCount backend;   // department.txt, course.txt 읽기
    private Manager<Course> courseMgr;    // 전체 과목 Manager

    
    private SelectCoursePage selectCoursePage;

    // 졸업 결과에 필요한 필드들
    private int selectedYear = 0;                 // 선택한 학번(입학년도)
    private String departmentName = "컴퓨터공학부"; // 학과명(지금은 고정)
    private boolean isDoubleMajor = false;        // 복수전공 여부(지금은 기본 false)
    private int generalCredit = 0;                // 교양 이수 학점(추후 UI에서 입력)
    private int mscCredit = 0;                    // MSC 이수 학점(추후 UI에서 입력)
    private List<Integer> selectedCourseIndexes = new ArrayList<>(); // 선택된 과목 인덱스들

    public MainApp() {
        // 1) 백엔드 초기화: course.txt, department.txt 읽기
        backend = new StudentCourseCount();
        backend.run();                     // 내부에서 파일 읽기
        courseMgr = backend.getCourseMgr();// 읽어온 과목 Manager 가져오기

        // 2) 프레임 기본 설정
        frame = new JFrame("졸업 요건 확인 프로그램");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null); // 화면 가운데 정렬

        // 3) CardLayout을 사용하는 rootPanel 생성
        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);

        // 4) 페이지 전환용 navigator 구현
        PageNavigator navigator = pageName -> {
            cardLayout.show(rootPanel, pageName); // 해당 이름의 페이지로 전환
            rootPanel.revalidate();
            rootPanel.repaint();
        };

        
        IntConsumer onYearSelected = (int entryYear) -> {
            System.out.println("선택된 학번(입학연도): " + entryYear);
            this.selectedYear = entryYear;   

            List<Course> courses = new ArrayList<>(courseMgr.mList);

            
            if (selectCoursePage != null) {
                rootPanel.remove(selectCoursePage);
            }

            
            selectCoursePage = new SelectCoursePage(
                navigator,
                courses,
                entryYear
            
            );

            
            rootPanel.add(selectCoursePage, Pages.SELECT_COURSE_PAGE);

            
            navigator.navigateTo(Pages.SELECT_COURSE_PAGE);
        };

        //  각 페이지 생성
        MainPage mainPage = new MainPage(navigator);
        
        ChooseStudentNumberPage choosePage = new ChooseStudentNumberPage(navigator, onYearSelected);

        GraduationResultPage resultPage = new GraduationResultPage(
            navigator,
            selectedYear,        // 지금은 0
            departmentName,      // "컴퓨터공학부"
            isDoubleMajor,       // 기본 false
            generalCredit,       // 0
            mscCredit,           // 0
            selectedCourseIndexes 
        );

        
        rootPanel.add(mainPage, Pages.MAIN_PAGE);
        rootPanel.add(choosePage, Pages.CHOOSE_STUDENT_NUMBER_PAGE);
        rootPanel.add(resultPage, Pages.GRADUATION_RESULT_PAGE);
        


        cardLayout.show(rootPanel, Pages.MAIN_PAGE);


        frame.setContentPane(rootPanel);
        frame.setVisible(true);
    }


    public void navigateTo(String pageKey) {
        cardLayout.show(rootPanel, pageKey);
    }


    public int getSelectedYear() {
        return selectedYear;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}