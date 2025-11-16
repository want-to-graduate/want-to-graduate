package graduate;

import java.util.Scanner;

public class Course implements Manageable{

    //이수코드 과목명  3(학점)
    //DD015 객체지향프로그래밍 3
    private String code;
    private String name;
    private int credit;
    private CourseType courseType;

    @Override
    public void read(Scanner scan) {
        code = scan.next();
        name = scan.next();
        credit = scan.nextInt();
        courseType = CourseType.valueOf(scan.next().toUpperCase());
    }

    @Override
    public void print() {
        System.out.printf("[%s] %s (%d학점, %s)\n",
                code, name, credit, courseType.getDescription());
    }

    @Override
    public boolean matches(String kwd) {
        if (code.equals(kwd)) return true;
        if (name.contains(kwd)) return true;
        return false;
    }
    

    public int getCredit() {
        return credit;
    }

    public CourseType getCourseType() {
        return courseType;
    }

}
