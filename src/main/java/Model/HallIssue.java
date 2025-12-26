package Model;

import java.sql.Timestamp;

public class HallIssue {
    private int issueId;
    private int hallId;
    private String hallName;
    private int reporterId;
    private String reporterType;
    private String issueDescription;
    private String status;
    private Timestamp reportedDate;

    public HallIssue(int issueId, int hallId, String hallName, int reporterId, String reporterType, String issueDescription, String status, Timestamp reportedDate) {
        this.issueId = issueId;
        this.hallId = hallId;
        this.hallName = hallName;
        this.reporterId = reporterId;
        this.reporterType = reporterType;
        this.issueDescription = issueDescription;
        this.status = status;
        this.reportedDate = reportedDate;
    }

    public int getIssueId() { return issueId; }
    public int getHallId() { return hallId; }
    public String getHallName() { return hallName; }
    public int getReporterId() { return reporterId; }
    public String getReporterType() { return reporterType; }
    public String getIssueDescription() { return issueDescription; }
    public String getStatus() { return status; }
    public Timestamp getReportedDate() { return reportedDate; }

    public String getFormattedDate() {
        return reportedDate != null ? reportedDate.toString().substring(0, 16) : "N/A";
    }
}