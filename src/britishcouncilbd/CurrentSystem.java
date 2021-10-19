package britishcouncilbd;

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
import java.io.Serializable;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

/**
 *
 * @author f_ahmed
 */
public class CurrentSystem implements Serializable {
    BranchNames thisBranch;
    private int candidateIDcount = 9_000_000;
    private int employeeIDcount = 20_000;
    private int memberIDcount = 500_000;
    
    
    public CurrentSystem( BranchNames branchToSave ){
        thisBranch = branchToSave;
    }
    
    public static BranchNames getCurrentBranchName() {
        CurrentSystem c = fetchCurrentSystemFile();
        return c.thisBranch;
    }
    
    public int incremEmployeeID() { return ++employeeIDcount; }
    public int incremCandidateID() { return ++candidateIDcount; }
    public int incremMemberID() { return ++memberIDcount; }
    
    public static int generateNewEmployeeID() {
        CurrentSystem c = fetchCurrentSystemFile();
        int id = c.incremEmployeeID();
        writeToCurrentSystemBin(c);
        return id;
    }
    public static int generateNewMemberID(){
        CurrentSystem c = fetchCurrentSystemFile();
        int id = c.incremMemberID();
        writeToCurrentSystemBin(c);
        return id;
    }
    public static int generateNewCandidateNumber() {
        CurrentSystem c = fetchCurrentSystemFile();
        int id = c.incremCandidateID();
        writeToCurrentSystemBin(c);
        return id;
    }
    
    public static CurrentSystem fetchCurrentSystemFile() {
        File f = new File("CurrentSystem.bin");
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            return (CurrentSystem) ois.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return new CurrentSystem(BranchNames.DHAKA);        // for debug purposes
        }
    }
    
    public static void writeToCurrentSystemBin( CurrentSystem s ){
        File f = new File("CurrentSystem.bin");
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( s );
        } catch (Exception e) {
            System.out.println(e);
            Alert b = new Alert(Alert.AlertType.ERROR);
            b.setTitle("Database Error");
            b.setContentText("FATAL ERROR - CurrentSystem.bin corrupt");
            b.showAndWait();
        }
    }
    
    public static void generatePdfFromString( String myString ) {
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
}
