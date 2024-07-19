package id.ac.astra.polytechnic.trpab.data.model;

public class ActionForMaintananceReport {
    private String act_id;
    private String act_nama;
    private String act_foto;
    private String act_keterangan;
    private String result_check;

    public ActionForMaintananceReport() {
    }

    public ActionForMaintananceReport(String act_id, String act_nama, String act_foto, String act_keterangan, String result_check) {
        this.act_id = act_id;
        this.act_nama = act_nama;
        this.act_foto = act_foto;
        this.act_keterangan = act_keterangan;
        this.result_check = result_check;
    }

    public String getAct_id() {
        return act_id;
    }

    public void setAct_id(String act_id) {
        this.act_id = act_id;
    }

    public String getAct_nama() {
        return act_nama;
    }

    public void setAct_nama(String act_nama) {
        this.act_nama = act_nama;
    }

    public String getAct_foto() {
        return act_foto;
    }

    public void setAct_foto(String act_foto) {
        this.act_foto = act_foto;
    }

    public String getAct_keterangan() {
        return act_keterangan;
    }

    public void setAct_keterangan(String act_keterangan) {
        this.act_keterangan = act_keterangan;
    }

    public String getResult_check() {
        return result_check;
    }

    public void setResult_check(String result_check) {
        this.result_check = result_check;
    }
}
