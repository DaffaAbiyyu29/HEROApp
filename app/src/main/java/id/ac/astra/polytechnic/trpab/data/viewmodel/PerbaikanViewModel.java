package id.ac.astra.polytechnic.trpab.data.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import id.ac.astra.polytechnic.trpab.data.api.ApiClient;
import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.api.DataResponse;
import id.ac.astra.polytechnic.trpab.data.api.StringResponse;
import id.ac.astra.polytechnic.trpab.data.model.Perbaikan;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerbaikanViewModel extends ViewModel {
    private MutableLiveData<List<Perbaikan>> perbaikanList = new MutableLiveData<>(Collections.emptyList());
    Gson gson = new Gson();

    public PerbaikanViewModel() {
        perbaikanList = new MutableLiveData<>();
//        loadDataFromServer();
    }

    public LiveData<List<Perbaikan>> getPerbaikanList() {
        return perbaikanList;
    }

    public void createPerbaikan(Perbaikan perbaikan, PerbaikanCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("unt_id", perbaikan.getUntId());
            jsonObject.put("pbk_jenis", perbaikan.getJenis());
            jsonObject.put("pbk_creaby", perbaikan.getCreatedBy());

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<StringResponse> call = apiService.createPerbaikan(requestBody);
            call.enqueue(new Callback<StringResponse>() {
                @Override
                public void onResponse(Call<StringResponse> call, Response<StringResponse> response) {
                    Log.d("PerbaikanViewModel", "Response: " + gson.toJson(response.body()));
                    if (response.isSuccessful() && response.body() != null) {
                        StringResponse dataResponse = response.body();
                        if (dataResponse.getResult() != null) {
                            if (callback != null) {
                                callback.onSuccess(dataResponse.getResult());
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailure(new Exception("Result is null"));
                            }
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure(new Exception("Response unsuccessful or body is null"));
                        }
                    }
                }

                @Override
                public void onFailure(Call<StringResponse> call, Throwable t) {
                    if (callback != null) {
                        callback.onFailure(t);
                    }
                    Log.e("PerbaikanViewModel", "Failed to fetch data from server: " + t.getMessage());
                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.onFailure(e);
            }
            Log.e("PerbaikanViewModel", "Failed to create JSON object: " + e.getMessage());
        }
    }

    public interface PerbaikanCallback {
        void onSuccess(String result);
        void onFailure(Throwable t);
    }
}
