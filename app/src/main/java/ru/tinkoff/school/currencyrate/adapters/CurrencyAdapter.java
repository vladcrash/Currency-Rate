package ru.tinkoff.school.currencyrate.adapters;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.models.Currency;


public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {

    private SortedList<Currency> mCurrencies;
    private boolean mOnBind;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemCheckedChangeListener onItemCheckedChangeListener;

    public CurrencyAdapter() {
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
        Currency currency = mCurrencies.get(position);
        mOnBind = true;
        holder.bind(currency);
        mOnBind = false;
    }

    @Override
    public int getItemCount() {
        return mCurrencies.size();
    }

    public void setCurrencies(List<Currency> currencies) {
        mCurrencies.addAll(currencies);
    }

    public void recalculatePosition(int position) {
        mCurrencies.recalculatePositionOfItemAt(position);
    }

    public Currency getItem(int position) {
        return mCurrencies.get(position);
    }

    public void remove(Currency currency) {
        mCurrencies.remove(currency);
    }

    public void add(Currency currency) {
        mCurrencies.add(currency);
    }

    public int getItemPositionByName(String name) {
        int position = -1;
        for (int i = 0; i < mCurrencies.size(); i++) {
            if (mCurrencies.get(i).getName().equals(name)) {
                position = i;
            }
        }

        return position;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener onItemCheckedChangeListener) {
        this.onItemCheckedChangeListener = onItemCheckedChangeListener;
    }

    public interface OnItemClickListener {
        void onItemClick(CurrencyAdapter.CurrencyViewHolder holder, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(CurrencyAdapter.CurrencyViewHolder holder, int position);
    }

    public interface OnItemCheckedChangeListener {
        void OnItemCheckedChanged(CompoundButton button, boolean favourite, CurrencyAdapter.CurrencyViewHolder holder, int position, boolean isBind);
    }

    public class CurrencyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {

        private TextView mNameTextView;
        private CheckBox mFavouriteCheckBox;
        private Currency mCurrency;

        public CurrencyViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_currency, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mNameTextView = itemView.findViewById(R.id.currency_name);
            mFavouriteCheckBox = itemView.findViewById(R.id.currency_favourite);
            mFavouriteCheckBox.setOnCheckedChangeListener(this);
            mFavouriteCheckBox.setButtonDrawable(R.drawable.selector_checkbox_favourite);
        }

        public void bind(Currency currency) {
            mCurrency = currency;
            mNameTextView.setText(currency.getName());
            mFavouriteCheckBox.setChecked(currency.isFavourite());
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(this, getAdapterPosition());

        }

        @Override
        public boolean onLongClick(View view) {
            return onItemLongClickListener.onItemLongClick(this, getAdapterPosition());
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean favourite) {
            onItemCheckedChangeListener.OnItemCheckedChanged(compoundButton, favourite, this, getAdapterPosition(), mOnBind);

        }

        public Currency getCurrency() {
            return mCurrency;
        }
    }

    public void setSortedList(SortedList<Currency> currencies) {
        mCurrencies = currencies;
    }
}