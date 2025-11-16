package graduate;

import java.util.ArrayList;
import java.util.Scanner;

public class Student {

    //학번
    private int year;
    private Department department;
    private boolean isDoubleMajor;
    private ArrayList<Course> takenCourses = new ArrayList<>();

    private int totalCredit = 0;
    private int generalCredit = 0;
    private int mscCredit = 0;
    private int majorCredit = 0;
    private int majorRequired = 0;
    private int majorElective = 0;
    private int facultyRequired = 0;
    private int facultyElective = 0;

    public void inputStudent(Scanner scan, Manager<Department> depMgr) {

        System.out.print("학번 입력 [21, 22, 23, 24, 25]: ");
        year = scan.nextInt();

        System.out.print("학과명 입력 [컴공, 인지, 안전]: ");
        String depName = scan.next();
        department = depMgr.find(depName);
        if (department == null) {
            System.out.println("해당 학과가 존재하지 않습니다!");
            return;
        }

        System.out.print("복수전공 여부 (Y/N): ");
        String ans = scan.next().toUpperCase();
        isDoubleMajor = ans.equals("Y");

        System.out.println("교양 이수 학점 입력: ");
        generalCredit = scan.nextInt();

        System.out.println("Msc 이수 학점 입력: ");
        mscCredit = scan.nextInt();
    }

    public void selectCourses(Scanner scan, Manager<Course> courseMgr) {
        System.out.println("수강한 과목 코드 입력 (종료: 0)");
        while (true) {
            String code = scan.next();
            if (code.equals("0")) break;
            Course c = courseMgr.find(code);
            if (c != null) {
                takenCourses.add(c);
                totalCredit += c.getCredit();
                majorCredit += c.getCredit();

                switch (c.getCourseType()) {

                    case MAJOR_REQUIRED:
                        majorRequired ++;
                        break;
                    case MAJOR_ELECTIVE:
                        majorElective ++;
                        break;
                    case FACULTY_REQUIRED:
                        facultyRequired ++;
                        break;
                    case FACULTY_ELECTIVE:
                        facultyElective ++;
                        break;
                }
            }
        }
        totalCredit+=generalCredit;
        totalCredit+=mscCredit;
    }

    public void checkGraduation() {
        if (department == null) {
            System.out.println("학과 정보가 없습니다.");
            return;
        }

        boolean pass = true;
        int requiredMajor = isDoubleMajor ?
                department.getDoubleMajor() : department.getSingleMajor();

        if (totalCredit < department.getTotalCredit()) {
            System.out.printf("총이수학점 부족 (%d/%d학점)\n",
                    totalCredit, department.getTotalCredit());
            pass = false;
        }

        if (majorCredit < requiredMajor) {
            System.out.printf("전공학점 부족 (%d/%d학점)\n",
                    majorCredit, requiredMajor);
            pass = false;
        }

        if (generalCredit < department.getGeneralCredit()) {
            System.out.printf("교양학점 부족 (%d/%d학점)\n",
                    generalCredit, department.getGeneralCredit());
            pass = false;
        }
        if (mscCredit < department.getMscCredit()) {
            System.out.printf("MSC학점 부족 (%d/%d학점)\n",
                    mscCredit, department.getMscCredit());
            pass = false;
        }

        if (majorRequired< department.getMajorRequired()) {
            System.out.printf("전공필수 과목 이수 부족 (%d/%d과목)\n",
                    majorRequired, department.getMajorRequired());
            pass = false;
        }

        if (majorElective < department.getMajorElective()) {
            System.out.printf("전공선택 과목 이수 부족 (%d/%d과목)\n",
                    majorElective, department.getMajorElective());
            pass = false;
        }

        if (facultyRequired < department.getFacultyRequired()) {
            System.out.printf("학부기초필수 과목 이수 부족 (%d/%d과목)\n",
                    facultyRequired, department.getFacultyRequired());
            pass = false;
        }


        if (facultyElective < department.getFacultyElective()) {
            System.out.printf("학부기초선택 과목 이수 부족 (%d/%d과목)\n",
                    facultyElective, department.getFacultyElective());
            pass = false;
        }

        if (pass)
            System.out.println("졸업 가능합니다! 축하합니다!");
        else
            System.out.println("졸업요건을 만족하지 못했습니다.");
    }
}
