package graduate;

import java.util.Scanner;

public class StudentCourseCount {

    Scanner scan = new Scanner(System.in);
    Manager<Department> depMgr = new Manager<>();
    static Manager<Course> courseMgr = new Manager<>();

    public void run(){
        courseMgr.readAll("course.txt", Course::new);

        depMgr.readAll("department.txt", Department::new);

        Student student = new Student();
        student.inputStudent(scan, depMgr);
        student.selectCourses(scan, courseMgr);

        student.checkGraduation();
    }


    public static void main(String[] args) {
        StudentCourseCount main = new StudentCourseCount();
        main.run();

    }
}
