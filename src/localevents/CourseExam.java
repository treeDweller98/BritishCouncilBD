package localevents;

import java.io.Serializable;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

/**
 *
 * @author f_ahmed
 */
public class CourseExam implements Serializable {
    private LocalDate scheduledDate;
    public final int MARKS, PERCENTAGE;
    public final String TITLE;
    private int[] studentMarks;
    private boolean gradeStatus = false;              // true if graded
    
    public CourseExam( int totalMarks, int percentage, String title, int totalStudents, LocalDate scheduledDate ) {
        this.scheduledDate = scheduledDate;
        this.MARKS = totalMarks;
        this.PERCENTAGE = percentage;
        this.TITLE = title;
        this.studentMarks = new int[ totalStudents ];
    }
    
    public ObservableList<PieChart.Data> generatePieChartOfExam() {
        int t90 = 0, t80 = 0, t70 = 0, t60 = 0, t50 = 0, tFail = 0;
        
        for ( int mark : studentMarks ) {
            mark = Math.round( (mark/MARKS) * 100 );
                    
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
    
    public void setStudentMarks( int[] marks ) {
        studentMarks = marks; gradeStatus = true;
    }

    public static boolean checkAllGraded( ObservableList<CourseExam> exams ) {
        for ( var e : exams ) {
            if ( e.gradeStatus == false ) return false;
        } return true;
    }
    
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }
    
    public int getMarks() {
        return MARKS;
    }

    public int getPercentage() {
        return PERCENTAGE;
    }

    public String getTitle() {
        return TITLE;
    }

    public int[] getStudentMarks() {
        return studentMarks;
    }

    public String getGradeStatusString() {
        if (gradeStatus == true) return "GRADED"; else return "PENDING";
    }

    public boolean getGradeStatus() {
        return gradeStatus;
    }
    
}
