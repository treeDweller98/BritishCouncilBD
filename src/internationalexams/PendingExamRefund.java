package internationalexams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author f_ahmed
 */
public class PendingExamRefund implements Serializable {
    private static final int REFUND_PERCENT = 75;
    private String examName, candidateName;
    private int fee;
    public final int CANDIDATE_NUM;
    
    
    public PendingExamRefund( String exam, String name, int id, int payment ) {
        examName = name; candidateName = name; CANDIDATE_NUM = id; fee = Math.round( payment / REFUND_PERCENT );
    }

    public String generateRefundCompleteSlip( String branch, String receiver, String accName, int accId ) {
        return ( 
            "\t\t\tBritish Council Bangladesh\n\t\t" + branch + "Branch\n\tRefund Slip: " + examName + "\nName of Candidate: " 
           + candidateName + ", Candidate No. " + CANDIDATE_NUM + "\n" + REFUND_PERCENT + "% Refunded amount (in BDT): " + fee
           + "\nReceived by " + receiver + " on " + LocalDate.now() + "\nPayment Approved by Accountant " + accName + ", ID " + accId + "\n\n\tHave a Great Day!"     
        );
    }
    
    public static ArrayList<PendingExamRefund> fetchPendingExamRefundsFile() {
        String filename = "AllPendingExamRefunds.bin";
        File f = new File(filename);
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            return (ArrayList<PendingExamRefund>) ois.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<PendingExamRefund>();
        }
    }
    
    public static void writePendingExamRefundsFile( ArrayList<PendingExamRefund> refundsList ) throws Exception {
        String filename = "AllPendingExamRefunds.bin";
        File f = new File(filename);
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( refundsList );
        }
    }
    
    
    
    public String getExamName() {
        return examName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public int getCandidateNum() {
        return CANDIDATE_NUM;
    }

    public int getFee() {
        return fee;
    }
}
