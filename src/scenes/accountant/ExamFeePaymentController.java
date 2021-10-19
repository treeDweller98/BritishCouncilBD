package scenes.accountant;

import britishcouncilbd.CurrentSystem;
import finances.FinancialAccountNames;
import finances.TransactionType;
import internationalexams.Candidate;
import internationalexams.Exam;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import people.Accountant;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class ExamFeePaymentController implements Initializable {

    @FXML
    private TableView<Candidate> pendingExamFeeList;
    @FXML
    private TableColumn<Candidate, Integer> candNumCol;
    @FXML
    private TableColumn<Candidate, String> candNameCol;
    @FXML
    private TableColumn<Candidate, Integer> amountCol;
    @FXML
    private TableColumn<Candidate, LocalDate> dateAddedCol;
    @FXML
    private TextField payerName;
    @FXML
    private TextField paidAmount;
    @FXML
    private Label errorStatusLabel;
    @FXML
    private TextField searchBar;
    @FXML
    private Button searchBtn;
    @FXML
    private ComboBox<Exam> examSelector;
    @FXML
    private Button payBtn;
     
    private Accountant accountant;
    private Exam exam;
    private Candidate cTemp;
    private ObservableList<Candidate> candidateList;
   
    public void initData( Accountant user ){
        accountant = user;     
        examSelector.setItems( Exam.getAllExamsToView() );
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        candNumCol.setCellValueFactory( new PropertyValueFactory<>("candidateNum") );
        candNameCol.setCellValueFactory( new PropertyValueFactory<>("candidateName") );
        amountCol.setCellValueFactory( new PropertyValueFactory<>("fee") );
        dateAddedCol.setCellValueFactory( new PropertyValueFactory<>("regdate") );
    }    

    @FXML
    private void markAsPaidButtonOnClick(ActionEvent event) {
        String payer = payerName.getText();
        int paid;
        if ( payer.isBlank() ) { errorStatusLabel.setText( "You must enter payer name"); return; }
        try {
            paid = Integer.parseInt( paidAmount.getText() );
            if (paid != cTemp.getFee() ) { errorStatusLabel.setText( "Paid amount and fee must match"); return; }
        } catch ( NumberFormatException e ) {
            errorStatusLabel.setText( "Paid amount field must contain integers only"); return;
        } 
        
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Requesting Authorisation");
        dialog.setContentText("Enter Authorisation Code to Proceed");
        Optional<String> auth = dialog.showAndWait();

        if ( accountant.authenticate(auth.get()) ) {
        
            Candidate candidate = accountant.validateCandidateRegistration( 
                    exam.series, exam.session, exam.year, 
                    pendingExamFeeList.getSelectionModel().getSelectedItem().getCandidateNumber() 
            );
            
            if ( candidate == null ) {
                errorStatusLabel.setText( "Database error occured");
                Alert c = new Alert(Alert.AlertType.ERROR);
                c.setTitle("Database Error");
                c.setContentText("A database error has occured. Could not finish Validation procedure.");
                c.showAndWait();
            } else {
                errorStatusLabel.setText("Succesfully validated " + candidate.getName() + ", ID" + candidate.getCandidateNumber() );
                Alert c = new Alert(Alert.AlertType.INFORMATION);
                c.setTitle("Success");
                c.setContentText("Candidate has been successfully validated. A seat has been reserved.");
                c.showAndWait();
                
                // generate slip
                CurrentSystem.generatePdfFromString( candidate.generateValidatedRegSlip( exam.getFullNameOfExam(), payer , accountant.getName() , accountant.getId()) );

                //make payment record
                String detail = exam.getFullNameOfExam() + " reg candidate no." + cTemp.CANDIDATE_NUM;
                if ( !accountant.recordTransaction( FinancialAccountNames.INTERNATIONAL_EXAM_PAYMENT, payer, detail, paid, TransactionType.EXAM_PAY ) ) {
                    c = new Alert(Alert.AlertType.ERROR);
                    c.setTitle("Database Error");
                    c.setContentText("Transaction could not be recorded to database - please note down manually.");
                    c.showAndWait();
                }
                
                // clean-up
                candidateList.remove(cTemp); payBtn.setDisable(true); payerName.clear(); paidAmount.clear();
            }
                       
        } else {
            Alert c = new Alert(Alert.AlertType.ERROR);
            c.setTitle("Failed");
            c.setContentText("Could not authenticate user");
            c.showAndWait();
        }
    }

    @FXML
    private void searchButtonOnClick(ActionEvent event) {
    }

    @FXML
    private void examSelectedOnAction(ActionEvent event) {
        exam = examSelector.getValue();
        candidateList = FXCollections.observableArrayList( exam.getAwaitingPayment() );
        pendingExamFeeList.setItems( candidateList );
    }

    @FXML
    private void canidateClicked(MouseEvent event) {
        cTemp = pendingExamFeeList.getSelectionModel().getSelectedItem();
        payBtn.setDisable(false);
        
        if ( event.getClickCount() == 2 ) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ExamineeDetails.fxml"));

            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException ex) {
                System.out.println(ex);
            }
            Scene scene = new Scene( root );

            ExamineeDetailsController controller = loader.getController();
            controller.initData( cTemp, exam.getFullNameOfExam(), exam.series );

            Stage window = new Stage();
            window.setTitle( "Examinee Details" );
            window.setScene( scene );
            window.show();
        }
    }
    
}
