package ru.tinkoff.school.currencyrate.models;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


@Entity(tableName = "exchange_currency")
public class ExchangeCurrency {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @SerializedName("base")
    private String from;
    private double value;

    @Embedded
    @SerializedName("rates")
    private Currency currency;

    public ExchangeCurrency() {

    }

    @Ignore
    public ExchangeCurrency(String from, double valueFrom, String to, double valueTo) {
        this.from = from;
        this.value = valueFrom;
        currency = new Currency(valueTo, to);

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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
        return value + " " + from + " = " +
                currency.getValue() + " " + currency.getTo();
    }
}
