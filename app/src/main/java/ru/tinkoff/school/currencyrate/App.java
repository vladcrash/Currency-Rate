package ru.tinkoff.school.currencyrate;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.tinkoff.school.currencyrate.database.CurrencyDatabase;
import ru.tinkoff.school.currencyrate.models.Currency;
import ru.tinkoff.school.currencyrate.network.CurrencyDeserializer;
import ru.tinkoff.school.currencyrate.network.FixerApi;


public class App extends Application {
    private static final String DATABASE_NAME = "CurrencyDatabase";

    private static FixerApi sFixerApi;
    private static CurrencyDatabase sDatabase;
    private Retrofit mRetrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Currency.class, new CurrencyDeserializer())
                .create();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.fixer.io/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        sFixerApi = mRetrofit.create(FixerApi.class);

        sDatabase = Room.databaseBuilder(getApplicationContext(), CurrencyDatabase.class, DATABASE_NAME)
                .build();
    }

    public static FixerApi getFixerApi() {
        return sFixerApi;
    }

    public static CurrencyDatabase getDatabase() {
        return sDatabase;
    }
}
