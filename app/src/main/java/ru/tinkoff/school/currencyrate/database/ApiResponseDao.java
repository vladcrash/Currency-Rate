package ru.tinkoff.school.currencyrate.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.tinkoff.school.currencyrate.models.ApiResponse;

@Dao
public interface ApiResponseDao {

    @Query("SELECT * FROM api_response")
    List<ApiResponse> getAll();

    @Query("SELECT * FROM api_response " +
            "WHERE (base IN (:list) OR name IN (:list)) " +
            "AND (recent >= :startDate AND recent <= :endDate)")
    List<ApiResponse> getFilteredByChosenTime(List<String> list, long startDate, long endDate);

    @Query("SELECT * FROM api_response " +
            "WHERE recent >= :startDate AND recent <= :endDate")
    List<ApiResponse> getAllByChosenTime(long startDate, long endDate);

    @Query("SELECT * FROM api_response " +
            "WHERE (base IN (:list) OR name IN (:list))")
    List<ApiResponse> getFilteredByAllTime(List<String> list);

    @Insert
    void insert(ApiResponse apiResponse);
}
