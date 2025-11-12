package ui;

@FunctionalInterface // 메서드는 하나만 있어야 한다를 정의
public interface PageNavigator { // 페이지 간 이동을 위한 인터페이스
    void navigateTo(String pageName);

}
