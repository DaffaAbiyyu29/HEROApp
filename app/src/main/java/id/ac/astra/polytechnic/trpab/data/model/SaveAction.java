package id.ac.astra.polytechnic.trpab.data.model;

import com.google.gson.annotations.SerializedName;

public class SaveAction {
    @SerializedName("dltsave_service_action_id")
    private String id;

    @SerializedName("act_id")
    private String actid;

    @SerializedName("sch_id")
    private String schid;

    @SerializedName("pbk_id")
    private String pbkid;

    @SerializedName("result_check")
    private String resultcheck;

    public SaveAction() {
    }

    public SaveAction(String id, String actid, String schid, String pbkid, String resultcheck) {
        this.id = id;
        this.actid = actid;
        this.schid = schid;
        this.pbkid = pbkid;
        this.resultcheck = resultcheck;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActid() {
        return actid;
    }

    public void setActid(String actid) {
        this.actid = actid;
    }

    public String getSchid() {
        return schid;
    }

    public void setSchid(String schid) {
        this.schid = schid;
    }

    public String getPbkid() {
        return pbkid;
    }

    public void setPbkid(String pbkid) {
        this.pbkid = pbkid;
    }

    public String getResultcheck() {
        return resultcheck;
    }

    public void setResultcheck(String resultcheck) {
        this.resultcheck = resultcheck;
    }
}
