package id.ac.astra.polytechnic.trpab.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class SavePerbaikan {
    @SerializedName("mtc_id")
    private String id;

    @SerializedName("pbk_id")
    private String pbk_id;

    @SerializedName("mtc_keterangan")
    private String keterangan;

    @SerializedName("mtc_creadate")
    private Date creadate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPbk_id() {
        return pbk_id;
    }

    public void setPbk_id(String pbk_id) {
        this.pbk_id = pbk_id;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Date getCreadate() {
        return creadate;
    }

    public void setCreadate(Date creadate) {
        this.creadate = creadate;
    }
}
