package id.ac.astra.polytechnic.trpab.ui.home;

public class HomeItem {
    private String title;
    private String hours;
    private String status;
    private int imageResId;

    public HomeItem(String title, String hours, String status, int imageResId) {
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
