package scheduleapplication;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import static scheduleapplication.AllAppointmentController.allAppointments;

/**
 * FXML Controller class
 *
 * @author Duy Hua
 */
public class CustomerRecordController implements Initializable {

    @FXML
    private TableView<CustomerRecords> customerTable;
    @FXML
    private TableColumn<CustomerRecords, Integer> customerId;
    @FXML
    private TableColumn<CustomerRecords, String> customerName;
    @FXML
    private TableColumn<CustomerRecords, String> customerAddress;
    @FXML
    private TableColumn<CustomerRecords, String> customerZipcode;
    @FXML
    private TableColumn<CustomerRecords, String> customerPhone;
    @FXML
    private Label customerRecordLabel;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    
    public static ObservableList<CustomerRecords> customerRecords = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
        // Set up table view for customer record
        customerTable.setItems(customerRecords);
        
        customerId.setCellValueFactory(new PropertyValueFactory("customerId"));
        customerName.setCellValueFactory(new PropertyValueFactory("customerName"));
        customerAddress.setCellValueFactory(new PropertyValueFactory("customerAddress"));
        customerZipcode.setCellValueFactory(new PropertyValueFactory("customerZipcode"));
        customerPhone.setCellValueFactory(new PropertyValueFactory("customerPhone"));
        
        if (customerRecords.isEmpty()) {
            for (CustomerRecords record : getAllCustomerRecords()) {
                customerRecords.add(record);
            }
        }
    }

    /**
     * This method define the action when the "Add" button is clicked
     * @param event
     * @throws IOException 
     */
    @FXML
    private void addButtonOnClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addCustomer.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

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
        
        CustomerRecords selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        int customerRecordId = selectedCustomer.getCustomerId();
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("modifyCustomer.fxml"));
        Parent root = loader.load();
        
        ModifyCustomerController controller = loader.getController();
        controller.initData(customerRecords, customerRecordId);

        Scene scene = new Scene(root);
        
        
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
        
        CustomerRecords selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        
        if (selectedCustomer == null) {
            return;
        }
        
        ObservableList<Appointments> allAppointments = AllAppointmentController.getAllAppointments();
        for (Appointments a : allAppointments) {
            if (selectedCustomer.getCustomerId() == a.getCustomerId()) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("You can not delete this record");
                alert.setContentText("This customer has appointments, please remove all the appoinments for this customer first"); 

                alert.showAndWait();
                return;
            }
        }
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Customer Record");
        alert.setHeaderText("Delete");
        alert.setContentText("Do you want to delete this customer record?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // Delete from database
            Connection conn = DBConnection.getConnection();
            Statement statement = conn.createStatement();
            
            String deleteStatement = "DELETE FROM customers"
                + " WHERE Customer_ID = " + selectedCustomer.getCustomerId()
                + ";";
        
            // Print out deleteStatement for debugging purpose
            System.out.println(deleteStatement);
            statement.execute(deleteStatement);
                
            // Delete from allAppointments list
            customerRecords.remove(selectedCustomer);
        }
    }
    
    /**
     * This method gets all CustomerRecords data from the database and returns an observable list of the CustomerRecords object
     * @return 
     */
    public static ObservableList<CustomerRecords> getAllCustomerRecords() {
        ObservableList<CustomerRecords> clist = FXCollections.observableArrayList();
        
        try {
            String sql = "SELECT * from customers;";
            
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                int customerId = rs.getInt("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                String customerAddress = rs.getString("Address");
                String customerZipcode = rs.getString("Postal_Code");
                String customerPhone = rs.getString("Phone");
                int divisionId = rs.getInt("Division_ID");
                CustomerRecords c = new CustomerRecords(customerId, customerName, customerAddress, customerZipcode, customerPhone, divisionId);
                clist.add(c);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        
        return clist;
    }
    
    /**
     * This method gets all information that the user enter from "Add Customer Record" view and adds them to the database and the Customer Record table
     * @param name
     * @param address
     * @param zipcode
     * @param phone
     * @param divisionId
     * @throws SQLException 
     */
    public void addNewCustomerRecords(String name, String address, String zipcode, String phone, int divisionId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement statement = conn.createStatement();

        String insertStatement = "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Division_ID) " +
                "VALUES(" + 
                "'" + name + "'," + 
                "'" + address + "'," + 
                "'" + zipcode + "'," + 
                "'" + phone + "'," + 
                divisionId + 
                ");";
        
        statement.execute(insertStatement);
        
        // Add new record to the table
        customerRecords = getAllCustomerRecords();
        customerTable.setItems(customerRecords);
    }
    
    /**
     * This method gets all information that the user enter from "Update Customer Record" view and updates them to the database and the Customer Record table
     * @param name
     * @param address
     * @param zipcode
     * @param phone
     * @param divisionId
     * @param customerId
     * @throws SQLException 
     */
    public void updateNewCustomerRecords(String name, String address, String zipcode, String phone, int divisionId, int customerId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement statement = conn.createStatement();
        
        CustomerRecords updatedRecord = new CustomerRecords(customerId, name, address, zipcode, phone, divisionId);

        String updateStatement = "UPDATE customers SET"
                + " Customer_Name = '" + name + "',"
                + " Address = '" + address + "',"
                + " Postal_Code = '" + zipcode + "',"
                + " Phone = '" + phone + "',"
                + " Division_ID = " + divisionId
                + " WHERE Customer_ID = " + customerId
                + ";";
        
        // Print out updateStatement for debugging purpose
        System.out.println(updateStatement);
        
        statement.execute(updateStatement);
        
        // Update new record to the table
        customerRecords = getAllCustomerRecords();
        customerTable.setItems(customerRecords);
    }

    /**
     * This method defines the action when the "Go Back" button is clicked
     * @param event
     * @throws IOException 
     */
    @FXML
    private void goBackBtnOnClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("logIn.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * This method defines the action when the "All Appointments" button is clicked
     * @param event
     * @throws IOException 
     */
    @FXML
    private void allAptOnClick(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("allAppointment.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
}