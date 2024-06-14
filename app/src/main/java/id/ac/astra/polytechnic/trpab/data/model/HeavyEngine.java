package id.ac.astra.polytechnic.trpab.data.model;

public class HeavyEngine {
    private String id;
    private String title;
    private String hours;
    private String status;
    private int imageResId;

    public HeavyEngine(String id, String title, String hours, String status, int imageResId) {
        this.id = id;
        this.title = title;
        this.hours = hours;
        this.status = status;
        this.imageResId = imageResId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
