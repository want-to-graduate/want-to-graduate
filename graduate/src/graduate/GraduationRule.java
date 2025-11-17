package graduate;

import java.util.*;

public class GraduationRule implements Manageable{
    //학번 학과 최저이수학점(졸업, 교양, MSC, 단일전공, 복수전공), 전공필수, 전공선택, 학부기초필수, 학부기초선택, 강좌들 ,,, -
    //22 컴공 136 40 21 67 42 1 8 0 0

    // 학번 또는 입학년도 (예: 22)
    private int curriculumYear;

    // 학과명 (예: "컴공")
    private String major;

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
    public ArrayList<Course> courses = new ArrayList<>();


    @Override
    public void read(Scanner scan) {
        curriculumYear = scan.nextInt();
        major = scan.next();
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
            if (code.charAt(0) == '-')
                break;
            course = StudentCourseCount.courseMgr.find(code);
            if (course == null) continue;
            courses.add(course);
        }
    }


    @Override
    public boolean matches(String kwd) {
        if (major.equals(kwd)) return true;
        if ((curriculumYear + "").equals(kwd)) return true;
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
                "%s(%d) 졸업요건 - 졸업최저이수학점:%d, 교양/MSC최저이수학점:%d/%d, 전공최저이수학점(단일전공:%d, 복수전공:%d) 전공필수:%d과목이수, 전공선택:%d과목이수, 학부기초필수:%d과목이수, 학부기초선택:%d과목이수",
                major, curriculumYear,
                totalCredit,
                generalCredit,
                mscCredit,
                singleMajor,
                doubleMajor,
                majorRequired,
                majorElective,
                facultyRequired,
                facultyElective
        );
    }



    public String getName() {
        return major;
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

    public ArrayList<Course> getCourses() {
        return courses;
    }
}
