package scheduleapplication;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import static scheduleapplication.CustomerRecordController.customerRecords;
import static scheduleapplication.CustomerRecordController.getAllCustomerRecords;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import static java.time.ZoneId.systemDefault;
import static java.time.ZoneOffset.UTC;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import static scheduleapplication.CustomerRecordController.customerRecords;

/**
 * FXML Controller class
 *
 * @author Duy Hua
 */
public class AllAppointmentController implements Initializable {

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TableView<Appointments> appointmentTable;
    @FXML
    private TableColumn<Appointments, Integer> aptId;
    @FXML
    private TableColumn<Appointments, String> aptTitle;
    @FXML
    private TableColumn<Appointments, String> aptDes;
    @FXML
    private TableColumn<Appointments, String> aptLocation;
    @FXML
    private TableColumn<Appointments, String> aptContact;
    @FXML
    private TableColumn<Appointments, String> aptType;
    @FXML
    private TableColumn<Appointments, String> aptStart;
    @FXML
    private TableColumn<Appointments, String> aptEnd;
    @FXML
    private TableColumn<Appointments, Integer> customerId;
    @FXML
    private ToggleGroup tgroup;
    @FXML
    private DatePicker selectWeekMonth;
    @FXML
    private Button getAppointmentsBtn;
    
    public static ObservableList<Appointments> allAppointments = FXCollections.observableArrayList();
    public static ObservableList<Appointments> byWeekAppointments = FXCollections.observableArrayList();
    public static ObservableList<Appointments> byMonthAppointments = FXCollections.observableArrayList();
    public static ObservableList<Contacts> allContacts = FXCollections.observableArrayList();
    
    public static int trackNum = 0;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        displayViewAll();
        selectWeekMonth.setVisible(false);
        getAppointmentsBtn.setVisible(false);
    }
    
    public void displayViewAll() {        
        // Set up table view for customer record
        appointmentTable.setItems(allAppointments);
                
        aptId.setCellValueFactory(new PropertyValueFactory("appointmentId"));
        aptTitle.setCellValueFactory(new PropertyValueFactory("title"));
        aptDes.setCellValueFactory(new PropertyValueFactory("description"));
        aptLocation.setCellValueFactory(new PropertyValueFactory("location"));
        aptContact.setCellValueFactory(new PropertyValueFactory("contactStr"));
        aptType.setCellValueFactory(new PropertyValueFactory("type"));
        aptStart.setCellValueFactory(new PropertyValueFactory("startDateTime"));
        aptEnd.setCellValueFactory(new PropertyValueFactory("endDateTime"));
        customerId.setCellValueFactory(new PropertyValueFactory("customerId"));
        
        if (allAppointments.isEmpty()) {
            for (Appointments a : getAllAppointments()) {
                allAppointments.add(a);
            }
        }
    }
    
    public void displayViewWeek(int week) {
        // Set up table view for customer record
        appointmentTable.setItems(byWeekAppointments);
                
        aptId.setCellValueFactory(new PropertyValueFactory("appointmentId"));
        aptTitle.setCellValueFactory(new PropertyValueFactory("title"));
        aptDes.setCellValueFactory(new PropertyValueFactory("description"));
        aptLocation.setCellValueFactory(new PropertyValueFactory("location"));
        aptContact.setCellValueFactory(new PropertyValueFactory("contactStr"));
        aptType.setCellValueFactory(new PropertyValueFactory("type"));
        aptStart.setCellValueFactory(new PropertyValueFactory("startDateTime"));
        aptEnd.setCellValueFactory(new PropertyValueFactory("endDateTime"));
        customerId.setCellValueFactory(new PropertyValueFactory("customerId"));
        
        Locale locale = Locale.US;
        
        byWeekAppointments.clear();
        for (Appointments a : getAllAppointments()) {
            if(a.getStartDate().get(WeekFields.of(locale).weekOfWeekBasedYear()) == week) {
                byWeekAppointments.add(a);
            }
        }

    }
    
    public void displayViewMonth(int month) {
        // Set up table view for customer record
        appointmentTable.setItems(byMonthAppointments);
                
        aptId.setCellValueFactory(new PropertyValueFactory("appointmentId"));
        aptTitle.setCellValueFactory(new PropertyValueFactory("title"));
        aptDes.setCellValueFactory(new PropertyValueFactory("description"));
        aptLocation.setCellValueFactory(new PropertyValueFactory("location"));
        aptContact.setCellValueFactory(new PropertyValueFactory("contactStr"));
        aptType.setCellValueFactory(new PropertyValueFactory("type"));
        aptStart.setCellValueFactory(new PropertyValueFactory("startDateTime"));
        aptEnd.setCellValueFactory(new PropertyValueFactory("endDateTime"));
        customerId.setCellValueFactory(new PropertyValueFactory("customerId"));
        
        Locale locale = Locale.US;
        
        byMonthAppointments.clear();
        for (Appointments a : getAllAppointments()) {
            if(a.getStartDate().getMonthValue() == month) {
                byMonthAppointments.add(a);
            }
        }
    }
    
    @FXML
    private void getAppointmentsBtnOnClick(ActionEvent event) {
        LocalDate selectedDate = selectWeekMonth.getValue();
        Locale locale = Locale.US;
        
        if (trackNum == 1) {

            int week = selectedDate.get(WeekFields.of(locale).weekOfWeekBasedYear());
            
            // System.out.println("On Week");
            // System.out.println(week);
            
            displayViewWeek(week);
            
        } else if (trackNum == 2) {
            
            int month = selectedDate.getMonthValue();
            
            // System.out.println("On Month");
            // System.out.println(month);
            
            displayViewMonth(month);
        }
    }
    
    @FXML
    private void onViewAll(ActionEvent event) {
        displayViewAll();
        selectWeekMonth.setVisible(false);
        getAppointmentsBtn.setVisible(false);
        trackNum = 0;
    }

    @FXML
    private void onViewWeek(ActionEvent event) {
        selectWeekMonth.setVisible(true);
        selectWeekMonth.setPromptText("Select Week");
        selectWeekMonth.setValue(LocalDate.now());
        getAppointmentsBtn.setVisible(true);
        trackNum = 1;
    }

    @FXML
    private void onViewMonth(ActionEvent event) {
        selectWeekMonth.setVisible(true);
        selectWeekMonth.setPromptText("Select Month");
        selectWeekMonth.setValue(LocalDate.now());
        getAppointmentsBtn.setVisible(true);
        trackNum = 2;
    }
    
    /**
     * This method gets all Appointments data from the database and return an observable list of Appointments object
     * @return 
     */
    public static ObservableList<Appointments> getAllAppointments() {
        ObservableList<Appointments> alist = FXCollections.observableArrayList();
        
        try {
            String sql = "SELECT * from appointments;";
            
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                int appointmentId = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                
                Timestamp start = rs.getTimestamp("Start");
                LocalDateTime startDateTime = start.toLocalDateTime();
                // System.out.println("Before converting" + startDateTime);
                
                ZonedDateTime startUTC = startDateTime.atZone(UTC);
                ZonedDateTime startOrigin = startUTC.withZoneSameInstant(ZoneId.systemDefault());
                startDateTime = startOrigin.toLocalDateTime();
               //  System.out.println("After converting" + startDateTime);
                
                LocalDate startDate = startDateTime.toLocalDate();
                LocalTime startTime = startDateTime.toLocalTime();
                
                Timestamp end = rs.getTimestamp("End");
                LocalDateTime endDateTime = end.toLocalDateTime();
                ZonedDateTime endUTC = endDateTime.atZone(UTC);
                ZonedDateTime endOrigin = endUTC.withZoneSameInstant(ZoneId.systemDefault());
                endDateTime = endOrigin.toLocalDateTime();
                LocalDate endDate = endDateTime.toLocalDate();
                LocalTime endTime = endDateTime.toLocalTime();
                
                int customerId = rs.getInt("Customer_ID");
                int userId = rs.getInt("User_ID");
                int contactId = rs.getInt("Contact_ID");
                
                Contacts contact = null;
                for (Contacts c : getAllContacts()) {
                    if (c.getContactId() == contactId) {
                        // System.out.println("Found");
                        contact = c;
                    }
                }
                
                // System.out.println(contact);

                Appointments a = new Appointments(appointmentId, title, description, location, type, startDate, endDate, startTime, endTime, customerId, userId, contact);
                alist.add(a);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        
        return alist;
    }
    
    /**
     * This method takes all the information that the user enters from the "Add Appointment" view and adds them to the database and the Appointment table
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
     * @throws SQLException 
     */
    public void addNewAppointment(String title, String location, String type, String description, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, int customerId, int userId, Contacts contact) throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement statement = conn.createStatement();
        
        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        LocalDateTime end = LocalDateTime.of(endDate, endTime);
        
        ZonedDateTime startOrigin = start.atZone(systemDefault());
        ZonedDateTime endOrigin = end.atZone(systemDefault());
        
        ZonedDateTime startUTC = startOrigin.withZoneSameInstant(UTC);
        ZonedDateTime endUTC = endOrigin.withZoneSameInstant(UTC);
        
        start = startUTC.toLocalDateTime();
        end = endUTC.toLocalDateTime();

        String insertStatement = "INSERT INTO appointments(Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) " +
                "VALUES(" + 
                "'" + title + "'," + 
                "'" + location + "'," + 
                "'" + type + "'," + 
                "'" + description + "'," + 
                "'" + Timestamp.valueOf(start) + "'," +
                "'" + Timestamp.valueOf(end) + "'," +
                customerId + "," +
                contact.getContactId() + "," + 
                userId +
                ");";
        
        System.out.println(insertStatement);
        statement.execute(insertStatement);
        
        // Add new appointment to the table
        this.allAppointments = getAllAppointments();
        appointmentTable.setItems(allAppointments);
        
    }

    /**
     * This method takes all the information that the user enters from the "Update Appointment" view and updates them to the database and the Appointment table
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
     * @throws SQLException 
     */
    public void updateAppointment(int appointmentId, String title, String location, String type, String description, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, int customerId, int userId, Contacts contact) throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement statement = conn.createStatement();
        
        LocalDateTime start = LocalDateTime.of(startDate, startTime);
        LocalDateTime end = LocalDateTime.of(endDate, endTime);
        
        ZonedDateTime startOrigin = start.atZone(systemDefault());
        ZonedDateTime endOrigin = end.atZone(systemDefault());
        
        ZonedDateTime startUTC = startOrigin.withZoneSameInstant(UTC);
        ZonedDateTime endUTC = endOrigin.withZoneSameInstant(UTC);
        
        start = startUTC.toLocalDateTime();
        end = endUTC.toLocalDateTime();
        
        String updateStatement = "UPDATE appointments SET"
                + " Title = '" + title + "',"
                + " Description = '" + description + "',"
                + " Location = '" + location + "',"
                + " Type = '" + type + "',"
                + " Start = '" + Timestamp.valueOf(start) + "',"
                + " End = '" + Timestamp.valueOf(end) + "',"
                + " Customer_ID = " + customerId + ","
                + " User_ID = " + userId + ","
                + " Contact_ID = " + contact.getContactId()
                + " WHERE Appointment_ID = " + appointmentId
                + ";";
        
        // Print out updateStatement for debugging purpose
        System.out.println(updateStatement);
        statement.execute(updateStatement);
        
        // Update new changes to the table
        this.allAppointments = getAllAppointments();
        appointmentTable.setItems(allAppointments);
    }
    
    /**
     * This method gets all Contacts data from the database and returns an observable list of the Contacts object
     * @return 
     */
    public static ObservableList<Contacts> getAllContacts() {
        ObservableList<Contacts> clist = FXCollections.observableArrayList();
        
        try {
            String sql = "SELECT * from contacts;";
            
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                int contactId = rs.getInt("Contact_ID");
                String contactName = rs.getString("Contact_Name");
                String contactEmail = rs.getString("Email");

                Contacts c = new Contacts(contactId, contactName, contactEmail);
                clist.add(c);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        
        return clist;
        
    }

    /**
     * This method defines the action when the "Add" button is clicked
     * @param event
     * @throws IOException 
     */
    @FXML
    private void addButtonOnClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addAppointment.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        AddAppointmentController controller = loader.getController();
        controller.getAllAppointments(allAppointments);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * This method defines the action when the "Update" button is clicked
     * @param event
     * @throws IOException 
     */
    @FXML
    private void updateButtonOnClick(ActionEvent event) throws IOException {
        
        Appointments selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        
        int appointmentId = selectedAppointment.getAppointmentId();
        String title = selectedAppointment.getTitle();
        String location = selectedAppointment.getLocation();
        String type = selectedAppointment.getType();
        String description = selectedAppointment.getDescription();
        LocalDate startDate = selectedAppointment.getStartDate();
        LocalDate endDate = selectedAppointment.getEndDate();
        LocalTime startTime = selectedAppointment.getStartTime();
        LocalTime endTime = selectedAppointment.getEndTime();
        int customerId = selectedAppointment.getCustomerId();
        int userId = selectedAppointment.getUserId();
        Contacts contact = selectedAppointment.getContact();
        
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("updateAppointment.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        UpdateAppointmentController controller = loader.getController();
        controller.initData(appointmentId, title, location, type, description, startDate, endDate, startTime, endTime, customerId, userId, contact, allAppointments);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * This method defines the action when the "Delete" button is clicked
     * @param event
     * @throws SQLException 
     */
    @FXML
    private void deleteButtonOnClick(ActionEvent event) throws SQLException {
        
        Appointments selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        
        if (selectedAppointment == null) {
            return;
        }
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Appointment");
        alert.setHeaderText("Delete");
        alert.setContentText("Do you want to delete this appointment?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // Delete from database
            Connection conn = DBConnection.getConnection();
            Statement statement = conn.createStatement();
            
            String deleteStatement = "DELETE FROM appointments"
                + " WHERE Appointment_ID = " + selectedAppointment.getAppointmentId()
                + ";";
        
            // Print out deleteStatement for debugging purpose
            System.out.println(deleteStatement);
            statement.execute(deleteStatement);
                
            // Delete from allAppointments list
            allAppointments.remove(selectedAppointment);
        }
    }

    /**
     * This method define the action when the "Go Back" button is clicked
     * @param event
     * @throws IOException 
     */
    @FXML
    private void goBackBtnOnClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("customerRecord.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
    /**
     * This method defines the action when the "View Report" button is clicked
     * @param event
     * @throws IOException 
     */
    @FXML
    private void viewReportOnClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("report.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}
