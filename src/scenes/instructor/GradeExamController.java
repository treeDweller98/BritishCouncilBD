package scenes.instructor;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import localevents.CourseExam;
import localevents.CourseStudent;
import people.Instructor;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class GradeExamController implements Initializable {

    @FXML
    private Label examTitleLabel;
    @FXML
    private Button gradeBtn;
    @FXML
    private Button commitBtn;
    @FXML
    private TextField selectedStudentName;
    @FXML
    private TextField selectedStudentID;
    @FXML
    private TextField marksField;
    @FXML
    private Label errorStatusLabel;
    @FXML
    private Label maxMarksLabel;
    @FXML
    private TableView<CourseStudent> studentsListTable;
    @FXML
    private TableColumn<CourseStudent, Integer> rollCol;
    @FXML
    private TableColumn<CourseStudent, String> studNameCol;
    @FXML
    private TableColumn<CourseStudent, Integer> studIdCol;
    @FXML
    private TableColumn<CourseStudent, Integer> marksCol;
    
    private Instructor instructor;
    private String courseName;
    private CourseExam exam;
    private CourseStudent student;
    private ObservableList<CourseStudent> studList;
    private int[] marks; private int mark, currentRoll = 0;
    
    public void initData( Instructor user, CourseExam examToGrade, String course, ObservableList<CourseStudent> list ) {
        instructor = user; exam = examToGrade; courseName = course; studList = list; 
        studentsListTable.getItems().setAll( studList );
        studentsListTable.setSelectionModel( null );
        maxMarksLabel.setText( String.valueOf( exam.MARKS ) );
        examTitleLabel.setText( exam.TITLE );
        
        try {
            student = studList.get(0);
            setStudent(student);
            marks = new int[ studList.size() ];
        } catch ( IndexOutOfBoundsException e ) {
            System.out.println(e); errorStatusLabel.setText("No students in class"); gradeBtn.setDisable(true);
        }
    }
    
    private void setStudent( CourseStudent student ){
        selectedStudentName.setText( student.NAME ); selectedStudentID.setText( String.valueOf(student.ID) );
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        studNameCol.setCellValueFactory( new PropertyValueFactory<>("name") );
        studIdCol.setCellValueFactory( new PropertyValueFactory<>("id") );
        rollCol.setCellValueFactory( new PropertyValueFactory<>("roll") );
        marksCol.setCellValueFactory( new PropertyValueFactory<>("tempMarks") );        // inelegant workaround, needs replacing
    }    

    @FXML
    private void gradeButtonOnClick(ActionEvent event) {
        try {
            mark = Math.abs( Integer.parseInt(marksField.getText()) );
            if ( mark > exam.MARKS ) {
                errorStatusLabel.setText("Input greater than max marks available"); return;
            }
            
        } catch ( NumberFormatException e ) {
            System.out.println(e); errorStatusLabel.setText("Marks field may only contain positive integers"); return;
        }
        
        marks[ currentRoll ] = mark;        
        student.tempMarks = mark;   
        
        try {
            student = studList.get( ++currentRoll );      
            setStudent(student);
        } catch ( IndexOutOfBoundsException e ) {
            System.out.println(e); errorStatusLabel.setText("All students marked. Please commit to database.");
            commitBtn.setDisable(false); gradeBtn.setDisable(true);
        }
    }

    @FXML
    private void commitBtnOnClick(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Requesting Authorisation");
        dialog.setContentText("Enter Authorisation Code to Proceed");
        Optional<String> auth = dialog.showAndWait();

        if ( instructor.authenticate(auth.get()) ) {
            boolean stat = instructor.gradeCourseExam( courseName, exam.TITLE, marks );
             
            if ( stat == false ) {
                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setTitle("Database Error");
                b.setContentText("A database error has occured. Could not record marks to database.");
                b.showAndWait();
            } else {
                Alert b = new Alert(Alert.AlertType.INFORMATION);
                b.setTitle("Success");
                b.setContentText("Marks have been successfully recorded");
                b.showAndWait();
                
                commitBtn.setDisable(true); gradeBtn.setDisable(true);
                exam.setStudentMarks(marks);
            }
            
        } else {
            Alert c = new Alert(Alert.AlertType.ERROR);
            c.setTitle("Failed");
            c.setContentText("Could not authenticate user");
            c.showAndWait();
        }
    }
    
}
