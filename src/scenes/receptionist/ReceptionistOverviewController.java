package scenes.receptionist;

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
import people.Receptionist;
import scenes.DefaultPageController;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class ReceptionistOverviewController implements Initializable {
    @FXML
    private Label labelBranchTitle;
    @FXML
    private Label loggedInReceptionist;
    @FXML
    private AnchorPane displayPane;
     
     
    private Receptionist receptionist;
    public void initData( Receptionist user, BranchNames branch ) {
        receptionist = user;
        labelBranchTitle.setText( branch.name() );
        loggedInReceptionist.setText( receptionist.getName() );
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
        controller.initData( receptionist );
        
        // Show scene in current window
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle( "Welcome " + receptionist.getName() );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void newMemberButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("NewMemberReg.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        
        //access the controller
        NewMemberRegController controller = loader.getController();
        controller.initData( receptionist );

        Stage window = new Stage();
        window.setTitle( "Registering a New Member" );
        window.setScene( scene );
        window.show();
    }
    
    @FXML
    private void memberListButtonOnClick(ActionEvent event) {
        FXMLLoader sloader = new FXMLLoader();
        Parent root = null;
        displayPane.getChildren().clear();
        
        sloader.setLocation(getClass().getResource("MembersList.fxml"));
        try {
            root = sloader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        MembersListController mcontroller = sloader.getController();
        mcontroller.initData( receptionist );
        displayPane.getChildren().add( root );
    }

    @FXML
    private void candidateListButtonOnClick(ActionEvent event) {
        FXMLLoader sloader = new FXMLLoader();
        Parent root = null;
        displayPane.getChildren().clear();
  
        sloader.setLocation(getClass().getResource("IntlExamRegList.fxml"));
        root = null;
        try {
            root = sloader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        IntlExamRegListController econtroller = sloader.getController();
        econtroller.initData( receptionist );
        displayPane.getChildren().add( root );
    }
    

    @FXML
    private void exmCertsButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("QueryCertificates.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        
        //access the controller
        QueryCertificatesController controller = loader.getController();
        controller.initData( receptionist );

        Stage window = new Stage();
        window.setTitle( "Exam Certificates" );
        window.setScene( scene );
        window.show();
    }
}
