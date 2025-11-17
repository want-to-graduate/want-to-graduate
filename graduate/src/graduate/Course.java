package graduate;

import java.util.Scanner;

public class Course implements Manageable{

    // id  이수코드  과목명  3(학점)  과목구분  학년  학기
	// 0 DD848 AI컴퓨터공학부전공및진로탐색 1 FACULTY_REQUIRED 1 1
    private int id;
    private String code;
    private String name;
    private int credit;
    private CourseType type;
    private int grade;
    private int semester;

    @Override
    public void read(Scanner scan) {
        id = scan.nextInt();
        code = scan.next();
        name = scan.next();
        credit = scan.nextInt();
        type = CourseType.valueOf(scan.next().toUpperCase());
        grade = scan.nextInt();
        semester = scan.nextInt();

    }

    //학수코드나 과목명으로 검색
    @Override
    public boolean matches(String kwd) {
        if ((id+ "").equals(kwd)) return true;
        if (code.equals(kwd)) return true;
        if (name.contains(kwd)) return true;
        return false;
    }

    @Override
    public boolean matches(String[] kwds) {
        for (String kwd : kwds) {
            if (!matches(kwd)) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s %d학점 %s (%d-%d)",
                code, name, credit, type.getDescription(), grade, semester
        );
    }

    public int getId() {
        return id;
    }

    public int getCredit() {
        return credit;
    }

    public CourseType getType() {
        return type;
    }

}
