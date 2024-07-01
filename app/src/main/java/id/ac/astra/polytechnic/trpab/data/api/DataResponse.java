package id.ac.astra.polytechnic.trpab.data.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;

public class DataResponse {
    @SerializedName("result")
    private List<HeavyEngine> result;

    public List<HeavyEngine> getResult() {
        return result;
    }

    public void setResult(List<HeavyEngine> result) {
        this.result = result;
    }
}
