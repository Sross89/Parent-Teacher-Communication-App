package Controller;

import Model.Assignment;
import Model.Course;
import Model.Student;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.scene.control.Alert.AlertType.INFORMATION;

public class MainScreenController implements Initializable {

    // FXML variables for this stage
    @FXML
    private Button btnCreateCourse;
    @FXML
    private Button btnNewCourse;
    @FXML
    private Button btnImport;
    @FXML
    private Button btnNewDirectory;
    @FXML
    private Button btnAnalytics;
    @FXML
    public Button btnSave;
    @FXML
    private Button btnDirectory;
    @FXML
    public Label directoryLabel;
    @FXML
    public Label newDirectoryLabel;
    @FXML
    public TextField courseNameLabel;
    @FXML
    public DatePicker endDatePicker;
    @FXML
    private TableView<Student> table = new TableView<>();
    @FXML
    private ObservableList<Student> studentObsList = FXCollections.observableArrayList();

    FileChooser fileChooser = new FileChooser();
    DirectoryChooser directoryChooser = new DirectoryChooser();
    public Course course = new Course();
    public List<Student> studentList = new ArrayList<>();
    public File informationFile;
    public File gradebookFile;
    public File studentDataDirectory;
    public File contactsFile;
    public String courseName;
    public LocalDate endDate;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btnImport.setDisable(true);
        courseNameLabel.setDisable(true);
        endDatePicker.setDisable(true);
        btnCreateCourse.setDisable(true);
        btnSave.setDisable(true);
        btnNewDirectory.setDisable(true);
        btnAnalytics.setDisable(true);
        endDatePicker.setValue(LocalDate.now());
        TableColumn name = new TableColumn("Student Name");
        TableColumn ssid = new TableColumn("SSID");
        TableColumn studentEmail = new TableColumn("Student E-mail");
        TableColumn parentEmail = new TableColumn("Parent E-mail");
        TableColumn<Student, Float> grade = new TableColumn("Current Grade");
        TableColumn progress = new TableColumn("Current Progress");
        TableColumn assignmentsCompleted = new TableColumn("Assignments Completed");
        TableColumn assignmentsRemaining = new TableColumn("Assignments Remaining");
        TableColumn totalAssignments = new TableColumn("Total Assignments in Course");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        ssid.setCellValueFactory(new PropertyValueFactory<>("ssid"));
        studentEmail.setCellValueFactory(new PropertyValueFactory<>("studentEmail"));
        parentEmail.setCellValueFactory(new PropertyValueFactory<>("parentEmail"));
        grade.setCellValueFactory(new PropertyValueFactory<>("currentGrade"));
        progress.setCellValueFactory(new PropertyValueFactory<>("progress"));
        assignmentsCompleted.setCellValueFactory(new PropertyValueFactory<>("assignmentsCompleted"));
        assignmentsRemaining.setCellValueFactory(new PropertyValueFactory<>("assignmentsRemaining"));
        totalAssignments.setCellValueFactory(new PropertyValueFactory<>("assignments"));
        table.getColumns().addAll(name, ssid, studentEmail, parentEmail, grade, progress, assignmentsCompleted, assignmentsRemaining, totalAssignments);

        btnNewCourse.setOnAction(
                actionEvent -> {
                    courseNameLabel.setDisable(false);
                    endDatePicker.setDisable(false);
                    btnCreateCourse.setDisable(false);
                    btnNewCourse.setDisable(true);
                    btnNewDirectory.setDisable(false);
                });

        btnNewDirectory.setOnAction(
                actionEvent -> setDirectoryPath(btnNewDirectory, newDirectoryLabel));

        btnCreateCourse.setOnAction(
                actionEvent -> {

                    if (!courseNameLabel.getText().isEmpty() && !endDatePicker.getValue().toString().isEmpty() && !newDirectoryLabel.getText().isEmpty()){
                        try {
                        createFoldersInDirectory(newDirectoryLabel.getText());
                        String fileContent = courseNameLabel.getText() + "\n"
                                + endDatePicker.getValue();
                        FileWriter fileWriter = new FileWriter(newDirectoryLabel.getText() + "/information.txt");
                        fileWriter.write(fileContent);
                        fileWriter.close();
                        courseNameLabel.setDisable(true);
                        endDatePicker.setDisable(true);
                        btnCreateCourse.setDisable(true);
                        btnNewCourse.setDisable(false);
                        Alert alert = new Alert(INFORMATION);
                        alert.setHeaderText("INFORMATION");
                        alert.setContentText("A new course directory has been created. You'll need to fill the directory with your canvas gradebook and the course contact list before proceeding.");
                        alert.showAndWait();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    }
                });

        btnDirectory.setOnAction(
                actionEvent -> {
                        setDirectoryPath(btnDirectory, directoryLabel);

                        if (!directoryLabel.getText().isEmpty()){
                            btnImport.setDisable(false);
                        }
                });

        btnImport.setOnAction(
                actionEvent -> {
                    if(validateDirectory(directoryLabel.getText())){
                        importData();
                        btnImport.setDisable(true);
                    }
                });

        btnAnalytics.setOnAction(
                actionEvent -> {
                    try {
                        Stage stage;
                        Parent root;
                        stage = (Stage) btnAnalytics.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/reportsScreen.fxml"));
                        root = loader.load();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();

                        // set variables for next stage:
                        ReportsScreenController controller = loader.getController();
                        controller.studentDataDirectory = studentDataDirectory;
                        controller.contactsFile = contactsFile;
                        controller.gradebookFile = gradebookFile;
                        controller.informationFile = informationFile;
                        controller.studentList = studentList;
                        controller.endDate = endDate;
                        controller.courseName = courseName;

                        // Create Table and Graph for next Stage
                        controller.cbStudent.getItems().clear();
                        for (int i = 0; i < course.getStudentList().size(); i++) {
                            controller.cbStudent.getItems().add(course.getStudentList().get(i).getName());
                        }
                        controller.allStudents = controller.runAnalysis();
                        for (int i = 0; i < controller.allStudents.size(); i++) {
                            controller.processStudentData(controller.allStudents.get(i));
                        }
                        controller.createTable();
                        controller.graphAllStudentsAssignmentsCompleted();
                        controller.studentList = studentList;
                        } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        btnSave.setOnAction(
                actionEvent -> {
                    try {
                        saveData(course);
                        btnSave.setDisable(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }

    private void setDirectoryPath(Button button, Label label){
        Stage stage = (Stage) button.getScene().getWindow();
        File file = directoryChooser.showDialog(stage);
        if(file != null){
            label.setText(file.getAbsolutePath());
        }
    }

    private List importFileToList(File file) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(file), ',' , '"');
        List<String[]> importList = new ArrayList<>();
        String[] line;
        while((line = reader.readNext()) != null){
            importList.add(line);
        }
        reader.close();

        return importList;
    }

    private void createContacts(List<String[]> importList){
        List<List<String>> allRows = new ArrayList<>();
        for (int i = 0; i < importList.size(); i++) {
            List<String> row = new ArrayList(Arrays.asList(importList.get(i)));
            allRows.add(row);
        }

        for (int i = 0; i < studentList.size(); i++) {
            for (int j = 0; j < allRows.size(); j++) {
                if (studentList.get(i).getStudentEmail().equals(allRows.get(j).get(1))){
                    studentList.get(i).setParentEmail(allRows.get(j).get(2));
                }

            }
        }

    }

    private List<Assignment> createAssignments(List<String[]> list){
        List<Assignment> assignmentList = new ArrayList<>();
        Pattern p = Pattern.compile("\\d");
        List<List<String>> allRows = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            List<String> row = new ArrayList(Arrays.asList(list.get(i)));
            allRows.add(row);
        }

        for (int i = 0; i < allRows.get(0).size(); i++) {
            Matcher m = p.matcher(allRows.get(1).get(i));
            if(m.find()){
                Assignment assignment = new Assignment();
                assignment.setName(allRows.get(0).get(i));
                assignment.setMaxScore(allRows.get(1).get(i));
                assignmentList.add(assignment);
            }
        }
        return assignmentList;
    }
    
    private void createStudents(List<String[]> list, List<Assignment> assignmentList){
        float courseGradeCounter = 0.0f;
        float studentGradeCounter = 0.0f;
        List<List<String>> allRows = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            List<String> row = new ArrayList(Arrays.asList(list.get(i)));
            allRows.add(row);
        }
        for (int i = 2; i < allRows.size(); i++) {
            Student student = new Student();
            student.setName(allRows.get(i).get(0));
            student.setSsid(allRows.get(i).get(2));
            student.setStudentEmail(allRows.get(i).get(3));
            student.setAssignments(assignmentList.size());

            // calculate completed/remaining assignments:
            int assignmentCounter = 0;


            for (int j = 0; j < allRows.get(0).size(); j++) {
                Pattern p = Pattern.compile("\\d");
                Matcher m = p.matcher(allRows.get(1).get(j));
                if(m.find()){
                    if (!allRows.get(i).get(j).isEmpty()){
                        assignmentCounter += 1;
                        courseGradeCounter += Float.parseFloat(allRows.get(1).get(j).trim());
                        studentGradeCounter += Float.parseFloat(allRows.get(i).get(j).trim());
                    }
                }
            }
            student.setCurrentGrade(100.0f *(studentGradeCounter/courseGradeCounter));
            student.setAssignmentsCompleted(assignmentCounter);
            student.setAssignmentsRemaining(assignmentList.size()-assignmentCounter);
            student.setProgress((assignmentCounter * 100f)/ assignmentList.size());
            studentList.add(student);
        }
    }

    private void createTable(Course course){
        studentObsList.setAll(course.getStudentList());
        table.setItems(FXCollections.observableArrayList(studentObsList));
    }

    private void saveData(Course course) throws IOException {
        // Verify that a directory has been selected
        if (!directoryLabel.getText().isEmpty()) {

            List<String> info = new ArrayList<>();
            try(BufferedReader br = Files.newBufferedReader(informationFile.toPath())){
                String line;
                while((line=br.readLine()) != null){
                    info.add(line);
                }
            }catch (IOException e){
                System.err.format("IOException: %s%n", e);
            }
            info.remove(0);
            info.remove(0);
            LocalDate gradebookDate = LocalDate.parse(gradebookFile.getName().substring(0, gradebookFile.getName().indexOf("T")));
            LocalDate lastUpdate = gradebookDate;
            if (info.size()>0){
                lastUpdate = LocalDate.parse(info.get(info.size()-1).substring(0, info.get(info.size()-1).indexOf(":")).trim());
            }
            if(lastUpdate.isBefore(gradebookDate) || info.size() == 0){
                for (int i = 0; i < course.getStudentList().size(); i++) {
                    String csv = directoryLabel.getText() + "/Student Records/" + course.getStudentList().get(i).getName() + "-data.csv";
                    File tempFile = new File(csv);
                    if (tempFile.exists()) {
                        CSVWriter writer = new CSVWriter(new FileWriter(csv, true));
                        String[] record = (gradebookDate.toString() + "," +
                                course.getStudentList().get(i).getName() + ", " +
                                course.getStudentList().get(i).getSsid() + ", " +
                                course.getStudentList().get(i).getCurrentGrade() + ", " +
                                course.getStudentList().get(i).getProgress() + ", " +
                                course.getStudentList().get(i).getAssignmentsCompleted() + ", " +
                                course.getStudentList().get(i).getAssignmentsRemaining() + ", " +
                                course.getStudentList().get(i).getAssignments()).split(",");
                        writer.writeNext(record);
                        writer.close();

                    } else {
                        String csvHeader = directoryLabel.getText() + "/Student Records/" + course.getStudentList().get(i).getName() + "-data.csv";
                        CSVWriter writer = new CSVWriter(new FileWriter(csvHeader));
                        String[] record = {"Date", "Name", "SSID", "Grade", "Progress", "Assignments Completed", "Assignments Remaining", "Assignments"};
                        writer.writeNext(record);
                        record = (gradebookDate.toString() + "," +
                                course.getStudentList().get(i).getName() + ", " +
                                course.getStudentList().get(i).getSsid() + ", " +
                                course.getStudentList().get(i).getCurrentGrade() + ", " +
                                course.getStudentList().get(i).getProgress() + ", " +
                                course.getStudentList().get(i).getAssignmentsCompleted() + ", " +
                                course.getStudentList().get(i).getAssignmentsRemaining() + ", " +
                                course.getStudentList().get(i).getAssignments()).split(",");
                        writer.writeNext(record);
                        writer.close();
                    }

                }
                try {
                    FileWriter fw = new FileWriter(informationFile, true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.newLine();
                    bw.write(gradebookDate.toString() + "     : Last  Gradebook Update to Student Records");
                    bw.close();
                    fw.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }

                Alert alert = new Alert(INFORMATION);
                alert.setHeaderText("SUCCESS");
                alert.setContentText("Student data has been updated");
                alert.showAndWait();
            }
            else{
                Alert alert = new Alert(INFORMATION);
                alert.setHeaderText("ERROR");
                alert.setContentText("Student data has already been updated for this gradebook");
                alert.showAndWait();
            }

        } else {
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("ERROR");
            alert.setContentText("Select a directory to store student data first");
            alert.showAndWait();
        }
    }

    private void createFoldersInDirectory(String directory){
        new File(directory + "/gradebooks").mkdirs();
        new File(directory + "/Student Records").mkdirs();
    }

    private Boolean validateDirectory(String directory){
        File path = new File(directory);
        File [] files = path.listFiles();
        if (Objects.requireNonNull(path.list()).length == 0){
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("WARNING");
            alert.setContentText("This folder is empty and doesn't contain the necessary files/folders\nCreate a new course if you need a new directory.");
            alert.showAndWait();
            return false;
        }
        else{
            int count = 0;
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory() && files[i].getName().equals("gradebooks")){
                    count += 1;
                }
                if (files[i].isDirectory() && files[i].getName().equals("Student Records")){
                    count += 1;
                }
                if (files[i].isFile() && files[i].getName().equals("information.txt")){
                    count += 1;
                }
                if (files[i].isFile() && files[i].getName().equals("contact list.csv")){
                    count += 1;
                }
            }
            if (count == 4){
                return true;
            }
            else{
                Alert alert = new Alert(INFORMATION);
                alert.setHeaderText("WARNING");
                alert.setContentText("This folder doesn't contain all the necessary files and folders. The directory should contain:\n" +
                        "a 'gradebooks' folder\n" +
                                "a 'student records folder'\n" +
                                "an 'information.txt' file\n" +
                                "a 'contact list.csv' file");
                alert.showAndWait();
                return false;
            }
        }
    }

    public void setFilePathsFromDirectory(String directory){
        File path = new File(directory);
        File [] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory() && files[i].getName().equals("gradebooks")){
                File [] gradebooks = files[i].listFiles();
                int index = gradebooks[0].getName().indexOf("T");
                String date = gradebooks[0].getName().substring(0, index);
                LocalDate lastDate = LocalDate.parse(date);
                for (int j = 0; j < gradebooks.length; j++) {
                    index = gradebooks[j].getName().indexOf("T");
                    date = gradebooks[j].getName().substring(0, index);
                    if (LocalDate.parse(date).isAfter(lastDate) || LocalDate.parse(date).isEqual(lastDate)){
                        lastDate = (LocalDate.parse(date));
                        gradebookFile = gradebooks[j];
                    }
                }
            }
            if (files[i].isDirectory() && files[i].getName().equals("Student Records")){
                studentDataDirectory = files[i];
            }
            if (files[i].isFile() && files[i].getName().equals("contact list.csv")){
                contactsFile = files[i];
            }
            if (files[i].isFile() && files[i].getName().equals("information.txt")){
                informationFile = files[i];
            }
        }
    }

    public void importData(){
        table.getItems().clear();
        studentList.clear();
        studentObsList.clear();
        setFilePathsFromDirectory(directoryLabel.getText());

        // read gradebook and create student objects
        try {
            List<String[]> list = importFileToList(gradebookFile);
            List<Assignment> assignmentList = createAssignments(list);
            createStudents(list, assignmentList);

            //import and add contacts to studentlist
            List contactList = importFileToList((contactsFile));
            createContacts(contactList);

            //Get and set the course name and course end date
            List<String> info = new ArrayList<>();
            try(BufferedReader br = Files.newBufferedReader(informationFile.toPath())){
                String line;
                while((line=br.readLine()) != null){
                    info.add(line);
                }
            }catch (IOException e){
                System.err.format("IOException: %s%n", e);
            }
            courseName = info.get(0);
            endDate  = LocalDate.parse(info.get(1));

            course.setStudentList(studentList);
            course.setAssignmentList(assignmentList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create table
        table.getItems().clear();
        createTable(course);
        btnSave.setDisable(false);
        btnAnalytics.setDisable(false);
    }
}
