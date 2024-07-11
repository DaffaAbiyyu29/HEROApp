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
import id.ac.astra.polytechnic.trpab.data.model.User;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<List<User>> userList = new MutableLiveData<>(Collections.emptyList());

    public LoginViewModel() {
        userList = new MutableLiveData<>();
//        loadDataFromServer();
    }

    public LiveData<List<User>> getUserList() {
        return userList;
    }

    public void login(String username, String password, UserCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("username", username);
            jsonObject.put("password", password);

            Gson gson = new Gson();
            Log.i("oooo", gson.toJson(jsonObject));
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<DataResponse<User>> call = apiService.login(requestBody);
            call.enqueue(new Callback<DataResponse<User>>() {
                @Override
                public void onResponse(Call<DataResponse<User>> call, Response<DataResponse<User>> response) {
                    Log.d("ppp", String.valueOf(response));
                    if (response.isSuccessful() && response.body() != null) {
                        DataResponse<User> dataResponse = response.body();
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
                public void onFailure(Call<DataResponse<User>> call, Throwable t) {
                    if (callback != null) {
                        callback.onFailure(t);
                    }
                    Log.e("UserViewModel", "Failed to fetch data from server: " + t.getMessage());
                }
            });
        } catch (JSONException e) {
            if (callback != null) {
                callback.onFailure(e);
            }
            Log.e("UserViewModel", "Failed to create JSON object: " + e.getMessage());
        }
    }

    public interface UserCallback {
        void onSuccess(DataResponse<User> result);
        void onFailure(Throwable t);
    }
}
