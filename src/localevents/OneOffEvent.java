package localevents;

import britishcouncilbd.BranchNames;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author f_ahmed
 */
public class OneOffEvent extends LocalEvent {
    private ArrayList<String> registeredMembers;       // "membername; id"
    
    public OneOffEvent ( String eventName, String eventDetail, String schedule, int eventFee, int capacity, LocalDate regEnds ) {
        super( eventName, eventDetail, schedule, eventFee,capacity, regEnds );
        this.registeredMembers = new ArrayList<>();
    }
    
    @Override
    public boolean hasCapacity() {
        return ( registeredMembers.size() > getCapacity() );
    }
    
    @Override
    public void enroll( String name, int id ){
        registeredMembers.add( (name + "; " + id ) );
    }
    
    @Override
    public String toString() {
        return "EVENT " + getEventName();
    }
    
    public static boolean registerMemberForOneOffEvent( BranchNames branch, String eventName, int id, String memberName ) {
        ArrayList<OneOffEvent> oneOffEvents = fetchAllOneOffEvents(branch);
        
        OneOffEvent eventToRegFor = null;
        for ( OneOffEvent e : oneOffEvents ) {
            if ( eventName.equals( e.getEventName() ) ) {
                eventToRegFor = e; break;
            }
        } if ( eventToRegFor == null ) { System.out.println("cant find event") ; return false; }
        
        eventToRegFor.enroll( memberName, id );
        
        try {
            writeOneOffEventsFile( oneOffEvents, branch );
        } catch ( Exception e ) {
            System.out.println("cant write event");
            return false;
        } return true;
    }
    
    public static ArrayList<OneOffEvent> fetchAllOneOffEvents( BranchNames branch ) {
        String filename = branch + "_oneOffEvents.bin";
        File f = new File(filename);

        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            return (ArrayList<OneOffEvent>) ois.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<OneOffEvent>();
        }
    }
    
    public static void writeOneOffEventsFile( ArrayList<OneOffEvent> events, BranchNames branch ) throws Exception {
        String filename = branch + "_oneOffEvents.bin";
        File f = new File(filename);
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( events );
        }
    }
    
}
