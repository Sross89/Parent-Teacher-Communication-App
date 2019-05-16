package Controller;

import Model.Analysis;
import Model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ContactsScreenController implements Initializable {

    @FXML
    private Button btnAdd;
    @FXML
    private Button btnRemove;
    @FXML
    private Button btnAddAll;
    @FXML
    private Button btnConfirm;
    @FXML
    public ObservableList<Student> studentObsList = FXCollections.observableArrayList();
    @FXML
    public ObservableList<Student> recipientObsList = FXCollections.observableArrayList();
    @FXML
    public TableView<Student> tableRecipients = new TableView<>();
    @FXML
    public TableView<Student> tableContacts = new TableView<>();

    public List<Student> studentList = new ArrayList<>();
    public File informationFile;
    public File gradebookFile;
    public File studentDataDirectory;
    public File contactsFile;
    public String courseName;
    public LocalDate endDate;
    public List<Analysis> analyses;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tableRecipients.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );
        tableContacts.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );


        btnAdd.setOnAction(
                actionEvent -> {
                    addContacts();
                });
        btnRemove.setOnAction(
                actionEvent -> {
                    removeContacts();
                });
        btnAddAll.setOnAction(
                actionEvent -> {
                    addAll();
                });
        btnConfirm.setOnAction(actionEvent -> {
            try {
                Stage stage;
                Parent root;
                stage = (Stage) btnConfirm.getScene().getWindow();
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
                controller.studentList = studentList;
                controller.analyses = analyses;
                controller.recipientList = recipientObsList;
                controller.setRecipientsBox(recipientObsList);
                controller.setCustomOptions(recipientObsList);
                if(recipientObsList.size()>0){
                    controller.btnSend.setDisable(false);
                }
                else{

                    controller.btnSend.setDisable(true);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void setContactsTable(){
        TableColumn name = new TableColumn("Student Name");
        TableColumn studentEmail = new TableColumn("Student E-mail");
        TableColumn parentEmail = new TableColumn("Parent E-mail");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentEmail.setCellValueFactory(new PropertyValueFactory<>("studentEmail"));
        parentEmail.setCellValueFactory(new PropertyValueFactory<>("parentEmail"));
        tableContacts.getColumns().addAll(name, studentEmail, parentEmail);
        studentObsList.setAll(studentList);
        tableContacts.setItems(FXCollections.observableArrayList(studentObsList));
    }

    public void setRecipientsTable(){
        TableColumn name = new TableColumn("Student Name");
        TableColumn studentEmail = new TableColumn("Student E-mail");
        TableColumn parentEmail = new TableColumn("Parent E-mail");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentEmail.setCellValueFactory(new PropertyValueFactory<>("studentEmail"));
        parentEmail.setCellValueFactory(new PropertyValueFactory<>("parentEmail"));
        tableRecipients.getColumns().addAll(name, studentEmail, parentEmail);
        tableRecipients.setItems(FXCollections.observableArrayList(recipientObsList));
    }

    private void  addContacts(){
        if (tableContacts.getSelectionModel().getSelectedItems() != null){
            recipientObsList.addAll(tableContacts.getSelectionModel().getSelectedItems());
            tableRecipients.getItems().clear();
            setRecipientsTable();
        }
    }

    private void removeContacts(){
        if (tableRecipients.getSelectionModel().getSelectedItems() != null){
            recipientObsList.removeAll(tableRecipients.getSelectionModel().getSelectedItems());
            tableRecipients.getItems().clear();
            setRecipientsTable();
        }
    }

    private void addAll(){
        recipientObsList.clear();
        recipientObsList.addAll(studentList);
        tableRecipients.getItems().clear();
        setRecipientsTable();
    }
}
