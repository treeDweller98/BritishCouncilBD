package scenes.instructor;

import britishcouncilbd.CurrentSystem;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import localevents.Course;
import localevents.CourseExam;
import localevents.CourseStudent;
import people.Instructor;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class CourseDetailsController implements Initializable {

    @FXML
    private AnchorPane classSummaryPane;
    @FXML
    private ComboBox<Course> classSelectorComboBox;
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
    @FXML
    private TableView<CourseExam> examListTable;
    @FXML
    private TableColumn<CourseExam, LocalDate> examDateCol;
    @FXML
    private TableColumn<CourseExam, String> examTitleCol;
    @FXML
    private TableColumn<CourseExam, Integer> examMarksCol;
    @FXML
    private TableColumn<CourseExam, Integer> examPercentageCol;
    @FXML
    private TableColumn<CourseExam, String> examStatCol;
    @FXML
    private Button addNewExamBtn;
    @FXML
    private Button gradeExamBtn;
    @FXML
    private Button generateReportCardBtn;
    @FXML
    private Button viewPieChartBtn;
    @FXML
    private RadioButton pieSelectedExamToggle;
    @FXML
    private ToggleGroup piechartSelector;
    @FXML
    private RadioButton pieOverallToggle;
    @FXML
    private Label courseScheduleLabel;
    @FXML
    private Label errorStatusLabel;
    @FXML
    private PieChart pieChartOfStudentPerformance;
    
    private Instructor instructor;
    private Course selectedCourse;
    private ObservableList<CourseStudent> studentsOfSelectedCourse;
    private ObservableList<CourseExam> examsOfSelectedCourse;
    private CourseExam selectedExam;
    private CourseStudent student;
    
    public void initData( Instructor user ) {
        instructor = user;
        classSelectorComboBox.setItems( FXCollections.observableArrayList( instructor.fetchAllCoursesForThisInstructor() ) );
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
       marksCol.setCellValueFactory( new PropertyValueFactory<>("totalMarksAchieved") );
       
       examDateCol.setCellValueFactory( new PropertyValueFactory<>("scheduledDate") );
       examTitleCol.setCellValueFactory( new PropertyValueFactory<>("title") );
       examMarksCol.setCellValueFactory( new PropertyValueFactory<>("marks") );
       examPercentageCol.setCellValueFactory( new PropertyValueFactory<>("percentage") );
       examStatCol.setCellValueFactory( new PropertyValueFactory<>("gradeStatusString") );      
    }    

    @FXML
    private void classSelectorComboBoxOnAction(ActionEvent event) {
        selectedCourse = classSelectorComboBox.getValue();
        courseScheduleLabel.setText( selectedCourse.getSchedule() );
        
        studentsOfSelectedCourse = FXCollections.observableArrayList( selectedCourse.getEnrolledStudents() );
        studentsListTable.getItems().setAll( studentsOfSelectedCourse );
        examsOfSelectedCourse = FXCollections.observableArrayList( selectedCourse.getExams() );
        examListTable.getItems().setAll( examsOfSelectedCourse );
        
        pieOverallToggle.setSelected(true); viewPieChartBtn.setDisable(false);
        if( selectedCourse.getPercentageOfTotalAssessed() < 100 ) addNewExamBtn.setDisable(false);
    }

    @FXML
    private void addNewExamButtonOnClick(ActionEvent event) {
         if( selectedCourse.getPercentageOfTotalAssessed() < 100 ) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CourseExamCreator.fxml"));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException ex) {
                System.out.println(ex);
            }
            Scene scene = new Scene( root );

            CourseExamCreatorController controller = loader.getController();
            controller.initData( instructor, selectedCourse );

            Stage window = new Stage();
            window.setTitle( "Course Exam Creation Wizard" );
            window.setScene( scene );
            window.show();
         } else {
             addNewExamBtn.setDisable(true); errorStatusLabel.setText("100% marks allocated. Cannot create more exams.");
         }
    }

    @FXML
    private void gradeExamButtonOnClick(ActionEvent event) {
        if ( selectedExam.getGradeStatus() == false ) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Requesting Authorisation");
            dialog.setContentText("Enter Authorisation Code to Proceed");
            Optional<String> auth = dialog.showAndWait();
       
            if ( instructor.authenticate(auth.get()) ) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("GradeExam.fxml"));
                Parent root = null;
                try {
                    root = loader.load();
                } catch (IOException ex) {
                    System.out.println(ex);
                }
                Scene scene = new Scene( root );

                GradeExamController controller = loader.getController();
                controller.initData( instructor, selectedExam, selectedCourse.getEventName(), studentsOfSelectedCourse );

                Stage window = new Stage();
                window.setTitle( "Exam Grading Wizard" );
                window.setScene( scene );
                window.show();
            } else {
                Alert c = new Alert(Alert.AlertType.ERROR);
                c.setTitle("Failed");
                c.setContentText("Could not authenticate user");
                c.showAndWait();
            }
        } else {
            gradeExamBtn.setDisable(true);  errorStatusLabel.setText("Exam already graded");
        }
    }

    @FXML
    private void generateReportCardButtonOnClick(ActionEvent event) {
        if ( selectedCourse.getPercentageOfTotalAssessed() == 100 && CourseExam.checkAllGraded(examsOfSelectedCourse) ) {
            CurrentSystem.generatePdfFromString(
                instructor.generateReportCardForStudent( selectedCourse.getEventName(), student )
            );
        } else {
             errorStatusLabel.setText("Cannot create transcript until 100% assessments graded");
        }
        
    }

    @FXML
    private void viewPieChartButtonOnClick(ActionEvent event) {
        if ( pieOverallToggle.isArmed() ) {
            pieChartOfStudentPerformance.setData( instructor.generateCourseOverallPieChartData( selectedCourse.getEventName() ) );
        } else if ( pieSelectedExamToggle.isArmed() && selectedExam != null ) {
             pieChartOfStudentPerformance.setData( instructor.generateExamPieChartData( selectedCourse.getEventName(), selectedExam.TITLE ) );
        } else {
             errorStatusLabel.setText("You must select an exam to generate exam piechart");
        }
    }

    @FXML
    private void studentSelected(MouseEvent event) {
        student = studentsListTable.getSelectionModel().getSelectedItem();
        generateReportCardBtn.setDisable(false);
    }

    @FXML
    private void examSelected(MouseEvent event) {
        selectedExam = examListTable.getSelectionModel().getSelectedItem();
        if ( selectedExam.getGradeStatus() == false ) gradeExamBtn.setDisable(false);
    }
    
}
