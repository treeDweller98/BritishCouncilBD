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
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import localevents.Course;
import localevents.CourseExam;
import people.Instructor;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class CourseExamCreatorController implements Initializable {

    @FXML
    private DatePicker scheduleDatePicker;
    @FXML
    private TextField examTitle;
    @FXML
    private TextField marksField;
    @FXML
    private Slider percentageSlider;
    @FXML
    private Button createBtn;
    @FXML
    private Label errorStatusLabel;
    
    private Instructor instructor;
    private Course course;
    private int percentage;
    
    public void initData( Instructor user, Course selectedCourse ) {
        instructor = user; course = selectedCourse;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void createButtonOnClick(ActionEvent event) {
        percentage = (int) percentageSlider.getValue();
        if ( percentage + course.getPercentageOfTotalAssessed() > 100 ) {
            errorStatusLabel.setText("Exceeds 100% - select a lower percentage."); return;
        } 
        
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation Alert");
        a.setContentText("Are you sure you wish to create this exam?");
        Optional<ButtonType> result = a.showAndWait();
        
        if(result.get() == ButtonType.OK){ 
            CourseExam newExam = null;
            try {
                newExam = instructor.createAnExamForCourse( 
                    course.getEventName(), Integer.parseInt( marksField.getText() ), percentage,
                    examTitle.getText(), scheduleDatePicker.getValue()
                );
            } catch ( Exception e ) {
                System.out.println(e); errorStatusLabel.setText("Ensure all fields are filled properly"); return;
            }
            
            if ( newExam == null ) {
                errorStatusLabel.setText("Failed: database error");
                a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Failed");
                a.setContentText("Database error - could not create exam.");
                a.showAndWait();
            } else {
                errorStatusLabel.setText("Successfully created " + newExam.TITLE + ". Have a nice day!");
                a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Success");
                a.setContentText("Exam has been created");
                a.showAndWait();
                
                course.addExam(newExam); createBtn.setDisable(true);
            }
        } 
    }
    
}
