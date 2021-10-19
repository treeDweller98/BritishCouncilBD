package localevents;

import britishcouncilbd.BranchNames;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author f_ahmed
 */
public class Notice implements Serializable {
    public final LocalDate POSTED_ON;
    private String noticeText;
    
    public Notice ( String noticeText ) {
        this.POSTED_ON = LocalDate.now();
        this.noticeText = noticeText;
    }
    
    public static boolean removeNoticeFromList( String text, ArrayList<Notice> notices ) {
        for ( Notice n : notices ) {
            if ( n.noticeText.equals(text) ){
                return notices.remove(n);
            }
        } return false;
    }
    
    public static ArrayList<Notice> fetchNotices( BranchNames branch ) {
        String filename = branch + "_noticeBoard.bin";
        File f = new File(filename);
        
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            return (ArrayList<Notice>) ois.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Notice>();
        }
    }
    
    @Override
    public String toString() {
        return ( POSTED_ON.toString() + ":\n" + noticeText + "\n\n" );
    }

    public LocalDate getPostedOn() {
        return POSTED_ON;
    }

    public String getNoticeText() {
        return noticeText;
    }
   
}
