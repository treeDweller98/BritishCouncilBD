package people;

import branchLibrary.Book;
import branchLibrary.BookGenre;
import branchLibrary.BorrowedBook;
import britishcouncilbd.BranchNames;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author f_ahmed
 */
public class Librarian extends Employee {
    public static final int MIN_NUMBER_OF_COPIES_TO_KEEP = 1;
    public static final int MIN_LATE_FEE = 50;
    public static final int PER_DAY_LATE_FEE = 15;
    public static final int LOST_PENALTY_BDT = 300;
    
    public Librarian() {
        super( EmployeeType.LIBRARIAN );
    }

    public Librarian( String name, String address, String email, String phoneNumber, String empPassword, int salary, int authorisationCode, 
                        LocalDate dob, BranchNames branch, EmployeeType post, int employeeID ) {
        super( name, address, email, phoneNumber, empPassword, salary, authorisationCode, dob, branch, post, employeeID );
    }
    
    
    public String addBookToCollection( String bookTitle, String bookISBN, String bookAuthor, String bookPublisher, BookGenre bookGenre, int publishYear, 
                                             int marketPrice, int noOfTotalCopies/*, Image bookCover*/) {
        
        // Read database
        String filename = getBranch().name() + "_bookCollection.bin";
        ArrayList<Book> books = null;
        File f = new File(filename);
        if( !f.exists() || f.length() == 0 ) {
            books = new ArrayList<>();
        }
        else {
            try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
                books = (ArrayList<Book>) ois.readObject();
            }
            catch (Exception ex){
                System.out.println(ex);
                return null;
            }
        }
        
        // Work
        Book newBook = new Book( bookTitle, bookISBN, bookAuthor, bookPublisher, bookGenre, publishYear, marketPrice, noOfTotalCopies, /*bookCover, */getId() );
        books.add( newBook );
        
        // Update database
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( books );
        }
        catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
                            
        return ( "Successfully added book " + bookTitle + " to the database." );
    }
    
    
    public String returnBookFromMember( String title, int memberID ) throws Exception {
        
        // Access database
        String memberfilename = "AllMembers.bin";
        ArrayList<Member> members = null;
        File f = new File(memberfilename);
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            members = (ArrayList<Member>) ois.readObject();
        } catch ( Exception e ) {
            System.out.println(e);
            throw e;
        }
        
        String bookfilename = getBranch().name() + "_bookCollection.bin";
        ArrayList<Book> books = null;
        File f2 = new File(bookfilename);
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f2) ) ) {
            books = (ArrayList<Book>) ois.readObject();
        } catch ( Exception e ) {
            System.out.println(e);
            throw e;
        }
        
        // Update
        for ( var m : members ) {
            if ( memberID == m.ID  ) {
                if ( !m.removeBorrowedBook( title ) ) {
                    throw new Exception("Coud not update member returning book"); 
                }
                break;
            }
        }
        for ( var b : books ) {
            if ( title.equals( b.getTitle() )  ) {
                if ( !b.returnThisBook( memberID ) ) {
                    throw new Exception("Coud not update book with member return"); 
                }
                break;
            }
        }
        
        // Write back
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( members );
        } catch (Exception ex) {
            System.out.println(ex);
            throw ex;
        }
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f2) ) ) {
            oos.writeObject( books );
        } catch (Exception ex) {
            System.out.println(ex);
            throw ex;
        }
        
        return ( title + " successfully registered as \"Returned\" by member ID:" + memberID );
    }
    
    
    
    public BorrowedBook lendBookToMember( String title, int memberID, int validity ) throws Exception {
        
        // Access Book database
        String bookfilename = getBranch().name() + "_bookCollection.bin";
        ArrayList<Book> books = null;
        File f2 = new File(bookfilename);
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f2) ) ) {
            books = (ArrayList<Book>) ois.readObject();
        } catch ( Exception e ) {
            System.out.println(e); System.out.println("Failed to read book arr");
            throw e;
        }
        
        // Find and update book with borrowing info
        Book toBeBorrowed = Book.searchBook( title, books );
        if ( !toBeBorrowed.borrowThisBook(memberID, validity) ) {
            throw new Exception("Coud not update book with member info. Check if already borrowed");
        }
        
        // Acess Member database
        String memberfilename = "AllMembers.bin";
        ArrayList<Member> members = null;
        File f = new File(memberfilename);
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            members = (ArrayList<Member>) ois.readObject();
        } catch ( Exception e ) {
            System.out.println(e); System.out.println("Failed to read member arr");
            throw e;
        }
        
        // Find and update member with borrowing info
        Member borrower = Member.searchMember( memberID, members );
        BorrowedBook book = borrower.addBorrowedBook( toBeBorrowed.getTitle(), validity );
        if ( book == null ) {
            throw new Exception("Coud not update member with book info. Check if already borrowed");
        }
        
        // Write back
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( members );
        } catch (Exception ex) {
            System.out.println(ex); System.out.println("Failed to write members arr");
            throw ex;
        }
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f2) ) ) {
            oos.writeObject( books );
        } catch (Exception ex) {
            System.out.println(ex); System.out.println("Failed to write book arr");
            throw ex;
        }
        
        return book;
    }
    
    
    public int lateReturnFromMember( BorrowedBook book, int memberID ) throws Exception {
        
        // Access database
        String memberfilename = "AllMembers.bin";
        ArrayList<Member> members = null;
        File f = new File(memberfilename);
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            members = (ArrayList<Member>) ois.readObject();
        } catch ( Exception e ) {
            System.out.println(e);
            throw e;
        }
        
        String bookfilename = getBranch().name() + "_bookCollection.bin";
        ArrayList<Book> books = null;
        File f2 = new File(bookfilename);
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f2) ) ) {
            books = (ArrayList<Book>) ois.readObject();
        } catch ( Exception e ) {
            System.out.println(e);
            throw e;
        }
        
        // Update
        int due = (book.getLateDays() * PER_DAY_LATE_FEE) + MIN_LATE_FEE;
        String title = book.getTitle();        
        for ( var m : members ) {
            if ( memberID == m.ID  ) {
                if ( !m.removeBorrowedBook( title ) ) {
                    throw new Exception("Coud not update member returning book"); 
                }
                m.addDue( due );
                break;
            }
        }
        for ( var b : books ) {
            if ( title.equals( b.getTitle() )  ) {
                if ( !b.returnThisBook( memberID ) ) {
                    throw new Exception("Coud not update book with member return"); 
                }
                break;
            }
        }
        
        // Write back
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( members );
        } catch (Exception ex) {
            System.out.println(ex);
            throw ex;
        }
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f2) ) ) {
            oos.writeObject( books );
        } catch (Exception ex) {
            System.out.println(ex);
            throw ex;
        }
        
        return due;    
    }
    
    public int lostBookByMember( BorrowedBook book, int memberID ) throws Exception {
        
        // Access database
        String memberfilename = "AllMembers.bin";
        ArrayList<Member> members = null;
        File f = new File(memberfilename);
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            members = (ArrayList<Member>) ois.readObject();
        } catch ( Exception e ) {
            System.out.println(e);
            throw e;
        }
        String bookfilename = getBranch().name() + "_bookCollection.bin";
        ArrayList<Book> books = null;
        File f2 = new File(bookfilename);
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f2) ) ) {
            books = (ArrayList<Book>) ois.readObject();
        } catch ( Exception e ) {
            System.out.println(e);
            throw e;
        }
        
        // Update
        int cost = 0;
        int due = (book.getLateDays() * PER_DAY_LATE_FEE) + MIN_LATE_FEE;
        String title = book.getTitle();
        
        for ( var b : books ) {
            if ( title.equals( b.getTitle() )  ) {
                if ( !b.returnThisBook( memberID ) ) {
                    throw new Exception("Coud not update book with member return"); 
                }
                cost = b.getPrice();
                break;
            }
        }
        due += cost + LOST_PENALTY_BDT;
        for ( var m : members ) {
            if ( memberID == m.ID  ) {
                if ( !m.removeBorrowedBook( title ) ) {
                    throw new Exception("Coud not update member returning book"); 
                }
                m.addDue( due );
                break;
            }
        }
        
        
        // Write back
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( members );
        } catch (Exception ex) {
            System.out.println(ex);
            throw ex;
        }
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f2) ) ) {
            oos.writeObject( books );
        } catch (Exception ex) {
            System.out.println(ex);
            throw ex;
        }
        
        return due;    
    }
    
    
    
    public ObservableList<Book> getBookListToView() {
        String filename = getBranch() + "_bookCollection.bin";
        File f = new File( filename );
        if( !f.exists() || f.length() == 0 ) {
            return FXCollections.observableArrayList();
        }
        else {
            try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
                return FXCollections.observableArrayList( (ArrayList<Book>) ois.readObject() );
            }
            catch ( Exception e ){
                System.out.println(e);
                return FXCollections.observableArrayList();
            }
        }
    }
            
}
