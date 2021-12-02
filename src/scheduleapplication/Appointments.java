/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduleapplication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Duy Hua
 */
public class Appointments {
    private int appointmentId;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int customerId;
    private int userId;
    private Contacts contact;

    public Appointments(int appointmentId, String title, String description, String location, String type, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, int customerId, int userId, Contacts contact) {
        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerId = customerId;
        this.userId = userId;
        this.contact = contact;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public String getStartDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        String startDateTimeStr = dtf.format(startDateTime);
        return startDateTimeStr;
    }
    
    public LocalDateTime getLocalStartDateTime() {
        return LocalDateTime.of(startDate, startTime);
    }
    
    public String getEndDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
        String endDateTimeStr = dtf.format(endDateTime);
        return endDateTimeStr;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Contacts getContact() {
        return contact;
    }
    
    public String getContactStr() {
        if (contact != null) {
            return contact.getContactName();
        }
        return null;
    }

    public void setContact(Contacts contact) {
        this.contact = contact;
    }

}
