package scenes;

import britishcouncilbd.BranchNames;
import britishcouncilbd.CurrentSystem;
import internationalexams.Exam;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import localevents.LocalEvent;
import localevents.Notice;
import people.Accountant;
import people.Employee;
import people.EmployeeType;
import people.Instructor;
import people.Librarian;
import people.Manager;
import people.Receptionist;
import scenes.accountant.AccountantOverviewController;
import scenes.instructor.InstructorOverviewController;
import scenes.librarian.LibraryOverviewController;
import scenes.manager.ManagerOverviewController;
import scenes.receptionist.ReceptionistOverviewController;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class DefaultPageController implements Initializable {

    @FXML
    private Label labelBranchTitle;
    @FXML
    private TextField textboxUserID;
    @FXML
    private TextField textboxUserPassword;
    @FXML
    private ChoiceBox<EmployeeType> comboboxUserType;
    @FXML
    private Button login_logoutButton;
    @FXML
    private Label loggedInName;
    @FXML
    private Label loggedInTitle;
    @FXML
    private TableView<LocalEvent> tableLocalEvents;
    @FXML
    private TableColumn<LocalEvent, String> eventTitleCol;
    @FXML
    private TableColumn<LocalEvent, String> detailsCol;
    @FXML
    private TableView<Exam> tableIntExams;
    @FXML
    private TableColumn<Exam, String> examCol;
    @FXML
    private TextArea noticeBoardText;
     
    private Employee employee;
    private BranchNames thisBranch; 
    private ObservableList<LocalEvent> events;
    private ObservableList<Exam> exams;
    private ArrayList<Notice> noticeBoard;
    @FXML
    private Button buttonHome;
   
    
    public void initData( Employee user ) {                 // for users navigating to default page
        employee = user;
        loggedInName.setText( employee.getName() );
        loggedInTitle.setText( employee.getPost().name() );
        textboxUserID.setText( String.valueOf( employee.getId() ) ); textboxUserPassword.setText("***");
        textboxUserID.setDisable(true); textboxUserPassword.setDisable(true);
        login_logoutButton.setText("logout");
        
        thisBranch = CurrentSystem.getCurrentBranchName();
        labelBranchTitle.setText( thisBranch.name() );
        setTablesAndNotices();
    }
    
    public void initData(){                                 // for non-logged in default page
        employee = null;
        loggedInName.setText("");
        loggedInTitle.setText("");
        textboxUserID.setText("") ; textboxUserPassword.setText("");
        textboxUserID.setDisable(false); textboxUserPassword.setDisable(false);
        login_logoutButton.setText("login");
        
        thisBranch = CurrentSystem.getCurrentBranchName();
        labelBranchTitle.setText( thisBranch.name() );
        setTablesAndNotices();
    }
    
    void setTablesAndNotices() {
        noticeBoardText.clear();
        events = LocalEvent.fetchAllLocalEventListToView( thisBranch );
        tableLocalEvents.setItems(events);
        exams = Exam.getAllExamsToView();
        tableIntExams.setItems( exams );
        
        noticeBoard = Notice.fetchNotices(thisBranch);
        for ( Notice n : noticeBoard ){
            noticeBoardText.appendText(n.toString());
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        comboboxUserType.getItems().setAll( EmployeeType.values() );
        eventTitleCol.setCellValueFactory( new PropertyValueFactory<>("eventName") );
        detailsCol.setCellValueFactory( new PropertyValueFactory<>("eventDetailFull") );
        examCol.setCellValueFactory( new PropertyValueFactory<>("fullNameOfExam") );
    }    

    @FXML
    private void intlExamRegButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("IntExamReg.fxml"));    
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        Stage window = new Stage();
        window.setTitle( "Candidate Registration" );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void localRegButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("LocalEventReg.fxml"));    
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        LocalEventRegController controller = loader.getController(); controller.initData( thisBranch );
        Stage window = new Stage();
        window.setTitle( "Local Event Registration for Members" );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void loginButtonOnClick(ActionEvent event) {
        if ( employee != null ) {
            initData();
            Alert b = new Alert(Alert.AlertType.INFORMATION);
            b.setTitle("Logged Out");
            b.setContentText("User has been successfully logged out.");
            b.showAndWait();
        } else {
            try {
                employee = Employee.login(
                    Integer.parseInt(textboxUserID.getText()), textboxUserPassword.getText(),
                    comboboxUserType.getValue(), thisBranch
                );
                if ( employee != null ){
                    initData( employee );
                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setTitle("Success");
                    b.setContentText("Logged-in successfully. Welcome " + employee.getName() );
                    b.showAndWait();
                } else {
                    Alert b = new Alert(Alert.AlertType.WARNING);
                    b.setTitle("Failed");
                    b.setContentText("Could not authenticate user.");
                    b.showAndWait();
                }
            } catch (Exception ex) {
                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setTitle("Input Error");
                b.setContentText("Please ensure login credentials are typed correctly.");
                b.showAndWait();
            }
        }
    }

    @FXML
    private void homeButtonOnClick(ActionEvent event) {
        if ( employee != null ) {
            EmployeeType type = employee.getPost();           
            FXMLLoader loader = new FXMLLoader(); Parent root = null; Scene scene;
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setTitle( "Welcome " + employee.getName() );
            
            switch (type) {
                case RECEPTIONIST:
                    loader.setLocation(getClass().getResource("/scenes/receptionist/ReceptionistOverview.fxml"));
                    try {
                        root = loader.load();
                    } catch (IOException ex) {
                        System.out.println(ex); return;
                    }
                    scene = new Scene( root );
                    ReceptionistOverviewController rcontroller = loader.getController(); rcontroller.initData( (Receptionist) employee, thisBranch );
                    window.setScene( scene ); window.show();
                    break;
                    
                case ACCOUNTANT: 
                    loader.setLocation(getClass().getResource("/scenes/accountant/AccountantOverview.fxml"));
                    try {
                        root = loader.load();
                    } catch (IOException ex) {
                        System.out.println(ex); return;
                    }
                    scene = new Scene( root );
                    AccountantOverviewController acontroller = loader.getController(); acontroller.initData((Accountant) employee, thisBranch );
                    window.setScene( scene ); window.show();
                    break;
                
                case MANAGER:
                    loader.setLocation(getClass().getResource("/scenes/manager/ManagerOverview.fxml"));
                    try {
                        root = loader.load();
                    } catch (IOException ex) {
                        System.out.println(ex); return;
                    }
                    scene = new Scene( root );
                    ManagerOverviewController mcontroller = loader.getController(); mcontroller.initData((Manager) employee, thisBranch );
                    window.setScene( scene ); window.show();
                    break;
                    
                case INSTRUCTOR:
                    loader.setLocation(getClass().getResource("/scenes/instructor/InstructorOverview.fxml"));
                    try {
                        root = loader.load();
                    } catch (IOException ex) {
                        System.out.println(ex); return;
                    }
                    scene = new Scene( root );
                    InstructorOverviewController icontroller = loader.getController(); icontroller.initData((Instructor) employee, thisBranch );
                    window.setScene( scene ); window.show();
                    break;
                    
                case LIBRARIAN:
                    loader.setLocation(getClass().getResource("/scenes/librarian/LibraryOverview.fxml"));
                    try {
                        root = loader.load();
                    } catch (IOException ex) {
                        System.out.println(ex); return;
                    }
                    scene = new Scene( root );
                    LibraryOverviewController lcontroller = loader.getController(); lcontroller.initData((Librarian) employee, thisBranch );
                    window.setScene( scene ); window.show();
                    break;       
            }
        }
    }

    @FXML
    private void eventSelected(MouseEvent event) {
        if( event.getClickCount() == 2 ) {
            LocalEvent e = tableLocalEvents.getSelectionModel().getSelectedItem();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("DetailTextView.fxml"));    
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException ex) {
                System.out.println(ex);
            }
            Scene scene = new Scene( root );
            DetailTextViewController controller = loader.getController(); 
            controller.initData( "\t\t\t" + e.toString() + "\n\n" + e.getEventDetailFull() );
            Stage window = new Stage();
            window.setTitle( "Event Details" );
            window.setScene( scene );
            window.show();
        }
    }

    @FXML
    private void examSelected(MouseEvent event) {
        if( event.getClickCount() == 2 ) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("DetailTextView.fxml"));    
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException ex) {
                System.out.println(ex);
            }
            Scene scene = new Scene( root );
            DetailTextViewController controller = loader.getController(); 
            controller.initData( tableIntExams.getSelectionModel().getSelectedItem().fullDetails() );
            Stage window = new Stage();
            window.setTitle( "Exam Details" );
            window.setScene( scene );
            window.show();
        }
    }
}
