/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduleapplication;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import static java.time.ZoneId.systemDefault;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static scheduleapplication.AddAppointmentController.allAppointments;
import static scheduleapplication.AllAppointmentController.allAppointments;

/**
 * FXML Controller class
 *
 * @author Duy Hua
 */
public class UpdateAppointmentController implements Initializable {

    @FXML
    private TextField aptIdTextfield;
    @FXML
    private TextField titleTextfield;
    @FXML
    private TextField locationTextfield;
    @FXML
    private TextField typeTextfield;
    @FXML
    private TextField desTextfield;
    @FXML
    private TextField customerIdTextfield;
    @FXML
    private ComboBox<Contacts> contactName;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField userIdTextfield;
    @FXML
    private TextField startTimeTextfield;
    @FXML
    private TextField endTimeTextfield;
    @FXML
    private Label errorMessage;
    public static ObservableList<Appointments> allAppointments = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Disable id text field
        aptIdTextfield.setDisable(true);
        
        // Populate contact names
        contactName.setItems(AllAppointmentController.getAllContacts());
        
    }    
    
    /**
     * This methods fills all the text fields on the view
     * @param appointmentId
     * @param title
     * @param location
     * @param type
     * @param description
     * @param startDate
     * @param endDate
     * @param startTime
     * @param endTime
     * @param customerId
     * @param userId
     * @param contact 
     */
    public void initData(int appointmentId, String title, String location, String type, String description, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, int customerId, int userId, Contacts contact, ObservableList<Appointments> allAppointments) {
        aptIdTextfield.setText(Integer.toString(appointmentId));
        titleTextfield.setText(title);
        locationTextfield.setText(location);
        typeTextfield.setText(type);
        desTextfield.setText(description);
        startDatePicker.setValue(startDate);
        // endDatePicker.setValue(endDate);
        startTimeTextfield.setText(startTime.toString());
        endTimeTextfield.setText(endTime.toString());
        customerIdTextfield.setText(Integer.toString(customerId));
        contactName.getSelectionModel().select(contact);
        userIdTextfield.setText(Integer.toString(userId));
        this.allAppointments = allAppointments;
    }

    /**
     * This method defines the action when "Update" button is clicked
     * @param event
     * @throws IOException
     * @throws SQLException 
     */
    @FXML
    private void updateButtonOnClick(ActionEvent event) throws IOException, SQLException {
        
        // Get contact from comboBox
        Contacts contact = contactName.getValue();
        
        // Get user inputs from the textfields
        int appointmentId = Integer.parseInt(aptIdTextfield.getText());
        String title = titleTextfield.getText();
        String location = locationTextfield.getText();
        String type = typeTextfield.getText();
        String description = desTextfield.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = startDate;
        String startTimeStr = startTimeTextfield.getText();
        String endTimeStr = endTimeTextfield.getText();
        int customerId = Integer.parseInt(customerIdTextfield.getText());
        int userId = Integer.parseInt(userIdTextfield.getText());
        
        LocalTime startTime = LocalTime.of(Integer.parseInt(startTimeStr.substring(0, 2)), Integer.parseInt(startTimeStr.substring(3,5)));
        LocalTime endTime = LocalTime.of(Integer.parseInt(endTimeStr.substring(0, 2)), Integer.parseInt(endTimeStr.substring(3,5)));
                
        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        LocalDateTime end = LocalDateTime.of(endDate, endTime);
        
        ZonedDateTime startOrigin = start.atZone(systemDefault());
        ZonedDateTime endOrigin = end.atZone(systemDefault());
        
        ZonedDateTime startEST = startOrigin.withZoneSameInstant(ZoneId.of("US/Eastern"));
        ZonedDateTime endEST = endOrigin.withZoneSameInstant(ZoneId.of("US/Eastern"));
        
        ZonedDateTime companyOpen = ZonedDateTime.of(startDate, LocalTime.of(8,00), ZoneId.of("US/Eastern"));
        ZonedDateTime companyClose = ZonedDateTime.of(endDate, LocalTime.of(22, 00), ZoneId.of("US/Eastern"));
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        
        // Lamba expressions
        GeneralInterface compareToOpen = time -> time.compareTo(companyOpen);
        GeneralInterface compareToClose = time -> time.compareTo(companyClose);
        
        // Validate start and end time
        if (start.isAfter(end)) {
            errorMessage.setText("Error Message: \nStart is after End");
            return;
        }

        if ((compareToOpen.compareTimeZone(startEST) < 0) || (compareToClose.compareTimeZone(endEST)) > 0) {
            System.out.println("StartEST compare to companyOpen: " + compareToOpen.compareTimeZone(startEST));
            System.out.println("EndEST compare to companyClose: " + compareToClose.compareTimeZone(endEST));
            String startStr = dtf.format(startEST.toLocalDateTime());
            String endStr = dtf.format(endEST.toLocalDateTime());
            errorMessage.setText("Error Message: \nCompany's hours are 8:00 AM - 22:00 AM" 
                    + "\nYour start time in EST is: " + startStr + " AM"
                    + "\nYour end time in EST is: " + endStr + " AM");
            return;
        }

        for (Appointments a : allAppointments) {
            LocalDateTime startDateTimeAppt = LocalDateTime.of(a.getStartDate(), a.getStartTime());
            LocalDateTime endDateTimeAppt = LocalDateTime.of(a.getEndDate(), a.getEndTime());
            
            // System.out.println(startDateTimeAppt + " | " + start);
            // System.out.println(start.equals(startDateTimeAppt));
            
            /*
            if (((start.isAfter(startDateTimeAppt) || startDateTimeAppt.isAfter(start) || start.equals(startDateTimeAppt)) &&
                    (start.isBefore(endDateTimeAppt) || start.equals(endDateTimeAppt))) || 
                    (end.isAfter(startDateTimeAppt) || startDateTimeAppt.isAfter(end) || end.equals(startDateTimeAppt)) &&
                    (end.isBefore(endDateTimeAppt) || end.equals(endDateTimeAppt))) {
                errorMessage.setText("Error Message: \nYour times is overlap with other customers");
                return;
            }
            */
            
            if (!(end.isBefore(startDateTimeAppt) || (start.isAfter(endDateTimeAppt)))) {
                errorMessage.setText("Error Message: \nYour times is overlap with other customers");
                return;
            }
        }
        
        for (CustomerRecords c : CustomerRecordController.getAllCustomerRecords()) {
            if (c.getCustomerId() == customerId) {
                break;
            }
            errorMessage.setText("Can not find customer with the input ID");
            return;
        }
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("allAppointment.fxml"));
        Parent root = loader.load();

        AllAppointmentController controller = loader.getController();
        controller.updateAppointment(appointmentId, title, location, type, description, startDate, endDate, startTime, endTime, customerId, userId, contact);
        
        Scene scene = new Scene(root);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * This method defines the action when "Go Back" button is clicked
     * @param event
     * @throws IOException 
     */
    @FXML
    private void goBackBtnOnClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("allAppointment.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
}
