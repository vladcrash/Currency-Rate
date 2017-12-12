package ru.tinkoff.school.currencyrate.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.tinkoff.school.currencyrate.models.Currency;


@Dao
public interface CurrencyDao {

    @Query("SELECT * FROM currency")
    List<Currency> getAll();

    @Insert
    void insertAll(List<Currency> currencies);

    @Update
    void update(Currency currency);
}
