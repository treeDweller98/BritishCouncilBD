package scenes.receptionist;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import people.Member;
import people.Receptionist;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class MembersListController implements Initializable {

    @FXML
    private AnchorPane displayPane;
    @FXML
    private TableView<Member> allMembersTable;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, Integer> idCol;
    @FXML
    private TableColumn<Member, LocalDate> dobCol;
    @FXML
    private TableColumn<Member, LocalDate> joinedCol;
    @FXML
    private TableColumn<Member, LocalDate> validTillCol;
    
    private Receptionist receptionist;
    public void initData( Receptionist user ) {
        receptionist = user;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        nameCol.setCellValueFactory( new PropertyValueFactory<>("name") );
        idCol.setCellValueFactory( new PropertyValueFactory<>("id") );
        dobCol.setCellValueFactory( new PropertyValueFactory<>("dateOfBirth") );
        joinedCol.setCellValueFactory( new PropertyValueFactory<>("joined") );
        validTillCol.setCellValueFactory( new PropertyValueFactory<>("validTill") );
        allMembersTable.setItems( FXCollections.observableArrayList( Member.fetchMembersList() ) );
    }    

    @FXML
    private void memberSelectedOnClick(MouseEvent event) {
        if (event.getClickCount() == 2)
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("MemberRenewModify.fxml"));

            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException ex) {
                System.out.println(ex);
            }
            Scene scene = new Scene( root );

            MemberRenewModifyController controller = loader.getController();
            controller.initData( receptionist, allMembersTable.getSelectionModel().getSelectedItem() );

            Stage window = new Stage();
            window.setTitle( "Renew Membership or Modify Member Details" );
            window.setScene( scene );
            window.show();
        }    
    } 
}
