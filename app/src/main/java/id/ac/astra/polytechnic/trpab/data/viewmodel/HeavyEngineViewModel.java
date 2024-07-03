package id.ac.astra.polytechnic.trpab.data.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import id.ac.astra.polytechnic.trpab.data.api.ApiClient;
import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.api.DataResponse;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HeavyEngineViewModel extends ViewModel {
    private MutableLiveData<List<HeavyEngine>> heavyEngineList = new MutableLiveData<>(Collections.emptyList());

    public HeavyEngineViewModel() {
        heavyEngineList = new MutableLiveData<>();
        loadDataFromServer();
    }

    public LiveData<List<HeavyEngine>> getHeavyEngineList() {
        return heavyEngineList;
    }

    private void loadDataFromServer() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<DataResponse<HeavyEngine>> call = apiService.getDataUnit();

        call.enqueue(new Callback<DataResponse<HeavyEngine>>() {
            @Override
            public void onResponse(Call<DataResponse<HeavyEngine>> call, Response<DataResponse<HeavyEngine>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    heavyEngineList.setValue(response.body().getResult());
                } else {
                    // Handle unsuccessful response
                    heavyEngineList.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<DataResponse<HeavyEngine>> call, Throwable t) {
                // Handle network failure or other exceptions
                heavyEngineList.setValue(null);
                Log.e("HeavyEngineViewModel", "Failed to fetch data from server: " + t.getMessage());
            }
        });
    }

    public void fetchDataUnitByName(String untNama, List<Integer> untStatus) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("unt_nama", untNama);

            if (untStatus != null && !untStatus.isEmpty()) {
                JSONArray statusArray = new JSONArray();
                for (Integer status : untStatus) {
                    statusArray.put(status);
                }
                jsonObject.put("unt_status", statusArray);
            } else {
                jsonObject.put("unt_status", JSONObject.NULL);
            }

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<DataResponse<HeavyEngine>> call = apiService.getDataUnitByName(requestBody);
            call.enqueue(new Callback<DataResponse<HeavyEngine>>() {
                @Override
                public void onResponse(Call<DataResponse<HeavyEngine>> call, Response<DataResponse<HeavyEngine>> response) {
                    if (response.isSuccessful()) {
                        // Handle the response
                        DataResponse<HeavyEngine> dataResponse = response.body();
                        heavyEngineList.setValue(response.body().getResult());
                    } else {
                        // Handle the error
                        heavyEngineList.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<DataResponse<HeavyEngine>> call, Throwable t) {
                    heavyEngineList.setValue(null);
                    Log.e("HeavyEngineViewModel", "Failed to fetch data from server: " + t.getMessage());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<HeavyEngine> filterByStatus(List<HeavyEngine> data, String... statuses) {
        List<HeavyEngine> filteredList = new ArrayList<>();
        for (HeavyEngine engine : data) {
            for (String status : statuses) {
                if (engine.getStatus().equals(status)) {
                    filteredList.add(engine);
                    break;
                }
            }
        }
        return filteredList;
    }

    public List<HeavyEngine> getAvailableList() {
        List<HeavyEngine> data = heavyEngineList.getValue();
        if (data == null) {
            return new ArrayList<>();
        }
        return filterByStatus(data, "1");
    }

    public List<HeavyEngine> getPendingList() {
        List<HeavyEngine> data = heavyEngineList.getValue();
        if (data == null) {
            return new ArrayList<>();
        }
        return filterByStatus(data, "4");
    }

    public List<HeavyEngine> getUnavailableList() {
        List<HeavyEngine> data = heavyEngineList.getValue();
        if (data == null) {
            return new ArrayList<>();
        }
        return filterByStatus(data, "3", "2");
    }

    public List<HeavyEngine> getProcessMaintenancePerbaikan() {
        List<HeavyEngine> data = heavyEngineList.getValue();
        if (data == null) {
            return new ArrayList<>();
        }
        return filterByStatus(data, "2");
    }

    public List<HeavyEngine> getProcessMaintenanceService() {
        List<HeavyEngine> data = heavyEngineList.getValue();
        if (data == null) {
            return new ArrayList<>();
        }
        return filterByStatus(data, "5");
    }
}
