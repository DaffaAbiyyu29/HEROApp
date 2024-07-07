package id.ac.astra.polytechnic.trpab.data.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String id;
    @SerializedName("npk")
    private String npk;
    @SerializedName("nim")
    private String nim;
    @SerializedName("nama")
    private String nama;
    @SerializedName("role")
    private String role;

    public User() {
    }

    public User(String id, String npk, String nim, String nama, String role) {
        this.id = id;
        this.npk = npk;
        this.nim = nim;
        this.nama = nama;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNpk() {
        return npk;
    }

    public void setNpk(String npk) {
        this.npk = npk;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
