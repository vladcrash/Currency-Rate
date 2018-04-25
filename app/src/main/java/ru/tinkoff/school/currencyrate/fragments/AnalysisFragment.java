package ru.tinkoff.school.currencyrate.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.tinkoff.school.currencyrate.App;
import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.adapters.CurrencyAnalysisAdapter;
import ru.tinkoff.school.currencyrate.database.CurrencyDao;
import ru.tinkoff.school.currencyrate.models.ExchangeCurrency;
import ru.tinkoff.school.currencyrate.models.Currency;


public class AnalysisFragment extends Fragment {
    public static final String EUR = "EUR";
    public static final String USD = "USD";
    public static final int WEEK = -6;
    public static final int TWO_WEEKS = -13;
    public static final int MONTH = -29;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private LineChart mChart;
    private List<Entry> mYValues;
    private List<String> mXValues;
    private CurrencyAnalysisAdapter mAdapter;

    private RecyclerView mCurrencyRecyclerView;
    private CurrencyDao mCurrencyDao;
    private RadioGroup mPeriodGroup;
    private String mBaseCurrencyName;
    private String mCurrencyForRequest;
    private int mXIndex;
    private int mNumberOfDays;
    private LineData mLineData;
    private View mSelectedView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrencyDao = App.getDatabase().currencyDao();
        mAdapter = new CurrencyAnalysisAdapter();
        new AsyncTask<Void, Void, List<Currency>>() {
            @Override
            protected List<Currency> doInBackground(Void... voids) {
                return mCurrencyDao.getAll();
            }

            @Override
            protected void onPostExecute(List<Currency> currencies) {
                mAdapter.setCurrencies(currencies);
            }
        }.execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        mPeriodGroup = view.findViewById(R.id.time_period_group);
        mChart = view.findViewById(R.id.line_chart);
        mChart.setDescription(null);

        mCurrencyRecyclerView = view.findViewById(R.id.analysis_recycler_view);
        mCurrencyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mCurrencyRecyclerView.setAdapter(mAdapter);
        setListenerForRecyclerView();
        getActivity().setTitle(R.string.plot);

        mNumberOfDays = WEEK;
        RadioButton button = view.findViewById(R.id.week);
        button.setChecked(true);
        button.jumpDrawablesToCurrentState();

        initRadioGroup();
        initLineChart();

        return view;
    }

    private void initLineChart() {
        mYValues = new ArrayList<>();
        mXValues = new ArrayList<>();
        mChart.getAxisLeft().setStartAtZero(false);
        mChart.getAxisLeft().setTextSize(16);
        mChart.getAxisRight().setStartAtZero(false);
        mChart.getAxisRight().setTextSize(16);
        mChart.getAxisRight().setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return "";
            }
        });
        mChart.getXAxis().setTextSize(16);
        mChart.getLegend().setTextSize(16);
        mChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return String.format(Locale.ENGLISH, "%.3f", value);
            }
        });
    }


    private void fillData() {
        mXIndex = 0;
        mXValues.clear();
        mYValues.clear();

        if (mCurrencyForRequest.equals(EUR)) {
            mBaseCurrencyName = USD;
        } else {
            mBaseCurrencyName = EUR;
        }

        for (int i = mNumberOfDays; i <= 0; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, i);
            makeRequest(dateFormat.format(calendar.getTime()));
        }

        LineDataSet dataSet = new LineDataSet(mYValues, mBaseCurrencyName);
        dataSet.setValueTextSize(8);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.format(Locale.ENGLISH, "%.3f", value);
            }
        });
        dataSet.setDrawCubic(true);
        mLineData = new LineData(mXValues, dataSet);
        mChart.setData(mLineData);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    private void makeRequest(final String date) {
        Call<ExchangeCurrency> call = App.getFixerApi().getCurrencyRate(mBaseCurrencyName, mCurrencyForRequest, date);
        mXValues.add(date.substring(8) + date.substring(4, 7));

        call.enqueue(new Callback<ExchangeCurrency>() {
            @Override
            public void onResponse(Call<ExchangeCurrency> call, Response<ExchangeCurrency> response) {
                if (response.isSuccessful()) {
                    double rate = response.body().getCurrency().getRate();
                    mYValues.add(new Entry((float) rate, mXIndex++));
                    mLineData.notifyDataChanged();
                    mChart.notifyDataSetChanged();
                    mChart.invalidate();
                }
            }

            @Override
            public void onFailure(Call<ExchangeCurrency> call, Throwable t) {

            }
        });
    }

    private void initRadioGroup() {
        mPeriodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.month:
                        mNumberOfDays = MONTH;
                        fillData();
                        break;
                    case R.id.two_weeks:
                        mNumberOfDays = TWO_WEEKS;
                        fillData();
                        break;
                    case R.id.week:
                        mNumberOfDays = WEEK;
                        fillData();
                        break;
                }
            }
        });
    }

    private void setListenerForRecyclerView() {
        mAdapter.setOnItemClickListener(new CurrencyAnalysisAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, CurrencyAnalysisAdapter.CurrencyViewHolder holder) {
                mCurrencyForRequest = holder.getCurrency().getTo();

                if (mSelectedView == null) {
                    mSelectedView = view;
                    mSelectedView.setSelected(true);
                } else if (mSelectedView != view) {
                    mSelectedView.setSelected(false);
                    view.setSelected(true);
                    mSelectedView = view;
                }

                fillData();
            }
        });
    }


}
