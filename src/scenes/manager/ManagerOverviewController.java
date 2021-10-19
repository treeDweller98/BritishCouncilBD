package scenes.manager;

import britishcouncilbd.BranchNames;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import people.Employee;
import people.EmployeeType;
import people.Manager;
import scenes.DefaultPageController;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class ManagerOverviewController implements Initializable {

    @FXML
    private Label labelBranchTitle;
    @FXML
    private Label loggedInManager;
    @FXML
    private TableView<Employee> employeeList;
    @FXML
    private TableColumn<Employee, String> empNameCol;
    @FXML
    private TableColumn<Employee, Integer> empIDCol;
    @FXML
    private TableColumn<Employee, EmployeeType> empPostCol;
    @FXML
    private TableColumn<Employee, String> empPhoneCol;
    
    private Manager manager;
    private ObservableList<Employee> employees;
    
    public void initData( Manager user, BranchNames branch ) {
        manager = user;
        labelBranchTitle.setText( branch.name() );
        loggedInManager.setText( manager.getName() );
        employees = manager.getEmployeesListToView();
        employeeList.setItems( employees );
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        empNameCol.setCellValueFactory( new PropertyValueFactory<>("name") );
        empIDCol.setCellValueFactory( new PropertyValueFactory<>("id") );
        empPostCol.setCellValueFactory( new PropertyValueFactory<>("post") );
        empPhoneCol.setCellValueFactory( new PropertyValueFactory<>("phoneNumber") );   
    }    

    @FXML
    private void homeButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scenes/DefaultPage.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        
        //access the controller
        DefaultPageController controller = loader.getController();
        controller.initData( manager );
        
        // Show scene in current window
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle( "Welcome " + manager.getName() );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void addEmployeeButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AddNewEmployee.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        
        //access the controller
        AddNewEmployeeController controller = loader.getController();
        controller.initData( manager );
        
        // Show scene in new window
        Stage window = new Stage();
        window.setTitle( "Add New Employee to System" );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void createLocalEventButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("CreateLocalEvent.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        
        //access the controller
        CreateLocalEventController controller = loader.getController();
        controller.initData( manager );
        
        // Show scene in new window
        Stage window = new Stage();
        window.setTitle( "Local Event Creator" );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void intlExamEnableButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("EnableExams.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        
        //access the controller
        EnableExamsController controller = loader.getController();
        controller.initData( manager );
        
        // Show scene in new window
        Stage window = new Stage();
        window.setTitle( "International Exam Registration Enabler" );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void viewEmployeeDetailsBtnOnClick(ActionEvent event) {
        Employee selected = employeeList.getSelectionModel().getSelectedItem();
        if ( selected != null ) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ViewEmployeeDetails.fxml"));

            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException ex) {
                System.out.println(ex);
            }
            Scene scene = new Scene( root );

            //access the controller
            ViewEmployeeDetailsController controller = loader.getController();
            controller.initData( selected );

            // Show scene in new window
            Stage window = new Stage();
            window.setTitle( "Employee Details" );
            window.setScene( scene );
            window.show();
        }
    }

    @FXML
    private void postRemoveNoticeButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("PostRemoveNotices.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        
        //access the controller
        PostRemoveNoticesController controller = loader.getController();
        controller.initData( manager );
        
        // Show scene in new window
        Stage window = new Stage();
        window.setTitle( "Notice Manager" );
        window.setScene( scene );
        window.show();
    } 

    @FXML
    private void refreshBtnOnClick(ActionEvent event) {
        employees = manager.getEmployeesListToView();
        employeeList.setItems( employees );
    }

}
