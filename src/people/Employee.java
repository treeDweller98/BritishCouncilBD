package people;
import britishcouncilbd.BranchNames;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;

/**
 *
 * @author f_ahmed
 */
public abstract class Employee implements Serializable {
    private String name, address, email, phoneNumber, empPassword;
    private int salary, authorisationCode;
    private LocalDate dateOfBirth;
    private LocalDate leftDate = null;

    private final LocalDate JOINED;
    private final BranchNames BRANCH;        // final because an employee may only be a user of a single branch
    private final EmployeeType POST;
    private final int ID;


    public Employee( String name, String address, String email, String phoneNumber, String empPassword, int salary, int authorisationCode, LocalDate dob,
                        BranchNames branch, EmployeeType post, int employeeID ) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.empPassword = empPassword;
        this.salary = salary;
        this.authorisationCode = authorisationCode;
        this.dateOfBirth = dob;
        this.BRANCH = branch;
        this.POST = post;
        this.ID = employeeID;
        this.JOINED = LocalDate.now();
    }
    
    public Employee( EmployeeType type ){       // modify to fetch from CurrentSystem.bin file
        name = "TEST USER " + type.name();
        BRANCH = BranchNames.DHAKA;
        POST = type;
        ID = 0;
        empPassword = "password";
        authorisationCode = 12345;
        JOINED = LocalDate.now();
    }
    
    public boolean authenticate( String code ) {
        return code.equals( String.valueOf(authorisationCode) );
    }
    
    public static Employee login( int id, String password, EmployeeType post, BranchNames branch ) {
        String filename = branch + "_";
        switch (post) {
            case RECEPTIONIST:  filename += "receptionists.bin"; break;
            case MANAGER:       filename += "managers.bin";      break;
            case ACCOUNTANT:    filename += "accountants.bin";   break;
            case INSTRUCTOR:    filename += "instructors.bin";   break;
            case LIBRARIAN:     filename += "librarians.bin";    break;
        }
        
        ArrayList<Employee> employees = null;
        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(filename) ) ) {
           employees = (ArrayList<Employee>) ois.readObject();
        } catch ( Exception ex ) {
            Logger.getLogger(Employee.class.getName()).log(Level.SEVERE, null, ex); System.out.println(ex);
            
            Alert b = new Alert(Alert.AlertType.ERROR);
            b.setTitle("Login Failed");
            b.setContentText("A database error has occured. Try again later.");
            b.showAndWait();
            return null;
        }
        
        for ( Employee e : employees ){
            if ( e.ID == id && e.empPassword.equals( password ) ) {
                return e;
            }
        } return null;
    }
    
    
    
    
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmpPassword() {
        return empPassword;
    }

    public int getSalary() {
        return salary;
    }

    public int getAuthorisationCode() {
        return authorisationCode;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDate getLeftDate() {
        return leftDate;
    }

    public LocalDate getJoined() {
        return JOINED;
    }

    public BranchNames getBranch() {
        return BRANCH;
    }

    public EmployeeType getPost() {
        return POST;
    }

    public int getId() {
        return ID;
    }
}
