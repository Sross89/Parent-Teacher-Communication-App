package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Student {

    LocalDate date;
    String name;
    String ssid;
    String studentEmail;
    String parentEmail;
    Float currentGrade;
    Float progress;
    Integer assignments;
    Integer assignmentsCompleted;
    Integer assignmentsRemaining;

    public Student() {
    }

    public Student(LocalDate date, String name, String ssid, String studentEmail, String parentEmail, Float currentGrade, Float progress, Integer assignments, Integer assignmentsCompleted, Integer assignmentsRemaining) {
        this.date = date;
        this.name = name;
        this.ssid = ssid;
        this.studentEmail = studentEmail;
        this.parentEmail = parentEmail;
        this.currentGrade = currentGrade;
        this.progress = progress;
        this.assignments = assignments;
        this.assignmentsCompleted = assignmentsCompleted;
        this.assignmentsRemaining = assignmentsRemaining;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public Float getCurrentGrade() {
        return currentGrade;
    }

    public void setCurrentGrade(Float currentGrade) {
        this.currentGrade = currentGrade;
    }

    public Float getProgress() {
        return progress;
    }

    public void setProgress(Float progress) {
        this.progress = progress;
    }

    public Integer getAssignments() {
        return assignments;
    }

    public void setAssignments(Integer assignments) {
        this.assignments = assignments;
    }

    public Integer getAssignmentsCompleted() {
        return assignmentsCompleted;
    }

    public void setAssignmentsCompleted(Integer assignmentsCompleted) {
        this.assignmentsCompleted = assignmentsCompleted;
    }

    public Integer getAssignmentsRemaining() {
        return assignmentsRemaining;
    }

    public void setAssignmentsRemaining(Integer assignmentsRemaining) {
        this.assignmentsRemaining = assignmentsRemaining;
    }
}



