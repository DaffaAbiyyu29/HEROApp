package id.ac.astra.polytechnic.trpab.data.model;

import com.google.gson.annotations.SerializedName;

public class Schadule {
    @SerializedName("sch_id")
    private String id;

    @SerializedName("sch_nama")
    private String name;

    @SerializedName("sch_hours_meter")
    private String hours;

    @SerializedName("unt_id")
    private String untid;

    @SerializedName("sch_status")
    private String status;

    public Schadule() {
    }

    public Schadule(String id, String name, String hours, String untid, String status) {
        this.id = id;
        this.name = name;
        this.hours = hours;
        this.untid = untid;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getUntid() {
        return untid;
    }

    public void setUntid(String untid) {
        this.untid = untid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
