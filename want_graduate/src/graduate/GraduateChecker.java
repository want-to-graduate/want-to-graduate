package graduate;

public class GraduateChecker {
	/** 22학번용 졸업요건(학점 + 전필/전선) 검사 */
    public GraduateResult check(GraduateState s, GraduateRule rule) {
        GraduateResult r = new GraduateResult();

        // (1) 이 규칙이 학생에게 맞는지 확인 (학번/전공)
        if (rule.getCurriculumYear() != s.curriculumYear ||
            !rule.getMajor().equalsIgnoreCase(s.major)) {

            // 필요하면 여기서 예외 던지거나, UI에서 "해당 규칙 없음" 메시지로 처리
            r.addMissingCredit("ruleMismatch", 1);
            r.finalizePass();
            return r;
        }

        // (2) 학점 조건
        // 2-1) 총 이수학점 ≥ 136
        r.addMissingCredit("totalCredit",
                Math.max(0, rule.getTotalCredit() - s.totalCredit));

        // 2-2) 교양 이수학점 ≥ 34
        r.addMissingCredit("generalCredit",
                Math.max(0, rule.getGeneralCredit() - s.generalCredit));

        // 2-3) MSC 이수학점 ≥ 15
        r.addMissingCredit("mscCredit",
                Math.max(0, rule.getMscCredit() - s.mscCredit));

        // 2-4) 전공 이수학점 ≥ (단일 67 / 복수 42)
        int needMajorCr = s.doubleMajor ? rule.getDoubleMajor()
                                        : rule.getSingleMajor();
        r.addMissingCredit("majorCredit",
                Math.max(0, needMajorCr - s.majorCredit));

        // (3) 과목 개수 조건
        // 3-1) 전공필수 ≥ 3과목
        r.addMissingCount("majorRequired",
                Math.max(0, rule.getMajorRequired() - s.majorRequiredCount));

        // 3-2) 전공선택 ≥ 5과목
        r.addMissingCount("majorElective",
                Math.max(0, rule.getMajorElective() - s.majorElectiveCount));

        // 3-3) 학부기초필수 ≥ facultyRequired
        r.addMissingCount("facultyRequired",
                Math.max(0, rule.getFacultyRequired() - s.facultyRequiredCount));

        // 3-4) 학부기초선택 ≥ facultyElective
        r.addMissingCount("facultyElective",
                Math.max(0, rule.getFacultyElective() - s.facultyElectiveCount));

        // (4) 최종 PASS/FAIL
        r.finalizePass();
        return r;
    }
}
