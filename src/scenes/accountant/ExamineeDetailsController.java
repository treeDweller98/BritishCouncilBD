package scenes.accountant;

import internationalexams.Candidate;
import internationalexams.ExamSeries;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class ExamineeDetailsController implements Initializable {

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
   
    private Candidate c;
    private ExamSeries examSeries;
    private String sessionAndYear;
    
    
    public void initData( Candidate candidate, String sessionYear, ExamSeries series ) {
        c = candidate; examSeries = series; sessionAndYear = sessionYear;
        
        examSeriesLabel.setText( series.name() ); sessionLabel.setText( sessionYear ); 
        candidateNameTxt.setText( c.getName()); dob.setText( c.getDateOfBirth().toString() ); passportNumTxt.setText( c.getPassportNum() ); 
        emailTxt.setText( c.getEmail()); phoneNumTxt.setText( c.getPhone()); addressTxt.setText( c.getAddress()); 
        candidateNumLabel.setText( String.valueOf(c.getCandidateNumber()) );
        venueLabel.setText( c.getVenueChosen() ); subjectCodesList.setText( Arrays.toString(c.getSubjectsRegistered()) );
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
