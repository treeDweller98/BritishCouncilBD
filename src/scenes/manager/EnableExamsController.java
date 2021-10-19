package scenes.manager;

import internationalexams.ExamSeries;
import internationalexams.ExamSession;
import internationalexams.ExamSubject;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import people.Manager;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class EnableExamsController implements Initializable {

    @FXML
    private DatePicker regBeginsDatePicker;
    @FXML
    private DatePicker regEndsDatePicker;
    @FXML
    private DatePicker regCancelDatePicker;
    @FXML
    private ComboBox<ExamSeries> examSeriesComboBox;
    @FXML
    private ComboBox<ExamSession> examSessionComboBox;
    @FXML
    private TextField examYear;
    @FXML
    private TextField subjectCodeField;
    @FXML
    private TextField subjectTitleField;
    @FXML
    private TextField subjectFeeField;
    @FXML
    private TextArea allSubjectsList;
    @FXML
    private TextArea examVenuesList;
    @FXML
    private TextField examVenuesField;
    @FXML
    private Label errorMessageLabel;

    private Manager manager;
    ArrayList< ExamSubject > subjects = new ArrayList<>();
    ArrayList<String> venues = new ArrayList<>();
    
    public void initData( Manager user ){
        manager = (Manager) user;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        examSeriesComboBox.getItems().setAll( ExamSeries.values() );
        examSessionComboBox.getItems().setAll( ExamSession.values() ); 
    }    

    @FXML
    private void enableRegButtonOnClick(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation Alert");
        a.setHeaderText("Confirm");
        a.setContentText("Are you sure you want to enable this exam?");        
        Optional<ButtonType> result = a.showAndWait();
        
        String stat = "";
        if(result.get() == ButtonType.OK){
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Requesting Authorisation");
            dialog.setContentText("Enter Authorisation Code to Proceed");
            Optional<String> auth = dialog.showAndWait();
            
            if ( manager.authenticate(auth.get()) ) {
                try {
                    stat = manager.enableExam( 
                                examSeriesComboBox.getValue(), examSessionComboBox.getValue(), Integer.parseInt( examYear.getText() ),
                                venues, subjects, regBeginsDatePicker.getValue(), regEndsDatePicker.getValue(), regCancelDatePicker.getValue()
                    );
                }
                catch( IllegalArgumentException e ){
                    errorMessageLabel.setText( "Error: please ensure data fields are all properly entered" );
                    System.out.println(e);
                    Alert c = new Alert(Alert.AlertType.ERROR);
                    c.setTitle("Input Error");
                    c.setContentText("please ensure data fields are all properly entered");
                    c.showAndWait();
                }

                if( stat == "" );
                else if ( stat == null ){
                    errorMessageLabel.setText( "Database Error: Could not add Exam to database.");
                    Alert b = new Alert(Alert.AlertType.ERROR);
                    b.setTitle("Database Error");
                    b.setContentText("Could not add Exam to database.");
                    b.showAndWait();
                }
                else {
                    errorMessageLabel.setText( stat );
                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setTitle("Success!");
                    b.setContentText(stat);
                    b.showAndWait();

                    examYear.clear(); venues.clear(); subjects.clear();
                    examVenuesList.clear(); allSubjectsList.clear();         
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
    private void addSubjectButtonOnClick(ActionEvent event) {
        String code = subjectCodeField.getText(), 
               title = subjectTitleField.getText(), 
               fee = subjectFeeField.getText();
        
        if ( !code.isBlank() && !title.isBlank()&& !fee.isBlank() ) {
            try {
                ExamSubject sub = new ExamSubject ( code, title, Integer.parseInt(fee) );
                subjects.add( sub );

                allSubjectsList.appendText( sub.toString() + "\n" );
                subjectCodeField.clear(); subjectTitleField.clear(); subjectFeeField.clear();
                errorMessageLabel.setText("");
            }
            catch( Exception e ){
                errorMessageLabel.setText( "Error: please ensure data fields of the subject is properly entered" );
            }
        } else {
            errorMessageLabel.setText( "Error: please ensure data fields of the subject is properly entered" );
        }
    }

    @FXML
    private void addVenueButtonOnClick(ActionEvent event) {
        String venueName = examVenuesField.getText();
        
        if( !venueName.isBlank() ) {
            venues.add( venueName );
            examVenuesList.appendText( venueName + "\n" );
            examVenuesField.clear();
        }
        else {
            errorMessageLabel.setText( "You must enter a venue name before adding" );
        }
    }
    
}

                
