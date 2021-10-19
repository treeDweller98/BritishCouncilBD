package scenes.librarian;

import branchLibrary.BorrowedBook;
import britishcouncilbd.CurrentSystem;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import people.Librarian;
import people.Member;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class LateOrLostBookController implements Initializable {

    @FXML
    private TextField memberIDfield;
    @FXML
    private Label memberNameLabel;
    @FXML
    private Label memberSinceLabel;
    @FXML
    private TableView<BorrowedBook> borrowedBooksTable;
    @FXML
    private TableColumn<BorrowedBook, String> titleCol;
    @FXML
    private TableColumn<BorrowedBook, LocalDate> borrowedCol;
    @FXML
    private TableColumn<BorrowedBook, LocalDate> returnByCol;
    @FXML
    private Label errorStatusLabel;
    @FXML
    private Label idErrorLabel;
    
    private Librarian librarian;                             // user
    private Member member;                                   // borrower
    private ObservableList<BorrowedBook> borrowings;         // borrowed books for the tableview
    
    
    public void initData( Librarian user ) {
        librarian = user;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        titleCol.setCellValueFactory( new PropertyValueFactory<>("title") );
        borrowedCol.setCellValueFactory( new PropertyValueFactory<>("borrowDate") );
        returnByCol.setCellValueFactory( new PropertyValueFactory<>("returnByDate") ); 
    }    

    @FXML
    private void searchMemberIDonAction(ActionEvent event) {
        int searchedID;
        try {
            searchedID = Integer.parseInt( memberIDfield.getText() );       // NumberFormatException might occur
            try {
                member = null;
                member = Member.searchMember( searchedID );                 // Member database file might be corrupt - exception   
                
                if ( member != null ) {
                    // Show found member details, Load table 
                    idErrorLabel.setText("found");
                    memberNameLabel.setText( member.getName() + ", ID:" + member.getId() );
                    memberSinceLabel.setText( "Joined " + member.JOINED.toString() + ", Valid Till " + member.getValidTill() );
                    borrowings = member.getBorrowedBooksListToView();
                    borrowedBooksTable.setItems( borrowings );       
                } else {
                    idErrorLabel.setText("not found");
                    
                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setTitle("Search Failed");
                    b.setContentText("Could not find member ID:" + searchedID );
                    Optional<ButtonType> result = b.showAndWait();
                }                
            } catch (Exception e ) {
                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setTitle("Database Error");
                b.setContentText("Corrupt database. Contact Admin for support.");
                Optional<ButtonType> result = b.showAndWait();
            }           
        } catch ( NumberFormatException e ) {
            idErrorLabel.setText("invalid input");
        }     
    }

    @FXML
    private void lateReturnBookButtonOnClick(ActionEvent event) {
        BorrowedBook selected = borrowedBooksTable.getSelectionModel().getSelectedItem();
        int due;
        String stat;
        
        if ( selected != null ) {
            if ( LocalDate.now().isBefore(selected.getReturnByDate()) ) {
                Alert b = new Alert(Alert.AlertType.INFORMATION);
                b.setTitle("Return date not passed");
                b.setContentText("No late fee has been incurred. Please go to the Borrow/Return section to return this book");
                Optional<ButtonType> result = b.showAndWait();
            } else {
                try {
                    due = librarian.lostBookByMember( selected, member.ID );
                    stat =  selected.getTitle() + " successfully registered as \"Returned Late\" by member ID:" + member.ID + ". Due BDT." + due;
                    errorStatusLabel.setText(stat);
                    borrowings.remove(selected);
                    
                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setTitle("Success");
                    b.setContentText(stat);
                    Optional<ButtonType> result = b.showAndWait();
                    
                    CurrentSystem.generatePdfFromString(selected.generateLateSlip(librarian.getName(), librarian.getBranch().name(), 
                                                                                    librarian.getId(), member.getName(), member.ID, due));
                  
                } catch ( Exception e ) {
                    errorStatusLabel.setText("A database error has occured");
                    Alert b = new Alert(Alert.AlertType.ERROR);
                    b.setTitle("Database Error");
                    b.setHeaderText( e.toString() );
                    b.setContentText("Contact Admin for support.");
                    Optional<ButtonType> result = b.showAndWait();
                }
            }
        } else {
            errorStatusLabel.setText("You must select a book before clicking return");
        }
    }

    @FXML
    private void lostBookButtonOnClick(ActionEvent event) {
        BorrowedBook selected = borrowedBooksTable.getSelectionModel().getSelectedItem();
        int due;
        String stat;
        
        if ( selected != null ) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Confirmation Alert");
            a.setContentText("Are you sure you wish to mark this book as lost?");
            Optional<ButtonType> result = a.showAndWait();

            if(result.get() == ButtonType.OK){
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Requesting Authorisation");
                dialog.setContentText("Enter Authorisation Code to Proceed");
                Optional<String> auth = dialog.showAndWait();

                if ( librarian.authenticate(auth.get()) ) {
                    try {
                        due = librarian.lateReturnFromMember( selected, member.ID );
                        stat =  selected.getTitle() + " successfully registered as \"LOST\" by member ID:" + member.ID + ". Due BDT." + due;
                        errorStatusLabel.setText(stat);
                        borrowings.remove(selected);

                        Alert b = new Alert(Alert.AlertType.INFORMATION);
                        b.setTitle("Success");
                        b.setContentText(stat);
                        result = b.showAndWait();

                        CurrentSystem.generatePdfFromString(selected.generateLostSlip(librarian.getName(), librarian.getBranch().name(),
                                                                                        librarian.getId(), member.getName(), member.ID, due));

                    } catch ( Exception e ) {
                        errorStatusLabel.setText("A database error has occured");
                        Alert b = new Alert(Alert.AlertType.ERROR);
                        b.setTitle("Database Error");
                        b.setHeaderText( e.toString() );
                        b.setContentText("Contact Admin for support.");
                        result = b.showAndWait();
                    }
                }
            } else {
                Alert c = new Alert(Alert.AlertType.ERROR);
                c.setTitle("Failed");
                c.setContentText("Could not authenticate user");
                result = c.showAndWait();
            }
        } else {
            errorStatusLabel.setText("You must select a book before clicking return");
        }        
    }
    
}
