package scenes.receptionist;

import britishcouncilbd.CurrentSystem;
import finances.FinancialAccountNames;
import finances.TransactionType;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import people.Member;
import people.Receptionist;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class NewMemberRegController implements Initializable {

    @FXML
    private TextField memberName;
    @FXML
    private TextField memberPhoneNumber;
    @FXML
    private TextField memberEmail;
    @FXML
    private TextField memberAddress;
    @FXML
    private DatePicker memberDoBpicker;
    @FXML
    private RadioButton memberIDtypeNID;
    @FXML
    private ToggleGroup idDocType;
    @FXML
    private RadioButton memberIDtypePassport;
    @FXML
    private TextField memberIDDocNum;
    @FXML
    private Label errorStatusMessageLabel;
    @FXML
    private Button generateIDBtn;
    
    private Receptionist receptionist;
    private Member newMember;
    private boolean addedOnce;
    private String idDocTypeString;
    int idNew;
    
    public void initData( Receptionist user ) {
        receptionist = user;
    } 

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void newMemberRegButtonOnClick(ActionEvent event) {
        if ( addedOnce == true ) {
            errorStatusMessageLabel.setText("You may only add one member at a time");
            return;
        } else if ( memberIDDocNum.getText().isBlank() ) {
            errorStatusMessageLabel.setText("You cannot leave ID document number line empty");
            return;
        } else {
            idDocTypeString += memberIDDocNum.getText();
        }
        
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation Alert");
        a.setHeaderText("Confirm");
        a.setContentText("Are you sure to you want to add this member?");        
        Optional<ButtonType> result = a.showAndWait();
        
        if(result.get() == ButtonType.OK){
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Requesting Authorisation");
            dialog.setContentText("Enter Authorisation Code to Proceed");
            Optional<String> auth = dialog.showAndWait();
            
            if ( receptionist.authenticate(auth.get()) ) {
                try {
                    try {
                        idNew = CurrentSystem.generateNewMemberID();
                    } catch ( Exception e ) {
                        System.out.println(e);
                        Alert c = new Alert(Alert.AlertType.ERROR);
                        c.setTitle("Database Error");
                        c.setContentText("A database error has occured. Could not finish registration process.");
                        c.showAndWait();
                    }
                    
                    newMember = receptionist.newMemberRegistration(
                        memberName.getText(), memberAddress.getText(), memberEmail.getText(), memberDoBpicker.getValue(),
                        memberPhoneNumber.getText(), idNew, idDocTypeString
                    );
                    
                    if ( newMember != null ) {
                        addedOnce = true;
                        errorStatusMessageLabel.setText("Success!" + newMember.ID + "has been registered. Validity till " + newMember.getValidTill().toString() );
                        generateIDBtn.setDisable(false);
                        
                        Alert c = new Alert(Alert.AlertType.INFORMATION);
                        c.setTitle("Success");
                        c.setContentText("Member" + newMember.ID + "has been succesfully registered. Please print the generated fee slip");
                        c.showAndWait();
                        
                        CurrentSystem.generatePdfFromString( newMember.generateNewMemberRegSlip( receptionist.getName(), receptionist.getBranch().name(),
                                                                                                    receptionist.getId() ) );
                        
                        String detail =  "Registration memberID." + newMember.ID;
                        if ( !receptionist.recordTransaction( FinancialAccountNames.MEMBERSHIP_FEES, newMember.getName(), detail, 
                                                              Member.REG_FEE, TransactionType.MEMBER_REG) ) {

                            a = new Alert(Alert.AlertType.ERROR);
                            a.setTitle("Database Error");
                            a.setContentText("Transaction could not be recorded to database - please note down manually.");
                            a.showAndWait();
                        }
                        
                    } else {
                        Alert c = new Alert(Alert.AlertType.ERROR);
                        c.setTitle("Database Error");
                        c.setContentText("A database error has occured. Could not finish registration process.");
                        c.showAndWait();
                    }
                } catch ( IllegalArgumentException e) {
                    System.out.println(e);
                    errorStatusMessageLabel.setText( "input error - please ensure all fields are properly entered");
                }    
            } else {
                Alert c = new Alert(Alert.AlertType.ERROR);
                c.setTitle("Failed");
                c.setContentText("Could not authenticate user");
                c.showAndWait();
            }
        }
    }

    @FXML
    private void generateIDcardButtonOnClick(ActionEvent event) {
        // generate PDF
        CurrentSystem.generatePdfFromString(newMember.generateMemberIdCard());
    }

    @FXML
    private void nidSelected(ActionEvent event) {
        idDocTypeString = "NID:";
    }

    @FXML
    private void passportSelected(ActionEvent event) {
        idDocTypeString = "Passport:";
    }
    
}
