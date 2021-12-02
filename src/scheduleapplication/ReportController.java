/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduleapplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Duy Hua
 */
public class ReportController implements Initializable {

    @FXML
    private Label loginReport;
    @FXML
    private Label totalCustomerType;
    @FXML
    private Label totalCustomerMonth;
    @FXML
    private Label scheduleContacts;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        totalCustomerType.setText(displayTotalAppointmentType());
        totalCustomerMonth.setText(displayTotalAppointmentMonth());
        scheduleContacts.setText(displayScheduleContacts());
        try {
            loginReport.setText(displayLoginReport());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * This method return a String of the report of total Appointments by Type
     * @return 
     */
    private String displayTotalAppointmentType() {
        
        String report = "Type --- Total Appointments\n";
        
        try {
            String sql = "SELECT COUNT(*), Type FROM WJ08mRY.appointments GROUP BY Type;";
            
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                int totalAppointments = rs.getInt("COUNT(*)");
                String type = rs.getString("Type");
                
                report += "\n" + type + " --- " + totalAppointments;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        
        return report;
    }
    
    /**
     * This method returns a String of total Appointments by Month
     * @return 
     */
    private String displayTotalAppointmentMonth() {
        String report = "Month --- Total Appointments\n";
        
        try {
            String sql = "SELECT COUNT(*), MONTHNAME(Start) FROM WJ08mRY.appointments GROUP BY MONTHNAME(Start);";
            
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                int totalAppointments = rs.getInt("COUNT(*)");
                String startDateTimeStr = rs.getString("MONTHNAME(Start)");
                
                report += "\n" + startDateTimeStr + " --- " + totalAppointments;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        
        return report;
    }
     
    /**
     * This method returns a String of the Schedules of each Contact for every contacts
     * @return 
     */
    private String displayScheduleContacts() {
        String report = "Contact Name --- Appointment ID --- Title --- Description --- Type --- Start Date-Time --- End Date-Time --- Customer ID\n";
        
        try {
            String sql = "SELECT appointments.Appointment_ID,\n" +
                        "	appointments.Title, \n" +
                        "    appointments.Description,\n" +
                        "    appointments.Start,\n" +
                        "    appointments.End,\n" +
                        "    appointments.Customer_ID,\n" +
                        "    contacts.Contact_ID,\n" +
                        "    contacts.Contact_Name\n" +
                        "	FROM WJ08mRY.appointments\n" +
                        "    INNER JOIN WJ08mRY.contacts\n" +
                        "    ON appointments.Contact_ID = contacts.Contact_ID\n" +
                        "    ORDER BY contacts.Contact_Name;";
            
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                String contactName = rs.getString("Contact_Name");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String start = rs.getString("Start");
                String end = rs.getString("End");
                int customerId = rs.getInt("Customer_ID");
                
                report += "\n" + contactName + " --- " + title + " --- " + description + " --- " + start + " --- " + end + " --- " + customerId;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return report;
    }
    
    /**
     * This method returns a String of the report of login activities
     * @return
     * @throws FileNotFoundException 
     */
    private String displayLoginReport() throws FileNotFoundException {
        String report = "User --- Successful Attempt --- Fail Attempt\n";

        for (Users u : LogInController.getAllUsers()) {
            int failAttempt = 0;
            int successAttempt = 0;
            
            File file = new File("login_activity.txt");
            Scanner sc = new Scanner(file);
            
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] words = line.split(Pattern.quote(" | "));
                // System.out.println(Arrays.toString(words));
                // System.out.println(words[1]);
                // System.out.println(words[3]);
                if (words[1].equals(u.getUserName())) {
                    if (words[3].equals("Login Fail")) {
                        failAttempt += 1;
                    }
                    if (words[3].equals("Login Successful")) {
                        successAttempt += 1;
                    }
                }
            }
            report += "\n" + u.getUserName() + " --- " + successAttempt + " --- " + failAttempt;
        }
    
        return report;
    }

    /**
     * This method defines the action when the "Go Back" button is clicked
     * @param event
     * @throws IOException 
     */
    @FXML
    private void goBackOnClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("allAppointment.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
}
