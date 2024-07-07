package id.ac.astra.polytechnic.trpab.data.api;

import id.ac.astra.polytechnic.trpab.data.model.HeavyEngine;
import id.ac.astra.polytechnic.trpab.data.model.Penggunaan;
import id.ac.astra.polytechnic.trpab.data.model.User;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("HEROApp_BE/GetDataUnit.php")
    Call<DataResponse<HeavyEngine>> getDataUnit();

    @POST("HEROApp_BE/GetDataUnitBySearch.php")
    Call<DataResponse<HeavyEngine>> getDataUnitByName(@Body RequestBody body);

    @POST("HEROApp_BE/CreatePengajuan.php")
    Call<StringResponse> createPengajuan(@Body RequestBody body);

    @POST("HEROApp_BE/Login.php")
    Call<DataResponse<User>> login(@Body RequestBody body);
}
