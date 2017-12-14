package ru.tinkoff.school.currencyrate.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.tinkoff.school.currencyrate.App;
import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.activities.ExchangeActivity;
import ru.tinkoff.school.currencyrate.adapters.CurrencyAdapter;
import ru.tinkoff.school.currencyrate.asynchronous.GettingListTask;
import ru.tinkoff.school.currencyrate.database.CurrencyDao;
import ru.tinkoff.school.currencyrate.models.Currency;


public class CurrencyListFragment extends Fragment {
    public static final String TAG = "CurrencyListFragment";
    private static final int EXCHANGE_REQUEST_CODE = 0;

    private RecyclerView mCurrencyRecyclerView;
    private CurrencyAdapter mAdapter;
    private CurrencyDao mCurrencyDao;
    private String mUpperCurrency;
    private List<String> mCurrencyNamesForUpdate;
    private Currency mTempRemovedCurrency;

    @Override
    public void onResume() {
        super.onResume();
        if (mCurrencyNamesForUpdate != null) {
            mCurrencyNamesForUpdate.clear();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle(R.string.currencies);
        mAdapter = new CurrencyAdapter();
        setListeners();
        mCurrencyDao = App.getDatabase().currencyDao();
        mCurrencyNamesForUpdate = new ArrayList<>();
        new GettingListTask(mAdapter, getActivity().getApplicationContext()).execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_currency_list, container, false);
        mCurrencyRecyclerView = v.findViewById(R.id.currency_recycler_view);
        mCurrencyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCurrencyRecyclerView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mTempRemovedCurrency != null) {
            mAdapter.add(mTempRemovedCurrency);
            mTempRemovedCurrency = null;
        }
    }

    private void setListeners() {
        mAdapter.setOnItemClickListener(new CurrencyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CurrencyAdapter.CurrencyViewHolder holder, int position) {
                Currency supposeFavouriteCurrency = mAdapter.getItem(0);
                Currency currentCurrency = holder.getCurrency();
                String favourite = null;

                if (mCurrencyNamesForUpdate.isEmpty()) {
                    mCurrencyNamesForUpdate.add(currentCurrency.getName());
                    if (supposeFavouriteCurrency.isFavourite()) {
                        favourite = supposeFavouriteCurrency.getName();
                        if (favourite.equals(currentCurrency.getName())) {
                            if (favourite.equals(ExchangeActivity.USD)) {
                                mCurrencyNamesForUpdate.add(ExchangeActivity.RUB);
                            } else {
                                mCurrencyNamesForUpdate.add(ExchangeActivity.USD);
                            }
                        } else {
                            mCurrencyNamesForUpdate.add(favourite);
                        }
                    } else {
                        if (!currentCurrency.getName().equals(ExchangeActivity.USD)) {
                            mCurrencyNamesForUpdate.add(ExchangeActivity.USD);
                        } else {
                            mCurrencyNamesForUpdate.add(ExchangeActivity.RUB);
                        }
                    }
                } else {
                    mCurrencyNamesForUpdate.add(currentCurrency.getName());
                }


                ExchangeActivity.startForResult(CurrencyListFragment.this, mUpperCurrency, holder.getCurrency().getName(), favourite, EXCHANGE_REQUEST_CODE);
                mUpperCurrency = null;
            }
        });

        mAdapter.setOnItemLongClickListener(new CurrencyAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(CurrencyAdapter.CurrencyViewHolder holder, int position) {
                if (mTempRemovedCurrency != null) {
                    return false;
                }

                mTempRemovedCurrency = holder.getCurrency();
                mCurrencyNamesForUpdate.add(mTempRemovedCurrency.getName());
                mUpperCurrency = mTempRemovedCurrency.getName();
                mAdapter.remove(mTempRemovedCurrency);

                return true;
            }
        });

        mAdapter.setOnItemCheckedChangeListener(new CurrencyAdapter.OnItemCheckedChangeListener() {
            @Override
            public void OnItemCheckedChanged(CompoundButton button, boolean favourite, CurrencyAdapter.CurrencyViewHolder holder, int position, boolean isBind) {
                if (!isBind) {
                    holder.getCurrency().setFavourite(favourite);
                    mAdapter.recalculatePosition(position);
                    updateItem(holder.getCurrency());
                }
            }
        });
    }


    private void updateItem(Currency currency) {
        new AsyncTask<Currency, Void, Void>() {
            @Override
            protected Void doInBackground(Currency... currencies) {
                mCurrencyDao.update(currencies[0]);
                return null;
            }
        }.execute(currency);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EXCHANGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                for (String name : mCurrencyNamesForUpdate) {
                    int position = mAdapter.getItemPositionByName(name);
                    Currency currency = mAdapter.getItem(position);
                    currency.setRecent(new Date());
                    mAdapter.recalculatePosition(position);
                    updateItem(currency);
                }
            }
            mCurrencyNamesForUpdate.clear();
        }
    }

}
