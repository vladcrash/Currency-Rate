package ru.tinkoff.school.currencyrate.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.models.Currency;


public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    private boolean mOnBind;
    private List<Currency> mHistoryList;

    public FilterAdapter(List<Currency> historyList) {
        mHistoryList = historyList;
    }

    @Override
    public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FilterViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(FilterViewHolder holder, int position) {
        Currency currencyHistory = mHistoryList.get(position);
        mOnBind = true;
        holder.bind(currencyHistory);
        mOnBind = false;
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {

        private Currency mCurrencyHistory;
        private TextView mName;
        private CheckBox mFavouriteCheckBox;

        public FilterViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_currency, parent, false));
            mName = itemView.findViewById(R.id.currency_name);
            mFavouriteCheckBox = itemView.findViewById(R.id.currency_favourite);
            mFavouriteCheckBox.setOnCheckedChangeListener(this);
        }

        public void bind(Currency currencyHistory) {
            mCurrencyHistory = currencyHistory;
            mName.setText(currencyHistory.getName());
            mFavouriteCheckBox.setChecked(currencyHistory.isFavourite());
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (!mOnBind) {
                mCurrencyHistory.setFavourite(b);
            }
        }
    }
}
