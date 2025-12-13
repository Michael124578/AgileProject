package Model;

import java.sql.Timestamp;

public class Material {
    private int materialId;
    private int courseId;
    private String fileName;
    private String filePath;
    private Timestamp uploadDate;

    public Material(int materialId, int courseId, String fileName, String filePath, Timestamp uploadDate) {
        this.materialId = materialId;
        this.courseId = courseId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadDate = uploadDate;
    }

    public int getMaterialId() { return materialId; }
    public int getCourseId() { return courseId; }
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public Timestamp getUploadDate() { return uploadDate; }

    @Override
    public String toString() { return fileName; }
}