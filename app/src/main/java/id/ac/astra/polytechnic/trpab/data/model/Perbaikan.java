package id.ac.astra.polytechnic.trpab.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Perbaikan {

    @SerializedName("pbk_id")
    private int id;

    @SerializedName("unt_id")
    private Integer untId;

    @SerializedName("pbk_jenis")
    private Integer jenis;

    @SerializedName("pbk_tanggal_awal")
    private Date tanggalAwal;

    @SerializedName("pbk_jam_awal")
    private String jamAwal;

    @SerializedName("pbk_tanggal_akhir")
    private Date tanggalAkhir;

    @SerializedName("pbk_jam_akhir")
    private String jamAkhir;

    @SerializedName("pbk_hours_meter")
    private Integer hoursMeter;

    @SerializedName("pbk_creaby")
    private Integer createdBy;

    @SerializedName("pbk_creadate")
    private Date createDate;

    @SerializedName("pbk_modiby")
    private Integer modifiedBy;

    @SerializedName("pbk_modidate")
    private Date modifyDate;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUntId() {
        return untId;
    }

    public void setUntId(Integer untId) {
        this.untId = untId;
    }

    public Integer getJenis() {
        return jenis;
    }

    public void setJenis(Integer jenis) {
        this.jenis = jenis;
    }

    public Date getTanggalAwal() {
        return tanggalAwal;
    }

    public void setTanggalAwal(Date tanggalAwal) {
        this.tanggalAwal = tanggalAwal;
    }

    public String getJamAwal() {
        return jamAwal;
    }

    public void setJamAwal(String jamAwal) {
        this.jamAwal = jamAwal;
    }

    public Date getTanggalAkhir() {
        return tanggalAkhir;
    }

    public void setTanggalAkhir(Date tanggalAkhir) {
        this.tanggalAkhir = tanggalAkhir;
    }

    public String getJamAkhir() {
        return jamAkhir;
    }

    public void setJamAkhir(String jamAkhir) {
        this.jamAkhir = jamAkhir;
    }

    public Integer getHoursMeter() {
        return hoursMeter;
    }

    public void setHoursMeter(Integer hoursMeter) {
        this.hoursMeter = hoursMeter;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Integer modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
}

