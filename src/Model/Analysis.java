package Model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Analysis {

    // This class will be used to store and calculate data that will be used for student analysis
    // The following are indicators are what needs to be calculated:
    // - Student's current work pace (assignments per week)
    // - Days left till student completes the course at current pace
    // - Days the student has left in the course
    // - The corrected amount of assignments per week the student needs to do to meet the deadline
    // - The last week the student submitted an assignment
    // - The student's current status as "on-track," "behind," or "ahead."

    private String name;
    private int numberOfRecords;
    private LocalDate firstReport;
    private LocalDate latestReport;
    private LinkedHashMap<LocalDate, Float> progressMap;
    private float grade;
    private float progress;
    private float rateOfCompletion;
    private int actualDaysLeft;
    private LocalDate estimatedCompletionDate;
    private float correctedRateOfCompletion;
    private int daysLeft;
    private LocalDate endDateOfCourse;
    private int assignmentsCompleted;
    private int assignmentsRemaining;
    private int assignments;

    public Analysis() {
    }

    public Analysis(String name, int numberOfRecords, LocalDate firstReport, LocalDate latestReport, LinkedHashMap<LocalDate, Float> progressMap, float grade, float progress, float rateOfCompletion, int actualDaysLeft, LocalDate estimatedCompletionDate, float correctedRateOfCompletion, int daysLeft, LocalDate endDateOfCourse, int assignmentsCompleted, int assignmentsRemaining, int assignments) {
        this.name = name;
        this.numberOfRecords = numberOfRecords;
        this.firstReport = firstReport;
        this.latestReport = latestReport;
        this.progressMap = progressMap;
        this.grade = grade;
        this.progress = progress;
        this.rateOfCompletion = rateOfCompletion;
        this.actualDaysLeft = actualDaysLeft;
        this.estimatedCompletionDate = estimatedCompletionDate;
        this.correctedRateOfCompletion = correctedRateOfCompletion;
        this.daysLeft = daysLeft;
        this.endDateOfCourse = endDateOfCourse;
        this.assignmentsCompleted = assignmentsCompleted;
        this.assignmentsRemaining = assignmentsRemaining;
        this.assignments = assignments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public LocalDate getFirstReport() {
        return firstReport;
    }

    public void setFirstReport(LocalDate firstReport) {
        this.firstReport = firstReport;
    }

    public LocalDate getLatestReport() {
        return latestReport;
    }

    public void setLatestReport(LocalDate latestReport) {
        this.latestReport = latestReport;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public float getRateOfCompletion() {
        return rateOfCompletion;
    }

    public void setRateOfCompletion(float rateOfCompletion) {
        this.rateOfCompletion = rateOfCompletion;
    }

    public int getActualDaysLeft() {
        return actualDaysLeft;
    }

    public void setActualDaysLeft(int actualDaysLeft) {
        this.actualDaysLeft = actualDaysLeft;
    }

    public LocalDate getEstimatedCompletionDate() {
        return estimatedCompletionDate;
    }

    public void setEstimatedCompletionDate(LocalDate estimatedCompletionDate) {
        this.estimatedCompletionDate = estimatedCompletionDate;
    }

    public float getCorrectedRateOfCompletion() {
        return correctedRateOfCompletion;
    }

    public void setCorrectedRateOfCompletion(float correctedRateOfCompletion) {
        this.correctedRateOfCompletion = correctedRateOfCompletion;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }

    public LocalDate getEndDateOfCourse() {
        return endDateOfCourse;
    }

    public void setEndDateOfCourse(LocalDate endDateOfCourse) {
        this.endDateOfCourse = endDateOfCourse;
    }

    public int getAssignmentsCompleted() {
        return assignmentsCompleted;
    }

    public void setAssignmentsCompleted(int assignmentsCompleted) {
        this.assignmentsCompleted = assignmentsCompleted;
    }

    public int getAssignmentsRemaining() {
        return assignmentsRemaining;
    }

    public void setAssignmentsRemaining(int assignmentsRemaining) {
        this.assignmentsRemaining = assignmentsRemaining;
    }

    public int getAssignments() {
        return assignments;
    }

    public void setAssignments(int assignments) {
        this.assignments = assignments;
    }

    public LinkedHashMap<LocalDate, Float>  getProgressMap() {
        return progressMap;
    }

    public void setProgressMap(LinkedHashMap<LocalDate, Float> progressMap) {
        this.progressMap = progressMap;
    }
}
