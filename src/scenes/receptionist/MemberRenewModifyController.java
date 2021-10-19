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
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
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
public class MemberRenewModifyController implements Initializable {

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
    private Label membershipValidityLabel;
    @FXML
    private CheckBox isRenewingCheckBox;
    @FXML
    private Label errorStatusMessageLabel;
    @FXML
    private Label currentMemberID;
    @FXML
    private Label currentDob;
    
    private Receptionist receptionist;
    private Member member;
    private Member temp;            // stores the newly set details
    private String idDocTypeString;
    private boolean renew;
    boolean changedOnce = false;
   
    public void initData( Receptionist user, Member selected ) {
        receptionist = user;
        member = selected;
        currentMemberID.setText( String.valueOf(member.ID) );
        setAllFields( member );
        memberIDDocNum.clear();
    }
    public void setAllFields( Member member ) {
        memberName.setText( member.getName() ); memberPhoneNumber.setText( member.getPhoneNumber() );
        memberEmail.setText( member.getEmail() );
        memberAddress.setText( member.getAddress() );
        memberIDDocNum.setText( member.getIdDocTypeAndNum() );
        membershipValidityLabel.setText( member.getValidTill().toString() );
        currentDob.setText( member.getDateOfBirth().toString() );
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void confirmChangesButtonOnClick(ActionEvent event) {
        if ( changedOnce == true ) {
            errorStatusMessageLabel.setText("You have already changed this member detail");
            return;
        } else if ( memberIDDocNum.getText().isBlank() ) {
            errorStatusMessageLabel.setText("You cannot leave ID document number line empty");
            return;
        } else {
            idDocTypeString += memberIDDocNum.getText();
        }
        
        renew = isRenewingCheckBox.isArmed();
        String renewMessage = null; if(renew) renewMessage = " and renew this user";
        
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation Alert");
        a.setHeaderText("Confirm");
        a.setContentText("Are you sure to you want to commit this changes" + renewMessage + "?");        
        Optional<ButtonType> result = a.showAndWait();
        
        if(result.get() == ButtonType.OK){
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Requesting Authorisation");
            dialog.setContentText("Enter Authorisation Code to Proceed");
            Optional<String> auth = dialog.showAndWait();
           
            if ( receptionist.authenticate(auth.get()) ) {
                try {
                    temp = receptionist.updateMember(
                        memberName.getText(), memberAddress.getText(), memberEmail.getText(), memberDoBpicker.getValue(),
                        memberPhoneNumber.getText(), member.ID, idDocTypeString , member.getJoined(), member.getBorrowedBooks(), 
                        member.getLibraryDues(), renew, member.getValidTill()
                    ); 

                    if ( temp != null ) {
                        member = temp;
                        changedOnce = true; setAllFields(member);
                        errorStatusMessageLabel.setText("Successfully updated member information");

                        if (renew) {
                            renewMessage = " Renewed validity extended to " + member.getValidTill() + ". Please print the generated fee slip.";
                            
                            // record transaction
                            String detail =  "Renewal memberID." + member.ID;
                            if ( !receptionist.recordTransaction( FinancialAccountNames.MEMBERSHIP_FEES, member.getName(), detail, 
                                                                  Member.RENEW_FEE, TransactionType.MEMBER_RENEW ) ) {
                                
                                a = new Alert(Alert.AlertType.ERROR);
                                a.setTitle("Database Error");
                                a.setContentText("Transaction could not be recorded to database - please note down manually.");
                                a.showAndWait();
                            }
                        }
                        Alert c = new Alert(Alert.AlertType.INFORMATION);
                        c.setTitle("Success");
                        c.setContentText("Member has been succesfully updated. " + renewMessage);
                        c.showAndWait();
                        
                        CurrentSystem.generatePdfFromString( 
                                member.generateMemberRenewSlip( receptionist.getName(), receptionist.getBranch().name(), receptionist.getId() ) 
                        );
                        
                    } else {
                        Alert c = new Alert(Alert.AlertType.ERROR);
                        c.setTitle("Database Error");
                        c.setContentText("A database error has occured. Could not update member information");
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
        CurrentSystem.generatePdfFromString(member.generateMemberIdCard());
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
