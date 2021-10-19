package branchLibrary;

import java.io.Serializable;
import java.time.LocalDate;


public class BookBorrower implements Serializable {
    private final int MEMBER_ID, VALIDITY_IN_DAYS;
    private final LocalDate BORROW_DATE;
    
    // Methods
    BookBorrower ( int borrowerID, LocalDate borrowingDate, int validityInDays ) {
        MEMBER_ID = borrowerID;
        BORROW_DATE = borrowingDate;
        VALIDITY_IN_DAYS = validityInDays;
    }

    public int getMemberId() {
        return MEMBER_ID;
    }

    public int getDaysValid() {
        return VALIDITY_IN_DAYS;
    }

    public LocalDate getBorrowDate() {
        return BORROW_DATE;
    }
    
    public LocalDate getReturnDate() {
        return BORROW_DATE.plusDays(VALIDITY_IN_DAYS);
    }
}
