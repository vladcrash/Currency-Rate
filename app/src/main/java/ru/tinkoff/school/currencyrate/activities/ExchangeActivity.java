package ru.tinkoff.school.currencyrate.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.tinkoff.school.currencyrate.App;
import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.database.ExchangeCurrencyDao;
import ru.tinkoff.school.currencyrate.models.ExchangeCurrency;
import ru.tinkoff.school.currencyrate.network.CurrencyCache;

import static ru.tinkoff.school.currencyrate.network.CurrencyCache.FIVE_MINUTES;


public class ExchangeActivity extends AppCompatActivity {
    public static final String USD = "USD";
    public static final String RUB = "RUB";
    private static final String UPPER_CURRENCY = "UPPER_CURRENCY";
    private static final String LOWER_CURRENCY = "LOWER_CURRENCY";
    private static final String FAVOURITE_CURRENCY = "FAVOURITE_CURRENCY";

    private EditText mUpperValueEditText;
    private EditText mLowerValueEditText;
    private TextView mUpperCurrencyTextView;
    private TextView mLowerCurrencyTextView;
    private Button mExchangeButton;
    private String mUpperCurrency;
    private String mLowerCurrency;
    private double mUpperValue;
    private double mLowerValue;
    private double mRate;
    private ExchangeCurrencyDao mExchangeCurrencyDao;
    private Boolean mIsRequest;
    private Date mDate;
    private boolean mIsFirstTime;
    private boolean mIsShowDialog;
    private CurrencyCache mCache;

    public static void startForResult(Fragment fragment, String upperCurrency, String lowerCurrency, String favourite,
                                      int requestCode) {
        Intent intent = new Intent(fragment.getActivity().getApplicationContext(), ExchangeActivity.class);
        intent.putExtra(UPPER_CURRENCY, upperCurrency);
        intent.putExtra(LOWER_CURRENCY, lowerCurrency);
        intent.putExtra(FAVOURITE_CURRENCY, favourite);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        setTitle(R.string.currency_exchange);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mUpperValueEditText = findViewById(R.id.upper_currency_input);
        mLowerValueEditText = findViewById(R.id.lower_currency_input);
        mUpperCurrencyTextView = findViewById(R.id.upper_currency);
        mLowerCurrencyTextView = findViewById(R.id.lower_currency);
        mExchangeButton = findViewById(R.id.exchange_button);
        mExchangeButton.setEnabled(false);
        mExchangeCurrencyDao = App.getDatabase().exchangeCurrencyDao();
        initEditTexts();
        setCurrencyOrder();

        mUpperCurrencyTextView.setText(mUpperCurrency);
        mLowerCurrencyTextView.setText(mLowerCurrency);

        mIsFirstTime = true;
        mCache = new CurrencyCache(this, mUpperCurrency, mLowerCurrency);
        setRate();
        mUpperValue = 1.0;
        mUpperValueEditText.setText(Double.toString(mUpperValue));
    }

    private void setCurrencyOrder() {
        mUpperCurrency = getIntent().getStringExtra(UPPER_CURRENCY);
        if (mUpperCurrency == null) {
            mUpperCurrency = getIntent().getStringExtra(LOWER_CURRENCY);
            mLowerCurrency = getIntent().getStringExtra(FAVOURITE_CURRENCY);
            if (mLowerCurrency == null) {
                checkForUSD();
            } else {
                if (mUpperCurrency.equals(mLowerCurrency)) {
                    checkForUSD();
                }
            }
        } else {
            mLowerCurrency = getIntent().getStringExtra(LOWER_CURRENCY);
        }
    }

    private void checkForUSD() {
        if (mUpperCurrency.equals(USD)) {
            mLowerCurrency = RUB;
        } else {
            mLowerCurrency = USD;
        }
    }

    private void requestRate(final boolean upOrDown) {
        Call<ExchangeCurrency> call = App.getFixerApi().getCurrencyRate(mUpperCurrency, mLowerCurrency, null);
        call.enqueue(new Callback<ExchangeCurrency>() {
            @Override
            public void onResponse(Call<ExchangeCurrency> call, Response<ExchangeCurrency> response) {
                if (response.isSuccessful()) {
                    mRate = response.body().getCurrency().getRate();
                    mDate = new Date();
                    if (upOrDown) {
                        updateUpper();
                    } else {
                        updateLower();
                    }

                    mExchangeButton.setEnabled(true);

                    if (mIsShowDialog) {
                        makeDialog(mUpperValue + " " + mUpperCurrency + " = " + mLowerValue + " " + mLowerCurrency);
                        mIsShowDialog = false;
                    }

                    mIsFirstTime = false;
                    mCache.writeToCache(mRate, mDate, mIsRequest);
                }
            }

            @Override
            public void onFailure(Call<ExchangeCurrency> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setRate() {
        mIsRequest = mCache.isShouldGoToTheNetwork();
        if (mIsRequest != null) {
            if (mIsRequest) {
                requestRate(true);
            } else {
                if (mIsFirstTime) {
                    mRate =  mCache.getRate();
                    mDate = mCache.getDate();
                    mLowerValue = mRate;
                    mLowerValue = (double) Math.round(mLowerValue * 100.0) / 100.0;
                    mLowerValueEditText.setText(String.format(Locale.ENGLISH, "%.2f", mLowerValue));
                    mIsFirstTime = false;
                    mExchangeButton.setEnabled(true);
                }
            }
        } else {
            requestRate(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void updateLower() {
        mUpperValue = mLowerValue / mRate;
        mUpperValue = (double) Math.round(mUpperValue * 100.0) / 100.0;
        mUpperValueEditText.setText(String.format(Locale.ENGLISH, "%.2f", mUpperValue));
    }

    private void updateUpper() {
        mLowerValue = mUpperValue * mRate;
        mLowerValue = (double) Math.round(mLowerValue * 100.0) / 100.0;
        mLowerValueEditText.setText(String.format(Locale.ENGLISH, "%.2f", mLowerValue));
    }


    private void initEditTexts() {

        mUpperValueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (getCurrentFocus() == mUpperValueEditText) {
                    setUpperClick(true);
                    mUpperValue = checkForCorrectness(charSequence);
                    updateUpper();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mLowerValueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (getCurrentFocus() == mLowerValueEditText) {
                    setUpperClick(false);
                    mLowerValue = checkForCorrectness(charSequence);
                    updateLower();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private double checkForCorrectness(CharSequence charSequence) {
        double value;
        if (charSequence.toString().isEmpty() || charSequence.toString().equals(".")) {
            value = 0.0;
        } else {
            value = Double.valueOf(charSequence.toString());
        }

        return value;
    }

    private void setUpperClick(boolean upOrDown) {
        if (!mIsFirstTime) {
            if ((new Date()).getTime() - mDate.getTime() > FIVE_MINUTES) {
                mIsRequest = true;
                requestRate(upOrDown);
            }
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exchange_button:
                if ((new Date()).getTime() - mDate.getTime() > FIVE_MINUTES) {
                    mIsRequest = true;
                    mIsShowDialog = true;
                    requestRate(true);
                } else {
                    exchange();
                }
        }
    }

    private void makeDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ExchangeActivity.this);
        builder.setTitle(getResources().getString(R.string.dialog_title))
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        exchange();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exchange() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ExchangeCurrency apiResponse = new ExchangeCurrency(mUpperCurrency, mUpperValue, mLowerCurrency, mLowerValue);
                mExchangeCurrencyDao.insert(apiResponse);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                setResult(RESULT_OK);
                finish();
            }
        }.execute();
    }
}
