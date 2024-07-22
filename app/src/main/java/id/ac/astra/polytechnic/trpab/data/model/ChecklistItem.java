package id.ac.astra.polytechnic.trpab.data.model;

import com.google.gson.annotations.SerializedName;

public class ChecklistItem {
    @SerializedName("act_id")
    private String id;

    @SerializedName("act_nama")
    private String name;

    @SerializedName("act_foto")
    private String Foto;

    @SerializedName("act_keterangan")
    private String keterangan;

    @SerializedName("act_status")
    private String status;

    @SerializedName("sch_id")
    private String schid;

    private boolean isChecked; // Add this field to track if the item is checked

    public ChecklistItem() {
    }

    public ChecklistItem(String id, String name, String foto, String keterangan, String status, String schid, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.Foto = foto;
        this.keterangan = keterangan;
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

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSchid() {
        return schid;
    }

    public void setSchid(String schid) {
        this.schid = schid;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
