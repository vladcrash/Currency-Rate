package ru.tinkoff.school.currencyrate.asynchronous;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import ru.tinkoff.school.currencyrate.App;
import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.adapters.CurrencyAdapter;
import ru.tinkoff.school.currencyrate.database.CurrencyDao;
import ru.tinkoff.school.currencyrate.models.Currency;


public class GettingListTask extends AsyncTask<Void, Void, List<Currency>> {

    private CurrencyAdapter mAdapter;
    private CurrencyDao mCurrencyDao = App.getDatabase().currencyDao();
    private WeakReference<Context> mContext;

    public GettingListTask(CurrencyAdapter adapter, Context context) {
        mAdapter = adapter;
        mContext = new WeakReference<>(context);
    }

    @Override
    protected List<Currency> doInBackground(Void... voids) {
        List<Currency> list = mCurrencyDao.getAll();
        Context context = mContext.get();
        if (list.isEmpty() && context != null) {
            String[] names = context.getResources().getStringArray(R.array.currency_list);
            for (String name : names) {
                list.add(new Currency(name));
            }
            mCurrencyDao.insertAll(list);
            list = mCurrencyDao.getAll();
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<Currency> currencies) {
        mAdapter.setCurrencies(currencies);
    }
}