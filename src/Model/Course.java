package Model;

import java.util.List;

public class Course {
    List<Student> studentList;
    List<Assignment> assignmentList;

    public Course() {
    }

    public Course(List<Student> studentList, List<Assignment> assignmentList) {
        this.studentList = studentList;
        this.assignmentList = assignmentList;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public List<Assignment> getAssignmentList() {
        return assignmentList;
    }

    public void setAssignmentList(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }
}
