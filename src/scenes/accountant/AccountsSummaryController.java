package scenes.accountant;

import finances.FinancialAccount;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import people.Accountant;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class AccountsSummaryController implements Initializable {

    @FXML
    private TableView<FinancialAccount> accountsSummaryTable;
    @FXML
    private TableColumn<FinancialAccount, String> accountCol;
    @FXML
    private TableColumn<FinancialAccount, Integer> balanceCol;
    
    private Accountant accountant;
    
    public void initData( Accountant user ) {
        accountant = user;
        accountsSummaryTable.setItems( accountant.fetchAllAccountsToView() );
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        accountCol.setCellValueFactory( new PropertyValueFactory<>("name") );
        balanceCol.setCellValueFactory( new PropertyValueFactory<>("balance") );
    }    

    @FXML
    private void viewHistoryButtonOnClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AccountTransactionHistory.fxml"));
        
        Parent AccountTransactionHistoryParent = null;
        try {
            AccountTransactionHistoryParent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(AccountsSummaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Scene AccountTransactionHistoryScene = new Scene( AccountTransactionHistoryParent );
        
        //access the controller
        AccountTransactionHistoryController controller = loader.getController();
        controller.initData( accountsSummaryTable.getSelectionModel().getSelectedItem() );
        
        // Show scene in new window
        Stage window = new Stage();
        window.setTitle( "Account Transaction History" );
        window.setScene( AccountTransactionHistoryScene );
        window.show();
    }
    
}
