package localevents;

import java.io.Serializable;

/**
 *
 * @author f_ahmed
 */
public class CourseStudent implements Serializable {
    public final String NAME;
    public final int ID, ROLL;
    private int totalMarksAchieved = 0;         // percentage
    public int tempMarks = 0;
    
    public CourseStudent( String name, int memberID, int roll ) {
        this.NAME = name;
        this.ID = memberID;
        this.ROLL = roll;
    }
    
    

    public String getName() {
        return NAME;
    }

    public int getId() {
        return ID;
    }

    public int getRoll() {
        return ROLL;
    }

    public int getTotalMarksAchieved() {
        return totalMarksAchieved;
    }

    public void addTotalMarksAchieved( int marks ) {
        this.totalMarksAchieved += marks;
    }
    
    public int getTempMarks() { return tempMarks; }     // inelegant workaround, needs replacing
    
}
