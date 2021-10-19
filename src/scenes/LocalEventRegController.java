package scenes;

import britishcouncilbd.BranchNames;
import britishcouncilbd.CurrentSystem;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import localevents.LocalEvent;
import localevents.LocalEventRegistration;
import people.Member;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class LocalEventRegController implements Initializable {

    @FXML
    private TextField fieldMemberID;
    @FXML
    private Label memberValidityStatusLabel;
    @FXML
    private Button regButton;
    @FXML
    private ComboBox<LocalEvent> boxEventChoices;
    @FXML
    private TextArea eventDetailsText;
    @FXML
    private Label branchName;
        
    private Member member;
    private BranchNames thisBranch;
    private LocalEvent chosenEvent;
    private ObservableList<LocalEvent> events;

    
    public void initData( BranchNames branch ) {
        thisBranch = branch;
        branchName.setText( thisBranch.name() );
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void createLocalEventRegOnClick(ActionEvent event) {
        LocalEventRegistration reg = LocalEvent.registerForLocalEvent(
                thisBranch, chosenEvent.getClass().getSimpleName(), chosenEvent.getEventName(), chosenEvent.getEventFee(),
                member.ID, member.getName()
        );
        
        if ( reg == null ) {
            Alert b = new Alert(Alert.AlertType.ERROR);
            b.setTitle("Database Error");
            b.setContentText("Could not finish registration process.");
            b.showAndWait();
        } else {
            events.remove( chosenEvent );
            Alert b = new Alert(Alert.AlertType.INFORMATION);
            b.setTitle("Success!");
            b.setContentText("You have been successfully registered. Please print the reg. slip.");
            b.showAndWait();
            CurrentSystem.generatePdfFromString( reg.generatePendingRegSlip( thisBranch ) );
        }
    }

    @FXML
    private void memberSearched(ActionEvent event) {
        boxEventChoices.setDisable(true); regButton.setDisable(true);
        
        try {
            member = Member.searchMemberV2( Integer.parseInt( fieldMemberID.getText() ) );
        } catch ( NumberFormatException ne ) {
            memberValidityStatusLabel.setText( "Input Error: you may only enter digits.");
        }
        
        if ( member == null ) { 
            memberValidityStatusLabel.setText( "Could not find member with that ID. Please recheck." );
        } else {
            if ( LocalDate.now().isBefore( member.getValidTill() ) ) {
                memberValidityStatusLabel.setText( "Name: " + member.getName() + "\nMembership valid till " + member.getValidTill() );
                
                boxEventChoices.setDisable(false);
                events =  LocalEvent.fetchAllLocalEventListToView( thisBranch ); 
                boxEventChoices.getItems().addAll( events );
            } else {
                memberValidityStatusLabel.setText( "Name: " + member.getName() + "\nMembership expired on " + member.getValidTill() + 
                                                    "\nMEMBER CAN NOT REGISTER UNTILL MEMBERSHIP IS RENEWED");
            }
        }
    }

    @FXML
    private void eventChosen(ActionEvent event) {
        chosenEvent = boxEventChoices.getValue();
        
        if ( chosenEvent.hasCapacity() ) {
            regButton.setDisable(false);
        }
        else {
            eventDetailsText.setText( "EXCEEDED CAPACITY - CANNOT REGISTER\n" );
        }
        
        eventDetailsText.appendText( chosenEvent.getEventDetailFull() );  
    }
}
