package internationalexams;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.ObservableList;

/**
 *
 * @author f_ahmed
 */
public class Candidate implements Serializable {
    public final int CANDIDATE_NUM;
    private String[] subjectsRegistered;
    private String venueChosen;
    private String name, passportNum, email, phone, address;
    private LocalDate dateOfBirth;
    private LocalDate feePaidOn;
    private int fee;
    
    
    public Candidate( String name, String passportNum, String email, String phone, String address,
                        LocalDate dob, int candidateNumber, String[] subjectsRegistered, String venueChosen, int fee  ) {
        
        this.name = name;
        this.passportNum = passportNum;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dob;
        this.CANDIDATE_NUM = candidateNumber;
        this.venueChosen = venueChosen;
        this.subjectsRegistered = subjectsRegistered;
        this.fee = fee;
    }
    
    public static Candidate searchCandidate( int searchedCandidateID, ArrayList<Candidate> candidateList ) {
        
        for ( var candidate : candidateList ){
            if ( candidate.CANDIDATE_NUM == searchedCandidateID )
                return candidate;
        }
        return null;
    }
    
    public String generateRegSlip( String examDetail, LocalDate lastPayDate, ObservableList<ExamSubject> subsChosen ) {
        String header = "\t\t\tBritish Council Bangladesh\n\t\t\t" + examDetail + "Registration Form: " + CANDIDATE_NUM + "\n\n";
        String personalDetails = "Personal Details\nName: " + name + "\nDate Of Birth: " + dateOfBirth + "\nAddress " + address + "\nPhone:" + phone + "\tEmail: " + email;
        String subjects = "\n\n~~ Subjects chosen and their fees ~~\n";
        for ( ExamSubject s : subsChosen ) {
            subjects += "\t" + s.toString() + "\n";
        }
        String paymentDetails = "\nGrand Total Fee (in BDT): " + fee + "\tLast Date of Payment: " + lastPayDate;
        String ending = """
                        
                        
                        
                                    Please print out this form and attach 2 copies passport-sized photos and the fee payment slip.
                        Submit your form the into the designated drop box at a British Council branch or at any Standard Chartered Service1 branch.""";
        return ( header + personalDetails + subjects + paymentDetails + ending );
    }
    
    public String generateValidatedRegSlip( String examDetail, String paidBy, String accountantName, int accountantID ) {
        String header = "\t\t\tBritish Council Bangladesh\n\t\t\t" + examDetail + "Registration Form: " + CANDIDATE_NUM + "\n\n";
        String personalDetails = "Personal Details\nName: " + name + "\nDate Of Birth: " + dateOfBirth + "\nAddress " + address + "\nPhone:" + phone + "\tEmail: " + email;
        String subjects = "\n\n~~ Subject Codes ~~\n";
        for ( String s : subjectsRegistered ) {
            subjects += "\t" + s + "\n";
        }
        String paymentDetails = "\nGrand Total Fee (in BDT): " + fee + "\tPayment Completed On: " + feePaidOn;
        String ending = "Paid by: " + paidBy + "\tApproved by:" + accountantName + ", ID:" + accountantID;
        return ( header + personalDetails + subjects + paymentDetails + ending );
    }
    
    public String generateRefundApplicationSlip( String examDetail, String receptionistName, int recID ) {
        String header = "\t\t\tBritish Council Bangladesh\n\t\t\t" + examDetail + "Refund Application Form: " + CANDIDATE_NUM + "\n\n";
        String personalDetails = "Personal Details\nName: " + name + "\nDate Of Birth: " + dateOfBirth + "\nAddress " + address + "\nPhone:" + phone + "\tEmail: " + email;
        String subjects = "\n\n~~ Subject Codes ~~\n";
        for ( String s : subjectsRegistered ) {
            subjects += "\t" + s + "\n";
        }
        String paymentDetails = "\nGrand Total Fee (in BDT): " + fee + "\tPayment Completed On: " + feePaidOn;
        String ending = "\n\nApplication Date: " + LocalDate.now() + "\tby Receptionist:" + receptionistName + ", ID:" + recID;
        return ( header + personalDetails + subjects + paymentDetails + ending );
    }
    

    public void setFeePaidOn() { feePaidOn = LocalDate.now(); }
    
    public String getEmail() {
        return email;
    }

    public int getFee() {
        return fee;
    }

    public LocalDate getFeePaidOn() {
        return feePaidOn;
    }
     
    public int getCandidateNumber() { 
        return CANDIDATE_NUM; 
    } 
    
    public String getName() { 
        return name;
    }

    public String[] getSubjectsRegistered() {
        return subjectsRegistered;
    }

    public String getVenueChosen() {
        return venueChosen;
    }

    public String getPassportNum() {
        return passportNum;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
           
}
