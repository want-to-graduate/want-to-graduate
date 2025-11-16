package graduate;

/**
 * 졸업요건 체크에 필요한 "학생"의 현재 이수 상태 요약.
 * (비교하기 위해 Student에서 값을 뽑아서 채우는 용도)
 */
public class GraduateState {
    public int totalCredit;        // 전체 이수학점
    public int generalCredit;      // 교양 이수학점
    public int mscCredit;          // MSC 이수학점
    public int majorCredit;        // 전공 이수학점 전체

    public int majorRequiredCount; // 전공필수 이수 과목 수
    public int majorElectiveCount; // 전공선택 이수 과목 수
    public int facultyRequiredCount; // 학부기초필수 과목 수
    public int facultyElectiveCount; // 학부기초선택 과목 수

    public boolean doubleMajor;    // 복수전공 여부 (true면 복수전공 기준 사용)

    public int curriculumYear;     // 2022 같은 기준년도
    public String major;           // "컴공" 같은 전공 코드/이름
}
