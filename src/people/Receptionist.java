package people;

import branchLibrary.BorrowedBook;
import britishcouncilbd.BranchNames;
import finances.FinancialAccount;
import finances.FinancialAccountNames;
import finances.Transaction;
import finances.TransactionMaker;
import finances.TransactionType;
import internationalexams.Candidate;
import internationalexams.CandidateSeekingRefundApproval;
import internationalexams.ExamCertificate;
import internationalexams.ExamCertificateShipment;
import internationalexams.ExamSeries;
import internationalexams.ExamSession;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author f_ahmed
 */
public class Receptionist extends Employee implements TransactionMaker {
    
    public Receptionist( String name, String address, String email, String phoneNumber, String empPassword, int salary, int authorisationCode, 
                            LocalDate dob, BranchNames branch, EmployeeType post, int employeeID ) {
        super( name, address, email, phoneNumber, empPassword, salary, authorisationCode, dob, branch, post, employeeID );
    }
    
    public Receptionist() {
        super(EmployeeType.RECEPTIONIST);
    }
    
    @Override 
    public boolean recordTransaction( FinancialAccountNames accountName, String nameOfIndividual, String details, int amount, TransactionType type ) {
        ArrayList<FinancialAccount> accounts = FinancialAccount.fetchFinancialAccountsFile( this.getBranch() );
        
        FinancialAccount desiredAccount = null;
        for ( var a : accounts ) {
            if( a.NAME.equals( accountName ) ) {
                desiredAccount = a; break;
            }
        } if ( desiredAccount == null ) return false;
        
        desiredAccount.addTransaction( new Transaction( this.getId(), nameOfIndividual, details, amount, type ) );
        
        try {
            FinancialAccount.writeFinancialAccountsFile( accounts, this.getBranch() );
        } catch ( Exception e ) {
            System.out.println(e); return false;
        } return true;
    }

    public Member updateMember ( String name, String address, String email, LocalDate dob, String phoneNum, int id, String idDocTypeAndNum,
                   LocalDate joinedDate, ArrayList<BorrowedBook> books, int dues, boolean renew, LocalDate prevValid ) {
        
        // Remove previous entry
        ArrayList<Member> members = Member.fetchMembersList();
        for ( Member m : members ) {
            if ( m.ID == id ) {
                members.remove(m);
            }
        }
        
        // Add updated entry
        Member newMember = new Member(  name, address, email, dob, phoneNum, id, idDocTypeAndNum, joinedDate, books, dues, renew, prevValid );
        members.add(newMember);
        
        // Overwrite
        try {
            Member.writeMemberFileToDatabase(members);
        } catch (Exception e) {
            return null;
        } return newMember;
    }
    
    public Member newMemberRegistration( String name, String address, String email, LocalDate dob, String phoneNum, int id, String idDocTypeAndNum ) {
        // Open database
        ArrayList<Member> members = Member.fetchMembersList();
        
        // Create,Add member
        Member newMember = new Member(  name, address, email, dob, phoneNum, id, idDocTypeAndNum );
        members.add(newMember);
        
        // Write back;
        try {
            Member.writeMemberFileToDatabase(members);
        } catch (Exception e) {
            return null;
        } return newMember;
    }
    
    public boolean applyForExamRefund ( ExamSeries series, ExamSession session, int year, Candidate candidate, String reasons ) {
        ArrayList<CandidateSeekingRefundApproval> waitList = CandidateSeekingRefundApproval.fetchSeekingRefundApprovalFile();
        waitList.add( new CandidateSeekingRefundApproval( series, session, year, candidate, reasons ) );
        
        try {
            CandidateSeekingRefundApproval.writeSeekingRefundApprovalFile( waitList );
        } catch (Exception e) {
            System.out.println(e); return false;
        } return true;
    }
    
    public ExamCertificate queryCertificate( ExamSeries series, ExamSession session, int year, int id ) {
        ArrayList<ExamCertificateShipment> allCertificates = ExamCertificateShipment.fetchCertificatesFile( this.getBranch() );
        ExamCertificateShipment desired = null;
        for ( var s : allCertificates ) {
            if ( s.SERIES == series && s.SESSION == session && s.YEAR == year ) {
                desired = s; break;
            }
        } if (desired == null) return null;
        
        return desired.searchExamCertificate(id);
    }
    
    public ExamCertificate markCertificateAsHandedOver( ExamSeries series, ExamSession session, int year, int id ) {
        ArrayList<ExamCertificateShipment> allCertificates = ExamCertificateShipment.fetchCertificatesFile( this.getBranch() );
        ExamCertificateShipment desired = null;
        for ( var s : allCertificates ) {
            if ( s.SERIES == series && s.SESSION == session && s.YEAR == year ) {
                desired = s; break;
            }
        } if (desired == null) return null;
        
        ExamCertificate ec = desired.searchExamCertificate( id ); ec.setHandedOver(); 
        
        try {
            ExamCertificateShipment.writeCertificatesFile( allCertificates, this.getBranch() );
        } catch (Exception e) {
            System.out.println(e); return null;
        } return ec;
    }
}
