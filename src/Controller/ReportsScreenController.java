package Controller;

import Model.Analysis;
import Model.Student;
import com.opencsv.CSVReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

public class ReportsScreenController implements Initializable {

    // All FXML variables
    @FXML
    public Button btnViewAllStudents;
    @FXML
    public Button btnBack;
    @FXML
    public Button btnEmail;
    @FXML
    public Button btnViewAll;
    @FXML
    public Button btnRunIndividual;
    @FXML
    public ComboBox cbStudent;
    @FXML
    public Label studentLabel;
    @FXML
    public ObservableList<Analysis> analysesObsList = FXCollections.observableArrayList();
    @FXML
    public CategoryAxis xAxis = new CategoryAxis();
    @FXML
    public NumberAxis yAxis = new NumberAxis();
    @FXML
    public LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
    @FXML
    public TableView<Analysis> table = new TableView<>();

    // Local Variables for this stage
    public FileChooser fileChooser = new FileChooser();
    public List<Analysis> analyses = new ArrayList<>();
    public List<List<Student>> allStudents;

    // Variables to hold file path data
    public List<Student> studentList = new ArrayList<>();
    public File informationFile;
    public File gradebookFile;
    public File studentDataDirectory;
    public File contactsFile;
    public String courseName;
    public LocalDate endDate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TableColumn name = new TableColumn("Student Name");
        TableColumn numOfRecords = new TableColumn("# of Records");
        TableColumn firstDate = new TableColumn("Start Date");
        TableColumn<Analysis, Float> grade = new TableColumn("Current Grade");
        TableColumn progress = new TableColumn("Progress");
        TableColumn assignmentsCompleted = new TableColumn("Assignments Completed");
        TableColumn assignmentsRemaining = new TableColumn("Assignments Remaining");
        TableColumn totalAssignments = new TableColumn("Total Assignments in Course");
        TableColumn actualRate = new TableColumn("Rate of Completion");
        TableColumn daysLeftAtRate = new TableColumn("Days To Complete Course At Rate");
        TableColumn correctedRate = new TableColumn("Corrected Rate");
        TableColumn actualDaysLeft = new TableColumn("Actual Days Left in Course");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        numOfRecords.setCellValueFactory(new PropertyValueFactory<>("numberOfRecords"));
        firstDate.setCellValueFactory(new PropertyValueFactory<>("firstReport"));
        grade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        progress.setCellValueFactory(new PropertyValueFactory<>("progress"));
        assignmentsCompleted.setCellValueFactory(new PropertyValueFactory<>("assignmentsCompleted"));
        assignmentsRemaining.setCellValueFactory(new PropertyValueFactory<>("assignmentsRemaining"));
        totalAssignments.setCellValueFactory(new PropertyValueFactory<>("assignments"));
        actualRate.setCellValueFactory(new PropertyValueFactory<>("rateOfCompletion"));
        daysLeftAtRate.setCellValueFactory(new PropertyValueFactory<>("daysLeft"));
        correctedRate.setCellValueFactory(new PropertyValueFactory<>("correctedRateOfCompletion"));
        actualDaysLeft.setCellValueFactory(new PropertyValueFactory<>("actualDaysLeft"));
        table.getColumns().addAll(name, numOfRecords, firstDate, grade, progress, assignmentsCompleted, assignmentsRemaining, totalAssignments, actualRate, daysLeftAtRate, correctedRate, actualDaysLeft);



        btnBack.setOnAction(
            actionEvent -> {
                try {
                Stage stage;
                Parent root;
                stage = (Stage) btnBack.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/mainScreen.fxml"));
                root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

                MainScreenController controller = loader.getController();
                controller.gradebookFile = gradebookFile;
                controller.directoryLabel.setText(gradebookFile.getParentFile().getAbsolutePath());
                controller.studentDataDirectory = studentDataDirectory;
                controller.contactsFile =contactsFile;
                controller.informationFile =informationFile;
                controller.endDate = endDate;
                controller.courseName = courseName;
                controller.importData();
                controller.btnSave.setDisable(true);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        btnViewAll.setOnAction(
            actionEvent -> {
                lineChart.getData().clear();
                try {
                    allStudents = runAnalysis();
                    analyses.clear();
                    for (int i = 0; i < allStudents.size(); i++) {
                        processStudentData(allStudents.get(i));
                    }
                    createTable();
                    graphAllStudentsAssignmentsCompleted();
                    studentLabel.setText("Viewing All Students");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        btnRunIndividual.setOnAction(
                actionEvent -> {
                    String temp = (String) cbStudent.getValue();
                    for (int i = 0; i < analyses.size(); i++) {
                        if (analyses.get(i).getName().equals(temp)){
                            Analysis a = analyses.get(i);
                            setTableForStudent(a);
                            setGraphForStudent(a);
                            setStudentLabel(a);
                        }
                    }

                });

        btnEmail.setOnAction(
                actionEvent -> {
                    try {
                        Stage stage;
                        Parent root;
                        stage = (Stage) btnEmail.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/emailScreen.fxml"));
                        root = loader.load();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();

                        EmailScreenController controller = loader.getController();
                        controller.gradebookFile = gradebookFile;
                        controller.studentDataDirectory = studentDataDirectory;
                        controller.contactsFile = contactsFile;
                        controller.informationFile = informationFile;
                        controller.endDate = endDate;
                        controller.courseName = courseName;
                        controller.analyses = analyses;
                        controller.studentList = studentList;


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });



    }

    public List importFileToList(File file) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(file), ',' , '"');
        List<String[]> importList = new ArrayList<>();
        String[] line;
        while((line = reader.readNext()) != null){
            importList.add(line);
        }
        reader.close();

        return importList;
    }

    public List<List<Student>> runAnalysis() throws IOException {
        File[] files;
        files = studentDataDirectory.listFiles();
        List<List<Student>> allStudents = new ArrayList<>();
        if (files != null){
            for (File file : files) {
                List<Student> student = new ArrayList<>();
                List<String[]> importList = importFileToList(file);
                for (int j = 1; j < importList.size(); j++) {
                    List<String> row = new ArrayList(Arrays.asList(importList.get(j)));
                    Student s = new Student();
                    s.setDate(LocalDate.parse(row.get(0).trim()));
                    s.setName(row.get(1));
                    s.setSsid(row.get(2));
                    s.setCurrentGrade(Float.valueOf(row.get(3)));
                    s.setProgress(Float.valueOf(row.get(4).trim()));
                    s.setAssignmentsCompleted(Integer.valueOf(row.get(5).trim()));
                    s.setAssignmentsRemaining(Integer.valueOf(row.get(6).trim()));
                    s.setAssignments(Integer.valueOf(row.get(7).trim()));
                    student.add(s);
                }
                allStudents.add(student);
            }
        }
        return allStudents;
    }

    public void processStudentData(List<Student> list){
        Analysis a = new Analysis();

        int assignmentsCompleted = (list.get(list.size() - 1).getAssignmentsCompleted());
        int assignmentsRemaining = list.get(list.size() -1).getAssignmentsRemaining();
        int numOfEntries = 0;
        LocalDate firstReportDate = list.get(0).getDate();
        LocalDate lastReportDate = LocalDate.now();
        LinkedHashMap<LocalDate, Float> progressMap = new LinkedHashMap<>();

        for (int i = 0; i < list.size(); i++) {
            numOfEntries += 1;

            if (list.get(i).getDate().isBefore(firstReportDate)){
                firstReportDate = list.get(i).getDate();
            }
            if (list.get(i).getDate().isAfter(lastReportDate)){
                lastReportDate = list.get(i).getDate();
            }

            progressMap.put(list.get(i).getDate(), list.get(i).getProgress());
        }

        int actualDaysLeft = (int) DAYS.between(lastReportDate, endDate);
        int daysSpent = (int) DAYS.between(firstReportDate, lastReportDate);
        float rateOfCompletion = 0.0f;
        if(daysSpent != 0){
            float weeks = daysSpent/7;
            rateOfCompletion = ((float)assignmentsCompleted/weeks);
        }
        int daysLeft = 0;
        if(assignmentsRemaining != 0){
            daysLeft = (int) (7*(assignmentsRemaining/rateOfCompletion));
        }
        float correctedRateOfCompletion = ((float)assignmentsRemaining/(actualDaysLeft/7));

        a.setName(list.get(0).getName());
        a.setNumberOfRecords(numOfEntries);
        a.setFirstReport(firstReportDate);
        a.setLatestReport(lastReportDate);
        a.setProgressMap(progressMap);
        a.setEstimatedCompletionDate((LocalDate.now().plusDays(daysLeft)));
        a.setGrade(list.get(list.size() - 1).getCurrentGrade());
        a.setProgress(list.get(list.size() - 1).getProgress());
        a.setRateOfCompletion(rateOfCompletion);
        a.setActualDaysLeft(actualDaysLeft);
        a.setCorrectedRateOfCompletion(correctedRateOfCompletion);
        a.setDaysLeft(daysLeft);
        a.setAssignmentsCompleted(list.get(list.size() - 1).getAssignmentsCompleted());
        a.setAssignmentsRemaining(list.get(list.size() - 1).getAssignmentsRemaining());
        a.setAssignments(list.get(list.size() - 1).getAssignments());
        analyses.add(a);
    }

    public void createTable(){
        table.getItems().clear();
        analysesObsList.clear();
        analysesObsList.addAll(analyses);
        table.setItems(FXCollections.observableArrayList(analysesObsList));
    }

    public void setTableForStudent(Analysis analysis){
        analysesObsList.clear();
        table.getItems().clear();
        analysesObsList.add(analysis);
        table.setItems(FXCollections.observableArrayList(analysesObsList));
    }

    public void graphAllStudentsAssignmentsCompleted(){
        xAxis.setLabel("Time");
        yAxis.setLabel("Assignments Completed");
        List<XYChart.Series> data = new ArrayList<>();
        for (int i = 0; i < analyses.size(); i++) {
            XYChart.Series student = new XYChart.Series();
            student.setName(analyses.get(i).getName());
            for (int j = 0; j < allStudents.size(); j++) {
                for (int k = 0; k < allStudents.get(j).size(); k++) {
                    if (allStudents.get(j).get(k).getName().equals(student.getName())){
                        String time = allStudents.get(j).get(k).getDate().toString();
                        int assignments = allStudents.get(j).get(k).getAssignmentsCompleted();
                        student.getData().add(new XYChart.Data(time, assignments));
                    }
                }

            }
            data.add(student);
        }
        for (int i = 0; i < data.size(); i++) {

            lineChart.getData().add(data.get(i));
        }
    }

    public void setGraphForStudent(Analysis analysis){
        lineChart.getData().clear();
        xAxis.setLabel("Time");
        yAxis.setLabel("Assignments Completed");
        XYChart.Series student = new XYChart.Series();
        student.setName(analysis.getName());
        for (int i = 0; i < allStudents.size(); i++) {
            for (int j = 0; j < allStudents.get(i).size(); j++) {
                if (allStudents.get(i).get(j).getName().equals(analysis.getName())){
                    String time = allStudents.get(i).get(j).getDate().toString();
                    int assignments = allStudents.get(i).get(j).getAssignmentsCompleted();
                    student.getData().add(new XYChart.Data(time, assignments));
                }
            }
        }
        lineChart.getData().add(student);
    }

    public void setStudentLabel(Analysis analysis){
        LocalDate projectedEndDate = LocalDate.now().plusDays(analysis.getDaysLeft());
        String s = (analysis.getName() + ":     " +
                "Assignments Completed/Day: " + analysis.getRateOfCompletion() + "     " +
            "Projected Completion Date: " + projectedEndDate.toString());
        studentLabel.setText(s);
    }
}





























