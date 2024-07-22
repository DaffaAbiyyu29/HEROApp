package id.ac.astra.polytechnic.trpab.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class RiwayatMaintenance {
    @SerializedName("pbk_id")
    private int pbkId;
    @SerializedName("pbk_creaby")
    private int pbkCreaby;
    @SerializedName("nama_pembuat")
    private String namaPembuat;
    @SerializedName("foto_pembuat")
    private String fotoPembuat;
    @SerializedName("pbk_tanggal_akhir")
    private Date pbkTanggalAkhir;
    @SerializedName("pbk_jam_akhir")
    private String pbkJamAkhir;
    @SerializedName("unt_id")
    private int untId;
    @SerializedName("unt_hours_meter")
    private int untHoursMeter;

    // Constructors, getters, and setters
    public RiwayatMaintenance() {
    }

    public RiwayatMaintenance(int pbkId, int pbkCreaby, String namaPembuat, String fotoPembuat, Date pbkTanggalAkhir, String pbkJamAkhir, int untId, int untHoursMeter) {
        this.pbkId = pbkId;
        this.pbkCreaby = pbkCreaby;
        this.namaPembuat = namaPembuat;
        this.fotoPembuat = fotoPembuat;
        this.pbkTanggalAkhir = pbkTanggalAkhir;
        this.pbkJamAkhir = pbkJamAkhir;
        this.untId = untId;
        this.untHoursMeter = untHoursMeter;
    }

    public int getPbkId() {
        return pbkId;
    }

    public void setPbkId(int pbkId) {
        this.pbkId = pbkId;
    }

    public int getPbkCreaby() {
        return pbkCreaby;
    }

    public void setPbkCreaby(int pbkCreaby) {
        this.pbkCreaby = pbkCreaby;
    }

    public String getNamaPembuat() {
        return namaPembuat;
    }

    public void setNamaPembuat(String namaPembuat) {
        this.namaPembuat = namaPembuat;
    }

    public String getFotoPembuat() {
        return fotoPembuat;
    }

    public void setFotoPembuat(String fotoPembuat) {
        this.fotoPembuat = fotoPembuat;
    }

    public Date getPbkTanggalAkhir() {
        return pbkTanggalAkhir;
    }

    public void setPbkTanggalAkhir(Date pbkTanggalAkhir) {
        this.pbkTanggalAkhir = pbkTanggalAkhir;
    }

    public String getPbkJamAkhir() {
        return pbkJamAkhir;
    }

    public void setPbkJamAkhir(String pbkJamAkhir) {
        this.pbkJamAkhir = pbkJamAkhir;
    }

    public int getUntId() {
        return untId;
    }

    public void setUntId(int untId) {
        this.untId = untId;
    }

    public int getUntHoursMeter() {
        return untHoursMeter;
    }

    public void setUntHoursMeter(int untHoursMeter) {
        this.untHoursMeter = untHoursMeter;
    }
}
