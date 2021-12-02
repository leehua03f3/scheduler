/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduleapplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Duy Hua
 */
public class DBCountries {
    
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
    
}
