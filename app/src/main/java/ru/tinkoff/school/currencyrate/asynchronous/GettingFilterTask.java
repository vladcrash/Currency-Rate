package ru.tinkoff.school.currencyrate.asynchronous;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.tinkoff.school.currencyrate.App;
import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.activities.FilterActivity;
import ru.tinkoff.school.currencyrate.adapters.FilterAdapter;
import ru.tinkoff.school.currencyrate.database.ExchangeCurrencyDao;
import ru.tinkoff.school.currencyrate.models.Currency;
import ru.tinkoff.school.currencyrate.models.Filter;

import static ru.tinkoff.school.currencyrate.activities.FilterActivity.BEGIN_DATE_OTHER;
import static ru.tinkoff.school.currencyrate.activities.FilterActivity.CURRENCY_LIST;
import static ru.tinkoff.school.currencyrate.activities.FilterActivity.END_DATE_OTHER;
import static ru.tinkoff.school.currencyrate.activities.FilterActivity.HISTORY_LIST_SIZE;
import static ru.tinkoff.school.currencyrate.activities.FilterActivity.RADIO_BUTTON_ID;

public class GettingFilterTask extends AsyncTask<Void, Void, List<Currency>> {
    private static final String TAG = GettingFilterTask.class.getSimpleName();

    private Filter mFilter;
    private WeakReference<FilterActivity> mFilterActivityWeakReference;
    private ExchangeCurrencyDao mExchangeCurrencyDao;
    private SharedPreferences mPreferences;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public GettingFilterTask(FilterActivity activity, Filter filter) {
        mFilterActivityWeakReference = new WeakReference<>(activity);
        mFilter = filter;
        mPreferences = activity.getSharedPreferences(FilterActivity.CURRENCY_DATA, Context.MODE_PRIVATE);
        mExchangeCurrencyDao = App.getDatabase().exchangeCurrencyDao();
    }

    @Override
    protected List<Currency> doInBackground(Void... voids) {
        int historyListSize = mPreferences.getInt(HISTORY_LIST_SIZE, 0);
        String listPref = mPreferences.getString(CURRENCY_LIST, null);
        Gson gson = new Gson();

        List<Currency> currencies = gson.fromJson(listPref, new TypeToken<ArrayList<Currency>>() {
        }.getType());

        if (currencies == null) {
            mFilter.getCurrencies().addAll(getCurrencyList());
            saveHistoryListSize();
        } else if (mExchangeCurrencyDao.getAll().size() > historyListSize) {
            currencies.addAll(getOnlyNewItems(getCurrencyList(), currencies));
            mFilter.getCurrencies().addAll(currencies);
            saveHistoryListSize();
        } else {
            mFilter.getCurrencies().addAll(currencies);
        }

        return mFilter.getCurrencies();
    }

    private List<Currency> getCurrencyList() {
        List<Currency> currencies = new ArrayList<>();

        for (String name : mExchangeCurrencyDao.getUniqueCurrencyNames()) {
            currencies.add(new Currency(name));
        }

        return currencies;
    }

    private List<Currency> getOnlyNewItems(List<Currency> newCurrencyList, List<Currency> oldCurrencyList) {
        List<Currency> subtraction = new ArrayList<>(newCurrencyList.size());
        subtraction.addAll(newCurrencyList);
        subtraction.removeAll(oldCurrencyList);
        return subtraction;
    }

    private void saveHistoryListSize() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(HISTORY_LIST_SIZE, mExchangeCurrencyDao.getAll().size());
        editor.apply();
    }

    @Override
    protected void onPostExecute(List<Currency> list) {
        FilterActivity filterActivity = mFilterActivityWeakReference.get();
        if (filterActivity != null) {
            filterActivity.mFilterRecyclerView.setAdapter(new FilterAdapter(list));
            filterActivity.initRadioGroup();

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
        FilterActivity filterActivity = mFilterActivityWeakReference.get();
        if (filterActivity != null) {
            filterActivity.mBeginDateButton.setText(sdf.format(start));
            filterActivity.mEndDateButton.setText(sdf.format(end));
        }

        mFilter.setDatePickerStartDate(start);
        mFilter.setDatePickerEndDate(end);
    }

    private void setDateOther(long start, long end) {
        mFilter.setBeginDateOther(start);
        mFilter.setEndDateOther(end);
    }

    private void setCheckedButton(int id) {
        FilterActivity filterActivity = mFilterActivityWeakReference.get();
        if (filterActivity != null) {
            RadioButton button = filterActivity.findViewById(id);
            button.setChecked(true);
            button.jumpDrawablesToCurrentState();
        }
    }
}

