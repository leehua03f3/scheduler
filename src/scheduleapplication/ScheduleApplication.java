/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduleapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Duy Hua
 */
public class ScheduleApplication extends Application {
    
    /**
     * This method loads the login view
     * @param stage
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("logIn.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method launch the application and connect to the database
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        DBConnection.startConnection();
        launch(args);
        DBConnection.closeConnection();
        
    }   
}