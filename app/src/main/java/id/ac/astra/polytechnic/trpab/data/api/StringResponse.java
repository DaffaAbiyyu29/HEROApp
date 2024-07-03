package id.ac.astra.polytechnic.trpab.data.api;

import com.google.gson.annotations.SerializedName;

public class StringResponse {
    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
