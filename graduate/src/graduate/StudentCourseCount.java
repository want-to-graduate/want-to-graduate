package graduate;

import java.util.ArrayList;
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
        
      //msc과목 추가로 넣어줌.
        courseMgr.readAll("msc.txt", new Factory<Course>() {
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
    
    // 어떤 학생 수강 내역 파일 읽을 건지 결정
    public List<Integer> loadStudentFile(String fullId) {
    	String filename = "studentData/" + fullId + ".txt";
    	return courseMgr.readFile(filename);
    }
    
    public void saveStudentFile(Student s) {
    	String filename = "studentData/" + s.getFullId() + ".txt";
    	
    	List<String> out = new ArrayList<>();
    	for (Course c : s.getTakenCourses()) {
    		out.add(String.valueOf(c.getId()));
    	}
    	out.add("-");
    	
    	try {
    		java.nio.file.Files.write(
    			java.nio.file.Paths.get(filename),
    			out,
    			java.nio.file.StandardOpenOption.CREATE,
    			java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
    		);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

  //여기는 프론트에서 예시
    public static void main(String[] args) {

        StudentCourseCount main = new StudentCourseCount();
        main.run(); // course.txt, msc.txt, department.txt 로드
        
        //학생 객체 만들어서 학생의 정보 넣어주기
        Student student = new Student();

        // 학번(9자리)
        String fullId = "202211548";
        
        // fullId.txt에서 과목 id 로드
        List<Integer> ids = main.loadStudentFile(fullId);
        
        if (!ids.isEmpty()) {
        	// fullId.txt 파일 있는 경우
        	student.inputStudent(fullId, "컴공", false, 50, main.getDepMgr());
        	student.loadStudentCourses(ids, main.getCourseMgr());
        } else {
        // fullId.txt 없는 경우
        System.out.println("파일 없음");

            //이제 MSC는 교양이 아닌 전공처럼 직접 넣어주기로함.
        	// 신규 학번 등록
        	student.inputStudent(fullId, "컴공", false, 50, main.getDepMgr());
        	// 새 학생 파일 생성
            main.saveStudentFile(student);
          //학생이 들은 전공 과목 넣어주기
            student.selectCourses(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), main.getCourseMgr());
            System.out.println();
            student.selectCourses(List.of(101, 102, 103), main.getCourseMgr());
            System.out.println();
            System.out.println("학생 파일 생성 완료");
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
        System.out.println();

        //MSC 과목 불러오기
        System.out.println("MSC과목 리스트");
        List<Course> mscList = courseMgr.filterBy(c -> c.getType() == CourseType.MSC);
        for (Course course : mscList) {
            System.out.println(course.toString());
        }

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


        //학생이 들은 MSC과목 가져오기
        System.out.println("학생이 들은 MSC 과목");
        List<String> takenMSCCourseList = student.getTakenMscCourseList();
        for (String s : takenMSCCourseList) {
            System.out.println(s);
        }
        System.out.println();

        //과목 검색하기(과목명 or 학수코드)
        List<Course> result = main.getCourseMgr().search("객");
        for (Course course : result) {
            System.out.println(course.toString());
        }
        main.saveStudentFile(student);
    }
}