package id.ac.astra.polytechnic.trpab.data.api;

import id.ac.astra.polytechnic.trpab.data.model.ChecklistItem;
import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.data.model.MaintenanceReport;
import id.ac.astra.polytechnic.trpab.data.model.Schadule;
import id.ac.astra.polytechnic.trpab.data.model.User;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("HEROApp_BE/GetDataUnit.php")
    Call<DataResponse<HeavyEngine>> getDataUnit();

    @POST("HEROApp_BE/LoadActionImage.php")
    Call<DataResponse<ChecklistItem>> getImage(@Body RequestBody body);

    @POST("HEROApp_BE/GetDataSchedule.php")
    Call<DataResponse<Schadule>> getDataSchedule(@Body RequestBody body);

    @POST("HEROApp_BE/GetDataAction.php")
    Call<DataResponse<ChecklistItem>> getDataAction(@Body RequestBody body);

    @POST("HEROApp_BE/GetDataUnitBySearch.php")
    Call<DataResponse<HeavyEngine>> getDataUnitByName(@Body RequestBody body);

    @POST("HEROApp_BE/CreatePengajuan.php")
    Call<StringResponse> createPengajuan(@Body RequestBody body);

    @POST("HEROApp_BE/Login.php")
    Call<DataResponse<User>> login(@Body RequestBody body);
    @POST("HEROApp_BE/GetActionServiceByScheduleForPDF.php")
    Call<DataResponse<MaintenanceReport>> getDataActionForReport(@Body RequestBody body);
}
