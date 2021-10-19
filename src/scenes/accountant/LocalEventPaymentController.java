package scenes.accountant;

import britishcouncilbd.CurrentSystem;
import finances.FinancialAccountNames;
import finances.TransactionType;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import localevents.LocalEventRegistration;
import people.Accountant;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class LocalEventPaymentController implements Initializable {

    @FXML
    private TableView<LocalEventRegistration> pendingEventFeeTable;
    @FXML
    private TableColumn<LocalEventRegistration, String> eventNameCol;
    @FXML
    private TableColumn<LocalEventRegistration, Integer> idCol;
    @FXML
    private TableColumn<LocalEventRegistration, String> memberNameCol;
    @FXML
    private TableColumn<LocalEventRegistration, Integer> amountCol;
    @FXML
    private TextField searchBar;
    @FXML
    private Button searchBtn;
    @FXML
    private TextField payerName;
    @FXML
    private TextField paidAmount;
    @FXML
    private Button markAsPaidBtn;
    @FXML
    private Label errorStatusLabel;
    
    private Accountant accountant;
    private ObservableList<LocalEventRegistration> regList;
    private LocalEventRegistration regTemp;
    
    public void initData( Accountant user ) {
        accountant = user;
        regList = FXCollections.observableArrayList( LocalEventRegistration.fetchLocalEventsPendingPaymentFile( accountant.getBranch() ) );
        pendingEventFeeTable.setItems( regList );
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        eventNameCol.setCellValueFactory( new PropertyValueFactory<>("event") );
        idCol.setCellValueFactory( new PropertyValueFactory<>("id") );
        memberNameCol.setCellValueFactory( new PropertyValueFactory<>("name") );
        amountCol.setCellValueFactory( new PropertyValueFactory<>("fee") );
    }    

    @FXML
    private void searchButtonOnClick(ActionEvent event) {
    }

    @FXML
    private void markAsPaidButtonOnClick(ActionEvent event) {
        boolean stat = false; 
        String payer = payerName.getText();
        int paid;
        if ( payer.isBlank() ) { errorStatusLabel.setText( "You must enter payer name"); return; }
        try {
            paid = Integer.parseInt( paidAmount.getText() );
            if ( paid != regTemp.FEE ) { errorStatusLabel.setText( "Paid amount and fee must match"); return; }
        } catch ( NumberFormatException e ) {
            errorStatusLabel.setText( "Paid amount field must contain integers only"); return;
        } 
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Requesting Authorisation");
        dialog.setContentText("Enter Authorisation Code to Proceed");
        Optional<String> auth = dialog.showAndWait();

        if ( accountant.authenticate(auth.get()) ) {
            stat = accountant.validateLocalEventReg(regTemp);
            
            if ( stat == false ) {
                errorStatusLabel.setText( "Database error occured");
                Alert c = new Alert(Alert.AlertType.ERROR);
                c.setTitle("Database Error");
                c.setContentText("A database error has occured. Could not finish Validation procedure.");
                c.showAndWait();
            } else {
                errorStatusLabel.setText("Successfully validated member" + regTemp.NAME + "'s registration for " + regTemp.EVENT );
                Alert c = new Alert(Alert.AlertType.INFORMATION);
                c.setTitle("Success");
                c.setContentText("Member's event registration has been successfully validated. A seat has been reserved.");
                c.showAndWait();
                
                // generate reg-paid slip
                CurrentSystem.generatePdfFromString( regTemp.generatePaidRegSlip( accountant.getBranch(), payer, accountant.getName(), accountant.getId() ) );
 
                // record transaction
                TransactionType type = ( regTemp.TYPE.equals("Course") ) ? TransactionType.MEMBER_COURSE : TransactionType.MEMBER_EVENT; 
                String detail =  regTemp.TYPE + " " + regTemp.EVENT + " reg. memberID " + regTemp.ID;
                if ( !accountant.recordTransaction( FinancialAccountNames.LOCAL_EVENT_FEES, payer, detail, paid, type ) ) {
                    c = new Alert(Alert.AlertType.ERROR);
                    c.setTitle("Database Error");
                    c.setContentText("Transaction could not be recorded to database - please note down manually.");
                    c.showAndWait();
                }
                
                // clean-up
                regList.remove(regTemp); markAsPaidBtn.setDisable(true); payerName.clear(); paidAmount.clear();
                
            }
            
            
        } else {
            Alert c = new Alert(Alert.AlertType.ERROR);
            c.setTitle("Failed");
            c.setContentText("Could not authenticate user");
            c.showAndWait();
        }
    }

    @FXML
    private void regSelected(MouseEvent event) {
        regTemp = pendingEventFeeTable.getSelectionModel().getSelectedItem();
        markAsPaidBtn.setDisable(false);
    }
    
}
