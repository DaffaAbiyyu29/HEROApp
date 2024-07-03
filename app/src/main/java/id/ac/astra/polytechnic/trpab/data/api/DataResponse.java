package id.ac.astra.polytechnic.trpab.data.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;

public class DataResponse<T> {
    @SerializedName("result")
    private List<T> result;

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }
}
