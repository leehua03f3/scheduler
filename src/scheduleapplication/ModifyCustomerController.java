package scheduleapplication;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static scheduleapplication.AddCustomerController.getAllCountries;
import static scheduleapplication.AddCustomerController.getAllDivisions;

/**
 * FXML Controller class
 *
 * @author Duy Hua
 */
public class ModifyCustomerController implements Initializable {

    @FXML
    private Label firstLevelLable;
    @FXML
    private TextField idTextfield;
    @FXML
    private TextField nameTextfield;
    @FXML
    private TextField zipcodeTextfield;
    @FXML
    private TextField phoneNumberTextfield;
    @FXML
    private TextField addressTextfield;
    @FXML
    private ComboBox<Countries> countryCombox;
    @FXML
    private ComboBox<FirstLevelDivision> firstLevelCombox;
    @FXML
    private Button updateButton;
    
    private CustomerRecords selectedCustomer;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Disable id text field
        idTextfield.setDisable(true);

    }    

    /**
     * This method define the action when the "Update" button is clicked
     * @param event
     * @throws IOException
     * @throws SQLException 
     */
    @FXML
    private void updateButtonOnClick(ActionEvent event) throws IOException, SQLException {
        Countries selectedCountry = countryCombox.getSelectionModel().getSelectedItem();
        FirstLevelDivision selectedDivision = firstLevelCombox.getSelectionModel().getSelectedItem();
        
        // Get user inputs from the textfields
        String name = nameTextfield.getText();
        String address = addressTextfield.getText() + ", " + selectedDivision.getName() + ", " + selectedCountry.getCountryName();
        String zipcode = zipcodeTextfield.getText();
        String phone = phoneNumberTextfield.getText();
        int divisionId = getFirstDivisionIdByName(selectedDivision.getName());
        int customerId = Integer.parseInt(idTextfield.getText());
        
        // Switch back to customer record page
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("customerRecord.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        CustomerRecordController controller = loader.getController();
        controller.updateNewCustomerRecords(name, address, zipcode, phone, divisionId, customerId);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
    /**
     * This method fills all the text fields on the view
     * @param customerRecords
     * @param customerRecordId 
     */
    public void initData(ObservableList<CustomerRecords> customerRecords, int customerRecordId) {
        
        // Find the selected customer in the Observable list
        for (CustomerRecords c : customerRecords) {
            if (customerRecordId == c.getCustomerId()) {
                this.selectedCustomer = c;
                break;
            }
        }
        
        String[] address;
        address = selectedCustomer.getCustomerAddress().split(", ");
        
        // Prefill textfields
        idTextfield.setText(Integer.toString(selectedCustomer.getCustomerId()));
        nameTextfield.setText(selectedCustomer.getCustomerName());
        addressTextfield.setText(address[0]);
        zipcodeTextfield.setText(selectedCustomer.getCustomerZipcode());
        phoneNumberTextfield.setText(selectedCustomer.getCustomerPhone());
        
        // Get country name
        String selectedCountryName = address[address.length-1];
        
        if (!(selectedCountryName.equals("U.S") || selectedCountryName.equals("UK") || selectedCountryName.equals("Canada"))) {
            selectedCountryName = "U.S";
            selectedCustomer.setDivisionId(1);
        }

        // Populate country combobox
        countryCombox.setItems(getAllCountries());
        
        int index = 0;
        for (Countries c : getAllCountries()) {
            if (c.getCountryName().equals(selectedCountryName)) {
                break;
            }
            index += 1;
        }
        
        // Set country base on selected customer record
        countryCombox.getSelectionModel().select(index);
        
        Countries selectedCountry = countryCombox.getSelectionModel().getSelectedItem();
        
        // Populate first level combobox
        firstLevelCombox.setItems(getFirstDivisionByCountryId(selectedCountry.getCountryId()));
        
        // Find selected division
        int divisionIndex = 0;
        for (FirstLevelDivision f : getFirstDivisionByCountryId(selectedCountry.getCountryId())) {
            if (f.getId() == selectedCustomer.getDivisionId()) {
                break;
            }
            divisionIndex += 1;
        }
        
        // Set first division based on selected customer record
        firstLevelCombox.getSelectionModel().select(divisionIndex);
        
        if (selectedCountryName.equals("U.S")) {
            firstLevelLable.setText("States");
        } else {
            firstLevelLable.setText("Provinces");
        }
        
        // Change first division combobox when country changes
        countryCombox.setOnAction(e -> countryComboxOnSelected());
    }
    
    /**
     * The method takes a country ID and return an observable list of all the first divisions of that country
     * @param CountryId
     * @return 
     */
    public ObservableList<FirstLevelDivision> getFirstDivisionByCountryId (int CountryId) {
        ObservableList<FirstLevelDivision> flist = FXCollections.observableArrayList();
        for (FirstLevelDivision f : getAllDivisions()) {
            if (CountryId == f.getCountryId()) {
                flist.add(f);
            }
        }
        return flist;  
    }
    
    /**
     * This method changes the label of the first division
     */
    private void countryComboxOnSelected() {
        // Get selected country
        Countries selectedCountry = countryCombox.getSelectionModel().getSelectedItem();
        
        // Populate first level combobox
        firstLevelCombox.setItems(getFirstDivisionByCountryId(selectedCountry.getCountryId()));
        
        // Set default first division
        firstLevelCombox.getSelectionModel().selectFirst();
        
        if (selectedCountry.getCountryName().equals("U.S")) {
            firstLevelLable.setText("States");
        } else {
            firstLevelLable.setText("Provinces");
        }
    }
    
    /**
     * This method takes a division name and return the ID of that division
     * @param division
     * @return 
     */
    public int getFirstDivisionIdByName (String division) {
        for (FirstLevelDivision f : getAllDivisions()) {
            if (division.equals(f.getName())) {
                return f.getId();
            }
        }
        return 0;
    }

    /**
     * This method defines the action when the "Go Back" button is clicked
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

}
