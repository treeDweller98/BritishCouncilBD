package scenes.accountant;


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
import people.Accountant;
import scenes.DefaultPageController;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class AccountantOverviewController implements Initializable {

    @FXML
    private Label labelBranchTitle;
    @FXML
    private Label loggedInAccountant;
    @FXML
    private AnchorPane displayPane;
    
    private Accountant accountant;

    
    public void initData( Accountant user, BranchNames branch ) {
        accountant = user;
        labelBranchTitle.setText( branch.name() );
        loggedInAccountant.setText( accountant.getName() );
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
        controller.initData( accountant );
        
        // Show scene in current window
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle( "Welcome " + accountant.getName() );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void intlExamFeeButtonOnClick(ActionEvent event) {
        displayPane.getChildren().clear();
        
        FXMLLoader loader = new FXMLLoader();
        Parent root = null; 
        loader.setLocation(getClass().getResource("ExamFeePayment.fxml"));
        root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        ExamFeePaymentController controller = loader.getController();
        controller.initData( accountant );
        displayPane.getChildren().add( root );
    }


    @FXML
    private void localEventFeeButtonOnClick(ActionEvent event) {
        displayPane.getChildren().clear();
        
        FXMLLoader loader = new FXMLLoader();
        Parent root = null;
        loader.setLocation(getClass().getResource("LocalEventPayment.fxml"));
        root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        LocalEventPaymentController controller = loader.getController();
        controller.initData( accountant );
        displayPane.getChildren().add( root );
    }


    @FXML
    private void accountsSummaryButtonOnClick(ActionEvent event) {
        displayPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader();
        Parent root = null;
        loader.setLocation(getClass().getResource("AccountsSummary.fxml"));
        root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        AccountsSummaryController controller = loader.getController();
        controller.initData( accountant );
        displayPane.getChildren().add( root );
    }

    @FXML
    private void examRegRefundButtonOnClick(ActionEvent event) {
        displayPane.getChildren().clear();
        
        FXMLLoader loader = new FXMLLoader();
        Parent root = null;
        loader.setLocation(getClass().getResource("ExamRefund.fxml"));
        root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        ExamRefundController controller = loader.getController();
        controller.initData( accountant );
        displayPane.getChildren().add( root );
    }

    @FXML
    private void libraryFinesButtonOnClick(ActionEvent event) {
        displayPane.getChildren().clear();
  
        FXMLLoader loader = new FXMLLoader();
        Parent root = null;
        loader.setLocation(getClass().getResource("LibraryFines.fxml"));
        root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        LibraryFinesController controller = loader.getController();
        controller.initData( accountant );
        displayPane.getChildren().add( root );
    }
    
}
