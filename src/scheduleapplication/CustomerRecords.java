package scheduleapplication;

/**
 *
 * @author Duy Hua
 */
public class CustomerRecords {
    private int customerId;
    private String customerName;
    private String customerAddress;
    private String customerZipcode;
    private String customerPhone;
    private int divisionId;

    public CustomerRecords(int customerId, String customerName, String customerAddress, String customerZipcode, String customerPhone, int divisionId) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerZipcode = customerZipcode;
        this.customerPhone = customerPhone;
        this.divisionId = divisionId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerZipcode() {
        return customerZipcode;
    }

    public void setCustomerZipcode(String customerZipcode) {
        this.customerZipcode = customerZipcode;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public int getDivisionId() {
        return divisionId;
    }
    
    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

}
