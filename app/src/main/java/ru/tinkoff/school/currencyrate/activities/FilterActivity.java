package ru.tinkoff.school.currencyrate.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.asynchronous.GettingFilterTask;
import ru.tinkoff.school.currencyrate.fragments.DatePickerFragment;
import ru.tinkoff.school.currencyrate.models.Filter;


public class FilterActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener {
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
    private static final String DIALOG_DATE = "Dialog_Date";
    private static final int START_DATE_ID = 0;
    private static final int END_DATE_ID = 1;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public Button mBeginDateButton;
    public Button mEndDateButton;
    public RecyclerView mFilterRecyclerView;
    private RadioGroup mDateGroup;
    private SharedPreferences mPreferences;
    private Filter mFilter;


    public static void start(Context context) {
        Intent intent = new Intent(context, FilterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        setTitle(R.string.filter);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDateGroup = findViewById(R.id.date_group);
        mBeginDateButton = findViewById(R.id.begin_date_button);
        mEndDateButton = findViewById(R.id.end_date_button);
        mFilterRecyclerView = findViewById(R.id.filter_recycler_view);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPreferences = getSharedPreferences(CURRENCY_DATA, Context.MODE_PRIVATE);
        mFilter = new Filter();
        new GettingFilterTask(this, mFilter).execute();
    }

    public void initRadioGroup() {
        mDateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i != R.id.other) {
                    mBeginDateButton.setVisibility(View.GONE);
                    mEndDateButton.setVisibility(View.GONE);
                }

                switch (i) {
                    case R.id.all_time_button:
                        mFilter.setBeginDate(0);
                        mFilter.setEndDate(0);
                        break;
                    case R.id.month_button:
                        mFilter.setBeginDate(getDate(MONTH).getTime());
                        mFilter.setEndDate(new Date().getTime());
                        break;
                    case R.id.week_button:
                        mFilter.setBeginDate(getDate(WEEK).getTime());
                        mFilter.setEndDate(new Date().getTime());
                        break;
                    case R.id.other:
                        mBeginDateButton.setVisibility(View.VISIBLE);
                        mEndDateButton.setVisibility(View.VISIBLE);
                        mFilter.setBeginDate(mFilter.getDatePickerStartDate().getTime());
                        mFilter.setEndDate(mFilter.getDatePickerEndDate().getTime());
                        break;
                }
            }
        });
    }


    private Date getDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public void onClick(View view) {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment dialog;

        switch (view.getId()) {
            case R.id.begin_date_button:
                dialog = DatePickerFragment.newInstance(mFilter.getDatePickerStartDate(), START_DATE_ID);
                dialog.show(fm, DIALOG_DATE);
                break;
            case R.id.end_date_button:
                dialog = DatePickerFragment.newInstance(mFilter.getDatePickerEndDate(), END_DATE_ID);
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
                if (mFilter.getBeginDateOther() >= mFilter.getEndDateOther()) {
                    long temp = mFilter.getBeginDateOther();
                    mFilter.setBeginDateOther(mFilter.getEndDateOther());
                    mFilter.setEndDateOther(temp);
                }

                if (mFilter.getBeginDate() >= mFilter.getEndDate()) {
                    long temp = mFilter.getBeginDate();
                    mFilter.setBeginDate(mFilter.getEndDate());
                    mFilter.setEndDate(temp);
                }

                SharedPreferences.Editor editor = mPreferences.edit();
                editor.clear();
                Gson gson = new Gson();
                String list = gson.toJson(mFilter.getCurrencies());
                editor.putString(CURRENCY_LIST, list);
                editor.putInt(RADIO_BUTTON_ID, mDateGroup.getCheckedRadioButtonId());
                editor.putLong(BEGIN_DATE_OTHER, mFilter.getBeginDateOther());
                editor.putLong(END_DATE_OTHER, mFilter.getEndDateOther());
                editor.putLong(BEGIN_DATE, mFilter.getBeginDate());
                editor.putLong(END_DATE, mFilter.getEndDate());
                editor.apply();

                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void setDate(Date date, int trigger) {

        switch (trigger) {
            case START_DATE_ID:
                mBeginDateButton.setText(sdf.format(date));
                mFilter.setDatePickerStartDate(date);
                mFilter.setBeginDate(date.getTime());
                mFilter.setBeginDateOther(date.getTime());
                break;
            case END_DATE_ID:
                mEndDateButton.setText(sdf.format(date));
                mFilter.setDatePickerEndDate(date);
                mFilter.setEndDate(date.getTime());
                mFilter.setEndDateOther(date.getTime());
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
