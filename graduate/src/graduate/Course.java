package graduate;

public class Course {
	// 0 DD848 AI컴퓨터공학부전공및진로탐색 1 FACULTY_REQUIRED 1 1
	private int id;	// 중복 방지 id
	private String code;	// 과목 code
	private String name;	// 과목명
	private int credit;	// 학점
	private CourseType courseType;	// 과목구분
	private int grade;	// 학년
	private int semester;	// 학기
	
	// 검색용 기능 - 검색 키워드로 과목 찾기
	public boolean matches(String kwd) {
		if (code.equals(kwd)) return true;
		if (name.contains(kwd)) return true;
		return false;
	}
	
	public int getId() {
		return id;
	}
	
	public int getCredit() {
		return credit;
	}
	
	public CourseType getCourseType() {
		return courseType;
	}
	
	public int getGrade() {
		return grade;
	}
	
	public int getSemester() {
		return semester;
	}
}
