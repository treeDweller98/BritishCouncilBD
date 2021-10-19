package scenes.manager;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import localevents.Notice;
import people.Manager;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class PostRemoveNoticesController implements Initializable {

    @FXML
    private TableView<Notice> noticesTable;
    @FXML
    private TableColumn<Notice, LocalDate> noticesTableDate;
    @FXML
    private TableColumn<Notice, String> noticesTableText;
    @FXML
    private TextArea newNoticeTextField;
    @FXML
    private Label presentDateLabel;
    @FXML
    private Label postSuccessLabel;
    
    private Manager manager;
    private ObservableList<Notice> observableNoticeTable;

    /**
     * Initializes the controller class.
     */
    public void initData( Manager user) {
        manager = user;
        observableNoticeTable = manager.getNoticesToDisplay();
        noticesTable.setItems( observableNoticeTable );
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        noticesTableDate.setCellValueFactory( new PropertyValueFactory<>("postedOn") );
        noticesTableText.setCellValueFactory( new PropertyValueFactory<>("noticeText") );
        presentDateLabel.setText( LocalDate.now().toString() );   
    }  

    @FXML
    private void removeNoticeButtonOnClick(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation Alert");
        a.setContentText("Are you sure you wish to remove this notice?");
        Optional<ButtonType> result = a.showAndWait();
        
        if ( result.get() == ButtonType.OK ) {
            Notice toBeRemoved = noticesTable.getSelectionModel().getSelectedItem();

            boolean checker = false;
            if ( toBeRemoved != null ){
                checker = manager.removeNotice( toBeRemoved.getNoticeText() );
            } else {
                postSuccessLabel.setText( "Please select a notice before clicking Remove" );
            }

            if (checker){
                postSuccessLabel.setText( "Successfully Removed" );     
                observableNoticeTable.remove( toBeRemoved );

                Alert b = new Alert(Alert.AlertType.INFORMATION);
                b.setTitle("Success!");
                b.setContentText("Notice has been removed");
                b.showAndWait();
            } else {
                postSuccessLabel.setText( "Failed to remove. Contact admin if issue persists." );

                Alert b = new Alert(Alert.AlertType.ERROR);
                b.setTitle("Database Error");
                b.setContentText("Could not remove notice.");
                b.showAndWait();
            }
        }
    }

    @FXML
    private void postNewNoticeButtonOnClick(ActionEvent event) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmation Alert");
        a.setContentText("Are you sure you wish to post this notice?");
        Optional<ButtonType> result = a.showAndWait();
        
        if ( result.get() == ButtonType.OK ) {
            String text = newNoticeTextField.getText();
            if ( !text.isEmpty() ) {
                Notice newNotice = manager.postNewNotice( text );

                if( newNotice != null ){
                    postSuccessLabel.setText( "Notice successfully posted" );
                    observableNoticeTable.add( newNotice );
                    newNoticeTextField.clear();

                    Alert b = new Alert(Alert.AlertType.INFORMATION);
                    b.setTitle("Success!");
                    b.setContentText("Notice has been posted");
                    b.showAndWait();
                } else {
                    postSuccessLabel.setText( "Notice failed to post. Contact admin if issue persists." );

                    Alert b = new Alert(Alert.AlertType.ERROR);
                    b.setTitle("Database Error");
                    b.setContentText("Could not post notice.");
                    b.showAndWait();
                }
            }
            else {
                postSuccessLabel.setText( "Error: you must type a notice before clicking Post" );
            }
        }
    }
    
}
