/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package branchLibrary;

import britishcouncilbd.BranchNames;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javafx.scene.image.Image;
import java.time.LocalDate;
import java.util.ArrayList;
import people.Librarian;



public class Book implements Serializable {
    private String title, isbn, author, publisher;
    private BookGenre genre; 
    private int year, price, availableCopies, totalCopies;
    //private Image coverPhoto;
    private ArrayList<BookBorrower> borrowerList;
    private final LocalDate dateAdded;
    private final int ADDED_BY;             // librarian ID
    
    
    // Methods
    public Book ( String bookTitle, String bookISBN, String bookAuthor, String bookPublisher, BookGenre bookGenre, int publishYear, 
                        int marketPrice, int noOfTotalCopies, /*Image bookCover,*/ int addedBy ) {
        
        title = bookTitle; isbn = bookISBN; author = bookAuthor; publisher = bookPublisher;
        genre = bookGenre; year = publishYear; price = marketPrice;
        availableCopies = noOfTotalCopies; totalCopies = noOfTotalCopies;
        //coverPhoto = bookCover;
        borrowerList = new ArrayList<>();
        dateAdded = java.time.LocalDate.now();
        ADDED_BY = addedBy;
    }
    
    
    public static Book searchBook( String searchedBookTitle, BranchNames branch ) throws Exception {
        
        String filename = branch + "_bookCollection.bin";
        ArrayList<Book> books = null;
        File f = new File(filename);
        if( !f.exists() || f.length() == 0 ) {
            return null;
        }
        else {
            try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
                books = (ArrayList<Book>) ois.readObject();
            } catch ( Exception e ){
                System.out.println(e);
                throw e;
            }
        }
        
        for ( Book book : books ){ System.out.println(book.title);
            if ( searchedBookTitle.equals( book.title ) ) {      // needs a better algorithm
                return book;
            }
        }
        return null;
    }
    
    public static Book searchBook( String title, ArrayList<Book> books ) {
        for ( Book b : books ) {
            if ( title.equals( b.getTitle() ) ) {
                return b;
            }
        } return null;
    }
    
    public static int getBookPrice( String title, BranchNames branch ) {
        Book b;
        try {
            b = searchBook( title, branch );
        } catch ( Exception e ) {
            System.out.println(e);
            return -1;
        } return b.getPrice();
    }
            
    
    
    public boolean borrowThisBook( int borrowingMemberID, int borrowingPeriodInDays ) {
        
        // Check if already borrowed by this member
        for ( var borrower : borrowerList ) {
            if ( borrower.getMemberId() == borrowingMemberID )
                return false;
        }
        if ( availableCopies > Librarian.MIN_NUMBER_OF_COPIES_TO_KEEP ) {
            // Update Book to reflect borrowing has taken place
            borrowerList.add( new BookBorrower( borrowingMemberID, java.time.LocalDate.now(), borrowingPeriodInDays ) );
            availableCopies--;
            return true;
        }
        return false;
    }
    
    public boolean returnThisBook( int borrowingMemberID ) {
        boolean stat = false;
        for ( var borrower : borrowerList ) {
            if ( borrower.getMemberId() == borrowingMemberID ) {
                stat = borrowerList.remove( borrower );
                break;
            }
        }
        availableCopies++;
        return stat;
    }
    
    public void markCopyAsLost() {
         totalCopies--;
         availableCopies--;
     }
    
    
    

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public BookGenre getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public int getPrice() {
        return price;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    /*public Image getCoverPhoto() {
    return coverPhoto;
    }*/

    public ArrayList<BookBorrower> getBorrowerList() {
        return borrowerList;
    }

    public LocalDate getDateAdded() {
        return dateAdded;
    }
}
