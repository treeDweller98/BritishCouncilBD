package scenes.librarian;

import branchLibrary.Book;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import people.Librarian;
import people.Member;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class BorrowReturnBookController implements Initializable {

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
    private TableColumn<BorrowedBook, LocalDate> borrowDateCol;
    @FXML
    private TableColumn<BorrowedBook, LocalDate> returnDateCol;
    @FXML
    private Button returnBookBtn;
    @FXML
    private Button borrowAnotherBookBtn;
    @FXML
    private TextField bookSearchBar;
    @FXML
    private Label titleLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label genreLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private Label isbnLabel;
    @FXML
    private Label availableCopiesLabel;
    @FXML
    private Label totalCopiesLabel;
    @FXML
    private Label borrowingErrorStatusLabel;
    @FXML
    private Label returningErrorStatusLabel;
    @FXML
    private Slider returnDateSlider;
    @FXML
    private ImageView bookCoverImage;
    @FXML
    private Label idErrorLabel;
    @FXML
    private Button bookSearchBtn;
    
        
    private Librarian librarian;                             // user
    private Book book;                                       // book to be borrowed
    private Member member;                                   // borrower
    private ObservableList<BorrowedBook> borrowings;         // borrowed books for the tableview
    private BorrowedBook newBorrowing;                       // to update table with 
    
    public void initData( Librarian user ) {
        librarian = user;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        bookSearchBtn.setDisable(true);
        returnBookBtn.setDisable(true);
        borrowAnotherBookBtn.setDisable(true);
        titleCol.setCellValueFactory( new PropertyValueFactory<>("title") );
        borrowDateCol.setCellValueFactory( new PropertyValueFactory<>("borrowDate") );
        returnDateCol.setCellValueFactory( new PropertyValueFactory<>("returnByDate") );        
    }    

    @FXML
    private void searchMemberIDonAction(ActionEvent event) {
        int searchedID;
        try {
            searchedID = Integer.parseInt( memberIDfield.getText() );       // NumberFormatException might occur
            try {
                member = null;
                member = Member.searchMember( searchedID );                 // Member database file might be corrupt exception   
                
                if ( member != null ) {
                    // Show found member details, Load table 
                    memberNameLabel.setText( member.getName() + ", ID:" + member.getId() );
                    memberSinceLabel.setText( "Joined " + member.JOINED.toString() + ", Valid Till " + member.getValidTill() );
                    borrowings = member.getBorrowedBooksListToView();
                    borrowedBooksTable.setItems( borrowings ); 
                    
                    // Check validity of membership and capacity, enable-disable borrow/return as appropriate
                    borrowingErrorStatusLabel.setText("");
                    returningErrorStatusLabel.setText("");
                    borrowAnotherBookBtn.setDisable(false);
                    returnBookBtn.setDisable(false);
                    bookSearchBtn.setDisable(false);
                    
                    if ( member.getValidTill().isBefore(LocalDate.now()) ) {    
                        idErrorLabel.setText("membership expired");
                        borrowingErrorStatusLabel.setText("Member " + member.ID + "  may not borrow until membership is renewed");
                        bookSearchBtn.setDisable(true);
                    } else {
                        idErrorLabel.setText("member valid");
                        bookSearchBtn.setDisable(false);
                    }    

                    if ( borrowings.size() >= Member.MAX_BORROW_CAPACITY ) {
                        borrowingErrorStatusLabel.setText("Member " + member.ID + " at max capacity. May not borrow more books");
                        borrowAnotherBookBtn.setDisable(true);
                        bookSearchBtn.setDisable(true);
                    } else if ( borrowings.isEmpty() ) {
                        returnBookBtn.setDisable(true);
                        returningErrorStatusLabel.setText("No books to return");
                    } 
                    
                    if ( member.hasOutstandingDues() ) {
                        borrowAnotherBookBtn.setDisable(true);
                        bookSearchBtn.setDisable(true);
                        borrowingErrorStatusLabel.setText("Member " + member.ID + " has outstanding dues. May not borrow until cleared.");
                    }
                } else {
                    idErrorLabel.setText("not found");
                    borrowingErrorStatusLabel.setText("");
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
    private void returnBookButtonOnClick(ActionEvent event) {
        
        BorrowedBook selected = borrowedBooksTable.getSelectionModel().getSelectedItem();
        if ( selected != null ) {
            if ( LocalDate.now().isAfter(selected.getReturnByDate()) ) {
                returningErrorStatusLabel.setText("Late/Lost due");
                Alert b = new Alert(Alert.AlertType.WARNING);
                b.setTitle("Return Date Passed");
                b.setContentText("Late fee has been incurred. Please go to the Late/Lost section to return this book");
                Optional<ButtonType> result = b.showAndWait();
            } else {
                try {
                    String stat = librarian.returnBookFromMember( selected.getTitle(), member.ID );
                    
                    returningErrorStatusLabel.setText(stat);
                    borrowings.remove(selected);
                    
                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setTitle("Success");
                    b.setContentText(stat);
                    Optional<ButtonType> result = b.showAndWait();
                    
                    // generate pdf slip
                    CurrentSystem.generatePdfFromString(selected.generateReturnSlip( librarian.getName(), librarian.getBranch().name(),
                                                                                        librarian.getId(), member.getName(), member.ID ));
                  
                } catch ( Exception e ) {
                    returningErrorStatusLabel.setText("A database error has occured");
                    Alert b = new Alert(Alert.AlertType.ERROR);
                    b.setTitle("Database Error");
                    b.setHeaderText( e.toString() );
                    b.setContentText("Contact Admin for support.");
                    Optional<ButtonType> result = b.showAndWait();
                } 
            }     
        } else {
            returningErrorStatusLabel.setText("You must select a book before clicking return");
        }
    }

    @FXML
    private void borrowAnotherBookButtonOnClick(ActionEvent event) {
        if ( book != null ) {
            try {
                newBorrowing = librarian.lendBookToMember(book.getTitle() , member.ID, (int) returnDateSlider.getValue());  System.out.println(returnDateSlider.getValue());
                borrowings.add(newBorrowing);
                book = null;
                
                borrowingErrorStatusLabel.setText( member.getName() + " successfully borrowed " + book.getTitle() );
                Alert b = new Alert(Alert.AlertType.INFORMATION);
                b.setTitle("Success");
                b.setContentText( "Book has been lent to member " + member.ID );
                Optional<ButtonType> result = b.showAndWait();
                
                // generate pdf slip
                CurrentSystem.generatePdfFromString(newBorrowing.generateBorrowSlip(librarian.getName(), librarian.getBranch().name(),
                                                                                        librarian.getId(), member.getName(), member.ID));
                
            } catch ( Exception e ) {
                borrowingErrorStatusLabel.setText("A database error has occured");
                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setTitle("Database Error");
                b.setHeaderText( e.toString() );
                b.setContentText("Contact Admin for support.");
                Optional<ButtonType> result = b.showAndWait();
            }                    
        } else {
            borrowingErrorStatusLabel.setText("You must seach a book first before clicking borrow");    
        }
    }

    @FXML
    private void searchBookButtonOnClick(ActionEvent event) {
        String titleToSearch = bookSearchBar.getText();
        if ( !titleToSearch.isEmpty() ) {
            try {
                book = null;
                book = Book.searchBook( titleToSearch, librarian.getBranch() );
                
                // If book in database, display
                if ( book != null ) {
                    titleLabel.setText(book.getTitle());
                    authorLabel.setText(book.getAuthor());
                    genreLabel.setText(book.getGenre().name());
                    yearLabel.setText( String.valueOf(book.getYear()) ); 
                    publisherLabel.setText(book.getPublisher());
                    isbnLabel.setText(book.getIsbn());      
                    availableCopiesLabel.setText( String.valueOf(book.getAvailableCopies()) );
                    totalCopiesLabel.setText( String.valueOf(book.getTotalCopies()) );
                    //bookCoverImage.setImage(book.getCoverPhoto());
                    
                    if ( book.getAvailableCopies() <= Librarian.MIN_NUMBER_OF_COPIES_TO_KEEP ) {
                        borrowingErrorStatusLabel.setText("Insufficient Copies - cannot lend this book.");
                    }
                    borrowingErrorStatusLabel.setText("");
                } else {
                    borrowingErrorStatusLabel.setText("Book not found");
                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setTitle("Search Failed");
                    b.setContentText("Could not find book the book \"" + titleToSearch + "\" in the database");
                    Optional<ButtonType> result = b.showAndWait();
                }
            } catch ( Exception e ) {
                System.out.println(e);
                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setTitle("Database Error");
                b.setContentText("Corrupt database. Contact Admin for support.");
                Optional<ButtonType> result = b.showAndWait();
            }
        } else {
            borrowingErrorStatusLabel.setText("Search field cannot be empty");
        }
    }
    
}
