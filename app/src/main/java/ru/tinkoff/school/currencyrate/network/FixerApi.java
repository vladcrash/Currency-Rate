package ru.tinkoff.school.currencyrate.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.tinkoff.school.currencyrate.models.ApiResponse;


public interface FixerApi {

    @GET("latest")
    Call<ApiResponse> getCurrencyRate(@Query("base") String from, @Query("symbols") String to);

    @GET("latest?")
    Call<ApiResponse> getRateByDate(@Query("base") String from, @Query("symbols") String to,
                                    @Query("date") String date);
}
