package graduate;

import java.util.ArrayList;
import java.util.Scanner;

public class Department implements Manageable{
    //학과 학번 최저이수학점(졸업, 교양, MSC, 단일전공, 복수전공), 전공필수, 전공선택, 학부기초필수, 학부기초선택, 강좌들 ,,, 0
    //컴공 21 136 40 21 67 42 1 8 0 0

    // 학과명 (예: "컴퓨터공학과")
    private String name;

    // 학번 또는 입학년도 (예: 21)
    private int year;

    // 최소 이수 학점들
    private int totalCredit;   // 졸업 최저 이수학점
    private int generalCredit;  // 교양 최저 이수학점
    private int mscCredit;               // MSC 최저 이수학점
    private int singleMajor;       // 단일전공 최저 이수학점
    private int doubleMajor;       // 복수전공 최저 이수학점

    // 교과구분별 과목 개수
    private int majorRequired;      // 전공필수 과목 수
    private int majorElective;      // 전공선택 과목 수
    private int facultyRequired;      // 학부기초필수 과목 수
    private int facultyElective;      // 학부기초선택 과목 수

    //강좌들
    ArrayList<Course> courses = new ArrayList<>();

    @Override
    public void read(Scanner scan) {
        name = scan.next();
        year = scan.nextInt();
        totalCredit = scan.nextInt();
        generalCredit = scan.nextInt();
        mscCredit = scan.nextInt();
        singleMajor = scan.nextInt();
        doubleMajor = scan.nextInt();
        majorRequired = scan.nextInt();
        majorElective = scan.nextInt();
        facultyRequired = scan.nextInt();
        facultyElective = scan.nextInt();

        String code;
        Course course = null;
        while(true){
            code = scan.next();
            if (code.charAt(0) == '0')
                break;
            course = StudentCourseCount.courseMgr.find(code);
            if (course == null) continue;
            courses.add(course);
        }

    }

    @Override
    public void print() {
        System.out.printf("[%s %d학번] 졸업:%d 교양:%d MSC:%d 단일:%d 복수:%d (전필:%d 전선:%d 학필:%d 학선:%d)\n",
                name, year, totalCredit, generalCredit, mscCredit,
                singleMajor, doubleMajor,
                majorRequired, majorElective, facultyRequired, facultyElective);
    }

    @Override
    public boolean matches(String kwd) {
        return name.equals(kwd);
    }



    public String getName() {
        return name;
    }

    public int getTotalCredit() {
        return totalCredit;
    }

    public int getGeneralCredit() {
        return generalCredit;
    }

    public int getMscCredit() {
        return mscCredit;
    }

    public int getSingleMajor() {
        return singleMajor;
    }

    public int getDoubleMajor() {
        return doubleMajor;
    }

    public int getMajorRequired() {
        return majorRequired;
    }

    public int getMajorElective() {
        return majorElective;
    }

    public int getFacultyRequired() {
        return facultyRequired;
    }

    public int getFacultyElective() {
        return facultyElective;
    }



}
