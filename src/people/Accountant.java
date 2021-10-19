package people;

import britishcouncilbd.BranchNames;
import finances.FinancialAccount;
import finances.FinancialAccountNames;
import finances.Transaction;
import finances.TransactionMaker;
import finances.TransactionType;
import internationalexams.Candidate;
import internationalexams.Exam;
import internationalexams.ExamSeries;
import internationalexams.ExamSession;
import internationalexams.PendingExamRefund;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import localevents.Course;
import localevents.LocalEventRegistration;
import localevents.OneOffEvent;

/**
 *
 * @author f_ahmed
 */
public class Accountant extends Employee implements TransactionMaker {
    
    public Accountant( String name, String address, String email, String phoneNumber, String empPassword, int salary, int authorisationCode, 
                            LocalDate dob, BranchNames branch, EmployeeType post, int employeeID ) {
        super( name, address, email, phoneNumber, empPassword, salary, authorisationCode, dob, branch, post, employeeID );
    }
    
    public Accountant() {
        super(EmployeeType.ACCOUNTANT);
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
    
    public ObservableList<FinancialAccount> fetchAllAccountsToView() {
        return FXCollections.observableArrayList( FinancialAccount.fetchFinancialAccountsFile( this.getBranch() ) );
    }
    
    public Candidate validateCandidateRegistration( ExamSeries series, ExamSession session, int year, int candidateID ) {
        ArrayList<Exam> exams = Exam.fetchExamFileFromDatabase( series );
        
        Exam examToRegFor = null;
        for ( Exam e : exams) {
            if ( e.session == session && e.year == year ) {
                examToRegFor = e; break;
            }
        } if (examToRegFor == null) return null;
        
        Candidate candidateToValidate = null;
        for ( Candidate c : examToRegFor.getAwaitingPayment() ) {
            if ( c.getCandidateNumber() == candidateID ) {
                candidateToValidate = c; break;
            }
        } if (candidateToValidate == null) return null;
        
        candidateToValidate.setFeePaidOn();
        examToRegFor.setValidCandidates( candidateToValidate );
        
        try {
            Exam.writeExamFileToDatabase( exams, series );
        } catch (Exception e) {
            System.out.println(e); return null;
        }
        
        return candidateToValidate;
    } 
    
    public boolean validateLocalEventReg( LocalEventRegistration reg ) {
        boolean stat = false;
        if ( reg.TYPE.equals( "Course" ) ) {
            stat = Course.registerMemberForCourse( this.getBranch(), reg.EVENT, reg.ID, reg.NAME );
        } else if ( reg.TYPE.equals( "OneOffEvent" ) ) {
            stat = OneOffEvent.registerMemberForOneOffEvent( this.getBranch(), reg.EVENT, reg.ID, reg.NAME );
        }
        LocalEventRegistration.removePendingPaymentsRegEntry( this.getBranch(), reg );
        
        return stat;
    }
    
    public boolean clearLibraryFeeDues( int memberId ) {
        ArrayList<Member> members = Member.fetchMembersList();
        boolean stat = false;
        for ( var m : members ) {
            if ( m.ID == memberId ) {
                stat = m.clearDue(); break;
            }
        } if ( stat == false ) return false;
        
        try {
            Member.writeMemberFileToDatabase(members);
        } catch ( Exception e ) {
            System.out.println(e); return false;
        } return stat;
    }
    
    public boolean refundExam( int candidateID ) {
        ArrayList<PendingExamRefund> allRefunds =  PendingExamRefund.fetchPendingExamRefundsFile();
        
        for ( var r : allRefunds ) {
            if ( r.CANDIDATE_NUM == candidateID ) {
                allRefunds.remove(r); break;
            }
        }
       
        try {
            PendingExamRefund.writePendingExamRefundsFile(allRefunds);
        } catch ( Exception e ) {
            System.out.println(e); return false;
        } return true;
    }
}
