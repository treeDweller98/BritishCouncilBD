package scenes.accountant;

import finances.FinancialAccount;
import finances.Transaction;
import finances.TransactionType;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author f_ahmed
 */
public class AccountTransactionHistoryController implements Initializable {

    @FXML
    private Label accountNameLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private TableView<Transaction> historyTable;
    @FXML
    private TableColumn<Transaction, LocalDate> dateCol;
    @FXML
    private TableColumn<Transaction, String> detailsCol;
    @FXML
    private TableColumn<Transaction, Integer> amountCol;
    @FXML
    private TableColumn<Transaction, TransactionType> typeCol;
    @FXML
    private TableColumn<Transaction, String> payerCol;
    @FXML
    private TableColumn<Transaction, Integer> approverCol;
    
    private FinancialAccount selectedAccount;

    
    public void initData( FinancialAccount acc ){
        selectedAccount = acc;
        balanceLabel.setText( Integer.toString( selectedAccount.getBalance() ) );
        accountNameLabel.setText( selectedAccount.getName().name() );
        
        historyTable.setItems( FXCollections.observableArrayList( selectedAccount.getTransactions() ) );
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        dateCol.setCellValueFactory( new PropertyValueFactory<>("date") );
        amountCol.setCellValueFactory( new PropertyValueFactory<>("amoun") );
        detailsCol.setCellValueFactory( new PropertyValueFactory<>("details") );
        payerCol.setCellValueFactory( new PropertyValueFactory<>("payername") );
        approverCol.setCellValueFactory( new PropertyValueFactory<>("employee_ID") );
        typeCol.setCellValueFactory( new PropertyValueFactory<>("type") );
    }    
    
}
