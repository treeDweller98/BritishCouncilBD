package people;

import britishcouncilbd.BranchNames;
import internationalexams.CandidateSeekingRefundApproval;
import internationalexams.Exam;
import internationalexams.PendingExamRefund;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart.Data;
import localevents.Course;
import localevents.CourseExam;
import localevents.CourseStudent;

/**
 *
 * @author f_ahmed
 */
public class Instructor extends Employee {
    
    
    public Instructor( String name, String address, String email, String phoneNumber, String empPassword, int salary, int authorisationCode, 
                         LocalDate dob, BranchNames branch, EmployeeType post, int employeeID ) {
        super( name, address, email, phoneNumber, empPassword, salary, authorisationCode, dob, branch, post, employeeID );
    }
    
    public Instructor() {
        super(EmployeeType.INSTRUCTOR);
    }
    
    public boolean approveRefundRequest( CandidateSeekingRefundApproval refundee ) {
        
        // Remove from approval queue
        ArrayList<CandidateSeekingRefundApproval> seekingApprovalList =  CandidateSeekingRefundApproval.fetchSeekingRefundApprovalFile(); 
        
        int id = refundee.getCandidate().CANDIDATE_NUM;
        for ( var r : seekingApprovalList ) {
            if ( r.getCandidate().CANDIDATE_NUM == id ) {
                seekingApprovalList.remove(r); break;
            }
        }
        try {
            CandidateSeekingRefundApproval.writeSeekingRefundApprovalFile(seekingApprovalList);
        } catch ( Exception e ) {
            System.out.println(e); return false;
        }
        
        // Remove from Exam
        ArrayList<Exam> exams = Exam.fetchExamFileFromDatabase( refundee.SERIES );
        for ( var e : exams ) {
            if ( e.session == refundee.SESSION && e.year == refundee.YEAR ) {
                e.removeAValidCandidate(id);
            }
        }
        try {
            Exam.writeExamFileToDatabase( exams, refundee.SERIES );
        } catch ( Exception e ) {
            System.out.println(e); return false;
        }
        
        // Add a pending refund entry
        ArrayList<PendingExamRefund> pendingRefunds =  PendingExamRefund.fetchPendingExamRefundsFile();
        pendingRefunds.add( new PendingExamRefund( 
            refundee.getFullNameOfExam(), refundee.getCandidate().getName(), 
            refundee.getCandidate().CANDIDATE_NUM, refundee.getCandidate().getFee() ) 
        );
        try {
            PendingExamRefund.writePendingExamRefundsFile( pendingRefunds );
        } catch ( Exception e ) {
            System.out.println(e); return false;
        } return true;
    }
    
    public boolean disapproveRefundRequest( CandidateSeekingRefundApproval refundee ) {
        
        // Remove from approval queue
        ArrayList<CandidateSeekingRefundApproval> seekingApprovalList =  CandidateSeekingRefundApproval.fetchSeekingRefundApprovalFile(); 
        int id = refundee.getCandidate().CANDIDATE_NUM;
        for ( var r : seekingApprovalList ) {
            if ( r.getCandidate().CANDIDATE_NUM == id ) {
                seekingApprovalList.remove(r); break;
            }
        }
        try {
            CandidateSeekingRefundApproval.writeSeekingRefundApprovalFile(seekingApprovalList);
        } catch ( Exception e ) {
            System.out.println(e); return false;
        } return true;
    }
    
    public ArrayList<Course> fetchAllCoursesForThisInstructor() {
        ArrayList<Course> allCourses = Course.fetchAllCourses( this.getBranch() );
        return Course.fetchAllCoursesOfAnInstructor( allCourses, this.getId() );
    }
    
    public CourseExam createAnExamForCourse( String courseName, int totalMarks, int percentage, String title, LocalDate scheduledDate ) {
        ArrayList<Course> allCourses = Course.fetchAllCourses( this.getBranch() );
        Course desiredCourse = null;
        for ( var e : allCourses ) {
            if ( e.getEventName().equals( courseName ) ) {
                desiredCourse = e; break;
            }
        } if ( desiredCourse == null ) return null;
        
        CourseExam newExam = desiredCourse.addExam( totalMarks, percentage, title, scheduledDate );
        
        try {
            Course.writeCourseFile( allCourses, this.getBranch() );
        } catch ( Exception e ) {
            System.out.println(e); return null;
        } return newExam;
    }
    
    public boolean gradeCourseExam( String courseName, String examName, int[]marks ) {
        ArrayList<Course> allCourses = Course.fetchAllCourses( this.getBranch() );
        Course desiredCourse = null;
        for ( var c : allCourses ) {
            if ( c.getEventName().equals( courseName ) ) {
                desiredCourse = c; break;
            }
        } if ( desiredCourse == null ) return false;
        
        CourseExam exam = desiredCourse.searchCourseExam( examName );
        exam.setStudentMarks( marks );
        desiredCourse.updateStudentTotals( marks, exam.MARKS, exam.PERCENTAGE );
        
        try {
            Course.writeCourseFile( allCourses, this.getBranch() );
        } catch ( Exception e ) {
            System.out.println(e); return false;
        } return true;
    }
    
    public String generateReportCardForStudent( String courseName, CourseStudent student ) {
        ArrayList<Course> allCourses = fetchAllCoursesForThisInstructor();
        
        Course desiredCourse = null;
        for ( var e : allCourses ) {
            if ( e.getEventName().equals( courseName ) ) {
                desiredCourse = e; break;
            }
        } if ( desiredCourse == null ) return null;
        
        ArrayList<CourseExam> exams = desiredCourse.getExams();
        
        String examGrades = "\n\t\t~~Exam Marks~~\n";
        int mark = 0, percent = 0;
        for ( var e : exams ) {
            mark = e.getStudentMarks()[ student.ROLL-1 ];
            percent += Math.round( (mark/e.MARKS) * (e.PERCENTAGE / 100) );
            examGrades += e.TITLE + ":\t" + mark + " Marks out of " + e.MARKS + " (contributing " + e.PERCENTAGE +"%)\n";
        } examGrades += "\nGrand Total: " + percent + "/100n\nPrinted: " + LocalDate.now();
        
        String header = "\t\t\tBritish Council Bangladesh\n\t\t\t" + this.getBranch() + " Branch\n\t\tCourse Report Card: " + 
                         courseName + "\n\t\tInstructor: " + this.getName() + ", ID:" + this.getId();
        String studentInfo = "\n\nName: " + student.NAME + "\tMember ID:" + student.ID + "\nRoll: " + student.ROLL;
        
        return header + studentInfo + examGrades;
    }
    
    public ObservableList<Data> generateCourseOverallPieChartData( String courseName ) {
        ArrayList<Course> allCourses = fetchAllCoursesForThisInstructor();
        Course desiredCourse = null;
        for ( var e : allCourses ) {
            if ( e.getEventName().equals( courseName ) ) {
                desiredCourse = e; break;
            }
        } if ( desiredCourse == null ) return null;
        
        return desiredCourse.generatePieChartOverall();
    }
    
    public ObservableList<Data> generateExamPieChartData( String courseName, String examName ) {
        ArrayList<Course> allCourses = Course.fetchAllCourses( this.getBranch() );
        Course desiredCourse = null;
        for ( var c : allCourses ) {
            if ( c.getEventName().equals( courseName ) ) {
                desiredCourse = c; break;
            }
        } if ( desiredCourse == null ) return null;
        
        return desiredCourse.searchCourseExam( examName ).generatePieChartOfExam();
    }
    
}
