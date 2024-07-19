package id.ac.astra.polytechnic.trpab.data.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import id.ac.astra.polytechnic.trpab.data.api.ApiService;
import id.ac.astra.polytechnic.trpab.data.api.DataResponse;
import id.ac.astra.polytechnic.trpab.data.model.MaintenanceReport;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaintananceReportViewModel extends ViewModel {
    private MutableLiveData<List<MaintenanceReport>> maintenanceReportList = new MutableLiveData<>();
    private ApiService apiService;
    private Gson gson = new Gson();

    public MaintananceReportViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public MutableLiveData<List<MaintenanceReport>> getMaintenanceReportList() {
        return maintenanceReportList;
    }

    public void fetchDataForReport(String untId, String pbkId, final ReportCallback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("unt_id", untId);
            jsonObject.put("pbk_id", pbkId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create RequestBody from JSON object
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Call<DataResponse<MaintenanceReport>> call = apiService.getDataActionForReport(requestBody);
        call.enqueue(new Callback<DataResponse<MaintenanceReport>>() {
            @Override
            public void onResponse(Call<DataResponse<MaintenanceReport>> call, Response<DataResponse<MaintenanceReport>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MaintenanceReport> reports = response.body().getResult();
                    maintenanceReportList.setValue(reports);
                    callback.onSuccess(reports);
                } else {
                    callback.onFailure(new Throwable("Response unsuccessful or body is null"));
                }
            }

            @Override
            public void onFailure(Call<DataResponse<MaintenanceReport>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public interface ReportCallback {
        void onSuccess(List<MaintenanceReport> result);
        void onFailure(Throwable t);
    }
}
