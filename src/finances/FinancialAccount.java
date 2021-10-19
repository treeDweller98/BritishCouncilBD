package finances;

import britishcouncilbd.BranchNames;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *
 * @author f_ahmed
 */
public class FinancialAccount {
    public final FinancialAccountNames NAME;
    private ArrayList< Transaction > transactions;
    private int balance = 0;
    
    public FinancialAccount( FinancialAccountNames name ) {
        NAME = name;
    }
    
    public void addTransaction( Transaction transaction ){
        transactions.add( transaction );
        balance += transaction.getAmount();
    }
    
    public static ArrayList<FinancialAccount> fetchFinancialAccountsFile( BranchNames branch ) {
        String filename = branch + "_financialAccounts.bin";
        File f = new File(filename);
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            return (ArrayList<FinancialAccount>) ois.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<FinancialAccount>();
        }
    }
    
    public static void writeFinancialAccountsFile( ArrayList<FinancialAccount> list, BranchNames branch ) throws Exception {
        String filename = branch + "_financialAccounts.bin";
        File f = new File(filename);
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( list );
        }
    } 
    
    public FinancialAccountNames getName(){ return NAME; }
    public int getBalance(){ return balance; }
    public ArrayList< Transaction > getTransactions() { return transactions; }
}
