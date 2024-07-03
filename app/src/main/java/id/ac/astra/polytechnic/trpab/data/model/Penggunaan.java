package id.ac.astra.polytechnic.trpab.data.model;

import com.google.gson.annotations.SerializedName;

public class Penggunaan {
    @SerializedName("unt_id")
    private String id;
    @SerializedName("pgn_keterangan")
    private String keterangan;
    @SerializedName("pgn_creaby")
    private String creaby;

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
}

