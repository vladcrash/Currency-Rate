package ru.tinkoff.school.currencyrate.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.tinkoff.school.currencyrate.models.ExchangeCurrency;


public interface FixerApi {

    @GET("latest")
    Call<ExchangeCurrency> getCurrencyRate(@Query("base") String from, @Query("symbols") String to,
                                           @Query("date") String date);
}
