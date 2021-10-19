package scenes.manager;

import britishcouncilbd.CurrentSystem;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import people.EmployeeType;
import people.Manager;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class AddNewEmployeeController implements Initializable {

    @FXML
    private TextField employeeName;
    @FXML
    private TextField employeePhoneNumber;
    @FXML
    private TextField employeeEmail;
    @FXML
    private TextField employeeAddress;
    @FXML
    private DatePicker employeeDoBpicker;
    @FXML
    private TextField employeeSalary;
    @FXML
    private TextField newEmployeePassword;
    @FXML
    private TextField newEmployeeAuthCode;
    @FXML
    private ComboBox<EmployeeType> userTypeComboBox;
    @FXML
    private Label errorStatusMessageLabel;

    
    private Manager manager;
    private int idNew = 12345;
    
    public void initData( Manager user ){
        manager = user;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        userTypeComboBox.getItems().setAll( EmployeeType.values() );
    }    

    @FXML
    private void addNewEmployeeButtonOnClick(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation Alert");
        a.setContentText("Are you sure you wish to add this employee?");
        Optional<ButtonType> result = a.showAndWait();
        
        String stat = "";
        if(result.get() == ButtonType.OK){
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Requesting Authorisation");
            dialog.setContentText("Enter Authorisation Code to Proceed");
            Optional<String> auth = dialog.showAndWait();
            
            if ( manager.authenticate(auth.get()) ) {
                try {
                    try {
                        idNew = CurrentSystem.generateNewEmployeeID();
                    } catch ( Exception e ) {
                        System.out.println(e);
                        Alert c = new Alert(Alert.AlertType.ERROR);
                        c.setTitle("Database Error");
                        c.setContentText("A database error has occured. Could not finish registration process.");
                        result = c.showAndWait();
                    }
                    
                    stat = manager.addNewEmployee( employeeName.getText(), employeeAddress.getText(), employeeEmail.getText(), employeePhoneNumber.getText(), 
                                                   newEmployeePassword.getText(), Integer.parseInt(employeeSalary.getText()), Integer.parseInt(newEmployeeAuthCode.getText()), 
                                                   employeeDoBpicker.getValue(), userTypeComboBox.getValue(), idNew );
                }
                catch( IllegalArgumentException e ){
                    errorStatusMessageLabel.setText( "Error: please ensure data fields are all properly entered" );
                    System.out.println(e);
                    Alert c = new Alert(Alert.AlertType.ERROR);
                    c.setTitle("Input Error");
                    c.setContentText("please ensure data fields are all properly entered");
                    result = c.showAndWait();
                }

                if( stat == "" );               // executes when caught ^
                else if ( stat == null ){
                    errorStatusMessageLabel.setText( "Database Error: Could not add employee to database.");
                    Alert b = new Alert(Alert.AlertType.ERROR);
                    b.setTitle("Database Error");
                    b.setContentText("Could not add employee to database.");
                    b.showAndWait();
                }
                else {
                    errorStatusMessageLabel.setText( stat );
                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setTitle("Success!");
                    b.setContentText(stat);
                    b.showAndWait();

                    employeeName.clear(); employeePhoneNumber.setText("+880"); employeeEmail.clear(); employeeAddress.clear();
                    employeeSalary.clear(); newEmployeePassword.clear(); newEmployeeAuthCode.clear();         
                }
            } else {
                Alert c = new Alert(Alert.AlertType.ERROR);
                c.setTitle("Failed");
                c.setContentText("Could not authenticate user");
                result = c.showAndWait();
            }  
        }
    }
    
}
