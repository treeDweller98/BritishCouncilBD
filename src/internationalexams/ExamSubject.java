package internationalexams;

import java.io.Serializable;

/**
 *
 * @author f_ahmed
 */
public class ExamSubject implements Serializable {
    public final String CODE, TITLE;
    public final int FEE;
    
    public ExamSubject( String code, String title, int fee ){
        CODE = code;
        TITLE = title;
        FEE = fee;
    }
    
    @Override
    public String toString(){
        return ( CODE + ": " + TITLE + ". Fee Tk." + FEE );
    }
    
    public String getSubName(){
        return ( CODE + ": " + TITLE );
    }
    public int getFee() { return FEE; }
}
