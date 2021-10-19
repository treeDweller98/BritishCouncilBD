package scenes.manager;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import people.Employee;
import people.Manager;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class CreateLocalEventController implements Initializable {

    @FXML
    private Label errorStatusMessageLabel;
    @FXML
    private RadioButton eventTypeCourse;
    @FXML
    private ToggleGroup eventType;
    @FXML
    private RadioButton eventTypeOneOff;
    @FXML
    private TextField eventTitle;
    @FXML
    private TextField eventFee;
    @FXML
    private TextField eventSchedule;
    @FXML
    private TextField instructorName;
    @FXML
    private TextField instructorID;
    @FXML
    private DatePicker regEndDatePicker;
    @FXML
    private TextField maxCapacity;
    @FXML
    private TextArea eventDetailsText;
    
    private Manager manager = new Manager();
    
    public void initData( Employee user ) {
        //manager = user;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void courseChosen(ActionEvent event) {
        instructorName.setEditable(true);
        instructorID.setEditable(true);
    }

    @FXML
    private void oneOffChosen(ActionEvent event) {
        instructorName.clear();
        instructorID.clear();
        instructorName.setEditable(false);
        instructorID.setEditable(false);
    }

    @FXML
    private void createEventButtonOnClick(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation Alert");
        a.setContentText("Are you sure you wish to create this event?");
        Optional<ButtonType> result = a.showAndWait();
        
        String stat = "";
        if(result.get() == ButtonType.OK){
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Requesting Authorisation");
            dialog.setContentText("Enter Authorisation Code to Proceed");
            Optional<String> auth = dialog.showAndWait();
            
            if ( manager.authenticate(auth.get()) ) {
                try {
                    if ( eventTypeCourse.isSelected() ) {
                        stat = manager.createNewLocalEvent( eventTitle.getText(), eventDetailsText.getText(), eventSchedule.getText(), 
                                                            Integer.parseInt(eventFee.getText()), Integer.parseInt(maxCapacity.getText()), regEndDatePicker.getValue(), 
                                                            instructorName.getText(), Integer.parseInt(instructorID.getText()) );
                    }
                    else {
                        stat = manager.createNewLocalEvent( eventTitle.getText(), eventDetailsText.getText(), eventSchedule.getText(), 
                                                            Integer.parseInt(eventFee.getText()), Integer.parseInt(maxCapacity.getText()), regEndDatePicker.getValue() );
                    }
                }
                catch( IllegalArgumentException e ){
                    errorStatusMessageLabel.setText( "Error: please ensure data fields are all properly entered" );
                    System.out.println(e);
                    Alert c = new Alert(Alert.AlertType.ERROR);
                    c.setTitle("Input Error");
                    c.setContentText("please ensure data fields are all properly entered");
                    c.showAndWait();
                }

                if( stat == "" );
                else if ( stat == null ){
                    errorStatusMessageLabel.setText( "Database Error: Could not add event to database.");
                    Alert b = new Alert(Alert.AlertType.ERROR);
                    b.setTitle("Database Error");
                    b.setContentText("Could not add event to database.");
                    b.showAndWait();
                }
                else {
                    errorStatusMessageLabel.setText( stat );
                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setTitle("Success!");
                    b.setContentText(stat);
                    b.showAndWait();

                    eventTitle.clear(); eventDetailsText.clear(); eventSchedule.clear(); eventFee.clear();
                    maxCapacity.clear(); instructorName.clear(); instructorID.clear();         
                }
            } else {
                Alert c = new Alert(Alert.AlertType.ERROR);
                c.setTitle("Failed");
                c.setContentText("Could not authenticate user");
                c.showAndWait();
            } 
        }
    }
    
}
