/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduleapplication;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author Duy Hua
 */
public class SampleController implements Initializable {

    @FXML
    private Button testBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void testBtnOnClick(ActionEvent event) { 
        
        ObservableList<Countries> countrylist = DBCountries.getAllCountries();
        
        for(Countries c : countrylist) {
            System.out.println("Country Id: " + c.getCountryId() + " Name: " + c.getCountryName());
        }
   }
    
}
