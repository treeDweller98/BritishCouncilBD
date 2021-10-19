package internationalexams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author f_ahmed
 */
public class CandidateSeekingRefundApproval implements Serializable {
    public final int YEAR;
    public final ExamSeries SERIES;
    public final ExamSession SESSION;
    private Candidate candidate;
    private String reasons;
    
    
    public CandidateSeekingRefundApproval( ExamSeries series, ExamSession sessionName, int year, Candidate candidate, String reasons ) {
        this.YEAR = year;
        this.SERIES = series;
        this.SESSION = sessionName;
        this.candidate = candidate;
        this.reasons = reasons;
    }
    
    public static ArrayList<CandidateSeekingRefundApproval> fetchSeekingRefundApprovalFile() {
        String filename = "AllCandidatesSeekingRefundApproval.bin";
        File f = new File(filename);
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            return (ArrayList<CandidateSeekingRefundApproval>) ois.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<CandidateSeekingRefundApproval>();
        }
    }
    
    public static void writeSeekingRefundApprovalFile( ArrayList<CandidateSeekingRefundApproval> seekers ) throws Exception {
        String filename = "AllCandidatesSeekingRefundApproval.bin";
        File f = new File(filename);
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( seekers );
        }
    }

    public String getFullNameOfExam() {
        return SERIES + " " + SESSION + " " + YEAR;
    }
    public String getCandidateName() {
        return candidate.getName();
    }
    public int getCandidateId() {
        return candidate.CANDIDATE_NUM;
    }
            
    public Candidate getCandidate() {
        return candidate;
    }

    public String getReasons() {
        return reasons;
    } 
}
