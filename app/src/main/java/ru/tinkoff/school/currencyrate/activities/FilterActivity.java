package ru.tinkoff.school.currencyrate.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.tinkoff.school.currencyrate.App;
import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.adapters.FilterAdapter;
import ru.tinkoff.school.currencyrate.database.ApiResponseDao;
import ru.tinkoff.school.currencyrate.fragments.DatePickerFragment;
import ru.tinkoff.school.currencyrate.models.ApiResponse;
import ru.tinkoff.school.currencyrate.models.Currency;


public class FilterActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener {
    private static final String DIALOG_DATE = "Dialog_Date";
    private static final int START_DATE_ID = 0;
    private static final int END_DATE_ID = 1;
    public static final int WEEK = -6;
    public static final int MONTH = -29;
    public static final String CURRENCY_DATA = "currency_data";
    public static final String CURRENCY_LIST = "currency_list";
    public static final String HISTORY_LIST_SIZE = "history_list_size";
    public static final String RADIO_BUTTON_ID = "radio_button_id";
    public static final String BEGIN_DATE_OTHER = "begin_date_other";
    public static final String END_DATE_OTHER = "end_date_other";
    public static final String BEGIN_DATE = "begin_date";
    public static final String END_DATE = "end_date";
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    private RadioGroup mDateGroup;
    private Button mBeginDateButton;
    private Button mEndDateButton;
    private RecyclerView mFilterRecyclerView;
    private ApiResponseDao mApiResponseDao;
    private ArrayList<Currency> mCurrencies;
    private long mBeginDate;
    private long mEndDate;
    private long mBeginDateOther;
    private long mEndDateOther;
    private Date mDatePickerStartDate;
    private Date mDatePickerEndDate;
    private SharedPreferences mPreferences;


    public static void start(Context context) {
        Intent intent = new Intent(context, FilterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        setTitle(R.string.filter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDateGroup = findViewById(R.id.date_group);
        mBeginDateButton = findViewById(R.id.begin_date_button);
        mEndDateButton = findViewById(R.id.end_date_button);
        mFilterRecyclerView = findViewById(R.id.filter_recycler_view);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mApiResponseDao = App.getDatabase().apiResponseDao();
        mCurrencies = new ArrayList<>();
        mPreferences = getSharedPreferences(CURRENCY_DATA, Context.MODE_PRIVATE);
        init();

    }

    private void init() {
        new AsyncTask<Void, Void, List<Currency>>() {
            private SharedPreferences.Editor mEditor = mPreferences.edit();

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
                    mCurrencies.addAll(currencySet);
                    saveHistoryListSize();
                } else if (mApiResponseDao.getAll().size() > historyListSize) {
                    Set<Currency> currencySet = new HashSet<>();
                    makeFilterList(currencySet);
                    currencies.addAll(getOnlyNewItems(currencySet, currencies));
                    mCurrencies.addAll(currencies);
                    saveHistoryListSize();
                } else {
                    mCurrencies.addAll(currencies);
                }
                return mCurrencies;
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
                mEditor.putInt(HISTORY_LIST_SIZE, mApiResponseDao.getAll().size());
                mEditor.apply();
            }

            @Override
            protected void onPostExecute(List<Currency> list) {
                mFilterRecyclerView.setAdapter(new FilterAdapter(list));

                initRadioGroup();
                int buttonId = mPreferences.getInt(RADIO_BUTTON_ID, 0);
                if (buttonId != 0) {
                    RadioButton button = findViewById(buttonId);
                    retrieveCorrectValues();

                    mDatePickerStartDate = new Date(mBeginDateOther);
                    mDatePickerEndDate = new Date(mEndDateOther);
                    mBeginDateButton.setText(sdf.format(mDatePickerStartDate));
                    mEndDateButton.setText(sdf.format(mDatePickerEndDate));

                    button.setChecked(true);
                    button.jumpDrawablesToCurrentState();
                } else {
                    RadioButton button = findViewById(R.id.all_time_button);
                    button.setChecked(true);
                    button.jumpDrawablesToCurrentState();

                    mDatePickerStartDate = new Date();
                    mDatePickerEndDate = new Date();
                    mBeginDateButton.setText(sdf.format(mDatePickerStartDate));
                    mEndDateButton.setText(sdf.format(mDatePickerEndDate));
                    mBeginDateOther = mDatePickerStartDate.getTime();
                    mEndDateOther = mDatePickerEndDate.getTime();
                }
            }

            private void retrieveCorrectValues() {
                long startDate = mPreferences.getLong(BEGIN_DATE_OTHER, (new Date()).getTime());
                long endDate = mPreferences.getLong(END_DATE_OTHER, (new Date()).getTime());
                if (startDate < endDate) {
                    mBeginDateOther = startDate;
                    mEndDateOther = endDate;
                } else {
                    mBeginDateOther = endDate;
                    mEndDateOther = startDate;
                }
            }

        }.execute();
    }

    private void initRadioGroup() {
        mDateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i != R.id.other) {
                    mBeginDateButton.setVisibility(View.GONE);
                    mEndDateButton.setVisibility(View.GONE);
                }

                switch (i) {
                    case R.id.all_time_button:
                        mBeginDate = 0;
                        mEndDate = 0;
                        break;
                    case R.id.month_button:
                        mBeginDate = getDate(MONTH);
                        mEndDate = (new Date()).getTime();
                        break;
                    case R.id.week_button:
                        mBeginDate = getDate(WEEK);
                        mEndDate = (new Date()).getTime();
                        break;
                    case R.id.other:
                        mBeginDate = mDatePickerStartDate.getTime();
                        mEndDate = mDatePickerEndDate.getTime();
                        mBeginDateButton.setVisibility(View.VISIBLE);
                        mEndDateButton.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    public long getDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        return calendar.getTimeInMillis();
    }

    public void onClick(View view) {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment dialog;

        switch (view.getId()) {
            case R.id.begin_date_button:
                dialog = DatePickerFragment.newInstance(mDatePickerStartDate, START_DATE_ID);
                dialog.show(fm, DIALOG_DATE);
                break;
            case R.id.end_date_button:
                dialog = DatePickerFragment.newInstance(mDatePickerEndDate, END_DATE_ID);
                dialog.show(fm, DIALOG_DATE);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm:
                if (mBeginDateOther >= mEndDateOther) {
                    mBeginDateOther = swap(mEndDateOther, mEndDateOther = mBeginDateOther);
                }

                if (mBeginDate >= mEndDate) {
                    mBeginDate = swap(mEndDate, mEndDate = mBeginDate);
                }

                SharedPreferences.Editor editor = mPreferences.edit();
                editor.clear();
                Gson gson = new Gson();
                String list = gson.toJson(mCurrencies);
                editor.putString(CURRENCY_LIST, list);
                editor.putInt(RADIO_BUTTON_ID, mDateGroup.getCheckedRadioButtonId());
                editor.putLong(BEGIN_DATE_OTHER, mBeginDateOther);
                editor.putLong(END_DATE_OTHER, mEndDateOther);
                editor.putLong(BEGIN_DATE, mBeginDate);
                editor.putLong(END_DATE, mEndDate);
                editor.apply();

                finish();
                return true;
            default:
                return false;
        }
    }

    private long swap(long value, long dummy) {
        return value;
    }


    @Override
    public void setDate(Date date, int trigger) {

        switch (trigger) {
            case START_DATE_ID:
                mBeginDateButton.setText(sdf.format(date));
                mDatePickerStartDate = date;
                mBeginDate = date.getTime();
                mBeginDateOther = date.getTime();
                break;
            case END_DATE_ID:
                mEndDateButton.setText(sdf.format(date));
                mDatePickerEndDate = date;
                mEndDate = date.getTime();
                mEndDateOther = date.getTime();
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
