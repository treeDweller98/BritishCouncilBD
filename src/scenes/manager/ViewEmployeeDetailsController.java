package scenes.manager;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import people.Employee;
import people.Manager;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class ViewEmployeeDetailsController implements Initializable {

    @FXML
    private Label chosenEmployeeID;
    @FXML
    private TextField employeeName;
    @FXML
    private TextField employeeDoBpicker;
    @FXML
    private TextField employeePhoneNumber;
    @FXML
    private TextField employeeEmail;
    @FXML
    private TextField employeeAddress;
    @FXML
    private TextField employeeSalary;
    @FXML
    private TextField employeePassword;
    @FXML
    private TextField employeeAuthCode;
    @FXML
    private TextField empType;
    
    private Employee showing;

    public void initData( Employee emp ) {
        showing = emp;System.out.println(emp.getAddress());
        employeeAddress.setText( showing.getAddress() );
        chosenEmployeeID.setText( String.valueOf(showing.getId() ) );
        employeeName.setText( showing.getName() );
        employeePhoneNumber.setText( showing.getPhoneNumber() );
        employeeEmail.setText( showing.getEmail() );
        employeeAddress.setText( showing.getAddress() );
        employeeDoBpicker.setText( showing.getDateOfBirth().toString() );
        employeeSalary.setText( String.valueOf((showing.getSalary())) );
        employeePassword.setText( showing.getEmpPassword() );
        employeeAuthCode.setText( String.valueOf((showing.getAuthorisationCode())) );
        empType.setText( showing.getPost().name() );
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
