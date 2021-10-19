package branchLibrary;

import java.io.Serializable;
import static java.lang.Math.abs;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;


public class BorrowedBook implements Serializable {
    private final String TITLE;
    private final LocalDate BORROW_DATE;
    private int DAYS_VALID;
    
    // Methods
    public BorrowedBook ( String title, int validityInDays ) {
        TITLE = title;
        BORROW_DATE = LocalDate.now();
        DAYS_VALID = validityInDays;
    }
    
    public String generateReturnSlip( String librarianName, String branch, int libID, String borrowerName, int id ) {
        return (
            "\t\tBritish Council Bangladesh\n\t\t" + branch + "-Branch Library Return Slip\n" + "Name: " + borrowerName + " Membership ID:" + id  + "\nBook: " +
             TITLE + "\nBorrowed: " + BORROW_DATE + "\nValid till: " + getReturnByDate() + "\nReturned on: " + LocalDate.now() + " to Librarian" + librarianName
            + " ID:" + libID + "\n\tHave a nice day!"
        );
    }
    
    public String generateBorrowSlip( String librarianName, String branch, int libID, String borrowerName, int id ) {
        return (
            "\t\tBritish Council Bangladesh\n\t\t" + branch + "-Branch Library Borrow Slip\n" + "Name: " + borrowerName + " Membership ID:" + id  + "\nBook: " +
            TITLE + "\nBorrowed: " + BORROW_DATE + "\nValid till: " + getReturnByDate() + "\nLibrarian in-charge: " + librarianName
            + " ID:" + libID + "\n\tHave a nice day!"
        );
    }
    
    public String generateLateSlip( String librarianName, String branch, int libID, String borrowerName, int id, int due ) {
        return (
            "\t\tBritish Council Bangladesh\n\t\t" + branch + "-Branch Library Late-Return Slip\n" + "Name: " + borrowerName + " Membership ID:" + id  + "\nBook: " 
            + TITLE + "\nBorrowed: " + BORROW_DATE + "\nValid till: " + getReturnByDate() + "\nReturned on: " + LocalDate.now() + " to Librarian" + librarianName
            + " ID:" + libID + "\nIncurred fine amount (in BDT):" + due + "\n\n\tHave a nice day!"
        );
    }
    
    public String generateLostSlip( String librarianName, String branch, int libID, String borrowerName, int id, int due ) {
        return (
            "\t\tBritish Council Bangladesh\n\t\t" + branch + "-Branch Library Lost-Book Slip\n" + "Name: " + borrowerName + " Membership ID:" + id  + "\nBook: " + 
                TITLE +
            "\nBorrowed: " + BORROW_DATE + "\nValid till: " + getReturnByDate() + "\nReported on: " + LocalDate.now() + " to Librarian" + librarianName
            + " ID:" + libID + "\nIncurred fine amount (in BDT):" + due + "\n\n\tHave a nice day!"
        );
    }
    
    public boolean checkLate() {                                  // true if late
        return ( LocalDate.now().isAfter( getReturnByDate() ) );
    }
    
    public int getLateDays() {
        return ( abs( (int) DAYS.between( LocalDate.now(), getReturnByDate() ) ) );
    }

    public String getTitle() {
        return TITLE;
    }

    public LocalDate getBorrowDate() {
        return BORROW_DATE;
    }
    
    public LocalDate getReturnByDate() {
        return BORROW_DATE.plusDays(DAYS_VALID);
    }

    public int getDaysValid() {
        return DAYS_VALID;
    }
}
