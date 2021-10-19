package scenes.receptionist;

import internationalexams.ExamCertificate;
import internationalexams.ExamSeries;
import internationalexams.ExamSession;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import people.Receptionist;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class QueryCertificatesController implements Initializable {

    
    private Receptionist receptionist;
    @FXML
    private Button markAsHandedOverBtn;
    @FXML
    private ComboBox<ExamSeries> examSeriesComboBox;
    @FXML
    private ComboBox<ExamSession> sessionComboBox;
    @FXML
    private TextField candidateNumber;
    @FXML
    private TextField examYear;
    @FXML
    private Label availabilityStatLabel;
    
    ExamCertificate ec = null;
    public void initData( Receptionist user ) {
        receptionist = user;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        examSeriesComboBox.getItems().addAll( ExamSeries.values() );
        sessionComboBox.getItems().addAll( ExamSession.values() );
    }    

    @FXML
    private void searchButtonOnClick(ActionEvent event) {
        try {
            ec = receptionist.queryCertificate(
                examSeriesComboBox.getValue(), sessionComboBox.getValue(), 
                Integer.parseInt( examYear.getText() ), Integer.parseInt( candidateNumber.getText() )        
            );
        } catch ( Exception e ) {
            System.out.println(e); availabilityStatLabel.setText("Error: ensure all fields are correctly entered");
        }
        
        if ( ec == null ) {
            availabilityStatLabel.setText("Not Found");
        } else {
            availabilityStatLabel.setText( ec.toString() ); markAsHandedOverBtn.setDisable(false);
        }
    }

    @FXML
    private void markAsHandedOverButtonOnClick(ActionEvent event) {
        try {
            ec = receptionist.markCertificateAsHandedOver(
                examSeriesComboBox.getValue(), sessionComboBox.getValue(), 
                Integer.parseInt( examYear.getText() ), Integer.parseInt( candidateNumber.getText() )        
            );
        } catch ( Exception e ) {
            System.out.println(e); availabilityStatLabel.setText("Error: ensure all fields are correctly entered");
        }
        
        if ( ec == null ) {
            availabilityStatLabel.setText("Failed to record to database");
        } else {
            availabilityStatLabel.setText( ec.toString() ); markAsHandedOverBtn.setDisable(true);
        }
    }
    
}
