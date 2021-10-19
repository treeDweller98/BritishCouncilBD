package localevents;

import britishcouncilbd.BranchNames;
import java.io.File;
import javafx.scene.chart.PieChart.Data;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

/**
 *
 * @author f_ahmed
 */
public class Course extends LocalEvent {
    private String instructorName;
    private int instructorID;
    private ArrayList< CourseExam > exams;
    private ArrayList< CourseStudent > enrolledStudents;
    private int percentageOfTotalAssessed = 0;
    
    
    public Course( String eventName, String eventDetail, String schedule, int eventFee, int capacity, LocalDate regEnds, 
                        String instructorName, int instructorID ){
        super( eventName, eventDetail, schedule, eventFee, capacity, regEnds );
        this.instructorName = instructorName;
        this.instructorID = instructorID;
        this.exams = new ArrayList<>();
        this.enrolledStudents = new ArrayList<>();
    }
    
    @Override
    public boolean hasCapacity() {
        return ( enrolledStudents.size() > getCapacity() );
    }
    
    @Override
    public void enroll( String name, int id ) {
        int roll = enrolledStudents.size() + 1; 
        CourseStudent s = new CourseStudent( name, id, roll );
        enrolledStudents.add(s);
    }
    
    @Override
    public String toString() {
        return "COURSE " + getEventName();
    }
    
    public ObservableList<Data> generatePieChartOverall() {
        int t90 = 0, t80 = 0, t70 = 0, t60 = 0, t50 = 0, tFail = 0, mark;
        
        for ( var s : enrolledStudents ) {
            mark = s.getTotalMarksAchieved();
            
            if ( mark >= 90 ) t90++;
            else if ( mark >= 80 ) t80++;
            else if ( mark >= 70 ) t70++;
            else if ( mark >= 60 ) t60++;
            else if ( mark >= 50 ) t50++;
            else tFail++;
        }
        
        return  FXCollections.observableArrayList(
                new PieChart.Data( "A: 90+", t90 ),
                new PieChart.Data( "B: 80+", t80 ),
                new PieChart.Data( "C: 70+", t70 ),
                new PieChart.Data( "D: 60+", t60 ),
                new PieChart.Data( "E: 50+", t50 ),
                new PieChart.Data( "F: below 50", tFail )
        );
    }
    
    public void updateStudentTotals( int[]allMarks, int marksInExam, int examPercentage ) {
        int i = 0;
        for ( var s : enrolledStudents ) {
            s.addTotalMarksAchieved( Math.round( (allMarks[i]/marksInExam) * (examPercentage/100) ) ); i++;
        }
    }
    
    public CourseExam addExam( int totalMarks, int percentage, String title, LocalDate scheduled ) {
        CourseExam e = new CourseExam( totalMarks, percentage, title, enrolledStudents.size(), scheduled );
        exams.add(e); 
        percentageOfTotalAssessed += percentage;
        return e;
    }
    
    public void addExam( CourseExam e ) {           // for updating table
        exams.add(e); 
        percentageOfTotalAssessed += e.PERCENTAGE;
    }
    
    public CourseExam searchCourseExam( String examName ) {
        for ( var e : exams ) {
            if ( e.TITLE.equals( examName ) ) return e;
        } return null;
    }
    
    public static boolean registerMemberForCourse( BranchNames branch, String courseName, int id, String memberName ) {
        ArrayList<Course> courses = fetchAllCourses(branch);
        
        Course courseToEnrollIn = null;
        for ( Course c : courses ) {
            if ( courseName.equals( c.getEventName() ) ) {
                courseToEnrollIn = c; break;
            }
        } if ( courseToEnrollIn == null ) { System.out.println("cant find course") ; return false; }
        
        courseToEnrollIn.enroll( memberName, id );
        
        try {
            writeCourseFile( courses, branch );
        } catch ( Exception e ) {
            System.out.println("cant write course");
            return false;
        } return true;
    }
    
    public static ArrayList<Course> fetchAllCoursesOfAnInstructor( ArrayList<Course> courses, int id ) {
        ArrayList<Course> myCourses = new ArrayList<>();
        for ( var c : courses ) {
            if ( c.instructorID == id ) {
                myCourses.add(c);
            }
        } return myCourses;
    }
    
    public static ArrayList<Course> fetchAllCourses( BranchNames branch ) {
        String filename = branch + "_courses.bin";
        File f = new File(filename);

        try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream(f) ) ) {
            return (ArrayList<Course>) ois.readObject();
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Course>();
        }
    }
    
    public static void writeCourseFile( ArrayList<Course> course, BranchNames branch ) throws Exception {
        String filename = branch + "_oneOffEvents.bin";
        File f = new File(filename);
        try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(f) ) ) {
            oos.writeObject( course );
        }
    }

    
    
    public String getInstructorName() {
        return instructorName;
    }

    public int getInstructorID() {
        return instructorID;
    }

    public ArrayList<CourseExam> getExams() {
        return exams;
    }

    public ArrayList<CourseStudent> getEnrolledStudents() {
        return enrolledStudents;
    }

    public int getPercentageOfTotalAssessed() {
        return percentageOfTotalAssessed;
    }
    
}
