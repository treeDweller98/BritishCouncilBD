package finances;

/**
 *
 * @author f_ahmed
 */
public interface TransactionMaker {
    public abstract boolean recordTransaction( FinancialAccountNames accountName, String nameOfIndividual, String details, int amount, TransactionType type );
}
