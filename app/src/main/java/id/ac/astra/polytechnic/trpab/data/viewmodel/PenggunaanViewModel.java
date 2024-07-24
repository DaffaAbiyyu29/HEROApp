package id.ac.astra.polytechnic.trpab.data.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import id.ac.astra.polytechnic.trpab.data.api.ApiClient;
import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.api.DataResponse;
import id.ac.astra.polytechnic.trpab.data.api.StringResponse;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.data.model.Penggunaan;
import id.ac.astra.polytechnic.trpab.data.model.Penggunaan;
import id.ac.astra.polytechnic.trpab.data.model.Penggunaan;
import id.ac.astra.polytechnic.trpab.data.model.User;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PenggunaanViewModel extends ViewModel {
    private MutableLiveData<List<Penggunaan>> penggunaanList = new MutableLiveData<>(Collections.emptyList());
    Gson gson = new Gson();

    public PenggunaanViewModel() {
        penggunaanList = new MutableLiveData<>();
//        loadDataFromServer();
    }

    public LiveData<List<Penggunaan>> getPenggunaanList() {
        return penggunaanList;
    }

    public void createPengajuan(Penggunaan penggunaan, PenggunaanCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("unt_id", penggunaan.getId());
            jsonObject.put("pgn_keterangan", penggunaan.getKeterangan());
            jsonObject.put("pgn_creaby", penggunaan.getCreaby());

            Log.d("oooo", "ppp : " + jsonObject);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<StringResponse> call = apiService.createPengajuan(requestBody);
            call.enqueue(new Callback<StringResponse>() {
                @Override
                public void onResponse(Call<StringResponse> call, Response<StringResponse> response) {
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
                    Log.e("PenggunaanViewModel", "Failed to fetch data from server: " + t.getMessage());
                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.onFailure(e);
            }
            Log.e("PenggunaanViewModel", "Failed to create JSON object: " + e.getMessage());
        }
    }

    public void CreatePersetujuan(Penggunaan penggunaan, PenggunaanCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("unt_id", penggunaan.getId());
            jsonObject.put("pgn_modiby", penggunaan.getModiby());

            Log.d("oooo", "ppp : " + jsonObject);

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<StringResponse> call = apiService.CreatePersetujuan(requestBody);
            call.enqueue(new Callback<StringResponse>() {
                @Override
                public void onResponse(Call<StringResponse> call, Response<StringResponse> response) {
                    Log.d("PenggunaanViewModel", "Response: " + gson.toJson(response.body()));
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
                    Log.e("PenggunaanViewModel", "Failed to fetch data from server: " + t.getMessage());
                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.onFailure(e);
            }
            Log.e("PenggunaanViewModel", "Failed to create JSON object: " + e.getMessage());
        }
    }

    public void createPengembalian(Penggunaan penggunaan, PenggunaanCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("unt_id", penggunaan.getId());
            jsonObject.put("pgn_hours_meter_akhir", penggunaan.getHoursMeterAkhir());
            jsonObject.put("pgn_modiby", penggunaan.getModiby());

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<StringResponse> call = apiService.createPengembalian(requestBody);
            call.enqueue(new Callback<StringResponse>() {
                @Override
                public void onResponse(Call<StringResponse> call, Response<StringResponse> response) {
                    Log.d("PenggunaanViewModel", "Response: " + gson.toJson(response.body()));
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
                    Log.e("PenggunaanViewModel", "Failed to fetch data from server: " + t.getMessage());
                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.onFailure(e);
            }
            Log.e("PenggunaanViewModel", "Failed to create JSON object: " + e.getMessage());
        }
    }

    public void getTotalMaintenance(PenggunaanCallback callback) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<StringResponse> call = apiService.getTotalMaintenance();

        call.enqueue(new Callback<StringResponse>() {
            @Override
            public void onResponse(Call<StringResponse> call, Response<StringResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringResponse dataResponse = response.body();
                    callback.onSuccess(dataResponse.getResult());
                } else {
                    // Handle unsuccessful response
                    callback.onFailure(new Exception("Result is null"));
                }
            }

            @Override
            public void onFailure(Call<StringResponse> call, Throwable t) {
                // Handle network failure or other exceptions
                callback.onFailure(new Exception("Response unsuccessful or body is null"));
                Log.e("HeavyEngineViewModel", "Failed to fetch data from server: " + t.getMessage());
            }
        });
    }

    public void getDataPenggunaan(String untId, PenggunaanViewModel.PenggunaanCallback2 callback) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("unt_id", untId);

            Gson gson = new Gson();
            Log.i("oooo", gson.toJson(jsonObject));
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<DataResponse<Penggunaan>> call = apiService.getDataPenggunaan(requestBody);
            call.enqueue(new Callback<DataResponse<Penggunaan>>() {
                @Override
                public void onResponse(Call<DataResponse<Penggunaan>> call, Response<DataResponse<Penggunaan>> response) {
                    Log.d("ppp", String.valueOf(response));
                    if (response.isSuccessful() && response.body() != null) {
                        DataResponse<Penggunaan> dataResponse = response.body();
                        if (dataResponse.getResult() != null) {
                            if (callback != null) {
                                callback.onSuccess(dataResponse);
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
                public void onFailure(Call<DataResponse<Penggunaan>> call, Throwable t) {
                    if (callback != null) {
                        callback.onFailure(t);
                    }
                    Log.e("PenggunaanViewModel", "Failed to fetch data from server: " + t.getMessage());
                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.onFailure(e);
            }
            Log.e("PenggunaanViewModel", "Failed to create JSON object: " + e.getMessage());
        }
    }

    public interface PenggunaanCallback {
        void onSuccess(String result);
        void onFailure(Throwable t);
    }

    public interface PenggunaanCallback2 {
        void onSuccess(DataResponse<Penggunaan> result);
        void onFailure(Throwable t);
    }
}
