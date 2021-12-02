/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduleapplication;

/**
 *
 * @author Duy Hua
 */
public class Countries {
    
    private int countryId;
    private String countryName;

    public Countries(int countryId, String countryName) {
        this.countryId = countryId;
        this.countryName = countryName;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
    
    @Override
    public String toString() {
        return this.countryName;
    }
}
