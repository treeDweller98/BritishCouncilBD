package people;

import internationalexams.Exam;
import internationalexams.ExamSeries;
import internationalexams.ExamSession;
import internationalexams.ExamSubject;
import localevents.Course;
import localevents.Notice;
import localevents.OneOffEvent;
import britishcouncilbd.BranchNames;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author f_ahmed
 */
public class Manager extends Employee {
    
    // Methods
    public Manager(){
        super( EmployeeType.MANAGER );
    }
    
    public Manager( String name, String address, String email, String phoneNumber, String empPassword, int salary, int authorisationCode, 
                        LocalDate dob, BranchNames branch, EmployeeType post, int employeeID ) {
        super( name, address, email, phoneNumber, empPassword, salary, authorisationCode, dob, branch, post, employeeID );
    }
    
    public String addNewEmployee( String name, String address, String email, String phoneNumber, String empPassword, int salary, 
                                    int authorisationCode, LocalDate dob, EmployeeType post, int employeeID ) {
        
        // Create the appropriate employee
        BranchNames branch = getBranch();       // manager may only add employees to his own branch
        String filename = branch.name(); 
        Employee newEmployee;
        switch (post) {
            case RECEPTIONIST:
                filename += "_receptionists.bin";
                newEmployee = new Receptionist( name, address, email, phoneNumber, empPassword, salary, authorisationCode, dob, branch, post, employeeID );
                break;
            case ACCOUNTANT:
                filename += "_accountants.bin";
                newEmployee = new Accountant( name, address, email, phoneNumber, empPassword, salary, authorisationCode, dob, branch, post, employeeID );
                break;
            case MANAGER:
                filename += "_managers.bin";
                newEmployee = new Manager( name, address, email, phoneNumber, empPassword, salary, authorisationCode, dob, branch, post, employeeID );
                break;
            case INSTRUCTOR:
                filename += "_instructors.bin";
                newEmployee = new Instructor( name, address, email, phoneNumber, empPassword, salary, authorisationCode, dob, branch, post, employeeID );
                break;
            case LIBRARIAN: 
                filename += "_librarians.bin";
                newEmployee = new Librarian( name, address, email, phoneNumber, empPassword, salary, authorisationCode, dob, branch, post, employeeID );
                break;
            default:
                return null;         // should never ever execute
        }
        
        // Read file, add, overwrite updated list
        ArrayList<Employee> empList = null;
        File f = new File(filename);
        if( !f.exists() || f.length() == 0 ) {
            empList = new ArrayList<>();
        }
        else {
            try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
                empList = (ArrayList<Employee>) ois.readObject();
            }
            catch (Exception ex){
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex);
                return null;
            }
        }
        
        empList.add( newEmployee );
        
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( empList );
        }
        catch (Exception ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            return null;
        }
        
        return ( "Successfully Added employee " + name + ", ID:" + employeeID + " to the database." ); 
    }
    
    
    public String createNewLocalEvent( String eventName, String eventDetail, String schedule, int eventFee, int capacity, LocalDate regEnds ) {
        
        OneOffEvent newEvent = new OneOffEvent( eventName, eventDetail, schedule, eventFee,capacity, regEnds );

        // Read file, add, overwrite updated list
        String filename = getBranch().name() + "_oneOffEvents.bin";
        ArrayList<OneOffEvent> list = null;
        File f = new File(filename);
        if( !f.exists() || f.length() == 0 ) {
            list = new ArrayList<>();
        }
        else {
            try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
                list = (ArrayList<OneOffEvent>) ois.readObject();
            }
            catch (Exception ex){
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex);
                return null;
            }
        }
        
        list.add( newEvent );
        
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( list );
        }
        catch (Exception ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            return null;
        }
        return ( "Success! Members may register for this event from now on." );
    }
    
    public String createNewLocalEvent( String eventName, String eventDetail, String schedule, int eventFee, int capacity, LocalDate regEnds, 
                                            String instructorName, int instructorID ) {
        
        Course newCourse = new Course( eventName, eventDetail, schedule, eventFee,capacity, regEnds, instructorName, instructorID );
        
        String filename = getBranch().name() + "_courses.bin";
        ArrayList<Course> list = null;
        File f = new File(filename);
        if( !f.exists() || f.length() == 0 ) {
            list = new ArrayList<>();
        }
        else {
            try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
                list = (ArrayList<Course>) ois.readObject();
            }
            catch (Exception ex){
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex);
                return null;
            }
        }
        
        list.add( newCourse );
        
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( list );
        }
        catch (Exception ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            return null;
        }
        return ( "Success! Members may register for this course from now on." );
    }
    
    
    public String enableExam( ExamSeries series, ExamSession session, int year, ArrayList<String> venues, 
                                ArrayList<ExamSubject> subjectsOffered, LocalDate regStart, LocalDate regEnd, LocalDate lastDayOfCancellation ) {
        
        // Create the proper exam type for the input
        Exam newExam = new Exam( series, session, year, venues, subjectsOffered, regStart, regEnd, lastDayOfCancellation );
        
        // Read, add, overwrite
        String filename = "AllActive" + series + ".bin";
        ArrayList<Exam> list = null;
        File f = new File(filename);
        if( !f.exists() || f.length() == 0 ) {
            list = new ArrayList<>();
        }
        else {
            try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
                list = (ArrayList<Exam>) ois.readObject();
            }
            catch (Exception ex){
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex);
                return null;
            }
        }
        
        list.add( newExam );
        
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( list );
        }
        catch (Exception ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            return null;
        }
        
        return ( "Success! Candidates may now register for this exam." );
    }
    
    
    public Notice postNewNotice( String noticeText ) {
        
        // Open file
        String filename = getBranch() + "_noticeBoard.bin";      // manager may only post notices to his branch
        ArrayList<Notice> notices = null;
        File f = new File(filename);
        if( !f.exists() || f.length() == 0 ) {
            notices = new ArrayList<>();
        }
        else {
            try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
                notices = (ArrayList<Notice>) ois.readObject();
            }
            catch (Exception ex){
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex);
                return null;
            }
        }
        
        // Work
        Notice newNotice = new Notice( noticeText );
        notices.add( newNotice );
        
        // Rewrite
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( notices );
        }
        catch (Exception ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            return null;
        }
        
        // Success
        return newNotice;
    }
    
    public boolean removeNotice( String text ){
        
        // Read File
        String filename = getBranch() + "_noticeBoard.bin";      // manager may only post notices to his branch
        ArrayList<Notice> notices = null;
        File f = new File(filename);
        if( !f.exists() || f.length() == 0 ) {
            return false;
        }
        else {
            try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
                notices = (ArrayList<Notice>) ois.readObject();
            }
            catch (Exception ex){
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex);
                return false;
            }
        }
        
        // Work
        if ( !Notice.removeNoticeFromList( text, notices ) ){
            return false;
        }    
        
        // Rewrite
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( notices );
        }
        catch (Exception ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
            return false;
        }
        
        // Success
        return true;
    }
    
    public ObservableList<Notice> getNoticesToDisplay() {
        String filename = getBranch() + "_noticeBoard.bin";
        File f = new File( filename );
        if( !f.exists() || f.length() == 0 ) {
            return FXCollections.observableArrayList();
        }
        else {
            try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
                return FXCollections.observableArrayList( (ArrayList<Notice>) ois.readObject() );
            }
            catch ( Exception e ){
                System.out.println(e);
                return FXCollections.observableArrayList();
            }
        }
    }
    
    public ObservableList<Employee> getEmployeesListToView() {
        
        ObservableList<Employee> list = FXCollections.observableArrayList();
        ArrayList<Employee> temp;
        
        String branch = getBranch().name();
        String[] suffixes = { "_receptionists.bin", "_managers.bin", "_accountants.bin", 
                                  "_instructors.bin", "_librarians.bin" };
        String filename;
        for( String suffix : suffixes ){
            filename = branch+suffix;
            File f = new File(filename);
            
            if( !f.exists() || f.length() == 0 );
            else {
                try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
                    temp = (ArrayList<Employee>) ois.readObject();
                    for( Employee e : temp ) {
                        list.add(e);
                    }
                }
                catch ( Exception e ){
                    System.out.println(e);
                }
            }        
        }
        return list;
    }

}
