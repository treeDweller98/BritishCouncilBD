package internationalexams;

import britishcouncilbd.BranchNames;
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
public class ExamCertificateShipment implements Serializable {
    public final ExamSeries SERIES;
    public final ExamSession SESSION;
    public final int YEAR;
    private ArrayList<ExamCertificate> certificates;

    ExamCertificateShipment( ExamSeries series, ExamSession session, int year ) {
        SERIES = series; SESSION = session; YEAR = year; certificates = new ArrayList<>();
    }
    
    public static ArrayList<ExamCertificateShipment> fetchCertificatesFile( BranchNames branch ) {
        String filename = branch + "_certificatesArchive.bin";
        File f = new File(filename);

        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            return (ArrayList<ExamCertificateShipment>) ois.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<ExamCertificateShipment>();
        }
    }
    
    public static void writeCertificatesFile( ArrayList<ExamCertificateShipment> certificates, BranchNames branch ) throws Exception {
        String filename = branch + "_certificatesArchive.bin";
        File f = new File(filename);
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( certificates );
        }
    }
    
    public ExamCertificate searchExamCertificate( int id ) {
        for ( var c : certificates ) {
            if ( c.candidateID == id )
                return c;
        } return null;
    }
}
