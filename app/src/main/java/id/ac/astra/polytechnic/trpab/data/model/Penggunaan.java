package id.ac.astra.polytechnic.trpab.data.model;

import com.google.gson.annotations.SerializedName;

public class Penggunaan {
    @SerializedName("unt_id")
    private String id;
    @SerializedName("pgn_keterangan")
    private String keterangan;
    @SerializedName("pgn_creaby")
    private String creaby;
    @SerializedName("pgn_hours_meter_akhir")
    private String hoursMeterAkhir;
    @SerializedName("pgn_modiby")
    private String modiby;

    public Penggunaan() {
    }

    public Penggunaan(String id, String keterangan, String creaby) {
        this.id = id;
        this.keterangan = keterangan;
        this.creaby = creaby;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getCreaby() {
        return creaby;
    }

    public void setCreaby(String creaby) {
        this.creaby = creaby;
    }

    public String getHoursMeterAkhir() {
        return hoursMeterAkhir;
    }

    public void setHoursMeterAkhir(String hoursMeterAkhir) {
        this.hoursMeterAkhir = hoursMeterAkhir;
    }

    public String getModiby() {
        return modiby;
    }

    public void setModiby(String modiby) {
        this.modiby = modiby;
    }
}

