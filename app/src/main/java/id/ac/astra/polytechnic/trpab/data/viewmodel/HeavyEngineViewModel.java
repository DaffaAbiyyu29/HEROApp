package id.ac.astra.polytechnic.trpab.data.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.api.DataResponse;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HeavyEngineViewModel extends ViewModel {

    private static final String BASE_URL = "http://10.1.17.214/";
    private MutableLiveData<List<HeavyEngine>> heavyEngineList;

    public HeavyEngineViewModel() {
        heavyEngineList = new MutableLiveData<>();
        loadDataFromServer();
    }

    public LiveData<List<HeavyEngine>> getHeavyEngineList() {
        return heavyEngineList;
    }

    private void loadDataFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<DataResponse> call = apiService.getDataUnit();

        call.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    heavyEngineList.setValue(response.body().getResult());
                } else {
                    // Handle unsuccessful response
                    heavyEngineList.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                // Handle network failure or other exceptions
                heavyEngineList.setValue(null);
                Log.e("HeavyEngineViewModel", "Failed to fetch data from server: " + t.getMessage());
            }
        });
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
        return filterByStatus(data, "1");
    }

    public List<HeavyEngine> getPendingList() {
        List<HeavyEngine> data = heavyEngineList.getValue();
        return filterByStatus(data, "4");
    }

    public List<HeavyEngine> getUnavailableList() {
        List<HeavyEngine> data = heavyEngineList.getValue();
        return filterByStatus(data, "3", "2");
    }

    public List<HeavyEngine> getProcessMaintenance() {
        List<HeavyEngine> data = heavyEngineList.getValue();
        return filterByStatus(data, "2");
    }
}
