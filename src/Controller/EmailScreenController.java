package Controller;

import Model.Analysis;
import Model.Student;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.INFORMATION;

public class EmailScreenController implements Initializable {

    @FXML
    private Button btnBack;
    @FXML
    private Button btnView;
    @FXML
    private Button btnRecipients;
    @FXML
    public Button btnSend;
    @FXML
    private TextArea emailBody;
    @FXML
    private TextArea recipientsBox;
    @FXML
    private TextArea confirmationBox;
    @FXML
    private TextField tfSenderEmail;
    @FXML
    private TextField password;
    @FXML
    private ComboBox<String> cbStudents;

    private String mainBody = "";

    // public variables
    public List<Analysis> analyses;
    public List<Student> recipientList;
    public List<Student> studentList;
    public File informationFile;
    public File gradebookFile;
    public File studentDataDirectory;
    public File contactsFile;
    public String courseName;
    public LocalDate endDate;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnSend.setDisable(true);
        emailBody.setDisable(true);
        recipientsBox.setDisable(true);
        confirmationBox.setDisable(true);

        btnRecipients.setOnAction(
                actionEvent -> {
                    try {
                        Stage stage;
                        Parent root;
                        stage = (Stage) btnRecipients.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/contactsScreen.fxml"));
                        root = loader.load();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();

                        ContactsScreenController controller = loader.getController();
                        controller.gradebookFile = gradebookFile;
                        controller.studentDataDirectory = studentDataDirectory;
                        controller.contactsFile = contactsFile;
                        controller.informationFile = informationFile;
                        controller.endDate = endDate;
                        controller.courseName = courseName;
                        controller.studentList = studentList;
                        controller.analyses = analyses;
                        controller.setContactsTable();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        btnBack.setOnAction(
                actionEvent -> {
                    try {
                        Stage stage;
                        Parent root;
                        stage = (Stage) btnBack.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/reportsScreen.fxml"));
                        root = loader.load();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();

                        ReportsScreenController controller = loader.getController();
                        controller.gradebookFile = gradebookFile;
                        controller.studentDataDirectory = studentDataDirectory;
                        controller.contactsFile = contactsFile;
                        controller.informationFile = informationFile;
                        controller.endDate = endDate;
                        controller.studentList = studentList;
                        controller.courseName = courseName;

                        // Create Table and Graph for next Stage
                        controller.cbStudent.getItems().clear();
                        for (int i = 0; i < studentList.size(); i++) {
                            controller.cbStudent.getItems().add(studentList.get(i).getName());
                        }
                        controller.allStudents = controller.runAnalysis();
                        for (int i = 0; i < controller.allStudents.size(); i++) {
                            controller.processStudentData(controller.allStudents.get(i));
                        }
                        controller.createTable();
                        controller.graphAllStudentsAssignmentsCompleted();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        btnView.setOnAction(
                actionEvent -> {
                    viewStudentMessage();

                });

        btnSend.setOnAction(
                actionEvent -> {
                    if(!tfSenderEmail.getText().isEmpty() && !password.getText().isEmpty())
                    {
                        for (int i = 0; i < recipientList.size(); i++) {
                            String from = tfSenderEmail.getText();
                            String pass = password.getText();
                            String subject = "Student Update Report";
                            String message;
                            System.out.println(recipientList.get(i).getName());

                            for (int j = 0; j < analyses.size(); j++) {
                                if(recipientList.get(i).getName().equals(analyses.get(j).getName())){
                                    message = standardTemplate(analyses.get(j));
                                    System.out.println("analysis match");
                                    if(createEmail(from, pass, recipientList.get(i), subject, message)){
                                        System.out.println("email sent");
                                        String studentConfirm = recipientList.get(i).getStudentEmail() + " Email sent\n";
                                        String parentConfirm = recipientList.get(i).getParentEmail() + " Email sent\n";
                                        confirmationBox.appendText(studentConfirm + parentConfirm);
                                        btnSend.setDisable(true);
                                    }
                                    else{
                                        btnSend.setDisable(false);
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Alert alert = new Alert(INFORMATION);
                        alert.setHeaderText("ERROR");
                        alert.setContentText("Input a user email and password first");
                        alert.showAndWait();
                    }

                });


    }

    public void setRecipientsBox(List<Student> list){
        String recipients = "Recipients: ";
        for (int i = 0; i < list.size(); i++) {
            recipients += "\n " + list.get(i).getName();
        }
        recipientsBox.setText(recipients);
    }

    public void setCustomOptions(List<Student> list)
    {
        cbStudents.getItems().clear();
        for (int i = 0; i < list.size(); i++) {
            cbStudents.getItems().add(list.get(i).getName());
        }
    }

    private String standardTemplate(Analysis a){
        String s = "";
        DecimalFormat df = new DecimalFormat("#");
        if(a.getRateOfCompletion()>a.getCorrectedRateOfCompletion()){
            s = "ahead/On-track";
        }
        else{
            s = "behind";
        }
        String greeting = "Hi,\nThis is an update for " + a.getName() + "'s progress in " + courseName + "\n\n";
        String grade = "Current Grade = " + a.getGrade() + "% \n";
        String progress =  "Current Progress = " + a.getProgress() + "% \n";
        String status = "Student Status = " + s + "\n";
        String rate = "\n" + a.getName() + " is completing roughly "+ df.format(a.getRateOfCompletion()) +" assignments per week \n";
        String estimatedCompletionDate = "The estimated completion date for the course at your student's current work rate is: "+a.getEstimatedCompletionDate()+"\n";
        String progressTable = "\nYour student's progress rate: \n";
        for (Map.Entry<LocalDate, Float> entry : a.getProgressMap().entrySet()) {
            LocalDate key = entry.getKey();
            float value = entry.getValue();
            progressTable += "Date: " + key + "   " + value + "% \n";
        }
        String actualDays = "\nThere are "+a.getActualDaysLeft()+" actual days left in the course.\n";
        String correctedWorkRate = "\nYour student would need to start completing "+ df.format(a.getCorrectedRateOfCompletion()) +" assignments per week in order to catch up\n";
        String signature = " ";

        String mainBody = "";
        if(a.getRateOfCompletion()>a.getCorrectedRateOfCompletion()){
            mainBody += greeting;
            mainBody += grade;
            mainBody += progress;
            mainBody += status;
            mainBody += rate;
            mainBody += estimatedCompletionDate;
            mainBody += progressTable;
            mainBody += signature;
        }
        else{
            mainBody += greeting;
            mainBody += grade;
            mainBody += progress;
            mainBody += status;
            mainBody += rate;
            mainBody += estimatedCompletionDate;
            mainBody += progressTable;
            mainBody += actualDays;
            mainBody += correctedWorkRate;
            mainBody += signature;
        }

        emailBody.setText(mainBody);
        return(mainBody);
    }

    private void viewStudentMessage(){
        String name = cbStudents.getValue();
        for (int i = 0; i < analyses.size(); i++) {
            if(analyses.get(i).getName().equals(name)){
                String temp = standardTemplate(analyses.get(i));
            }
        }
    }

    private static Boolean createEmail(String from, String pass, Student student, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, pass);
                    }
                });

        try {

            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(from));
            email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(student.getStudentEmail() + ", " + student.getParentEmail()));
            email.setSubject(subject);
            email.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", from, pass);
            transport.send(email);
            transport.close();

            return true;
        }catch(javax.mail.AuthenticationFailedException e){
            Alert alert = new Alert(INFORMATION);
            alert.setHeaderText("Authentication Error");
            alert.setContentText("Authentication Error: Make sure you're using a correct username and password.");
            alert.showAndWait();
            return false;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
