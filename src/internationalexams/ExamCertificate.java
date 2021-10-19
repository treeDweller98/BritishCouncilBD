package internationalexams;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author f_ahmed
 */
public class ExamCertificate implements Serializable {
    public final int candidateID;
    public final String candidateName;
    private boolean isHandedOver = false;
    private LocalDate handOverDate;

    ExamCertificate( int id, String name ) {
        candidateID= id; candidateName = name;
    }
    public String toString(){
        String stat = (isHandedOver)? ("\nHanded Over on " + handOverDate) : "\nAvailable";
        return "Name: " + candidateName + "ID: " + candidateID + stat;
    }
    public void setHandedOver() { 
        isHandedOver = true; handOverDate = LocalDate.now();
    }
}