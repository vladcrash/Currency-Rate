package ru.tinkoff.school.currencyrate.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ru.tinkoff.school.currencyrate.models.ExchangeCurrency;
import ru.tinkoff.school.currencyrate.models.Currency;


@Database(entities = {ExchangeCurrency.class, Currency.class}, version = 1)
public abstract class CurrencyDatabase extends RoomDatabase {
    public abstract ExchangeCurrencyDao exchangeCurrencyDao();
    public abstract CurrencyDao currencyDao();
}
