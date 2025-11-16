package graduate;


import java.util.List;

/**
 * 1개 "졸업요건 JSON"을 그대로 담는 객체.
 * 2022.Json 내용이 그대로 들어온다고 생각하면 됨.
 */
public class GraduateRule {
    // 교육과정 기준년도
    private int curriculumYear;

    // 전공 코드/이름
    private String major;

    // 졸업 최저 이수학점
    private int totalCredit;

    // 교양 최저 이수학점
    private int generalCredit;

    // MSC 최저 이수학점
    private int mscCredit;

    // 단일전공 최저 전공학점
    private int singleMajor;

    // 복수전공 최저 전공학점
    private int doubleMajor;

    // 교과구분별 과목 개수
    // 전공 필수 학점
    private int majorRequired;
    // 전공 선택 학점
    private int majorElective;
    // 학부 필수 학점
    private int facultyRequired;
    // 학부 선택 학점
    private int facultyElective;

    // 이 요건에 포함되는 과목 ID 목록 (예: 91, 1, 2, 3, ...)
    private List<Integer> courses;

    // JSON 라이브러리가 쓸 기본 생성자
    public GraduateRule() {}

    // ======= Getter들 =======
    public int getCurriculumYear() { return curriculumYear; }
    public String getMajor() { return major; }

    public int getTotalCredit() { return totalCredit; }
    public int getGeneralCredit() { return generalCredit; }
    public int getMscCredit() { return mscCredit; }

    public int getSingleMajor() { return singleMajor; }
    public int getDoubleMajor() { return doubleMajor; }

    public int getMajorRequired() { return majorRequired; }
    public int getMajorElective() { return majorElective; }
    public int getFacultyRequired() { return facultyRequired; }
    public int getFacultyElective() { return facultyElective; }

    public List<Integer> getCourses() { return courses; }

    // ======= 편의 메서드들 =======

    /** 이 규칙이 해당 학번(교육과정년도)에 적용되는지 체크 */
    public boolean appliesTo(int year, String majorCode) {
        if (this.curriculumYear != year) return false;
        if (this.major == null) return true;
        // "컴공" 같이 간단한 문자열이면 equalsIgnoreCase 정도만 해도 충분
        return this.major.equalsIgnoreCase(majorCode);
    }

    /** 복수전공 여부에 따라 필요한 전공 최저 이수학점 반환 */
    public int getRequiredMajorCredit(boolean isDoubleMajor) {
        return isDoubleMajor ? doubleMajor : singleMajor;
    }
}
