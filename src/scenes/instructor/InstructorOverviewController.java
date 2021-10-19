package scenes.instructor;

import britishcouncilbd.BranchNames;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import people.Instructor;
import scenes.DefaultPageController;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class InstructorOverviewController implements Initializable {

    @FXML
    private Label labelBranchTitle;
    @FXML
    private Label loggedInInstructor;
    @FXML
    private AnchorPane displayPane;
    
    private Instructor instructor;
    
    public void initData( Instructor user, BranchNames branch ) {
        instructor = user;
        labelBranchTitle.setText( branch.name() );
        loggedInInstructor.setText( instructor.getName() );
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void homeButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scenes/DefaultPage.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        
        //access the controller
        DefaultPageController controller = loader.getController();
        controller.initData( instructor );
        
        // Show scene in current window
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle( "Welcome " + instructor.getName() );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void examRefundRequestsButtonOnClick(ActionEvent event) {
        displayPane.getChildren().clear();
        
        FXMLLoader loader = new FXMLLoader();
        Parent root = null; 
        loader.setLocation(getClass().getResource("ExamRefundsPendingApprovalList.fxml"));
        root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        ExamRefundsPendingApprovalListController controller = loader.getController();
        controller.initData( instructor );
        displayPane.getChildren().add( root );
    }

    @FXML
    private void coursesButtonOnClick(ActionEvent event) {
        displayPane.getChildren().clear();
        
        FXMLLoader loader = new FXMLLoader();
        Parent root = null; 
        loader.setLocation(getClass().getResource("CourseDetails.fxml"));
        root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        CourseDetailsController controller = loader.getController();
        controller.initData( instructor );
        displayPane.getChildren().add( root );
    }
    
}
