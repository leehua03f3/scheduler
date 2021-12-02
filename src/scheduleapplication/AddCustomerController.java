/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduleapplication;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

/**
 * FXML Controller class
 *
 * @author Duy Hua
 */
public class AddCustomerController implements Initializable {

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
    private Button addButton;

    /**
     * Initializes the controller class
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Disable id text field
        idTextfield.setDisable(true);
        
        // Populate country combobox
        countryCombox.setItems(getAllCountries());
        
        // Set US is the default country
        countryCombox.getSelectionModel().selectFirst();
        
        Countries selectedCountry = countryCombox.getSelectionModel().getSelectedItem();
        
        // Populate first level combobox
        firstLevelCombox.setItems(getFirstDivisionByCountryId(selectedCountry.getCountryId()));
        
        // Set default first division
        firstLevelCombox.getSelectionModel().selectFirst();
        
        firstLevelLable.setText("States");
        
        // Change first division combobox when country changes
        countryCombox.setOnAction(e -> countryComboxOnSelected());

    }    

    /**
     * The method defines the action when the "Add" button is clicked
     * @param event
     * @throws IOException
     * @throws SQLException 
     */
    @FXML
    private void addButtonOnClick(ActionEvent event) throws IOException, SQLException {
        Countries selectedCountry = countryCombox.getSelectionModel().getSelectedItem();
        FirstLevelDivision selectedDivision = firstLevelCombox.getSelectionModel().getSelectedItem();
        
        // Get user inputs from the textfields
        String name = nameTextfield.getText();
        String address = addressTextfield.getText() + ", " + selectedDivision.getName() + ", " + selectedCountry.getCountryName();
        String zipcode = zipcodeTextfield.getText();
        String phone = phoneNumberTextfield.getText();
        int divisionId = getFirstDivisionIdByName(selectedDivision.getName());
        
        // Switch back to customer record page
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("customerRecord.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        CustomerRecordController controller = loader.getController();
        controller.addNewCustomerRecords(name, address, zipcode, phone, divisionId);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }
    
    /**
     * This method change the label of the first division
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
     * This method get all countries data from database and return an observable list
     * @return 
     */
    public static ObservableList<Countries> getAllCountries() {
        ObservableList<Countries> clist = FXCollections.observableArrayList();
        
        try {
            String sql = "SELECT * from countries";
            
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                int countryId = rs.getInt("Country_ID");
                String countryName = rs.getString("Country");
                Countries c = new Countries(countryId, countryName);
                clist.add(c);
            }
            
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        
        return clist;
    }
    
    /**
     * This method get all divisions data from database and return an observable list
     * @return 
     */
    public static ObservableList<FirstLevelDivision> getAllDivisions() {
        ObservableList<FirstLevelDivision> clist = FXCollections.observableArrayList();
        
        try {
            String sql = "SELECT * FROM first_level_divisions";
            
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                int id = rs.getInt("Division_ID");
                String name = rs.getString("Division");
                int countryId = rs.getInt("COUNTRY_ID");
                FirstLevelDivision c = new FirstLevelDivision(id, name, countryId);
                clist.add(c); 
           }
            
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        
        return clist;
    }
    
    /**
     * This method takes a country name and return the object of that country
     * @param countryName
     * @return 
     */
    public Countries getCountryByName(String countryName) {
        for (Countries c : getAllCountries()) {
            if (countryName.equals(c.getCountryName())) {
                return c;
            }
        }
        return null;
    }
    
    /**
     * This method takes a country ID return an observable list of all first divisions of that country
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
     * This method take a first division name and returns an object of that division
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
