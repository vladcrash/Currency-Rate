package ru.tinkoff.school.currencyrate.asynchronous;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.tinkoff.school.currencyrate.App;
import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.activities.FilterActivity;
import ru.tinkoff.school.currencyrate.adapters.HistoryAdapter;
import ru.tinkoff.school.currencyrate.database.ApiResponseDao;
import ru.tinkoff.school.currencyrate.models.ApiResponse;
import ru.tinkoff.school.currencyrate.models.Currency;


public class GettingFilteredCurrencyTask extends AsyncTask<Void, Void, List<ApiResponse>> {

    private Context mContext;
    private HistoryAdapter mAdapter;
    private ApiResponseDao mApiResponseDao;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private String mTimePeriod;

    public GettingFilteredCurrencyTask(Context context, HistoryAdapter adapter) {
        mContext = context;
        mAdapter = adapter;
        mApiResponseDao = App.getDatabase().apiResponseDao();
    }

    @Override
    protected List<ApiResponse> doInBackground(Void... voids) {

        List<ApiResponse> list;
        SharedPreferences preferences = mContext.getSharedPreferences(FilterActivity.CURRENCY_DATA, Context.MODE_PRIVATE);
        String listPref = preferences.getString(FilterActivity.CURRENCY_LIST, null);
        long startDate = preferences.getLong(FilterActivity.BEGIN_DATE, 0);
        long endDate = preferences.getLong(FilterActivity.END_DATE, 0);
        Gson gson = new Gson();
        List<Currency> currencies = gson.fromJson(listPref, new TypeToken<ArrayList<Currency>>() {
        }.getType());

        if (currencies != null) {
            List<String> filtered = new ArrayList<>();

            for (Currency currency : currencies) {
                if (currency.isFavourite()) {
                    filtered.add(currency.getName());
                }
            }

            if (!filtered.isEmpty() && startDate != 0) {
                list = mApiResponseDao.getFilteredByChosenTime(filtered, startDate, endDate);
                mTimePeriod = sdf.format(startDate) + " - " + sdf.format(endDate);
            } else if (!filtered.isEmpty() && startDate == 0) {
                list = mApiResponseDao.getFilteredByAllTime(filtered);
                mTimePeriod = mContext.getString(R.string.all_currencies_in_the_world);
            } else if (filtered.isEmpty() && startDate != 0) {
                list = mApiResponseDao.getAllByChosenTime(startDate, endDate);
                mTimePeriod = sdf.format(startDate) + " - " + sdf.format(endDate);
            } else {
                list = mApiResponseDao.getAll();
                mTimePeriod = mContext.getString(R.string.all_currencies_in_the_world);
            }
        } else {
            list = mApiResponseDao.getAll();
            mTimePeriod = mContext.getString(R.string.story);
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<ApiResponse> currencyHistories) {
        mAdapter.setCurrencyHistories(currencyHistories);
        ((AppCompatActivity) mContext).setTitle(mTimePeriod);
    }

}


