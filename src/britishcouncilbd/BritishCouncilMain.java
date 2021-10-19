/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package britishcouncilbd;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import people.Accountant;
import people.Instructor;
import people.Librarian;
import people.Manager;
import people.Receptionist;
import scenes.DefaultPageController;
import scenes.accountant.AccountantOverviewController;
import scenes.instructor.InstructorOverviewController;
import scenes.librarian.LibraryOverviewController;
import scenes.manager.ManagerOverviewController;
import scenes.receptionist.ReceptionistOverviewController;


/**
 *
 * @author f_ahmed
 */
public class BritishCouncilMain extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {     
        FXMLLoader loader = new FXMLLoader();
        
        //loader.setLocation(getClass().getResource("/scenes/receptionist/ReceptionistOverview.fxml"));
        //loader.setLocation(getClass().getResource("/scenes/accountant/AccountanttOverview.fxml"));
        //loader.setLocation(getClass().getResource("/scenes/manager/ManagerOverview.fxml"));
        //loader.setLocation(getClass().getResource("/scenes/instructor/InstructorOverview.fxml"));
        //loader.setLocation(getClass().getResource("/scenes/librarian/LibrarianOverview.fxml"));                // originally in linux system, may need to flip slashes
        loader.setLocation(getClass().getResource("/scenes/DefaultPage.fxml"));   
        //loader.setLocation(getClass().getResource("/britishcouncilbd/DemoScene.fxml")); 
        
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        Scene scene = new Scene( root );
        

        //ReceptionistOverviewController controller = loader.getController();
        //controller.initData( new Receptionist(), BranchNames.DHAKA );
        
        //AccountantOverviewController controller = loader.getController();
        //controller.initData( new Accountant(), BranchNames.DHAKA );
        
        //ManagerOverviewController controller = loader.getController();
        //controller.initData( new Manager(), BranchNames.DHAKA );
        
        //InstructorOverviewController controller = loader.getController();
        //controller.initData( new Instructor(), BranchNames.DHAKA );
        
       // LibraryOverviewController controller = loader.getController();
       // controller.initData( new Librarian(), BranchNames.DHAKA );
        
        DefaultPageController controller = loader.getController();
        controller.initData();
       
        
        primaryStage.setTitle( "Welcome to the British Council" );
        primaryStage.setScene( scene );
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
