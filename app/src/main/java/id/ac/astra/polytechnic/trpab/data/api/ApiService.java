package id.ac.astra.polytechnic.trpab.data.api;

import com.google.gson.JsonObject;

import java.util.List;

import id.ac.astra.polytechnic.trpab.data.model.ChecklistItem;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.data.model.MaintenanceReport;

import id.ac.astra.polytechnic.trpab.data.model.Penggunaan;
import id.ac.astra.polytechnic.trpab.data.model.Perbaikan;
import id.ac.astra.polytechnic.trpab.data.model.Schedule;
import id.ac.astra.polytechnic.trpab.data.model.RiwayatMaintenance;
import id.ac.astra.polytechnic.trpab.data.model.SaveAction;
import id.ac.astra.polytechnic.trpab.data.model.User;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("HEROApp_BE/GetDataUnit.php")
    Call<DataResponse<HeavyEngine>> getDataUnit();

    @POST("HEROApp_BE/SaveActionServiceBySchedule.php")
    Call<JsonObject> saveAction(@Body RequestBody body);

    @POST("HEROApp_BE/SaveActionPerbaikan.php")
    Call<JsonObject> savePerbaikan(@Body RequestBody body);

    @GET("HEROApp_BE/GetPerbaikanList.php")
    Call<DataResponse<Perbaikan>> getPerbaikanList(@Query("unt_id") String untId);

    @POST("HEROApp_BE/GetRiwayatMaintanance.php")
    Call<ResponseBody> getRiwayatMaintanance(@Body RequestBody body);

    @GET("HEROApp_BE/GetDataMaintenanceTotal.php")
    Call<StringResponse> getTotalMaintenance();

    @POST("HEROApp_BE/LoadActionImage.php")
    Call<DataResponse<ChecklistItem>> getImage(@Body RequestBody body);

    @POST("HEROApp_BE/GetDataSchedule.php")
    Call<DataResponse<Schedule>> getDataSchedule(@Body RequestBody body);

    @POST("HEROApp_BE/GetDataAction.php")
    Call<DataResponse<ChecklistItem>> getDataAction(@Body RequestBody body);

    @GET("HEROApp_BE/GetDataUnitDalamPengajuan.php")
    Call<DataResponse<HeavyEngine>> getDataUnitDalamPengajuan();

    @POST("HEROApp_BE/GetDataUnitBySearch.php")
    Call<DataResponse<HeavyEngine>> getDataUnitByName(@Body RequestBody body);

    @POST("HEROApp_BE/CreatePengajuan.php")
    Call<StringResponse> createPengajuan(@Body RequestBody body);

    @POST("HEROApp_BE/CreatePersetujuan.php")
    Call<StringResponse> CreatePersetujuan(@Body RequestBody body);

    @POST("HEROApp_BE/CreatePengembalian.php")
    Call<StringResponse> createPengembalian(@Body RequestBody body);

    @POST("HEROApp_BE/CreateDataPerbaikan.php")
    Call<StringResponse> createPerbaikan(@Body RequestBody body);

    @POST("HEROApp_BE/Login.php")
    Call<DataResponse<User>> login(@Body RequestBody body);

    @POST("HEROApp_BE/GetDataPenggunaanByUnit.php")
    Call<DataResponse<Penggunaan>> getDataPenggunaan(@Body RequestBody body);

    @POST("HEROApp_BE/GetActionServiceByScheduleForPDF.php")
    Call<DataResponse<MaintenanceReport>> getDataActionForReport(@Body RequestBody body);
}
