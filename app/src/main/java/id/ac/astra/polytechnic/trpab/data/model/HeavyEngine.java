package id.ac.astra.polytechnic.trpab.data.model;

public class HeavyEngine {
    private String title;
    private String hours;
    private String status;
    private int imageResId;

    public HeavyEngine(String title, String hours, String status, int imageResId) {
        this.title = title;
        this.hours = hours;
        this.status = status;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getHours() {
        return hours;
    }

    public String getStatus() {
        return status;
    }

    public int getImageResId() {
        return imageResId;
    }
}
