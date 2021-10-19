package scenes.receptionist;

import internationalexams.Candidate;
import internationalexams.Exam;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import people.Receptionist;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class IntlExamRegListController implements Initializable {

    @FXML
    private AnchorPane displayPane;
    @FXML
    private TableView<Candidate> AllExamRegTable;
    @FXML
    private TableColumn<Candidate, Integer> candidateNumCol;
    @FXML
    private TableColumn<Candidate, String> nameCol;
    @FXML
    private TableColumn<Candidate, Integer> feeCol;
    @FXML
    private TableColumn<Candidate, String> phoneCol;
    @FXML
    private TableColumn<Candidate, LocalDate> paidOnCol;
    @FXML
    private ComboBox<Exam> seriesComboBox;
    
    private Receptionist receptionist;
    private ObservableList<Exam> exams;             // all the exams in database
    private ObservableList<Candidate> candidates;   // all candidates of selected exam
    private Exam selectedExam;                      // selected exam
    
    public void initData( Receptionist user ) {
        receptionist = user;
        exams = Exam.getAllExamsToView();
        seriesComboBox.getItems().addAll( exams );
        candidateNumCol.setCellValueFactory( new PropertyValueFactory<>("candidateNumber") );
        nameCol.setCellValueFactory( new PropertyValueFactory<>("name") );
        feeCol.setCellValueFactory( new PropertyValueFactory<>("fee") );
        phoneCol.setCellValueFactory( new PropertyValueFactory<>("phone") );
        paidOnCol.setCellValueFactory( new PropertyValueFactory<>("feePaidOn") );
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
    }    

    @FXML
    private void candidateSelectedOnClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ExamRegDetails.fxml"));

            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException ex) {
                System.out.println(ex);
            }
            Scene scene = new Scene( root );

            ExamRegDetailsController controller = loader.getController();
            controller.initData( 
                    receptionist, AllExamRegTable.getSelectionModel().getSelectedItem(), 
                    selectedExam.year, selectedExam.session, selectedExam.series, selectedExam.getLastDayOfCancellation()
            );

            Stage window = new Stage();
            window.setTitle( "Candidate Details" );
            window.setScene( scene );
            window.show();
        }
    }

    @FXML
    private void seriesComboBoxOnAction(ActionEvent event) {
        selectedExam = seriesComboBox.getValue();
        candidates = FXCollections.observableArrayList( selectedExam.getValidCandidates() );
        AllExamRegTable.setItems( candidates );
    }
    
}
