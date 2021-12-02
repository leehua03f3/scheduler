package scheduleapplication;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import java.time.ZoneId;
import static java.time.ZoneId.systemDefault;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;

/**
 * FXML Controller class
 *
 * @author Duy Hua
 */
public class AddAppointmentController implements Initializable {

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
        // Disabling id text field
        aptIdTextfield.setDisable(true);
        
        // Disabling end datepicker
        // endDatePicker.setDisable(true);
        
        System.out.println(AllAppointmentController.getAllContacts());
        // Populate contact names
        contactName.setItems(AllAppointmentController.getAllContacts());
        contactName.getSelectionModel().selectFirst();
    }
    
    /**
     * This method defines the action when the "Add" button is clicked
     * Lambda Expressions:
     * 1) Line 135: This Lambda Expression takes a ZonedDateTime object and compares it to the company open hour
     * 2) Line 136: This Lambda Expression takes a ZonedDateTime object and compares it to the company close hour
     * @param event
     * @throws IOException
     * @throws SQLException 
     */
    @FXML
    public void addButtonOnClick(ActionEvent event) throws IOException, SQLException {
        
        // Get contact from comboBox
        Contacts contact = contactName.getValue();
        
        // Get user inputs from the textfields
        String title = titleTextfield.getText();
        String location = locationTextfield.getText();
        String type = typeTextfield.getText();
        String description = desTextfield.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = startDate;
        
        // LocalDate endDate = endDatePicker.getValue();
        
        // startDatePicker.setOnAction(e -> startDateSelected(startDate));
        // startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
        //    endDatePicker.setValue(newValue.plusDays(1));
        //    });

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
        
        // System.out.println(startEST);
        // System.out.println(endEST);
        
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
        
        // Switch back to all appointment view
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("allAppointment.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        AllAppointmentController controller = loader.getController();
        controller.addNewAppointment(title, location, type, description, startDate, endDate, startTime, endTime, customerId, userId, contact);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * This method define the action when the "Go Back" button is clicked
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
    
    public void getAllAppointments(ObservableList<Appointments> allAppointments) {
        this.allAppointments = allAppointments;
    }
    
    private void startDateSelected(LocalDate startDate) {
        endDatePicker.setValue(startDate);
    }
    
}