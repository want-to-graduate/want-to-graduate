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

        // 1) 백엔드 초기화
        scc = new StudentCourseCount();
        scc.run();                        // course.txt, department.txt 읽기
        courseMgr = scc.getCourseMgr();   // 전체 과목 Manager 준비

        // 2) 메인 프레임 설정
        frame = new JFrame("졸업 요건 확인 프로그램");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLocationRelativeTo(null); // 화면 가운데 정렬

        // 3) 카드 레이아웃/루트 패널
        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);

        // 4) PageNavigator 구현 (문자열 키로 페이지 전환)
        PageNavigator navigator = pageKey -> {
            cardLayout.show(rootPanel, pageKey);
            rootPanel.revalidate();
            rootPanel.repaint();
        };

        // 5) 학번 입력 후 처리 로직
        Consumer<String> onFullIdSelected = (inputFullId) -> {
            System.out.println("입력한 학번: " + inputFullId);
            this.fullId = inputFullId;
            this.currentStudent = new Student();

            // 5-1) 기존 학생 파일에서 과목 id 리스트 불러오기
            List<Integer> savedIds = scc.loadStudentFile(fullId);
            System.out.println("불러온 과목 개수: " + savedIds.size());

            // 5-2) Student 기본 정보 주입 (학번/학과/교양 등)
            currentStudent.inputStudent(
                    fullId,
                    departmentName,
                    isDoubleMajor,
                    generalCredit,
                    scc.getDepMgr()
            );

            // 5-3) 기존에 저장된 과목이 있으면 Student 에 로드
            if (!savedIds.isEmpty()) {
                currentStudent.loadStudentCourses(savedIds, courseMgr);
            } else {
                // 파일이 없다면 studentData/학번.txt 새로 만들어주는 정도로만 사용
                System.out.println("신규 학번, 파일 생성");
                scc.saveStudentFile(currentStudent);
            }

            // 5-4) 이 학번의 전공 교육과정 / MSC 과목 목록 준비
            List<Course> curriculumCourses =
                    currentStudent.getGraduationRule().getCourses();

            List<Course> mscCourses =
                    courseMgr.filterBy(c -> c.getType() == CourseType.MSC);

            // 5-5) 이전에 만들어진 페이지들이 있으면 제거
            if (selectCoursePage != null) {
                rootPanel.remove(selectCoursePage);
            }
            if (selectMscCoursePage != null) {
                rootPanel.remove(selectMscCoursePage);
            }
            if (graduationResultPage != null) {
                rootPanel.remove(graduationResultPage);
            }

            // 5-6) 과목 선택 페이지들/결과 페이지 생성
            //  -> 여기서는 txt 갱신을 각 페이지에서 할 것이므로
            //     fullId, 과목 리스트, 이미 들은 과목 id 리스트 정도만 넘겨줌
            selectCoursePage = new SelectCoursePage(
                    navigator,
                    curriculumCourses,   // 전공 과목 목록
                    fullId,
                    savedIds            // 이미 들은 과목 id 리스트 (하이라이트용 등)  
                    // 결과 이동 Consumer 는 제거 (txt 기반으로 바꾸기 때문)
            );

            selectMscCoursePage = new SelectMscCoursePage(
                    navigator,
                    fullId,
                    mscCourses,
                    savedIds            // 마찬가지로 이미 들은 과목 id 리스트
            );

            graduationResultPage = new GraduationResultPage(
                    navigator,
                    fullId              // 졸업 여부 계산은 이 안에서 txt 읽어서 수행
            );

            // 5-7) 카드에 페이지 등록
            rootPanel.add(selectCoursePage, Pages.SELECT_COURSE_PAGE);
            rootPanel.add(selectMscCoursePage, Pages.SELECT_MSC_PAGE);
            rootPanel.add(graduationResultPage, Pages.GRADUATION_RESULT_PAGE);

            // 5-8) 전공 과목 선택 페이지로 이동
            navigator.navigateTo(Pages.SELECT_COURSE_PAGE);
        };

        // 6) 처음 진입하는 메인/학번입력 페이지
        MainPage mainPage = new MainPage(navigator);
        ChooseStudentNumberPage choosePage = new ChooseStudentNumberPage(
                navigator,
                onFullIdSelected       // 학번 입력 완료 시 위 로직 실행
        );

        rootPanel.add(mainPage, Pages.MAIN_PAGE);
        rootPanel.add(choosePage, Pages.CHOOSE_STUDENT_NUMBER_PAGE);

        // 7) 시작 페이지: 메인
        cardLayout.show(rootPanel, Pages.MAIN_PAGE);

        frame.setContentPane(rootPanel);
        frame.setVisible(true);
    }

    // 혹시 다른 데서 직접 쓰고 싶을 때를 위한 navigateTo
    public void navigateTo(String pageKey) {
        cardLayout.show(rootPanel, pageKey);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    // --- Getter (필요하면 유지, 안 쓰면 정리해도 됨) ---

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