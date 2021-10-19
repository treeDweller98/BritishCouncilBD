package localevents;

import britishcouncilbd.BranchNames;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author f_ahmed
 */
public class LocalEventRegistration implements Serializable {
    public final String EVENT, TYPE, NAME;
    public final int ID, FEE;
    
    public LocalEventRegistration( String event, String type, String name, int id, int fee ){
        this.EVENT = event; this.NAME = name; this.TYPE = type; ID= id; this.FEE = fee;
    }
    
    public String generatePendingRegSlip( BranchNames branch ) {
        String header = "\t\t\tBritish Council Bangladesh\n\t\t\t" + branch + " Branch\n\t\t\t" + TYPE + " Registration slip";
        String info = "Name of Member: " + NAME + " \tID: " + ID + "\nRegistered for: " + EVENT + "\n\tFEE (in BDT):  " + FEE;
        String end = "\n\n";
        
        if ( FEE == 0 ) {
            end += "You have successfully enrolled for this event.\nPlease present this slip along with you ID card to gain entrance.";
        } else {
            end += "Your registration is pending payment.\nPlease pay the fee amount at our accounts section in-person to complete the enrollment procedure.";
        }
        return header+info+end;
    }
    
    public String generatePaidRegSlip( BranchNames branch, String payer, String accountant, int accId ) {
        String header = "\t\t\tBritish Council Bangladesh\n\t\t\t" + branch + " Branch\n\t\t\t" + TYPE + " Registration Payment Slip";
        String info = "Name of Member: " + NAME + " \tID: " + ID + "\nRegistered for: " + EVENT + "\n\tFEE (in BDT):  " + FEE +
                      "\n\nPaid by " + payer + " on " + LocalDate.now() + ". Validated by " + accountant + ", ID: " + accId; 
        String end = "\n\nYou have successfully enrolled for this event.\nPlease present this slip along with you ID card to gain entrance.";
        return header+info+end;
    }
    
    public static boolean createPendingPaymentsRegEntry( BranchNames branch, LocalEventRegistration reg) {
        ArrayList<LocalEventRegistration> registrations = fetchLocalEventsPendingPaymentFile( branch );
        boolean stat = registrations.add( reg );

        try {
           writeLocalEventsPendingPaymentFile( registrations, branch );
        } catch ( Exception e ) {
            System.out.println(e); return false;
        }
        return stat;
    }
    
    public static void removePendingPaymentsRegEntry( BranchNames branch, LocalEventRegistration reg ) {
        ArrayList<LocalEventRegistration> registrations = fetchLocalEventsPendingPaymentFile( branch );
        
        for ( var r : registrations ) {
            if ( reg.EVENT.equals( r.EVENT ) && reg.ID == r.ID ) {
                registrations.remove(r); break;
            }
        }
         
        try {
           writeLocalEventsPendingPaymentFile( registrations, branch );
        } catch ( Exception e ) {
            System.out.println(e);
        }
    }
    
    public static ArrayList<LocalEventRegistration> fetchLocalEventsPendingPaymentFile( BranchNames branch ) {
        String filename = branch + "_localEventPayments.bin";
        File f = new File(filename);

        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            return (ArrayList<LocalEventRegistration>) ois.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<LocalEventRegistration>();
        }
    }
    
    public static void writeLocalEventsPendingPaymentFile( ArrayList<LocalEventRegistration> list, BranchNames branch ) throws Exception {
        String filename = branch + "_localEventPayments.bin";
        File f = new File(filename);
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( list );
        }
    }
    
    

    public String getEvent() {
        return EVENT;
    }

    public String getType() {
        return TYPE;
    }

    public String getName() {
        return NAME;
    }

    public int getId() {
        return ID;
    }

    public int getFee() {
        return FEE;
    }
    
    
}
