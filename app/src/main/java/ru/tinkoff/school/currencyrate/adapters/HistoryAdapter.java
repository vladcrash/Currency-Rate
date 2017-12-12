package ru.tinkoff.school.currencyrate.adapters;


import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.tinkoff.school.currencyrate.R;
import ru.tinkoff.school.currencyrate.models.ApiResponse;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context mContext;
    private SortedList<ApiResponse> mCurrencyHistories;

    public HistoryAdapter(Context context) {
        mContext = context;
        mCurrencyHistories = new SortedList<>(ApiResponse.class, new SortedListAdapterCallback<ApiResponse>(this) {
            @Override
            public int compare(ApiResponse o1, ApiResponse o2) {
                return o1.getCurrency().getRecent().compareTo(o2.getCurrency().getRecent()) * (-1);
            }

            @Override
            public boolean areContentsTheSame(ApiResponse oldItem, ApiResponse newItem) {
                return oldItem.getCurrency().getRecent().equals(newItem.getCurrency().getRecent());
            }

            @Override
            public boolean areItemsTheSame(ApiResponse item1, ApiResponse item2) {
                return item1.getId() == item2.getId();
            }
        });
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new HistoryViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        ApiResponse currencyHistory = mCurrencyHistories.get(position);
        holder.bind(currencyHistory);
    }

    @Override
    public int getItemCount() {
        return mCurrencyHistories.size();
    }

    public void setCurrencyHistories(List<ApiResponse> currencyHistories) {
        mCurrencyHistories.clear();
        mCurrencyHistories.addAll(currencyHistories);
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView mExchangeInfo;

        public HistoryViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_history, parent, false));
            mExchangeInfo = itemView.findViewById(R.id.exchange_info);
        }

        public void bind(ApiResponse currencyHistory) {
            mExchangeInfo.setText(currencyHistory.toString());
        }

    }
}