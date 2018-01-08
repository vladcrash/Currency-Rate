package ru.tinkoff.school.currencyrate.asynchronous;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.tinkoff.school.currencyrate.App;
import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.activities.FilterActivity;
import ru.tinkoff.school.currencyrate.adapters.FilterAdapter;
import ru.tinkoff.school.currencyrate.database.ApiResponseDao;
import ru.tinkoff.school.currencyrate.models.ApiResponse;
import ru.tinkoff.school.currencyrate.models.Currency;
import ru.tinkoff.school.currencyrate.models.Filter;

import static ru.tinkoff.school.currencyrate.activities.FilterActivity.BEGIN_DATE_OTHER;
import static ru.tinkoff.school.currencyrate.activities.FilterActivity.CURRENCY_LIST;
import static ru.tinkoff.school.currencyrate.activities.FilterActivity.END_DATE_OTHER;
import static ru.tinkoff.school.currencyrate.activities.FilterActivity.HISTORY_LIST_SIZE;
import static ru.tinkoff.school.currencyrate.activities.FilterActivity.RADIO_BUTTON_ID;

public class GettingFilterTask extends AsyncTask<Void, Void, List<Currency>> {

    private Filter mFilter;
    private FilterActivity mFilterActivity;
    private ApiResponseDao mApiResponseDao;
    private SharedPreferences mPreferences;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public GettingFilterTask(FilterActivity activity, Filter filter) {
        mFilterActivity = activity;
        mFilter = filter;
        mPreferences = activity.getSharedPreferences(FilterActivity.CURRENCY_DATA, Context.MODE_PRIVATE);
        mApiResponseDao = App.getDatabase().apiResponseDao();
    }

    @Override
    protected List<Currency> doInBackground(Void... voids) {
        int historyListSize = mPreferences.getInt(HISTORY_LIST_SIZE, 0);
        String listPref = mPreferences.getString(CURRENCY_LIST, null);
        Gson gson = new Gson();

        List<Currency> currencies = gson.fromJson(listPref, new TypeToken<ArrayList<Currency>>() {
        }.getType());

        if (currencies == null) {
            Set<Currency> currencySet = new HashSet<>();
            makeFilterList(currencySet);
            mFilter.getCurrencies().addAll(currencySet);
            saveHistoryListSize();
        } else if (mApiResponseDao.getAll().size() > historyListSize) {
            Set<Currency> currencySet = new HashSet<>();
            makeFilterList(currencySet);
            currencies.addAll(getOnlyNewItems(currencySet, currencies));
            mFilter.getCurrencies().addAll(currencies);
            saveHistoryListSize();
        } else {
            mFilter.getCurrencies().addAll(currencies);
        }
        return mFilter.getCurrencies();
    }

    private void makeFilterList(Set<Currency> currencySet) {
        for (ApiResponse apiResponse : mApiResponseDao.getAll()) {
            currencySet.add(new Currency(apiResponse.getBase()));
            currencySet.add(new Currency(apiResponse.getCurrency().getName()));
        }
    }

    private List<Currency> getOnlyNewItems(Set<Currency> currencySet, List<Currency> currencies) {
        List<Currency> subtraction = new ArrayList<>(currencySet.size());
        subtraction.addAll(currencySet);
        subtraction.removeAll(currencies);
        return subtraction;
    }

    private void saveHistoryListSize() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(HISTORY_LIST_SIZE, mApiResponseDao.getAll().size());
        editor.apply();
    }

    @Override
    protected void onPostExecute(List<Currency> list) {
        mFilterActivity.mFilterRecyclerView.setAdapter(new FilterAdapter(list));

        mFilterActivity.initRadioGroup();
        int buttonId = mPreferences.getInt(RADIO_BUTTON_ID, 0);
        if (buttonId != 0) {
            retrieveCorrectValues();

            setTextOnDateButtons(new Date(mFilter.getBeginDateOther()), new Date(mFilter.getEndDateOther()));
            setCheckedButton(buttonId);
        } else {
            setTextOnDateButtons(new Date(), new Date());
            setCheckedButton(R.id.all_time_button);
            setDateOther(mFilter.getDatePickerStartDate().getTime(), mFilter.getDatePickerEndDate().getTime());
        }
    }

    private void retrieveCorrectValues() {
        long startDate = mPreferences.getLong(BEGIN_DATE_OTHER, (new Date()).getTime());
        long endDate = mPreferences.getLong(END_DATE_OTHER, (new Date()).getTime());
        if (startDate < endDate) {
            setDateOther(startDate, endDate);
        } else {
            setDateOther(endDate, startDate);
        }
    }

    private void setTextOnDateButtons(Date start, Date end) {
        mFilter.setDatePickerStartDate(start);
        mFilter.setDatePickerEndDate(end);
        mFilterActivity.mBeginDateButton.setText(sdf.format(start));
        mFilterActivity.mEndDateButton.setText(sdf.format(end));
    }

    private void setDateOther(long start, long end) {
        mFilter.setBeginDateOther(start);
        mFilter.setEndDateOther(end);
    }

    private void setCheckedButton(int id) {
        RadioButton button = mFilterActivity.findViewById(id);
        button.setChecked(true);
        button.jumpDrawablesToCurrentState();
    }
}

