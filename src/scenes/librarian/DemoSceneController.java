package britishcouncilbd;

import branchLibrary.Book;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class DemoSceneController implements Initializable {

    @FXML
    private TableView<Book> bookTV;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, Integer> priceCol;
    @FXML
    private TableColumn<Book, Integer> idCol;
    @FXML
    private Label resultLabel;
    @FXML
    private Button showBtn;
    @FXML
    private ComboBox<String> categoryCmb;
    
    
    private ObservableList<Book> allBooksToView;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        priceCol.setCellValueFactory( new PropertyValueFactory<>("price") );
        idCol.setCellValueFactory( new PropertyValueFactory<>("id") );
        titleCol.setCellValueFactory( new PropertyValueFactory<>("name") );
        //
        
        ArrayList<Book> myBooks = new ArrayList<>();
        myBooks.add( new Book( "abcd", "horror", 234, 400 ) );
        myBooks.add( new Book( "abcd", "horror", 23474, 450 ) );
        myBooks.add( new Book( "abafgcd", "action", 2356, 500 ) );

        try ( ObjectOutputStream oos = new ObjectOutputStream ( new FileOutputStream("bookData.bin"))) {
            oos.writeObject(myBooks);
        }catch ( Exception e ) {
            System.out.println(e);
            //handle code
        }
        
        
        // Load book TV from file
        ArrayList<Book> allBooks = null;
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream("bookData.bin") ) ) {
            allBooks = (ArrayList<Book>) ois.readObject();
        } catch ( Exception e ) {
            System.out.println(e);
        }
        allBooksToView = FXCollections.observableArrayList(allBooks);
        bookTV.setItems( allBooksToView );
        
        // combobox
        File f = null; Scanner scanner; String categs; String[] tokens = null;
        try {
            f = new File( "BookCategory.txt" );
            scanner = new Scanner(f);
            while (scanner.hasNextLine()){
                categs = scanner.nextLine();
                tokens = categs.split(","); 
            }
        } catch(Exception e ){
            System.out.println(e); 
            // hadnling code
        }
        
        categoryCmb.setItems( FXCollections.observableArrayList( tokens ) );

    }    

    @FXML
    private void showButtonOnClick(ActionEvent event) {
        String categChosen = categoryCmb.getValue();
        String myString = "";
        
        ArrayList<Book> allOfSameCateg = new ArrayList<>();
        for ( var b : allBooksToView ) {
            if ( b.category.equals(categChosen) ) {
                allOfSameCateg.add(b);
                myString += b + "\n";
            }
        } myString += "\n\nTotal books of " + categChosen + ":   " +  allOfSameCateg.size();
        
        int totalPrice = 0;
        for ( var b : allOfSameCateg ) {
            totalPrice += b.price;
        }
        
        resultLabel.setText( "Average Price of " + categChosen + " is BDT " + (totalPrice / allOfSameCateg.size()) );
        
        
        try{
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.bmp", "*.png"));
            File f = fc.showSaveDialog(null);
            if(f!=null){              
                PdfWriter pw = new PdfWriter(new FileOutputStream(f));
                PdfDocument pdf =  new PdfDocument(pw);
                pdf.addNewPage();
                Document doc = new Document(pdf);
                doc.setLeftMargin(70);
        
                doc.add( new Paragraph( new Text( myString ) ) );
                doc.close();

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("The PDF is saved successfully.");
                a.showAndWait();    
            }
            else{
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Saving as PDF: cancelled!");
                a.showAndWait();               
            }
        }
        catch(Exception e){
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("Oops! Exception: " + e.toString()+ " occured.");
            a.showAndWait(); 
        }
        
    }
    
    
    
    
    /////
    class Book {
        String name,category;
        int id, price;
        
        public Book( String name, String categ, int id, int price ) {
            this.name = name; 
            category = categ;
            this.id =  id; this.price = price;
        }
        @Override
        public String toString() {
            return "name " + name + "  price " + price; 
        }
        
    }
    
}
