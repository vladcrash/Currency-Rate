package ru.tinkoff.school.currencyrate.adapters;


import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.models.Currency;

public class CurrencyAnalysisAdapter extends RecyclerView.Adapter<CurrencyAnalysisAdapter.CurrencyViewHolder> {

    private SortedList<Currency> mCurrencies;
    private boolean mIsFirstTime;
    private Integer mSelectedPosition;
    private OnItemClickListener onItemClickListener;

    public CurrencyAnalysisAdapter() {
        mIsFirstTime = true;
        mCurrencies = new SortedList<>(Currency.class,
                new SortedListAdapterCallback<Currency>(this) {
                    @Override
                    public int compare(Currency o1, Currency o2) {
                        int i = 0;
                        int i1 = o1.isFavourite() ? 1 : -1;
                        int i2 = o2.isFavourite() ? 1 : -1;
                        if (i1 > i2) {
                            i = -1;
                        } else if (i1 < i2) {
                            i = 1;
                        }
                        if (i != 0) return i;

                        i = o1.getRecent().compareTo(o2.getRecent()) * (-1);
                        return i;
                    }

                    @Override
                    public boolean areContentsTheSame(Currency oldItem, Currency newItem) {
                        return oldItem.getName().equals(newItem.getName());
                    }

                    @Override
                    public boolean areItemsTheSame(Currency item1, Currency item2) {
                        return item1.getId() == item2.getId();
                    }
                });
    }

    @Override
    public CurrencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new CurrencyViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(CurrencyViewHolder holder, int position) {
        if (mSelectedPosition != null && mSelectedPosition == position) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }

        Currency currency = mCurrencies.get(position);
        holder.onBind(currency);
        if (mIsFirstTime) {
            holder.itemView.performClick();
            mIsFirstTime = false;
        }
    }

    @Override
    public int getItemCount() {
        return mCurrencies.size();
    }

    public void setCurrencies(List<Currency> currencies) {
        mCurrencies.addAll(currencies);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, CurrencyAnalysisAdapter.CurrencyViewHolder holder);
    }

    public class CurrencyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mCurrencyName;
        private Currency mCurrency;

        public CurrencyViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_analysis, parent, false));
            itemView.setOnClickListener(this);
            mCurrencyName = itemView.findViewById(R.id.currency_name);
        }

        public void onBind(Currency currency) {
            mCurrency = currency;
            mCurrencyName.setText(currency.getName());
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(view, this);
            mSelectedPosition = getAdapterPosition();
        }

        public Currency getCurrency() {
            return mCurrency;
        }
    }
}
