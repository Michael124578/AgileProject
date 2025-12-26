package Model;

import java.sql.Date;

public class HallBooking {
    private int bookingId;
    private int hallId;
    private String hallName;
    private int studentId;
    private Date bookingDate;
    private String startTime;
    private String endTime;
    private String purpose;
    private String status;

    public HallBooking(int bookingId, int hallId, String hallName, int studentId, Date bookingDate, String startTime, String endTime, String purpose, String status) {
        this.bookingId = bookingId;
        this.hallId = hallId;
        this.hallName = hallName;
        this.studentId = studentId;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = status;
    }

    public int getBookingId() { return bookingId; }
    public int getHallId() { return hallId; }
    public String getHallName() { return hallName; }
    public Date getBookingDate() { return bookingDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getPurpose() { return purpose; }
    public String getStatus() { return status; }
}