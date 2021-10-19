package localevents;

import britishcouncilbd.BranchNames;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author f_ahmed
 */
public abstract class LocalEvent implements Serializable {
    private String eventName, eventDetail, schedule;
    private int eventFee, capacity;
    private LocalDate regEnds;
    
    public LocalEvent ( String eventName, String eventDetail, String schedule, int eventFee, int capacity, LocalDate regEnds ) {
        this.eventName = eventName;
        this.eventDetail = eventDetail;
        this.schedule = schedule;
        this.eventFee = eventFee;
        this.capacity = capacity;
        this.regEnds = regEnds;
    }
    
    public abstract boolean hasCapacity();
    public abstract void enroll( String name, int id );
    
    public static LocalEventRegistration registerForLocalEvent( BranchNames branch, String eventType, String eventTitle, int fee, int id, String name ) {
        
        LocalEventRegistration reg = new LocalEventRegistration( eventTitle, eventType, name, id, fee );
        boolean stat = false;
        if ( fee == 0 ) {                                                                    // for free registrations
            if ( eventType.equals("Course") ) {
                stat = Course.registerMemberForCourse( branch, eventTitle, id, name );
            } else if ( eventType.equals("OneOffEvent") ) {
                stat = OneOffEvent.registerMemberForOneOffEvent( branch, eventTitle, id, name );
            } else {
                return null;
            }
        } else {
            stat = LocalEventRegistration.createPendingPaymentsRegEntry( branch, reg );     // for paid registrations
        }
        
        if ( stat == false ) return null; else return reg;
    } 
 
    public String getEventDetailFull() {
        String fee = "";
        if (eventFee == 0) fee = "free!"; else fee = String.valueOf(eventFee);
        return( eventDetail + "\nSchedule: " + schedule + "\nFee in BDT: " + fee + 
                "\tCapacity: " + capacity + "\nLast Date of Registration:" + regEnds.toString() );
    }
    
    public static ObservableList<LocalEvent> fetchAllLocalEventListToView( BranchNames branch ) {
        ObservableList<LocalEvent> events = FXCollections.observableArrayList();
        
        ArrayList<Course> courses = Course.fetchAllCourses(branch);
        ArrayList<OneOffEvent> oneOffs = OneOffEvent.fetchAllOneOffEvents(branch);
        
        for ( Course c : courses ) {
            events.add(c);
        }
        for ( OneOffEvent e : oneOffs ) {
            events.add(e);
        }
        
        return events;
    }
    
    public static ArrayList<LocalEvent> fetchAllLocalEventList( BranchNames branch ) {
        ArrayList<LocalEvent> events = new ArrayList<>();
        
        ArrayList<Course> courses = Course.fetchAllCourses(branch);
        ArrayList<OneOffEvent> oneOffs = OneOffEvent.fetchAllOneOffEvents(branch);
        
        for ( Course c : courses ) {
            events.add(c);
        }
        for ( OneOffEvent e : oneOffs ) {
            events.add(e);
        }
        
        return events;
    }
        
    public int getCapacity(){ 
        return capacity;
    }
    public String getEventName() {
        return eventName;
    }
    public String getSchedule() {
        return schedule;
    }
    public LocalDate getRegEnds() {
        return regEnds;
    }
    public int getEventFee() {
        return eventFee;
    }
    
}
