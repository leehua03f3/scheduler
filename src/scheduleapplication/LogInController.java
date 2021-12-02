package scheduleapplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Duy Hua
 */
public class LogInController implements Initializable {

    @FXML
    private TextField usernameTextfield;
    @FXML
    private PasswordField passwordTextfield;
    @FXML
    private Label userLocation;
    @FXML
    private Button loginButton;
    @FXML
    private Button exitButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    
    // Create a locale to get user's country and language
    private final Locale locale = Locale.getDefault();
    
    // Setup the resource bundle for French language
    private ResourceBundle translate_rb = ResourceBundle.getBundle("scheduleapplication/ScheduleApplication", locale);
    @FXML
    private Label userTimeZone;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set lable to user's country
        userLocation.setText(locale.getDisplayCountry());

        // Translate the application to fr if user is using French language
        if (locale.getLanguage().equals("fr")) {
            usernameLabel.setText(translate_rb.getString(usernameLabel.getText()));
            passwordLabel.setText(translate_rb.getString(passwordLabel.getText()));
            loginButton.setText(translate_rb.getString(loginButton.getText()));
        }
        
        // Display user's timezone
        ZonedDateTime currentTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("z");
        String curentTimeStr = currentTime.format(formatter);
        userTimeZone.setText(curentTimeStr);
    }

    /**
     * This method defines the action when the "Login" button is clicked
     * @param event
     * @throws IOException 
     */
    @FXML
    private void loginButtonOnClick(ActionEvent event) throws IOException {
        String username;
        String password;
        
        FileWriter logActivityFw = new FileWriter("login_activity.txt", true);
        BufferedWriter logActivityBw = new BufferedWriter(logActivityFw);
        PrintWriter logActivityPw = new PrintWriter(logActivityBw);
        
        LocalDateTime currentTime = LocalDateTime.now();
        
       // Getting username and password from FXML
        username = usernameTextfield.getText();
        password = passwordTextfield.getText();
        
        // Get list of users from the database
        ObservableList<Users> users = getAllUsers();
        
        boolean flag = false;
        boolean isComingApt = false;
        
        for(Users user : users) {
            // Go to customer record view if the login credentials are correct
            if (username.equals(user.getUserName()) && password.equals(user.getUserPassword())) {
                // Alert user if there is appointment comming within 15 minutes
                ObservableList<Appointments> allAppointments = FXCollections.observableArrayList();
                allAppointments = AllAppointmentController.getAllAppointments();
                for (Appointments c : allAppointments) {
                    long timeDiff = currentTime.toLocalTime().until(c.getStartTime(), ChronoUnit.MINUTES);
                    
                    // Print out Appointment Time and Time Different in number
                    System.out.println("Appointment Time" + c.getLocalStartDateTime());
                    System.out.println("Current Time" + currentTime);
                    System.out.println("Time Difference:" + c.getLocalStartDateTime().until(currentTime, ChronoUnit.MINUTES));
                    
                    if (((timeDiff <= 15) && (timeDiff >= 0)) && (currentTime.toLocalDate().equals(c.getStartDate()))) {
                        isComingApt = true;
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("You have an appointment coming");
                        alert.setHeaderText("You have an appointment within 15 minutes!");
                        alert.setContentText("Appointment ID: " + c.getAppointmentId() + "\nAppointment Date and Time: " + c.getStartDateTime());

                        alert.showAndWait();
                    }
                }
                
                if (!isComingApt) {
                    // There is no upcoming appointment
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Appointment");
                    alert.setHeaderText("There is no upcoming appointment");
                    alert.setContentText("Click 'OK' to continue");

                    alert.showAndWait();
                }

                // Log activity when user successfully login
                logActivityPw.println(currentTime + " | "  + username + " | " + password + " | Login Successful");
                logActivityPw.flush();
                
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("customerRecord.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);

                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
                flag = true;
                break;
            }
        }
        
        if (flag == false) {
            
            // Log activity when user fail to login
            logActivityPw.println(currentTime + " | " + username + " | " + password + " | Login Fail");
            logActivityPw.flush();
            
            // Alert error when user enter wrong account information
            Alert alert = new Alert(AlertType.ERROR);
            if (locale.getLanguage().equals("fr")) {
                alert.setTitle("Vous ne pouvez pas vous connecter");
                alert.setHeaderText("Mauvais nom d'utilisateur ou mot de passe");
                alert.setContentText("Vous avez entré un nom d'utilisateur ou un mot de passe incorrect, veuillez réessayer!");
            } else {
                alert.setTitle("Can not login");
                alert.setHeaderText("Wrong username or password");
                alert.setContentText("You have enter wrong username or password, please try again!");
            }

            alert.showAndWait();
        }
        
        logActivityPw.close();
        logActivityBw.close();
        logActivityFw.close();
    }
    
    /**
     * This method gets all User data from the database and return an observable list
     * @return
     */
    public static ObservableList<Users> getAllUsers() {
        ObservableList<Users> clist = FXCollections.observableArrayList();
        
        try {
            String sql = "SELECT * from users";
            
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                int userId = rs.getInt("User_ID");
                String userName = rs.getString("User_Name");
                String password = rs.getString("Password");
                Users c = new Users(userId, userName, password);
                clist.add(c);
            }
            
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        
        return clist;
    }

    /**
     * This method defines the action when the "Exit" button is clicked
     * @param event 
     */
    @FXML
    private void exitBtnOnClick(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    
}