package id.ac.astra.polytechnic.trpab.data.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("HEROApp_BE/GetDataUnit.php")
    Call<DataResponse> getDataUnit();
}
