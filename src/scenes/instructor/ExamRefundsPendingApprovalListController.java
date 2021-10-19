package scenes.instructor;

import internationalexams.CandidateSeekingRefundApproval;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import people.Instructor;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class ExamRefundsPendingApprovalListController implements Initializable {

    @FXML
    private AnchorPane displayPane;
    @FXML
    private TableView<CandidateSeekingRefundApproval> pendingExamCancellationList;
    @FXML
    private TableColumn<CandidateSeekingRefundApproval, String> examNameCol;
    @FXML
    private TableColumn<CandidateSeekingRefundApproval, Integer> candNumCol;
    @FXML
    private TableColumn<CandidateSeekingRefundApproval, String> candNameCol;
    @FXML
    private TextField searchBar;
    @FXML
    private Button searchBtn;
    
    private Instructor instructor;
    private ObservableList<CandidateSeekingRefundApproval> seekingApprovalList;
    
    public void initData( Instructor user ) {
        instructor = user;
        seekingApprovalList = FXCollections.observableArrayList( CandidateSeekingRefundApproval.fetchSeekingRefundApprovalFile() );
        pendingExamCancellationList.setItems( seekingApprovalList );
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        examNameCol.setCellValueFactory( new PropertyValueFactory<>("fullNameOfExam") );
        candNumCol.setCellValueFactory( new PropertyValueFactory<>("candidateId") );
        candNameCol.setCellValueFactory( new PropertyValueFactory<>("candidateName") );
    }    

    @FXML
    private void searchButtonOnClick(ActionEvent event) {
    }

    @FXML
    private void reqSelected(MouseEvent event) {
        if ( event.getClickCount() == 2 ) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ExamCancellationDetails.fxml")); 
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException ex) {
                System.out.println(ex);
            }
            Scene scene = new Scene( root );

            ExamCancellationDetailsController controller = loader.getController();
            controller.initData( instructor, pendingExamCancellationList.getSelectionModel().getSelectedItem(), seekingApprovalList );

            // Show scene in current window
            Stage window = new Stage();
            window.setTitle( "Approve/Disapprove Exam Refund: Candidate Details" );
            window.setScene( scene );
            window.show();
        }
    }
}
