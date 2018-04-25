package ru.tinkoff.school.currencyrate.asynchronous;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.tinkoff.school.currencyrate.App;
import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.activities.FilterActivity;
import ru.tinkoff.school.currencyrate.adapters.HistoryAdapter;
import ru.tinkoff.school.currencyrate.database.ExchangeCurrencyDao;
import ru.tinkoff.school.currencyrate.models.ExchangeCurrency;
import ru.tinkoff.school.currencyrate.models.Currency;


public class GettingFilteredCurrencyTask extends AsyncTask<Void, Void, List<ExchangeCurrency>> {

    private WeakReference<Context> mContext;
    private HistoryAdapter mAdapter;
    private ExchangeCurrencyDao mExchangeCurrencyDao;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private String mTimePeriod;

    public GettingFilteredCurrencyTask(Context context, HistoryAdapter adapter) {
        mContext = new WeakReference<>(context);
        mAdapter = adapter;
        mExchangeCurrencyDao = App.getDatabase().exchangeCurrencyDao();
    }

    @Override
    protected List<ExchangeCurrency> doInBackground(Void... voids) {
        Context context = mContext.get();
        List<ExchangeCurrency> list;
        String listPref;
        long startDate;
        long endDate;

        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences(FilterActivity.CURRENCY_DATA, Context.MODE_PRIVATE);
            listPref = preferences.getString(FilterActivity.CURRENCY_LIST, null);
            startDate = preferences.getLong(FilterActivity.BEGIN_DATE, 0);
            endDate = preferences.getLong(FilterActivity.END_DATE, 0);


            Gson gson = new Gson();
            List<Currency> currencies = gson.fromJson(listPref, new TypeToken<ArrayList<Currency>>() {
            }.getType());

            if (currencies != null) {
                List<String> filtered = new ArrayList<>();

                for (Currency currency : currencies) {
                    if (currency.isFavourite()) {
                        filtered.add(currency.getTo());
                    }
                }

                if (!filtered.isEmpty() && startDate != 0) {
                    list = mExchangeCurrencyDao.getFilteredByChosenTime(filtered, startDate, endDate);
                    mTimePeriod = sdf.format(startDate) + " - " + sdf.format(endDate);
                } else if (!filtered.isEmpty() && startDate == 0) {
                    list = mExchangeCurrencyDao.getFilteredByAllTime(filtered);
                    mTimePeriod = context.getString(R.string.all_currencies_in_the_world);
                } else if (filtered.isEmpty() && startDate != 0) {
                    list = mExchangeCurrencyDao.getAllByChosenTime(startDate, endDate);
                    mTimePeriod = sdf.format(startDate) + " - " + sdf.format(endDate);
                } else {
                    list = mExchangeCurrencyDao.getAll();
                    mTimePeriod = context.getString(R.string.all_currencies_in_the_world);
                }
            } else {
                list = mExchangeCurrencyDao.getAll();
                mTimePeriod = context.getString(R.string.story);
            }

            return list;
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<ExchangeCurrency> currencyHistories) {
        Context context = mContext.get();
        if (context != null) {
            mAdapter.setCurrencyHistories(currencyHistories);
            ((AppCompatActivity) context).setTitle(mTimePeriod);
        }
    }

}


