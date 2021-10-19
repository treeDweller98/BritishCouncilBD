package scenes.instructor;

import internationalexams.Candidate;
import internationalexams.CandidateSeekingRefundApproval;
import internationalexams.ExamSeries;
import internationalexams.ExamSession;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import people.Instructor;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class ExamCancellationDetailsController implements Initializable {

    @FXML
    private TextField candidateNameTxt;
    @FXML
    private TextField passportNumTxt;
    @FXML
    private TextField emailTxt;
    @FXML
    private TextField phoneNumTxt;
    @FXML
    private TextField addressTxt;
    @FXML
    private TextField dob;
    @FXML
    private Label examSeriesLabel;
    @FXML
    private Label sessionLabel;
    @FXML
    private Label venueLabel;
    @FXML
    private TextArea subjectCodesList;
    @FXML
    private Label candidateNumLabel;
    @FXML
    private Button refundRequestBtn;
    @FXML
    private Button denyRequestBtn;
    @FXML
    private TextArea refundReasonText;

    private Instructor instructor;
    private Candidate c;
    private ExamSeries examSeries; private int examYear; private ExamSession examSession;
    private CandidateSeekingRefundApproval refundeeToApprove;
    ObservableList<CandidateSeekingRefundApproval> listFromPreviousPage;
    
    public void initData( Instructor user, CandidateSeekingRefundApproval refundee, ObservableList<CandidateSeekingRefundApproval> list ) {
        instructor = user;
        listFromPreviousPage = list;
        c = refundee.getCandidate(); refundeeToApprove = refundee;
        
        examSeries = refundee.SERIES; examYear = refundee.YEAR; examSession = refundee.SESSION;
        
        examSeriesLabel.setText( examSeries.name() ); sessionLabel.setText( examYear + " " + examSession ); 
        candidateNameTxt.setText( c.getName()); dob.setText( c.getDateOfBirth().toString() ); passportNumTxt.setText( c.getPassportNum() ); 
        emailTxt.setText( c.getEmail()); phoneNumTxt.setText( c.getPhone()); addressTxt.setText( c.getAddress()); 
        candidateNumLabel.setText( String.valueOf(c.getCandidateNumber()) );
        venueLabel.setText( c.getVenueChosen() ); subjectCodesList.setText( Arrays.toString(c.getSubjectsRegistered()) );
        refundReasonText.setText( refundee.getReasons() );
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void refundRequestBtnOnClick(ActionEvent event) {
        boolean stat = false;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Requesting Authorisation");
        dialog.setContentText("Enter Authorisation Code to Proceed with Refund Request Approval");
        Optional<String> auth = dialog.showAndWait();
       
        if ( instructor.authenticate(auth.get()) ) {
            stat = instructor.approveRefundRequest( refundeeToApprove );
            
            if ( stat == false ) {
                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setTitle("Database Error");
                b.setContentText("A database error has occured. Could not finish refund approval process.");
                b.showAndWait();
            } else {
                Alert b = new Alert(Alert.AlertType.INFORMATION);
                b.setTitle("Success");
                b.setContentText("Refund has been successfully recorded as 'Approved'");
                b.showAndWait();
                
                listFromPreviousPage.remove( refundeeToApprove );
                refundRequestBtn.setDisable(true); denyRequestBtn.setDisable(true);
            }
        } else {
            Alert c = new Alert(Alert.AlertType.ERROR);
            c.setTitle("Failed");
            c.setContentText("Could not authenticate user");
            c.showAndWait();
        }
    }

    @FXML
    private void denyRequestBtnOnClick(ActionEvent event) {
        boolean stat = false;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Requesting Authorisation");
        dialog.setContentText("Enter Authorisation Code to Proceed with Refund Request Disapproval");
        Optional<String> auth = dialog.showAndWait();

        if ( instructor.authenticate(auth.get()) ) {   
            stat = instructor.disapproveRefundRequest( refundeeToApprove );
            
            if ( stat == false ) {
                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setTitle("Database Error");
                b.setContentText("A database error has occured. Could not finish refund disapproval process.");
                b.showAndWait();
            } else {
                Alert b = new Alert(Alert.AlertType.INFORMATION);
                b.setTitle("Success");
                b.setContentText("Refund request has been successfully rejected.");
                b.showAndWait();
                
                listFromPreviousPage.remove( refundeeToApprove );
                refundRequestBtn.setDisable(true); denyRequestBtn.setDisable(true);
            }
        } else {
            Alert c = new Alert(Alert.AlertType.ERROR);
            c.setTitle("Failed");
            c.setContentText("Could not authenticate user");
            c.showAndWait();
        }
    }
}
