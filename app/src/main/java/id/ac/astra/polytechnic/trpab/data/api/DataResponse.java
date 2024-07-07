package id.ac.astra.polytechnic.trpab.data.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;

public class DataResponse<T> {
    @SerializedName("result")
    private List<T> result;
    @SerializedName("message")
    private String message;

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
