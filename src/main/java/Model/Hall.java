package Model;

public class Hall {
    private int hallId;
    private String hallName;
    private int capacity;
    private boolean isActive;
    private String hallType;

    public Hall(int hallId, String hallName, int capacity, boolean isActive, String hallType) {
        this.hallId = hallId;
        this.hallName = hallName;
        this.capacity = capacity;
        this.isActive = isActive;
        this.hallType = hallType;
    }

    public int getHallId() { return hallId; }
    public String getHallName() { return hallName; }
    public int getCapacity() { return capacity; }
    public boolean isActive() { return isActive; }
    public String getHallType() { return hallType; }

    @Override
    public String toString() {
        return hallName + " (" + hallType + ")";
    }
}