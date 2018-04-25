package ru.tinkoff.school.currencyrate.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.tinkoff.school.currencyrate.models.ExchangeCurrency;

@Dao
public interface ExchangeCurrencyDao {

    @Query("SELECT * FROM exchange_currency")
    List<ExchangeCurrency> getAll();

    @Query("SELECT mTo FROM exchange_currency " +
            "UNION " +
            "SELECT `from` FROM exchange_currency")
    List<String> getUniqueCurrencyNames();

    @Query("SELECT * FROM exchange_currency " +
            "WHERE (`from` IN (:list) OR mTo IN (:list)) " +
            "AND (recent >= :startDate AND recent <= :endDate)")
    List<ExchangeCurrency> getFilteredByChosenTime(List<String> list, long startDate, long endDate);

    @Query("SELECT * FROM exchange_currency " +
            "WHERE recent >= :startDate AND recent <= :endDate")
    List<ExchangeCurrency> getAllByChosenTime(long startDate, long endDate);

    @Query("SELECT * FROM exchange_currency " +
            "WHERE (`from` IN (:list) OR mTo IN (:list))")
    List<ExchangeCurrency> getFilteredByAllTime(List<String> list);

    @Insert
    void insert(ExchangeCurrency exchangeCurrency);
}
