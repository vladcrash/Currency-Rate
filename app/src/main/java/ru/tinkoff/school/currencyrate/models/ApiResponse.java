package ru.tinkoff.school.currencyrate.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


@Entity(tableName = "api_response")
public class ApiResponse {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String base;
    private double value;

    @Embedded
    @SerializedName("rates")
    private Currency currency;

    public ApiResponse() {

    }

    @Ignore
    public ApiResponse(String from, double valueFrom, String to, double valueTo) {
        this.base = from;
        this.value = valueFrom;
        currency = new Currency(valueTo, to);

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return value + " " + base + " = " +
                currency.getValue() + " " + currency.getName();
    }
}
