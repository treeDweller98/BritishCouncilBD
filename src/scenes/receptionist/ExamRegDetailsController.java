package scenes.receptionist;

import britishcouncilbd.CurrentSystem;
import internationalexams.Candidate;
import internationalexams.ExamSeries;
import internationalexams.ExamSession;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import people.Receptionist;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class ExamRegDetailsController implements Initializable {

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
    private TextField dob;
    @FXML
    private Button refundRequestBtn;
    @FXML
    private TextArea refundReasonText;
    
    private Receptionist receptionist;
    private Candidate c;
    private ExamSeries examSeries; private int examYear; private ExamSession examSession;
    
    public void initData( Receptionist user, Candidate candidate, int year, ExamSession session, ExamSeries series, LocalDate lastCancelDate ) {
        receptionist = user;
        c = candidate; examSeries = series; examYear = year; examSession = session;
        
        examSeriesLabel.setText( series.name() ); sessionLabel.setText( year + " " + session ); 
        candidateNameTxt.setText( c.getName()); dob.setText( c.getDateOfBirth().toString() ); passportNumTxt.setText( c.getPassportNum() ); 
        emailTxt.setText( c.getEmail()); phoneNumTxt.setText( c.getPhone()); addressTxt.setText( c.getAddress()); 
        candidateNumLabel.setText( String.valueOf(c.getCandidateNumber()) );
        venueLabel.setText( c.getVenueChosen() ); subjectCodesList.setText( Arrays.toString(c.getSubjectsRegistered()) );
        
        if ( LocalDate.now().isAfter( lastCancelDate ) ) {
            refundReasonText.setText("EXAM REGISTRATION REFUND PERIOD HAS PASSED."); refundReasonText.setEditable(false);
            refundRequestBtn.setDisable(true);
        }
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Requesting Authorisation");
        dialog.setContentText("Enter Authorisation Code to Proceed");
        Optional<String> auth = dialog.showAndWait();
        
        boolean stat = false;
        if ( receptionist.authenticate(auth.get()) ) {
         
            stat = receptionist.applyForExamRefund(examSeries, examSession, examYear, c, refundReasonText.getText() );
            
            if ( stat == false ) {
                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setTitle("Database Error");
                b.setContentText("A database error has occured. Could not finish refund application process.");
                b.showAndWait();
            } else {
                Alert b = new Alert(Alert.AlertType.INFORMATION);
                b.setTitle("Success");
                b.setContentText("Refund application has been successfully recorded");
                b.showAndWait();
                
                refundRequestBtn.setDisable(true);
                CurrentSystem.generatePdfFromString( 
                    c.generateRefundApplicationSlip(  
                        (examSeries+" "+examSession+" "+examYear), 
                        receptionist.getName(), receptionist.getId() 
                    ) 
                ); 
            }
        } else {
            Alert c = new Alert(Alert.AlertType.ERROR);
            c.setTitle("Failed");
            c.setContentText("Could not authenticate user");
            c.showAndWait();
        }
    }
}
