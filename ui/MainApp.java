package ui;

import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ui.MainPage.MainPage;
import ui.chooseStudentNumberPage.ChooseStudentNumberPage;
import ui.selectCoursePage.SelectCoursePage;


public class MainApp {

    
    private JFrame frame; // 메인 윈도우
    
    private JPanel rootPanel; // 여러 페이지를 담는 루트 패널
    
    private CardLayout cardLayout; // 페이지 전환을 담당하는 레이아웃

    public MainApp() {
        // 프레임 기본 설정
        frame = new JFrame("졸업 요건 확인 프로그램");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null); // 화면 가운데 정렬

        // CardLayout을 사용하는 rootPanel 생성
        cardLayout = new CardLayout();
        rootPanel = new JPanel(cardLayout);

        // 페이지 전환
        PageNavigator navigator = pageName -> {
            cardLayout.show(rootPanel, pageName); // 해당 이름의 페이지로 전환
            rootPanel.revalidate();
            rootPanel.repaint();
        };

        // 각 페이지 생성
        MainPage mainPage = new MainPage(navigator);
        ChooseStudentNumberPage choosePage = new ChooseStudentNumberPage(navigator);
        SelectCoursePage selectCoursePage = new SelectCoursePage(navigator);

        // 각 페이지를 카드 레이아웃에 등록
        rootPanel.add(mainPage, Pages.MAIN_PAGE);
        rootPanel.add(choosePage, Pages.CHOOSE_STUDENT_NUMBER_PAGE);
        rootPanel.add(selectCoursePage, Pages.SELECT_COURSE_PAGE);

        // 처음은 메인 페이지
        cardLayout.show(rootPanel, Pages.MAIN_PAGE);

        // 프레임에 rootPanel을 붙이고 화면에 표시
        frame.setContentPane(rootPanel);
        frame.setVisible(true);
    }

    public void navigateTo(String pageKey) {
        cardLayout.show(rootPanel, pageKey);
    }

    public static void main(String[] args) {
        // Swing UI는 이벤트 스레드에서 실행하는 것이 원칙
        SwingUtilities.invokeLater(MainApp::new);
    }
}