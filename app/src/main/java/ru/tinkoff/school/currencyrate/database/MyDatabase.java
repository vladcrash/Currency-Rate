package ru.tinkoff.school.currencyrate.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ru.tinkoff.school.currencyrate.models.ApiResponse;
import ru.tinkoff.school.currencyrate.models.Currency;


@Database(entities = {ApiResponse.class, Currency.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract ApiResponseDao apiResponseDao();
    public abstract CurrencyDao currencyDao();
}
