package ru.tinkoff.school.currencyrate.adapters;


import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.models.ExchangeCurrency;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private SortedList<ExchangeCurrency> mCurrencyHistories;

    public HistoryAdapter() {
        mCurrencyHistories = new SortedList<>(ExchangeCurrency.class, new SortedListAdapterCallback<ExchangeCurrency>(this) {
            @Override
            public int compare(ExchangeCurrency o1, ExchangeCurrency o2) {
                return o1.getCurrency().getRecent().compareTo(o2.getCurrency().getRecent()) * (-1);
            }

            @Override
            public boolean areContentsTheSame(ExchangeCurrency oldItem, ExchangeCurrency newItem) {
                return oldItem.getCurrency().getRecent().equals(newItem.getCurrency().getRecent());
            }

            @Override
            public boolean areItemsTheSame(ExchangeCurrency item1, ExchangeCurrency item2) {
                return item1.getId() == item2.getId();
            }
        });
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new HistoryViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        ExchangeCurrency currencyHistory = mCurrencyHistories.get(position);
        holder.bind(currencyHistory);
    }

    @Override
    public int getItemCount() {
        return mCurrencyHistories.size();
    }

    public void setCurrencyHistories(List<ExchangeCurrency> currencyHistories) {
        mCurrencyHistories.clear();
        mCurrencyHistories.addAll(currencyHistories);
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView mExchangeInfo;

        public HistoryViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_history, parent, false));
            mExchangeInfo = itemView.findViewById(R.id.exchange_info);
        }

        public void bind(ExchangeCurrency currencyHistory) {
            mExchangeInfo.setText(currencyHistory.toString());
        }

    }
}