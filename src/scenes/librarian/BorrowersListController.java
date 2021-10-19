package scenes.librarian;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import people.Member;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class BorrowersListController implements Initializable {

    @FXML
    private TableView<Member> borrowersTable;
    @FXML
    private TableColumn<Member, String> memberNameCol;
    @FXML
    private TableColumn<Member, Integer> idCol;
    @FXML
    private TableColumn<Member, String> books;
    @FXML
    private Label errorStatusLabel;
    @FXML
    private TableColumn<Member, Integer> dueCol;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        memberNameCol.setCellValueFactory( new PropertyValueFactory<>("name") ); 
        idCol.setCellValueFactory( new PropertyValueFactory<>("id") );
        books.setCellValueFactory( new PropertyValueFactory<>("borrowedBookNames") );
        dueCol.setCellValueFactory( new PropertyValueFactory<>("libraryDues") );
        borrowersTable.setItems( Member.fetchBorrowingMembersListToView() );
    }    
    
}
