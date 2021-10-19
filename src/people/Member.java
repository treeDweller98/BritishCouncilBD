package people;

import branchLibrary.BorrowedBook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author f_ahmed
 */
public class Member implements Serializable  {
    public static final int VALIDITY_IN_DAYS = 365;      // for all members
    public static final int MAX_BORROW_CAPACITY = 3;
    public static final int REG_FEE = 1000;
    public static final int RENEW_FEE = 500;

    private String name, address, email, idDocTypeAndNum, phoneNumber;
    private LocalDate dateOfBirth, validTill;
    public final LocalDate JOINED;
    public final int ID;
    
    private ArrayList<BorrowedBook> borrowedBooks;
    private int libraryDues = 0;

    
    // Methods
    public Member( String name, String address, String email, LocalDate dob, String phoneNum, int ID, String idDocTypeAndNum ){
        this.ID = ID;
        this.name = name; this.address = address; this.email = email;
        this.dateOfBirth = dob; 
        this.phoneNumber = phoneNum;
        this.JOINED = LocalDate.now();
        this.validTill = JOINED.plusDays( VALIDITY_IN_DAYS );
        this.idDocTypeAndNum = idDocTypeAndNum;
        this.borrowedBooks = new ArrayList<>();
    }
    public Member( String name, String address, String email, LocalDate dob, String phoneNum, int ID, String idDocTypeAndNum,
                   LocalDate joinedDate, ArrayList<BorrowedBook> books, int dues, boolean renew, LocalDate prevValid ) {      // for renewing
        this.ID = ID;
        this.name = name; this.address = address; this.email = email;
        this.dateOfBirth = dob; 
        this.phoneNumber = phoneNum;
        this.JOINED = joinedDate;
        this.libraryDues = dues;
        this.validTill = prevValid;
        if (renew) {
            this.validTill = LocalDate.now().plusDays( VALIDITY_IN_DAYS );
        }
        this.borrowedBooks = books;
    }
    
    public String generateMemberIdCard() {
        return (
            "\t\tBritish Council Bangladesh\n\t\tMembership ID:" + ID + "\nName:" + name + "\nPhone:" + phoneNumber +
            "\nJoined: " + JOINED + "\nValid till: " + validTill
        );
    }
    
    public String generateNewMemberRegSlip( String recName, String branch, int id ) {
        return  (
            "\t\tBritish Council Bangladesh\n\t\t" + branch + " Branch New Membership Registration\nID:" + ID + "\nName:" + name + "\nPhone:" + phoneNumber +
            "\nJoined: " + JOINED + "\nValid till: " + validTill + "\n Registered by " + recName + " ID:" + id + "\nFee: BDT." + REG_FEE
        );
    }
    
    public String generateMemberRenewSlip( String recName, String branch, int id ) {
        return  (
            "\t\tBritish Council Bangladesh\n\t\t" + branch + " Branch Membership Renewal\nID:" + ID + "\nName:" + name + "\nPhone:" + phoneNumber +
            "\nJoined: " + JOINED + "\nValid till: " + validTill + "\n Registered by " + recName + " ID:" + id + "\nFee: BDT." + RENEW_FEE
        );
    }
    
    public String generateLibraryDuePaidSlip( String branch, int paidAmount ) {
         return  (
            "\t\tBritish Council Bangladesh\n\t\t" + branch + " Branch Library Dues Payment Slip\nID:" + ID + "\nName:" + name + "\nPhone:" + phoneNumber +
            "\n\tPaid (in BDT): " + paidAmount + "\nHave a Nice Day!" 
        );
    }
    
    public boolean removeBorrowedBook( String title ) {
        for ( BorrowedBook book : borrowedBooks ) {
            if ( title.equals( book.getTitle() ) ) {
                return borrowedBooks.remove( book );
            }
        }
        return false;
    }
    
    public BorrowedBook addBorrowedBook( String title, int validity ) {        
        BorrowedBook newBorrowedBook = new BorrowedBook( title, validity );
        for ( BorrowedBook book : borrowedBooks ) {
            if ( title.equals( book.getTitle() ) ) {
                return null;
            }
        }
        if ( !borrowedBooks.add( newBorrowedBook ) ) {
            return null;
        } return newBorrowedBook;
    }
    
    public ObservableList<BorrowedBook> getBorrowedBooksListToView() {
        return FXCollections.observableArrayList( borrowedBooks );
    }
    
    public static Member searchMember( int memberIdNum ) throws Exception {
        
        String filename = "AllMembers.bin";
        ArrayList<Member> members = null;
        File f = new File(filename);
        if( !f.exists() || f.length() == 0 ) {
            return null;
        } else {
            try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
                members = (ArrayList<Member>) ois.readObject();
            } catch ( Exception e ) {
                System.out.println(e);
                throw e;
            }
        }
        
        for ( var member : members ){
            if ( memberIdNum == member.ID  )       // needs a better algorithm
                return member;
        } return null;
    }
    
    public static Member searchMember( int memberIdNum, ArrayList<Member> members ) {
        for ( Member m : members ) {
            if ( m.ID == memberIdNum ) {
                return m;
            }
        } return null;
    }
    
    public static Member searchMemberV2( int id ) {
        ArrayList<Member> members = fetchMembersList();
         for ( Member m : members ) {
            if ( m.ID == id ) {
                return m;
            }
        } return null;
    }
    
    public static ArrayList<Member> fetchMembersList() {
        String filename = "AllMembers.bin";
        File f = new File(filename);
        
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            return (ArrayList<Member>) ois.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Member>();
        }
    }
    public static void writeMemberFileToDatabase ( ArrayList<Member> members ) throws Exception {
        String filename = "AllMembers.bin";
        File f = new File(filename);
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( members );
        }
    }
    
    public static ObservableList<Member> fetchDueMembersList() {
        ArrayList<Member> allMembers = fetchMembersList();
        ArrayList<Member> membersWithDues = new ArrayList<>();
        for ( Member m : allMembers ) {
            if ( m.hasOutstandingDues() ) {
                membersWithDues.add(m);
            }
        } return FXCollections.observableArrayList( membersWithDues );
    }
    
    public static ObservableList<Member> fetchBorrowingMembersListToView() {
        ArrayList<Member> allMembers = fetchMembersList();
        ArrayList<Member> borrowers = new ArrayList<>();
        for ( Member m : allMembers ) {
            if ( m.borrowedBooks.size() > 0 ) {
                borrowers.add(m);
            }
        } return FXCollections.observableArrayList(borrowers);
    }
    
    public String getBorrowedBookNames() {
        String names = null;
        for ( BorrowedBook b : borrowedBooks ) {
            names += "  |  " + b.getTitle();
        } return names;
    }
    
    public boolean hasOutstandingDues() {
        if ( libraryDues > 0 ) return true;
        return false;
    }
    
    public void addDue( int amount ) {
        libraryDues += amount;
    }
    public boolean clearDue() {
        libraryDues = 0; return true;
    }
    
    public int howManyBorrowedBook() {
        return borrowedBooks.size();
    }

    
    
    
    
    public String getIdDocTypeAndNum() {
        return idDocTypeAndNum;
    }

    public int getLibraryDues() {
        return libraryDues;
    }
    public ArrayList<BorrowedBook> getBorrowedBooks() {
        return borrowedBooks;
    }

    public static int getValidityInDays() {
        return VALIDITY_IN_DAYS;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDate getValidTill() {
        return validTill;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getJoined() {
        return JOINED;
    }

    public int getId() {
        return ID;
    }
    
}
