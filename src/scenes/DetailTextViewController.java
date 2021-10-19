package scenes;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class DetailTextViewController implements Initializable {

    @FXML
    private TextArea text;

    public void initData( String textDetail ) {
        text.setText(textDetail);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
