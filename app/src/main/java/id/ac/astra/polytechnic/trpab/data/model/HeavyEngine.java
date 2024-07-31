package id.ac.astra.polytechnic.trpab.data.model;

import com.google.gson.annotations.SerializedName;

public class HeavyEngine {
    @SerializedName("unt_id")
    private String id;

    @SerializedName("unt_nama")
    private String title;

    @SerializedName("unt_hours_meter")
    private String hours;

    @SerializedName("unt_status")
    private String status;

    @SerializedName("unt_foto")
    private String imageUrl;

    public HeavyEngine() {
    }

    public HeavyEngine(String id, String title, String hours, String status, String imageUrl) {
        this.id = id;
        this.title = title;
        this.hours = hours;
        this.status = status;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}