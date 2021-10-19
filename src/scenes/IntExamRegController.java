package scenes;

import britishcouncilbd.CurrentSystem;
import internationalexams.Candidate;
import internationalexams.Exam;
import internationalexams.ExamSubject;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class IntExamRegController implements Initializable {

    @FXML
    private ComboBox<Exam> examSeriesComboBox;
    @FXML
    private ComboBox<String> venueComboBox;
    @FXML
    private ComboBox<ExamSubject> subjectsListComboBox;
    @FXML
    private Label grandTotalFeesLabel;
    @FXML
    private Button regButton;
    @FXML
    private Button removeButton;
    @FXML
    private TextField candidateNameTxt;
    @FXML
    private DatePicker candidateDoBPicker;
    @FXML
    private TextField passportNumTxt;
    @FXML
    private TextField emailTxt;
    @FXML
    private TextField phoneNumTxt;
    @FXML
    private TextField addressTxt;
    @FXML
    private Label errorMessagelabel;
    @FXML
    private TableView<ExamSubject> regSubjectsTable;
    @FXML
    private TableColumn<ExamSubject, String> subjectDetailCol;
    @FXML
    private TableColumn<ExamSubject, Integer> feeCol;
    
    private ObservableList<Exam> allExams;
    private ObservableList<ExamSubject> subsChosen;
    private ExamSubject tempSub;
    private int feeTotal = 0;
    private Exam examChosen;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        allExams = Exam.getAllExamsToView();
        examSeriesComboBox.getItems().addAll(allExams);
        subsChosen = FXCollections.observableArrayList();

        subjectDetailCol.setCellValueFactory( new PropertyValueFactory<>("subName") );
        feeCol.setCellValueFactory( new PropertyValueFactory<>("fee") );
    }    

    @FXML
    private void examSeriesSelected(ActionEvent event) {
         examChosen = examSeriesComboBox.getValue();
         venueComboBox.getItems().setAll( examChosen.getVenues() );
    }

    @FXML
    private void examVenueSelected(ActionEvent event) {
        subjectsListComboBox.getItems().setAll( examSeriesComboBox.getValue().getSubjectsOffered() );
    }
    
    @FXML
    private void registerButtonOnClick(ActionEvent event) {

        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation Alert");
        a.setContentText("Are you sure you wish to register? Ensure all details provided are correct. "
                            + "British Council Bangladesh takes no responsibility for candidates selecting the incorrect examination/subjects.");
        Optional<ButtonType> result = a.showAndWait();
        
        if(result.get() == ButtonType.OK){ 
            
            String[] candidateSubCodes = new String[ subsChosen.size() ]; int i = 0;
            for ( ExamSubject s : subsChosen ) {
                candidateSubCodes[i] = s.CODE; i++;
            }

            int id = CurrentSystem.generateNewCandidateNumber();

            Candidate candidate = null;
            try {
                candidate = Exam.registerForExam (
                    examChosen.series, examChosen.year, examChosen.session,  candidateNameTxt.getText(), passportNumTxt.getText(), emailTxt.getText(),
                    phoneNumTxt.getText(), addressTxt.getText(), candidateDoBPicker.getValue(), id, candidateSubCodes, venueComboBox.getValue(), feeTotal
                );
            } catch ( IllegalArgumentException e ) {
                errorMessagelabel.setText( "Please ensure all fields have been properly filled with the correct information");
            }

            if ( candidate == null ) {
                errorMessagelabel.setText( "Failed");
                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setTitle("Database Error");
                b.setContentText("Could not finish registration process.");
                b.showAndWait();
            } else {
                errorMessagelabel.setText( "Success! Please print the registration form and complete payment.");
                Alert b = new Alert(Alert.AlertType.INFORMATION);
                b.setTitle("Successfully Registered");
                b.setContentText( "Candidate number " + id + " has been successfully registered. Awaiting payment.");
                b.showAndWait();
                
                regButton.setDisable(true); removeButton.setDisable( true );
                examSeriesComboBox.setDisable(true); venueComboBox.setDisable(true); subjectsListComboBox.setDisable(true);
                
                CurrentSystem.generatePdfFromString( candidate.generateRegSlip( examChosen.getFullNameOfExam(), examChosen.getRegEnd(), subsChosen ) );
            }
        }
    }
    
    @FXML
    private void subjectSelected(ActionEvent event) {
        tempSub = subjectsListComboBox.getValue();
        subsChosen.add( tempSub );
        feeTotal += tempSub.FEE;
        grandTotalFeesLabel.setText( String.valueOf(feeTotal) );
        regSubjectsTable.getItems().setAll(subsChosen);
        regButton.setDisable( false ); removeButton.setDisable( false );
    }
    @FXML
    private void removeBtnOnClick(ActionEvent event) {
        tempSub = regSubjectsTable.getSelectionModel().getSelectedItem();
        subsChosen.remove( tempSub );
        feeTotal -= tempSub.FEE;
        grandTotalFeesLabel.setText( String.valueOf(feeTotal) );
        
        if ( subsChosen.size() == 0 ) {
            regButton.setDisable( true ); removeButton.setDisable( true );
        } 
    }
    
}
