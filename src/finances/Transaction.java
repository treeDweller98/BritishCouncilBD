package finances;

import java.time.LocalDate;

/**
 *
 * @author f_ahmed
 */
public class Transaction {
    private final int EMPLOYEE_ID;
    private final String PAYERNAME;
    private final LocalDate DATE;
    private final int AMOUNT;
    private final String DETAILS;         // lost book isbn, exam or course title etc
    public final TransactionType TYPE;
    
    public Transaction( int userID, String paidBy, String details, int amount, TransactionType type ){
        DATE = LocalDate.now();
        EMPLOYEE_ID = userID;
        PAYERNAME = paidBy;
        DETAILS = details;
        AMOUNT = amount;
        TYPE = type;
    }
    
    @Override
    /*public String toString(){
        return ( DATE.toString() + ": " + TYPE.name() + " " + DETAILS + ". BDT." + AMOUNT +
                 " paid by " + PAYERNAME + ", received by employee " + EMPLOYEE_ID ); 
    }*/
    public String toString(){
        return ( TYPE.name() + " " + DETAILS + " paid by " + PAYERNAME + ", received by employee " + EMPLOYEE_ID ); 
    }
    
    public int getAmount() { 
        return AMOUNT; 
    } 

    public int getEmployee_ID() {
        return EMPLOYEE_ID;
    }

    public String getPayername() {
        return PAYERNAME;
    }

    public LocalDate getDate() {
        return DATE;
    }

    public String getDetails() {
        return DETAILS;
    }

    public TransactionType getTYPE() {
        return TYPE;
    }
    
}
