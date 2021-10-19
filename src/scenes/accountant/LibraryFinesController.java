package scenes.accountant;

import britishcouncilbd.CurrentSystem;
import finances.FinancialAccountNames;
import finances.TransactionType;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import people.Accountant;
import people.Member;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class LibraryFinesController implements Initializable {

    @FXML
    private TextField searchBar;
    @FXML
    private Button searchBtn;
    @FXML
    private TextField payerName;
    @FXML
    private TextField paidAmount;
    @FXML
    private Button markAsPaidBtn;
    @FXML
    private Label errorStatusLabel;
    @FXML
    private TableView<Member> pendingFinesTable;
    @FXML
    private TableColumn<Member, Integer> idCol;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, Integer> feeCol;

    private Accountant accountant;
    private ObservableList<Member> membersWithDues;
    private Member mTemp;
    
    public void initData( Accountant user ) {
        accountant = user;
        membersWithDues = Member.fetchDueMembersList();
        pendingFinesTable.setItems( membersWithDues );
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        feeCol.setCellValueFactory( new PropertyValueFactory<>("libraryDues") );
        idCol.setCellValueFactory( new PropertyValueFactory<>("id") );
        nameCol.setCellValueFactory( new PropertyValueFactory<>("name") );
    }    

    @FXML
    private void searchButtonOnClick(ActionEvent event) {
    }

    @FXML
    private void markAsPaidButtonOnClick(ActionEvent event) {
         boolean stat = false; 
        String payer = payerName.getText();
        int paid;
        if ( payer.isBlank() ) { errorStatusLabel.setText( "You must enter payer name"); return; }
        try {
            paid = Integer.parseInt( paidAmount.getText() );
            if ( paid != mTemp.getLibraryDues() ) { errorStatusLabel.setText( "Paid amount and fee must match"); return; }
        } catch ( NumberFormatException e ) {
            errorStatusLabel.setText( "Paid amount field must contain integers only"); return;
        } 
        
        Alert c = new Alert(Alert.AlertType.INFORMATION);
        c.setTitle("Confirmation");
        c.setContentText("Are you sure you wish to proceed?");
        Optional<ButtonType> result = c.showAndWait();
        
        if( result.get() == ButtonType.OK ) { 
            stat = accountant.clearLibraryFeeDues( mTemp.ID );
            
            if ( stat == false ) {
                errorStatusLabel.setText( "Database error occured");
                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setTitle("Database Error");
                b.setContentText("A database error has occured. Could not finish procedure.");
                b.showAndWait();
            } else {
                errorStatusLabel.setText("Successfully validated member" + mTemp.getName() + "'s Library Dues amounting to BDT. " + paid );
                Alert b = new Alert(Alert.AlertType.INFORMATION);
                b.setTitle("Success");
                b.setContentText("Member's dues have been clear. Member may borrow books again.");
                b.showAndWait();
                
                // generate due-paid slip
                CurrentSystem.generatePdfFromString( mTemp.generateLibraryDuePaidSlip( accountant.getBranch().name(), paid ) );
                
                // record transaction
                String detail =  "Library dues memberID " + mTemp.ID;
                if ( !accountant.recordTransaction( FinancialAccountNames.LIBRARY_FINES, payer, detail, paid, TransactionType.LIBRARY_DUE ) ) {
                    c = new Alert(Alert.AlertType.ERROR);
                    c.setTitle("Database Error");
                    c.setContentText("Transaction could not be recorded to database - please note down manually.");
                    c.showAndWait();
                }
                
                // clean-up
                membersWithDues.remove(mTemp); markAsPaidBtn.setDisable(true); payerName.clear(); paidAmount.clear();
            } 
        }
        
    }

    @FXML
    private void memberSelected(MouseEvent event) {
        mTemp = pendingFinesTable.getSelectionModel().getSelectedItem();
        markAsPaidBtn.setDisable(false);
    }
    
}
