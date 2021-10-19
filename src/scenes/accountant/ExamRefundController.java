package scenes.accountant;

import britishcouncilbd.CurrentSystem;
import finances.FinancialAccountNames;
import finances.TransactionType;
import internationalexams.PendingExamRefund;
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
import people.Accountant;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class ExamRefundController implements Initializable {

    @FXML
    private TableView<PendingExamRefund> pendingExamRefundList;
    @FXML
    private TableColumn<PendingExamRefund, String> examCol;
    @FXML
    private TableColumn<PendingExamRefund, Integer> candidateNumCol;
    @FXML
    private TableColumn<PendingExamRefund, String> candidateNameCol;
    @FXML
    private TableColumn<PendingExamRefund, Integer> amountCol;
    @FXML
    private TextField searchBar;
    @FXML
    private Button searchBtn;
    @FXML
    private TextField receiverName;
    @FXML
    private TextField refundAmount;
    @FXML
    private Button markAsRefundedBtn;
    @FXML
    private Label errorStatusLabel;
    
    private Accountant accountant;
    private ObservableList<PendingExamRefund> refundsList;
    private PendingExamRefund rTemp;
    
    public void initData( Accountant user ) {
        accountant = user;
        refundsList = FXCollections.observableArrayList( PendingExamRefund.fetchPendingExamRefundsFile() );
        pendingExamRefundList.setItems( refundsList );
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        examCol.setCellValueFactory( new PropertyValueFactory<>("examName") );
        candidateNumCol.setCellValueFactory( new PropertyValueFactory<>("candidateNum") );
        candidateNameCol.setCellValueFactory( new PropertyValueFactory<>("candidateName") );
        amountCol.setCellValueFactory( new PropertyValueFactory<>("fee") );
    }    

    @FXML
    private void searchButtonOnClick(ActionEvent event) {
    }

    @FXML
    private void markAsRefundedButtonOnClick(ActionEvent event) {
        
        String receiver = receiverName.getText();
        int paid;
        if ( receiver.isBlank() ) { errorStatusLabel.setText( "You must enter receiver name"); return; }
        try {
            paid = Integer.parseInt( refundAmount.getText() );
            if (paid != rTemp.getFee() ) { errorStatusLabel.setText( "Refund amount must match with table amount"); return; }
        } catch ( NumberFormatException e ) {
            errorStatusLabel.setText( "Refund amount field must contain integers only"); return;
        } 

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Requesting Authorisation");
        dialog.setContentText("Enter Authorisation Code to Proceed");
        Optional<String> auth = dialog.showAndWait();
        boolean stat = false;
        if ( accountant.authenticate(auth.get()) ) {
            stat = accountant.refundExam( rTemp.CANDIDATE_NUM );
            
            if ( stat == false ) {
                errorStatusLabel.setText( "Database error occured");
                Alert c = new Alert(Alert.AlertType.ERROR);
                c.setTitle("Database Error");
                c.setContentText("A database error has occured. Could not record refund.");
                c.showAndWait();
            } else {
                errorStatusLabel.setText("Succesfully refunded " + rTemp.getCandidateName() + ", ID" + rTemp.CANDIDATE_NUM );
                Alert c = new Alert(Alert.AlertType.INFORMATION);
                c.setTitle("Success");
                c.setContentText("Refund Successfully Recorded");
                c.showAndWait();
                
                // generate refund slip
                CurrentSystem.generatePdfFromString( rTemp.generateRefundCompleteSlip( 
                    accountant.getBranch().name(), receiver, accountant.getName(), accountant.getId() ) 
                );
                             
                // record transaction
                String detail = rTemp.getExamName()+ " refund candidate no." + rTemp.CANDIDATE_NUM;
                if ( !accountant.recordTransaction( FinancialAccountNames.INTERNATIONAL_EXAM_REFUND, receiver, detail, paid, TransactionType.EXAM_REFUND ) ) {
                    c = new Alert(Alert.AlertType.ERROR);
                    c.setTitle("Database Error");
                    c.setContentText("Transaction could not be recorded to database - please note down manually.");
                    c.showAndWait();
                }
                
                // clean-up
                refundsList.remove(rTemp); markAsRefundedBtn.setDisable(true); receiverName.clear(); refundAmount.clear();
            }
        } else {
            Alert c = new Alert(Alert.AlertType.ERROR);
            c.setTitle("Failed");
            c.setContentText("Could not authenticate user");
            c.showAndWait();
        }
    }
    
    @FXML
    private void refundSelected(MouseEvent event) {
        rTemp = pendingExamRefundList.getSelectionModel().getSelectedItem();
        markAsRefundedBtn.setDisable(false);
    }
    
}

    
