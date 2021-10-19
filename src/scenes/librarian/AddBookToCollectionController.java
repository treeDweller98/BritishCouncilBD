package scenes.librarian;

import branchLibrary.BookGenre;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import people.Librarian;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class AddBookToCollectionController implements Initializable {

    @FXML
    private TextField bookTitle;
    @FXML
    private TextField bookYear;
    @FXML
    private TextField bookPublisher;
    @FXML
    private TextField bookAuthor;
    @FXML
    private TextField bookISBN;
    @FXML
    private TextField bookCopies;
    @FXML
    private TextField bookPrice;
    @FXML
    private ComboBox<BookGenre> bookGenreComboBox;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private ImageView coverPicImgSample;
        
    private Librarian librarian;
    private ArrayList<String> fileTypeList;
    
    public void initData( Librarian user ) {
        librarian = user;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        bookGenreComboBox.getItems().setAll( BookGenre.values() );
        fileTypeList = new ArrayList<>();
        fileTypeList.add("*.jpeg");
        fileTypeList.add("*.png");
    }    

    @FXML
    private void addButtonOnClick(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation Alert");
        a.setContentText("Are you sure you wish to add this book?");
        Optional<ButtonType> result = a.showAndWait();
        
        String stat = "";
        if(result.get() == ButtonType.OK){ 
            try {
                stat = librarian.addBookToCollection( 
                        bookTitle.getText(), bookISBN.getText(), bookAuthor.getText(), bookPublisher.getText(), 
                        bookGenreComboBox.getValue(), Integer.parseInt(bookYear.getText()), Integer.parseInt(bookPrice.getText()), 
                        Integer.parseInt(bookCopies.getText())/*, coverPicImgSample.getImage()*/
                );
            }
            catch( IllegalArgumentException e ){
                errorMessageLabel.setText( "Error: please ensure data fields are all properly entered and a cover image is selected" );
                System.out.println(e);
                Alert c = new Alert(Alert.AlertType.ERROR);
                c.setTitle("Input Error");
                c.setContentText("please ensure data fields are all properly entered");
                result = c.showAndWait();
            }
            
            if( stat == "" );
            else if ( stat == null ){
                errorMessageLabel.setText( "Database Error: Could not add book to database.");
                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setTitle("Database Error");
                b.setContentText("Could not add book to database.");
                result = b.showAndWait();
            }
            else {
                errorMessageLabel.setText( stat );
                Alert b = new Alert(Alert.AlertType.INFORMATION);
                b.setTitle("Success!");
                b.setContentText(stat);
                result = b.showAndWait();
                
                bookTitle.clear(); bookISBN.clear(); bookAuthor.clear(); bookPublisher.clear();
                bookYear.clear(); bookPrice.clear(); bookCopies.clear(); coverPicImgSample.setImage(null);       
            }
        }
    }

    @FXML
    private void fileBrowseBtnOnClick(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", fileTypeList));
        File file = fc.showOpenDialog(null);
        if ( file != null ) {
            coverPicImgSample.setImage( new Image(file.toURI().toString()) );
        }
    }
    
}
