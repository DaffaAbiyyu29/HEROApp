package id.ac.astra.polytechnic.trpab.data.model;

import com.google.gson.annotations.SerializedName;

public class ChecklistItem {
    @SerializedName("act_id")
    private String id;

    @SerializedName("act_nama")
    private String name;

    @SerializedName("act_status")
    private String status;

    @SerializedName("sch_id")
    private String schid;

    private boolean isChecked;

    public ChecklistItem() {
    }

    public ChecklistItem(String id, String name, String status, String schid, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.schid = schid;
        this.isChecked = isChecked;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUntid() {
        return schid;
    }

    public void setUntid(String untid) {
        this.schid = untid;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
