package Model;

import java.sql.Timestamp;

public class Message {
    private String senderType;
    private String text;
    private Timestamp date;

    public Message(String senderType, String text, Timestamp date) {
        this.senderType = senderType;
        this.text = text;
        this.date = date;
    }

    public String getSenderType() { return senderType; }
    public String getText() { return text; }
    public Timestamp getDate() { return date; }

    public String getFormatted() {
        return "[" + date.toString().substring(0, 16) + "] " + senderType + ": " + text;
    }
}