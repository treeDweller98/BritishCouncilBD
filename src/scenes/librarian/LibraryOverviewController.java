package scenes.librarian;

import branchLibrary.Book;
import branchLibrary.BookBorrower;
import branchLibrary.BookGenre;
import britishcouncilbd.BranchNames;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import people.Librarian;
import scenes.DefaultPageController;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class LibraryOverviewController implements Initializable {

    @FXML
    private Label labelBranchTitle;
    @FXML
    private Label loggedInLibrarianInfo;
    @FXML
    private TableView<Book> bookCollectionTable;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> authCol;
    @FXML
    private TableColumn<Book, BookGenre> genreCol;
    @FXML
    private TableColumn<Book, Integer> availableCol;
    @FXML
    private TableColumn<Book, Integer> totalCopiesCol;
    @FXML
    private TableView<BookBorrower> borrowerTable;
    @FXML
    private TableColumn<BookBorrower, Integer> memberIDCol;
    @FXML
    private TableColumn<BookBorrower, LocalDate> borrowDateCol;
    @FXML
    private TableColumn<BookBorrower, LocalDate> returnDateCol;
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
    private ImageView bookCover;
    @FXML
    private TextField bookSearchbar;
    
    private Librarian librarian;
    private ObservableList<Book> books;
    private Book book;
    
    public void initData( Librarian user, BranchNames branch ) {
        librarian = user;
        labelBranchTitle.setText( branch.name() );
        loggedInLibrarianInfo.setText( librarian.getName() );
        books = librarian.getBookListToView();
        bookCollectionTable.setItems( books );
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        titleCol.setCellValueFactory( new PropertyValueFactory<>("title") );
        authCol.setCellValueFactory( new PropertyValueFactory<>("author") );
        genreCol.setCellValueFactory( new PropertyValueFactory<>("genre") );
        availableCol.setCellValueFactory( new PropertyValueFactory<>("availableCopies") );
        totalCopiesCol.setCellValueFactory( new PropertyValueFactory<>("totalCopies") );
        
        memberIDCol.setCellValueFactory( new PropertyValueFactory<>("memberId") );
        borrowDateCol.setCellValueFactory( new PropertyValueFactory<>("borrowDate") );
        returnDateCol.setCellValueFactory( new PropertyValueFactory<>("returnDate") );
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
        
        DefaultPageController controller = loader.getController();
        controller.initData( librarian );
        
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setTitle( "Welcome " + librarian.getName() );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void addBookButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AddBookToCollection.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );

        AddBookToCollectionController controller = loader.getController();
        controller.initData( librarian );

        Stage window = new Stage();
        window.setTitle( "Add-Book Utility" );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void borrowReturnButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("BorrowReturnBook.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        
        BorrowReturnBookController controller = loader.getController();
        controller.initData( librarian );

        Stage window = new Stage();
        window.setTitle( "Members' Borrow-Return Area" );
        window.setScene( scene );
        window.show();
    }
    
    @FXML
    private void lateReturnButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("LateOrLostBook.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        
        LateOrLostBookController controller = loader.getController();
        controller.initData( librarian );

        Stage window = new Stage();
        window.setTitle( "Library Fine Area" );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void borrowerListButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("BorrowersList.fxml"));
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        
        //BorrowersListController controller = loader.getController();
        //controller.initData();
        
        Stage window = new Stage();
        window.setTitle( "List Of All Borrowing Members" );
        window.setScene( scene );
        window.show();
    }

    @FXML
    private void refreshBtnOnClick(ActionEvent event) {
        books = librarian.getBookListToView();
        bookCollectionTable.setItems( books );
        
        titleLabel.setText(""); authorLabel.setText(""); genreLabel.setText("");
        yearLabel.setText(""); publisherLabel.setText(""); isbnLabel.setText("");
        availableCopiesLabel.setText(""); totalCopiesLabel.setText("");
        bookCover.setImage(null); borrowerTable.getItems().clear(); 
    }

    @FXML
    private void viewBookDetailsOnClick(MouseEvent event) {
        if (event.getClickCount() == 2)
        {
            book = bookCollectionTable.getSelectionModel().getSelectedItem();
            titleLabel.setText(book.getTitle());
            authorLabel.setText(book.getAuthor());
            genreLabel.setText(book.getGenre().name());
            yearLabel.setText( String.valueOf(book.getYear()) ); 
            publisherLabel.setText(book.getPublisher());
            isbnLabel.setText(book.getIsbn());      
            availableCopiesLabel.setText( String.valueOf(book.getAvailableCopies()) );
            totalCopiesLabel.setText( String.valueOf(book.getTotalCopies()) );
            //bookCover.setImage(book.getCoverPhoto());

            borrowerTable.setItems( FXCollections.observableArrayList(book.getBorrowerList()) );
        }
    }

    @FXML
    private void searchBtnOnClick(ActionEvent event) {
        try {
            book = Book.searchBook( bookSearchbar.getText(), librarian.getBranch() );
            
            if ( book != null ) {
                titleLabel.setText(book.getTitle());
                authorLabel.setText(book.getAuthor());
                genreLabel.setText(book.getGenre().name());
                yearLabel.setText( String.valueOf(book.getYear()) ); 
                publisherLabel.setText(book.getPublisher());
                isbnLabel.setText(book.getIsbn());      
                availableCopiesLabel.setText( String.valueOf(book.getAvailableCopies()) );
                totalCopiesLabel.setText( String.valueOf(book.getTotalCopies()) );
                //bookCover.setImage(book.getCoverPhoto());

                borrowerTable.setItems( FXCollections.observableArrayList(book.getBorrowerList()) );
            } else {
                Alert b = new Alert(Alert.AlertType.WARNING);
                b.setTitle("Failed");
                b.setContentText("Could not find book with that title.");
                Optional<ButtonType> result = b.showAndWait();
            }
            
        } catch ( IllegalArgumentException ex ) {
            Alert b = new Alert(Alert.AlertType.WARNING);
            b.setTitle("Input Error");
            b.setContentText("Search Bar cannot be blank");
            Optional<ButtonType> result = b.showAndWait();
        } catch ( Exception e ) {
            System.out.println(e);
            Alert b = new Alert(Alert.AlertType.ERROR);
            b.setTitle("Database Error");
            b.setContentText("Corrupt database. Please contact admin.");
            Optional<ButtonType> result = b.showAndWait();
        }
    }
}
