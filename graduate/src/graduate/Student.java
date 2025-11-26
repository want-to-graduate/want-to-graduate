package graduate;

import java.util.ArrayList;
import java.util.List;

public class Student {

	// 전체 학번(9자리)
	private String fullId;
    //학번
    private int year;
    private GraduationRule graduationRule;
    //복수전공 여부
    private boolean isDoubleMajor;
    private ArrayList<Course> takenCourses = new ArrayList<>();

    //총학점
    private int totalCredit = 0;
    private int generalCredit = 0;
    private int mscCredit = 0;
    //전공총학점
    private int majorCredit = 0;

    //개수
    private int majorRequired = 0;
    private int majorElective = 0;
    private int facultyRequired = 0;
    private int facultyElective = 0;

    // fullId 저장, year 추출, 그 외는 기존 inputStudent 메서드와 동일
    public void inputStudent(String fullId, String depName, boolean isDoubleMajor, int generalCredit, Manager<GraduationRule> depMgr) {
    	this.fullId = fullId;
    	// fullId에서 year 추출(예: 202200000 -> 22)
    	this.year = Integer.parseInt(fullId.substring(2, 4));
    	inputStudent(year, depName, isDoubleMajor, generalCredit, depMgr);
    }

    public void inputStudent(int year, String depName, boolean isDoubleMajor, int generalCredit, Manager<GraduationRule> depMgr) {
        this.year = year;
        this.graduationRule = depMgr.find(new String[]{ String.valueOf(year), depName});
        this.isDoubleMajor = isDoubleMajor;
        this.generalCredit = generalCredit;
        totalCredit += generalCredit;
    }
    
    // 학생 파일에서 읽은 과목 id 리스트 누적
    public void loadStudentCourses(List<Integer> courseIds, Manager<Course> courseMgr) {
    	for (Integer id : courseIds) {
        	selectCourse(String.valueOf(id), courseMgr);
    	}
    }
    
  //낱개 과목 추가
    public void selectCourse(int courseIndex, Manager<Course> courseMgr) {
        Course c = courseMgr.mList.get(courseIndex);
        takenCourses.add(c);
        totalCredit += c.getCredit();

        switch (c.getType()) {
            case MAJOR_REQUIRED:  majorRequired++; majorCredit += c.getCredit(); break;
            case MAJOR_ELECTIVE:  majorElective++; majorCredit += c.getCredit(); break;
            case FACULTY_REQUIRED: facultyRequired++; majorCredit += c.getCredit(); break;
            case FACULTY_ELECTIVE: facultyElective++; majorCredit += c.getCredit(); break;
            case MSC: mscCredit += c.getCredit(); break;
        }

    }

    //여러 과목 추가
    public void selectCourses(List<Integer> courseIndexes, Manager<Course> courseMgr) {

        for (Integer idx : courseIndexes) {
            Course c = courseMgr.mList.get(idx);
            takenCourses.add(c);
            totalCredit += c.getCredit();


            switch (c.getType()) {
                case MAJOR_REQUIRED:  majorRequired++; majorCredit += c.getCredit(); break;
                case MAJOR_ELECTIVE:  majorElective++; majorCredit += c.getCredit(); break;
                case FACULTY_REQUIRED: facultyRequired++; majorCredit += c.getCredit(); break;
                case FACULTY_ELECTIVE: facultyElective++; majorCredit += c.getCredit(); break;
                case NONE: majorCredit += c.getCredit(); break;
                case MSC: mscCredit += c.getCredit(); break;
            }
        }

    }
    
    // 학생 파일의 과목 id 기반, 계산 로직은 기존 selectCourse 메서드와 동일
    public void selectCourse(String courseId, Manager<Course> courseMgr) {
        Course c = courseMgr.find(courseId);
        takenCourses.add(c);
        totalCredit += c.getCredit();

        switch (c.getType()) {
            case MAJOR_REQUIRED:  majorRequired++; majorCredit += c.getCredit(); break;
            case MAJOR_ELECTIVE:  majorElective++; majorCredit += c.getCredit(); break;
            case FACULTY_REQUIRED: facultyRequired++; majorCredit += c.getCredit(); break;
            case FACULTY_ELECTIVE: facultyElective++; majorCredit += c.getCredit(); break;
            case NONE: majorCredit += c.getCredit(); break;
            case MSC: mscCredit += c.getCredit(); break;
        }
    }
    
    // 과목 index 삭제
    public void deleteCourse(int courseIndex, Manager<Course> courseMgr){
        Course c = courseMgr.mList.get(courseIndex);
        takenCourses.remove(c);
    }
    
    public List<String> checkGraduation() {
        boolean pass = true;
        List<String> messages = new ArrayList<>();

        int requiredMajor = isDoubleMajor ?
                graduationRule.getDoubleMajor() : graduationRule.getSingleMajor();

        if (totalCredit < graduationRule.getTotalCredit()) {
            messages.add(String.format("❌ 총이수학점 부족 (%d/%d학점)",
                    totalCredit, graduationRule.getTotalCredit()));
            pass = false;
        } else {
            messages.add(String.format("✔ 총이수학점 충족 (%d/%d학점)",
                    totalCredit, graduationRule.getTotalCredit()));
        }

        if (majorCredit < requiredMajor) {
            messages.add(String.format("❌ 전공학점 부족 (%d/%d학점)",
                    majorCredit, requiredMajor));
            pass = false;
        } else {
            messages.add(String.format("✔ 전공학점 충족 (%d/%d학점)",
                    majorCredit, requiredMajor));
        }

        if (generalCredit < graduationRule.getGeneralCredit()) {
            messages.add(String.format("❌ 교양학점 부족 (%d/%d학점)",
                    generalCredit, graduationRule.getGeneralCredit()));
            pass = false;
        } else {
            messages.add(String.format("✔ 교양학점 충족 (%d/%d학점)",
                    generalCredit, graduationRule.getGeneralCredit()));
        }

        if (mscCredit < graduationRule.getMscCredit()) {
            messages.add(String.format("❌ MSC학점 부족 (%d/%d학점)",
                    mscCredit, graduationRule.getMscCredit()));
            pass = false;
        } else {
            messages.add(String.format("✔ MSC학점 충족 (%d/%d학점)",
                    mscCredit, graduationRule.getMscCredit()));
        }

        if (majorRequired < graduationRule.getMajorRequired()) {
            messages.add(String.format("❌ 전공필수 과목 이수 부족 (%d/%d과목)",
                    majorRequired, graduationRule.getMajorRequired()));
            pass = false;
        } else {
            messages.add(String.format("✔ 전공필수 과목 이수 충족 (%d/%d과목)",
                    majorRequired, graduationRule.getMajorRequired()));
        }

        if (majorElective < graduationRule.getMajorElective()) {
            messages.add(String.format("❌ 전공선택 과목 이수 부족 (%d/%d과목)",
                    majorElective, graduationRule.getMajorElective()));
            pass = false;
        } else {
            messages.add(String.format("✔ 전공선택 과목 이수 충족 (%d/%d과목)",
                    majorElective, graduationRule.getMajorElective()));
        }

        if (facultyRequired < graduationRule.getFacultyRequired()) {
            messages.add(String.format("❌ 학부기초필수 과목 이수 부족 (%d/%d과목)",
                    facultyRequired, graduationRule.getFacultyRequired()));
            pass = false;
        } else {
            messages.add(String.format("✔ 학부기초필수 과목 이수 충족 (%d/%d과목)",
                    facultyRequired, graduationRule.getFacultyRequired()));
        }

        if (facultyElective < graduationRule.getFacultyElective()) {
            messages.add(String.format("❌ 학부기초선택 과목 이수 부족 (%d/%d과목)",
                    facultyElective, graduationRule.getFacultyElective()));
            pass = false;
        } else {
            messages.add(String.format("✔ 학부기초선택 과목 이수 충족 (%d/%d과목)",
                    facultyElective, graduationRule.getFacultyElective()));
        }

        if (pass)
            messages.add("졸업 가능합니다! 축하합니다!");
        else
            messages.add("졸업요건을 만족하지 못했습니다.");

        return messages;
    }

 // MSC과목을 제외한 전공과목만 가져오기
    public List<String> getTakenMajorCourseList() {
        List<String> result = new ArrayList<>();
        for (Course c : takenCourses) {
            if (c.getType() != CourseType.MSC) {
                result.add(c.toString());
            }
        }
        return result;
    }

    //학생이 들은 과목 중 MSC과목만 가져오기
    public List<String> getTakenMscCourseList() {
        List<String> result = new ArrayList<>();
        for (Course c : takenCourses) {
            if (c.getType() == CourseType.MSC) {
                result.add(c.toString());
            }
        }
        return result;
    }

    public GraduationRule getGraduationRule() {
        return graduationRule;
    }
    
    // fullId getter
    public String getFullId() {
    	return fullId;
    }
    
    // year getter
    public int getYear() {
    	return year;
    }
    
    // 저장용
    public List<Course> getTakenCourses() {
    	return takenCourses;
    }
}
