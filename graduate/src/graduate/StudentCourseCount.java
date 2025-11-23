package graduate;

import java.util.List;

public class StudentCourseCount {

    Manager<GraduationRule> depMgr = new Manager<>();
    static Manager<Course> courseMgr = new Manager<>();

    public Manager<GraduationRule> getDepMgr() { return depMgr; }
    public Manager<Course> getCourseMgr() { return courseMgr; }

    public void run(){
        courseMgr.readAll("course.txt", new Factory<Course>() {
            @Override
            public Course create() {
                return new Course();
            }
        });

        depMgr.readAll("department.txt", new Factory<GraduationRule>() {
            @Override
            public GraduationRule create() {
                return new GraduationRule();
            }
        });
    }
    
    //어떤 학생 수강 내역 파일 읽을 건지 결정
    public List<Integer> loadStudentFile(String fullId) {
    	String filename = fullId + ".txt";
    	return courseMgr.readFile(filename);
    }
 
    //여기는 프론트에서 예시
    public static void main(String[] args) {

        StudentCourseCount main = new StudentCourseCount();
        main.run();
        //학생 객체 만들어서 학생의 정보 넣어주기
        Student student = new Student();
        //프론트에서 fullId 받은 경우
        String fullId = "202211548";
        student.inputStudent(fullId, "컴공", false, 50, 30, main.getDepMgr());
        //202211548.txt에서 과목 id 로드
        List<Integer> ids = main.loadStudentFile(fullId);
        
        if (!ids.isEmpty()) {
        	student.selectCourses(ids, main.getCourseMgr());
        } else {
        //fullId.txt 없는 경우
        student.inputStudent(22, "컴공", false, 50, 30, main.getDepMgr());
        }
        //학생이 다니는 학과의 졸업요건(필요시 사용)
        System.out.println(student.getGraduationRule().toString());
        System.out.println();

        //학과 과목 리스트 버튼 띄울 때(버튼에는 course.getId() 로 id 넣어주기)
        System.out.println("학과의 과목 리스트");
        List<Course> courseList = student.getGraduationRule().getCourses();
        for (Course course : courseList) {
            System.out.println(course.toString());
        }

        //학생이 들은 과목 넣어주기
        //student.selectCourses(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), main.getCourseMgr());
        System.out.println();

        //졸업요건 체크하기
        List<String> message = student.checkGraduation();
        for (String s : message) {
            System.out.println(s);
        }

        System.out.println();

        //학생이 들은 과목 가져오기(필요시 사용)
        System.out.println("학생이 들은 전공 과목");
        List<String> takenCourseList = student.getTakenCourseList();
        for (String s : takenCourseList) {
            System.out.println(s);
        }

        System.out.println();

        //과목 검색하기(과목명 or 학수코드)
        List<Course> result = main.getCourseMgr().search("객");
        for (Course course : result) {
            System.out.println(course.toString());
        }
    }
}