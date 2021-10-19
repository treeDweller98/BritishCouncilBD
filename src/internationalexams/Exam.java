package internationalexams;

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
public class Exam implements Serializable {
    public final ExamSeries series;
    public final ExamSession session;
    public final int year;
    private ArrayList< String > venues;
    private ArrayList< ExamSubject > subjectsOffered;
    private LocalDate regStart, regEnd, lastDayOfCancellation;
    
    private ArrayList< Candidate > validCandidates;
    private ArrayList< Candidate > awaitingPayment;
    
    
    // Methods
    public Exam ( ExamSeries series, ExamSession session, int year, ArrayList< String > venues, ArrayList< ExamSubject > subjectsOffered, 
                    LocalDate regStart, LocalDate regEnd, LocalDate lastDayOfCancellation ) {
        
        this.series = series;
        this.session = session;
        this.year = year;
        this.venues = venues;
        this.subjectsOffered = subjectsOffered;
        this.regStart = regStart;
        this.regEnd = regEnd;
        this.lastDayOfCancellation = lastDayOfCancellation;
        this.validCandidates = new ArrayList<>();
        this.awaitingPayment = new ArrayList<>();
    }
    
    public String toString() {
        return ( year + " " + series + " " + session  );
    }
    
    public String fullDetails() {
        String subjects = "\n~~~Exam Subjects Offered~~\n\n";
        for ( ExamSubject s : subjectsOffered ) {
            subjects += "\t" + s + "\n";
        }
        String venuesList = "\n\nVenues available: \n" + venues ;
        String dates = "\n\n\t~~Important Dates~~\nRegistration begins: " + regStart + "\nRegistration ends: " + regEnd + 
                       "\nLast Date of Refund Application:" + lastDayOfCancellation + "\n\n_______________";
        
        return ( "\t\t\t" + this.toString() + subjects + venuesList + dates );
    }
    
    public void setValidCandidates( Candidate examinee ) {
        validCandidates.add( examinee );
        awaitingPayment.remove( examinee );
    }
    
    public void removeAValidCandidate( int id ) {
        for ( var c : validCandidates ) {
            if ( c.CANDIDATE_NUM == id ) {
                validCandidates.remove(c); return;
            }
        }
    }
    
    public static Candidate registerForExam( ExamSeries series, int year, ExamSession session, String name, String passportNum, String email, String phone,
                                            String address, LocalDate dob, int candidateNumber, String[] subjectsRegistered, String venueChosen, int fee ) {       
        
        ArrayList<Exam> exams = fetchExamFileFromDatabase( series );
        
        Exam examToRegisterFor = null;
        for ( Exam e : exams ) {
            if ( e.year == year && e.session == session ) {
                examToRegisterFor = e; break;
            }
        } if ( examToRegisterFor == null ) return null;
        
        Candidate candidate = new Candidate( name, passportNum, email, phone, address, dob, candidateNumber, subjectsRegistered, venueChosen, fee );
        examToRegisterFor.awaitingPayment.add( candidate );
        
        try {
            writeExamFileToDatabase( exams, series );
        } catch ( Exception e ) {
            return null;
        }
        return candidate;
    }
    
    public static ObservableList<Exam> getAllExamsToView() {  
        ArrayList<Exam> exams = new ArrayList<>();
        exams.addAll( Exam.fetchExamFileFromDatabase(ExamSeries.IAL) );
        exams.addAll( Exam.fetchExamFileFromDatabase(ExamSeries.IELTS) );
        exams.addAll( Exam.fetchExamFileFromDatabase(ExamSeries.IGCSE) );
        
        return FXCollections.observableArrayList(exams);
    }
    
    public static ArrayList<Exam> fetchExamFileFromDatabase ( ExamSeries series ) {
        String filename = "AllActive" + series + ".bin";
        File f = new File(filename);
        
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            return (ArrayList<Exam>) ois.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Exam>();
        }
    }
    
    public static void writeExamFileToDatabase ( ArrayList<Exam> examArrayList, ExamSeries series ) throws Exception {
        String filename = "AllActive" + series + ".bin";
        File f = new File(filename);
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( examArrayList );
        }
    }
    
    
    
    
    
    public String getFullNameOfExam(){
        return ( year + " " + series + " " + session  );
    }

    public LocalDate getRegStart() {
        return regStart;
    }

    public LocalDate getRegEnd() {
        return regEnd;
    }

    public LocalDate getLastDayOfCancellation() {
        return lastDayOfCancellation;
    }

    public ArrayList<Candidate> getValidCandidates() {
        return validCandidates;
    }

    public ArrayList<Candidate> getAwaitingPayment() {
        return awaitingPayment;
    }

    public ArrayList<String> getVenues() {
        return venues;
    }

    public ArrayList<ExamSubject> getSubjectsOffered() {
        return subjectsOffered;
    }
    
    
    
    
}
    