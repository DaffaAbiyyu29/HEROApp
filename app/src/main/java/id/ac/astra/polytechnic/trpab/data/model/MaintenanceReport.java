package id.ac.astra.polytechnic.trpab.data.model;

import java.util.List;

public class MaintenanceReport {
    private String unt_id;
    private String unt_nama;
    private String pbk_tanggal_awal;
    private String pbk_jam_awal;
    private String pbk_tanggal_akhir;
    private String pbk_jam_akhir;
    private String pbk_hours_meter;
    private String pbk_creaby;
    private String nama_pelaksana;
    private String sch_id;
    private String sch_nama;
    private List<ActionForMaintananceReport> actions;

    public MaintenanceReport() {
    }

    public MaintenanceReport(String unt_id, String unt_nama, String pbk_tanggal_awal, String pbk_jam_awal, String pbk_tanggal_akhir, String pbk_jam_akhir, String pbk_hours_meter, String pbk_creaby, String nama_pelaksana, String sch_id, String sch_nama, List<ActionForMaintananceReport> actions) {
        this.unt_id = unt_id;
        this.unt_nama = unt_nama;
        this.pbk_tanggal_awal = pbk_tanggal_awal;
        this.pbk_jam_awal = pbk_jam_awal;
        this.pbk_tanggal_akhir = pbk_tanggal_akhir;
        this.pbk_jam_akhir = pbk_jam_akhir;
        this.pbk_hours_meter = pbk_hours_meter;
        this.pbk_creaby = pbk_creaby;
        this.nama_pelaksana = nama_pelaksana;
        this.sch_id = sch_id;
        this.sch_nama = sch_nama;
        this.actions = actions;
    }

    public String getUnt_id() {
        return unt_id;
    }

    public void setUnt_id(String unt_id) {
        this.unt_id = unt_id;
    }

    public String getUnt_nama() {
        return unt_nama;
    }

    public void setUnt_nama(String unt_nama) {
        this.unt_nama = unt_nama;
    }

    public String getPbk_tanggal_awal() {
        return pbk_tanggal_awal;
    }

    public void setPbk_tanggal_awal(String pbk_tanggal_awal) {
        this.pbk_tanggal_awal = pbk_tanggal_awal;
    }

    public String getPbk_jam_awal() {
        return pbk_jam_awal;
    }

    public void setPbk_jam_awal(String pbk_jam_awal) {
        this.pbk_jam_awal = pbk_jam_awal;
    }

    public String getPbk_tanggal_akhir() {
        return pbk_tanggal_akhir;
    }

    public void setPbk_tanggal_akhir(String pbk_tanggal_akhir) {
        this.pbk_tanggal_akhir = pbk_tanggal_akhir;
    }

    public String getPbk_jam_akhir() {
        return pbk_jam_akhir;
    }

    public void setPbk_jam_akhir(String pbk_jam_akhir) {
        this.pbk_jam_akhir = pbk_jam_akhir;
    }

    public String getPbk_hours_meter() {
        return pbk_hours_meter;
    }

    public void setPbk_hours_meter(String pbk_hours_meter) {
        this.pbk_hours_meter = pbk_hours_meter;
    }

    public String getPbk_creaby() {
        return pbk_creaby;
    }

    public void setPbk_creaby(String pbk_creaby) {
        this.pbk_creaby = pbk_creaby;
    }

    public String getNama_pelaksana() {
        return nama_pelaksana;
    }

    public void setNama_pelaksana(String nama_pelaksana) {
        this.nama_pelaksana = nama_pelaksana;
    }

    public String getSch_id() {
        return sch_id;
    }

    public void setSch_id(String sch_id) {
        this.sch_id = sch_id;
    }

    public String getSch_nama() {
        return sch_nama;
    }

    public void setSch_nama(String sch_nama) {
        this.sch_nama = sch_nama;
    }

    public List<ActionForMaintananceReport> getActions() {
        return actions;
    }

    public void setActions(List<ActionForMaintananceReport> actions) {
        this.actions = actions;
    }
}
